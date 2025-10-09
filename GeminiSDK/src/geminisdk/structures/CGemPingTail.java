package geminisdk.structures;

import java.io.DataInput;
import java.io.IOException;

public class CGemPingTail extends CGemData {

	  public int  m_pingID;
	  public byte  m_flags;
	  public short m_spare;
	  
	public CGemPingTail(CGemHdr cGemHdr) {
		super(cGemHdr);
	}

	public boolean read(DataInput dis) {
		try {
			m_pingID = Byte.toUnsignedInt(dis.readByte());
			m_flags = dis.readByte();
			m_spare = dis.readShort();
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
