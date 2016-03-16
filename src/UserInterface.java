import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserInterface extends Application {
	
	private static final Logger logger = Logger.getLogger(UserInterface.class.getName());
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		
		Pane mainPane = null;
		try {
			mainPane = (Pane) FXMLLoader.load(getClass().getResource("mainUI.fxml"));
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		
		primaryStage.setScene(new Scene(mainPane));
		
		primaryStage.setTitle("ESTHER");
		
		
		
		primaryStage.show();
		
	}
	
	
}
