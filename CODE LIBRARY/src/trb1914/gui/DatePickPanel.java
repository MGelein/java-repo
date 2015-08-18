package trb1914.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A replacement class for the first DatePicker I wrote that was
 * based of a JTextfield. 
 * @author Mees Gelein
 */
public class DatePickPanel extends JPanel{

	/**The format used to format the date into dd-MM-yyyy format*/
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	
	/**List of all listeners to the changes in this object*/
	private ArrayList<DatePickPanel.IDatePickReady> listeners = new ArrayList<DatePickPanel.IDatePickReady>();
	
	/**The date that we are displaying in string form*/
	private String currentDateString;
	
	/**A list of all the labels, used for operations that need to be performed on all the labels*/
	private ArrayList<JLabel> allLabels = new ArrayList<JLabel>();
	
	/**
	 * Creates a new DatePickPanel with the provided Date as the
	 * starting data
	 */
	public DatePickPanel(Date date) {
		currentDateString = DATE_FORMAT.format(date);
		makeGUI();
	}
	
	/**
	 * Builds the UI
	 */
	private void makeGUI(){
		//set to flowLayout with little spacing
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		//remove all "-" from the date
		currentDateString = currentDateString.replaceAll("-", "");
		
		//draw the border around it to make it at least slightly look like a JTextField
		setBorder(BorderFactory.createLineBorder(Color.gray));
		setBackground(Color.white);
	}
	
	/**
	 * Sets the font of all the labels to the provided font
	 */
	public void setDateFont(Font f){
		for(JLabel l : allLabels) l.setFont(f);
		revalidate();
		repaint();
	}
	
	/**
	 * Adds the provided parameter object as subscriber to be notified when the date
	 * picking is done
	 * @param subscriber
	 */
	public void addListener(IDatePickReady subscriber){
		if(!listeners.contains(subscriber)) listeners.add(subscriber);
	}
	
	/**
	 * Removes the provided parameter object as a subscriber if it is one
	 * @param subscriber
	 */
	public void removeListener(IDatePickReady subscriber){
		listeners.remove(subscriber);
	}
	
	/**
	 * Notifies all subscribers that the datePick is done
	 */
	public void datePickDone(){
		for(IDatePickReady r : listeners) r.datePickReady();
	}
	
	/**
	 * Main unit for unit testing
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setLayout(new FlowLayout(FlowLayout.CENTER));
				frame.add(new DatePickPanel(new Date()));
				frame.setSize(640, 480);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}
	
	/**
	 * Interface that can be implemented by the listeners
	 * @author Mees Gelein
	 */
	public interface IDatePickReady{
		public void datePickReady();
	}
}
