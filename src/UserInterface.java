import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UserInterface extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Pane mainPane = (Pane) FXMLLoader.load(getClass().getResource("mainUI.fxml"));
		primaryStage.setScene(new Scene(mainPane));
		
		primaryStage.setTitle("ESTHER");
		
		primaryStage.show();
		
	}
	
	
}
