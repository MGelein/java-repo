package trb1914.preferences;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import trb1914.debug.Debug;

/**
 * Uses a standard Java Properties file to save the application preferences
 * @author Mees Gelein
 */
public abstract class Preferences {

	private static Properties props = new Properties();

	/**
	 * Saves the properties file to the specified location relative to the
	 * current location. Adds the specified comment to the top
	 * @param location		a file url
	 */
	public static void save(String location, String comment){
		try{
			File file = new File(location);
			file.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(file);
			//alphabetically order them
			Properties tmp = new Properties() {
			    @Override
			    public synchronized Enumeration<Object> keys() {
			        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			    }
			};
			tmp.putAll(props);
			tmp.store(outputStream, comment);
			outputStream.close();
		}catch(Exception e){
			Debug.println("[Preferences]: Something went wrong trying to save the properties file", Preferences.class);
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the properties file to the specified location relative to the current
	 * location. Adds a empty comment to the top of the file
	 * @param location
	 */
	public static void save(String location){
		save(location, "");
	}

	/**
	 * Tries to load a properties file from the specified location. Acces the properties using
	 * this classes' static methods
	 * @param location
	 */
	public static void load(String location){
		try{
			File file = new File(location);
			if(file.exists()){
				FileInputStream inputStream = new FileInputStream(file);
				props.load(inputStream);
				inputStream.close();
			}
		}catch(Exception e){
			Debug.println("[Preferences]: Something went wrong trying to load the properties file", Preferences.class);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the provided key to the provided value
	 * @param key
	 * @param value
	 */
	public static void set(String key, String value){
		props.setProperty(key, value);
	}
	
	/**
	 * Sets the provided key to the provided value
	 * @param key
	 * @param value
	 */
	public static void set(String key, int value){
		props.setProperty(key, Integer.toString(value));
	}
	
	/**
	 * Sets the provided key to the provided value
	 * @param key
	 * @param value
	 */
	public static void set(String key, boolean value){
		props.setProperty(key, Boolean.toString(value));
	}
	
	/**
	 * Sets the provided key to the provided value
	 * @param key
	 * @param value
	 */
	public static void set(String key, Dimension value){
		props.setProperty(key, value.width + "," + value.height);
	}
	
	/**
	 * Sets the provided key to the provided value
	 * @param key
	 * @param value
	 */
	public static void set(String key, float value){
		props.setProperty(key, Float.toString(value));
	}
	
	/**
	 * Returns the value associated with the provided key
	 * @param key
	 * @return
	 */
	public static String get(String key){
		return props.getProperty(key);
	}
	
	/**
	 * Returns the value associated with the provided key and tries
	 * to parse it
	 * @param key
	 * @return
	 */
	public static int getInteger(String key){
		return Integer.parseInt(get(key));
	}
	
	/**
	 * Returns the value associated with the provided key and tries
	 * to parse it
	 * @param key
	 * @return
	 */
	public static float getFloat(String key){
		return Float.parseFloat(get(key));
	}
	
	/**
	 * Returns the value associated with the provided key and tries
	 * to parse it
	 * @param key
	 * @return
	 */
	public static Dimension getDimension(String key){
		Dimension d = new Dimension();
		String value = get(key);
		if(value == null) return null;
		int index = value.indexOf(",");
		if(index > -1){
			d.width = Integer.parseInt(value.substring(0, index));
			d.height = Integer.parseInt(value.substring(index + 1));
			return d;
		}else{
			return null;
		}
	}
	
	/**
	 * Returns the value associated with the provided key and tries
	 * to parse it. If the value cannot be found the default value is returned
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(String key, boolean defaultValue){
		return Boolean.parseBoolean(get(key, Boolean.toString(defaultValue)));
	}
	
	/**
	 * Returns the value associated with the provided key and tries
	 * to parse it
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key){
		return Boolean.parseBoolean(get(key));
	}
	
	/**
	 * Returns the value associated with the provided key and tries
	 * to parse it. If the value cannot be found the default value is returned
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getInteger(String key, int defaultValue){
		return Integer.parseInt(get(key, Integer.toString(defaultValue)));
	}
	
	/**
	 * Returns the value associated with the provided key and tries
	 * to parse it. If the value cannot be found the default value is returned
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static float getFloat(String key, float defaultValue){
		return Float.parseFloat(get(key, Float.toString(defaultValue)));
	}
	
	/**
	 * Returns the value associated with the provided key and tries
	 * to parse it. If the value cannot be found the default value is returned
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static Dimension getDimension(String key, Dimension defaultValue){
		Dimension d = getDimension(key);
		if(d != null) return d;
		else return defaultValue;
	}
	
	/**
	 * Returns the value associated with the provided key. If
	 * the value cannot be found the default value is returned
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String get(String key, String defaultValue){
		return props.getProperty(key, defaultValue);
	}
}
