package geminisdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import com.sun.jna.Pointer;

import geminisdk.GenesisSerialiser.GlfLib.Svs5Callback;
import geminisdk.structures.GemStatusPacket;
import geminisdk.structures.LoggerPlaybackUpdate;
import tritechgemini.fileio.CatalogException;
import tritechgemini.fileio.GLFFileCatalog;
import tritechgemini.fileio.GLFGenericHeader;
import tritechgemini.fileio.LittleEndianDataInputStream;
import tritechgemini.imagedata.GLFImageRecord;
import tritechgemini.imagedata.GLFStatusData;

/**
 * Standard callback for Svs5 messages. As far as is practical, these messages are receiving the data
 * exactly as is in the C++ SDK. There may however be a couple of exceptions, particularly for messages
 * in which the message structure contains pointers to memory blocks or other structures as was the case
 * with the GLF image. In this / those case(s) the format has been rewritten into my own raw format so that
 * the data could me messaged through in a single lump.  <p>
 * 
 * Users of the Java Svs5 interface need to extend this class, completing the abstract functions, then pass 
 * that callback object to GLFLib.svs5StartSvs5
 * 
 * @author dg50
 *
 */
public abstract class Svs5StandardCallback implements Svs5Callback {

	private int[] nMessages = new int[Svs5MessageType.NUM_MESSAGETYPES];

	private GLFFileCatalog glfFileCatalog;
	
	private boolean verbose = false;
	
	private LinkedList<callBackData> callQueue = new LinkedList<>();

	private boolean playbackMode;

	public Svs5StandardCallback() {
		glfFileCatalog = new GLFFileCatalog(null);
		
		QueueReader queueReader = new QueueReader();
		Thread t = new Thread(queueReader);
		t.start();
	}

	@Override
	public void callback(int msgType, long size, Pointer data) {
		processSv5Callback(msgType, size, data);
	}

	private void processSv5Callback(int msgType, long size, Pointer pointer) {

//		System.out.printf("Callback message %d size %d\n", msgType, size);
		try {
			synchronized (callQueue) {
				byte[] byteData = null;
				if (pointer != null) {					
					byteData = pointer.getByteArray(0, (int) size);
				}
				//			if (msgType == 2) return;
				//			Arrays.copy
				callBackData queueItem = new callBackData(msgType, size, byteData.clone());
				if (playbackMode) {
					useQueueItem(queueItem);
				}
				else {
					callQueue.add(queueItem);
				}
			}
		}
		catch (Exception e) {
			System.out.printf("Message %d size %d, exception %s", msgType, size, e.getMessage());
		}
		
		nMessages[msgType]++;

	}
	
	private class QueueReader implements Runnable {

		@Override
		public void run() {
			callBackData queueItem;
			while (true) {
				while (callQueue.size() > 0) {
					synchronized (callQueue) {
						queueItem = callQueue.remove(0);
					}
					useQueueItem(queueItem);
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Use an item that arrived in the Svs5 callback, then got queued to make sure
	 * this processing doesn't ever block the callback thread from the Gemini software. 
	 * The easiest way to work out what all these different callbacks need to do is by 
	 * looking in the example SDK file GeminiSDKConsole.cpp. 
	 * @param item
	 */
	private void useQueueItem(callBackData item) {
//		if (item.messageId>0 && item.messageId != Svs5MessageType.GLF_LIVE_TARGET_IMAGE) {
//			System.out.printf("Status message %d with %d bytes data\n", item.messageId, item.size);
//		}
		switch (item.messageId) {
		case Svs5MessageType.GEMINI_STATUS:
//			System.out.printf("Status message with %d bytes data\n", item.size);
			geminiStatusMsg(item.data);
			break;
		case Svs5MessageType.GLF_LIVE_TARGET_IMAGE:
			glfLiveImage(item.data);
			break;
		case Svs5MessageType.FRAME_RATE:
			frameRateMessage(item.data);
			break;
		case Svs5MessageType.LOGGER_PLAYBACK_UPDATE:
			// get these once per sec during file playback. size 2572
			loggerPlaybackUpdate(item.data, item.size);			
			break;
		case Svs5MessageType.LOGGER_FILE_INDEX:
			// need to deal with this one too - size 4, so just a plain int32.
			loggerFileIndex(item.data, item.size);
			break;
		case Svs5MessageType.LOGGER_REC_UPDATE:
			recUpdateMessage(item.data, item.size);
			break;
		case Svs5MessageType.LOGGER_STATUS_INFO:
			// getting this when the hard drive is full. 
			loggerStatusInfo(item.data, item.size);
			break;
		default:
			/**
			 * Probably expecting additional callbacks here when we start recording, 
			 * and if we use this for reanalysis of data in files. 
			 * e.g. may get message LOGGER_REC_UPDATE (0x4)
			 */
			System.out.printf("SvS5 callback type %d (%s) size %d \n", item.messageId, Svs5MessageType.getMessageName(item.messageId), item.size);

		}
		
	}
	
	private void loggerFileIndex(byte[] data, long size) {
/*
 * Seems to just get 4 bytes of data which is the index of the playing file
 */
		int fileIndex = 0;
		LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new ByteArrayInputStream(data));
		try {
			fileIndex = dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		loggerFileIndex(fileIndex);
		
	}

	private void loggerPlaybackUpdate(byte[] data, long size) {
/*
#define MAX_FILES_SUPPORTED         10
#define MAX_FILENAME_LENGTH         256
    UInt32 m_uiNumberOfRecords;   !< Number of records in total 
    UInt32 m_uiPercentProcessed;  !< Percent of total list processed 
    UInt32 m_uiNumberOfFiles;     !< Number of files processed or being processed 
    char   m_filenames[MAX_FILES_SUPPORTED][MAX_FILENAME_LENGTH];           !< List of filenames 
 */
		LoggerPlaybackUpdate loggerPlaybackUpdate = LoggerPlaybackUpdate.readData(data, size);
		if (loggerPlaybackUpdate != null) {
			newLoggerPlaybackUpdate(loggerPlaybackUpdate);
		}
	}

	/**
	 * Callback repackaged into something sensible from Svs5MessageType.LOGGER_PLAYBACK_UPDATE
	 * @param loggerPlaybackUpdate
	 */
	protected abstract void newLoggerPlaybackUpdate(LoggerPlaybackUpdate loggerPlaybackUpdate);

	/**
	 * for message Svs5MessageType.LOGGER_REC_UPDATE, create a SOutputFileInfo
	 * @param data
	 * @param size
	 */
	private void recUpdateMessage(byte[] data, long size) {
		OutputFileInfo outputFileInfo = OutputFileInfo.unpackMessageData(data);
		if (outputFileInfo != null) {
			recUpdateMessage(outputFileInfo);
		}
	}

	/**
	 * Get and unpack messages of type Svs5MessageType.LOGGER_STATUS_INFO
	 * @param data
	 * @param size
	 */
	private void loggerStatusInfo(byte[] data, long size) {
		LoggerStatusInfo loggerStatusInfo = new LoggerStatusInfo(data);
		loggerStatusInfo(loggerStatusInfo); // pass on to the abstract function
	}

	/**
	 * Called from Svs5MessageType.LOGGER_FILE_INDEX which passes a single
	 * int32 of the file index
	 * @param fileIndex
	 */
	public abstract void loggerFileIndex(int fileIndex);

	/**
	 * Callback from Svs5MessageType.LOGGER_STATUS_INFO
	 * @param loggerStatusInfo
	 */
	public abstract void loggerStatusInfo(LoggerStatusInfo loggerStatusInfo);

	public abstract void recUpdateMessage(OutputFileInfo outputFileInfo);

	public abstract void setFrameRate(int framesPerSecond);
	
	public abstract void newGLFLiveImage(GLFImageRecord glfImage);
	
	public abstract void newStatusPacket(GLFStatusData gemStatus);

	private void frameRateMessage(byte[] byteData) {
		// looks like this is a single int. It's 4 bytes. 
		LittleEndianDataInputStream li = new LittleEndianDataInputStream(new ByteArrayInputStream(byteData));
		int val = 0;
		try {
			val = li.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (verbose) {
			System.out.printf("Frame rate is %d (nFrames = %d)\n", val, nMessages[Svs5MessageType.GLF_LIVE_TARGET_IMAGE]);
		}
		setFrameRate(val);
	}

	private void glfLiveImage(byte[] byteData) {
		LittleEndianDataInputStream is = new LittleEndianDataInputStream(new ByteArrayInputStream(byteData));
		GLFGenericHeader header = new GLFGenericHeader();
		GLFImageRecord glfRecord = null; //new GLFImageRecord(null, 0, 0);
		try {
			header.read(is);
			glfRecord = new GLFImageRecord(header, null, 0, 0);
			glfFileCatalog.readGlfRecord(glfRecord, is, true);
		} catch (CatalogException e) {
			e.printStackTrace();
			return;
		}			
		if (verbose) {
			System.out.println("GLF REcord from sonar " + glfRecord.genericHeader.tm_deviceId);
		}
		newGLFLiveImage(glfRecord);
	}

	private void geminiStatusMsg(byte[] byteData) {
		try {
			GLFGenericHeader header = new GLFGenericHeader();
			LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new ByteArrayInputStream(byteData));
			header.read(dis);
			GLFStatusData gemStatus = new GLFStatusData(header);
			gemStatus.read(dis, true);
			newStatusPacket(gemStatus);
			String ip = "?";
			InetAddress iNA = InetAddress.getByName(String.valueOf(Integer.toUnsignedLong(gemStatus.m_sonarAltIp)));
			ip = iNA.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		if (verbose) {
		//			System.out.printf("Gem status packet of %d bytes: sonar %d, 0x%x %s\n", byteData.length,
		//					gemStatus.m_sonarId, gemStatus.m_sonarFixIp, ip);
		//		}
		catch (CatalogException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * @param verbose the verbose to set
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * In playback mode, incoming data from C are sent straight for processing 
	 * in the same thread. In non playback mode (i.e. acqusition) then data are put into 
	 * a queue so that the JNA call can return immediately into the C. 
	 * @return the playbackMode
	 */
	public boolean isPlaybackMode() {
		return playbackMode;
	}

	/**
	 * In playback mode, incoming data from C are sent straight for processing 
	 * in the same thread. In non playback mode (i.e. acqusition) then data are put into 
	 * a queue so that the JNA call can return immediately into the C. 
	 * @param playbackMode set playback mode
	 */
	public void setPlaybackMode(boolean playbackMode) {
		this.playbackMode = playbackMode;
	}
	
	public class callBackData {
		
		int messageId;
		long size;
		byte[] data;
		
		public callBackData(int messageId, long size, byte[] data) {
			super();
			this.messageId = messageId;
			this.size = size;
			this.data = data;
		}
	}
}
