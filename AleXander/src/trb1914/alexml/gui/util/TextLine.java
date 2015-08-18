package trb1914.alexml.gui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import trb1914.alexml.data.WordPanel;
/**
 * One line of text that has words that can be highlighted
 * @author Mees Gelein
 */
public class TextLine extends JPanel {

	/**The panel that holds all the word panels of this line*/
	private JPanel contentPanel;
	
	/**A reference to the parent panel. Used to set the selected index*/
	private WordHighlightPanel parentPanel;
	
	/**
	 * Creates a new TextLine instance. Mainly creates the layout. Use addWord to add words
	 * to this line
	 */
	public TextLine(WordHighlightPanel parent){
		setLayout(new BorderLayout());
		parentPanel = parent;
		//Creating a flowlayout variant that has no horizontal or vertical component padding
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
		fl.setVgap(0);
		fl.setHgap(0);
		contentPanel = new JPanel(fl);
		contentPanel.setBackground(Color.WHITE);
		add(contentPanel, BorderLayout.NORTH);
	}
	
	/**
	 * Adds a word to the line (replaces a '#' to a ': ')
 	 * @param s
	 */
	public WordPanel addWord(String s){
		boolean allowSelect = true;
		if(s.indexOf("#") != -1) allowSelect = false;
		s = s.replaceAll("#", ":");
		WordPanel wordPanel = new WordPanel(s, parentPanel, allowSelect);
		contentPanel.add(wordPanel);
		return wordPanel;
	}
	
	/**
	 * Returns the nextLine of the text
	 * @return
	 */
	public TextLine getNextLine(){
		TextLine nextLine = new TextLine(parentPanel);
		add(nextLine, BorderLayout.CENTER);
		return nextLine;
	}
}
