package trb1914.dictionary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import trb1914.debug.Debug;
import trb1914.helper.FileHelper;
import trb1914.helper.ImageHelper;
import trb1914.helper.SystemHelper;
import trb1914.preferences.Preferences;

import com.sun.glass.events.KeyEvent;

/**
 * Main class for the dictionary lookup app. This application only
 * is a shorthand for the morph tool of perseus
 * @author Mees Gelein
 */
public class PerseusDictionary extends JFrame{

	/**Cache to store words for offline acces*/
	public static String CACHE = System.getProperty("user.home") + File.separator +"PerseusDictionaryCache";
	/**The display panel for the found solutions*/
	private ResultPanel resultPanel = new ResultPanel();
	/**The url we use to lookup a word*/
	private String target = "http://www.perseus.tufts.edu/hopper/morph?l=entos&la=lang";
	/**The area to input your query*/
	private JTextField queryField = new JTextField();
	/**The language we use for the query*/
	private String language = "greek";

	/**
	 * Creates a new instance of the program
	 */
	public PerseusDictionary() {
		loadPrefs();
		makeGUI();
		setTitle("Perseus Dictionary Tool");

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		File pdCache = new File(CACHE);
		if(!pdCache.exists()){
			pdCache.mkdirs();
		}
	}
	
	/**
	 * Loads the preferences saved in the cache directory
	 */
	private void loadPrefs(){
		Preferences.load(CACHE + File.separator +"config.properties");
		resultPanel.shownResults = Preferences.getInteger("numResults", 2);
		language = Preferences.get("queryLanguage", "greek");
	}
	
	/**
	 * Saves the preferences
	 */
	private void savePrefs(){
		Preferences.set("numResults", resultPanel.shownResults);
		Preferences.set("queryLanguage", language);
		Preferences.save(CACHE + File.separator +"config.properties", "Perseus Dictionary Properties");
	}
	
	private void closeWindow(){
		savePrefs();
		dispose();
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * Creates the GUI
	 */
	private void makeGUI(){
		setIconImage(ImageHelper.getImage("/trb1914/dictionary/icon.png"));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				closeWindow();
			}
		});
		setLayout(new BorderLayout());

		Border bLine = BorderFactory.createMatteBorder(0, 1, 0, 1, Color.gray);
		Border bA = BorderFactory.createEmptyBorder(4, 0, 0, 0);
		Border bB = BorderFactory.createMatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY);
		CompoundBorder border = new CompoundBorder(bLine, new CompoundBorder(bA, bB));

		JPanel searchPanel = new JPanel(new BorderLayout());
		add(searchPanel, BorderLayout.WEST);
		searchPanel.setBorder(border);

		JPanel inputPanel = new JPanel(new BorderLayout());
		searchPanel.add(inputPanel, BorderLayout.NORTH);
		queryField.setHorizontalAlignment(SwingConstants.CENTER);
		inputPanel.add(queryField, BorderLayout.NORTH);
		queryField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		inputPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		JLabel keyLabel = new JLabel(ImageHelper.getImageIcon("/trb1914/dictionary/keyCaps.png"));
		JLabel logoLabel = new JLabel(ImageHelper.getImageIcon("/trb1914/dictionary/newbanner.png"));
		JPanel holderPanel = new JPanel(new GridLayout(2, 1));
		holderPanel.add(logoLabel);
		holderPanel.add(keyLabel);
		searchPanel.add(holderPanel, BorderLayout.SOUTH);

		JComboBox<Integer> numResultsBox = new JComboBox<Integer>();
		JLabel numResultLabel = new JLabel("Show ");
		JLabel numResultLabel2 = new JLabel("result(s)");
		JPanel numResultPanel = new JPanel(); 
		numResultPanel.add(numResultLabel);
		numResultPanel.add(numResultsBox);
		numResultPanel.add(numResultLabel2);
		numResultsBox.addItem(1);
		numResultsBox.addItem(2);
		numResultsBox.addItem(3);
		numResultsBox.addItem(4);
		numResultsBox.addItem(5);
		numResultsBox.setSelectedItem(resultPanel.shownResults);
		numResultsBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultPanel.shownResults = (int) numResultsBox.getSelectedItem();
				checkWindowSize();
				queryField.requestFocus();
			}
		});

		JPanel langPanel = new JPanel();
		JRadioButton latinButton = new JRadioButton("Latin");
		latinButton.setMnemonic(KeyEvent.VK_L);
		latinButton.setToolTipText("Alt + L");
		JRadioButton greekButton = new JRadioButton("Greek");
		greekButton.setMnemonic(KeyEvent.VK_G);
		greekButton.setToolTipText("Alt + G");
		langPanel.add(latinButton);
		langPanel.add(greekButton);
		langPanel.add(numResultPanel);
		searchPanel.add(langPanel);
		latinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				language = "latin";
				latinButton.setSelected(true);
				greekButton.setSelected(false);
				queryField.requestFocus();
			}
		});
		greekButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				language = "greek";
				greekButton.setSelected(true);
				latinButton.setSelected(false);
				queryField.requestFocus();
			}
		});
		if(language.equals("greek")) greekButton.setSelected(true);
		else latinButton.setSelected(true);

		add(resultPanel);

		JButton searchButton = new JButton("Search...");
		searchButton.setIcon(ImageHelper.getImageIcon("/trb1914/dictionary/search.png"));
		inputPanel.add(searchButton, BorderLayout.SOUTH);
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});

		queryField.setFont(searchButton.getFont().deriveFont(20.0f));
		checkWindowSize();
		setResizable(false);
	}

	/**
	 * Resizes the window based upon the amount of results displayed
	 */
	private void checkWindowSize(){
		setPreferredSize(new Dimension(370 + resultPanel.shownResults * (Result.PREF_SIZE.width + 8) , 390));
		pack();
	}

	/**
	 * Triggers the searching action
	 */
	private void search(){
		resultPanel.makeSpace();
		new Thread(new Runnable(){
			public void run(){
				String response;
				File queryCache = new File(CACHE + File.separator + queryField.getText().trim() + ".txt");
				if(queryCache.exists()){
					response = FileHelper.openFile(queryCache, true);
				}else{
					response =	getHTMLData(queryField.getText().trim());
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						resultPanel.showResult(response, queryField.getText().trim());
						queryField.setText("");
					}
				});
			}
		}).start();
	}

	/**
	 * Returns the HTML data for the lookup
	 * @return
	 */
	private String getHTMLData(String query){
		StringBuilder builder = new StringBuilder();
		String line = "";
		if(query.length() < 1) return "No Result";
		String tempTarget = target;
		tempTarget = tempTarget.replace("lang", language);
		try{
			URL url = new URL(tempTarget.replace("entos", query));
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			boolean start = false, tempStop = false, skip = false;
			line = reader.readLine();
			while(line != null){
				//START <> END
				if(line.contains("main_col")) {//start reading when you find the main col div
					start = true;
				}else if(line.contains("alt=\"view as XML")){//stop reading when you find the view as XML button
					start = false;
				}
				//SKIPPING LINES
				skip = false;
				if(line.contains("<p>")) tempStop = true;
				if(line.contains("</p>")) tempStop = false;
				if(line.contains("frequency stat")) skip = true;

				//ACTUAL ADDING
				if(start){
					if(!tempStop && !skip) builder.append(line);
				}
				line = reader.readLine();
			}
			reader.close();
		}catch(Exception ex){
			Debug.println("Something went wrong during connection.", this);
			ex.printStackTrace();
			return "<h1>An error occured</h1><p>Here is a selection of possible reasons:"
			+ "<ol><li>Your internet connection does not function properly</li>"
			+ "<li><a href=\"www.perseus.tufts.edu\">Perseus</a> is temporarily offline</li></ol>"
			+ "</p><p>If this is not the case there is another (unknown) problem. You are basically screwed.</p>";
		}

		String result = builder.toString().replaceAll( "(?s)<!--.*?-->", "" );		
		return result;
	}

	/**
	 * Entry point
	 * @param args	cmd line arguments
	 */
	public static void main(String[] args){
		SystemHelper.setSystemDefaultLF();

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new PerseusDictionary();
			}
		});
	}
}
