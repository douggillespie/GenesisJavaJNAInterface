package geminisdk.structures;

public class ConfigOnline extends BoolStructure {

	public ConfigOnline(byte[] inputBytes) {
		super(inputBytes);
	}

	public ConfigOnline(boolean value) {
		super(value);
	}

	@Override
	public int defaultCommand() {
		return SVS5_CONFIG_ONLINE;
	}

}
