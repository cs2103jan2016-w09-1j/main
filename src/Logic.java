
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
 * TODO:
 * 1. For SEARCH function, call UI method to pass data to UI
 *    (compulsory implementation, confirm with Mingxuan)
 * 2. Read data from config file after initializing Storage,
 *    retrieve HashMap of fields from there (Storage will have
 *    2 initialization methods for different things: Config +
 *    ArrayList<Task>)
 * 3. Pass this HashMap to Parser after initializing Parser
 * 
 * @@author A0129660A
 */

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
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
	
	//private UI _ui; // -> TODO: WHICH INSTANCE OF UI SHOULD I GET: UICONTROLLER OR USERINTERFACE?
	private Parser _parser;
	private Storage _storage;
	private ArrayList<Task> _tasks;
	private Stack<State> _undoStack;
	private Config _config; // TODO: for retrieving HashMap of Task Fields to pass to Parser
	//private static Logger //logger = Logger.getLogger("Logic");

	
	// =========================================================================================== //
	// =========================== HIGH-LEVEL IMPLEMENTATION OF LOGIC ============================ //
	// These methods are the highest-level implementation of the core structure of Logic.          //
	// The methods falling into this category are: 												   //
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
		_storage = new Storage();
		//logger.logp(Level.CONFIG, "Storage", "Storage()", "Initializing Storage.");
		assert _storage != null;
		_config = _storage.getConfig();
		//logger.logp(Level.CONFIG, "Storage", "getConfig()", "Initializing Config.");
		assert _config != null;
		//logger.logp(Level.CONFIG, "Parser", "Parser()", "Initializing Parser.");
		_parser = new Parser(/*_config*/); // TODO for Parser: accept HashMap
		assert _parser != null;
		_tasks = new ArrayList<Task>();
		_undoStack = new Stack<State>();
		//logger.logp(Level.CONFIG, "Logic", "updateInternalStorage",
					//"Reading tasks into inner memory upon initialization.");
		updateInternalStorage();
		System.out.println(_config.getReferenceID());
		Task.setGlobalId(_config.getReferenceID());
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	        	System.out.println("Saving current system configurations.");
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
		Command command = _parser.acceptUserInput(userInput);
		// TODO: change this to try-catch in future
		if (command == null) {
			//logger.log(Level.WARNING, "Error from Parser: encountered null Command object.");
			Status._msg = null;
			return Status.getMessage(null, null, null);
		}
		return executeCommand(command);
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
				
			case COMPLETED :
				statusMessage = completeTask(command);
				break;
				
			case SHOW :
				statusMessage = showTask(command);
				break;
				
			case SORT :
				statusMessage = sortFile(command);
				break;
				
			/*case SEARCH : // TODO for Parser: add enum field + value 
				statusMessage = searchFile(command);
				break;*/
				
			case UNDO :
				statusMessage = undo();
				break;
				
			case HELP :
				Status._msg = Status.msg.SUCCESS;
				statusMessage = Status.getMessage(null, null, commandName);
				break;
				
			default :
				assert commandType != null;
				//logger.logp(Level.INFO, "Logic", "executeCommand(Command command)",
							//"Unrecognized command.");
				Status._msg = null;
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
	public ArrayList<Task> getInternalStorage() {
		return _tasks;
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
			Collections.sort(_tasks);
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
	// 2. Add your case statements under 2 methods:												   //
	//    																						   //
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
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());
		//logger.logp(Level.INFO, "Logic", "addTask(Command command)",
					//"Adding a task.", taskName);
		try {
			Task task = new Task(command);
			_tasks.add(task);
			_storage.writeSaveFile(_tasks);
			_undoStack.push(storePreviousState(command, task));
			Status._msg = Status.msg.SUCCESS;
		} catch (ParseException pe) {
			//logger.logp(Level.SEVERE, "Logic", "addTask(Command command)",
						//"Add task: Inappropriate date format passed into Task.", pe);
			Status._msg = Status.msg.ERROR;
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "addTask(Command command)",
					//"Add task: Error in writing to file.", ioe);
			Status._msg = Status.msg.ERROR;
		}
		return Status.getMessage(taskName, null, command.getCommand());
	}
	
	/**
	 * Removes the task from the text file.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the delete-task operation
	 * @@author A0129660A
	 */
	private String removeTask(Command command) {
		Task removed = null;
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());
		String taskID = command.hasParameter(TaskField.ID.getTaskKeyName())
						? command.getSpecificParameter(TaskField.ID.getTaskKeyName())
						: "-1";
		//System.out.println(taskID);
		String[] params = {taskName, command.getSpecificParameter(TaskField.ID.getTaskKeyName())};
		//logger.logp(Level.INFO, "Logic", "removeTask(Command command)",	"Removing a task.", params);
		for (Task existingTask: _tasks) {
			if (existingTask.getName().equals(taskName) ||
				existingTask.getId() == Integer.parseInt(taskID)) {
				removed = existingTask;
				break;
			}
		}
		
		try {
			if (removed != null) {
				_tasks.remove(removed);
				_storage.writeSaveFile(_tasks);
				_undoStack.push(storePreviousState(command, removed));
				Status._msg = Status.msg.SUCCESS;
			} else {
				//logger.logp(Level.WARNING, "Logic", "removeTask(Command command)",
							//"Delete task: Task not found. Possible user-side error or no name/ID matching.");
				Status._msg = Status.msg.ERROR;
			}
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "removeTask(Command command)",
						//"Delete task: cannot write to file.", ioe);
			Status._msg = Status.msg.ERROR;
		}
		return Status.getMessage(taskName, command.getSpecificParameter("taskID"), command.getCommand());
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
		Task toUpdate = null;
		int updateIndex = -1;
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());
		String checkTaskId = command.hasParameter(TaskField.ID.getTaskKeyName())
						? command.getSpecificParameter(TaskField.ID.getTaskKeyName())
						: "-1";
		//System.out.println(checkTaskId);
		String[] params = {taskName, command.getSpecificParameter(TaskField.ID.getTaskKeyName())};
		//logger.logp(Level.INFO, "Logic", "updateTask(Command command)", "Updating a task.", params);
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getName().equals(taskName) ||
				_tasks.get(i).getId() == Integer.parseInt(checkTaskId)) {
				toUpdate = _tasks.get(i);
				updateIndex = i;
				break;
			}
		}
		
		try {
			if (toUpdate != null) {
				String old = toUpdate.getName();
				_undoStack.push(storePreviousState(command, toUpdate.clone()));
				toUpdate.updateTask(command);
				_tasks.set(updateIndex, toUpdate);
				_storage.writeSaveFile(_tasks);
				//System.out.println("Old name: " + old + " New name: " + _tasks.get(updateIndex).getName());
				Status._msg = Status.msg.SUCCESS;
			} else {
				//logger.logp(Level.WARNING, "Logic", "updateTask(Command command)",
							//"Update task: Task not found. Possible user-side error or no name/ID matching.");
				Status._msg = Status.msg.ERROR;
			}
		} catch (ParseException pe) {
			//logger.logp(Level.SEVERE, "Logic", "updateTask(Command command)",
						//"Update task: Inappropriate date formated passed into Task.", pe);
			Status._msg = Status.msg.ERROR;
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "updateTask(Command command)",
						//"Update task: cannot write to file.", ioe);
			Status._msg = Status.msg.ERROR;
		}
		return Status.getMessage(taskName, command.getSpecificParameter("taskID"), command.getCommand());
	}

	/**
	 * Sets a task as completed.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the set-task-completed operation
	 * @@author A0129660A
	 */
	private String completeTask(Command command) {
		Task toUpdate = null;
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());
		String taskID = command.hasParameter(TaskField.ID.getTaskKeyName())
						? command.getSpecificParameter(TaskField.ID.getTaskKeyName())
						: "-1";
		//System.out.println(taskID);
		String[] params = {taskName, command.getSpecificParameter(TaskField.ID.getTaskKeyName())};
		//logger.logp(Level.INFO, "Logic", "completeTask(Command command)", "Completing a task.", params);
		int updateIndex = -1;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getName().equals(taskName) ||
				_tasks.get(i).getId() == Integer.parseInt(taskID)) {
				toUpdate = _tasks.get(i);
				updateIndex = i;
				break;
			}
		}

		try {
			if (toUpdate != null) {
				_undoStack.push(storePreviousState(command, toUpdate.clone()));
				toUpdate.setCompleted(true);
				_tasks.set(updateIndex, toUpdate);
				_storage.writeSaveFile(_tasks);
				Status._msg = Status.msg.SUCCESS;
			} else {
				//logger.logp(Level.WARNING, "Logic", "completeTask(Command command)",
							//"Complete task: Task not found. Possible user-side error or no name/ID matching.");
				Status._msg = Status.msg.ERROR;
			}
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "completeTask(Command command)",
						//"Complete task: cannot write to file.", ioe);
			Status._msg = Status.msg.ERROR;
		}
		return Status.getMessage(taskName, command.getSpecificParameter("taskID"), command.getCommand());
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
		try {
			_undoStack.push(storePreviousState(command, null));
			String sortOrder = command.getSpecificParameter(TaskField.SHOW.getTaskKeyName());
			//logger.logp(Level.INFO, "Logic", "showTask(Command command)",
						//"Displaying all tasks by user-specified order.", sortOrder);
			if (sortOrder != null) {
				/* 
				 * Should I alter Task's sortCriterion to default
				 * back to priority after user successfully sorts
				 * tasks by his/her own desired order? 
				 */
				Task.setSortCriterion(sortOrder);
				Collections.sort(_tasks);
				_storage.writeSaveFile(_tasks);
			}
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "showTask(Command command)",
						//"Displaying all tasks: cannot write to file.", ioe);
			Status._msg = Status.msg.ERROR;
			System.out.println(command.getCommand());
			return Status.getMessage(null, null, command.getCommand());
		}
		String listToDisplay = "";
		for (int i = 0; i < _tasks.size(); i++) {
			listToDisplay += _tasks.get(i).toString() + "\n";
		}
		Status._msg = Status.msg.SUCCESS;
		return listToDisplay;
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
		_undoStack.push(storePreviousState(command, null));
		String sortOrder = command.getSpecificParameter(TaskField.SHOW.getTaskKeyName());
		try {
			//logger.logp(Level.INFO, "Logic", "sortFile(Command command)",
						//"Sorting all tasks by user-specified order.", sortOrder);
			if (sortOrder != null) {
				/* 
				 * Should I alter Task's sortCriterion to default
				 * back to priority after user successfully sorts
				 * tasks by his/her own desired order? 
				 */
				Task.setSortCriterion(sortOrder);
				Collections.sort(_tasks);
				_storage.writeSaveFile(_tasks);
				Status._msg = Status.msg.SUCCESS;
			}
		} catch (IOException ioe) {
			//logger.logp(Level.SEVERE, "Logic", "sortFile(Command command)",
						//"Sort file: cannot write to file.", ioe);
			Status._msg = Status.msg.ERROR;
		}
		return Status.getMessage(null, null, command.getCommand());
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
		String stringResults = "search";
		// String searchKey = command.getSpecificParameter("something_for_search");
		//logger.logp(Level.INFO, "Logic", "searchFile(Command command)",
				  	  //"Searching tasks in file.", searchKey);
		//ArrayList<Task> results = new ArrayList<Task>();
		//updateInternalStorage();
		for (Task entry: _tasks) {
			if (entry.getName().contains(command.getSpecificParameter("taskName"))) {
				stringResults += (entry.toString() + "\n");
				//results.add(entry);
			}
		}
		//return results;
		System.out.println(stringResults);
		return stringResults;
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
				
			case COMPLETED :
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
				assert command.getSpecificParameter(TaskField.SHOW.getTaskKeyName()) != null;
				previous.setSortOrder(command.getSpecificParameter(TaskField.SHOW.getTaskKeyName()));
				ArrayList<Task> preSortTaskList = (ArrayList<Task>) _tasks.clone();
				previous.storeInnerMemoryState(preSortTaskList);
				break;
				
			/*case SEARCH : // TODO for Parser: add enum field + value
				// not needed to store a State for searching tasks
				break;*/

			case UNDO :
				break;

			case HELP :
				break;
				
			default :
				previous = new State("Invalid");
				//logger.logp(Level.INFO, "Logic", "storePreviousState(Command command, Task original)",
							//"Dummy state is created.");
				break;
		}
		return previous;
	}
	
	/**
	 * Undo one step back into the previous state of the program.
	 * 
	 * @return a message indicating status of the undo operation
	 * @@author A0129660A
	 */
	private String undo() {
		String commandName = "undo";
		try {
			State previousState = _undoStack.pop();
			commandName = previousState.getCommand();
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
					
				case COMPLETED :
					undoCompleted(previousState.getState().get(0));
					break;
					
				case SHOW :
					undoDisplay(previousState.getState());
					break;
					
				case SORT :
					undoSort(previousState.getState());
					break;
					
				/*case SEARCH :
				 	// TODO for Parser: add enum field + value
					// undo a search does not make logical sense
					break;*/
					
				case HELP :
					break;
			
				default :
					//logger.logp(Level.INFO, "Logic", "undo()", "Dummy State encountered.");
					Status._msg = Status.msg.ERROR;
					break;
			}
			//System.out.println("Undo successful.");
			if (commandName != null) {
				Status._msg = Status.msg.SUCCESS;
			}
		} catch (EmptyStackException e) {
			//logger.logp(Level.INFO, "Logic", "undo()", "User cannot undo any further.");
			//System.out.println("Undo not successful.");
			Status._msg = Status.msg.ERROR;
		}
		return Status.getMessage(null, null, commandName);
	}
	
	// ============================ USER OPERATION METHODS FOR LOGIC ============================= //
	// =========================================================================================== //
	
	
	
	// =========================================================================================== //
	// ====================== LOWER-LEVEL USER OPERATION METHODS FOR LOGIC ======================= //
	// These methods are lower-level methods used within user operation methods.				   //
	// During code re-factoring, you may place these lower-level methods here.					   //
	//																							   //
	// =========================================================================================== //
	
	/**
	 * Reverts an add-task operation.
	 * 
	 * @param task the reference of the initially added task to remove
	 */
	// TODO: error handling
	private void undoAdd(Task task) {
		int taskID = task.getId();
		int removeIndex = -1;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskID) {
				removeIndex = i;
				break;
			}
		}
		_tasks.remove(removeIndex);
		try {
			_storage.writeSaveFile(_tasks);
		} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
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
		} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
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
		int updateIndex = -1;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskID) {
				updateIndex = i;
				break;
			}
		}
		_tasks.set(updateIndex, task);
		try {
		_storage.writeSaveFile(_tasks);
		} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
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
		int updateIndex = -1;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskID) {
				updateIndex = i;
				break;
			}
		}
		_tasks.set(updateIndex, task);
		try {
			_storage.writeSaveFile(_tasks);
		} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
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
		} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
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
		} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}
	}

	// ====================== LOWER-LEVEL USER OPERATION METHODS FOR LOGIC ======================= //
	// =========================================================================================== //
}
