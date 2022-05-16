package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class GemFileLocation extends GeminiStructure {

	private String filePath;

	public GemFileLocation(byte[] inputBytes) {
		super(inputBytes);
	}

	public GemFileLocation(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		try {
			dataOutput.writeBytes(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public int defaultCommand() {
		return SVS5_CONFIG_FILE_LOCATION;
	}

	@Override
	public boolean fromBytes(DataInput dataInput, int length) {
		byte[] data = new byte[length];
		try {
			dataInput.readFully(data);
			filePath = new String(data);
		} catch (IOException e) {
			e.printStackTrace();
			filePath = null;
			return false;
		}
		return true;
	}

	@Override
	protected int dataOutputSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

}
