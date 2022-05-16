package geminisdk.image;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import geminisdk.structures.GeminiStructure;

public class CommonInterfaceHeader extends GeminiStructure {

    public byte   m_idChar;               // Start character used to signify start of CI header (Always = “*”)
    public byte   m_version;              // Version number of the CI Header
    public int  m_length;               // Total length (number of bytes) of CI Header and Data message.
    public double  m_timestamp;            // Date / Time in UTC with millisecond resolution (format TBC)
    public byte   m_dataType;             // Type of data contained in attached data record
                                    // 0 = SVS5
                                    // 1 = V4
                                    // 2 = Analog_Video
                                    // .
                                    // .
                                    // 98 = Raw_Serial
                                    // 99 = Generic (see Section 6.12.3)
    public short  m_deviceID;             // This will be:
                                    // A Sonar ID number when m_dataType = 0
                                    // An incrementing deviceID when m_dataType = 99
                                    //(e.g. 1st GPS: m_deviceID = 1, 2nd GPS: m_deviceID = 2, etc)
    public short  m_nodeID;               // node ID.
    public short  m_spare;                // Reserved for future expansion

	@Override
	public boolean fromBytes(DataInput dataInput, int length) {
		try {
			// 21 useful bytes + three packers. 
			m_idChar = dataInput.readByte();
			m_version = dataInput.readByte();
			m_length = dataInput.readInt();
			dataInput.skipBytes(2); // aligns to byte 8
			m_timestamp = dataInput.readDouble();
			dataInput.skipBytes(1); // aligns next read to word bound. 
			m_dataType = dataInput.readByte();
			/*
			 *  device id is clearly at byte 18, but there are only 15 prior to this.The id is the 85,3
			 *  which is short for two bytes = 853
			 *  [42, 1, 0, 0, 0, 0, 0, 0, 62, 94, -117, 94, -112, -45, -45, 65, 0, 0, 85, 3, 
			 *  The 42 is correct *
			 *  1 is version. 
			 */
			m_deviceID = dataInput.readShort();
			m_nodeID = dataInput.readShort();
			m_spare = dataInput.readShort();
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	public CommonInterfaceHeader() {
		super();
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		return false;
	}
	
	public CommonInterfaceHeader(byte[] inputBytes) {
		super(inputBytes);
	}

	@Override
	protected int dataOutputSize() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int defaultCommand() {
		// TODO Auto-generated method stub
		return 0;
	}


}
