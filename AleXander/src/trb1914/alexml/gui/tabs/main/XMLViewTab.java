package trb1914.alexml.gui.tabs.main;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import trb1914.alexml.Debug;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.gui.CodeSettingsEditor;
import trb1914.alexml.xml.XMLParser;

/**
 * the xml view tab as a separate class
 * @author Mees Gelein
 *
 */
public class XMLViewTab extends JPanel {
	
	///The JTextArea that actually holds all the text
	private JTextPane xmlTextArea;
	///The size of the font (default)
	private float FONT_SIZE = 12f;
	
	/*
	 * creates a new XMLViewTab
	 */
	public XMLViewTab() {
		//make textArea
		xmlTextArea = new JTextPane();
		xmlTextArea.setFont(FileRegistry.COURIER_FONT.deriveFont(FONT_SIZE));
		xmlTextArea.setEditable(false);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(xmlTextArea);
		//make the scrollPanel that holds it
		JScrollPane xmlScrollPane = new JScrollPane(contentPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		xmlScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		//add it all to this panel
		setLayout(new BorderLayout());
		add(xmlScrollPane);
	}
	
	/**
	 * does syntax hightlighting. Very basic!
	 */
	private void colorCode(){
		new Thread(new Runnable(){
			public void run(){
				try {
					String s = xmlTextArea.getDocument().getText(0, xmlTextArea.getDocument().getLength());
					SimpleAttributeSet basicSet = new SimpleAttributeSet();
					StyleConstants.setForeground(basicSet, CodeSettingsEditor.mainColor);
					StyledDocument styledDoc = xmlTextArea.getStyledDocument();
					styledDoc.setCharacterAttributes(0, xmlTextArea.getDocument().getLength(), basicSet, true);
					
					int startIndex = s.indexOf("<");
					int endIndex; int length;
					SimpleAttributeSet tagSet = new SimpleAttributeSet();
					StyleConstants.setForeground(tagSet, CodeSettingsEditor.tagColor);
					
					while(startIndex >= 0){
						endIndex = s.indexOf(">", startIndex);
						length = endIndex - startIndex + 1;
						styledDoc.setCharacterAttributes(startIndex, length, tagSet, true);
						searchAttributes(s.substring(startIndex, endIndex + 1), startIndex);
						startIndex = s.indexOf("<", endIndex);
					}

					SimpleAttributeSet attributeSet = new SimpleAttributeSet();
					StyleConstants.setForeground(attributeSet, CodeSettingsEditor.attributeValueColor);
					startIndex = s.indexOf("\"");
					while(startIndex >= 0){
						endIndex = s.indexOf("\"", startIndex + 1);
						length = endIndex - startIndex + 1;
						styledDoc.setCharacterAttributes(startIndex, length, attributeSet, true);
						startIndex = s.indexOf("\"", endIndex + 1);
					}
					xmlTextArea.setStyledDocument(styledDoc);
				} catch (Exception e) {
					Debug.println("Error with color coding", this);
				}
				
			}
		}).start();
	}
	
	/**
	 * searches if the provided string contains any spaces (attributes of an xml element)
	 * @param s
	 * @param offset
	 */
	private void searchAttributes(String s, int offset) {
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, CodeSettingsEditor.attributeKeyColor);
		int startIndex = s.indexOf(" ");
		int endIndex;
		int length;
		while(startIndex >= 0){
			endIndex = s.indexOf("=", startIndex);
			length = endIndex - startIndex;
			xmlTextArea.getStyledDocument().setCharacterAttributes(startIndex + offset, length, set, true);
			startIndex = s.indexOf("\" ", endIndex);
		}
	}

	/**
	 * returns the xml-view textArea. This contains the data that in the end will 
	 * get saved
	 * @return		the xml-view JTextArea
	 */
	public JTextPane getXMLTextArea(){
		return xmlTextArea;
	}
	
	/**
	 * updates the content of this panel
	 */
	public void update(){
		int caretPos = xmlTextArea.getCaretPosition();
		xmlTextArea.setText(XMLParser.getString());
		colorCode();
		if(caretPos < xmlTextArea.getText().length()) xmlTextArea.setCaretPosition(caretPos);
		else xmlTextArea.setCaretPosition(0);
	}

}
