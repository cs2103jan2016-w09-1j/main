import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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


public class UIController {
	
	private Logic logic = new Logic();
	
    @FXML
    private Pane mainPane;

    @FXML
    private VBox displayWindow;

    @FXML
    // load internal memory
    private Text display;
   
    
    @FXML
    private Label commandLog;

    @FXML
    private TextField input;

    @FXML
    void ENTER(KeyEvent event) {
    	if (event.getCode() == KeyCode.ENTER) {
    		String userInput = input.getText();
    		commandLog.setText(logic.executeCommand(userInput));
    		display.setText("String internal memory");
    	}
    }

    // stub: new window for search
    void isSearch(boolean search, String in, Label label, String searchResult) throws Exception {
    	if (search/*some evalutation here*/) {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("secondWindow"));
    		Pane secondWindow = (Pane) loader.load();
    		secondController second = loader.getController();
    		second.setMainController(this);
    		second.setSearchResult(new Text(searchResult));
    		
    		Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(label.getScene().getWindow());
            Scene scene = new Scene(secondWindow);
            stage.setScene(scene);
            stage.show();
    		
    	} else {
    		label.setText(logic.executeCommand(in));
    	}
    }
}
