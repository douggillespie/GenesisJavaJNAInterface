package geminisdk;

import java.io.File;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.ShortByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Callback;



public class GeminiLoader {

	//	private static final String libName = "C:\\PamguardCode2021\\GeminiSDK\\lib\\x64\\GeminiComms.dll";
	private static final String libName = "\\lib\\x64\\GeminiComms.dll";

	public GeminiLoader() {
		// TODO Auto-generated constructor stub
	}


	TG loadGeminiLibrary() throws Exception {

		System.out.println("Java class path " + System.getProperty("java.class.path"));

		String libraryPath;// = findLibrary();
		//		libraryPath = "C:\\Program Files\\Tritech\\Genesis\\Application Files\\GeminiComms.dll";
		libraryPath = "C:\\Program Files\\Tritech\\Gemini SDK\\bin\\x64\\GeminiComms.dll";

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

	public class HandlerCallback implements Callback {
		public void callback(int eType, int len, String dataBlock) {
			System.out.printf("HandlerCallback eType %d (0x%x), len %d\n", eType, eType, len);
		}
	}
	/**
	 * Function prototypes from GeminiCommsPublic.h
	 * 
	 * @author dg50
	 *
	 */
	public interface TG extends Library {
				
		
		/*************************************//**
		Purpose: Initialises the network interface ready to operate.

		Parameters:
		    sonarID             sonar ID

		Returns:                0:A failure occurred initialising the network interface.
		                          The most likely cause is that another piece of software using the
		                          Gemini library is already running, and so the port could not be opened
		                        1: The network initialised correctly and is ready to communicate.
		 ****************************************/
		public int GEM_StartGeminiNetworkWithResult(short sonarID);/*************************************//**
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
		void GEM_SetGeminiSoftwareMode(byte[] softwareMode);

		void GEM_SetHandlerFunction(HandlerCallback callback);
		/*************************************//**
		Purpose: Resets internal counters held by the library.

		Returns:                None
		 ****************************************/
		void GEM_ResetInternalCounters();
		/*************************************//**
		Purpose: Closes the network interface and stops the internal tasks running.

		Returns:                None
		 ****************************************/
		void GEM_StopGeminiNetwork();
		/*************************************//**
		Purpose: Returns the maximum version of the data link protocol that the library can support.

		 * Currently DLL link version is 2.

		Returns:                DLL Link Version
		 ****************************************/
		byte GEM_GetDLLLinkVersionNumber();

		/*************************************//**
		Purpose: Returns the length of the version string which identifies the library

		Returns:                Length of version string
		 ****************************************/
		int  GEM_GetVStringLen();

		/*************************************//**
		Purpose: Gets the copy of version string in the data buffer

		 * e.g. 'Gemini Comms V2.0.0 Tritech International Ltd.'

		Parameters:
		    data                User specified buffer
		    len                 Length of the data buffer

		Returns:                None
		 ****************************************/
		void GEM_GetVString(byte[] data, int len);


		/*************************************//**
Purpose: Returns the length of the version number string which identifies the library

Returns:                Length of version string
		 ****************************************/
		int  GEM_GetVNumStringLen();

		/*************************************//**
Purpose: Gets the copy of version number string in the data buffer

		 * e.g. 'V2.0.0'

Parameters:
    data                User specified buffer
    len                 Length of the data buffer

Returns:                None
		 ****************************************/
		void GEM_GetVNumString(byte[] data, int len);


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
		void GEMX_SetGeminiEvoQuality(short sonarID, byte evoQualitySetting);

		/*************************************//**
Purpose: This is required at the start of initialisation to configure the library for the
         correct sonar head type.

Parameters:
    sonarID             sonar ID
    headType            One of the head type

                        GEM_HEADTYPE_720I
                        GEM_HEADTYPE_720ID
                        GEM_HEADTYPE_NBI
                        GEM_HEADTYPE_MK2_720IS
                        GEM_HEADTYPE_MK2_720IK

Returns:                None
		 ****************************************/
		void GEMX_SetHeadType(short sonarID, int headType);


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
		void GEMX_GetAltSonarIPAddress(short sonarID, 
				ByteByReference a1, ByteByReference a2, ByteByReference a3, ByteByReference a4,
				ByteByReference s1, ByteByReference s2, ByteByReference s3, ByteByReference s4);

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
		void GEMX_SetAltSonarIPAddress(short sonarID, 
				byte a1, byte a2, byte a3, byte a4,
				byte s1, byte s2, byte s3, byte s4);

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
		void GEMX_UseAltSonarIPAddress(short sonarID, 
				byte a1, byte a2, byte a3, byte a4,
				byte s1, byte s2, byte s3, byte s4);;

				/*************************************//**
Purpose: Use alternate or fixed IP address for communication with sonar

Parameters:
    sonarID             Sonar ID
    useAltIPAddress     0: Use fixed IP address
                        1: Use alternate IP address

Returns:                None
				 ****************************************/
				void GEMX_TxToAltIPAddress(short sonarID, int useAltIPAddress);


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
				void GEMX_SetPingToDefaults(short sonarID);


				/*************************************//**
Purpose: Configure ping parameters ( only for MK1 products )

Parameters:
    sonarID             Sonar ID
    range               Range in meters ( 0.1 to 150 )m
    percentGain         Percentage Gain
    sos                 Speed of sound (in m/s)

Returns:                None
				 ****************************************/
				void GEMX_AutoPingConfig( short  sonarID,
						float           range,
						short  percentGain,
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
				void GEMX_SetVDSLSetting(short sonarID, short level);

				/*************************************//**
Purpose: Returns last negotiated VDSL adaption level ( Not supported for 1200ik)

Parameters:
    sonarID             Sonar ID

Returns:                Level : ( 0 - 2 )
				 ****************************************/
				short GEMX_GetVDSLSetting(short sonarID );

				/*************************************//**
Purpose: Returns the compression factor applied when a ping was requested in EvoC mode.

Parameters:
    sonarID             Sonar ID

Returns:                Compression Factor : ( 1, 2, 4, 8, 16 )
				 ****************************************/
				short GEMX_GetRequestedCompressionFactor(short sonarID);

				/*************************************//**
Purpose: Configure the ping mode

Parameters:
    sonarID             Sonar ID
    pingMethod          0: Ping once on receipt of ping configuration message
                        1: Ping repeatedly at interval fixed by GEMX_SetInterPingPeriod

Returns:                None
				 ****************************************/
				void GEMX_SetPingMode(short sonarID, short pingMethod);

				/*************************************//**
Purpose: Returns the ping mode.

Parameters:
    sonarID             Sonar ID

Returns:                Ping Mode [ 0 - 1 ]
				 ****************************************/
				short GEMX_GetPingMode(short sonarID);

				/*************************************//**
Purpose: Sets the time delay between start of the one ping and start of the next ping

				 * Max inter-ping period is 999 milli-seconds.

Parameters:
    sonarID                 Sonar ID
    periodInMicroSeconds    Time delay ( In Micro seconds )

Returns:                None
				 ****************************************/
				void      GEMX_SetInterPingPeriod(short sonarID, int periodInMicroSeconds);

				/*************************************//**
Purpose: Returns the time delay between two pings

Parameters:
    sonarID             Sonar ID

Returns:                Time delay in micro-seconds
				 ****************************************/
				int  GEMX_GetInterPingPeriod(short sonarID);

				/*************************************//**
Purpose: Sets the optimum transmit pulse length for a given range, GEMX_SetTXLength is an alternative
         to this API

Parameters:
    sonarID             Sonar ID
    range               Range( In meters )

Returns:                Returns the length of the transmit pulse in cycles
				 ****************************************/
				short  GEMX_AutoTXLength(short sonarID, float range);

				/*************************************//**
Purpose: Sets the length of the transmit pulse, use GEMX_AutoTXLength instead to set the optimum
         transmit pulse length for a given range

Parameters:
    sonarID             Sonar ID
    txLength            The length of the transmit pulse in cycles

Returns:                None
				 ****************************************/
				void      GEMX_SetTXLength(short sonarID, short txLength);



				/*************************************//**
Purpose: Returns the length of the transmit pulse in cycles

Parameters:
    sonarID             Sonar ID

Returns:                the length of the transmit pulse in cycles
				 ****************************************/
				short  GEMX_GetTXLength(short sonarID);


				/*************************************//**
Purpose: Sets the out of water override flag in the ping configuration which will be sent to the
         sonar when ping is requested.

Parameters:
    sonarID             Sonar ID
    outOfWaterOverride  0 : Do not ping when out of water
                        1 : Ping regardless of out of water indicator

Returns:                None
				 ****************************************/
				void GEMX_SetExtModeOutOfWaterOverride( short sonarID, short outOfWaterOverride);

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
				void GEMX_SetVelocimeterMode(
						short sonarID,
						short gainMode,
						short outputMode
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
				void GEMX_SetRangeCompression(
						short sonarID,
						short compressionLevel,
						short compressionType
						);

				/*************************************//**
Purpose: Gets the active range compression level and compression type

Parameters:
    sonarID             Sonar ID
    compressionLevel    Active compression level
    compressionType     Active compression type

Returns:                None
				 ****************************************/
				void GEMX_GetRangeCompression(
						short sonarID,
						ShortByReference compressionLevel,
						ShortByReference compressionType
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
				void GEMX_SetRLEThreshold(short sonarID, short threshold);

				/*************************************//**
Purpose: Returns the number of beams (nBeams) that the Gemini will form.

				 * If pBrgTbl is not NULL, then GEMX_GetGeminiBeams will write a table of the bearings in radians
  for each beam at this address

Parameters:
    sonarID             Sonar ID
    pBrgTbl             User allocated buffer [ 256 / 512 ]

Returns:                Number of beams
				 ****************************************/
				int  GEMX_GetGeminiBeams(short sonarID, float[] pBrgTbl );

				/*************************************//**
Purpose: Sets the number of beams (nBeams) that the Gemini will form.

				 * Only supported for the MK2 platform

Parameters:
    sonarID             Sonar ID
    nBeams              Beams [256/512] default: 256

Returns:                None
				 ****************************************/
				void GEMX_SetGeminiBeams(short sonarID, short nBeams);


				/*************************************//**
Purpose: Send ping configuration command to the sonar

Parameters:
    sonarID             Sonar ID

Returns:                None
				 ****************************************/
				void GEMX_SendGeminiPingConfig(short sonarID);

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
				boolean  GEMX_ConfigureMainPort(
						short sonarID,
						boolean            negotiate,
						boolean            rs232,
						int    baudRate,
						String     portName // was const char*
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
				void GEMX_AutoHighRangeResolution(short sonarID, boolean enable);


				/*************************************//**
Purpose: Configure Auto Range frequency (Only for 1200ik)

User can configure auto range (1-50)m to switch between high/low frequency

Parameters:
    sonarID             Sonar ID
    autoRangeConfig     RangeFrequencyConfig

Returns:                true, if configuration parameters passed are valid else false
				 ****************************************/
				//boolean GEMX_ConfigureAutoRangeFrequency(
				//                                            short          sonarID,
				//                                            RangeFrequencyConfig   autoRangeConfig
				//                                            );

				///*************************************//**
				//Purpose: Configure chirp mode
				//
				//Parameters:
				//    sonarID             Sonar ID
				//    chirpMode           CHIRP_MODE
				//            CHIRP_DISABLED  : Chirp will be disabled
				//            CHIRP_ENABLED   : Chirp will be enabled
				//            CHIRP_AUTO      : chirp will be enabled/disabled automatically based on the range selected
				//
				//Returns:                true, if configuration parameters passed are valid else false
				//****************************************/
				//bool GEMX_ConfigureChirpMode(unsigned short sonarID, CHIRP_MODE chirpMode );
				//
				/*************************************//**
Purpose: Get the active chirp mode

Parameters:
    sonarID             Sonar ID

Returns:                true, chirp on else off
				 ****************************************/
				boolean GEMX_GetActiveChirpMode(short sonarID );


				/*************************************//**
Purpose: Increase update rate in low frequency mode at longer ranges

Parameters:
    sonarID             Sonar ID
    enable              true/false

Returns:                None
				 ****************************************/
				void GEMX_SetHigherFrameRateinLFMode(short sonarID, boolean enable );

				/**************************************************************************************************/
				/*                                   Sonar Specific                                               */
				/**************************************************************************************************/

				/*************************************//**
Purpose: Build a list of sonars and return the number sof sonars

Parameters:
    pList               List of sonars

Returns:                Number of sonars in the list
				 ****************************************/
				//short GEMX_GetSonars(unsigned short *pList);
				short GEMX_GetSonars(short[] pList);

				/*************************************//**
Purpose: Delete the sonar from the list, if sonar ID is specified as 0 then
         all sonars gets deleted

Parameters:
    sonarID             sonar ID

Returns:                Number of sonars deleted from the internal list
				 ****************************************/
				short GEMX_DeleteSonarID(short sonarID);

				/*************************************//**
Purpose: Copies a null-terminated ASCII string name for the device into pBuf.

Parameters:
    sonarID             sonar ID
    pBuf                User specified buffer
    len                 Length of the pBuf

Returns:                None
				 ****************************************/
				void GEMX_GetDeviceName(short sonarID, byte[] pBuf, int len);

				/*************************************//**
Purpose: Returns the operating frequency of the sonar in Hz.

Parameters:
    sonarID             Sonar ID

Returns:                Frequency in Hz
				 ****************************************/
				int  GEMX_GetGeminiFrequency(short sonarID);

				/*************************************//**
Purpose: Returns the modulation frequency in conjustion with the speed of sound

Parameters:
    sonarID             Sonar ID

Returns:                The modulation frequency in Hz.
				 ****************************************/
				int  GEMX_GetGeminiModFrequency(short sonarID);


				/*************************************//**
Purpose: Keep the communication alive between PC and Sonar

Parameters:
    sonarID             Sonar ID

Returns:                None
				 ****************************************/
				void GEMX_SendGeminiStayAlive(short sonarID);

				/*************************************//**
Purpose: Reboot sonar

Parameters:
    sonarID             Sonar ID

Returns:                None
				 ****************************************/
				void GEMX_RebootSonar(short sonarID);
				//
				//		
				//		
				////		void GEM_SetSurveyNetworkMode(char[] hostname,  int port,  short format,  int sampRate);
				//
				//		
				
				void FT_GetDeviceInfoList(byte[] pBuf, int len);
	}	

}
