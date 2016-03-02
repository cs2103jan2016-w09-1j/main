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
 * @author Tay Guo Qiang
 */

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
	 * Creates a Logic instance. Also initializes the undo stack.
	 */
	public Logic() {
		_parser = new Parser();
		_storage = new Storage();
		_undoStack = new Stack<State>();
	}

	/**
	 * Retrieves the internal memory that is used by Logic.
	 * Used only for internal testing.
	 * 
	 * @return the internal memory representation of the
	 * 		   contents stored in the text file.
	 */
	ArrayList<Task> getInternalStorage() {
		return _tasks;
	}
	
	/**
	 * Updates the internal memory of the Logic to account
	 * for any changes done to the text file.
	 */
	public void updateInternalStorage() {
		_tasks = new ArrayList<Task>();
		// TODO: might want to consider inserting try-catch block here
		ArrayList<String> entriesList = _storage.readFromFile();
		for (int i = 0; i < entriesList.size(); i++) {
			// TODO: add [String --> Task] into _tasks
		}
	}
	
	/**
	 * Retrieves the undo stack of the program logic.
	 * The undo stack is maintained to accommodate the
	 * undo functionality.
	 * 
	 * @see 	Logic#undo()
	 * @return	the undo stack of the program logic
	 */
	public Stack<State> getUndoStack() {
		return _undoStack;
	}

	// TODO: switch statement to at least be able to handle
	// add, update, delete, show_all, sort, undo
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
				displayAll();
				statusMessage = "Not valid.";
				break;
				
			case "sort" :
				statusMessage = sortFile();
				break;
				
			case "undo" :
				statusMessage = undo();
				break;
				
			case "help" :
				statusMessage = MESSAGE_HELP;
				break;
				
			default :
				_status = Status.STATUS_UNKNOWN_STATE;
				statusMessage = getStatusMessage(null, null); 
				break;
		}
		return statusMessage;
	}
	
	// TODO: create a Task object that is the reverse of the one supplied
	/**
	 * 
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a Task object that has an opposite command to be
	 * 				performed on it, where applicable.
	 */
	private State storePreviousState(Command command, Task original) {
		String commandType = command.getCommand();
		State previous = null;
		switch (commandType) {
			case "add" :
				previous = new State("delete");
				previous.storeOriginalTaskState(original);
				break;

			case "delete" :
				previous = new State("add");
				previous.storeOriginalTaskState(original);
				break;

			case "update" :
				previous = new State("update");
				previous.storeOriginalTaskState(original);
				break;

			case "show" :
				previous = new State("show");
				previous.storeInnerMemoryState(_tasks);
				break;

			case "sort" :
				previous = new State("sort");
				previous.storeInnerMemoryState(_tasks);
				break;

			case "undo" :
				break;

			case "help" :
				break;
				
			default :
				previous = new State("Invalid");
				System.out.println("Not supposed to happen.");
				break;
		}
		return previous;
	}
	
	// TODO: also accommodate for sort and show_all
	/**
	 * Retrieves the status message depending on the status
	 * of an operation being carried out by the program logic.
	 * 
	 * @return the corresponding status message based on operation status
	 */
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
		}
		return getStatusMessage(taskName, null);
	}
	
	/**
	 * Removes the task from the text file.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the delete-task operation
	 */
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
		if (removed != null) {
			_tasks.remove(removed);
			_storage.writeToFile(_tasks);
			_undoStack.push(storePreviousState(command, removed));
			_status = Status.STATUS_SUCCESS_DELETE;
		} else {
			_status = Status.STATUS_ERROR_DELETE;
		}
		return getStatusMessage(taskName, command.getSpecificParameter("taskId"));
	}
	
	/**
	 * Updates a particular task entry in the text file.
	 * 
	 * @param task a Task object representation of the user's input
	 * @return 	   a message indicating the status of the update-task operation
	 */
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
				_undoStack.push(storePreviousState(command, toUpdate));
				Task updatedTask = toUpdate;
				updatedTask.updateTask(command);
				_tasks.set(updateIndex, updatedTask);
				_storage.writeToFile(_tasks);
				_status = Status.STATUS_SUCCESS_UPDATE;
			} else {
				_status = Status.STATUS_ERROR_UPDATE_NOT_FOUND;
			}
		} catch (ParseException pe) {
			_status = Status.STATUS_ERROR_UPDATE;
		}
		return getStatusMessage(taskName, command.getSpecificParameter("taskId"));
	}

	/**
	 * Sets a task as completed.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the set-task-completed operation
	 */
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
		if (toUpdate != null) {
			_undoStack.push(storePreviousState(command, toUpdate));
			Task updatedTask = toUpdate;
			updatedTask.setCompleted(true);
			_tasks.set(updateIndex, updatedTask);
			// TODO: insert try-catch block here
			_storage.writeToFile(_tasks);
			_status = Status.STATUS_SUCCESS_SET_COMPLETED;
		} else {
			_status = Status.STATUS_ERROR_SET_COMPLETED;
		}
		return getStatusMessage(taskName, command.getSpecificParameter("taskId"));
	} 
	
	/**
	 * Undo one step back into the previous state of the program.
	 * 
	 * @return a message indicating status of the undo operation
	 */
	// TODO: to fix
	private String undo() {
		try {
			State previousState = _undoStack.pop();
			//_tasks = previousState.getState();
			_status = Status.STATUS_SUCCESS_UNDO;
		} catch (EmptyStackException e) {
			_status = Status.STATUS_ERROR_UNDO;
		}
		return getStatusMessage(null, null);
	}
	
	
	/*
	 * --- TO BE IMPLEMENTED IN LATER VERSIONS ---
	 * 
	 * These methods below are methods that may be
	 * implemented in the future, for later versions.
	 * As such, these do not need to be finalized at
	 * this stage.
	 * 
	 */

	/**
	 * Empties the text file of any contents.
	 * Only used for internal testing.
	 */
	public void flushInternalStorage() {
		/* Might want to consider insert try-catch block here
		 * Then again, if this method isn't needed anymore in the future,
		 * this method can just be removed totally. 
		 */
		_storage.flushFile();
		_tasks = new ArrayList<Task>();
	}
	
	// TODO: implement this
	/**
	 * Displays all tasks recorded in the text file.
	 * 
	 * NOTE: implementation can be extended to display
	 * only certain tasks to the user.
	 * 
	 * @return a list of all tasks to show to the user.
	 */
	private ArrayList<Task> displayAll() {
		//_undoStack.push(null);
		return _tasks;
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
	private String sortFile() {
		_undoStack.push(null); // TODO: need to store a previous ordering
		Collections.sort(_tasks);
		_storage.writeToFile(_tasks);
		return "OK.";
	}
	
	/**
	 * Searches for a task based on the user's desired criteria.
	 * 
	 * NOTE: default search is by whether a task contains a specified
	 * task name or not. This implementation can be extended to support
	 * varying user criteria.
	 * 
	 * @param task	a Task object representation of a keyword to lookup
	 * @return		a list of Task objects that match the search criteria
	 */
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
