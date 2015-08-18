package trb1914.alexml.gui.tabs.segment;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.gui.SegmentEditor;
import trb1914.alexml.xml.XMLParser;
import trb1914.alexml.xml.XMLTags;
/**
 * the keywordstab, allows the user easy interaction with the keywords
 * of the given segment
 * @author Mees Gelein
 *
 */
public class KeywordsTab extends JPanel{

	//the keyterms in an array of String
	private String[] keyTerms;
	
	//a reference to two elements to allow this object to do its own parsing and saving
	private Element segment;
	private Element keywords;
	
	//a reference to the GUI object to set/get data
	private JTextArea keywordArea;

	/**
	 * creates a new KeywordTab using the provided segment
	 * @param cSegment
	 */
	public KeywordsTab(Element cSegment){
		SegmentEditor.keywordsTab = this;
		segment = cSegment;
		keywords = (Element) segment.getElementsByTagName(XMLTags.KEYWORDS).item(0);
		makeGUI();
	}
	
	/**
	 * get all the keyterms for this segment and add them 
	 * to the keyTerms list
	 */
	public void parseXML(){
		NodeList terms = keywords.getElementsByTagName(XMLTags.TERM);
		keyTerms = new String[terms.getLength()];
		for(int i = 0;i<terms.getLength();i++){
			keyTerms[i] = terms.item(i).getTextContent();
		}
		StringBuilder keywordBuilder = new StringBuilder();
		for(int i = 0; i < keyTerms.length; i++){
			keywordBuilder.append(keyTerms[i]);
			keywordBuilder.append(", ");
		}
		String keywordString = keywordBuilder.toString();
		keywordString = keywordString.substring(0,keywordString.length() - 2);
		keywordArea.setText(keywordString);
		keywordArea.setToolTipText(LanguageRegistry.KEYWORDS_TEXTAREA_TOOLTIP);
	}
	
	/**
	 * creates the GUI for this tab
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		
		keywordArea = new JTextArea(3, 10);
		keywordArea.setLineWrap(true);
		keywordArea.setWrapStyleWord(true);
		
		keywordArea.setFont(FileRegistry.getNormalFont());
		
		JScrollPane keywordScrollPane = new JScrollPane(keywordArea);
		add(new JLabel(LanguageRegistry.KEYWORDS_TAB_TITLE + ": "), BorderLayout.PAGE_START);
		add(keywordScrollPane, BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(5, 2, 0, 0));
	}
		
	/**
	 * parses the data inside the keyterms textfield and adds it to the current segment
	 */
	public void saveToElement(){
		keyTerms = keywordArea.getText().split(",");
		Element keys = XMLParser.xmlDocument.createElement(XMLTags.KEYWORDS);
		for(int i = 0; i < keyTerms.length; i++){
			Element term = XMLParser.xmlDocument.createElement(XMLTags.TERM);
			term.setTextContent(keyTerms[i].replaceAll(" ", ""));
			keys.appendChild(term);
		}
		if(keywords != null){
			segment.removeChild(keywords);
		}
		XMLParser.xmlDocument.importNode(keys, true);
		segment.appendChild(keys);
	}
}
