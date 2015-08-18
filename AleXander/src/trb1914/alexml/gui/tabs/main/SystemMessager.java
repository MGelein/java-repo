package trb1914.alexml.gui.tabs.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * tiny class to allow system message to be printed at the bottom
 * of the screen
 * @author Mees Gelein
 *
 */
public class SystemMessager extends JPanel {

	//amount of millis to display the message 
	private static final int DISPLAY_TIME = 5000;
	
	//the label that actually displays the information
	private JLabel label = new JLabel("");
	
	private ArrayList<String> msgList = new ArrayList<String>();
	/**
	 * creates a new systemMessager. only one is needed
	 */
	public SystemMessager(){
		setLayout(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		panel.add(label, BorderLayout.WEST);
		label.setEnabled(false);
		add(panel);
		setBorder(BorderFactory.createEmptyBorder(0,8,2,2));
	}
	/**
	 * displays the given String immediately. For queued behavior use addtoList
	 * @param s
	 */
	public void display(final String s){
		new Thread(new Runnable(){
			public void run(){
				label.setText(s);
				try {
					Thread.sleep(DISPLAY_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				label.setText(" ");
				if(msgList.size() > 0){
					display(msgList.get(0));
					msgList.remove(0);
				}
			}
		}).start();
	}
	/**
	 * adds the specified String to the queue of messages to display
	 * @param s
	 */
	public void addToList(String s){
		msgList.add(s);
	}
}
