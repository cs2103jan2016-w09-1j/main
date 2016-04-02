package cs2103_w09_1j.esther;

/**
 * ========= [ STATE OBJECT DEFINITIONS ] =========
 * This class contains the representation of the
 * state object that will be used by the Logic
 * component of the program. This class is used to
 * support the undo functionality.
 * 
 * @@author A0130749A
 */

import java.util.ArrayList;

public class State {
	
	private String _command;
	private String _filePath;
	private String _sortOrder;
	private ArrayList<ArrayList<Task>> _tasks = new ArrayList<ArrayList<Task>>();
	private int _oldIndices[] = new int[2];
	
	public State(String command) {
		_command = command;
	}
	
	public void setSortOrder(String order) {
		_sortOrder = order;
	}
	
	public String getCommand() {
		return _command;
	}
	
	public String getSortOrder() {
		return _sortOrder;
	}
	
	public ArrayList<ArrayList<Task>> getState() {
		return _tasks;
	}
	
	public void setState(ArrayList<ArrayList<Task>> taskLists) {
		_tasks = taskLists;
	}
	
	public int[] getIndices() {
		return _oldIndices;
	}
	
	public void setIndices(int[] indices) {
		_oldIndices = indices;
	}
	
	public String getFilePath() {
		return _filePath;
	}
	
	public void setFilePath(String filePath) {
		_filePath = filePath;
	}
	
}
