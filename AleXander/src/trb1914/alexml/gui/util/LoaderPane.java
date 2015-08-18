package trb1914.alexml.gui.util;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import trb1914.alexml.Main;
import trb1914.alexml.data.FileRegistry;
/**
 * a small class that can show a loadBar when necessary
 * @author Mees Gelein
 */
public class LoaderPane extends JDialog{

	//reference to the loadBar object to allow to set it to a undefined lenght loadbar
	public JProgressBar loadBar;

	/**
	 * creates a new LoaderBar window and displays the given message String
	 * @param msg
	 */
	public LoaderPane(final String msg){
		Runnable initRunner = new Runnable(){
			public void run(){
				//the normal constructor
				ImageIcon image= new ImageIcon(FileRegistry.APP_ICON);
				setIconImage(image.getImage());
				setTitle(msg);
				loadBar = new JProgressBar();
				add(BorderLayout.CENTER, loadBar);
				add(BorderLayout.NORTH, new JLabel(msg + "..."));
				setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				setSize(300, 75);
				setLocationRelativeTo(Main.main);
				setPercentage(0);
				setLocationRelativeTo(null);
				setVisible(true);
			}
		};
		SwingUtilities.invokeLater(initRunner);
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
