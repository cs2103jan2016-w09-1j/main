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
	private ArrayList<String> _dataList;
	private Stack<String> _undoStack;
	
	public Logic() {
		_undoStack = new Stack<String>();
	}
	
	// ========== INITIALIZATION METHODS ========== //
	public void setParser(Parser parser) {
		_parser = parser;
	}
	
	public void setStorage(Storage storage) {
		_storage = storage;
	}
	
	public void initializeInternalStorage() {
		_dataList = _storage.readFromFile();
	}
	
	// ========== !INITIALIZATION METHODS ========== //
	
	// ============= METHODS FOR USER OPERATIONS ============= //
	public String executeCommand(String command, String input) {
		return "OK.";
	}
	
	// TODO: default - adds to end of list
	private String addToFile(String input) {
		_dataList.add(input);
		_storage.writeToFile(_dataList);
		_undoStack.push("add");
		return "OK.";
	}
	
	// TODO: remove: by matching task name as well as by index
	private String removeFromFile(String input) {
		_dataList.remove(input);
		_storage.writeToFile(_dataList);
		_undoStack.push("remove");
		return "OK.";
	}
	
	// TODO: update task with matching task name
	private String updateToFile(String current, String updated) {
		int updateIndex = _dataList.indexOf(current);
		_dataList.set(updateIndex, updated);
		_storage.writeToFile(_dataList);
		return "OK.";
	}
	
	
	// --- TO BE IMPLEMENTED IN LATER VERSIONS --- //
	private ArrayList<String> displayAll() {
		return _dataList;
	}
	
	private String sortFile() {
		Collections.sort(_dataList);
		_storage.writeToFile(_dataList);
		return "OK.";
	}
	
	private String undo() {
		_undoStack.pop();
		return "OK.";
	}
	// --- !TO BE IMPLEMENTED IN LATER VERSIONS --- //
	// ============= !METHODS FOR USER OPERATIONS ============= //
}
