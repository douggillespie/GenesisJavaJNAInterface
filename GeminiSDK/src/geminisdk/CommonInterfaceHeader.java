package geminisdk;

import java.io.IOException;

import tritechgemini.fileio.LittleEndianDataInputStream;

/**
 * This is the one member that's part of PublicMessageHeader which needs to be 
 * read at the start of some other callback objects, e.g. LoggerStatusInfo. 
 * <p>
 * shit - there is already one of these in image and there are all sorts of problems with byte alighnment !
 * @author dg50
 *
 */
public class CommonInterfaceHeader {


    protected byte   m_idChar;               // Start character used to signify start of CI header (Always = “*”)
    protected byte   m_version;              // Version number of the CI Header
    protected int  m_length;               // Total length (number of bytes) of CI Header and Data message.
    protected double  m_timestamp;            // Date / Time in UTC with millisecond resolution (format TBC)

    protected byte   m_dataType;             // Type of data contained in attached data record
                                    // 0 = SVS5
                                    // 1 = V4
                                    // 2 = Analog_Video
                                    // .
                                    // .
                                    // 98 = Raw_Serial
                                    // 99 = Generic (see Section 6.12.3)

    protected short  m_deviceID;             // This will be:
                                    // A Sonar ID number when m_dataType = 0
                                    // An incrementing deviceID when m_dataType = 99
                                    //(e.g. 1st GPS: m_deviceID = 1, 2nd GPS: m_deviceID = 2, etc)

    protected short  m_nodeID;               // node ID.

    protected short  m_spare;                // Reserved for future expansion
	
	public CommonInterfaceHeader() {
		
	}
	
	/**
	 * Create a common interface header from a data input stream. This is called by 
	 * other objects reading data from callback messages from svs5. 
	 * @param dis
	 * @throws IOException
	 */
	public CommonInterfaceHeader(LittleEndianDataInputStream dis) throws IOException {
		m_idChar = dis.readByte();
		m_version = dis.readByte();
		m_length = dis.readInt();
		m_timestamp = dis.readDouble();
		m_dataType = dis.readByte();
		m_deviceID = dis.readShort();
		m_nodeID = dis.readShort();
		m_spare = dis.readShort();
		
	}
	

}
