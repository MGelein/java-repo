package trb1914.alexml.gui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import trb1914.alexml.data.WordPanel;
import trb1914.alexml.interfaces.ISelectionUpdate;
/**
 * A class to display a multiline piece of text that allows a word to be 
 * highlighted (toggling state). The highlighted words can be requested
 * by a method. Each word is printed on a separate JPanel. This may have
 * some performance implications but I'm just guessing about that. 
 * @author Mees Gelein
 */
public class WordHighlightPanel extends JPanel {
	
	/**This object is updated when selection changes take place*/
	private ISelectionUpdate objectToUpdate = null;
	/**All the wordPanels that make up this panel. Use this to find the index of a specific word etc*/
	public Vector<WordPanel> allWords = new Vector<WordPanel>();
	
	/**
	 * Creates a new HighlightPanel using the provided String
	 * as input text. '\n' is supposed to be the newline character
	 * and spaces separate words.
	 * @param s
	 * @param toUpdate		the parent to update when selection changes occur
	 */
	public WordHighlightPanel(String s, ISelectionUpdate toUpdate){
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(2, 8, 8, 2));
		parseInputString(s);
		objectToUpdate = toUpdate;
	}
	
	/**
	 * Sets the word at the provided index to the provided boolean value.
	 * Unless the index = -1. In that case the function call is ignored
	 * @param index
	 * @param selected
	 */
	public void setSelected(int index, boolean selected){
		if(index != -1){
		allWords.get(index).setSelected(selected);
		}
	}
	
	/**
	 * Used by the wordPanels to notify this panel that they have been selected/deselected
	 */
	public void selectionUpdated(){
		if(objectToUpdate != null){
			objectToUpdate.selectionUpdate();
		}
	}
	
	/**
	 * Returns the string as it was selected
	 * @return
	 */
	public String getSelectedString(){
		StringBuilder builder = new StringBuilder();
		int i, max = allWords.size();
		for(i = 0; i < max; i++){
			if(allWords.get(i).isSelected()){
				builder.append(allWords.get(i).getText());
				builder.append(" ");
			}
		}
		String s = builder.toString();
		return s;
	}
	
	/**
	 * Returns a vector of ints of the indices that are selected
	 * @return
	 */
	public Vector<Integer> getSelectedWordIndices(){
		Vector<Integer> indices = new Vector<Integer>();
		int i, max = allWords.size();
		for(i = 0; i < max; i++){
			if(allWords.get(i).isSelected()){
				indices.add(i);
			}
		}
		if(indices.size() == 0){
			indices.add(-1);//add a -1 element to make it clear that the word can't be found
		}
		return indices;
	}
	
	/**
	 * Returns the index of the word, return -1 if no match is foud
	 * @param s
	 * @return
	 */
	public int getWordIndex(String s){
		int i, max = allWords.size();
		for(i = 0; i < max; i++){
			if(allWords.get(i).getText().equals(s)){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Parses the input String into separate words and lines
	 * @param s
	 */
	private void parseInputString(String s){
		String[] lines = s.split("\n");
		int i,j, max2, max = lines.length;
		TextLine cLine = new TextLine(this);
		JPanel contentPanel = new JPanel(new BorderLayout());
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setBackground(WordPanel.normalColor);
		contentPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
		contentPanel.add(cLine, BorderLayout.NORTH);
		for(i = 0; i < max; i++){
			String[] words = lines[i].split(" ");
			max2 = words.length;
			for(j = 0; j < max2; j++){
				WordPanel wp = cLine.addWord(words[j]);
				if(wp.allowSelecting){//if it is not a linecounter
					allWords.add(wp);
				}
			}
			cLine = cLine.getNextLine();
		}
	}

}
