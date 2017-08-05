package trb1914.helper;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import trb1914.debug.Debug;

/**
 * A class to help with the hell that is audio playback in java
 * @author Mees Gelein
 */
public abstract class AudioHelper {

	/**
	 * Tries to play the provided instance of audioInputStream. Normally you want to 
	 * use either the playSoundFile(File f) or playSound(String url) method. Both
	 * use this method internally. Adjusts playback gain
	 * with the provided amount of decibel
	 * @param audioInputStream
	 */
	public static void playAudioInputStream(AudioInputStream audioInputStream, float dB){
		if(audioInputStream == null) {Debug.println("Can not play a null AudioInputStream!", AudioHelper.class); return;}
		if(dB > 6.0f) {Debug.println("Playback gain can never exceed 6.0f!", AudioHelper.class); return;}
		
		AudioFormat audioFormat = audioInputStream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
		Clip clip = null;
		
		try {
			clip = (Clip) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			Debug.println("Could not get the Clip using Audiosystem.getline", AudioHelper.class);
			e.printStackTrace();
			return;
		}
		try {
			if(clip!=null)clip.open(audioInputStream);
			else Debug.println("Clip has not been initialized", AudioHelper.class);
		} catch (LineUnavailableException e) {
			Debug.println("The line was unavailable", AudioHelper.class);
			e.printStackTrace();
		} catch (IOException e) {
			Debug.println("A mysterious IOException occured. Stream interrupted?", AudioHelper.class);
			e.printStackTrace();
			return;
		}
		
		//adjust to playback gain
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(dB);
		
		//finally play the clip
		clip.start();
	}
	
	/**
	 * Tries to load the provided url and play the file as a sound.
	 * @param url
	 */
	public static void playSound(String url){
		playSound(url, 0.0f);
	}
	
	/**
	 * Tries to load the provided url and play the file as a sound. Adjusts playback gain
	 * with the provided amount of decibel
	 * @param url
	 */
	public static void playSound(String url, float dB){
		if(url == null) {Debug.println("Can not play a null url", AudioHelper.class); return;}
		try {
			playAudioInputStream(AudioSystem.getAudioInputStream(new BufferedInputStream(AudioHelper.class.getResourceAsStream(url))), dB);
		} catch (UnsupportedAudioFileException e) {
			Debug.println("This file type is not supported for playback", AudioHelper.class);
			e.printStackTrace();
		} catch (IOException e) {
			Debug.println("An IOException occured. Maybe an interupted stream?", AudioHelper.class);
			e.printStackTrace();
		}
	}
}
	