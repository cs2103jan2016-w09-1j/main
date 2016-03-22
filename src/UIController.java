import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class UIController implements Initializable {

	private Logic logic;

	@FXML
	private Pane mainPane;

	@FXML
	private Text intro;


	@FXML
	private VBox displayWindow;

	@FXML
	private Text display;


	@FXML
	private Label commandLog;

	@FXML
	private TextField input;

	@FXML
	void ENTER(KeyEvent event) throws Exception {
		if (event.getCode() == KeyCode.ENTER) {
			String userInput = input.getText();
			System.out.println(userInput);

			String logicOutput =logic.executeCommand(userInput); 
			 /* down below is for testing purpose
			  *String logicOutput = "searchSearch Result should be displayed here";
			  *String logicOutput = "search";
			  */
			isSearch(logicOutput, commandLog);
			input.clear();
		}
		
	}

	void isSearch(String in, Label label) throws Exception {
		String header = in.substring(0, 6);
		System.out.println(header);
		if (header.equalsIgnoreCase("search")) {
			label.setText("Search completed");
			String searchResult = in.substring(6);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("secondWindow.fxml"));
			Pane secondWindow = (Pane) loader.load();
			secondController second = loader.getController();
			second.setMainController(this);

			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(label.getScene().getWindow());
			Scene scene = new Scene(secondWindow);
			scene.getStylesheets().add("cs2103_w09_1j/esther/UI.css");
			stage.setScene(scene);
			stage.setTitle("Search Result");
			stage.show();
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ESCAPE) {
						stage.close();
					}
				}
			});

			if (searchResult.length() > 0) {
				second.setSearchResult(searchResult);
			} else {
				second.setSearchResult("No result found!");
			}

			// TODO stub: receive logic internal memory
			display.setText(logic.executeCommand("show .by date"));

		} else {
			label.setText(in);
			// TODO stub: receive logic internal memory
			display.setText(logic.executeCommand("show .by date"));
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			logic = new Logic();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// TODO stub: need logic feedback
		display.setText(logic.executeCommand("show .by date"));
		intro.setText("All available tasks: ");
		commandLog.setText("Loaded tasks!");
		input.clear();
	}
}
