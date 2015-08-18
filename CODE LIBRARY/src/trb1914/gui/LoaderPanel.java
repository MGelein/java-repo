package trb1914.gui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
/**
 * a small class that can show a loadBar when necessary
 * @author Mees Gelein
 */
public class LoaderPanel extends JDialog{

	/**Reference to the loading bar we are using to display progress*/
	public JProgressBar loadBar;
	/**The JLabel that displays the message*/
	public JLabel messageLabel = new JLabel();

	/**
	 * creates a new LoaderBar window and displays the given message String
	 * @param msg
	 */
	public LoaderPanel(final String msg){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				//set empty image as icon
				setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
				setTitle(msg);
				
				//createGUI
				loadBar = new JProgressBar();
				add(BorderLayout.CENTER, loadBar);
				messageLabel.setText(msg + "...");
				add(BorderLayout.NORTH, messageLabel);
				setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				setSize(300, 75);
				setPreferredSize(getSize());
				setPercentage(0);
				setResizable(false);
				setLocationRelativeTo(null);
				setVisible(true);
			}
		});
	}
	
	/**
	 * Schedules a String change
	 * @param s
	 */
	public void setString(final String s){
		Runnable changeValueRunnable = new Runnable(){
			public void run(){
				loadBar.setString(s);
			}
		};
		SwingUtilities.invokeLater(changeValueRunnable);
		messageLabel.setText(s);
	}
	
	/**
	 * Sets the loadBar to indeterminate loading. This is loading that
	 * takes an unknown amount of time
	 * @param b
	 */
	public void setIndeterminate(final boolean b){
		Runnable changeValueRunnable = new Runnable(){
			public void run(){
				loadBar.setIndeterminate(b);
			}
		};
		SwingUtilities.invokeLater(changeValueRunnable);
	}

	/**
	 * sets the percentage completed of the loadBar. When above
	 * 100 the loadBar automatically closes
	 * @param i
	 */
	public void setPercentage(int i){
		if(loadBar != null) loadBar.setValue(i);
		if(i >= 100){
			dispose();
		}
	}

}
