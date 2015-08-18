package trb1914.alexml.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import trb1914.alexml.Debug;
import trb1914.alexml.data.FileRegistry;
import trb1914.alexml.data.LanguageRegistry;
import trb1914.alexml.data.tree.Word;
import trb1914.alexml.gui.util.WordHighlightPanel;
import trb1914.alexml.interfaces.ISelectionUpdate;
/**
 * This class used to be just a long method but for easy readability this has been made a seperate class
 * @author Mees Gelein
 */
public class WordEditor extends JFrame implements ISelectionUpdate{

	/**The word that we are currently editing*/
	private Word w;
	/**The JTextfield that shows what we have selected and allows editing*/
	private JTextField field;
	/**The Word hightlighting panel*/
	private WordHighlightPanel wordHighLightPanel;
	/**
	 * Opens a tiny separate window that allows the user to 
	 * edit the textcontent of this word
	 * @param w		the word we want to edit
	 * @param p		the point the editorwindow should be opened at
	 */
	public WordEditor(final Word word, Point p){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		ImageIcon img = new ImageIcon(FileRegistry.APP_ICON);
		setIconImage(img.getImage());
		p.x -= getSize().width / 3;
		p.y -= getSize().height / 3;
		if(p.x < 0) p.x = 0;
		if(p.y < 0) p.y = 0;
		setLocation(p);
		w = word;
		
		makeGUI();
	}
	
	/**
	 * Builds the GUI
	 */
	private void makeGUI(){
		JPanel fieldPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		JPanel panel = new JPanel(new BorderLayout());
		field = new JTextField();
		field.setFont(FileRegistry.getNormalFont());
		fieldPanel.add(field);
		fieldPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		panel.add(fieldPanel, BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		add(panel);
		field.setText(w.textContent);
		
		String allText = SegmentEditor.mainTextTab.mainTextArea.getText();
		allText = allText.replace(LanguageRegistry.NOTE_STRING, "");
		allText = allText.replaceAll("#", "# ");
		wordHighLightPanel = new WordHighlightPanel(allText, this);
		
		for(int i=0; i < w.indices.size(); i++){
			wordHighLightPanel.setSelected(w.indices.get(i), true);
		}
		
		panel.add(wordHighLightPanel, BorderLayout.CENTER);
				
		field.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {setTitle(field.getText());}
			public void insertUpdate(DocumentEvent e) {setTitle(field.getText());}
			public void removeUpdate(DocumentEvent e) {setTitle(field.getText());}
		});

		setTitle(w.textContent);

		JButton saveButton = new JButton(LanguageRegistry.SAVE_AND_CLOSE);
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String mainText = SegmentEditor.mainTextTab.mainTextArea.getText();
				String word = field.getText();
				int index = mainText.indexOf(word);
				Debug.println(word + " was found at index: " + index, this);
				SegmentEditor.wordEditorTab.setWordTextContent(w, word, wordHighLightPanel.getSelectedWordIndices());
				dispose();
			}
		});

		JButton discardButton = new JButton(LanguageRegistry.DISCARD_CHANGES);
		discardButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});

		buttonPanel.add(saveButton);
		buttonPanel.add(discardButton);
		
		pack();//packs the frame after adding all components
		setMinimumSize(getSize());
	}

	/**Called when the wordHightLightPanel is updated*/
	@Override
	public void selectionUpdate() {
		field.setText(wordHighLightPanel.getSelectedString());
	}
}