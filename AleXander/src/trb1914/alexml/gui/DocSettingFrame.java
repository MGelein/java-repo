package trb1914.alexml.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.gui.tabs.main.TabHolder;
import trb1914.alexml.interfaces.ICloseWindow;
import trb1914.alexml.xml.XMLParser;
import trb1914.alexml.xml.XMLTags;
/**
 * This class is a separate frame that allows the user to edit the
 * "document settings", things like author, title, place etc.
 * @author Mees Gelein
 *
 */
public class DocSettingFrame extends JFrame implements ICloseWindow{

	///the content panels
	private JTabbedPane tabbedPane;
	
	///the textFields that will be used to pull data from
	private JTextField authorTextField;
	private JTextField titleTextField;
	private JTextArea licenseTextArea;
	private JTextField locusFromTextField;
	private JTextField locusToTextField;
	private JTextField textIDTextField;
	private JTextField dateTextField;
	private JTextField notAfterField;
	private JTextField notBeforeField;
	private JTextField origTextField;
	private JTextArea keywordsArea;
	private JScrollPane licenseScrollPane;
	private JScrollPane keywordScrollPane;
	
	///the elements of the current document. For easy acces
	private Element titleStmt;
	private Element locus;
	private Element text;

	///some nodes of the current document. Cast to Elements was not necessary
	///because these Nodes have no attributes
	private Node availability;
	private Node author;
	private Node title;
	private Node origDate;
	private Node origPlace;

	/**
	 * creates a separate frame to edit the document settings
	 */
	public DocSettingFrame(){
		Main.openFrames.add(this);
		Point point = Main.main.getLocation(); point.x += 40; point.y += 30;
		this.setLocation(point);
		this.setResizable(false);
		this.setTitle(LanguageRegistry.DOC_SETTING_WINDOW_TITLE);
		ImageIcon img = new ImageIcon(FileRegistry.APP_ICON);
		this.setIconImage(img.getImage());
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(JOptionPane.showConfirmDialog(null, LanguageRegistry.UNSAVED_WARNING)==JOptionPane.YES_OPTION){
					closeWindow(false);
				}
			}
		});
		
		makeGUI();
		//pack to make it the right size
		pack();
		this.setVisible(true);
	}
	/**
	 * creates the content of the window
	 */
	private void makeGUI() {
		locus = (Element) XMLParser.getNodeAt("//" + XMLTags.LOCUS);
		text = (Element) XMLParser.getNodeAt("//" + XMLTags.TEXT);
		titleStmt = (Element) XMLParser.getNodeAt("//"+ XMLTags.TITLE_STMT);
		title = titleStmt.getElementsByTagName(XMLTags.TITLE).item(0);
		if(title == null){
			title = XMLParser.xmlDocument.createElement(XMLTags.TITLE);
			titleStmt.appendChild(title);
		}
		author = titleStmt.getElementsByTagName(XMLTags.AUTHOR).item(0);
		if(author == null){
			author = XMLParser.xmlDocument.createElement(XMLTags.AUTHOR);
			titleStmt.appendChild(author);
		}
		origDate = titleStmt.getElementsByTagName(XMLTags.ORIG_DATE).item(0);
		if(origDate == null){
			origDate = XMLParser.xmlDocument.createElement(XMLTags.ORIG_DATE);
			titleStmt.appendChild(origDate);
		}
		origPlace = titleStmt.getElementsByTagName(XMLTags.ORIG_PLACE).item(0);
		if(origPlace == null){
			origPlace = XMLParser.xmlDocument.createElement(XMLTags.ORIG_PLACE);
			titleStmt.appendChild(origPlace);
		}
		
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

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(saveButton);
		buttonPanel.add(discardButton);
		
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));
		
		titleTextField = new JTextField(title.getTextContent());
		
		authorTextField = new JTextField(author.getTextContent());
		
		dateTextField = new JTextField(origDate.getTextContent());
		
		Element origDateElement = (Element) origDate;
		notBeforeField = new JTextField(origDateElement.getAttribute(XMLTags.NOT_BEFORE));
		notAfterField = new JTextField(origDateElement.getAttribute(XMLTags.NOT_AFTER));
		
		origTextField = new JTextField(origPlace.getTextContent());
		
		availability = XMLParser.getNodeAt("//" + XMLTags.AVAILABILITY);
		
		licenseTextArea = new JTextArea(availability.getTextContent(), 4, 15);
		licenseTextArea.setLineWrap(true); licenseTextArea.setWrapStyleWord(true);
		licenseTextArea.setFont(authorTextField.getFont());
		licenseScrollPane = new JScrollPane(licenseTextArea);
		
		locusFromTextField = new JTextField(locus.getAttribute(XMLTags.FROM));
		locusToTextField = new JTextField(locus.getAttribute(XMLTags.TO));
		textIDTextField = new JTextField(text.getAttribute("n"));
		
		keywordsArea = new JTextArea(10,40);
		keywordsArea.setFont(authorTextField.getFont());
		keywordsArea.setLineWrap(true); keywordsArea.setWrapStyleWord(true);
		NodeList nodeTerms = XMLParser.getNodeListOf("//" + XMLTags.TEXT + "/" + XMLTags.KEYWORDS + "/" + XMLTags.TERM); 
		String [] keywords = new String[nodeTerms.getLength()];
		for(int i = 0; i<nodeTerms.getLength();i++){
			keywords[i] = nodeTerms.item(i).getTextContent();
		}
		int i = 0;
		if(keywords.length > 0){
			for(i=0;i<keywords.length;i++){
				keywordsArea.append(keywords[i] + ", ");
			}
			keywordsArea.setText(keywordsArea.getText().substring(0, keywordsArea.getText().length() - 2));
		}
		keywordScrollPane = new JScrollPane(keywordsArea);
		
		JLabel authorLabel = new JLabel(LanguageRegistry.AUTHOR_LABEL + ":");
		JLabel licenseLabel = new JLabel(LanguageRegistry.LICENSE_LABEL + ":");
		JLabel locusFromLabel = new JLabel(LanguageRegistry.LOCUS_FROM_LABEL + ":");
		JLabel locusToLabel = new JLabel(LanguageRegistry.LOCUS_TO_LABEL + ":");
		JLabel textIDLabel = new JLabel(LanguageRegistry.TEXT_ID_LABEL + ":");
		JLabel keywordsLabel = new JLabel(LanguageRegistry.KEYWORDS_LABEL + ":");
		JLabel titleLabel = new JLabel(LanguageRegistry.TITLE_LABEL + ":");
		JLabel dateLabel = new JLabel(LanguageRegistry.DATE_LABEL + ":");
		JLabel notBeforeLabel = new JLabel(LanguageRegistry.DATE_NOT_BEFORE_LABEL + ":");
		JLabel notAfterLabel = new JLabel(LanguageRegistry.DATE_NOT_AFTER_LABEL + ":");
		JLabel placeLabel = new JLabel(LanguageRegistry.PLACE_LABEL + ": ");
		
		JPanel contentPanel = new JPanel(new GridBagLayout());
		addLabelAndField(contentPanel, authorLabel, authorTextField, 0);
		addLabelAndField(contentPanel, titleLabel, titleTextField, 1);
		addLabelAndField(contentPanel, textIDLabel, textIDTextField, 2);
		addLabelAndField(contentPanel, locusFromLabel, locusFromTextField, 3);
		addLabelAndField(contentPanel, locusToLabel, locusToTextField, 4);
		addLabelAndField(contentPanel, dateLabel, dateTextField, 5);
		addLabelAndField(contentPanel, notBeforeLabel, notBeforeField, 6);
		addLabelAndField(contentPanel, notAfterLabel, notAfterField, 7);
		addLabelAndField(contentPanel, placeLabel, origTextField, 8);
		
		JPanel keywordsPanel = new JPanel();
		keywordsPanel.setLayout(new BorderLayout());
		keywordsPanel.add(keywordsLabel, BorderLayout.PAGE_START); keywordsPanel.add(keywordScrollPane);
		keywordsArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		keywordsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		JPanel licensePanel = new JPanel();
		licensePanel.setLayout(new BorderLayout());
		licensePanel.add(licenseLabel, BorderLayout.PAGE_START); licensePanel.add(licenseScrollPane);
		licensePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(LanguageRegistry.GENERAL_TAB_TITLE, contentPanel);
		tabbedPane.addTab(LanguageRegistry.LICENSE_TAB_TITLE, licensePanel);
		tabbedPane.addTab(LanguageRegistry.KEYWORDS_TAB_TITLE, keywordsPanel);
		
		add(tabbedPane, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.SOUTH);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
	}
	
	/**
	 * adds the specified label and field to the provided parent using GridBagLayout
	 * @param parent
	 * @param label
	 * @param field
	 */
	private void addLabelAndField(JPanel parent,JLabel label,JTextField field, int row){
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,0,0,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = row;
		c.gridwidth = 1;
		parent.add(label, c);
		c.gridx = 1;
		c.insets = new Insets(2,5,0,0);
		c.gridy = row;
		c.gridwidth = 2;
		c.weightx = 1.0f;
		parent.add(field, c);
	}
	
	/**
	 * called after close is pressed, closes the window
	 */
	public void closeWindow(boolean saveChanges) {
		if(saveChanges){ 
			author.setTextContent(authorTextField.getText());
			availability.setTextContent(licenseTextArea.getText());
			locus.setAttribute(XMLTags.FROM, locusFromTextField.getText());
			locus.setAttribute(XMLTags.TO, locusToTextField.getText());
			locus.setTextContent(locusFromTextField.getText() + " - " + locusToTextField.getText());
			text.setAttribute("n", textIDTextField.getText());
			title.setTextContent(titleTextField.getText());
			origDate.setTextContent(dateTextField.getText());
			Element origDateElement = (Element) origDate;
			origDateElement.setAttribute(XMLTags.NOT_BEFORE, notBeforeField.getText());
			origDateElement.setAttribute(XMLTags.NOT_AFTER, notAfterField.getText());
			origPlace.setTextContent(origTextField.getText());
			String[] keywords = keywordsArea.getText().split(",");
			for(int i = 0; i < keywords.length;i++){
				keywords[i] = keywords[i].replace(" ", "");
			}
			Node keywordElement =XMLParser.getNodeAt("//" + XMLTags.TEXT + "/" + XMLTags.KEYWORDS);
			
			Element keyElement = XMLParser.xmlDocument.createElement(XMLTags.KEYWORDS);
			if(keywords.length > 0){
				for(int i = 0; i < keywords.length;i++){
					Element element = XMLParser.xmlDocument.createElement(XMLTags.TERM);
					element.setTextContent(keywords[i]);
					keyElement.appendChild(element);
				}
				try{
					text.replaceChild(keyElement, keywordElement);
				}catch(Exception e){
					text.appendChild(keyElement);
				}
			}
		}
		boolean succes = Main.openFrames.remove(this);
		Debug.println("Removed frame from openList: " + succes, this);
		TabHolder.updateView();
		dispose();
	}

}
