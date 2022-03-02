package geminisdk.image;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import geminisdk.structures.GeminiStructure;

public class GMainImage extends GImage {
	/**
	 * There is basically not a cats chance in hell of unpacking this thing entirely on the Java side
	 * be sending through a pointer. First off, the C compiler has packed the structures so that float, 
	 * double and pointers are all aligned on 8 byte boundaries. That's kind of OK if we assume it's stable
	 * but might be flakey and may not be entirely the same in different windows versions and processing 
	 * platforms ? I really don't know, but although I can unpack it byte by byte and get it to give sensible
	 * values, it's flakey as ....
	 * Second off, the data contain at least two pointers to vectors of the data. The Pointer is 8 bytes
	 * which can be turned into a JNA pointer, and I can try to read the 24 bytes equivalent to the sizeof(vector)
	 * but when I do, nothing makes sense and further, some of those 24 bytes must be yet another pointer
	 * to the allocated array of the actual data. In any case, I see no bytes in the 24 which may correspond 
	 * to the expected 512 beams for current data (0x200) since I can't see how Vector would work without 
	 * storing it's size in it's header. I'd guess the header is a pointer, an int of the current size and an int
	 * of the allocated size, but don't see that in any form. 
	 * So what to do ? GMainImage and GImage both have a copyTo function, which I'm guessing is used by the serialiser 
	 * to make files. I'm therefore guessing that if I call this with a very big fat amount of allocated memory, it's going
	 * to write the data in the exact same format it uses for the GLF files. I can then transfer the byte buffer via a 
	 * different callback function and unpack it using the same reads that I use for unpacking files. Hopefully 
	 * this won't be too slow. Alternative plans would be a reverse callback where I pass back a Java object and it's filled, 
	 * all in the same thread, while the memory is still allocated and copy over data variable by variable. 
	 * This is similar to what i've done in the GLF reader via the C API (which I don't use). Either way probably makes 
	 * little difference and writing raw data will be safest since that means less reliance on equivalence between a
	 * java structure and a C structure and how they map memory.    
	 * Using the former method I can actually use the same callback. I just need to know that the data for that enum 
	 * are packed in their particular way!   
	 */

//    std::vector<double>* m_vecBearingTable;      //!< bearing table pointer
	public double[] bearingTable;
	public int              m_uiStateFlags;         //!< State Flags
                                                //!< Bit 15-13: Sonar orientation 0: Up, 1: down
    public int              m_uiModulationFrequency;//!< The modulation frequency
    public float               m_fBeamFormAperture;    //!< Calculate the number of beams to provide the correct aperture
    public double              m_dbTxTime;             //!< Time of transmit
    public short              m_usPingFlags;          //!< Ping Flags
                                                //!< Bit 0:  ( 1: HF, 0 : LF )
                                                //!< Bit 15: ( 1: Manual, 0 : sonar )
    public float               m_fSosAtXd;             //!< The SOS at the transducer
    public  int               m_sPercentGain;         //!< The Percentage gain used to record
    public boolean                m_fChirp;               //!< Chirp enabled/disabled
    public byte               m_ucSonarType;          //!< MK1 Imager, MK1 Profiler, Mk2 Imager
    public  byte               m_ucPlatform;           //!< Platform 720im, 720is, 720ik, 1200ik etc
    
    public long bearingPointer;
    
    /**
     * Size of GMainImate should be 31 for data + 8 for bearing table + 32 for the GImage superclass
     * which comes to 71 but we're getting 80 for sizeof(GMainImage). So I guess it's rounding things
     * up again. where is it packing ?  At start of read we're at byte 56 in input stream = 7 8 bit 
     * words. 24 for common header, 32 for GImage. 
     */
    /**
     * 
     * @param inputBytes
     */
	public GMainImage(byte[] inputBytes) {
		super(inputBytes);
		// TODO Auto-generated constructor stub
	}

	public GMainImage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean fromBytes(DataInput dataInput) {
		super.fromBytes(dataInput);
		try {
			bearingPointer = dataInput.readLong();
			m_uiStateFlags = dataInput.readInt();
			m_uiModulationFrequency = dataInput.readInt();
			m_fBeamFormAperture = dataInput.readFloat(); // probably correct at 120.
			dataInput.skipBytes(4); // move to word boundary
			m_dbTxTime = dataInput.readDouble();
			m_usPingFlags = dataInput.readShort();
			dataInput.skipBytes(2); // move to word boundary. 
			m_fSosAtXd = dataInput.readFloat();
			m_sPercentGain = dataInput.readShort();
			m_fChirp = dataInput.readByte() == 0 ? false : true;
			m_ucSonarType = dataInput.readByte();
			m_ucPlatform = dataInput.readByte(); // ok to here with the above skips. 
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
