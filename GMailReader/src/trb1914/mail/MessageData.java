package trb1914.mail;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

import trb1914.debug.Debug;

/**
 * Holds the data from the messages to display after the connection has been closed
 * @author Mees Gelein
 *
 */
public class MessageData {

	public String subject = "Unknown";
	public String sender = "Unknown";
	private Object content = null;
	private boolean debug = false;

	/**
	 * Created from every message
	 * @param m
	 */
	public MessageData(Message m){
		try{
			subject = m.getSubject();
			sender = m.getFrom()[0].toString();
			sender = parseSender(sender);
			content = m.getContent();
		}catch(Exception e){
			Debug.println("Couldn't convert");
			e.printStackTrace();
		}

	}
	
	/**
	 * Constructor for debugging only
	 * @param sub
	 * @param send
	 * @param content
	 */
	public MessageData(String sub, String send, String content){
		subject = sub;
		sender = send;
		this.content = content;
		debug = true;
	}
	
	/**
	 * Parses the sender
	 * @param s
	 * @return
	 */
	private String parseSender(String s){
		s = s.replaceAll("<", "<i#");
		s = s.replaceAll(">", "</i>");
		s = s.replaceAll("#", ">");
		return s;
	}

	/**
	 * Parses the message content object
	 * @param o
	 * @return
	 */
	public String parseContent(){
		if(debug) return content.toString();
		
		String s = "";
		if(content instanceof MimeMultipart){
			MimeMultipart mp = (MimeMultipart) content;
			try{
				s = getText(mp.getBodyPart(0));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		s = s.replaceAll("\n", "<br>");
		return s;
	}

	/**
	 * Return the primary text content of the message.
	 */
	private String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
			return s;
		}
		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart)p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart)p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}
		return null;
	}
}
