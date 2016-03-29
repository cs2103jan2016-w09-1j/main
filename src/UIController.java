import java.io.IOException;
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

/*
 * @author: Guo Mingxuan
 * @@A0130749A
 */

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
			  *String logicOutput = "search:\n" + "Search Result should be displayed here";
			  *String logicOutput = "search";
			  *String logicOutput = "help:\n" + "this is the help menu";
			  */
			logicResult(logicOutput, commandLog);
			input.clear();
		}
		
	}

	void logicResult(String in, Label label) throws Exception {
		String header = in.substring(0, 8);
		System.out.println(header);
		if (header.equalsIgnoreCase("search:\n")) {
			label.setText("Search completed!");
			String searchResult = in.substring(8);
			
			secondController searchController = createSecondWindow(label, "Search Result");
			if (searchResult.length() > 0) {
				searchController.setResult(searchResult);
			} else {
				searchController.setResult("No result found!");
			}

			display.setText(logic.getInternalStorageInString());
			
		} else if (in.substring(0, 6).equalsIgnoreCase("help:\n")) {
			String help = in.substring(6);
			label.setText("Help Menu");
			secondController helpController = createSecondWindow(label, "Help");
			helpController.setResult(help);

		} else {
			label.setText(in);
			display.setText(logic.getInternalStorageInString());
		}
	}

	private secondController createSecondWindow(Label lb, String title) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("secondWindow.fxml"));
		Pane secondWindow = null;
		try {
			secondWindow = (Pane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		secondController second = loader.getController();
		second.setMainController(this);

		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(lb.getScene().getWindow());
		Scene scene = new Scene(secondWindow);
		scene.getStylesheets().add("cs2103_w09_1j/esther/UI.css");
		stage.setScene(scene);
		stage.setTitle(title);
		stage.show();
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					stage.close();
				}
			}
		});

		return second;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			logic = new Logic();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		display.setText(logic.executeCommand("show .by date"));
		intro.setText("All available tasks: ");
		commandLog.setText("Loaded tasks!");
	}
}
