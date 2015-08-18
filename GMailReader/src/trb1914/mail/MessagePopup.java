package trb1914.mail;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.mail.Folder;
import javax.mail.Store;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
/**
 * Used for notification of new messages
 * @author Mees Gelein
 */
public class MessagePopup extends JWindow{

	public static MessagePopup current = null;
	private static final int STAGE_EASE_IN = 0;
	private static final int STAGE_EASE_OUT = 2;
	private static final int STAGE_DISPLAY = 1;
	
	private Timer animTimer;
	private Rectangle bounds = new Rectangle(0, 0, 400, 0);
	private int stage = STAGE_EASE_IN;
	private int counter = 0;
	private float targetHeight = 100;
	private final float easeFactor = 0.2f;
	private final int DISPLAY_TIME = 50; //~2,5 seconds
	private MessageData[] messages;
	private int messageIndex = 0;
	private JLabel subjectField = new JLabel();
	private JLabel senderField = new JLabel();
	private JLabel contentField = new JLabel();
	private Folder inboxFolder;
	private Store store;
	private int easeDir = 1;
	
	/**
	 * Constructor for debugging only
	 * @param newMessages
	 */
	public MessagePopup(MessageData[] newMessages){
		this(newMessages, null, null);
	}
	/**
	 * Creates a new messagepopup
	 */
	public MessagePopup(MessageData[] newMessages, Folder inbox, Store s){
		if(current == null){
			current = this;
		}else{
			current.die();
			current = this;
		}
		GMailReader.main.playSound();
		store = s;
		inboxFolder = inbox;
		setBounds(bounds);
		messageIndex = newMessages.length - 1;
		messages = newMessages;
		setVisible(true);
		makeGUI();
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		switch(currentLocation){
		case TOP_LEFT:
			bounds.x = 0;
			bounds.y = 0;
			easeDir = 1;
			break;
		case TOP_RIGHT:
			bounds.x = screen.width - bounds.width;
			bounds.y = 0;
			easeDir = 1;
			break;
		case BOTTOM_LEFT:
			bounds.x = 0;
			bounds.y = screen.height;
			easeDir = -1;
			break;
		case BOTTOM_RIGHT:
			bounds.x = screen.width - bounds.width;
			bounds.y = screen.height;
			easeDir = -1;
			break;
		}
		
		animTimer = new Timer(50, new ActionListener(){
			public void actionPerformed(ActionEvent e){
				update();
			}
		});
		animTimer.start();
		
		this.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getButton() > 2){//rmb or mmb
					if(Desktop.isDesktopSupported()){
						Desktop desktop = Desktop.getDesktop();
						try{
							desktop.browse(new URI("http://mail.google.com"));
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
				current.die();
			}
		});
	}
	
	/**
	 * Makes the GUI
	 */
	private void makeGUI(){
		setAlwaysOnTop(true);
		setLayout(new GridLayout(2, 1));
		getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
		
		JPanel headerPanel = new JPanel(new GridLayout(2, 1));
		headerPanel.setBackground(Color.white);
		add(headerPanel);
		headerPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
		
		JPanel subjectPanel = new JPanel(new BorderLayout());
		subjectPanel.setBackground(Color.white);
		JLabel subjectLabel = new JLabel("<html><b>Subject: </b></html>");
		subjectLabel.setForeground(Color.gray);
		subjectPanel.add(subjectLabel, BorderLayout.WEST);
		subjectPanel.add(subjectField);
		headerPanel.add(subjectPanel);
		
		JPanel senderPanel = new JPanel(new BorderLayout());
		senderPanel.setBackground(Color.white);
		JLabel senderLabel = new JLabel("<html><b>Sender: </b></html>");
		senderLabel.setForeground(subjectLabel.getForeground());
		senderPanel.add(senderLabel, BorderLayout.WEST);
		senderPanel.add(senderField);
		senderPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		headerPanel.add(senderPanel);
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		add(contentPanel);
		contentPanel.add(contentField);
		contentField.setVerticalAlignment(SwingConstants.TOP);
		contentPanel.setBackground(Color.white);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 4, 4));
		loadMessage();
	}
	/**
	 * Loads the message into the GUI elements
	 */
	private void loadMessage(){
		subjectField.setText(messages[messageIndex].subject);
		senderField.setText("<html>" + messages[messageIndex].sender + "</html>");
		contentField.setText("<html>" + messages[messageIndex].parseContent() + "</html>");
	}
	
	/**
	 * Called to do any animation. Not called in the Event Dispatch Thread
	 */
	public void update(){
		
		switch(stage){
		case STAGE_EASE_IN:
			doEaseIn();
			break;
		case STAGE_EASE_OUT:
			doEaseOut();
			break;
		case STAGE_DISPLAY:
			doDisplay();
			break;
		}
	}
	
	/**
	 * Called to ease in  
	 */
	private void doEaseIn(){
		float dy = (targetHeight - bounds.height * 1.0f) * easeFactor;
		if(dy > 1) {
			bounds.height += (int) dy;
			if(easeDir < 0) bounds.y -= (int) dy;
		}else {
			bounds.height = (int) targetHeight;
			stage = STAGE_DISPLAY;
		}
		setBounds(bounds);
	}
	
	/**
	 * Called to ease out and stop displaying this popup
	 */
	private void doEaseOut(){
		float dy = (0.0f - bounds.height * 1.0f) * easeFactor;
		if(dy < -1) {
			bounds.height += ((int) dy);
			if(easeDir < 0) bounds.y -= (int) dy;
		}else {
			bounds.height = 0;
			die();
		}
		setBounds(bounds);
	}
	
	/**
	 * Keeps track of display time and displays the list of messages that are new
	 */
	private void doDisplay(){
		counter++;
		if(counter > DISPLAY_TIME){
			messageIndex--;
			counter = 0;
			if(messageIndex < 0){//if we have displayed the final message
				stage++;
			}else{
				loadMessage();
			}
		}
	}
	
	/**
	 * Kills this message popup and its timers
	 */
	public void die(){
		try{
			if(inboxFolder != null) inboxFolder.close(true);
			if(store != null) store.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		animTimer.stop();
		current = null;
		dispose();
	}
	/**
	 * Set location to one of the four possible locations: MessagePopup.TOP_LEFT, MessagePopup.TOP_RIGHT,
	 * MessagePopup.BOTTOM_LEFT or MessagePopup.BOTTOM_RIGHT;
	 * @param location
	 */
	public static void setLocation(int location){
		currentLocation = location;
	}
	public static int currentLocation = 1;
	public static final int TOP_LEFT = 1;
	public static final int TOP_RIGHT = 2;
	public static final int BOTTOM_LEFT = 3;
	public static final int BOTTOM_RIGHT = 4;
}
