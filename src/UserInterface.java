import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * @author: Guo Mingxuan
 * @@author A0130749A
 */

public class UserInterface extends Application {
	
	private static final Logger logger = Logger.getLogger(UserInterface.class.getName());
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		
		Pane mainPane = null;
		try {
			mainPane = (Pane) FXMLLoader.load(getClass().getResource("tabView.fxml"));
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		
		Scene scene = new Scene(mainPane);
		scene.getStylesheets().add("cs2103_w09_1j/esther/UI.css");
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.ESCAPE) {
					final Stage dialog = new Stage();
	                dialog.initModality(Modality.APPLICATION_MODAL);
	                dialog.initOwner(primaryStage);
	                VBox dialogVbox = new VBox(10);
	                Text Qn = new Text("Are you sure you want to quit?");
	                Text option = new Text("Press Escape to quit or any other key to cancel");
	                dialogVbox.getChildren().addAll(Qn, option);
	                dialogVbox.setMargin(Qn, new Insets(10,5,10,10));
	                dialogVbox.setMargin(option, new Insets(10,5,10,10));
	                Scene dialogScene = new Scene(dialogVbox, 300, 100);
	                dialogScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

						@Override
						public void handle(KeyEvent event) {
							if (event.getCode() == KeyCode.ESCAPE) {
								primaryStage.close();
							} else {
								dialog.close();
							}
						}
	                	
	                });
	                dialog.setScene(dialogScene);
	                dialog.show();
				}
				
			}
			
		});
		
		primaryStage.setScene(scene);
		
		primaryStage.setTitle("ESTHER");
		
		primaryStage.show();
		
	}
	
	
}
