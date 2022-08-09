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

	    
	    public static final int NUM_MESSAGETYPES = 20; // number of message types. now at least 16 
	    
	    public static String getMessageName(int message) {
	    	switch (message) {
	    	case GEMINI_STATUS:
	    		return "GEMINI_STATUS";
	    	case ECD_LIVE_TARGET_IMAGE:
	    		return "ECD_LIVE_TARGET_IMAGE";
	    	case GLF_LIVE_TARGET_IMAGE:
	    		return "GLF_LIVE_TARGET_IMAGE";
	    	case SENSOR_RECORD:
	    		return "SENSOR_RECORD";
	    	case LOGGER_REC_UPDATE:
	    		return "LOGGER_REC_UPDATE";
	    	case LOGGER_PLAYBACK_UPDATE:
	    		return "LOGGER_PLAYBACK_UPDATE";
	    	case TGT_IMG_PLAYBACK:
	    		return "TGT_IMG_PLAYBACK";
	    	case ECD_IMG_PLAYBACK:
	    		return "ECD_IMG_PLAYBACK";
	    	case LOGGER_FILE_INDEX:
	    		return "LOGGER_FILE_INDEX";
	    	case LOGGER_STATUS_INFO:
	    		return "LOGGER_STATUS_INFO";
	    	case FRAME_RATE:
	    		return "FRAME_RATE";
	    	case GPS_RECORD:
	    		return "GPS_RECORD";
	    	case COMPASS_RECORD:
	    		return "COMPASS_RECORD";
	    	case AUXPORT1_DATA:
	    		return "AUXPORT1_DATA";
	    	case AUXPORT2_DATA:
	    		return "AUXPORT2_DATA";
	    	}
	    	return "Unknown message id " + message;
	    }
}
