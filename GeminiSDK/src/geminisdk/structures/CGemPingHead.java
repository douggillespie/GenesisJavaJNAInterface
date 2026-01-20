package geminisdk.structures;

import java.io.DataInput;
import java.io.IOException;

public class CGemPingHead extends CGemData {

	public short m_pingID;
	public short m_extMode;
	/*
	 * These are times since power on, so not massively helpful
	 */
	public long   m_transmitTimestampL;
	public long   m_transmitTimestampH;
	public long  m_transmitTimestamp;
	public short m_startRange;
	public short m_endRange;
	public int   m_lineTime;
	public short m_numBeams;
	public short m_numChans;
	public byte  m_sampChan;
	public byte  m_baseGain;
	public int m_spdSndVel;
	public short m_velEchoTime;
	public short m_velEntries;
	public short m_velGain;
	public int m_sosUsed;
	public byte  m_RLEThresholdUsed;
	public byte  m_rangeCompressionUsed;
	public byte  m_decimation;
	private int sosType; // 0 = velocometer, 1 = ping config

	public CGemPingHead(CGemHdr cGemHdr) {
		super(cGemHdr);
	}

	public boolean read(DataInput dis) {
		try {
			m_pingID = dis.readShort();
			m_extMode = dis.readShort();
			m_transmitTimestampL = Integer.toUnsignedLong(dis.readInt());
			m_transmitTimestampH = Integer.toUnsignedLong(dis.readInt());
			m_transmitTimestamp = (m_transmitTimestampH<<32) + m_transmitTimestampL;
			m_startRange = dis.readShort();
			m_endRange = dis.readShort();
			m_lineTime = dis.readInt();
			m_numBeams = dis.readShort();
			m_numChans = dis.readShort();
			m_sampChan = dis.readByte();
			m_baseGain = dis.readByte();
			m_spdSndVel = dis.readUnsignedShort();
			m_velEchoTime = dis.readShort();
			m_velEntries = dis.readShort();
			m_velGain = dis.readShort();
			m_sosUsed = dis.readUnsignedShort();
			sosType = (1<<15 & m_sosUsed) >> 15;
			m_sosUsed = m_sosUsed & 0x7FFF;
			m_RLEThresholdUsed = dis.readByte();
			m_rangeCompressionUsed = dis.readByte();
			m_decimation = dis.readByte();
//			System.out.printf("New ping head id %d\n", Short.toUnsignedInt(m_pingID));
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
