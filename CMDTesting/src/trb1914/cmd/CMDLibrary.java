package trb1914.cmd;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * A class that evaluates and executes user defined commands
 * @author Mees Gelein
 */
public class CMDLibrary {

	/**
	 * Returns the desktop if it can, otherwise it exits with an error
	 * @return
	 */
	public static Desktop getDesktop(){
		if(Desktop.isDesktopSupported()){
			return Desktop.getDesktop();
		}
		System.out.println("Error, could not execute, Desktop is not supported.");
		System.exit(0);
		return null;
	}

	/**
	 * The entry point
	 * @param args	the command line params
	 */
	public static void main(String[] args) {
		if(args.length < 1) return;//stop running if there are no commands

		switch(args[0].toLowerCase()){
		case "open":
			for(String arg : subset(args)) open(arg);
			break;
		case "create":
			create(subset(args));
			break;
		case "get":
			get(subset(args));
			break;
		case "info":
			info(subset(args));
			break;
		case "power":
			power(subset(args));
			break;
		}
	}
	
	/**
	 * Changes the powersaving method to the provided option
	 * @param args
	 */
	private static void power(String[] args){
		String guid = "unrecognized";
		switch(args[0].toLowerCase()){
		case "low":
		case "lo":
			guid = "a1841308-3541-4fab-bc81-f71556f20b4a";
			break;
		case "high":
		case "hi":
			guid = "8c5e7fda-e8bf-4a96-9a85-a6e23a8c635c";
			break;
		case "medium":
		case "med":
			guid = "381b4222-f694-41f0-9685-ff5bb260df2e";
			break;
		}
		if(guid.equals("unrecognized")){
			System.out.println("Unrecognized powerconfiguration!");
		}else{
			System.out.println("Setting powerconfiguration to: " + guid);
			try {
				Runtime.getRuntime().exec("powercfg /s " + guid);
				System.out.println("Succes!");
			} catch (IOException e) {
				System.out.println("Failed!");
				//e.printStackTrace();
			}
		}
		//always print currently active powercfg
		System.out.println("---------------------");
		System.out.println("Currently active powerconfiguration:");
		Process p;
		try {
			p = Runtime.getRuntime().exec("powercfg /getactivescheme");
			BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
			System.out.println(output.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Opens with the provided params
	 * @param args
	 */
	private static void open(String arg){
		//first check if it is defined in the shorthand list
		ArrayList<CMDDefinition> cmdList = parseDefinition(new File("C:\\ProgramData\\CustomCMD\\open.txt"));
		if(cmdList != null){
			for(CMDDefinition def : cmdList){
				if(def.name.equals(arg.toLowerCase())){//we found a match for short command version
					def.execute();
					return;
				}
			}
		}
		//if we reach this code, we handle it as if it is a url to open
		openURL(arg);

	}

	/**
	 *Interprets the provided string as a url
	 * @param url
	 */
	public static void openURL(String url){
		url = url.toLowerCase();
		if(url.startsWith("www") || url.endsWith(".com") || url.endsWith(".nl")){//probably web based
			System.out.println("Detected web-address. Opening in browser... " + url);
			try {
				getDesktop().browse(new URI("http://" + url));
			} catch (Exception e) {
				System.out.println("Failed. Check your URL syntax.");
			}
		}else{//open it as if it is a file
			System.out.println("Detected local file. Opening with default method... " + url);
			try {
				getDesktop().open(new File(url));
			} catch (Exception e) {
				System.out.println("Failed. File does not exist or can't be opened.");
			}
		}
	}

	/**
	 * Creates with the provided params
	 * @param args
	 */
	private static void create(String[] args){
		if(args[0].contains("d")){
			for(String arg : subset(args)){
				File f = new File(arg);
				if(!f.exists()){
					System.out.println("Creating directory... " + f.getName());
					boolean succes = f.mkdirs();
					System.out.println(((succes) ? "Success" : "Failed"));
					
				}else{
					System.out.println("Directory already exists. Creation is cancelled.");
				}
			}
		}else if(args[0].contains("f")){
			for(String arg : subset(args)){
				File f = new File(arg);
				if(!f.exists()){
					System.out.println("Creating file... " + f.getName());
					boolean succes = false;
					try {
						succes = f.createNewFile();
					} catch (IOException e) {}
					System.out.println(((succes) ? "Success" : "Failed"));
					
				}else{
					System.out.println("File already exists. Creation is cancelled.");
				}
			}
		}else{
			System.out.println("Creation type unrecognized. Currently only file and directory are supported");
		}
	}

	/**
	 * Downloads or retrieves with the provided params
	 */
	private static void get(String[] args){
		try {
			if(!args[0].startsWith("http://")) args[0] = "http://" + args[0];
			URL url = new URL(args[0]);
			File targetFile = new File(url.getFile().substring(1));
			int fileSize = getFileSize(url);
			System.out.println("Downloading " + targetFile.getName() + " : " + fileSize + " bytes");
			
			InputStream inputStream = url.openStream();
			OutputStream outputStream = new FileOutputStream(targetFile);
			
			byte[] bytes = new byte[2048];
			int length;	
			int totalLength = 0;
			float percentage = 0.0f;
			String percentageString = "0%";
			System.out.print(percentageString + " downloaded");
			while((length = inputStream.read(bytes)) != -1){
				outputStream.write(bytes, 0, length);
				totalLength += length;
				
				percentage = ((totalLength * 1.0f) / (fileSize * 1.0f)) * 100;
				
				System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
				percentageString = Float.toString(percentage).substring(0, 3) + "% downloaded";
				System.out.print(percentageString);
			}
			System.out.println();
			System.out.println("Download finished.");
			
			inputStream.close();
			outputStream.close();
			
		} catch (MalformedURLException e) {
			System.out.println("The provided URL was Malformed");
		} catch (IOException e) {
			System.out.println("The file couldn't not be downloaded, stream couldn't be opened.");
		}
	}
	
	/**
	 * Returns the content-length in the http head. Returns -1 if the length cannot be retrieved
	 * @param url
	 * @return
	 */
	private static int getFileSize(URL url){
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("HEAD");
			connection.getInputStream();
			return connection.getContentLength();
		} catch (IOException e) {
			System.out.println("Couldn't retrieve file size");
		}finally {
			connection.disconnect();
		}
		return -1;
	}

	/**
	 * Checks with the provided params
	 */
	private static void info(String[] args){
		
		//if it is not a predefined command, it is probably	 a file, give file info
		File f = new File(args[0]);
		if(f.exists()){
			System.out.println("Name: " + f.getName());
			System.out.println("Directory: " + new File(f.getAbsolutePath()).getParent());
			System.out.println("Filesize: " + f.length() + " bytes");
			System.out.println("Last edited: " + new Date(f.lastModified()));
			String permissions = ".";
			if(f.canRead() && f.canWrite()) permissions = "read/write";
			else if(f.canRead()) permissions = "read";
			else if(f.canWrite()) permissions = "write";
			else permissions = "none";
			System.out.println("Permissions: " + permissions);
		}else{
			System.out.println("No info could be retrieved since the file does not exist.");
		}
	}

	/**
	 * Returns the array without the first element
	 * @param array
	 * @return
	 */
	private static String[] subset(String[] array){
		return Arrays.copyOfRange(array, 1, array.length);
	}

	/**
	 * Loads and parses a list of command definitions
	 * @param f
	 * @return
	 */
	private static ArrayList<CMDDefinition> parseDefinition(File f){
		//check to see if we can load the predefined 
		if(!f.exists()) return null;

		//setup
		ArrayList<CMDDefinition> cmdList = new ArrayList<CMDDefinition>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line = reader.readLine();
			while(line != null){
				cmdList.add(new CMDDefinition(line));
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.out.println(f.getAbsolutePath() + "doesnt exist");
			return null;
		}

		return cmdList;
	}
}
