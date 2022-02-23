package geminisdk;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class PamGlfRecord extends Structure {

	public int framNumber;
	
	public char idChar;
	
	public int version;
	
	public int dataType;
	
	public double geminiTimestamp;
	
	public int deviceId;
	
	public int nodeID;
	
	public Pointer bearingTable;
	
	public int nBearing;
	
	public int startBearing; 
	
	public int endBearing;
	
	public int startRange;
	
	public int endRange;
	
	public int stateFlags;
	
	public int modulationFrequency;
	
	public double txTime;
	
	public int txFlags;
	
	public float sosAtXd;
	
	public short percentGain;
	
	public boolean fChirp;
	
	public byte ucSonarType;
	
	public byte ucPlatform;
	
	public Pointer imageData;
	
	public int dataLength;
	
	
	
    public PamGlfRecord() {
        super();
    }
    
    @Override
    public void finalize() {
    	// I think we need this for pointers allocated C side. 
    	Native.free(Pointer.nativeValue(bearingTable)); // ?
    	Native.free(Pointer.nativeValue(imageData)); // ?
    }
    
    public double[] getBearingTable() {
    	if (bearingTable == null) {
    		return null;
    	}
    	return bearingTable.getDoubleArray(0, nBearing);
    }
    
    public byte[] getImageData() {
    	if (imageData == null) {
    		return null;
    	}
    	return imageData.getByteArray(0, dataLength);
    }

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("framNumber",
		"idChar",
		"version",
		"dataType",
		"geminiTimestamp",
		"deviceId",
		"nodeID",
		"bearingTable",
		"nBearing",
		"startBearing",
		"endBearing",
		"startRange",
		"endRange",
		"stateFlags",
		"modulationFrequency",
		"txTime",
		"txFlags",
		"sosAtXd",
		"percentGain",
		"fChirp",
		"ucSonarType",
		"ucPlatform",
		"imageData",
		"dataLength");
	}
	
    public PamGlfRecord(Pointer peer) {
        super(peer);
    }

    public static class ByReference extends PamGlfRecord implements Structure.ByReference {
    };

    public static class ByValue extends PamGlfRecord implements Structure.ByValue {
    };

}
