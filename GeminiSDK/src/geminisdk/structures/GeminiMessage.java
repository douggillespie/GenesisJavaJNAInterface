package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

abstract public class GeminiMessage extends GeminiStructure {


	public CGemHdr gemHdr = new CGemHdr();
	  
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
	public boolean fromBytes(DataInput dataInput, int length) {
		if (gemHdr == null) {
			gemHdr = new CGemHdr();
		}
		return gemHdr.fromBytes(dataInput, length);
	}

	@Override
	public int dataOutputSize() {
		// TODO Auto-generated method stub
		return 8;
	}

}
