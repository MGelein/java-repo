package trb1914.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Used for easy debugging and shortcut to System.out
 * @author Mees Gelein
 */
public abstract class Debug {

	/**Saves all the debug output in this object. Can be saved to a location*/
	private static StringBuilder debugContent = new StringBuilder();
	/**Should all content be added to the stringBuilder?*/
	public static boolean debug = false;
	
	/**
	 * Prints the provided string
	 * @param s			the String to print
	 */
	public static void print(String s){
		print(s, null, false);
	}
	
	/**
	 * Prints the provided String with the classname in brackets infront
	 * @param s			the String to print
	 * @param c			the class of the instance that called this function
	 * @param n			flag determining if this is a newline
	 */
	public static void print(String s, Class<?> c, boolean n){
		String line = (c == null) ? s : "[" + c.getName() + "]: " + s;
		if(debug) debugContent.append(line);
		if(n) System.out.println(line);
		else System.out.println(line);
	}
	
	/**
	 * Prints the provided String with the classname in brackets infront
	 * @param s			the String to print
	 * @param o			the Object that called this function
	 */
	public static void print(String s, Object o){
		print(s, o.getClass(), false);
	}
	
	/**
	 * Prints the provided String and adds a newline
	 * @param s			the String to print
	 */
	public static void println(String s){
		print(s, null, true);
	}
	
	/**
	 * Prints the provided String with class name in front, ended with a newline
	 * @param s			the String to print
	 * @param o			the Object that called this function
	 */
	public static void println(String s, Object o){
		print(s, o.getClass(), true);
	}
	
	/**
	 * Prints the provided String with class name in front, ended with a newline
	 * @param s			the String to print
	 * @param c			the class of the instance that called this function
	 */
	public static void println(String s, Class<?> c){
		print(s, c, true);
	}
	
	/**
	 * Saves the debug output up untill now in the specified file. Only works
	 * if the Debug.debug option is turned to true!
	 * @param f			the file to save the output to.
	 */
	public static void save(File f){
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			w.write(debugContent.toString());
			w.close();
			debugContent = new StringBuilder();
		}catch(Exception e){
			println("Could not save the file at the specified location: " + f.getAbsolutePath(), Debug.class);
			e.printStackTrace();
		}
	}
}
