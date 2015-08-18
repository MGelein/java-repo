package trb1914.mail;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Allows the user to change the settings
 * @author Mees Gelein
 */
public class SettingsFrame extends JFrame{

	public static boolean open = false;
	public static final Integer[] intervals = new Integer[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 150, 180, 210, 240, 270, 300};
	public static final String[] locations = new String[]{"Top left", "Top right", "Bottom left", "Bottom right (default)"};
	private JComboBox<Integer> timeBox = new JComboBox<Integer>(intervals);
	private JCheckBox silentBox;
	private JTextField userField = new JTextField(15);
	private JPasswordField passField = new JPasswordField(15);
	private JCheckBox minimizedBox;
	private JComboBox<String> popupBox = new JComboBox<String>(locations);
	/**
	 * Creates a new settingsframe
	 */
	public SettingsFrame(){
		SettingsFrame.open = true;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				SettingsFrame.open = false;
				saveSettings();
				GMailReader.main.reloadSettings();
			}
		});
		setIconImage(GMailReader.APP_ICON.getImage());
		setTitle(GMailReader.TITLE + " - Settings");
		makeGUI();
		setResizable(false);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Creates the GUI
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		
		JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 6));
		add(contentPanel, BorderLayout.CENTER);
		Border emptyBorder = BorderFactory.createEmptyBorder(4, 4, 4, 4);
		
		JPanel appPanel = new JPanel(new GridLayout(3, 1, 0, 4));
		appPanel.setBorder(BorderFactory.createTitledBorder("Application Settings"));
		
		JPanel intervalPanel = new JPanel(new BorderLayout());
		intervalPanel.setBorder(emptyBorder);
		JLabel intervalLabel = new JLabel("Check inbox every ");
		JLabel secondLabel = new JLabel(" seconds");
		timeBox.setSelectedIndex(getIndexOf(GMailReader.interval));
		intervalPanel.add(intervalLabel, BorderLayout.WEST);
		intervalPanel.add(timeBox, BorderLayout.CENTER);
		intervalPanel.add(secondLabel, BorderLayout.EAST);
		
		JPanel silentPanel = new JPanel(new BorderLayout());
		silentPanel.setBorder(emptyBorder);
		silentBox = new JCheckBox("Voiced message notification");
		silentPanel.add(silentBox, BorderLayout.WEST);
		silentBox.setSelected(!GMailReader.silent);
		
		JPanel minimPanel = new JPanel(new BorderLayout());
		minimizedBox = new JCheckBox("Start the program in system tray");
		minimPanel.add(minimizedBox);
		appPanel.add(minimPanel);
		
		appPanel.add(intervalPanel);
		appPanel.add(silentPanel);
		
		
		JPanel emailPanel = new JPanel(new GridLayout(3, 1, 0, 4));
		emailPanel.setBorder(BorderFactory.createTitledBorder("Email Settings"));
		
		JPanel userPanel = new JPanel(new BorderLayout());
		userPanel.setBorder(emptyBorder);
		JLabel userLabel = new JLabel("Username: ");
		userPanel.add(userLabel, BorderLayout.WEST);
		userPanel.add(userField);
		userField.setHorizontalAlignment(JTextField.RIGHT);
		try{
			userField.setText(GMailReader.user.substring(0, GMailReader.user.indexOf("@")));
		}catch(Exception e){
			e.printStackTrace();
		}
		userPanel.add(new JLabel("@gmail.com"), BorderLayout.EAST);
		
		JPanel passPanel = new JPanel(new BorderLayout());
		passPanel.setBorder(emptyBorder);
		JLabel passLabel = new JLabel("Password: ");
		passPanel.add(passLabel, BorderLayout.WEST);
		passPanel.add(passField, BorderLayout.CENTER);
		passField.setText(GMailReader.pass);
		passField.setHorizontalAlignment(userField.getHorizontalAlignment());
		JCheckBox showBox = new JCheckBox("Show text");
		showBox.setSelected(false);
		showBox.addActionListener(new ActionListener(){
			private boolean showing = false;
			private char echoChar = passField.getEchoChar();
			public void actionPerformed(ActionEvent e){
				if(!showing){
					showing = true;
					passField.setEchoChar((char) 0);
				}else{
					showing = false;
					passField.setEchoChar(echoChar);
				}
			}
		});
		passPanel.add(showBox, BorderLayout.EAST);
		
		JPanel popupPanel = new JPanel(new BorderLayout());
		JLabel popupLabel = new JLabel("Message popup location: ");
		popupPanel.add(popupBox);
		popupBox.setSelectedIndex(MessagePopup.currentLocation - 1);
		popupPanel.add(popupLabel, BorderLayout.WEST);
		
		emailPanel.add(userPanel);
		emailPanel.add(passPanel);
		emailPanel.add(popupPanel);
		
		contentPanel.add(appPanel);
		contentPanel.add(emailPanel);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SettingsFrame.open = false;
				saveSettings();
				GMailReader.main.reloadSettings();
				dispose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SettingsFrame.open = false;
				GMailReader.main.reloadSettings();
				dispose();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	/**
	 * Saves all the settings back to the mainapplication
	 */
	@SuppressWarnings("deprecation")
	private void saveSettings(){
		GMailReader.interval = timeBox.getItemAt(timeBox.getSelectedIndex()) * 1000;
		GMailReader.silent = !silentBox.isSelected();
		GMailReader.user = userField.getText() + "@gmail.com";
		GMailReader.pass = passField.getText();
		GMailReader.minimized = minimizedBox.isSelected();
		MessagePopup.setLocation(popupBox.getSelectedIndex() + 1);
	}
	
	/**
	 * Tries to find the index in the list of the provided interval. Returns 0 
	 * if it cant find a match
	 * @param interval
	 * @return
	 */
	private int getIndexOf(int interval){
		for(int i = 0; i < intervals.length; i ++){
			if(interval / 1000 == intervals[i]) return i;
		}
		return 0;
	}
}
