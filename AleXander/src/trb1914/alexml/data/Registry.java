package trb1914.alexml.data;

import java.awt.Toolkit;

/**
 * holds some values that don't fit the FileRegistry or 
 * the LanguageRegistry
 * @author Mees Gelein
 *
 */
public abstract class Registry {

	//the ctrl key equivalent on all Operating Systems
	public final static int CTRL_MASK_CROSS_PLATFORM = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	//the amount of characters in a standard sized textField
	public final static int CHAR_AMOUNT = 30;
	//the amount of characters in a half sized textField
	public final static int CHAR_AMOUNT_SMALL = 15;
	
}
