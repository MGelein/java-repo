package trb1914.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import trb1914.debug.Debug;
import trb1914.helper.AudioHelper;
import trb1914.helper.ImageHelper;
import trb1914.helper.SystemHelper;

public class OnScreenKeyboard extends JPanel {

	/**Should buttons make a sound when pressed?*/
	public static boolean BUTTON_SOUND = true;
	/**The font-size used on the keys*/
	public static float FONT_SIZE = 17.0f;
	/**The amt of gain on the sound (in decibel)*/
	public static float SOUND_GAIN = 0.0f;
	
	/**The backgroundColor*/
	public static Color bgColor = new Color(0.7f, 0.8f, 0.8f);
	
	/**The keys on the keyboard*/
	private static final String[][] keys = new String[][]{{"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"},
															{"a", "s", "d", "f", "g", "h", "j", "k", "l"},
															{"z", "x", "c", "v", "b", "n", "m"}};
	/**The keys on the numpad*/
	private static final String[][] nums = new String[][]{{"1", "2", "3"},{"4", "5", "6"}, {"7", "8", "9"}};
	
	/**The size of an individual key*/
	public static Dimension KEY_SIZE = new Dimension(64, 64);
	
	/**The robot instance used to do the actual typing*/
	private Robot typeRobot;
	/**The font used on all keys*/
	private Font keyFont;
	/**The panel that holds any extra buttons added by the user*/
	private JPanel miscPanel = new JPanel();
	
	/**
	 * When you launch this class seperately you launch a test version
	 * @param args
	 */
	public static void main(String[] args) {
		SystemHelper.setSystemDefaultLF();
		JFrame frame = new JFrame();
		frame.add(new OnScreenKeyboard());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * Creates a new OnScreenKeyboard Panel
	 */
	public OnScreenKeyboard() {
		keyFont = (new JButton()).getFont().deriveFont(FONT_SIZE).deriveFont(Font.BOLD);
		try {
			typeRobot = new Robot();
		} catch (AWTException e) {
			Debug.println("Couldn't initialize a Robot", this);
			e.printStackTrace();
		}
		makeGUI();
	}
	
	/**
	 * Populates this panel
	 */
	private void makeGUI(){
		setBorder(BorderFactory.createLineBorder(Color.gray));
		setBackground(bgColor);
		
		//panel that holds the letter keys
		JPanel letterPanel = new JPanel();
		add(letterPanel);
		letterPanel.setLayout(new GridLayout(keys.length + 1, 1));
		letterPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		letterPanel.setBackground(bgColor);
		
		for(String[] row : keys){
			JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
			rowPanel.setBackground(bgColor);
			letterPanel.add(rowPanel);
			for(String key : row){
				rowPanel.add(getButtonForKey(key));
			}
		}
		
		JPanel numPanel = new JPanel();
		add(numPanel);
		numPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		numPanel.setBackground(bgColor);
		numPanel.setLayout(new BorderLayout());
		JPanel numPad = new JPanel(new GridLayout(nums.length + 1, 1));
		numPanel.add(numPad);
		
		for(String[] row : nums){
			JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
			rowPanel.setBackground(bgColor);
			numPad.add(rowPanel);
			for(String key : row){
				rowPanel.add(getButtonForKey(key));
			}
		}
		JPanel spacePanel = new JPanel(new BorderLayout());
		spacePanel.setBackground(bgColor);
		JButton spaceKey = getButtonForKey("SPACE");
		spaceKey.setText("Spatie");
		spacePanel.add(spaceKey);
		letterPanel.add(spacePanel);
		
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setBackground(bgColor);
		numPad.add(southPanel);
		southPanel.add(getButtonForKey("0"));
		
		JPanel eastPanel = new JPanel(new GridLayout(2, 1));
		eastPanel.setBackground(bgColor);
		numPanel.add(eastPanel, BorderLayout.EAST);
		JButton enterKey = getButtonForKey("ENTER");
		enterKey.setText("");
		enterKey.setIcon(ImageHelper.getImageIcon("/trb1914/img/enter.png"));
		
		JButton backKey = getButtonForKey("BACKSPACE");
		backKey.setText("");
		backKey.setIcon(ImageHelper.getImageIcon("/trb1914/img/backspace.png"));
		eastPanel.add(backKey);
		eastPanel.add(enterKey);
		
		add(miscPanel);
		miscPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		miscPanel.setBackground(bgColor);
		miscPanel.setLayout(new BorderLayout());
	}
	
	/**
	 * Adds an extra button to the misc panel (most right panel)
	 * @param button
	 */
	public void addButton(JButton button){
		miscPanel.add(button);
	}
	
	/**
	 * Returns a new JButton completely setup
	 * @param key
	 * @return
	 */
	private JButton getButtonForKey(final String key){
		JButton button = new JButton(key.toUpperCase());
		button.setBackground(bgColor);
		button.setFont(keyFont);
		button.setFocusable(false);
		button.setFocusPainted(false);
		button.setSize(KEY_SIZE);
		button.setPreferredSize(KEY_SIZE);
		button.setMinimumSize(KEY_SIZE);
		button.setMaximumSize(KEY_SIZE);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				typeKey(key);
			}
		});
		return button;
	}
	
	/**
	 * Types the specified key (only standard characters)
	 * @param key
	 */
	private void typeKey(String key){
		switch(key.toUpperCase()){
		case "A":
			pressReleaseKey(KeyEvent.VK_A);
			break;
		case "B":
			pressReleaseKey(KeyEvent.VK_B);
			break;
		case "C":
			pressReleaseKey(KeyEvent.VK_C);
			break;
		case "D":
			pressReleaseKey(KeyEvent.VK_D);
			break;
		case "E":
			pressReleaseKey(KeyEvent.VK_E);
			break;
		case "F":
			pressReleaseKey(KeyEvent.VK_F);
			break;
		case "G":
			pressReleaseKey(KeyEvent.VK_G);
			break;
		case "H":
			pressReleaseKey(KeyEvent.VK_H);
			break;
		case "I":
			pressReleaseKey(KeyEvent.VK_I);
			break;
		case "J":
			pressReleaseKey(KeyEvent.VK_J);
			break;
		case "K":
			pressReleaseKey(KeyEvent.VK_K);
			break;
		case "L":
			pressReleaseKey(KeyEvent.VK_L);
			break;
		case "M":
			pressReleaseKey(KeyEvent.VK_M);
			break;
		case "N":
			pressReleaseKey(KeyEvent.VK_N);
			break;
		case "O":
			pressReleaseKey(KeyEvent.VK_O);
			break;
		case "P":
			pressReleaseKey(KeyEvent.VK_P);
			break;
		case "Q":
			pressReleaseKey(KeyEvent.VK_Q);
			break;
		case "R":
			pressReleaseKey(KeyEvent.VK_R);
			break;
		case "S":
			pressReleaseKey(KeyEvent.VK_S);
			break;
		case "T":
			pressReleaseKey(KeyEvent.VK_T);
			break;
		case "U":
			pressReleaseKey(KeyEvent.VK_U);
			break;
		case "V":
			pressReleaseKey(KeyEvent.VK_V);
			break;
		case "W":
			pressReleaseKey(KeyEvent.VK_W);
			break;
		case "X":
			pressReleaseKey(KeyEvent.VK_X);
			break;
		case "Y":
			pressReleaseKey(KeyEvent.VK_Y);
			break;
		case "Z":
			pressReleaseKey(KeyEvent.VK_Z);
			break;
		case "0":
			pressReleaseKey(KeyEvent.VK_0);
			break;
		case "1":
			pressReleaseKey(KeyEvent.VK_1);
			break;
		case "2":
			pressReleaseKey(KeyEvent.VK_2);
			break;
		case "3":
			pressReleaseKey(KeyEvent.VK_3);
			break;
		case "4":
			pressReleaseKey(KeyEvent.VK_4);
			break;
		case "5":
			pressReleaseKey(KeyEvent.VK_5);
			break;
		case "6":
			pressReleaseKey(KeyEvent.VK_6);
			break;
		case "7":
			pressReleaseKey(KeyEvent.VK_7);
			break;
		case "8":
			pressReleaseKey(KeyEvent.VK_8);
			break;
		case "9":
			pressReleaseKey(KeyEvent.VK_9);
			break;
		case "ENTER":
			pressReleaseKey(KeyEvent.VK_ENTER);
			break;
		case "BACKSPACE":
			pressReleaseKey(KeyEvent.VK_BACK_SPACE);
			break;
		case "SPACE":
			pressReleaseKey(KeyEvent.VK_SPACE);
			break;
		}
	}
	
	/**
	 * Presses and releases the key, effectively typing it
	 * @param keyCode
	 */
	private void pressReleaseKey(int keyCode){
		if(BUTTON_SOUND) AudioHelper.playSound("/trb1914/audio/click.wav");
		typeRobot.keyPress(keyCode);
		typeRobot.keyRelease(keyCode);
	}

}
