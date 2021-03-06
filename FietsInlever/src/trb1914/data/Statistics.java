package trb1914.data;

import java.util.HashMap;

/**
 * The class that contains the set of calculated data
 * @author Mees Gelein
 */
public class Statistics {

	/**The HashMap used to store the amount of items returned per date*/
	public HashMap<String, Integer> returnAmount = new HashMap<String, Integer>();
	/**The HashMap used to store the amount of items rented per date*/
	public HashMap<String, Integer> rentAmount = new HashMap<String, Integer>();
	/**The HashMap used to store the amount of customers per date*/
	public HashMap<String, Integer> customerAmount = new HashMap<String, Integer>();
	/**The HashMap used to store the amount of days an item has been rented*/
	public HashMap<String, Integer> daysRented = new HashMap<String, Integer>();

	/**
	 * Extracts all the necessary data from the specified Rental
	 * @param r
	 */
	public void extractData(Rental r){
		addCustomerOn(r.getStartDate());
		for(RentalItem ri : r.objects) extractObjectData(ri);
		for(RentalItem ri : r.paidObjects) extractObjectData(ri);
		for(RentalItem ri : r.swappedObjects) extractObjectData(ri);
	}
	
	/**
	 * Extracts all the necessary data from one single rentalItem
	 */
	private void extractObjectData(RentalItem r){
		addDaysForObject(r.name, r.getRentLength());
		addItemsReturnedOn(r.getEndDateString(), 1);
		addItemsRentedOn(r.getStartDateString(), 1);
	}
	
	/**
	 * The amount of items that were rented on the specified date
	 * @param date
	 * @return
	 */
	public int amountRentedOn(String date){
		if(rentAmount.get(date) != null) return rentAmount.get(date);
		else return 0;
	}
	
	/**
	 * The amount of customers on the specified date
	 * @param date
	 * @return
	 */
	public int amountCustomerOn(String date){
		if(customerAmount.get(date) != null) return customerAmount.get(date);
		else return 0;
	}
	
	/**
	 * The amount of items that should be returned on the specified date
	 * @param date
	 * @return
	 */
	public int amountReturnedOn(String date){
		if(returnAmount.get(date) != null) return returnAmount.get(date);
		else return 0;
	}
	
	/**
	 * Returns the amount of days the specified object has been rented for
	 * @param object
	 * @return
	 */
	public int daysRentedFor(String object){
		if(daysRented.get(object) != null) return daysRented.get(object);
		else return 0;
	}
	
	/**
	 * Adds a single customer to the provided date
	 * @param date
	 */
	private void addCustomerOn(String date){
		if(customerAmount.containsKey(date)){
			int prevAmt = customerAmount.get(date);
			customerAmount.put(date, prevAmt + 1);
		}else{
			customerAmount.put(date, 1);
		}
	}
	
	/**
	 * Adds the specified amount of items to the specified date
	 * @param date
	 * @param amt
	 */
	private void addItemsReturnedOn(String date, int amt){
		if(returnAmount.containsKey(date)){
			int prevAmt = returnAmount.get(date);
			returnAmount.put(date, prevAmt + amt);
		}else{
			returnAmount.put(date, amt);
		}
	}
	
	/**
	 * Adds the specified amount of items to the specified date
	 * @param date
	 * @param amt
	 */
	private void addItemsRentedOn(String date, int amt){
		if(rentAmount.containsKey(date)){
			int prevAmt = rentAmount.get(date);
			rentAmount.put(date, prevAmt + amt);
		}else{
			rentAmount.put(date, amt);
		}
	}
	
	/**
	 * Adds the specified amount of days of renting to the specified object
	 * @param object
	 * @param amt
	 */
	private void addDaysForObject(String object, int amt){
		if(daysRented.containsKey(object)){
			int prevAmt = daysRented.get(object);
			daysRented.put(object, prevAmt + amt);
		}else{
			daysRented.put(object, amt);
		}
	}
}
