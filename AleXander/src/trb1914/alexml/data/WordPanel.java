package trb1914.alexml.data;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import trb1914.alexml.gui.util.WordHighlightPanel;
/**
 * A tiny class used as a data and view object at the same time. 
 * This is probably not according to the Swing idea of seperating
 * model and view, but screw that :)
 * @author Mees Geleins
 */
public class WordPanel extends JPanel{

	/**The color that is used as background by a non-selected word. Defaults to white*/
	public static Color normalColor = Color.white;
	/**The color that is used as background by a selected word. Defaults to a blueish color (0x8888FF)*/
	public static Color selectedColor = new Color(0x8888FF);

	/**The label that displays the current word*/
	private JLabel label;
	/**Is this word highlighted*/
	private boolean selected = false;
	/**This is the parent that holds all these word panels. This reference is used to update the parent.*/
	private WordHighlightPanel holderPanel;
	/**Set this to false to make it non selectable*/
	public boolean allowSelecting = true;

	/**
	 * Creates a new wordPanel using the provided String. 
	 * @param s
	 */
	public WordPanel(String s, WordHighlightPanel parent, boolean allowSelect){
		//creates a flowLayout with 0-padding around the components
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		flowLayout.setHgap(0);
		flowLayout.setVgap(0);
		allowSelecting = allowSelect;
		holderPanel = parent;

		setBackground(WordPanel.normalColor);

		label = new JLabel(s);
		label.setFont(FileRegistry.getNormalFont());
		add(label);
		if(allowSelect){//if you can select this piece of text
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					selected = !selected;
					setSelected(selected);
				}
			});
		}
	}

	/**
	 * Sets the selected state of this word to the provided boolean parameter
	 * @param b
	 */
	public void setSelected(boolean b){
		if(b){
			setBackground(WordPanel.selectedColor);
		}else{
			setBackground(WordPanel.normalColor);
		}
		selected = b;
		holderPanel.selectionUpdated();//Messy way of notifying the parent of the update. Maybe should use a Event based structure?
	}

	/**
	 * Returns the text of this word
	 * @return
	 */
	public String getText(){
		return label.getText();
	}

	/**
	 * Returns the selected state of this word
	 * @return
	 */
	public boolean isSelected(){
		return selected;
	}
}
