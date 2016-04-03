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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class UiMainController implements Initializable {

	private Logic logic;

	private static UIResult res;

	public static UIResult getRes() {
		return res;
	}

	public static void setRes(UIResult res) {
		UiMainController.res = res;
	}

	// Home tab
	@FXML
	private Tab homeTab;

	@FXML
	private Button overdueButton;

	@FXML
	private Button upcomingButton;

	@FXML
	private TreeView<?> upcomingListUI;

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
	
	@FXML
    private TreeView<String> upcomingTabContent;

	// floating tab
	@FXML
	private Tab floatingTab;

	// standalone overdue tab
	@FXML
	private Tab overdueTab;

	@FXML
	private TableView<Task> overdue;

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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TODO get UIResult from logic somehow
		UIResult res = new UIResult();
		initializeTabs(res);





	}

	private void initializeTabs(UIResult res) {
		initializeHomeTab(res);
		initializeUpcomingList(res);
	}

	private void initializeHomeTab(UIResult res) {
		// TODO can use css file to set images
		// homeTab.setGraphic(new ImageView(new Image("cs2103_w09_1j/esther/Resources/HomeTab.jpg")));
		initializeOverdueList(res);
		initializeUpcomingList(res);
	}

	private void initializeOverdueList(UIResult res) {
		ArrayList<Task> overdueBuffer = res.getOverdueBuffer();
		ObservableList<Task> odList = (ObservableList) FXCollections.observableArrayList(overdueBuffer);
		overdueList.setItems(odList);
		overdue.setItems(odList);
	}

	private void initializeUpcomingList(UIResult res) {
		ArrayList<Task> todayBuffer = res.getTodayBuffer();
		ArrayList<Task> tmrBuffer = res.getTmrBuffer();
		ArrayList<Task> weekBuffer = res.getWeekBuffer();
		ObservableList<Task> tdList = (ObservableList) FXCollections.observableArrayList(todayBuffer);
		ObservableList<Task> tmList = (ObservableList) FXCollections.observableArrayList(tmrBuffer);
		ObservableList<Task> wkList = (ObservableList) FXCollections.observableArrayList(weekBuffer);


		TreeItem<String> rootItem = new TreeItem<String>("This is a fking dummy root");
		TreeItem<String> todayNode = new TreeItem<String>("Today");
		TreeItem<String> tomorrowNode = new TreeItem<String>("Tomorrow");
		TreeItem<String> weekNode = new TreeItem<String>("One Week");

		rootItem.getChildren().addAll(todayNode, tomorrowNode, weekNode);
		upcomingListUI = new TreeView<>(rootItem);
		upcomingTabContent = new TreeView<>(rootItem);
		//upcomingList.setShowRoot(false);
	}
}
