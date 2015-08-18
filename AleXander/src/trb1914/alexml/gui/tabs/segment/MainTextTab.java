package trb1914.alexml.gui.tabs.segment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import trb1914.alexml.Debug;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Note;
import trb1914.alexml.gui.NoteEditor;
import trb1914.alexml.gui.SegmentEditor;
import trb1914.alexml.xml.XMLParser;
import trb1914.alexml.xml.XMLTags;

public class MainTextTab extends JPanel{

	//contains refs to some necessary elements
	private Element segment;
	private Element displayElement;
	
	//the JTextPane the user sees
	public JTextPane mainTextArea;
	
	//the notes in this segment
	private Note[] notes;
	
	//if the mouse is currently over a Note
	private boolean overNote = false;
	
	//a ref  to the undoManager
	private UndoManager undoManager = new UndoManager();

	
	/**
	 * creates a new MainTextTab opening the specified segment
	 * @param cSegment
	 */
	public MainTextTab(Element cSegment){
		segment = cSegment;
		
		makeGUI();
	}
	
	/**
	 * parses the XML and populates the textArea
	 */
	public void parseXML(){
		displayElement = (Element) segment.getElementsByTagName(XMLTags.DISPLAYSEGMENT).item(0);
		
		NodeList nodeList = displayElement.getElementsByTagName(XMLTags.NOTE);
		notes = Note.parseNotes(nodeList, notes);
		
		DocumentBuilderFactory docBuildFac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuildFac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			displayElement = (Element) doc.importNode(displayElement, true);
			doc.appendChild(displayElement);
			String displaySegmentString = XMLParser.getString(doc, true);
			setMainTextArea(displaySegmentString);
		} catch (ParserConfigurationException e1) {
			Debug.println("XML parsing problem with Main Text", this);
			JOptionPane.showMessageDialog(null, LanguageRegistry.PARSING_ERROR);
		}
	}
	
	/**
	 * builds the GUI and adds the listeners
	 */
	private void makeGUI(){
		mainTextArea = new JTextPane();
		mainTextArea.getDocument().addUndoableEditListener(undoManager);
		JScrollPane mainTextScroll = new JScrollPane(mainTextArea);
		
		setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(mainTextScroll);
		add(contentPanel);
		add(new KeywordsTab(segment), BorderLayout.SOUTH);
		
		JButton insertNoteButton = new JButton(LanguageRegistry.INSERT_NOTE_LABEL);
		insertNoteButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int index = mainTextArea.getCaretPosition();
				int noteAmt = 0;
				String s = mainTextArea.getText();
				if(index > 0){
					String start = s.substring(0, index);
					noteAmt = start.length() - start.replaceAll(LanguageRegistry.NOTE_STRING, "").length();
					String end = s.substring(index);
					s = start + LanguageRegistry.NOTE_STRING + end;
					mainTextArea.setText(s);
					mainTextArea.setCaretPosition(index + 1);
					notes = Note.addNewEmptyNote(notes, noteAmt);
				}else if(index==0){
					s = LanguageRegistry.NOTE_STRING + s;
					mainTextArea.setText(s);
					mainTextArea.setCaretPosition(index + 1);
					notes = Note.addNewEmptyNote(notes, 0);
				}
			}
		});
		JPanel insertButtonPanel = new JPanel(); insertButtonPanel.add(insertNoteButton);
		insertButtonPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.gray));//border only on bottom and left
		contentPanel.add(insertButtonPanel, BorderLayout.SOUTH);
		
		mainTextArea.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				int charIndex = mainTextArea.viewToModel(e.getPoint());
				String s = mainTextArea.getText();
				if(charIndex < s.length()){
					if(s.charAt(charIndex) == LanguageRegistry.NOTE_CHAR){
						int index = findNoteIndex(s,charIndex);
						new NoteEditor(SegmentEditor.frame, notes[index], index, false);
					}
				}
			}			
		});
		
		mainTextArea.addMouseMotionListener(new MouseMotionListener(){
			public void mouseDragged(MouseEvent e) {}
			public void mouseMoved(MouseEvent e) {
				if((mainTextArea.getCursor().getType() == Cursor.HAND_CURSOR) &&(!overNote )){
					mainTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
					mainTextArea.setToolTipText(null);
				}
				int charIndex = mainTextArea.viewToModel(e.getPoint());
				String s = mainTextArea.getText();
				if(charIndex < s.length()){
					if(s.charAt(charIndex) == LanguageRegistry.NOTE_CHAR){
						if(!overNote){
							mainTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
							int noteIndex = findNoteIndex(mainTextArea.getText(), charIndex);
							mainTextArea.setToolTipText(LanguageRegistry.NOTE_CONTENT_LABEL + " " + Integer.toString(noteIndex + 1));
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
				
		mainTextArea.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {}
			public void insertUpdate(DocumentEvent e) {}
			
			public void removeUpdate(DocumentEvent e) {
				String s = mainTextArea.getText();
				if(s.length() > 0){
					int noteAmt = s.length() - s.replaceAll(LanguageRegistry.NOTE_STRING, "").length();
					if(noteAmt < notes.length){
						s = s.substring(0,mainTextArea.getCaretPosition());
						int beforeAmt = s.length() - s.replaceAll(LanguageRegistry.NOTE_STRING, "").length();
						notes = Note.removeNote(notes, beforeAmt);
						Debug.println("Removed a note", this);
					}
				}
			}
			
		});
		
		mainTextArea.setFont(FileRegistry.getNormalFont());
	}
	
	/**
	 * sets the mainTextArea to the provided String
	 * @param s 	the String to set it to
	 */
	private void setMainTextArea(String s) {
		s = s.replaceAll("\t", "");
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\f", "");
		s = s.replaceAll("<ex>", "smallerThan");
		s = s.replaceAll("</ex>", "largerThan");
		s = s.replaceAll( "(?s)<!--.*?-->", "" );
		s = s.replaceAll("<lb n=\"", "@");
		s = s.replaceAll("    ", "");
		s = s.replaceAll("   ", "");
		s = s.replaceAll("  ", "");
		int startIndex = s.indexOf("<note");
		while(startIndex >= 0){
			int endIndex = s.indexOf("ote>");
			int charUsed = 4;
			if(endIndex == -1) {endIndex = s.indexOf("/>", startIndex); charUsed = 2;}
			s = s.substring(0, startIndex) + LanguageRegistry.NOTE_STRING + s.substring(endIndex + charUsed);
			startIndex = s.indexOf("<note");
		}
		s = s.replaceAll( "(?s)<.*?>", "" );
		s = s.replaceAll("\"/>", "#");
		s = s.replaceAll("/>", "");
		s = s.replaceAll("smallerThan", "<");
		s = s.replaceAll("largerThan", ">");
		String[] lines = s.split("@");
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i < lines.length; i++){
			
			builder.append(lines[i] + "\n");
		}
		if(builder !=null) mainTextArea.setText(builder.toString());
	}
	
	/**
	 * returns the mainTextArea as an XML object
	 * @return
	 */
	private String getMainTextAreaXML(){
		String s = mainTextArea.getText();
		s = s.replaceAll("\t", "");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\f", "");
		s = s.replaceAll("    ", "");
		s = s.replaceAll("   ", "");
		s = s.replaceAll("  ", "");
		String[] split = s.split("<");
		StringBuilder builder = new StringBuilder();
		for(int i = 0;i<split.length - 1;i++){
			split[i] = split[i] + "@ex";
			int index = split[i].lastIndexOf(' ');
			boolean found = false;
			if(index >= 0){
				found = true;
				split[i] = split[i].substring(0, index + 1) + "@expan" + split[i].substring(index + 1);
			}
			index = split[i].indexOf(':');
			if((index >= 0)&&(!found)){
				found = true;
				split[i] = split[i].substring(0, index + 1) + "@expan" + split[i].substring(index + 1);
			}
			if(!found){
				split[i] = "@expan" + split[i];
			}
			builder.append(split[i]);
		}
		builder.append(split[split.length - 1]);
		
		s = builder.toString();
		
		split = s.split(">");
		builder = new StringBuilder();
		for(int i = 0;i<split.length - 1;i++){
			split[i] = split[i] + "#ex";
			int index = split[i + 1].indexOf(' ');
			boolean found = false;
			if(index >= 0){
				found = true;
				split[i + 1] = split[i + 1].substring(0, index + 1) + "#expan" + split[i + 1].substring(index + 1);
			}
			index = split[i + 1].indexOf(':');
			if((index >= 0)&&(!found)){
				found = true;
				split[i + 1] = split[i + 1].substring(0, index + 1) + "#expan" + split[i + 1].substring(index + 1);
			}
			index = split[i + 1].indexOf(',');
			if((index >= 0)&&(!found)){
				found = true;
				split[i + 1] = split[i + 1].substring(0, index + 1) + "#expan" + split[i + 1].substring(index + 1);
			}
			index = split[i + 1].indexOf('.');
			if((index >= 0)&&(!found)){
				found = true;
				split[i + 1] = split[i + 1].substring(0, index + 1) + "#expan" + split[i + 1].substring(index + 1);
			}
			builder.append(split[i]);
			if(!found){
				split[i + 1] = "#expan" + split[i+1];
			}
		}
		builder.append(split[split.length - 1]);
		s = builder.toString();
		
		s = s.replaceAll("@expan","<expan>");
		s = s.replaceAll("#expan", "</expan>");
		s = s.replaceAll("@ex", "<ex>");
		s = s.replaceAll("#ex", "</ex>");
		
		String[] lines = s.split("\n");
		builder = new StringBuilder();
		for(int i = 0; i < lines.length; i++){
			lines[i] = lines[i].replaceAll("#", "\"/>");
			lines[i] = "<lb n=\"" + lines[i];
			builder.append(lines[i]);
		}
		s = builder.toString();
		s = "<" + XMLTags.DISPLAYSEGMENT + ">"+s+"</" + XMLTags.DISPLAYSEGMENT + ">";
		s = Note.insertNotes(notes, s);
		return s;
	}
	
	/**
	 * sets the Note at the provided index
	 * @param n
	 * @param index
	 */
	public void setNote(Note n, int index){
		notes[index] = n;
	}
	
	/**
	 * finds the index of the note the user is currently hovering
	 * over with the mouse
	 * @param s			the String to scan
	 * @param index
	 * @return
	 */
	private int findNoteIndex(String s, int index) {
		int count = 0;
		for(int i = 0; i < s.length(); i++){
			if(s.charAt(i) == LanguageRegistry.NOTE_CHAR){
				if(i == index) return count;
				count++;
			}
		}
		return -1;
	}
	
	/**
	 * saves the changes back to the XML object
	 */
	public void saveToElement(){
		Node displayElement = segment.getElementsByTagName(XMLTags.DISPLAYSEGMENT).item(0);
		segment.removeChild(displayElement);
		undoManager.discardAllEdits();
		XMLParser.appendXmlFragment(segment, getMainTextAreaXML());
	}
}
