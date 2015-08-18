package trb1914.alexml.gui.tabs.main;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.xml.XMLParser;
/**
 * inits the Main GUI (toolbar and tabs)
 * @author Mees Gelein
 */
public class TabHolder extends JTabbedPane {

	//A flag of the current program State. When the programState is false, several buttons are disabled and
	//only a few buttons can be pressed. NOTE: Do NOT set this separately, use TabHolder.setProgramState(boolean);
	public static boolean programState = false;
	
	//The JPanel that displays the segment view. This is added to tabbedPane. 
	public static SegmentViewTab segmentPanel;

	//The JPanel that displays the xml-view. This is added to tabbedPane;
	public static XMLViewTab xmlPanel;

	//The PreviewTab (extends JPanel) that displays the HTML preview. This is added to tabbedPane.
	public static PreviewTab previewPanel;

	//The toolbar with all the icons for saving, opening, new file etc.
	public static MainToolBar toolBar;

	//Static reference to self to allow program state setting
	public static TabHolder tabbedPane;
	
	//used to display simple message at the bottom of the screen
	public static SystemMessager sysMsg;	
	
	
	/**
	 * creates the complete main Screen GUI. All the tabs and toolbar
	 */
	public TabHolder(){
		tabbedPane = this;
		toolBar = new MainToolBar();
		segmentPanel = new SegmentViewTab();
		xmlPanel = new XMLViewTab();
		previewPanel = new PreviewTab();
		sysMsg = new SystemMessager();
		
		
		setEnabled(false);
		addTab(LanguageRegistry.SEGMENT_VIEW_NAME, segmentPanel);
		setMnemonicAt(0, KeyEvent.VK_S); //S for segment view
		addTab(LanguageRegistry.PREVIEW_VIEW_NAME, previewPanel);
		setMnemonicAt(1, KeyEvent.VK_P); //P for preview
		addTab(LanguageRegistry.XML_VIEW_NAME, xmlPanel);
		setMnemonicAt(2, KeyEvent.VK_X); //X for xml view
		
		addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				xmlPanel.getXMLTextArea().setText(XMLParser.getString());
				xmlPanel.getXMLTextArea().setCaretPosition(0);
				if(getSelectedIndex() == 0){ segmentPanel.updateSegmentList();}
				if(getSelectedIndex() == 1){ }
				if(getSelectedIndex() == 2){ xmlPanel.update();}
			}
		});
		
		getActionMap().put("SwitchTo1", new AbstractAction("SwitchTo1"){
			public void actionPerformed(ActionEvent e){
				setSelectedIndex(0);
			}
		});
		getActionMap().put("SwitchTo2", new AbstractAction("SwitchTo2"){
			public void actionPerformed(ActionEvent e){
				setSelectedIndex(1);
			}
		});
		getActionMap().put("SwitchTo3", new AbstractAction("SwitchTo3"){
			public void actionPerformed(ActionEvent e){
				setSelectedIndex(2);
			}
		});
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, Registry.CTRL_MASK_CROSS_PLATFORM), "SwitchTo1");
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, Registry.CTRL_MASK_CROSS_PLATFORM), "SwitchTo2");
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, Registry.CTRL_MASK_CROSS_PLATFORM), "SwitchTo3");
		
		setBorder(BorderFactory.createEmptyBorder(4,8,4,4));
	}
	/**
	 * controls the enabled-state of some components to allow for disabling when 
	 * no or an invalid file is loaded
	 * @param b			true = enabled, false = disabled
	 */
	public static void setProgramState(boolean b){
		programState = b;
		segmentPanel.setSegmentButtonState(b);
		segmentPanel.getSegmentList().setEnabled(b);
		toolBar.setState(b);
		tabbedPane.setEnabled(b);
	}

	/**
	 * updates the view of the complete application. At least, the content
	 * @param toDefault		if set to true the segmentList will be emptied
	 */
	public static void updateView() {
		segmentPanel.updateSegmentList();
		xmlPanel.update();
		previewPanel.updatePreview(xmlPanel.getXMLTextArea().getText());
	}
}
