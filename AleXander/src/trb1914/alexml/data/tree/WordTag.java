package trb1914.alexml.data.tree;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import trb1914.alexml.data.LanguageRegistry;
/**
 * Small data class to make the attribute editor combo boxes work
 * @author Mees Gelein
 *
 */
public class WordTag {

	//the element
	private Node _node;
	//the display name in the JComboBox
	private String _name = "";
	//the description of this tag
	private String _desc = LanguageRegistry.NO_DESCRIPTION;
	/**
	 * new WordTag object
	 * @param name		the displayName
	 * @param element	the XML-node
	 */
	public WordTag(String name, Node node){
		_node = node;
		_name = name;
	}
	/**
	 * sets the description of this tag
	 * @param s
	 */
	public void setDescription(String s){
		_desc = s;
	}
	/**
	 * returns the description of this tag
	 * @return
	 */
	public String getDescription(){
		return _desc;
	}
	/**
	 * returns a String representation of this object
	 */
	public String toString(){
		return _name;
	}
	/**
	 * returns the XML-Node saved by this object
	 * @return
	 */
	public Node getElement(){
		return _node;
	}
	/**
	 * returns true when the given element has the same structure (attributes and names)
	 * as this wordTag.
	 * @return
	 */
	public boolean isOfSameType(Element e){
		if(e.getNodeName().equals(_node.getNodeName())){
			NamedNodeMap attributes = _node.getAttributes();
			if(attributes!= null){
				for(int i = 0; i < attributes.getLength(); i++){
					if(!e.hasAttribute(attributes.item(i).getNodeName())){
						return false;
					}
				}
				return true;
			}
			return false;
		}else{
			return false;
		}
	}
}
