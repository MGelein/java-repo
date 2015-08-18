package trb1914.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import trb1914.debug.Debug;

/**
 * Contains some static file helper methods
 * @author Mees Gelein
 */
public abstract class FileHelper {

	/**
	 * Returns the file name without extension
	 * @param f
	 * @return
	 */
	public static String getFileName(File f){
		String name = f.getName();
		int i = name.lastIndexOf(".");
		if(i > -1){
			return name.substring(0, i);
		}else{
			return name;
		}
	}
	
	/**
	 * Returns the extension (everything after the last dot) of the file
	 * @param f
	 * @return
	 */
	public static String getFileExtension(File f){
		String name = f.getName();
		int i = name.lastIndexOf(".");
		if(i > -1){
			return name.substring(i + 1);
		}else{
			return name;
		}
	}
	
	/**
	 * Returns the provided location as an InputStream
	 * @param url
	 * @return
	 */
	public static InputStream getInputStream(String url){
		return SystemHelper.TOOLKIT.getClass().getResourceAsStream(url);
	}
	
	/**
	 * Saves the provided string on the provided location
	 * @param location
	 */
	public static void saveString(String content, File location, boolean utf8){
		try{
			BufferedWriter writer;
			if(utf8) writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(location),"UTF-8"));
			else writer = new BufferedWriter(new FileWriter(location));
			String[] lines = content.split("\n");
			for(String line : lines){
				writer.write(line);
				writer.newLine();
			}
			writer.close();
		}catch(Exception e){
			Debug.println("[trb1914.helper.FileHelper]: Could not save the provided content to the provided location: " + location.getAbsolutePath());
		}
	}
	
	/**
	 * Returns the String contents read from a file
	 * @param location
	 * @return
	 */
	public static String openFile(File location, boolean utf8){
		try{
			BufferedReader reader;
			if(!utf8) reader = new BufferedReader(new FileReader(location));
			else reader = new BufferedReader(new InputStreamReader(new FileInputStream(location), "UTF-8"));
			String line = reader.readLine();
			StringBuilder content = new StringBuilder();
			while(line != null){
				content.append(line);
				line = reader.readLine();
			}
			reader.close();
			return content.toString();
		}catch(Exception e){
			Debug.println("[trb1914.helper.FileHelper]: Could not open the provided location: " + location.getAbsolutePath());
		}
		return "";
	}
}
