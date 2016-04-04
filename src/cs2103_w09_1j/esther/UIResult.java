package cs2103_w09_1j.esther;

import java.util.ArrayList;
import cs2103_w09_1j.esther.Status;

public class UIResult {

	// buffers
	private ArrayList<Task> overdueBuffer;
	private ArrayList<Task> todayBuffer;
	private ArrayList<Task> tomorrowBuffer;
	private ArrayList<Task> weekBuffer;
	private ArrayList<Task> allTaskBuffer;
	private ArrayList<Task> floatingBuffer;
	private ArrayList<Task> completedBuffer;
	private ArrayList<Task> searchBuffer;
	
	/* buffer & position indicator
	 * index[0] shows which buffer to use
	 * 	0 - overdueBuffer
	 * 	1 - todayBuffer
	 * 	2 - tomorrowBuffer
	 * 	3 - weekBuffer
	 * 	4 - allTaskBuffer
	 * 	5 - floatingBuffer
	 * 	6 - completedBuffer
	 * index[1] shows position of the task inside the buffer
	 */
	private int[] index = new int[2];
	private String commandType;
	private static final String message = Status.MESSAGE_HELP;
	
	public UIResult() {
		overdueBuffer = new ArrayList<Task>();
		todayBuffer = new ArrayList<Task>();
		tomorrowBuffer = new ArrayList<Task>();
		weekBuffer = new ArrayList<Task>();
		allTaskBuffer = new ArrayList<Task>();
		floatingBuffer = new ArrayList<Task>();
		completedBuffer = new ArrayList<Task>();
		searchBuffer = new ArrayList<Task>();
	}
	
	public ArrayList<Task> getOverdueBuffer() {
		return overdueBuffer;
	}

	public void setOverdueBuffer(ArrayList<Task> overdueBuffer) {
		this.overdueBuffer = overdueBuffer;
	}

	public ArrayList<Task> getTodayBuffer() {
		return todayBuffer;
	}

	public void setTodayBuffer(ArrayList<Task> todayBuffer) {
		this.todayBuffer = todayBuffer;
	}

	public ArrayList<Task> getTomorrowBuffer() {
		return tomorrowBuffer;
	}

	public void setTomorrowBuffer(ArrayList<Task> tomorrowBuffer) {
		this.tomorrowBuffer = tomorrowBuffer;
	}

	public ArrayList<Task> getWeekBuffer() {
		return weekBuffer;
	}

	public void setWeekBuffer(ArrayList<Task> weekBuffer) {
		this.weekBuffer = weekBuffer;
	}

	public ArrayList<Task> getFloatingBuffer() {
		return floatingBuffer;
	}

	public void setFloatingBuffer(ArrayList<Task> floatingBuffer) {
		this.floatingBuffer = floatingBuffer;
	}

	public ArrayList<Task> getCompletedBuffer() {
		return completedBuffer;
	}

	public void setCompletedBuffer(ArrayList<Task> completedBuffer) {
		this.completedBuffer = completedBuffer;
	}

	public ArrayList<Task> getAllTaskBuffer() {
		return allTaskBuffer;
	}
	
	public void setAllTaskBuffer(ArrayList<Task> remainingBuffer) {
		this.allTaskBuffer = remainingBuffer;
	}
	
	public ArrayList<Task> getSearchBuffer() {
		return searchBuffer;
	}
	
	public void setSearchBuffer(ArrayList<Task> searchBuffer) {
		this.searchBuffer = searchBuffer;
	}

	public int[] getIndex() {
		return index;
	}

	public void setIndex(int[] index) {
		this.index = index;
	}
	
	public String getCommandType() {
		return commandType;
	}
	
	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}
	
	public String getMessage() {
		return message;
	}
	
}
