import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class secondController {
	
	private UIController mainController;

	public void setMainController(UIController mainController) {
		this.mainController = mainController;
	}

	@FXML
    private Text searchResult;

	public void setSearchResult(Text searchResult) {
		this.searchResult = searchResult;
	}
	
	@FXML
    void exit(KeyEvent event) {
		if (event.getCode() == KeyCode.ESCAPE) {
			Platform.exit();
		}
    }


}
