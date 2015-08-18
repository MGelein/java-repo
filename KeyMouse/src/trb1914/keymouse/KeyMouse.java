package trb1914.keymouse;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import trb1914.helper.ImageHelper;
import trb1914.helper.SystemHelper;
import trb1914.preferences.Preferences;

public class KeyMouse extends TimerTask implements NativeKeyListener{


	/**
	 * Flag to determine if the stopKey is down
	 */
	private static boolean stopKeyDown = false;
	/**
	 * Flag to determine if the mouse needs to go left
	 */
	private static boolean leftKeyDown = false;

	private static boolean rightKeyDown = false;
	private static boolean upKeyDown = false;
	private static boolean downKeyDown = false;
	private static boolean precisionDown = false;
	private static boolean rightPressed = false;
	private static boolean leftPressed = false;
	private static float vx = 0;
	private static float vy = 0;
	private static float maxSpeed = 0;
	private static float precisionSpeed = 0;
	private static float speed = 0.0f;
	private static float acceleration;
	/**
	 * The timer that fires of the input events
	 */
	private static Timer inputTimer;
	/**
	 * The robot that moves the mouse
	 */
	private static Robot mouseRobot;
	/**
	 * The location of the mouse on the screen
	 */
	private static Point mousePoint = new Point(0, 0);
	/**
	 * The main entry point
	 * @param args
	 */
	public static void main(String[] args) {
		//Disables the atrocious amount of logging by default :)
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		
		Preferences.load("config.properties");
		acceleration = Preferences.getFloat("mouseAcceleration", 0.1f);
		precisionSpeed = Preferences.getFloat("precisionSpeed", 10.0f);
		maxSpeed = Preferences.getFloat("maxSpeed", 60.0f);
		Preferences.set("mouseAcceleration", acceleration);
		Preferences.set("precisionSpeed", precisionSpeed);
		Preferences.set("maxSpeed", maxSpeed);
		Preferences.save("config.properties", "Saved by KeyMouse. Some mouse settings.");

		//center mouse on screen
		mousePoint.y = SystemHelper.SCREEN_SIZE.height / 2;
		mousePoint.x = SystemHelper.SCREEN_SIZE.width / 2;

		try{
			mouseRobot = new Robot();
			mouseRobot.mouseMove(mousePoint.x, mousePoint.y);
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}

		inputTimer = new Timer(false);
		KeyMouse keyMouse = new KeyMouse();
		inputTimer.scheduleAtFixedRate(keyMouse, 0, 16);

		keyMouse.hook();

		//create tray menu
		PopupMenu popup = new PopupMenu("KeyMouse");
		MenuItem closeItem = new MenuItem("Close...");
		closeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keyMouse.unhook();
				System.exit(0);
			}
		});
		popup.add(closeItem);
		TrayIcon trayIcon = new TrayIcon(ImageHelper.getImage("/trb1914/keymouse/logo.png").getScaledInstance(16, 16, Image.SCALE_SMOOTH), "KeyMouse");
		trayIcon.setPopupMenu(popup);
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when the timer runs out
	 */
	public void run(){
		if(precisionDown) speed = precisionSpeed;
		else speed = maxSpeed;
		
		if(leftKeyDown){
			vx += (-speed - vx) * acceleration;
		}else if(rightKeyDown){
			vx += (speed - vx) * acceleration;
		}else{
			vx = 0;
		}

		if(upKeyDown){
			vy += (-speed - vy) * acceleration;
		}else if(downKeyDown){
			vy += (speed - vy) * acceleration;
		}else{
			vy = 0;
		}

		if(vx != 0 || vy != 0){
			mousePoint = MouseInfo.getPointerInfo().getLocation();
			mousePoint.x += vx;
			mousePoint.y += vy;
			
			mouseRobot.mouseMove(mousePoint.x, mousePoint.y);
		}

		//stop when we want to stop
		if(stopKeyDown){
			unhook();
			System.exit(0);
		}
	}

	/**
	 * Called when a key is pressed
	 */
	public void nativeKeyPressed(NativeKeyEvent e) {
		if(nativeKey(e.getKeyCode(), true)){
			try {
				Field f = NativeInputEvent.class.getDeclaredField("reserved");
				f.setAccessible(true);
				f.setShort(e, (short) 0x01);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}	

	/**
	 * Called when a key is released
	 */
	public void nativeKeyReleased(NativeKeyEvent e) {
		if(nativeKey(e.getKeyCode(), false)){
			try {
				Field f = NativeInputEvent.class.getDeclaredField("reserved");
				f.setAccessible(true);
				f.setShort(e, (short) 0x01);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private boolean nativeKey(int keyCode, boolean down){
		if(keyCode == NativeKeyEvent.VC_KP_DIVIDE){
			stopKeyDown = down;
			return true;
		}
		if(keyCode == NativeKeyEvent.VC_KP_4){
			leftKeyDown = down;
			return true;
		}
		if(keyCode == NativeKeyEvent.VC_KP_6){
			rightKeyDown = down;
			return true;
		}
		if(keyCode == NativeKeyEvent.VC_KP_8){
			upKeyDown = down;
			return true;
		}
		if(keyCode == NativeKeyEvent.VC_KP_5){
			downKeyDown = down;
			return true;
		}
		if(keyCode == NativeKeyEvent.VC_KP_0){
			precisionDown = down;
			return true;
		}
		
		//mouse buttons are handled immediately
		if(keyCode == NativeKeyEvent.VC_KP_7){
			if(down){
				if(!leftPressed) {
					leftPressed = true;
					mouseRobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				}
			}else{
				mouseRobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				leftPressed = false;
			}
			return true;
		}
		
		if(keyCode == NativeKeyEvent.VC_KP_9){
			if(down){
				if(!rightPressed){
					rightPressed = true;
					mouseRobot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				}
			}else{
				mouseRobot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				rightPressed = false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Called when a key is typed
	 */
	public void nativeKeyTyped(NativeKeyEvent e) {}

	/**
	 * Removes all native hooks
	 */
	public void unhook(){
		GlobalScreen.getInstance().removeNativeKeyListener(this);
	}
	/**
	 * Installs the native keyboard hook
	 */
	public void hook(){
		try{
			GlobalScreen.unregisterNativeHook();
			GlobalScreen.registerNativeHook();
		}catch(Exception e){
			System.out.println("Tried to register native hook, but failed :(");
		}
		//adds this class as the actual listener
		GlobalScreen.getInstance().addNativeKeyListener(this);
		GlobalScreen.getInstance().setEventDispatcher(new VoidExecutorService());
	}


	/**
	 * Private class from the jnativehook google code page
	 */
	private class VoidExecutorService extends AbstractExecutorService {
		private boolean isRunning;

		public VoidExecutorService() {
			isRunning = true;
		}

		public void shutdown() {
			isRunning = false;
		}

		public List<Runnable> shutdownNow() {
			return new ArrayList<Runnable>(0);
		}

		public boolean isShutdown() {
			return !isRunning;
		}

		public boolean isTerminated() {
			return !isRunning;
		}

		public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
			return true;
		}

		public void execute(Runnable r) {
			r.run();
		}
	}
}
