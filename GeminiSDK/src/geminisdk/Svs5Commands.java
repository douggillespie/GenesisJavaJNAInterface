package geminisdk;

import geminisdk.GenesisSerialiser.GlfLib;
import geminisdk.structures.GeminiStructure;
import tritechgemini.fileio.LittleEndianDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;


/**
 * Wrapper around the jna interface to send objects to SvS5 
 * @author dg50
 *
 */
public class Svs5Commands {

	public Svs5Commands() {
		
	}


	public int setConfiguration(GeminiStructure geminiStructure) throws Svs5Exception {
		return setConfiguration(geminiStructure, 0);
	}
	
	/**
	 * Send a config structure to the Gemini. Use the default command for 
	 * the structure and specified deviceId.
	 * @param geminiStructure Gemin control structure. 
	 * @param deviceId device id (not sure if this is index or id !). 
	 * @return Error code from Svs5ErrorType
	 * @throws Svs5Exception 
	 */
	public int setConfiguration(GeminiStructure geminiStructure, int deviceId) throws Svs5Exception {
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
	public int setConfiguration(int commandId, GeminiStructure geminiStructure, int deviceId)  throws Svs5Exception {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			throw new Svs5Exception("No Svs5Library");
		}
		byte[] data = null;
		int dataLength = 0;
		if (geminiStructure != null) {
			data = geminiStructure.toBytes();
			dataLength = data.length;
			dataLength = Math.max(dataLength, geminiStructure.dataOutputSize());
		}
		
//		ByteByReference bbr = new ByteByReference();
//		bbr.setValue(data[0]);
		
		return lib.svs5SetConfiguration(commandId, dataLength, data, deviceId);
	}
	
	public int setPingMode(boolean freeRun, short msInterval) throws Svs5Exception {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			throw new Svs5Exception("No Svs5Library");
		}
		return lib.setPingMode(freeRun, msInterval, 0);
		
	}


	/**
		 * Get a config structure from the Gemini. Use the default command for 
		 * the structure and specified deviceId.
		 * @param commandId overrides command in the gemini control structuer
		 * @param geminiStructure Gemini control structure must exist with appropriate memory allocation. 
		 * @param deviceId device id (not sure if this is index or id !). 
		 * @return Error code from Svs5ErrorType
		 */
		public int getConfiguration(int commandId, GeminiStructure geminiStructure, int deviceId)  throws Svs5Exception {
			GlfLib lib = GenesisSerialiser.getLibrary();
			if (lib == null) {
				throw new Svs5Exception("No Svs5Library");
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


	public long sendStringCommand(int commandId, String string, int deviceId)  throws Svs5Exception {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			throw new Svs5Exception("No Svs5Library");
		}
		byte[] data = string.getBytes();
		int len = data.length+1;
		data = Arrays.copyOf(data, len+1);
		return lib.svs5SetConfiguration(commandId, len, data, deviceId);
	}
	
	public String getStringCommand(int commandId, int maxLength, int deviceId)  throws Svs5Exception {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			throw new Svs5Exception("No Svs5Library");
		}
		byte[] data = new byte[maxLength];
//		data[0] = 1;
		int err = lib.svs5GetConfiguration(commandId, maxLength, data, deviceId);
		if (err == 0) {
			return new String(data);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Send a double command. 
	 * @param commandId
	 * @param command
	 * @param deviceId
	 * @return
	 * @throws Svs5Exception
	 */
	public int setDoubleCommand(int commandId, double command, int deviceId)  throws Svs5Exception {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			throw new Svs5Exception("No Svs5Library");
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream(Double.BYTES);
		LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(bos);
		try {
			dos.writeDouble(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] value = bos.toByteArray();
		return lib.svs5SetConfiguration(commandId, value.length, value, deviceId);
	}

	
	public int setBoolCommand(int commandId, boolean command, int deviceId)  throws Svs5Exception {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			throw new Svs5Exception("No Svs5Library");
		}
		byte[] value = new byte[1];
		value[0] = (byte) (command ? 1 : 0);
		return lib.svs5SetConfiguration(commandId, 1, value, deviceId);
	}
	
	public boolean getBoolCommand(int commandId, int deviceId) throws Svs5Exception {
		GlfLib lib = GenesisSerialiser.getLibrary();
		if (lib == null) {
			throw new Svs5Exception("No Svs5Library");
		}
		byte[] value = new byte[1];
		int ans = lib.svs5GetConfiguration(commandId, 1, value, deviceId);
		if (ans != Svs5ErrorType.SVS5_SEQUENCER_STATUS_OK) {
			throw new Svs5Exception(ans);
		}
		return value[0] == 0 ? false : true;
	}

}
