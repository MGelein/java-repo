package trb1914.helper;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.UIManager;

import trb1914.debug.Debug;

/**
 * Abstract class for some static System helper classes.
 * This allows simple stuff, like setting UI to system, getting
 * OS name, the default toolkit etc.
 * @author Mees Gelein
 */
public abstract class SystemHelper {

	/**Reference to the default toolkit.*/
	public static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
	/**The size of the screen, contained in a Dimension instance*/
	public static final Dimension SCREEN_SIZE = TOOLKIT.getScreenSize();
	/**The width of the screen in pixels*/
	public static final int SCREEN_WIDTH = SCREEN_SIZE.width;
	/**The height of the screen in pixels*/
	public static final int SCREEN_HEIGHT = SCREEN_SIZE.height;
	/**Amount of pixels per inch (dots per inch > DPI)*/
	public static final int SCREEN_DPI = TOOLKIT.getScreenResolution();
	/**Used to allow the JVM to talk with the system. E.g: launch files, default browsers etc.*/
	public static final Desktop DESKTOP = Desktop.getDesktop();

	/**
	 * Returns the System OS' name
	 * @return
	 */
	public static String getOSName(){
		return System.getProperty("os.name");
	}
	
	/**
	 * Beeps. Uses the system toolkit beep function :D
	 */
	public static void beep(){
		TOOLKIT.beep();
	}

	/**
	 * Returns the name of the user of this PC
	 * @return
	 */
	public static String getUserName(){
		return System.getProperty("user.name");
	}
	
	/**
	 * Returns the user home directory
	 * @return
	 */
	public static String getUserHomer(){
		return System.getProperty("user.home");
	}

	/**
	 * Tries to set the default System LF
	 */
	public static void setSystemDefaultLF(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			Debug.println("[trb1914.helper.SystemHelper]: Could not load the System LF.");
		}
	}

	/**
	 * Loads all the UI defaults in dutch
	 */
	public static void loadSystemNL(){
		UIManager.put("OptionPane.cancelButtonText", "Annuleren");
		UIManager.put("OptionPane.noButtonText", "Nee");
		UIManager.put("OptionPane.okButtonText", "OK");
		UIManager.put("OptionPane.yesButtonText", "Ja");

		UIManager.put("FileChooser.openDialogTitleText", "Openen");
		UIManager.put("FileChooser.saveDialogTitleText", "Opslaan");
		UIManager.put("FileChooser.openButtonText", "Openen");
		UIManager.put("FileChooser.saveButtonText", "Opslaan");
		UIManager.put("FileChooser.cancelButtonText", "Annuleren");
		UIManager.put("FileChooser.fileNameLabelText", "Bestandsnaam");
		UIManager.put("FileChooser.filesOfTypeLabelText", "Bestandstypes");
		UIManager.put("FileChooser.openButtonToolTipText", "Open Bestand");
		UIManager.put("FileChooser.cancelButtonToolTipText","Annuleren");
		UIManager.put("FileChooser.fileNameHeaderText","Bestandsnaam");
		UIManager.put("FileChooser.upFolderToolTipText", "Een niveau omhoog");
		UIManager.put("FileChooser.homeFolderToolTipText","Bureaublad");
		UIManager.put("FileChooser.newFolderToolTipText","Maak nieuwe map");
		UIManager.put("FileChooser.listViewButtonToolTipText","Lijst");
		UIManager.put("FileChooser.newFolderButtonText","Maak nieuwe map");
		UIManager.put("FileChooser.renameFileButtonText", "Naam wijzigen");
		UIManager.put("FileChooser.deleteFileButtonText", "Bestand verwijderen");
		UIManager.put("FileChooser.filterLabelText", "Bestandstype");
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "Details");
		UIManager.put("FileChooser.fileSizeHeaderText","Grootte");
		UIManager.put("FileChooser.fileDateHeaderText", "Laatst aangepast");
	}
}
