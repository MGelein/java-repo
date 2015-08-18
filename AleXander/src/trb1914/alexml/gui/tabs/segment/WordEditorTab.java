package trb1914.alexml.gui.tabs.segment;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.tree.AttributeTree;
import trb1914.alexml.data.tree.Word;
import trb1914.alexml.data.tree.WordTag;
import trb1914.alexml.gui.AttributeEditor;
import trb1914.alexml.gui.WordEditor;
import trb1914.alexml.xml.XMLParser;
import trb1914.alexml.xml.XMLTags;
/**
 * this class allows the user to edit the XMLTAGS.LW list using
 * a JTree object for easy viewing and some pop up windows for
 * entering data.
 * @author Mees Gelein
 *
 */
public class WordEditorTab extends JPanel{

	/**
	 * A small static thingy that will run during startup
	 */
	static{
		//set the JTree icons		
		UIManager.put("Tree.leafIcon", new ImageIcon(FileRegistry.LEAF_ICON));
		UIManager.put("Tree.openIcon", new ImageIcon(FileRegistry.MAP_ICON));
		UIManager.put("Tree.closedIcon", new ImageIcon(FileRegistry.MAP_ICON));

	}
	//the standard font size
	private static int FONT_SIZE = 15;

	//The current list we're editing
	private Element lwElement;

	//The list of accepted tags. Is loaded once the wordEditor is loaded
	private static Element tagsElement = null;

	//The tree that displays the lwElement
	private JTree tree;

	//The rootNode of the JTree, this Node is invisible but contains all the other
	//nodes that are added to the JTree.
	private DefaultMutableTreeNode rootNode;

	//contains a list of all the words
	private Word[] words;

	//contains a ref to the segment we're editing
	private Element segment;

	/**
	 * creates a new WordEditor. This is a child of the SegmentEditor's tabbedPane.
	 * @param segment	a ref to the segment we're currently editing
	 */
	public WordEditorTab(Element cSegment) {
		lwElement = (Element) cSegment.getElementsByTagName(XMLTags.LIST_WORDS).item(0);
		segment = cSegment;
		if(tagsElement ==null ) {tagsElement = XMLParser.getTagsConfig();}
		makeGUI();
	}

	/**
	 * Creates the main GUI and listeners and stuff
	 */
	private void makeGUI() {
		NodeList nodeList = tagsElement.getElementsByTagName(XMLTags.ENTRY);
		AttributeTree.tags = new WordTag[nodeList.getLength()];
		for(int i = 0; i < nodeList.getLength(); i++){
			Element e = (Element) nodeList.item(i);
			String name = e.getAttribute(XMLTags.NAME);
			Node child = e.getChildNodes().item(1);
			WordTag tag = new WordTag(name, child);
			if(e.hasAttribute(XMLTags.DESCRIPTION)){
				tag.setDescription(e.getAttribute(XMLTags.DESCRIPTION));
			}
			AttributeTree.tags[i] = tag;
		}
		AttributeTree.tagListInit = true;

		setLayout(new BorderLayout());

		rootNode = new DefaultMutableTreeNode("root");
		tree = new JTree(rootNode) { //anonymous override to prevent nodes from collapsing when double clicked
			protected void setExpandedState(TreePath path, boolean state) {

				if (state) {
					super.setExpandedState(path, state);
				}
			}
		};
		tree.setRootVisible(false);

		Icon leafIcon = ((DefaultTreeCellRenderer) tree.getCellRenderer()).getLeafIcon();

		Icon mapIcon = ((DefaultTreeCellRenderer) tree.getCellRenderer()).getOpenIcon();

		//open on double click
		tree.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() >= 2){
					tree.setSelectionRow(tree.getRowForLocation(e.getX(), e.getY()));
					openSelectedTreeElement(new Point(e.getXOnScreen(), e.getYOnScreen()));
				}
			}
		});

		//open on enter pressed
		tree.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					openSelectedTreeElement();
				}else if (e.getKeyCode() == KeyEvent.VK_DELETE){
					removeSelectedTreeElement();
				}
			}
		});

		JScrollPane treeScrollPane = new JScrollPane(tree);
		add(treeScrollPane);

		JToolBar buttonPanel = new JToolBar(JToolBar.VERTICAL);
		buttonPanel.setFloatable(false);//make it immovable

		JButton moveUpButton = new JButton(new ImageIcon(FileRegistry.UP_ARROW));
		moveUpButton.setToolTipText(LanguageRegistry.MOVE_ENTRY_UP_LABEL);
		moveUpButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveWord(-1);
			}
		});

		JButton moveDownButton = new JButton(new ImageIcon(FileRegistry.DOWN_ARROW));
		moveDownButton.setToolTipText(LanguageRegistry.MOVE_ENTRY_DOWN_LABEL);
		moveDownButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				moveWord(1);
			}
		});

		JButton newWordButton = new JButton(mapIcon);
		newWordButton.setToolTipText(LanguageRegistry.NEW_WORD_LABEL);
		newWordButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				appendWord(" ", false);
			}
		});

		JButton newAttributeButton = new JButton(leafIcon);
		newAttributeButton.setToolTipText(LanguageRegistry.NEW_ATTRIBUTE_LABEL);
		newAttributeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateTree();
				Object userObject = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();
				Word w;
				int foundIndex = -1;
				if(userObject instanceof AttributeTree){
					AttributeTree attribute = (AttributeTree) userObject;
					w = attribute.word;
					foundIndex = w.getIndexOfTag(attribute.element);
				}else{
					w = (Word) userObject;
				}
				Element element = XMLParser.xmlDocument.createElement("w");
				element.setAttribute("type", "noun");
				int tagCount = w.tags.size();
				int index = tree.getSelectionRows()[0];
				tree.setSelectionRow(index + (tagCount - foundIndex));
				w.addTag(element);
				updateTree();
				userObject = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();
			}
		});

		JButton removeEntryButton = new JButton(new ImageIcon(FileRegistry.DELETE_ICON));
		removeEntryButton.setToolTipText(LanguageRegistry.DELETE_ENTRY_LABEL);
		removeEntryButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeSelectedTreeElement();
			}
		});
		buttonPanel.add(newWordButton);
		buttonPanel.add(newAttributeButton);
		buttonPanel.addSeparator();
		buttonPanel.add(removeEntryButton);
		buttonPanel.addSeparator();
		buttonPanel.add(moveUpButton);
		buttonPanel.add(moveDownButton);		
		add(buttonPanel, BorderLayout.EAST);


		Dimension prefSize = moveUpButton.getPreferredSize();
		prefSize = new Dimension(prefSize.width, (int) (prefSize.height * .75));
		newWordButton.setSize(prefSize);
		newWordButton.setMaximumSize(prefSize);
		newWordButton.setPreferredSize(prefSize);
		newAttributeButton.setSize(prefSize);
		newAttributeButton.setMaximumSize(prefSize);
		newAttributeButton.setPreferredSize(prefSize);

	}

	/**
	 * removes the currently selected element in the 
	 * JTree
	 */
	private void removeSelectedTreeElement(){
		if(tree.getSelectionCount() > 0){
			Object userObject = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();
			if(userObject instanceof Word){
				Word w = (Word) userObject;
				removeWord(w);
			}else if(userObject instanceof AttributeTree){
				AttributeTree attribute = (AttributeTree) userObject;
				Word w = attribute.word;
				w.removeTag(attribute.element);
			}
			updateTree();
		}
	}

	/**
	 * opens the selected tree element in the correct editor.
	 * If it s an attribute in the AttributeEditor if its a word
	 * in the singleWordEditor;
	 */
	private void openSelectedTreeElement(Point p){
		Object userObject = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();
		if(userObject instanceof Word){
			new WordEditor((Word) userObject, p);
		}else if(userObject instanceof AttributeTree){
			new AttributeEditor((AttributeTree) userObject, p, this);
		}else{
			Debug.println("Couldn't decode type of clicked tree cell", this);
		}
	}

	/**
	 * opens the selected tree element in the correct editor.
	 * If it s an attribute in the AttributeEditor if its a word
	 * in the singleWordEditor;
	 */
	private void openSelectedTreeElement(){
		openSelectedTreeElement(new Point(0,0));
	}

	/**
	 * moves the given word in a direction controlled by the passed integer
	 * @param dir		-1 or 1
	 */
	private void moveWord(int dir) {
		Object pathComponent = tree.getLastSelectedPathComponent();
		if(pathComponent != null){ 
			Object userObject = ((DefaultMutableTreeNode) pathComponent).getUserObject();
			int selectedRow = tree.getSelectionRows()[0];
			if(userObject instanceof AttributeTree){
				AttributeTree attribute = (AttributeTree) userObject;
				if(attribute.word.moveTag(attribute, dir)){
					updateTree();
					tree.setSelectionRow(selectedRow + dir);
				}
			}else if(userObject instanceof Word){
				Word userWord = (Word) userObject;
				int index = -1;
				for(int i = 0; i < words.length; i++){
					if(userWord == words[i]){
						index = i;
					}
				}
				if(index > -1){
					if((dir == 1) && (index < words.length - 1)){
						Word wordA = words[index];
						Word wordB = words[index + dir];
						words[index] = wordB;
						words[index + dir] = wordA;
					}else if((dir == -1) && (index > 0)){
						Word wordA = words[index];
						Word wordB = words[index + dir];
						words[index] = wordB;
						words[index + dir] = wordA;
					}
					updateTree();
				}
			}
		}
	}

	/**
	 * sets the content of the word to match the provided String and sets indices
	 * @param w			the word to change	
	 * @param s			the String to set it to
	 * @param indices	the indices of the words
	 */
	public void setWordTextContent(Word w, String s, Vector<Integer> indices){
		for(int i = 0; i < words.length; i++){
			if(words[i] == w){
				words[i].textContent = s;
				words[i].indices.clear();
				for(int j = 0; j < indices.size(); j++){
					words[i].indices.add(indices.get(j));
				}
			}
		}
		updateTree();//updates the tree after this word value has been changed to reflect changes in the GUI
	}

	/**
	 * This function parses the lwElement
	 */
	public void parseXML() {
		NodeList wordNodeList = lwElement.getChildNodes();
		words = new Word[wordNodeList.getLength()];
		for(int i = 0; i < wordNodeList.getLength(); i++){
			Element cWordElement = (Element) wordNodeList.item(i);
			words[i] = new Word(cWordElement);
		}

		if(wordNodeList.getLength() < 1){
			appendWord(" ", true);
		}
		updateTree();
	}	

	/**
	 * updates the treeView to reflect any changes that may have been made
	 * to this object. Basically empties the tree and then recreates it with the
	 * new, edited, list
	 */
	public void updateTree(){
		System.out.println("[WordEditorTab]: Updating JTree");
		int prevIndex = 0;
		if(tree.getSelectionCount() != 0){
			prevIndex = tree.getSelectionRows()[0];
		}
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		rootNode.removeAllChildren();
		for(int i = 0; i < words.length; i++){
			rootNode.add(words[i].getTreeNode());
		}
		model.reload();
		for(int i = 0; i < tree.getRowCount(); i++){
			tree.expandRow(i);
		}
		tree.setFont(FileRegistry.getNormalFont(FONT_SIZE));
		tree.setRowHeight(-1);
		tree.setSelectionRow(prevIndex);
	}

	/**
	 * adds a word to the end of the list. The silent parameter is there 
	 * to allow the program to append a word to this list in case the lw
	 * list has no entries to start with.
	 * @param name		the name of the word to append
	 * @param silent	add with or without dialog?
	 */
	private void appendWord(String name, boolean silent){
		Word[] backup = new Word[words.length + 1];
		for(int i = 0; i < words.length; i++){
			backup[i] = words[i];
		}
		Element e = XMLParser.xmlDocument.createElement(XMLTags.WORD);
		e.setTextContent(name);
		e.setAttribute("type", "undefined");
		backup[words.length] = new Word(e);
		words = backup;
		updateTree();
		//open editor after creating if not added in silent mode
		if(!silent){
			tree.setSelectionRow(tree.getRowCount() - 2);
			tree.scrollRowToVisible(tree.getRowCount() - 1);
			Object userObject = ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();
			if(userObject instanceof Word){
				openSingleWordEditor((Word) userObject);
			}
		}
	}

	/**
	 * opens the singleWordEditor using a default location. Useful when the 
	 * function is being called outside of a mouseListener.
	 * @param word
	 */
	private void openSingleWordEditor(Word word) {
		Point point = Main.main.getLocation();
		point.x += 200;
		point.y += 200;
		new WordEditor(word, point);
	}

	/**
	 * removes a word from the words list
	 * @param name
	 */
	private void removeWord(Word w){
		boolean found = false;
		Word[] backup = new Word[words.length - 1];
		for(int i = 0; i < words.length; i++){
			if(words[i] == w){
				found = true;
			}else if(!found){
				backup[i] = words[i];
			}else if(found){
				backup[i - 1] = words[i];
			}
		}
		words = backup;
		updateTree();
	}

	/**
	 * turns the wordslist back into an xmlElement.
	 * @return		the finished lw-element
	 */
	public Element getLW(){
		Element lw = XMLParser.xmlDocument.createElement(XMLTags.LIST_WORDS);
		for(int i = 0; i < words.length; i++){
			lw.appendChild(words[i].getXMLElement());
		}
		return lw;
	}

	/**
	 * saves the changes back to the segment we're editing
	 */
	public void saveToElement(){
		try{
			segment.removeChild(segment.getElementsByTagName(XMLTags.LIST_WORDS).item(0));
		}catch(Exception e){
			System.out.println("[WordEditorTab]: Couldn't find lw element");
		}
		segment.appendChild(getLW());
	}

}
