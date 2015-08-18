package trb1914.alexml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import trb1914.alexml.data.FileRegistry;

/**
 * A simple class that is a gateway to System.out to allow 
 * the System.out.println calls to be stored in a StringBuilder
 * and saved in a file after the application closes
 * @author Mees Gelein
 */
public class Debug {

	//is debug mode enabled?
	private static boolean debug = true;
	
	//the StringBuilder all strings are appended to
	private static StringBuilder debugContent = new StringBuilder();

	/**
	 * Prints the provided String in the console
	 * and saves it in a local StringBuilder if 
	 * debugging is enabled to save it after the 
	 * application closes
	 * @param s
	 */
	public static void print(String s){
		System.out.print(s);
		if(debug){
			debugContent.append(s);
		}
	}
	
	/**
	 * Prints the provided String and a newline in the console
	 * and saves it in a local StringBuilder if 
	 * debugging is enabled to save it after the 
	 * application closes. The object reference is used to automatically display
	 * the class the println call came from
	 * @param o		the object that called this method
	 * @param s
	 */
	public static void println(String s, Object o){
		String temp = o.getClass().getName();
		int index = temp.lastIndexOf(".");
		if(index != -1) temp = temp.substring(index + 1);
		index = temp.lastIndexOf("$");
		if(index != -1) temp = temp.substring(0, index);
		println("[" + temp + "]: "+ s);
	}

	/**
	 * Prints the provided String and a newline in the console
	 * and saves it in a local StringBuilder if 
	 * debugging is enabled to save it after the 
	 * application closes
	 * @param s
	 */
	private static void println(String s){
		Debug.print(s.concat("\n"));
	}

	/**
	 * creates a new log file at the location that is encoded in the FileRegistry.
	 * This also empties the local Storage of all debugContent, so after this call 
	 * you can start with a clean slate. 
	 */
	public static void createLogFile(){
		new Thread(new Runnable(){
			public void run(){
				File f = new File(FileRegistry.LOG_FILE);
				System.out.println("[Debug]: Creating log file at: " + f.getAbsolutePath());
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String date = dateFormat.format(Calendar.getInstance().getTime());
				try{
					BufferedWriter writer = new BufferedWriter(new FileWriter(f));

					String s = debugContent.toString();
					String[] lines = s.split("\n");
					System.out.println("[Debug]: Creating log file of " + lines.length + " lines");
					writer.write("[" + date + "]");
					writer.newLine();
					for(int i = 0; i < lines.length; i++){
						writer.write(lines[i]);
						writer.newLine();
					}
					writer.close();
					debugContent = new StringBuilder();
				}catch(Exception e){
					System.out.println("Couldn't produce error file. Error-ception :P");
				}
			}
		}).start();
	}

	/**
	 * turns debug mode on or off
	 * @param b
	 */
	public static void setDebugMode(boolean b){
		debug = b;
	}
}
