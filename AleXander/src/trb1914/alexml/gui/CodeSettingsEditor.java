package trb1914.alexml.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.alexml.Preferences;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.gui.util.ColorTile;
import trb1914.alexml.interfaces.ICloseWindow;
/**
 * This class allows the user to set the color code highlighting colors
 * @author Mees Gelein
 */
public class CodeSettingsEditor extends JFrame implements ICloseWindow{

	//All the colors currently being used by the code hightlighting
	public static Color mainColor = CodeSettingsEditor.getColor(Preferences.mainColor);
	public static Color attributeKeyColor = CodeSettingsEditor.getColor(Preferences.attributeKeyColor);
	public static Color attributeValueColor = CodeSettingsEditor.getColor(Preferences.attributeValueColor);
	public static Color tagColor = CodeSettingsEditor.getColor(Preferences.tagColor);

	//the textfields that contain the color codes
	private JTextField mainField;
	private JTextField attributeKeyField;
	private JTextField attributeValueField;
	private JTextField tagField;

	//the color tiles that show the color that is coded by the textfield
	private ColorTile mainTile = new ColorTile(mainColor);
	private ColorTile tagTile = new ColorTile(tagColor);
	private ColorTile attributeKeyTile = new ColorTile(attributeKeyColor);
	private ColorTile attributeValueTile = new ColorTile(attributeValueColor);

	/**
	 * creates a new codeSettingsEditor instance
	 */
	public CodeSettingsEditor(){
		Main.openFrames.add(this);
		setTitle(LanguageRegistry.MENU_CODE_HIGHLIGHT_SETTINGS);
		ImageIcon img = new ImageIcon(FileRegistry.APP_ICON);
		setIconImage(img.getImage());
		makeGUI();
		pack();
		setVisible(true);
	}

	/**
	 * sets all the colors back to the defaults that were loaded at startup
	 */
	public static void reloadPrefs(){
		mainColor = getColor(Preferences.mainColor);
		tagColor = getColor(Preferences.tagColor);
		attributeValueColor = getColor(Preferences.attributeValueColor);
		attributeKeyColor = getColor(Preferences.attributeKeyColor);
	}

	/**
	 * builds the GUI as usual
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		JLabel mainLabel = new JLabel(LanguageRegistry.MAIN_COLOR_LABEL + ": ");
		JLabel tagLabel = new JLabel(LanguageRegistry.TAG_COLOR_LABEL + ": ");
		JLabel attributeKeyLabel = new JLabel(LanguageRegistry.ATTRIBUTE_KEY_COLOR_LABEL + ": ");
		JLabel attributeValueLabel = new JLabel(LanguageRegistry.ATTRIBUTE_VALUE_COLOR_LABEL + ": ");

		mainField = new JTextField(getColorString(mainColor, false), Registry.CHAR_AMOUNT_SMALL);
		final UndoManager mainUndo = new UndoManager();
		mainField.getDocument().addUndoableEditListener(mainUndo);
		tagField = new JTextField(getColorString(tagColor, false), Registry.CHAR_AMOUNT_SMALL);
		final UndoManager tagUndo = new UndoManager();
		tagField.getDocument().addUndoableEditListener(tagUndo);
		attributeKeyField = new JTextField(getColorString(attributeKeyColor, false), Registry.CHAR_AMOUNT_SMALL);
		final UndoManager attributeKeyUndo = new UndoManager();
		attributeKeyField.getDocument().addUndoableEditListener(attributeKeyUndo);
		attributeValueField = new JTextField(getColorString(attributeValueColor, false), Registry.CHAR_AMOUNT_SMALL);
		final UndoManager attributeValueUndo = new UndoManager();
		attributeKeyField.getDocument().addUndoableEditListener(attributeValueUndo);	

		mainField.getActionMap().put("Undo", new AbstractAction("Undo"){
			public void actionPerformed(ActionEvent e){
				if(mainUndo.canUndo()){
					mainUndo.undo();
				}
			}
		});
		mainField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Registry.CTRL_MASK_CROSS_PLATFORM), "Undo");
		mainField.getActionMap().put("Redo", new AbstractAction("Redo"){
			public void actionPerformed(ActionEvent e){
				if(mainUndo.canRedo()){
					mainUndo.redo();
				}
			}
		});
		mainField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Registry.CTRL_MASK_CROSS_PLATFORM), "Redo");
		mainField.getActionMap().put("Undo", new AbstractAction("Undo"){
			public void actionPerformed(ActionEvent e){
				if(mainUndo.canUndo()){
					mainUndo.undo();
				}
			}
		});
		tagField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Registry.CTRL_MASK_CROSS_PLATFORM), "Undo");
		tagField.getActionMap().put("Redo", new AbstractAction("Redo"){
			public void actionPerformed(ActionEvent e){
				if(tagUndo.canRedo()){
					tagUndo.redo();
				}
			}
		});
		tagField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Registry.CTRL_MASK_CROSS_PLATFORM), "Redo");
		tagField.getActionMap().put("Undo", new AbstractAction("Undo"){
			public void actionPerformed(ActionEvent e){
				if(attributeKeyUndo.canUndo()){
					attributeKeyUndo.undo();
				}
			}
		});
		attributeKeyField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Registry.CTRL_MASK_CROSS_PLATFORM), "Undo");
		attributeKeyField.getActionMap().put("Redo", new AbstractAction("Redo"){
			public void actionPerformed(ActionEvent e){
				if(attributeKeyUndo.canRedo()){
					attributeKeyUndo.redo();
				}
			}
		});
		attributeKeyField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Registry.CTRL_MASK_CROSS_PLATFORM), "Redo");

		attributeValueField.getActionMap().put("Undo", new AbstractAction("Undo"){
			public void actionPerformed(ActionEvent e){
				if(attributeValueUndo.canUndo()){
					attributeValueUndo.undo();
				}
			}
		});
		attributeValueField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Registry.CTRL_MASK_CROSS_PLATFORM), "Undo");
		attributeValueField.getActionMap().put("Redo", new AbstractAction("Redo"){
			public void actionPerformed(ActionEvent e){
				if(attributeValueUndo.canRedo()){
					attributeValueUndo.redo();
				}
			}
		});
		attributeValueField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Registry.CTRL_MASK_CROSS_PLATFORM), "Redo");

		JPanel contentPanel = new JPanel(new GridBagLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout());

		addLabelAndField(contentPanel, mainLabel, mainField, mainTile, 0);
		addLabelAndField(contentPanel, tagLabel, tagField, tagTile, 1);
		addLabelAndField(contentPanel, attributeKeyLabel, attributeKeyField, attributeKeyTile, 2);
		addLabelAndField(contentPanel, attributeValueLabel, attributeValueField, attributeValueTile, 3);

		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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

		buttonPanel.add(saveButton);
		buttonPanel.add(discardButton);

		add(contentPanel);
		add(buttonPanel, BorderLayout.SOUTH);

		addDocumentListeners();
	}

	/**
	 * adds all the documentListeners to update the colorTiles
	 */
	private void addDocumentListeners(){
		mainField.getDocument().addDocumentListener(new DocumentListener(){
			public void insertUpdate(DocumentEvent e){updateTile();}
			public void changedUpdate(DocumentEvent e){updateTile();}
			public void removeUpdate(DocumentEvent e){updateTile();}

			private void updateTile(){
				mainTile.setColor(getColor(mainField));
			}
		});
		tagField.getDocument().addDocumentListener(new DocumentListener(){
			public void insertUpdate(DocumentEvent e){updateTile();}
			public void changedUpdate(DocumentEvent e){updateTile();}
			public void removeUpdate(DocumentEvent e){updateTile();}

			private void updateTile(){
				tagTile.setColor(getColor(tagField));
			}
		});
		attributeKeyField.getDocument().addDocumentListener(new DocumentListener(){
			public void insertUpdate(DocumentEvent e){updateTile();}
			public void changedUpdate(DocumentEvent e){updateTile();}
			public void removeUpdate(DocumentEvent e){updateTile();}

			private void updateTile(){
				attributeKeyTile.setColor(getColor(attributeKeyField));
			}
		});
		attributeValueField.getDocument().addDocumentListener(new DocumentListener(){
			public void insertUpdate(DocumentEvent e){updateTile();}
			public void changedUpdate(DocumentEvent e){updateTile();}
			public void removeUpdate(DocumentEvent e){updateTile();}

			private void updateTile(){
				attributeValueTile.setColor(getColor(attributeValueField));
			}
		});
	}

	/**
	 * adds the specified label and field to the provided parent using GridBagLayout
	 * @param parent
	 * @param label
	 * @param field
	 */
	private void addLabelAndField(JPanel parent,JLabel label,JTextField field, ColorTile tile, int row){
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,0,0,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = row;
		c.gridwidth = 1;
		parent.add(label, c);
		c.gridx = 1;
		c.gridy = row;
		c.gridwidth = 1;
		parent.add(tile, c);
		c.gridx = 2;
		c.insets = new Insets(2,5,0,0);
		c.gridy = row;
		c.gridwidth = 2;
		c.weightx = 1.0f;
		parent.add(field, c);
	}

	/**
	 * turns a color into a String
	 * @param c		the color to change
	 * @param withThing		include a #
	 * @return
	 */
	public static String getColorString(Color c, boolean withThing){
		String s = Integer.toHexString(c.getRGB());
		s = s.toUpperCase();
		if(withThing){
			s = s.replaceFirst("FF", "#");
		}else{
			s = s.replaceFirst("FF", "");
		}
		return s;
	}

	/**
	 * parses a color from the provided JTextField
	 * @param t
	 * @return
	 */
	public static Color getColor(JTextField t){
		return getColor(t.getText());
	}

	/**
	 * parse a color from the provided String
	 * @param s
	 * @return
	 */
	public static Color getColor(String s){
		s = s.toUpperCase();
		s = s.replaceAll(" ", "");
		switch(s){
		case "RED": return Color.red;
		case "BLUE": return Color.blue;
		case "GREEN": return Color.green;
		case "BLACK": return Color.black;
		case "PINK": return Color.pink;
		case "GRAY": return Color.gray;
		case "WHITE": return Color.white;
		case "PURPLE": return new Color(0x9900FF);
		case "YELLOW": return Color.yellow;
		case "ORANGE": return Color.orange;
		case "CYAN": return Color.cyan;
		case "DARKGRAY": return Color.darkGray;
		case "LIGHTGRAY": return Color.lightGray;
		case "MAGENTA": return Color.magenta;
		}
		try{
			s = s.replace("0x", "#");
			s = s.replace("#", "");
			s = s.toLowerCase();
			int colorInt = 0;
			if(s.length() > 0){	
				colorInt = Integer.valueOf(s, 16);
			}
			if(colorInt > 0xFFFFFF){
				colorInt = 0xFFFFFF;
			}
			return new Color(colorInt);
		}catch(Exception e){
			return new Color(0);
		}
	}

	/**
	 * closes that window and depending on the provided boolean
	 * saves the changes
	 */
	public void closeWindow(boolean saveChanges){
		if(saveChanges){
			mainColor = getColor(mainField);
			tagColor = getColor(tagField);
			attributeKeyColor = getColor(attributeKeyField);
			attributeValueColor = getColor(attributeValueField);
		}
		boolean succes = Main.openFrames.remove(this);
		Debug.println("Removed frame from openList: " + succes, this);
		dispose();
	}
}
