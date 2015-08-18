package trb1914.cmd.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * this class provides the means to launch commands from this app
 * @author Mees Gelein
 */
public class CMDLauncher extends JFrame{

	/**
	 * No idea why, but apparently we need a UID serial number..
	 */
	private static final long serialVersionUID = 5917033323045369427L;

	/**
	 * The target size, if we are animating, we are trying to reach this target size
	 */
	private Dimension targetSize = new Dimension(800, 135);
	private boolean animating = true;
	private float easeFactor = 0.2f;
	private float alphaFactor = 0.05f;
	private float dx = 0;
	private float dy = 0;
	private String[] commands = new String[]{"open", "create", "check", "get"};
	private String[] internal = new String[]{"shoo", "close", "cancel", "quit", "clear"};
	private JTextArea responseArea = new JTextArea();

	/**
	 * The initial state of the bar
	 */
	private final Dimension START_SIZE = new Dimension(3000, 0);

	/**
	 * Entry point. args = command line params
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CMDLauncher();
			}
		});
	}

	/**
	 * The CMDLauncher constructor, takes no parameters
	 */
	public CMDLauncher() {
		makeGUI();

		Timer animTimer = new Timer(16, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(animating) animate();
			}
		});
		animTimer.start();

		setSize(START_SIZE);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Performs one frame of animation
	 */
	private void animate(){
		dx = targetSize.width - dx;
		dy = targetSize.height - dy;
		if(Math.abs(dx) < 10.0f && Math.abs(dy) < 7.0f){
			setSize(targetSize);
			animating = false;
			if(targetSize.height == 0){//we want to close
				close();
			}
		}
		dx *= easeFactor;
		dy *= easeFactor;
		setOpacity(Math.max(0.0f, Math.min(0.8f, getOpacity() + alphaFactor)));
		dx += getSize().width;
		dy += getSize().height;
		int tempX = (dx == 0) ? 0 : (int)(dx + 0.5f);
		int tempY = (dy == 0) ? 0 : (int)(dy + 0.5f);
		setSize(tempX, tempY);
		setLocationRelativeTo(null);
		repaint();
	}

	/**
	 * Builds the UI
	 */
	private void makeGUI() {
		setUndecorated(true);
		setType(Window.Type.UTILITY);
		setLayout(new BorderLayout());
		setBackground(new Color(0, 0, 0, 0));
		setOpacity(0.0f);//slightly see-through

		//the textfield we use for input
		JTextField inputField = new JTextField();
		add(inputField, BorderLayout.NORTH);
		inputField.requestFocus();
		inputField.setHorizontalAlignment(JTextField.CENTER);

		//create the borders, fonts and colors
		inputField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		inputField.setBackground(Color.BLACK);
		inputField.setForeground(Color.GREEN);
		inputField.setCaretColor(Color.GREEN);
		inputField.setFont(Font.decode("Courier New-20").deriveFont(Font.BOLD));

		//the necessary actionlisteners
		inputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute(inputField.getText());
				inputField.setText("");
				inputField.requestFocus();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(responseArea);
		scrollPane.setBackground(getBackground());
		scrollPane.setBorder(null);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		responseArea.setFont(inputField.getFont().deriveFont(15.0f));
		add(scrollPane);
		responseArea.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GREEN));
		responseArea.setForeground(inputField.getForeground());
		responseArea.setBackground(inputField.getBackground());

		inputField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				die();
			}
		});
	}

	/**
	 * Closes this window
	 */
	private void close(){
		System.exit(0);
	}

	/**
	 * Displays the death animation and closes the window
	 */
	private void die(){
		targetSize = START_SIZE;
		animating = true;
		alphaFactor = -alphaFactor;
	}

	/**
	 * Executes the provided string into the command prompt
	 * @param s
	 */
	private void execute(final String s){
		//first  check if it is a close command for the interface, this we don't need to fwd to the cmd line
		for(String internalCmd : internal) if(internalCmd.equals(s)){ internalCommand(s); return;}
		
		new Thread(new Runnable() {
			public void run() {
				//try to format to ensure that the first parameter points to a .bat file if it is a recognized command
				String[] args = s.split(" ");
				for(String cmd : commands) if(cmd.equals(args[0])){
					args[0] += ".bat";
				}
				String newCommand = "";
				for(String part : args) newCommand += part + " ";
				
				//then if it is not a cmd for the interface, we fwd it to the cmd line
				try {
					Process p = Runtime.getRuntime().exec(newCommand);
					BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = r.readLine();
					while(line != null){
						final String l = line;
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								responseArea.append(l);
								responseArea.append("\n");
								responseArea.setCaretPosition(responseArea.getText().length());
							}
						});
						line = r.readLine();
						if(line.startsWith("C:")) line = r.readLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Executes a command related to the program itself
	 * @param s
	 */
	private void internalCommand(String s){
		switch(s){
		case "cancel":
		case "shoo":
		case "close":
		case "quit": 
			die();
			break;
		case "clear":
			responseArea.setText("");
			break;
		}
	}
}
