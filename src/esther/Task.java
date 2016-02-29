package esther;

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
 * To implement makeReverse() operation.
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
	private static String _sortCriterion = "priority";
	private int _priority;
	private int _id;
	private static int _assignId = 0;
	private boolean _isCompleted;
	
	/**
	 * A null constructor.
	 * 
	 * @author Tay Guo Qiang
	 */
	public Task() {
		
	}
	
	/**
	 * Creates a Task object with all the supplied arguments.
	 * This shall be used as the default constructor.
	 * 
	 * @param  name
	 * @param  date
	 * @param  priority
	 * @param  isCompleted
	 * @author Tay Guo Qiang
	 */
	public Task(String name, Date date, int priority, boolean isCompleted) {
		_name = name;
		_date = date;
		_priority = priority;
		_isCompleted = isCompleted;
		_id = _assignId;
		_assignId++;
	}
	
	/**
	 * Parses the user input to split and extract out necessary details
	 * (e.g. task name, task deadline, task priority, etc),
	 * then constructs the Task object with all the information extracted
	 * from the user input.
	 * 
	 * @author Tay Guo Qiang
	 */
	public Task(String userInput) {
		this(null, null, -1, false);
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
	 * @param  name	the desired task name
	 * @author Tay Guo Qiang
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
	 * @param  date	the desired task deadline
	 * @author Tay Guo Qiang
	 */
	public void setDate(Date date) {
		_date = date;
	}

	/**
	 * Getter method for sorting criterion.
	 * 
	 * The default sorting criterion is by task priority.
	 * 
	 * @see		Task#compareTo(Task)
	 * @return	a String representing the sorting criterion
	 * @author	Tay Guo Qiang
	 */
	public static String getSortCriterion() {
		return _sortCriterion;
	}

	/**
	 * Setter method for sorting criterion.
	 * 
	 * @see    Task#compareTo(Task)
	 * @param  sortCriterion	the criteria to sort tasks by
	 * @author Tay Guo Qiang
	 */
	public static void setSortCriterion(String sortCriterion) {
		Task._sortCriterion = sortCriterion;
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
	 * @param  priority	the desired task's priority level
	 * @author Tay Guo Qiang
	 */
	public void setPriority(int priority) {
		_priority = priority;
	}
	
	/**
	 * Getter method for task ID.
	 * 
	 * @return the task ID
	 * @author Tay Guo Qiang
	 */
	public int getId() {
		return _id;
	}
	
	/**
	 * Setter method for task ID.
	 * 
	 * @param  id	the task ID
	 * @author Tay Guo Qiang
	 */
	public void setId(int id) {
		_id = id;
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
	 * @param  isCompleted	the status of the task (completed or not)
	 * @author Tay Guo Qiang
	 */
	public void setCompleted(boolean isCompleted) {
		_isCompleted = isCompleted;
	}
	
	/**
	 * Returns an instance of the task's original state.
	 * 
	 * TODO
	 * @param  task the task object being operated on
	 * @return the original state of the task.
	 * @author Tay Guo Qiang 
	 */
	public Task getOriginalState(Task task) {
		return null;
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
	 * is used for sorting tasks in certain order. The default
	 * sorting order is by task priority, then by task deadline
	 * and finally by name of task. However, other sorting
	 * criterion, such as by name or by date, is also supported.
	 * 
	 * @author Tay Guo Qiang
	 */
	@Override
	public int compareTo(Task task) {
		int compareValue = 0;
		switch (_sortCriterion) {
			case "date" :
				compareValue = compareByDate(task);
				break;
				
			case "name" :
				compareValue = compareByName(task);
				break;
				
			default :
				compareValue = compareByPriority(task);
				break;
		}
		return compareValue;
	}
	
	/**
	 * The comparison method invoked when sorting criteria is by task deadline.
	 * 
	 * Comparison order is by date, then by priority and then by name.
	 * 
	 * @param  task	the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself;
	 * 		   a value less than 0 if the Task compared to comes after itself;
	 * 		   and a value more than 0 if the Task compared to comes before itself.
	 * @author Tay Guo Qiang
	 */
	private int compareByDate(Task task) {
		if (_date.equals(task.getDate())) {
			if (_priority == task.getPriority()) {
				return _name.compareTo(task.getName());
			} else {
				return Integer.compare(_priority, task.getPriority());
			}
		} else {
			return _date.compareTo(task.getDate());
		}
	}
	
	/**
	 * The comparison method invoked when sorting criteria is by task name.
	 * 
	 * Comparison order is by name, then by priority and then by date.
	 * 
	 * @param  task	the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself;
	 * 		   a value less than 0 if the Task compared to comes after itself;
	 * 		   and a value more than 0 if the Task compared to comes before itself.
	 * @author Tay Guo Qiang
	 */
	private int compareByName(Task task) {
		if (_name.equals(task.getName())) {
			if (_priority == task.getPriority()) {
				return _date.compareTo(task.getDate());
			} else {
				return Integer.compare(_priority, task.getPriority());
			}
		} else {
			return _name.compareTo(task.getName());
		}
	}
	
	/**
	 * The comparison method invoked when sorting criteria is by task priority.
	 * 
	 * @param  task	the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself;
	 * 		   a value less than 0 if the Task compared to comes after itself;
	 * 		   and a value more than 0 if the Task compared to comes before itself.
	 * @author Tay Guo Qiang
	 */
	private int compareByPriority(Task task) {
		if (_priority == task.getPriority()) {
			if (_date.equals(task.getDate())) {
				return _name.compareTo(task.getName());
			} else {
				return _date.compareTo(task.getDate());
			}
		} else {
			return Integer.compare(_priority, task.getPriority());
		}
	}
	
}