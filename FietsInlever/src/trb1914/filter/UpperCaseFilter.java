package trb1914.filter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
/**
 * copied from http://stackoverflow.com/questions/11571779/java-force-jtextfield-to-be-uppercase-only
 * @author Mees Gelein
 *
 */
public class UpperCaseFilter extends DocumentFilter{
	
	public void insertString(DocumentFilter.FilterBypass fb, int offset,
            String text, AttributeSet attr) throws BadLocationException {

        fb.insertString(offset, text.toUpperCase(), attr);
    }

    public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
            String text, AttributeSet attrs) throws BadLocationException {

        fb.replace(offset, length, text.toUpperCase(), attrs);
    }

}
