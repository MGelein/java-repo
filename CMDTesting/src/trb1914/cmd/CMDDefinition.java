package trb1914.cmd;
/**
 * An object that represents a command and the shortened form
 * @author Mees Gelein
 */
public class CMDDefinition {

	/**The short version of the command*/
	public String name = "";
	public String longVersion = "";
	
	/**
	 * Constructs this object based on 1 definition line
	 * @param defLine
	 */
	public CMDDefinition(String defLine){
		String[] parts = defLine.split("=");
		name = parts[0];
		if(parts.length > 1) longVersion = parts[1];
	}
	
	/**
	 * Tries to execute the long version of this command
	 */
	public void execute(){
		CMDLibrary.openURL(longVersion);
	}
}
