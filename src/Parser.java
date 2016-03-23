import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Command.CommandKey;
import cs2103_w09_1j.esther.DateParser;
import cs2103_w09_1j.esther.InvalidInputException;
import cs2103_w09_1j.esther.Task.TaskField;

public class Parser {

	public static final String ERROR_NOSUCHCOMMAND = "No such command. Please type help to check the available commands.";
	public static final String ERROR_ADDFORMAT = "Wrong format. Format for add command: add [taskname] [from] [date] [time] [to] [date] [time]";
	public static final String ERROR_DELETEFORMAT = "Wrong format.Format for delete command: delete [taskname/taskid]";
	public static final String ERROR_SEARCHFORMAT = "Wrong format. Format for search command: search [searchword]";
	public static final String ERROR_SHOWFORMAT = "Wrong format. Format for show command : show [on/by/from] [name/id/priority]";
	public static final String ERROR_SORTFORMAT = "Wrong format. Format for sort command: sort by [name/id/startDate/endDate]";
	public static final String ERROR_COMPLETEFORMAT = "Wrong format. Format for complete command: complete [taskName/taskID]";
	public static final String ERROR_UNKNOWN = "Unknown error.";

	public static final char QUOTE = '"';
	public static final String WHITESPACE = " ";
	private Command currentCommand;

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

	public static void main(String[] args) throws ParseException, InvalidInputException {
		Parser parser = new Parser();
		Command command = parser.acceptUserInput("add \"meeting on monday\" from 3pm to 5pm");
		HashMap<String, String> map = command.getParameters();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println("Key " + key + "  Value " + value);
		}
	}

	public Parser() {
		this.currentCommand = new Command();
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
		case COMPLETED:
			parseComplete(commandInput);
			break;
		case UNDO:
			parseUndo();
			break;
		case HELP:
			parseHelp();
			break;
		default:
			throw new InvalidInputException(ERROR_NOSUCHCOMMAND);
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
		System.out.println(inputArray[0]);
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
		} else {
			for (int i = 0; i < inputArray.length; i++) {
				if (getParseKey(inputArray[i])) {
					endOfTaskName = i;
					break;
				}
				taskName += inputArray[i] + " ";
				endOfTaskName = i;
			}
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

			int toParseKeyIndex = 0;
			if (parseKey == ParseKey.FROM) {
				//Case 6: add something from date/time to date/time
				toParseKeyIndex = getToKeyIndex(inputArray, supposeToBeParseKeyIndex + 2);
				if (toParseKeyIndex == -1) {
					throw new InvalidInputException(ERROR_ADDFORMAT);
				}
				String startDateTime="";
				String endDateTime="";
				for(int i=supposeToBeParseKeyIndex;i<toParseKeyIndex;i++){
					startDateTime+=inputArray[i]+" ";
				}
				for(int i=toParseKeyIndex;i<inputArray.length;i++){
					endDateTime+=inputArray[i]+" ";
				}
				
				currentCommand.addFieldToMap(TaskField.STARTDATE.getTaskKeyName(), startDateTime);
				currentCommand.addFieldToMap(TaskField.ENDDATE.getTaskKeyName(), endDateTime);
			}
			else{
				String dateTime="";
				for(int i=supposeToBeParseKeyIndex;i<inputArray.length;i++){
					dateTime+=inputArray[i]+" ";
				}
				currentCommand.addFieldToMap(TaskField.ENDDATE.getTaskKeyName(), dateTime);
			}
		}

	}

	// Format: update [taskName/taskID] [taskField] to [updatedValue]
	// update Tea With Grandma date to 22/07/2016
	private void parseUpdate(String input) {
		String[] inputArray = input.split(WHITESPACE);
		int parseKeyIndex = getParseKeyIndex(inputArray); // get the .to

		for (int i = 0; i < inputArray.length; i++) {
			System.out.print(inputArray[i] + " | ");
		}

		String updateBy = "";
		for (int i = 0; i < parseKeyIndex - 1; i++) {
			updateBy += inputArray[i] + " ";
		}
		int getNameOrID = isNameOrID(updateBy);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID.getTaskKeyName(), updateBy);
		} else if (getNameOrID == 0) {
			currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), updateBy);
			// currentCommand.addFieldToMap(TaskField.UPDATENAME.getTaskKeyName(),
			// inputArray[1]);
		} else {
			// Throw error
		}
		// TaskField taskField = TaskField.get(inputArray[2]);
		String updateValue = "";
		for (int i = parseKeyIndex + 1; i < inputArray.length; i++) {
			updateValue += inputArray[i] + " ";
		}
		System.out.println(updateValue);
		TaskField givenField = TaskField.get(inputArray[parseKeyIndex - 1]);
		currentCommand.addFieldToMap(givenField.getTaskKeyName(), updateValue);
		// if (inputArray[2].equals("taskName")) {
		// currentCommand.addFieldToMap(TaskField.UPDATENAME.getTaskKeyName(),
		// updateValue);
		// } else {
		// currentCommand.addFieldToMap(inputArray[2], updateValue);
		// }
	}

	// Format: delete 10
	private void parseDelete(String input) throws InvalidInputException {
		if (input == "") {
			throw new InvalidInputException(ERROR_DELETEFORMAT);
		}
		// String[] inputArray = input.split(SPLITBY_WHITESPACE);
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
		if (input == "") {
			throw new InvalidInputException(ERROR_SEARCHFORMAT);
		}
		currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), input);
	}

	// Format: show by name
	private void parseShow(String input) throws InvalidInputException {

		if (input == "") {
			currentCommand.addFieldToMap(TaskField.SHOW.getTaskKeyName(), TaskField.ID.getTaskKeyName());
			return;
		}
		String[] inputArray = input.split(WHITESPACE);

		if (inputArray.length != 2) {
			throw new InvalidInputException(ERROR_SORTFORMAT);
		}
		currentCommand.addFieldToMap(TaskField.SHOW.getTaskKeyName(), inputArray[1]);
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
		currentCommand.addFieldToMap(TaskField.SORT.getTaskKeyName(), inputArray[1]);

	}

	// Format: complete 20
	private void parseComplete(String input) throws InvalidInputException {
		if (input == "") {
			throw new InvalidInputException(ERROR_COMPLETEFORMAT);
		}
		int getNameOrID = isNameOrID(input);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID.getTaskKeyName(), input);
		} else if (getNameOrID == 1) {
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

	private int getParseKeyIndex(String[] inputArray) {
		for (ParseKey parseKeyName : ParseKey.values()) {
			for (int i = 0; i < inputArray.length; i++) {
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

	private String getProperDateFormat(String inputDate) {
		DateParser dp = new DateParser();
		String dateFormat = dp.getDateFormat(inputDate);
		return dateFormat;
	}

	private String getProperTimeFormat(String input, String dateFormat) {
		DateParser dp = new DateParser();
		String timeFormat = dp.getTimeFormat(input, dateFormat);
		return timeFormat;
	}

	private String[] getDateTime(String input, String listedDateFormat, String listedTimeFormat) throws ParseException {
		String[] dateTimeList = new String[2];
		DateParser dp = new DateParser();
		if (listedDateFormat != "" && listedTimeFormat != "") {
			String[] givenDate = dp.getDateTime(input, listedDateFormat + " " + listedTimeFormat);
			dateTimeList = givenDate;
		} else if (listedDateFormat != "" && listedTimeFormat == "") {
			dateTimeList[0] = dp.getDate(input, listedDateFormat);
		} else if (listedTimeFormat != "") {
			dateTimeList[1] = dp.getTime(input, listedTimeFormat);
		}
		return dateTimeList;
	}

}