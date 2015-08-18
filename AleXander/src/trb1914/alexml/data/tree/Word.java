package trb1914.alexml.data.tree;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import trb1914.alexml.Debug;
import trb1914.alexml.xml.XMLParser;
import trb1914.alexml.xml.XMLTags;
/**
 * A data object to hold all the tags for a specific word
 * @author Mees Gelein
 */
public class Word {
	/**List of all the tags of this word*/
	public ArrayList<Element> tags = new ArrayList<Element>();
	/**The textContent of the word.*/
	public String textContent = "";
	/**The indices of the words we are talking about*/
	public Vector<Integer> indices = new Vector<Integer>();
	
	/**
	 * Creates a new word object.
	 * @param w		the starting w tag
	 */
	public Word(Element w) {
		addTag(w);
		textContent = w.getTextContent();
		String indexString = w.getAttribute(XMLTags.INDEX);
		String[] indexStrings = indexString.split(",");
		int index = 0;
		for(int i = 0; i < indexStrings.length; i++){
			index = -1;
			try{
				index = Integer.parseInt(indexStrings[i]);
			}catch(Exception e){
				
			}
			if(index != -1){
				indices.add(index);
			}
		}
	}
	
	/**
	 * removes the tag from the word.
	 * @param tag
	 */
	public void removeTag(Element tag){
		tags.remove(tag);
	}
	
	/**
	 * adds a tag to the existing word. Only adds the current tag and not its children
	 * @param tag
	 */
	public void addTag(Element tag){
		Element e = XMLParser.xmlDocument.createElement(tag.getNodeName());
		NamedNodeMap attributes = tag.getAttributes();
		for(int i = 0; i < attributes.getLength(); i++){
			Attr attribute = (Attr) attributes.item(i);
			e.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
		}
		tags.add(e);
		if(tag.hasChildNodes()){
			if(tag.getFirstChild().getNodeType() == Element.ELEMENT_NODE){
				addTag((Element) tag.getFirstChild());
			}
		}
	}
	
	/**
	 * gets the representation of this object as a defaultmutableTreeNode;
	 * @return
	 */
	public DefaultMutableTreeNode getTreeNode(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(textContent);
		root.setUserObject(this);
		for(int i = 0; i < tags.size(); i++){
			AttributeTree attribute = new AttributeTree(tags.get(i), this);
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(attribute);
			root.add(child);
		}
		return root;
	}
	
	/**
	 * moves the given attribute up or down the list of the given word
	 * @param attribute
	 * @param dir
	 * @return
	 */
	public boolean moveTag(AttributeTree attribute, int dir){
		boolean moved = false;
		int index = tags.indexOf(attribute.element);
		Debug.print("Found element at: " + index + "\n");
		if(index > -1){
			if((dir == -1) && (index > 0)){
				Element tagA = tags.get(index);
				Element tagB = tags.get(index - 1);
				tags.set(index, tagB);
				tags.set(index - 1, tagA);
				moved = true;
			}else if((dir == 1) && (index < (tags.size() - 1))){
				Element tagA = tags.get(index);
				Element tagB = tags.get(index + 1);
				tags.set(index, tagB);
				tags.set(index + 1, tagA);
				moved = true;
			}
		}
		return moved;
	}
	
	/**
	 * returns the index of the given tag in the tags list of this word
	 * @param tag
	 * @return
	 */
	public int getIndexOfTag(Element tag){
		return tags.indexOf(tag);
	}
	
	/**
	 * returns the DOM Element of this word
	 * @return
	 */
	public Element getXMLElement(){
		Element rootNode = tags.get(0);
		Element prevNode = rootNode;
		for(int i = 1; i < tags.size(); i++){
			prevNode.appendChild(tags.get(i));
			prevNode = tags.get(i);
		}
		StringBuilder indexText = new StringBuilder();
		for(int i = 0; i < indices.size(); i++){
			indexText.append(indices.get(i));
			indexText.append(",");
		}
		if(indexText.length() > 1){
			String indexString = indexText.toString();
			int lastCommaIndex = indexString.lastIndexOf(",");
			indexString = indexString.substring(0, lastCommaIndex);
			rootNode.setAttribute(XMLTags.INDEX, indexString);
		}
		tags.get(tags.size() - 1).setTextContent(textContent);
		return rootNode;
	}
	
	/**
	 * to properly display in JTree
	 */
	public String toString(){
		return textContent;
	}
}
