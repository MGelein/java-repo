package trb1914.data;

import java.util.ArrayList;

/**
 * Class that will do analysis on the rentals data
 * @author Mees Gelein
 */
public class RentalAnalysis {

	/**The list of all the rentals we do an analysis on*/
	private ArrayList<Rental> rentals;
	
	/**
	 * Creates a new RentalAnalysis object. You can query this object for info
	 * @param rentalList
	 */
	public RentalAnalysis(ArrayList<Rental> rentalList) {
		rentals = rentalList;
	}
	
	/**
	 * Finds the amount of days the item has been rented
	 * @param object
	 * @return
	 */
	public int getDaysUsed(String object){
		//go through all rentals and see if it has been paid
		int days = 0;
		for(Rental r : rentals){
			days += r.getRentLength(object);
		}
		return days;
	}
}
