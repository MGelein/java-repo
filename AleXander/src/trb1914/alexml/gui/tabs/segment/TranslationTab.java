package trb1914.alexml.gui.tabs.segment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import trb1914.alexml.Debug;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Note;
import trb1914.alexml.data.tree.TranslationEntry;
import trb1914.alexml.gui.NoteEditor;
import trb1914.alexml.gui.SegmentEditor;
import trb1914.alexml.xml.XMLParser;
import trb1914.alexml.xml.XMLTags;
/**
 * This class is the translation tab of the segmenteditor window. This shows
 * the user the translation and allows them to add notes
 * @author Mees Gelein
 */
public class TranslationTab extends JPanel{

	//the JTextPane that actually holds the text
	private JTextPane translationArea;

	//the translationDesc, this used to be a separate tab, but is now only a JPanel
	private TranslationDescTab transDescTab;

	//a ref to the segment we're editing
	private Element segment;

	//a list of Notes that are contained in this text
	//private Note[] transNotes;

	//the List of all the current translations
	private JList translationList;

	//is the mouse currently over a Note?
	private boolean overNote = false;

	//a list of all the translationEntries that are connected to this segment
	private ArrayList<TranslationEntry> translations = new ArrayList<TranslationEntry>();

	//the current index of the translation that is being edited
	private int cIndex = 0;


	/**
	 * creates a new TranslationTab opening the specified segment
	 * @param cSegment		the segment to Open
	 */
	public TranslationTab(Element cSegment){
		segment = cSegment;
		makeGUI();
	}

	/**
	 * parses the XML and uses the result to populate the textArea
	 */
	public void parseXML(){
		NodeList allTranslations = segment.getElementsByTagName(XMLTags.TRANSLATION);

		//for every translation
		TranslationEntry[] transListData = new TranslationEntry[allTranslations.getLength()];
		for(int i = 0; i < allTranslations.getLength(); i++){
			TranslationEntry t = new TranslationEntry();

			t.transElement = (Element) allTranslations.item(i);
			NodeList nodeList = ((Element) t.transElement).getElementsByTagName(XMLTags.NOTE);
			t.transNotes = Note.parseNotes(nodeList, t.transNotes);
			t.language = t.transElement.getAttribute(XMLTags.LANG);
			t.transDescElement = (Element) t.transElement.getElementsByTagName(XMLTags.TRANSLATION_DESC).item(0);
			t.author = t.transDescElement.getElementsByTagName(XMLTags.AUTHOR).item(0).getTextContent();
			t.translationDescription = t.transDescElement.getTextContent().replaceAll(t.author, "");
			transListData[i] = t;
			translations.add(t);

			NodeList emphElements = ((Element) t.transElement).getElementsByTagName(XMLTags.EMPHASIZE);
			
			//Remove some empty emph elements
			if(emphElements != null){
				for(int j = 0; j < emphElements.getLength(); j++){
					if(emphElements.item(j).getTextContent() == null){
						emphElements.item(j).getParentNode().removeChild(emphElements.item(j));
					}else if(emphElements.item(j).getTextContent().equals(" ")){
						emphElements.item(j).getParentNode().removeChild(emphElements.item(j));
					}
				}
			}

			DocumentBuilderFactory docBuildFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			try{
				docBuilder = docBuildFac.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				t.transElement = (Element) doc.importNode(t.transElement, true);
				doc.appendChild(t.transElement);
				String translationString = XMLParser.getString(doc, true);
				translationString = translationString.replaceAll(t.transDescElement.getTextContent(), "");
				t.translationString = getTranslationText(translationString, true);
			}catch(Exception e4){
				JOptionPane.showMessageDialog(null, LanguageRegistry.PARSING_ERROR);
			}
			
			try{
				docBuilder = docBuildFac.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				t.transDescElement = (Element) doc.importNode(t.transDescElement, true);
				doc.appendChild(t.transDescElement);
				String translationDescString = XMLParser.getString(doc, true);
				translationDescString = translationDescString.replaceAll(t.author, "");
				t.translationDescription = getTranslationText(translationDescString, false);
			}catch(Exception e4){
				JOptionPane.showMessageDialog(null, LanguageRegistry.PARSING_ERROR);
			}
		}

		translationList.setListData(transListData);
		translationList.setSelectedIndex(cIndex);
		loadTranslation(-1);
	}

	/**
	 * builds the GUI
	 */
	private void makeGUI(){
		translationList = new JList();
		translationList.setPrototypeCellValue("A very long authorname.......");//setting the default cellwidth
		translationList.setFont(FileRegistry.getNormalFont(12));
		DefaultListCellRenderer defRenderer = (DefaultListCellRenderer) translationList.getCellRenderer();
		defRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		JScrollPane transListScrollPanel = new JScrollPane(translationList);
		JPanel transListPanel = new JPanel(new BorderLayout());
		transListPanel.add(transListScrollPanel);
		JLabel transLabel = new JLabel(LanguageRegistry.TRANSLATIONS_LABEL +  ": ");
		transLabel.setBorder(BorderFactory.createEmptyBorder(0,0,3,0));
		transListPanel.add(transLabel, BorderLayout.PAGE_START);
		transListPanel.add(addToolBar(), BorderLayout.EAST);
		transListPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

		translationList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				loadTranslation(translationList.getSelectedIndex());
			}
		});

		translationArea = new JTextPane();
		JScrollPane translationScrollPane = new JScrollPane(translationArea);	
		JPanel translationPane = new JPanel(new BorderLayout());
		translationPane.add(translationScrollPane);
		translationPane.add(new JLabel(LanguageRegistry.TRANSLATION_TAB_TITLE + ":"), BorderLayout.PAGE_START);
		translationPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		translationArea.addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent e) {}
			public void mouseMoved(MouseEvent e) {
				if((translationArea.getCursor().getType() == Cursor.HAND_CURSOR) &&(!overNote )){
					translationArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
					translationArea.setToolTipText(null);
				}
				int charIndex = translationArea.viewToModel(e.getPoint());
				String s = translationArea.getText();
				if(charIndex < s.length()){
					if(s.charAt(charIndex) == LanguageRegistry.NOTE_CHAR){
						if(!overNote){
							translationArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
							int noteIndex = findTransNoteIndex(translationArea.getText(), charIndex);
							translationArea.setToolTipText(LanguageRegistry.NOTE_CONTENT_LABEL + " " + translations.get(cIndex).transNotes[noteIndex].ID);
						}
						overNote = true;
					}else{
						overNote = false;
					}
				}else{
					overNote = false;
				}

			}	
		});

		translationArea.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {	}
			public void insertUpdate(DocumentEvent e) {}
			public void removeUpdate(DocumentEvent e) {
				String s = translationArea.getText();
				if(s.length() > 0){
					int noteAmt = s.length() - s.replaceAll(LanguageRegistry.NOTE_STRING, "").length();
					if(noteAmt > 0){
						if(translations.get(cIndex).transNotes != null){
						if((noteAmt < translations.get(cIndex).transNotes.length)){
							int maxIndex = s.length();
							if(translationArea.getCaretPosition() < maxIndex){
								maxIndex = translationArea.getCaretPosition();
							}
							s = s.substring(0, maxIndex);
							int beforeAmt = s.length() - s.replaceAll(LanguageRegistry.NOTE_STRING, "").length();
							translations.get(cIndex).transNotes = Note.removeNote(translations.get(cIndex).transNotes, beforeAmt);
						}
						}else{
							Debug.println("Translations[" + cIndex + "] transNote is null", this);
						}
					}
				}
			}

		});

		translationArea.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				int charIndex = translationArea.viewToModel(e.getPoint());
				String s = translationArea.getText();
				if(charIndex < s.length()){
					if(s.charAt(charIndex) == LanguageRegistry.NOTE_CHAR){
						int index = findTransNoteIndex(s, charIndex);
						Debug.println("Click on note index no." + index, this);
						new NoteEditor(SegmentEditor.frame, translations.get(cIndex).transNotes[index], index, true);
					}
				}
			}
		});

		JButton italicButton = new JButton(new StyledEditorKit.BoldAction());
		italicButton.setText(LanguageRegistry.EMPHASIZE_LABEL);

		JButton insertTransNote = new JButton(LanguageRegistry.INSERT_NOTE_LABEL);
		insertTransNote.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int index = translationArea.getCaretPosition();
				int noteAmt = 0;
				String s = translationArea.getText();
				if(index > 0){
					String start = s.substring(0, index);
					noteAmt = start.length() - start.replaceAll(LanguageRegistry.NOTE_STRING, "").length();
					String end = s.substring(index);
					s = start + LanguageRegistry.NOTE_STRING + end;
					translationArea.setText(s);
					translationArea.setCaretPosition(index + 1);
					translations.get(cIndex).transNotes = Note.addNewEmptyNote(translations.get(cIndex).transNotes, noteAmt, LanguageRegistry.ANNOTATION);
				}else if(index==0){
					s = LanguageRegistry.NOTE_STRING + s;
					translationArea.setText(s);
					translationArea.setCaretPosition(index + 1);
					translations.get(cIndex).transNotes = Note.addNewEmptyNote(translations.get(cIndex).transNotes, 0, LanguageRegistry.ANNOTATION);
				}
			}
		});

		JPanel insertTransPanel = new JPanel(); insertTransPanel.add(insertTransNote);
		insertTransPanel.add(italicButton);
		insertTransPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.gray));
		setLayout(new BorderLayout());
		transDescTab = new TranslationDescTab(segment);

		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,2));
		textPanel.add(transDescTab, BorderLayout.NORTH);
		textPanel.add(translationPane);
		textPanel.add(insertTransPanel, BorderLayout.SOUTH);

		transListPanel.setBorder(BorderFactory.createEmptyBorder(2,5,10,2));
		
		add(textPanel);
		add(transListPanel, BorderLayout.EAST);
		translationArea.setFont(FileRegistry.getNormalFont());
	}

	/**
	 * moves the translation specified by startIndex in the direction
	 * provided by dir (-1 is up the list, 1 is down the list)
	 * @param dir
	 * @param startIndex
	 */
	private void moveTranslation(int dir, int startIndex){
		if(startIndex > 0 && dir==-1){
			TranslationEntry a = translations.get(startIndex);
			TranslationEntry b = translations.get(startIndex + dir);
			translations.set(startIndex, b);
			translations.set(startIndex + dir, a);
			updateTranslationList();
			translationList.setSelectedIndex(startIndex + dir);
		}else if(startIndex < translations.size() - 1 && dir == 1){
			TranslationEntry a = translations.get(startIndex);
			TranslationEntry b = translations.get(startIndex + dir);
			translations.set(startIndex, b);
			translations.set(startIndex + dir, a);
			updateTranslationList();
			translationList.setSelectedIndex(startIndex + dir);
		}
	}

	/**
	 * deletes the translation with the specified index
	 * @param index
	 */
	private void deleteTranslation(int index){
		translations.remove(index);
		updateTranslationList();
	}

	/**
	 * updates the translationList
	 */
	private void updateTranslationList(){
		if(translations.size() < 1){
			translations.set(0, new TranslationEntry());
		}
		TranslationEntry[] allTranslationEntries = new TranslationEntry[translations.size()];
		for(int i = 0; i < translations.size(); i++){
			allTranslationEntries[i] = translations.get(i);
		}
		translationList.setListData(allTranslationEntries);
	}

	/**
	 * creates the toolbar that allows modifying (creating, deleting and moving) of
	 * translations	
	 * @return
	 */
	private JToolBar addToolBar(){
		JToolBar bar = new JToolBar("Translation tools", JToolBar.VERTICAL);
		bar.setFloatable(false);
		JButton plusButton = new JButton(new ImageIcon(FileRegistry.PLUS_ICON_SMALL));
		plusButton.setToolTipText(LanguageRegistry.ADD_NEW_TRANSLATION_LABEL);
		plusButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				translations.add(new TranslationEntry());
				updateTranslationList();
				translationList.setSelectedIndex(translations.size() - 1);
			}
		});
		JButton deleteButton = new JButton(new ImageIcon(FileRegistry.DELETE_ICON_SMALL));
		deleteButton.setToolTipText(LanguageRegistry.DELETE_SELECTED_TRANSLATION_LABEL);
		deleteButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deleteTranslation(translationList.getSelectedIndex());
			}
		});
		JButton moveUpButton = new JButton(new ImageIcon(FileRegistry.UP_ARROW_SMALL));
		moveUpButton.setToolTipText(LanguageRegistry.MOVE_TRANSLATION_UP_LABEL);
		moveUpButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveTranslation(-1, translationList.getSelectedIndex());
			}
		});
		JButton moveDownButton = new JButton(new ImageIcon(FileRegistry.DOWN_ARROW_SMALL));
		moveDownButton.setToolTipText(LanguageRegistry.MOVE_TRANSLATION_DOWN_LABEL);
		moveDownButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveTranslation(1, translationList.getSelectedIndex());
			}
		});
		bar.add(plusButton);
		bar.add(deleteButton);
		bar.addSeparator();
		bar.add(moveUpButton);
		bar.add(moveDownButton);
		return bar;
	}

	/**
	 * loads the translation with the provided index. checks to see if 
	 * that translation is not already loaded
	 * @param index
	 */
	private void loadTranslation(int index){
		if(cIndex == index){

		}else{
			if(index == -1){//first time the segment is loaded. No need to save a prev translation
				index = 0;
			}else{//we have to save the current values before loading the new translation
				TranslationEntry t = translations.get(cIndex);
				t.author = transDescTab.getAuthorFieldText();
				t.translationDescription = transDescTab.getTranslationDescAreaText();
				t.translationString = codeEmphasis();//turns bold text into @ and #
				t.language = transDescTab.langField.getText();
			}
			cIndex = index;
			TranslationEntry t = translations.get(cIndex);
			translationArea.setText(t.translationString);
			decodeEmphasis();
			transDescTab.authorField.setText(t.author);
			transDescTab.setTransDescField(t.translationDescription);
			transDescTab.langField.setText(t.language);
		}
	}

	/**
	 * turns # and @ in the translationArea field into bold tags
	 */
	private void decodeEmphasis(){
		try{
			SimpleAttributeSet boldStyle = new SimpleAttributeSet();
			boldStyle.addAttribute(StyleConstants.Bold, true);
			StyledDocument doc = translationArea.getStyledDocument();
			doc.setCharacterAttributes(0, doc.getLength(), new SimpleAttributeSet(), true);
			String s = doc.getText(0, doc.getLength());
			int startIndex = s.indexOf("@");
			while(startIndex > -1){
				int endIndex = s.indexOf("#", startIndex);
				doc.setCharacterAttributes(startIndex, (endIndex - startIndex), boldStyle, true);
				doc.remove(startIndex, 1);
				doc.remove(endIndex - 1, 1);
				s = doc.getText(0, doc.getLength());
				startIndex = s.indexOf("@", endIndex);
			}
		}catch(Exception e){
			Debug.println("Decoding not working with emphasis", this);
		}
	}

	/**
	 * turns bold text into text surrounded with @ and # and returns the
	 * result
	 */
	private String codeEmphasis(){
		StyledDocument doc = translationArea.getStyledDocument();
		boolean start = false;
		for(int i = 0; i < doc.getLength(); i++){
			javax.swing.text.Element e = doc.getCharacterElement(i);
			if(e.getAttributes().containsAttribute(StyleConstants.Bold, true)){
				if(!start){//start of a bold piece
					start = true;
					try {
						doc.insertString(i, "@", new SimpleAttributeSet());
					} catch (BadLocationException e1) {
						Debug.println("Bad location to insert String @ TranslationTab", this);
					}
				}
			}else if(start){//the end of a bold piece
				start = false;
				try {
					doc.insertString(i, "#", new SimpleAttributeSet());
				} catch (BadLocationException e1) {
					Debug.println("Bad location to insert String @ TranslationTab", this);
				}
			}

		}
		String s = translationArea.getText();
		if(start){//the end of the string has been reached but no non-bold char has been found. So bold until the end 
			s += "#";
		}
		//Debug.println(s);

		return s;
	}

	/**
	 * sets the note at the given index
	 * @param n			the Note
	 * @param index		the index of the Note
	 */
	public void setTransNote(Note n, int index){
		translations.get(cIndex).transNotes[index] = n;
	}

	/**
	 * finds the noteIndex of the note at the given character index
	 * in the provided String
	 * @param s			the string to search in
	 * @param index		the index of the NOTE_CHAR
	 * @return			-1 if not found
	 */
	private int findTransNoteIndex(String s, int index){
		int count = 0;
		for(int i = 0; i < s.length(); i++){
			if(s.charAt(i) == LanguageRegistry.NOTE_CHAR){
				if(i==index) return count;
				count++;
			}
		}
		return -1;
	}

	/**
	 * cleans the provided string and sets the translationArea text
	 * @param s		the String to set it to
	 */
	private String getTranslationText(String s, boolean removeTransDesc) {
		s = s.replaceAll("\t", "");
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\f", "");
		s = s.replaceAll("    ", "");
		s = s.replaceAll("   ", "");
		s = s.replaceAll("  ", "");
		s = s.replaceAll("<emph>", "@");
		s = s.replaceAll("</emph>", "#");
		s = s.replaceAll("<ref", "{");
		s = s.replaceAll("</ref>", "}");
		int startIndex = s.indexOf("<note");
		while(startIndex >= 0){
			int endIndex = s.indexOf("ote>");
			int charUsed = 4;
			if(endIndex == -1) {endIndex = s.indexOf("/>", startIndex); charUsed = 2;}
			s = s.substring(0, startIndex) + LanguageRegistry.NOTE_STRING + s.substring(endIndex + charUsed);
			startIndex = s.indexOf("<note");
		}
		if(removeTransDesc){
			startIndex = s.indexOf("<translationDesc");
			while(startIndex >= 0){
				int endIndex = s.indexOf("/translationDesc>");
				s = s.substring(0, startIndex) + s.substring(endIndex + 17);
				startIndex = s.indexOf("<translationDesc");
			}
		}
		s = s.replaceAll( "(?s)<.*?>", "" );
		s = s.replaceAll("\"/>", "#");
		s = s.replaceAll("/>", "");
		s = s.replaceAll("<", "");
		s = s.replaceAll(">", "");
		

		return s;
	}

	/**
	 * saves the changes the user has made back to the XML object
	 */
	public void saveToElement(){
		//save the current loaded translation into the translations list
		TranslationEntry t = translations.get(cIndex);
		t.author = transDescTab.getAuthorFieldText();
		t.translationDescription = transDescTab.getTranslationDescAreaText();
		String s = t.translationDescription;
		t.translationDescription = s;
		t.translationString = codeEmphasis();
		t.language = transDescTab.langField.getText();

		//remove all old translations
		NodeList map = segment.getElementsByTagName(XMLTags.TRANSLATION);
		while(map.getLength() > 0){
			segment.removeChild(map.item(0));
			map = segment.getElementsByTagName(XMLTags.TRANSLATION);
		}

		//append all new translations
		for(int i = 0; i < translations.size(); i++){
			XMLParser.appendXmlFragment(segment, translations.get(i).getTranslationText(), Integer.toString(i));
		}
	}
}
