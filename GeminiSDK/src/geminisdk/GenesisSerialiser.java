package geminisdk;

import java.io.File;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;


/**
 * Access records from Tritech Genesis glf files. Currently only supporting Gemini imaging 
 * sonar records. <p>
 * It proved impossible to call the Genesis dll's directly, so had to wrap them in my own 
 * dll which the functions in the class access through jna.  
 * @author dg50
 *
 */
public class GenesisSerialiser {
	
	/*
	 * Glf structres are very complicated, and worse, the functions are behind a namespace
	 * which makes them impossible to call through jna. Have therefore written wrapper functions
	 * and classes to transfer data via my own DLL.  
	 */
	private static final String libName = "GENESISJAVAINTERFACE.dll";
	
	// error codes copied from GlfApi.h
	public static final int  GLF_ERROR_CODE =                  ( 0x01000000 );
	public static final int  GLF_OK  =                         ( 0 );
	public static final int  GLF_LOGGER_NOT_CREATED  =         ( GLF_ERROR_CODE | 1 );
	public static final int  GLF_INVALID_HANDLE      =         ( GLF_ERROR_CODE | 2 );
	public static final int  GLF_DATA_NOT_AVAILABLE  =         ( GLF_ERROR_CODE | 3 );
	public static final int  GLF_FAILED_TO_WRITE     =         ( GLF_ERROR_CODE | 4 );
	public static final int  GLF_INVALID_PARAM       =         ( GLF_ERROR_CODE | 5 );
	public static final int  GLF_BUSY_PROCESSING_INDEX_TABLE = ( GLF_ERROR_CODE | 6 );
	
	private static GlfLib glfLib = null;
	
	private static int loadFails = 0;

	public static GlfLib getLibrary() {
		if (glfLib == null && loadFails++ < 2) {
			try {
				glfLib = loadGeminiLibrary();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return glfLib;
	}
	
	private static GlfLib loadGeminiLibrary() throws Exception {
		
		
		/**
		 * If jna path is not set, then use the library path everything else uses. 
		 */
		String jnaPath = System.getProperty("jna.library.path");
//		jnaPath +=";C:\\Program Files\\Tritech\\Gemini SDK\\bin\\x64";
		if (jnaPath == null) {
			String javaPath = System.getProperty("java.library.path");
			if (javaPath != null) {
				javaPath +=File.pathSeparator+"C:\\Program Files\\Tritech\\Gemini SDK\\bin\\x64";
				System.setProperty("jna.library.path", javaPath);
				System.setProperty("java.library.path", javaPath);
			}
		}
		System.out.println("JAVA library path is " + System.getProperty("java.library.path"));
		System.out.println("JNA library path is " + System.getProperty("jna.library.path"));

		/*
		 * Two dependencies for my own tritech lib are GenesisSerializer.dll and zlibwapi.dll and these must be 
		 * in the same folder as the main library, or in the windows system path
		 */
		try {
			glfLib = Native.load(libName, GlfLib.class);
		}
		catch (Error e) {
//			e.printStackTrace();
//			throw new Exception(String.format("Tritech %s is not available: %s", libName, e.getMessage()));
			System.out.println(String.format("Tritech %s is not available: %s", libName, e.getMessage()));
		}
		
		return glfLib;
	}

	public interface GlfLib extends Library {

		/**
		 * Callback function for Svs5SeqLib
		 */
		public interface Svs5Callback extends Callback {
			public void callback(int msgType, long size, Pointer data);
		}
		
		/**
		 * Wrapper functions for Svs5SeqLib functions The Set and Get configuration are going to be a right royal 
		 * pain in the arse, since they basically go to switch statements which cast the configData into a particular
		 * object type, many of which are enums, so not even entirely clear what their values are going to be on the 
		 * C side of things. A specific structure needs to be sent with each configType. There are two options on how
		 * we can handle this. 1) assume enum values and work out their size, then send byte arrays of data through 
		 * the existing commands. For this to work, we'll have to make a Java object for each structure and include a
		 * byte reader and writer in it which can easily send to the C or 2) write a separate C function for each of the 
		 * configTypes and only worry about the read C type on the C side. Most config's only need a couple of parameters
		 * so can largely be done as primitives, using referenced primitives to return values.
		 * For instance, if we look at the FREQEUNCY enum in  GeminiStructuresPublic, their size is 4 and their values are
		 * 0,1,2 in order, so they are basically 32 bit integers. 
		 * Then of course there is endienness ! Java and C are different on Windows, so will have to use 
		 * specialist input and output streams for the bodge to work. 
		 * bool seems to be a single byte.  
		 * Guess I'll have to try both and see which is least painful. 
		 */
		public String svs5GetLibraryVersionInfo();
		
		public long svs5StartSvs5(Svs5Callback svs5Callback);
		
		public int svs5SetConfiguration(int configType, int size, byte[] configData, int deviceId);
		
		public int svs5GetConfiguration(int configType, int size, byte[] configData, int deviceId);
		
		public long svs5StopSvs5();
		
		public int setInputFileList(String[] fileNames, int nFiles);
		
		public int setPingMode(boolean freeRun, short msInterval, int deviceId);
		
		/**
		 * Set the speed of sound mode
		 * @param useUserSos Use a fixed speed of sound set by the user
		 * @param manualSoS Manual speed of sound set by the user
		 * @param deviceId device Id. 
		 * @return
		 */
		public int setSoSConfig(boolean useUserSoS, double manualSoS, int deviceId);
		
		public int valueTest();
		
		
		/**
		 * Callback function
		 * @author dg50
		 *
		 */
		public interface SigActiveFileIndex extends Callback {
			public void callback(int fileIndex);
		}
		/**
		 * Callback from CreateLogFileReader. This is called once on completion when asynch == false or 
		 * multiple times when asynch is true. <p>
		 * In synchronous mode it will have been called back just before the function returns, so it should be
		 * possible to have a record of how many records there are in total and in how many files. However it 
		 * doesn't tell us much about which records are n which files.   
		 * @author dg50
		 *
		 */
		public interface SigPlaybackInfo extends StdCallCallback {
			public void callback(int numRecords, int percentProcessed, int numFiles);
		}
		public interface SigInformationString extends StdCallCallback {
			public void callback(int error, String str);
		}
		
		int glfCreateLogFileReader(PointerByReference glf_Handle, String[] fileNames, int nFiles, 
				SigActiveFileIndex sigActiveFileIndex, SigPlaybackInfo sigPlaybackInfo, SigInformationString sigInformationString, 
				boolean asynchronous);
		
		int glfGetFileStartPosition(Pointer readHandle, int uiFileIndex, IntByReference filePosition);
				
		int glfCloseLogFileHandler(Pointer readerHandle);
		
		int glfGetRecord(Pointer readerHandle, int recordIndex, PamGlfRecord.ByReference glfRecord);
	

	}
}
