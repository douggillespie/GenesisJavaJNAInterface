package geminisdk;

import java.io.File;

import com.sun.jna.Library;
import com.sun.jna.Native;



public class GeminiLoader {

//	private static final String libName = "C:\\PamguardCode2021\\GeminiSDK\\lib\\x64\\GeminiComms.dll";
	private static final String libName = "\\lib\\x64\\GeminiComms.dll";
	
	public GeminiLoader() {
		// TODO Auto-generated constructor stub
	}


	TG loadGeminiLibrary() throws Exception {
		
		System.out.println("Java class path " + System.getProperty("java.class.path"));
		
		String libraryPath;// = findLibrary();
		libraryPath = "C:\\Program Files\\Tritech\\Genesis\\Application Files\\GeminiComms.dll";
		
		TG gf = null;

		try {
			gf = Native.load(libraryPath, TG.class);
		}
		catch (Error e) {
			e.printStackTrace();
			throw new Exception(String.format("Tritech %s is not available: %s", libraryPath, e.getMessage()));
		}
		return gf;
	}
	
	private String findLibrary() {
		String classPath = System.getProperty("java.class.path");
		String[] paths = classPath.split(";");
		for (int i = 0; i < paths.length; i++) {
			String lib = findLibrary(paths[i]);
			if (lib != null) {
				return lib;
			}
		}
		return null;
	}
	
	private String findLibrary(String path) {
		File filePath = new File(path);
		while (filePath != null) {
			String newPath = filePath.getAbsolutePath()+libName;
			File newFile = new File(newPath);
			if (newFile.exists()) {
				return newPath;
			}
			filePath = filePath.getParentFile();
		}
		return null;
	}

	public interface TG extends Library {
		public int GEM_StartGeminiNetworkWithResult(short sonarID);
		void GEM_SetGeminiSoftwareMode(char[] softwareMode);
//		void GEM_SetHandlerFunction(void (cdecl *FnPtr)(int eType, int len, char[] dataBlock));
		void GEM_ResetInternalCounters();
		void GEM_StopGeminiNetwork();
		void GEM_SetSurveyNetworkMode(char[] hostname,  int port,  short format,  int sampRate);

		
	}	

}
