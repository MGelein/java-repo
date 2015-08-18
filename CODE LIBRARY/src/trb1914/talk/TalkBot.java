package trb1914.talk;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Uses the Windows Speech API to say things. This means this class
 * IS os specific!!!!
 * @author Mees Gelein
 */
public class TalkBot {

	/**The lines we use to create the vbscript*/
	private String[] lines = {"Dim Message, Speak",
			"Message=",
			"Set Speak=CreateObject(\"sapi.spvoice\")",
			"Speak.Speak Message"};
	/**Temporary file used to save the talkrobot and make it say stuff using vbs*/
	private File tempFile = new File("tempFile.vbs");
	
	/**
	 * Tries to say the provided message
	 * @param s
	 */
	public void say(String s){
		lines[1] = "Message=\"" + s + "\"";
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			for(int i = 0; i < lines.length; i++){
				writer.write(lines[i]);
				writer.newLine();
			}
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(Desktop.isDesktopSupported()){
			try {
				Desktop.getDesktop().open(tempFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tempFile.deleteOnExit();
	}
	
	/**
	 * Deletes any temporary files created by this talkbot.
	 */
	public void destroy(){
		if(tempFile.exists()) tempFile.delete();
	}
}
