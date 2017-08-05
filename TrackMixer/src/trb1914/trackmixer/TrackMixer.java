package trb1914.trackmixer;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/**
 * Project that allows the user to mix a multitrack song
 * @author Mees Gelein
 */
public class TrackMixer extends Application{

	/**The title of the appliction*/
	public static final String APP_TITLE = "TrackMixer";
	
	public void start(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE);
        
        
        Button btn = new Button();
        btn.setText("Play sound");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
            	File soundFile = new File("alert.mp3");
                Media sound = new Media(soundFile.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
            }
        });
        
        Slider slider = new Slider(0, 1, 0.5);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(0.25f);
        slider.setBlockIncrement(0.1f);
        
        BorderPane root = new BorderPane();
        root.setBottom(btn);
        root.setCenter(slider);
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
        
    }
	
	/*
	 * STATIC METHODS
	 */
	
	/**
	 * Entry point. Code execution starts here
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
