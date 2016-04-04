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
	private ArrayList<Task> overdue;
	private ArrayList<Task> today;
	private ArrayList<Task> tomorrow;
	private ArrayList<Task> thisWeek;
	private ArrayList<Task> remaining;
	private ArrayList<Task> floating;
	private ArrayList<Task> completed;
	
	private int _oldIndices[] = new int[2];
	
	public State(String command) {
		_command = command;
		overdue = new ArrayList<Task>();
		today = new ArrayList<Task>();
		tomorrow = new ArrayList<Task>();
		thisWeek = new ArrayList<Task>();
		remaining = new ArrayList<Task>();
		floating = new ArrayList<Task>();
		completed = new ArrayList<Task>();
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
	
	public void setState(ArrayList<ArrayList<Task>> taskLists) {
		overdue.addAll(taskLists.get(0));
		today.addAll(taskLists.get(1));
		tomorrow.addAll(taskLists.get(2));
		thisWeek.addAll(taskLists.get(3));
		remaining.addAll(taskLists.get(4));
		floating.addAll(taskLists.get(5));
		completed.addAll(taskLists.get(6));
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
	
	public ArrayList<Task> getOverdue() {
		return overdue;
	}
	
	public ArrayList<Task> getToday() {
		return today;
	}
	
	public ArrayList<Task> getTomorrow() {
		return tomorrow;
	}
	
	public ArrayList<Task> getThisWeek() {
		return thisWeek;
	}
	
	public ArrayList<Task> getRemaining() {
		return remaining;
	}
	
	public ArrayList<Task> getFloating() {
		return floating;
	}
	
	public ArrayList<Task> getCompleted() {
		return completed;
	}
	
}
