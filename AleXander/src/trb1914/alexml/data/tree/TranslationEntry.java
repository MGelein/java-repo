package trb1914.alexml.data.tree;

import org.w3c.dom.Element;

import trb1914.alexml.data.Note;
import trb1914.alexml.xml.XMLTags;

/**
 * this class holds all the data that is needed to create one single translation.
 * Multiple instances of this can be added to one segment to allow multiple translations
 * for example for different languages or usages
 * @author Mees Gelein
 *
 */
public class TranslationEntry {

	/**All the notes contained in this translation*/
	public Note[] transNotes;
	/**Reference to the translation element*/
	public Element transElement;
	/**Reference to the translation Desription element*/
	public Element transDescElement;
	/**The string that is the actual translation*/
	public String translationString;
	/**The String that is the translation description*/
	public String translationDescription;
	/**The author of this translation*/
	public String author =  "Unknown Author";
	/**The language of this translation*/
	public String language = "EN";
	
	/**
	 * the string representation of the translation is the Author. Could be changed
	 */
	public String toString(){
		return author;
	}
	
	/**
	 * turns the data into a String that is then parsed into an XML object. Not ideal. :(
	 * TODO: how to do this better?
	 * @return
	 */
	public String getTranslationText(){
		//insertEmphasis();
		String s = translationString;
		s = s.replaceAll("@", "<emph>");
		s = s.replaceAll("#", "</emph>");
		String targetString = translationDescription.replaceAll("\\{", "");
		targetString = targetString.replaceAll("\\}", "");
		targetString = "<ref target=\"" + targetString + "\">";
		translationDescription = translationDescription.replaceAll("\\{", targetString);
		translationDescription = translationDescription.replaceAll("\\}", "</ref>");		
		s = "<" + XMLTags.TRANSLATION + " " + XMLTags.LANG + "='" + language + "'><" + XMLTags.TRANSLATION_DESC + ">" + translationDescription + "<author>" + author + "</author>"
		+ "</" + XMLTags.TRANSLATION_DESC + ">"+s+"</" + XMLTags.TRANSLATION + ">";
		s = Note.insertNotes(transNotes, s);
		return s;
	}
}
