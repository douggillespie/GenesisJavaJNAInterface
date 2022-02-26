package geminisdk;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

import geminisdk.GenesisSerialiser.GlfLib;
import geminisdk.GenesisSerialiser.GlfLib.SigActiveFileIndex;
import geminisdk.GenesisSerialiser.GlfLib.SigInformationString;
import geminisdk.GenesisSerialiser.GlfLib.SigPlaybackInfo;
import geminisdk.GenesisSerialiser.GlfLib.Svs5Callback;
import geminisdk.structures.ChirpMode;
import geminisdk.structures.ConfigOnline;
import geminisdk.structures.PingMode;
import geminisdk.structures.RangeFrequencyConfig;
import geminisdk.structures.SimulateADC;

public class GeminiTest extends GeminiLoader {

	public GeminiTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		

		System.out.printf("Running %s bit platform\n", System.getProperty("os.arch"));
		GeminiTest gt = new GeminiTest();
		
			
		gt.glfCommsTest();
//		gt.glfFileReadTest();
		gt.Svs5Test();
		
	}
	
	private void Svs5Test() {
		System.out.println("**************************** Svs5 Tests ***************************");
		GlfLib gSer = GenesisSerialiser.getLibrary();
		if (gSer == null) {
			return;
		}
		Svs5Commands svs5Commands = new Svs5Commands();
//		int c = gSer.pgadd(3,6);
		String sv5Inf = gSer.svs5GetLibraryVersionInfo();
		System.out.println(sv5Inf);
//		byte[] sv5Inf = gSer.JGetLibraryVersionInfo();
//		System.out.println(sv5Inf);
		long ans1 = gSer.svs5StartSvs5(new Svs5Callback() {
			
			@Override
			public void callback(int msgType, long size, String data) {
				System.out.println("SvS5 callback " + msgType);
			}
		});
		
		long err;
		ChirpMode chirpMode = new ChirpMode(ChirpMode.CHIRP_AUTO);
		err = svs5Commands.setConfiguration(chirpMode);
		System.out.println("setConfiguration chirpMode returned " + err);
			

		RangeFrequencyConfig rfConfig = new RangeFrequencyConfig();
		err = svs5Commands.setConfiguration(rfConfig);
		System.out.println("setConfiguration returned " + err);
		
		SimulateADC simADC = new SimulateADC(true);
		err = svs5Commands.setConfiguration(simADC);
		System.out.println("Simulate returned " + err);

		PingMode pingMode = new PingMode();
		err = svs5Commands.setConfiguration(pingMode);
		System.out.println("setConfiguration pingMode returned " + err);
		
		ConfigOnline cOnline = new ConfigOnline(true);
		err = svs5Commands.setConfiguration(cOnline);
		System.out.println("setOnline returned " + err);
		
//		try {
//			Thread.sleep(4000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		cOnline.value = false;
		err = svs5Commands.setConfiguration(cOnline);
		System.out.println("setoffline returned " + err);
		
		
		
		long ans2 = gSer.svs5StopSvs5();
		System.out.printf("SvS5 started and stopped with code %d and %d\n", ans1, ans2);
		
		
		int val = gSer.valueTest();
		System.out.println("Value test returned " + val);
		
	}

	/**
	 * Check we can call GEM functions direct across JNA. 
	 */
	void glfCommsTest() {
		TG gf = null;
		try {
			gf = new GeminiLoader().loadGeminiLibrary();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int result = gf.GEM_StartGeminiNetworkWithResult((short) 0);
		System.out.println("Result network start: " + result );
		
		gf.GEM_SetHandlerFunction(new HandlerCallback());
		
		byte[] swMode = new String("EvoC").getBytes();
		gf.GEM_SetGeminiSoftwareMode(swMode);
		
		short[] sonarList = new short[8];
		int n = gf.GEMX_GetSonars(sonarList);
		System.out.println("Devices found: " + n);
		
		byte versionNumber = gf.GEM_GetDLLLinkVersionNumber();
		System.out.println("DLL version number " + versionNumber);
		
		int vLen = gf.GEM_GetVStringLen();
		int vNumLen = gf.GEM_GetVNumStringLen();
		byte[] version = new byte[vLen+1];
		byte[] vNum = new byte[vNumLen+1];
		gf.GEM_GetVString(version, vLen);
		gf.GEM_GetVNumString(vNum, vNumLen);
		System.out.println("Version: " + new String(version) + " " + new String(vNum));
		
		short id = 857;
		ByteByReference[] ipAddr = new ByteByReference[8];
		for (int i = 0; i < 8; i++) {
			ipAddr[i] = new ByteByReference();
		}
		
		gf.GEMX_GetAltSonarIPAddress(id, ipAddr[0], ipAddr[1], ipAddr[2], ipAddr[3], ipAddr[4], ipAddr[5], ipAddr[6], ipAddr[7]);
		byte[] ipBytes = new byte [8];
		for (int i = 0; i < 8; i++) {
			ipBytes[i] = ipAddr[i].getValue();
			System.out.print(ipBytes[i]);
		}
		System.out.println();
		
		byte[] name = new byte[128];
		gf.GEMX_GetDeviceName((short)-1, name, 128);
		System.out.println("Device name is  " + new String(name));
//
//		gf.FT_GetDeviceInfoList(0, name, 128);
//		System.out.println("Device name is  " + new String(name));
		
//		String list = gf.FT_GetDeviceInfoList();
		
//		byte[] swMode = new byte[30];
//		gf.GEM_
		
		gf.GEM_StopGeminiNetwork();
	}
	
	/**
	 * Test opening one or more glf files to check data can be read across the JNA. 
	 */
	void glfFileReadTest() {
		/*
		 * Now try to open the serialiser library and open then close a file reader stream 
		 * 
		 */	
		GlfLib gSer = GenesisSerialiser.getLibrary();
		if (gSer == null) {
			return;
		}
		
		PointerByReference readerHandle = new PointerByReference();
		SigActiveFileIndex sigActiveFileIndex = new SigActiveFileIndex() {	
			@Override
			public void callback(int fileIndex) {
				System.out.println("Callback: Active file index is " + fileIndex);
			}
		};
		
		SigPlaybackInfo sigFilelistInfo = new SigPlaybackInfo() {
			
			@Override
			public void callback(int numRecords, int percentProcessed, int numFiles) {
				System.out.printf("Callback: numRecords: %d, percent processed: %d, num Files: %d\n",
						numRecords, percentProcessed, numFiles);
			}
		};
		
		SigInformationString sigInFormationString  = new SigInformationString() {
			
			@Override
			public void callback(int error, String str) {
				System.out.printf("Callback error code %d message %s", error, str);
			}
		};
		
		int ans;
				
		
		String[] fileList = {"C:\\ProjectData\\RobRiver\\glfexamples\\log_2021-04-10-085615.glf",
				"C:\\ProjectData\\RobRiver\\glfexamples\\log_2021-04-10-090655.glf"};
//		String[] fileList = {"C:\\ProjectData\\RobRiver\\glfexamples\\log_2021-04-10-090655.glf"};
		char[][] charList = new char[fileList.length][];
		for (int i = 0; i < fileList.length; i++) {
			charList[i] = fileList[i].toCharArray();
		}
		long t1 = System.nanoTime();
		ans = gSer.glfCreateLogFileReader(readerHandle, fileList, fileList.length, 
				sigActiveFileIndex, sigFilelistInfo, sigInFormationString, false);
		long t2 = System.nanoTime();
		System.out.printf("CreateLogReader return is %d after %3.3fs\n", ans, (double)(t2-t1)/1.e9);
		IntByReference recordNumber = new IntByReference();
		for (int i = 0; i < fileList.length; i++) {
			ans = gSer.glfGetFileStartPosition(readerHandle.getValue(), i, recordNumber);
			System.out.printf("File %d starts at record %d\n", i, recordNumber.getValue());
		}
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		PamGlfRecord.ByReference glfRecord = new PamGlfRecord.ByReference();
		ans = gSer.glfGetRecord(readerHandle.getValue(), 10, glfRecord);
		double[] bearingTable = glfRecord.getBearingTable();
		byte[] imageData = glfRecord.getImageData();
		System.out.printf("Get record return is 0X%X with %d bearings\n", ans, glfRecord.nBearing);
		
			 ans = gSer.glfCloseLogFileHandler(readerHandle.getPointer());
			System.out.printf("Close return is 0x%x\n", ans);
			
//		}
		
	}

}
