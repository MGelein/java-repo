package trb1914.alexml.gui.util;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.helper.ImageHelper;
/**
 * Shows the splash screen (hard coded) for the specified amount of time
 * @author Mees Gelein
 */
public class SplashScreen extends JWindow {
	
	//The amount of miliseconds the splash logo is shown
	public final static int SPLASH_TIME = 3000;
	
	//A flag that tells if the splash screen is currently showing. Used to prevent the user from using hotkeys 
	//to open a file during splash-screen.
	public static boolean splashing = true;
	
	/**
	 * shows a new SplashScreen for SPLASH_TIME amount of miliseconds.
	 * After this amount of time has passed the window will dispose of itself
	 */
	public SplashScreen() {
		//adds picture to the panel
		Image img = ImageHelper.getImage("/trb1914/alexml/img/splash.png");
		getContentPane().add(new JLabel("",new ImageIcon(img), SwingConstants.CENTER));
		setSize(400, 300);
		
		//make it visible
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		Main.main.setEnabled(false);
		
		//wait for specified time
		try{
			Thread.sleep(SPLASH_TIME);
		}catch(Exception e){
			Debug.println("Thread has insomnia problems", this);
		}

		//re-enable program and dispose of this window. Set splashing flag to false
		Main.main.setEnabled(true);		
		splashing = false;
		dispose();
	}

}
