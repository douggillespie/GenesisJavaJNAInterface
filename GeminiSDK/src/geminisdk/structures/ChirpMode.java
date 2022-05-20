package geminisdk.structures;

public class ChirpMode extends IntStructure {
	

	static public final int CHIRP_DISABLED = 0;
	static public final int CHIRP_ENABLED = 1;
	static public final int CHIRP_AUTO = 2;

	public ChirpMode(byte[] inputBytes) {
		super(inputBytes);
		value = CHIRP_DISABLED;
	}

	public ChirpMode(int value) {
		super(value);
	}

	@Override
	public int defaultCommand() {
		return SVS5_CONFIG_CHIRP_MODE;
	}

	public static int[] getModes() {
		int[] modes = {CHIRP_DISABLED, CHIRP_ENABLED, CHIRP_AUTO};
		return modes;
	}
	
	public static String getModeName(int mode) {
		switch (mode) {
		case CHIRP_DISABLED:
			return "Disabled";
		case CHIRP_ENABLED:
			return "Enabled";
		case CHIRP_AUTO:
			return "Auto";
		}
		return "Unknown chirp mode";
	}
	
}
