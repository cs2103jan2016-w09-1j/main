import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Command.CommandKey;
import cs2103_w09_1j.esther.DateParser;
import cs2103_w09_1j.esther.InvalidInputException;
import cs2103_w09_1j.esther.Task.TaskField;

public class Parser {

	public static final String ERROR_WRONGFORMAT = "Wrong format. ";
	public static final String ERROR_NOSUCHCOMMAND = "No such command. Please type help to check the available commands.";
	public static final String ERROR_ADDFORMAT = "Wrong format. Format for add command: add [taskname] [from] [date] [time] [to] [date] [time]";
	public static final String ERROR_UPDATEFORMAT = ERROR_WRONGFORMAT
			+ "\n Format for update command: update [taskname/taskID] [fieldname] to [newvalue].";
	public static final String ERROR_DELETEFORMAT = ERROR_WRONGFORMAT
			+ "\nFormat for delete command: delete [taskname/taskid]";
	public static final String ERROR_SEARCHFORMAT = ERROR_WRONGFORMAT
			+ "\nFormat for search command: search [searchword]";
	public static final String ERROR_SHOWFORMAT = ERROR_WRONGFORMAT
			+ "\nFormat for show command : show [on/by/from] [name/id/priority]";
	public static final String ERROR_SORTFORMAT = ERROR_WRONGFORMAT
			+ "\nFormat for sort command: sort by [name/id/startDate/endDate]";
	public static final String ERROR_COMPLETEFORMAT = ERROR_WRONGFORMAT
			+ "\nFormat for complete command: complete [taskName/taskID]";
	public static final String ERROR_DATETIMEFORMAT = ERROR_WRONGFORMAT
			+ "\nYour date or time is invalid. Please check again.";
	public static final String ERROR_PRIORITYFORMAT="Priority is only allowed in integer format.";
	public static final String ERROR_UNKNOWN = "Unknown error.";

	public static final char QUOTE = '"';
	public static final String WHITESPACE = " ";
	public static final String defaultStartTime = "00:00";
	public static final String defaultEndTime = "23:59";

	private Command currentCommand;
	private HashMap<String, String> fieldNameAliases;
	private DateParser dateParser;

	public enum ParseKey {
		ON("on"), BY("by"), FROM("from"), TO("to");

		private String parseKeyName;
		private static final Map<String, ParseKey> lookup = new HashMap<String, ParseKey>();

		private ParseKey(String _parseKeyName) {
			this.parseKeyName = _parseKeyName;
		}

		public String getParseKeyName() {
			return parseKeyName;
		}

		/**
		 * This operations reversely gets the CommandKey from the value.
		 * 
		 * @param commandValue
		 *            The input given by the user.
		 * @return The command based on the input.
		 */
		public static ParseKey get(String parseKeyValue) {
			return lookup.get(parseKeyValue);
		}

		static {
			// Create reverse lookup hash map
			for (ParseKey _parseKeyName : ParseKey.values()) {
				lookup.put(_parseKeyName.getParseKeyName(), _parseKeyName);
			}
		}
	}
	
	public Parser(HashMap<String, String> fieldNameAliases) {
		this.currentCommand = new Command();
		this.dateParser = new DateParser();
		this.fieldNameAliases = fieldNameAliases;
	}

	public Command acceptUserInput(String input) throws ParseException, InvalidInputException {
		String commandName = "";
		String commandInput = "";
		currentCommand.clear();
		try {
			int endOfCommandName = input.indexOf(" ");
			commandName = input.substring(0, endOfCommandName);
			commandInput = input.substring(endOfCommandName + 1, input.length());
		} catch (StringIndexOutOfBoundsException sioobe) {
			commandName = input;
		}
		currentCommand.setCommand(commandName);
		parseCommand(commandName, commandInput);
		return currentCommand;

	}

	private void parseCommand(String commandName, String commandInput) throws ParseException, InvalidInputException {
		CommandKey key = CommandKey.get(commandName);
		if (key == null) {
			throw new InvalidInputException(ERROR_NOSUCHCOMMAND);
		}
		switch (key) {
		case ADD:
			parseAdd(commandInput);
			break;
		case UPDATE:
			parseUpdate(commandInput);
			break;
		case DELETE:
			parseDelete(commandInput);
			break;
		case SEARCH:
			parseSearch(commandInput);
			break;
		case SHOW:
			parseShow(commandInput);
			break;
		case SORT:
			parseSort(commandInput);
			break;
		case COMPLETE:
			parseComplete(commandInput);
			break;
		case UNDO:
			parseUndo();
			break;
		case HELP:
			parseHelp();
			break;
		default:
			throw new InvalidInputException(ERROR_UNKNOWN);
		}

	}

	// Format: add [taskName] [on] [date]
	// add "Tea With Grandma" on tomorrow
	// Current implementation only date
	private void parseAdd(String input) throws ParseException, InvalidInputException {

		// Case 1: add
		if (input.isEmpty()) {
			throw new InvalidInputException(ERROR_ADDFORMAT);
		}

		String[] inputArray = input.split(WHITESPACE);
		String taskName = "";
		int endOfTaskName = -1;

		// Check for taskname

		// Case: "office meeting on budget" (with quote)
		if (inputArray[0].charAt(0) == QUOTE) {
			taskName += inputArray[0].substring(1, inputArray[0].length()) + WHITESPACE;
			for (int i = 1; i < inputArray.length; i++) {
				if (inputArray[i].charAt(inputArray[i].length() - 1) == QUOTE) {
					taskName += inputArray[i].substring(0, inputArray[i].length() - 1);
					endOfTaskName = i;
					break;
				} else {
					taskName += inputArray[i] + WHITESPACE;
					endOfTaskName = -1;
				}
			}
		}
		// Case: office meeting
		else {
			for (int i = 0; i < inputArray.length; i++) {
				if (getParseKey(inputArray[i])) {
					break;
				}
				taskName += inputArray[i] + WHITESPACE;
				endOfTaskName = i;
			}
			taskName = taskName.substring(0, taskName.length() - 1);
		}

		currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), taskName);
		// Case 2: add on Monday (No name) or add "something (no end ")
		if (endOfTaskName == -1) {
			throw new InvalidInputException(ERROR_ADDFORMAT);
		}
		// Case 3: add something or add "Office meeting on Sunday" (no date and
		// time)
		else if (endOfTaskName == inputArray.length - 1) {
			return;
		}
		// Case 4: add something on (empty date/time)
		else if (endOfTaskName == inputArray.length) {
			throw new InvalidInputException(ERROR_ADDFORMAT);
		}
		// Case 5: normal case add something on date/time
		else {
			int supposeToBeParseKeyIndex = endOfTaskName + 1;
			ParseKey parseKey = ParseKey.get(inputArray[supposeToBeParseKeyIndex]);
			if (parseKey == null) {
				throw new InvalidInputException(ERROR_ADDFORMAT);
			}

			if (parseKey == ParseKey.FROM) {
				// Case 6: add something from date/time to date/time
				int toParseKeyIndex = getNextParseKeyIndex(inputArray, supposeToBeParseKeyIndex + 1);
				if (toParseKeyIndex == -1) {
					System.out.println(toParseKeyIndex);
					throw new InvalidInputException(ERROR_ADDFORMAT);
				}
				String startDateTime = "";
				String endDateTime = "";
				for (int i = supposeToBeParseKeyIndex + 1; i < toParseKeyIndex; i++) {
					startDateTime += inputArray[i] + " ";
				}
				for (int i = toParseKeyIndex + 1; i < inputArray.length; i++) {
					endDateTime += inputArray[i] + " ";
				}
				String[] startDateTimeArray = dateParser.getDateTime(startDateTime);
				String[] endDateTimeArray = dateParser.getDateTime(endDateTime);
				checkNullDateTime(startDateTimeArray);
				checkNullDateTime(endDateTimeArray);
				addStartEndDateTime(startDateTimeArray, endDateTimeArray);
				int startValid = addDateTime(startDateTimeArray, TaskField.STARTDATE, TaskField.STARTTIME);
				int endValid = addDateTime(endDateTimeArray, TaskField.ENDDATE, TaskField.ENDTIME);
				if (startValid == -1 || endValid == -1) {
					throw new InvalidInputException(ERROR_DATETIMEFORMAT);
				}
			}
			// Case 5
			else {
				int otherParseKeyIndex = getNextParseKeyIndex(inputArray, supposeToBeParseKeyIndex + 1);
				if (otherParseKeyIndex != -1) {
					System.out.println(otherParseKeyIndex);
					throw new InvalidInputException(ERROR_ADDFORMAT);
				}
				String dateTime = "";
				for (int i = supposeToBeParseKeyIndex + 1; i < inputArray.length; i++) {
					dateTime += inputArray[i] + " ";
				}
				String[] dateTimeArray = dateParser.getDateTime(dateTime);
				int valid = addDateTime(dateTimeArray, TaskField.ENDDATE, TaskField.ENDTIME);
				if (valid == -1) {
					throw new InvalidInputException(ERROR_DATETIMEFORMAT);
				}
			}
		}

	}

	// Format: update [taskName/taskID] [taskField] to [updatedValue]
	// update Tea With Grandma date to 22/07/2016
	private void parseUpdate(String input) throws InvalidInputException, ParseException {
		String[] inputArray = input.split(WHITESPACE);

		int toParseKeyIndex = getToKeyIndex(inputArray, 0); // get the to
		if (toParseKeyIndex == -1) {
			throw new InvalidInputException(ERROR_UPDATEFORMAT);
		}

		String taskName = "";
		int endOfTaskName = -1;
		if (inputArray[0].charAt(0) == QUOTE) {
			taskName = inputArray[0].substring(1, inputArray[0].length()) + WHITESPACE;
			for (int i = 1; i < toParseKeyIndex - 1; i++) {
				if (inputArray[i].charAt(inputArray[i].length() - 1) == QUOTE) {
					taskName += inputArray[i].substring(0, inputArray[i].length() - 1);
					endOfTaskName = i;
					break;
				}
				taskName += inputArray[i] + WHITESPACE;
			}
			if (endOfTaskName == -1) {
				throw new InvalidInputException(ERROR_UPDATEFORMAT);
			}
		} else {
			for (int i = 0; i < toParseKeyIndex - 1; i++) {
				taskName += inputArray[i] + WHITESPACE;
			}
			taskName = taskName.substring(0, taskName.length() - 1);
		}

		int getNameOrID = isNameOrID(taskName);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID.getTaskKeyName(), taskName);
		} else if (getNameOrID == 0) {
			currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), taskName);
		} else {
			throw new InvalidInputException(ERROR_UNKNOWN);
		}

		String taskFieldName = fieldNameAliases.get(inputArray[toParseKeyIndex - 1]);
		if (taskFieldName == null) {
			throw new InvalidInputException(ERROR_UPDATEFORMAT);
		}

		TaskField aliaseField = TaskField.get(taskFieldName);
		if (aliaseField == null) {
			throw new InvalidInputException(ERROR_UPDATEFORMAT);
		} else if (aliaseField == TaskField.NAME){
			aliaseField = TaskField.UPDATENAME;
		}
		
		String newValue = "";
		for (int i = toParseKeyIndex + 1; i < inputArray.length; i++) {
			newValue += inputArray[i] + " ";
		}
		newValue = newValue.substring(0, newValue.length() - 1);
		if (newValue.isEmpty()) {
			throw new InvalidInputException(ERROR_UPDATEFORMAT);
		}
		if (aliaseField == TaskField.STARTDATE || aliaseField == TaskField.ENDDATE) {
			newValue = dateParser.getDateTime(newValue)[0];
		} else if (aliaseField == TaskField.STARTTIME || aliaseField == TaskField.ENDTIME) {
			newValue = dateParser.getDateTime(newValue)[1];
		} else if (aliaseField == TaskField.PRIORITY) {
			try {
				Integer.parseInt(newValue);
			} catch (NumberFormatException nfe) {
				throw new InvalidInputException(ERROR_PRIORITYFORMAT);
			}
		}

		if (newValue == null) {
			throw new InvalidInputException(ERROR_DATETIMEFORMAT);
		}
		currentCommand.addFieldToMap(aliaseField.getTaskKeyName(), newValue);
	}

	// Format: delete 10
	private void parseDelete(String input) throws InvalidInputException {
		if (input.isEmpty()) {
			throw new InvalidInputException(ERROR_DELETEFORMAT);
		}

		if (input.charAt(0) == QUOTE) {
			if (input.charAt(input.length() - 1) == QUOTE) {
				input = input.substring(1, input.length() - 1);
			} else {
				throw new InvalidInputException(ERROR_DELETEFORMAT);
			}
		}
		int getNameOrID = isNameOrID(input);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID.getTaskKeyName(), input);
		} else if (getNameOrID == 0) {
			currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), input);
		} else {
			throw new InvalidInputException(ERROR_UNKNOWN);
		}
	}

	// Format: search [task name]
	private void parseSearch(String input) throws InvalidInputException {
		if (input.isEmpty()) {
			throw new InvalidInputException(ERROR_SEARCHFORMAT);
		}

		if (input.charAt(0) == QUOTE) {
			if (input.charAt(input.length() - 1) == QUOTE) {
				input = input.substring(1, input.length() - 1);
			} else {
				throw new InvalidInputException(ERROR_SEARCHFORMAT);
			}
		}
		currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), input);
	}

	// Format: show by name
	private void parseShow(String input) throws InvalidInputException {

		if (input.isEmpty()) {
			currentCommand.addFieldToMap(TaskField.SHOW.getTaskKeyName(), TaskField.ID.getTaskKeyName());
			return;
		}
		String[] inputArray = input.split(WHITESPACE);

		if (inputArray.length != 2) {
			throw new InvalidInputException(ERROR_SORTFORMAT);
		}

		String fieldName = fieldNameAliases.get(inputArray[1]);
		if (fieldName == null) {
			throw new InvalidInputException(ERROR_SORTFORMAT);
		}
		currentCommand.addFieldToMap(TaskField.SHOW.getTaskKeyName(), fieldName);
	}

	// Format: show by [field]
	private void parseSort(String input) throws InvalidInputException {

		if (input == "") {
			throw new InvalidInputException(ERROR_SORTFORMAT);
		}
		String[] inputArray = input.split(WHITESPACE);
		if (inputArray.length != 2) {
			throw new InvalidInputException(ERROR_SORTFORMAT);
		}

		String fieldName = fieldNameAliases.get(inputArray[1]);
		if (fieldName == null) {
			throw new InvalidInputException(ERROR_SORTFORMAT);
		}
		currentCommand.addFieldToMap(TaskField.SORT.getTaskKeyName(), fieldName);

	}

	// Format: complete 20
	private void parseComplete(String input) throws InvalidInputException {
		if (input.isEmpty()) {
			throw new InvalidInputException(ERROR_COMPLETEFORMAT);
		}

		if (input.charAt(0) == QUOTE) {
			if (input.charAt(input.length() - 1) == QUOTE) {
				input = input.substring(1, input.length() - 1);
			} else {
				throw new InvalidInputException(ERROR_COMPLETEFORMAT);
			}
		}
		int getNameOrID = isNameOrID(input);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID.getTaskKeyName(), input);
		} else if (getNameOrID == 0) {
			currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), input);
		} else {
			throw new InvalidInputException(ERROR_UNKNOWN);
		}
	}

	// Format: undo
	private void parseUndo() {
		currentCommand.addFieldToMap(TaskField.UNDO.getTaskKeyName(), "");

	}

	// Format: help
	private void parseHelp() {
		currentCommand.addFieldToMap(TaskField.HELP.getTaskKeyName(), "");
	}

	private int isNameOrID(String givenInput) {
		try {
			Integer.parseInt(givenInput);
			return 1;
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	private boolean getParseKey(String input) {
		for (ParseKey parseKeyName : ParseKey.values()) {
			if (input.equals(parseKeyName.getParseKeyName())) {
				return true;
			}
		}
		return false;
	}

	private int getNextParseKeyIndex(String[] inputArray, int startIndex) {
		for (ParseKey parseKeyName : ParseKey.values()) {
			for (int i = startIndex; i < inputArray.length; i++) {
				if (inputArray[i].equals(parseKeyName.getParseKeyName())) {
					return i;
				}
			}
		}
		return -1;
	}

	private int getToKeyIndex(String[] inputArray, int startIndex) {
		for (int i = startIndex; i < inputArray.length; i++) {
			if (inputArray[i].equals(ParseKey.TO.getParseKeyName())) {
				return i;
			}
		}
		return -1;
	}

	private void checkNullDateTime(String[] dateTimeArray) throws InvalidInputException {
		if (dateTimeArray[0] == null && dateTimeArray[1] == null) {
			throw new InvalidInputException(ERROR_DATETIMEFORMAT);
		}
	}

	private void addStartEndDateTime(String[] startDateTimeArray, String[] endDateTimeArray)
			throws InvalidInputException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String currentDate = sdf.format(date);

		// Transform date
		if (startDateTimeArray[0] == null) {
			startDateTimeArray[0] = currentDate;
		}
		if (endDateTimeArray[0] == null) {
			endDateTimeArray[0] = startDateTimeArray[0];
		}

		// Transform time
		if (startDateTimeArray[1] != null || endDateTimeArray[1] != null) {
			if (startDateTimeArray[1] == null) {
				startDateTimeArray[1] = defaultStartTime;
			} else if (endDateTimeArray[1] == null) {
				endDateTimeArray[1] = defaultEndTime;
			}
		}

		Date startDate = sdf.parse(startDateTimeArray[0]);
		Date endDate = sdf.parse(endDateTimeArray[0]);
		//check if start date is after end date
		if (startDate.compareTo(endDate) > 0) {
			throw new InvalidInputException(ERROR_DATETIMEFORMAT);
		} else if(startDateTimeArray[1] != null && endDateTimeArray[1] != null){
			if (startDateTimeArray[1].compareTo(endDateTimeArray[1]) > 0) {
				throw new InvalidInputException(ERROR_DATETIMEFORMAT);
			}
		}
	}

	private int addDateTime(String[] dateTimeArray, TaskField dateField, TaskField timeField) {
		int valid = -1;
		if (dateTimeArray[0] != null) {
			currentCommand.addFieldToMap(dateField.getTaskKeyName(), dateTimeArray[0]);
			valid = 1;
		}
		if (dateTimeArray[1] != null) {
			currentCommand.addFieldToMap(timeField.getTaskKeyName(), dateTimeArray[1]);
			valid = 1;
		}
		return valid;
	}
}