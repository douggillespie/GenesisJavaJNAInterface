package geminisdk;

import java.io.File;

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
				javaPath += File.pathSeparator+"C:\\Program Files\\Tritech\\Gemini SDK\\bin\\x64";
				System.setProperty("jna.library.path", javaPath);
				System.setProperty("java.library.path", javaPath);
			}
		}
//		System.out.println("JAVA library path is " + System.getProperty("java.library.path"));
//		System.out.println("JNA library path is " + System.getProperty("jna.library.path"));

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
			return null;
		}
		
		/*
		 * Check the underlying dll versoin and the input version of the interface lib. 
		 * 
		 */
		String sdkVersion = glfLib.svs5GetLibraryVersionInfo();
		String interfaceVersion = null;
		try {
			interfaceVersion = glfLib.getSDKCompatibility();
		}
		catch (Error e) {
			System.out.println("You are using an old " + libName);
		}
		System.out.printf("PAMGuard JNA interface v%s, Tritech SDK version %s\n",
				sdkVersion, interfaceVersion);
		
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
		 * Callback function for gemxLib (probably the same as Svs5Callback
		 */
		public interface GEMXCallback extends Callback {
			public void callback(int msgType, int size, Pointer data);
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
		 * boolean seems to be a single byte.  
		 * Guess I'll have to try both and see which is least painful. 
		 */
		public String svs5GetLibraryVersionInfo();
		
		/**
		 * Get the SDK version the interface DLL was developed for. <p>
		 * This should be the same as the return from svs5GetLibraryVersionInfo()
		 * @return
		 */
		public String getSDKCompatibility();
		
		/**
		 * Call to SequencerApi::StartSvs5 - Starts Svs5 library, look for compatible network interface provided and start listening
         from Gemini network interface e.g. broadcast sonar status messages
		 * @param svs5Callback
		 * @return
		 */
		public long svs5StartSvs5(Svs5Callback svs5Callback);
		
		/**
		 * Call to Svs5SetConfiguration : Sets the configuration in the library so it can configure sonar
         accordingly.
		 * @param configType
		 * @param size
		 * @param configData
		 * @param deviceId
		 * @return
		 */
		public int svs5SetConfiguration(int configType, int size, byte[] configData, int deviceId);
		
		/**
		 * Call to Svs5GetConfiguration : Gets the last configuration set by the application.
		 * @param configType
		 * @param size
		 * @param configData
		 * @param deviceId
		 * @return
		 */
		public int svs5GetConfiguration(int configType, int size, byte[] configData, int deviceId);
		
		/**
		 * Stop Svs5 library, Stop listening from the Gemini network interface, release all allocated
         resources and exit
		 * @return
		 */
		public long svs5StopSvs5();
		
		/**
		 * 
		 * @param fileNames
		 * @param nFiles
		 * @return
		 */
		public int setInputFileList(String[] fileNames, int nFiles);
		
		/**
		 * Calls SequencerApi::Svs5SetConfiguration(SequencerApi::SVS5_CONFIG_PING_MODE
		 * @param freeRun
		 * @param msInterval
		 * @param deviceId
		 * @return
		 */
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
	
		/**
		 * GEMX commands
		 */
//		public void gemxSetPingMode(int sonarID, int pingMethod);
//		
//		public void gemxAutoPingConfig(int sonarID, float range, int gain, float sos);
//
//		public void gemxSendGeminiPingConfig(int sonarID);
//
//		public void gemxSetRangeCompression(int sonarID, int compressionLevel, int compressionType);
		
		

		public int setGeminiCallback(GEMXCallback fnCallback);
		/*************************************//**
		Purpose: Initialises the network interface ready to operate.

		Parameters:
		    sonarID             sonar ID

		Returns:                0:A failure occurred initialising the network interface.
		                          The most likely cause is that another piece of software using the
		                          Gemini library is already running, and so the port could not be opened
		                        1: The network initialised correctly and is ready to communicate.
		****************************************/
		public int gemStartGeminiNetworkWithResult(int sonarID);
		/*************************************//**
		Purpose: Set the library operating mode

		Parameters:
		    softwareMode        Evo: The Gemini library sends the data from the Gemini sonar head to the
		                        calling program unprocessed as CGemPingHead, CGemPingLine and
		                        CGemPingTailExtended messages using the callback function.

		                        EvoC: The Gemini library sends the data from the Gemini sonar head to the
		                        calling program unprocessed as CGemPingHead, CGemPingLine and
		                        CGemPingTailExtended messages using the callback function.
		                        The Gemini library makes use of the range compression feature of the
		                        sonar head firmware to ensure that the head does not return more range lines
		                        than is appropriate for the size and quality of the display being used by
		                        the calling program.

		                        SeaNet: The Gemini library processes the data and sends it to the
		                        calling program as CGemPingHead, CGemBearingData and CGemPingTailExtended
		                        messages using the callback function.

		                        SeaNetC: The Gemini library processes the data and sends it to the
		                        calling program as CGemPingHead, CGemBearingData and CGemPingTailExtended
		                        messages using the callback function.
		                        The Gemini library makes use of the range compression feature of the
		                        sonar head firmware to ensure that the head does not return 1500 or more
		                        range lines to the Seanet software.

		Returns:                None
		****************************************/
		public void gemSetGeminiSoftwareMode(String softwareMode);

		/*************************************//**
		Purpose: The parent code install the callback function which will be used by the library to return
		         data from the sonar head to the parent code.

		Parameters:
		    FnPtr               CALLBACK function

		Returns:                None
		****************************************/
//		public void gemSetHandlerFunction(void (cdecl *FnPtr)(int eType, int len, char *dataBlock));

		/*************************************//**
		Purpose: Resets internal counters held by the library.

		Returns:                None
		****************************************/
		public void gemResetInternalCounters();

		/*************************************//**
		Purpose: Closes the network interface and stops the internal tasks running.

		Returns:                None
		****************************************/
		public void gemStopGeminiNetwork();

		/*************************************//**
		Purpose: Returns the maximum version of the data link protocol that the library can support.

		* Currently DLL link version is 2.

		Returns:                DLL Link Version
		****************************************/
		public char gemGetDLLLinkVersionNumber();

		/*************************************//**
		Purpose: Returns the length of the version string which identifies the library

		Returns:                Length of version string
		****************************************/
		public int  gemGetVStringLen();

		/*************************************//**
		Purpose: Gets the copy of version string in the data buffer

		* e.g. 'Gemini Comms V2.0.0 Tritech International Ltd.'

		Parameters:
		    data                User specified buffer
		    len                 Length of the data buffer

		Returns:                None
		****************************************/
		public void gemGetVString(char[] data, int len);

		/*************************************//**
		Purpose: Returns the length of the version number string which identifies the library

		Returns:                Length of version string
		****************************************/
		public int  gemGetVNumStringLen();

		/*************************************//**
		Purpose: Gets the copy of version number string in the data buffer

		* e.g. 'V2.0.0'

		Parameters:
		    data                User specified buffer
		    len                 Length of the data buffer

		Returns:                None
		****************************************/
		public void gemGetVNumString(char[] data, int len);


		/**************************************************************************************************/
		/*                                 INITIALISATION API's                                           */
		/**************************************************************************************************/


		/*************************************//**
		Purpose: Sets the compression factor in EVOC mode so that less than number of range lines are returned
		         in each mode.

		Parameters:
		    sonarID             sonar ID
		    evoQualitySetting   Quality Settings : [0-7]
		                        [7]   = 4096;
		                        [6]   = 2048;
		                        [5]   = 1024;
		                        [4]   = 512;
		                        [3]   = 256;
		                        [2]   = 128;
		                        [1]   = 64;
		                        [0]   = 32;

		Returns:                None
		****************************************/
		public void gemxSetGeminiEvoQuality(int sonarID, char evoQualitySetting);

		/*************************************//**
		Purpose: This is required at the start of initialisation to configure the library for the
		         correct sonar head type.

		Parameters:
		    sonarID             sonar ID
		    headType            One of the head type

		                        gemHEADTYPE_720I
		                        gemHEADTYPE_720ID
		                        gemHEADTYPE_NBI
		                        gemHEADTYPE_MK2_720IS
		                        gemHEADTYPE_MK2_720IK

		Returns:                None
		****************************************/
		public void gemxSetHeadType(int sonarID, int headType);


		/*************************************//**
		Purpose: Gets the alternate IP address and subnet mask of the Gemini Sonar.
		         e.g. IP address : 192.168.2.201, Subnet : 255.255.255.0

		Parameters:
		    sonarID             Sonar ID
		    a1                  a1 = 192
		    a2                  a2 = 168
		    a3                  a3 = 2
		    a4                  a4 = 201
		    s1                  s1 = 255
		    s2                  s2 = 255
		    s3                  s3 = 255
		    s4                  s4 = 0

		Returns:                None
		****************************************/
		public void gemxGetAltSonarIPAddress(int sonarID,
		                             char[] a1,  char[] a2,  char[] a3,  char[] a4,
		                             char[] s1,  char[] s2,  char[] s3,  char[] s4);

		/*************************************//**
		Purpose: Sets the new alternate IP address and subnet mask on the Gemini Sonar head.
		         e.g. IP address : 192.168.2.201, Subnet : 255.255.255.0

		Parameters:
		    sonarID             Sonar ID
		    a1                  a1 = 192
		    a2                  a2 = 168
		    a3                  a3 = 2
		    a4                  a4 = 201
		    s1                  s1 = 255
		    s2                  s2 = 255
		    s3                  s3 = 255
		    s4                  s4 = 0

		Returns:                None
		****************************************/
		public void gemxSetAltSonarIPAddress(int sonarID,
		                             char a1,  char a2,  char a3,  char a4,
		                             char s1,  char s2,  char s3,  char s4);

		/*************************************//**
		Purpose: Uses the alternate IP address and subnet mask of the Gemini Sonar for communication.
		         e.g. IP address : 192.168.2.201, Subnet : 255.255.255.0

		Parameters:
		    sonarID             Sonar ID
		    a1                  a1 = 192
		    a2                  a2 = 168
		    a3                  a3 = 2
		    a4                  a4 = 201
		    s1                  s1 = 255
		    s2                  s2 = 255
		    s3                  s3 = 255
		    s4                  s4 = 0

		Returns:                None
		****************************************/
		public void gemxUseAltSonarIPAddress(int sonarID,
		                             byte a1,  byte a2,  byte a3,  byte a4,
		                             byte s1,  byte s2,  byte s3,  byte s4);

		/*************************************//**
		Purpose: Use alternate or fixed IP address for communication with sonar

		Parameters:
		    sonarID             Sonar ID
		    useAltIPAddress     0: Use fixed IP address
		                        1: Use alternate IP address

		Returns:                None
		****************************************/
		public void gemxTxToAltIPAddress(int sonarID, int useAltIPAddress);


		/**************************************************************************************************/
		/*                                 END OF INITIALISATION                                          */
		/**************************************************************************************************/





		/**************************************************************************************************/
		/*                                  PING CONFIGURATION                                            */
		/**************************************************************************************************/

		/*************************************//**
		Purpose: Reset ping configuration to the factory defaults

		Parameters:
		    sonarID             sonar ID

		Returns:                None
		****************************************/
		public void gemxSetPingToDefaults(int sonarID);

		/*************************************//**
		Purpose: Configure ping parameters ( only for MK1 products )

		Parameters:
		    sonarID             Sonar ID
		    range               Range in meters ( 0.1 to 150 )m
		    percentGain         Percentage Gain
		    sos                 Speed of sound (in m/s)

		Returns:                None
		****************************************/
		public void gemxAutoPingConfig(
		                                    int  sonarID,
		                                    float           range,
		                                    int  percentGain,
		                                    float           sos
		                                    );


		/*************************************//**
		Purpose: Negotiate a VDSL adaption rate ( Not supported for 1200ik)

		Parameters:
		    sonarID             Sonar ID
		    level               0: Normal electrical noise environment
		                        1: Medium electrical noise environment
		                        2: High electrical noise environment

		Returns:                None
		****************************************/
		public void gemxSetVDSLSetting(int sonarID, int level);

		/*************************************//**
		Purpose: Returns last negotiated VDSL adaption level ( Not supported for 1200ik)

		Parameters:
		    sonarID             Sonar ID

		Returns:                Level : ( 0 - 2 )
		****************************************/
		public int gemxGetVDSLSetting(int sonarID );

		/*************************************//**
		Purpose: Returns the compression factor applied when a ping was requested in EvoC mode.

		Parameters:
		    sonarID             Sonar ID

		Returns:                Compression Factor : ( 1, 2, 4, 8, 16 )
		****************************************/
		public int gemxGetRequestedCompressionFactor(int sonarID);

		/*************************************//**
		Purpose: Configure the ping mode

		Parameters:
		    sonarID             Sonar ID
		    pingMethod          0: Ping once on receipt of ping configuration message
		                        1: Ping repeatedly at interval fixed by gemxSetInterPingPeriod

		Returns:                None
		****************************************/
		public void gemxSetPingMode(int sonarID, int pingMethod);

		/*************************************//**
		Purpose: Returns the ping mode.

		Parameters:
		    sonarID             Sonar ID

		Returns:                Ping Mode [ 0 - 1 ]
		****************************************/
		public int gemxGetPingMode(int sonarID);

		/*************************************//**
		Purpose: Sets the time delay between start of the one ping and start of the next ping

		* Max inter-ping period is 999 milli-seconds.

		Parameters:
		    sonarID                 Sonar ID
		    periodInMicroSeconds    Time delay ( In Micro seconds )

		Returns:                None
		****************************************/
		public void  gemxSetInterPingPeriod(int sonarID, int periodInMicroSeconds);

		/*************************************//**
		Purpose: Returns the time delay between two pings

		Parameters:
		    sonarID             Sonar ID

		Returns:                Time delay in micro-seconds
		****************************************/
		public int  gemxGetInterPingPeriod(int sonarID);

		/*************************************//**
		Purpose: Sets the optimum transmit pulse length for a given range, gemxSetTXLength is an alternative
		         to this API

		Parameters:
		    sonarID             Sonar ID
		    range               Range( In meters )

		Returns:                Returns the length of the transmit pulse in cycles
		****************************************/
		public int  gemxAutoTXLength(int sonarID, float range);

		/*************************************//**
		Purpose: Sets the length of the transmit pulse, use gemxAutoTXLength instead to set the optimum
		         transmit pulse length for a given range

		Parameters:
		    sonarID             Sonar ID
		    txLength            The length of the transmit pulse in cycles

		Returns:                None
		****************************************/
		public void      gemxSetTXLength(int sonarID, int txLength);



		/*************************************//**
		Purpose: Returns the length of the transmit pulse in cycles

		Parameters:
		    sonarID             Sonar ID

		Returns:                the length of the transmit pulse in cycles
		****************************************/
		public int  gemxGetTXLength(int sonarID);


		/*************************************//**
		Purpose: Sets the out of water override flag in the ping configuration which will be sent to the
		         sonar when ping is requested.

		Parameters:
		    sonarID             Sonar ID
		    outOfWaterOverride  0 : Do not ping when out of water
		                        1 : Ping regardless of out of water indicator

		Returns:                None
		****************************************/
		public void gemxSetExtModeOutOfWaterOverride(int sonarID, int outOfWaterOverride);

		/*************************************//**
		Purpose: Sets the gain and velocimeter mode in the ping configuration

		Parameters:
		    sonarID             Sonar ID
		    gainMode            0: Auto gain
		                        1 : Manual gain
		    outputMode          0: Use velocimeter calculated speed of sound
		                        1: Use speed of sound specified in this ping configuration message

		Returns:                None
		****************************************/
		public void gemxSetVelocimeterMode(
		                                    int sonarID,
		                                    int gainMode,
		                                    int outputMode
		                                    );

		/*************************************//**
		Purpose: Sets the Range compression level and compression type

		*For MK2 Chirp operation, it is recommended that peak compressionType (=1) be used for better results

		Parameters:
		    sonarID             Sonar ID
		    compressionLevel    0: No range compression
		                        1:  2 * range compression
		                        2:  4 * range compression
		                        3:  8 * range compression
		                        4: 16 * range compression
		    compressionType     0: Use average compression
		                        1: Use peak compression

		Returns:                None
		****************************************/
		public void gemxSetRangeCompression(
		                                    int sonarID,
		                                    int compressionLevel,
		                                    int compressionType
		                                    );

		/*************************************//**
		Purpose: Gets the active range compression level and compression type

		Parameters:
		    sonarID             Sonar ID
		    compressionLevel    Active compression level
		    compressionType     Active compression type

		Returns:                None
		****************************************/
		public void gemxGetRangeCompression(
		                                    int sonarID,
		                                    int[] compressionLevel,
		                                    int[] compressionType
		                                    );

		/*************************************//**
		Purpose: May reduce the bandwidth required to get the data from the Gemini sonar head to the
		         hardware running the calling software

		Parameters:
		    sonarID             Sonar ID
		    threshold           0:  No run length encoding
		                        1:  Not available for use, will be increased to 3
		                        2:  Not available for use, will be increased to 3
		                        3:
		                        ...
		                        255: Maximum run length encoding

		Returns:                None
		****************************************/
		public void gemxSetRLEThreshold(int sonarID, int threshold);

		/*************************************//**
		Purpose: Returns the number of beams (nBeams) that the Gemini will form.

		* If pBrgTbl is not NULL, then gemxGetGeminiBeams will write a table of the bearings in radians
		  for each beam at this address

		Parameters:
		    sonarID             Sonar ID
		    pBrgTbl             User allocated buffer [ 256 / 512 ]

		Returns:                Number of beams
		****************************************/
		public int  gemxGetGeminiBeams(int sonarID, float[] pBrgTbl );

		/*************************************//**
		Purpose: Sets the number of beams (nBeams) that the Gemini will form.

		* Only supported for the MK2 platform

		Parameters:
		    sonarID             Sonar ID
		    nBeams              Beams [256/512] default: 256

		Returns:                None
		****************************************/
		public void gemxSetGeminiBeams(int sonarID, int nBeams);


		/*************************************//**
		Purpose: Send ping configuration command to the sonar

		Parameters:
		    sonarID             Sonar ID

		Returns:                None
		****************************************/
		public void gemxSendGeminiPingConfig(int sonarID);

		/*************************************//**
		Purpose: Configure main port (Only for 720im)

		** For Linux, add user in the same group of permission to open the device e.g. /dev/ttyUSB0
		   change the permission of the device sudo usermod -a -G dialout MY_USER_NAME

		** Note : Only maximum of 16 (720im) devices supported to open on either regular COM port or using
		         Tritech USB-To_Serial converter with device ID 65535 to 65520
		         To open the first device ID, use device ID 65535
		         To open the second device ID, use device ID 65534
		         To open the third device ID, use device ID 65533

		Parameters:
		    sonarID             Sonar ID
		    negotiate           true: reset baudrate to 115200 then goes up,
		                        false: used fixed baudrate specified by the user
		    rs232               true: RS232, false: RS485
		    baudRate            baudrate: 115200, 230400, 460800, 921600
		    comPortName         COM1, COM2... COM(N)

		Returns:                0: failed, 1: success
		****************************************/
		public boolean  gemxConfigureMainPort(
		                                int sonarID,
		                                boolean            negotiate,
		                                boolean            rs232,
		                                int    baudRate,
		                                char[]     portName
		                                );

		/*************************************//**
		Purpose: Configure High Range resolution (Only for Mk2 platforms supported from 0x200e firmware version)

		* Automatically switch high range resolution based on the range selected. By enabling high range
		* resolution would improve the sonar imagery in lower range e.g.

		**  720khz : reduce the range lines from 8mm to 4mm
		** 1200khz : reduce the range lines from 4mm to 2.4

		Parameters:
		    sonarID             Sonar ID
		    enable              true/false

		Returns:                None
		****************************************/
		public void gemxAutoHighRangeResolution(int sonarID, boolean enable);


		/*************************************//**
		Purpose: Configure Auto Range frequency (Only for 1200ik)

		User can configure auto range (1-50)m to switch between high/low frequency

		Parameters:
		    sonarID             Sonar ID
		    autoRangeConfig     RangeFrequencyConfig

		Returns:                true, if configuration parameters passed are valid else false
		****************************************/
		public boolean gemxConfigureAutoRangeFrequency(
		                                            int          sonarID,
		                                            int m_Frequency, double m_RamgeThreshold 
		                                            );

		/*************************************//**
		Purpose: Configure chirp mode

		Parameters:
		    sonarID             Sonar ID
		    chirpMode           CHIRP_MODE
		            CHIRP_DISABLED  : Chirp will be disabled
		            CHIRP_ENABLED   : Chirp will be enabled
		            CHIRP_AUTO      : chirp will be enabled/disabled automatically based on the range selected

		Returns:                true, if configuration parameters passed are valid else false
		****************************************/
		public boolean gemxConfigureChirpMode(int sonarID, int chirpMode );

		/*************************************//**
		Purpose: Get the active chirp mode

		Parameters:
		    sonarID             Sonar ID

		Returns:                true, chirp on else off
		****************************************/
		public boolean gemxGetActiveChirpMode(int sonarID );


		/*************************************//**
		Purpose: Increase update rate in low frequency mode at longer ranges

		Parameters:
		    sonarID             Sonar ID
		    enable              true/false

		Returns:                None
		****************************************/
		public void gemxSetHigherFrameRateinLFMode(int sonarID, boolean enable );

		/**************************************************************************************************/
		/*                                   Sonar Specific                                               */
		/**************************************************************************************************/

		/*************************************//**
		Purpose: Build a list of sonars and return the number sof sonars

		Parameters:
		    pList               List of sonars

		Returns:                Number of sonars in the list
		****************************************/
		public int gemxGetSonars(short[] pList);

		/*************************************//**
		Purpose: Delete the sonar from the list, if sonar ID is specified as 0 then
		         all sonars gets deleted

		Parameters:
		    sonarID             sonar ID

		Returns:                Number of sonars deleted from the internal list
		****************************************/
		public int gemxDeleteSonarID(int sonarID);

		/*************************************//**
		Purpose: Copies a null-terminated ASCII string name for the device into pBuf.

		Parameters:
		    sonarID             sonar ID
		    pBuf                User specified buffer
		    len                 Length of the pBuf

		Returns:                None
		****************************************/
		public void gemxGetDeviceName(int sonarID, char[] pBuf, int len);

		/*************************************//**
		Purpose: Returns the operating frequency of the sonar in Hz.

		Parameters:
		    sonarID             Sonar ID

		Returns:                Frequency in Hz
		****************************************/
		public int  gemxGetGeminiFrequency(int sonarID);

		/*************************************//**
		Purpose: Returns the modulation frequency in conjustion with the speed of sound

		Parameters:
		    sonarID             Sonar ID

		Returns:                The modulation frequency in Hz.
		****************************************/
		public int  gemxGetGeminiModFrequency(int sonarID);


		/*************************************//**
		Purpose: Keep the communication alive between PC and Sonar

		Parameters:
		    sonarID             Sonar ID

		Returns:                None
		****************************************/
		public void gemxSendGeminiStayAlive(int sonarID);

		/*************************************//**
		Purpose: Reboot sonar

		Parameters:
		    sonarID             Sonar ID

		Returns:                None
		****************************************/
		public void gemxRebootSonar(int sonarID);


		/*************************************//**
		Purpose: Enable / Disable Noise reduction

		NOTE: This API is only available for Micron Gemini

		Parameters:
		    sonarID             Sonar ID
		    enable              true/false

		Returns:                true: if applied else false
		****************************************/
		public boolean gemxSetNoiseReduction(int sonarID, boolean enable);

		/*************************************//**
		Purpose: Enable / Disable H264 Compression

		NOTE: This API is only available for 720im/Micron Gemini.  Compression is always applied
		to Serial (RS-485/232) connected devices.

		Parameters:
		    sonarID             Sonar ID
		    enableH264          true/false

		Returns:                None
		****************************************/
		public void gemx720imEnableH264(int sonarID, boolean enableH264);

		/**************************************************************************************************/
		/*                                        END                                                     */
		/**************************************************************************************************/

	}
}
