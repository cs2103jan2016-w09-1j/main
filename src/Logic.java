
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
 * Logical code needs to be refined. Also need to write
 * overload method for executeCommand()
 * 
 * 
 * @@author Tay Guo Qiang
 */

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Task;
import cs2103_w09_1j.esther.State;
import cs2103_w09_1j.esther.InvalidInputException;

class Logic {
	
	private Parser _parser;
	private Storage _storage;
	private ArrayList<Task> _tasks;
	private Stack<State> _undoStack;
	
	private enum Status {
		STATUS_SUCCESS_ADD, STATUS_ERROR_ADD, STATUS_SUCCESS_DELETE, STATUS_ERROR_DELETE,
		STATUS_SUCCESS_UPDATE, STATUS_ERROR_UPDATE, STATUS_ERROR_UPDATE_NOT_FOUND,
		STATUS_SUCCESS_SET_COMPLETED, STATUS_ERROR_SET_COMPLETED, STATUS_SUCCESS_UNDO,
		STATUS_ERROR_UNDO, STATUS_UNKNOWN_STATE
	}
	
	Status _status;
	
	private static final String MESSAGE_SUCCESS_ADD = "%1$s is successfully added to file.\n";
	private static final String MESSAGE_ERROR_ADD = "[ERROR] Failed to add %1$s to file.\n";
	private static final String MESSAGE_SUCCESS_DELETE = "%1$s is successfully deleted from file.\n";
	private static final String MESSAGE_ERROR_DELETE = "[ERROR] Failed to delete %1$s from file.\n";
	private static final String MESSAGE_SUCCESS_UPDATE = "%1$s is successfully updated.\n";
	private static final String MESSAGE_ERROR_UPDATE_NOT_FOUND = "[ERROR] Task with supplied name or ID not found.\n";
	private static final String MESSAGE_ERROR_UPDATE = "[ERROR] Failed to update %1$s.\n";
	private static final String MESSAGE_SUCCESS_SET_COMPLETED = "%1$s is marked as completed.\n";
	private static final String MESSAGE_ERROR_SET_COMPLETED = "[ERROR] Failed to mark %1$s as completed.\n";
	private static final String MESSAGE_SUCCESS_UNDO = "Undo is successful.\n";
	private static final String MESSAGE_ERROR_UNDO = "[ERROR] Cannot undo any further.\n";
	private static final String MESSAGE_UNKNOWN_STATE = "[ERROR] Command not recognized.\n";
	private static final String MESSAGE_HELP = "List of commands are:\n1. add\n2. delete\n3. update\n"
											   + "4.completed\n5.undo\n\n" + "Note that for these commands, "
											   + "_value_ indicates that these fields are compulsory and need "
											   + "to be substituted with the relevant values.\n"
											   + "[optional] indicates optional fields to input.\n\n"
											   + "Using the 'add' command:\n"
											   + "General usage: add _task name_ [on _date/time_]\n"
											   + "-> 'add something on this date or time'\n"
											   + "add _task name_ (adds a task with the specified task name)\n"
											   + "add _task name_ on _date/time_ (adds task with deadline)\n\n"
											   + "Using the 'delete' command:\n"
											   + "General usage: delete _task name/task ID_\n"
											   + "-> 'delete something'\n"
											   + "delete _task name_ (deletes a task with exact matching name)\n"
											   + "delete _task ID_ (deletes a task with exact matching ID)\n\n"
											   + "Using the 'update' command:\n"
											   + "General usage: update _task name/task ID_ _field name_ to _value_\n"
											   + "-> 'update something to something else'\n"
											   + "update _task name_ time to _time_ (updates time for the task)\n"
											   + "update _task name_ name to _name_ (changes the name of task)\n\n"
											   + "Using the 'completed' command:\n"
											   + "General usage: completed _task name_\n"
											   + "-> 'completed a task'\n\n"
											   + "Using the 'undo' command:\n"
											   + "General usage: undo\n" + "Undo one step back to previous state.\n";
	
	/**
	 * Constructs a Logic component instance.
	 */
	public Logic() {
		_parser = new Parser();
		_storage = new Storage();
		_tasks = new ArrayList<Task>();
		_undoStack = new Stack<State>();
	}

	/**
	 * Retrieves the internal memory that is used by Logic.
	 * Used only for internal testing.
	 * 
	 * @return the internal memory representation of the
	 * 		   contents stored in the text file.
	 */
	public ArrayList<Task> getInternalStorage() {
		return _tasks;
	}
	
	/**
	 * Updates the internal memory of the Logic to account
	 * for any changes done to the text file.
	 */
	// TODO: finalize implementation, error handling
	public void updateInternalStorage() {
		/*
		try {
			_tasks = _storage.readFromFile();
			Collections.sort(_tasks);
		} catch (IOException ioe) {
			// set Exception state
			// get error message
			// return error message
		}
		*/
	}

	/**
	 * An overloaded method that operates on an input and passes the call
	 * to the actual handler method.
	 * 
	 * @see    Logic#executeCommand(Command)
	 * @param  userInput the input that the user entered
	 * @return a message indicating the status of the operation carried out
	 */
	// TODO: finalize implementation
	public String executeCommand(String userInput) {
		Command command = _parser.acceptUserInput(userInput);
		return executeCommand(command);
	}
	
	/**
	 * This method acts as the main handler for all user
	 * operations. This handler will attempt to execute a
	 * command and informs the user of the status of the
	 * operation that is carried out.
	 * 
	 * @param  command	the Command object containing all required information
	 * @return a message indicating the status of the operation carried out
	 */
	public String executeCommand(Command command) {
		String commandType = command.getCommand();
		String statusMessage;
		switch (commandType) {
			case "add" : 
				statusMessage = addTask(command);
				break;
				
			case "delete" :
				statusMessage = removeTask(command);
				break;
				
			case "update" :
				statusMessage = updateTask(command);
				break;
				
			case "completed" :
				statusMessage = completeTask(command);
				break;
				
			case "show" :
				statusMessage = showTask(command);
				break;
				
			case "sort" :
				statusMessage = sortFile(command);
				break;
				
			case "undo" :
				statusMessage = undo();
				break;
				
			case "help" :
				// TODO: finalize implementation
				statusMessage = MESSAGE_HELP;
				break;
				
			default :
				_status = Status.STATUS_UNKNOWN_STATE;
				statusMessage = getStatusMessage(null, null); 
				break;
		}
		return statusMessage;
	}
	
	/**
	 * Stores the program state before a user operation was performed.
	 * 
	 * @param  task	a Task object representation of the user's input
	 * @return a Task object that has an opposite command to be
	 * 		   performed on it, where applicable.
	 */
	private State storePreviousState(Command command, Task original) {
		String commandType = command.getCommand();
		State previous = null;
		switch (commandType) {
			case "add" :
				previous = new State(commandType);
				previous.storeOriginalTaskState(original);
				break;

			case "delete" :
				previous = new State(commandType);
				previous.storeOriginalTaskState(original);
				break;

			case "update" :
				previous = new State(commandType);
				previous.storeOriginalTaskState(original);
				break;
				
			case "completed" :
				previous = new State(commandType);
				previous.storeOriginalTaskState(original);
				break;

			case "show" :
				previous = new State(commandType);
				previous.setSortOrder(command.getSpecificParameter("order"));
				ArrayList<Task> preDisplayTaskList = (ArrayList<Task>) _tasks.clone();
				previous.storeInnerMemoryState(preDisplayTaskList);
				break;

			case "sort" :
				previous = new State(commandType);
				previous.setSortOrder(command.getSpecificParameter("order"));
				ArrayList<Task> preSortTaskList = (ArrayList<Task>) _tasks.clone();
				previous.storeInnerMemoryState(preSortTaskList);
				break;

			case "undo" :
				break;

			case "help" :
				break;
				
			default :
				previous = new State("Invalid");
				break;
		}
		return previous;
	}
	
	/**
	 * Retrieves the status message depending on the status
	 * of an operation being carried out by the program logic.
	 * 
	 * @return the corresponding status message based on operation status
	 */
	// TODO: adjust this method to reflect Mingxuan's implementations
	// presently, sort and show states are missing in here.
	private String getStatusMessage(String taskName, String taskId) {
		String message;
		switch (_status) {
			case STATUS_SUCCESS_ADD :
				message = String.format(MESSAGE_SUCCESS_ADD, taskName);
				break;
				
			case STATUS_ERROR_ADD :
				message = String.format(MESSAGE_ERROR_ADD, taskName);
				break;
				
			case STATUS_SUCCESS_DELETE :
				if (taskName != null) {
					message = String.format(MESSAGE_SUCCESS_DELETE, taskName);
				} else {
					message = String.format(MESSAGE_SUCCESS_DELETE, taskId);
				}
				break;
				
			case STATUS_ERROR_DELETE :
				if (taskName != null) {
					message = String.format(MESSAGE_ERROR_DELETE, taskName);
				} else {
					message = String.format(MESSAGE_ERROR_DELETE, taskId);
				}
				break;
				
			case STATUS_SUCCESS_UPDATE :
				if (taskName != null) {
					message = String.format(MESSAGE_SUCCESS_UPDATE, taskName);
				} else {
					message = String.format(MESSAGE_SUCCESS_UPDATE, taskId);
				}
				break;
				
			case STATUS_ERROR_UPDATE :
				if (taskName != null) {
					message = String.format(MESSAGE_ERROR_UPDATE, taskName);
				} else {
					message = String.format(MESSAGE_ERROR_UPDATE, taskId);
				}
				break;
				
			case STATUS_ERROR_UPDATE_NOT_FOUND :
				message = MESSAGE_ERROR_UPDATE_NOT_FOUND;
				break;
				
			case STATUS_SUCCESS_SET_COMPLETED :
				if (taskName != null) {
					message = String.format(MESSAGE_SUCCESS_SET_COMPLETED, taskName);
				} else {
					message = String.format(MESSAGE_SUCCESS_SET_COMPLETED, taskId);
				}
				break;
				
			case STATUS_ERROR_SET_COMPLETED :
				if (taskName != null) {
					message = String.format(MESSAGE_ERROR_SET_COMPLETED, taskName);
				} else {
					message = String.format(MESSAGE_ERROR_SET_COMPLETED, taskId);
				}
				break;
				
			case STATUS_SUCCESS_UNDO :
				message = MESSAGE_SUCCESS_UNDO;
				break;
				
			case STATUS_ERROR_UNDO :
				message = MESSAGE_ERROR_UNDO;
				break;
				
			default :
				message = MESSAGE_UNKNOWN_STATE;
				break;
		}
		return message;
	}
	
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
	 */
	// TODO: error handling
	private String addTask(Command command) {
		String taskName = command.getSpecificParameter("taskName");
		try {
			Task task = new Task(command);
			_tasks.add(task);
			_storage.writeToFile(_tasks);
			_undoStack.push(storePreviousState(command, task));
			_status = Status.STATUS_SUCCESS_ADD;
		} catch (ParseException pe) {
			_status = Status.STATUS_ERROR_ADD;
			// set Exception state
			// retrieve error message
			// return error message
		} /*catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
		return getStatusMessage(taskName, null);
	}
	
	/**
	 * Removes the task from the text file.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the delete-task operation
	 */
	// TODO: error handling
	private String removeTask(Command command) {
		Task removed = null;
		String taskName = command.getSpecificParameter("taskName");
		String taskId = command.hasParameter("taskId")
						? command.getSpecificParameter("taskId")
						: "-1";
		for (Task existingTask: _tasks) {
			if (existingTask.getName().equals(taskName) ||
				existingTask.getId() == Integer.parseInt(taskId)) {
				removed = existingTask;
				break;
			}
		}
		// try {
		if (removed != null) {
			_tasks.remove(removed);
			_storage.writeToFile(_tasks);
			_undoStack.push(storePreviousState(command, removed));
			_status = Status.STATUS_SUCCESS_DELETE;
		} else {
			_status = Status.STATUS_ERROR_DELETE;
			// set Exception state
			// retrieve error message
			// return error message
		}
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
		return getStatusMessage(taskName, command.getSpecificParameter("taskId"));
	}
	
	/**
	 * Updates a particular task entry in the text file.
	 * 
	 * @param task a Task object representation of the user's input
	 * @return 	   a message indicating the status of the update-task operation
	 */
	// TODO: error handling
	private String updateTask(Command command) {
		Task toUpdate = null;
		int updateIndex = -1;
		String taskName = command.getSpecificParameter("taskName");
		String checkTaskId = command.hasParameter("taskId")
						? command.getSpecificParameter("taskId")
						: "-1";
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
				_undoStack.push(storePreviousState(command, toUpdate.clone()));
				toUpdate.updateTask(command);
				_tasks.set(updateIndex, toUpdate);
				_storage.writeToFile(_tasks);
				_status = Status.STATUS_SUCCESS_UPDATE;
			} else {
				_status = Status.STATUS_ERROR_UPDATE_NOT_FOUND;
			}
		} catch (ParseException pe) {
			_status = Status.STATUS_ERROR_UPDATE;
			// set Exception state
			// retrieve error message
			// return error message
		} /*catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
		return getStatusMessage(taskName, command.getSpecificParameter("taskId"));
	}

	/**
	 * Sets a task as completed.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the set-task-completed operation
	 */
	// TODO: error handling
	private String completeTask(Command command) {
		Task toUpdate = null;
		String taskName = command.getSpecificParameter("taskName");
		String taskId = command.hasParameter("taskId")
						? command.getSpecificParameter("taskId")
						: "-1";
		int updateIndex = -1;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getName().equals(taskName) ||
				_tasks.get(i).getId() == Integer.parseInt(taskId)) {
				toUpdate = _tasks.get(i);
				updateIndex = i;
				break;
			}
		}
		// try {
		if (toUpdate != null) {
			_undoStack.push(storePreviousState(command, toUpdate.clone()));
			toUpdate.setCompleted(true);
			_tasks.set(updateIndex, toUpdate);
			_storage.writeToFile(_tasks);
			_status = Status.STATUS_SUCCESS_SET_COMPLETED;
		} else {
			_status = Status.STATUS_ERROR_SET_COMPLETED;
			// set Exception state
			// retrieve error message
			// return error message
		}
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
		return getStatusMessage(taskName, command.getSpecificParameter("taskId"));
	} 
	
	/**
	 * Displays all tasks recorded in the text file.
	 * 
	 * NOTE: implementation can be extended to display
	 * only certain tasks to the user.
	 * 
	 * @return a list of all tasks to show to the user.
	 */
	// TODO: error handling
	private String showTask(Command command) {
		// try {
		_undoStack.push(storePreviousState(command, null));
		String sortOrder = command.getSpecificParameter("order");
		if (sortOrder != null) {
			/* 
			 * Should I alter Task's sortCriterion to default
			 * back to priority after user successfully sorts
			 * tasks by his/her own desired order? 
			 */
			Task.setSortCriterion(sortOrder);
			Collections.sort(_tasks);
			_storage.writeToFile(_tasks);
		}
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
		String listToDisplay = "";
		// listToDisplay = _parser.parse(_tasks);
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
	 */
	// TODO: error handling
	private String sortFile(Command command) {
		_undoStack.push(storePreviousState(command, null));
		String sortOrder = command.getSpecificParameter("order");
		// try {
		if (sortOrder != null) {
			/* 
			 * Should I alter Task's sortCriterion to default
			 * back to priority after user successfully sorts
			 * tasks by his/her own desired order? 
			 */
			Task.setSortCriterion(sortOrder);
			Collections.sort(_tasks);
			_storage.writeToFile(_tasks);
		}
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
		return "OK."; // TODO: change to a status message
	}
	
	/**
	 * Undo one step back into the previous state of the program.
	 * 
	 * @return a message indicating status of the undo operation
	 */
	private String undo() {
		try {
			State previousState = _undoStack.pop();
			String commandType = previousState.getCommand();
			switch (commandType) {
				case "add" :
					undoAdd(previousState.getState().get(0));
					break;
					
				case "delete" :
					undoDelete(previousState.getState().get(0));
					break;
					
				case "update" :
					undoUpdate(previousState.getState().get(0));
					break;
					
				case "completed" :
					undoCompleted(previousState.getState().get(0));
					break;
					
				case "show" :
					undoDisplay(previousState.getState());
					break;
					
				case "sort" :
					undoSort(previousState.getState());
					break;
					
				case "help" :
					break;
			
				default :
					_status = Status.STATUS_ERROR_UNDO;
					System.out.println("Unknown error in undo.");
					return getStatusMessage(null, null);
			}
			_status = Status.STATUS_SUCCESS_UNDO;
		} catch (EmptyStackException e) {
			_status = Status.STATUS_ERROR_UNDO;
		}
		return getStatusMessage(null, null);
	}
	
	/**
	 * Reverts an add-task operation.
	 * 
	 * @param task the reference of the initially added task to remove
	 */
	// TODO: error handling
	private void undoAdd(Task task) {
		int taskId = task.getId();
		int removeIndex = -1;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskId) {
				removeIndex = i;
				break;
			}
		}
		_tasks.remove(removeIndex);
		// try {
		_storage.writeToFile(_tasks);
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
	}
	
	/**
	 * Reverts a delete-task operation.
	 * 
	 * @param task a reference of the initially deleted task to add back
	 */
	// TODO: error handling
	private void undoDelete(Task task) {
		_tasks.add(task);
		// try {
		_storage.writeToFile(_tasks);
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
	}
	
	/**
	 * Reverts an update-task operation.
	 * 
	 * @param task a reference of the previous state of a task before it was updated
	 */
	// TODO: error handling
	private void undoUpdate(Task task) {
		int taskId = task.getId();
		int updateIndex = -1;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskId) {
				updateIndex = i;
				break;
			}
		}
		_tasks.set(updateIndex, task);
		// try {
		_storage.writeToFile(_tasks);
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
	}
	
	/**
	 * Reverts a complete-task operation.
	 * 
	 * @param task a reference of a task before it was set as completed
	 */
	// TODO: error handling
	private void undoCompleted(Task task) {
		int taskId = task.getId();
		int updateIndex = -1;
		for (int i = 0; i < _tasks.size(); i++) {
			if (_tasks.get(i).getId() == taskId) {
				updateIndex = i;
				break;
			}
		}
		_tasks.set(updateIndex, task);
		// try {
		_storage.writeToFile(_tasks);
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
	}
	
	/**
	 * Reverts a show-task operation.
	 * 
	 * @param tasks a reference to the previous ordering of tasks
	 */
	// TODO: error handling
	private void undoDisplay(ArrayList<Task> tasks) {
		_tasks = tasks;
		// try {
		_storage.writeToFile(_tasks);
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
	}
	
	/**
	 * Reverts a sort-task operation.
	 * 
	 * @param tasks a reference to the previous ordering of tasks
	 */
	// TODO: error handling
	private void undoSort(ArrayList<Task> tasks) {
		_tasks = tasks;
		// try {
		_storage.writeToFile(_tasks);
		/*} catch (IOException ioe) {
			// set Exception state
			// retrieve error message
			// return error message
		}*/
	}
	
	/*
	 * --- FOR LATER VERSIONS OR TO BE DISCARDED ---
	 * 
	 * These methods below are methods that may be
	 * implemented in the future, for later versions.
	 * As such, these do not need to be finalized at
	 * this stage.
	 * 
	 * The other methods not falling in the above
	 * criteria might be temporarily needed at this
	 * stage. Removal of these methods shall be done
	 * only when product is finalized and is to be
	 * released for production.
	 * 
	 */

	/**
	 * Empties the text file of any contents.
	 * Only used for internal testing.
	 */
	public void flushInternalStorage() {
		/* 
		 * Might want to consider insert try-catch block here
		 * Then again, if this method isn't needed anymore in the future,
		 * this method can just be removed totally. 
		 */
		_storage.flushFile();
		_tasks = new ArrayList<Task>();
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
	 */
	// TODO: implement (for later stages)
	private ArrayList<Task> searchFile(Task task) {
		ArrayList<Task> results = new ArrayList<Task>();
		//_undoStack.push(null);
		updateInternalStorage();
		for (Task entry: _tasks) {
			if (entry.getName().contains(task.getName())) {
				results.add(entry);
			}
		}
		return results;
	}

}
