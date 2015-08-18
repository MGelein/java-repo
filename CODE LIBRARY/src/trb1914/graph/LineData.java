package trb1914.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import trb1914.debug.Debug;

/**
 * A class that holds the necessary data to draw a line
 * @author Mees Gelein
 */
public class LineData {
	/**The color of the line*/
	public Color lineColor = Color.red;
	/**The color of the points that make up the lines*/
	public Color pointColor = Color.red;
	/**Flag that determines if the lines between the points are being drawn.*/
	public boolean drawLines = true;
	/**Flag that determines if the points that make up the lines are being drawn*/
	public boolean drawPoints = true;
	/**The radius used for the points if they are drawn*/
	public int pointRadius = 4;
	/**Flag that determines if this line should be drawn at all*/
	private boolean visible = true;
	/**The list of numbers that makes up the points*/
	private ArrayList<Float> data = new ArrayList<Float>();

	/**
	 * Sets the array of data numbers to display. Quickly copies over all
	 * data from the array that is passed on to this method. Altering the array
	 * after it has been passed on to this class will not alter the lineData.
	 * @param dataArray
	 */
	public void setData(ArrayList<Float> dataArray){
		data = new ArrayList<Float>();
		if(dataArray == null){
			Debug.println("Can't use a null array!", this);
			return;
		}
		for(float f : dataArray) data.add(f);
	}

	/**
	 * Returns the lineData
	 */
	public ArrayList<Float> getData(){
		return data;
	}

	/**
	 * Sets the line visibility
	 * @param b
	 */
	public void setVisible(boolean b){
		visible = b;
	}

	/**Returns the visibility of this line*/
	public boolean getVisible(){return visible;}

	/**
	 * Gets the value of the data at the provided horizontal value. If the data doesn't reach this far, 0 is returned
	 * @param horizontal
	 * @return
	 */
	public float getDataAt(int horizontal){
		if(horizontal < data.size()){
			return data.get(horizontal);
		}else{
			return 0;
		}
	}
	
	/**
	 * Uses the LineGraph instance to determine the drawing range and makes it draw the data.
	 * This method is called by the lineGraph itself.
	 * @param g
	 */
	public void drawLineData(LineGraph graph, Graphics2D g){
		if(!visible) return;//we don't need to draw anything if the line is invisible
		int i, max = graph.getHorizontalMaximum() + 1;
		if(max > data.size()) max = data.size(); //if the data is too short, we display all of it we can

		//start drawing
		
		//draw lines
		g.setColor(lineColor);
		for(i = graph.getHorizontalMinimum(); i < max; i++){//only draw the lines that can be displayed on the horizontal axis
			if(drawLines && i > 0) graph.paintLine(i - 1, i, data.get(i - 1), data.get(i), g);
		}
		//draw points
		g.setColor(pointColor);
		for(i = graph.getHorizontalMinimum(); i < max; i++){//only draw the points that can be displayed on the horizontal axis
			if(drawPoints) graph.paintPoint(i, data.get(i), pointRadius, g);
		}		
	}
}