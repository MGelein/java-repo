package trb1914.alexml.data;

import java.awt.Font;
import java.awt.Image;
import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import trb1914.debug.Debug;
import trb1914.helper.FileHelper;
import trb1914.helper.ImageHelper;
/**
 * Contains all the paths to the required external files.
 * Also contains the templates the application uses to make
 * new files and segments
 * @author Mees Gelein
 */
public class FileRegistry{

	//the standard font used at some places
	private static int DEFAULT_FONT_SIZE = 16;
	private static Font NORMAL_FONT = Font.decode("Courier New-" + DEFAULT_FONT_SIZE);
	public static Font COURIER_FONT = Font.decode("Courier New-" + DEFAULT_FONT_SIZE);
	
	/**
	 * Returns the base font with a standard size of DEFAULT_FONT_SIZE
	 * @return
	 */
	public static Font getNormalFont(){
		return getNormalFont(DEFAULT_FONT_SIZE);
	}
	
	/**
	 * Derives a font from the base font
	 * @param size
	 * @return
	 */
	public static Font getNormalFont(float size){
		return NORMAL_FONT.deriveFont(size);
	}
	
	/**
	 * This will try to load the fonts specified in the locations below
	 */
	public static void loadFonts(){
		Font f = loadFont(SEGOEUI_LOCATION);
		if(f!= null) {
			Debug.println("[FileRegistry]: Font creation: " + FileRegistry.getNormalFont().getFontName());
			NORMAL_FONT = f;
		}
		f = loadFont(COURIER_LOCATION);
		if(f!= null) {
			Debug.println("[FileRegistry]: Font creation: " + f.getFontName());
			COURIER_FONT = f;
		}
	}
	
	/**
	 * Tries to load a font using an InputStream from the specified location
	 * @param location
	 * @return
	 */
	private static Font loadFont(String location){
		try{
			return Font.createFont(Font.TRUETYPE_FONT, FileHelper.getInputStream(location));
		}catch(Exception e){
			Debug.println("[FileRegistry]: Failed to load the font at " +  location);
		}
		return null;
	}
	
	//fonts embedded in the jar
	public static final String SEGOEUI_LOCATION = "/trb1914/alexml/font/SEGOEUI.TTF";
	public static final String COURIER_LOCATION = "/trb1914/alexml/font/COUR.TTF";
	
	//the icons used for JTree
	public static final String LEAF_ICON = "assets"+File.separatorChar + "icons" + File.separatorChar + "bullet_blue_16.png";
	public static final String MAP_ICON = "assets"+File.separatorChar + "icons" + File.separatorChar + "bullet_red_16.png";
	
	//all the png's used for JButtons
	public static final String OPEN_ICON = "assets"+File.separatorChar + "icons" + File.separatorChar + "folder_32.png";
	public static final String SAVE_ICON = "assets"+File.separatorChar + "icons"+File.separatorChar + "save_32.png";
	public static final String NEW_ICON = "assets"+File.separatorChar + "icons"+File.separatorChar + "document_32.png";
	public static final String DOC_SETTING_ICON = "assets"+File.separatorChar + "icons"+File.separatorChar + "clipboard_32.png";
	public static final String UP_ARROW = "assets" + File.separatorChar + "icons" + File.separatorChar + "up_32.png";
	public static final String DOWN_ARROW = "assets" + File.separatorChar + "icons" + File.separatorChar + "down_32.png";
	public static final String UP_ARROW_SMALL = "assets" + File.separatorChar + "icons" + File.separatorChar + "up_16.png";
	public static final String DOWN_ARROW_SMALL = "assets" + File.separatorChar + "icons" + File.separatorChar + "down_16.png";
	public static final String BLOCK_ICON = "assets" + File.separatorChar + "icons" + File.separatorChar + "block_16.png";
	public static final String DELETE_ICON_SMALL = "assets" + File.separatorChar + "icons" + File.separatorChar + "delete_16.png";
	public static final String DELETE_ICON = "assets" + File.separatorChar + "icons" + File.separatorChar + "delete_32.png";
	public static final String PLUS_ICON = "assets" + File.separatorChar + "icons" + File.separatorChar + "plus_32.png";
	public static final String PLUS_ICON_SMALL = "assets" + File.separatorChar + "icons" + File.separatorChar + "plus_16.png";
	public static final String PENCIL_ICON = "assets" + File.separatorChar + "icons" + File.separatorChar + "pencil_32.png";
	public static final String REFRESH_ICON = "assets" + File.separatorChar + "icons" + File.separatorChar + "refresh_32.png";
	public static final String TICK_ICON_SMALL = "assets" + File.separatorChar + "icons" + File.separatorChar + "tick_16.png";
	public static final String STOP_ICON_SMALL = "assets" + File.separatorChar + "icons" + File.separatorChar + "stop_16.png";
	public static final String LINK_ICON_SMALL = "assets" + File.separatorChar + "icons" + File.separatorChar + "link_16.png";
	
	///The Application icon
	public static Image APP_ICON = ImageHelper.getImage("/trb1914/alexml/img/icon.png");
	
	///The standard files used to edit
	public static final FileFilter XML_FILE_FILTER = new FileNameExtensionFilter("TEI Protagoras XML-Files", "xml");
	
	///Location of the backup file
	public static final String BACKUP_FILE = "assets"+File.separatorChar + "tempFile.xml";
	///Location for the debug to save the logging file
	public static final String LOG_FILE = "assets"+File.separatorChar + "prefs"+File.separatorChar + "logFile.txt";
	///Location of the file that has the XML tag templates
	public static final String TAGS_FILE = "assets" + File.separatorChar + "tags" + File.separatorChar + "tags.xml";
	///Location of the Preferences file
	public static final String PREF_FILE = "assets"+File.separatorChar + "prefs"+File.separatorChar + "prefs.txt";
	///URL to check if there is a new version
	public static final String VERSION_CHECK_URL = "alexml.heliohost.org/version.txt";
	///Location of the XSL used to generate the HTML
	public static final String TEI_XSL = "assets" + File.separatorChar + "xsl" + File.separatorChar + "TEIdisplayEdit.xsl";
	///URL for the user to download a new version
	public static final String UPDATE_URL = "http://download1591.mediafire.com/aqms6oqxdcwg/j5kgxtyf233p4sr/AleXML.zip";

	///The skeleton of an empty TEI file
	public static final String emptyFile = "<?xml version='1.0' encoding='UTF-8' standalone='no'?><TEI xmlns='http://www.tei-c.org/ns/1.0'>" +
			"<teiHeader></teiHeader><text n='Undefined'></text></TEI>";

	///The bare bones of a new empty segment
	public static final String emptySegment = "<seg n='Undefined'><keywords><term></term></keywords>" +
			"<translation><translationDesc><author></author></translationDesc></translation>" +
			"<displaySegment><lb n='1'/></displaySegment><lw></lw></seg>";

	///An empty header to add to the emptyFile
	public static final String emptyHeader = "<teiHeader><fileDesc><titleStmt><title></title><locus from='0' to='0'>0-0</locus>" +
			"<author></author><origDate></origDate></titleStmt><publicationStmt><availability>" +
			"This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License" +
			"</availability><docDate></docDate></publicationStmt></fileDesc><profileDesc><langUsage><language ident='grc' usage='40'>Ancient Greek</language>" +
			"<language ident='en-US' usage='50'>English</language><language ident='la' usage='10'>Latin</language></langUsage></profileDesc></teiHeader>";

}
