/**
 * ========== [ TASK OBJECT DEFINITIONS ] ==========
 * This class contains the representation of the
 * task object that will be passed around by the
 * program.
 * 
 * 
 * ============= [ IMPORTANT NOTICES ] =============
 * NOTE: Date (java.util.Date) class methods are
 * largely deprecated and it has been recommended
 * by Java that we use Calendar class instead.
 * 
 * 
 * @author Tay Guo Qiang
 * (add your name to list of authors if you made
 * changes to this class definition)
 */

//import java.util.Calendar;
import java.util.Date;

class Task implements Comparable<Task> {

	private String _name;
	//private Calendar date;
	private Date _date;
	private String _command;
	private int _priority;
	private boolean _isCompleted;
	
	public Task(String name, Date date, String command, int priority, boolean isCompleted) {
		_name = name;
		_date = date;
		_command = command;
		_priority = priority;
		_isCompleted = isCompleted;
	}
	
	/**
	 * @author Go Hui Shan
	 */
	public Task(String userInput) {
		// TODO: Hui Shan to implement
	}
	
	// ========== GETTERS AND SETTERS ========== //
	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public Date getDate() {
		return _date;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public String getCommand() {
		return _command;
	}

	public void setCommand(String command) {
		_command = command;
	}
	
	public int getPriority() {
		return _priority;
	}

	public void setPriority(int priority) {
		_priority = priority;
	}

	public boolean isCompleted() {
		return _isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		_isCompleted = isCompleted;
	}
	// ========== !GETTERS AND SETTERS ========== //
	
	// How shall a task be displayed to the user?
	/**
	 * @author Go Hui Shan
	 */
	@Override
	public String toString() {
		// TODO: method stub, Hui Shan to implement
		String taskString = "";
		return taskString;
	}

	@Override
	public int compareTo(Task task) {
		if (_priority == task.getPriority()) {
			if (_date == task.getDate()) {
				// priority and date is the same,
				// compare lexicographically
				return _name.compareTo(task.getName());
			}
			// TODO: same priority but different date,
			//       compare date (use Date or Calendar?)
			return 0;
		}
		else {
			// compare by priority
			return _priority - task.getPriority();
		}
	}
	
}
