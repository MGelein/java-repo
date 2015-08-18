package trb1914;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;

import trb1914.data.Registry;
import trb1914.data.Rental;
import trb1914.filter.NumberOnlyFilter;
import trb1914.filter.UpperCaseFilter;
import trb1914.util.DatePicker;
import trb1914.util.DatePicker.IDatePickReady;

/**
 * the window that creates a new rental
 * @author Mees Gelein
 */
public class RentWindow extends JPanel implements IDatePickReady{

	/**The amount of characters that are displayed in the codeField and objectField*/
	private static final int CHAR_AMOUNT = 6;
	/**The amount of visible rows in the objectList*/
	private static final int VISIBLE_ROWS = 5;
	/**The currently opened instance*/
	public static RentWindow cOpen = null;

	/**The field that accepts the rental code*/
	private JTextField codeField = new JTextField(CHAR_AMOUNT);
	/**The field that accepts the start Date*/
	private DatePicker startDatePicker;
	/**The field that accepts the end Date*/
	private DatePicker endDatePicker;
	/**The input field for the objects*/
	private JTextField objectField = new JTextField(CHAR_AMOUNT);
	/**The list of objects that have been added to the rental*/
	private JList<String> objectList = new JList<String>();
	/**The scrollPanel that holds the comments field*/
	private JScrollPane commentsScrollPane;
	/**The field that holds the comments*/
	private JTextPane commentsField = new JTextPane();
	/**Button that controls if a warning is associated with this rental*/
	private JToggleButton warningButton;
	/**Sets the warning level to low priority (default)*/
	private JToggleButton lowPBox;
	/**Sets the warning level to high priority*/
	private JToggleButton highPBox;
	/**The panel that holds all priority settings*/
	private JPanel priorityPanel;
	/**The button that saves and closes this window*/
	private JButton doneButton;

	/**
	 * creates a new RentWindow only if there is none currently open
	 */
	public RentWindow(){
		Main.playOpenSound();
		if(RentViewer.cOpen != null) RentViewer.cOpen.closeWindow();
		if(cOpen != null){
			cOpen.closeWindow(false);
		}
		cOpen = this;

		Main.mainWindow.mainList.clearSelection();

		makeGUI();
		objectList.setListData(new String[]{" "});
		setVisible(true);
		setFocusPolicy();
	}

	/**
	 * Builds the GUI
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.gray));

		Font standardFont = codeField.getFont().deriveFont(Registry.RENT_WINDOW_FONT_SIZE);
		codeField.setFont(standardFont);
		codeField.setText("" + Main.mainWindow.getLastUsedCode());

		codeField.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
				int selectionStart = codeField.getText().length() - 2;
				if(selectionStart < 0) selectionStart = 0;
				codeField.setSelectionStart(selectionStart);//start 2 chars before the end
				codeField.setSelectionEnd(codeField.getText().length());
			}
		});
		objectField.setFont(standardFont);
		objectList.setFont(standardFont);

		((AbstractDocument) objectField.getDocument()).setDocumentFilter(new UpperCaseFilter());
		((AbstractDocument) codeField.getDocument()).setDocumentFilter(new NumberOnlyFilter());

		//the eastpanel, where all the start/end date and rental code lives
		JPanel eastPanel = new JPanel(new BorderLayout());
		JPanel eastGridPanel = new JPanel(new GridLayout(3, 2, 8, 8));
		add(eastPanel, BorderLayout.EAST);
		eastPanel.add(eastGridPanel, BorderLayout.NORTH);
		eastPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

		//codefield
		eastGridPanel.add(new JLabel(Registry.KLANT_CODE + ": "), BorderLayout.WEST);
		eastGridPanel.add(codeField);

		//startdate
		startDatePicker = new DatePicker(new Date());
		startDatePicker.setDateFieldFont(standardFont);
		eastGridPanel.add(new JLabel(Registry.START_DATE + ": "), BorderLayout.WEST);
		eastGridPanel.add(startDatePicker);
		startDatePicker.addReadyListener(this);

		//enddate
		endDatePicker = new DatePicker(new Date());
		endDatePicker.setDateFieldFont(standardFont);
		eastGridPanel.add(new JLabel(Registry.END_DATE + ": "), BorderLayout.WEST);
		eastGridPanel.add(endDatePicker);
		endDatePicker.addReadyListener(this);
		
		//+1week and -1 week
		JPanel weekPanel = new JPanel(new GridLayout(2, 2, 4, 4));
		eastPanel.add(weekPanel);
		JButton plusWeekButton = new JButton(Registry.PLUS_WEEK);
		plusWeekButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addToEndDate(7);
			}
		});
		weekPanel.add(plusWeekButton);
		JButton plusDayButton = new JButton(Registry.PLUS_DAY);
		plusDayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addToEndDate(1);
			}
		});
		weekPanel.add(plusDayButton);
		JButton minusWeekButton = new JButton(Registry.MINUS_WEEK);
		minusWeekButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addToEndDate(-7);
			}
		});
		weekPanel.add(minusWeekButton);
		JButton minusDayButton = new JButton(Registry.MINUS_DAY);
		weekPanel.add(minusDayButton);
		minusDayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addToEndDate(-1);
			}
		});
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		JPanel sidePanel = new JPanel(new BorderLayout());

		JButton deleteButton = new JButton(Registry.DELETE_16);
		deleteButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				deleteSelectedObject();
			}
		});
		sidePanel.add(deleteButton, BorderLayout.NORTH);
		contentPanel.add(sidePanel, BorderLayout.WEST);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

		JPanel objectFieldPanel = new JPanel(new BorderLayout());
		objectFieldPanel.add(objectField);
		JButton submitButton = new JButton(Registry.RIGHT_16);
		submitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				submitObject();
			}
		});
		objectFieldPanel.add(submitButton, BorderLayout.WEST);

		contentPanel.add(objectFieldPanel, BorderLayout.NORTH);
		objectField.setHorizontalAlignment(SwingConstants.CENTER);
		JScrollPane scrollPane = new JScrollPane(objectList);
		contentPanel.add(scrollPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		objectList.setBorder(BorderFactory.createLineBorder(Color.gray));
		String[] dummyString = new String[VISIBLE_ROWS];
		for(int i = 0 ; i < VISIBLE_ROWS; i++){
			dummyString[i] = " ";
		}
		objectList.setListData(dummyString);
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) objectList.getCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		add(contentPanel);
		objectField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitObject();
			}
		});

		objectList.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_DELETE){
					deleteSelectedObject();
				}
			}
		});

		JPanel southPanel = new JPanel(new BorderLayout());

		JPanel commentsPanel = new JPanel(new BorderLayout());
		commentsScrollPane = new JScrollPane(commentsField);
		commentsPanel.add(commentsScrollPane);

		priorityPanel = new JPanel();
		commentsPanel.add(priorityPanel, BorderLayout.SOUTH);
		priorityPanel.setVisible(false);
		lowPBox = new JToggleButton("Lage prioriteit (" + Registry.F5_KeyName + ")");
		highPBox = new JToggleButton("Hoge prioriteit (" + Registry.F6_KeyName + ")");
		lowPBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				changePriority(XMLParser.LOW);
			}
		});
		lowPBox.getActionMap().put("SetLow", new AbstractAction("SetLow"){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				changePriority(XMLParser.LOW);
			}
		});
		lowPBox.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F5_KeyStroke, "SetLow");
		highPBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				changePriority(XMLParser.HIGH);
			}
		});
		highPBox.getActionMap().put("SetHigh", new AbstractAction("SetHigh"){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				changePriority(XMLParser.HIGH);
			}
		});
		highPBox.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F6_KeyStroke, "SetHigh");
		lowPBox.setIcon(Registry.WARNING_YELLOW_16);
		lowPBox.setSelected(true);
		highPBox.setIcon(Registry.WARNING_RED_16);
		priorityPanel.add(lowPBox); 
		priorityPanel.add(highPBox);

		commentsField.setEnabled(false);
		commentsField.setFont(Registry.LIST_FONT);
		commentsScrollPane.setPreferredSize(new Dimension(-1, 28));
		commentsPanel.setBorder(BorderFactory.createTitledBorder("Opmerkingen"));
		southPanel.add(commentsPanel);
		add(southPanel, BorderLayout.SOUTH);

		JPanel buttonPanel = new JPanel();
		southPanel.add(buttonPanel, BorderLayout.SOUTH);

		doneButton = new JButton(Registry.TICK_48);
		doneButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeWindow();
			}
		});
		doneButton.getActionMap().put("Done", new AbstractAction("Done"){
			public void actionPerformed(ActionEvent e){
				closeWindow();
			}
		});
		doneButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F1_KeyStroke, "Done");
		doneButton.setText("Invoeren (" + Registry.F1_KeyName + ")");

		JButton closeButton = new JButton(Registry.DELETE_48);
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeWindow(false);
			}
		});
		closeButton.getActionMap().put("Close", new AbstractAction("Close"){
			public void actionPerformed(ActionEvent e){
				closeWindow(false);
			}
		});
		closeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F3_KeyStroke, "Close");
		closeButton.setText("Annuleren (" + Registry.F3_KeyName + ")");

		warningButton = new JToggleButton(Registry.WARNING_YELLOW_48);
		buttonPanel.add(doneButton);
		buttonPanel.add(warningButton);
		buttonPanel.add(closeButton);

		warningButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				toggleCommentButton(true);
			}
		});
		warningButton.getActionMap().put("Comment", new AbstractAction("Comment"){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				toggleCommentButton(false);
			}
		});
		warningButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F2_KeyStroke, "Comment");
		warningButton.setText("Opmerking (" + Registry.F2_KeyName + ")");
		buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));
	}

	/**
	 * Changes the priority of the comment. Use XMLParser.HIGH or XMLParser.LOW
	 * @param priority
	 */
	private void changePriority(String priority){
		switch(priority){
		case XMLParser.HIGH:
			lowPBox.setSelected(false);
			highPBox.setSelected(true);
			warningButton.setIcon(Registry.WARNING_RED_48);
			break;
		case XMLParser.LOW:
			lowPBox.setSelected(true);
			highPBox.setSelected(false);
			warningButton.setIcon(Registry.WARNING_YELLOW_48);
			break;
		}
	}
	
	/**
	 * Adds the specified amount of days (negative or positive) to the endDate
	 * @param days
	 */
	private void addToEndDate(int days){
		Calendar c = Calendar.getInstance();
		c.setTime(endDatePicker.getDate());
		c.add(Calendar.DAY_OF_MONTH, days);
		endDatePicker.setDatefieldText(DatePicker.DATE_FORMAT.format(c.getTime()));
	}

	/**
	 * Toggles the commentsfield
	 * @param button
	 * @param scrollPane
	 * @param field
	 */
	private void toggleCommentButton(boolean fromButton){
		if(!fromButton) warningButton.setSelected(!warningButton.isSelected());
		if(!warningButton.isSelected()){
			commentsField.setEnabled(false);
			commentsScrollPane.setPreferredSize(new Dimension(-1, 28));
			priorityPanel.setVisible(false);
		}else{
			commentsField.setEnabled(true);
			commentsScrollPane.setPreferredSize(new Dimension(-1, 56));
			priorityPanel.setVisible(true);
		}
		revalidate();
		repaint();
	}

	/**
	 * deletes the selected object
	 */
	private void deleteSelectedObject(){
		int selectedIndex = objectList.getSelectedIndex();
		if(selectedIndex > -1){
			String[] newObjects;
			ListModel<String> listModel = objectList.getModel();
			if(listModel.getSize() > 1){
				newObjects = new String[listModel.getSize() - 1];

				for(int i = 0; i < listModel.getSize(); i++){
					if(i < selectedIndex){
						newObjects[i] = listModel.getElementAt(i);
					}else if(i > selectedIndex){
						newObjects[i-1] = listModel.getElementAt(i);
					}
				}
			}else{
				newObjects = new String[]{" "};
			}
			objectList.setListData(newObjects);
			repaint();
			revalidate();
			if(objectList.getModel().getSize() <= selectedIndex){
				objectList.setSelectedIndex(objectList.getModel().getSize() - 1);
			}else{
				objectList.setSelectedIndex(selectedIndex);
			}
		}
	}

	/**
	 * submits the value from the objectField to the objectList
	 */
	private void submitObject(){
		Main.playPingSound();
		if(objectField.getText().length() < 1) closeWindow();//we close the window if the user presses enter with an empty object field
		
		ListModel<String> model = objectList.getModel();
		String[] objects = new String[model.getSize() + 1];

		//do a quick check to turn B4 into B04
		String toSubmit = objectField.getText();
		toSubmit = toSubmit.replaceAll(" ", "");
		if(toSubmit.length() < 1) return;//silently don't accept input because of 0 length

		String[] parts = toSubmit.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		if(parts.length > 1){//if there is a number in this code. Also do already rented check
			int number = Integer.parseInt(parts[1]);
			if(number < 10){
				parts[1] = parts[1].replaceAll("0", "");
				parts[1] = "0" + parts[1];
			}
			toSubmit = parts[0] + parts[1];

			if(Main.mainWindow.isAlreadyRented(toSubmit)){//already rented. STOP IT 
				Main.playOpenSound();
				int reply = JOptionPane.showConfirmDialog(null, "Deze fiets-code staat op dit moment nog als verhuurd in het systeem. Weet u zeker dat u wilt doorgaan?");
				if(reply != JOptionPane.OK_OPTION) {
					return;
				}
			}
		}//if not a number and letter also no need to check if already rented 
		for(int i = 0 ; i < model.getSize(); i++){
			objects[i + 1] = model.getElementAt(i);
		}
		if(model.getSize() == 1 && objects[1] == " "){
			objects = new String[1];
			objects[0] = toSubmit;
			objectField.setText("");
		}else{
			objects[0] = toSubmit;
			objectField.setText("");
		}

		objectList.setListData(objects);
		repaint();
		revalidate();
	}

	/**
	 * closes the current window and assumes you want to save the rental
	 */
	public void closeWindow(){
		closeWindow(true);
	}

	/**
	 * closes the current window
	 */
	public void closeWindow(boolean saveChanges){
		if(objectField.getText().length() == 0){
			if(saveChanges){
				if(codeField.getText().length() > 0){
					if(Main.mainWindow.codeIsAvailable(codeField.getText())){
						Rental r = new Rental();
						r.setCode(codeField.getText());
						r.hasWarning = warningButton.isSelected();
						r.comments = commentsField.getText();
						Main.lastUsedNumber = Integer.parseInt(codeField.getText());
						r.setStartDate(startDatePicker.getDateString());
						r.setEndDate(endDatePicker.getDateString());
						if(lowPBox.isSelected()) r.priority = XMLParser.LOW;
						else r.priority = XMLParser.HIGH;
						ListModel<String> listModel = objectList.getModel();
						for(int i = 0 ; i < listModel.getSize(); i++){
							r.addObject(listModel.getElementAt(i));
						}
						if(r.objects.size() > 0){
							cOpen = null;
							Main.mainWindow.resetWindowPanel();
							Main.mainWindow.broadCast(r.getAsString());
						}else{
							Main.playOpenSound();
							JOptionPane.showMessageDialog(null, "Er zijn geen fietsen aan de order toegevoegd. Deze moet nog worden ingevuld om door te gaan");
							Main.playCloseSound();
						}
					}else{
						Main.playOpenSound();
						JOptionPane.showMessageDialog(null, "De klantcode die is ingevuld is al gebruikt. Gebruik een nieuwe klantcode of\nverwijder de oude klantcode uit het systeem");
						Main.playCloseSound();
					}
				}else{
					Main.playOpenSound();
					JOptionPane.showMessageDialog(null, "Er is nog geen klantcode ingevuld. Deze moet nog worden ingevuld om door te gaan");
					Main.playCloseSound();
				}
			}else{
				cOpen = null;
				Main.mainWindow.resetWindowPanel();
			}
		}else{
			submitObject();
		}
	}

	/**
	 * Creates the correct tab-order
	 */
	private void setFocusPolicy(){
		this.setFocusTraversalPolicy(new FocusTraversalPolicy(){
			public Component getComponentAfter(Container arg0, Component aComponent) {
				if(aComponent.equals(codeField)) return startDatePicker;
				if(aComponent.equals(startDatePicker)) return endDatePicker;
				if(aComponent.equals(endDatePicker)) return objectField;
				if(aComponent.equals(objectField)) return doneButton;
				else return codeField;
			}

			public Component getComponentBefore(Container aContainer,
					Component aComponent) {
				if(aComponent.equals(codeField)) return doneButton;
				if(aComponent.equals(doneButton)) return objectField;
				if(aComponent.equals(objectField)) return endDatePicker;
				if(aComponent.equals(endDatePicker)) return startDatePicker;
				else return codeField;				
			}
			public Component getDefaultComponent(Container aContainer) {
				return codeField;
			}
			public Component getFirstComponent(Container aContainer) {
				return codeField;
			}
			public Component getLastComponent(Container aContainer) {
				return doneButton;
			}

		});
	}

	/**
	 * Called when the datePick is ready
	 */
	public void datePickReady(DatePicker parent) {
		if(parent.equals(startDatePicker)){
			endDatePicker.askFocus();
		}else if(parent.equals(endDatePicker)){
			objectField.requestFocus();
		}

	}
}
