package trb1914.util;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import trb1914.helper.ImageHelper;
import trb1914.helper.SystemHelper;

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
		Image img = ImageHelper.getImage("/trb1914/img/splash.png");
		getContentPane().add(new JLabel("",new ImageIcon(img), SwingConstants.CENTER));
		
		//set panel size and position
		int hw = SystemHelper.SCREEN_WIDTH / 2;
		int hh = SystemHelper.SCREEN_HEIGHT / 2;
		hw -= 320; hh-=240;
		setBounds(hw, hh, 640, 480);
		
		//make it visible
		setVisible(true);
		
		//wait for specified time
		try{
			Thread.sleep(SPLASH_TIME);
		}catch(Exception e){
			//Debug.println("[SplashScreen]: Thread has insomnia problems");
		}

		//re-enable program and dispose of this window. Set splashing flag to false
		splashing = false;
		dispose();
	}

}
