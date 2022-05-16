package geminisdk.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class GeminiGain  extends GeminiStructure {
		
		public int gain;

		public GeminiGain(byte[] inputBytes) {
			super(inputBytes);
			// TODO Auto-generated constructor stub
		}

		public GeminiGain(int gain) {
			this.gain = gain;
		}

		@Override
		public boolean toBytes(DataOutput dataOutput) {
			try {
				dataOutput.writeInt(gain);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		public int defaultCommand() {
			return SVS5_CONFIG_GAIN;
		}

		@Override
		public boolean fromBytes(DataInput dataInput, int length) {
			try {
				gain = dataInput.readInt();
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
