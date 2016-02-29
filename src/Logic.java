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
 * Logical code needs to be refined.
 * 
 * 
 * @author Tay Guo Qiang
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Stack;
import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Task;
import cs2103_w09_1j.esther.InvalidInputException;

class Logic {
	
	private Parser _parser;
	private Storage _storage;
	private ArrayList<Task> _taskList;
	private Stack<Task> _undoStack;
	
	private enum Status {
		STATUS_SUCCESS_ADD, STATUS_ERROR_ADD,
		STATUS_SUCCESS_DELETE, STATUS_ERROR_DELETE,
		STATUS_SUCCESS_UPDATE, STATUS_ERROR_UPDATE,
		STATUS_SUCCESS_SET_COMPLETED, STATUS_ERROR_SET_COMPLETED,
		STATUS_SUCCESS_UNDO, STATUS_ERROR_UNDO,
		STATUS_UNKNOWN_STATE
	}
	
	Status _status;
	
	private static final String MESSAGE_SUCCESS_ADD = "%1$s is successfully added to file.\n";
	private static final String MESSAGE_ERROR_ADD = "[ERROR] Failed to add %1$s to file.\n";
	private static final String MESSAGE_SUCCESS_DELETE = "%1$s is successfully deleted from file.\n";
	private static final String MESSAGE_ERROR_DELETE = "[ERROR] Failed to delete %1$s from file.\n";
	private static final String MESSAGE_SUCCESS_UPDATE = "Task is successfully updated.\n";
	private static final String MESSAGE_ERROR_UPDATE = "[ERROR] Failed to update task.\n";
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
		_undoStack = new Stack<Task>();
	}
	
	/**
	 * Setter method to establish a system that allows Logic
	 * to communicate with Parser.
	 * 
	 * @param parser an instance of the Parser component
	 */
	public void setParser(Parser parser) {
		_parser = parser;
	}
	
	/**
	 * Setter method to establish a system that allows Logic
	 * to communicate with Storage.
	 * 
	 * @param storage
	 */
	public void setStorage(Storage storage) {
		_storage = storage;
	}

	/**
	 * Retrieves the internal memory that is used by Logic.
	 * 
	 * @return the internal memory representation of the
	 * 		   contents stored in the text file.
	 */
	public ArrayList<Task> getInternalStorage() {
		return _taskList;
	}
	
	/**
	 * Updates the internal memory of the Logic to account
	 * for any changes done to the text file.
	 */
	public void updateInternalStorage() {
		ArrayList<Task> _taskList = new ArrayList<Task>();
		// TODO: might want to consider inserting try-catch block here
		ArrayList<String> entriesList = _storage.readFromFile();
		if (entriesList == null) {
			return ;
		}
		for (String entry: entriesList) {
			_taskList.add(new Task(entry));
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
	public Stack<Task> getUndoStack() {
		return _undoStack;
	}

	// TODO: switch statement to handle differing operations
	/**
	 * This method acts as the main handler for all user
	 * operations. This handler will attempt to execute a
	 * command and informs the user of the status of the
	 * operation that is carried out.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return 		a message indicating the status of the operation carried out
	 */
	public String executeCommand(Task task) {
		String command = task.getCommand();
		String statusMessage;
		switch (command) {
			case "add" : 
				statusMessage = addToFile(task);
				break;
				
			case "delete" :
				statusMessage = removeFromFile(task);
				break;
				
			case "update" :
				statusMessage = updateToFile(task);
				break;
				
			case "completed" :
				statusMessage = markDone(task);
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
				statusMessage = getStatusMessage(task); 
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
	private Task createReverseOperation(Task task) {
		Task reversed = task;
		String command = task.getCommand();
		switch (command) {
			case "add" :
				reversed.setCommand("delete");
				break;

			case "delete" :
				reversed.setCommand("add");
				break;

			case "update" :
				// nothing happens here because the updated task
				// already contains the previous state
				break;

			case "show" :
				break;

			case "sort" :
				// TODO: store original order of task list
				break;

			case "undo" :
				break;

			case "help" :
				break;
				
			default :
				System.out.println("Not supposed to happen.");
				break;
		}
		return reversed;
	}
	
	/**
	 * Retrieves the status message depending on the status
	 * of an operation being carried out by the program logic.
	 * 
	 * @param task	the Task object being processed
	 * @return		the corresponding status message based on operation status
	 */
	private String getStatusMessage(Task task) {
		String message;
		switch (_status) {
			case STATUS_SUCCESS_ADD :
				message = String.format(MESSAGE_SUCCESS_ADD, task.getName());
				break;
				
			case STATUS_ERROR_ADD :
				message = String.format(MESSAGE_ERROR_ADD, task.getName());
				break;
				
			case STATUS_SUCCESS_DELETE :
				message = String.format(MESSAGE_SUCCESS_DELETE, task.getName());
				break;
				
			case STATUS_ERROR_DELETE :
				message = String.format(MESSAGE_ERROR_DELETE, task.getName());
				break;
				
			case STATUS_SUCCESS_UPDATE :
				message = MESSAGE_SUCCESS_UPDATE;
				break;
				
			case STATUS_ERROR_UPDATE :
				message = MESSAGE_ERROR_UPDATE;
				break;
				
			case STATUS_SUCCESS_SET_COMPLETED :
				message = String.format(MESSAGE_SUCCESS_SET_COMPLETED, task.getName());
				break;
				
			case STATUS_ERROR_SET_COMPLETED :
				message = String.format(MESSAGE_ERROR_SET_COMPLETED, task.getName());
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
	
	// TODO: default - adds to end of list
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
	private String addToFile(Task task) {
		_taskList.add(task);
		// TODO: insert try-catch block here
		_storage.writeToFile(_taskList);
		_undoStack.push(createReverseOperation(task));
		_status = Status.STATUS_SUCCESS_ADD;
		return getStatusMessage(task);
	}
	
	// TODO: remove: by matching task name as well as by ID
	/**
	 * Removes the task from the text file.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the delete-task operation
	 */
	private String removeFromFile(Task task) {
		Task removed = null;
		for (Task existingTask: _taskList) {
			if (existingTask.getName().equals(task.getName())) {
				removed = existingTask;
				break;
			}
		}
		if (removed != null) {
			_taskList.remove(removed);
			// TODO: insert try-catch block here
			_storage.writeToFile(_taskList);
			_undoStack.push(createReverseOperation(removed));
			_status = Status.STATUS_SUCCESS_DELETE;
			return getStatusMessage(removed);
		} else {
			_status = Status.STATUS_ERROR_DELETE;
			return getStatusMessage(task);
		}
	}
	
	/**
	 * Updates a particular task entry in the text file.
	 * 
	 * @param task a Task object representation of the user's input
	 * @return 	   a message indicating the status of the update-task operation
	 */
	private String updateToFile(Task task) {
		Task toUpdate = null;
		int updateIndex = -1;
		for (int i = 0; i < _taskList.size(); i++) {
			if (_taskList.get(i).getName().equals(task.getName()) ||
				_taskList.get(i).getId() == task.getId()) {
				toUpdate = _taskList.get(i);
				updateIndex = i;
				break;
			}
		}
		if (toUpdate != null) {
			Task updatedTask = returnUpdatedTask(toUpdate, task.getUpdateState());
			_taskList.set(updateIndex, updatedTask);
			// TODO: insert try-catch block here
			_storage.writeToFile(_taskList);
			_undoStack.push(createReverseOperation(updatedTask));
			_status = Status.STATUS_SUCCESS_UPDATE;
			return getStatusMessage(task);
		} else {
			_status = Status.STATUS_ERROR_UPDATE;
			return getStatusMessage(task);
		}
	}
	
	/**
	 * Updates a selected task with the desired updated state and
	 * returns an instance of the updated task.
	 * 
	 * @param originalTask	the Task to be updated
	 * @param updatedTask	the state that the original task should be
	 */
	private Task returnUpdatedTask(Task originalTask, Task updatedState) {
		// TODO Auto-generated method stub
		Task result = new Task(updatedState.getName(), updatedState.getDate(),
							   "update", updatedState.getPriority(),
							   originalTask.getId(), updatedState.isCompleted(),
							   originalTask);
		return result;
	}

	/**
	 * Sets a task as completed.
	 * 
	 * @param task	a Task object representation of the user's input
	 * @return		a message indicating the status of the set-task-completed operation
	 */
	private String markDone(Task task) {
		Task toUpdate = null;
		Task previousState = task;
		int updateIndex = -1;
		for (int i = 0; i < _taskList.size(); i++) {
			if (_taskList.get(i).getName().equals(task.getName()) ||
				_taskList.get(i).getId() == task.getId()) {
				toUpdate = _taskList.get(i);
				updateIndex = i;
				break;
			}
		}
		if (toUpdate != null) {
			toUpdate.setCompleted(true);
			_taskList.set(updateIndex, toUpdate);
			// TODO: insert try-catch block here
			_storage.writeToFile(_taskList);
			toUpdate.setUpdateState(previousState);
			toUpdate.setCommand("update");
			_undoStack.push(createReverseOperation(toUpdate));
			_status = Status.STATUS_SUCCESS_SET_COMPLETED;
			return getStatusMessage(task);
		} else {
			_status = Status.STATUS_ERROR_SET_COMPLETED;
			return getStatusMessage(task);
		}
	} 
	
	/**
	 * Undo one step back into the previous state of the program.
	 * 
	 * @return a message indicating status of the undo operation
	 */
	private String undo() {
		try {
			Task originalState = _undoStack.pop();
			executeCommand(originalState);
			updateInternalStorage();
			_status = Status.STATUS_SUCCESS_UNDO;
			return getStatusMessage(originalState);
		} catch (EmptyStackException e) {
			_status = Status.STATUS_ERROR_UNDO;
			return getStatusMessage(null);
		} finally {
			
		}
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
	 */
	public void flushInternalStorage() {
		/* Might want to consider insert try-catch block here
		 * Then again, if this method isn't needed anymore in the future,
		 * this method can just be removed totally. 
		 */
		_storage.flushFile();
		_taskList = new ArrayList<Task>();
	}
	
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
		return _taskList;
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
		Collections.sort(_taskList);
		_storage.writeToFile(_taskList);
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
		for (Task entry: _taskList) {
			if (entry.getName().contains(task.getName())) {
				results.add(entry);
			}
		}
		return results;
	}

}
