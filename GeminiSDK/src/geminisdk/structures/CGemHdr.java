package geminisdk.structures;

import java.io.DataInput;
import java.io.IOException;

public class CGemHdr {

	byte  m_type;
	byte  m_version;
	int m_deviceID;
	short m_packetLatency;
	/*
	 ** First 8 bytes are source sub device id (Hardware)
	 ** while 2nd 8 bytes are destinataions sub device ID  (Software)
	 ** because of endian, these should be declared otherway around
	 */
	short m_dst_sub_device_id;
	short m_src_sub_device_id;  
	
	public CGemHdr() {

	}
	
	public boolean read(DataInput dataInput) {
		try {
			m_type = dataInput.readByte();
			m_version = dataInput.readByte();
			m_deviceID = (short) dataInput.readUnsignedShort();
			m_packetLatency = dataInput.readShort();
			m_dst_sub_device_id = (short) dataInput.readUnsignedByte();
			m_src_sub_device_id = (short) dataInput.readUnsignedByte();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("Sonar %d, type %d, version %d", m_deviceID, m_type, m_version);
	}
	
	

}
