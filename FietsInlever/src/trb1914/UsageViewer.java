package trb1914;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import trb1914.data.Registry;
import trb1914.data.Rental;
import trb1914.filter.UpperCaseFilter;
import trb1914.helper.SystemHelper;
import trb1914.util.UsageRenderer;
/**
 * simple window to view the usage of all bikes
 * @author Mees Gelein
 */
public class UsageViewer extends JFrame{

	//The currently open instance of the usageViewer
	public static UsageViewer cOpen = null;
	//All items that have been used
	private Vector<String> allUsage = new Vector<String>();
	//All the items that match the query
	private Vector<String> foundUsage = new Vector<String>();
	//The field that contains the query
	private JTextField searchField = new JTextField();
	//The list that shows the data
	private JList<String> usageList;
	
	/**
	 * new instance of UsageViewer
	 */
	public UsageViewer(){
		if(cOpen == null){
			cOpen = this;
			setIconImage(Registry.APP_ICON.getImage());
			setTitle(Registry.APP_TITLE);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					closeWindow();
				}
			});
			setSize(800,600);
			setMinimumSize(getSize());
			setVisible(true);
			setLocationRelativeTo(null);

			makeGUI();
		}else{//if we have an instance that is already open we need it to request focus
			cOpen.requestFocus();
			SystemHelper.TOOLKIT.beep();
		}
	}
	
	/**
	 * builds the GUI
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		JPanel northPanel = new JPanel(new BorderLayout());
		contentPanel.add(northPanel, BorderLayout.NORTH);
		northPanel.add(searchField);
		northPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
		JLabel label = new JLabel(Registry.SEARCH_16);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
		northPanel.add(label, BorderLayout.WEST);
		searchField.setHorizontalAlignment(SwingConstants.CENTER);
		searchField.setFont(Font.decode(searchField.getFont().getName()+"-" + Registry.SEARCH_FONT_SIZE));
		((AbstractDocument) searchField.getDocument()).setDocumentFilter(new UpperCaseFilter());
		searchField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent arg0) {updateList();}
			public void insertUpdate(DocumentEvent arg0) {updateList();}
			public void removeUpdate(DocumentEvent arg0) {updateList();}

		});
		
		usageList = new JList<String>();
		usageList.setCellRenderer(new UsageRenderer());
		JScrollPane scrollPane = new JScrollPane(usageList);
		allUsage = generateUsageData();
		usageList.setListData(generateUsageData());
		contentPanel.add(scrollPane);
		add(contentPanel);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	}	
	
	/**
	 * generates the listData
	 * @return
	 */
	private Vector<String> generateUsageData(){
		Vector<String> listData = new Vector<String>();
		ArrayList<Rental> list = Main.mainWindow.allRentals;
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int i; int maxRentals = list.size(); int j; int maxObjects; 
		Rental cRental; String object; Integer count;
		for(i = 0; i < maxRentals; i++){
			cRental = list.get(i);
			maxObjects = cRental.objects.size();
			for(j = 0; j < maxObjects; j++){
				object = cRental.objects.get(j).name;
				count = map.get(object);
				if(count == null){
					map.put(object, 1);
				}else{
					count += 1;
					map.put(object, count);
				}
			}
			maxObjects = cRental.swappedObjects.size();
			for(j = 0; j < maxObjects; j++){
				object = cRental.swappedObjects.get(j).name;
				count = map.get(object);
				if(count == null){
					map.put(object, 1);
				}else{
					count += 1;
					map.put(object, count);
				}
			}
			maxObjects = cRental.paidObjects.size();
			for(j = 0; j < maxObjects; j++){
				object = cRental.paidObjects.get(j).name;
				count = map.get(object);
				if(count == null){
					map.put(object, 1);
				}else{
					count += 1;
					map.put(object, count);
				}
			}
		}
		
		Iterator<String> keySetIterator = map.keySet().iterator();
		while(keySetIterator.hasNext()){
			String key = keySetIterator.next();
			Integer value = map.get(key);
			listData.add(key+","+value.toString());
		}
		Collections.sort(listData);
		return listData;
	}
	
	/**
	 * updates the list
	 */
	private void updateList(){
		foundUsage = new Vector<String>();
		int i = 0; int max = allUsage.size();
		String s;
		String query = searchField.getText();
		for(i = 0; i < max; i++){
			s = allUsage.get(i);
			s = s.substring(0, s.indexOf(","));
			if(s.contains(query)){
				foundUsage.add(allUsage.get(i));
			}
		}
		usageList.setListData(foundUsage);
		revalidate();
		repaint();		
	}
	
	/**
	 * close this window
	 */
	public void closeWindow(){
		cOpen = null;
		dispose();
	}

}
