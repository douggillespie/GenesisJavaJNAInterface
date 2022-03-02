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
	
	public long setConfiguration(GeminiStructure geminiStructure, int deviceId) {
		return setConfiguration(geminiStructure.defaultCommand(), geminiStructure, deviceId);
	}
	
	public long setConfiguration(int commandId, GeminiStructure geminiStructure, int deviceId) {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			return -1;
		}
		byte[] data = geminiStructure.toBytes();
		
//		ByteByReference bbr = new ByteByReference();
//		bbr.setValue(data[0]);
		
		return lib.svs5SetConfiguration(commandId, data.length, data, deviceId);
	}
	
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
