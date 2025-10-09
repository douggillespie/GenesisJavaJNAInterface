package geminisdk.structures;

import java.io.DataInput;
import java.io.IOException;

/**
 * CGemStatusPacket
 * @author dg50
 *
 */
public class CGemStatus extends CGemData {

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

	public CGemStatus(CGemHdr cGemHdr) {
		super(cGemHdr);
	}
	
	public boolean read(DataInput dis) {
		try {
			m_firmwareVer = dis.readShort();
			m_sonarId = dis.readShort();
			m_sonarFixIp = dis.readInt();
			m_sonarAltIp = dis.readInt();
			m_surfaceIp = dis.readInt();
			m_flags = dis.readShort();
			m_vccInt = dis.readShort();
			m_vccAux = dis.readShort();
			m_dcVolt = dis.readShort();
			m_dieTemp = dis.readShort();
			m_dipSwitch = dis.readShort();
			m_vga1aTemp = dis.readShort();
			m_vga1bTemp = dis.readShort();
			m_vga2aTemp = dis.readShort();
			m_vga2bTemp = dis.readShort();
			m_psu1Temp = dis.readShort();
			m_psu2Temp = dis.readShort();
			m_currentTimestampL = dis.readInt();
			m_currentTimestampH = dis.readInt();
			m_transducerFrequency = dis.readShort();
			m_subnetMask = dis.readInt();
			m_TX1Temp = dis.readShort();
			m_TX2Temp = dis.readShort();
			m_TX3Temp = dis.readShort();
			m_BOOTSTSRegister = dis.readInt();
			m_shutdownStatus = dis.readShort();
			m_dieOverTemp = dis.readShort();
			m_vga1aShutdownTemp = dis.readShort();
			m_vga1bShutdownTemp = dis.readShort();
			m_vga2aShutdownTemp = dis.readShort();
			m_vga2bShutdownTemp = dis.readShort();
			m_psu1ShutdownTemp = dis.readShort();
			m_psu2ShutdownTemp = dis.readShort();
			m_linkType = dis.readShort();
			m_VDSLDownstreamSpeed1 = dis.readShort();
			m_VDSLDownstreamSpeed2 = dis.readShort();
			m_macAddress1 = dis.readShort();
			m_macAddress2 = dis.readShort();
			m_macAddress3 = dis.readShort();
			m_VDSLUpstreamSpeed1 = dis.readShort();
			m_VDSLUpstreamSpeed2 = dis.readShort();
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
