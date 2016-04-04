import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import cs2103_w09_1j.esther.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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

	@FXML
	private TabPane tabSum;
	
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
	private TableView<TaskWrapper> floatingContent;

	@FXML
	private TableColumn<TaskWrapper, String> FTID;

	@FXML
	private TableColumn<TaskWrapper, String> FTTask;

	@FXML
	private TableColumn<TaskWrapper, String> FTDate;

	@FXML
	private TableColumn<TaskWrapper, String> FTPriority;


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
	private TableView<TaskWrapper> completedContent;

	@FXML
	private TableColumn<TaskWrapper, String> CTID;

	@FXML
	private TableColumn<TaskWrapper, String> CTTask;

	@FXML
	private TableColumn<TaskWrapper, String> CTDate;

	@FXML
	private TableColumn<TaskWrapper, String> CTPriority;

	/*
	 * This section is for reference to 
	 * All Tab content
	 */
	@FXML
	private Tab allTab;

	@FXML
	private TableView<TaskWrapper> allContent;

	@FXML
	private TableColumn<TaskWrapper, String> ATID;

	@FXML
	private TableColumn<TaskWrapper, String> ATTask;

	@FXML
	private TableColumn<TaskWrapper, String> ATDate;

	@FXML
	private TableColumn<TaskWrapper, String> ATPriority;

	// handle user input
	@FXML
    private TextField input;

	@FXML
	private Label commandLog;
	
	@FXML
	void ENTER(KeyEvent event) throws Exception {
		if (event.getCode() == KeyCode.ENTER) {
			String userInput = input.getText();
			System.out.println(userInput);

			String logicOutput =logic.executeCommand(userInput); 

			String command = res.getCommandType();
			if (command.equalsIgnoreCase("search")) {
				Tab searchTab = new Tab();
				searchTab.setText("Search");
				tabSum.getTabs().add(searchTab);
				
				//searchTab
				
			} else if (command.equalsIgnoreCase("help")) {
				
			}
			
			commandLog.setText(logicOutput);
			input.clear();
		}
		
	}	
	
	// initialize logic
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
		//res = new UIResult();
		initializeTabs(res);


	}

	private void initializeTabs(UIResult res) {
		initializeOverdueList(res);
		initializeUpcomingList(res);
		initializeCompletedContent(res);
		initializeFloatingContent(res);
		initializeAllContent(res);
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
		todayNode.setExpanded(true);
		tomorrowNode.setExpanded(true);
		weekNode.setExpanded(true);

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

	private void initializeCompletedContent(UIResult res) {
		ArrayList<Task> completedBuffer = res.getCompletedBuffer();

		ArrayList<TaskWrapper> completedWrapper = new ArrayList<TaskWrapper>();
		for (int i = 0; i < completedBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(completedBuffer.get(i));
			completedWrapper.add(tw);
		}

		ObservableList<TaskWrapper> cList = (ObservableList) FXCollections.observableArrayList(completedWrapper);

		CTID.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("id"));
		CTTask.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("taskName"));
		CTDate.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("date"));
		CTPriority.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("priority"));

		completedContent.setItems(cList);
	}
	
	private void initializeFloatingContent(UIResult res) {
		ArrayList<Task> floatingBuffer = res.getFloatingBuffer();

		ArrayList<TaskWrapper> floatingWrapper = new ArrayList<TaskWrapper>();
		for (int i = 0; i < floatingBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(floatingBuffer.get(i));
			floatingWrapper.add(tw);
		}

		ObservableList<TaskWrapper> fList = (ObservableList) FXCollections.observableArrayList(floatingWrapper);

		FTID.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("id"));
		FTTask.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("taskName"));
		FTDate.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("date"));
		FTPriority.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("priority"));

		floatingContent.setItems(fList);
	}
	
	private void initializeAllContent(UIResult res) {
		ArrayList<Task> overdueBuffer = res.getOverdueBuffer();
		ArrayList<Task> todayBuffer = res.getTodayBuffer();
		ArrayList<Task> tomorrowBuffer = res.getTomorrowBuffer();
		ArrayList<Task> weekBuffer = res.getWeekBuffer();
		ArrayList<Task> floatingBuffer = res.getFloatingBuffer();
		ArrayList<Task> allBuffer = new ArrayList<Task>();
		allBuffer.addAll(overdueBuffer);
		allBuffer.addAll(todayBuffer);
		allBuffer.addAll(tomorrowBuffer);
		allBuffer.addAll(weekBuffer);
		allBuffer.addAll(floatingBuffer);

		ArrayList<TaskWrapper> allWrapper = new ArrayList<TaskWrapper>();
		for (int i = 0; i < allBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(allBuffer.get(i));
			allWrapper.add(tw);
		}

		ObservableList<TaskWrapper> aList = (ObservableList) FXCollections.observableArrayList(allWrapper);

		ATID.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("id"));
		ATTask.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("taskName"));
		ATDate.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("date"));
		ATPriority.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("priority"));

		allContent.setItems(aList);
	}

}
