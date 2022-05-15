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
		default:
			System.out.printf("SvS5 callback type %d size %d \n", item.messageId, item.size);

		}
		
	}
	
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
