package trb1914.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import trb1914.data.Registry;
import trb1914.debug.Debug;

/**
 * This panel displays numbers inside a specified span of days
 * @author Mees Gelein
 */
public class DatesView extends JPanel{

	/**The format used to format dates*/
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	/**The title of this dateView*/
	private String title = "No Title";
	/**Local cache to allow resetting of range without recalculating all data*/
	private HashMap<String, Integer> hashMap;
	
	/**
	 * Creates a new DatesView with the specified date range using the data from the specified 
	 * statistics object
	 */
	public DatesView(String title) {
		setTitle(title);
	}
	
	/**
	 * Sets the title of this DatesView
	 * @param t
	 */
	public void setTitle(String t){
		title = t;
	}
	
	/**
	 * Sets the contents of this view
	 * @param startDate
	 * @param endDate
	 * @param stats
	 */
	public void set(String startDate, String endDate, HashMap<String, Integer> map){
		hashMap = map;
		setRange(startDate, endDate);
	}
	
	/**
	 * Displays the given range 
	 * @param startDate
	 * @param endDate
	 */
	private void setRange(String startDate, String endDate){
		removeAll();
		setLayout(new BorderLayout());
		Border bA = BorderFactory.createLineBorder(Color.gray);
		Border bB = BorderFactory.createEmptyBorder(8,8,8,8);
		setBorder(new CompoundBorder(bA, bB));
		
		//the title panel
		JPanel titlePanel = new JPanel();
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(Registry.LIST_FONT.deriveFont(Registry.LIST_FONT_SIZE + 4.0f));
		titlePanel.add(titleLabel);
		add(titlePanel, BorderLayout.PAGE_START);
		
		//make a list of the dates we need to view
		Date sDate = getDate(startDate);
		int range = (int) (TimeUnit.DAYS.convert(getDate(endDate).getTime() - sDate.getTime(), TimeUnit.MILLISECONDS) + 1);
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(sDate);
		ArrayList<String> dates = new ArrayList<String>();
		while(range > 0){
			dates.add(dateFormat.format(startTime.getTime()));
			startTime.roll(Calendar.DATE, true);
			range--;
		}
		
		JPanel daysPanel = new JPanel(new GridLayout(1, dates.size()));
		for(String date : dates){
			daysPanel.add(createDayPanel(date, hashMap.get(date)));
		}
		add(daysPanel);
		
		//add the two date setters
		JPanel rangePanel = new JPanel(new GridLayout(1, 4, 16, 4));
		add(rangePanel, BorderLayout.SOUTH);
		JLabel startRangeLabel = new JLabel("Start datum: ");
		startRangeLabel.setFont(Registry.LIST_FONT);
		final JTextField startRangeField = new JTextField(startDate);
		startRangeField.setFont(startRangeLabel.getFont());
		JLabel endRangeLabel = new JLabel("Eind datum: ");
		endRangeLabel.setFont(startRangeLabel.getFont());
		final JTextField endRangeField = new JTextField(endDate);
		endRangeField.setFont(startRangeLabel.getFont());
		rangePanel.add(startRangeLabel);
		rangePanel.add(startRangeField);
		rangePanel.add(endRangeLabel);
		rangePanel.add(endRangeField);
		ActionListener resetRangeListener = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setRange(startRangeField.getText(), endRangeField.getText());
			}
		};
		endRangeField.addActionListener(resetRangeListener);
		startRangeField.addActionListener(resetRangeListener);
	}
	
	/**
	 * Creates a single date display panel
	 * @param date
	 * @param map
	 * @return
	 */
	private JPanel createDayPanel(String date, Integer amount){
		if(amount == null) amount = 0;
		boolean today = date.equals(dateFormat.format(new Date()));
		int borderThickness = 1;
		if(today) borderThickness = 2;
		
		JPanel panel = new JPanel(new GridLayout(2, 1));
		//topppanel displays the date
		JPanel topPanel = new JPanel();
		if(!today){
			topPanel.setBackground(RentalRenderer.LATE_COLOR_BG);
		}else{
			topPanel.setBackground(RentalRenderer.DUE_COLOR_BG);
		}
		topPanel.setBorder(BorderFactory.createMatteBorder(borderThickness, borderThickness, borderThickness, borderThickness, Color.gray));
		JLabel dateLabel = new JLabel(date.substring(0, date.lastIndexOf("-")));
		dateLabel.setFont(Registry.BOLD_LIST_FONT);
		topPanel.add(dateLabel);
		panel.add(topPanel);
		
		//bottomPanel displays the amount of items
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setBorder(BorderFactory.createMatteBorder(borderThickness, borderThickness, borderThickness, borderThickness, Color.gray));
		JLabel amtLabel = new JLabel(Integer.toString(amount));
		amtLabel.setFont(Registry.LIST_FONT);
		bottomPanel.add(amtLabel);
		panel.add(bottomPanel);
		
		JPanel returnPanel = new JPanel(new BorderLayout());
		returnPanel.add(panel, BorderLayout.NORTH);
		return returnPanel;
	}
	
	/**
	 * Returns the date from the formatted String
	 * @param s
	 * @return
	 */
	private Date getDate(String s){
		try {
			return dateFormat.parse(s);
		} catch (ParseException e) {
			Debug.println("Couldn't parse the provided date (" + s + "). Return today instead", this);
			e.printStackTrace();
		}
		return new Date();
	}
}
