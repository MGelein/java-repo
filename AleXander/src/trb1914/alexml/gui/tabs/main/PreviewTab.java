package trb1914.alexml.gui.tabs.main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLDocument;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import trb1914.alexml.Debug;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.gui.util.LoaderPane;
/**
 * The HTML preview Tab
 * @author Mees Gelein
 *
 */
public class PreviewTab extends JPanel {

	//The editorPane that displays the HTML content
	private JEditorPane editorPane;
	
	//The ScrollPane that holds the editorPane
	private JScrollPane scrollPane;
	
	//a reference to the TEI XSL doc
	private Source xslDoc;
	
	//ref to the transformer
	private Transformer transformer;
	
	//if the xsl values have been initialized
	private boolean xslInit = false;
	
	
	/**
	 * creates a new PreviewTab
	 */
	public PreviewTab(){
		setLayout(new BorderLayout());
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		Font font = new Font("Verdana", Font.PLAIN, 14);
	    String bodyRule = "body { font-family: " + font.getFamily() + "; " +
	            "font-size: " + font.getSize() + "pt; }";
	    ((HTMLDocument)editorPane.getDocument()).getStyleSheet().addRule(bodyRule);
		editorPane.setText("XSL PARSING ERROR");
		scrollPane = new JScrollPane(editorPane);
		add(scrollPane);
		JButton refreshButton = new JButton(new ImageIcon(FileRegistry.REFRESH_ICON));
		refreshButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updatePreview(TabHolder.xmlPanel.getXMLTextArea().getText());
			}
		});
		JPanel refreshPanel = new JPanel(new BorderLayout());
		refreshPanel.add(refreshButton, BorderLayout.WEST);
		refreshButton.setToolTipText(LanguageRegistry.REFRESH_LABEL);
		add(refreshPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * updates the content of the HTML-panel to match the 
	 * given (XML) String. Will be parsed to xml, then parsed
	 * using XSL into HTML to be displayed
	 * @param s
	 */
	public void updatePreview(final String s){
		new Thread(new Runnable(){
			public void run(){
				LoaderPane loader = new LoaderPane("Loading Preview");
				loader.setPercentage(10);
				int caretPos = editorPane.getCaretPosition();
				try{
					if(!xslInit){//only runs first update after the program started. 
						xslDoc = new StreamSource(FileRegistry.TEI_XSL);
						transformer = TransformerFactory.newInstance().newTransformer(xslDoc);
						xslInit = true;
						
					}
					loader.setPercentage(20);
					//get the XML and the XSL
					Source xmlDoc = new StreamSource(new StringReader(s));
					loader.setPercentage(30);
					//Write the transformer to a string and string to editorPane
					StringWriter writer = new StringWriter();
					transformer.transform(xmlDoc, new StreamResult(writer));
					loader.setPercentage(40);
					String s2 = writer.getBuffer().toString();
					editorPane.setText(s2);
					loader.setPercentage(50);
				}catch(Exception e){
					Debug.println("Error with XML preview-view", this);
				}
				loader.setPercentage(60);
				try{
					editorPane.setCaretPosition(caretPos);
				}catch(Exception e){
					editorPane.setCaretPosition(0);
				}
				loader.setPercentage(100);
			}
		}).start();
	}

}
