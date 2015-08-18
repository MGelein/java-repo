package trb1914.dictionary;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLDocument;

import trb1914.anim.Animator;
import trb1914.anim.Animator.IAnimate;

/**
 * Class that holds a single result
 * @author Mees Gelein
 */
public class Result extends JPanel implements IAnimate{

	/**Used to make the html look semi decent using a little dirty css*/
	private static String fontName = new JButton().getFont().getFamily();
	
	/**The panel to display the actual results*/
	private JEditorPane editorPane = new JEditorPane("text/html", "No Result");
	/**The size of the result we are showing*/
	public static final Dimension PREF_SIZE = new Dimension(282, 345);
	
	/**The floats used for calculations*/
	private float x = - (PREF_SIZE.width - 80);
	private float y = 4.0f;
	/**The float used to determine where to animate to*/
	private float xGoal = 8.0f;
	private float yGoal = 4.0f;
	/**Easefactor we start easing with*/
	private final float startEase = -0.1f;
	/**The easeFactor used for the calculations*/
	private float easeFactor = startEase;
	/**The easeFactor we want to ease with*/
	private float easeFactorGoal = 0.3f;
	/**Reference to the containing panel*/
	private ResultPanel resultPanel;
	/**
	 * Creates a new Result
	 */
	public Result(ResultPanel parentPanel){
		resultPanel = parentPanel;
		resultPanel.add(this);
		setLayout(new BorderLayout());
		setPreferredSize(PREF_SIZE);
		setBounds(0, 0, 0, 0);
		setSize(getPreferredSize());
		setMaximumSize(getSize());
		setMinimumSize(getMaximumSize());
		makeGUI();
		setVisible(false);
	}
	
	/**
	 * Sets the display position of this result. Only used in setup
	 */
	public void setIndex(int i){
		xGoal += (PREF_SIZE.width + 8) * i;
	}
	
	/**
	 * Shows the Perseus server response after we are done changing it :)
	 * This method is called from the EDT
	 * @param response
	 */
	public void showResult(String response){
		editorPane.setText(response);
		editorPane.setCaretPosition(0);
		easeFactor = startEase;
		Animator.start(this);
	}
	
	/**
	 * Hops all results over one to make room for this one
	 */
	public void gotoNext(){
		xGoal += (PREF_SIZE.width + 8);
		easeFactor = startEase;
		Animator.start(this);
	}
	
	/**
	 * Eases out to bottom
	 */
	public void die(){
		xGoal = x;
		yGoal = y + getPreferredSize().height + 50;
		easeFactor = startEase;
		Animator.start(this);
	}
	
	/**
	 * Builds the GUI
	 */
	private void makeGUI(){
		JScrollPane editorScroll = new JScrollPane(editorPane);
		editorScroll.setPreferredSize(new Dimension(400, 600));
		editorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(editorScroll);
		
		editorPane.setEditable(false);
		restyleTextField();
		editorPane.setText(editorPane.getText());
	}
	
	/**
	 * Sets the css for the textfield
	 */
	private void restyleTextField(){
		((HTMLDocument) editorPane.getDocument()).getStyleSheet().addRule("body{font-family:" + fontName + "; font-size: 18pt;} span{font-style: italic;} td{color: #666666; font-size: 12pt;} tr{ border: 1px solid #CCCCCC;}");
	}
	
	/**
	 * Animates this component
	 */
	public void animate(){
		setVisible(true);
		float dx = xGoal - x;
		float dy = yGoal - y;
		x += dx * easeFactor;
		y += dy * easeFactor;
		easeFactor += 0.02f;
		if(easeFactor > easeFactorGoal) easeFactor = easeFactorGoal;
		setBounds(Math.round(x), Math.round(y), getPreferredSize().width, getPreferredSize().height);
		if(dx < 1 && dy < 1) {
			Animator.stop(this);
			setBounds(Math.round(xGoal), Math.round(yGoal), getPreferredSize().width, getPreferredSize().height);
			if(yGoal > y + getPreferredSize().height){
				resultPanel.remove(this);
			}
		}
		repaint();
	}
}
