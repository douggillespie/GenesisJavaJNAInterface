package geminisdk.structures;


public class GemRecord extends BoolStructure {

	public GemRecord(byte[] inputBytes) {
		super(inputBytes);
		// TODO Auto-generated constructor stub
	}

	public GemRecord(boolean record) {
		super(record);
	}

	@Override
	public int defaultCommand() {
		return SVS5_CONFIG_REC;
	}

	/**
	 * Is recording set ?
	 * @return recording state
	 */
	public boolean isRecord() {
		return this.value;
	}
	
}
