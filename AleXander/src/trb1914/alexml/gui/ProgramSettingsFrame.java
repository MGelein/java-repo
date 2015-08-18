package trb1914.alexml.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.alexml.Preferences;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.interfaces.ICloseWindow;
/**
 * This class shows a window that allows the user to set some
 * preferences using a GUI instead of the prefs.txt file
 * @author Mees Gelein
 */
public class ProgramSettingsFrame extends JFrame implements ICloseWindow{

	//The GUI components that allow the user to see and set some preferences
	private JComboBox languageBox;
	private JCheckBox updateCheckBox;
	private JComboBox LFBox;
	
	/**
	 * creates a new window that allows the user to set some Preferences like
	 * Language, Look and Feel and Autoupdate.
	 */
	public ProgramSettingsFrame() {
		Main.openFrames.add(this);
		Point point = Main.main.getLocation(); point.x += 100; point.y += 100;
		this.setLocation(point);
		this.setResizable(false);
		this.setSize(650,200);
		ImageIcon img = new ImageIcon(FileRegistry.APP_ICON);
		this.setIconImage(img.getImage());
		this.setTitle(LanguageRegistry.SETTINGS_WINDOW_TITLE);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(JOptionPane.showConfirmDialog(null, LanguageRegistry.UNSAVED_WARNING)==JOptionPane.YES_OPTION){
					closeWindow(false);
				}
			}
		});
		
		makeGUI();
		this.setVisible(true);
	}

	/**
	 * Builds the GUI, adds the listeners and populates
	 * the objects with the correct data
	 */
	private void makeGUI() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		add(contentPanel, BorderLayout.WEST);
		
		languageBox = new JComboBox();
		//TODO: how to automatically detect languages and add them here. Maybe using external files?
		languageBox.addItem("EN");
		languageBox.addItem("NL");
		languageBox.setSelectedItem(Preferences.language);
		JLabel languageLabel = new JLabel(LanguageRegistry.LANGUAGE_LABEL + ": ");
		JPanel languagePanel = new JPanel();
		languagePanel.add(languageLabel);
		languagePanel.add(languageBox);
		JPanel allLangPanel = new JPanel(new BorderLayout());
		allLangPanel.add(languagePanel, BorderLayout.WEST);
		contentPanel.add(allLangPanel);
		
		updateCheckBox = new JCheckBox(LanguageRegistry.CHECK_FOR_UPDATES_LABEL);
		updateCheckBox.setSelected(Preferences.checkForUpdates);
		JPanel updatePanel = new JPanel(new BorderLayout());
		updatePanel.add(updateCheckBox, BorderLayout.WEST);
		contentPanel.add(updatePanel);
		
		JButton saveButton = new JButton(new ImageIcon(FileRegistry.TICK_ICON_SMALL));
		saveButton.setToolTipText(LanguageRegistry.SAVE_AND_CLOSE);
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeWindow(true);
			}
		});
		saveButton.getActionMap().put("SaveAndExit", new AbstractAction("SaveAndExit"){
			public void actionPerformed(ActionEvent e){
				closeWindow(true);
			}
		});
		saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Registry.CTRL_MASK_CROSS_PLATFORM), "SaveAndExit");

		JButton discardButton = new JButton(new ImageIcon(FileRegistry.STOP_ICON_SMALL));
		discardButton.setToolTipText(LanguageRegistry.DISCARD_CHANGES);
		discardButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeWindow(false);
			}
		});
		discardButton.getActionMap().put("DiscardChanges", new AbstractAction("DiscardChanges"){
			public void actionPerformed(ActionEvent e){
				closeWindow(false);
			}
		});
		discardButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, Registry.CTRL_MASK_CROSS_PLATFORM), "DiscardChanges");

		JLabel LFLabel = new JLabel(LanguageRegistry.SET_LF_LABEL + ": ");
		JPanel LFPanel = new JPanel();
		LFBox = new JComboBox();
		LookAndFeelInfo[] lfs = Preferences.installedLF;
		for(int i = 0 ; i < lfs.length; i++){
			if(Preferences.isAllowedLF(lfs[i].getName())){
				LFBox.addItem(lfs[i].getName());
			}
		}
		LFPanel.add(LFLabel);
		LFPanel.add(LFBox);
		LFBox.setSelectedItem(UIManager.getLookAndFeel().getName());
		add(LFPanel, BorderLayout.EAST);
		
		JLabel noteLabel = new JLabel(LanguageRegistry.NEW_SETTINGS_WARNING);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		JPanel notePanel = new JPanel(new BorderLayout()); notePanel.add(noteLabel, BorderLayout.CENTER);
		noteLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		buttonPanel.add(saveButton);
		buttonPanel.add(discardButton);
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(buttonPanel, BorderLayout.NORTH);
		bottomPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		bottomPanel.add(noteLabel, BorderLayout.SOUTH);
		bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.gray));
		add(bottomPanel, BorderLayout.SOUTH);
		
		contentPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.gray));
		pack();
	}
	
	/**
	 * closes this window with a boolean parameter if changes should be saved.
	 * If false the changes will be lost once the window is closed
	 * @param saveChanges		should the changes be saved?
	 */
	public void closeWindow(boolean saveChanges) {
		if(saveChanges){
			if(!Preferences.language.equals((String)languageBox.getSelectedItem())){
				Preferences.language = (String) languageBox.getSelectedItem();
				//TODO: make program autorestart after switching languages
				//LanguageRegistry.changeLanguage(Preferences.language);
			}			
			Preferences.checkForUpdates = updateCheckBox.isSelected();
			Preferences.LF = ((String)LFBox.getSelectedItem());
		}
		boolean succes = Main.openFrames.remove(this);
		Debug.println("Removed frame from openList: " + succes, this);
		dispose();
	}

}
