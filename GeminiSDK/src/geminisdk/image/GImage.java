package geminisdk.image;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import geminisdk.structures.GeminiStructure;

public class GImage extends GeminiStructure {

    public short m_usImageVersion;  //!< Version number, in case if there is a change in the strcuture
    public short m_usRangeCompUsed;
    public int m_uiStartBearing;
    public int m_uiEndBearing;
    public int m_uiStartRange;
    public int m_uiEndRange; // 20 bytes to here. 
    public byte[] m_vecData;
    
    /*
     * size(GLF::GImage) in C returns 32, but there are only 28 obviously created bytes:
     * 2 int16 + 4 int32 + one pointer to a vector = 4 + 16 + 8 = 28. No idea where the
     * extra 4 come from. Is it the pointer ?  Is it rounding up to a 64 bit word boundary ? 
     * Size of vector* is 8, sizeof(vector<double>) is 24, which is about right. 
     * Looks like it's rounding up to a word boundary. 
     */

	public GImage(byte[] inputBytes) {
		super(inputBytes);
		// TODO Auto-generated constructor stub
	}

	public GImage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean fromBytes(DataInput dataInput) {
		try {
			m_usImageVersion = dataInput.readShort();
			m_usRangeCompUsed = dataInput.readShort();
			m_uiStartBearing = dataInput.readInt();
			m_uiEndBearing = dataInput.readInt();
			m_uiStartRange = dataInput.readInt();
			m_uiEndRange = dataInput.readInt();
			long vecPointer = dataInput.readLong(); // should be 8 bytes.
			int dum = dataInput.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
	protected int dataOutputSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}
