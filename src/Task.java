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
	
	/**
	 * Creates a Task object with all the supplied arguments.
	 * This shall be used as the default constructor.
	 * 
	 * @param name
	 * @param date
	 * @param command
	 * @param priority
	 * @param isCompleted
	 * @author Tay Guo Qiang
	 */
	public Task(String name, Date date, String command, int priority, boolean isCompleted) {
		_name = name;
		_date = date;
		_command = command;
		_priority = priority;
		_isCompleted = isCompleted;
	}
	
	/**
	 * Parses the user input to split and extract out necessary details
	 * (e.g. task name, task deadline, task priority, etc),
	 * then constructs the Task object with all the information extracted
	 * from the user input.
	 * 
	 * @author Go Hui Shan
	 */
	public Task(String userInput) {
		// TODO: Hui Shan to implement
	}
	
	/**
	 * Getter method for task name.
	 * 
	 * Logic will use this to access the task's name.
	 * 
	 * @return the name of the task
	 * @author Tay Guo Qiang
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Setter method for task name.
	 * 
	 * @param name	the desired task name
	 * @author		Tay Guo Qiang
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * Getter method for task deadline.
	 * 
	 * Logic will use this to access the deadline of the task.
	 * 
	 * @return the deadline of the task
	 * @author Tay Guo Qiang
	 */
	public Date getDate() {
		return _date;
	}

	/**
	 * Setter method for task deadline.
	 * 
	 * @param date	the desired task deadline
	 * @author 		Tay Guo Qiang
	 */
	public void setDate(Date date) {
		_date = date;
	}

	/**
	 * Getter method for the command associated with the task.
	 * 
	 * Logic will use this to determine the command to execute on the task.
	 * 
	 * @return the command to execute on the task
	 * @author Tay Guo Qiang
	 */
	public String getCommand() {
		return _command;
	}

	/**
	 * Setter method for the command associated with the task.
	 * 
	 * @param command	the command to execute on the task
	 * @author 			Tay Guo Qiang
	 */
	public void setCommand(String command) {
		_command = command;
	}

	/**
	 * Getter method for the priority level of the task.
	 * 
	 * Logic will use this to access the task's priority level.
	 * 
	 * @return the priority level of the task
	 * @author Tay Guo Qiang
	 */
	public int getPriority() {
		return _priority;
	}

	/**
	 * Setter method for the priority level of the task.
	 * 
	 * @param priority	the desired task's priority level
	 * @author Tay Guo Qiang
	 */
	public void setPriority(int priority) {
		_priority = priority;
	}

	/**
	 * Getter method for whether the task is completed or not.
	 * 
	 * Logic will use this to check if task is completed.
	 * 
	 * @return task status (whether it is completed or not)
	 * @author Tay Guo Qiang
	 */
	public boolean isCompleted() {
		return _isCompleted;
	}

	/**
	 * Setter method for task status.
	 * 
	 * @param isCompleted	the status of the task (completed or not)
	 * @author 				Tay Guo Qiang
	 */
	public void setCompleted(boolean isCompleted) {
		_isCompleted = isCompleted;
	}
	
	// How shall a task be displayed to the user?
	/**
	 * Provides a human-readable String representation of a task.
	 * 
	 * UI will display this to the user.
	 * 
	 * @author Go Hui Shan
	 */
	@Override
	public String toString() {
		// TODO: method stub, Hui Shan to implement
		String taskString = "";
		return taskString;
	}

	/**
	 * The comparison method for comparing tasks. This method
	 * is used for sorting tasks in certain order. The current
	 * (and default) sorting order is by task priority, then
	 * by task deadline and finally by name of task.
	 * 
	 * This method shall be refined when dealing with varying
	 * sorting criteria defined by the user.
	 * 
	 * @author Tay Guo Qiang
	 */
	@Override
	public int compareTo(Task task) {
		if (_priority == task.getPriority()) {
			if (_date.equals(task.getDate())) {
				return _name.compareTo(task.getName());
			}
			return _date.compareTo(task.getDate());
		}
		else {
			return Integer.compare(_priority, task.getPriority());
		}
	}
	
}
