package geminisdk.structures;

import java.io.DataInput;
import java.io.IOException;

public class CGemPingLine extends CGemData {

	public byte  m_gain;
	public byte  m_pingID;
	public short m_lineID;
	public short m_scale;
	public short m_lineInfo;
	public byte  m_startOfData;
	byte[] data;

	public CGemPingLine(CGemHdr cGemHdr) {
		super(cGemHdr);
	}

	public boolean read(DataInput dis) {
		try {
			m_gain = dis.readByte();
			m_pingID = dis.readByte();
			m_lineID = dis.readShort();
			m_scale = dis.readShort();
			m_lineInfo = dis.readShort();
			m_startOfData = dis.readByte();
			int nData = dis.readShort();
			int cSize = dis.readShort();
			data = new byte[nData];
			dis.readFully(data);
//			System.out.printf("Ping id %3d, line id %3d\n", Byte.toUnsignedInt(m_pingID), Short.toUnsignedInt(m_lineID));
//			int nZ = 0;
//			for (int i = nData-1; i >= 0; i--) {
//				if (data[i] > 0) {
//					break;
//				}
//				nZ++;
//			}
//			if (data[0] >= 0) {
//				System.out.printf("cSize %d, Data start %d zeros %d diff %d, First and last data: %d - %d\n", 
//						cSize, m_startOfData, nZ, m_startOfData-nZ, data[0], data[nData-1]);
//			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
