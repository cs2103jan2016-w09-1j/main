import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import cs2103_w09_1j.esther.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UiMainController implements Initializable {

	private Logic logic;

	private static UIResult res = new UIResult();

	private BooleanProperty ctrlPressed = new SimpleBooleanProperty(false);
	private BooleanProperty leftPressed = new SimpleBooleanProperty(false);
	private BooleanProperty rightPressed = new SimpleBooleanProperty(false);
	private BooleanBinding switchLeft = ctrlPressed.and(leftPressed);
	private BooleanBinding switchRight = ctrlPressed.and(rightPressed);

	public static UIResult getRes() {
		return res;
	}

	public static void setRes(UIResult res) {
		UiMainController.res = res;
	}

	private SingleSelectionModel<Tab> selectionModel;
	private ObservableList<TaskWrapper> sList, cList, odList, fList, aList, tdList, tmrList, wkList;

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
	void overdueClick(MouseEvent event) {

		selectionModel.select(1);
	}

	@FXML
	void upcomingClick(MouseEvent event) {
		selectionModel.select(2);
	}

	@FXML
	void EnterAndNavigate(KeyEvent event) throws Exception {
		if (event.getCode() == KeyCode.CONTROL) {
			ctrlPressed.set(true);
		} else if (event.getCode() == KeyCode.RIGHT) {
			rightPressed.set(true);
		} else if (event.getCode() == KeyCode.LEFT) {
			leftPressed.set(true);
		} else if (event.getCode() == KeyCode.ENTER) {
			String userInput = input.getText();
			System.out.println(userInput);

			String logicOutput =logic.executeCommand(userInput); 

			String command = res.getCommandType();
			if (command.equalsIgnoreCase("search")) {

				displaySearch();
				commandLog.setText(logicOutput);
				initializeTabs(res);

			} else if (command.equalsIgnoreCase("help")) {

				commandLog.setText("Help Menu Opened!");
				UiSecondController helpController = createSecondWindow(commandLog, "Help");
				helpController.setResult(res.getMessage());
				initializeTabs(res);

			} else {
				commandLog.setText(logicOutput);
				initializeTabs(res);
				if (command.equalsIgnoreCase("sort")) {

				} else {
					int[] index = res.getIndex();
					int line = index[1];
					
					switch(index[0]) {
					
					case(0):
						//overdue
						if (selectionModel.isSelected(1)) {
							overdue.getSelectionModel().select(line);
						} else {
							selectionModel.select(0);
							overdueList.getSelectionModel().select(line);
						}
						break;

					case(1):
						//today
						if (selectionModel.isSelected(2)) {
							upTabTree.getSelectionModel().select(line+1);
						} else {
							selectionModel.select(0);
							homeTree.getSelectionModel().select(line+1);
						}
						break;

					case(2):
						//tomorrow
						if (selectionModel.isSelected(2)) {
							upTabTree.getSelectionModel().select(res.getTodayBuffer().size() 
									+ line + 2);
						} else {
							selectionModel.select(0);
							homeTree.getSelectionModel().select(res.getTodayBuffer().size() 
									+ line + 2);
						}
						break;

					case(3):
						//week
						if (selectionModel.isSelected(2)) {
							upTabTree.getSelectionModel().select(res.getTodayBuffer().size()
									+ res.getTomorrowBuffer().size() + line + 3);
						} else {
							selectionModel.select(0);
							homeTree.getSelectionModel().select(res.getTodayBuffer().size()
									+ res.getTomorrowBuffer().size() + line + 3);
						}
						break;

					case(4):
						//all - nothing at all, should not even use this
						selectionModel.select(5);
						allContent.getSelectionModel().select(line);
						System.out.println("the line is " + line);
						break;

					case(5):
						//floating
						selectionModel.select(3);
						floatingContent.getSelectionModel().select(line);
						break;
						
					case(6):
						//completed
						selectionModel.select(4);
						completedContent.getSelectionModel().select(line);
						break;
						
					default:
						//default
						break;
					}
				}

			}

			input.clear();
		}

	}	

	private UiSecondController createSecondWindow(Label lb, String title) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("secondWindow.fxml"));
		Pane secondWindow = null;
		try {
			secondWindow = (Pane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		UiSecondController second = loader.getController();
		second.setMainController(this);

		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(lb.getScene().getWindow());
		Scene scene = new Scene(secondWindow);
		scene.getStylesheets().add("cs2103_w09_1j/esther/Resources/UI.css");
		stage.setScene(scene);
		stage.setTitle(title);
		stage.show();
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					stage.close();
				}
			}
		});

		return second;
	}

	private void displaySearch() {
		Tab searchTab = new Tab();

		searchTab.setText("Search");
		tabSum.getTabs().add(searchTab);

		AnchorPane ap = new AnchorPane();
		ArrayList<Task> searchBuffer = res.getSearchBuffer();

		ArrayList<TaskWrapper> searchWrapper = new ArrayList<TaskWrapper>();
		for (int i = 0; i < searchBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(searchBuffer.get(i));
			searchWrapper.add(tw);
		}

		sList = (ObservableList) FXCollections.observableArrayList(searchWrapper);

		TableView<TaskWrapper> searchTable = new TableView<TaskWrapper>();

		TableColumn<TaskWrapper, String> STID = new TableColumn<TaskWrapper, String>("ID");
		TableColumn<TaskWrapper, String> STTask = new TableColumn<TaskWrapper, String>("Task");
		TableColumn<TaskWrapper, String> STDate = new TableColumn<TaskWrapper, String>("Date & Time");
		TableColumn<TaskWrapper, String> STPriority = new TableColumn<TaskWrapper, String>("Priority");

		STID.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("id"));
		STTask.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("taskName"));
		STDate.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("date"));
		STPriority.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("priority"));

		searchTable.getColumns().addAll(STID, STTask, STDate, STPriority);

		searchTable.setItems(sList);

		ap.getChildren().add(searchTable);
		AnchorPane.setBottomAnchor(searchTable, 14.0);
		AnchorPane.setRightAnchor(searchTable, 14.0);
		AnchorPane.setTopAnchor(searchTable, 14.0);
		AnchorPane.setLeftAnchor(searchTable, 14.0);

		searchTab.setContent(ap);

		selectionModel.select(searchTab);

	}

	// initialize logic and other tabs
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			logic = new Logic();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		selectionModel = tabSum.getSelectionModel();

		input.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.CONTROL) {
					ctrlPressed.set(false);
				} else if (arg0.getCode() == KeyCode.RIGHT) {
					rightPressed.set(false);
				} else if (arg0.getCode() == KeyCode.LEFT) {
					leftPressed.set(false);
				}

			}

		});

		switchRight.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (selectionModel.getSelectedIndex() != tabSum.getTabs().size() - 1) {
					selectionModel.select(selectionModel.getSelectedIndex() + 1);
				}
			}

		});

		switchLeft.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (selectionModel.getSelectedIndex() != 0) {
					selectionModel.select(selectionModel.getSelectedIndex() - 1);
				}
			}

		});

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

		odList = (ObservableList) FXCollections.observableArrayList(overdueWrapper);

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

		// initialize all buffers of wrappers for listening purposes
		ArrayList<TaskWrapper> todayWrapper = new ArrayList<TaskWrapper>();
		ArrayList<TaskWrapper> tmrWrapper = new ArrayList<TaskWrapper>();
		ArrayList<TaskWrapper> weekWrapper = new ArrayList<TaskWrapper>();

		TreeItem<TaskWrapper> rootNode = new TreeItem<TaskWrapper>(new TaskWrapper("This is the root node for upcoming tab"));
		TreeItem<TaskWrapper> todayNode = new TreeItem<TaskWrapper>(new TaskWrapper("Today"));
		TreeItem<TaskWrapper> tomorrowNode = new TreeItem<TaskWrapper>(new TaskWrapper("Tomorrow"));
		TreeItem<TaskWrapper> weekNode = new TreeItem<TaskWrapper>(new TaskWrapper("One Week"));

		int i;
		for (i = 0; i < todayBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(todayBuffer.get(i));
			todayNode.getChildren().add(new TreeItem<TaskWrapper>(tw));
		}

		for (i = 0; i < tmrBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(tmrBuffer.get(i));
			tomorrowNode.getChildren().add(new TreeItem<TaskWrapper>(tw));
		}

		for (i = 0; i < weekBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(weekBuffer.get(i));
			weekNode.getChildren().add(new TreeItem<TaskWrapper>(tw));
		}

		tdList = (ObservableList) FXCollections.observableArrayList(todayWrapper);

		tmrList = (ObservableList) FXCollections.observableArrayList(tmrWrapper);

		wkList = (ObservableList) FXCollections.observableArrayList(weekWrapper);

		rootNode.getChildren().addAll(todayNode, tomorrowNode, weekNode);

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

		cList = (ObservableList) FXCollections.observableArrayList(completedWrapper);

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

		fList = (ObservableList) FXCollections.observableArrayList(floatingWrapper);

		FTID.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("id"));
		FTTask.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("taskName"));
		FTDate.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("date"));
		FTPriority.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("priority"));

		floatingContent.setItems(fList);
	}

	private void initializeAllContent(UIResult res) {
		ArrayList<Task> allBuffer = res.getAllTaskBuffer();

		ArrayList<TaskWrapper> allWrapper = new ArrayList<TaskWrapper>();
		for (int i = 0; i < allBuffer.size(); i++) {
			TaskWrapper tw = new TaskWrapper(allBuffer.get(i));
			allWrapper.add(tw);
		}

		aList = (ObservableList) FXCollections.observableArrayList(allWrapper);

		ATID.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("id"));
		ATTask.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("taskName"));
		ATDate.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("date"));
		ATPriority.setCellValueFactory(new PropertyValueFactory<TaskWrapper,String>("priority"));

		allContent.setItems(aList);
	}

}
