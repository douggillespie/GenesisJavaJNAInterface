package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PingMode extends GeminiStructure {
	
	boolean m_bFreeRun = true;
	
	short m_msInterval = 20;

	public PingMode(byte[] inputBytes) {
		super(inputBytes);
	}

	public PingMode() {
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		try {
			dataOutput.writeByte(m_bFreeRun ? 1 : 0);
			dataOutput.writeShort(m_msInterval);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public int defaultCommand() {
		return SVS5_CONFIG_PING_MODE;
	}

	@Override
	public boolean fromBytes(DataInput dataInput) {
		try {
			m_bFreeRun = (dataInput.readByte() == 0) ? false : true;
			m_msInterval = dataInput.readShort();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	protected int dataOutputSize() {
		return 3;
	}

}
