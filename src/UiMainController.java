import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import cs2103_w09_1j.esther.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

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
	private TableView<TaskWrapper> overdueList;

	@FXML
	private TableColumn<TaskWrapper, String> HOID;

	@FXML
	private TableColumn<TaskWrapper, String> HOTask;

	@FXML
	private TableColumn<TaskWrapper, String> HODate;

	@FXML
	private TableColumn<TaskWrapper, String> HOPriority;

	@FXML
	void overdueClick(MouseEvent event) {

	}

	// home tab's upcoming list
	@FXML
	private TreeTableView<TaskWrapper> homeTree;

	@FXML
	private TreeTableColumn<TaskWrapper, String> homeTreeID;

	@FXML
	private TreeTableColumn<TaskWrapper, String> homeTreeTask;

	@FXML
	private TreeTableColumn<TaskWrapper, String> homeTreeDate;

	@FXML
	private TreeTableColumn<TaskWrapper, String> homeTreePriority;

	// upcoming tab
	@FXML
	private Tab upcomingTab;

	@FXML
	private VBox upContent;

	@FXML
	private TreeTableView<TaskWrapper> upTabTree;

	@FXML
	private TreeTableColumn<TaskWrapper, String> upTreeID;

	@FXML
	private TreeTableColumn<TaskWrapper, String> upTreeTask;

	@FXML
	private TreeTableColumn<TaskWrapper, String> upTreeDate;

	@FXML
	private TreeTableColumn<TaskWrapper, String> upTreePriority;

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
	private TableView<TaskWrapper> overdue;

	@FXML
	private TableColumn<TaskWrapper, String> OTID;

	@FXML
	private TableColumn<TaskWrapper, String> OTTask;

	@FXML
	private TableColumn<TaskWrapper, String> OTDate;

	@FXML
	private TableColumn<TaskWrapper, String> OTPriority;


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

		ObservableList<TaskWrapper> odList = (ObservableList) FXCollections.observableArrayList(overdueWrapper);

		HOID.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("id"));
		HOTask.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("taskName"));
		HODate.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("date"));
		HOPriority.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("priority"));

		OTID.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("id"));
		OTTask.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("taskName"));
		OTDate.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("date"));
		OTPriority.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("priority"));

		overdueList.setItems(odList);
		overdue.setItems(odList);
	}

	private void initializeUpcomingList(UIResult res) {
		// get all buffers from UIResult
		ArrayList<Task> todayBuffer = res.getTodayBuffer();
		ArrayList<Task> tmrBuffer = res.getTomorrowBuffer();
		ArrayList<Task> weekBuffer = res.getWeekBuffer();

		// initialize all buffers of wrappers
		/*ArrayList<TaskWrapper> todayWrapper = new ArrayList<TaskWrapper>();
		ArrayList<TaskWrapper> tmrWrapper = new ArrayList<TaskWrapper>();
		ArrayList<TaskWrapper> weekWrapper = new ArrayList<TaskWrapper>();*/

		TreeItem<TaskWrapper> rootNode = new TreeItem<TaskWrapper>(new TaskWrapper("This is the root node for upcoming tab"));
		TreeItem<TaskWrapper> todayNode = new TreeItem<TaskWrapper>(new TaskWrapper("Today"));
		TreeItem<TaskWrapper> tomorrowNode = new TreeItem<TaskWrapper>(new TaskWrapper("Tomorrow"));
		TreeItem<TaskWrapper> weekNode = new TreeItem<TaskWrapper>(new TaskWrapper("One Week"));

		int i;
		for (i = 0; i < todayBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(todayBuffer.get(i));
			todayNode.getChildren().add(new TreeItem<TaskWrapper>(tw));
			//todayWrapper.add(tw);
		}

		for (i = 0; i < tmrBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(tmrBuffer.get(i));
			tomorrowNode.getChildren().add(new TreeItem<TaskWrapper>(tw));
			//tmrWrapper.add(tw);
		}

		for (i = 0; i < weekBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(weekBuffer.get(i));
			weekNode.getChildren().add(new TreeItem<TaskWrapper>(tw));
			//weekWrapper.add(tw);
		}

		rootNode.getChildren().addAll(todayNode, tomorrowNode, weekNode);

		/*ObservableList<TaskWrapper> tdList = (ObservableList) FXCollections.observableArrayList(todayWrapper);
		 *ObservableList<TaskWrapper> tmList = (ObservableList) FXCollections.observableArrayList(tmrWrapper);
		 *ObservableList<TaskWrapper> wkList = (ObservableList) FXCollections.observableArrayList(weekWrapper);
		 */



		rootNode.setExpanded(true);

		/*
		 * For Home Tab,
		 * initialize TreeTableView nodes for today
		 */
		homeTreeID.setCellValueFactory(new Callback<CellDataFeatures<TaskWrapper, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<TaskWrapper, String> task) {
				return new ReadOnlyObjectWrapper(task.getValue().getValue().getId());
			}
		});

		homeTreeTask.setCellValueFactory(new Callback<CellDataFeatures<TaskWrapper, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<TaskWrapper, String> task) {
				return new ReadOnlyObjectWrapper(task.getValue().getValue().getTaskName());
			}
		});

		homeTreeDate.setCellValueFactory(new Callback<CellDataFeatures<TaskWrapper, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<TaskWrapper, String> task) {
				return new ReadOnlyObjectWrapper(task.getValue().getValue().getDate());
			}
		});

		homeTreePriority.setCellValueFactory(new Callback<CellDataFeatures<TaskWrapper, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<TaskWrapper, String> task) {
				return new ReadOnlyObjectWrapper(task.getValue().getValue().getPriority());
			}
		});

		homeTree.setRoot(rootNode);
		homeTree.setShowRoot(false);


		/*
		 * For upcoming tab,
		 * initialize TreeTableView nodes for today, tomorrow and this week
		 */
		upTreeID.setCellValueFactory(new Callback<CellDataFeatures<TaskWrapper, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<TaskWrapper, String> task) {
				return new ReadOnlyObjectWrapper(task.getValue().getValue().getId());
			}
		});

		upTreeTask.setCellValueFactory(new Callback<CellDataFeatures<TaskWrapper, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<TaskWrapper, String> task) {
				return new ReadOnlyObjectWrapper(task.getValue().getValue().getTaskName());
			}
		});

		upTreeDate.setCellValueFactory(new Callback<CellDataFeatures<TaskWrapper, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<TaskWrapper, String> task) {
				return new ReadOnlyObjectWrapper(task.getValue().getValue().getDate());
			}
		});

		upTreePriority.setCellValueFactory(new Callback<CellDataFeatures<TaskWrapper, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<TaskWrapper, String> task) {
				return new ReadOnlyObjectWrapper(task.getValue().getValue().getPriority());
			}
		});

		upTabTree.setRoot(rootNode);
		upTabTree.setShowRoot(false);
	}


}
