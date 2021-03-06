package trb1914.data;

import java.util.ArrayList;

import trb1914.Main;

/**
 * This class calculates the statistics based on a snapshot of the rentals file
 * @author Mees Gelein
 */
public abstract class StatisticsCalculator {
	
	/**Caching statistics might be useful*/
	private static Statistics stats = null;
	
	/*
	 * Useful statistics:
	 * for every date the number of rented items
	 * for every date the number of returned items
	 * for every date the amount of customers
	 * for every item the number of days it was rented
	 */
	
	/**
	 * Calculates the statistics
	 */
	private static void calculate(){
		//create the stats object
		stats = new Statistics();
		
		//copy the rentals list of the mainwindow
		ArrayList<Rental> list = new ArrayList<Rental>();
		if(Main.mainWindow != null && Main.mainWindow.allRentals != null){
			for(Rental r : Main.mainWindow.allRentals) list.add(r);
		}
		
		//do analysis
		for(Rental r : list) stats.extractData(r);
	}
	
	/**
	 * Returns the statistics after recalculating the results
	 * @return
	 */
	public static Statistics getNewStatistics(){
		calculate();
		return stats;
	}
	
	/**
	 * Returns the last calculated version of the statistics
	 * @return
	 */
	public static Statistics getStatistics(){
		if(stats == null) return getNewStatistics();
		else return stats;
	}
}
