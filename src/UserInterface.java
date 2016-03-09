import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UserInterface extends Application {
	
	private static String res;
	private static TextArea input, display;
	private Logic logic = new Logic();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.setTitle("ESTHER");

		GridPane root = new GridPane();
		root.setPadding(new Insets(10, 10, 10, 10));
		root.setHgap(10);
		root.setVgap(10);

		// Text Area for display
		display = new TextArea();
		display.setEditable(false);
		display.setText("Welcome to ESTHER\n");
		display.setPrefHeight(300);
		GridPane.setConstraints(display, 0, 0);
		
		// Text Area for input
		input = new TextArea();
		input.setPromptText("Enter your commands here:");
		input.setPrefHeight(150);
		input.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					String text = input.getText();
					display.appendText(text + "\n");
					
					// call parser
					String result = logic.executeCommand(text.trim());
					display.appendText(result);
					
					input.clear();
				}
			}
			
		});
		GridPane.setConstraints(input, 0, 1);
		
		root.getChildren().addAll(display, input);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);

		primaryStage.show();
	}
	
	static void showResult(String result) {
		res = result;
		display.appendText(res);
	}

}
