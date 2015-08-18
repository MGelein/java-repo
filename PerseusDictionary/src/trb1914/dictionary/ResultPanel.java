package trb1914.dictionary;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import trb1914.helper.FileHelper;
/**
 * The panel that will display and animate the results of the search queries
 * @author Mees Gelein
 */
public class ResultPanel extends JPanel{

	/**The amount of results that are shown. Used to compute the width of the window*/
	public int shownResults = 2;
	/**List of the results that are showing*/
	public ArrayList<Result> results = new ArrayList<Result>();
	/**
	 * Creates a new (should be the only one) ResultPanel
	 */
	public ResultPanel() {
		super(null);//give this thing BorderLayout for now		
	}
	
	/**
	 * Makes the first one in the list show the response
	 * @param response
	 */
	public void showResult(String response, String query){
		saveResult(response, query);
		results.get(results.size() - 1).showResult(response);
	}
	
	/**
	 * Tries to save the result to a textfile
	 */
	private void saveResult(String content, String query){
		if(content.contains("Sorry") || content.contains("An error")) return;
		new Thread(new Runnable() {	
			public void run() {
				FileHelper.saveString(content, new File(PerseusDictionary.CACHE + File.separator + query + ".txt"), true);
			}
		}).start();
	}
	
	/**
	 * Hops all results over one and kills of the last one to make space
	 */
	public void makeSpace(){
		int i, max = results.size();
		ArrayList<Result> toRemove = new ArrayList<Result>();
		int toKill = results.size() - shownResults + 1;
		//removes the last element
		for(i = 0; i < max; i++){
			if(toKill <= 0){
				results.get(i).gotoNext();
			}else{
				results.get(i).die();
				toRemove.add(results.get(i));
				toKill --;
			}
		}
		if(toRemove.size() > 0){
			//remove the elements that died
			for(Result r : toRemove){
				results.remove(r);
			}
		}
		
		//add the new one to show the search result
		Result r = new Result(this);
		results.add(r);
		revalidate();
	}
}
