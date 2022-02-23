package geminisdk;


public class SInputfileListInfo {

static int MAX_FILES_SUPPORTED    =     10;
static int MAX_FILENAME_LENGTH    =     256;
static int MAX_ERRORCODE_STR_LENGTH  =  512;

	int m_uiNumberOfRecords;
	int m_uiPercentProcessed;
	int m_uiNumberOfFiles;
	char[][] m_filenames = new char[MAX_FILES_SUPPORTED][MAX_FILENAME_LENGTH];
//	String[] m_filenames = new String[MAX_FILES_SUPPORTED];
	
}
