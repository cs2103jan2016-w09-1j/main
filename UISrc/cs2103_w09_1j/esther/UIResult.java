package cs2103_w09_1j.esther;

import java.util.ArrayList;

/**
 * The <code>UIResult</code> class contains a view of tasks that will be shown
 * to users via the <code>UserInterface</code> component. This view is created
 * by the <code>Logic</code> component each time a user operation is performed
 * and is updated into the <code>UserInterface</code>.
 * 
 * @author
 */
public class UIResult {

	private ArrayList<Task> overdueBuffer;
	private ArrayList<Task> todayBuffer;
	private ArrayList<Task> tomorrowBuffer;
	private ArrayList<Task> weekBuffer;
	private ArrayList<Task> allTaskBuffer;
	private ArrayList<Task> floatingBuffer;
	private ArrayList<Task> completedBuffer;
	private ArrayList<Task> searchBuffer;

	private int[] index = new int[NUM_INDICES];
	private String commandType;

	private static final int NUM_INDICES = 2;

	public static final int OVERDUE_INDEX = 0;
	public static final int TODAY_INDEX = 1;
	public static final int TOMORROW_INDEX = 2;
	public static final int WEEK_INDEX = 3;
	public static final int ALL_INDEX = 4;
	public static final int FLOATING_INDEX = 5;
	public static final int COMPLETED_INDEX = 6;
	public static final int NUM_BUFFERS = 7;

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
	
	public void setAllTaskBuffer(ArrayList<Task> allBuffer) {
		this.allTaskBuffer = allBuffer;
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

	/**
	 * Retrieves the help message.
	 * 
	 * @return the help message
	 */
	public String getMessage() {
		return Status.MESSAGE_HELP;
	}

	// ===========TESTING METHODS================

	public ArrayList<Task> getBuffer(int whichBuffer) {
		switch (whichBuffer) {
			case (OVERDUE_INDEX):
				return getOverdueBuffer();
			case (TODAY_INDEX):
				return getTodayBuffer();
			case (TOMORROW_INDEX):
				return getTomorrowBuffer();
			case (WEEK_INDEX):
				return getWeekBuffer();
			case (ALL_INDEX):
				return getAllTaskBuffer();
			case (FLOATING_INDEX):
				return getFloatingBuffer();
			case (COMPLETED_INDEX):
				return getCompletedBuffer();
			default:
				System.out.println("No buffer provided" + whichBuffer);
				return null;
		}
	}

	public Task getTask(int whichBuffer, int posInBuffer) {
		ArrayList<Task> buffer = getBuffer(whichBuffer);
		if (buffer != null) {
			if (buffer.size() > posInBuffer) {
				return buffer.get(posInBuffer);
			}
		}
		return null;
	}

	public Task getModifiedTask() {
		return getTask(getIndex()[0], getIndex()[1]);
	}

}
