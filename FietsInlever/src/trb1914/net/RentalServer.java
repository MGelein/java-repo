package trb1914.net;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;

import trb1914.XMLParser;
import trb1914.data.Registry;
import trb1914.data.Rental;
import trb1914.debug.Debug;
import trb1914.threading.ThreadManager;

/**
 * Server that will do the actual file reading,parsing and writing
 * @author Mees Gelein
 */
public class RentalServer {

	public static int PORT_NUMBER = 8886; ///starting point for all the other port numbers. Configured through prefs file
	///The currently active instance, should be the only instance of this server
	public static RentalServer activeInstance = null;
	public static int DETECT_PORT = PORT_NUMBER + 1;//used to find the server
	public static int DOWN_PORT = PORT_NUMBER + 2;//used to download the rentals list from the server
	public static int BROAD_PORT = PORT_NUMBER + 3;//used for sending messages from server to client
	public static int COMM_PORT = PORT_NUMBER;//used to send messages from client to server
	///The rentals file loaded into memory
	private ArrayList<Rental> allRentals = new ArrayList<Rental>();
	/**List of all the member addresses*/
	private ArrayList<String> addresses = new ArrayList<String>();
	/**The timeout to wait for a connection when searching for this server*/
	public static int CHECK_TIMEOUT = 40;

	/**
	 * Just a way for me to launch the server separately. Mostly for testing purposes
	 * @param args
	 */
	public static void main(String[] args){
		new RentalServer();
		JFrame frame = new JFrame();
		frame.setTitle("Server");
		frame.setPreferredSize(new Dimension(200, 100));
		frame.pack();
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				activeInstance.shutDown();
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}


	/**
	 * Creating a new RentalServer instance will automatically
	 * load the rentals file
	 */
	public RentalServer(){
		activeInstance = this;
		parseXML();
		openDetectPort();
		openCommPort();
	}

	/**
	 * Opens the communication port that listens for a broadcast from one of the client members
	 */
	private void openCommPort(){
		ThreadManager.submit(new Runnable(){
			public void run(){
				try{
					processInput(SocketHelper.receiveOn(COMM_PORT));
					run();
				}catch(Exception e){
					Debug.println("Could not open communication listening port. Program will not function!", this);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Processes the data that has been sent
	 * @param s
	 */
	private void processInput(String s){
		Debug.println("Received: " + s, this);
		broadCast(s);//first send it back to all members
		if(s.startsWith("//")) {
			return;//dont do anything with a comment	
		}
		if(s.startsWith("@del=")){
			removeRental(s);
		}
		Rental r = new Rental();
		r.loadFromDef(s);
		synchronized(allRentals){
			for(Rental rental : allRentals){
				if(rental.getCode().equals(r.getCode())){//we have found a match
					allRentals.set(allRentals.indexOf(rental), r);
					return;
				}
			}
			//if no match was found this is a new rental and must be added to the system
			Debug.println("No match was found. Add new rental: " + s, this);
			allRentals.add(r);
		}
		saveFile();
	}
	
	private void saveFile(){
		synchronized(allRentals){
			XMLParser.saveRentals(allRentals);
		}
	}

	/**
	 * Broadcasts the specified String to all members of the network
	 * @param s
	 */
	private void broadCast(final String s){
		ThreadManager.submit(new Runnable(){
			public void run(){
				for(String address : addresses){
					Debug.println("broadcastAll: " + s, this);
					broadCastTo(address, s);
				}
			}
		});
	}

	/**
	 * Sends the broadcast to a specific client
	 * @param address
	 */
	private void broadCastTo(String ipAddress, String s){
		try{
			SocketHelper.sendTo(ipAddress, RentalServer.BROAD_PORT, s);
		}catch(Exception e){
			Debug.println("Couldn't reach client (" + ipAddress + ")...", this);
		}
	}

	/**
	 * Processes the remove command
	 * @param command
	 */
	private void removeRental(String command){
		Rental toRemove = null;
		synchronized(allRentals){
			for(Rental rental : allRentals){
				if(rental.getCode().equals(command.split("=")[1])){//we have found a match
					toRemove = rental;
					break;
				}
			}
			if(toRemove != null){
				allRentals.remove(toRemove);
				Debug.println("Succesfully removed rental (" + command +")", this);
			}else{
				Debug.println("Couldn't find the Rental that was supposed to be removed: " + command, this);
			}
		}
	}

	/**
	 * Keeps port 8888 open to detect new devices and give them the
	 * list of rentals. Done on a separate thread
	 */
	private void openDetectPort(){
		ThreadManager.submit(new Runnable(){
			public void run(){
				try{
					Debug.println("Started detection on port " + DETECT_PORT + "...", this);
					//port opened for detection
					ServerSocket detectServerSocket = new ServerSocket(DETECT_PORT);
					Socket detectSocket = detectServerSocket.accept();
					Debug.println("Received a connection from: " + detectSocket.getInetAddress().getHostAddress(), this);
					addresses.add(detectSocket.getInetAddress().getHostAddress());
					detectServerSocket.close();
					detectSocket.close();

					Debug.println("Opening list download port " + DOWN_PORT + "...", this);				
					//port opened for downloading of the rentals list
					ServerSocket downServerSocket = new ServerSocket(DOWN_PORT);
					Socket downSocket = downServerSocket.accept();
					Debug.println("Received download connection from: " + detectSocket.getInetAddress().getHostAddress(), this);
					PrintWriter out = new PrintWriter(downSocket.getOutputStream());
					Debug.println("Rental list parsing started..", this);
					String data = getRentalsAsString();
					Debug.println("Sending data: " + data, this);
					out.write(data + "\n");//sends the entire list of StringData to the other side
					Debug.println("Data sent. Restarting detect port...", this);
					out.close();
					downSocket.close();
					downServerSocket.close();

					//be ready for detection again
					openDetectPort();

				}catch(Exception e){
					Debug.println("Couldn't open the detect port. This means the program won't function. This could be caused"
							+ "\n\tby another instance of the rentalserver already running on this pc.", this);
				}
			}
		});
	}

	/**
	 * Loads the XML-file from the location stored in the Registry
	 */
	private void parseXML(){
		Debug.println("Rentals file parsing started...", this);
		ThreadManager.submit(new Runnable(){
			public void run(){
				File rentalsFile = new File(Registry.RENTALS_FILE_LOCATION);
				if(rentalsFile.exists()){
					Debug.println("Rentals file exists..", this);
					synchronized(allRentals){
						allRentals = XMLParser.parseXMLFile(rentalsFile, null);
					}
					Debug.println("Rentals file finished parsing sucessfully", this);
				}else{
					Debug.println("Rentals file couldn't be found at location: " + rentalsFile.getAbsolutePath(), this);
				}

			}
		});
	}
	/**
	 * Shuts down the server. Saves the rentals file
	 */
	public void shutDown(){
		saveFile();
	}

	/**
	 * Returns a representation of all the rentals as a String. Needs
	 * to be parsed on the other side
	 * @return
	 */
	private String getRentalsAsString(){
		StringBuilder builder = new StringBuilder();
		for(Rental r : allRentals){
			if(r.isValid()){
				builder.append(r.getAsString());
			}
		}
		return builder.toString();
	}

}
