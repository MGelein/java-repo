package trb1914.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

/**
 * Test class to make Graph tests
 * @author Mees Gelein
 */
public class GraphTest extends JFrame{

	/**
	 * The entry point for the tests
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GraphTest test = new GraphTest();
				test.setPreferredSize(new Dimension(800, 600));
				test.setResizable(true);
				test.setTitle("Graph tests");
				test.setSize(test.getPreferredSize());
				test.setLocationRelativeTo(null);
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				test.setVisible(true);
			}
		});
	}
	
	/**
	 * The constructor for the test
	 */
	public GraphTest() {
		makeGUI();
	}
	
	/**
	 * Builds the GUI
	 */
	private void makeGUI(){
		setLayout(new BorderLayout());
		
		//add the new lineGraph to the panel
		LineGraph lineGraph = new LineGraph();
		add(lineGraph);
		
		ArrayList<Float> data = new ArrayList<Float>();
		for(int i = 0 ; i < 11; i++){
			data.add(i * 30.0f);
		}

		LineData line = new LineData();
		line.pointColor = Color.orange.darker();
		line.lineColor = Color.orange;
		line.setData(data);
		
		LineData line2 = new LineData();
		line2.pointColor = Color.red.darker();
		data = new ArrayList<Float>();
		for(int i = 0 ; i < 20; i++){
			data.add((float) Math.random() * 300.0f - 100.0f);
		}
		line2.setData(data);
		
		lineGraph.addLineData(line2, true);
		lineGraph.addLineData(line, true);
		
		lineGraph.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	}

}
