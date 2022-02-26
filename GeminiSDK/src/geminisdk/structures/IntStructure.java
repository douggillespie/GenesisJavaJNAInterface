package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

abstract public class IntStructure extends GeminiStructure {
	
	int value;

	public IntStructure(byte[] inputBytes) {
		super(inputBytes);
	}

	public IntStructure(int value) {
		this.value = value;
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		try {
			dataOutput.writeInt(value);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean fromBytes(DataInput dataInput) {
		try {
			value = dataInput.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected int dataOutputSize() {
		return Integer.BYTES;
	}

}
