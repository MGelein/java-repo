package trb1914.alexml.data;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * a data object class to hold the information of a Note
 * including methods to turn it into a XML string
 * @author Mees
 *
 */
public class Note {

	///the content string, the textContent of this Node
	public String content = "NoContent";
	
	///the ID of this note, the n attribute of the <note> tag
	public String ID = "Undefined";
	
	///the type attribute of the <note> tag
	public String type = "appCrit";
	
	/**
	 * returns a String representation of the XML-object of the data in this object
	 * @return
	 */
	public String getXMLString(){
		return "<note n=\"" + ID + "\" type=\"" + type + "\">" + content + "</note>";
	}
	
	/**
	 * inserts a Note in the given string
	 * @param notes
	 * @param s
	 * @return
	 */
	public static String insertNotes(Note[] notes, String s){
		int count = 0; String start, end;
		for(int i = 0; i < s.length() ; i++ ){
			if(s.charAt(i) == LanguageRegistry.NOTE_CHAR){
				start = s.substring(0, i);
				end = s.substring(i + 1);
				notes[count].ID = Integer.toString(count + 1);
				s = start + notes[count].getXMLString() + end;
				count++;
			}
		}
		return s;
	}
	
	/**
	 * parses the given NodeList into Note objects
	 * @param nodeList
	 * @param notes
	 * @return
	 */
	public static Note[] parseNotes(NodeList nodeList, Note[] notes) {
		notes = new Note[nodeList.getLength()]; Element e;
		for(int i = 0; i < nodeList.getLength(); i++){
			e = (Element) nodeList.item(i);
			notes[i] = new Note();
			notes[i].content = e.getTextContent();
			notes[i].type = e.getAttribute("type");
			notes[i].ID = e.getAttribute("n");
		}
		return notes;
	}
	
	/**
	 * adds a new empty node at the given index
	 * @param notes
	 * @param index
	 * @return
	 */
	public static Note[] addNewEmptyNote(Note[] notes, int index){
		return addNewEmptyNote(notes, index, LanguageRegistry.APP_CRIT);
	}
	
	/**
	 * removes the note at the given index
	 * @param notes
	 * @param index
	 * @return
	 */
	public static Note[] removeNote(Note[] notes, int index){
		if(notes.length > 1){
			Note[] newNotes = new Note[notes.length - 1];
			boolean found = false;
			for(int i = 0; i < newNotes.length;i++){
				if(i == index){
					found = true;
					newNotes[i] = notes[i+1];
				}else if(found){
					newNotes[i] = notes[i+1];
				}else{
					newNotes[i] = notes[i];
				}
			}
			return newNotes;
		}
		return null;
	}
	
	/**
	 * adds a new empty node at the given index with the provided type
	 * @param notes
	 * @param index
	 * @return
	 */
	public static Note[] addNewEmptyNote(Note[] notes, int index, String type){
		if(notes != null){
			Note[] newNotes = new Note[notes.length + 1];
			boolean found = false;
			for(int i = 0; i < newNotes.length;i++){
				if(i == index){
					found = true;
					Note n = new Note();
					n.type = type;
					newNotes[i] = n;
				}else if(found){
					newNotes[i] = notes[i-1];
				}else{
					newNotes[i] = notes[i];
				}
			}
			return newNotes;
		}else{
			Note[] newNotes = new Note[1];
			newNotes[0] = new Note();
			newNotes[0].type = type;
			return newNotes;
		}
	}

}