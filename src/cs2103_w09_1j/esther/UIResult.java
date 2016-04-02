package cs2103_w09_1j.esther;

import java.util.ArrayList;

class UIResult {

	// buffers
	private ArrayList<Task> overdueBuffer;
	private ArrayList<Task> todayBuffer;
	private ArrayList<Task> tmrBuffer;
	private ArrayList<Task> weekBuffer;
	private ArrayList<Task> floatingBuffer;
	private ArrayList<Task> completedBuffer;
	
	/* buffer & position indicator
	 * index[0] shows which buffer to use
	 * 	0 - overdueBuffer
	 * 	1 - todayBuffer
	 * 	2 - tmrBuffer
	 * 	3 - weekBuffer
	 * 	4 - floatingBuffer
	 * 	5 - floatingBuffer
	 * 	6 - completedBuffer
	 * index[1] shows position of the task inside the buffer
	 */
	private int[] index = new int[2];
	
	public UIResult() {
		overdueBuffer = new ArrayList<Task>();
		todayBuffer = new ArrayList<Task>();
		tmrBuffer = new ArrayList<Task>();
		weekBuffer = new ArrayList<Task>();
		floatingBuffer = new ArrayList<Task>();
		completedBuffer = new ArrayList<Task>();
	}
	
	public UIResult(ArrayList<Task> overdueBuffer, ArrayList<Task> todayBuffer,
			ArrayList<Task> tmrBuffer, ArrayList<Task> weekBuffer,
			ArrayList<Task> floatingBuffer, ArrayList<Task> completedBuffer) {
		this.overdueBuffer = overdueBuffer;
		this.todayBuffer = todayBuffer;
		this.tmrBuffer = tmrBuffer;
		this.weekBuffer = weekBuffer;
		this.floatingBuffer = floatingBuffer;
		this.completedBuffer = completedBuffer;
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

	public ArrayList<Task> getTmrBuffer() {
		return tmrBuffer;
	}

	public void setTmrBuffer(ArrayList<Task> tmrBuffer) {
		this.tmrBuffer = tmrBuffer;
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

	public int[] getIndex() {
		return index;
	}

	public void setIndex(int[] index) {
		this.index = index;
	}
	
}
