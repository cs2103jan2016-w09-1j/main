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

// Remove the unused class (either Calendar or Date) when
// the class to be used is finalized.
class Task implements Comparable</*Calendar*/Date> {

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
	@Override
	public String toString() {
		// TODO: method stub
		String taskString = "";
		return taskString;
	}

	// After deciding on whether to use Date or Calendar,
	// implement the compareTo() function for sorting 
	@Override
	public int compareTo(Date o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
