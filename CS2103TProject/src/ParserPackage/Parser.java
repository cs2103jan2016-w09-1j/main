package ParserPackage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ParserPackage.Command.CommandKey;
import ParserPackage.Task.TaskField;

public class Parser {

	public static final String SPLITBY_WHITESPACE = " ";
	public Command currentCommand;
	public static final String[] parseKeys = { ".on", ".by", ".from", ".to" };

	public enum ParseKey {
		ON(".on"), BY(".by"), FROM(".from"), TO(".to");

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

	public Parser() {
		this.currentCommand = new Command();
	}

	public Command acceptUserInput(String input) {
		int endOfCommandName = input.indexOf(" ");
		String commandName = input.substring(0, endOfCommandName);
		currentCommand.clear();
		currentCommand.setCommandName(commandName);
		String commandInput = input.substring(endOfCommandName + 1, input.length());
		parseCommand(commandName, commandInput);
		return currentCommand;

	}

	private void parseCommand(String commandName, String commandInput) {
		CommandKey key = CommandKey.get(commandName);
		switch (key) {
		case ADD:
			parseCreate(commandInput);
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
			// THROW ERROR;
			break;
		}

	}

	private void parseSort(String commandInput) {
		// TODO Auto-generated method stub
		
	}

	private void parseUndo() {
		// TODO Auto-generated method stub
		
	}

	private Command parseHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	// Format: add [taskName] [on] [date]
	// add "Tea With Grandma" on tomorrow
	// Current implementation only date
	private void parseCreate(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		int parseKeyIndex = getParseKeyIndex(inputArray);

		// Case 1: add Tea With Grandma (No parse key)
		if (parseKeyIndex == -1) {
			currentCommand.addFieldToMap(TaskField.NAME, input);
			return;

		}
		// Case 2: add Tea With Grandma on Thursday
		ParseKey key = ParseKey.get(inputArray[parseKeyIndex]);
		String sName = "";
		String sDate = "";

		// Parse the name
		for (int i = 0; i < parseKeyIndex; i++) {
			sName += inputArray[i] + " ";
		}
		// THROW ERROR FOR INVALID INPUT
		// Parse the date
		for (int i = parseKeyIndex + 1; i < inputArray.length; i++) {
			sDate += inputArray[i] + " ";
		}
		// Need to format the date
		currentCommand.addFieldToMap(TaskField.NAME, sName);
		currentCommand.addFieldToMap(TaskField.DATE, sDate);

	}

	private void parseUpdate(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		String updateBy = inputArray[0];
		if (updateBy.equals("id")) {
			currentCommand.addFieldToMap(TaskField.ID, inputArray[1]);
		} else if (updateBy.equals("name")) {
			currentCommand.addFieldToMap(TaskField.NAME, inputArray[1]);
		} else {
			// Throw error
		}
		TaskField taskField = TaskField.get(inputArray[2]);
		String updateValue = "";
		for (int i = 3; i < inputArray.length; i++) {
			updateValue += inputArray[i];
		}
		currentCommand.addFieldToMap(taskField, updateValue);
	}

	public void parseDelete(String input) {
		String[]inputArray=input.split(SPLITBY_WHITESPACE);
		String deleteBy=inputArray[0];
		if (deleteBy.equals("id")) {
			currentCommand.addFieldToMap(TaskField.ID, inputArray[1]);
		} else if (deleteBy.equals("name")) {
			currentCommand.addFieldToMap(TaskField.NAME, inputArray[1]);
		} else {
			// Throw error
		}
	}

	public void parseShow(String input) {
		String[]inputArray=input.split(SPLITBY_WHITESPACE);
		String showBy=inputArray[1];
		TaskField showField=TaskField.get(showBy);
		currentCommand.addFieldToMap(showField, inputArray[1]);
	}
	/*
	 * public Date getDate(DateFormat dateFormat){ switch(dateFormat){ case
	 * MONDAY:
	 * 
	 * break; case TUESDAY: break; case WEDNESDAY: break; case THURSDAY: break;
	 * case FRIDAY: break; case SATURDAY: break; case SUNDAY: break; case
	 * TOMORROW: break; case TODAY: break; case NORMAL: break; } }
	 */

	public void parseCompleted(String input) {
		String[]inputArray=input.split(SPLITBY_WHITESPACE);
		String completedNo=inputArray[0];
		if (completedNo.equals("id")) {
			currentCommand.addFieldToMap(TaskField.ID, inputArray[1]);
		} else if (completedNo.equals("name")) {
			currentCommand.addFieldToMap(TaskField.NAME, inputArray[1]);
		} else {
			// Throw error
		}
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

	public int getSpecificKeyIndex(String key, String[] inputArray) {
		for (int i = 0; i < inputArray.length; i++) {
			if (inputArray[i].equals(key)) {
				return i;
			}
		}
		return -1;
	}
}

class Task {

	public enum TaskField {
		NAME("taskName"), ID("taskID"), PRIORITY("priority"), DATE("date"), UPDATENAME("updateName");

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
}
