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
 * CHANGES MADE: Added to TaskField, STARTDATE, ENDDATE, STARTTIME, ENDTIME, 
 * 				 Removed date to cater start and end date.
 * 
 * 
 */

// import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cs2103_w09_1j.esther.Status;

import sun.util.resources.cldr.id.LocaleNames_id;

public class Task implements Comparable<Task> {
	public enum TaskField {
		NAME("taskName"), ID("taskID"), PRIORITY("priority"), STARTDATE("startDate"), ENDDATE("endDate"), STARTTIME(
				"startTime"), ENDTIME("endTime"), SORT("order"), UPDATENAME("updateName"), KEYWORD(
						"keyword"), SHOW("order"), UNDO("undo"), HELP("help"), COMPLETE(
								"complete"), PATH("path");

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
	private int _priority; // for now, lower number indicates higher priority
	private int _id;
	private boolean _isCompleted;
	private boolean _isValid = false;

	private static String _sortCriterion = SORT_BY_PRIORITY_KEYWORD;
	private static int _assignId = DEFAULT_STARTING_ID;

	public static SimpleDateFormat _dateOnlyFormatter = new SimpleDateFormat("dd/MM/yyyy");
	public static SimpleDateFormat _dateAndTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private final static Logger taskLogger = Logger.getLogger("taskLogger");
	private final static int NUM_FIELDS = 6;
	private final static String completedStr = "Completed";
	private final static String notCompletedStr = "Incomplete";

	private final static String delimiterPattern = "\\|";
	private final static String idnoString = "ID\\: (\\d+)";
	private final static String dateString = "\\[([^\\]]+)\\] ";
	private final static String nameString = "([^\\|]+)";
	private final static String prioString = "Priority: (\\d+)";
	private final static String compString = "(" + completedStr + "|" + notCompletedStr + ")";
	private final static String[] regexArray = { idnoString, dateString, dateString, nameString, prioString,
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
				? command.getSpecificParameter(TaskField.STARTDATE.getTaskKeyName()) : null;

		String startTimeString = command.hasParameter(TaskField.STARTTIME.getTaskKeyName())

								 ? command.getSpecificParameter(TaskField.STARTTIME.getTaskKeyName())
								 : null;
		startDate = parseDateTimeToString(today, startDateString, startTimeString, true);

		String endDateString = command.hasParameter(TaskField.ENDDATE.getTaskKeyName())
				? command.getSpecificParameter(TaskField.ENDDATE.getTaskKeyName()) : null;

		String endTimeString = command.hasParameter(TaskField.ENDTIME.getTaskKeyName())

							   ? command.getSpecificParameter(TaskField.ENDTIME.getTaskKeyName())
							   : null;
		endDate = parseDateTimeToString(today, endDateString, endTimeString, false);

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
	 * 
	 * @param string
	 * @@author A0127572A
	 * @throws ParseException
	 */
	public Task(String string) throws ParseException {
		this();
		String[] resultsArray = new String[NUM_FIELDS];
		String[] matcherInput = string.split(delimiterPattern);

		if (matcherInput.length != NUM_FIELDS) {
			taskLogger.severe(
					"Task constructor expected " + NUM_FIELDS + " arguments but received " + matcherInput.length + ".");
			return;
		}

		for (int i = 0; i < regexArray.length; i++) {
			resultsArray[i] = findMatch(regexArray[i], matcherInput[i]);
			if (resultsArray[i] == null) {
				taskLogger.warning("Task builder could not parse " + i + "th element for task " + resultsArray[0]);
				resultsArray[i] = "";
			}
		}

		if (resultsArray[0] == "") {
			taskLogger.severe("Task constructor cannot find an ID");
			return;
		}

		int localID = Integer.parseInt(resultsArray[0]);
		Date sDate = parseDate(resultsArray[1]);
		Date eDate = parseDate(resultsArray[2]);
		String taskName = resultsArray[3];
		int priority = Integer.parseInt(resultsArray[4]);
		boolean complete = parseCompleted(resultsArray[5]);

		this.setID(localID);
		this.setName(taskName.trim());
		this.setStartDate(sDate);
		this.setEndDate(eDate);
		this.setPriority(priority);
		this.setCompleted(complete);
		this.setIsValid(true);
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
	 * 
	 * @return
	 * @@author A0127572A
	 */
	public String sDateToString() {
		return dateToString(_startDate);
	}

	/**
	 * 
	 * @return
	 * @@author A0127572A
	 */
	public String eDateToString() {
		return dateToString(_endDate);
	}

	/**
	 * 
	 * @param date
	 * @return
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
	 * 
	 * @param today
	 * @param dateString
	 * @param timeString
	 * @return
	 * @throws ParseException
	 * @@author A0127572A
	 */

	public static Date parseDateTimeToString(Date today, String dateString, String timeString, boolean start)
			throws ParseException {
		Date date = null;
		if (dateString != null && timeString != null) {
			// System.out.println("Date and time parts are modified.");
			date = _dateAndTimeFormatter.parse(dateString + " " + timeString);
		} else if (dateString != null && timeString == null) {
			date = _dateAndTimeFormatter.parse(dateString + " " + (start ? "00:00" : "23:59"));
		} else if (dateString == null && timeString != null) {
			// System.out.println("Time part is modified.");
			date = _dateAndTimeFormatter.parse(_dateOnlyFormatter.format(today) + " " + timeString);
		}
		return date;
	}

	/**
	 * 
	 * @param dateStr
	 * @return
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
	 * 
	 * @return
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
	 * 
	 * @param completeStr
	 * @return
	 * @@author A0127572A
	 */
	public boolean parseCompleted(String completeStr) {
		return completeStr.contains(completedStr);
	}

	/**
	 * 
	 * @return
	 * @@author A0127572A
	 */
	public boolean isValid() {
		return _isValid;
	}

	/**
	 * 
	 * @param _isValid
	 * @@author A0127572A
	 */
	public void setIsValid(boolean _isValid) {
		this._isValid = _isValid;
	}

	/**
	 * Creates a copy of this Task object.
	 * 
	 * @return a copy of the Task object
	 * @@author A0130749A
	 */
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
			newStartDate = parseDateTimeToString(new Date(), startDate, startTime, true);
			this.setStartDate(newStartDate);
		} else if (startTime == null) {
			newStartDate = parseDateTimeToString(_startDate, startDate, oldStartTime, true);
			this.setStartDate(newStartDate);
		} else {
			newStartDate = parseDateTimeToString(_startDate, startDate, startTime, true);
			this.setStartDate(newStartDate);
		}

		if (_endDate == null) {
			newEndDate = parseDateTimeToString(new Date(), endDate, endTime, false);
			this.setEndDate(newEndDate);
		} else if (endTime == null) {
			newEndDate = parseDateTimeToString(_endDate, endDate, oldEndTime, false);
			this.setEndDate(newEndDate);
		} else {

			newEndDate = parseDateTimeToString(_endDate, endDate, endTime, false);
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
		// System.out.println(dateToString(_startDate));
		// System.out.println(dateToString(_endDate));

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
	 * Checks the status of a task/event.
	 * 
	 * The status of a task is listed below:
	 * <br>
	 * 1. Overdue
	 * <br>
	 * 2. Due today
	 * <br>
	 * 3. Due tomorrow
	 * <br>
	 * 4. Due within this week
	 * <br>
	 * 5. Floating task
	 * <br>
	 * 6. Completed task/event
	 * <br>
	 * 7. Not falling in any of the above categories
	 * 
	 * @param today Today's date
	 * @return an Integer representing the status of the task/event.
	 */
	public int getTaskCode(Date today) {
		//System.out.println(today);
		Date todayEnd = (Date) today.clone();
		todayEnd.setHours(23);
		todayEnd.setMinutes(59);
		//System.out.println(todayEnd);
		Date tomorrow = (Date) today.clone();
		tomorrow.setDate(today.getDate() + 1);
		tomorrow.setHours(0);
		tomorrow.setMinutes(0);
		Date tomorrowEnd = (Date) tomorrow.clone();
		tomorrowEnd.setHours(23);
		tomorrowEnd.setMinutes(59);
		//System.out.println(tomorrow);
		Date afterTomorrow = (Date) today.clone();
		afterTomorrow.setDate(today.getDate() + 2);
		afterTomorrow.setHours(0);
		afterTomorrow.setMinutes(0);
		//System.out.println(afterTomorrow);
		Date thisWeek = (Date) today.clone();
		thisWeek.setDate(today.getDate() + 7);
		thisWeek.setHours(23);
		thisWeek.setMinutes(59);
		if (isCompleted()) { // is completed task
			return COMPLETED_TASK_INDEX;
		} else if (isFloatingTask()) { // is floating task
			return FLOATING_TASK_INDEX;
		} else if ((isEvent() && _startDate.compareTo(today) < 0) ||
				   _endDate.compareTo(today) < 0) { // overdue event or task
			return OVERDUE_TASK_INDEX;
		} else if ((isEvent() && _startDate.compareTo(today) >= 0 && _startDate.compareTo(todayEnd) < 0) ||
				   (_endDate.compareTo(today) >= 0 && _endDate.compareTo(todayEnd) < 0)) { // today's event or task
			return TODAY_TASK_INDEX;
		} else if ((isEvent() && _startDate.compareTo(tomorrow) >= 0 && _startDate.compareTo(tomorrowEnd) < 0) ||
				   (_endDate.compareTo(tomorrow) >= 0 && _endDate.compareTo(tomorrowEnd) < 0)) { // tomorrow's event or task
			return TOMORROW_TASK_INDEX;
		} else if ((isEvent() && _startDate.compareTo(afterTomorrow) >= 0 && _startDate.compareTo(thisWeek) < 0) ||
				   (_endDate.compareTo(afterTomorrow) >= 0 && _endDate.compareTo(thisWeek) < 0)) {
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
			// System.out.println("Sorting by date.");
			return compareByDate(task);

		case SORT_BY_NAME_KEYWORD:
			return compareByName(task);

		case SORT_FLOATING_BY_NAME_KEYWORD:
			return compareFloatingByName(task);

		case SORT_BY_PRIORITY_KEYWORD:
			return compareByPriority(task);

		case SORT_FLOATING_BY_PRIORITY_KEYWORD:
			return compareFloatingByPriority(task);

		case SORT_BY_ID_KEYWORD:
			return compareById(task);

		case SORT_BY_START_DATE_KEYWORD:
			return compareByDate(task);

		case SORT_BY_END_DATE_KEYWORD:
			return compareByDate(task);

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
		if (_startDate != null && task.getStartDate() == null) { // compare event to task
			if (_startDate.equals(task.getEndDate())) {
				if (_priority == task.getPriority()) {
					return _name.compareTo(task.getName());
				} else {
					return Integer.compare(_priority, task.getPriority());
				}
			} else {
				return _startDate.compareTo(task.getEndDate());
			}
		} else if (_startDate == null && task.getStartDate() != null) { // compare task to event
			if (_endDate.equals(task.getStartDate())) {
				if (_priority == task.getPriority()) {
					return _name.compareTo(task.getName());
				} else {
					return Integer.compare(_priority, task.getPriority());
				}
			} else {
				return _endDate.compareTo(task.getStartDate());
			}
		} else if (_startDate != null && task.getStartDate() != null) { // compare event to event
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
			if (_endDate.equals(task.getEndDate())) {
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
				if (_endDate == null && task.getEndDate() == null) { // comparing floating tasks
					// do nothing
					return 0;
				} else if (_startDate != null && task.getStartDate() == null) { // compare event with task
					return _startDate.compareTo(task.getEndDate());
				} else if (_startDate == null && task.getStartDate() != null) { // compare task with event
					return _endDate.compareTo(task.getStartDate());
				} else if (_startDate != null && task.getStartDate() != null) { // compare event with event
					return _startDate.compareTo(task.getStartDate());
				} else { // compare task with task
					return _endDate.compareTo(task.getEndDate());
				}
			} else {
				return Integer.compare(_priority, task.getPriority());
			}
		} else {
			return _name.compareTo(task.getName());
		}
	}

	/**
	 * The comparison method invoked when sorting criteria is by task name. This
	 * comparison method is used only on floating tasks.
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
	private int compareFloatingByName(Task task) {
		if (_name.equals(task.getName())) {
			return Integer.compare(_priority, task.getPriority());
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
			if (_startDate != null && task.getStartDate() == null) { // compare event with task
				if (_endDate == null || task.getEndDate() == null) {
					return _name.compareTo(task.getName());
				} else if (_startDate.equals(task.getEndDate())) {
					return _name.compareTo(task.getName());
				} else {
					return _startDate.compareTo(task.getEndDate());
				}
			} else if (_startDate == null && task.getStartDate() != null) { // compare task with event
				if (_endDate == null || task.getEndDate() == null) {
					return _name.compareTo(task.getName());
				} else if (_endDate.equals(task.getStartDate())) {
					return _name.compareTo(task.getName());
				} else {
					return _endDate.compareTo(task.getStartDate());
				}
			} else if (_startDate != null && task.getStartDate() != null) { // compare event with event
				if (_endDate == null || task.getEndDate() == null) {
					return _name.compareTo(task.getName());
				} else if (_startDate.equals(task.getStartDate())) {
					return _name.compareTo(task.getName());
				} else {
					return _startDate.compareTo(task.getStartDate());
				}
			} else { // compare task with task
				if (_endDate == null || task.getEndDate() == null) {
					return _name.compareTo(task.getName());
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
	 * The comparison method invoked when sorting criteria is by task priority.
	 * This comparison method is used only on floating tasks.
	 * 
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself; a value less than 0
	 *         if the Task compared to comes after itself; and a value more than
	 *         0 if the Task compared to comes before itself.
	 * @@author A0130749A
	 */
	private int compareFloatingByPriority(Task task) {
		if (_priority == task.getPriority()) {
			return _name.compareTo(task.getName());
		} else {
			return Integer.compare(_priority, task.getPriority());
		}
	}

	/**
	 * The comparison method invoked when sorting criteria is by task ID.
	 * 
	 * @param task
	 *            the Task object to compare to
	 * @return 0 if the Task compared to is equal to itself; a value less than 0
	 *         if the Task compared to comes after itself; and a value more than
	 *         0 if the Task compared to comes before itself.
	 * @@author A0130749A
	 */
	private int compareById(Task task) {
		return Integer.compare(_id, task.getId());
	}

}