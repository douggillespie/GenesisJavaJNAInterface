package geminisdk.structures;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import tritechgemini.fileio.LittleEndianDataInputStream;

/**
 * Data pulled from a logger playback update message
 * @author dg50
 * 
#define MAX_FILES_SUPPORTED         10
#define MAX_FILENAME_LENGTH         256
    UInt32 m_uiNumberOfRecords;   !< Number of records in total 
    UInt32 m_uiPercentProcessed;  !< Percent of total list processed 
    UInt32 m_uiNumberOfFiles;     !< Number of files processed or being processed 
    char   m_filenames[MAX_FILES_SUPPORTED][MAX_FILENAME_LENGTH];           !< List of filenames 
 *
 */
public class LoggerPlaybackUpdate {

	public static final int MAX_FILES_SUPPORTED = 10;
	public static final int MAX_FILENAME_LENGTH = 256;
	public int m_uiNumberOfRecords;  // !< Number of records in total 
    public int m_uiPercentProcessed; // !< Percent of total list processed 
    public int m_uiNumberOfFiles;    // !< Number of files processed or being processed 
    public ArrayList<String> fileNames = new ArrayList<>();
    
	
	public LoggerPlaybackUpdate(int m_uiNumberOfRecords, int m_uiPercentProcessed, int m_uiNumberOfFiles) {
		super();
		this.m_uiNumberOfRecords = m_uiNumberOfRecords;
		this.m_uiPercentProcessed = m_uiPercentProcessed;
		this.m_uiNumberOfFiles = m_uiNumberOfFiles;
	}


	public static LoggerPlaybackUpdate readData(byte[] data, long size) {
		LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new ByteArrayInputStream(data));
		LoggerPlaybackUpdate loggerPlaybackUpdate = null;
		try {
			int m_uiNumberOfRecords = dis.readInt();
			int m_uiPercentProcessed = dis.readInt();
			int m_uiNumberOfFiles = dis.readInt();
			loggerPlaybackUpdate = new LoggerPlaybackUpdate(m_uiNumberOfRecords, m_uiPercentProcessed, m_uiNumberOfFiles);
			byte[] strData = new byte[MAX_FILENAME_LENGTH];
			for (int i = 0; i < MAX_FILES_SUPPORTED; i++) {
				dis.readFully(strData);
				String str = GeminiStructure.readNTString(strData);
				if (str.length() > 0) {
					loggerPlaybackUpdate.fileNames.add(str);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loggerPlaybackUpdate;
	}

}
