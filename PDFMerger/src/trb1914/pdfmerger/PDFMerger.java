package trb1914.pdfmerger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.util.PDFMergerUtility;

import trb1914.helper.ImageHelper;
/**
 * Nothing but a shell for PDFMergerUtility from PDFBox (apache) 
 * @author Mees Gelein
 */
public class PDFMerger extends JFrame{

	//STATIC STRINGS
	public static final String APP_TITLE = "PDF Merger";
	public static final String APP_VERSION = "0.0.5";
	public static final String LABEL_ADD_NEW = "Add new PDF file";
	public static final String LABEL_MERGE = "Merge PDF files";
	
	//ICONS
	public static ImageIcon APP_ICON;
	public static ImageIcon ICON_ARROW_UP;
	public static ImageIcon ICON_ARROW_DOWN;
	public static ImageIcon ICON_TICK;
	public static ImageIcon ICON_PLUS;
	public static ImageIcon ICON_DELETE;
	
	//GUI Components
	private JButton plusButton;
	private JButton mergeButton;
	private JButton deleteButton;
	private JButton upButton;
	private JButton downButton;
	private JCheckBox openBox;
	private JList<PDFEntry> fileList;
	
	//DATA HOLDERS
	private Vector<PDFEntry> pdfFiles = new Vector<PDFEntry>();

	/**
	 * Program entry
	 * @param args
	 */
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			System.out.println("[PDFMerger]: Couldn't set LF to system default. Using cross platform LF.");
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				PDFMerger mainFrame = new PDFMerger();
				mainFrame.setTitle(PDFMerger.APP_TITLE + " " + PDFMerger.APP_VERSION);
				mainFrame.setIconImage(APP_ICON.getImage());
				mainFrame.setVisible(true);
				mainFrame.setLocationRelativeTo(null);
				mainFrame.setResizable(false);
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	/**
	 * Instantiation of the main application window
	 */
	public PDFMerger(){
		getImages();
		makeGUI();
	}

	/**
	 * Builds the GUI (who'd've thought?)
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		add(contentPanel);

		JPanel topPanel = new JPanel(new GridLayout(1,2));
		topPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		contentPanel.add(topPanel, BorderLayout.NORTH);

		CustomListener generalListener = new CustomListener();

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(topPanel.getBorder());
		contentPanel.add(centerPanel);
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(topPanel.getBorder());
		contentPanel.add(bottomPanel, BorderLayout.SOUTH);

		plusButton = new JButton(ICON_PLUS);
		plusButton.setText(PDFMerger.LABEL_ADD_NEW);
		plusButton.addActionListener(generalListener);
		topPanel.add(plusButton);

		mergeButton = new JButton(ICON_TICK);
		mergeButton.setText(PDFMerger.LABEL_MERGE);
		mergeButton.addActionListener(generalListener);
		topPanel.add(mergeButton);

		fileList = new JList<PDFEntry>();
		JScrollPane scrollPanel = new JScrollPane(fileList);
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		centerPanel.add(scrollPanel);
		fileList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		JToolBar buttonBar = new JToolBar(JToolBar.VERTICAL);
		buttonBar.setFloatable(false);
		centerPanel.add(buttonBar, BorderLayout.EAST);

		upButton = new JButton(PDFMerger.ICON_ARROW_UP);
		upButton.addActionListener(generalListener);
		downButton = new JButton(PDFMerger.ICON_ARROW_DOWN);
		downButton.addActionListener(generalListener);
		deleteButton = new JButton(PDFMerger.ICON_DELETE);
		deleteButton.addActionListener(generalListener);
		buttonBar.add(upButton);
		buttonBar.add(downButton);
		buttonBar.addSeparator();
		buttonBar.add(deleteButton);
		
		openBox = new JCheckBox("Open PDF file in default PDF viewer after merge");
		bottomPanel.add(openBox, BorderLayout.PAGE_START);
		openBox.setSelected(true);
		openBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
			}
		});
		
		pack();
	}

	/**
	 * registers all the images to the Registry
	 */
	private void getImages(){
		APP_ICON = ImageHelper.getImageIcon("/trb1914/pdfmerger/icon.png");
		ICON_PLUS = ImageHelper.getImageIcon("/trb1914/pdfmerger/plus_64.png");
		ICON_TICK = ImageHelper.getImageIcon("/trb1914/pdfmerger/tick_64.png");
		ICON_ARROW_UP = ImageHelper.getImageIcon("/trb1914/pdfmerger/up_16.png");
		ICON_ARROW_DOWN = ImageHelper.getImageIcon("/trb1914/pdfmerger/down_16.png");
		ICON_DELETE = ImageHelper.getImageIcon("/trb1914/pdfmerger/delete_16.png");
	}
	/**
	 * A tiny class that holds a file url. Used for display in the JList
	 * @author Mees Gelein
	 */
	private class PDFEntry{

		private File file;
		public PDFEntry(File f){
			file = f;
		}
		/**
		 * Used for display in the JList
		 */
		public String toString(){
			return file.getAbsolutePath();
		}
		/**
		 * Returns the associated file
		 * @return
		 */
		public File getFile(){
			return file;
		}
	}
	/**
	 * Private class to have all the button handling in one place. When the button has been
	 * pressed the actionPerformed e.source will determine who set it off and toggle to the
	 * correct behaviour accordingly 
	 * @author Mees Gelein
	 */
	private class CustomListener implements ActionListener{

		private FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF File","pdf");
		private File currentDir = null;

		public void actionPerformed(ActionEvent e){
			Object src = e.getSource();
			if(src.equals(plusButton)){
				doPlus();
			}else if(src.equals(mergeButton)){
				doMerge();
			}else if(src.equals(downButton)){
				doDown();
			}else if(src.equals(upButton)){
				doUp();
			}else if(src.equals(deleteButton)){
				doDelete();
			}else{
				System.out.println("[CustomListener]: Source was not recognized.. no action taken");
			}
		}
		/**
		 * Called when the plusButton is pressed
		 */
		private void doPlus(){
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(filter);
			if(currentDir != null){
				fileChooser.setCurrentDirectory(currentDir);
			}
			int reply = fileChooser.showOpenDialog(null);
			if(reply != JFileChooser.CANCEL_OPTION){
				PDFEntry newEntry = new PDFEntry(fileChooser.getSelectedFile());
				pdfFiles.add(newEntry);
				fileList.setListData(pdfFiles);
				currentDir = fileChooser.getCurrentDirectory();
			}
		}
		/**
		 * Called when the deleteButton is pressed
		 */
		private void doDelete(){
			int index = fileList.getSelectedIndex();
			if(index >= 0){
				pdfFiles.remove(index);
			}
			reloadList();
		}

		/**
		 * Called when the moveUp button is pressed
		 */
		private void doUp(){
			int index = fileList.getSelectedIndex();
			if(index >= 1){
				PDFEntry entryA = pdfFiles.get(index - 1);
				PDFEntry entryB = pdfFiles.get(index);
				pdfFiles.set(index - 1, entryB);
				pdfFiles.set(index, entryA);
				fileList.setSelectedIndex(index - 1);
			}
			reloadList();
		}

		/**
		 * Called when the moveUp button is pressed
		 */
		private void doDown(){
			int index = fileList.getSelectedIndex();
			if(index <= pdfFiles.size() - 2){
				PDFEntry entryA = pdfFiles.get(index + 1);
				PDFEntry entryB = pdfFiles.get(index);
				pdfFiles.set(index + 1, entryB);
				pdfFiles.set(index, entryA);
				fileList.setSelectedIndex(index + 1);
			}
			reloadList();
		}

		/**
		 * Called when the mergeButton is pressed
		 */
		private void doMerge(){
			PDFMergerUtility merger = new PDFMergerUtility();
			int i, max = pdfFiles.size();
			for(i = 0; i < max; i++){
				merger.addSource(pdfFiles.get(i).getFile());
			}
			JFileChooser fChooser = new JFileChooser();
			fChooser.setFileFilter(filter);
			File f;
			if(currentDir == null){
				f = new File("merged.pdf");
			}else{
				f = new File(currentDir.getAbsolutePath() + File.separator + "merged.pdf");
			}
			fChooser.setSelectedFile(f);
			int reply = fChooser.showSaveDialog(null);
			if(reply != JFileChooser.CANCEL_OPTION){
				try{
					int result = JOptionPane.YES_OPTION;
					if(f.exists()){
						result = JOptionPane.showConfirmDialog(null, "A file with that name already exists. If you continue the previous file will be\noverwritten. Are you sure you want to continue?");
					}
					if(result == JOptionPane.YES_OPTION){
						merger.setDestinationFileName(fChooser.getSelectedFile().getAbsolutePath());
						merger.mergeDocuments();
						currentDir = fChooser.getCurrentDirectory();
						pdfFiles = new Vector<PDFEntry>();
						reloadList();
						JOptionPane.showMessageDialog(null, "Succesfully merged the PDF files. Saved the result to:\n" + f.getAbsolutePath());
						
						if(openBox.isSelected()){
							if(Desktop.isDesktopSupported()){
								Desktop.getDesktop().open(f);
							}
						}
					}
				}catch(Exception e){
					System.out.println("[CustomListener]: Encountered an error during merge process");
				}
			}
		}

		/**
		 * Reloads the list after modifications have been made
		 */
		private void reloadList(){
			int index = fileList.getSelectedIndex();
			fileList.setListData(pdfFiles);
			fileList.revalidate();
			fileList.repaint();
			if(fileList.getModel().getSize() < index){
				fileList.setSelectedIndex(fileList.getModel().getSize());
			}else{
				fileList.setSelectedIndex(index);
			}
		}
	}
}
