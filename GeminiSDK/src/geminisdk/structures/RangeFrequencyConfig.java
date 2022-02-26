/**
 * 
 */
package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author dg50
 *
 */
public class RangeFrequencyConfig extends GeminiStructure {

	public static final int FREQUENCY_AUTO = 0;
	public static final int FREQUENCY_LOW = 1;
	public static final int FREQUENCY_HIGH = 2;
	
	public int m_frequency = FREQUENCY_AUTO;
	
	public double m_rangeThreshold = 40.;
	/**
	 * @param inputBytes
	 */
	public RangeFrequencyConfig(byte[] inputBytes) {
		super(inputBytes);
	}


	public RangeFrequencyConfig() {
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		try {
			dataOutput.writeInt(m_frequency);
			dataOutput.writeDouble(m_rangeThreshold);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean fromBytes(DataInput dataInput) {

		try {
			m_frequency = dataInput.readInt();
			m_rangeThreshold = dataInput.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected int dataOutputSize() {
		return 12;
	}

	public int defaultCommand() {
		return SVS5_CONFIG_RANGE_RESOLUTION;
	}

}
