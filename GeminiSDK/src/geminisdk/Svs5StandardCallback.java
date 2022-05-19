package geminisdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import com.sun.jna.Pointer;

import geminisdk.GenesisSerialiser.GlfLib.Svs5Callback;
import geminisdk.structures.GemStatusPacket;
import tritechgemini.fileio.CatalogException;
import tritechgemini.fileio.GLFFileCatalog;
import tritechgemini.fileio.LittleEndianDataInputStream;
import tritechgemini.imagedata.GLFImageRecord;

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

		synchronized (callQueue) {
			byte[] byteData = null;
			if (pointer != null) {					
				byteData = pointer.getByteArray(0, (int) size);
			}
//			if (msgType == 2) return;
			callQueue.add(new callBackData(msgType, size, byteData.clone()));
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
		switch (item.messageId) {
		case Svs5MessageType.GEMINI_STATUS:
			geminiStatusMsg(item.data);
			break;
		case Svs5MessageType.GLF_LIVE_TARGET_IMAGE:
			glfLiveImage(item.data);
			break;
		case Svs5MessageType.FRAME_RATE:
			frameRateMessage(item.data);
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
	 * Callback from Svs5MessageType.LOGGER_STATUS_INFO
	 * @param loggerStatusInfo
	 */
	public abstract void loggerStatusInfo(LoggerStatusInfo loggerStatusInfo);

	public abstract void recUpdateMessage(OutputFileInfo outputFileInfo);

	public abstract void setFrameRate(int framesPerSecond);
	
	public abstract void newGLFLiveImage(GLFImageRecord glfImage);
	
	public abstract void newStatusPacket(GemStatusPacket statusPacket);

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
		GLFImageRecord glfRecord = new GLFImageRecord(null, 0, 0);
		try {
			glfFileCatalog.readGlfRecord(glfRecord, is, true);
		} catch (CatalogException e) {
			e.printStackTrace();
			return;
		}			
		if (verbose) {
			System.out.println("GLF REcord from sonar " + glfRecord.tm_deviceId);
		}
		newGLFLiveImage(glfRecord);
	}

	private void geminiStatusMsg(byte[] byteData) {
		GemStatusPacket gemStatus = new GemStatusPacket(byteData);
		String ip = "?";
		try {
			InetAddress iNA = InetAddress.getByName(String.valueOf(Integer.toUnsignedLong(gemStatus.m_sonarAltIp)));
			ip = iNA.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (verbose) {
			System.out.printf("Gem status packet of %d bytes: sonar %d, 0x%x %s\n", byteData.length,
					gemStatus.m_sonarId, gemStatus.m_sonarFixIp, ip);
		}
		newStatusPacket(gemStatus);
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
