package trb1914.alexml.data.tree;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Data object to represent a tag given to a word in the lw. 
 * This is the object shown in the JTree word tagging.
 * @author Mees Gelein
 */
public class AttributeTree {

	//a list of possible objects defined by XML and a flag once the XML has been parsed
	public static WordTag[] tags = null;
	public static boolean tagListInit = false;
	
	///the element that will be used for the lw
	public Element element;
	
	///the word that is the father of this element
	public Word word;
	
	///the WordTag of this object
	public WordTag tag;
	
	/**
	 * creates a new attributetree with the given element
	 * @param e
	 */
	public AttributeTree(Element e, Word w) {
		element = e;
		if(tagListInit){
			for(int i = 0; i < tags.length; i++){
				if(tags[i].isOfSameType(e)){
					tag = tags[i];
				}
			}
		}
		word = w;
	}
	
	/**
	 * returns a String representation of this object for use in the JTree word tagging
	 */
	public String toString(){
		StringBuilder builder = new StringBuilder();
		if(tag == null)builder.append(element.getNodeName());
		else builder.append(tag.toString());
		NamedNodeMap attributes = element.getAttributes();
		for(int i = 0; i < attributes.getLength(); i++){
			Attr attribute = (Attr) attributes.item(i);
			builder.append(" - ");
			builder.append(attribute.getNodeName());
			builder.append(":");
			builder.append(attribute.getNodeValue());
			builder.append(" ");
		}
		return builder.toString();
	}

}
