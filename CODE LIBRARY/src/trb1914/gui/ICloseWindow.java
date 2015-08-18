package trb1914.gui;
/**
 * Interface that allows a window to be closed
 * using the closeWindow(boolean) method. Boolean
 * parameter can be used to indicate if changes should be
 * saved
 * @author Mees Gelein
 */
public interface ICloseWindow {

	/**Closes the window. The boolean parameter can be used as a saveChanges flag.*/
	public void closeWindow(boolean b);
}
