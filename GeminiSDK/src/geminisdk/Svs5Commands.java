package geminisdk;

import geminisdk.GenesisSerialiser.GlfLib;
import geminisdk.structures.GeminiStructure;

import com.sun.jna.ptr.ByteByReference;

/**
 * Wrapper around the jna interface to send objects to SvS5 
 * @author dg50
 *
 */
public class Svs5Commands {

	public Svs5Commands() {
		
	}


	public long setConfiguration(GeminiStructure geminiStructure) {
		return setConfiguration(geminiStructure, 0);
	}
	
	/**
	 * Send a config structure to the Gemini. Use the default command for 
	 * the structure and specified deviceId.
	 * @param geminiStructure Gemin control structure. 
	 * @param deviceId device id (not sure if this is index or id !). 
	 * @return Error code from Svs5ErrorType
	 */
	public long setConfiguration(GeminiStructure geminiStructure, int deviceId) {
		return setConfiguration(geminiStructure.defaultCommand(), geminiStructure, deviceId);
	}

	/**
	 * Send a config structure to the Gemini. Use the default command for 
	 * the structure and specified deviceId.
	 * @param commandId overrides command in the gemini control structuer
	 * @param geminiStructure Gemin control structure. 
	 * @param deviceId device id (not sure if this is index or id !). 
	 * @return Error code from Svs5ErrorType
	 */
	public long setConfiguration(int commandId, GeminiStructure geminiStructure, int deviceId) {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			return -1;
		}
		byte[] data = null;
		int dataLength = 0;
		if (geminiStructure != null) {
			data = geminiStructure.toBytes();
			dataLength = data.length;
		}
		
//		ByteByReference bbr = new ByteByReference();
//		bbr.setValue(data[0]);
		
		return lib.svs5SetConfiguration(commandId, dataLength, data, deviceId);
	}
	
	/**
	 * Get a config structure from the Gemini. Use the default command for 
	 * the structure and specified deviceId.
	 * @param commandId overrides command in the gemini control structuer
	 * @param geminiStructure Gemini control structure must exist with appropriate memory allocation. 
	 * @param deviceId device id (not sure if this is index or id !). 
	 * @return Error code from Svs5ErrorType
	 */
	public int getConfiguration(int commandId, GeminiStructure geminiStructure, int deviceId) {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			return -1;
		}
		byte[] data = geminiStructure.toBytes();

//		ByteByReference bbr = new ByteByReference();
//		bbr.setValue(data[0]);
		
		int ans = lib.svs5GetConfiguration(commandId, data.length, data, deviceId);
		
		if (ans == 0) {
			geminiStructure.fromBytes(data);
		}
		
		return ans;
		
	}

}
