package geminisdk.structures;

import java.io.DataInput;
import java.io.IOException;

public class CGemHdr {

	byte  m_type;
	byte  m_version;
	short m_deviceID;
	short m_packetLatency;
	/*
	 ** First 8 bytes are source sub device id (Hardware)
	 ** while 2nd 8 bytes are destinataions sub device ID  (Software)
	 ** because of endian, these should be declared otherway around
	 */
	byte m_dst_sub_device_id;
	byte m_src_sub_device_id;  
	
	public CGemHdr() {

	}
	
	public boolean fromBytes(DataInput dataInput, int length) {
		try {
			m_type = dataInput.readByte();
			m_version = dataInput.readByte();
			m_deviceID = dataInput.readShort();
			m_packetLatency = dataInput.readShort();
			m_dst_sub_device_id = dataInput.readByte();
			m_src_sub_device_id = dataInput.readByte();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	

}
