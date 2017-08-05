import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MusicMixer extends Application{

	/**
	 * Starts the application by passing a reference to the primarystage
	 */
	public void start(Stage stage) throws Exception {
		BorderPane borderPane = new BorderPane();
		Scene scene = new Scene(borderPane, 1280, 720);
		
		stage.setTitle("Music Mixer");
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Entry point
	 * @param args
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

}
