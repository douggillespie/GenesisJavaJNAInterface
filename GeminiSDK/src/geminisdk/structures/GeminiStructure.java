package geminisdk.structures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;

import tritechgemini.fileio.LittleEndianDataInputStream;
import tritechgemini.fileio.LittleEndianDataOutputStream;

abstract public class GeminiStructure {
	

public static final int SVS5_CONFIG_ONLINE = 0;             // Enable/Disable streaming ( bool )
public static final int SVS5_CONFIG_RANGE = 1;              // Range ( 1 - 120 ) in meters ( double )
public static final int SVS5_CONFIG_GAIN = 2;               // Gain ( 1 - 100 ) in percentage ( int )
public static final int SVS5_CONFIG_SIMULATE_ADC = 3;       // Enable/Disable simulation ( bool )
public static final int SVS5_CONFIG_PING_MODE = 4;          // Ping configuration parametersin SequencerPingMode struct
public static final int SVS5_CONFIG_SOUND_VELOCITY = 5;     // Configure sound velocity in SequencerSosConfig ( Using sonar SOS or user configured )
public static final int SVS5_CONFIG_SONAR_ORIENTATION = 6;  // Sonar orientaton ( See ESvs5SonarOrientation structures )
public static final int SVS5_CONFIG_RANGE_RESOLUTION = 7;   // Configure sonar range resolution ( Only applies to 1200ik product)
public static final int SVS5_CONFIG_HIGH_RESOLUTION = 8;    // Configure sonar improved range resolution ( Only applies to 1200ik product)
public static final int SVS5_CONFIG_CHIRP_MODE = 9;         // Configure sonar chirp mode ( 0: Disabled, 1: Enabled, 2: Auto )
public static final int SVS5_CONFIG_LOG_RAW_GPS = 10;        // Log RAW GPS data
public static final int SVS5_CONFIG_LOG_RAW_COMPASS = 11;    // Log RAW Compass data
public static final int SVS5_CONFIG_CPU_PERFORMANCE = 12;    // Configure SDK based on the CPU performance (SonarImageQaulityLevel struct)
public static final int SVS5_CONFIG_APERTURE = 13;           // Configure sonar Aperture ( Can switch between 120 / 65 degrees )
public static final int SVS5_CONFIG_REBOOT_SONAR = 14;       // Reboot Gemini sonar
public static final int SVS5_CONFIG_LOG_GPS = 15;            // Log formatted GPS data
public static final int SVS5_CONFIG_LOG_COMPASS = 16;        // Log formatted Compass data
public static final int SVS5_CONFIG_OPEN_720IM_COM_PORT = 17; // Open 720im on regular com port ( See ComPortConfig struct in  GeminiStructuresPublic.h )
public static final int SVS5_CONFIG_AUX_PORT = 18;           // Configure Aux port ( See AuxPortConfig structure )
									// Note: Sonar should be online/pinging when sending the AUX port configuration message

	// Config logger (ECD/GLF)
public static final int SVS5_CONFIG_LOGGER = 19;             // Configure logger (default : GLF )

	// Record Logger
public static final int SVS5_CONFIG_FILE_LOCATION = 20;      // Configure default location ( const char* )
public static final int SVS5_CONFIG_REC = 21;                // Start/Stop logger ( bool )

	// Playback Logger
public static final int SVS5_CONFIG_PLAY_START = 22;         //List of files (struct ListOfFileNames )
public static final int SVS5_CONFIG_PLAY_FILE_INDEX = 23;    //File Index
public static final int SVS5_CONFIG_PLAY_PAUSE = 24;         //Playback in pause state
public static final int SVS5_CONFIG_PLAY_REPEAT = 25;        //Playback in the loopback state
public static final int SVS5_CONFIG_PLAY_STOP = 26;          //Stop playback
public static final int SVS5_CONFIG_PLAY_SPEED  = 27;         //0 for free running, 1: RealTime
public static final int SVS5_CONFIG_PLAY_FRAME = 28 ;        //Explicitly request frame


	/**
	 * Constructor for objects read from JNA
	 * @param inputBytes
	 */
	public GeminiStructure(byte[] inputBytes) {
		fromBytes(inputBytes);
	}
	
	/**
	 * constructor for new objects to send to JNA
	 */
	public GeminiStructure() {
		
	}
	
	/**
	 * Write data to output stream. endienness dealt with !
	 * @param dataOutput
	 * @return true if OK
	 */
	public abstract boolean toBytes(DataOutput dataOutput);
	
	public abstract int defaultCommand(); 
	
	/**
	 * Read data from input stream. endienness dealt with. 
	 * @param dataInput
	 * @param length of byte data in dataInput stream
	 * @return true if OK
	 */
	public abstract boolean fromBytes(DataInput dataInput, int length);
	
	public byte[] toBytes() {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(dataOutputSize());
		DataOutput dout = getDataOutput(bytesOut);
		if (toBytes(dout)) {
			return bytesOut.toByteArray();
		}
		return null;
	}
	
	private DataOutput getDataOutput(ByteArrayOutputStream bytesOut) {
		return new LittleEndianDataOutputStream(bytesOut);
	}
	
	public boolean fromBytes(byte[] data) {
		if (data == null) {
			return false;
		}
		return fromBytes(getDataInput(data), data.length);
	}

	private DataInput getDataInput(byte[] data) {
		return new LittleEndianDataInputStream(new ByteArrayInputStream(data));
	}

	/**
	 * initial guess of data output size. Fastest if it's about right. 
	 * @return
	 */
	protected abstract int dataOutputSize();
}
