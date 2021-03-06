package trb1914.data;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import trb1914.debug.Debug;

/**
 * A single rental item with two dates attached. A return and rent date
 * @author trb1914
 */
public class RentalItem {

	/**The name of the rented object*/
	public String name;
	/**The startDate of this object*/
	public Date startDate;
	/**The return date of this object*/
	public Date endDate;
	
	/**
	 * Returns the network traffic representation of this RentalItem
	 * @return
	 */
	public String getAsString(){
		return name + "#s:" + getStartDateString() + "&e:" + getEndDateString();
	}
	
	/**
	 * Returns the startDate String
	 * @return
	 */
	public String getStartDateString(){
		if(startDate == null) return "--";
		return Rental.dateFormat.format(startDate);
	}
	
	/**
	 * If this items contains numbers in its name it returns true
	 * @return
	 */
	public boolean isBike(){
		if(name.contains("1")) return true;
		else if(name.contains("2")) return true;
		else if(name.contains("3")) return true;
		else if(name.contains("4")) return true;
		else if(name.contains("5")) return true;
		else if(name.contains("6")) return true;
		else if(name.contains("7")) return true;
		else if(name.contains("8")) return true;
		else if(name.contains("9")) return true;
		else if(name.contains("0")) return true;
		return false;
	}
	
	/**
	 * Parses the provided String into a date if possible
	 * @param s
	 */
	public void parseStartDateString(String s){
		s = s.replaceAll("&", "");
		if(s.equals("--")) return;
		try{
			startDate = Rental.dateFormat.parse(s);
		}catch(Exception e){
			Debug.println("Could not parse startDate: "  + s, this);
		}
	}
	
	/**
	 * Parses the provided String into a date if possible
	 * @param s
	 */
	public void parseEndDateString(String s){
		s = s.replaceAll("&", "");
		if(s.equals("--")) return;
		try{
			endDate = Rental.dateFormat.parse(s);
		}catch(Exception e){
			Debug.println("Could not parse endDate: "  + s, this);
		}
	}
	
	/**
	 * Returns the startDate String
	 * @return
	 */
	public String getEndDateString(){
		if(endDate == null) return "--";
		return Rental.dateFormat.format(endDate);
	}
	
	/**
	 * Parses the provided String into a RentalItem
	 * @param s
	 */
	public void parseFromString(String s){
		String[] parts = s.split("#");
		name = parts[0].replaceAll("#", "");
		String[] dates = parts[1].split("&");
		String[] sDate = dates[0].split(":");
		String[] eDate = dates[1].split(":");
		parseStartDateString(sDate[1]);
		parseEndDateString(eDate[1]);
	}
	
	/**
	 * Returns the amount of days this rental has taken
	 */
	public int getRentLength(){
		if(endDate != null && startDate != null){
			return (int) TimeUnit.DAYS.convert(endDate.getTime() - startDate.getTime(), TimeUnit.MILLISECONDS) + 1;
		}else if(startDate!= null){
			return (int) TimeUnit.DAYS.convert(new Date().getTime() - startDate.getTime(), TimeUnit.MILLISECONDS) + 1;
		}else{
			return 0;
		}
	}
}
