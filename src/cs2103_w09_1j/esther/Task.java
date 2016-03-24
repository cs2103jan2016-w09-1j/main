package cs2103_w09_1j.esther;

/**
 * ========== [ TASK OBJECT DEFINITIONS ] ==========
 * This class contains the representation of the
 * task object that will be used by the program.
 * 
 * ============= [ IMPORTANT NOTICES ] =============
 * NOTE: Date (java.util.Date) class methods are
 * largely deprecated and it has been recommended
 * by Java that we use Calendar class instead.
 * 
 * @author Tay Guo Qiang
 *         (add your name to list of authors if you made
 *         changes to this class definition)
 */

// import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import Task.TaskField;
import sun.util.resources.cldr.id.LocaleNames_id;

public class Task implements Comparable<Task> {
	public enum TaskField {
		NAME("taskName"), ID("taskID"), PRIORITY("priority"), DATE("date"), SORT("order"), UPDATENAME(
				"updateName"), SHOW("order"), UNDO("undo"),HELP("help"), COMPLETED("completed");

		private String taskKeyName;
		private static final Map<String, TaskField> lookup = new HashMap<String, TaskField>();

		private TaskField(String _taskKeyName) {
			this.taskKeyName = _taskKeyName;
		}

		public String getTaskKeyName() {
			return taskKeyName;
		}

		/**
		 * This operations reversely gets the CommandKey from the value.
		 * 
		 * @param commandValue
		 *            The input given by the user.
		 * @return The command based on the input.
		 */
		public static TaskField get(String taskKeyValue) {
			return lookup.get(taskKeyValue);
		}

		static {
			// Create reverse lookup hash map
			for (TaskField _taskKeyName : TaskField.values()) {
				lookup.put(_taskKeyName.getTaskKeyName(), _taskKeyName);
			}
		}
	}

	private static SimpleDateFormat _dateFormatter = new SimpleDateFormat("dd/MM/yyyy"); // TODO: to
																							// change
																							// over
																							// time
	private static String _sortCriterion = "priority";
	private static int _assignId = 0;
	private String _name;
	private Date _date;
	private int _priority; // for now, lower number indicates higher priority
	private int _id;
	private boolean _isCompleted;

	private static String idnoString = "ID\\: (\\d+)\\| ";
	private static String dateString = "\\[([^\\]]+)\\] ";
	private static String nameString = "([^\\|]+)\\| ";
	private static String prioString = "Priority: (\\d+)\\| ";
	private static String compString = "Completed: (true|false)";
	private static String[] regexArray = {idnoString, dateString, nameString, prioString, compString};
	

	/**
	 * Constructs an empty Task object.
	 * 
	 * @author Tay Guo Qiang
	 */
	public Task() {

	}

	/**
	 * Constructs a Task with reference to a Command object.
	 * 
	 * @param command
	 *            the Command object containing the required parameters
	 * @throws ParseException
	 * @return a Task with the attributes set with the parameters
	 * @author Tay Guo Qiang
	 */
	public Task(Command command) throws ParseException {
		this();
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());
		Date date = command.hasParameter(TaskField.DATE.getTaskKeyName())
				? _dateFormatter.parse(command.getSpecificParameter(TaskField.DATE.getTaskKeyName())) : null;
		int priority = command.hasParameter(TaskField.PRIORITY.getTaskKeyName())
				? Integer.parseInt(command.getSpecificParameter(TaskField.PRIORITY.getTaskKeyName())) : 0;
		this.setName(taskName);
		this.setDate(date);
		this.setPriority(priority);
		this.setCompleted(false);
		_id = _assignId;
		_assignId++;
	}

	/**
	 * Builds a task from a String with specific format "ID: {id}| [{dd/MM/yyyy}] {name}| Priority: {prio}| Completed: {com}"
	 * 
	 * @param string
	 * @author Jeremy Hon
	 * @throws ParseException 
	 */
	public Task(String string) throws ParseException {
		this();
		String[] resultsArray = new String[5];
		String matcherInput = string;
		for (int i = 0; i < regexArray.length; i++) {
			resultsArray[i] = findMatch(regexArray[i], matcherInput);
			if(resultsArray[i] == null){
				System.out.println("Match failed on "+i+"th element");
				return;
			} else {
				matcherInput = matcherInput.replaceFirst(regexArray[i], "");
			}
		}
		
		int localID = Integer.parseInt(resultsArray[0]);
		Date date = _dateFormatter.parse(resultsArray[1]);
		String taskName = resultsArray[2];
		int priority = Integer.parseInt(resultsArray[3]);
		boolean complete = Boolean.parseBoolean(resultsArray[4]);
		
		this.setName(taskName.trim());
		this.setDate(date);
		this.setPriority(priority);
		this.setCompleted(complete);
		_id = localID;
	}
	
	/**
	 * Returns the matching string given a regex and a string
	 * @param regex
	 * @param input
	 * @return
	 */
	public static String findMatch(String regex, String input){
		Matcher matcher = Pattern.compile(regex).matcher(input);
		if(matcher.find()){
			return matcher.group(1);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns a human-readable String representation of a Task.
	 * 
	 * @return a String representation of the Task
	 * @author Jeremy Hon
	 */
	@Override
	public String toString() {
		String taskString = "";
		taskString += "ID: " + _id + "| ";
		taskString += "[" + _dateFormatter.format(_date) + "] ";
		taskString += _name + "| ";
		taskString += "Priority: " + _priority + "| ";
		taskString += "Completed: " + _isCompleted;
		taskString += "\n";
		return taskString;
	}

	/**
	 * Gets the name of the Task.
	 * 
	 * @return the name of the task
	 * @author Tay Guo Qiang
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Sets the name of the Task.
	 * 
	 * @param name
	 *            the desired task name
	 * @author Tay Guo Qiang
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * Gets the deadline of the Task.
	 * 
	 * @return the deadline of the task
	 * @author Tay Guo Qiang
	 */
	public Date getDate() {
		return _date;
	}

	/**
	 * Sets the deadline of the Task.
	 * 
	 * @param date
	 *            the desired task deadline
	 * @author Tay Guo Qiang
	 */
	public void setDate(Date date) {
		_date = date;
	}

	/**
	 * Gets the sorting criterion to sort Tasks by.
	 * 
	 * The default sorting criterion is by task priority.
	 * 
	 * @see Task#compareTo(Task)
	 * @return a String representing the sorting criterion
	 * @author Tay Guo Qiang
	 */
	public static String getSortCriterion() {
		return _sortCriterion;
	}

	/**
	 * Sets the sorting criterion to sort Tasks by.
	 * 
	 * @see Task#compareTo(Task)
	 * @param sortCriterion
	 *            the criteria to sort tasks by
	 * @author Tay Guo Qiang
	 */
	public static void setSortCriterion(String sortCriterion) {
		_sortCriterion = sortCriterion;
	}

	/**
	 * Gets the priority of the Task.
	 * 
	 * @return the priority level of the task
	 * @author Tay Guo Qiang
	 */
	public int getPriority() {
		return _priority;
	}

	/**
	 * Sets the priority of the Task.
	 * 
	 * @param priority
	 *            the desired task's priority level
	 * @author Tay Guo Qiang
	 */
	public void setPriority(int priority) {
		_priority = priority;
	}

	/**
	 * Gets the ID of the Task.
	 * 
	 * @return the task ID
	 * @author Tay Guo Qiang
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Sets the ID of the Task.
	 * 
	 * @param id
	 *            the task ID
	 * @author Tay Guo Qiang
	 */
	public void setId(int id) {
		_id = id;
	}
	
	/**
	 * Gets the global ID variable for system usage.
	 * 
	 * @return the global ID variable in this class
	 * @author Tay Guo Qiang
	 */
	public static int getGlobalId() {
		return _assignId;
	}
	
	/**
	 * Sets the global ID variable for system usage.
	 * 
	 * @return the global ID variable in this class
	 * @author Tay Guo Qiang
	 */
	public static void setGlobalId(int newId) {
		_assignId = newId;
	}

	/**
	 * Gets completion status of the Task.
	 * 
	 * @return task status (whether it is completed or not)
	 * @author Tay Guo Qiang
	 */
	public boolean isCompleted() {
		return _isCompleted;
	}

	/**
	 * Sets completion status of the Task.
	 * 
	 * @param isCompleted
	 *            the status of the task (completed or not)
	 * @author Tay Guo Qiang
	 */
	public void setCompleted(boolean isCompleted) {
		_isCompleted = isCompleted;
	}

	/**
	 * Creates a copy of this Task object.
	 * 
	 * @return a copy of the Task object
	 * @author Tay Guo Qiang
	 */
	public Task clone() {
		Task copy = new Task();
		copy.setName(_name);
		copy.setDate(_date);
		copy.setId(_id);
		copy.setPriority(_priority);
		copy.setCompleted(_isCompleted);
		return copy;
	}

	/**
	 * Updates the state of the Task object based on the Command object parameters.
	 * 
	 * @param command
	 *            the Command object containing the required parameters
	 * @throws ParseException
	 * @author Tay Guo Qiang
	 */
	public void updateTask(Command command) throws ParseException {
		if (command.hasParameter(TaskField.NAME.getTaskKeyName())) {
			this.setName(command.getSpecificParameter(TaskField.NAME.getTaskKeyName()));
		}
		if (command.hasParameter(TaskField.UPDATENAME.getTaskKeyName())) {
			this.setName(command.getSpecificParameter(TaskField.UPDATENAME.getTaskKeyName()));
		}
		if (command.hasParameter(TaskField.DATE.getTaskKeyName())) {
			this.setDate(_dateFormatter.parse(command.getSpecificParameter(TaskField.DATE.getTaskKeyName())));
		}
		if (command.hasParameter(TaskField.PRIORITY.getTaskKeyName())) {
			this.setPriority(Integer.parseInt(command.getSpecificParameter(TaskField.PRIORITY.getTaskKeyName())));
		}
		if (command.hasParameter(TaskField.ID.getTaskKeyName())) {
			this.setId(Integer.parseInt(command.getSpecificParameter(TaskField.ID.getTaskKeyName())));
		}
		if (command.hasParameter(TaskField.COMPLETED.getTaskKeyName())) {
			this.setCompleted(Boolean.parseBoolean(command.getSpecificParameter(TaskField.COMPLETED.getTaskKeyName())));
		}
	}

	/**
	 * The comparison method for comparing tasks. This method
	 * is used for sorting tasks in certain order. The default
	 * sorting order is by task priority, then by task deadline
	 * and finally by name of task. However, other sorting
	 * criteria, such as by name or by date, is also supported.
	 * 
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself;
	 *         a value less than 0 if the Task compared to comes after itself;
	 *         and a value more than 0 if the Task compared to comes before itself.
	 * @author Tay Guo Qiang
	 */
	@Override
	public int compareTo(Task task) {
		switch (_sortCriterion) {
			case "date" :
				return compareByDate(task);
				
			case "name" :
				return compareByName(task);
				
			default :
				return compareByPriority(task);
		}
	}

	/**
	 * The comparison method invoked when sorting criteria is by task deadline.
	 * 
	 * Comparison order is by date, then by priority and then by name.
	 * 
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself;
	 *         a value less than 0 if the Task compared to comes after itself;
	 *         and a value more than 0 if the Task compared to comes before itself.
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
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself;
	 *         a value less than 0 if the Task compared to comes after itself;
	 *         and a value more than 0 if the Task compared to comes before itself.
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
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself;
	 *         a value less than 0 if the Task compared to comes after itself;
	 *         and a value more than 0 if the Task compared to comes before itself.
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