package trb1914.anim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Static class that handles animation. This class should NOT be instantiated
 * @author Mees Gelein
 */
public abstract class Animator {

	/**
	 * List of all objects we need to animate
	 */
	private static ArrayList<IAnimate> toAnimate = new ArrayList<IAnimate>();
	/**A Swing timer that will take care of the regular updating of all objects*/
	private static Timer animTimer = new Timer(40, new ActionListener(){
		public void actionPerformed(ActionEvent e){
			animateAll();
		}
	});

	/**
	 * Starts animating this object. If the object is already on the list nothing will
	 * happen
	 * @param obj
	 */
	public static void start(final IAnimate obj){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if(!toAnimate.contains(obj)){
					toAnimate.add(obj);
					if(toAnimate.size() == 1) animTimer.start();
				}
			}
		});
	}

	/**
	 * Starts animating the provided objects. If a objects is already on the list nothing will happen
	 * @param objs
	 */
	public static void start(IAnimate... objs){
		for(IAnimate a : objs) start(a); 
	}

	/**
	 * Stops animating this object. If the object is not on the list nothing will happen
	 * @param obj
	 */
	public static void stop(final IAnimate obj){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if(toAnimate.contains(obj)){
					toAnimate.remove(obj);
					if(toAnimate.size() == 0) animTimer.stop();
				}
			}
		});
	}

	/**
	 * Stops animating the provided objects. If a object is not on the list nothing will happen
	 * @param objs
	 */
	public static void stop(IAnimate... objs){
		for(IAnimate a : objs) stop(a);
	}

	/**
	 * Animates all objects in the toAnimate list
	 */
	private static void animateAll(){
		for(IAnimate a: toAnimate) a.animate();
	}


	/**
	 * Animatable class. When you implement this interface you can
	 * start animating any object
	 * @author Mees Gelein
	 */
	public interface IAnimate{
		/**Is called regularly by the animator if this is object is registered to animate*/
		public void animate();
	}

	/**
	 * Don't instantiate this class! It is meant to be used as a static class
	 * @throws Exception */
	public Animator() throws Exception{throw new Exception("You should not instantiate the trb194.anim.Animator class!");}

}
