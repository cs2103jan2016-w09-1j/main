/**
 * @@author
 */
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class secondController {

	private UIController mainController;

	public void setMainController(UIController mainController) {
		this.mainController = mainController;
	}

	@FXML
	private Text result;

	public void setResult(String searchResult) {
		this.result.setText(searchResult);
	}

}
