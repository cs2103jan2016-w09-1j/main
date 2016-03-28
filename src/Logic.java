
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.*;
import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Command.CommandKey;
import cs2103_w09_1j.esther.Task.TaskField;
import cs2103_w09_1j.esther.Config;
import cs2103_w09_1j.esther.Task;
import cs2103_w09_1j.esther.State;
import cs2103_w09_1j.esther.Status;
import cs2103_w09_1j.esther.InvalidInputException;

class Logic {
	
	private static final int NOT_FOUND_INDEX = -1;
	private static final String EMPTY_STATE = "Empty";
	private static final String DEFAULT_TASKS_SORT_ORDER = "date";
	private static final String DEFAULT_FLOATING_TASKS_SORT_ORDER = "id";
	
	private Parser _parser;
	private Storage _storage;
	private ArrayList<Task> _tasks;
	private ArrayList<Task> _floatingTasksHolder;
	private Stack<State> _undoStack;
	private Config _config;
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
	 * @@author A0129660A
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
	 * @@author A0129660A
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
			return Status.getMessage(null, null, null);
		} catch (ParseException pe) {
			//logger.log(Level.WARNING, "Invalid input supplied by user.");
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.INVALID_COMMAND;
			return Status.getMessage(null, null, null);
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
	 * @@author A0129660A
	 */
	// TODO: implement search functionality
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
				
			case SHOW :
				statusMessage = showTask(command);
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
	 * @@author A0129660A
	 */
	protected ArrayList<Task> getInternalStorage() {
		ArrayList<Task> display = new ArrayList<Task>();
		display.addAll(_tasks);
		display.addAll(_floatingTasksHolder);
		return display;
	}
	
	/**
	 * Retrieves the internal memory that is used by Logic.
	 * This internal memory is represented in a whole String.
	 * 
	 * @return the internal memory representation of the
	 * 		   contents stored in the text file, in String
	 * 		   form.
	 * @@author A0129660A
	 */
	public String getInternalStorageInString() {
		String listToDisplay = "";
		for (int i = 0; i < _tasks.size(); i++) {
			listToDisplay += _tasks.get(i).toString() + "\n";
		}
		return listToDisplay;
	}
	
	/**
	 * Initializes a system logger. Used for testing purposes only.
	 * 
	 * @@author A0129660A
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
	 * @@author A0129660A
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
	 * @@author A0129660A
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
	 * @@author A0129660A
	 */
	private void initializeLogicSystemVariables() throws ParseException, IOException {
		updateInternalStorage();
		_undoStack = new Stack<State>();
		//logger.logp(Level.CONFIG, "Logic", "updateInternalStorage",
					//"Reading tasks into inner memory upon initialization.");
		//System.out.println("Inner variables initialized.");
	}
	
	/**
	 * Attaches a shutdown-event handler to perform necessary system updates
	 * when ESTHER is shut down.
	 * 
	 * @@author A0129660A
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
	 * @@author A0129660A
	 */
	// TODO: finalize implementation, error handling
	// current implementation is to terminate program, for now
	public void updateInternalStorage() {
		//logger.logp(Level.INFO, "Logic", "updateInternalStorage()", "Retrieving tasks list from Storage.");
		try {
			_tasks = _storage.readSaveFile();
			assert _tasks != null;
			_floatingTasksHolder = new ArrayList<Task>();
			filterFloatingTasks(DEFAULT_TASKS_SORT_ORDER, false);
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
	 * @@author A0129660A
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
		_tasks = new ArrayList<Task>();
	}
	
	/**
	 * Update Logic's list of tasks into text file.
	 * 
	 * @throws IOException
	 * @@author A0129660A
	 */
	private void updateTextFile() throws IOException {
		_storage.writeSaveFile(_tasks);
	}
	
	/**
	 * Updates the undo stack whenever a user operation is carried out.
	 * 
	 * @param command a Command object representing the user operation being carried out
	 * @param task a Task object representing the task that was operated on
	 * @@author A0129660A
	 */
	private void updateUndoStack(Command command, Task task) {
		_undoStack.push(storePreviousState(command, task));
	}
	
	/**
	 * Separates floating tasks from tasks with deadlines for proper sorting.
	 * 
	 * @@author A0129660A
	 */
	private void filterFloatingTasks(String sortOrder, boolean toSort) {
		_floatingTasksHolder = new ArrayList<Task>();
		Iterator<Task> iter = _tasks.iterator();
		while (iter.hasNext()) {
			Task currentTask = iter.next();
			//System.out.println(currentTask.isFloatingTask());
			if (currentTask.isFloatingTask()) {
				//System.out.println("Floating task present.");
				_floatingTasksHolder.add(currentTask);
				iter.remove();
			}
		}
		
		if (toSort) {
			Task.setSortCriterion(sortOrder);
			Collections.sort(_tasks);
			Task.setSortCriterion(DEFAULT_FLOATING_TASKS_SORT_ORDER);
			Collections.sort(_floatingTasksHolder);
		}
		_tasks.addAll(_floatingTasksHolder);
		Task.setSortCriterion(DEFAULT_TASKS_SORT_ORDER);
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
	 * @@author A0129660A
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
	 * @@author A0129660A
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
	 * @@author A0129660A
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
	 * @@author A0129660A
	 */
	private String completeTask(Command command) {
		//logger.logp(Level.INFO, "Logic", "completeTask(Command command)", "Completing a task.", params);
		completeTaskInFile(command);
		return getOperationStatus(command);
	}
	
	/**
	 * Displays all tasks recorded in the text file.
	 * 
	 * NOTE: implementation can be extended to display
	 * only certain tasks to the user.
	 * 
	 * @return a list of all tasks to show to the user.
	 * @@author A0129660A
	 */
	private String showTask(Command command) {
		updateUndoStack(command, null);
		//System.out.println(_undoStack.size());
		sortAndUpdateFile(command);
		String result = getInternalStorageInString(); 
		Status._outcome = Status.Outcome.SUCCESS;
		return result;
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
	 * @@author A0129660A
	 */
	private String sortFile(Command command) {
		updateUndoStack(command, null);
		//System.out.println(_undoStack.size());
		sortAndUpdateFile(command);
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
	 * @@author A0129660A
	 */
	// TODO: implement (for later stages)
	private String/*ArrayList<Task>*/ searchFile(Command command) {
		String results = "Search:\n";
		// String searchKey = command.getSpecificParameter("something_for_search");
		//logger.logp(Level.INFO, "Logic", "searchFile(Command command)",
				  	  //"Searching tasks in file.", searchKey);
		//ArrayList<Task> results = new ArrayList<Task>();
		for (Task entry: _tasks) {
			String taskNameCopy = entry.getName();
			String taskNameLowerCase = taskNameCopy.toLowerCase();
			if (taskNameLowerCase.contains(command.getSpecificParameter("taskName").trim().toLowerCase())) {
				results += (entry.toString() + "\n");
				//results.add(entry);
			}
		}
		System.out.println(results);
		return results;
	}
	
	/**
	 * Undo one step back into the previous state of the program.
	 * 
	 * @param command a Command objecting representing the user operation to be carried out
	 * @return a message indicating status of the undo operation
	 * @@author A0129660A
	 */
	private String undo(Command command) {
		if (_undoStack.size() <= 1) {
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
					undoAdd(previousState.getState().get(0));
					break;
					
				case DELETE :
					undoDelete(previousState.getState().get(0));
					break;
					
				case UPDATE :
					undoUpdate(previousState.getState().get(0));
					break;
					
				case COMPLETE :
					undoCompleted(previousState.getState().get(0));
					break;
					
				case SHOW :
					undoDisplay(previousState.getState());
					break;
					
				case SORT :
					undoSort(previousState.getState());
					break;
					
				case SEARCH :
				 	// TODO for Parser: add enum field + value
					// undo a search does not make logical sense
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
			addedTask = new Task(command);
			_tasks.add(addedTask);
			if (!addedTask.isFloatingTask()) {
				filterFloatingTasks(null, false);
			}
			updateTextFile();
			updateUndoStack(command, addedTask);
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
	 * Retrieves the index of a task if a task can be found. NOT_FOUND_INDEX is returned
	 * if there are duplicate tasks found or if no tasks can be found.
	 * 
	 * @param command the Command object representing the user operation being carried out
	 * @return the index of a task, if a single task can be located; -1 if no task can be found
	 * 		   or if there are duplicate tasks
	 * @@author A0129660A
	 */
	private int getTaskIndex(Command command) {
		int index = -1;
		boolean hasDuplicate = false;
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());
		//System.out.println("Task name to update: " + taskName);
		String taskID = command.hasParameter(TaskField.ID.getTaskKeyName())
						? command.getSpecificParameter(TaskField.ID.getTaskKeyName())
						: String.valueOf(NOT_FOUND_INDEX);
		//System.out.println(taskID);
		String[] params = {taskName, command.getSpecificParameter(TaskField.ID.getTaskKeyName())};
		//logger.logp(Level.INFO, "Logic", "removeTask(Command command)",	"Removing a task.", params);
		for (int i = 0; i < _tasks.size(); i++) {
			//System.out.println("Current task accessed is " + _tasks.get(i).getName());
			if (_tasks.get(i).getName().equals(taskName) ||
				_tasks.get(i).getId() == Integer.parseInt(taskID)) {
				if (index != NOT_FOUND_INDEX) {
					hasDuplicate = true;
				}
				index = i;
			}
		}
		if (hasDuplicate) {
			index = NOT_FOUND_INDEX;
		}
		System.out.println(index);
		return index;
	}
	
	/**
	 * Removes a task from both Logic's internal storage as well as from the text file.
	 * 
	 * @param command a Command object representing the user operation being carried out
	 */
	private void removeTaskAndUpdateFile(Command command) {
		Task removed = null;
		int taskIndex = getTaskIndex(command);
		try {
			if (taskIndex != NOT_FOUND_INDEX) {
				removed = _tasks.get(taskIndex);
				_tasks.remove(removed);
				updateTextFile();
				updateUndoStack(command, removed);
				//System.out.println(_undoStack.size());
				Status._outcome = Status.Outcome.SUCCESS;
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
	 * @@author A0129660A
	 */
	private void updateTaskInFile(Command command) {
		Task toUpdate = null;
		int taskIndex = getTaskIndex(command);
		
		try {
			if (taskIndex != NOT_FOUND_INDEX) {
				//String old = toUpdate.getName();
				toUpdate = _tasks.get(taskIndex);
				Task copyOfOldTask = toUpdate.clone();
				boolean isUpdated = toUpdate.updateTask(command);
				if (isUpdated) {
					_tasks.set(taskIndex, toUpdate);
					updateTextFile();
					updateUndoStack(command, copyOfOldTask);
					//System.out.println(_undoStack.size());
					//System.out.println("Old name: " + old + " New name: " + _tasks.get(updateIndex).getName());
					Status._outcome = Status.Outcome.SUCCESS;
				} else {
					Status._outcome = Status.Outcome.ERROR;
					Status._errorCode = Status.ErrorCode.UPDATE_START_END_VIOLATE;
				}
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
	 * @@author A0129660A
	 */
	private void completeTaskInFile(Command command) {
		Task toUpdate = null;
		int taskIndex = getTaskIndex(command);

		try {
			if (taskIndex != NOT_FOUND_INDEX) {
				toUpdate = _tasks.get(taskIndex);
				if (toUpdate.isCompleted()) {
					Status._outcome = Status.Outcome.ERROR;
				}
				else {
					Task copyOfOldTask = toUpdate.clone();
					toUpdate.setCompleted(true);
					_tasks.set(taskIndex, toUpdate);
					updateTextFile();
					updateUndoStack(command, copyOfOldTask);
					//System.out.println(_undoStack.size());
					Status._outcome = Status.Outcome.SUCCESS;
				}
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
		System.out.println(sortOrder);
		try {
			//logger.logp(Level.INFO, "Logic", "sortFile(Command command)",
						//"Sorting all tasks by user-specified order.", sortOrder);
			if (sortOrder == null) {
				sortOrder = DEFAULT_TASKS_SORT_ORDER;
			}
			filterFloatingTasks(sortOrder, true);
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
	 * @@author A0129660A
	 */
	private State storePreviousState(Command command, Task original) {
		//logger.logp(Level.INFO, "Logic", "storePreviousState(Command command, Task original)",
					//"Storing previous program memory state.");
		String commandName = command.getCommand();
		CommandKey commandType = CommandKey.get(commandName);
		State previous = null;
		switch (commandType) {
			case ADD :
				previous = new State(commandName);
				previous.storeOriginalTaskState(original);
				break;

			case DELETE :
				previous = new State(commandName);
				previous.storeOriginalTaskState(original);
				break;

			case UPDATE :
				previous = new State(commandName);
				previous.storeOriginalTaskState(original);
				break;
				
			case COMPLETE :
				previous = new State(commandName);
				previous.storeOriginalTaskState(original);
				break;

			case SHOW :
				previous = new State(commandName);
				assert command.getSpecificParameter(TaskField.SHOW.getTaskKeyName()) != null;
				previous.setSortOrder(command.getSpecificParameter(TaskField.SHOW.getTaskKeyName()));
				ArrayList<Task> preDisplayTaskList = (ArrayList<Task>) _tasks.clone();
				previous.storeInnerMemoryState(preDisplayTaskList);
				break;

			case SORT :
				previous = new State(commandName);
				assert command.getSpecificParameter(TaskField.SORT.getTaskKeyName()) != null;
				previous.setSortOrder(command.getSpecificParameter(TaskField.SORT.getTaskKeyName()));
				ArrayList<Task> preSortTaskList = (ArrayList<Task>) _tasks.clone();
				previous.storeInnerMemoryState(preSortTaskList);
				break;
				
			case SEARCH : // TODO for Parser: add enum field + value
				previous = new State(EMPTY_STATE);
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
	private void undoAdd(Task task) {
		int taskID = task.getId();
		int removeIndex = NOT_FOUND_INDEX;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskID) {
				removeIndex = i;
				break;
			}
		}
		assert removeIndex != NOT_FOUND_INDEX;
		_tasks.remove(removeIndex);
		try {
			_storage.writeSaveFile(_tasks);
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
	private void undoDelete(Task task) {
		_tasks.add(task);
		try {
			_storage.writeSaveFile(_tasks);
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
	private void undoUpdate(Task task) {
		int taskID = task.getId();
		int updateIndex = NOT_FOUND_INDEX;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskID) {
				updateIndex = i;
				break;
			}
		}
		assert updateIndex != NOT_FOUND_INDEX;
		_tasks.set(updateIndex, task);
		try {
			_storage.writeSaveFile(_tasks);
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
	private void undoCompleted(Task task) {
		int taskID = task.getId();
		int updateIndex = NOT_FOUND_INDEX;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskID) {
				updateIndex = i;
				break;
			}
		}
		assert updateIndex != NOT_FOUND_INDEX;
		_tasks.set(updateIndex, task);
		try {
			_storage.writeSaveFile(_tasks);
			Status._outcome = Status.Outcome.SUCCESS;
		} catch (IOException ioe) {
			Status._outcome = Status.Outcome.ERROR;
			Status._errorCode = Status.ErrorCode.SYSTEM;
		}
	}
	
	/**
	 * Reverts a show-task operation.
	 * 
	 * @param tasks a reference to the previous ordering of tasks
	 */
	// TODO: error handling
	private void undoDisplay(ArrayList<Task> tasks) {
		_tasks = tasks;
		try {
			_storage.writeSaveFile(_tasks);
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
	private void undoSort(ArrayList<Task> tasks) {
		_tasks = tasks;
		try {
			_storage.writeSaveFile(_tasks);
			Status._outcome = Status.Outcome.SUCCESS;
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
	 * @@author A0129660A
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
	 * @@author A0129660A
	 */
	private boolean isUndoCommand(Command command) {
		return command.getCommand().equals("undo");
	}

	// ====================== LOWER-LEVEL USER OPERATION METHODS FOR LOGIC ======================= //
	// =========================================================================================== //
}
