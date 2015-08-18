package trb1914.util;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
/**
 * A simple DatePicker that shows the date in dd-MM-yyyy format;
 * @author Mees Gelein
 */
public class DatePicker extends JPanel{

	/**The format used to format the date in the JTextField*/
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	
	/**The textField that actually displays the data*/
	private JTextField dateField = new JTextField(DATE_FORMAT.toPattern().length());
	/**
	 * When this flag is set to true you can't change the year input
	 */
	private boolean skipYearInput = true;
	
	/**
	 * All the classes that listen for this DatePickers complete Event.
	 */
	private Vector<IDatePickReady> listenerList = new Vector<IDatePickReady>();
	
	/**
	 * Creates a new DatePicker with the selected date set to
	 * the provided parameter-date
	 * @param date		the date to set the selected date to
	 */
	public DatePicker(Date date){
		setDate(date);
		makeUI();
	}
	
	/**
	 * Makes the textfield request focus instead of the panel
	 */
	public void askFocus(){
		dateField.requestFocus();
	}
	
	/**
	 * Creates the UI
	 */
	private void makeUI(){
		setLayout(new BorderLayout());
		add(dateField, BorderLayout.CENTER);
		((AbstractDocument)dateField.getDocument()).setDocumentFilter(new DateFieldFilter(DATE_FORMAT.toPattern().length(), dateField));
		dateField.setSelectionStart(0); dateField.setSelectionEnd(1);
		
		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				dateField.setSelectionStart(0);
				dateField.setSelectionEnd(1);
			}
			
			public void focusLost(FocusEvent e) {
				
			}
			
		});
	}
	
	/**
	 * Adds the provided object to the list of objects to notify when the date has been
	 * entered completely (skipYearInput)
	 * @param object
	 */
	public void addReadyListener(IDatePickReady object){
		listenerList.add(object);
	}
	/**
	 * Tries to find and remove the provided object from the listenerList.
	 * @param object
	 */
	public void removeReadyListener(IDatePickReady object){
		listenerList.remove(object);
	}
	
	/**
	 * Sets the date of this DatePicker instance
	 * @param date
	 */	
	public void setDate(Date date){
		dateField.setText(DATE_FORMAT.format(date));
	}
	
	/**
	 * Sets the font of the date-textfield.
	 */
	public void setDateFieldFont(Font f){
		dateField.setFont(f);
	}
	/**
	 * Calls all the listeners that have been registered by this DatePicker
	 */
	private void datePickDone(){
		int i, max = listenerList.size();
		for(i = 0; i < max; i++){
			listenerList.get(i).datePickReady(this);
		}
	}
	
	/**
	 * simple private class that is used for the document filter. I didn't
	 * feel like writing an anonymous inner class
	 * @author Mees Gelein
	 *
	 */
	private class DateFieldFilter extends DocumentFilter{
		
		private int maxChar = 0;
		private JTextField tf;
		
		/**
		 * Creates a new DateFieldFilter with the provided amount of 
		 * characters as a limit
		 * @param maxCharacters
		 */
		public DateFieldFilter(int maxCharacters, JTextField textField){
			maxChar = maxCharacters;
			tf = textField;
		}
		
		@Override
	    public void insertString(DocumentFilter.FilterBypass fb, int offset, String s, AttributeSet as) throws BadLocationException {
	        int i, len = s.length();
	        boolean isValidInteger = true;
	        int selectedChars = tf.getSelectionEnd() - tf.getSelectionStart();
	        boolean stillFits = fb.getDocument().getLength() + len - selectedChars <= maxChar;
	        

	        tf.setSelectionStart(offset);
	        tf.setSelectionEnd(offset + 1);
	        
	        for (i = 0; i < len; i++)
	        {
	            if (!Character.isDigit(s.charAt(i)))
	            {
	                isValidInteger = false;
	                break;
	            }
	        }
	        
	        if (isValidInteger && stillFits){
	            super.insertString(fb, offset, s, as);
	        }else{
	            Toolkit.getDefaultToolkit().beep();
	        }
	        
	        offset ++;
	        if(offset == 2) offset = 3;
	        if(offset == 5){
	        	if(!skipYearInput){
	        		offset = 6;
	        	}else{
	        		datePickDone();
	        	}
	        }
	        if(offset == maxChar){
	        	datePickDone();
	        }
	        tf.setSelectionStart(offset);
	        tf.setSelectionEnd(offset + 1);	 
	    }

	    @Override
	    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String s, AttributeSet as) throws BadLocationException {
	        int i, len = s.length();
	        boolean isValidInteger = true;
	        int selectedChars = tf.getSelectionEnd() - tf.getSelectionStart();
	        boolean stillFits = fb.getDocument().getLength() + len - selectedChars <= maxChar;
	        
	        tf.setSelectionStart(offset);
	        tf.setSelectionEnd(offset + 1);	        
	        
	        for (i = 0; i < len; i++)
	        {
	            if (!Character.isDigit(s.charAt(i)))
	            {
	                isValidInteger = false;
	                break;
	            }
	        }
	        
	        if (isValidInteger && stillFits){
	            super.replace(fb, offset, length, s, as);
	        }else{
	            Toolkit.getDefaultToolkit().beep();
	        }
	        
	        offset ++;
	        if(offset == 2) offset = 3;
	        if(offset == 5){
	        	if(!skipYearInput){
	        		offset = 6;
	        	}else{
	        		datePickDone();
	        	}
	        }
	        if(offset == maxChar){
	        	datePickDone();
	        }
	        tf.setSelectionStart(offset);
	        tf.setSelectionEnd(offset + 1);	 
	    }
	}
	
	/**
	 * An Interface that allows this class to call a class that implements this.
	 * Sort of a makeshift listener structure 
	 * @author Mees Gelein
	 *
	 */
	public interface IDatePickReady{
		public void datePickReady(DatePicker parent);
	}
}