package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import tritechgemini.fileio.LittleEndianDataOutputStream;

public class GeminiRange extends GeminiStructure {
	
	public double range;

	public GeminiRange(byte[] inputBytes) {
		super(inputBytes);
		// TODO Auto-generated constructor stub
	}

	public GeminiRange(double range) {
		this.range = range;
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		try {
			dataOutput.writeDouble(range);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public int defaultCommand() {
		return SVS5_CONFIG_RANGE;
	}

	@Override
	public boolean fromBytes(DataInput dataInput, int length) {
		try {
			range = dataInput.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public int dataOutputSize() {
		return Double.BYTES;
	}

}
