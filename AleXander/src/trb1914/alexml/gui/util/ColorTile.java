package trb1914.alexml.gui.util;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * Class to display a colored tile. Can be repainted with setColor();
 * @author Mees Gelein
 *
 */
public class ColorTile extends JPanel{

	private int width = 16;
	private int height = 16;
	private BufferedImage _img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	/**
	 * makes a new ColorTile
	 * @param c
	 */
	public ColorTile(Color c){
		add(new JLabel(new ImageIcon(_img)));
		setColor(c);
	}
	
	/**
	 * Sets the ColorTile to match the provided dimensions
	 */
	public void setSize(int w, int h){
		width = w;
		height = h;
		_img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		removeAll();
		add(new JLabel(new ImageIcon(_img)));
		repaint();
	}
	
	/**
	 * sets the color of the tile and repaints();
	 * @param c
	 */
	public void setColor(Color c){
		int i, j;
		for(i = 0; i < width; i++){
			for(j = 0; j < height; j++){
				_img.setRGB(i, j, c.getRGB());
			}
		}
		repaint();
	}
}
