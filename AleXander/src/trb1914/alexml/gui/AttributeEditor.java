package trb1914.alexml.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.data.tree.AttributeTree;
import trb1914.alexml.data.tree.WordTag;
import trb1914.alexml.gui.tabs.segment.WordEditorTab;
import trb1914.alexml.gui.util.AttributePanel;
import trb1914.alexml.interfaces.ICloseWindow;
import trb1914.alexml.xml.XMLParser;

/**
 * this was originally not a class but a single method. This class
 * opens a tiny separate window that allows the user to edit
 * the attributes of the parent element.
 * @author Mees Gelein
 *
 */
public class AttributeEditor extends JFrame implements ICloseWindow{

	///The attribute we're editing
	private AttributeTree attribute;
	/**the parent WordEditorTab. This reference is needed to allow this object
	to update the Tree after edits are made*/
	private WordEditorTab wordEditorTab;
	///A list of all the AttributePanels of the current Element
	public static ArrayList<AttributePanel> allAttributePanels = new ArrayList<AttributePanel>();
	///The panel that holds all the attributePanels
	private JPanel attributePanelHolder = new JPanel();
	///the scrollPane that holds all the attributePanels
	private JScrollPane scrollPane;
	///if the attribute editor is opened
	private static boolean opened = false;
	///a reference to the type selection box
	private JComboBox typeBox;
	
	/**
	 * opens a separate tiny window that allows the user to edit this tag,its attributes
	 * and the value of those attributes
	 * @param attribute		the attr (tag) to edit
	 * @param p			the point the mouse was at when opening the current attribute(tag)
	 */
	public AttributeEditor(AttributeTree attr, Point p, WordEditorTab parent){
		if(!opened){
			opened = true;
			Main.openFrames.add(this);
			allAttributePanels = new ArrayList<AttributePanel>();
			attribute = attr;
			wordEditorTab = parent;
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					closeWindow(false);
				}
			});
			ImageIcon img = new ImageIcon(FileRegistry.APP_ICON);
			setIconImage(img.getImage());
			setSize(500,400);
			if(p == null){
				setLocationRelativeTo(null);
			}else{
				if(p.x < 0) p.x = 0;
				if(p.y < 0) p.y = 0;
				setLocation(p);
			}
			setTitle(attribute.word.textContent + ": " + attribute.element.getNodeName());
			setResizable(false);

			makeGUI();
			setVisible(true);
		}else{
			Debug.println("Another AttributeEditor windows is already open", this);
		}
	}

	/**
	 * creates and populates the GUI
	 */
	private void makeGUI(){
		Element element = attribute.element;
		typeBox = new JComboBox();
		for(int i = 0; i < AttributeTree.tags.length; i++){
			WordTag tag = AttributeTree.tags[i];
			typeBox.addItem(tag);
			if(tag.isOfSameType(attribute.element)){
				typeBox.setSelectedIndex(i);
				setTitle(attribute.word.textContent + ": " + tag.toString());
			}
		}

		final JLabel descLabel = new JLabel(((WordTag) typeBox.getSelectedItem()).getDescription());
		descLabel.setForeground(Color.GRAY);
		descLabel.setFont(descLabel.getFont().deriveFont(10f));

		JPanel fieldPanel = new JPanel(new BorderLayout());
		JPanel topPanel = new JPanel();
		fieldPanel.add(topPanel, BorderLayout.NORTH);
		JLabel nameLabel = new JLabel(LanguageRegistry.ATTRIBUTE_LABEL + ": ");
		topPanel.add(nameLabel); topPanel.add(typeBox);
		fieldPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		JPanel buttonPanel = new JPanel();
		JPanel labelPanel = new JPanel();
		labelPanel.add(descLabel);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(fieldPanel);
		panel.add(labelPanel, BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.add(panel);
		getContentPane().add(contentPanel);

		typeBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				setAttributeEditArea(((WordTag)typeBox.getSelectedItem()).getElement());
				setTitle(attribute.word.textContent + ": " + ((WordTag)typeBox.getSelectedItem()).toString());
				descLabel.setText(((WordTag)typeBox.getSelectedItem()).getDescription());
			}
		});

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

		buttonPanel.add(saveButton);
		buttonPanel.add(discardButton);

		attributePanelHolder.setLayout(new BorderLayout());
		scrollPane = new JScrollPane(attributePanelHolder);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		fieldPanel.add(scrollPane);

		setAttributeEditArea(element);
	}

	/**
	 * removes all previous children from the list and rebuilds it
	 * and then revalidates the component so the changes are shown
	 */
	public void updateAttributeEditorArea(){
		attributePanelHolder.removeAll();
		JPanel panel = new JPanel(new BorderLayout());
		attributePanelHolder.add(panel, BorderLayout.NORTH);
		for(int i = 0; i < allAttributePanels.size(); i++){
			panel.add(allAttributePanels.get(i), BorderLayout.NORTH);
			JPanel panel2 = new JPanel(new BorderLayout());
			panel.add(panel2);
			panel = panel2;
		}

		final AttributeEditor selfRef = this;

		JButton plusButton = new JButton(new ImageIcon(FileRegistry.PLUS_ICON_SMALL));
		plusButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				allAttributePanels.add(new AttributePanel("", "", selfRef));
				updateAttributeEditorArea();
			}
		});
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(plusButton, BorderLayout.NORTH);
		panel.add(buttonPanel);
		revalidate();
		repaint();
	}

	/**
	 * parses an element and populates the provided area with it.
	 * This turns the attributes an element has into a String
	 * @param n			the Node to populate the area with
	 */
	private void setAttributeEditArea(Node n){
		allAttributePanels = new ArrayList<AttributePanel>();
		NamedNodeMap map = n.getAttributes();
		for(int i = 0 ; i < map.getLength(); i++){
			AttributePanel a = new AttributePanel(map.item(i).getNodeName(), map.item(i).getNodeValue(), this);
			allAttributePanels.add(a);
			attributePanelHolder.add(a);
		}
		updateAttributeEditorArea();
	}

	/**
	 * sets the attribute according to the list of AttributePanels
	 * @param e		the element to set	
	 */
	private void setAttribute(Element e){
		XMLParser.removeAllAttributes(e);
		Debug.println("Length after XML removing: " + e.getAttributes().getLength(), this);
		for(int i = 0; i < allAttributePanels.size(); i++){
			Debug.println("saving attr:" + (i + 1), this);
			allAttributePanels.get(i).saveToElement(e);
		}
		Debug.println("Length after attribute adding: " + e.getAttributes().getLength(), this);
	}

	/**
	 * closes the current attributeEditor window
	 */
	public void closeWindow(boolean saveChanges) {
		if(saveChanges){
			XMLParser.xmlDocument.renameNode(attribute.element, null, ((WordTag)typeBox.getSelectedItem()).getElement().getNodeName());
			setAttribute(attribute.element);
			Debug.println(attribute.toString(), this);
		}
		boolean succes = Main.openFrames.remove(this);
		Debug.println("Removed frame from openList: " + succes, this);
		wordEditorTab.updateTree();
		AttributeEditor.opened = false;
		dispose();
	}

}
