package cs2103_w09_1j.esther;

import java.text.ParseException;

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
 * CHANGES MADE: Added to TaskField, STARTDATE, ENDDATE, STARTTIME, ENDTIME, 
 * 				 Removed date to cater start and end date.
 * 
 * 
 */

// import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task implements Comparable<Task> {
	public enum TaskField {
		NAME("taskName"), ID("taskID"), PRIORITY("priority"), STARTDATE("startDate"), ENDDATE("endDate"), STARTTIME(
				"startTime"), ENDTIME("endTime"), SORT("order"), UPDATENAME("updateName"), KEYWORD(
						"keyword"), SHOW("order"), UNDO("undo"), HELP("help"), COMPLETE("complete"), PATH("path");

		private String taskKeyName;
		private static final Map<String, TaskField> lookup = new HashMap<String, TaskField>();

		// @@author A0126000H
		private TaskField(String _taskKeyName) {
			this.taskKeyName = _taskKeyName;
		}

		// @@author A0126000H
		public String getTaskKeyName() {
			return taskKeyName;
		}

		/**
		 * This operations reversely gets the CommandKey from the value.
		 * 
		 * @param commandValue
		 *            The input given by the user.
		 * @return The command based on the input.
		 * @@author A0126000H
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

	public static final String SORT_BY_DATE_KEYWORD = "date";
	public static final String SORT_BY_START_DATE_KEYWORD = "startDate";
	public static final String SORT_BY_END_DATE_KEYWORD = "endDate";
	public static final String SORT_BY_NAME_KEYWORD = "taskName";
	public static final String SORT_FLOATING_BY_NAME_KEYWORD = "float_taskName";
	public static final String SORT_BY_PRIORITY_KEYWORD = "priority";
	public static final String SORT_FLOATING_BY_PRIORITY_KEYWORD = "float_priority";
	public static final String SORT_BY_ID_KEYWORD = "id";
	private static final int DEFAULT_STARTING_ID = 0;
	private static final int DEFAULT_TASK_PRIORITY = 5;
	private static final int HIGHEST_TASK_PRIORITY = 1;

	public static final int OVERDUE_TASK_INDEX = 0;
	public static final int TODAY_TASK_INDEX = 1;
	public static final int TOMORROW_TASK_INDEX = 2;
	public static final int THIS_WEEK_TASK_INDEX = 3;
	public static final int UNCODED_TASK_INDEX = 4;
	public static final int FLOATING_TASK_INDEX = 5;
	public static final int COMPLETED_TASK_INDEX = 6;

	private String _name;
	private Date _startDate;
	private Date _endDate;
	private int _priority;
	private int _id;
	private boolean _isCompleted;
	private boolean _isValid = false;

	private static String _sortCriterion = SORT_BY_PRIORITY_KEYWORD;
	private static int _assignId = DEFAULT_STARTING_ID;
	public static SimpleDateFormat _dateOnlyFormatter = new SimpleDateFormat("dd/MM/yyyy");
	public static SimpleDateFormat _dateAndTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private final static Logger taskLogger = Logger.getLogger("estherLogger");
	private final static int NUM_FIELDS = 6;
	private final static String completedStr = "Completed";
	private final static String notCompletedStr = "Incomplete";

	private final static String delimiterPattern = "\\|";
	private final static String idnoString = "ID\\: (\\d+)";
	private final static String dateString = "\\[([^\\]]+)\\] ";
	private final static String nameString = "([^\\|]+)";
	private final static String prioString = "Priority: (\\d+)";
	private final static String compString = "(" + completedStr + "|" + notCompletedStr + ")";
	private final static String[] regexArray = {	idnoString,
													dateString,
													dateString,
													nameString,
													prioString,
													compString };

	/**
	 * Constructs an empty Task object.
	 * 
	 * @@author A0130749A
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
	 * @@author A0130749A
	 */
	public Task(Command command) throws ParseException {
		this();
		Date today = new Date();
		Date startDate = null;
		Date endDate = null;
		String taskName = command.getSpecificParameter(TaskField.NAME.getTaskKeyName());

		String startDateString = command.hasParameter(TaskField.STARTDATE.getTaskKeyName())
				? command.getSpecificParameter(TaskField.STARTDATE.getTaskKeyName())
				: null;

		String startTimeString = command.hasParameter(TaskField.STARTTIME.getTaskKeyName())
				? command.getSpecificParameter(TaskField.STARTTIME.getTaskKeyName())
				: null;
		startDate = parseStringsToDateTime(startDateString, startTimeString, today, true);

		String endDateString = command.hasParameter(TaskField.ENDDATE.getTaskKeyName())
				? command.getSpecificParameter(TaskField.ENDDATE.getTaskKeyName())
				: null;

		String endTimeString = command.hasParameter(TaskField.ENDTIME.getTaskKeyName())
				? command.getSpecificParameter(TaskField.ENDTIME.getTaskKeyName())
				: null;
		endDate = parseStringsToDateTime(endDateString, endTimeString, today, false);

		int priority = command.hasParameter(TaskField.PRIORITY.getTaskKeyName())
				? Integer.parseInt(command.getSpecificParameter(TaskField.PRIORITY.getTaskKeyName()))
				: DEFAULT_TASK_PRIORITY;
		this.setName(taskName);
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		this.setPriority(priority);
		this.setCompleted(false);
		this.setID(_assignId);
		this.setIsValid(true);
		_assignId++;
	}

	/**
	 * Constructs a Task object from a String. The string is expected to have
	 * the correct number of fields, separated by the <code>|</code> vertical
	 * bar.
	 * 
	 * @param string
	 *            String containing all elements of a task object
	 * @throws ParseException
	 *             If parsing fails on any element
	 * @@author A0127572A
	 */
	public Task(String string) throws ParseException {
		this();
		String[] resultsArray = new String[NUM_FIELDS];
		String[] matcherInput = string.split(delimiterPattern);

		// check number of fields
		if (matcherInput.length != NUM_FIELDS) {
			taskLogger.severe(
					"Task constructor expected " + NUM_FIELDS + " arguments but received " + matcherInput.length + ".");
			return;
		}

		parseElementsToArray(resultsArray, matcherInput);

		// check if an ID exists
		if (resultsArray[0] == "") {
			taskLogger.severe("Task constructor cannot find an ID");
			return;
		}

		setAllElements(resultsArray);
	}

	/**
	 * Returns the matching string given a regex and a string
	 * 
	 * @param regex
	 * @param input
	 * @return
	 * @@author A0127572A
	 */
	public static String findMatch(String regex, String input) {
		Matcher matcher = Pattern.compile(regex).matcher(input);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	@Override
	/**
	 * Converts the task object into a formatted String. Fields are separated by
	 * a vertical bar | separator. This method is used when writing tasks to
	 * file.
	 * 
	 * @@author A0127572A
	 */
	public String toString() {
		String taskString = "";
		taskString += "ID: " + _id + " | ";
		taskString += "[" + sDateToString() + "] | ";
		taskString += "[" + eDateToString() + "] | ";
		taskString += _name + " | ";
		taskString += "Priority: " + _priority + " | ";
		taskString += completedToString();
		taskString += "\n";
		return taskString;
	}

	/**
	 * Gets the name of the Task.
	 * 
	 * @return the name of the task
	 * @@author A0130749A
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Sets the name of the Task.
	 * 
	 * @param name
	 *            the desired task name
	 * @@author A0130749A
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * Gets the starting deadline of the Task.
	 * 
	 * @return the deadline of the task
	 * @@author A0130749A
	 */
	public Date getStartDate() {
		return _startDate;
	}

	/**
	 * Sets the starting deadline of the Task.
	 * 
	 * @param date
	 *            the desired task deadline
	 * @@author A0130749A
	 */
	public void setStartDate(Date date) {
		_startDate = date;
	}

	/**
	 * Gets the latest deadline of the Task.
	 * 
	 * @return the deadline of the task
	 * @@author A0130749A
	 */
	public Date getEndDate() {
		return _endDate;
	}

	/**
	 * Sets the latest deadline of the Task.
	 * 
	 * @param date
	 *            the desired task deadline
	 * @@author A0130749A
	 */
	public void setEndDate(Date date) {
		_endDate = date;
	}

	/**
	 * Converts the startdate of a task to a string in the form
	 * <code>dd/MM/yyyy HH:mm</code>
	 * 
	 * @return start date in formatted string
	 * @@author A0127572A
	 */
	public String sDateToString() {
		return dateToString(_startDate);
	}

	/**
	 * Converts the enddate of a task to a string in the form
	 * <code>dd/MM/yyyy HH:mm</code>
	 * 
	 * @return end date in formatted string form
	 * @@author A0127572A
	 */
	public String eDateToString() {
		return dateToString(_endDate);
	}

	/**
	 * This method parses strings and returns a date depending on the contents
	 * of the strings. Either the dateString or timeString may be null. In any
	 * case, a fallback must be given if either is null. <br>
	 * For a null date string, a fallback date must be given in the form of a
	 * <code>Date</code> object. <br>
	 * For a null time string, a boolean must be given to indicate whether to
	 * fallback as a start time (which defaults to 0000) or an end time (which
	 * defaults to 2359).
	 * 
	 * @param dateString
	 *            A string containing either a formatted date or null to be
	 *            parsed
	 * @param timeString
	 *            A string containing either a formatted time or null to be
	 *            parsed
	 * @param fallbackDate
	 *            A date object used as fallback if the dateString is null
	 * @param start
	 *            A boolean indicating whether to default to 0000 or 2359 if
	 *            timeString is null. True will default to 0000
	 * @return a date object containing the parsed date and time strings
	 * @throws ParseException
	 * @@author A0127572A
	 */
	public static Date parseStringsToDateTime(String dateString, String timeString, Date fallbackDate, boolean start)
			throws ParseException {
		Date date = null;
		if (dateString != null && timeString != null) {
			date = _dateAndTimeFormatter.parse(dateString + " " + timeString);
		} else if (dateString != null && timeString == null) {
			date = _dateAndTimeFormatter.parse(dateString + " " + (start ? "00:00" : "23:59"));
		} else if (dateString == null && timeString != null) {
			date = _dateAndTimeFormatter.parse(_dateOnlyFormatter.format(fallbackDate) + " " + timeString);
		}
		return date;
	}

	/**
	 * Parses a date string to a date object. If the string is null or empty,
	 * returns null.
	 * 
	 * @param dateStr
	 *            String to parse
	 * @return Formatted date or null if string given is null or empty.
	 * @throws ParseException
	 * @@author A0127572A
	 */
	public Date parseDate(String dateStr) throws ParseException {
		if (dateStr == null || dateStr.length() == 0) {
			return null;
		}
		return _dateAndTimeFormatter.parse(dateStr);
	}

	/**
	 * Gets the sorting criterion to sort Tasks by.
	 * 
	 * The default sorting criterion is by task priority.
	 * 
	 * @see Task#compareTo(Task)
	 * @return a String representing the sorting criterion
	 * @@author A0130749A
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
	 * @@author A0130749A
	 */
	public static void setSortCriterion(String sortCriterion) {
		_sortCriterion = sortCriterion;
	}

	/**
	 * Gets the priority of the Task.
	 * 
	 * @return the priority level of the task
	 * @@author A0130749A
	 */
	public int getPriority() {
		return _priority;
	}

	/**
	 * Sets the priority of the Task.
	 * 
	 * @param priority
	 *            the desired task's priority level
	 * @@author A0130749A
	 */
	public void setPriority(int priority) {
		if (priority >= HIGHEST_TASK_PRIORITY && priority <= DEFAULT_TASK_PRIORITY) {
			_priority = priority;
		}
	}

	/**
	 * Gets the ID of the Task.
	 * 
	 * @return the task ID
	 * @@author A0130749A
	 */
	public int getId() {
		return _id;
	}

	/**
	 * Sets the ID of the Task.
	 * 
	 * @param id
	 *            the task ID
	 * @@author A0130749A
	 */
	public void setID(int id) {
		_id = id;
	}

	/**
	 * Gets the global ID variable for system usage.
	 * 
	 * @return the global ID variable in this class
	 * @@author A0130749A
	 */
	public static int getGlobalId() {
		return _assignId;
	}

	/**
	 * Sets the global ID variable for system usage.
	 * 
	 * @param newId
	 *            the ID to set
	 * @return the global ID variable in this class
	 * @@author A0130749A
	 */
	public static void setGlobalId(int newId) {
		_assignId = newId;
	}

	/**
	 * Gets completion status of the Task.
	 * 
	 * @return task status (whether it is completed or not)
	 * @@author A0130749A
	 */
	public boolean isCompleted() {
		return _isCompleted;
	}

	/**
	 * Sets completion status of the Task.
	 * 
	 * @param isCompleted
	 *            the status of the task (completed or not)
	 * @@author A0130749A
	 */
	public void setCompleted(boolean isCompleted) {
		_isCompleted = isCompleted;
	}

	/**
	 * Checks if a task is a floating task (i.e. a task without date and time).
	 * 
	 * @return true if the task is a floating task; false otherwise
	 * @@author A0130749A
	 */
	public boolean isFloatingTask() {
		return (_startDate == null && _endDate == null) ? true : false;
	}

	/**
	 * Checks if a task is an event (i.e. a task with start and end dates and
	 * times).
	 * 
	 * @return true if the task is an event; false otherwise
	 * @@author A0130749A
	 */
	public boolean isEvent() {
		return (_startDate != null && _endDate != null) ? true : false;
	}

	/**
	 * Gets isComplete and returns a corresponding string
	 * 
	 * @return completedStr if true and notCompletedStr if false
	 * @@author A0127572A
	 */
	public String completedToString() {
		if (isCompleted()) {
			return completedStr;
		} else {
			return notCompletedStr;
		}
	}

	/**
	 * Parses a given string for the isCompleted attribute. Returns true if it
	 * contains completedStr
	 * 
	 * @param completeStr
	 *            String to parse
	 * @return true if contains completedStr
	 * @@author A0127572A
	 */
	public boolean parseCompleted(String completeStr) {
		return completeStr.contains(completedStr);
	}

	/**
	 * Gets the isValid boolean value. isValid is set to true if Task object was
	 * constructed successfully.
	 * 
	 * @return boolean value of isValid
	 * @@author A0127572A
	 */
	public boolean isValid() {
		return _isValid;
	}

	/**
	 * Creates a copy of this Task object.
	 * 
	 * @return a copy of the Task object
	 * @@author A0130749A
	 */
	@Override
	public Task clone() {
		Task copy = new Task();
		copy.setName(_name);
		copy.setStartDate(_startDate);
		copy.setEndDate(_endDate);
		copy.setID(_id);
		copy.setPriority(_priority);
		copy.setCompleted(_isCompleted);
		copy.setIsValid(_isValid);
		return copy;
	}

	/**
	 * Updates the state of the Task object based on the Command object
	 * parameters.
	 * 
	 * @param command
	 *            the Command object containing the required parameters
	 * @return
	 * @throws ParseException
	 * @@author A0130749A
	 */
	public boolean updateTask(Command command) throws ParseException {
		String startDate = null;
		String startTime = null;
		String endDate = null;
		String endTime = null;

		// DATE AND TIME HANDLING
		if (command.hasParameter(TaskField.STARTDATE.getTaskKeyName())) {
			startDate = command.getSpecificParameter(TaskField.STARTDATE.getTaskKeyName());
		}

		if (command.hasParameter(TaskField.STARTTIME.getTaskKeyName())) {
			startTime = command.getSpecificParameter(TaskField.STARTTIME.getTaskKeyName());
		}

		if (command.hasParameter(TaskField.ENDDATE.getTaskKeyName())) {
			endDate = command.getSpecificParameter(TaskField.ENDDATE.getTaskKeyName());
		}

		if (command.hasParameter(TaskField.ENDTIME.getTaskKeyName())) {
			endTime = command.getSpecificParameter(TaskField.ENDTIME.getTaskKeyName());
		}

		String oldStartTime = (dateToString(_startDate).equals("")) ? null : dateToString(_startDate).substring(11);
		String oldEndTime = (dateToString(_endDate).equals("")) ? null : dateToString(_endDate).substring(11);
		Date oldStartDate = _startDate;
		Date oldEndDate = _endDate;
		Date newStartDate = null;
		Date newEndDate = null;

		if (_startDate == null) {
			newStartDate = parseStringsToDateTime(startDate, startTime, new Date(), true);
			this.setStartDate(newStartDate);
		} else if (startTime == null) {
			newStartDate = parseStringsToDateTime(startDate, oldStartTime, _startDate, true);
			this.setStartDate(newStartDate);
		} else {
			newStartDate = parseStringsToDateTime(startDate, startTime, _startDate, true);
			this.setStartDate(newStartDate);
		}

		if (_endDate == null) {
			newEndDate = parseStringsToDateTime(endDate, endTime, new Date(), false);
			this.setEndDate(newEndDate);
		} else if (endTime == null) {
			newEndDate = parseStringsToDateTime(endDate, oldEndTime, _endDate, false);
			this.setEndDate(newEndDate);
		} else {
			newEndDate = parseStringsToDateTime(endDate, endTime, _endDate, false);
			this.setEndDate(newEndDate);
		}

		if (isAcceptableDateChange(newStartDate, newEndDate)) {
			// do nothing
		} else {
			this.setStartDate(oldStartDate);
			this.setEndDate(oldEndDate);
			Status._errorCode = Status.ErrorCode.UPDATE_START_END_VIOLATE;
			return false;
		}

		if (command.hasParameter(TaskField.PRIORITY.getTaskKeyName())) {
			int newPriority = Integer.parseInt(command.getSpecificParameter(TaskField.PRIORITY.getTaskKeyName()));
			if (newPriority < HIGHEST_TASK_PRIORITY || newPriority > DEFAULT_TASK_PRIORITY) {
				Status._errorCode = Status.ErrorCode.UPDATE_INVALID_PRIORITY;
				return false;
			} else {
				this.setPriority(newPriority);
			}
		}

		if (command.hasParameter(TaskField.NAME.getTaskKeyName())) {
			this.setName(command.getSpecificParameter(TaskField.NAME.getTaskKeyName()));
		}

		if (command.hasParameter(TaskField.UPDATENAME.getTaskKeyName())) {
			this.setName(command.getSpecificParameter(TaskField.UPDATENAME.getTaskKeyName()));
		}

		if (command.hasParameter(TaskField.ID.getTaskKeyName())) {
			this.setID(Integer.parseInt(command.getSpecificParameter(TaskField.ID.getTaskKeyName())));
		}

		if (command.hasParameter(TaskField.COMPLETE.getTaskKeyName())) {
			this.setCompleted(Boolean.parseBoolean(command.getSpecificParameter(TaskField.COMPLETE.getTaskKeyName())));
		}

		return true;
	}

	/**
	 * Checks the status of a task/event.
	 * 
	 * The status of a task is listed below: <br>
	 * 1. Overdue <br>
	 * 2. Due today <br>
	 * 3. Due tomorrow <br>
	 * 4. Due within this week <br>
	 * 5. Floating task <br>
	 * 6. Completed task/event <br>
	 * 7. Not falling in any of the above categories
	 * 
	 * @param today
	 *            Today's date
	 * @return an Integer representing the status of the task/event.
	 */
	public int getTaskCode(Date today) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		Date todayEnd = calendar.getTime();

		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrowEnd = calendar.getTime();

		calendar.add(Calendar.DAY_OF_YEAR, 6);
		Date thisWeekEnd = calendar.getTime();
		if (isCompleted()) {
			// is completed task
			return COMPLETED_TASK_INDEX;
		} else if (isFloatingTask()) {
			// is floating task
			return FLOATING_TASK_INDEX;
		} else if ((isEvent() && _startDate.compareTo(today) < 0) || _endDate.compareTo(today) < 0) {
			// overdue event or task
			return OVERDUE_TASK_INDEX;
		} else if ((isEvent() && _startDate.compareTo(todayEnd) < 0) || _endDate.compareTo(todayEnd) < 0) {
			// today's event or task
			return TODAY_TASK_INDEX;
		} else if ((isEvent() && _startDate.compareTo(tomorrowEnd) < 0) || _endDate.compareTo(tomorrowEnd) < 0) {
			// tomorrow's event or task
			return TOMORROW_TASK_INDEX;
		} else if ((isEvent() && _startDate.compareTo(thisWeekEnd) < 0) || _endDate.compareTo(thisWeekEnd) < 0) {
			// this week's event or task
			return THIS_WEEK_TASK_INDEX;
		} else {
			return UNCODED_TASK_INDEX;
		}
	}

	/**
	 * The comparison method for comparing tasks. This method is used for
	 * sorting tasks in certain order. The default sorting order is by task
	 * priority, then by task deadline and finally by name of task. However,
	 * other sorting criteria, such as by name or by date, is also supported.
	 * 
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself; a value less than 0
	 *         if the Task compared to comes after itself; and a value more than
	 *         0 if the Task compared to comes before itself.
	 * @@author A0130749A
	 */
	@Override
	public int compareTo(Task task) {
		switch (_sortCriterion) {
			case SORT_BY_DATE_KEYWORD:
				return compareByDate(task);

			case SORT_BY_NAME_KEYWORD:
				return compareByName(task);

			case SORT_BY_PRIORITY_KEYWORD:
				return compareByPriority(task);

			default:
				return compareByDate(task);
		}
	}

	/**
	 * The comparison method invoked when sorting criteria is by task deadline.
	 * 
	 * Comparison order is by date, then by priority and then by name.
	 * 
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself; a value less than 0
	 *         if the Task compared to comes after itself; and a value more than
	 *         0 if the Task compared to comes before itself.
	 * @@author A0130749A
	 */
	private int compareByDate(Task task) {
		if (_startDate != null && task.getStartDate() == null) { // compare
																	// event to
																	// task
			if (task.getEndDate() == null) {
				return -1;
			} else if (_startDate.equals(task.getEndDate())) {
				if (_priority == task.getPriority()) {
					return _name.compareTo(task.getName());
				} else {
					return Integer.compare(_priority, task.getPriority());
				}
			} else {
				return _startDate.compareTo(task.getEndDate());
			}
		} else if (_startDate == null && task.getStartDate() != null) { // compare
																		// task
																		// to
																		// event
			if (_endDate == null) {
				return 1;
			} else if (_endDate.equals(task.getStartDate())) {
				if (_priority == task.getPriority()) {
					return _name.compareTo(task.getName());
				} else {
					return Integer.compare(_priority, task.getPriority());
				}
			} else {
				return _endDate.compareTo(task.getStartDate());
			}
		} else if (_startDate != null && task.getStartDate() != null) { // compare
																		// event
																		// to
																		// event
			if (_startDate.equals(task.getStartDate())) {
				if (_priority == task.getPriority()) {
					return _name.compareTo(task.getName());
				} else {
					return Integer.compare(_priority, task.getPriority());
				}
			} else {
				return _startDate.compareTo(task.getStartDate());
			}
		} else { // compare task to task
			if (_endDate == null || task.getEndDate() == null) {
				if (_endDate == null) {
					return 1;
				} else {
					return -1;
				}
			} else if (_endDate.equals(task.getEndDate())) {
				if (_priority == task.getPriority()) {
					return _name.compareTo(task.getName());
				} else {
					return Integer.compare(_priority, task.getPriority());
				}
			} else {
				return _endDate.compareTo(task.getEndDate());
			}
		}
	}

	/**
	 * The comparison method invoked when sorting criteria is by task name.
	 * 
	 * Comparison order is by name, then by priority and then by date.
	 * 
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself; a value less than 0
	 *         if the Task compared to comes after itself; and a value more than
	 *         0 if the Task compared to comes before itself.
	 * @@author A0130749A
	 */
	private int compareByName(Task task) {
		if (_name.equals(task.getName())) {
			if (_priority == task.getPriority()) {
				if (_endDate == null && task.getEndDate() == null) { // compare
																		// floating
																		// tasks
					return 0;
				} else if (_startDate != null && task.getStartDate() == null) { // compare
																				// event
																				// with
																				// task
					return _startDate.compareTo(task.getEndDate());
				} else if (_startDate == null && task.getStartDate() != null) { // compare
																				// task
																				// with
																				// event
					return _endDate.compareTo(task.getStartDate());
				} else if (_startDate != null && task.getStartDate() != null) { // compare
																				// event
																				// with
																				// event
					return _startDate.compareTo(task.getStartDate());
				} else { // compare task with task
					return _endDate.compareTo(task.getEndDate());
				}
			} else {
				if (_endDate == null && task.getEndDate() == null) { // comparing
																		// floating
																		// tasks
					// do nothing
					return 0;
				} else {
					return Integer.compare(_priority, task.getPriority());
				}
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
	 * @return 0 if the Task compared to is equal to itself; a value less than 0
	 *         if the Task compared to comes after itself; and a value more than
	 *         0 if the Task compared to comes before itself.
	 * @@author A0130749A
	 */
	private int compareByPriority(Task task) {
		if (_priority == task.getPriority()) {
			if (_startDate != null && task.getStartDate() == null) { // compare
																		// event
																		// with
																		// task
				if (_endDate == null || task.getEndDate() == null) {
					return _name.compareTo(task.getName());
				} else if (_startDate.equals(task.getEndDate())) {
					return _name.compareTo(task.getName());
				} else {
					return _startDate.compareTo(task.getEndDate());
				}
			} else if (_startDate == null && task.getStartDate() != null) { // compare
																			// task
																			// with
																			// event
				if (_endDate == null || task.getEndDate() == null) {
					return _name.compareTo(task.getName());
				} else if (_endDate.equals(task.getStartDate())) {
					return _name.compareTo(task.getName());
				} else {
					return _endDate.compareTo(task.getStartDate());
				}
			} else if (_startDate != null && task.getStartDate() != null) { // compare
																			// event
																			// with
																			// event
				if (_endDate == null || task.getEndDate() == null) {
					return _name.compareTo(task.getName());
				} else if (_startDate.equals(task.getStartDate())) {
					return _name.compareTo(task.getName());
				} else {
					return _startDate.compareTo(task.getStartDate());
				}
			} else { // compare task with task
				if (_endDate == null) {
					return 1;
				} else if (task.getEndDate() == null) {
					return -1;
				} else if (_endDate.equals(task.getEndDate())) {
					return _name.compareTo(task.getName());
				} else {
					return _endDate.compareTo(task.getEndDate());
				}
			}
		} else {
			return Integer.compare(_priority, task.getPriority());
		}
	}

	/**
	 * Sets all elements of a task object with an array of Strings
	 * 
	 * @param resultsArray
	 *            An array of strings containing the elements to be parsed
	 * @throws ParseException
	 *             if an error is encountered while parsing
	 */
	private void setAllElements(String[] resultsArray) throws ParseException {
		this.setID(Integer.parseInt(resultsArray[0]));
		this.setName(resultsArray[3]);
		this.setStartDate(parseDate(resultsArray[1]));
		this.setEndDate(parseDate(resultsArray[2]));
		this.setPriority(Integer.parseInt(resultsArray[4]));
		this.setCompleted(parseCompleted(resultsArray[5]));
		this.setIsValid(true);
	}

	/**
	 * Converts a given date object to a string in the form
	 * <code>dd/MM/yyyy HH:mm</code>
	 * 
	 * @param date
	 *            given date object to format
	 * @return formatted string
	 * @@author A0127572A
	 */
	private String dateToString(Date date) {
		if (date == null) {
			return "";
		} else {
			return _dateAndTimeFormatter.format(date);
		}
	}

	/**
	 * Parses a list of strings into a list of elements in formatted string
	 * form. Used in the Task(string) constructor.
	 * 
	 * @param resultsArray
	 *            An array to contain the results of the parsing
	 * @param matcherInput
	 *            An array containing strings to parse
	 * 
	 */
	private void parseElementsToArray(String[] resultsArray, String[] matcherInput) {
		for (int i = 0; i < regexArray.length; i++) {
			resultsArray[i] = findMatch(regexArray[i], matcherInput[i]);
			if (resultsArray[i] == null) {
				resultsArray[i] = "";
			}
		}
	}

	/**
	 * Checks if the date-time properties of the task satisfies the formal
	 * definition of a task, event or floating task.
	 * 
	 * To maintain consistency in the logical processing of a Task object, we
	 * define the date-time property of a task as below: <br>
	 * <br>
	 * 1. A typical task ALWAYS has a deadline (i.e. end date-time). <br>
	 * 2. An event ALWAYS has start and end date-times. <br>
	 * 3. A floating task will have NO date-times. <br>
	 * <br>
	 * 
	 * @param startDate
	 *            the starting date-time of the task
	 * @param endDate
	 *            the ending date-time of the task
	 * @return true if the date change preserves a task's formal definition as a
	 *         task, event or floating task; false otherwise.
	 * @@author A0130749A
	 */
	private boolean isAcceptableDateChange(Date startDate, Date endDate) {
		if (startDate != null && endDate == null) {
			return false;
		} else if (startDate == null && endDate == null) {
			return true;
		} else if (startDate == null && endDate != null) {
			return true;
		} else if (startDate.compareTo(endDate) < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sets the isValid boolean value. isValid is set to true if Task object was
	 * constructed successfully.
	 * 
	 * @param _isValid
	 * @@author A0127572A
	 */
	private void setIsValid(boolean _isValid) {
		this._isValid = _isValid;
	}
}