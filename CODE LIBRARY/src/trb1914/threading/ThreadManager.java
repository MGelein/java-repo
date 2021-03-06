package trb1914.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple Thread Manager to allow acces to a static Executor Service
 * @author Mees Gelein
 */
public class ThreadManager {

	/**The thread pool used to service submitted taks*/
	private static ExecutorService service = Executors.newFixedThreadPool(10);
	
	/**
	 * Submits the provided Runnable to be performed
	 * @param r
	 */
	public static void submit(Runnable r){
		service.submit(r);
	}
}
