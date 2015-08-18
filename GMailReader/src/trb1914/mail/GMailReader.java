package trb1914.mail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.Key;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import trb1914.talk.TalkBot;

/**
 * Main class and main applicationframe for the application
 * @author Mees Gelein
 */
public class GMailReader extends JFrame{

	public static final String TITLE = "GMailReader";
	public static ImageIcon APP_ICON;
	public static ImageIcon GEAR_ICON;
	public static ImageIcon REFRESH_ICON;
	public static ImageIcon HOURGLASS_ICON;
	public static ImageIcon NEW_ICON;
	public static ImageIcon ACCEPT_ICON;
	public static ImageIcon ERROR_ICON;

	public static Clip ALERT_CLIP;

	///SETTINGS
	public static boolean silent = true;
	public static boolean minimized = true;
	private final String SILENT = "silent";

	public static String pass = "";
	public static String user = "";
	public static int interval = 10000;
	private TalkBot talkBot = new TalkBot();
	private JButton checkButton;
	private JButton settingsButton;
	private StatusPanel statusLabel;
	public static GMailReader main;
	private int lastUnread = -1;
	private static JProgressBar progressBar;//static to allow it to initailize before other elements so it can use the Metal LF
	private TrayIcon trayIcon;

	private Timer checkTimer;
	private Timer animTimer;
	private int deltaTime = 0;
	private int totalTime = 0;
	/**
	 * Entry point
	 * @param args
	 */
	public static void main(String[] args) {
		//progressbar color
		UIManager.put("ProgressBar.background", Color.LIGHT_GRAY);
		UIManager.put("ProgressBar.foreground", Color.orange);
		//progressbar text color
		UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
		UIManager.put("ProgressBar.selectionForeground", Color.white);
		try {
			progressBar = new JProgressBar();
			progressBar.setString("Time untill next check");
			progressBar.setStringPainted(true);

			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new GMailReader();
			}
		});
	}

	/**
	 * Instantiating the actual program
	 */
	public GMailReader(){
		main = this;
		loadImages();
		readPreferences();
		createTrayIcon();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			/**
			 * Called when the window wants to close
			 */
			public void windowClosing(WindowEvent e){
				closeProgram();
			}

			/**
			 * Called when the window minimizes
			 */
			public void windowIconified(WindowEvent e){
				minimizeToTray();
			}

		});
		setTitle(TITLE);
		setIconImage(APP_ICON.getImage());
		makeGUI();
		statusLabel = new StatusPanel(user + " - Unchecked", ERROR_ICON);
		add(statusLabel, BorderLayout.NORTH);

		pack();
		setLocationRelativeTo(null);
		setResizable(false);

		if(!minimized) setVisible(true);
		else minimizeToTray();

		checkTimer = new Timer(interval, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				connect();
			}
		});
		checkTimer.setInitialDelay(1000);
		checkTimer.start();
		animTimer = new Timer(interval / 100, new ActionListener(){
			private int prevTime = 0;
			private float value = 0;
			public void actionPerformed(ActionEvent e){
				deltaTime = 0;
				deltaTime += (System.currentTimeMillis() - prevTime);
				totalTime += deltaTime;
				prevTime = (int) System.currentTimeMillis();
				value = (totalTime * 1.0f) / (interval * 1.0f) * 100;
				progressBar.setValue((int) value);
				if(value >= 100){
					totalTime = 0;
				}
				if(deltaTime > 5000){
					reloadSettings();
				}
			}
		});
		animTimer.setInitialDelay(1000);
		animTimer.start();

	}

	/**
	 * Builds the GUI
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());

		checkButton = new JButton("  Check Now");
		checkButton.setFont(Font.decode(checkButton.getFont().getName() + "-16"));
		checkButton.setIcon(APP_ICON);
		checkButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				reloadSettings();
			}
		});
		add(checkButton);

		settingsButton = new JButton(GEAR_ICON);
		settingsButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!SettingsFrame.open) new SettingsFrame();
			}
		});
		add(settingsButton, BorderLayout.EAST);
		add(progressBar, BorderLayout.SOUTH);
		progressBar.setFont(settingsButton.getFont());
	}

	/**
	 * Connects to the gmail account
	 */
	private void connect(){
		progressBar.setValue(100);
		new Thread(new Runnable(){
			public void run(){
				statusLabel.setStatus(user + " - Checking...", HOURGLASS_ICON);
				checkButton.setEnabled(false);
				Properties props = new Properties();
				props.setProperty("mail.host", "imap.gmail.com");
				props.setProperty("mail.port", "995");
				props.setProperty("mail.transport.protocol", "imaps");

				Session session = Session.getDefaultInstance(props, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(user, pass);
					}
				});

				try{
					Store store = session.getStore("imaps");
					store.connect();

					Folder inbox = store.getFolder("INBOX");
					inbox.open(Folder.READ_ONLY);
					Message[] messages = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));

					sayNewMessages(messages.length);
					if(messages.length > 0){
						MessageData[] messageData = new MessageData[messages.length];
						setTitle(TITLE + " (" + messages.length + ")");
						for(int i = 0; i < messages.length; i++){
							messageData[i] = new MessageData(messages[i]);
						}
						if(messages.length > lastUnread){
							new MessagePopup(messageData, inbox, store);

						}

					}else{
						inbox.close(true);
						store.close();
						setTitle(TITLE);	
					}
					lastUnread = messages.length;
					trayIcon.setToolTip(getTitle());
					totalTime = 0;
				}catch(MessagingException e){
					e.printStackTrace();
					statusLabel.setStatus(user + " - Error!", ERROR_ICON);
					if(!silent) talkBot.say("Unable to connect to email account.");
				}
			}
		}).start();
	}

	/**
	 * Says how many new messages you have
	 * @param amt
	 */
	private void sayNewMessages(int amt){
		checkButton.setEnabled(true);
		String msg = "You have " + amt + " unread message";
		if(amt > lastUnread){
			if(amt != 1) msg += "s";
			if(!silent) talkBot.say(msg);
			if(amt != 0){
				statusLabel.setStatus(user + " - New message", NEW_ICON);
			}else{
				statusLabel.setStatus(user + " - Nothing changed", ACCEPT_ICON);
			}
		}else{
			statusLabel.setStatus(user + " - Nothing changed", ACCEPT_ICON);
		}
		if(amt != 1) msg += "s";

	}

	/**
	 * Closes the program
	 */
	private void closeProgram(){
		savePreferences();
		setVisible(false);
		if(MessagePopup.current != null) MessagePopup.current.die();
		dispose();
		System.exit(0);
	}

	/**
	 * Creates the trayIcon
	 */
	private void createTrayIcon(){
		ActionListener openAction = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFromTray();
			}
		};

		//Creating the popupmenu
		PopupMenu menu = new PopupMenu();
		MenuItem openItem = new MenuItem("Open");
		openItem.addActionListener(openAction);
		menu.add(openItem);

		MenuItem checkItem = new MenuItem("Check now...");
		checkItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				reloadSettings();//not using connect because this function also resets all timers
			}
		});
		menu.add(checkItem);

		MenuItem mailItem = new MenuItem("Open Mail...");
		mailItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){

			}
		});

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeProgram();
			}
		});
		menu.addSeparator();
		menu.add(exitItem);

		//scaling the icon
		Image image = APP_ICON.getImage();
		int desiredWidth = new TrayIcon(image).getSize().width;
		trayIcon = new TrayIcon(image.getScaledInstance(desiredWidth, desiredWidth, Image.SCALE_SMOOTH), getTitle(), menu);
		trayIcon.addActionListener(openAction);
	}

	/**
	 * Minimizes the main window to the SystemTray if supported
	 */
	private void minimizeToTray(){
		if(SystemTray.isSupported()){
			SystemTray systemTray = SystemTray.getSystemTray();
			try{
				systemTray.add(trayIcon);
				setVisible(false);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens the tray back to normal
	 */
	private void openFromTray(){
		if(SystemTray.isSupported()){
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.remove(trayIcon);
			setVisible(true);
			setState(JFrame.NORMAL);
		}
	}

	/**
	 * Loads all external images
	 */
	private void loadImages(){
		APP_ICON = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/trb1914/mail/icon.png")));
		GEAR_ICON = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/trb1914/mail/gear.png")));
		ACCEPT_ICON = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/trb1914/mail/accept.png")));
		REFRESH_ICON = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/trb1914/mail/refresh.png")));
		HOURGLASS_ICON = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/trb1914/mail/hourglass.png")));
		NEW_ICON = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/trb1914/mail/new.png")));
		ERROR_ICON = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/trb1914/mail/error.png")));
	}

	/**
	 * Plays the alert sound
	 */
	public void playSound(){
		try{
			AudioInputStream aIn = AudioSystem.getAudioInputStream(getClass().getResource("/trb1914/mail/alert.wav"));
			DataLine.Info info = new DataLine.Info(Clip.class, aIn.getFormat());
			Clip c = (Clip) AudioSystem.getLine(info);
			c.open(aIn);
			c.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	/**
	 * Called when changes have been made to the settings
	 */
	public void reloadSettings(){
		connect();
		checkTimer.stop();
		animTimer.stop();
		totalTime = 0;
		checkTimer.setDelay(interval);
		animTimer.setDelay(interval / 100);
		checkTimer.setInitialDelay(interval);
		animTimer.setInitialDelay(0);
		checkTimer.start();
		animTimer.start();
		lastUnread = -1;
	}

	/**
	 * Reads the preferences
	 */
	private void readPreferences(){
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		silent = prefs.getBoolean(SILENT, true);
		interval = prefs.getInt("INTERVAL", 30000);
		minimized = prefs.getBoolean("MINIM", false);
		MessagePopup.setLocation(prefs.getInt("MSGLOC", MessagePopup.BOTTOM_RIGHT));

		Key aesKey = new SecretKeySpec("Bar12345Bar12345".getBytes(), "AES");
		try{
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
			pass = new String(cipher.doFinal(prefs.get("PASS", pass).getBytes()));
			user = new String(cipher.doFinal(prefs.get("USER", user).getBytes()));
		}catch(Exception e){
			System.out.println("AES-encryption is not available for some reason: ");
			e.printStackTrace();
		}
	}

	/**
	 * Saves the application preferences
	 */
	private void savePreferences(){
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		prefs.putBoolean(SILENT, silent);
		prefs.putBoolean("MINIM", minimized);
		prefs.putInt("INTERVAL", interval);
		prefs.putInt("MSGLOC", MessagePopup.currentLocation);

		Key aesKey = new SecretKeySpec("Bar12345Bar12345".getBytes(), "AES");
		try{
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			prefs.put("PASS", new String(cipher.doFinal(pass.getBytes())));
			prefs.put("USER", new String(cipher.doFinal(user.getBytes())));
		}catch(Exception e){
			System.out.println("AES-encryption is not available for some reason");
			e.printStackTrace();
		}
	}

	private class StatusPanel extends JPanel{

		private JLabel label = new JLabel();
		private JLabel icon = new JLabel();
		public StatusPanel(String msg, ImageIcon icon){
			label.setHorizontalAlignment(SwingConstants.CENTER);
			this.icon.setIcon(icon);
			label.setText(msg);

			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));


			setLayout(new BorderLayout());
			add(label);
			add(this.icon, BorderLayout.WEST);
		}
		public void setStatus(String msg, ImageIcon icon){
			this.icon.setIcon(icon);
			label.setText(msg);

		}
	}


}
