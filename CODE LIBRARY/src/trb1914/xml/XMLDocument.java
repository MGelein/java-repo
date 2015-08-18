package trb1914.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import trb1914.debug.Debug;
import trb1914.helper.FileHelper;

/**
 * This class loads an XML file and holds the XML architecture inside of it.
 * Any changes you make to this document will be reflected in the version you
 * save.
 * @author Mees Gelein
 */
public class XMLDocument {

	/**The actual XML DOM object loaded into memory*/
	protected Document document;

	/**
	 * Creates a new XMLDocument. It tries to parse the provided file into
	 * an XML object
	 * @param f		the file you want to try and parse
	 */
	public XMLDocument(File f) {
		load(f);
	}
	
	/**
	 * Creates a new XMLDocument. It tries to parse the provided String into an XML object
	 * @param s
	 */
	public XMLDocument(String s){
		load(s);
	}

	/**
	 * Loads the provided file into memory completely replacing the current
	 * file. Returns succes. If no file is provided, it will create a new empty document
	 * @param f			the file to load
	 * @return			has the parsing succeeded.
	 */
	public boolean load(File f){
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if(f != null) document = documentBuilder.parse(f);
			else document = documentBuilder.newDocument();
			document.normalize();
			return true;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			Debug.println("Something wen't wrong during parsing", this);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Loads the provided String into memory completely replacing the current
	 * file. Returns succes
	 * @param s			the String to load
	 * @return			has the parsing succeeded.
	 */
	public boolean load(String s){
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = documentBuilder.parse(new InputSource(new StringReader(s)));
			document.normalize();
			return true;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			Debug.println("Something wen't wrong during parsing", this);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Saves the content of this xmlDocument to the specified File. Returns succes
	 * @param f			the file to save the document to
	 * @return			the succes of the operation
	 */
	public boolean save(File f){
		try {
			//setup the transformer to turn the XML document into a string
			Transformer transformer;
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			//setup the Stringwriter to do the actual converting
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));

			//save the generated String to the disk
			FileHelper.saveString(writer.getBuffer().toString(), f, true);
			//return succes
			return true;
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			Debug.println("An error occured during saving of the file: " + f.getAbsolutePath(), this);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns the document rootElement
	 * @return		the document root.
	 */
	public Element getRootElement(){
		if(document == null) {Debug.println("No document has been loaded yet", this); return null;}
		return document.getDocumentElement();
	}
}
