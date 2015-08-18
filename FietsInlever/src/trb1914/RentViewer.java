package trb1914;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.text.AbstractDocument;

import trb1914.data.Registry;
import trb1914.data.Rental;
import trb1914.data.RentalItem;
import trb1914.filter.UpperCaseFilter;
import trb1914.util.DatePicker;
/**
 * allows the user to view and edit existing rentals
 * @author Mees Gelein
 */
public class RentViewer extends JPanel{

	/**The height of the rent object viewer list*/
	private static final int LIST_HEIGHT = 200;

	/**The currently opened RentViewer*/
	public static RentViewer cOpen = null;

	/**Made public to allow acces if udpates from the server affect this Rental*/
	public Rental rental;

	/**The field that holds the customer code*/
	private JTextField codeField = new JTextField(8);
	/**The field that holds the object input*/
	private JTextField objectField = new JTextField(8);

	private DatePicker startDatePicker;
	private JToggleButton warningButton;
	private DatePicker endDatePicker;
	private JTextPane commentsField = new JTextPane();
	private JPanel contentPanel;
	private JPanel emptyPanel;
	private JPanel priorityPanel;
	private JScrollPane commentsScrollPane;
	private JToggleButton lowPBox;
	private JToggleButton highPBox;

	/**
	 * creates a new rental screen
	 * @param rental
	 */
	public RentViewer(Rental r){
		Main.playOpenSound();
		if(RentWindow.cOpen != null) RentWindow.cOpen.closeWindow(false);
		if(cOpen != null){
			cOpen.closeWindow();
		}
		cOpen = this;
		Main.mainWindow.mainList.clearSelection();
		rental = r.copy();
		makeGUI();

		setVisible(true);
		Main.mainWindow.broadCast("@del=" + r.getCode());
	}

	/**
	 * builds the GUI
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.gray));

		Font standardFont = codeField.getFont().deriveFont(Registry.RENT_WINDOW_FONT_SIZE);
		codeField.setFont(standardFont);
		codeField.setText(rental.getCode());
		codeField.setEnabled(false);
		objectField.setHorizontalAlignment(SwingConstants.CENTER);
		objectField.setFont(standardFont);

		//eastpanel contains all dates and code and the pay all button
		JPanel eastPanel = new JPanel(new BorderLayout());
		JPanel eastGridPanel = new JPanel(new GridLayout(3, 2, 0, 8));
		eastGridPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
		eastPanel.add(eastGridPanel, BorderLayout.NORTH);
		add(eastPanel, BorderLayout.EAST);
		eastPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

		//the rental code
		eastGridPanel.add(new JLabel(Registry.KLANT_CODE + ": "), BorderLayout.WEST);
		eastGridPanel.add(codeField);

		//startDate
		JLabel startDateLabel = new JLabel(Registry.START_DATE + ": ");
		startDatePicker = new DatePicker(rental.getStartDateValue());
		startDatePicker.allowInput(false);
		startDatePicker.setDateFieldFont(standardFont);
		eastGridPanel.add(startDateLabel, BorderLayout.WEST);
		eastGridPanel.add(startDatePicker);

		//enddate
		endDatePicker = new DatePicker(rental.getEndDateValue());
		endDatePicker.setDateFieldFont(standardFont);
		eastGridPanel.add(new JLabel(Registry.END_DATE + ": "), BorderLayout.WEST);
		eastGridPanel.add(endDatePicker);
		
		//The payAll button
		JPanel payAllPanel = new JPanel(new BorderLayout());
		payAllPanel.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
		JButton payAllButton = new JButton(Registry.TICK_48);
		payAllButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				payAll();
			}
		});
		payAllButton.setText("Alles inleveren (" + Registry.F4_KeyName + ")");
		payAllPanel.add(payAllButton);
		eastPanel.add(payAllPanel);
		payAllButton.getActionMap().put("PayAll", new AbstractAction("PayAll"){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				payAll();
			}
		});
		payAllButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F4_KeyStroke, "PayAll");

		//only accepts uppercase input
		((AbstractDocument) objectField.getDocument()).setDocumentFilter(new UpperCaseFilter());

		contentPanel = new JPanel(new BorderLayout());
		JPanel objectFieldPanel = new JPanel(new BorderLayout());
		objectFieldPanel.add(objectField);
		contentPanel.add(objectFieldPanel, BorderLayout.NORTH);
		JButton submitButton = new JButton(Registry.RIGHT_16);
		submitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				submitObject();
			}
		});
		objectField.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					submitObject();
				}
			}
		});
		objectFieldPanel.add(submitButton, BorderLayout.WEST);
		contentPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
		add(contentPanel);
		emptyPanel = new JPanel(new BorderLayout());
		contentPanel.add(emptyPanel);

		JPanel southPanel = new JPanel(new BorderLayout());

		JPanel commentsPanel = new JPanel(new BorderLayout());
		commentsScrollPane = new JScrollPane(commentsField);
		commentsPanel.add(commentsScrollPane);

		//priority check boxes for comment
		priorityPanel = new JPanel(new FlowLayout());
		commentsPanel.add(priorityPanel, BorderLayout.SOUTH);
		lowPBox = new JToggleButton("Lage prioriteit (" + Registry.F5_KeyName +")");
		lowPBox.setSelected(true);
		lowPBox.setIcon(Registry.WARNING_YELLOW_16);
		highPBox = new JToggleButton("Hoge prioriteit (" + Registry.F6_KeyName + ")");
		highPBox.setIcon(Registry.WARNING_RED_16);
		priorityPanel.add(lowPBox); 
		priorityPanel.add(highPBox);
		priorityPanel.setVisible(rental.hasWarning);

		commentsField.setText(rental.comments);
		commentsField.setFont(Registry.LIST_FONT);
		commentsField.setEnabled(rental.hasWarning);
		if(!commentsField.isEnabled()){
			commentsScrollPane.setPreferredSize(new Dimension(-1, 28));
		}else{
			commentsScrollPane.setPreferredSize(new Dimension(-1, 100));
		}
		commentsPanel.setBorder(BorderFactory.createTitledBorder("Opmerkingen"));
		southPanel.add(commentsPanel);
		add(southPanel, BorderLayout.SOUTH);

		JPanel buttonPanel = new JPanel();
		southPanel.add(buttonPanel, BorderLayout.SOUTH);
		JButton doneButton = new JButton(Registry.TICK_48);

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
		doneButton.setText("Akkoord (" + Registry.F1_KeyName + ")");

		JButton closeButton = new JButton(Registry.DELETE_48);
		closeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				closeWindow();
			}
		});
		closeButton.getActionMap().put("Close", new AbstractAction("Close"){
			public void actionPerformed(ActionEvent e){
				closeWindow();
			}
		});
		closeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.F3_KeyStroke, "Close");
		closeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(Registry.ESC_KeyStroke, "Close");
		closeButton.setText("Sluiten (" + Registry.F3_KeyName + ")");

		warningButton = new JToggleButton();
		if(rental.priority.equals(XMLParser.HIGH)) {
			warningButton.setIcon(Registry.WARNING_RED_48);
			highPBox.setSelected(true);
			lowPBox.setSelected(false);
		}
		else warningButton.setIcon(Registry.WARNING_YELLOW_48);
		warningButton.setSelected(rental.hasWarning);
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

		buttonPanel.add(doneButton);
		buttonPanel.add(warningButton);
		buttonPanel.add(closeButton);
		buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));

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


		objectField.requestFocus();
		setFocusPolicy();
		renderObjects();
	}

	/**
	 * Changes the priority of this rentals comment. Use XMLParser.HIGH or XMLParser.LOW
	 * @param priority
	 */
	private void changePriority(String priority){
		switch(priority){
		case XMLParser.HIGH:
			warningButton.setIcon(Registry.WARNING_RED_48);
			lowPBox.setSelected(false);
			highPBox.setSelected(true);
			rental.priority = XMLParser.HIGH;
			break;
		case XMLParser.LOW:
			warningButton.setIcon(Registry.WARNING_YELLOW_48);
			highPBox.setSelected(false);
			lowPBox.setSelected(true);
			rental.priority = XMLParser.LOW;
			break;
		}
	}

	/**
	 * Toggles the commentButton and hides/shows the commentPane
	 * @param fromButton
	 */
	private void toggleCommentButton(boolean fromButton){
		if(!fromButton) warningButton.setSelected(!warningButton.isSelected());

		if(!warningButton.isSelected()){
			commentsScrollPane.setPreferredSize(new Dimension(-1, 28));
			commentsField.setEnabled(false);
			priorityPanel.setVisible(false);
		}else{
			commentsScrollPane.setPreferredSize(new Dimension(-1, 100));
			commentsField.setEnabled(true);
			priorityPanel.setVisible(true);
		}
		revalidate();
		repaint();
	}

	/**
	 * Renders all objects on the objects pane
	 */
	private void renderObjects(){
		emptyPanel.removeAll();
		JPanel objectsPanel = new JPanel();
		objectsPanel.setLayout(new BorderLayout());
		objectsPanel.setBackground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(objectsPanel);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getSize().width, RentViewer.LIST_HEIGHT));

		emptyPanel.add(scrollPane);
		
		int totalIndex = 0;
		for(int i = 0 ; i < rental.objects.size(); i++){
			totalIndex++;
			objectsPanel.add(makeObjectPanel(rental.objects.get(i).name, 0,(totalIndex % 2 == 0)), BorderLayout.NORTH);
			JPanel holderPanel = new JPanel(new BorderLayout());
			objectsPanel.add(holderPanel, BorderLayout.CENTER);
			objectsPanel = holderPanel;
		}
		for(int i = 0 ; i < rental.paidObjects.size(); i++){
			totalIndex++;
			objectsPanel.add(makeObjectPanel(rental.paidObjects.get(i).name, 2,(totalIndex % 2 == 0)), BorderLayout.NORTH);
			JPanel holderPanel = new JPanel(new BorderLayout());
			objectsPanel.add(holderPanel, BorderLayout.CENTER);
			objectsPanel = holderPanel;
		}
		for(int i = 0 ; i < rental.swappedObjects.size(); i++){
			totalIndex++;
			objectsPanel.add(makeObjectPanel(rental.swappedObjects.get(i).name, 1,(totalIndex % 2 == 0)), BorderLayout.NORTH);
			JPanel holderPanel = new JPanel(new BorderLayout());
			objectsPanel.add(holderPanel, BorderLayout.CENTER);
			objectsPanel = holderPanel;
		}
		repaint();
		revalidate();
	}

	/**
	 * Pays all the rented objects (only changes rented objects, not swapped!)
	 */
	private void payAll(){
		//make a copy of the currently rented objects
		for(RentalItem r : rental.objects) rental.paidObjects.add(r);

		//clear all objects that have been rented
		rental.objects.clear();

		//render the changes
		renderObjects();
	}

	/**
	 * Creates one row in the objectPanel. This row displays the buttons to change the rental status or remove the rented object
	 * @param s							the rental Object this row controls
	 * @param checkedIndex				
	 * @param differentBackground		use this to make a alternating background color
	 * @return
	 */
	private JPanel makeObjectPanel(final String s, int checkedIndex, boolean differentBackground){
		JPanel panel = new JPanel(new GridLayout(1,5));
		Color bgColor = differentBackground ? new Color(0xDDDDFF) : Color.WHITE;

		//the objectName panel
		Font f = codeField.getFont().deriveFont(Registry.RENT_WINDOW_FONT_SIZE * 1.0f);
		JPanel objectNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JLabel objectNameLabel = new JLabel(s);
		objectNameLabel.setFont(f);
		objectNamePanel.add(objectNameLabel);
		panel.add(objectNamePanel);
		panel.setBorder(BorderFactory.createMatteBorder(0,1,1,1,bgColor.darker()));
		objectNamePanel.setBackground(bgColor);

		//the rented button
		JPanel rentedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		rentedPanel.setBackground(bgColor);
		final JToggleButton rentedButton = new JToggleButton("Verhuurd");
		rentedButton.setIcon(Registry.HOURGLASS);
		rentedButton.setBackground(bgColor);
		if(checkedIndex == 0) {
			rentedButton.setSelected(true);
			objectNameLabel.setIcon(Registry.HOURGLASS);
		}
		rentedPanel.add(rentedButton);
		panel.add(rentedPanel);
		rentedPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

		//the swapped button
		JPanel swappedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		swappedPanel.setBackground(bgColor);
		final JToggleButton swappedButton = new JToggleButton("Omgeruild");
		swappedButton.setIcon(Registry.REFRESH_ICON_16);
		swappedButton.setBackground(bgColor);
		if(checkedIndex == 1) {
			swappedButton.setSelected(true);
			objectNameLabel.setIcon(Registry.REFRESH_ICON_16);
		}
		swappedPanel.add(swappedButton);
		panel.add(swappedPanel);

		//the paid button
		JPanel paidPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		paidPanel.setBackground(bgColor);
		final JToggleButton paidButton = new JToggleButton("Ingeleverd");
		paidButton.setIcon(Registry.TICK_16);
		paidButton.setBackground(bgColor);
		if(checkedIndex == 2) {
			paidButton.setSelected(true);
			objectNameLabel.setIcon(Registry.TICK_16);
		}
		paidPanel.add(paidButton);
		panel.add(paidPanel);
		paidPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.lightGray));

		//the remove button
		JButton removeButton = new JButton("Verwijderen");
		removeButton.setIcon(Registry.DELETE_16);
		removeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playOpenSound();
				if(JOptionPane.showConfirmDialog(null, 
						"Weet u zeker dat u dit object wilt verwijderen zonder in te leveren? \nDit kan niet meer ongedaan gemaakt worden!",
						"Bevestig uw handeling", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					rental.unpayObject(s);
					rental.unswapObject(s);
					rental.removeObject(s);
					renderObjects();
				}
				Main.playCloseSound();
			}
		});

		JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		removePanel.setBackground(bgColor);
		removeButton.setBackground(bgColor);
		removePanel.add(removeButton);
		panel.add(removePanel);

		//The actionlisteners for the buttons
		rentedButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				swappedButton.setSelected(false);
				paidButton.setSelected(false);
				rentedButton.setSelected(true);
				rental.unpayObject(s);
				rental.unswapObject(s);
				objectNameLabel.setIcon(Registry.HOURGLASS);
				repaint();
			}
		});
		swappedButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				paidButton.setSelected(false);
				rentedButton.setSelected(false);
				swappedButton.setSelected(true);
				rental.unpayObject(s);
				rental.swapObject(s);
				objectNameLabel.setIcon(Registry.REFRESH_ICON_16);
				repaint();
			}
		});
		paidButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Main.playPingSound();
				swappedButton.setSelected(false);
				rentedButton.setSelected(false);
				paidButton.setSelected(true);
				rental.unswapObject(s);
				rental.payObject(s);
				objectNameLabel.setIcon(Registry.TICK_16);
				repaint();
			}
		});

		return panel;
	}

	/**
	 * submits the object in the objectField
	 */
	private void submitObject(){
		Main.playPingSound();
		rental.addObject(objectField.getText());
		renderObjects();
		objectField.setText("");
	}

	/**
	 * closes the current window
	 */
	public void closeWindow(){
		cOpen = null;
		Main.mainWindow.resetWindowPanel();
		rental.setEndDate(endDatePicker.getDateString());
		rental.hasWarning = warningButton.isSelected();
		rental.comments = commentsField.getText();

		if(rental.objects.size() == 0 && rental.paidObjects.size() == 0 && rental.swappedObjects.size() == 0){
			Main.mainWindow.broadCast("@del=" + rental.getCode());
		}else{
			Main.mainWindow.broadCast(rental.getAsString());
		}
	}

	/**
	 * Creates the correct tab-order
	 */
	private void setFocusPolicy(){
		this.setFocusTraversalPolicy(new FocusTraversalPolicy(){
			public Component getComponentAfter(Container arg0, Component aComponent) {
				if(aComponent.equals(objectField)) return endDatePicker;
				else return objectField;
			}

			public Component getComponentBefore(Container aContainer, Component aComponent) {
				if(aComponent.equals(objectField)) return endDatePicker;
				else return objectField;
			}

			public Component getDefaultComponent(Container aContainer) {
				return objectField;
			}
			public Component getFirstComponent(Container aContainer) {
				return objectField;
			}
			public Component getLastComponent(Container aContainer) {
				return endDatePicker;
			}
		});
	}
}
