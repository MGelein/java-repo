package trb1914.clipboardviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import trb1914.clipboardviewer.data.Registry;
import trb1914.debug.Debug;
import trb1914.helper.ImageHelper;

/**
 * Main class of the clipboard viewer
 * @author Mees Gelein
 */
public class ClipboardViewer extends JFrame implements ClipboardOwner{

	/**The clipboard image that is used to make up the main window*/
	public static final ImageIcon CLIPBOARD = ImageHelper.getImageIcon("/trb1914/img/clipboard.png");
	public static final ImageIcon LEFT_ARROW = ImageHelper.getImageIcon("/trb1914/img/arrow_left.png");
	public static final ImageIcon RIGHT_ARROW = ImageHelper.getImageIcon("/trb1914/img/arrow_right.png");
	public static final ImageIcon CLOCK = ImageHelper.getImageIcon("/trb1914/img/clock_64.png");
	
	/**Application width*/
	public static final int APP_WIDTH = CLIPBOARD.getIconWidth();
	/**Application height*/
	public static final int APP_HEIGHT = CLIPBOARD.getIconHeight();
	public static final int CONT_WIDTH = APP_WIDTH - 40;
	public static final int CONT_HEIGHT = APP_HEIGHT - 96;

	/**The popupmenu used by the application*/
	private PopupMenu popupMenu = getPopupMenu();

	private TrayIcon trayIcon;

	private JLabel titleLabel;
	private JPanel clipPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private MouseAdapter mouseAdapter;
	private JButton leftButton;
	private JButton rightButton;
	private JLabel indexLabel = new JLabel();

	private ArrayList<Transferable> clips = new ArrayList<Transferable>();
	private int maxSize = 9;
	private int cIndex = 0;
	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private ClipboardViewer selfRef = this;
	/**
	 * Creates a new ClipboardViewer instance
	 */
	public ClipboardViewer(){
		makeGUI();
		showLoading();
		createTrayIcon();
		setListeners();
		parseClipboardContents();
	}

	/**
	 * Automatically assumes that it is not being called by us
	 */
	private void parseClipboardContents(){
		parseClipboardContents(true);
	}
	
	/**
	 * Parses the clipboard contents
	 */
	private void parseClipboardContents(final boolean fromSys){
		new Thread(new Runnable() {
			public void run() {
				try{
					Thread.sleep(200);
				}catch(Exception e){}//waits untill the clipboard is ready
				Transferable contents = clipboard.getContents(null);
				if(contents.isDataFlavorSupported(DataFlavor.imageFlavor)){
					Debug.println("Imageflavor data", selfRef);
					try{
						Image i = (Image) contents.getTransferData(DataFlavor.imageFlavor);
						showImageContent(i);
					}catch(Exception e){
						Debug.println("Could not parse Image from data", selfRef);
						e.printStackTrace();
					}
				}else if(contents.isDataFlavorSupported(DataFlavor.stringFlavor)){
					Debug.println("Stringflavor data", selfRef);
					try{
						String s = (String) contents.getTransferData(DataFlavor.stringFlavor);
						showStringContent(s);
					}catch(Exception e){
						Debug.println("Could not parse String from data", selfRef);
					}
				}else if(contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					Debug.println("Filelistflavor data", selfRef);
					try{
						@SuppressWarnings("unchecked")
						List<File> l = (List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor);
						showFileList(l);
					}catch(Exception e){
						Debug.println("Could not parse Filelist from data", selfRef);
					}
				}else{
					Debug.println("Unknownflavor data: " + contents.getTransferDataFlavors()[0], selfRef);
					showStringContent("Geen- of onbekende data op het klembord...");
				}
				clipboard.setContents(contents, selfRef);
				
				if(fromSys){
					clips.add(0, contents);
					while(clips.size() >= maxSize){
						clips.remove(clips.size() - 1);
					}
				}
				updateIndex();
			}
		}).start();
	}

	/**
	 * Shows the list of files
	 * @param list
	 */
	private void showFileList(final List<File> list){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				getContentPane().removeAll();
				repaint();
				add(titleLabel, BorderLayout.NORTH);
				add(buttonPanel, BorderLayout.SOUTH);
				FileSystemView view = FileSystemView.getFileSystemView();
				JPanel iconPanel = new JPanel(new GridLayout(10, 1));
				add(iconPanel);
				iconPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
				iconPanel.setBackground(new Color(0, 0, 0, 0));
				String name = "";
				for(File f : list){
					JLabel l = new JLabel(view.getSystemIcon(f));
					l.setHorizontalAlignment(SwingConstants.LEFT);
					name = f.getName();
					if(name.length() > 15) name = name.substring(0, 15) + "...";
					l.setText(name);
					iconPanel.add(l);
				}
			}
		});
		
	}

	/**
	 * Shows the provided String
	 * @param s
	 */
	private void showStringContent(final String s){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getContentPane().removeAll();
				repaint();
				add(titleLabel, BorderLayout.NORTH);
				add(buttonPanel, BorderLayout.SOUTH);
				JTextArea area = new JTextArea();
				area.setLineWrap(true);
				area.setWrapStyleWord(true);
				area.setBackground(new Color(0, 0, 0, 0));
				area.setEditable(false);
				area.setHighlighter(null);
				area.addMouseListener(mouseAdapter);
				area.addMouseMotionListener(mouseAdapter);
				add(area);
				area.setFont(titleLabel.getFont());
				area.setText(s);
				area.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			}
		});
	}

	/**
	 * Shows the provided Image
	 * @param i
	 */
	private void showImageContent(final Image i){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				int w = i.getWidth(null);
				int h = i.getHeight(null);
				//Debug.println("Found image: [" + w + ", " + h + "]");
				if(w > CONT_WIDTH){
					w = CONT_WIDTH;
				}
				if(h > CONT_HEIGHT){
					h = CONT_HEIGHT;
				}
				//Debug.println("Cropped image to : [" + w + ", " + h + "]");
				getContentPane().removeAll();
				repaint();
				add(titleLabel, BorderLayout.NORTH);
				add(buttonPanel, BorderLayout.SOUTH);
				Image scaledImage = i.getScaledInstance(w, h, Image.SCALE_FAST);
				JLabel imgLabel = new JLabel(new ImageIcon(scaledImage));
				add(imgLabel);
				imgLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			}
		});

	}
	
	/**
	 * Shows the loading symbol
	 */
	private void showLoading(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getContentPane().removeAll();
				add(titleLabel, BorderLayout.NORTH);
				add(buttonPanel, BorderLayout.SOUTH);
				JLabel imgLabel = new JLabel(CLOCK);
				add(imgLabel);
				repaint();
			}
		});
	}
	
	/**
	 * Updates the index view and its buttons
	 */
	private void updateIndex(){
		indexLabel.setText((cIndex + 1 ) + "/" + maxSize);
		leftButton.setEnabled(true);
		rightButton.setEnabled(true);
		if(cIndex >= clips.size() - 1){
			leftButton.setEnabled(false);
		}
		if(cIndex <= 0){
			rightButton.setEnabled(false);
		}
		repaint();
	}

	/**
	 * Sets up all the listeners
	 */
	private void setListeners(){
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				closeWindow();
			}
		});
		mouseAdapter = new MouseAdapter(){
			private int posX = 0;
			private int posY = 0;
			public void mousePressed(MouseEvent e){
				posX = e.getX();
				posY = e.getY();
				if(e.getButton() == 3){
					popupMenu.show(e.getComponent(), posX, posY);
				}
			}			
			public void mouseDragged(MouseEvent e){
				setLocation(e.getXOnScreen() - posX , e.getYOnScreen()-posY);
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	/**
	 * Builds the GUI
	 */
	private void makeGUI(){
		add(popupMenu);
		setLayout(new BorderLayout());
		setUndecorated(true);
		//setType(Type.UTILITY);
		setContentPane(new BackgroundImagePanel());
		setIconImage(CLIPBOARD.getImage());
		setBackground(new Color(0, 0, 0, 0));

		titleLabel = new JLabel("<html><b>" + Registry.TITLE + "</b></html>");
		titleLabel.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));
		add(titleLabel, BorderLayout.NORTH);
		add(clipPanel);
		
		final ClipboardViewer selfRef = this;
		buttonPanel = new JPanel();
		buttonPanel.setBackground(new Color(0, 0, 0, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		add(buttonPanel, BorderLayout.SOUTH);
		leftButton = new JButton(LEFT_ARROW);
		leftButton.setBackground(new Color(0, 0, 0, 0));
		leftButton.setBorder(BorderFactory.createEmptyBorder());
		leftButton.setContentAreaFilled(false);
		leftButton.setMnemonic(KeyEvent.VK_LEFT);
		leftButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showLoading();
				cIndex++;
				if(cIndex >= maxSize) cIndex = maxSize - 1;
				clipboard.setContents(clips.get(cIndex), selfRef);
				parseClipboardContents(false);
			}
		});
		rightButton = new JButton(RIGHT_ARROW);
		rightButton.setBackground(new Color(0, 0, 0, 0));
		rightButton.setBorder(BorderFactory.createEmptyBorder());
		rightButton.setContentAreaFilled(false);
		rightButton.setMnemonic(KeyEvent.VK_RIGHT);
		rightButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showLoading();
				cIndex--;
				if(cIndex < 0) cIndex = 0;
				clipboard.setContents(clips.get(cIndex), selfRef);
				parseClipboardContents(false);
			}
		});
		buttonPanel.add(leftButton);
		buttonPanel.add(indexLabel);
		buttonPanel.add(rightButton);
	}

	/**
	 * Creates the popup menu to close this application
	 * @return
	 */
	private PopupMenu getPopupMenu(){
		PopupMenu menu = new PopupMenu();
		MenuItem closeItem = new MenuItem(Registry.CLOSE);
		closeItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeWindow();
			}
		});
		MenuItem emptyItem = new MenuItem(Registry.EMPTY);
		emptyItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clipboard.setContents(new Transferable() {
					public DataFlavor[] getTransferDataFlavors() {
						return new DataFlavor[0];
					}

					public boolean isDataFlavorSupported(DataFlavor flavor) {
						return false;
					}

					public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
						throw new UnsupportedFlavorException(flavor);
					}
				}, null);
			}
		});

		MenuItem minimItem = new MenuItem(Registry.MINIMIZE);
		minimItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				minimizeToTray();
			}
		});
		menu.add(emptyItem);
		menu.add(minimItem);
		menu.addSeparator();
		menu.add(closeItem);
		return menu;
	}

	/**
	 * Minimizes the main window to the SystemTray if supported
	 */
	private void minimizeToTray(){
		if(SystemTray.isSupported()){
			SystemTray systemTray = SystemTray.getSystemTray();
			try{
				setVisible(false);
				systemTray.add(trayIcon);
				setVisible(false);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens the tray back to normal
	 */
	private void openFromTray(){
		if(SystemTray.isSupported()){
			SystemTray systemTray = SystemTray.getSystemTray();
			systemTray.remove(trayIcon);
			setVisible(true);
		}
	}

	private void createTrayIcon(){
		ActionListener openAction = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFromTray();
			}
		};

		//Creating the popupmenu
		PopupMenu menu = new PopupMenu();
		MenuItem openItem = new MenuItem("Open");
		openItem.addActionListener(openAction);
		menu.add(openItem);

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeWindow();
			}
		});
		menu.addSeparator();
		menu.add(exitItem);

		//scaling the icon
		Image image = CLIPBOARD.getImage();
		trayIcon = new TrayIcon(image.getScaledInstance(16, 16, Image.SCALE_SMOOTH), titleLabel.getText(), menu);
		trayIcon.addActionListener(openAction);
	}

	/**
	 * Closes the program
	 */
	private void closeWindow(){
		dispose();
		System.exit(0);
	}


	/**
	 * Application entry point
	 * @param args	cmd line arguments
	 */
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			Debug.println("Settings LF to System default failed:" + e.getClass().getName(), getClassName());
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				ClipboardViewer c = new ClipboardViewer();
				c.setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));
				c.setMaximumSize(c.getPreferredSize());
				c.setMinimumSize(c.getMaximumSize());
				c.setLocationRelativeTo(null);
				int x = Toolkit.getDefaultToolkit().getScreenSize().width - APP_WIDTH;
				int y = 0;
				c.setLocation(x, y);
				c.setVisible(true);
			}
		});
	}

	/**
	 * Returns the fully qualified className
	 * @return
	 */
	private static String getClassName(){
		return "trb1914.clipboardviewer.ClipboardViewer";
	}

	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
		int imageWidth  = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double)width/imageWidth;
		double scaleY = (double)height/imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter(
				image,
				new BufferedImage(width, height, image.getType()));
	}

	/**
	 * Class to allow for a image background of the JFrame. Set the content PAne to this
	 * @author Mees Gelein
	 */
	private class BackgroundImagePanel extends JPanel{

		/**Used to draw the bg*/
		private Image image = CLIPBOARD.getImage();

		/**
		 * Creates a new instance of BackgroundImagePanel
		 */
		public BackgroundImagePanel(){
			setBackground(new Color(0, 0, 0, 0));
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		}

		/**
		 * Used to draw the component
		 */
		protected void paintComponent(Graphics g){
			g.drawImage(image, 0, 0, null);
		}
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		parseClipboardContents();
	}
}
