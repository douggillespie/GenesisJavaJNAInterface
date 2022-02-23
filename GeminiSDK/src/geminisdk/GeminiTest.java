package geminisdk;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import geminisdk.GenesisSerialiser.GlfLib;
import geminisdk.GenesisSerialiser.GlfLib.SigActiveFileIndex;
import geminisdk.GenesisSerialiser.GlfLib.SigInformationString;
import geminisdk.GenesisSerialiser.GlfLib.SigPlaybackInfo;

public class GeminiTest extends GeminiLoader {

	public GeminiTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		

		System.out.printf("Running %s bit platform\n", System.getProperty("os.arch"));
		GeminiTest gt = new GeminiTest();
		
			
		gt.glfCommsTest();
//		gt.glfFileReadTest();
		
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
		System.out.println("Result network start: " + result);
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
		GlfLib gSer = null;
		try {
			gSer = new GenesisSerialiser().loadGeminiLibrary();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		ans = gSer.pgCreateLogFileReader(readerHandle, fileList, fileList.length, 
				sigActiveFileIndex, sigFilelistInfo, sigInFormationString, false);
		long t2 = System.nanoTime();
		System.out.printf("CreateLogReader return is %d after %3.3fs\n", ans, (double)(t2-t1)/1.e9);
		IntByReference recordNumber = new IntByReference();
		for (int i = 0; i < fileList.length; i++) {
			ans = gSer.pgGetFileStartPosition(readerHandle.getValue(), i, recordNumber);
			System.out.printf("File %d starts at record %d\n", i, recordNumber.getValue());
		}
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		PamGlfRecord.ByReference glfRecord = new PamGlfRecord.ByReference();
		ans = gSer.pgGetRecord(readerHandle.getValue(), 10, glfRecord);
		double[] bearingTable = glfRecord.getBearingTable();
		byte[] imageData = glfRecord.getImageData();
		System.out.printf("Get record return is 0X%X with %d bearings\n", ans, glfRecord.nBearing);
		
			 ans = gSer.pgCloseLogFileHandler(readerHandle.getPointer());
			System.out.printf("Close return is 0x%x\n", ans);
			
//		}
		
	}

}
