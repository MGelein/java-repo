package trb1914.alexml.gui.tabs.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListModel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import trb1914.alexml.Preferences;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.gui.DocSettingFrame;
import trb1914.alexml.gui.SegmentEditor;
import trb1914.alexml.gui.util.SegmentListRenderer;
import trb1914.alexml.xml.XMLParser;
import trb1914.alexml.xml.XMLTags;

/**
 * the segment-view Tab.
 * @author Mees Gelein
 */
public class SegmentViewTab extends JPanel{

	//the font size of the segmentList
	private static final int FONT_SIZE = 20;

	//The JList that displays the segments
	private JList segmentList;

	//This JButton adds a new segment to the bottom of the segment list
	private JButton addSegmentButton;

	//This JButton opens the currently selected segment
	private JButton openSegmentButton;

	//This JButton moves the currently selected segment up in the segment list 
	private JButton moveSegDownButton;

	//This JButton moves the currently selected segment down in the segment list
	private JButton moveSegUpButton;

	//This JButton tries to remove the currently selected segment
	private JButton deleteSegmentButton;

	//This JButton opens the Document Settings Frame
	private JButton docSettingsButton;

	//the toolbar to allow segment creation, deletion and moving around
	private JToolBar segMovePanel;

	//the layout of this panel
	private BorderLayout panelLayout;
	
	/**
	 * creates a new segment view
	 */
	public SegmentViewTab() {
		setLayout(new BorderLayout());
		segmentList = new JList<Element>();
		segmentList.setCellRenderer(new SegmentListRenderer(FileRegistry.getNormalFont(FONT_SIZE)));
		segmentList.setEnabled(false);

		//open segmentEditor on double click
		segmentList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() >= 2){
					new SegmentEditor(segmentList.getSelectedIndex(), 0);
				}
			}
		});

		//open segmentEditor on enter
		segmentList.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					new SegmentEditor(segmentList.getSelectedIndex(), 0);
				}else if(e.getKeyCode() == KeyEvent.VK_DELETE){
					removeSelectedSegment();
				}
			}
		});

		//Action with hotkeys
		segmentList.getActionMap().put("NewSegment", new AbstractAction("NewSegment"){
			public void actionPerformed(ActionEvent e){
				addNewSegment();
			}
		});
		segmentList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Registry.CTRL_MASK_CROSS_PLATFORM), "NewSegment");

		segmentList.getActionMap().put("OpenDocSet", new AbstractAction("OpenDocSet"){
			public void actionPerformed(ActionEvent e){
				new DocSettingFrame();
			}
		});
		segmentList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, Registry.CTRL_MASK_CROSS_PLATFORM), "OpenDocSet");

		moveSegUpButton = new JButton(new ImageIcon(FileRegistry.UP_ARROW));
		moveSegUpButton.setEnabled(false);
		moveSegUpButton.setToolTipText(LanguageRegistry.MOVE_SEGMENT_UP_MESSAGE);
		moveSegUpButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveSegment(1);
			}
		});

		moveSegDownButton = new JButton(new ImageIcon(FileRegistry.DOWN_ARROW));
		moveSegDownButton.setEnabled(false);
		moveSegDownButton.setToolTipText(LanguageRegistry.MOVE_SEGMENT_DOWN_MESSAGE);
		moveSegDownButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveSegment(-1);
			}
		});


		deleteSegmentButton = new JButton(new ImageIcon(FileRegistry.DELETE_ICON));
		deleteSegmentButton.setEnabled(false);
		deleteSegmentButton.setToolTipText(LanguageRegistry.DELETE_SEGMENT_MESSAGE);
		deleteSegmentButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeSelectedSegment();
			}
		});

		addSegmentButton = new JButton(new ImageIcon(FileRegistry.PLUS_ICON));
		addSegmentButton.setToolTipText(LanguageRegistry.NEW_SEGMENT_MESSAGE);
		addSegmentButton.setEnabled(false);
		addSegmentButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				addNewSegment();
			}
		});

		openSegmentButton = new JButton(new ImageIcon(FileRegistry.PENCIL_ICON));
		openSegmentButton.setToolTipText(LanguageRegistry.OPEN_SEGMENT_MESSAGE);
		openSegmentButton.setEnabled(false);
		openSegmentButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				new SegmentEditor(segmentList.getSelectedIndex(), 0);
			}			
		});

		docSettingsButton = new JButton(new ImageIcon(FileRegistry.DOC_SETTING_ICON));
		docSettingsButton.setToolTipText(LanguageRegistry.DOC_SETTING_BUTTON);
		docSettingsButton.setEnabled(false);
		docSettingsButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new DocSettingFrame();
			}
		});


		segMovePanel = new JToolBar(LanguageRegistry.SEGMENT_TOOLBAR_LABEL, JToolBar.VERTICAL);
		segMovePanel.add(addSegmentButton);
		segMovePanel.add(deleteSegmentButton);
		segMovePanel.addSeparator();
		segMovePanel.add(moveSegUpButton);
		segMovePanel.add(moveSegDownButton);
		segMovePanel.addSeparator();
		segMovePanel.add(openSegmentButton);
		segMovePanel.addSeparator();
		segMovePanel.add(docSettingsButton);


		JScrollPane segmentScrollPane = new JScrollPane(segmentList);

		panelLayout = new BorderLayout();
		String BorderLayoutString = BorderLayout.EAST;
		switch(Preferences.mainToolBarPos){
		case "East":
			BorderLayoutString = BorderLayout.EAST;
			segMovePanel.setOrientation(JToolBar.VERTICAL);
			break;
		case "North":
			BorderLayoutString = BorderLayout.NORTH;
			segMovePanel.setOrientation(JToolBar.HORIZONTAL);
			break;
		case "West":
			BorderLayoutString = BorderLayout.WEST;
			segMovePanel.setOrientation(JToolBar.VERTICAL);
			break;
		case "South":
			BorderLayoutString = BorderLayout.SOUTH;
			segMovePanel.setOrientation(JToolBar.HORIZONTAL);
			break;
		}

		setLayout(panelLayout);
		add(segmentScrollPane, BorderLayout.CENTER);
		add(segMovePanel, BorderLayoutString);
	}

	/**
	 * adds a new empty segment at the end of the list and updates it
	 */
	private void addNewSegment(){
		XMLParser.addEmptySegment();
		TabHolder.segmentPanel.updateSegmentList();
		segmentList.setSelectedIndex(segmentList.getModel().getSize() - 1);
	}

	/**
	 * removes the segment that is currently selected
	 */
	private void removeSelectedSegment(){
		if(JOptionPane.showConfirmDialog(null, LanguageRegistry.DELETE_SEGMENT_WARNING) == JOptionPane.YES_OPTION){
			XMLParser.removeSegment(segmentList.getSelectedIndex());
			TabHolder.updateView();
		}
	}

	/**
	 * returns the current toolbar constraints
	 * @return
	 */
	public String getToolBarOrientation(){
		String s = (String) panelLayout.getConstraints(segMovePanel);
		if(s == null){
			s = BorderLayout.EAST;
		}
		return s;
	}

	/**
	 * moves the selected segment up or down, depending on given integer
	 * @param i		
	 */
	private void moveSegment(int i) {
		boolean doable = false;
		if(i == -1){
			doable = (segmentList.getSelectedIndex() < segmentList.getModel().getSize() - 1);
		}else if(i == 1){
			doable = (segmentList.getSelectedIndex() > 0);
		}
		if(doable){
			if(i == -1) i = 1;
			else i = -1;
			ListModel<Element> model = segmentList.getModel();
			Element[] listData = new Element[model.getSize()];
			NodeList segmentNodeList = XMLParser.getNodeListOf("//" + XMLTags.SEGMENT);
			for(int j = 0; j <model.getSize();j++){
				listData[j] = model.getElementAt(j);
			}
			Element swapA = listData[segmentList.getSelectedIndex()];
			Element swapB = listData[segmentList.getSelectedIndex() + i];
			listData[segmentList.getSelectedIndex()] = swapB;
			listData[segmentList.getSelectedIndex() + i] = swapA;
			int prevSel = segmentList.getSelectedIndex();
			segmentList.setListData(listData);
			segmentList.setSelectedIndex(prevSel + i);

			Element parent = (Element) XMLParser.getNodeAt("//" + XMLTags.TEXT);
			Element[] segments = new Element[segmentNodeList.getLength()];
			for(int j = 0;j < segments.length; j++){
				segments[j] = (Element) segmentNodeList.item(j);
			}
			Node swapNodeA = segments[prevSel];
			Node swapNodeB = segments[prevSel + i];
			segments[prevSel] = (Element) swapNodeB;
			segments[prevSel + i] = (Element) swapNodeA;
			for(int j = 0; j < segments.length; j++){
				segments[j].setAttribute("n", Integer.toString(j + 1));
				parent.appendChild(segments[j]);
			}
			updateSegmentList();
		}

	}
	/**
	 * updates the contents of the segment list.
	 */
	public void updateSegmentList(){
		int prevSelIndex = getSegmentList().getSelectedIndex();
		
		NodeList nodeList = XMLParser.getNodeListOf("//" + XMLTags.SEGMENT);
		Element[] segments = new Element[nodeList.getLength()];
		for(int j = 0;j < segments.length; j++){
			segments[j] = (Element) nodeList.item(j);
		}
		for(int j = 0; j < segments.length; j++){
			segments[j].setAttribute("n", Integer.toString(j + 1));
		}
		getSegmentList().setListData(segments);
		if(prevSelIndex != -1) getSegmentList().setSelectedIndex(prevSelIndex);
		else getSegmentList().setSelectedIndex(0);
		getSegmentList().revalidate();
		getSegmentList().repaint();
	}

	/**
	 * returns the JList that contains the segment references
	 * @return
	 */
	public JList<Element> getSegmentList(){
		return segmentList;
	}

	/**
	 * sets the state of all the buttons associated with the segment view
	 * @param state		on = true, off = false
	 */
	public void setSegmentButtonState(boolean state){
		openSegmentButton.setEnabled(state);
		addSegmentButton.setEnabled(state);
		deleteSegmentButton.setEnabled(state);
		moveSegUpButton.setEnabled(state);
		moveSegDownButton.setEnabled(state);
		docSettingsButton.setEnabled(state);
	}
}
