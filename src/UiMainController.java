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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class UiMainController implements Initializable {

	private Logic logic;

	private static UIResult res;

	public static UIResult getRes() {
		return res;
	}

	public static void setRes(UIResult res) {
		UiMainController.res = res;
	}

	/*
	 * This section is for reference to
	 * Home Tab content
	 */
	@FXML
	private Tab homeTab;

	@FXML
	private GridPane homeGrid;

	@FXML
	private Button overdueButton;

	@FXML
	private Button upcomingButton;

	// Home tab's overdue list
	@FXML
	private TableView<Task> overdueList;

	@FXML
	private TableColumn<Task, Integer> HOID;

	@FXML
	private TableColumn<Task, String> HOTask;

	@FXML
	private TableColumn<Task, String> HODate;

	@FXML
	private TableColumn<Task, Integer> HOPriority;

	@FXML
	void overdueClick(MouseEvent event) {

	}

	// upcoming tab
	@FXML
	private Tab upcomingTab;

	@FXML
	private VBox upContent;


	/*
	 * This section is for reference to 
	 * floating tab content
	 */
	@FXML
	private Tab floatingTab;

	@FXML
	private TableView<?> floatingContent;

	@FXML
	private TableColumn<?, ?> FTID;

	@FXML
	private TableColumn<?, ?> FTTask;

	@FXML
	private TableColumn<?, ?> FTDate;

	@FXML
	private TableColumn<?, ?> FTPriority;


	/*
	 * This section is for reference to 
	 * standalone Overdue Tab content
	 */
	@FXML
	private Tab overdueTab;

	@FXML
	private TableView<Task> overdue;

	@FXML
	private TableColumn<?, ?> OTID;

	@FXML
	private TableColumn<?, ?> OTTask;

	@FXML
	private TableColumn<?, ?> OTDate;

	@FXML
	private TableColumn<?, ?> OTPriority;


	/*
	 * This section is for reference to 
	 * Completed Tab content
	 */
	@FXML
	private Tab completedTab;

	@FXML
	private TableView<?> completedContent;

	@FXML
	private TableColumn<?, ?> CTID;

	@FXML
	private TableColumn<?, ?> CTTask1;

	@FXML
	private TableColumn<?, ?> CTDate1;

	@FXML
	private TableColumn<?, ?> CTPriority1;

	/*
	 * This section is for reference to 
	 * All Tab content
	 */
	@FXML
	private Tab allTab;

	@FXML
	private TableView<?> allContent;

	@FXML
	private TableColumn<?, ?> ATID1;

	@FXML
	private TableColumn<?, ?> ATTask11;

	@FXML
	private TableColumn<?, ?> ATDate11;

	@FXML
	private TableColumn<?, ?> ATPriority11;

	// initialize logic
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		/*try {
			logic = new Logic();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/

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
		
		ArrayList<TaskWrapper> overdueWrapper = new ArrayList<TaskWrapper>();
		for (int i = 0; i < overdueBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(overdueBuffer.get(i));
			overdueWrapper.add(tw);
		}
		
		ObservableList<Task> odList = (ObservableList) FXCollections.observableArrayList(overdueWrapper);
		
		HOID.setCellValueFactory(new PropertyValueFactory<Task,Integer>("id"));
		HOTask.setCellValueFactory(new PropertyValueFactory<Task,String>("taskName"));
		HODate.setCellValueFactory(new PropertyValueFactory<Task,String>("date"));
		HOPriority.setCellValueFactory(new PropertyValueFactory<Task,Integer>("id"));
		overdueList.setItems(odList);
		overdue.setItems(odList);
	}

	private void initializeUpcomingList(UIResult res) {
		ArrayList<Task> todayBuffer = res.getTodayBuffer();
		ArrayList<Task> tmrBuffer = res.getTomorrowBuffer();
		ArrayList<Task> weekBuffer = res.getWeekBuffer();
		ObservableList<Task> tdList = (ObservableList) FXCollections.observableArrayList(todayBuffer);
		ObservableList<Task> tmList = (ObservableList) FXCollections.observableArrayList(tmrBuffer);
		ObservableList<Task> wkList = (ObservableList) FXCollections.observableArrayList(weekBuffer);


		TreeItem<String> rootItem = new TreeItem<String>("This is a fking dummy root for homw");
		TreeItem<String> todayNode = new TreeItem<String>("Today");
		TreeItem<String> tomorrowNode = new TreeItem<String>("Tomorrow");
		TreeItem<String> weekNode = new TreeItem<String>("One Week");
		TreeItem<String> test = new TreeItem<String>("test 12");
		TreeItem<String> test1 = new TreeItem<String>("test 13");
		TreeItem<String> test2 = new TreeItem<String>("test 14");
		TreeItem<String> test3 = new TreeItem<String>("test 15");
		TreeItem<String> test4 = new TreeItem<String>("test 16");

		rootItem.getChildren().addAll(todayNode, tomorrowNode, weekNode);
		TreeView<String> upcomingListUI = new TreeView<>(rootItem);
		TreeView<String> upcomingTabContent = new TreeView<>(rootItem);

		// attach TreeView to HomeTab upcoming list
		homeGrid.getChildren().add(upcomingListUI);
		GridPane.setConstraints(upcomingListUI, 1, 1, 1, 2);

		// attach TreeView to UpcomingTab content
		upContent.getChildren().clear();
		upContent.getChildren().add(upcomingTabContent);
		upContent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		upcomingListUI.setShowRoot(false);
	}
}
