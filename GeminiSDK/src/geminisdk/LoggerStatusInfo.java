package geminisdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import geminisdk.structures.GeminiStructure;
import tritechgemini.fileio.LittleEndianDataInputStream;

public class LoggerStatusInfo {

	// match to the C type GLF::GnsLoggerStatusInfo
	public static final int MAX_ERRORCODE_STR_LENGTH = 512;
	
	private byte[] m_loggerStatusInfo = new byte[MAX_ERRORCODE_STR_LENGTH];
	
	private int errorType = -1;

	private String loggerStatusInfo;
	
	/*
	 * Error type will be one of 
	  	enum ErrorType{
    		INFO_TYPE,
    		WARNING_TYPE,
    		ERROR_TYPE
		};
	 */
	
	/**
	 * construct from the arrival of a Svs5Message.
	 * @param data raw data from message
	 */
	public LoggerStatusInfo(byte[] data) {
		int msgLen = Math.min(MAX_ERRORCODE_STR_LENGTH, data.length);
		m_loggerStatusInfo = Arrays.copyOf(data, msgLen);
		loggerStatusInfo = GeminiStructure.readNTString(m_loggerStatusInfo);
		if (data.length >= MAX_ERRORCODE_STR_LENGTH + Integer.SIZE) {
			LittleEndianDataInputStream is = new LittleEndianDataInputStream(new ByteArrayInputStream(data, MAX_ERRORCODE_STR_LENGTH, Integer.SIZE));
			try {
				errorType = is.readInt();
			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @return the errorType
	 */
	public int getErrorType() {
		return errorType;
	}

	/**
	 * @return the loggerStatusInfo
	 */
	public String getLoggerStatusInfo() {
		return loggerStatusInfo;
	}

}
