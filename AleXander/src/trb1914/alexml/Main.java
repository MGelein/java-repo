package trb1914.alexml;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.gui.tabs.main.TabHolder;
import trb1914.alexml.gui.util.LoaderPane;
import trb1914.alexml.gui.util.SplashScreen;
import trb1914.alexml.interfaces.ICloseWindow;
import trb1914.alexml.xml.XMLParser;
import trb1914.helper.FileHelper;
import trb1914.helper.SystemHelper;
/**
 * The Main class of the Alexander XML Editor. Contains a static reference to the main window
 * and does basic FileIO as well as building the interface. 
 * @author Mees Gelein
 */
public class Main extends JFrame{

	/**The reference to the main window. Used by a lot of components. 
	 * Maybe null before application initialization*/
	public static Main main;
	/**The amount of milli's between a backup event. Best at 1min?*/
	public static int BACKUP_DELAY = 60000;
	/**A flag showing if it is allowed to open a new file. Set to false when we are opening a new file.*/
	public static boolean allowOpening = true;
	/**Contains the current dimension of the application. Only updated at startup!*/
	public static Dimension appSize;
	/**This File contains a reference to the currently loaded file. If null, then the file has not yet been saved.*/
	public static File currentFile;
	/** contains the 5 recent files as parsed from the Preferences. Preferences will write these
	 * 5 files back down in the preferences file on closing.*/
	public static ArrayList<File> recentFiles = new ArrayList<File>();
	{
		for(int i = 0; i < 5; i++) recentFiles.add(null);
	}
	/**A reference to the tabholder object*/
	private static TabHolder tabHolder;
	/**Reference to the timer that starts as soon as you either open a file or create a new one*/
	public static Timer backupTimer;
	/**List of all currently open frames.*/
	public static ArrayList<ICloseWindow> openFrames = new ArrayList<ICloseWindow>();

	/**
	 * creates the Main JFrame and makes the GUI.
	 */
	public Main() {
		//Icon and size
		Main.main = this;
		setIconImage(FileRegistry.APP_ICON);
		appSize = new Dimension(Preferences.sizeW, Preferences.sizeH);
		setMinimumSize(new Dimension(700,400));
		setSize(Main.appSize);

		//sets the title and closing behavior
		setTitle(LanguageRegistry.APPLICATION_TITLE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(isCurrentDocumentSaved()){
					closeWindow();
				}else{
					if(JOptionPane.showConfirmDialog(main, LanguageRegistry.UNSAVED_CLOSE_WARNING) == JOptionPane.OK_OPTION){
						closeWindow();
					}
				}
			}
		});

		//maximized state and position
		if(Preferences.maximized){
			setExtendedState(getExtendedState()|Frame.MAXIMIZED_BOTH);
		}else{
			Main.main.setLocationRelativeTo(null);
		}

		//create the rest of the GUI
		makeGUI();

		//creating the backup timer
		backupTimer = new Timer(Main.BACKUP_DELAY, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Debug.println("Creating backup...", this);
				createBackup();
			}
		});

		setVisible(true);
	}

	/**
	 * Makes the GUI
	 */
	private void makeGUI(){
		//creates the tab holder
		tabHolder = new TabHolder();
		add(tabHolder, BorderLayout.CENTER);
		add(TabHolder.toolBar, BorderLayout.NORTH);
		add(TabHolder.sysMsg, BorderLayout.SOUTH);
		setJMenuBar(TabHolder.toolBar.getJMenuBar());

		TabHolder.sysMsg.display(LanguageRegistry.CREDITS);
		TabHolder.sysMsg.addToList(LanguageRegistry.CREDITS);
	}

	/**
	 * closes all windows that are currently open saving Changes according to
	 * provided boolean.
	 */
	private static void closeAllWindows(boolean saveChanges){
		for(ICloseWindow w : Main.openFrames){
			w.closeWindow(saveChanges);
		}
		Debug.println("Closed " + Main.openFrames.size() + " open window(s)...", Main.main);
	}

	/**
	 * closes the MainFrame and saves the preferences
	 */
	public static void closeWindow() {
		TabHolder.sysMsg.display("Goodbye!");
		closeAllWindows(false);

		new Thread(new Runnable(){
			public void run(){
				File backupFile = new File(FileRegistry.BACKUP_FILE);
				if(backupFile.exists()){
					Debug.println("Backup found, trying to delete..", this);
					Debug.println("Backup succes: " +  backupFile.delete(), this);
				}else{
					Debug.println("No backup found, goodbye..", this);
				}
				Preferences.maximized = (Main.main.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
				if(!Preferences.maximized){
					Preferences.sizeW = Main.main.getSize().width;
					Preferences.sizeH = Main.main.getSize().height;
				}else{
					Preferences.sizeW = Preferences.defaultSizeW;
					Preferences.sizeH = Preferences.defaultSizeH;
				}
				Preferences.mainToolBarPos = TabHolder.segmentPanel.getToolBarOrientation();
				Preferences.savePreferences();
				if(Preferences.debug){Debug.createLogFile();}
				Main.main.dispose();				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				System.exit(0);
			}
		}).start();
	}

	/**
	 * FILE IO -------->>>>>>>>>>
	 */

	/**
	 * creates a new empty file
	 */
	public static void createNewFile(){
		Debug.println("Creating New File...", Main.main);
		boolean saveToContinue = isCurrentDocumentSaved();
		if(!saveToContinue){
			if(JOptionPane.showConfirmDialog(main, LanguageRegistry.OPENING_UNSAVED_WARNING) == JOptionPane.OK_OPTION){
				saveToContinue = true;
			}
		}
		if(saveToContinue){
			closeAllWindows(false);
			boolean succes = XMLParser.parseXML(FileRegistry.emptyFile);
			XMLParser.addEmptyHeader();
			XMLParser.addEmptySegment();
			TabHolder.setProgramState(succes);
			TabHolder.updateView();
			setCurrentFile(null);
		}

		if(Preferences.backup){
			if(!backupTimer.isRunning()){
				backupTimer.start();
			}else{
				backupTimer.restart();
			}
		}
	}

	/**
	 * opens a file choose dialog and opens and tries to parse the loaded file
	 */
	public static void openFile(){
		if(!SplashScreen.splashing){
			JFileChooser fileChooser;
			if(recentFiles.get(0) != null){
				fileChooser = new JFileChooser(recentFiles.get(0));
			}else{
				fileChooser = new JFileChooser();
			}
			fileChooser.addChoosableFileFilter(FileRegistry.XML_FILE_FILTER);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(FileRegistry.XML_FILE_FILTER);
			if(fileChooser.showOpenDialog(Main.main) == JFileChooser.APPROVE_OPTION){
				File file = fileChooser.getSelectedFile();
				openSilently(file);		
			}	
		}
	}

	/**
	 * adds a new file to the recent files list, or, if the file is already on the list,
	 * puts it at the top position.
	 * @param f
	 */
	private static void addFileToRecentList(File f){
		boolean found = false; int foundIndex = -1;
		for(File file : recentFiles){
			if(file == f){
				found = true;
				foundIndex = recentFiles.indexOf(file);
			}
		}
		if(found){
			recentFiles.remove(foundIndex);
		}
		recentFiles.set(4, recentFiles.get(3));
		recentFiles.set(3, recentFiles.get(2));
		recentFiles.set(2, recentFiles.get(1));
		recentFiles.set(1, recentFiles.get(0));
		recentFiles.set(0, f);
		TabHolder.toolBar.updateRecentItem();
	}

	/**
	 * opens the file without dialog
	 * @param f
	 */
	public static void openSilently(final File file){
		if(allowOpening){
			allowOpening = false;
			closeAllWindows(false);
			Debug.println("Opening File: " + file.getAbsolutePath(), Main.main);
			addFileToRecentList(file);
			boolean saveToContinue = isCurrentDocumentSaved();
			if(!saveToContinue){
				if(JOptionPane.showConfirmDialog(main, LanguageRegistry.OPENING_UNSAVED_WARNING) == JOptionPane.OK_OPTION){
					saveToContinue = true;
				}
			}
			if((!SplashScreen.splashing) && saveToContinue){
				final LoaderPane loader = new LoaderPane("Opening File");
				TabHolder.sysMsg.display("Opening File: " + file.getAbsolutePath());
				new Thread(new Runnable(){
					public void run(){
						loader.setPercentage(20);
						loader.setPercentage(30);
						try{
							loader.setPercentage(40);
							BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
							TabHolder.xmlPanel.getXMLTextArea().read(reader, null);
							reader.close();
							loader.setPercentage(50);
						}catch(Exception e){
							Debug.print("Tried to open but failed: ");
							Debug.println(e.getMessage(), this);
							JOptionPane.showMessageDialog(null, LanguageRegistry.PARSING_ERROR);
						}
						final boolean succes =  XMLParser.parseXML(TabHolder.xmlPanel.getXMLTextArea().getText());
						loader.setPercentage(60);
						if(succes) setCurrentFile(file);
						TabHolder.xmlPanel.getXMLTextArea().setText(XMLParser.getString());
						loader.setPercentage(80);
						SwingUtilities.invokeLater(new Runnable(){
							public void run(){
								TabHolder.setProgramState(succes);
								TabHolder.updateView();
							}
						});
						loader.setPercentage(100);
						TabHolder.sysMsg.display("Opened File: " + file.getAbsolutePath());

						if(Preferences.backup){
							if(!backupTimer.isRunning()){
								backupTimer.start();
							}else{
								backupTimer.restart();
							}
						}
						allowOpening = true;
					}
				}).start();
			}
		}
	}

	/**
	 * checks if the current document has any changes compared to the one
	 * saved on the hard drive. If the document has not been saved yet this
	 * function will return false.
	 */
	private static boolean isCurrentDocumentSaved(){
		if(currentFile != null){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(currentFile), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				String aux = "";

				while ((aux = reader.readLine()) != null) {
					builder.append(aux);
				}

				String text = builder.toString();
				reader.close();

				int startIndex = text.indexOf("<");
				int endIndex = text.indexOf(">");
				text = text.replaceAll(text.substring(startIndex, endIndex + 1), "");

				String s = TabHolder.xmlPanel.getXMLTextArea().getText();
				s = s.replaceAll("\n", "");
				s = s.replaceAll("\r", "");
				return (s.equals(text));
			} catch(Exception e){

			}
			return true;
		}else{
			if(XMLParser.xmlDocument == null){
				return true;
			}
			return false;
		}
	}

	/**
	 * Creates a backup when necessary
	 */
	private void createBackup(){
		Debug.println("Creating Backup...", Main.main);
		XMLParser.setLastSavedDate();
		final String s = TabHolder.xmlPanel.getXMLTextArea().getText();
		if(TabHolder.programState){
			new Thread(new Runnable(){
				public void run(){
					String sLocal = LanguageRegistry.CREATED_DOCUMENT_COMMENT + s;
					try{
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(FileRegistry.BACKUP_FILE)),"UTF-8"));
						writer.write(sLocal);
						writer.close();
					}catch(Exception e){
						Debug.print("Tried to create backup but failed :" );
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	/**
	 * saves the currentFile silently
	 * @param s		provided String to save
	 */
	public static void saveSilently(){
		Debug.println("Saving File: " + currentFile.getAbsolutePath(), Main.main);
		XMLParser.setLastSavedDate();
		if(TabHolder.programState){
			final String s = TabHolder.xmlPanel.getXMLTextArea().getText();
			final LoaderPane loader = new LoaderPane("Saving File");
			TabHolder.sysMsg.display("Saving File: " + currentFile.getAbsolutePath());
			closeAllWindows(false);
			new Thread(new Runnable(){
				public void run(){
					String sLocal = LanguageRegistry.CREATED_DOCUMENT_COMMENT + s;
					try{
						loader.setPercentage(10);
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentFile),"UTF-8"));
						loader.setPercentage(30);
						writer.write(sLocal);
						loader.setPercentage(50);
						writer.close();
						loader.setPercentage(70);
					}catch(Exception e){
						Debug.print("Tried to save but failed: " );
						Debug.println(e.getMessage(), this);
						JOptionPane.showMessageDialog(null, LanguageRegistry.SAVING_ERROR);
					}
					loader.setPercentage(100);
					TabHolder.sysMsg.display("Saved File: " + currentFile.getAbsolutePath());
				}
			}).start();
		}
	}

	/**
	 * used to set the current File the correct way
	 * @param f
	 */
	private static void setCurrentFile(File f){
		currentFile = f;
		String fName = "unnamed.xml";
		if(f != null){
			fName = f.getName();
		}
		main.setTitle(LanguageRegistry.APPLICATION_TITLE + "- " + fName);
	}

	/**
	 * saves the provided string with a Dialog. Automatically provides the document 
	 * with a .xml extension
	 * @param s		the String to save
	 */
	public static void saveToFile(){
		JFileChooser fileChooser;
		//if we already have a file open, open the dialog there
		if(currentFile != null){
			fileChooser = new JFileChooser(currentFile);
			fileChooser.setSelectedFile(currentFile);
		}else if(recentFiles.get(0) != null){
			fileChooser = new JFileChooser(recentFiles.get(0));
		}else{
			fileChooser = new JFileChooser();
		}

		//sets the File filter, the files to show 
		fileChooser.addChoosableFileFilter(FileRegistry.XML_FILE_FILTER);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(FileRegistry.XML_FILE_FILTER);
		int returnValue = fileChooser.showSaveDialog(Main.main);
		if(returnValue == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			String fileName = FileHelper.getFileName(file) + ".xml";
			File fileToSave = new File(file.getParentFile().getAbsolutePath() + File.separatorChar + fileName);
			setCurrentFile(fileToSave);
			saveSilently();
		}else if(returnValue == JFileChooser.CANCEL_OPTION){
			Debug.println("FileChooser cancelled", Main.main);
		}
	}

	/**
	 * Start of program. Entry point of all code. Creates the Main JFrame
	 * from here on everything is initialized by Main.
	 * This also tries to set the System look and feel
	 * @param args
	 */
	public static void main(String[] args) {
		//enable font anti aliasing
		System.setProperty("awt.useSystemAAFontSettings","lcd");
		System.setProperty("swing.aatext", "true");

		Debug.print("[Init]: " + LanguageRegistry.APPLICATION_NAME + " " + LanguageRegistry.APPLICATION_VERSION + "\n");
		Debug.print("[Init]: Running on: " + SystemHelper.getOSName() + "\n");
		Debug.print("[Init]: Username: " + SystemHelper.getUserName() + "\n");
		Debug.print("[Init]: Screensize: " + SystemHelper.SCREEN_SIZE.toString() + "\n");
		Debug.print("[Init]: DPI " + SystemHelper.SCREEN_DPI + "\n");

		//set the OS default look and feel instead of the ugly Java crap
		SystemHelper.setSystemDefaultLF();
		Preferences.LF = UIManager.getLookAndFeel().getName();
		
		//loads the Preferences and displays the used language
		Preferences.loadPrefs();
		appSize = new Dimension(Preferences.sizeW, Preferences.sizeH);
		Debug.print("[Init]: Appsize: " + appSize.toString() + "\n");
		LanguageRegistry.changeLanguage(Preferences.language);
		Debug.print("[Init]: Used language: " + Preferences.language + "\n");
		FileRegistry.loadFonts();

		//runs the Main Constructor
		Main.main = new Main(); 

		//show a splash screen now that everything is loaded and start off the program with disabled controls
		new SplashScreen();
		TabHolder.setProgramState(false);

		checkForUpdates();

		//check if backup exists
		File backupFile = new File(FileRegistry.BACKUP_FILE);
		if(backupFile.exists()){
			if(JOptionPane.showConfirmDialog(null, LanguageRegistry.BACKUP_FOUND_WARNING) == JOptionPane.OK_OPTION){
				openSilently(backupFile);
			}
		}
	}

	/**
	 * Checks for updates if necessary
	 */
	private static void checkForUpdates(){
		//check for updates
		if(Preferences.checkForUpdates){
			LoaderPane checkerLoader = new LoaderPane("Checking for Updates");
			TabHolder.sysMsg.display("Checking for Updates...");
			checkerLoader.setIndeterminate(true);
			Debug.print("[Init]: Checking for updates...\n");
			try{
				URL url = new URL(FileRegistry.VERSION_CHECK_URL);
				URLConnection connection = url.openConnection();
				connection.setReadTimeout(3000);
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String newestVersion = in.readLine();
				if(Preferences.isNewerVersion(newestVersion)){
					Debug.print("[Init]: Update neccessary!\n");
					TabHolder.sysMsg.display("A new version has been found!");
					JOptionPane.showMessageDialog(null, LanguageRegistry.UPDATE_REQUIRED_MESSAGE);
					if(Desktop.isDesktopSupported()){
						//TODO: open browser to download page to update. Currently we don't have a download page :S
						try{
							URI uri = new URI("http://www.google.com");
							Desktop.getDesktop().browse(uri);
						}catch(Exception e){Debug.print("[Init]: opening browser failed\n");}

					}
				}else{
				}
			}catch(Exception e){
				Debug.print("[Init]: Error with update-check\n");
				checkerLoader.setIndeterminate(false);
				checkerLoader.setPercentage(99);
				checkerLoader.setTitle("Error with update-check");
				TabHolder.sysMsg.display("Error with update-check!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			checkerLoader.setPercentage(100);
		}

	}

}