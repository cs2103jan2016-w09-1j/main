import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import cs2103_w09_1j.esther.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class mainController implements Initializable {

	private Logic logic;
	
	// Home tab
    @FXML
    private Tab homeTab;

    @FXML
    private Button overdueButton;

    @FXML
    private Button upcomingButton;

    @FXML
    private TreeView<?> upcomingList;

    // Home tab's overdue list
    @FXML
    private TableView<Task> overdueList;

    @FXML
    private TableColumn<Task, Integer> HOID;

    @FXML
    private TableColumn<Task, String> HOTask;

    @FXML
    private TableColumn<Task, Date> HODate;

    @FXML
    private TableColumn<Task, Integer> HOPriority;

    // upcoming tab
    @FXML
    private Tab upcomingTab;
    
    // floating tab
    @FXML
    private Tab floatingTab;

    // standalone overdue tab
    @FXML
    private Tab overdueTab;

    // completed tab
    @FXML
    private Tab completedTab;

    // all tab
    @FXML
    private Tab allTab;

    @FXML
    void overdueClick(MouseEvent event) {
    	
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		try {
			logic = new Logic();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// get UIResult from logic somehow
		UIResult res = new UIResult();
		
		ArrayList<Task> overdue = res.getOverdueBuffer();
		ArrayList<Task> today = res.getTodayBuffer();
		ArrayList<Task> tmr = res.getTmrBuffer();
		ArrayList<Task> week = res.getWeekBuffer();
		
		ObservableList<Task> overdueList = (ObservableList) FXCollections.observableArrayList(overdue);
		ObservableList<Task> todayList = (ObservableList) FXCollections.observableArrayList(today);
		ObservableList<Task> tmrList = (ObservableList) FXCollections.observableArrayList(tmr);
		ObservableList<Task> weekList = (ObservableList) FXCollections.observableArrayList(week);
		((TableView<Task>) overdueList).setItems(overdueList);
		
		// TODO can use css file to set images
		// homeTab.setGraphic(new ImageView(new Image("cs2103_w09_1j/esther/Resources/HomeTab.ico")));
		
		
		
		
	}

    
}
