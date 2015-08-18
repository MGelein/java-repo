package trb1914.alexml.gui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.xml.XMLTags;
/**
 * Class only used to render the main List-view of segments.
 * Previously this was just a list but now the list displays a preview
 * of the main text and the translation
 * @author Mees Gelein.
 *
 */
public class SegmentListRenderer extends JPanel implements ListCellRenderer<Element>{

	/**The font used to render stuff*/
	private Font normalFont;
	/**The color of the font when the cell is selected*/
	private Color selectionForeGround = new Color(0x000000);
	/**The color of the background when the cell is selected*/
	private Color selectionBackGround = new Color(0xFFFFEE);
	/**The color of the font when the cell is not selected*/
	private Color foreGround = new Color(0x222222);
	/**The color of the background when the cell is not selected*/
	private Color backGround = new Color(0xFFFFCC);
	/**The amount of characters for both the translation and main text preview*/
	private final int CHAR_AMT = 100;
	/**The amount of characters for keyword preview*/
	private final int KEY_AMT = 50;
	
	/**
	 * Creates a new SegmentListRenderer and the font to render most of it with
	 */
	public SegmentListRenderer(Font f){
		normalFont = f;
		setLayout(new GridLayout(1, 3));
		setOpaque(true);
	}
	/**
	 * Draws the cellComponent
	 */
	@Override
	public Component getListCellRendererComponent(
			JList<? extends Element> list, Element element, int index,
			boolean isSelected, boolean cellHasFocus) {
		this.removeAll();
		
		//main 3 panels
		JPanel namePanel = new JPanel(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel transPanel = new JPanel(new BorderLayout());
		add(namePanel); add(mainPanel); add(transPanel);

		//first panel
		JLabel nameLabel = new JLabel();
		nameLabel.setFont(normalFont);
		JTextArea keywordArea = new JTextArea(); 
		keywordArea.setFont(FileRegistry.getNormalFont(normalFont.getSize() - 4));
		namePanel.add(nameLabel, BorderLayout.NORTH);
		namePanel.add(keywordArea, BorderLayout.CENTER);
		keywordArea.setLineWrap(true);
		keywordArea.setWrapStyleWord(true);
		
		//second (middle) panel
		JLabel mainLabel = new JLabel(); 
		mainLabel.setFont(nameLabel.getFont());
		JTextArea mainArea = new JTextArea(); 
		mainArea.setFont(keywordArea.getFont());
		mainPanel.add(mainLabel, BorderLayout.NORTH);
		mainPanel.add(mainArea, BorderLayout.CENTER);
		
		//third (translation) panel
		JLabel transLabel = new JLabel(); 
		transLabel.setFont(nameLabel.getFont());
		JTextArea transArea = new JTextArea(); 
		transArea.setFont(keywordArea.getFont());
		transPanel.add(transLabel, BorderLayout.PAGE_START);
		transPanel.add(transArea, BorderLayout.CENTER);
		
		//Color stuffs
		Color foreGroundColor = foreGround;
		Color backGroundColor = backGround;
		Border cellBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
		if(isSelected){
			foreGroundColor = selectionForeGround;
			backGroundColor = selectionBackGround;
			cellBorder = BorderFactory.createLineBorder(Color.gray);
		}
		namePanel.setBackground(backGroundColor);
		nameLabel.setBackground(backGroundColor);
		keywordArea.setBackground(backGroundColor);
		namePanel.setForeground(foreGroundColor);
		nameLabel.setForeground(foreGroundColor);
		keywordArea.setForeground(foreGroundColor);
		mainPanel.setBackground(backGroundColor);
		mainLabel.setBackground(backGroundColor);
		mainArea.setBackground(backGroundColor);
		mainPanel.setForeground(foreGroundColor);
		mainLabel.setForeground(foreGroundColor);
		mainArea.setForeground(foreGroundColor);
		transPanel.setBackground(backGroundColor);
		transLabel.setBackground(backGroundColor);
		transArea.setBackground(backGroundColor);
		transPanel.setForeground(foreGroundColor);
		transLabel.setForeground(foreGroundColor);
		transArea.setForeground(foreGroundColor);
		setBorder(cellBorder);

		//DATA POPULATION
		
		//FIRST PANEL
		nameLabel.setText("Segment " + element.getAttribute("n"));
		
		String keywordText = "\n" +  LanguageRegistry.KEYWORDS_LABEL + ": ";
		Element keywordsElement = ((Element) element.getElementsByTagName(XMLTags.KEYWORDS).item(0));
		if(keywordsElement != null){
			NodeList termList = keywordsElement.getElementsByTagName(XMLTags.TERM);
			boolean filled = false;
			for(int i = 0; i < termList.getLength(); i++){
				String toAdd = termList.item(i).getTextContent() + ", ";
				if(keywordText.length() + toAdd.length() < KEY_AMT){
					keywordText += toAdd;
				}else{
					filled = true;
					break;
				}
			}
			int commaIndex = keywordText.lastIndexOf(",");
			keywordText = keywordText.substring(0, commaIndex);
			if (filled) keywordText += "...";
		}
		keywordArea.setText(keywordText);
		
		//SECOND PANEL
		mainLabel.setText(LanguageRegistry.MAIN_TEXT_TITLE);
		Element displayElement = (Element) element.getElementsByTagName(XMLTags.DISPLAYSEGMENT).item(0);
		String displayTextContent = displayElement.getTextContent();
		NodeList noteList = displayElement.getElementsByTagName(XMLTags.NOTE);
		int i, max = noteList.getLength(); String toReplace;
		for(i = 0; i < max; i++){
			toReplace = noteList.item(i).getTextContent();
			displayTextContent = displayTextContent.replaceAll(toReplace, LanguageRegistry.NOTE_STRING);
		}
		mainArea.setLineWrap(true);
		mainArea.setWrapStyleWord(true);
		if(displayTextContent.length() > CHAR_AMT){
			displayTextContent = displayTextContent.substring(0, CHAR_AMT);
			displayTextContent += "...";
		}
		mainArea.setText(displayTextContent);
		
		//THIRD PANEL
		Element transElement = (Element) element.getElementsByTagName(XMLTags.TRANSLATION).item(0);
		String transTextContent = transElement.getTextContent();
		noteList = transElement.getElementsByTagName(XMLTags.NOTE);
		max = noteList.getLength();
		for(i = 0; i < max; i++){
			transTextContent = transTextContent.replaceAll(noteList.item(i).getTextContent(), "");
		}
		String transInfo = " ";
		transArea.setLineWrap(true);
		transArea.setWrapStyleWord(true);
		Element transDescElement = (Element) transElement.getElementsByTagName(XMLTags.TRANSLATION_DESC).item(0);
		if(transDescElement != null){ 
			transTextContent = transTextContent.replaceFirst(transDescElement.getTextContent(), "");
			Node authorNode = transDescElement.getElementsByTagName(XMLTags.AUTHOR).item(0);
			String lang = transElement.getAttribute(XMLTags.LANG);
			if(lang.length() != 0){
				lang += "#";
			}
			transInfo += lang;
			if(authorNode != null) {
				transInfo = transInfo.replaceAll("#", " - ");
				transInfo += authorNode.getTextContent();
			}
		}
		if(transTextContent.length() > CHAR_AMT){
			transTextContent = transTextContent.substring(0, CHAR_AMT);
			transTextContent += "...";
		}
		transArea.setText(transTextContent);		
		
		transInfo = transInfo.replaceAll("#", "");		
		transLabel.setText(LanguageRegistry.TRANSLATION_TAB_TITLE);
		return this;
	}
}