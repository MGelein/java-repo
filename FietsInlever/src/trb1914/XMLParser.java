package trb1914;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import trb1914.data.Registry;
import trb1914.data.Rental;
import trb1914.data.RentalItem;
import trb1914.debug.Debug;
import trb1914.helper.FileHelper;
import trb1914.util.LoaderPane;
/**
 * class that does the xml parsing and probably also the coding?
 * @author Mees
 *
 */
public class XMLParser {

	/**Should a backup be made everytime we save.*/
	public static boolean DO_BACKUP = false;
	
	public static final String RENTAL = "rental";
	public static final String PRIORITY = "priority";
	public static final String HIGH = "high";
	public static final String LOW = "low";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String OBJECT = "object";
	public static final String PAID_OBJECT = "paidObject";
	public static final String CODE = "code";
	public static final String SWAPPED_OBJECT = "swappedObject";
	public static final String WARNING = "hasWarning";
	public static final String COMMENT = "comment";
	public static final String RENT_DATE = "rentDate";
	public static final String RETURN_DATE = "returnDate";

	/**
	 * parses the rental file
	 * @param f
	 * @return
	 */
	public static ArrayList	<Rental> parseXMLFile(File f, LoaderPane loader){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ArrayList<Rental> rentals = new ArrayList<Rental>();
		if(loader != null)loader.setPercentage(10);
		if(loader != null)loader.setLocationRelativeTo(null);
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			if(loader != null)loader.setPercentage(20);
			Document document = builder.parse(f);
			if(loader != null)loader.setPercentage(50);
			document.getDocumentElement().normalize();
			NodeList allRentalNodes = document.getElementsByTagName(XMLParser.RENTAL);
			if(loader != null)loader.setPercentage(60);
			for(int i = 0 ; i < allRentalNodes.getLength(); i++){
				Node rentalNode = allRentalNodes.item(i);
				Rental r = parseRentalNode(rentalNode);
				if(r.isValid()){
					rentals.add(r);
				}
			}
			if(loader != null)loader.setPercentage(70);
		}catch(Exception e){
			Debug.println("Exception trying to parse the xml", XMLParser.class);
		}
		return rentals;
	}
	
	/**
	 * parses a single rental node
	 * @param n
	 * @return
	 */
	private static Rental parseRentalNode(Node n){
		Rental r = new Rental();
		NodeList childNodes = n.getChildNodes();
		Node cNode;
		for(int i = 0; i < childNodes.getLength(); i++){
			cNode = childNodes.item(i);
			switch(cNode.getNodeName()){
			case XMLParser.START_DATE:
				r.setStartDate(cNode.getTextContent());
				break;
			case XMLParser.END_DATE:
				r.setEndDate(cNode.getTextContent());
				break;
			case XMLParser.WARNING:
				r.hasWarning = Boolean.parseBoolean(cNode.getTextContent());
				if(cNode instanceof Element){
					r.priority = ((Element) cNode).getAttribute(PRIORITY);
				}
				break;
			case XMLParser.OBJECT:
				r.objects.add(parseItemElement((Element) cNode));
				break;
			case XMLParser.CODE:
				r.setCode(cNode.getTextContent());
				break;
			case XMLParser.COMMENT:
				r.comments = cNode.getTextContent();
				break;
			case XMLParser.PAID_OBJECT:
				r.paidObjects.add(parseItemElement((Element) cNode));
				break;
			case XMLParser.SWAPPED_OBJECT:
				r.swappedObjects.add(parseItemElement((Element) cNode));
				break;
			}
		}
		return r;
	}
	
	/**
	 * Parses the provided element as a rentalItem element
	 * @param e
	 */
	private static RentalItem parseItemElement(Element e){
		RentalItem r = new RentalItem();
		r.name = e.getTextContent();
		r.parseStartDateString(e.getAttribute(RENT_DATE));
		r.parseEndDateString(e.getAttribute(RETURN_DATE));
		return r;
	}
	
	/**
	 * saves all the rentals currently in memory to the disk location specified 
	 * in the registry
	 * @param rentals
	 */
	public static void saveRentals(ArrayList<Rental> rentals) {
		//first move all backup files up one and remove the oldest backup
		File f5 = new File(Registry.RENTALS_FILE_LOCATION + "_5");
		File f4 = new File(Registry.RENTALS_FILE_LOCATION + "_4");
		File f3 = new File(Registry.RENTALS_FILE_LOCATION + "_3");
		File f2 = new File(Registry.RENTALS_FILE_LOCATION + "_2");
		File f1 = new File(Registry.RENTALS_FILE_LOCATION + "_1");
		File rentalsFile = new File(Registry.RENTALS_FILE_LOCATION);
		if(f5.exists()){ f5.delete();}
		if(f4.exists()){ FileHelper.saveString(FileHelper.openFile(f4, true), f5, true);}
		if(f3.exists()){ FileHelper.saveString(FileHelper.openFile(f3, true), f4, true);}
		if(f2.exists()){ FileHelper.saveString(FileHelper.openFile(f2, true), f3, true);}
		if(f1.exists()){ FileHelper.saveString(FileHelper.openFile(f1, true), f2, true);}
		if(rentalsFile.exists()){ FileHelper.saveString(FileHelper.openFile(rentalsFile, true), f1, true);}
		
		//save the current file
		saveRentals(rentals, rentalsFile);
		
		//save to the external backup
		if(DO_BACKUP) saveRentals(rentals, new File(Registry.BACKUP_LOCATION));
	}
	
	/**
	 * saves all the rentals currently in memory to the disk location specified 
	 *  by the provided file
	 * @param rentals
	 */
	public static void saveRentals(ArrayList<Rental> rentals, File f){
		try{
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			doc.appendChild(doc.createElement("XML"));
			for(int i = 0; i < rentals.size(); i++){
				Node n = appendSingleRental(rentals.get(i), doc);
				if(n != null) doc.getDocumentElement().appendChild(n);				
			}
			transformer.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(f)));
		}catch(Exception e){
			Debug.println("Something went wrong trying to save the rentals to the file" + f.getAbsolutePath(), XMLParser.class);
		}
	}
	
	/**
	 * turns one Rental into an xml node
	 * @param r
	 * @return
	 */
	private static Node appendSingleRental(Rental r, Document d){
		Node rentalNode = d.createElement(RENTAL);
		Node startDateNode = d.createElement(START_DATE);
		Node endDateNode = d.createElement(END_DATE);
		Node codeNode = d.createElement(CODE);
		Element warningNode = d.createElement(WARNING);
		Node commentNode = d.createElement(COMMENT);
		startDateNode.setTextContent(r.getStartDate());
		if(r.getStartDate() == null || r.getStartDate().length() < 1) return null;//if this is an empty element we don't use it
		endDateNode.setTextContent(r.getEndDate());
		codeNode.setTextContent(r.getCode());
		warningNode.setTextContent("" + r.hasWarning);
		warningNode.setAttribute(PRIORITY, r.priority);
		commentNode.setTextContent(r.comments);
		rentalNode.appendChild(startDateNode);
		rentalNode.appendChild(endDateNode);
		rentalNode.appendChild(codeNode);
		rentalNode.appendChild(warningNode);
		Element objectNode;
		for(int i = 0; i < r.objects.size(); i++){
			objectNode = d.createElement(OBJECT);
			objectNode.setTextContent(r.objects.get(i).name);
			objectNode.setAttribute(RENT_DATE, r.objects.get(i).getStartDateString());
			objectNode.setAttribute(RETURN_DATE, r.objects.get(i).getEndDateString());
			rentalNode.appendChild(objectNode);
		}
		Element paidObjectNode;
		for(int i = 0; i < r.paidObjects.size(); i++){
			paidObjectNode = d.createElement(PAID_OBJECT);
			paidObjectNode.setTextContent(r.paidObjects.get(i).name);
			paidObjectNode.setAttribute(RENT_DATE, r.paidObjects.get(i).getStartDateString());
			paidObjectNode.setAttribute(RETURN_DATE, r.paidObjects.get(i).getEndDateString());
			rentalNode.appendChild(paidObjectNode);
		}
		Element swappedObjectNode;
		for(int i = 0; i < r.swappedObjects.size(); i++){
			swappedObjectNode = d.createElement(SWAPPED_OBJECT);
			swappedObjectNode.setTextContent(r.swappedObjects.get(i).name);
			swappedObjectNode.setAttribute(RENT_DATE, r.swappedObjects.get(i).getStartDateString());
			swappedObjectNode.setAttribute(RETURN_DATE, r.swappedObjects.get(i).getEndDateString());
			rentalNode.appendChild(swappedObjectNode);
		}
		rentalNode.appendChild(commentNode);
		return rentalNode;
	}
}
