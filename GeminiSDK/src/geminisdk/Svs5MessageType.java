package geminisdk;

public class Svs5MessageType {

	    public static final int GEMINI_STATUS = 0;
	    public static final int  ECD_LIVE_TARGET_IMAGE = 1;  //Deprecated
	    public static final int  GLF_LIVE_TARGET_IMAGE = 2;
	    public static final int  SENSOR_RECORD = 3;
	    public static final int  LOGGER_REC_UPDATE = 4;      // See structure GLF::SOutputFileInfo
	    public static final int LOGGER_PLAYBACK_UPDATE = 5; // See structure GLF::SInputFileListInfo
	    public static final int  TGT_IMG_PLAYBACK = 6;
	    public static final int  ECD_IMG_PLAYBACK = 7;       //Deprecated
	    public static final int  LOGGER_FILE_INDEX = 8;
	    public static final int   LOGGER_STATUS_INFO = 9;
	    public static final int  FRAME_RATE = 10;
	    public static final int   GPS_RECORD = 11;
	    public static final int  COMPASS_RECORD = 12;
	    public static final int  AUXPORT1_DATA = 13;
	    public static final int  AUXPORT2_DATA = 14;

}
