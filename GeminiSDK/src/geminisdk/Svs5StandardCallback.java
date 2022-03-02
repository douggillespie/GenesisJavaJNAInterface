package geminisdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

	public Svs5StandardCallback() {
		glfFileCatalog = new GLFFileCatalog(null);
	}

	@Override
	public void callback(int msgType, long size, Pointer data) {
		processSv5Callback(msgType, size, data);
	}

	private void processSv5Callback(int msgType, long size, Pointer data) {

		nMessages[msgType]++;

		byte[] byteData = null;
		if (data != null) {					
			byteData = data.getByteArray(0, (int) size);
		}
		switch (msgType) {
		case Svs5MessageType.GEMINI_STATUS:
			geminiStatusMsg(byteData);
			break;
		case Svs5MessageType.GLF_LIVE_TARGET_IMAGE:
			glfLiveImage(byteData);
			break;
		case Svs5MessageType.FRAME_RATE:
			frameRateMessage(byteData);
			break;
		default:
			System.out.printf("SvS5 callback type %d size %d \n", msgType, size);

		}
	}
	
	public abstract void setFrameRate(int framesPerSecond);
	
	public abstract void newGLFLiveImage(GLFImageRecord glfImage);
	
	public abstract void newStatusPacket(GemStatusPacket statusPacket);

	private void frameRateMessage(byte[] byteData) {
		// looks like this is a single float or int. It's 4 bytes. 
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
}
