package cs2103_w09_1j.esther;

import java.util.ArrayList;

/**
 * The <code>State</code> class represents a internal memory state in the <code>Logic</code> component.
 * This class is used alongside with <code>Logic</code> to support the <code>undo</code> functionality.
 * 
 * @@author A0130749A
 */
public class State {
	
	private String command;
	private String filePath;
	private String sortOrder;
	private ArrayList<Task> overdueTaskList;
	private ArrayList<Task> todayTaskList;
	private ArrayList<Task> tomorrowTaskList;
	private ArrayList<Task> thisWeekTaskList;
	private ArrayList<Task> remainingTaskList;
	private ArrayList<Task> floatingTaskList;
	private ArrayList<Task> completedTaskList;
	private ArrayList<Task> allTaskList;
	
	/*
	 * This special value for the indices indicate to the UI that it does not need to access any particular Task element
	 * in the list of tasks displayed to the user for highlighting.
	 */
	private static final int NOT_APPLICABLE = -1;
	
	private int oldIndices[] = new int[2];	// the indices represent [buffer_index, task_position] for UI's reference
	
	public State(String command) {
		this.command = command;
		overdueTaskList = new ArrayList<Task>();
		todayTaskList = new ArrayList<Task>();
		tomorrowTaskList = new ArrayList<Task>();
		thisWeekTaskList = new ArrayList<Task>();
		remainingTaskList = new ArrayList<Task>();
		floatingTaskList = new ArrayList<Task>();
		completedTaskList = new ArrayList<Task>();
	}
	
	public void setSortOrder(String order) {
		sortOrder = order;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getSortOrder() {
		return sortOrder;
	}
	
	public void setState(ArrayList<ArrayList<Task>> taskLists) {
		overdueTaskList.addAll(taskLists.get(Task.OVERDUE_TASK_INDEX));
		todayTaskList.addAll(taskLists.get(Task.TODAY_TASK_INDEX));
		tomorrowTaskList.addAll(taskLists.get(Task.TOMORROW_TASK_INDEX));
		thisWeekTaskList.addAll(taskLists.get(Task.THIS_WEEK_TASK_INDEX));
		remainingTaskList.addAll(taskLists.get(Task.UNCODED_TASK_INDEX));
		floatingTaskList.addAll(taskLists.get(Task.FLOATING_TASK_INDEX));
		completedTaskList.addAll(taskLists.get(Task.COMPLETED_TASK_INDEX));
	}
	
	public void setAllTaskList(ArrayList<Task> fullTaskList) {
		allTaskList = new ArrayList<Task>();
		allTaskList.addAll(fullTaskList);
	}
	
	public int[] getIndices() {
		return oldIndices;
	}

	/**
	 * Sets the state of the indices to the default.
	 * 
	 * @see #setIndices(int[])
	 */
	public void setIndices() {
		oldIndices[0] = NOT_APPLICABLE;
		oldIndices[1] = NOT_APPLICABLE;
	}
	
	/**
	 * Sets the state of the indices with a supplied array of 2 indices.
	 * 
	 * @param indices	an array of 2 indices representing [buffer_index, item_position]
	 * @see 			#setIndices()
	 */
	public void setIndices(int[] indices) {
		oldIndices = indices;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public ArrayList<Task> getOverdueTaskList() {
		return overdueTaskList;
	}
	
	public ArrayList<Task> getTodayTaskList() {
		return todayTaskList;
	}
	
	public ArrayList<Task> getTomorrowTaskList() {
		return tomorrowTaskList;
	}
	
	public ArrayList<Task> getThisWeekTaskList() {
		return thisWeekTaskList;
	}
	
	public ArrayList<Task> getRemainingTaskList() {
		return remainingTaskList;
	}
	
	public ArrayList<Task> getFloatingTaskList() {
		return floatingTaskList;
	}
	
	public ArrayList<Task> getCompletedTaskList() {
		return completedTaskList;
	}
	
	public ArrayList<Task> getAllTaskList() {
		return allTaskList;
	}
	
}