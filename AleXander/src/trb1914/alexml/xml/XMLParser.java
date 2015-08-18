package trb1914.alexml.xml;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import trb1914.alexml.Debug;
import trb1914.alexml.Main;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.gui.tabs.main.TabHolder;
/**
 * Provides acces and parsing for XML documents
 * @author Mees Gelein
 */
public class XMLParser {

	/**
	 * the current loaded default XML-Document. WARNING: Can be empty
	 */
	public static Document xmlDocument;

	/**
	 * for internal use in this class only, provides acces to the xmlDocument
	 */
	private static XPath xPath;

	/**
	 * the document builder used by this parser
	 */
	private static DocumentBuilder docBuilder;
	
	
	/**
	 * updates the docDate element inside publicationStmt to the
	 * current time
	 */
	public static void setLastSavedDate(){
		Node dateSaved = XMLParser.getNodeAt("//publicationStmt/docDate");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateSaved.setTextContent(dateFormat.format(Calendar.getInstance().getTime()));
		TabHolder.xmlPanel.update();
	}


	/**
	 * This function will try and parse a String to XML. Returns succes
	 * @param s			the String to convert
	 * @return			succes
	 */
	public static boolean parseXML(String s){
		XPathFactory xpathFactory = XPathFactory.newInstance();
		xPath = xpathFactory.newXPath();
		boolean succes = false;
		s = s.replaceAll("\t", "");
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\f", "");
		s = s.replaceAll("    ", "");
		s = s.replaceAll("   ", "");
		s = s.replaceAll("  ", "");
		s = s.replaceAll( "(?s)<!--.*?-->", "" );
		InputSource source = new InputSource(new StringReader(s));
		try{
			DocumentBuilderFactory docBuildFac = DocumentBuilderFactory.newInstance();
			docBuilder = docBuildFac.newDocumentBuilder();
			xmlDocument = docBuilder.parse(source);
			xmlDocument.getDocumentElement().normalize();
			succes = true;
		}catch(Exception e){
			JOptionPane.showMessageDialog(Main.main, LanguageRegistry.PARSING_ERROR);
		}
		return succes;
	}

	/**
	 * returns the evaluate return of XPath. Shorthand way to get
	 * xPath.evaluate();
	 * @param s			The XPath String Expression
	 * @return			The return of the xPath.evaluate() function
	 */
	public static String evaluate(String s){
		String returnValue="Error could not evaluate";
		try {
			returnValue = xPath.evaluate(s, xmlDocument);
		}catch(Exception e){
			Debug.print("[XMLParser]: Error evaluating the xml document with string: " + s + "\n");
		}
		return returnValue;
	}

	/**
	 * returns the first Node that matches the given XPath String expression
	 * @param s			a XPath String expression
	 * @return			the first Node to match the given XPath String expression
	 */
	public static Node getNodeAt(String s){
		Node n = null;
		try {
			n = (Node) xPath.evaluate(s, xmlDocument, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			Debug.print("[XMLParsers]: Could not get node at: " + s + "\n");
		}
		return n;
	}

	/**
	 * returns a list of nodes that match the given XPath String expression
	 * @param s			a XPath String expression
	 * @return			a NodeList that matches the expression
	 */
	public static NodeList getNodeListOf(String s)
	{
		NodeList n = null;
		try{
			n = (NodeList) xPath.evaluate(s, xmlDocument, XPathConstants.NODESET);
		}catch(Exception e){
			Debug.print("[XMLParsers]: Could net get nodelist of: " + s + "\n");
		}
		return n;
	}

	/**
	 * returns the String representation of the default XML document
	 * @return			the String representation of the default document
	 */
	public static String getString(){
		String s = getString(xmlDocument);
		return s;
	}

	/**
	 * returns the String representation of the provided XML document
	 * @return			the String representation of the provided document
	 */
	public static String getString(Document xmlDoc){
		String s= getString(xmlDoc, false);
		return s;
	}

	/**
	 * returns the String representation of the provided XML document using a Transformer
	 * @param xmlDoc	the Document to turn into a string
	 * @param omitDeclaration should the xml declaration be omitted?
	 * @return			the String representation of the provided document
	 */
	public static String getString(Document xmlDoc, boolean omitDeclaration){
		String s = "StringInitValue";
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			if(omitDeclaration)transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(xmlDoc), new StreamResult(writer));
			s = writer.getBuffer().toString();
		}catch(Exception e){
			Debug.print("[XMLParsers]: Something went wrong. Converting XML to String failed\n");
		}
		return s;
	}

	/**
	 * tries to turn a string into an XML Element
	 * @param text		the text to parse
	 * @return			the parsed XML element
	 */
	public static Element textToElement(String text){
		try{
			return docBuilder.parse(new InputSource(new StringReader(text))).getDocumentElement();
		}catch(Exception e){
			Debug.print("[XMLParsers]: Couldn't parse text: " + text + "\n");
			return null;
		}
	}

	/**
	 * appends the given String to the provided Node. First it parses the String to a XML node
	 * and then adds that node to the provided parent.
	 * @param parent		the Node to add the XML-String to
	 * @param fragment		the String representation of the XML-Node to append
	 */
	public static void appendXmlFragment(Node parent, String fragment, String nAttribute){
		try{
			Document doc = parent.getOwnerDocument();
			Node fragmentNode = docBuilder.parse(new InputSource(new StringReader(fragment))).getDocumentElement();
			fragmentNode = doc.importNode(fragmentNode, true);
			parent.appendChild(fragmentNode);
			((Element) fragmentNode).setAttribute("n", nAttribute);
		}catch(Exception e){
			Debug.print("[XMLParsers]: Appending node failed: " + fragment + "\n");
		}
	}
	
	/**
	 * appends the given String to the provided Node. First it parses the String to a XML node
	 * and then adds that node to the provided parent.
	 * @param parent		the Node to add the XML-String to
	 * @param fragment		the String representation of the XML-Node to append
	 */
	public static void appendXmlFragment(Node parent, String fragment){
		try{
			Document doc = parent.getOwnerDocument();
			Node fragmentNode = docBuilder.parse(new InputSource(new StringReader(fragment))).getDocumentElement();
			fragmentNode = doc.importNode(fragmentNode, true);
			parent.appendChild(fragmentNode);
		}catch(Exception e){
			Debug.print("[XMLParsers]: Appending node failed: " + fragment + "\n");
		}
	}

	/**
	 * removes all attributes from the given element
	 * @param e
	 */
	public static void removeAllAttributes(Element e){
		//Debug.println("[XMLParser]: length of attributes before removing: " + e.getAttributes().getLength());
		NamedNodeMap map = e.getAttributes();
		String[] names = new String[map.getLength()];
		for(int i = 0; i < map.getLength(); i++){
			names[i] = map.item(i).getNodeName();
		}
		for(int i = 0; i < names.length; i++){
			map.removeNamedItem(names[i]);
		}
		//Debug.println("[XMLParser]: length of attributes after removing: " + e.getAttributes().getLength());		
	}

	/**
	 * returns the element segment at the specified index
	 * @param index
	 * @return
	 */
	public static Element getSegment(int index){
		return (Element) XMLParser.getNodeListOf("//" + XMLTags.SEGMENT).item(index);
	}

	/**
	 * adds an empty segment to the end of the text
	 */
	public static void addEmptySegment(){
		Node n = XMLParser.getNodeAt("//" + XMLTags.TEXT);
		XMLParser.appendXmlFragment(n, FileRegistry.emptySegment);
	}

	/**
	 * removes the segment at the specified index
	 * @param index
	 */
	public static void removeSegment(int index){
		NodeList nl = XMLParser.getNodeListOf("//" + XMLTags.SEGMENT);
		Node n = nl.item(index);
		Node parent = n.getParentNode();
		parent.removeChild(n);
	}

	/**
	 * adds a empty header to the document
	 */
	public static void addEmptyHeader(){
		Node teiHeader = XMLParser.getNodeAt("//" + XMLTags.TEIHEADER);
		Node parent = teiHeader.getParentNode();
		parent.removeChild(teiHeader);
		Node text = XMLParser.getNodeAt("//" + XMLTags.TEXT);
		parent.removeChild(text);
		XMLParser.appendXmlFragment(parent, FileRegistry.emptyHeader);
		parent.appendChild(text);
	}
	
	/**
	 * tries to locate and read the tags XML file. Returns the
	 * root element of the document
	 */
	public static Element getTagsConfig(){
		try{
			DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
			File tagsFile = new File(FileRegistry.TAGS_FILE);
			Debug.print("[XMLParser]: TagsFile found: " + tagsFile.exists() + "\n");
			Document doc = docBuilder.parse(tagsFile);
			Element rootElement = doc.getDocumentElement();
			rootElement.normalize();
			return rootElement;
		}catch(Exception e){
			Debug.print("[XMLParsers]: Exception trying to read the Tagsconfig file: ");
			e.printStackTrace();
			Debug.print("\n");
		}
		return null;
	}

}
