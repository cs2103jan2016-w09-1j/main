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
	private Text searchResult;

	public void setSearchResult(String searchResult) {
		this.searchResult.setText(searchResult);
	}

	/*@FXML
	void ESCAPE(KeyEvent event) {
		System.out.println("Escape key is pressed");
		Stage stage = (Stage) searchResult.getScene().getWindow();
		stage.hide();
		System.out.println("Window closed");

	}*/

}
