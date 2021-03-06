package trb1914.data;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import trb1914.util.SystemMessager;

/**
 * the Registry for static storage of Strings and file URLS
 * @author Mees Gelein
 */
public class Registry {

	public static String APP_VERSION = "v1.7.4";
	public static String APP_TITLE = "Inleverpunt";
	public static Dimension APP_SIZE = new Dimension();
	public static float MASTER_GAIN = 0.0f;
	public static boolean APP_MAXIMIZED = true;
	public static boolean SHOW_OSK = true;//if we are showing the OnScreenKeyboard
	public static float SEARCH_FONT_SIZE = 25; //not final because font sizes are set using the Preferences
	public static int LIST_FONT_SIZE = 16;
	public static float RENT_WINDOW_FONT_SIZE = 18;
	public static final String KLANT_CODE = "Registratienummer";
	public static final String START_DATE = "Verhuurdatum";
	public static final String END_DATE = "Inleverdatum";
	public static final String REVERSED = "Omgekeerd";
	
	public static ImageIcon APP_ICON;
	
	public static Font LIST_FONT;
	public static Font BOLD_LIST_FONT;
	
	public static ImageIcon CLIPBOARD_16;
	public static ImageIcon PLUS_WEEK;
	public static ImageIcon PLUS_DAY;
	public static ImageIcon MINUS_WEEK;
	public static ImageIcon MINUS_DAY;
	public static ImageIcon WARNING_YELLOW_16;
	public static ImageIcon WARNING_YELLOW_48;
	public static ImageIcon WARNING_RED_16;
	public static ImageIcon WARNING_RED_48;
	public static ImageIcon SAVE_16;
	public static ImageIcon PLUS_48;
	public static ImageIcon PLUS_16;
	public static ImageIcon DELETE_48;
	public static ImageIcon DELETE_32;
	public static ImageIcon TICK_48;
	public static ImageIcon TICK_32;
	public static ImageIcon DELETE_16;
	public static ImageIcon RIGHT_16;
	public static ImageIcon SEARCH_32;
	public static ImageIcon SEARCH_16;
	public static ImageIcon BULLET_BLUE;
	public static ImageIcon BULLET_GREEN;
	public static ImageIcon BULLET_RED;
	public static ImageIcon BULLET_ORANGE;
	public static ImageIcon TICK_16;
	public static ImageIcon HOURGLASS;
	public static ImageIcon REFRESH_ICON_16;
	public static ImageIcon STATISTICS_16;
	public static ImageIcon START_DATE_64;
	public static ImageIcon END_DATE_64;
	public static ImageIcon UP_32;
	public static ImageIcon DOWN_32;
	public static ImageIcon SORT_CODE_48;
	public static ImageIcon SERVER_16;
	public static ImageIcon OPEN_16;
	
	public static KeyStroke F1_KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
	public static KeyStroke F2_KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
	public static KeyStroke F3_KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
	public static KeyStroke F4_KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0);
	public static KeyStroke F5_KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
	public static KeyStroke F6_KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
	public static KeyStroke F7_KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);
	public static KeyStroke ESC_KeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	public static String F1_KeyName = "F1";
	public static String F2_KeyName = "F2";
	public static String F3_KeyName = "F3";
	public static String F4_KeyName = "F4";
	public static String F5_KeyName = "F5";
	public static String F6_KeyName = "F6";
	public static String F7_KeyName = "F7";
	public static String ESC_KeyName = "Esc";
	
	public static String BACKUP_LOCATION = "verhuurBackup.xml";
	public static String RENTALS_FILE_LOCATION = "files" + File.separatorChar + "rentals.xml";
	public static String PREF_FILE_LOCATION = "files" + File.separatorChar + "config.properties";
	
	public static SystemMessager sysMsg;
	
	public static String S_FILE_MENU = "Bestand";
	public static String S_EXTRA_MENU = "Extra";
	public static String S_USAGE_STATISTICS_MENU = "Gebruiksstatistieken...";
	public static String S_NEW_MENU = "Nieuw...";
	public static String S_QUIT_MENU = "Afsluiten";
	public static String S_CREATE_BACKUP_MENU = "Archiveren...";
	public static String S_RESTORE_BACKUP_MENU = "Archief openen...";
	public static String S_SERVER_CONNECTION = "Server";
	
	/**Thread safe flag to determine if all images have been loaded*/
	public static AtomicBoolean IMAGES_LOADED = new AtomicBoolean(false);
}
