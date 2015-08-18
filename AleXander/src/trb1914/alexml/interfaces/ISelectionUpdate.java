package trb1914.alexml.interfaces;
/**
 * Interface that gaurantees the selectionUpdate method being there. Used 
 * instead of normal event based programming. Allows objects to listen for
 * selection changes
 * @author Mees Gelein
 */
public interface ISelectionUpdate {

	/**
	 * A selection update has taken place when this method is called
	 */
	public void selectionUpdate();
}
