package cs2103_w09_1j.esther;
/**
 * @@author
 */
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class UiSecondController {

	private UiMainController mainController;

	public void setMainController(UiMainController mainController) {
		this.mainController = mainController;
	}

	@FXML
	private Text result;

	public void setResult(String searchResult) {
		this.result.setText(searchResult);
	}

}
