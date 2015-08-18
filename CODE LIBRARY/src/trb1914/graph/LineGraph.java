package trb1914.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import trb1914.debug.Debug;
/**
 * A class that will visualize arrays of floats passed to it using 
 * lines with points.
 * @author Mees Gelein
 */
public class LineGraph extends JPanel {

	/**The maximum value displayed on the vertical axis*/
	private int vMax = 1;
	/**The minimum value displayed on the vertical axis*/
	private int vMin = 0;
	/**The maximum value displayed on the horizontal axis*/
	private int hMax = 1;
	/**The minimum value displayed on the horizontal axis*/
	private int hMin = 0;
	/**The amount of pixels of padding around the graph*/
	private Insets padding = new Insets(20, 50, 20, 20);
	/**The list of all the lines that need to be displayed*/
	private ArrayList<LineData> lines = new ArrayList<LineData>();
	/**The color of the axis. By default this is black*/
	public Color axisColor = Color.black;
	/**The color used to draw the grid*/
	public Color gridColor = new Color(0.0f, 0.5f, 1.0f, 0.2f);
	/**The height of the axis marker*/
	private int markerSize = 4;
	/**The spacing of the amt of markers on the vertical axis. Lower numbers give more markers*/
	private float markerSpacing = 30;
	/**Flag that determines if a grid will be drawn with the markers as guidelines*/
	private boolean drawGrid = true;
	
	/**
	 * Creates a new LineGraph instance without any starting data.
	 */
	public LineGraph() {
		setLayout(new BorderLayout());
		setBackground(Color.white);
	}
	
	/**
	 * Adds an instance of lineData to the graph. Call the method autoFit() to make the graph fit all lineData
	 * @param data
	 */
	public void addLineData(LineData data, boolean autoFit){
		if(lines.size() == 0){//first line added determines initial hMax
			hMax = data.getData().size() - 1;
		}
		lines.add(data);
		
		//if we want to autofit, we do that, else we just repaint
		if(autoFit) autoFit(hMin, hMax);
		else repaint();
	}
	
	/**
	 * Tries to fit all the values in the given range.
	 */
	public void autoFit(int hMin, int hMax){
		int newVMin = 0;
		int newVMax = 0;
		int i, max = hMax + 1;
		float cValue;
		for(LineData lineData : lines){
			for(i = hMin; i < max; i++){
				cValue = lineData.getDataAt(i);
				if(cValue > newVMax) newVMax = Math.round(cValue + 1);
				if(cValue < newVMin) newVMin = Math.round(cValue + 1);
			}
		}
		this.hMin = hMin;
		this.hMax = hMax;
		vMin = newVMin;
		vMax = newVMax;
		repaint();
	}
	
	/**
	 * Sets the area of the graph that we display
	 * @param hMin
	 * @param hStart
	 * @param vMin
	 * @param vMax
	 */
	public void setDisplayArea(int hMin, int hMax, int vMin, int vMax){
		this.hMin = hMin;
		this.hMax = hMax;
		this.vMin = vMin;
		this.vMax = vMax;
	}
	
	/**
	 * Sets the amount of pixels that remain empty around the graph.
	 */
	public void setPadding(Insets insets){
		padding = insets;
	}
	
	/*
	 * GETTERS AND SETTERS
	 */
	
	/**
	 * Sets the maximum value that can be displayed along the vertical axis. Maximum value
	 * can't be smaller than the mininum value.
	 * @param max
	 */
	public void setVerticalMaximum(int max){
		if(max < vMin){
			Debug.println("vertical maximum value can't be smaller than the minimum value", this); 
			return;
		}
		vMax = max;
	}
	
	/**
	 * Returns the maximum value that can be displayed along the vertical axis
	 * @return
	 */
	public int getVerticalMaximum(){ return vMax;}
	
	/**
	 * Sets the minimum value that can be displayed along the vertical axis. Minimum value
	 * can't be larger than the maximum value.
	 * @param min
	 */
	public void setVerticalMinimum(int min){
		if(min > vMax) { 
			Debug.println("vertical minimum value can't be larger than the maximum value", this); 
			return;
		}
		vMin = min;
	}
	
	/**
	 * Returns the minimum value that can be displayed along the vertical axis
	 * @return
	 */
	public int getVerticalMinimum(){ return vMin;}
	
	/**
	 * Sets the maximum value that can be displayed along the horizontal axis. Maximum value
	 * can't be smaller than the minimum value.
	 * @param max
	 */
	public void setHorizontalMaximum(int max){
		if(max < hMin){
			Debug.println("horizontal maximum value can't be smaller than the minimum value", this); 
			return;
		}
		hMax = max;
	}
	
	/**
	 * Returns the maximum value that can be displayed along the horizontal axis
	 * @return
	 */
	public int getHorizontalMaximum(){ return hMax;}
	
	/**
	 * Sets the minimum value that can be displayed along the horizontal axis. Minimum value
	 * can't be larger than the maximum value.
	 * @param min
	 */
	public void setHorizontalMinimum(int min){
		if(min > hMax) { 
			Debug.println("horizontal minimum value can't be larger than the maximum value", this); 
			return;
		}
		hMin = min;
	}
	
	/**
	 * Returns the minimum value that can be displayed along the horizontal axis
	 * @return
	 */
	public int getHorizontalMinimum(){ return hMin;}
	
	/*
	 * PAINTING
	 */
	
	/**
	 * Custom graph painting here
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		    RenderingHints.VALUE_ANTIALIAS_ON);
		
		drawAxis(g2);
		
		for(LineData ld : lines){
			ld.drawLineData(this, g2);//draws the line
		}
		
	}
	
	/**
	 * Draws the axis
	 */
	private void drawAxis(Graphics2D g){
		//draw axis
		g.setColor(axisColor);
		paintLine(hMin - hMax, hMax + hMax, 0, 0, g);
		g.drawLine(padding.left, getHeight(), padding.left, 0);
		
		//draw the markers on the horizontal axis
		for(int i = hMin; i < hMax + 1; i++) drawHMarker(i, g);
		
		//calculate marker precision / marker amt
		float graphHeight = getHeight() - (padding.bottom + padding.top);
		int markerAmt = Math.round((graphHeight - (graphHeight % markerSpacing)) / markerSpacing);
		float markerSpace = (vMax - vMin) / markerAmt + 1;
		float vertical = vMin;
		
		while(vertical < vMax - markerSpace){
			drawVMarker(vertical, g);
			vertical += markerSpace;
		}
		drawVMarker(vMax, g);
		
	}
	
	/**
	 * Draws a marker on the h-axis
	 * @param g
	 */
	private void drawHMarker(int horizontal, Graphics2D g){
		Point p = dataToScale(horizontal, 0);
		if(drawGrid){
			g.setColor(gridColor);
			g.drawLine(p.x, 0, p.x, getHeight());
			g.setColor(axisColor);
		}
		g.drawLine(p.x, p.y - markerSize, p.x, p.y + markerSize);
	}
	
	/**
	 * Draws a marker on the v-axis
	 * @param v
	 * @param g
	 */
	private void drawVMarker(float vertical, Graphics2D g){
		Point p = dataToScale(hMin, vertical);
		if(drawGrid){
			g.setColor(gridColor);
			g.drawLine(0, p.y, getWidth(), p.y);
			g.setColor(axisColor);
		}
		g.drawLine(p.x - markerSize, p.y, p.x + markerSize, p.y);
		g.drawString(Integer.toString(Math.round(vertical)), p.x - 40, p.y + 4);
	}
	
	
	/**
	 * Draws a line between two points on the graph. If the Starting points are null no line is drawn
	 * @param hStart		starting index on the horizontal axis
	 * @param hEnd			ending index on the horizontal axis
	 * @param dStart		datavalue on the startingIndex
	 * @param dEnd			datavalue on the endingindex
	 */
	protected void paintLine(int hStart, int hEnd, float dStart, float dEnd, Graphics2D g){
		Point ptA = dataToScale(hStart, dStart);
		Point ptB = dataToScale(hEnd, dEnd);
		g.drawLine(ptA.x, ptA.y, ptB.x, ptB.y);
	}
	
	/**
	 * Draws a point on the graph.
	 * @param h
	 * @param data
	 * @param c
	 */
	protected void paintPoint(int h, float data, int radius, Graphics2D g){
		Point pt = dataToScale(h, data);
		g.fillOval(pt.x - radius, pt.y - radius, radius * 2, radius * 2);
	}
	
	/**
	 * Scales the data to fit in the display
	 */
	private Point dataToScale(int horizontal, float vertical){
		int x = Math.round((horizontal - hMin) * ((getWidth() - (padding.left + padding.right)) / hMax * 1.0f - hMin));
		int y = Math.round(getHeight() - (vertical - vMin) * ((getHeight() - (padding.bottom + padding.top)) / (vMax * 1.0f - vMin)));
		x += padding.left;
		y -= padding.top;
		return new Point(x, y);
	}
	
	/*
	 * CLASSES
	 */
}
