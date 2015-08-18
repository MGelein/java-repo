package trb1914.helper;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import trb1914.debug.Debug;

/**
 * Static utility methods for Image stuffs
 * @author Mees Gelein
 */
public abstract class ImageHelper {

	/**The single thread service that will load the image on a separate thread*/
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	/**
	 * Uses Resource to load the ImageIcon from the specified location
	 * @param url
	 * @return
	 */
	public static ImageIcon getImageIcon(String url){
		return new ImageIcon(getImage(url));
	}
	
	/**
	 * Uses Resource to load the Image from the specified location
	 * @param url
	 * @return
	 */
	public static BufferedImage getImage(String url){
		try{
			return executorService.submit(new ImageCallable(url)).get();
		}catch(Exception e){
			Debug.println("[trb1914.helper.ImageHelper]: Error during image parsing.");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Resizes the provided image to fit within the provided dimensio
	 * @param icon
	 * @return
	 */
	public static Image resizeKeepScale(Image image, Dimension fit){
		return resizeKeepScale(new ImageIcon(image), fit);
	}
	
	/**
	 * Resizes the provided image to fit within the provided dimensio
	 * @param icon
	 * @return
	 */
	public static Image resizeKeepScale(ImageIcon icon, Dimension fit){
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		float ratio = (width * 1.0f) / (height * 1.0f);
		if(height > fit.height){
			height = fit.height;
			width = Math.round((height * 1.0f) * ratio);
		}
		if(width > fit.width){
			width = fit.width;
			height = Math.round((width * 1.0f) / ratio);
		}
		return icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}
	
	/**
	 * This loads an ImageLoader using ImageIO
	 * @author trb1914
	 */
	public static class ImageCallable implements Callable<BufferedImage>{
		
		/**The location of the image file we need to load*/
		private String url;
		
		/**
		 * Loads the image location specified by the String url
		 * @param url
		 */
		public ImageCallable(String url) {
			this.url = url;
		}
		
		/**
		 * Returns the bufferedImage
		 */
		public BufferedImage call() throws Exception {
			return ImageIO.read(ImageHelper.class.getResourceAsStream(url));
		}
		
	}
}
