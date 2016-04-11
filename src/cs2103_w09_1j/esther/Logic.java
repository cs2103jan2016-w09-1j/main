package cs2103_w09_1j.esther;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import cs2103_w09_1j.esther.Command.CommandKey;
import cs2103_w09_1j.esther.Task.TaskField;

/**
 * The <code>Logic</code> handles all operations as requested by the user.
 * A list of operations that are supported are:
 * <br>
 * 1. CRUD
 * <br>
 * 2. Sort
 * <br>
 * 3. Search
 * <br>
 * 4. Set save file-path
 * <br>
 * <br>
 * Generally, when these operations succeed or fail,
 * relevant messages confirming the statuses of these
 * operations shall be passed to the UI via the Parser,
 * which will then be shown to the user.
 * <br>
 * <br>
 * For all operations, a <code>UIResult</code> is passed to UI for
 * display to the user and a message indicating the status of the
 * user operation is displayed to the user through the UI.
 * 
 * @@author A0129660A
 */
class Logic {

	private static final String EMPTY_STATE = "Empty";
	private static final String DEFAULT_TASKS_SORT_ORDER = Task.TaskField.ENDDATE.getTaskKeyName();
	private static final String SEARCH_BEFORE = "before";
	private static final String SEARCH_ON = "on";
	private static final String SEARCH_AFTER = "after";
	private static final String INVALID_COMMAND = "Invalid";
	private static final String INITIALIZE_COMMAND = "Initialize";
	
	private static final int TASK_LIST_POSITION = 0;
	private static final int TASK_ITEM_POSITION = 1;
	private static final int NOT_APPLICABLE_INDEX = -1;
	private static final int NOT_FOUND_INDEX = -1;
	private static final int DUPLICATE_TASK_INDEX = -2;
	private static final int NUM_TASK_BUFFERS = 7;
	
	// To turn off logging, set this boolean to false
	private static boolean toDebug = false;
	
	private Parser _parser;
	private Storage _storage;
	private ArrayList<ArrayList<Task>> _taskDisplayLists;
	private ArrayList<Task> _fullTaskList;
	private ArrayList<Task> _searchList;
	private ArrayList<Task> _temporarySortList;
	private Stack<State> _undoStack;
	private Config _config;
	private Date _today;
	private static Logger logger;

	
	// =========================================================================================== //
	// =========================== HIGH-LEVEL IMPLEMENTATION OF LOGIC ============================ //
	// These methods are the highest-level implementation of the core structure of Logic.          //
	// The most important methods falling into this category are: 								   //
	// 1. Constructor, Logic()																	   //
	// 2. public String executeCommand(String input)											   //
	// 3. private String executeCommand(Command command)										   //
	//																							   //
	// As new functions are added, or as existing inner functions are extended,					   //
	// the method executeCommand(Command command) will be subject to more and more changes.		   //
	// =========================================================================================== //
	
	/**
	 * Constructs a Logic component instance.
	 */
	public Logic() throws ParseException, IOException {
		initializeLogger();
		initializeStorageAndConfig();
		initializeParser();
		initializeLogicSystemVariables();
		attachShutdownHandler();
	}
	
	/**
	 * An overloaded method that operates on an input and passes the call
	 * to the actual handler method. If a null Command object is detected,
	 * control is NOT passed to the actual handler method.
	 * 
	 * @see    Logic#executeCommand(Command)
	 * @param  userInput the input that the user entered
	 * @return a message indicating the status of the operation carried out
	 */
	public String executeCommand(String userInput) {		
		logger.log(Level.INFO, "Parsing user input into Command object for execution.");
		int[] indices = createDefaultIndices();		
		try {			
			Command command = _parser.acceptUserInput(userInput);			
			if (isNullCommand(command)) {				
				logger.log(Level.WARNING, "Error from Parser: encountered null Command object.");
				setUiTaskDisplays(INVALID_COMMAND, indices);
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.INVALID_COMMAND;
				return Status.getMessage(null, null, null);				
			}			
			return executeCommand(command);
		} catch (InvalidInputException iie) {			
			logger.log(Level.WARNING, "Invalid input supplied by user.");
			setUiTaskDisplays("Invalid", indices);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.INVALID_COMMAND;	
			return iie.getMessage();			
		}
	}
	
	/**
	 * This method acts as the main handler for all user
	 * operations. This handler will attempt to execute a
	 * command and informs the user of the status of the
	 * operation that is carried out.
	 * 
	 * This method assumes that the Command object passed
	 * in is not null.
	 * 
	 * @param  command	the Command object containing all required information
	 * @return			a message indicating the status of the operation carried out
	 * 
	 */
	protected String executeCommand(Command command) {
		String commandName = command.getCommand();
		CommandKey commandType = CommandKey.get(commandName);
		logger.log(Level.INFO, "Executing on Command object.");
		String statusMessage;
		
		switch (commandType) {
			case ADD : 
				statusMessage = addTask(command);
				break;
				
			case DELETE :
				statusMessage = removeTask(command);
				break;
				
			case UPDATE :
				statusMessage = updateTask(command);
				break;
				
			case COMPLETE :
				statusMessage = completeTask(command);
				break;
				
			case SORT :
				statusMessage = sortFile(command);
				break;
				
			case SEARCH : 
				statusMessage = searchFile(command);
				break;
				
			case UNDO :
				statusMessage = undo(command);
				break;
				
			case SET :
			  	statusMessage = setSaveFilePath(command);
			  	break;
				
			case HELP :
				statusMessage = help(command);
				break;
				
			default :
				/* 
				 * This state is unreachable because the Parser would have guarded against
				 *  weird Command objects.
				 */
				throw new Error("Impossible state.");
		}
		return statusMessage;
	}
	
	// =========================== HIGH-LEVEL IMPLEMENTATION OF LOGIC ============================ //
	// =========================================================================================== //
	
	

	// =========================================================================================== //
	// ================================ SYSTEM METHODS FOR LOGIC ================================= //
	// These methods are used to maintain the inner workings of Logic and are usually			   //
	// unrelated to user-related operations.													   //
	// =========================================================================================== //
	
	/**
	 * Retrieves the internal memory that is used by Logic.
	 * 
	 * @return the internal memory representation of the
	 * 		   contents stored in the text file.
	 */
	protected ArrayList<Task> getInternalStorage() {
		_fullTaskList = new ArrayList<Task>();
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			_fullTaskList.addAll(_taskDisplayLists.get(i));
		}
		return _fullTaskList;
	}
	
	/**
	 * Retrieves the list of overdue tasks stored in Logic.
	 * 
	 * @return the list of overdue tasks
	 */
	protected ArrayList<Task> getOverdueBuffer() {
		return _taskDisplayLists.get(Task.OVERDUE_TASK_INDEX);
	}
	
	/**
	 * Retrieves the list of today's tasks stored in Logic.
	 * 
	 * @return the list of today's tasks
	 */
	protected ArrayList<Task> getTodayBuffer() {
		return _taskDisplayLists.get(Task.TODAY_TASK_INDEX);
	}
	
	/**
	 * Retrieves the list of tomorrow's tasks stored in Logic.
	 * 
	 * @return the list of tomorrow's tasks
	 */
	protected ArrayList<Task> getTomorrowBuffer() {
		return _taskDisplayLists.get(Task.TOMORROW_TASK_INDEX);
	}
	
	/**
	 * Retrieves the list of this week's tasks stored in Logic.
	 * 
	 * @return the list of this week's tasks
	 */
	protected ArrayList<Task> getThisWeekBuffer() {
		return _taskDisplayLists.get(Task.THIS_WEEK_TASK_INDEX);
	}
	
	/**
	 * Retrieves the list of other tasks stored in Logic, excluding floating or completed tasks.
	 * 
	 * @return the list of other tasks, excluding floating or completed tasks
	 */
	protected ArrayList<Task> getRemainingBuffer() {
		return _taskDisplayLists.get(Task.UNCODED_TASK_INDEX);
	}
	
	/**
	 * Retrieves the list of floating tasks stored in Logic.
	 * 
	 * @return the list of floating tasks.
	 */
	protected ArrayList<Task> getFloatingBuffer() {
		return _taskDisplayLists.get(Task.FLOATING_TASK_INDEX);
	}
	
	/**
	 * Retrieves the list of completed tasks stored in Logic.
	 * 
	 * @return the list of completed tasks.
	 */
	protected ArrayList<Task> getCompletedBuffer() {
		return _taskDisplayLists.get(Task.COMPLETED_TASK_INDEX);
	}
	
	/**
	 * Retrieves the list of all tasks stored in Logic, excluding completed tasks.
	 * 
	 * @return the list of all tasks, excluding completed tasks.
	 */
	protected ArrayList<Task> getAllTasks() {
		ArrayList<Task> allTasks = new ArrayList<Task>();
		allTasks.addAll(getOverdueBuffer());
		allTasks.addAll(getTodayBuffer());
		allTasks.addAll(getTomorrowBuffer());
		allTasks.addAll(getThisWeekBuffer());
		allTasks.addAll(getRemainingBuffer());
		allTasks.addAll(getFloatingBuffer());
		return allTasks;
	}
	
	/**
	 * Retrieves the temporary list used for sorting in Logic.
	 * 
	 * @return the temporary list used for sorting
	 */
	protected ArrayList<Task> getTemporarySortList() {
		return _temporarySortList;
	}
	
	/**
	 * Retrieves the list representing the search results of a search query.
	 * 
	 * @return the list of Tasks representing the search result
	 */
	protected ArrayList<Task> getSearchResults() {
		return _searchList;
	}
	
	/**
	 * Updates the view of the <code>UserInterface</code> that is shown to the user.
	 * 
	 * @param commandType	the command that was executed
	 * @param indices		a tuple representing [UI_tab_index, task_item_index]
	 */
	public void setUiTaskDisplays(String commandType, int[] indices) {
		UIResult displayResult = createDisplayResult(commandType, indices);
		UiMainController.setRes(displayResult);
	}
	
	/**
	 * Creates a <code>UIResult</code> object representing the view that the user would see
	 * in the UI.
	 * 
	 * @param commandType	the type of user operation to be carried out
	 * @param indices		the indices used for showing focus to affected Task entries in <code>UIResult</code>
	 * @return				a <code>UIResult</code> that will be used by the UI
	 */
	public UIResult createDisplayResult(String commandType, int[] indices) {
		UIResult result = setUiResultWithIndividualLists();
		if (isSortCommand(commandType)) {
			Collections.sort(_temporarySortList);
			result.setAllTaskBuffer(getTemporarySortList());
		} else {
			if (isUndoCommand(commandType)) {
				result.setAllTaskBuffer(getTemporarySortList());
			} else {
				ArrayList<Task> allTasks = getAllTasks();
				result.setAllTaskBuffer(allTasks);
			}
		}
		result.setCommandType(commandType);
		result.setIndex(indices);
		if (isSearchCommand(commandType)) {
			result.setSearchBuffer(_searchList);
		}
		return result;
	}

	/**
	 * Initializes a <code>UIResult</code> object with the lists of tasks from each category.
	 * 
	 * @return an initialized <code>UIResult</code> object
	 */
	private UIResult setUiResultWithIndividualLists() {
		UIResult result = new UIResult();
		result.setOverdueBuffer(_taskDisplayLists.get(Task.OVERDUE_TASK_INDEX));
		result.setTodayBuffer(_taskDisplayLists.get(Task.TODAY_TASK_INDEX));
		result.setTomorrowBuffer(_taskDisplayLists.get(Task.TOMORROW_TASK_INDEX));
		result.setWeekBuffer(_taskDisplayLists.get(Task.THIS_WEEK_TASK_INDEX));
		result.setFloatingBuffer(_taskDisplayLists.get(Task.FLOATING_TASK_INDEX));
		result.setCompletedBuffer(_taskDisplayLists.get(Task.COMPLETED_TASK_INDEX));
		return result;
	}
	
	/**
	 * Initializes a system logger. Used for testing purposes only.
	 * 
	 * To turn logging on or off, simply modify the <code>toDebug</code> boolean attribute.
	 */
	private void initializeLogger() {
		try {
			logger = Logger.getLogger("estherLogger");
			if (toDebug) {
				logger.setLevel(Level.SEVERE);
			} else {
				logger.setLevel(Level.OFF);
			}
			logger.log(Level.CONFIG, "Initializing logger.");
		} catch (SecurityException se) {
			logger.log(Level.SEVERE, "Not granted permission for logging.", se);
		}
	}
	
	/**
	 * Initializes Storage and Config systems in Logic.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	private void initializeStorageAndConfig() throws ParseException, IOException {
		_storage = new Storage();
		logger.log(Level.CONFIG, "Initializing Storage.");
		assert _storage != null;
		_config = _storage.getConfig();
		logger.log(Level.CONFIG, "Initializing Config.");
		assert _config != null;
		Task.setGlobalId(_config.getReferenceID());
	}

	/**
	 * Initializes Parser system in Logic.
	 */
	private void initializeParser() {
		logger.log(Level.CONFIG, "Initializing Parser.");
		HashMap<String, String> fieldNameAliases = _config.getFieldNameAliases();
		_parser = new Parser(fieldNameAliases);
		assert _parser != null;
	}
	
	/**
	 * Initializes internal system variables in Logic.
	 * 
	 * @throws IOException 
	 * @throws ParseException
	 */
	private void initializeLogicSystemVariables() throws ParseException, IOException {
		initializeBuffers();
		logger.log(Level.CONFIG, "Reading tasks into inner memory upon initialization.");
		updateInternalStorage();
		_undoStack = new Stack<State>();
		int indices[] = createDefaultIndices();
		setUiTaskDisplays(INITIALIZE_COMMAND, indices);
	}
	
	/**
	 * Sets up all inner buffers needed for UI to show custom task views.
	 */
	private void initializeBuffers() {
		_taskDisplayLists = new ArrayList<ArrayList<Task>>(NUM_TASK_BUFFERS);
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			_taskDisplayLists.add(new ArrayList<Task>());
		}
	}
	
	/**
	 * Attaches a shutdown-event handler to perform necessary system updates when ESTHER is shut down.
	 */
	private void attachShutdownHandler() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	            try {
	            	logger.log(Level.INFO, "Attaching shut-down handler.");
	            	_config.setReferenceID(Task.getGlobalId());
	            	_storage.setConfig(_config);
	            } catch (IOException ioe) {
	            	logger.log(Level.SEVERE, "Cannot attach shut-down handler.", ioe);
	            }
	        }   
	    });
	}
	
	/**
	 * Updates the internal memory of the Logic to account for any changes done to the text file.
	 */
	public void updateInternalStorage() {
		logger.log(Level.INFO, "Retrieving tasks list from Storage.");
		try {
			_fullTaskList = _storage.readSaveFile();
			assert _fullTaskList != null;
			filterTasksToLists(DEFAULT_TASKS_SORT_ORDER, true, true);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Cannot read from save file in Storage.", e);
			System.exit(1);
		}
	}
	
	/**
	 * Empties the text file of any contents. Only used for internal testing.
	 */
	public void flushInternalStorage() {
		try {
			_storage.flushSaveFile();
		} catch (IOException ioe) {
			//ioe.printStackTrace();
		}
		_fullTaskList = new ArrayList<Task>();
		initializeBuffers();
	}
	
	/**
	 * Update Logic's list of tasks into text file.
	 * 
	 * @throws IOException
	 */
	private void updateTextFile() throws IOException {
		_fullTaskList = new ArrayList<Task>();
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			_fullTaskList.addAll(_taskDisplayLists.get(i));
		}
		_storage.writeSaveFile(_fullTaskList);
	}
	
	/**
	 * Creates a default tuple of indices to be used in <code>UIResult</code> object.
	 * 
	 * @return a tuple of 2 indices representing [UI_tab_index, task_item_index], set to [-1, -1]
	 */
	private int[] createDefaultIndices() {		
		int indices[] = {NOT_APPLICABLE_INDEX, NOT_APPLICABLE_INDEX};
		return indices;		
	}
	
	/**
	 * Updates the undo stack whenever a user operation is carried out.
	 * 
	 * @param state a <code>State</code> object representing the previous program state
	 */
	private void updateUndoStack(State state) {
		_undoStack.push(state);
	}
	
	/**
	 * Separates tasks to their respective categories for proper sorting.
	 */
	private void filterTasksToLists(String sortOrder, boolean toSort, boolean toInitialize) {
		if (toInitialize) {
			placeTasksIntoCategories();
		}
		
		if (toSort) {
			if (isRecognizedSortOrder(sortOrder)) {
				Task.setSortCriterion(sortOrder);
				sortAllLists();
			} else {
				// do nothing
			}
		}
	}

	/**
	 * Sorts each individual category list.
	 */
	private void sortAllLists() {
		_temporarySortList = new ArrayList<Task>();
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			if (i != Task.COMPLETED_TASK_INDEX) {
				_temporarySortList.addAll(_taskDisplayLists.get(i));
				Collections.sort(_taskDisplayLists.get(i));
			}
		}
	}

	/**
	 * Extracts each task and stores them in the respective category lists.
	 */
	private void placeTasksIntoCategories() {
		Iterator<Task> iter = _fullTaskList.iterator();
		_today = new Date();
		while (iter.hasNext()) {
			Task currentTask = iter.next();
			_taskDisplayLists.get(currentTask.getTaskCode(_today)).add(currentTask);
			iter.remove();
		}
		assert _fullTaskList.size() == 0;
	}

	
	// ================================ SYSTEM METHODS FOR LOGIC ================================= //
	// =========================================================================================== //
	
	
	
	// =========================================================================================== //
	// ============================ USER OPERATION METHODS FOR LOGIC ============================= //
	// These methods are used to carry out user operations in Logic.							   //
	// A method is always assigned to one user operation and for that particular method, it may    //
	// be composed of multiple lower-level methods that will handle different parts of that single //
	// user operation.																			   //
	//																							   //
	// Methods may be added or modified here, as desired by the developer. For your convenience,   //
	// the general idea to add/modify functions is stated as follows:							   //
	// 1. Create the private method first														   //
	// 2. Add your case statements for methods performing control flow							   //
	// 3. Update any status codes, where required												   //
	// =========================================================================================== //
	
	/**
	 * Adds the task to the text file.
	 * By default, all tasks are added to the end of the list
	 * of tasks.
	 * 
	 * NOTE: implementation can be extended to add the entry
	 * into a suitable position to maintain an ordering of tasks. 
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the add-task operation
	 * 
	 */
	private String addTask(Command command) {		
		logger.log(Level.INFO, "Adding a task.");
		createAndAddTaskToFile(command);
		return getOperationStatus(command);
	}
		
	/**
	 * Removes the task from the text file.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the delete-task operation
	 * 
	 */
	private String removeTask(Command command) {
		logger.log(Level.INFO, "Removing a task.");
		removeTaskAndUpdateFile(command);
		return getOperationStatus(command);
	}
	
	/**
	 * Updates a particular task entry in the text file.
	 * 
	 * @param task a Task object representation of the user's input
	 * @return 	   a message indicating the status of the update-task operation
	 * 
	 */
	private String updateTask(Command command) {
		logger.log(Level.INFO, "Logic", "Updating a task.");
		updateTaskInFile(command);
		return getOperationStatus(command);
	}

	/**
	 * Sets a task as completed.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the set-task-completed operation
	 * 
	 */
	private String completeTask(Command command) {
		logger.log(Level.INFO, "Completing a task.");
		completeTaskInFile(command);
		return getOperationStatus(command);
	}

	/**
	 * Sorts the list of tasks recorded in the text file.
	 * 
	 * @see 	Task#compareTo(Task)
	 * @return	a message indicating the status of the sort operation
	 */
	private String sortFile(Command command) {
		logger.log(Level.INFO, "Sorting tasks.");
		int indices[] = createDefaultIndices();
		State oldState = createPreviousState(command, indices);
		if (isSortedBefore()) {
			oldState.setAllTaskList(_temporarySortList);
		} else {
			oldState.setAllTaskList(getAllTasks());
		}
		sortAndUpdateFile(command);
		updateUndoStack(oldState);
		setUiTaskDisplays(command.getCommand(), indices);
		return getOperationStatus(command);
	}
	
	/**
	 * Searches for tasks based on the user-specified criterion.
	 * 
	 * @param command a Command object representing the user operation being carried out
	 * @return		  a message indicating the status of the search operation
	 */
	private String searchFile(Command command) {
		logger.log(Level.INFO, "Searching for tasks.");
		int indices[] = createDefaultIndices();
		getInternalStorage();
		_searchList = new ArrayList<Task>();
		String searchKeyword = command.getSpecificParameter(Task.TaskField.NAME.getTaskKeyName());
		String searchDateKeyword = command.getSpecificParameter(Task.TaskField.KEYWORD.getTaskKeyName());
		String dateString = command.getSpecificParameter(Task.TaskField.ENDDATE.getTaskKeyName());
		String timeString = command.getSpecificParameter(Task.TaskField.ENDTIME.getTaskKeyName());
		
		if (isInvalidSearch(searchKeyword, searchDateKeyword, dateString, timeString)) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SEARCH_INVALID;
			return getOperationStatus(command);
		} else {
			return searchForTasks(command, indices, searchKeyword, searchDateKeyword, dateString, timeString);
		}
	}	
	
	private String setSaveFilePath(Command command) {
		logger.log(Level.INFO, "Setting new save-path.");
		int indices[] = {-1, -1};
		State oldState = createPreviousState(command, indices);
		updateUndoStack(oldState);
		try {
			_config.setSavePath(command.getSpecificParameter(Task.TaskField.PATH.getTaskKeyName()));
			_storage.setConfig(_config);
			initializeBuffers();
			updateInternalStorage(); // refresh internal memory due to different file specified
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (InvalidPathException ipe) {
		 	Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SET_SAVEPATH;
		} catch (IOException ioe) {
		 	Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SET_SAVEPATH;
		}
		setUiTaskDisplays(command.getCommand(), indices);
		return getOperationStatus(command);
	}
	
	/**
	 * Undo one step back into the previous state of the program.
	 * 
	 * @param command a Command objecting representing the user operation to be carried out
	 * @return a message indicating status of the undo operation
	 * 
	 */
	private String undo(Command command) {
		if (_undoStack.size() == 0) {
			logger.log(Level.INFO, "User cannot undo any further.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.UNDO;
		} else {
			State previousState = _undoStack.pop();
			CommandKey commandType = CommandKey.get(previousState.getCommand());
			logger.log(Level.INFO, "Undoing a previous operation.");
			switch (commandType) {
				case ADD :
					undoAdd(previousState);
					break;
					
				case DELETE :
					undoDelete(previousState);
					break;
					
				case UPDATE :
					undoUpdate(previousState);
					break;
					
				case COMPLETE :
					undoCompleted(previousState);
					break;
					
				case SORT :
					undoSort(previousState);
					break;
					
				case SEARCH :
					break;
					
				case SET :
					undoSetSaveFilePath(previousState);
					break;
					
				case HELP :
					break;
			
				default :
					logger.log(Level.INFO, "Dummy State encountered.");
					Status._outcome = Status.Outcome.ERROR;
					Status._errorCode = Status.ErrorCode.SYSTEM;
					return getOperationStatus(command);
			}
		}
		return getOperationStatus(command);
	}
	
	/**
	 * Executes the help command.
	 * 
	 * @param command a Command objecting representing the user operation to be carried out
	 * @return a message indicating status of the undo operation
	 */
	private String help(Command command) {
		logger.log(Level.INFO, "Accessing helpsheet.");
		int indices[] = createDefaultIndices();
		setUiTaskDisplays(command.getCommand(), indices);
		Status._outcome = Status.Outcome.SUCCESS;
		return Status.getMessage(null, null, command.getCommand());
	}
	
	// ============================ USER OPERATION METHODS FOR LOGIC ============================= //
	// =========================================================================================== //
	
	
	
	// =========================================================================================== //
	// ====================== LOWER-LEVEL USER OPERATION METHODS FOR LOGIC ======================= //
	// These methods are lower-level methods used within user operation methods.				   //
	// During code re-factoring, you may place these lower-level methods here.					   //
	// =========================================================================================== //
		
	// ============================== [MAIN USER-OPERATION METHODS] ============================== //
	
	/**
	 * Creates and adds a task in both Logic and Storage.
	 * 
	 * @param command	a Command object representing the user operation being carried out
	 */
	private void createAndAddTaskToFile(Command command) {
		int indices[] = createDefaultIndices();
		State oldState = createPreviousState(command, indices);
		try {
			getInternalStorage();
			addTaskToInnerMemory(command, indices);
			updateTextFile();
			updateUndoStack(oldState);
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (ParseException pe) {
			logger.log(Level.SEVERE, "Add task: Inappropriate date format passed into Task.", pe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.ADD_INVALID_FORMAT;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Add task: Error in writing to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
		setUiTaskDisplays(command.getCommand(), indices);
	}

	/**
	 * Creates a task and adds it internally into Logic.
	 * 
	 * @param command	a Command object representing the user operation being carried out
	 * @param indices	the tuple of indices representing [UI_tab_index, task_item_index] for <code>UIResult</code>
	 * @throws 			ParseException
	 */
	private void addTaskToInnerMemory(Command command, int[] indices) throws ParseException {
		Task addedTask = new Task(command);
		_taskDisplayLists.get(addedTask.getTaskCode(_today)).add(addedTask);
		
		indices[TASK_LIST_POSITION] = addedTask.getTaskCode(_today);
		if (indices[TASK_LIST_POSITION] != Task.UNCODED_TASK_INDEX) {
			indices[TASK_ITEM_POSITION] = _taskDisplayLists.get(addedTask.getTaskCode(_today)).size() - 1;
		} else {
			indices[TASK_ITEM_POSITION] = _taskDisplayLists.get(Task.OVERDUE_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.TODAY_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.TOMORROW_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.THIS_WEEK_TASK_INDEX).size()
										  + _taskDisplayLists.get(addedTask.getTaskCode(_today)).size() - 1;
		}
	}
	
	/**
	 * Retrieves the index of a task as well as the index of the list that contains the task.<br>
	 * [ NOT_FOUND_INDEX, NOT_FOUND_INDEX ] is returned if the task cannot be found anywhere.<br>
	 * [ DUPLICATE_TASK_INDEX, DUPLICATE_TASK_INDEX ] is returned if tasks with duplicate names exist.<br>
	 * 
	 * @param command	the Command object representing the user operation being carried out
	 * @return 			the indices of a task, if a single task can be located;<br>
	 * 					[ NOT_FOUND_INDEX, NOT_FOUND_INDEX ] if no task can be found;<br>
	 * 		   			[ DUPLICATE_TASK_INDEX, DUPLICATE_TASK_INDEX ] if there are duplicate tasks
	 * 
	 */
	private int[] getTaskIndex(Command command) {
		int index[] = {NOT_FOUND_INDEX, NOT_FOUND_INDEX};
		boolean hasDuplicate = false;
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());
		String taskID = command.hasParameter(TaskField.ID.getTaskKeyName())
						? command.getSpecificParameter(TaskField.ID.getTaskKeyName())
						: String.valueOf(NOT_FOUND_INDEX);
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			for (int j = 0; j < _taskDisplayLists.get(i).size(); j++) {
				if (_taskDisplayLists.get(i).get(j).getName().equals(taskName) ||
					_taskDisplayLists.get(i).get(j).getId() == Integer.parseInt(taskID)) {
					if (index[1] != NOT_FOUND_INDEX) {
						hasDuplicate = true;
					}
					index[TASK_LIST_POSITION] = i;
					index[TASK_ITEM_POSITION] = j;
				}
			}
		}
		if (hasDuplicate) {
			index[TASK_LIST_POSITION] = DUPLICATE_TASK_INDEX;
			index[TASK_ITEM_POSITION] = DUPLICATE_TASK_INDEX;
		}
		return index;
	}
	
	/**
	 * Removes a task from both Logic's internal storage as well as from the text file.
	 * 
	 * @param command	a Command object representing the user operation being carried out
	 */
	private void removeTaskAndUpdateFile(Command command) {
		Task removed = null;
		int taskIndex[] = getTaskIndex(command);
		State oldState = createPreviousState(command, taskIndex);
		try {
			if (taskIndex[TASK_LIST_POSITION] != NOT_FOUND_INDEX &&
				taskIndex[TASK_LIST_POSITION] != DUPLICATE_TASK_INDEX) {
				removed = _taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).get(taskIndex[TASK_ITEM_POSITION]);
				_taskDisplayLists.get(taskIndex[0]).remove(removed);
				updateTextFile();
				updateUndoStack(oldState);
				Status._outcome = Status.Outcome.SUCCESS;
			} else if (taskIndex[TASK_LIST_POSITION] == DUPLICATE_TASK_INDEX) {
				logger.log(Level.WARNING, "Delete task: Duplicate task names found.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.DELETE_DUPLICATES_PRESENT;
			} else {
				logger.log(Level.WARNING, "Delete task: Task not found. Possible user-side error or no name/ID matching.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.DELETE_NOT_FOUND;
			}
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Delete task: cannot write to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
		setUiTaskDisplays(command.getCommand(), taskIndex);
	}
	
	/**
	 * Updates a task in both Logic's internal storage as well as from the text file. 
	 * 
	 * @param command	a Command object representing the user operation being carried out
	 */
	private void updateTaskInFile(Command command) {
		Task toUpdate = null;
		int taskIndex[] = getTaskIndex(command);
		State oldState = createPreviousState(command, taskIndex); 
		int indices[] = createDefaultIndices();
		
		try {
			if (taskIndex[TASK_LIST_POSITION] != NOT_FOUND_INDEX &&
				taskIndex[TASK_LIST_POSITION] != DUPLICATE_TASK_INDEX) {
				toUpdate = _taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).get(taskIndex[TASK_ITEM_POSITION]);
				Task copyOfOldTask = toUpdate.clone();
				boolean isUpdated = copyOfOldTask.updateTask(command);
				if (isUpdated) {
					updateInInnerMemory(taskIndex, indices, copyOfOldTask);
					updateTextFile();
					updateUndoStack(oldState);
					Status._outcome = Status.Outcome.SUCCESS;
				} else {
					logger.log(Level.WARNING, "Update task: Task could not be updated successfully.");
					Status._outcome = Status.Outcome.ERROR;
				}
			} else if (taskIndex[TASK_LIST_POSITION] == DUPLICATE_TASK_INDEX) {
				logger.log(Level.WARNING, "Update task: Duplicate task names found.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.UPDATE_DUPLICATES_PRESENT;
			} else {
				logger.log(Level.WARNING, "Update task: Task not found. Possible user-side error or no name/ID matching.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.UPDATE_NOT_FOUND;
			}
		} catch (ParseException pe) {
			logger.log(Level.SEVERE, "Update task: Inappropriate date formated passed into Task.", pe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.UPDATE_INVALID_FIELD;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Update task: cannot write to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
		setUiTaskDisplays(command.getCommand(), indices);
	}

	/**
	 * Updates a task internally in Logic.
	 * 
	 * @param taskIndex			the indices where the target Task may be found
	 * @param indices			the indices for <code>UIResult</code>
	 * @param copyOfOldTask		the copy of the target Task, that is now updated
	 */
	private void updateInInnerMemory(int[] taskIndex, int[] indices, Task copyOfOldTask) {
		_taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).remove(taskIndex[TASK_ITEM_POSITION]);
		_taskDisplayLists.get(copyOfOldTask.getTaskCode(_today)).add(copyOfOldTask);
		indices[TASK_LIST_POSITION] = copyOfOldTask.getTaskCode(_today);
		if (indices[TASK_LIST_POSITION] != Task.UNCODED_TASK_INDEX) {
			indices[TASK_ITEM_POSITION] = _taskDisplayLists.get(copyOfOldTask.getTaskCode(_today)).size() - 1;
		} else {
			indices[TASK_ITEM_POSITION] = _taskDisplayLists.get(Task.OVERDUE_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.TODAY_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.TOMORROW_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.THIS_WEEK_TASK_INDEX).size()
										  + _taskDisplayLists.get(copyOfOldTask.getTaskCode(_today)).size() - 1;
		}
	}
	
	/**
	 * Completes a task in both Logic's internal storage as well as the text file.
	 * 
	 * @param command	a Command object representing the user operation being carried out
	 */
	private void completeTaskInFile(Command command) {
		Task toUpdate = null;
		int taskIndex[] = getTaskIndex(command);
		State oldState = createPreviousState(command, taskIndex);
		int indices[] = createDefaultIndices();
		try {
			if (taskIndex[TASK_LIST_POSITION] != NOT_FOUND_INDEX &&
				taskIndex[TASK_LIST_POSITION] != DUPLICATE_TASK_INDEX) {
				toUpdate = _taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).get(taskIndex[TASK_ITEM_POSITION]);
				if (toUpdate.isCompleted()) {
					logger.log(Level.WARNING, "Complete task: Task is already completed.");
					Status._outcome = Status.Outcome.ERROR;
					Status._errorCode = Status.ErrorCode.COMPLETED_ALREADY_COMPLETED;
				}
				else {
					Task copyOfOldTask = toUpdate.clone();
					completeTaskInInnerMemory(taskIndex, indices, copyOfOldTask);
					updateTextFile();
					updateUndoStack(oldState);
					Status._outcome = Status.Outcome.SUCCESS;
				}
			} else if (taskIndex[TASK_LIST_POSITION] == DUPLICATE_TASK_INDEX) {
				logger.log(Level.WARNING, "Complete task: Duplicate task names present.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.COMPLETED_DUPLICATES_PRESENT;
			} else {
				logger.log(Level.WARNING, "Complete task: Task not found. Possible user-side error or no name/ID matching.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.COMPLETED_NOT_FOUND;
			}
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Complete task: cannot write to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
		setUiTaskDisplays(command.getCommand(), indices);
	}

	/**
	 * Marks a task as completed internally in Logic.
	 * 
	 * @param taskIndex			the indices of the target Task that may be found
	 * @param indices			the indices used for <code>UIResult</code>
	 * @param copyOfOldTask		a copy of the target Task, that is now marked as completed
	 */
	private void completeTaskInInnerMemory(int[] taskIndex, int[] indices, Task copyOfOldTask) {
		copyOfOldTask.setCompleted(true);
		_taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).remove(taskIndex[TASK_ITEM_POSITION]);
		_taskDisplayLists.get(copyOfOldTask.getTaskCode(_today)).add(copyOfOldTask);
		indices[TASK_LIST_POSITION] = copyOfOldTask.getTaskCode(_today);
		if (indices[TASK_LIST_POSITION] != Task.UNCODED_TASK_INDEX) {
			indices[TASK_ITEM_POSITION] = _taskDisplayLists.get(copyOfOldTask.getTaskCode(_today)).size() - 1;
		} else {
			indices[TASK_ITEM_POSITION] = _taskDisplayLists.get(Task.OVERDUE_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.TODAY_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.TOMORROW_TASK_INDEX).size()
										  + _taskDisplayLists.get(Task.THIS_WEEK_TASK_INDEX).size()
										  + _taskDisplayLists.get(copyOfOldTask.getTaskCode(_today)).size() - 1;
		}
	}
	
	/**
	 * Sorts entries in the Logic as well as the text file.
	 * 
	 * @param command	the Command object representing the user operation being carried out
	 */
	private void sortAndUpdateFile(Command command) {
		String sortOrder = command.getSpecificParameter(TaskField.SORT.getTaskKeyName());
		try {
			logger.log(Level.INFO, "Sorting all tasks by user-specified order.", sortOrder);
			if (isUnspecifiedSortOrder(sortOrder)) {
				sortOrder = DEFAULT_TASKS_SORT_ORDER;
			}
			filterTasksToLists(sortOrder, true, false);
			updateTextFile();
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Sort file: cannot write to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Searches for tasks based on criteria specified by the user.
	 * 
	 * @param command				the Command object representing the user operation being carried out
	 * @param indices				indices to be included into a <code>UIResult</code>
	 * @param searchKeyword			user-specified keyword(s) if he wishes to search by task name
	 * @param searchDateKeyword		a user-specified date-related keyword if he wishes to search by date
	 * @param dateString			a user-specified date
	 * @param timeString			a user-specified time
	 * @return						a message indicating the status of the search operation
	 */
	private String searchForTasks(Command command, int[] indices, String searchKeyword, String searchDateKeyword,
			String dateString, String timeString) {
		logger.log(Level.INFO, "Searching prerequisites met, now searching tasks in file.");
		if (isSearchByName(searchKeyword)) {
			searchTasksByName(searchKeyword);
		} else {
			Date referenceDate;
			try {
				referenceDate = setReferenceDate(command, searchDateKeyword, dateString, timeString);
			} catch (ParseException pe) {
				logger.log(Level.SEVERE, "Search date is not valid.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.SEARCH_INVALID;
				return getOperationStatus(command);
			}
			searchByDate(searchDateKeyword, referenceDate);
		}
		setUiTaskDisplays(command.getCommand(), indices);
		Status._outcome = Status.Outcome.SUCCESS;
		return getOperationStatus(command);
	}

	/**
	 * Searches tasks by date, as specified by the user.
	 * 
	 * @param searchDateKeyword	a reference keyword with respect to the Date specified ('before', 'on', 'after')
	 * @param referenceDate		the Date specified by the user
	 */
	private void searchByDate(String searchDateKeyword, Date referenceDate) {
		for (Task entry: _fullTaskList) {
			if (isSearchBeforeDate(searchDateKeyword)) {
				addToSearchResultsIfBeforeDate(referenceDate, entry);
			} else if (isSearchOnDate(searchDateKeyword)) {
				Date intervalStart = createDateStart(referenceDate);
				Date intervalEnd = createDateEnd(referenceDate);
				addToSearchResultIfOnDate(entry, intervalStart, intervalEnd);
			} else if (isSearchAfterDate(searchDateKeyword)){
				addToSearchResultIfAfterDate(referenceDate, entry);
			} else {
				
			}
		}
	}

	/**
	 * Adds target task to search results if the task is after a specified date.
	 * 
	 * @param referenceDate	a reference Date to compare the task date with
	 * @param entry			the Task to be checked
	 */
	private void addToSearchResultIfAfterDate(Date referenceDate, Task entry) {
		if (entry.isFloatingTask()) {
			
		} else if (entry.isEvent() && referenceDate.compareTo(entry.getStartDate()) <= 0) {
			_searchList.add(entry);
		} else if (referenceDate.compareTo(entry.getEndDate()) <= 0) {
			_searchList.add(entry);
		} else {
			
		}
	}

	/**
	 * Adds target task to search results if the task is on a specified date.
	 * 
	 * @param entry			the Task to be checked
	 * @param intervalStart	starting interval of the Date to compare the task date with
	 * @param intervalEnd	ending interval of the Date to compare the task date with
	 */
	private void addToSearchResultIfOnDate(Task entry, Date intervalStart, Date intervalEnd) {
		if (entry.isFloatingTask()) {
			
		} else if (entry.isEvent() && intervalStart.compareTo(entry.getStartDate()) <= 0 &&
			intervalEnd.compareTo(entry.getStartDate()) >= 0) {
			_searchList.add(entry);
		} else if (intervalStart.compareTo(entry.getEndDate()) <= 0
				   && intervalEnd.compareTo(entry.getEndDate()) >= 0) {
			_searchList.add(entry);
		} else {
			
		}
	}

	/**
	 * Sets the hours and minutes of the date to 23:59.
	 * 
	 * Used in searching for tasks due on a certain date.
	 * 
	 * @param referenceDate	the date used as the reference for a search query
	 * @return				the date with time set to 23:59
	 */
	private Date createDateEnd(Date referenceDate) {
		Date intervalEnd = (Date) referenceDate.clone();
		intervalEnd.setHours(23);
		intervalEnd.setMinutes(59);
		return intervalEnd;
	}

	/**
	 * Sets the hours and minutes of the date to 00:00.
	 * 
	 * Used in searching for tasks due on a certain date.
	 * 
	 * @param referenceDate	the date used as the reference for a search query
	 * @return				the date with time set to 00:00
	 */
	private Date createDateStart(Date referenceDate) {
		Date intervalStart = (Date) referenceDate.clone();
		intervalStart.setHours(0);
		intervalStart.setMinutes(0);
		return intervalStart;
	}

	/**
	 * Adds target task to search results if the task is before a specified date.
	 * 
	 * @param referenceDate	a reference Date to compare the task date with
	 * @param entry			the Task to be checked
	 */
	private void addToSearchResultsIfBeforeDate(Date referenceDate, Task entry) {
		if (entry.isFloatingTask()) {
			
		} else if (entry.isEvent() && referenceDate.compareTo(entry.getStartDate()) > 0) {
			_searchList.add(entry);
		} else if (referenceDate.compareTo(entry.getEndDate()) > 0) {
			_searchList.add(entry);
		} else {
			
		}
	}

	/**
	 * Sets reference date for searching tasks by date.
	 * 
	 * @param command				the Command object representing the user operation being carried out
	 * @param searchDateKeyword		the search-by-date keyword ('before', 'on', 'after')
	 * @param dateString			the String representing the date
	 * @param timeString			the String representing the time
	 * @return						a reference date for search operation
	 */
	private Date setReferenceDate(Command command, String searchDateKeyword, String dateString,
			String timeString) throws ParseException {
		Date referenceDate;
		if (isSearchBeforeDate(searchDateKeyword)) {
			referenceDate = Task.parseStringsToDateTime(dateString, timeString, new Date(), true);
		} else {
			referenceDate = Task.parseStringsToDateTime(dateString, timeString, new Date(), false);
		}
		return referenceDate;
	}

	/**
	 * Searches for tasks containing the specified keyword(s). 
	 * 
	 * @param searchKeyword	the keyword to look out for when searching tasks
	 */
	private void searchTasksByName(String searchKeyword) {
		String keywords[] = searchKeyword.toLowerCase().split(" ");
		for (Task entry: _fullTaskList) {
			String taskNameCopy = entry.getName();
			String taskNameLowerCase = taskNameCopy.toLowerCase();
			for (String word: keywords) {
				if (taskNameLowerCase.contains(word)) {
					if (!_searchList.contains(entry)) {
						_searchList.add(entry);
					} else {
						
					}
				}
			}
		}
	}
	
	/**
	 * Retrieves the status message of the user operation that is being carried out by Logic.
	 * 
	 * @param command	a Command object representing the user operation being carried out
	 * @return			the status message of the user operation being carried out
	 * 
	 */
	private String getOperationStatus(Command command) {
		if (isUndoCommand(command)) {
			return Status.getMessage(null, null, command.getCommand());
		} else {
			return Status.getMessage(command.getSpecificParameter(TaskField.NAME.getTaskKeyName()),
									 command.getSpecificParameter(TaskField.ID.getTaskKeyName()),
									 command.getCommand());
		}
	}
	
	
	// ============================== [UNDO-RELATED METHODS] ============================== //
	
	/**
	 * Stores the program state before a user operation was performed.
	 * 
	 * @param command		a Command object representing the user operation being carried out
	 * @param oldIndices	previous indices used by the UI
	 * @return				the previous state of the program
	 */
	private State createPreviousState(Command command, int[] oldIndices) {
		logger.log(Level.INFO, "Storing previous program memory state.");
		String commandName = command.getCommand();
		CommandKey commandType = CommandKey.get(commandName);
		State previous = null;
		switch (commandType) {
			case ADD :
				previous = new State(commandName);
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				previous.setAllTaskList(getAllTasks());
				break;

			case DELETE :
				previous = new State(commandName);
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				previous.setAllTaskList(getAllTasks());
				break;

			case UPDATE :
				previous = new State(commandName);
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				previous.setAllTaskList(getAllTasks());
				break;
				
			case COMPLETE :
				previous = new State(commandName);
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				previous.setAllTaskList(getAllTasks());
				break;

			case SORT :
				previous = new State(commandName);
				assert command.getSpecificParameter(TaskField.SORT.getTaskKeyName()) != null;
				previous.setSortOrder(Task.getSortCriterion());
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				break;
				
			case SEARCH :
				previous = new State(commandName);
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				previous.setAllTaskList(getAllTasks());
				break;
				
			case SET :
			  	previous = new State(commandName);
			  	String oldFilePath = _config.getSavePath().toString();
			  	previous.setFilePath(oldFilePath);
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				previous.setAllTaskList(getAllTasks());
			  	break;

			case UNDO :
				break;

			case HELP :
				previous = new State(commandName);
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				break;
				
			default :
				previous = new State(EMPTY_STATE);
				previous.setState(_taskDisplayLists);
				previous.setIndices(oldIndices);
				previous.setAllTaskList(getAllTasks());
				logger.log(Level.INFO, "Dummy state is created.");
				break;
		}
		return previous;
	}
	
	/**
	 * Reverts an add-task operation.
	 * 
	 * @param state the previous state of the program
	 */
	private void undoAdd(State state) {
		try {
			restoreOldState(state);
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays("undo", state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to undo add operation.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts a delete-task operation.
	 * 
	 * @param state the previous state of the program
	 */
	private void undoDelete(State state) {
		try {
			restoreOldState(state);
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays("undo", state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to undo delete operation.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts an update-task operation.
	 * 
	 * @param state the previous state of the program
	 */
	private void undoUpdate(State state) {
		try {
			restoreOldState(state);
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays("undo", state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to undo update operation.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts a complete-task operation.
	 * 
	 * @param state the previous state of the program
	 */
	private void undoCompleted(State state) {
		try {
			restoreOldState(state);
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays("undo", state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to undo complete operation.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts a sort-task operation.
	 * 
	 * @param state	the previous state of the program
	 */
	private void undoSort(State state) {
		try {
			restoreOldState(state);
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			_temporarySortList = new ArrayList<Task>();
			_temporarySortList.addAll(state.getAllTaskList());
			setUiTaskDisplays("undo", state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to undo sort operation.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts a set file-path operation.
	 * 
	 * @param state	the previous state of the program
	 */
	private void undoSetSaveFilePath(State state) {
		try {
			restoreOldState(state);
			String filePath = state.getFilePath();
			_config.setSavePath(filePath);
			_storage.setConfig(_config);
			initializeBuffers();
			updateInternalStorage();
			setUiTaskDisplays("undo", state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (InvalidPathException ipe) {
			logger.log(Level.SEVERE, "Failed to undo set filepath operation: invalid path.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SET_SAVEPATH;
		} catch (IOException ioe) {
			logger.log(Level.SEVERE, "Failed to undo set filepath operation: cannot read from file.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Restores inner memory and lists with previous program state.
	 * 
	 * @param state	a <code>State</code> object representing the previous program state
	 */
	private void restoreOldState(State state) {
		flushDisplayLists();
		_taskDisplayLists.get(Task.OVERDUE_TASK_INDEX).addAll(state.getOverdueTaskList());
		_taskDisplayLists.get(Task.TODAY_TASK_INDEX).addAll(state.getTodayTaskList());
		_taskDisplayLists.get(Task.TOMORROW_TASK_INDEX).addAll(state.getTomorrowTaskList());
		_taskDisplayLists.get(Task.THIS_WEEK_TASK_INDEX).addAll(state.getThisWeekTaskList());
		_taskDisplayLists.get(Task.UNCODED_TASK_INDEX).addAll(state.getRemainingTaskList());
		_taskDisplayLists.get(Task.FLOATING_TASK_INDEX).addAll(state.getFloatingTaskList());
		_taskDisplayLists.get(Task.COMPLETED_TASK_INDEX).addAll(state.getCompletedTaskList());
	}

	/**
	 * Empties the inner memory of any contents.
	 */
	private void flushDisplayLists() {
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			_taskDisplayLists.get(i).clear();
		}
	}

	

	// ============================== [CONDITIONAL CHECKS] ============================== //
	
	/**
	 * Checks if a Command object is null.
	 * 
	 * @param command	the Command object to be verified
	 * @return 			true if the object is not null; false otherwise
	 */
	private boolean isNullCommand(Command command) {
		return command == null;
	}
	
	/**
	 * Checks if the user command is about task-sorting.
	 * 
	 * @param commandType	the command type
	 * @return				true if the command type is about task-sorting; false otherwise
	 */
	private boolean isSortCommand(String commandType) {
		return commandType.equalsIgnoreCase(Command.CommandKey.SORT.getCommandKeyName());
	}
	
	/**
	 * Checks if the sort order is not specified.
	 * 
	 * @param sortOrder	the sorting criterion specified by the user
	 * @return			true if the criterion is NOT specified; false otherwise
	 */
	private boolean isUnspecifiedSortOrder(String sortOrder) {
		return sortOrder == null;
	}
	
	/**
	 * Checks if sort criterion is valid.
	 * 
	 * @param sortOrder a user-specified sorting criterion
	 * @return			true if sorting criterion is valid; false otherwise
	 */
	private boolean isRecognizedSortOrder(String sortOrder) {
		return isSortByName(sortOrder) || isSortByDate(sortOrder) || isSortByPriority(sortOrder);
	}

	/**
	 * Checks if sort criterion is by priority.
	 * 
	 * @param sortOrder a user-specified sorting criterion
	 * @return			true if sorting is by date; false otherwise
	 */
	private boolean isSortByPriority(String sortOrder) {
		return sortOrder.equals(Task.SORT_BY_PRIORITY_KEYWORD);
	}

	/**
	 * Checks if sort criterion is by name.
	 * 
	 * @param sortOrder	a user-specified sorting criterion
	 * @return			true if sorting is by name; false otherwise
	 */
	private boolean isSortByName(String sortOrder) {
		return sortOrder.equals(Task.SORT_BY_NAME_KEYWORD);
	}

	/**
	 * Checks if sort criterion is by date.
	 * 
	 * @param sortOrder	a user-specified sorting criterion
	 * @return			true if sorting is by date; false otherwise
	 */
	private boolean isSortByDate(String sortOrder) {
		return sortOrder.equals(Task.SORT_BY_START_DATE_KEYWORD) ||
			   sortOrder.equals(Task.SORT_BY_END_DATE_KEYWORD) ||
			   sortOrder.equals(Task.SORT_BY_DATE_KEYWORD);
	}
	
	/**
	 * Checks if a sort operation has been performed before by the user.
	 * 
	 * @return true if a sort operation was performed previously; false otherwise.
	 */
	private boolean isSortedBefore() {
		return _temporarySortList != null && _temporarySortList.size() != 0;  
	}
	
	/**
	 * Checks if a command is the search-task command.
	 * 
	 * @param commandType	the type of command
	 * @return				true if command is about task-search; false otherwise
	 */
	private boolean isSearchCommand(String commandType) {
		return commandType.equals("search");
	}
	
	/**
	 * Checks if search query is to search tasks after a certain date.
	 * 
	 * @param searchDateKeyword	the date-reference keyword specified by the user
	 * @return					true if search query is to search tasks after a certain date; false otherwise
	 */
	private boolean isSearchAfterDate(String searchDateKeyword) {
		return searchDateKeyword.trim().equalsIgnoreCase(SEARCH_AFTER);
	}

	/**
	 * Checks if search query is to search tasks on a certain date.
	 * 
	 * @param searchDateKeyword	the date-reference keyword specified by the user
	 * @return					true if search query is to search tasks on a certain date; false otherwise
	 */
	private boolean isSearchOnDate(String searchDateKeyword) {
		return searchDateKeyword.trim().equalsIgnoreCase(SEARCH_ON);
	}

	/**
	 * Checks if search query is to search tasks before a certain date.
	 * 
	 * @param searchDateKeyword	the date-reference keyword specified by the user
	 * @return					true if search query is to search tasks before a certain date; false otherwise
	 */
	private boolean isSearchBeforeDate(String searchDateKeyword) {
		return searchDateKeyword.trim().equalsIgnoreCase(SEARCH_BEFORE);
	}
	
	/**
	 * Checks if search query is by task name.
	 * 
	 * @param searchKeyword	the keyword to search for, supplied by the user
	 * @return				true if search query is by task name; false otherwise
	 */
	private boolean isSearchByName(String searchKeyword) {
		return searchKeyword != null;
	}

	/**
	 * Checks if search is valid. More precisely, it checks if the search query has a keyword for
	 * searching tasks by name, or checks if the search query has the date references for searching
	 * tasks by date.
	 * 
	 * @param searchKeyword			the keyword to look out for in the Task name
	 * @param searchDateKeyword		the keyword for searching tasks by date ('before', 'on', 'after')
	 * @param dateString			the date for reference when searching tasks by date
	 * @param timeString			the time for reference when searching tasks by date
	 * @return						true if important data for search query is omitted; false otherwise
	 */
	private boolean isInvalidSearch(String searchKeyword, String searchDateKeyword, String dateString,
			String timeString) {
		return (isUnspecifiedSortOrder(searchKeyword) && isUnspecifiedSortOrder(searchDateKeyword)) ||
			   (isSearchByName(searchDateKeyword) && isUnspecifiedSortOrder(dateString) && isUnspecifiedSortOrder(timeString));
	}
	
	/**
	 * Checks if the user command is an undo command. Note that there is an overloaded method that
	 * takes in a Command object as an argument instead.
	 * 
	 * @see 				#isUndoCommand(Command)
	 * @param commandType	the command type
	 * @return				true if the command type is undo; false otherwise
	 */
	private boolean isUndoCommand(String commandType) {
		return commandType.equalsIgnoreCase(Command.CommandKey.UNDO.getCommandKeyName());
	}
	
	/**
	 * Checks if a command is an undo operation. Note that there is an overloaded method that takes
	 * in a String as an argument instead.
	 * 
	 * @see 			#isUndoCommand(String)
	 * @param command	a Command representing the user operation to be carried out
	 * @return 			true if the Command represents an undo command, false otherwise
	 * 
	 */
	private boolean isUndoCommand(Command command) {
		return command.getCommand().equals("undo");
	}

	// ====================== LOWER-LEVEL USER OPERATION METHODS FOR LOGIC ======================= //
	// =========================================================================================== //
}