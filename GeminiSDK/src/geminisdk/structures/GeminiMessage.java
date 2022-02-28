package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

abstract public class GeminiMessage extends GeminiStructure {


	  public byte  m_type;
	  public byte  m_version;
	  public short m_deviceID;
	  public short m_packetLatency;
	  /*
	  ** First 8 bytes are source sub device id (Hardware)
	  ** while 2nd 8 bytes are destinataions sub device ID  (Software)
	  ** because of endian, these should be declared otherway around
	  */
	  public byte m_dst_sub_device_id;
	  public byte m_src_sub_device_id;
	  
	public GeminiMessage(byte[] inputBytes) {
		super(inputBytes);
	}

	public GeminiMessage() {
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
	public boolean fromBytes(DataInput dataInput) {
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

	@Override
	protected int dataOutputSize() {
		// TODO Auto-generated method stub
		return 8;
	}

}
