package geminisdk.structures;

public class SimulateADC extends BoolStructure {

	public SimulateADC(byte[] inputBytes) {
		super(inputBytes);
	}

	public SimulateADC(boolean value) {
		super(value);
	}

	@Override
	public int defaultCommand() {
		return SVS5_CONFIG_SIMULATE_ADC;
	}

}
