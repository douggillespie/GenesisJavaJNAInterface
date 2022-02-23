package geminisdk;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
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

	GlfLib loadGeminiLibrary() throws Exception {
		
		GlfLib glfLib = null;
		
		/**
		 * If jna path is not set, then use the library path everything else uses. 
		 */
		String jnaPath = System.getProperty("jna.library.path");
		if (jnaPath == null) {
			String javaPath = System.getProperty("java.library.path");
			if (javaPath != null) {
				System.setProperty("jna.library.path", javaPath);
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
			e.printStackTrace();
			throw new Exception(String.format("Tritech %s is not available: %s", libName, e.getMessage()));
		}
		
		return glfLib;
	}

	public interface GlfLib extends Library {
		
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
		
		int pgCreateLogFileReader(PointerByReference glf_Handle, String[] fileNames, int nFiles, 
				SigActiveFileIndex sigActiveFileIndex, SigPlaybackInfo sigPlaybackInfo, SigInformationString sigInformationString, 
				boolean asynchronous);
		
		int pgGetFileStartPosition(Pointer readHandle, int uiFileIndex, IntByReference filePosition);
				
		int pgCloseLogFileHandler(Pointer readerHandle);
		
		int pgGetRecord(Pointer readerHandle, int recordIndex, PamGlfRecord.ByReference glfRecord);
	}
}
