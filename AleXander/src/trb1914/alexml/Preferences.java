package trb1914.alexml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.gui.CodeSettingsEditor;

public class Preferences {

	///the allowed Look and Feels
	private static String[] allowedLFS = new String[]{"GTK+","Windows", "Nimbus"};
	///internally contains the file that has been read
	private static String preferences="";
	///the position of the main tool bar in the segmentview
	public static String mainToolBarPos = "East";
	///is the window maximized
	public static boolean maximized = false;
	///width of the window
	public static int sizeW = 1000;
	///height of the window
	public static int sizeH = 720;
	///default width of the window
	public static int defaultSizeW = 1000;
	///default height of the window
	public static int defaultSizeH = 720;
	///the last open directory's path
	public static String lastOpenDir = "$";
	///application language
	public static String language = "EN";
	///check the interwebs for updates?
	public static boolean checkForUpdates = true;
	///the 5 most recent files
	public static String[] recentFiles = new String[5];
	///the name of the current Look And Feel
	public static String LF = UIManager.getLookAndFeel().getName();
	///all the installed LF's 
	public static LookAndFeelInfo[] installedLF = UIManager.getInstalledLookAndFeels();
	///flag to indicate if a logging file will be produced on closing
	public static boolean debug = false;
	///do we create backups every BACKUP_DELAY?
	public static boolean backup = true;
	///Main color used for text
	public static String mainColor = "#000000";
	///The color of an XML tag
	public static String tagColor = "#0000FF";
	///The color of an XML-attribute key
	public static String attributeKeyColor = "#FF0000";
	///The color of an XML-attribute value
	public static String attributeValueColor = "#9900FF";
	
	/**
	 * Checks if the passed name is the name of an allowed LF
	 * @param name
	 * @return
	 */
	public static boolean isAllowedLF(String name){
		for(String s : allowedLFS){
			if(s.equals(name)) return true;
		}
		return false;
	}
	
	/**
	 * sets the current Look And Feel to a LF with
	 * the provided name. If none is found nothing will
	 * happen
	 * @param name
	 */
	public static void setLF(String name){
		LookAndFeelInfo[] lfs = UIManager.getInstalledLookAndFeels();
		for(LookAndFeelInfo info : lfs){
			if(info.getName().equals(name)){
				try{
					if(!UIManager.getLookAndFeel().getName().equals(name)){
						UIManager.setLookAndFeel(info.getClassName());
						LF = name;
						Debug.print("[Preferences]: Set Look And Feel to: " + name + "\n");
					}
				}catch(Exception e){
					Debug.print("[Preferences]: failed to set LF\n");
				}
			}
		}
	}
	
	/**
	 * Loads the prefs.txt file that contains the preferences, 
	 * also parses them
	 */
	public static void loadPrefs(){
		File file = new File(FileRegistry.PREF_FILE);
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			String line = "";
			while((line = reader.readLine())!= null){
				builder.append(line);
			}
			preferences = builder.toString();
			parsePreferences();
			reader.close();
		}catch(Exception e){
			Debug.print("[Preferences]: Error reading preferences, defaults will be used.\n");
		}
		
		Debug.print("[Preferences]: Allowed LF's on this system:\n");
		for(int i = 0; i < installedLF.length; i++){
			Debug.print("[Preferences]: " + (i+1) + ": " + installedLF[i].getName() + "\n");
		}
	}
	
	/**
	 * parses the preferences
	 */
	private static void parsePreferences() {
		preferences = preferences.replaceAll("\n", "");
		String[] lines = preferences.split(";");
		String[] set;
		for(String line : lines){
			set = line.split("=");
			parseSets(set);
		}
		Preferences.setLF(Preferences.LF);
		Preferences.parseRecentFiles();
		Debug.setDebugMode(Preferences.debug);
		CodeSettingsEditor.reloadPrefs();
	}
	
	/**
	 * parses a line of the preference file
	 * @param set
	 */
	private static void parseSets(String[] set) {
		switch(set[0]){
		case "maximized":
			maximized = (set[1].equals("true")); break;
		case "sizeW":
			sizeW = Integer.parseInt(set[1]); break;
		case "sizeH":
			sizeH = Integer.parseInt(set[1]); break;
		case "lastOpenDir":
			lastOpenDir = set[1]; break;
		case "lang":
			language = set[1]; break;
		case "checkForUpdates":
			checkForUpdates = (set[1].equals("true")); break;
		case "recentFile1":
			recentFiles[0] = set[1];
			break;
		case "recentFile2":
			recentFiles[1] = set[1];
			break;
		case "recentFile3":
			recentFiles[2] = set[1];
			break;
		case "recentFile4":
			recentFiles[3] = set[1];
			break;
		case "recentFile5":
			recentFiles[4] = set[1];
			break;
		case "LF":
			LF = set[1];
			break;
		case "debug":
			debug = (set[1].equals("true"));
			break;	
		case "mainToolBarPos":
			mainToolBarPos = set[1];
			break;
		case "doBackup":
			backup = (set[1].equals("true")); 
			break;
		case "backupDelay":
			Main.BACKUP_DELAY = Integer.parseInt(set[1]);
			break;
		case "mainColor":
			mainColor = set[1];
			break;
		case "tagColor":
			tagColor = set[1];
			break;
		case "attributeValueColor":
			attributeValueColor = set[1];
			break;
		case "attributeKeyColor":
			attributeKeyColor = set[1];
			break;
		}
	}
	
	/**
	 * makes and saves the preference file contents to the hard disk
	 */
	public static void savePreferences(){
		int i = 0;
		for(File f : Main.recentFiles){
			if(f != null){
				recentFiles[i] = f.getAbsolutePath();
			}else{
				recentFiles[i] = "%";
			}
			i++;
			if(i > 4) break;
		}
		preferences = "maximized=" + maximized + ";\nsizeW=" + sizeW +
				";\nsizeH=" + sizeH + ";\nlang=" + language + ";\nlastOpenDir=" + lastOpenDir + 
				";\ncheckForUpdates=" + checkForUpdates + ";\nrecentFile1=" + recentFiles[0]+ ";\nrecentFile2=" +
				recentFiles[1]+ ";\nrecentFile3=" + recentFiles[2]+ ";\nrecentFile4=" + recentFiles[3]+ ";\nrecentFile5=" + recentFiles[4] + 
				";\nLF=" + LF + ";\ndebug=" + debug + ";\nmainToolBarPos=" + mainToolBarPos + ";\ndoBackup=" + backup + 
				";\nbackupDelay=" + Main.BACKUP_DELAY + ";\nmainColor=" + CodeSettingsEditor.getColorString(CodeSettingsEditor.mainColor, true) +
				";\ntagColor=" + CodeSettingsEditor.getColorString(CodeSettingsEditor.tagColor, true) +
				";\nattributeKeyColor=" + CodeSettingsEditor.getColorString(CodeSettingsEditor.attributeKeyColor, true) +
				";\nattributeValueColor=" + CodeSettingsEditor.getColorString(CodeSettingsEditor.attributeValueColor, true) + ";";
		
		//actually making the file
		File file = new File(FileRegistry.PREF_FILE);		
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String[] lines = preferences.split("\n");
			for(i = 0; i < lines.length; i++){
				writer.write(lines[i]);
				writer.newLine();
			}
			writer.close();
		}catch(Exception e){
			Debug.print("[Preferences]: Error writing preferences, preferences won't be changed.\n");
		}
	}
	
	/**
	 * checks if the provided version is newer compared to the version
	 * that is LanguageRegistry.APP_VERSION.
	 * @param newVer
	 * @return
	 */
	public static boolean isNewerVersion(String newVer){
		String thisVer = LanguageRegistry.APPLICATION_VERSION;
		Debug.print("[Preferences]: Got from web: " + newVer + "\n");
		thisVer = thisVer.replaceAll("v", "");
		newVer = newVer.replaceAll("v", "");
		thisVer = thisVer.replace('.', ';');
		newVer = newVer.replace('.', ';');
		Debug.print("Current version: Version " + thisVer.replace(';', '.') + " <> New version : Version " + newVer.replace(';', '.') + "\n");
		String[] cVer = thisVer.split(";");
		String[] iVer = newVer.split(";");
		if((iVer.length == cVer.length) &&(iVer.length == 3)){
			int cMajVer = Integer.parseInt(cVer[0]);
			int cMinVer = Integer.parseInt(cVer[1]);
			int cBldVer = Integer.parseInt(cVer[2]);
			int iMajVer = Integer.parseInt(iVer[0]);
			int iMinVer = Integer.parseInt(iVer[1]);
			int iBldVer = Integer.parseInt(iVer[2]);
			if(iMajVer > cMajVer){
				return true;
			}else if(iMajVer < cMajVer){
				return false;
			}else{
				if(iMinVer > cMinVer){
					return true;
				}else if(iMinVer < cMinVer){
					return false;
				}else{
					if(iBldVer > cBldVer){
						return true;
					}else{
						return false;
					}
				}
			}
		}else{
			Debug.print("[Preferences]: Error reading the update version format\n");
			return false;
		}
	}
	
	/**
	 * not working right now. Should parse the recentfiles from 
	 * the preferences into File instances
	 * 
	 */
	public static void parseRecentFiles(){
		for(String s : recentFiles){
			File f = new File(s);
			if(f.exists()){
				Main.recentFiles.add(f);
			}
		}
	}

}