package trb1914;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import trb1914.data.Statistics;
import trb1914.data.StatisticsCalculator;
import trb1914.threading.ThreadManager;
import trb1914.util.DatesView;
/**
 * The view that displays any statistics
 * @author Mees Gelein
 */
public class StatisticsView extends JPanel{

	/**The statistics that are displayed*/
	private Statistics stats;
	/**Displays the amount of items that return each day*/
	private DatesView returnView = new DatesView("Terugkerende Items");
	/**Displays the amount of customers on each day*/
	private DatesView customerView = new DatesView("Klanten");
	/**Displays the amount of items rented each day*/
	private DatesView rentView = new DatesView("Verhuurde Items");
	
	/**
	 * Creates the StatisticsView
	 */
	public StatisticsView() {
		makeGUI();
		refresh();
	}
	
	/**
	 * Creates the display components that will show the statistics data
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel(new GridLayout(2, 2, 8, 8));
		add(contentPanel);
		
		contentPanel.add(returnView);
		contentPanel.add(customerView);
		contentPanel.add(rentView);
	}
	
	/**
	 * Refreshes (recalculates) the statistics on a separate thread and refreshes the display 
	 * when the calculations are done
	 */
	public void refresh(){
		ThreadManager.submit(new Runnable(){
			public void run() {
				stats = StatisticsCalculator.getNewStatistics();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						loadStats();
						revalidate();
						repaint();
					}
				});
			}
		});
	}
	
	/**
	 * Loads the statistics into the view components
	 */
	private void loadStats(){
		customerView.set(weekAgoString(), todayString(), stats.customerAmount);
		rentView.set(weekAgoString(), todayString(), stats.rentAmount);
		returnView.set(todayString(), nextWeekString(), stats.returnAmount);
	}
	
	/**
	 * The date of today represented in a String
	 * @return
	 */
	private String todayString(){
		return DatesView.dateFormat.format(new Date());
	}
	
	/**
	 * The date of today a week ago represented in a String
	 * @return
	 */
	private String weekAgoString(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -7);
		return DatesView.dateFormat.format(c.getTime());
	}
	
	/**'
	 * The date of next week in a String representation
	 * @return
	 */
	private String nextWeekString(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7);
		return DatesView.dateFormat.format(c.getTime());
	}
}
