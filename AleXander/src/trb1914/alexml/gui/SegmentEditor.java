package trb1914.alexml.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.w3c.dom.Element;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.gui.tabs.main.TabHolder;
import trb1914.alexml.gui.tabs.segment.KeywordsTab;
import trb1914.alexml.gui.tabs.segment.MainTextTab;
import trb1914.alexml.gui.tabs.segment.TranslationTab;
import trb1914.alexml.gui.tabs.segment.WordEditorTab;
import trb1914.alexml.gui.util.LoaderPane;
import trb1914.alexml.interfaces.ICloseWindow;
import trb1914.alexml.xml.XMLParser;
/**
 * A separate window that allows the user to see and edit 
 * a specific segment of the text. 
 * @author Mees Gelein
 *
 */
public class SegmentEditor extends JFrame implements ICloseWindow{

	//reference to some of the elements used in this SegmentEditor
	private Element segment;
	
	//all the tabs
	public static KeywordsTab keywordsTab;
	public static WordEditorTab wordEditorTab;
	public static TranslationTab translationTab;
	public static MainTextTab mainTextTab;
	
	//the tabbed pane
	private JTabbedPane tabbedPane;
	
	//the notes contained in both the translation and the main text
	public static SegmentEditor frame;

	//is the mouse currently over a note?
	protected boolean overNote = false;
	
	//is a frame already open?
	private static boolean segmentOpen = false;
	
	/**
	 * tries to open a segment in a separate segment editor window
	 * @param segmentIndex
	 */
	public SegmentEditor(int segmentIndex, int startTabIndex){
		Main.openFrames.add(this);
		if(!segmentOpen){
			segmentOpen = true;
		}else{
			return;
		}
		frame = this;
		TabHolder.segmentPanel.setSegmentButtonState(false);
		Point point = Main.main.getLocation();
		point.x += 20;
		point.y += 20;
		this.setLocation(point);
		ImageIcon img = new ImageIcon(FileRegistry.APP_ICON);
		this.setIconImage(img.getImage());
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);		
		segment = XMLParser.getSegment(segmentIndex);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(JOptionPane.showConfirmDialog(null, LanguageRegistry.UNSAVED_WARNING) == JOptionPane.YES_OPTION){
					closeWindow(false);
				}
			}
		});
		
		makeGUI();
		this.setVisible(true);
		pack();
		parseXML();
		tabbedPane.setSelectedIndex(startTabIndex);
		Debug.println("Opening a SegmentEditor for segment " + segmentIndex + " and showing tab " + startTabIndex, this);
	}
	
	/**
	 * parses the XML in a separate thread
	 */
	private void parseXML(){
		new Thread(new Runnable(){
			public void run(){
				LoaderPane loader = new LoaderPane("Parsing segment...");
				loader.setPercentage(10);
				mainTextTab.parseXML();
				loader.setPercentage(30);
				translationTab.parseXML();
				loader.setPercentage(50);
				wordEditorTab.parseXML();
				wordEditorTab.updateTree();
				loader.setPercentage(70);
				keywordsTab.parseXML();
				loader.setPercentage(101);
				
			}
		}).start();
	}
	
	/**
	 * makes and inits all components
	 */
	private void makeGUI() {
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

			
		wordEditorTab = new WordEditorTab(segment);
		translationTab = new TranslationTab(segment);
		mainTextTab = new MainTextTab(segment);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(LanguageRegistry.MAIN_TEXT_TITLE, mainTextTab);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_M); // M for 1st tab
		tabbedPane.addTab(LanguageRegistry.TRANSLATION_TAB_TITLE, translationTab);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_T); // T for 2nd tab
		tabbedPane.addTab(LanguageRegistry.WORD_EDITOR_LABEL, wordEditorTab);	
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_W); // W for 3rd tab
		
		tabbedPane.getActionMap().put("SwitchTo1", new AbstractAction("SwitchTo1"){
			public void actionPerformed(ActionEvent e){
				tabbedPane.setSelectedIndex(0);
			}
		});
		tabbedPane.getActionMap().put("SwitchTo2", new AbstractAction("SwitchTo2"){
			public void actionPerformed(ActionEvent e){
				tabbedPane.setSelectedIndex(1);
			}
		});
		tabbedPane.getActionMap().put("SwitchTo3", new AbstractAction("SwitchTo3"){
			public void actionPerformed(ActionEvent e){
				tabbedPane.setSelectedIndex(2);
			}
		});
		tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, Registry.CTRL_MASK_CROSS_PLATFORM), "SwitchTo1");
		tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, Registry.CTRL_MASK_CROSS_PLATFORM), "SwitchTo2");
		tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, Registry.CTRL_MASK_CROSS_PLATFORM), "SwitchTo3");
				
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(saveButton);
		buttonPanel.add(discardButton);		
		
		add(tabbedPane);
		add(buttonPanel, BorderLayout.SOUTH);
		pack();
	}
	
	/**
	 * this is called to save the contents of this segment before disposing of this JFrame
	 */
	public void closeWindow(boolean saveChanges){
		if(saveChanges){
			keywordsTab.saveToElement();
			translationTab.saveToElement();
			mainTextTab.saveToElement();
			wordEditorTab.saveToElement();
		}
		Main.openFrames.remove(this);
		TabHolder.updateView();
		TabHolder.segmentPanel.setSegmentButtonState(true);
		segmentOpen = false;
		this.dispose();
	}
}
