package trb1914.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;

import trb1914.Main;
import trb1914.XMLParser;
import trb1914.data.Registry;
import trb1914.data.Rental;
import trb1914.data.RentalItem;
/**
 * Renders the JList renderPanels
 * @author Mees Gelein
 */
public class RentalRenderer extends JPanel implements ListCellRenderer<Rental>{

	///The max amt of items a rental can display in the list
	public static int MAX_ITEMS = 6;
	
	//all colors
	public static Color PAID_COLOR = new Color(0x228822);
	public static Color PAID_COLOR_BG = new Color(0xEEFFEE);
	public static Color LATE_COLOR = Color.RED;
	public static Color LATE_COLOR_BG = new Color(0xFFEEEE);
	public static Color DUE_COLOR = new Color(0xFF7700);
	public static Color DUE_COLOR_BG = new Color(0xFFFEEE);
	public static Color STD_COLOR = Color.BLACK;
	public static Color STD_COLOR_BG = Color.WHITE;
	public static Color DISABLED_COLOR = Color.LIGHT_GRAY;
	public static Color NO_MATCH_COLOR = Color.GRAY;
	public static Color BORDER_COLOR = Color.LIGHT_GRAY;
	public static Color BORDER_COLOR_SEL = Color.GRAY;
	
	//all panels
	private JPanel westPanel;
	private JPanel eastPanel;
	private JPanel objectsPanel;
	private JPanel codePanel;
	private JPanel startDatePanel;
	private JPanel endDatePanel;
	private JPanel openPanel;
	
	//all labels
	private JLabel codeLabel;
	private JLabel warningLabel;
	private JLabel startDateLabel;
	private JLabel endDateLabel;
	private JLabel openLabel;
	
	//the bg color
	private Color bgColor;
	
	/**
	 * new RentalRenderer
	 */
	public RentalRenderer(){
		setLayout(new GridLayout(1, 2));
		setOpaque(true);
		initGUI();
	}

	private void initGUI(){
		westPanel = new JPanel(new GridLayout(1, 3));
		add(westPanel);
		eastPanel = new JPanel(new BorderLayout());
		add(eastPanel);
		objectsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		eastPanel.add(objectsPanel);
		codePanel = new JPanel(new BorderLayout());
		westPanel.add(codePanel, BorderLayout.WEST);
		codePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
		startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
		westPanel.add(startDatePanel);
		startDatePanel.setBorder(codePanel.getBorder());
		endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
		westPanel.add(endDatePanel);
		endDatePanel.setBorder(startDatePanel.getBorder());
		openPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		eastPanel.add(openPanel, BorderLayout.EAST);
		
		codeLabel = new JLabel();
		codePanel.add(codeLabel);
		warningLabel = new JLabel();
		warningLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
		codePanel.add(warningLabel, BorderLayout.EAST);
		
		startDateLabel = new JLabel();
		endDateLabel = new JLabel();
		startDatePanel.add(startDateLabel);
		endDatePanel.add(endDateLabel);
		
		openLabel = new JLabel(Registry.OPEN_16);
		openLabel.setVerticalAlignment(SwingConstants.CENTER);
		openLabel.setHorizontalAlignment(SwingConstants.CENTER);
		openPanel.add(openLabel);
		openPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8), BorderFactory.createMatteBorder(0, 1, 0, 0, Color.gray)));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Rental> list, Rental rental, int index, boolean isSelected, boolean cellHasFocus) {

		//checks the query and makes the query impossible to match if there is no query
		String query = Main.mainWindow.searchField.getText();
		if(query.length() < 1){
			query = "&*%^$%##@";
		}
		
		bgColor = STD_COLOR_BG;
		
		//Set the code label text to the rental code
		codeLabel.setText(rental.getCode()); 
		codeLabel.setEnabled(list.isEnabled());
		
		//Set the correct warning label
		if(rental.hasWarning){
			if(rental.priority.equals(XMLParser.HIGH)){
				warningLabel.setIcon(Registry.WARNING_RED_16);
			}else{
				warningLabel.setIcon(Registry.WARNING_YELLOW_16);
			}
			warningLabel.setEnabled(list.isEnabled());
		}else{
			warningLabel.setIcon(null);
		}

		//Check the rental status and change the bullet point and colors accordingly
		if(rental.isAllPaid()){
			codeLabel.setIcon(Registry.BULLET_GREEN);
			if(Main.FANCY_COLORS){
				codeLabel.setForeground(PAID_COLOR);
				bgColor = PAID_COLOR_BG;
			}
		}else{
			if(rental.isLate()){
				codeLabel.setIcon(Registry.BULLET_RED);
				if(Main.FANCY_COLORS){
					codeLabel.setForeground(LATE_COLOR);
					bgColor = LATE_COLOR_BG;
				}
			}else if(rental.isDueToday()){
				codeLabel.setIcon(Registry.BULLET_ORANGE);
				if(Main.FANCY_COLORS){
					codeLabel.setForeground(DUE_COLOR);
					bgColor = DUE_COLOR_BG;
				}
			}else{
				codeLabel.setForeground(STD_COLOR);
				codeLabel.setIcon(Registry.BULLET_BLUE);
			}
		}
		
		//Check for the query in the codeLabel
		if(codeLabel.getText().contains(query)){
			codeLabel.setFont(Registry.BOLD_LIST_FONT);
		}else{
			codeLabel.setFont(Registry.LIST_FONT);
		}

		//Populates the startDateLabel and checks if it matches the query
		startDateLabel.setText(rental.getStartDate());
		if(startDateLabel.getText().contains(query)){
			startDateLabel.setFont(Registry.BOLD_LIST_FONT);
		}else{
			startDateLabel.setFont(Registry.LIST_FONT);
		}
		
		//Populates the endDateLabel and checks if it matches the query
		endDateLabel.setText(rental.getEndDate());
		if(endDateLabel.getText().contains(query)){
			endDateLabel.setFont(Registry.BOLD_LIST_FONT);
		}else{
			endDateLabel.setFont(Registry.LIST_FONT);
		}
		
		//Fills the objectPanel with the all the objects this rental has in it (paid, swapped and normal objects)
		populateObjectsPanel(rental);

		//Disable any color changes made so far if the list is not enabled.
		if(!list.isEnabled()){
			bgColor = DISABLED_COLOR;
		}
		
		//Check if the current item is selected and if it is apply the appropriate style
		if(isSelected){
			bgColor = list.getSelectionBackground();
			codePanel.setBackground(bgColor);
			startDatePanel.setBackground(bgColor);
			endDatePanel.setBackground(bgColor);
			objectsPanel.setBackground(bgColor);
			setBorder(BorderFactory.createMatteBorder(1, 2, 2, 2, BORDER_COLOR_SEL));
		}else{
			codePanel.setBackground(bgColor);
			startDatePanel.setBackground(bgColor);
			endDatePanel.setBackground(bgColor);
			objectsPanel.setBackground(bgColor);
			setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, BORDER_COLOR));
		}
		openLabel.setBackground(bgColor);
		eastPanel.setBackground(bgColor);
		openPanel.setBackground(bgColor);
		//return the styled panel
		return this;
	}
	
	/**
	 * Fills the objectPanel with the normal, swapped and paid objects 
	 * @param objectsPanel		the panel to populate
	 * @param rental			the rental to use for data
	 */
	private void populateObjectsPanel(Rental rental) {
		objectsPanel.removeAll();//empty the panel before we start.
		
		//Check the query again and set a flag that checks for a query
		String query = Main.mainWindow.searchField.getText();
		boolean noQuery = false;
		if(query.length() < 1){
			query = "&*%^$%##@";
			noQuery = true;
		}
		
		//First add any items that match the query.
		int totalItems = 0; boolean tooMany = false;
		int addedItems = 0; ArrayList<String> matchedItems = new ArrayList<String>();
		for(RentalItem o : rental.objects) if(o.name.contains(query)) matchedItems.add(o.name);
		for(RentalItem s : rental.swappedObjects) if(s.name.contains(query)) matchedItems.add(s.name);
		for(RentalItem p : rental.paidObjects) if(p.name.contains(query)) matchedItems.add(p.name);
		int i; int max = matchedItems.size();
		totalItems += matchedItems.size();
		if(totalItems > MAX_ITEMS){
			max = MAX_ITEMS;
			tooMany = true;
		}
		//Add as many matched items as possible
		for(i = 0; i < max; i++){
			JLabel label = new JLabel(matchedItems.get(i));
			label.setForeground(STD_COLOR);
			label.setFont(Registry.BOLD_LIST_FONT);
			if(rental.hasObject(matchedItems.get(i))) label.setIcon(Registry.HOURGLASS);
			else if(rental.hasSwappedObject(matchedItems.get(i))) label.setIcon(Registry.REFRESH_ICON_16);
			else if(rental.hasPaidObject(matchedItems.get(i))) label.setIcon(Registry.TICK_16);
			objectsPanel.add(label);
		}
		
		//Keeps track of the amount of added items to make sure we are not adding too many
		addedItems += max;//update the amt of added objects
		max = rental.objects.size();
		totalItems += max;
		if(totalItems > MAX_ITEMS){
			max = MAX_ITEMS - addedItems;
			tooMany = true;
		}
		//Add as many objects as possible
		for(i = 0; i < max; i++){
			JLabel label = new JLabel(rental.objects.get(i).name);
			label.setForeground(STD_COLOR);
			if(label.getText().contains(query)){
				continue;//we have already added this item previously since it matches the query
			}else if(noQuery){
				label.setFont(Registry.LIST_FONT);
			}else{
				label.setForeground(NO_MATCH_COLOR);
			}
			label.setIcon(Registry.HOURGLASS);
			objectsPanel.add(label);
		}
		
		//Check the amt of added objects again to prevent too many added objects
		addedItems += max;//update the amt of added objects
		max = rental.paidObjects.size();
		totalItems += max;
		if(totalItems > MAX_ITEMS){
			max = MAX_ITEMS - addedItems;
			tooMany = true;
		}
		//Adds as many paid Objects as possible
		for(i = 0; i < max; i++){
			JLabel label = new JLabel(rental.paidObjects.get(i).name);
			label.setForeground(STD_COLOR);
			if(label.getText().contains(query)){
				continue;//we have already added this item previously since it matches the query
			}else{
				label.setForeground(NO_MATCH_COLOR);
			}
			label.setIcon(Registry.TICK_16);
			objectsPanel.add(label);
		}
		
		//Check the amt of added objects once again. 
		addedItems += max;
		max = rental.swappedObjects.size();
		totalItems += max;
		if(totalItems > MAX_ITEMS){
			max = MAX_ITEMS - addedItems;//if we have too many items, we strip off any more
			tooMany = true;
		}
		
		//Adds as many swapped objects as possible
		for(i = 0; i < max; i++){
			JLabel label = new JLabel(rental.swappedObjects.get(i).name);
			label.setForeground(STD_COLOR);
			if(label.getText().contains(query)){
				continue;//we have already added this item previously since it matches the query
			}else{
				label.setForeground(NO_MATCH_COLOR);
			}
			label.setIcon(Registry.REFRESH_ICON_16);
			objectsPanel.add(label);
		}
		
		//If tooMany items have been added and some items have been stripped off we need to make this apparent by adding a ... to the end
		if(tooMany){
			objectsPanel.add(new JLabel("..."));
		}
	}
}
