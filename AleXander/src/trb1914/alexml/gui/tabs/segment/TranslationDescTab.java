package trb1914.alexml.gui.tabs.segment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import trb1914.alexml.Debug;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.xml.XMLTags;
/**
 * this used to be a separate tab, but it is now part of the translationTab.
 * All code is still in this class to keep the two apart. Now that we support 
 * links it is also a lot cleaner to keep them separate.
 * @author Mees Gelein
 */
public class TranslationDescTab extends JPanel{

	private Element segment;
	private Node authorNode;
	private Element transDescElement;

	public JTextField authorField;
	public JTextField langField;

	private final int TRANS_DESC_HEIGHT = 80;

	public JTextPane translationDescArea;
	/**
	 * creates the translation description tab using the provided segment as the source
	 * @param cSegment
	 */
	public TranslationDescTab(Element cSegment){
		segment = cSegment;
		makeGUI();
	}

	/**
	 * creates the GUI
	 */
	private void makeGUI() {
		JLabel authorLabel = new JLabel(LanguageRegistry.AUTHOR_LABEL + ": ");
		JLabel langLabel = new JLabel(LanguageRegistry.LANGUAGE_LABEL + ": ");
		langLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		Element transElement = (Element) segment.getElementsByTagName(XMLTags.TRANSLATION).item(0);
		transDescElement = (Element) transElement.getElementsByTagName(XMLTags.TRANSLATION_DESC).item(0);
		authorNode = transDescElement.getElementsByTagName(XMLTags.AUTHOR).item(0);
		String authorString = authorNode.getTextContent();
		authorField = new JTextField(authorString, Registry.CHAR_AMOUNT);
		langField = new JTextField(transElement.getAttribute(XMLTags.LANG), Registry.CHAR_AMOUNT / 2);
		JPanel authorPanel = new JPanel();
		authorPanel.add(authorLabel);
		authorPanel.add(authorField);
		authorPanel.add(langLabel);
		authorPanel.add(langField);


		JLabel transDescLabel = new JLabel(LanguageRegistry.TRANSLATION_DESC_TITLE + ":");
		translationDescArea = new JTextPane();
		translationDescArea.setPreferredSize(new Dimension(0, TRANS_DESC_HEIGHT));
		String translationDesc = transElement.getElementsByTagName(XMLTags.TRANSLATION_DESC).item(0).getTextContent();
		translationDesc = translationDesc.replace(authorField.getText(),"");
		translationDescArea.setText(translationDesc);
		//add the mouse motion listener that checks if mouse is currently hovering a underlined piece of text
		translationDescArea.addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent e) {	}
			public void mouseMoved(MouseEvent e) {
				int charIndex = translationDescArea.viewToModel(e.getPoint());
				javax.swing.text.Element element = translationDescArea.getStyledDocument().getCharacterElement(charIndex);
				if(element.getAttributes().containsAttribute(StyleConstants.Underline, true)){
					translationDescArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else{
					translationDescArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
				}
			}
		});
		JScrollPane transDescScroll = new JScrollPane(translationDescArea);
		JPanel tdPanel = new JPanel();
		tdPanel.setLayout(new BorderLayout());
		tdPanel.add(transDescLabel, BorderLayout.NORTH);
		tdPanel.add(transDescScroll);

		//set the default font
		Font font = (FileRegistry.getNormalFont());
		translationDescArea.setFont(font);
	
		//add the link button
		JPanel buttonPanel = new JPanel();
		JButton linkButton = new JButton(new StyledEditorKit.UnderlineAction(){
			public void actionPerformed(ActionEvent e){
				super.actionPerformed(e);
			}
		});
		linkButton.setText("");
		linkButton.setIcon(new ImageIcon(FileRegistry.LINK_ICON_SMALL));
		
		buttonPanel.add(linkButton);
		buttonPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.gray));

		//add it all to the panel
		setLayout(new BorderLayout());
		add(authorPanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.SOUTH);
		add(tdPanel);
	}

	/**
	 * sets the translationDescArea to the provided string
	 */
	public void setTransDescField(String s){
		translationDescArea.setText(s);
		codeLinks();
	}

	/**
	 * gets the authorfield text
	 * @return
	 */
	public String getAuthorFieldText(){
		return authorField.getText();
	}
	
	/**
	 * turns links into blue underline
	 */
	private void codeLinks(){
		try{
			SimpleAttributeSet boldStyle = new SimpleAttributeSet();
			boldStyle.addAttribute(StyleConstants.Underline, true);
			//boldStyle.addAttribute(StyleConstants.Foreground, Color.blue);
			StyledDocument doc = translationDescArea.getStyledDocument();
			doc.setCharacterAttributes(0, doc.getLength(), new SimpleAttributeSet(), true);
			String s = doc.getText(0, doc.getLength());
			int startIndex = s.indexOf("{");
			while(startIndex > -1){
				int endIndex = s.indexOf("}", startIndex);
				doc.setCharacterAttributes(startIndex, (endIndex - startIndex), boldStyle, true);
				doc.remove(startIndex, 1);
				doc.remove(endIndex - 1, 1);
				s = doc.getText(0, doc.getLength());
				startIndex = s.indexOf("{", endIndex);
			}
		}catch(Exception e){
			Debug.println("Decoding not working with links", this);
		}
	}

	/**
	 * returns the translationDescAreaText
	 * @return
	 */
	public String getTranslationDescAreaText(){
		StyledDocument doc = translationDescArea.getStyledDocument();
		boolean start = false;
		for(int i = 0; i < doc.getLength(); i++){
			javax.swing.text.Element e = doc.getCharacterElement(i);
			if(e.getAttributes().containsAttribute(StyleConstants.Underline, true)){
				if(!start){//start of a bold piece
					start = true;
					try {
						doc.insertString(i, "{", new SimpleAttributeSet());
					} catch (BadLocationException e1) {
						Debug.println("Bad location to insert String @ TranslationTab", this);
					}
				}
			}else if(start){//the end of a bold piece
				start = false;
				try {
					doc.insertString(i, "}", new SimpleAttributeSet());
				} catch (BadLocationException e1) {
					Debug.println("Bad location to insert String @ TranslationTab", this);
				}
			}

		}
		String s = translationDescArea.getText();
		if(start){//the end of the string has been reached but no non-bold char has been found. So bold until the end 
			s += "}";
		}
		
		return s;
	}
}
