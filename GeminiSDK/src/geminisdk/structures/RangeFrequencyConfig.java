/**
 * 
 */
package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Range frequency resolution: 1200i only.  Using SVS5_CONFIG_RANGE_RESOLUTION <br>
 * This is basically a switch between 720 and 1200 kHz modes. 
 * @author dg50
 *
 */
public class RangeFrequencyConfig extends GeminiStructure {

	public static final int FREQUENCY_AUTO = 0;
	public static final int FREQUENCY_LOW = 1;
	public static final int FREQUENCY_HIGH = 2;
	
	public int m_frequency = FREQUENCY_LOW;
	
	public double m_rangeThreshold = 40.;
	/**
	 * @param inputBytes
	 */
	public RangeFrequencyConfig(byte[] inputBytes) {
		super(inputBytes);
	}

	public RangeFrequencyConfig(int config) {
		this.m_frequency = config;
	}

	public RangeFrequencyConfig() {
	}

	@Override
	public boolean toBytes(DataOutput dataOutput) {
		try {
			dataOutput.writeLong(m_frequency); // use long for an enum
			dataOutput.writeDouble(m_rangeThreshold);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean fromBytes(DataInput dataInput, int length) {

		try {
			m_frequency = (int) dataInput.readLong();
			m_rangeThreshold = dataInput.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public int dataOutputSize() {
		return 12;
	}

	public int defaultCommand() {
		return SVS5_CONFIG_RANGE_RESOLUTION;
	}

	/**
	 * Get the available modes. 
	 * @return available modes (0,1,2)
	 */
	public static int[] getModes() {
		int[] modes = {FREQUENCY_AUTO, FREQUENCY_LOW, FREQUENCY_HIGH};
		return modes;
	}
	
	/**
	 * Get the name of a mode. 
	 * @param mode mode index
	 * @return Name
	 */
	public static String getModeName(int mode) {
		switch (mode) {
		case FREQUENCY_AUTO:
			return "Auto";
		case FREQUENCY_LOW:
			return "Low";
		case FREQUENCY_HIGH:
			return "High";
		}
		return "Unknown frequency range mode";
	}
}
