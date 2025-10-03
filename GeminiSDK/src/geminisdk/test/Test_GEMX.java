package geminisdk.test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Set;

import com.sun.jna.Pointer;

import geminisdk.GenesisSerialiser;
import geminisdk.GenesisSerialiser.GlfLib;
import geminisdk.GenesisSerialiser.GlfLib.GEMXCallback;
import geminisdk.structures.CGemHdr;
import geminisdk.structures.CGemMessage;
import geminisdk.structures.CGemPingHead;
import geminisdk.structures.CGemPingLine;
import geminisdk.structures.CGemStatus;
import geminisdk.structures.ChirpMode;
import tritechgemini.fileio.LittleEndianDataInputStream;

public class Test_GEMX {

	private GlfLib lib;
	public static final int MAX_SONARS = 4;
	private short[] sonarsList = new short[MAX_SONARS];
//	private short[] sonars = {(short) 1637, (short) 2710};
	private short[] sonars = {(short) 2710};
	
	private volatile CGemStatus cGemStatus;
	
	private HashMap<Integer, Integer> messageCount = new HashMap<>();
	private volatile long globPingStart;
	private volatile long lastPingSent;
	public long lastDataTime;

	public Test_GEMX() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Test_GEMX testGEMX = new Test_GEMX();
		try {
			testGEMX.runtest();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		testGEMX.lib.gemStopGeminiNetwork();
	}

	private void runtest() {
		lib = GenesisSerialiser.getLibrary();
		int res;
		GemCallback gemCallback = new GemCallback();
//		res = lib.gemStartGeminiNetworkWithResult(0);
//		System.out.println("res " + res);
		String swMode = "EvoC";
		lib.gemSetGeminiSoftwareMode(swMode);
		for (int i = 0; i < sonars.length; i++) {
			res = lib.gemStartGeminiNetworkWithResult(sonars[i]);
		}
		int nSonars = lib.gemxGetSonars(sonarsList);
		System.out.println("nsonars " + nSonars);
		for (int i = 0; i < MAX_SONARS; i++) {
			if (sonarsList[i] != 0) {
				System.out.printf("Sonar %d is %d\n", i, sonarsList[i]);
			}
		}
		lib.setGeminiCallback(gemCallback);
		long sStart = System.currentTimeMillis();
		for (int i = 0; i < 20; i++) {
			if (cGemStatus != null) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long sEnd = System.currentTimeMillis();
		System.out.printf("Status found after %d millis: %s\n", sEnd-sStart, cGemStatus);
		
		
		lib.gemxSetHeadType(sonars[0], CGemMessage.GEM_HEADTYPE_MK2_1200IK);
//		lib.gemxTxToAltIPAddress(sonars[0], 1);
		
		byte b255 = (byte) 255;
		byte[] ipBits = splitInteger(cGemStatus.m_sonarAltIp);
		lib.gemxUseAltSonarIPAddress(sonars[0], ipBits[3], ipBits[2],ipBits[1],ipBits[0], b255, b255, b255, (byte)0);
		lib.gemxTxToAltIPAddress(sonars[0], 1);
//		lib.gemxGetAltSonarIPAddress(nSonars, null, null, null, null, null, null, null, null);
//		lib.gemxUseAltSonarIPAddress(sonars[0], 0, 0, 0, 0, 0, 0, 0, 0);
		
//		lib.gemxConfigureChirpMode(sonars[0], 0);
		lib.gemxConfigureAutoRangeFrequency(sonars[0], CGemMessage.FREQUENCY_LOW, 20.);
		int freq = lib.gemxGetGeminiFrequency(sonars[0]);
//		int nBeam = lib.gemxGetGeminiBeams(sonars[0], null);
//		float beamData[] = new float[nBeam];
//		lib.gemxGetGeminiBeams(sonars[0], beamData);
		
		lib.gemxSetInterPingPeriod(sonars[0], 100000);
		lib.gemxSetVelocimeterMode(sonars[0], 0, 1);
		lib.gemxConfigureChirpMode(sonars[0], ChirpMode.CHIRP_DISABLED);
		lib.gemxAutoPingConfig(sonars[0], 55f, 100, 1458.f);
//		lib.gemxSendGeminiPingConfig(sonars[0]);
		lib.gemxSetPingMode(sonars[0], 0);
		
		// seems to take a few s to settle and be ready
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		globPingStart = System.currentTimeMillis();

		int pingsSent = 0;
		long tic = System.currentTimeMillis();
		while (System.currentTimeMillis() < tic + 4000) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			lastPingSent = System.currentTimeMillis();
			long tic2 = lastPingSent;
			lib.gemxSendGeminiPingConfig(sonars[0]);
			long toc2 = System.currentTimeMillis()-tic2;
			System.out.printf("Ping sent in %d millis at %s\n", toc2, System.currentTimeMillis()-globPingStart);
//			System.out.printf("%d", System.currentTimeMillis());
			pingsSent++;
		}
		// lib.gemxRebootSonar(sonars[0]);
		// wait another second for last messages to come through
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lib.gemStopGeminiNetwork();

		// summarise message counts
		System.out.printf("\nPings sent = %d\n", pingsSent);
		Set<Integer> keys = messageCount.keySet();
		
		for (Integer aKey : keys) {
			int c = messageCount.get(aKey);
			System.out.printf("Message %d %s : count %d\n",aKey, CGemMessage.toMessageName(aKey), c);
		}
		
		System.out.println("end");
		
	}
	
	private byte[] splitInteger(int intVal) {
		byte[] bytes = new byte[4];
		long l = Integer.toUnsignedLong(intVal);
		long mask = 0xFF;
		for (int i = 0; i < 4; i++) {
			bytes[i] = (byte) (l & mask);
			l = l>>8;
		}
		return bytes;
	}

	private class GemCallback implements GEMXCallback {

		@Override
		public void callback(int msgType, int size, Pointer cData) {
			byte[] data = null;
			LittleEndianDataInputStream dis; 
			CGemHdr cGemHdr;
			
			if (size > 0) {
				data = cData.getByteArray(0, (int) size);
			}
			Integer c = messageCount.get(msgType);
			if (c == null) {
				c = 0;
			}
			c++;
			messageCount.put(msgType, c);
			switch (msgType) {
			case CGemMessage.GEM_STATUS:
//				data = cData.getByteArray(0, (int) size);
				dis = new LittleEndianDataInputStream(new ByteArrayInputStream(data));
				cGemHdr = new CGemHdr();
				cGemHdr.read(dis);
				CGemStatus newStatus = new CGemStatus(cGemHdr);
				newStatus.read(dis);
				cGemStatus = newStatus;
//				System.out.println("GEM_STATUS: " + cGemHdr.toString());
//				GLFStatusData statusData = new GLFS
				break;
			case CGemMessage.PING_HEAD:
				dis = new LittleEndianDataInputStream(new ByteArrayInputStream(data));
				cGemHdr = new CGemHdr();
				cGemHdr.read(dis);
				CGemPingHead pingHead = new CGemPingHead(cGemHdr);
				pingHead.read(dis);
//				System.out.printf(",%d\n", pingHead.m_transmitTimestamp);
				break;
			case CGemMessage.PING_TAIL:
//				System.out.printf("%13s received at %dms after ping\n", CGemMessage.toMessageName(msgType), System.currentTimeMillis()-lastPingSent);
				break;
			case CGemMessage.PING_TAIL_EX:
				System.out.printf("%13s at %dms after ping, %dms after last data\n",  
						CGemMessage.toMessageName(msgType), System.currentTimeMillis()-lastPingSent,
						System.currentTimeMillis()-lastDataTime);
				int nBeam = lib.gemxGetGeminiBeams(sonars[0], null);
				float beamData[] = new float[nBeam];
				lib.gemxGetGeminiBeams(sonars[0], beamData);
				break;
			case CGemMessage.PING_DATA:
//				lastDataTime = System.currentTimeMillis();
				dis = new LittleEndianDataInputStream(new ByteArrayInputStream(data));
				cGemHdr = new CGemHdr();
				cGemHdr.read(dis);
				CGemPingLine pingLine = new CGemPingLine(cGemHdr);
				pingLine.read(dis);
				break;
			case 33:
				//no sense coming out of this. 
//				LittleEndianDataInputStream diStream = new LittleEndianDataInputStream(new ByteArrayInputStream(data));
//				try {
//					long iVal = diStream.readUnsignedInt();
//					iVal = Long.reverse(iVal);
//					System.out.println("Val msg 33 = " + iVal);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				break;
			default:
//				System.out.printf("Unknown GEMXCallback type %d (%s) size %d\n", msgType, CGemMessage.toMessageName(msgType), size);
				
			}
		}
		
	}

}
