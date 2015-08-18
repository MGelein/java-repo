package trb1914;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;

import trb1914.data.Registry;
import trb1914.data.Rental;
import trb1914.debug.Debug;
import trb1914.filter.DateFormatter;
import trb1914.filter.UpperCaseFilter;
import trb1914.gui.OnScreenKeyboard;
import trb1914.helper.AudioHelper;
import trb1914.helper.ImageHelper;
import trb1914.helper.SystemHelper;
import trb1914.net.RentalServer;
import trb1914.net.SocketHelper;
import trb1914.preferences.Preferences;
import trb1914.util.LoaderPane;
import trb1914.util.RentalRenderer;
import trb1914.util.SplashScreen;
import trb1914.util.SystemMessager;
/**
 * the Main class of this application
 * @author Mees Gelein
 * 
 * TODO: multiple savefiles, calendarlike interface
 */
public class Main extends JFrame{

	/**Reference to the main application window*/
	public static Main mainWindow;

	/**Is this instance also the server?*/
	public static boolean isServer = false;

	/**Is the list sorted by start date?*/
	public static boolean SORT_BY_START_DATE = false;
	/**Is the list sorted by end date?*/
	public static boolean SORT_BY_END_DATE = true;
	/**Is the list sorted by code?*/
	public static boolean SORT_BY_CODE = false;
	/**Is the list currently reversed?*/
	public static boolean REVERSED = false;
	/**Reverses the list if code is the sorting order*/
	public static boolean CODE_REVERSED = false;
	/**Reverses the list if startDate is the sorting order*/
	public static boolean START_REVERSED = false;
	/**Reverses the list if endDate is the sorting order*/
	public static boolean END_REVERSED = false;

	/**The last used registration number (code) of the last entered rental*/
	public static int lastUsedNumber = 0;

	/**Flag indicating if late entries are shown*/
	public static boolean SHOW_LATE = true;
	/**Flag indicating if paid entries are shown*/
	public static boolean SHOW_PAID = false;
	/**Flag indicating if entries that are due today are shown*/
	public static boolean SHOW_DUE_TODAY = true;
	/**Flag indicating if all other entries that don't fit any of the other categories are shown*/
	public static boolean SHOW_NORMAL = true;

	/**Is the splash screen shown on startup?*/
	public static boolean SPLASHING = true;
	/**Are we drawing the list display using fancy background and font colors?*/
	public static boolean FANCY_COLORS = true;
	/**The last known location of the server. Will be checked first*/
	public static String LAST_SERVER_ADDRESS = "127.0.0.0";


	/**The main list that displays the search results*/
	public JList<Rental> mainList;
	/**The input searchfield. Put your query here*/
	public JTextField searchField;
	/**All the rentals that have been parsed from the rentals file*/
	public ArrayList<Rental> allRentals = new ArrayList<Rental>();
	/**All rentals that match the filter flags (SHOW_LATE, SHOW_PAID etc.)*/
	private ArrayList<Rental> filteredRentals = new ArrayList<Rental>();
	/**All rentals from the filtered rentals that match the query in the search field*/
	private ArrayList<Rental> foundRentals = new ArrayList<Rental>();

	/**Holds the deleteAction and can be registered to the correct JMenuItem and Delete Keypress*/
	private ActionListener deleteActionListener;

	/**Amount of objects that are due today*/
	private int dueTodayObjectCount = 0;
	/**Amount of rentals that have not yet been paid for*/
	private int unpaidRentalCount = 0;
	/**Amount of rentals that are late*/
	private int lateRentalCount = 0;
	/**Amount of results that are returned using the query and filters*/
	private int searchResultCount = 0;

	/**The Socket adress (String) used for communcation with the server*/
	private String serverAddress;

	/**The panel to put the RentViewer and RentWindow in*/
	private JPanel windowPanel = new JPanel(new BorderLayout());
	/**Reference to the plusButton, because we constantly remove and re-add it*/
	private JButton plusButton;

	/**List of all components for disabling/enabling*/
	private Vector<JComponent> components = new Vector<JComponent>();

	/**
	 * main entry point
	 * @param args		is useless right now
	 */
	public static void main(String[] args) {
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");

		//loads the pref file
		Preferences.load(Registry.PREF_FILE_LOCATION);
		loadPrefs();
		
		//get all images from the .jar
		getImages();

		if(Main.SPLASHING) new SplashScreen();
		if(Main.isServer) new RentalServer();

		SystemHelper.loadSystemNL();

		SystemHelper.setSystemDefaultLF();//sets the OS dependant LF

		SwingUtilities.invokeLater(new Runnable(){//starts the program on the right (EDT) thread.
			public void run(){
				//wait for all images to load
				while(!Registry.IMAGES_LOADED.get()){
					try{
						Thread.sleep(50);
					}catch(Exception e){
						Debug.println("Couldn't sleep to wait for all images to load");
					}
				}
				
				//startup main window
				mainWindow = new Main();
				mainWindow.setTitle(Registry.APP_TITLE);
				mainWindow.setIconImage(Registry.APP_ICON.getImage());
				mainWindow.setPreferredSize(SystemHelper.SCREEN_SIZE);
				if(Registry.APP_SIZE.width == 0){
					mainWindow.setSize(mainWindow.getPreferredSize());
				}else{
					mainWindow.setPreferredSize(Registry.APP_SIZE);
					mainWindow.setSize(mainWindow.getPreferredSize());
				}
				if(Registry.APP_MAXIMIZED) mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
				mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			}
		});
	}

	/**
	 * initializes the Main window
	 */
	public Main(){
		//listens for the window closing and exits the main application
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				closeWindow();
			}
		});

		//builds the GUI and after that makes the window visible.
		makeGUI();
		setVisible(true);

		//starts the network communication
		lookForServer(false);
		openListenPort();
	}

	/**
	 * This method will try to find the server on the network using its own IP.
	 */
	private void lookForServer(final boolean deepScan){
		final LoaderPane loaderPane = new LoaderPane("De server wordt gezocht...");
		new Thread(new Runnable(){
			public void run(){
				try{
					loaderPane.setIndeterminate(true);
					String localAddress = InetAddress.getLocalHost().getHostAddress();
					String subnet = localAddress.substring(0, localAddress.lastIndexOf(".")) + ".";
					String address = "";
					//check last know address first
					boolean found = SocketHelper.checkPort(LAST_SERVER_ADDRESS, RentalServer.DETECT_PORT, RentalServer.CHECK_TIMEOUT);
					
					if(!found){
						for(int i = 0; i < 254; i++){
							address = subnet + i;
							loaderPane.setString("De server wordt gezocht: " + address);
							if(SocketHelper.checkPort(address, RentalServer.DETECT_PORT, RentalServer.CHECK_TIMEOUT)){
								found = true;
								break;
							}
						}
					}else{
						address = LAST_SERVER_ADDRESS;
						found = true;
					}
					if(!found){
						loaderPane.setPercentage(101);
						showNoServerMessage();
					}else{
						Debug.println("Found server at: " + address, this);
						loaderPane.setIndeterminate(false);
						serverAddress = address;
						LAST_SERVER_ADDRESS = serverAddress;
						downloadRentals(address, loaderPane);
					}
				}catch(Exception e){
					Debug.println("An error occured during server lookup. The program will not function correctly.", this);
					e.printStackTrace();
					loaderPane.setPercentage(101);
					showNoServerMessage();
				}				
			}
		}).start();
	}

	/**
	 * This socket listens for any server broadcasts
	 */
	private void openListenPort(){
		new Thread(new Runnable(){
			public void run(){
				try{
					processInput(SocketHelper.receiveOn(RentalServer.BROAD_PORT));
					openListenPort();
				}catch(Exception e){
					Debug.println("Couldn't setup listening port. Program will not function!", this);
					e.printStackTrace();
					openListenPort();//try opening the listen port again
				}
			}
		}).start();
	}

	/**
	 * Plays open.wav. Use this method to prevent mistyping.
	 */
	public static void playOpenSound(){
		AudioHelper.playSound("/trb1914/audio/open.wav", Registry.MASTER_GAIN);
	}

	/**
	 * Plays close.wav. Use this method to prevent mistyping.
	 */
	public static void playCloseSound(){
		AudioHelper.playSound("/trb1914/audio/close.wav", Registry.MASTER_GAIN);
	}

	/**
	 * Plays ping.wav. Use this method to prevent mistyping.
	 */
	public static void playPingSound(){
		AudioHelper.playSound("/trb1914/audio/ping.wav", Registry.MASTER_GAIN);
	}

	/**
	 * Shows a message that says the server is unavailable
	 */
	private void showNoServerMessage(){
		Object[] choices = {"Opnieuw proberen", "Handmatig", "Afsluiten"};
		Object defaultChoice = choices[0];

		int reply = JOptionPane.showOptionDialog(this,
				"De database kan niet worden bereikt. De server staat uit of er zijn andere verbindingsproblemen. "
						+ "\nIs dit apparaat met het juiste netwerk verbonden?\nStaat de database server aan?",
						"Kies een optie",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						choices,
						defaultChoice);
		if(reply == JOptionPane.YES_OPTION){
			lookForServer(false);
		}else if(reply == JOptionPane.NO_OPTION){
			String address = JOptionPane.showInputDialog(this, "IP-adres: ");
			LoaderPane l = new LoaderPane("IP adress wordt bekeken...");
			l.setIndeterminate(true);
			boolean success = SocketHelper.checkPort(address, RentalServer.DETECT_PORT, SocketHelper.MESSAGE_TIMEOUT);
			l.setPercentage(101);
			if(success){
				downloadRentals(address, new LoaderPane("De database wordt gedownload..."));
				serverAddress = address;
				LAST_SERVER_ADDRESS = serverAddress;
			}else{
				showNoServerMessage();
			}
		}else{
			closeWindow();
		}
	}

	/**
	 * Enables/Disables the complete GUI. After disabling / enabling, the searchfield always requests focus
	 */
	private void enableGUI(boolean bd){
		for(JComponent c : components) c.setEnabled(bd);
		searchField.requestFocusInWindow();
	}

	/**
	 * Downloads the rentals list from the provided IP address (the server)
	 * @param ipAddress
	 */
	private void downloadRentals(String ipAddress, LoaderPane loader){
		loader.setPercentage(10);
		loader.setTitle("Database downloaden...");
		loader.setString("Database downloaden...");


		try{
			Debug.println("Attempting to download the rentals list", this);
			InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, RentalServer.DOWN_PORT);
			Socket downloadSocket = new Socket();
			loader.setPercentage(30);
			downloadSocket.connect(socketAddress, 10000);//timeout after 10 secs
			Debug.println("Received connection from server", this);
			loader.setPercentage(50);
			BufferedReader in = new BufferedReader(new InputStreamReader(downloadSocket.getInputStream()));
			while(!in.ready()){}//wait for the in stream to be ready to be read
			loader.setPercentage(70);
			String line = in.readLine();
			loader.setPercentage(80);
			parseRentalsList(line);
			enableGUI(true);
			loader.setPercentage(90);

			in.close();
			downloadSocket.close();
			loader.setPercentage(101);
		}catch(Exception e){
			loader.setPercentage(101);
			Debug.println("Couldn't download rentals list due to: " + e.getClass().getName(), this);
			e.printStackTrace();
		}
	}

	/**
	 * Parses the input received from the server
	 * @param s
	 */
	private void processInput(String s){
		Debug.println("Received input from server: " + s, this);

		//tests if the input is a comment.
		if(s.startsWith("//")) return;

		//is the input a delete message?
		if(s.startsWith("@del=")){
			removeRental(s);
			sortList();
			return;
		}

		//parses a rental from the provided input
		Rental r = new Rental();
		r.loadFromDef(s);
		int foundIndex = -1;
		for(Rental rental : allRentals){
			if(rental.getCode().equals(r.getCode())){//we have found a match
				foundIndex = allRentals.indexOf(rental);
				break;
			}
		}

		//if the rental could be found we need to replace it in the allRentals
		if(foundIndex > -1){
			if(r.isValid()) allRentals.set(foundIndex, r);
		}else{
			//if no match was found this is a new rental and must be added to the system
			Debug.println("No match was found. Add new rental: " + s, this);
			allRentals.add(r);
		}

		//sortlist after adding a new Rental
		sortList();
	}

	/**
	 * Processes the remove command
	 * @param command
	 */
	private void removeRental(String command){
		Rental toRemove = null;
		for(Rental rental : allRentals){
			if(rental.getCode().equals(command.split("=")[1])){//we have found a match
				toRemove = rental;
				break;
			}
		}
		if(toRemove != null){
			Debug.println("Succesfully removed rental (" + command + ")", this);
			allRentals.remove(toRemove);
		}else{
			Debug.println("Couldn't find the Rental that was supposed to be removed: " + command, this);
		}
	}

	/**
	 * Parses the list of coded rentals received from the server
	 * @param list
	 */
	private void parseRentalsList(String list){
		allRentals = new ArrayList<Rental>();
		String[] defs = list.split(Rental.OBJECT_DELIM_DEF);

		//parses the list each def per def.
		for(String def : defs){
			if(def.length() > 0){
				Rental r = new Rental();
				r.loadFromDef(def);
				if(r.isValid()) allRentals.add(r);
			}
		}
		sortList();
	}

	/**
	 * Broadcasts the specified string to the server
	 * @param s
	 */
	public void broadCast(final String s){
		new Thread(new Runnable(){
			public void run(){
				try{
					Debug.println("Broadcast message: " + s, this);
					SocketHelper.sendTo(serverAddress, RentalServer.COMM_PORT, s);
				}catch(Exception e){
					Debug.println("Couldn't reach Server...", this);
					enableGUI(false);
					allRentals = new ArrayList<Rental>();
					sortList();
					showNoServerMessage();
				}
			}
		}).start();
	}

	/**
	 * builds the GUI
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());

		//one action for two kinds of deleting
		deleteActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					if(mainList.getSelectedValue() != null){//check if not null.
						Rental toDelete = mainList.getSelectedValue();
						Main.playOpenSound();
						int reply = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u dit registratienummer definitief wilt verwijderen?\nDit kan niet meer ongedaan gemaakt worden!");
						if(reply == JOptionPane.OK_OPTION){
							broadCast("@del=" + toDelete.getCode());
						}
						Main.playCloseSound();
					}
				}catch(Exception exception){
					Debug.println("Could not remove selected value. Maybe a null error?", this);
					exception.printStackTrace();
				}
			}
		};

		//adds the System Messager
		JPanel contentPanel = new JPanel(new BorderLayout());
		add(contentPanel, BorderLayout.SOUTH);
		Registry.sysMsg = new SystemMessager("Welkom bij " + Registry.APP_TITLE);
		contentPanel.add(Registry.sysMsg);

		//the main List 
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(2,8,0,8));
		//the keyboard and plusButton
		plusButton = new JButton(Registry.PLUS_48);
		plusButton.setPreferredSize(new Dimension(OnScreenKeyboard.KEY_SIZE.width * 2, OnScreenKeyboard.KEY_SIZE.height * 4));//2 by 4 size
		centerPanel.add(windowPanel, BorderLayout.SOUTH);
		OnScreenKeyboard kb = new OnScreenKeyboard();
		kb.addButton(plusButton);
		windowPanel.add(kb);

		//The plus button
		components.add(plusButton);
		plusButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openRentWindow();
			}
		});
		plusButton.getActionMap().put("NewRental", new AbstractAction("NewRental"){
			public void actionPerformed(ActionEvent e){
				openRentWindow();
			}
		});
		plusButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F1_KeyStroke, "NewRental");
		plusButton.setHorizontalTextPosition(SwingConstants.CENTER);
		plusButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		plusButton.setText("Nieuw (" + Registry.F1_KeyName + ")");

		mainList = new JList<Rental>();
		components.add(mainList);

		final JScrollPane scrollPane = new JScrollPane(mainList);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		components.add(scrollPane);

		mainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mainList.setComponentPopupMenu(createPopupMenu());
		mainList.setCellRenderer(new RentalRenderer());
		mainList.setListData(getRentalsArray(allRentals));
		Registry.LIST_FONT = mainList.getFont().deriveFont(Registry.LIST_FONT_SIZE * 1.0f);
		Registry.BOLD_LIST_FONT = new Font(Registry.LIST_FONT.getName(), Font.BOLD, Registry.LIST_FONT.getSize());
		mainList.setBorder(BorderFactory.createLineBorder(Color.gray));
		mainList.addMouseListener(new MouseAdapter(){
			//if the mouse button has been pressed
			public void mousePressed(MouseEvent e){
				if(e.getButton() == 3){ //RMB (if we press it we select that index)
					mainList.setSelectedIndex(mainList.locationToIndex(e.getPoint()));
				}
			}
			//when we have a click
			public void mouseClicked(MouseEvent e){
				if(e.getPoint().x > mainList.getSize().width - 35){
					openRentViewer(mainList.getSelectedValue());
				}
				if(e.getClickCount() > 1){
					openRentViewer(mainList.getSelectedValue());
				}
			}
		});

		mainList.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					openRentViewer(mainList.getSelectedValue());
				}else if(e.getKeyCode() == KeyEvent.VK_DELETE){
					if(mainList.getSelectedValue() != null){ //if there is a list-value selected.
						deleteActionListener.actionPerformed(null);
					}
				}
			}
		});

		JPanel centerPanelHolder = new JPanel(new BorderLayout());
		add(centerPanelHolder, BorderLayout.CENTER);
		centerPanelHolder.add(centerPanel, BorderLayout.CENTER);
		JPanel headerPanel = new JPanel(new GridLayout(1, 2));
		centerPanelHolder.add(headerPanel, BorderLayout.NORTH);
		JPanel dateHeaderPanel = new JPanel(new GridLayout(1, 3));
		headerPanel.add(dateHeaderPanel);
		dateHeaderPanel.add(new JLabel(Registry.KLANT_CODE));
		dateHeaderPanel.add(new JLabel(Registry.START_DATE));
		dateHeaderPanel.add(new JLabel(Registry.END_DATE));
		dateHeaderPanel.setBorder(BorderFactory.createEmptyBorder(2, 30, 0, 0));
		headerPanel.add(new JLabel("Fietsen"));
		headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

		//makes the menu bar (top of the window)
		makeMenuBar();

		//the searchfield
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setPreferredSize(new Dimension(0, 35));
		searchField = new JTextField();
		components.add(searchField);
		searchField.setHorizontalAlignment(SwingConstants.CENTER);
		((AbstractDocument) searchField.getDocument()).setDocumentFilter(new UpperCaseFilter());
		//update the list whenever something changes in the search query field
		searchField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {updateList();}
			public void insertUpdate(DocumentEvent e) {updateList();}
			public void removeUpdate(DocumentEvent e) {updateList();}
		});
		//clear field when regaining focus i.e: after a search query
		searchField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				searchField.setText("");
				super.focusGained(e);
			}
		});
		//the attaching of the keys to the action.
		searchField.getActionMap().put("SearchFocus", new AbstractAction("SearchFocus"){
			public void actionPerformed(ActionEvent e){
				searchField.setText("");
				searchField.requestFocusInWindow();
			}
		});
		//clear the field when double clicked
		searchField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1 || e.getButton() == 3){//more than a single click or rmb click (long click on touch devices)
					searchField.setText("");
				}
			}
		});
		searchField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F2_KeyStroke, "SearchFocus");

		//adds the searchfield
		northPanel.add(searchField);
		JLabel searchLabel = new JLabel(Registry.SEARCH_16);
		searchLabel.setText("(" + Registry.F2_KeyName + ")");
		searchLabel.setBorder(BorderFactory.createEmptyBorder(2,2,2,6));
		northPanel.add(searchLabel, BorderLayout.WEST);
		components.add(searchLabel);

		northPanel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
		searchField.setFont(searchField.getFont().deriveFont(Registry.SEARCH_FONT_SIZE));
		add(northPanel, BorderLayout.NORTH);

		//east panel, the panel that holds the order settings and the info panel.
		JPanel eastPanel = new JPanel(new BorderLayout());
		add(eastPanel, BorderLayout.EAST);
		eastPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 6));
		JPanel searchSettingPanel = new JPanel(new GridLayout(3, 1));
		eastPanel.add(searchSettingPanel, BorderLayout.NORTH);

		//searchSettings and orderpanel
		JPanel orderPanel = new JPanel(new BorderLayout());
		orderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Volgorde"));
		JPanel listPanel = new JPanel(new BorderLayout());
		listPanel.add(orderPanel);


		final JToggleButton reverseButton = new JToggleButton(Registry.REVERSED + " (" + Registry.F3_KeyName + ")");
		reverseButton.getActionMap().put("reverseList", new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				Main.playPingSound();
				reverseButton.setSelected(!reverseButton.isSelected());
				reverseButton.getActionListeners()[0].actionPerformed(null);
			}
		});
		reverseButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F3_KeyStroke, "reverseList");
		final JToggleButton codeButton = new JToggleButton(Registry.KLANT_CODE + " (" + Registry.F4_KeyName + ")");
		codeButton.setIcon(Registry.SORT_CODE_48);
		codeButton.setHorizontalTextPosition(SwingConstants.CENTER);
		codeButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		codeButton.getActionMap().put("sortByCode", new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				Main.playPingSound();
				codeButton.getActionListeners()[0].actionPerformed(null);
			}
		});
		codeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F4_KeyStroke, "sortByCode");
		final JToggleButton startButton = new JToggleButton(Registry.START_DATE + " (" + Registry.F5_KeyName + ")");
		startButton.setIcon(Registry.START_DATE_64);
		startButton.setHorizontalTextPosition(SwingConstants.CENTER);
		startButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		startButton.getActionMap().put("sortByStart", new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				Main.playPingSound();
				startButton.getActionListeners()[0].actionPerformed(null);
			}
		});
		startButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F5_KeyStroke, "sortByStart");
		final JToggleButton endButton = new JToggleButton(Registry.END_DATE + " (" + Registry.F6_KeyName + ")");
		endButton.setIcon(Registry.END_DATE_64);
		endButton.setHorizontalTextPosition(SwingConstants.CENTER);
		endButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		endButton.getActionMap().put("sortByEnd", new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				Main.playPingSound();
				endButton.getActionListeners()[0].actionPerformed(null);
			}
		});
		endButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F6_KeyStroke, "sortByEnd");
		codeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				Main.SORT_BY_CODE = true;
				Main.SORT_BY_END_DATE = Main.SORT_BY_START_DATE = false;
				codeButton.setSelected(true);
				startButton.setSelected(false);
				endButton.setSelected(false);
				Main.REVERSED = Main.CODE_REVERSED;
				reverseButton.setSelected(Main.CODE_REVERSED);
				reverseButton.setIcon((reverseButton.isSelected()) ? Registry.UP_32: Registry.DOWN_32);
				reverseButton.setText(Main.REVERSED ? "Laagste boven" : "Hoogste boven");
				reverseButton.setText(reverseButton.getText() + " (" + Registry.F3_KeyName + ")");
				sortList();
			}
		});
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				Main.SORT_BY_START_DATE = true;
				Main.SORT_BY_CODE = Main.SORT_BY_END_DATE = false;
				codeButton.setSelected(false);
				startButton.setSelected(true);
				endButton.setSelected(false);
				Main.REVERSED = Main.START_REVERSED;
				reverseButton.setSelected(Main.START_REVERSED);
				reverseButton.setIcon((reverseButton.isSelected()) ? Registry.UP_32: Registry.DOWN_32);
				reverseButton.setText(Main.REVERSED ? "Nieuwste boven" : "Oudste boven");
				reverseButton.setText(reverseButton.getText() + " (" + Registry.F3_KeyName + ")");
				sortList();
			}
		});
		endButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				Main.SORT_BY_END_DATE = true;
				Main.SORT_BY_CODE = Main.SORT_BY_START_DATE = false;
				codeButton.setSelected(false);
				startButton.setSelected(false);
				endButton.setSelected(true);
				Main.REVERSED = Main.END_REVERSED;
				reverseButton.setSelected(Main.END_REVERSED);
				reverseButton.setIcon((reverseButton.isSelected()) ? Registry.UP_32: Registry.DOWN_32);
				reverseButton.setText(Main.REVERSED ? "Nieuwste boven" : "Oudste boven");
				reverseButton.setText(reverseButton.getText() + " (" + Registry.F3_KeyName + ")");
				sortList();
			}
		});
		JPanel orderButtonPanel = new JPanel(new GridLayout(1, 3));
		orderButtonPanel.add(codeButton);
		orderButtonPanel.add(startButton);
		orderButtonPanel.add(endButton);
		codeButton.setSelected(Main.SORT_BY_CODE);
		startButton.setSelected(Main.SORT_BY_START_DATE);
		endButton.setSelected(Main.SORT_BY_END_DATE);
		orderPanel.add(orderButtonPanel);

		//reverse button
		components.add(reverseButton);
		orderPanel.add(reverseButton, BorderLayout.SOUTH);
		if(Main.SORT_BY_CODE){
			Main.REVERSED = Main.CODE_REVERSED;
			reverseButton.setText(Main.REVERSED ? "Laagste boven" : "Hoogste boven");
		}
		if(Main.SORT_BY_END_DATE){
			Main.REVERSED = Main.END_REVERSED;
			reverseButton.setText(Main.REVERSED ? "Nieuwste boven" : "Oudste boven");
		}
		if(Main.SORT_BY_START_DATE){
			Main.REVERSED = Main.START_REVERSED;
			reverseButton.setText(Main.REVERSED ? "Nieuwste boven" : "Oudste boven");
		}
		reverseButton.setText(reverseButton.getText() + " (" + Registry.F3_KeyName + ")");
		reverseButton.setSelected(Main.REVERSED);
		reverseButton.setIcon((reverseButton.isSelected()) ? Registry.UP_32: Registry.DOWN_32);
		reverseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				Main.REVERSED = reverseButton.isSelected();
				if(Main.SORT_BY_CODE){
					Main.CODE_REVERSED = Main.REVERSED;
					reverseButton.setText(Main.REVERSED ? "Laagste boven" : "Hoogste boven");
					reverseButton.setText(reverseButton.getText() + " (" + Registry.F3_KeyName + ")");
				}
				if(Main.SORT_BY_END_DATE){
					Main.END_REVERSED = Main.REVERSED;
					reverseButton.setText(Main.REVERSED ? "Nieuwste boven" : "Oudste boven");
					reverseButton.setText(reverseButton.getText() + " (" + Registry.F3_KeyName + ")");
				}
				if(Main.SORT_BY_START_DATE){
					Main.START_REVERSED = Main.REVERSED;
					reverseButton.setText(Main.REVERSED ? "Nieuwste boven" : "Oudste boven");
					reverseButton.setText(reverseButton.getText() + " (" + Registry.F3_KeyName + ")");
				}

				reverseButton.setIcon((reverseButton.isSelected()) ? Registry.UP_32: Registry.DOWN_32);				
				sortList();
			}
		});
		searchSettingPanel.add(listPanel);

		//filterpanel
		JPanel filterPanel = new JPanel(new GridLayout(4,1));
		components.add(filterPanel);
		filterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Filters"));
		JPanel paidPanel = new JPanel(new BorderLayout());
		final JToggleButton paidButton = new JToggleButton("Afgehandeld");
		paidButton.setSelected(Main.SHOW_PAID);
		paidButton.setHorizontalAlignment(SwingConstants.LEFT);
		paidButton.setIcon((paidButton.isSelected()? Registry.TICK_16: Registry.DELETE_16));
		paidButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				Main.SHOW_PAID = paidButton.isSelected();
				paidButton.setIcon((paidButton.isSelected()? Registry.TICK_16: Registry.DELETE_16));
				sortList();
			}
		});
		paidPanel.add(paidButton);
		components.add(paidButton);
		filterPanel.add(paidPanel);

		JPanel latePanel = new JPanel(new BorderLayout());
		final JToggleButton lateButton = new JToggleButton("Te laat");
		lateButton.setSelected(Main.SHOW_LATE);
		lateButton.setHorizontalAlignment(SwingConstants.LEFT);
		lateButton.setIcon((lateButton.isSelected()? Registry.TICK_16: Registry.DELETE_16));
		lateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				Main.SHOW_LATE = lateButton.isSelected();
				lateButton.setIcon((lateButton.isSelected()? Registry.TICK_16: Registry.DELETE_16));
				sortList();
			}
		});
		latePanel.add(lateButton);
		components.add(lateButton);
		filterPanel.add(latePanel);

		searchSettingPanel.add(filterPanel);

		JPanel duePanel = new JPanel(new BorderLayout());
		final JToggleButton dueButton = new JToggleButton("Vandaag inleveren");
		dueButton.setSelected(Main.SHOW_DUE_TODAY);
		dueButton.setHorizontalAlignment(SwingConstants.LEFT);
		dueButton.setIcon((dueButton.isSelected()? Registry.TICK_16: Registry.DELETE_16));
		dueButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				Main.SHOW_DUE_TODAY = dueButton.isSelected();
				dueButton.setIcon((dueButton.isSelected()? Registry.TICK_16: Registry.DELETE_16));
				sortList();
			}
		});
		components.add(dueButton);
		duePanel.add(dueButton);
		filterPanel.add(duePanel);

		JPanel miscPanel = new JPanel(new BorderLayout());
		final JToggleButton miscButton = new JToggleButton("Overigen");
		miscButton.setSelected(Main.SHOW_LATE);
		miscButton.setHorizontalAlignment(SwingConstants.LEFT);
		miscButton.setIcon((miscButton.isSelected()? Registry.TICK_16: Registry.DELETE_16));
		miscButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				Main.SHOW_NORMAL = miscButton.isSelected();
				miscButton.setIcon((miscButton.isSelected()? Registry.TICK_16: Registry.DELETE_16));
				sortList();
			}
		});
		components.add(miscButton);
		miscPanel.add(miscButton);
		filterPanel.add(miscPanel);

		//infopanel
		JPanel infoPanel = new JPanel(new GridLayout(7,1));
		infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Informatie"));
		searchSettingPanel.add(infoPanel);
		components.add(infoPanel);
		JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel dateLabel = new JLabel("Datum: -- ");
		components.add(dateLabel);
		datePanel.add(dateLabel);
		JPanel dueTodayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel dueTodayLabel = new JLabel("Vandaag inleveren: -- ");
		components.add(dueTodayLabel);
		dueTodayPanel.add(dueTodayLabel);
		JPanel rentalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel rentalLabel = new JLabel("Aantal lopende orders: -- ");
		components.add(rentalLabel);
		rentalPanel.add(rentalLabel);
		JPanel lateCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel lateCountLabel = new JLabel("Te laat: -- ");
		components.add(lateCountLabel);
		lateCountPanel.add(lateCountLabel);
		JPanel searchCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel searchCountLabel = new JLabel("Zoekresultaten: -- ");
		components.add(searchCountLabel);
		searchCountPanel.add(searchCountLabel);

		infoPanel.add(dueTodayPanel);
		infoPanel.add(rentalPanel);
		infoPanel.add(lateCountPanel);
		infoPanel.add(datePanel);
		infoPanel.add(searchCountPanel);

		Timer infoUpdateTimer = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dateLabel.setText("Datum: " + DateFormatter.getCurrentTimeStamp());
				dueTodayLabel.setText("Vandaag inleveren: " + dueTodayObjectCount);
				rentalLabel.setText("Aantal lopende orders: " + unpaidRentalCount + "/" + allRentals.size());
				lateCountLabel.setText("Te laat: " + lateRentalCount);
				searchCountLabel.setText("Zoekresultaten: " + searchResultCount);
			}
		});
		infoUpdateTimer.start();
		enableGUI(false);
	}

	/**
	 * Opens the provided rental in the viewer
	 * @param r
	 */
	private void openRentViewer(Rental r){
		windowPanel.removeAll();
		OnScreenKeyboard kb = new OnScreenKeyboard();
		kb.add(plusButton);
		windowPanel.add(kb);
		windowPanel.add(new RentViewer(r), BorderLayout.NORTH);
		revalidate();
		repaint();
	}

	/**
	 * Opens a new RentalWindow
	 */
	private void openRentWindow(){
		windowPanel.removeAll();
		OnScreenKeyboard kb = new OnScreenKeyboard();
		kb.addButton(plusButton);
		windowPanel.add(kb);
		windowPanel.add(new RentWindow(), BorderLayout.NORTH);
		revalidate();
		repaint();
	}

	/**
	 * Resets the windowPanel to only display the OnScreenKeyBoard
	 */
	public void resetWindowPanel(){
		playCloseSound();
		windowPanel.removeAll();
		OnScreenKeyboard kb = new OnScreenKeyboard();
		kb.addButton(plusButton);
		windowPanel.add(kb);
		revalidate();
		repaint();
	}

	/**
	 * Returns an array of the provided arrayList.
	 * @param rentals
	 * @return
	 */
	private Rental[] getRentalsArray(ArrayList<Rental> rentals){
		Rental[] list = new Rental[rentals.size()];
		rentals.toArray(list);
		return list;
	}

	/**
	 * Builds the popupMenu that pops up when you right click the rentalList
	 * @return
	 */
	private JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem openItem = new JMenuItem("Openen...");
		openItem.setAccelerator(KeyStroke.getKeyStroke("ENTER"));
		JMenuItem payItem = new JMenuItem("Alles inleveren...");
		payItem.setIcon(Registry.TICK_16);
		JMenuItem deleteItem = new JMenuItem("Verwijderen...");
		deleteItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
		deleteItem.setIcon(Registry.DELETE_16);

		payItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					Rental toPay = mainList.getSelectedValue();
					toPay.payAll();
					broadCast(toPay.getAsString());
				}catch(Exception exception){
					Debug.println("Couldn't open after right clicking. Maybe a null error?", this);
					exception.printStackTrace();
				}
			}
		});

		openItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					openRentViewer(mainList.getSelectedValue());
				}catch(Exception exception){
					Debug.println("Couldn't open after right clicking. Maybe a null error?", this);
					exception.printStackTrace();
				}
			}
		});

		deleteItem.addActionListener(deleteActionListener);
		menu.add(openItem);
		menu.add(payItem);
		menu.addSeparator();
		menu.add(deleteItem);
		return menu;
	}

	/**
	 * builds the GUI menu 
	 */
	private void makeMenuBar(){
		//the topMenu
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(Registry.S_FILE_MENU);
		JMenu extraMenu = new JMenu(Registry.S_EXTRA_MENU);
		JMenuItem usageItem = new JMenuItem(Registry.S_USAGE_STATISTICS_MENU);
		usageItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new UsageViewer();
			}
		});
		extraMenu.add(usageItem);
		usageItem.setIcon(Registry.STATISTICS_16);

		//locates the server and redownloads all the rentals
		JMenuItem connectionItem = new JMenuItem(Registry.SERVER_16);
		connectionItem.setText("Server");
		connectionItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				enableGUI(false);
				synchronized(allRentals){
					allRentals = new ArrayList<Rental>();
				}
				lookForServer(false);
			}
		});
		extraMenu.add(connectionItem);

		JMenuItem newItem = new JMenuItem(Registry.S_NEW_MENU);
		newItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openRentWindow();
			}
		});
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		fileMenu.add(newItem);
		fileMenu.addSeparator();
		newItem.setIcon(Registry.PLUS_16);
		
		JMenuItem makeBackupItem = new JMenuItem(Registry.S_CREATE_BACKUP_MENU);
		makeBackupItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFileChooser fileChooser = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("Verhuur backup (XML)","xml");
				fileChooser.addChoosableFileFilter(filter);
				fileChooser.setFileFilter(filter);
				Main.playOpenSound();
				int chosenOption = JOptionPane.showConfirmDialog(null, "Dit archiveert alle verhuurdata die tot op dit moment in het geheugen staat.\nDeze data is dan niet meer te doorzoeken zonder eerst deze backup weer te openen.\nVervolgens wordt het programma afgesloten en moet u het opnieuw opstarten.\nWeet u zeker dat u dat wilt doen?");
				if(chosenOption == JOptionPane.YES_OPTION){
					if((fileChooser.showSaveDialog(null) != JFileChooser.CANCEL_OPTION)){
						File target = fileChooser.getSelectedFile();
						String s = target.getAbsolutePath();
						int lastIndex = s.lastIndexOf(".");
						if(lastIndex!=-1){
							String extension = s.substring(lastIndex);
							if(!extension.equals(".xml")){
								s += ".xml";
							}
						}else{
							s +=".xml";
						}
						File newTarget = new File(s);
						synchronized(allRentals){
							XMLParser.saveRentals(allRentals, newTarget);
						}
						closeWindow();
					}
				}
			}
		});
		makeBackupItem.setIcon(Registry.SAVE_16);
		makeBackupItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		fileMenu.add(makeBackupItem);

		JMenuItem quitItem = new JMenuItem(Registry.S_QUIT_MENU);
		quitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeWindow();
			}
		});
		quitItem.setIcon(Registry.DELETE_16);
		quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		fileMenu.addSeparator();
		fileMenu.add(quitItem);

		menuBar.add(fileMenu);
		menuBar.add(extraMenu);
		setJMenuBar(menuBar);
	}

	/**
	 * updates the info variables
	 */
	private void updateObjectCountVariables(){
		unpaidRentalCount = 0;
		dueTodayObjectCount = 0;
		lateRentalCount = 0;
		synchronized(allRentals){
			for(Rental cRental : allRentals){
				if(!cRental.isAllPaid()){
					unpaidRentalCount++;
					if(cRental.isLate()){
						lateRentalCount++;
					}
				}
				if(cRental.isDueToday() && !cRental.isAllPaid()){
					dueTodayObjectCount+= cRental.getObjectCount();
				}
			}
		}
	}

	/**
	 * does the necessary shutdown stuff
	 */
	private void closeWindow(){
		//closes any open "windows" for rentviewers or new rental windows
		if(RentWindow.cOpen != null) RentWindow.cOpen.closeWindow();
		if(RentViewer.cOpen != null) RentViewer.cOpen.closeWindow();

		//save preferences and close main screen
		Registry.APP_MAXIMIZED = (getExtendedState() == JFrame.MAXIMIZED_BOTH); 
		Registry.APP_SIZE = (Registry.APP_MAXIMIZED) ? new Dimension(1280, 720) : getSize();
		savePrefs();
		Preferences.save(Registry.PREF_FILE_LOCATION, Registry.APP_TITLE);
		dispose();

		//wait 5 sec for all communications to time out
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//close server and JVM
		if(Main.isServer) RentalServer.activeInstance.shutDown();
		System.exit(0);
	}

	/**
	 * Checks if the provided code is still available for use
	 * @param s
	 * @return
	 */
	public boolean codeIsAvailable(String s){
		synchronized(allRentals){
			for(Rental r : allRentals){
				if(r.getCode().equals(s)){
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Returns the last used code if it is available. Otherwise it keeps on adding
	 * one to it untill it is available
	 * @return
	 */
	public int getLastUsedCode(){
		while(!codeIsAvailable(Integer.toString(Main.lastUsedNumber))){
			Main.lastUsedNumber ++;
		}
		return Main.lastUsedNumber;
	}

	/**
	 * registers all the images to the Registry on a separate thread
	 */
	private static void getImages(){
		new Thread(new Runnable() {
			public void run() {
				Registry.PLUS_WEEK = ImageHelper.getImageIcon("/trb1914/img/plusWeek.png");
				Registry.MINUS_WEEK = ImageHelper.getImageIcon("/trb1914/img/minusWeek.png");
				Registry.PLUS_DAY = ImageHelper.getImageIcon("/trb1914/img/plusDay.png");
				Registry.MINUS_DAY = ImageHelper.getImageIcon("/trb1914/img/minusDay.png");
				Registry.OPEN_16 = ImageHelper.getImageIcon("/trb1914/img/folder_16.png");
				Registry.APP_ICON = ImageHelper.getImageIcon("/trb1914/img/icon.png");
				Registry.PLUS_48 = ImageHelper.getImageIcon("/trb1914/img/plus_48.png");
				Registry.PLUS_16 = ImageHelper.getImageIcon("/trb1914/img/plus_16.png");
				Registry.TICK_48 = ImageHelper.getImageIcon("/trb1914/img/tick_48.png");
				Registry.TICK_32 = ImageHelper.getImageIcon("/trb1914/img/tick_32.png");
				Registry.DELETE_48 = ImageHelper.getImageIcon("/trb1914/img/delete_48.png");
				Registry.DELETE_32 = ImageHelper.getImageIcon("/trb1914/img/delete_32.png");
				Registry.DELETE_16 = ImageHelper.getImageIcon("/trb1914/img/delete_16.png");
				Registry.SEARCH_16 = ImageHelper.getImageIcon("/trb1914/img/search_16.png");
				Registry.SEARCH_32 = ImageHelper.getImageIcon("/trb1914/img/search_32.png");
				Registry.RIGHT_16 = ImageHelper.getImageIcon("/trb1914/img/right_16.png");
				Registry.BULLET_BLUE = ImageHelper.getImageIcon("/trb1914/img/bullet_blue.png");
				Registry.BULLET_GREEN = ImageHelper.getImageIcon("/trb1914/img/bullet_green.png");
				Registry.BULLET_ORANGE = ImageHelper.getImageIcon("/trb1914/img/bullet_orange.png");
				Registry.BULLET_RED = ImageHelper.getImageIcon("/trb1914/img/bullet_red.png");
				Registry.TICK_16 = ImageHelper.getImageIcon("/trb1914/img/tick.png");
				Registry.HOURGLASS = ImageHelper.getImageIcon("/trb1914/img/hourglass.png");
				Registry.REFRESH_ICON_16 = ImageHelper.getImageIcon("/trb1914/img/arrow_refresh.png");
				Registry.SAVE_16 = ImageHelper.getImageIcon("/trb1914/img/save_16.png");		
				Registry.STATISTICS_16 = ImageHelper.getImageIcon("/trb1914/img/statistics_16.png");	
				Registry.WARNING_YELLOW_48 = ImageHelper.getImageIcon("/trb1914/img/warning_48.png");	
				Registry.WARNING_YELLOW_16 = ImageHelper.getImageIcon("/trb1914/img/warning_16.png");
				Registry.WARNING_RED_48 = ImageHelper.getImageIcon("/trb1914/img/warning_48_red.png");	
				Registry.WARNING_RED_16 = ImageHelper.getImageIcon("/trb1914/img/warning_16_red.png");
				Registry.START_DATE_64 = ImageHelper.getImageIcon("/trb1914/img/start_64.png");
				Registry.END_DATE_64 = ImageHelper.getImageIcon("/trb1914/img/end_64.png");
				Registry.SORT_CODE_48 = ImageHelper.getImageIcon("/trb1914/img/code_48.png");
				Registry.UP_32 = ImageHelper.getImageIcon("/trb1914/img/up_32.png");
				Registry.DOWN_32 = ImageHelper.getImageIcon("/trb1914/img/down_32.png");
				Registry.SERVER_16 = ImageHelper.getImageIcon("/trb1914/img/server_go.png");
				Registry.IMAGES_LOADED.set(true);
			}
		}).start();
	}

	/**
	 * updates the list and checks for the search query
	 */
	private void updateList(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				foundRentals = new ArrayList<Rental>();
				if(searchField.getText().length() > 0){
					String query = searchField.getText();
					for(Rental cRental : filteredRentals){
						if(cRental.hasCode(query)){
							foundRentals.add(cRental);
						}else if(cRental.hasObject(query, Main.SHOW_PAID)){
							foundRentals.add(cRental);
						}
					}
					Registry.sysMsg.display("Zoekopdracht: \""+query +  "\" Resultaten: " + foundRentals.size());
				}else{
					foundRentals = filteredRentals;
				}
				searchResultCount = foundRentals.size();
				mainList.clearSelection();
				mainList.setListData(getRentalsArray(foundRentals));
				updateObjectCountVariables();

				revalidate();
				repaint();
			}
		});
	}

	/**
	 * checks if the provided Rental is allowed to be displayed
	 */
	private boolean allowToAddToList(Rental r){
		if(r.isLate() && Main.SHOW_LATE && !r.isAllPaid()) return true;
		else if(r.isDueToday() && Main.SHOW_DUE_TODAY && !r.isAllPaid()) return true;
		else if(r.isAllPaid() && Main.SHOW_PAID) return true;
		else if(Main.SHOW_NORMAL && (!r.isDueToday()) && (!r.isLate()) && !(r.isAllPaid())) return true;
		return false;
	}

	/** Sorts the list according to the sorting flags.
	 * @param recallDelay	the delay to sort again in milliseconds.
	 */
	private void sortList(){
		new Thread(new Runnable(){
			public void run() {
				if(Main.SORT_BY_CODE){
					sortByCode(Main.REVERSED);
				}else if(Main.SORT_BY_END_DATE){
					sortByEndDate(Main.REVERSED);
				}else if(Main.SORT_BY_START_DATE){
					sortByStartDate(Main.REVERSED);
				}
				
				synchronized(allRentals){
					filteredRentals = new ArrayList<Rental>();
					for(Rental r : allRentals){
						if(allowToAddToList(r)) filteredRentals.add(r);
					}
				}
				
				updateList();
			}
		}).start();
	}

	/**
	 * checks ALL rentals to see if this object is available for rental
	 * @param s
	 * @return
	 */
	public boolean isAlreadyRented(String s){
		synchronized(allRentals){
			for(Rental r : allRentals){
				if(r.hasExactObject(s)) return true;
			}
		}
		return false;
	}

	/**
	 * sorts all the rentals by code
	 * @param reversed
	 */
	private void sortByCode(final boolean reversed){
		synchronized(allRentals){
			Collections.sort(allRentals ,new Comparator<Rental>(){
				public int compare(Rental r1, Rental r2) {
					if(!reversed) return r2.codeInt - r1.codeInt;
					return r1.codeInt - r2.codeInt;
				}						
			});
		}
	}

	/**
	 * sorts all the rentals by startDate
	 * @param reversed
	 */
	private void sortByStartDate(final boolean reversed){
		synchronized(allRentals){
			Collections.sort(allRentals ,new Comparator<Rental>(){
				public int compare(Rental r1, Rental r2) {
					if(!reversed) return r1.getStartDateValue().compareTo(r2.getStartDateValue());
					else return r2.getStartDateValue().compareTo(r1.getStartDateValue());
				}						
			});
		}
	}

	/**
	 * sorts all the rentals by endDate
	 * @param reversed
	 */
	private void sortByEndDate(final boolean reversed){
		synchronized(allRentals){
			Collections.sort(allRentals,new Comparator<Rental>(){
				public int compare(Rental r1, Rental r2) {
					if(!reversed) return r1.getEndDateValue().compareTo(r2.getEndDateValue());
					else return r2.getEndDateValue().compareTo(r1.getEndDateValue());
				}						
			});
		}
	}


	private static Color getColorFromPreferences(String key, Color defaultValue){
		try{
			return Color.decode(Preferences.get(key));
		}catch(Exception e){
			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * Returns the keyName from the keystroke
	 * @param s
	 */
	private static String getNameFromKeyStroke(KeyStroke stroke){
		String[] parts = stroke.toString().split(" ");
		if(parts.length > 1){
			return parts[parts.length - 1];
		}
		return "-";
	}

	/**
	 * Saves the easy acces variables back into the properties object that is going to be saved
	 */
	private static void savePrefs(){
		Preferences.set("SND_masterGain", Registry.MASTER_GAIN);
		Preferences.set("SND_buttonSoundEnabled", OnScreenKeyboard.BUTTON_SOUND);
		Preferences.set("WINDOW_maximized", Registry.APP_MAXIMIZED);
		Preferences.set("WINDOW_width", Registry.APP_SIZE.width);
		Preferences.set("WINDOW_height", Registry.APP_SIZE.height);
		Preferences.set("PROG_codeReversed", Main.CODE_REVERSED);
		Preferences.set("PROG_startReversed", Main.START_REVERSED);
		Preferences.set("PROG_endReversed", Main.END_REVERSED);
		Preferences.set("PROG_splashing", Main.SPLASHING);
		Preferences.set("UI_fancyColors", Main.FANCY_COLORS);
		Preferences.set("NET_isServer", Main.isServer);
		Preferences.set("PROG_lastUsedNumber", Main.lastUsedNumber);
		Preferences.set("PROG_reversed", Main.REVERSED);
		Preferences.set("PROG_showDue", Main.SHOW_DUE_TODAY);
		Preferences.set("PROG_showLate", Main.SHOW_LATE);
		Preferences.set("PROG_showNormal", Main.SHOW_NORMAL);
		Preferences.set("PROG_showPaid", Main.SHOW_PAID);
		Preferences.set("PROG_sortCode", Main.SORT_BY_CODE);
		Preferences.set("PROG_sortEnd", Main.SORT_BY_END_DATE);
		Preferences.set("PROG_sortStart", Main.SORT_BY_START_DATE);
		Preferences.set("NET_portNumber", RentalServer.PORT_NUMBER);
		Preferences.set("FNT_rentFontSize", Registry.RENT_WINDOW_FONT_SIZE);
		Preferences.set("FNT_searchFontSize", Registry.SEARCH_FONT_SIZE);
		Preferences.set("FNT_listFontSize", Registry.LIST_FONT_SIZE);
		Preferences.set("FNT_keyboardFontSize", OnScreenKeyboard.FONT_SIZE);
		Preferences.set("FILE_saveDelay", Registry.SAVE_DELAY);
		Preferences.set("NET_ipTimeout", RentalServer.CHECK_TIMEOUT);
		Preferences.set("NET_messageTimeout", SocketHelper.MESSAGE_TIMEOUT);
		Preferences.set("NET_serverAddress", Main.LAST_SERVER_ADDRESS);
		Preferences.set("KEY_func1Key", Registry.F1_KeyStroke.toString());
		Preferences.set("KEY_func2Key", Registry.F2_KeyStroke.toString());
		Preferences.set("KEY_func3Key", Registry.F3_KeyStroke.toString());
		Preferences.set("KEY_func4Key", Registry.F4_KeyStroke.toString());
		Preferences.set("KEY_func5Key", Registry.F5_KeyStroke.toString());
		Preferences.set("KEY_func6Key", Registry.F6_KeyStroke.toString());
		Preferences.set("KEY_func7Key", Registry.F7_KeyStroke.toString());
		Preferences.set("KEY_escKey", Registry.ESC_KeyStroke.toString());
		Preferences.set("FILE_backupLocation", new File(Registry.BACKUP_LOCATION).getAbsolutePath());
		Preferences.set("FILE_backupEnabled", XMLParser.DO_BACKUP);
		Preferences.set("UI_maxListItems", RentalRenderer.MAX_ITEMS);
		Preferences.set("UI_keyboardKeysize", OnScreenKeyboard.KEY_SIZE);
		Preferences.set("UI_colorKeyboard", Integer.toHexString(OnScreenKeyboard.bgColor.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorPaid", Integer.toHexString(RentalRenderer.PAID_COLOR.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorPaidBG", Integer.toHexString(RentalRenderer.PAID_COLOR_BG.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorLate", Integer.toHexString(RentalRenderer.LATE_COLOR.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorLateBG", Integer.toHexString(RentalRenderer.LATE_COLOR_BG.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorDue", Integer.toHexString(RentalRenderer.DUE_COLOR.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorDueBG", Integer.toHexString(RentalRenderer.DUE_COLOR_BG.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorStd", Integer.toHexString(RentalRenderer.STD_COLOR.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorStdBG", Integer.toHexString(RentalRenderer.STD_COLOR_BG.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorDisabled", Integer.toHexString(RentalRenderer.DISABLED_COLOR.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorNoMatch", Integer.toHexString(RentalRenderer.NO_MATCH_COLOR.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorBorder", Integer.toHexString(RentalRenderer.BORDER_COLOR.getRGB()).toUpperCase().replaceFirst("FF", "#"));
		Preferences.set("UI_colorBorderSel", Integer.toHexString(RentalRenderer.BORDER_COLOR_SEL.getRGB()).toUpperCase().replaceFirst("FF", "#"));
	}

	/**
	 * Loads the preferences into variables for easy acces
	 */
	private static void loadPrefs(){
		Registry.MASTER_GAIN = Preferences.getFloat("SND_masterGain", Registry.MASTER_GAIN);
		OnScreenKeyboard.SOUND_GAIN = Registry.MASTER_GAIN;//set OSK gain to match application gain
		OnScreenKeyboard.BUTTON_SOUND = Preferences.getBoolean("SND_buttonSoundEnabled", OnScreenKeyboard.BUTTON_SOUND);
		OnScreenKeyboard.KEY_SIZE = Preferences.getDimension("UI_keyboardKeysize", OnScreenKeyboard.KEY_SIZE);
		Registry.APP_MAXIMIZED = Preferences.getBoolean("WINDOW_maximized", Registry.APP_MAXIMIZED);
		Registry.APP_SIZE = new Dimension(Preferences.getInteger("WINDOW_width", Registry.APP_SIZE.width), 
				Preferences.getInteger("WINDOW_height", Registry.APP_SIZE.height));

		Main.SPLASHING = Preferences.getBoolean("PROG_splashing", Main.SPLASHING);
		Main.FANCY_COLORS = Preferences.getBoolean("UI_fancyColors", Main.FANCY_COLORS);
		Main.isServer = Preferences.getBoolean("NET_isServer", Main.isServer);
		Main.lastUsedNumber = Preferences.getInteger("PROG_lastUsedNumber", Main.lastUsedNumber);
		Main.REVERSED = Preferences.getBoolean("PROG_reversed", Main.REVERSED);
		Main.CODE_REVERSED = Preferences.getBoolean("PROG_codeReversed", Main.CODE_REVERSED);
		Main.START_REVERSED = Preferences.getBoolean("PROG_startReversed", Main.START_REVERSED);
		Main.END_REVERSED = Preferences.getBoolean("PROG_endReversed", Main.END_REVERSED);
		Main.SHOW_DUE_TODAY = Preferences.getBoolean("PROG_showDue", Main.SHOW_DUE_TODAY);
		Main.SHOW_LATE = Preferences.getBoolean("PROG_showLate", Main.SHOW_LATE);
		Main.SHOW_NORMAL = Preferences.getBoolean("PROG_showNormal", Main.SHOW_NORMAL);
		Main.SHOW_PAID = Preferences.getBoolean("PROG_showPaid", Main.SHOW_PAID);
		Main.SORT_BY_CODE = Preferences.getBoolean("PROG_sortCode", Main.SORT_BY_CODE);
		Main.SORT_BY_END_DATE = Preferences.getBoolean("PROG_sortEnd", Main.SORT_BY_END_DATE);
		Main.SORT_BY_START_DATE = Preferences.getBoolean("PROG_sortStart", Main.SORT_BY_START_DATE);
		RentalServer.PORT_NUMBER = Preferences.getInteger("NET_portNumber", RentalServer.PORT_NUMBER);
		RentalServer.CHECK_TIMEOUT = Preferences.getInteger("NET_ipTimeout", RentalServer.CHECK_TIMEOUT);
		Registry.RENT_WINDOW_FONT_SIZE = Preferences.getFloat("FNT_rentFontSize", Registry.RENT_WINDOW_FONT_SIZE);
		Registry.SEARCH_FONT_SIZE = Preferences.getFloat("FNT_searchFontSize", Registry.SEARCH_FONT_SIZE);
		Registry.LIST_FONT_SIZE = Preferences.getInteger("FNT_listFontSize", Registry.LIST_FONT_SIZE);
		OnScreenKeyboard.FONT_SIZE = Preferences.getFloat("FNT_keyboardFontSize", OnScreenKeyboard.FONT_SIZE);
		Registry.SAVE_DELAY = Preferences.getInteger("FILE_saveDelay", Registry.SAVE_DELAY);
		Registry.BACKUP_LOCATION = Preferences.get("FILE_backupLocation", Registry.BACKUP_LOCATION);
		SocketHelper.MESSAGE_TIMEOUT = Preferences.getInteger("NET_messageTimeout", SocketHelper.MESSAGE_TIMEOUT);
		Main.LAST_SERVER_ADDRESS = Preferences.get("NET_serverAddress", Main.LAST_SERVER_ADDRESS);
		RentalRenderer.MAX_ITEMS = Preferences.getInteger("UI_maxListItems", RentalRenderer.MAX_ITEMS);
		Registry.F1_KeyStroke = KeyStroke.getKeyStroke(Preferences.get("KEY_func1Key", Registry.F1_KeyStroke.toString()));
		Registry.F2_KeyStroke = KeyStroke.getKeyStroke(Preferences.get("KEY_func2Key", Registry.F2_KeyStroke.toString()));
		Registry.F3_KeyStroke = KeyStroke.getKeyStroke(Preferences.get("KEY_func3Key", Registry.F3_KeyStroke.toString()));
		Registry.F4_KeyStroke = KeyStroke.getKeyStroke(Preferences.get("KEY_func4Key", Registry.F4_KeyStroke.toString()));
		Registry.F5_KeyStroke = KeyStroke.getKeyStroke(Preferences.get("KEY_func5Key", Registry.F5_KeyStroke.toString()));
		Registry.F6_KeyStroke = KeyStroke.getKeyStroke(Preferences.get("KEY_func6Key", Registry.F6_KeyStroke.toString()));
		Registry.F7_KeyStroke = KeyStroke.getKeyStroke(Preferences.get("KEY_func7Key", Registry.F7_KeyStroke.toString()));
		Registry.ESC_KeyStroke = KeyStroke.getKeyStroke(Preferences.get("KEY_escKey", Registry.ESC_KeyStroke.toString()));
		Registry.F1_KeyName = getNameFromKeyStroke(Registry.F1_KeyStroke);
		Registry.F2_KeyName = getNameFromKeyStroke(Registry.F2_KeyStroke);
		Registry.F3_KeyName = getNameFromKeyStroke(Registry.F3_KeyStroke);
		Registry.F4_KeyName = getNameFromKeyStroke(Registry.F4_KeyStroke);
		Registry.F5_KeyName = getNameFromKeyStroke(Registry.F5_KeyStroke);
		Registry.F6_KeyName = getNameFromKeyStroke(Registry.F6_KeyStroke);
		Registry.F7_KeyName = getNameFromKeyStroke(Registry.F7_KeyStroke);
		Registry.ESC_KeyName = getNameFromKeyStroke(Registry.ESC_KeyStroke);
		XMLParser.DO_BACKUP = Preferences.getBoolean("FILE_backupEnabled", XMLParser.DO_BACKUP);
		OnScreenKeyboard.bgColor = getColorFromPreferences("UI_colorKeyboard", OnScreenKeyboard.bgColor);
		RentalRenderer.PAID_COLOR = getColorFromPreferences("UI_colorPaid", RentalRenderer.PAID_COLOR);
		RentalRenderer.PAID_COLOR_BG = getColorFromPreferences("UI_colorPaidBG", RentalRenderer.PAID_COLOR_BG);
		RentalRenderer.LATE_COLOR = getColorFromPreferences("UI_colorLate", RentalRenderer.LATE_COLOR);
		RentalRenderer.LATE_COLOR_BG = getColorFromPreferences("UI_colorLateBG", RentalRenderer.LATE_COLOR_BG);
		RentalRenderer.DUE_COLOR = getColorFromPreferences("UI_colorDue", RentalRenderer.DUE_COLOR);
		RentalRenderer.DUE_COLOR_BG = getColorFromPreferences("UI_colorDueBG", RentalRenderer.DUE_COLOR_BG);
		RentalRenderer.STD_COLOR = getColorFromPreferences("UI_colorStd", RentalRenderer.STD_COLOR);
		RentalRenderer.STD_COLOR_BG = getColorFromPreferences("UI_colorStdBG", RentalRenderer.STD_COLOR_BG);
		RentalRenderer.DISABLED_COLOR = getColorFromPreferences("UI_colorDisabled", RentalRenderer.DISABLED_COLOR);
		RentalRenderer.NO_MATCH_COLOR = getColorFromPreferences("UI_colorNoMatch", RentalRenderer.NO_MATCH_COLOR);
		RentalRenderer.BORDER_COLOR = getColorFromPreferences("UI_colorBorder", RentalRenderer.BORDER_COLOR);
		RentalRenderer.BORDER_COLOR_SEL = getColorFromPreferences("UI_colorBorderSel", RentalRenderer.BORDER_COLOR_SEL);
	}
}
