package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Copy of the class CGemStatusPacket: public CGemNetMsg in GeminiStructuresPublic.h
 * which is the wrong bloody structure. Should be using GLFStatusData, the one developed for
 * file input. 
 * @author dg50
 *
 */
public class GemStatusPacket extends GeminiMessage {
	public short m_firmwareVer;
	public short m_sonarId;
	public int   m_sonarFixIp;
	public int   m_sonarAltIp;
	public int   m_surfaceIp;
	public short m_flags;
	public short m_vccInt;
	public short m_vccAux;
	public short m_dcVolt;
	public short m_dieTemp;
	public short m_dipSwitch; /*Only for Mk2*/
	public short m_vga1aTemp;
	public short m_vga1bTemp;
	public short m_vga2aTemp;
	public short m_vga2bTemp;
	public short m_psu1Temp;
	public short m_psu2Temp;
	public int   m_currentTimestampL;
	public int   m_currentTimestampH;
	public short m_transducerFrequency;
	public int   m_subnetMask;
	public short m_TX1Temp;
	public short m_TX2Temp;
	public short m_TX3Temp;
	public int   m_BOOTSTSRegister;
	public short m_shutdownStatus;
	public short m_dieOverTemp;
	public short m_vga1aShutdownTemp;
	public short m_vga1bShutdownTemp;
	public short m_vga2aShutdownTemp;
	public short m_vga2bShutdownTemp;
	public short m_psu1ShutdownTemp;
	public short m_psu2ShutdownTemp;
	public short m_TX1ShutdownTemp;
	public short m_TX2ShutdownTemp;
	public short m_TX3ShutdownTemp;
	public short m_linkType;
	public short m_VDSLDownstreamSpeed1;
	public short m_VDSLDownstreamSpeed2;
	public short m_macAddress1;
	public short m_macAddress2;
	public short m_macAddress3;
	public short m_VDSLUpstreamSpeed1;
	public short m_VDSLUpstreamSpeed2;

	@Override
	public boolean fromBytes(DataInput dataInput, int length) {
		if (super.fromBytes(dataInput, length) == false) {
			return false;
		}
		try {
			/**
			 * firmware version gives 8213 which is 0x2015 which is the same as the 
			 * BF:20.15 displayed in SeaTec.
			 */
//			dataInput.skipBytes(8);
//			int something1 = dataInput.readInt(); // thesea are actually the header. 
//			int something2 = dataInput.readInt();
			m_firmwareVer = dataInput.readShort(); //   
			m_sonarId = dataInput.readShort();
			//			System.out.println("Status packet m_sonarID = " + m_sonarId);
			m_sonarFixIp = dataInput.readInt();
			m_sonarAltIp = dataInput.readInt();
			m_surfaceIp = dataInput.readInt();
			m_flags = dataInput.readShort();
			m_vccInt = dataInput.readShort();
			m_vccAux = dataInput.readShort();
			m_dcVolt = dataInput.readShort();
			m_dieTemp = dataInput.readShort();
			m_dipSwitch = dataInput.readShort();
			m_vga1aTemp = dataInput.readShort();
			m_vga1bTemp = dataInput.readShort();
			m_vga2aTemp = dataInput.readShort();
			m_vga2bTemp = dataInput.readShort();
			m_psu1Temp = dataInput.readShort();
			m_psu2Temp = dataInput.readShort();
			m_currentTimestampL = dataInput.readInt();
			m_currentTimestampH = dataInput.readInt();
			m_transducerFrequency = dataInput.readShort();
			m_subnetMask = dataInput.readInt();
			m_TX1Temp = dataInput.readShort();
			m_TX2Temp = dataInput.readShort();
			m_TX3Temp = dataInput.readShort();
			m_BOOTSTSRegister = dataInput.readInt();
			m_shutdownStatus = dataInput.readShort();
			m_dieOverTemp = dataInput.readShort();
			m_vga1aShutdownTemp = dataInput.readShort();
			m_vga1bShutdownTemp = dataInput.readShort();
			m_vga2aShutdownTemp = dataInput.readShort();
			m_vga2bShutdownTemp = dataInput.readShort();
			m_psu1ShutdownTemp = dataInput.readShort();
			m_psu2ShutdownTemp = dataInput.readShort();
			m_TX1ShutdownTemp = dataInput.readShort();
			m_TX2ShutdownTemp = dataInput.readShort();
			m_TX3ShutdownTemp = dataInput.readShort();
			m_linkType = dataInput.readShort();
			m_VDSLDownstreamSpeed1 = dataInput.readShort();
			m_VDSLDownstreamSpeed2 = dataInput.readShort();
			m_macAddress1 = dataInput.readShort();
			m_macAddress2 = dataInput.readShort();
			m_macAddress3 = dataInput.readShort();
			m_VDSLUpstreamSpeed1 = dataInput.readShort();
			m_VDSLUpstreamSpeed2 = dataInput.readShort();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private GemStatusPacket(byte[] inputBytes) {
		super(inputBytes);
		// TODO Auto-generated constructor stub
	}

	public GemStatusPacket() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int defaultCommand() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int dataOutputSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}
