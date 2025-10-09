package geminisdk.structures;

/**
 * Constants copied from GeminiStructuresPublic.h
 * @author dg50
 *
 */
public class CGemMessage {


	/*****************************************************************************//**
	 ************** Message types received in the callback function *******************
	 *********************************************************************************/
	public static final int PING_HEAD              =     0;
	public static final int PING_DATA              =     1;
	public static final int PING_TAIL              =     2;
	public static final int GEM_STATUS             =     3;
	public static final int GEM_ACKNOWLEDGE        =     4;
	public static final int GEM_BEARING_DATA       =     7;
	public static final int PING_TAIL_EX           =     10;
	public static final int GEM_IP_CHANGED         =     11;
	public static final int GEM_UNKNOWN_DATA       =     12;
	
	public static String toMessageName(int messageType) {
		switch (messageType) {
		case PING_HEAD:
			return "PING_HEAD";
		case PING_DATA:
			return "PING_DATA";
		case PING_TAIL:
			return "PING_TAIL";
		case GEM_STATUS:
			return "GEM_STATUS";
		case GEM_ACKNOWLEDGE:
			return "GEM_ACKNOWLEDGE";
		case GEM_BEARING_DATA:
			return "GEM_BEARING_DATA";
		case PING_TAIL_EX:
			return "PING_TAIL_EX";
		case GEM_IP_CHANGED:
			return "GEM_IP_CHANGED";
		case GEM_UNKNOWN_DATA:
			return "GEM_UNKNOWN_DATA";
		}
		return "Unknown";
	}


	/*****************************************************************************//**
	 **************************** Supported Product ID's *****************************
	 *********************************************************************************/
	public static final int  GEM_HEADTYPE_720I          =  0x0;
	public static final int  GEM_HEADTYPE_720ID         =  0x1;
	public static final int  GEM_HEADTYPE_NBI           =  0x2;
	public static final int  GEM_HEADTYPE_MK2_720IS     =  0x21;
	public static final int  GEM_HEADTYPE_MK2_1200IK    =  0x1D;
	public static final int  GEM_HEADTYPE_MK2_720IK     =  0x1E;
	public static final int  GEM_HEADTYPE_720IM         =  0x1F;
	public static final int  GEM_HEADTYPE_MICRON_GEMINI =  0x27;


	/*****************************************************************************//**
	 ********** Beam Spacing mode used in GEMX_GetBeamSpacing  ***********************
	 *********************************************************************************/
	public static final int  GEM_BEAMS_DEFAULT        = -1;
	public static final int  GEM_BEAMS_INVERSESINE    =  0;
	public static final int  GEM_BEAMS_EQUIDISTANT    =  1;
	public static final int  GEM_BEAMS_EQUIANGULAR    =  2;

	/*****************************************************************************//**
	 ********** Serial Port ID's and maximum supported serial ports*******************
	 ** 720is    Maximum : 2 Serial ports.
            Serial port 'A' can switch betwen RS232/RS485
            Serial port 'B' RS232 only

	 ** 720ik    Maximum : 1 Serial port.
            Serial port 'A' RS232 only

	 *********************************************************************************/
	// Serial ports
	public static final int SERIAL_PORT_A            =   0;
	public static final int SERIAL_PORT_B            =   1;
	public static final int MAX_SERIAL_PORTS         =   2;

	/*****************************************************************************//**
	 ********** FPGA ID's *******************
	 ** MK1      1 FPGA with ID 0
	 ** MK2      Maximum of 4 FPGA's

	 ** 720is    Maximum of 2 FPGA's
            BEAMFORMER_FPGA, DATA_ACQ_FPGA_0

	 ** 720ik    Maximum of 2 FPGA's
            BEAMFORMER_FPGA, DATA_ACQ_FPGA_0

	 *********************************************************************************/
	// FPGA ID's
	public static final int BEAMFORMER_FPGA  = 2;  // 0x02
	public static final int DATA_ACQ_FPGA_0  = 4;  // 0x04
	public static final int DATA_ACQ_FPGA_1  = 8;  // 0x08
	public static final int DATA_ACQ_FPGA_2  = 16;  // 0x10
	public static final int ALL_FPGA = BEAMFORMER_FPGA | DATA_ACQ_FPGA_0 | DATA_ACQ_FPGA_1 | DATA_ACQ_FPGA_2;


	/*****************************************************************************//**
	 *********** Number of FPGA in a single sonar *******************
	 ** MK1      1 FPGA with number 0
	 ** MK2      Maximum of 4 FPGA's

	 ** 720is, 720ik [0,1]
	 *********************************************************************************/
	// Number of FPGA in MK2 platform
	public static final int BEAMFORMER_FPGA_ENUM  =  0;
	public static final int DATA_ACQ_FPGA_0_ENUM  =  1;
	public static final int DATA_ACQ_FPGA_1_ENUM  =  2;
	public static final int DATA_ACQ_FPGA_2_ENUM  =  3;
    
	public static final int FREQUENCY_AUTO = 0; // Switch between low/high resolution based on the range threshold specified
	public static final int FREQUENCY_LOW = 1;  // Force to run with low frequency 720Hz
	public static final int FREQUENCY_HIGH = 2;  // Force to run with high frequency 1200Hz

}
