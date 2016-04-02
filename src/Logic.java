
/**
 * ============= [LOGIC COMPONENT FOR ESTHER] =============
 * 
 * Logic handles all operations as requested by the user,
 * which are basically CRUD operations.
 * 
 * Generally, when these operations succeed or fail,
 * relevant messages confirming the statuses of these
 * operations shall be passed to the UI via the Parser,
 * which will then be shown to the user.
 * 
 * For certain operations, Task objects are returned and
 * these will be passed to the Parser, which will parse
 * them to human-readable format and forward these to the
 * UI to be displayed to the user.
 * 
 * 
 * =================== [CURRENT STATUS] ===================
 * Most of ESTHER is broken due to Task changes. When all
 * necessary fixes are done, will proceed to test.
 * 
 * @@author A0129660A
 */

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.*;
import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Command.CommandKey;
import cs2103_w09_1j.esther.Task.TaskField;
import cs2103_w09_1j.esther.UIResult;
import cs2103_w09_1j.esther.Config;
import cs2103_w09_1j.esther.Task;
import cs2103_w09_1j.esther.State;
import cs2103_w09_1j.esther.Status;
import cs2103_w09_1j.esther.InvalidInputException;

class Logic {
	
	private static final int NOT_FOUND_INDEX = -1;
	private static final int DUPLICATE_TASK_INDEX = -2;
	
	// TODO: finalize buffer implementation
	/*
	 * Note: this implementation will affect ALL logic operations.
	 * */
	private static final int NUM_TASK_BUFFERS = 7;
	
	private static final String EMPTY_STATE = "Empty";
	private static final String DEFAULT_TASKS_SORT_ORDER = Task.TaskField.ENDDATE.getTaskKeyName();
	private static final String DEFAULT_FLOATING_TASKS_SORT_ORDER = "id";
	private static final String SEARCH_BEFORE = "before";
	private static final String SEARCH_ON = "on";
	private static final String SEARCH_AFTER = "after";
	private static final int TASK_LIST_POSITION = 0;
	private static final int TASK_ITEM_POSITION = 1;
	
	private Parser _parser;
	private Storage _storage;
	private ArrayList<Task> _fullTaskList;
	private ArrayList<ArrayList<Task>> _taskDisplayLists;
	private ArrayList<Task> _searchList;
	private Stack<State> _undoStack;
	private Config _config;
	private Date _today;
	//private static Logger //logger = Logger.getLogger("Logic");

	
	// =========================================================================================== //
	// =========================== HIGH-LEVEL IMPLEMENTATION OF LOGIC ============================ //
	// These methods are the highest-level implementation of the core structure of Logic.          //
	// The important methods falling into this category are: 									   //
	// 1. Constructor, Logic()																	   //
	// 2. public String executeCommand(String input)											   //
	// 3. private String executeCommand(Command command)										   //
	//																							   //
	// As new functions are added, or as existing inner functions are extended,					   //
	// method executeCommand(Command command) will be subject to more and more changes.			   //
	// =========================================================================================== //
	
	/**
	 * Constructs a Logic component instance.
	 * 
	 * 
	 */
	public Logic() throws ParseException, IOException {
		//initializeLogger();
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
	 * 
	 */
	// TODO: finalize implementation
	// Note that Parser will throw exception in future, conditional should be modified
	public String executeCommand(String userInput) {
		//logger.logp(Level.INFO, "Logic", "executeCommand",
					//"Parsing user input into Command object for execution.", userInput);
		try {
			Command command = _parser.acceptUserInput(userInput);
			if (command == null) {
				//logger.log(Level.WARNING, "Error from Parser: encountered null Command object.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.INVALID_COMMAND;
				return Status.getMessage(null, null, null);
			}
			return executeCommand(command);
		} catch (InvalidInputException iie) {
			//logger.log(Level.WARNING, "Invalid input supplied by user.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.INVALID_COMMAND;
			return iie.getMessage();
			//return Status.getMessage(null, null, null);
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
	 * @return a message indicating the status of the operation carried out
	 * 
	 */
	protected String executeCommand(Command command) {
		assert command != null;
		String commandName = command.getCommand();
		CommandKey commandType = CommandKey.get(commandName);
		//logger.logp(Level.INFO, "Logic", "executeCommand(Command command)",
					//"Executing on Command object.", commandType);
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
				Status._outcome = Status.Outcome.SUCCESS;
				statusMessage = Status.getMessage(null, null, commandName);
				break;
				
			default :
				assert commandType != null;
				//logger.logp(Level.INFO, "Logic", "executeCommand(Command command)",
							//"Unrecognized command.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.INVALID_COMMAND;
				statusMessage = Status.getMessage(null, null, commandName);
				break;
		}
		return statusMessage;
	}
	
	// =========================== HIGH-LEVEL IMPLEMENTATION OF LOGIC ============================ //
	// =========================================================================================== //
	
	

	// =========================================================================================== //
	// ================================ SYSTEM METHODS FOR LOGIC ================================= //
	// These methods are used to maintain the inner workings of Logic and are unrelated to         //
	// user-related operations.																	   //
	//																							   //
	// SOME of these methods may be removed upon release phase, as these methods are mostly		   //
	// used only for testing.																	   //
	// =========================================================================================== //
	
	/**
	 * Retrieves the internal memory that is used by Logic.
	 * Used only for internal testing.
	 * 
	 * @return the internal memory representation of the
	 * 		   contents stored in the text file.
	 * 
	 */
	protected ArrayList<Task> getInternalStorage() {
		_fullTaskList = new ArrayList<Task>();
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			_fullTaskList.addAll(_taskDisplayLists.get(i));
		}
		return _fullTaskList;
	}
	
	/**
	 * Retrieves the internal memory that is used by Logic.
	 * This internal memory is represented in a whole String.
	 * 
	 * @return the internal memory representation of the
	 * 		   contents stored in the text file, in String
	 * 		   form.
	 * 
	 */
	public void setUiTaskDisplays(String commandType, int[] indices) {
		UIResult displayResult = createDisplayResult(commandType, indices);
		MainController.setRes(displayResult);
	}
	
	public UIResult createDisplayResult(String commandType, int[] indices) {
		UIResult result = new UIResult();
		result.setOverdueBuffer(_taskDisplayLists.get(Task.OVERDUE_TASK_INDEX));
		result.setTodayBuffer(_taskDisplayLists.get(Task.TODAY_TASK_INDEX));
		result.setTomorrowBuffer(_taskDisplayLists.get(Task.TOMORROW_TASK_INDEX));
		result.setWeekBuffer(_taskDisplayLists.get(Task.THIS_WEEK_TASK_INDEX));
		result.setFloatingBuffer(_taskDisplayLists.get(Task.FLOATING_TASK_INDEX));
		result.setCompletedBuffer(_taskDisplayLists.get(Task.COMPLETED_TASK_INDEX));
		ArrayList<Task> allTasks = new ArrayList<Task>();
		allTasks.addAll(_taskDisplayLists.get(Task.OVERDUE_TASK_INDEX));
		allTasks.addAll(_taskDisplayLists.get(Task.TODAY_TASK_INDEX));
		allTasks.addAll(_taskDisplayLists.get(Task.TOMORROW_TASK_INDEX));
		allTasks.addAll(_taskDisplayLists.get(Task.UNCODED_TASK_INDEX));
		allTasks.addAll(_taskDisplayLists.get(Task.FLOATING_TASK_INDEX));
		result.setAllTaskBuffer(allTasks);
		result.setCommandType(commandType);
		result.setIndex(indices);
		if (commandType.equals("search")) {
			result.setSearchBuffer(_searchList);
		}
		return result;
	}
	
	/**
	 * Initializes a system logger. Used for testing purposes only.
	 * 
	 * 
	 */
	private void initializeLogger() {
		try {
			//logger.setLevel(Level.WARNING);
			// TODO: change log file path in future, upon release.
			FileHandler fh = new FileHandler("C:/Users/Tay/Documents/GitHub/main/Logic.log");
			//logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);
			//logger.logp(Level.CONFIG, "Logic", "initializeLogger()", "Initializing //logger.");
		} catch (SecurityException se) {
			//logger.logp(Level.SEVERE, "Logic", "initializeLogger()",
						//"Not granted permission for logging.", se);
			//System.exit(1);
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "initializeLogger()",
						//"Cannot create file for logging.", ioe);
			//System.exit(1);
		}
	}
	
	/**
	 * Initializes Storage and Config systems in Logic.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * 
	 */
	private void initializeStorageAndConfig() throws ParseException, IOException {
		_storage = new Storage();
		//logger.logp(Level.CONFIG, "Storage", "Storage()", "Initializing Storage.");
		assert _storage != null;
		_config = _storage.getConfig();
		//logger.logp(Level.CONFIG, "Storage", "getConfig()", "Initializing Config.");
		assert _config != null;
		//System.out.println(_config.getReferenceID());
		Task.setGlobalId(_config.getReferenceID());
		//System.out.println("Storage and Config initialized.");
	}

	/**
	 * Initializes Parser system in Logic.
	 * 
	 * 
	 */
	private void initializeParser() {
		//logger.logp(Level.CONFIG, "Parser", "Parser()", "Initializing Parser.");
		HashMap<String, String> fieldNameAliases = _config.getFieldNameAliases();
		_parser = new Parser(fieldNameAliases);
		assert _parser != null;
		//System.out.println("Parser initialized.");
	}
	
	/**
	 * Initializes internal system variables in Logic.
	 * @throws IOException 
	 * @throws ParseException 
	 * 
	 * 
	 */
	private void initializeLogicSystemVariables() throws ParseException, IOException {
		initializeBuffers();
		updateInternalStorage();
		_undoStack = new Stack<State>();
		//logger.logp(Level.CONFIG, "Logic", "updateInternalStorage",
					//"Reading tasks into inner memory upon initialization.");
		//System.out.println("Inner variables initialized.");
		setUiTaskDisplays("initialize", new int[2]);
	}
	
	/**
	 * Sets up all inner buffers needed for UI to show custom task views.
	 * 
	 * 
	 */
	private void initializeBuffers() {
		_taskDisplayLists = new ArrayList<ArrayList<Task>>(NUM_TASK_BUFFERS);
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			_taskDisplayLists.add(new ArrayList<Task>());
		}
	}
	
	/**
	 * Attaches a shutdown-event handler to perform necessary system updates
	 * when ESTHER is shut down.
	 * 
	 * 
	 */
	private void attachShutdownHandler() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	        	//System.out.println("Saving current system configurations.");
	            try {
	            	//logger.logp(Level.INFO, "Logic", "addTask(Command command)",
	            				  //"Updating Config file in Logic and Storage.");
	            	_config.setReferenceID(Task.getGlobalId());
	            	_storage.updateConfig(_config);
	            } catch (IOException ioe) {
	            	//logger.logp(Level.SEVERE, "Logic", "addTask(Command command)",
	            				  //"Cannot update Config file in Logic and Storage.", ioe);
	                System.out.println("Error updating Config file in both Logic and Storage.");
	            }
	        }   
	    });
	}
	
	/**
	 * Updates the internal memory of the Logic to account
	 * for any changes done to the text file.
	 * 
	 * 
	 */
	// TODO: finalize implementation, error handling
	// current implementation is to terminate program, for now
	public void updateInternalStorage() {
		//logger.logp(Level.INFO, "Logic", "updateInternalStorage()", "Retrieving tasks list from Storage.");
		try {
			_fullTaskList = _storage.readSaveFile();
			assert _fullTaskList != null;
			filterTasksToLists(DEFAULT_TASKS_SORT_ORDER, true, true);
		} catch (Exception e) {
			// TODO: error handling
			//logger.logp(Level.SEVERE, "Storage", "readSaveFile()",
						//"Cannot read from save file in Storage.", e);
			System.exit(1);
		}
	}
	
	/**
	 * Empties the text file of any contents.
	 * Only used for internal testing.
	 * 
	 * 
	 */
	public void flushInternalStorage() {
		/* 
		 * Might want to consider insert try-catch block here
		 * Then again, if this method isn't needed anymore in the future,
		 * this method can just be removed totally. 
		 */
		try {
			_storage.flushFile();
		} catch (IOException ioe) {
			// TODO: handle exception
		}
		_fullTaskList = new ArrayList<Task>();
		initializeBuffers();
	}
	
	/**
	 * Update Logic's list of tasks into text file.
	 * 
	 * @throws IOException
	 * 
	 */
	private void updateTextFile() throws IOException {
		_fullTaskList = new ArrayList<Task>();
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			_fullTaskList.addAll(_taskDisplayLists.get(i));
		}
		_storage.writeSaveFile(_fullTaskList);
	}
	
	/**
	 * Updates the undo stack whenever a user operation is carried out.
	 * 
	 * @param command a Command object representing the user operation being carried out
	 * @param task a Task object representing the task that was operated on
	 * 
	 */
	private void updateUndoStack(Command command, int[] indices) {
		_undoStack.push(storePreviousState(command, _taskDisplayLists, indices));
	}
	
	/**
	 * Separates floating tasks from tasks with deadlines for proper sorting.
	 * 
	 * 
	 */
	private void filterTasksToLists(String sortOrder, boolean toSort, boolean toInitialize) {
		if (toInitialize) {
			Iterator<Task> iter = _fullTaskList.iterator();
			_today = new Date();
			while (iter.hasNext()) {
				Task currentTask = iter.next();
				_taskDisplayLists.get(currentTask.getTaskCode(_today)).add(currentTask);
				iter.remove();
			}
			assert _fullTaskList.size() == 0;
		}
		
		if (toSort) {
			Task.setSortCriterion(sortOrder);
			if (sortOrder.equals(Task.SORT_BY_START_DATE_KEYWORD) ||
				sortOrder.equals(Task.SORT_BY_END_DATE_KEYWORD)) {
				Task.setSortCriterion(Task.SORT_BY_DATE_KEYWORD);
				for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
					// do not sort floating or completed tasks by date
					if (i != Task.FLOATING_TASK_INDEX || i != Task.COMPLETED_TASK_INDEX) {
						Collections.sort(_taskDisplayLists.get(i));
					}
				}
				Task.setSortCriterion(DEFAULT_FLOATING_TASKS_SORT_ORDER);
				Collections.sort(_taskDisplayLists.get(Task.FLOATING_TASK_INDEX));
				Collections.sort(_taskDisplayLists.get(Task.COMPLETED_TASK_INDEX));
			} else if (sortOrder.equals(Task.SORT_BY_NAME_KEYWORD)) {
				// specialized sorting for floating and completed tasks to be done separately
				for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
					if (i != Task.FLOATING_TASK_INDEX || i != Task.COMPLETED_TASK_INDEX) {
						Collections.sort(_taskDisplayLists.get(i));
					}
				}
				Task.setSortCriterion(Task.SORT_FLOATING_BY_NAME_KEYWORD);
				Collections.sort(_taskDisplayLists.get(Task.FLOATING_TASK_INDEX));
				Task.setSortCriterion(DEFAULT_FLOATING_TASKS_SORT_ORDER);
				Collections.sort(_taskDisplayLists.get(Task.COMPLETED_TASK_INDEX));
			} else if (sortOrder.equals(Task.SORT_BY_PRIORITY_KEYWORD)) {
				// specialized sorting for floating and completed tasks to be done separately
				for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
					if (i != Task.FLOATING_TASK_INDEX) {
						Collections.sort(_taskDisplayLists.get(i));
					}
				}
				Task.setSortCriterion(Task.SORT_FLOATING_BY_PRIORITY_KEYWORD);
				Collections.sort(_taskDisplayLists.get(Task.FLOATING_TASK_INDEX));
				Task.setSortCriterion(DEFAULT_FLOATING_TASKS_SORT_ORDER);
				Collections.sort(_taskDisplayLists.get(Task.COMPLETED_TASK_INDEX));
			} else {
				// do nothing
			}
		}
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
		//logger.logp(Level.INFO, "Logic", "addTask(Command command)",
					//"Adding a task.", taskName);
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
		//logger.log(Level.INFO, "Updating a task.");
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
		//logger.logp(Level.INFO, "Logic", "completeTask(Command command)", "Completing a task.", params);
		completeTaskInFile(command);
		return getOperationStatus(command);
	}

	/**
	 * Sorts the list of tasks recorded in the text file.
	 * By default, tasks are sorted by the order defined
	 * in the Task class.
	 * 
	 * NOTE: implementation can be extended to sort the
	 * list of tasks by user-defined criteria.
	 * 
	 * @see 	Task#compareTo(Task)
	 * @return	a message indicating the status of the sort operation
	 * 
	 */
	private String sortFile(Command command) {
		int indices[] = {-1, -1};
		updateUndoStack(command, indices);
		//System.out.println(_undoStack.size());
		sortAndUpdateFile(command);
		setUiTaskDisplays(command.getCommand(), indices);
		return getOperationStatus(command);
	}
	
	/**
	 * Searches for a task based on the user's desired criteria.
	 * 
	 * NOTE: default search is by whether a task contains a specified
	 * task name or not. This implementation can be extended to support
	 * varying user criteria.
	 * 
	 * @param  task	a Task object representation of a keyword to lookup
	 * @return a list of Task objects that match the search criteria
	 * 
	 */
	private String searchFile(Command command) {
		getInternalStorage();
		_searchList = new ArrayList<Task>();
		String searchKeyword = command.getSpecificParameter(Task.TaskField.NAME.getTaskKeyName());
		String searchDateKeyword = command.getSpecificParameter(Task.TaskField.KEYWORD.getTaskKeyName());
		//System.out.println(searchDateKeyword);
		String dateString = command.getSpecificParameter(Task.TaskField.ENDDATE.getTaskKeyName());
		String timeString = command.getSpecificParameter(Task.TaskField.ENDTIME.getTaskKeyName());
		
		if ((searchKeyword == null && searchDateKeyword == null) ||
			(searchDateKeyword != null && dateString == null && timeString == null)) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SEARCH_INVALID;
			return getOperationStatus(command);
		}
		//logger.logp(Level.INFO, "Logic", "searchFile(Command command)",
				  	  //"Searching tasks in file.", searchKey);
		if (searchKeyword != null) {
			for (Task entry: _fullTaskList) {
				String taskNameCopy = entry.getName();
				String taskNameLowerCase = taskNameCopy.toLowerCase();
				if (taskNameLowerCase.contains(command.getSpecificParameter(Task.TaskField.NAME.getTaskKeyName()).trim().toLowerCase())) {
					_searchList.add(entry);
				}
			}
		} else {
			Date referenceDate;
			try {
				if (searchDateKeyword.trim().equalsIgnoreCase(SEARCH_BEFORE)) {
					referenceDate = Task.parseDateTimeToString(new Date(), dateString, timeString, true);
				} else {
					referenceDate = Task.parseDateTimeToString(new Date(), dateString, timeString, false);
				}
			} catch (ParseException pe) {
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.SEARCH_INVALID;
				return getOperationStatus(command);
			}
			//System.out.println(Task._dateAndTimeFormatter.format(referenceDate));
			for (Task entry: _fullTaskList) {
				if (searchDateKeyword.trim().equalsIgnoreCase(SEARCH_BEFORE)) {
					if (entry.isEvent() && referenceDate.compareTo(entry.getStartDate()) > 0) {
						_searchList.add(entry);
					} else if (!entry.isFloatingTask() && referenceDate.compareTo(entry.getEndDate()) > 0) {
						_searchList.add(entry);
					} else {
						
					}
				} else if (searchDateKeyword.trim().equalsIgnoreCase(SEARCH_ON)) {
					Date intervalStart = (Date) referenceDate.clone();
					intervalStart.setHours(0);
					intervalStart.setMinutes(0);
					Date intervalEnd = (Date) referenceDate.clone();
					intervalEnd.setHours(23);
					intervalEnd.setMinutes(59);
					//System.out.println(Task._dateAndTimeFormatter.format(intervalStart));
					//System.out.println(Task._dateAndTimeFormatter.format(intervalEnd));
					if (entry.isEvent() && intervalStart.compareTo(entry.getStartDate()) <= 0 &&
						intervalEnd.compareTo(entry.getStartDate()) >= 0) {
						_searchList.add(entry);
					} else if (!entry.isFloatingTask() && intervalStart.compareTo(entry.getEndDate()) <= 0
							   && intervalEnd.compareTo(entry.getEndDate()) >= 0) {
						_searchList.add(entry);
					} else {
						
					}
				} else if (searchDateKeyword.trim().equalsIgnoreCase(SEARCH_AFTER)){
					if (entry.isEvent() && referenceDate.compareTo(entry.getStartDate()) <= 0) {
						_searchList.add(entry);
					} else if (!entry.isFloatingTask() && referenceDate.compareTo(entry.getEndDate()) <= 0) {
						_searchList.add(entry);
					} else {
						
					}
				} else {
					Status._outcome = Status.Outcome.ERROR;
					Status._errorCode = Status.ErrorCode.SEARCH_INVALID;
					return getOperationStatus(command);
				}
			}
		}
		//System.out.println(results);
		int indices[] = {-1, -1};
		setUiTaskDisplays(command.getCommand(), indices);
		Status._outcome = Status.Outcome.SUCCESS;
		return getOperationStatus(command);
	}
	
	private String setSaveFilePath(Command command) {
		try {
			_config.setSavePath(command.getSpecificParameter(Task.TaskField.PATH.getTaskKeyName()));
			_storage.updateConfig(_config);
			updateInternalStorage(); // refresh internal memory due to different file specified
			Status._outcome = Status.Outcome.SUCCESS;
			int indices[] = {-1, -1};
			updateUndoStack(command, indices);
		} catch (InvalidPathException ipe) {
		 	Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SET_SAVEPATH;
		 } catch (IOException ioe) {
		 	Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SET_SAVEPATH;
		 }
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
			//logger.logp(Level.INFO, "Logic", "undo()", "User cannot undo any further.");
			//System.out.println("Undo not successful.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.UNDO;
		} else {
			State previousState = _undoStack.pop();
			//System.out.println(previousState.getCommand());
			CommandKey commandType = CommandKey.get(previousState.getCommand());
			//logger.logp(Level.INFO, "Logic", "undo()", "Undoing a previous operation.", commandType);
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
					undoSetSaveFilePath(previousState.getFilePath());
					break;
					
				case HELP :
					break;
			
				default :
					//logger.logp(Level.INFO, "Logic", "undo()", "Dummy State encountered.");
					Status._outcome = Status.Outcome.ERROR;
					Status._errorCode = Status.ErrorCode.SYSTEM;
					return getOperationStatus(command);
			}
		}
		return getOperationStatus(command);
	}
	
	// ============================ USER OPERATION METHODS FOR LOGIC ============================= //
	// =========================================================================================== //
	
	
	
	// =========================================================================================== //
	// ====================== LOWER-LEVEL USER OPERATION METHODS FOR LOGIC ======================= //
	// These methods are lower-level methods used within user operation methods.				   //
	// During code re-factoring, you may place these lower-level methods here.					   //
	// =========================================================================================== //
	
	/**
	 * Creates and adds a task in both Logic and Storage.
	 * 
	 * @param command a Command object representing the user operation being carried out
	 * @return the Task that was added; null if an error occurred
	 */
	private void createAndAddTaskToFile(Command command) {
		Task addedTask = null;
		try {
			int indices[] = {-1, -1};
			addedTask = new Task(command);
			_taskDisplayLists.get(addedTask.getTaskCode(_today)).add(addedTask);
			updateTextFile();
			updateUndoStack(command, indices);
			indices[TASK_LIST_POSITION] = addedTask.getTaskCode(_today);
			indices[TASK_ITEM_POSITION] = _taskDisplayLists.get(addedTask.getTaskCode(_today)).size() - 1;
			setUiTaskDisplays(command.getCommand(), indices);
			//System.out.println(_undoStack.size());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (ParseException pe) {
			//logger.logp(Level.SEVERE, "Logic", "addTask(Command command)",
						//"Add task: Inappropriate date format passed into Task.", pe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.ADD_INVALID_FORMAT;
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "addTask(Command command)",
					//"Add task: Error in writing to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Retrieves the index of a task as well as the index of the list that contains the task.<br>
	 * [ NOT_FOUND_INDEX, NOT_FOUND_INDEX ] is returned if the task cannot be found anywhere.<br>
	 * [ DUPLICATE_TASK_INDEX, DUPLICATE_TASK_INDEX ] is returned if tasks with duplicate names exist.<br>
	 * 
	 * @param command the Command object representing the user operation being carried out
	 * @return the index of a task, if a single task can be located; -1 if no task can be found
	 * 		   or if there are duplicate tasks
	 * 
	 */
	private int[] getTaskIndex(Command command) {
		int index[] = {NOT_FOUND_INDEX, NOT_FOUND_INDEX};
		boolean hasDuplicate = false;
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());
		//System.out.println("Task name to update: " + taskName);
		String taskID = command.hasParameter(TaskField.ID.getTaskKeyName())
						? command.getSpecificParameter(TaskField.ID.getTaskKeyName())
						: String.valueOf(NOT_FOUND_INDEX);
		//System.out.println(taskID);
		String[] params = {taskName, command.getSpecificParameter(TaskField.ID.getTaskKeyName())};
		//logger.logp(Level.INFO, "Logic", "removeTask(Command command)",	"Removing a task.", params);
		for (int i = 0; i < NUM_TASK_BUFFERS; i++) {
			//System.out.println("Current task accessed is " + _tasks.get(i).getName());
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
		//System.out.println(index);
		return index;
	}
	
	/**
	 * Removes a task from both Logic's internal storage as well as from the text file.
	 * 
	 * @param command a Command object representing the user operation being carried out
	 */
	private void removeTaskAndUpdateFile(Command command) {
		Task removed = null;
		int taskIndex[] = getTaskIndex(command);
		try {
			if (taskIndex[TASK_LIST_POSITION] != NOT_FOUND_INDEX &&
				taskIndex[TASK_LIST_POSITION] != DUPLICATE_TASK_INDEX) {
				removed = _taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).get(taskIndex[TASK_ITEM_POSITION]);
				_taskDisplayLists.get(taskIndex[0]).remove(removed);
				updateTextFile();
				updateUndoStack(command, taskIndex);
				//System.out.println(_undoStack.size());
				int indices[] = {-1, -1};
				setUiTaskDisplays(command.getCommand(), indices);
				Status._outcome = Status.Outcome.SUCCESS;
			} else if (taskIndex[TASK_LIST_POSITION] == DUPLICATE_TASK_INDEX) {
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.DELETE_DUPLICATES_PRESENT;
			} else {
				//logger.logp(Level.WARNING, "Logic", "removeTask(Command command)",
							//"Delete task: Task not found. Possible user-side error or no name/ID matching.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.DELETE_NOT_FOUND;
			}
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "removeTask(Command command)",
						//"Delete task: cannot write to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Updates a task in both Logic's internal storage as well as from the text file. 
	 * 
	 * @param command a Command object representing the user operation being carried out
	 * 
	 */
	private void updateTaskInFile(Command command) {
		Task toUpdate = null;
		int taskIndex[] = getTaskIndex(command);
		//System.out.println(taskIndex);
		
		try {
			if (taskIndex[TASK_LIST_POSITION] != NOT_FOUND_INDEX &&
				taskIndex[TASK_LIST_POSITION] != DUPLICATE_TASK_INDEX) {
				//String old = toUpdate.getName();
				toUpdate = _taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).get(taskIndex[TASK_ITEM_POSITION]);
				Task copyOfOldTask = toUpdate.clone();
				boolean isUpdated = toUpdate.updateTask(command);
				if (isUpdated) {
					_taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).remove(taskIndex[TASK_ITEM_POSITION]);
					_taskDisplayLists.get(toUpdate.getTaskCode(_today)).add(toUpdate);
					updateTextFile();
					updateUndoStack(command, taskIndex);
					//System.out.println(_undoStack.size());
					//System.out.println("Old name: " + old + " New name: " + _tasks.get(updateIndex).getName());
					int indices[] = {toUpdate.getTaskCode(_today),
									 _taskDisplayLists.get(toUpdate.getTaskCode(_today)).size()};
					setUiTaskDisplays(command.getCommand(), indices);
					Status._outcome = Status.Outcome.SUCCESS;
				} else {
					Status._outcome = Status.Outcome.ERROR;
				}
			} else if (taskIndex[TASK_LIST_POSITION] == DUPLICATE_TASK_INDEX) {
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.UPDATE_DUPLICATES_PRESENT;
			} else {
				//logger.logp(Level.WARNING, "Logic", "updateTask(Command command)",
							//"Update task: Task not found. Possible user-side error or no name/ID matching.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.UPDATE_NOT_FOUND;
			}
		} catch (ParseException pe) {
			//logger.logp(Level.SEVERE, "Logic", "updateTask(Command command)",
						//"Update task: Inappropriate date formated passed into Task.", pe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.UPDATE_INVALID_FIELD;
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "updateTask(Command command)",
						//"Update task: cannot write to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Completes a task in both Logic's internal storage as well as the text file.
	 * 
	 * @param command a Command object representing the user operation being carried out
	 * 
	 */
	private void completeTaskInFile(Command command) {
		Task toUpdate = null;
		int taskIndex[] = getTaskIndex(command);

		try {
			if (taskIndex[TASK_LIST_POSITION] != NOT_FOUND_INDEX &&
				taskIndex[TASK_LIST_POSITION] != DUPLICATE_TASK_INDEX) {
				toUpdate = _taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).get(taskIndex[TASK_ITEM_POSITION]);
				if (toUpdate.isCompleted()) {
					Status._outcome = Status.Outcome.ERROR;
					Status._errorCode = Status.ErrorCode.COMPLETED_ALREADY_COMPLETED;
				}
				else {
					Task copyOfOldTask = toUpdate.clone();
					toUpdate.setCompleted(true);
					_taskDisplayLists.get(taskIndex[TASK_LIST_POSITION]).remove(taskIndex[TASK_ITEM_POSITION]);
					_taskDisplayLists.get(toUpdate.getTaskCode(_today)).add(toUpdate);
					updateTextFile();
					updateUndoStack(command, taskIndex);
					int indices[] = {toUpdate.getTaskCode(_today),
									 _taskDisplayLists.get(toUpdate.getTaskCode(_today)).size()};
					setUiTaskDisplays(command.getCommand(), indices);
					//System.out.println(_undoStack.size());
					Status._outcome = Status.Outcome.SUCCESS;
				}
			} else if (taskIndex[TASK_LIST_POSITION] == DUPLICATE_TASK_INDEX) {
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.COMPLETED_DUPLICATES_PRESENT;
			} else {
				//logger.logp(Level.WARNING, "Logic", "completeTask(Command command)",
							//"Complete task: Task not found. Possible user-side error or no name/ID matching.");
				Status._outcome = Status.Outcome.ERROR;
				Status._errorCode = Status.ErrorCode.COMPLETED_NOT_FOUND;
			}
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "completeTask(Command command)",
						//"Complete task: cannot write to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	private void sortAndUpdateFile(Command command) {
		String sortOrder = command.getSpecificParameter(TaskField.SORT.getTaskKeyName());
		//System.out.println(sortOrder);
		try {
			//logger.logp(Level.INFO, "Logic", "sortFile(Command command)",
						//"Sorting all tasks by user-specified order.", sortOrder);
			if (sortOrder == null) {
				sortOrder = DEFAULT_TASKS_SORT_ORDER;
			}
			filterTasksToLists(sortOrder, true, false);
			updateTextFile();
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "sortFile(Command command)",
						//"Sort file: cannot write to file.", ioe);
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Stores the program state before a user operation was performed.
	 * 
	 * @param  task	a Task object representation of the user's input
	 * @return a Task object that has an opposite command to be
	 * 		   performed on it, where applicable.
	 * 
	 */
	private State storePreviousState(Command command, ArrayList<ArrayList<Task>> taskLists, int[] oldIndices) {
		//logger.logp(Level.INFO, "Logic", "storePreviousState(Command command, Task original)",
					//"Storing previous program memory state.");
		String commandName = command.getCommand();
		CommandKey commandType = CommandKey.get(commandName);
		State previous = null;
		switch (commandType) {
			case ADD :
				previous = new State(commandName);
				previous.setState(taskLists);
				previous.setIndices(oldIndices);
				break;

			case DELETE :
				previous = new State(commandName);
				previous.setState(taskLists);
				previous.setIndices(oldIndices);
				break;

			case UPDATE :
				previous = new State(commandName);
				previous.setState(taskLists);
				previous.setIndices(oldIndices);
				break;
				
			case COMPLETE :
				previous = new State(commandName);
				previous.setState(taskLists);
				previous.setIndices(oldIndices);
				break;

			case SHOW :
				previous = new State(commandName);
				assert command.getSpecificParameter(TaskField.SHOW.getTaskKeyName()) != null;
				previous.setSortOrder(command.getSpecificParameter(TaskField.SHOW.getTaskKeyName()));
				previous.setState(taskLists);
				previous.setIndices(oldIndices);
				break;

			case SORT :
				previous = new State(commandName);
				assert command.getSpecificParameter(TaskField.SORT.getTaskKeyName()) != null;
				previous.setSortOrder(command.getSpecificParameter(TaskField.SORT.getTaskKeyName()));
				previous.setState(taskLists);
				previous.setIndices(oldIndices);
				break;
				
			case SEARCH :
				previous = new State(EMPTY_STATE);
				break;
				
			case SET :
			  	previous = new State(commandName);
			  	String oldFilePath = _config.getSavePath().toString();
			  	previous.setFilePath(oldFilePath);
			  	break;

			case UNDO :
				previous = new State(EMPTY_STATE);
				break;

			case HELP :
				previous = new State(EMPTY_STATE);
				break;
				
			default :
				previous = new State(EMPTY_STATE);
				//logger.logp(Level.INFO, "Logic", "storePreviousState(Command command, Task original)",
							//"Dummy state is created.");
				break;
		}
		return previous;
	}
	
	/**
	 * Reverts an add-task operation.
	 * 
	 * @param task the reference of the initially added task to remove
	 */
	// TODO: error handling
	private void undoAdd(State state) {
		try {
			_taskDisplayLists = state.getState();
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays(state.getCommand(), state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts a delete-task operation.
	 * 
	 * @param task a reference of the initially deleted task to add back
	 */
	// TODO: error handling
	private void undoDelete(State state) {
		try {
			_taskDisplayLists = state.getState();
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays(state.getCommand(), state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts an update-task operation.
	 * 
	 * @param task a reference of the previous state of a task before it was updated
	 */
	// TODO: error handling
	private void undoUpdate(State state) {
		try {
			_taskDisplayLists = state.getState();
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays(state.getCommand(), state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts a complete-task operation.
	 * 
	 * @param task a reference of a task before it was set as completed
	 */
	// TODO: error handling
	private void undoCompleted(State state) {
		try {
			_taskDisplayLists = state.getState();
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays(state.getCommand(), state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts a sort-task operation.
	 * 
	 * @param tasks a reference to the previous ordering of tasks
	 */
	// TODO: error handling
	private void undoSort(State state) {
		try {
			_taskDisplayLists = state.getState();
			getInternalStorage();
			_storage.writeSaveFile(_fullTaskList);
			setUiTaskDisplays(state.getCommand(), state.getIndices());
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	private void undoSetSaveFilePath(String filePath) {
		// TODO: set back to old filePath
		try {
			_config.setSavePath(filePath);
			_storage.updateConfig(_config);
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (InvalidPathException ipe) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SET_SAVEPATH;
		} catch (IOException ioe) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Retrieves the status message of the user operation that is being carried out by Logic.
	 * 
	 * @param command a Command object representing the user operation being carried out
	 * @return the status message of the user operation being carried out
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

	/**
	 * Checks if a command is an undo operation.
	 * 
	 * @param command a Command representing the user operation to be carried out
	 * @return true if the Command represents an undo command, false otherwise
	 * 
	 */
	private boolean isUndoCommand(Command command) {
		return command.getCommand().equals("undo");
	}

	// ====================== LOWER-LEVEL USER OPERATION METHODS FOR LOGIC ======================= //
	// =========================================================================================== //
}
