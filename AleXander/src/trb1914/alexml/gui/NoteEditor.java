package trb1914.alexml.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Note;
import trb1914.alexml.data.Registry;
import trb1914.alexml.interfaces.ICloseWindow;
/**
 * The NoteEditor is a separate JFrame that opens when a note is clicked.
 * It allows the user to edit a specific note's content
 * @author Mees Gelein
 *
 */
public class NoteEditor extends JFrame implements ICloseWindow{
	
	//The Gui components that show and allow editing to the note-data
	private JTextArea noteArea;
	private JComboBox typeBox;
	
	//contains the note-data
	private Note cNote = new Note();
	
	//contains a reference to the parent to allow disabling of the parent when this menu is highlighted
	private SegmentEditor _parent;
	
	//the note index. How many notes are preceding?
	private int _index;
	
	//if this call to the note editor came from the translationtext. If so, set the default note type-attribute to annotation
	private boolean inTranslation = false;
	
	/**
	 * creates a new NoteEditor window with the given parameters
	 * @param parent		reference to the caller of this window
	 * @param note			the note the user clicked on
	 * @param index			the index of the note the user clicked on
	 * @param transNote		did this call came from translation or mainText?
	 */
	public NoteEditor(SegmentEditor parent, Note note, int index, boolean transNote) {
		Main.openFrames.add(this);
		parent.setEnabled(false);
		inTranslation = transNote;
		cNote.ID = note.ID;
		cNote.content = note.content;
		cNote.type= note.type;
		_index = index;
		_parent = parent;
		this.setSize(600, 400);
		this.setMinimumSize(this.getSize());
		Point point = Main.main.getLocation();
		point.x += 20;
		point.y += 20;
		this.setLocation(point);
		ImageIcon img = new ImageIcon(FileRegistry.APP_ICON);
		this.setIconImage(img.getImage());
		this.setTitle(LanguageRegistry.NOTE_EDITOR_TITLE);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(JOptionPane.showConfirmDialog(null, LanguageRegistry.UNSAVED_WARNING) == JOptionPane.YES_OPTION){
					closeWindow(false);
				}
			}
		});
		
		makeGUI();
		this.setVisible(true);
	}

	/**
	 * sets up the GUI, populates it and adds listeners
	 */
	private void makeGUI() {
		JPanel contentPanel = new JPanel(new BorderLayout());
		add(contentPanel);
		
		JLabel noteTypeLabel = new JLabel(LanguageRegistry.NOTE_TYPE_LABEL + ": ");
		typeBox = new JComboBox<String>();
		typeBox.addItem(LanguageRegistry.NOTE_TYPE_CRIT);
		typeBox.addItem(LanguageRegistry.NOTE_TYPE_FONT);
		typeBox.addItem(LanguageRegistry.NOTE_TYPE_ANNO);
		if(cNote.type.equals(LanguageRegistry.APP_FONT)){
			typeBox.setSelectedIndex(1);
		}else if(cNote.type.equals(LanguageRegistry.ANNOTATION)){
			typeBox.setSelectedIndex(2);
		}else{
			typeBox.setSelectedIndex(0);
		}
		JPanel noteTypePanel = new JPanel();
		noteTypePanel.add(noteTypeLabel);
		noteTypePanel.add(typeBox);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(noteTypePanel, BorderLayout.WEST);
		contentPanel.add(topPanel, BorderLayout.PAGE_START);
		
		noteArea = new JTextArea(5, 10);
		noteArea.setLineWrap(true);
		noteArea.setWrapStyleWord(true);
		noteArea.setFont(FileRegistry.getNormalFont());
		noteArea.setText(cNote.content);
		JScrollPane noteScrollPane = new JScrollPane(noteArea);
		JTabbedPane notePane = new JTabbedPane();
		notePane.addTab(LanguageRegistry.NOTE_CONTENT_LABEL, noteScrollPane);
		contentPanel.add(notePane);
		
		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		
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

		buttonPanel.add(saveButton);
		
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

		buttonPanel.add(discardButton);
		
		add(contentPanel);
	}

	/**
	 * closes the current window.
	 * @param saveChanges		if true changes will be saved, if false changes will be ignored
	 */
	public void closeWindow(boolean saveChanges) {
		if(saveChanges){
			String[] types = new String[]{LanguageRegistry.APP_CRIT, LanguageRegistry.APP_FONT, LanguageRegistry.ANNOTATION};
			Note n = new Note();
			n.content = noteArea.getText();
			n.type = types[typeBox.getSelectedIndex()];
			if(!inTranslation)SegmentEditor.mainTextTab.setNote(n, _index);
			else SegmentEditor.translationTab.setTransNote(n, _index);
		}
		boolean succes = Main.openFrames.remove(this);
		Debug.println("Removed frame from openList: " + succes, this);
		_parent.setEnabled(true);
		dispose();
	}

}
