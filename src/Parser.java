import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Command.CommandKey;
import cs2103_w09_1j.esther.DateParser;
import cs2103_w09_1j.esther.InvalidInputException;
import cs2103_w09_1j.esther.Task.TaskField;

public class Parser {
	public static final String SPLITBY_WHITESPACE = " ";
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

	public static void main(String[] args) throws ParseException {
		Parser parser = new Parser();
		Command command = parser.acceptUserInput("add Office Meeting");
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

	public Command acceptUserInput(String input) throws ParseException {
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

	private void parseCommand(String commandName, String commandInput) throws ParseException {
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
		case SHOW:
			parseShow(commandInput);
			break;
		case SORT:
			parseSort(commandInput);
			break;
		case COMPLETED:
			parseCompleted(commandInput);
			break;
		case HELP:
			parseHelp();
			break;
		case UNDO:
			parseUndo();
			break;
		default:
			System.out.println("Haven't code");
		}

	}

	// Format: show by [field]
	private void parseSort(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		try{
		currentCommand.addFieldToMap(TaskField.SORT.getTaskKeyName(), inputArray[1]);
		}catch(ArrayIndexOutOfBoundsException ai){
			System.out.println("Sort error");
		}

	}

	// Format: undo
	private void parseUndo() {
		// TODO Auto-generated method stub
		currentCommand.addFieldToMap(TaskField.UNDO.getTaskKeyName(), "");

	}

	// Format: help
	private void parseHelp() {
		currentCommand.addFieldToMap(TaskField.HELP.getTaskKeyName(), "");
	}

	// Format: add [taskName] [on] [date]
	// add "Tea With Grandma" on tomorrow
	// Current implementation only date
	private void parseAdd(String input) throws ParseException {
		// String[] inputArray = input.split(SPLITBY_WHITESPACE);
		// int parseKeyIndex = getParseKeyIndex(inputArray);
		int taskNameStartIndex = -1;
		int taskNameEndIndex = -1;
		taskNameStartIndex = input.indexOf("\"");

		if (taskNameStartIndex != -1) {
			taskNameEndIndex = input.lastIndexOf("\"");
		}

		String afterQuotes = input.substring(taskNameEndIndex + 1);
		ParseKey givenParseKey = getParseKey(afterQuotes);
		// Case 1: add Tea With Grandma (No parse key)

		String taskName = "";
		if (givenParseKey == null) {
			if (taskNameStartIndex != -1 && taskNameEndIndex != -1) {
				taskName = input.substring(taskNameStartIndex + 1, taskNameEndIndex);
			} else {
				taskName = input;
			}
			currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), taskName);
			return;
		}

		// Case 2: add Tea With Grandma [from] Thursday 3pm [to] friday 3pm
		int parseKeyIndex = input.indexOf(givenParseKey.getParseKeyName());
		if (taskNameStartIndex != -1 && taskNameEndIndex != -1) {
			taskName = input.substring(taskNameStartIndex + 1, taskNameEndIndex);
		} else {
			taskName = input.substring(0, parseKeyIndex - 1);
		}
		currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), taskName);

		// Parse for date and time
		int dateTimeIndex = afterQuotes.indexOf(givenParseKey.getParseKeyName());
		String dateTimeField = afterQuotes.substring(dateTimeIndex + givenParseKey.getParseKeyName().length() + 1);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String startDate = "";
		String endDate = "";
		String startTime = "";
		String endTime = "";

		System.out.println(dateTimeField);
		String listedDateFormat = getProperDateFormat(dateTimeField);
		String listedTimeFormat = getProperTimeFormat(dateTimeField, listedDateFormat);
		System.out.println(listedDateFormat);
		System.out.println(listedTimeFormat);
		DateParser dp = new DateParser();
		// List<Date> dates=null;
		// if(listedDateFormat.equals("")&&listedTimeFormat.equals("")){
		// dates= new PrettyTimeParser().parse(dateTimeField);
		// }
		// String date = "";
		// String time = "";
		String[] dateTimeList = getDateTime(dateTimeField, listedDateFormat, listedTimeFormat);
		String date = dateTimeList[0];
		System.out.println(date);
		String time = dateTimeList[1];
		System.out.println(time);
		switch (givenParseKey) {
		case ON:
		case BY:
			// Date fullEndDate = dates.get(0);
			// endDate = dateFormat.format(fullEndDate);
			// endTime = timeFormat.format(fullEndDate);

			if (date != null) {
				currentCommand.addFieldToMap(TaskField.ENDDATE.getTaskKeyName(), date);
			}
			if (time != null) {
				currentCommand.addFieldToMap(TaskField.ENDTIME.getTaskKeyName(), time);
			}
			break;
		case FROM:
			// Date fromDate = dates.get(0);
			// startDate = dateFormat.format(fromDate);
			// startTime = timeFormat.format(fromDate);
			if (date != null) {
				currentCommand.addFieldToMap(TaskField.STARTTIME.getTaskKeyName(), date);
			}
			if (time != null) {
				currentCommand.addFieldToMap(TaskField.STARTTIME.getTaskKeyName(), time);
			}
			int toIndex = dateTimeField.lastIndexOf(ParseKey.TO.getParseKeyName());
			if (toIndex != -1) {
				// if (dates.get(1) != null) {
				String toField = input.substring(toIndex + 1);
				listedDateFormat = getProperDateFormat(dateTimeField);
				listedTimeFormat = getProperTimeFormat(dateTimeField, listedDateFormat);
				dateTimeList = getDateTime(dateTimeField, listedDateFormat, listedTimeFormat);
				date = dateTimeList[0];
				time = dateTimeList[1];
				// Date toDate = dates.get(1);
				// endDate = dateFormat.format(toDate);
				// endTime = timeFormat.format(toDate);
				if (date != "") {
					currentCommand.addFieldToMap(TaskField.ENDDATE.getTaskKeyName(), date);
				}
				if (time != "") {
					currentCommand.addFieldToMap(TaskField.ENDTIME.getTaskKeyName(), time);
				}
			}
			break;
		}
		return;

	}

	// Format: update [taskName/taskID] [taskField] to [updatedValue]
	// update Tea With Grandma date to 22/07/2016
	private void parseUpdate(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
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
	public void parseDelete(String input) {
		if(input==""){
			System.out.println("Delete no string");
			return;
		}
		// String[] inputArray = input.split(SPLITBY_WHITESPACE);
		int getNameOrID = isNameOrID(input);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID.getTaskKeyName(), input);
		} else if (getNameOrID == 0) {
			currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), input);
		} else {
			// Throw error
		}
	}

	// Format: show by name
	public void parseShow(String input) {
		if(input==""){
			System.out.println("Show error");
			return;
		}
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		// String showBy=inputArray[1];
		// TaskField showField=TaskField.get(showBy);
		currentCommand.addFieldToMap(TaskField.SHOW.getTaskKeyName(), inputArray[1]);
	}
	/*
	 * public Date getDate(DateFormat dateFormat){ switch(dateFormat){ case
	 * MONDAY:
	 * 
	 * break; case TUESDAY: break; case WEDNESDAY: break; case THURSDAY: break;
	 * case FRIDAY: break; case SATURDAY: break; case SUNDAY: break; case
	 * TOMORROW: break; case TODAY: break; case NORMAL: break; } }
	 */

	// Format: complete 20
	public void parseCompleted(String input) {
		if(input==""){
			System.out.println("Complete error");
			return;
		}
		// String[] inputArray = input.split(SPLITBY_WHITESPACE);
		// String completedBy = input;
		int getNameOrID = isNameOrID(input);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID.getTaskKeyName(), input);
		} else if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.NAME.getTaskKeyName(), input);
		} else {
			// Throw error
		}
	}

	public int isNameOrID(String givenInput) {
		try {
			Integer.parseInt(givenInput);
			return 1;
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public ParseKey getParseKey(String input) {
		for (ParseKey parseKeyName : ParseKey.values()) {
			if (input.contains(parseKeyName.getParseKeyName())) {
				return parseKeyName;
			}
		}
		return null;
	}

	public int getParseKeyIndex(String[] inputArray) {
		for (ParseKey parseKeyName : ParseKey.values()) {
			for (int i = 0; i < inputArray.length; i++) {
				if (inputArray[i].equals(parseKeyName.getParseKeyName())) {
					return i;
				}
			}
		}
		return -1;
	}

	public int getSpecificKeyIndex(String key, String[] inputArray, int startIndex) {
		for (int i = startIndex; i < inputArray.length; i++) {
			if (inputArray[i].equals(key)) {
				return i;
			}
		}
		return -1;
	}

	public String getProperDateFormat(String inputDate) {
		DateParser dp = new DateParser();
		String dateFormat = dp.getDateFormat(inputDate);
		return dateFormat;
	}

	public String getProperTimeFormat(String input, String dateFormat) {
		DateParser dp = new DateParser();
		String timeFormat = dp.getTimeFormat(input, dateFormat);
		return timeFormat;
	}

	public String[] getDateTime(String input, String listedDateFormat, String listedTimeFormat) throws ParseException {
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