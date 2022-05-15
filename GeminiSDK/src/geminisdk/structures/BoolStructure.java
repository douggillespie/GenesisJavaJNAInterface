package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class BoolStructure extends GeminiStructure {
	
	public boolean value;

	public BoolStructure(byte[] inputBytes) {
		super(inputBytes);
	}

	public BoolStructure(boolean value) {
		this.value = value;
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		try {
			for (int i = 0; i < 4; i++) {
				dataOutput.writeByte(value ? 1 : 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean fromBytes(DataInput dataInput) {
		try {
			value = dataInput.readByte() == 0 ? false : true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected int dataOutputSize() {
		return 4;
	}

}
