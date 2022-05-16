package geminisdk.image;

import java.io.DataInput;
import java.io.DataOutput;

import geminisdk.structures.GeminiStructure;

public class GLogTargetImage extends GeminiStructure {
	
	public CommonInterfaceHeader commonHeader;
	
	public GMainImage mainImage;

	public GLogTargetImage(byte[] inputBytes) {
		super(inputBytes);
		// TODO Auto-generated constructor stub
	}

	public GLogTargetImage() {
		// TODO Auto-generated constructor stub
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
	public boolean fromBytes(DataInput dataInput, int length) {
		commonHeader = new CommonInterfaceHeader();
		boolean headOk = commonHeader.fromBytes(dataInput, length);
		mainImage = new GMainImage();
		boolean imageOk = mainImage.fromBytes(dataInput, length);
		
		return headOk & imageOk;
	}

	@Override
	protected int dataOutputSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}
