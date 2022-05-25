package geminisdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import geminisdk.structures.GeminiStructure;
import tritechgemini.fileio.LittleEndianDataInputStream;

/**
 * Object passed in callback with message Svs5MessageType.LOGGER_REC_UPDATE
 */
public class OutputFileInfo {

	private static final int MAX_FILENAME_LENGTH = 256;
	
	private String m_strFileName;
	private int m_uiNumberOfRecords;
	private long m_fileSizeBytes;
	private long m_diskSpaceFreeBytes;
	private double m_percentDiskSpaceFree;
	private long m_recordingTimeLeftSecs;
    
	public OutputFileInfo(String m_strFileName, int m_uiNumberOfRecords, long m_fileSizeBytes,
			long m_diskSpaceFreeBytes, double m_percentDiskSpaceFree, long m_recordingTimeLeftSecs) {
		super();
		this.m_strFileName = m_strFileName;
		this.m_uiNumberOfRecords = m_uiNumberOfRecords;
		this.m_fileSizeBytes = m_fileSizeBytes;
		this.m_diskSpaceFreeBytes = m_diskSpaceFreeBytes;
		this.m_percentDiskSpaceFree = m_percentDiskSpaceFree;
		this.m_recordingTimeLeftSecs = m_recordingTimeLeftSecs;
	}
	
	/**
	 * Unpack a message Svs5MessageType.LOGGER_REC_UPDATE and create a structure 
	 * @param data
	 * @return
	 */
	public static OutputFileInfo unpackMessageData(byte[] data) {
		LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new ByteArrayInputStream(data));
		byte[] nameData = new byte[256];
		try {
			dis.readFully(nameData);
			String name = GeminiStructure.readNTString(nameData);
			int numRecords = dis.readInt();
			long filesize = dis.readLong();
			long freeSpace = dis.readLong();
			dis.skipBytes(4); // get byte alighnemtn for the next double. 
			double percentdisk = dis.readDouble();
			long leftSecs = dis.readLong();
			return new OutputFileInfo(name, numRecords, filesize, freeSpace, percentdisk, leftSecs);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the maxFilenameLength
	 */
	public static int getMaxFilenameLength() {
		return MAX_FILENAME_LENGTH;
	}

	/**
	 * @return the m_strFileName
	 */
	public String getM_strFileName() {
		return m_strFileName;
	}

	/**
	 * This seems to be a count of the number of updates (callbacks message LOGGER_REC_UPDATE)
	 * rather than the number of frames, but it does reset to 0 when a new file starts
	 * @return the m_uiNumberOfRecords
	 */
	public int getM_uiNumberOfRecords() {
		return m_uiNumberOfRecords;
	}

	/**
	 * @return the m_fileSizeBytes
	 */
	public long getM_fileSizeBytes() {
		return m_fileSizeBytes;
	}

	/**
	 * @return the m_diskSpaceFreeBytes
	 */
	public long getM_diskSpaceFreeBytes() {
		return m_diskSpaceFreeBytes;
	}

	/**
	 * @return the m_percentDiskSpaceFree
	 */
	public double getM_percentDiskSpaceFree() {
		return m_percentDiskSpaceFree;
	}

	/**
	 * @return the m_recordingTimeLeftSecs
	 */
	public long getM_recordingTimeLeftSecs() {
		return m_recordingTimeLeftSecs;
	}
	
}
