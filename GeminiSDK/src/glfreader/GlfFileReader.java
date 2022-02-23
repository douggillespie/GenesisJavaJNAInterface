package glfreader;

import java.io.File;

public class GlfFileReader {

	public GlfFile catalogFile(String fileName) throws GlfException {
		
		File glfFile = new File(fileName);
		return catalogFile(glfFile);
	}

	public GlfFile catalogFile(File glfFile) throws GlfException {
		if (glfFile.exists() == false) {
			throw new GlfException("File " + glfFile.getAbsolutePath() + " does not exist");
		}
		
		
		
		return null;
	}
	
	
	
}
