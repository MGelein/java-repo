package trb1914.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import trb1914.debug.Debug;

/**
 * Static helper class for stupid socket stuff and network communication
 * @author Mees Gelein
 */
public abstract class SocketHelper {
	
	/**
	 * An attempt to send a message will timeout after this value of milliseconds (default 5000).
	 */
	public static int MESSAGE_TIMEOUT = 5000;
	/**
	 * A check to see if this port is listening will timeout after this value of milliseconds
	 * default (40).
	 */
	public static int CHECK_TIMEOUT = 40;
	
	/**
	 * Tries to send the specified message to the specified address timeing out after
	 * the specified amount of milliseconds
	 * @param ipAddress
	 * @param portNumber
	 * @param message
	 * @param timeout
	 * @throws IOException 
	 */
	public static void sendTo(String ipAddress, int portNumber, String message, int timeout) 
			throws IOException{
		sendTo(new InetSocketAddress(ipAddress, portNumber), message, timeout);
	}
	
	/**
	 * Tries to send the specified message to the specified addres timeing out after the 
	 * specified amount of milliseconds
	 * @param address
	 * @param message
	 * @param timeout
	 * @throws IOException 
	 */
	public static void sendTo(InetSocketAddress address, String message, int timeout) 
			throws IOException{
		Socket socket = new Socket();
		socket.connect(address, timeout);
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		out.write(message);
		out.close();
		socket.close();
	}
	
	/**
	 * Tries to send the specified message to the specified addres with a timeout after
	 * 5 seconds
	 * @param address
	 * @param message
	 * @throws IOException
	 */
	public static void sendTo(InetSocketAddress address, String message) 
			throws IOException{
		sendTo(address, message, MESSAGE_TIMEOUT);
	}
	
	/**
	 * Tries to send the specified message to the specified address with a timeout value
	 * of 5 secs
	 * @param ipAddress
	 * @param portNumber
	 * @param message
	 * @throws IOException 
	 */
	public static void sendTo(String ipAddress, int portNumber, String message)
			throws IOException{
		sendTo(ipAddress, portNumber, message, MESSAGE_TIMEOUT);
	}
	
	/**
	 * Receives a message on the specified portNumber. Warning: This locks the current thread
	 * @param portNumber
	 * @return
	 * @throws IOException 
	 */
	public static String receiveOn(int portNumber) throws IOException{
		ServerSocket serverSocket = new ServerSocket(portNumber);
		Socket socket = serverSocket.accept();
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while(!in.ready()){}
		String msg = in.readLine();
		serverSocket.close();
		return msg;
	}
	
	/**
	 * Checks the specified address to see if a listening serversocket is found.
	 * No data is sent. It is advised to keep the timeout value low (default is 40)
	 * @param address
	 * @param timeout
	 * @return
	 */
	public static boolean checkPort(InetSocketAddress address, int timeout){
		boolean found = false;
		try{
			Socket socket = new Socket();
			socket.connect(address, timeout);
			socket.close();
			found = true;
		}catch(IOException e){
			Debug.println(address + " | response: " + e.getMessage(), SocketHelper.class);
		}
		return found;
	}
	
	/**
	 * Checks the specified address to see if a listening serversocket is found.
	 * No data is sent. Uses the default timeout value of 40 milliseconds
	 * @param address
	 * @param timeout
	 * @return
	 */
	public static boolean checkPort(InetSocketAddress address){
		return checkPort(address, CHECK_TIMEOUT);
	}
	
	/**
	 * Checks the specified address to see if a listening serversocket is found.
	 * No data is sent. Uses the default timeout value of 40 milliseconds
	 * @param address
	 * @param portNumber
	 * @return
	 */
	public static boolean checkPort(String address, int portNumber){
		return checkPort(new InetSocketAddress(address, portNumber), CHECK_TIMEOUT);
	}
	
	/**
	 * Checks the specified address to see if a listening serversocket is found.
	 * No data is sent. It is advised to keep the timeout value low
	 * @param address
	 * @param portNumber
	 * @param timeout
	 * @return
	 */
	public static boolean checkPort(String address, int portNumber, int timeout){
		return checkPort(new InetSocketAddress(address, portNumber), timeout);
	}
}
