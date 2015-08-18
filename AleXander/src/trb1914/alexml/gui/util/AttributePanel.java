package trb1914.alexml.gui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.w3c.dom.Element;

import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.Registry;
import trb1914.alexml.gui.AttributeEditor;
import trb1914.alexml.xml.XMLTags;
/**
 * a small class that displays a key value pair for XML 
 * attribute editing in a nice layout with a deleteButton
 * @author Mees Gelein
 *
 */
public class AttributePanel extends JPanel {

	/**
	 * the name (or key) of this key value pair
	 */
	private String _name;
	/**
	 * the value of this key value pair
	 */
	private String _value;
	
	/**
	 * A reference to the AttributeEditor window that is holding this object
	 */
	private AttributeEditor parent;
	
	//The textFields used to display and store data
	private JTextField nameField;
	private JTextField valueField;
	
	/**
	 * creates a new AttributePanel with the provided name, value and parent
	 * @param name				the name (or key) of the key value pair
	 * @param value				the value of the key value pair
	 * @param attributeEditor	the parent of this object
	 */
	public AttributePanel(String name, String value, AttributeEditor attributeEditor){
		_name = name;
		_value = value;
		parent = attributeEditor;
		
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2));
		makeGUI();
	}
	
	/**
	 * builds the GUI
	 */
	private void makeGUI(){
		nameField = new JTextField(_name, Registry.CHAR_AMOUNT_SMALL);
		valueField = new JTextField(_value);
		final AttributePanel selfRef = this;
		JButton deleteButton = new JButton(new ImageIcon(FileRegistry.DELETE_ICON_SMALL));
		deleteButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				AttributeEditor.allAttributePanels.remove(AttributeEditor.allAttributePanels.indexOf(selfRef));
				parent.updateAttributeEditorArea();
			}
		});
		deleteButton.setPreferredSize(new Dimension(25,27));//Manually setting size. Not realy happy with that.. but ah well :P
		
		JPanel namePanel = new JPanel(new BorderLayout());
		JPanel borderPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(deleteButton);
		borderPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
		borderPanel.add(nameField);
		namePanel.add(borderPanel);
		namePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.gray));
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets = new Insets(2,0,0,0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		add(buttonPanel, c);		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		add(namePanel, c);
		c.gridx = 2;
		c.insets = new Insets(2,2,0,0);
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1.0f;
		add(valueField, c);
	}
	
	/**
	 * saves the values from the textFields back into the element
	 * we were editing
	 * @param e		the element we were editing
	 */
	public void saveToElement(Element e){
		if(nameField.getText().length() > 0){
			if(nameField.getText().equals(XMLTags.PERSON)){
				nameField.setText(nameField.getText().substring(0, 1));
			}else{
				e.setAttribute(nameField.getText(), valueField.getText());
			}
		}
	}
}
