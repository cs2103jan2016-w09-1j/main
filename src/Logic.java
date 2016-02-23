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
 * All code are currently written in stubs. When the base
 * template is completed, the logic of the code shall be
 * written and subsequently be refined.
 * 
 * @author Tay Guo Qiang
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

class Logic {
	
	private Parser _parser;
	private Storage _storage;
	private ArrayList<Task> _taskList;
	private Stack<Task> _undoStack;
	
	public Logic() {
		_undoStack = new Stack<Task>();
	}
	
	// ========== INITIALIZATION METHODS ========== //
	public void setParser(Parser parser) {
		_parser = parser;
	}
	
	public void setStorage(Storage storage) {
		_storage = storage;
	}
	
	public void updateInternalStorage() {
		ArrayList<Task> _taskList = new ArrayList<Task>();
		ArrayList<String> entriesList = _storage.readFromFile();
		for (String entry: entriesList) {
			_taskList.add(new Task(entry));
		}
	}
	
	// ========== !INITIALIZATION METHODS ========== //
	
	// ======= GETTERS FOR INTERNAL VARIABLES ======= //
	public ArrayList<Task> getInternalStorage() {
		return _taskList;
	}
	
	public Stack<Task> getUndoStack() {
		return _undoStack;
	}
	
	// ======= GETTERS FOR INTERNAL VARIABLES ======= //
	
	// ============= METHODS FOR USER OPERATIONS ============= //
	// TODO: switch statement to handle differing operations
	public String executeCommand(Task task) {
		String command = task.getCommand();
		switch (command) {
			case "add" : break;
			case "delete" : break;
			case "update" : break;
			case "done" : break;
			case "show" : break;
			case "sort" : break;
			case "undo" : break;
			case "help" : break;
			default : break;
		}
		return "OK.";
	}
	
	// TODO: default - adds to end of list
	private String addToFile(Task task) {
		_taskList.add(task);
		_storage.writeToFile(_taskList);
		_undoStack.push(task);
		return "OK.";
	}
	
	// TODO: remove: by matching task name as well as by ID
	private String removeFromFile(Task task) {
		Task removed = null;
		for (Task existingTask: _taskList) {
			if (existingTask.getName().equals(task.getName())) {
				removed = existingTask;
				break;
			}
		}
		_taskList.remove(removed);
		_storage.writeToFile(_taskList);
		_undoStack.push(removed);
		return "OK.";
	}
	
	// TODO: update task with matching task name
	private String updateToFile(String currentTaskName, Task task) {
		Task toUpdate = null;
		int updateIndex = -1;
		for (int i = 0; i < _taskList.size(); i++) {
			if (_taskList.get(i).getName().equals(currentTaskName)) {
				toUpdate = _taskList.get(i);
				updateIndex = i;
				break;
			}
		}
		toUpdate.setName(task.getName());
		_taskList.set(updateIndex, toUpdate);
		_storage.writeToFile(_taskList);
		_undoStack.push(toUpdate);
		return "OK.";
	}
	
	private String undo() {
		Task originalState = _undoStack.pop();
		executeCommand(originalState);
		updateInternalStorage();
		return "OK.";
	}
	
	// --- TO BE IMPLEMENTED IN LATER VERSIONS --- //
	public void flushInternalStorage() {
		_storage.flushFile();
		_taskList = new ArrayList<Task>();
	}
	
	private ArrayList<Task> displayAll() {
		_undoStack.push(null);
		return _taskList;
	}
	
	private String sortFile() {
		_undoStack.push(null);
		Collections.sort(_taskList);
		_storage.writeToFile(_taskList);
		return "OK.";
	}
	
	// --- !TO BE IMPLEMENTED IN LATER VERSIONS --- //
	// ============= !METHODS FOR USER OPERATIONS ============= //
}
