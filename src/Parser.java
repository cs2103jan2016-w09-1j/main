import java.util.HashMap;
import java.util.Map;
//import cs2103_w09_1j.esther.Command;
//import ParserPackage.Task.TaskField;

public class Parser {

	public static final String SPLITBY_WHITESPACE = " ";
	public Command currentCommand;
	// public static final String[] parseKeys = { ".on", ".by", ".from", ".to"
	// };

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

	public static void main(String[] args) {
		Parser parser = new Parser();
		Command command = parser.acceptUserInput("dontknow");
		HashMap<TaskField, String> map = command.getFieldMap();
		for (Map.Entry<TaskField, String> entry : map.entrySet()) {
			String key = entry.getKey().getTaskKeyName();
			String value = entry.getValue();
			System.out.println("Key " + key + "  Value" + value);
		}
	}

	public Parser() {
		this.currentCommand = new Command();
	}

	public Command acceptUserInput(String input) {
		String commandName = "";
		String commandInput = "";
		try {
			int endOfCommandName = input.indexOf(" ");
			commandName = input.substring(0, endOfCommandName);
			commandInput = input.substring(endOfCommandName + 1, input.length());
		} catch (StringIndexOutOfBoundsException sioobe) {
			commandName = input;
		}
		currentCommand.clear();
		currentCommand.setCommandName(commandName);
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

	//Format: show .by [field]
	private void parseSort(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		currentCommand.addFieldToMap(TaskField.SORT, inputArray[1]);

	}

	//Format: undo
	private void parseUndo() {
		// TODO Auto-generated method stub
		currentCommand.addFieldToMap(TaskField.UNDO, "");

	}

	//Format: help
	private void parseHelp() {
		currentCommand.addFieldToMap(TaskField.HELP, "");
	}

	// Format: add [taskName] .[on] [date]
	// add "Tea With Grandma" .on tomorrow
	// Current implementation only date
	private void parseCreate(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		int parseKeyIndex = getParseKeyIndex(inputArray);

		// Case 1: add Tea With Grandma (No parse key)
		if (parseKeyIndex == -1) {
			currentCommand.addFieldToMap(TaskField.NAME, input);
			return;

		}
		// Case 2: add Tea With Grandma .on Thursday
		// ParseKey key = ParseKey.get(inputArray[parseKeyIndex]);
		String sName = "";
		String sDate = "";

		//Case 3: add Tea With Grandma .from Thursday .to Friday
		//Haven't implemented
		
		
		// Parse the name
		for (int i = 0; i < parseKeyIndex; i++) {
			if (inputArray[i].equals("on")) {
				break;
			}
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

	//Format: update [id/name] [number] [field] to [updatedvalue]
	//update name Tea With Grandma date to 22/7/2016
	private void parseUpdate(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		String updateBy = inputArray[0];
		int getNameOrID = isNameOrID(updateBy);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID, inputArray[1]);
		} else if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.UPDATENAME, inputArray[1]);
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

	//Format: delete id 10
	public void parseDelete(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		String deleteBy = inputArray[0];
		int getNameOrID = isNameOrID(deleteBy);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID, inputArray[1]);
		} else if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.NAME, inputArray[1]);
		} else {
			// Throw error
		}
	}

	//Format: show .by name
	public void parseShow(String input) {
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		// String showBy=inputArray[1];
		// TaskField showField=TaskField.get(showBy);
		currentCommand.addFieldToMap(TaskField.SHOW, inputArray[1]);
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
		String[] inputArray = input.split(SPLITBY_WHITESPACE);
		String completedBy = inputArray[0];
		int getNameOrID = isNameOrID(completedBy);
		if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.ID, inputArray[1]);
		} else if (getNameOrID == 1) {
			currentCommand.addFieldToMap(TaskField.NAME, inputArray[1]);
		} else {
			// Throw error
		}
	}

	public int isNameOrID(String givenInput) {
		switch (givenInput) {
		case "name":
			return 0;
		case "id":
			return 1;
		default:
			// throw error;
		}
		return -1;
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
