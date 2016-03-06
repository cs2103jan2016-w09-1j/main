package ParserPackage;

import java.util.HashMap;
import java.util.Map;

import ParserPackage.Task.TaskField;

public class Command {

	private String commandName;
	private HashMap<TaskField, String> fieldMap;

	public enum CommandKey {
		ADD("add"), UPDATE("update"), DELETE("delete"), UNDO("undo"), COMPLETED("complete"), SHOW("show"), SORT(
				"sort"), HELP("help");

		private String commandKeyName;
		private static final Map<String, CommandKey> lookup = new HashMap<String, CommandKey>();

		private CommandKey(String _commandKeyName) {
			this.commandKeyName = _commandKeyName;
		}

		static {
			// Create reverse lookup hash map
			for (CommandKey _commandKeyName : CommandKey.values()) {
				lookup.put(_commandKeyName.getCommandKeyName(), _commandKeyName);
			}
		}

		public String getCommandKeyName() {
			return commandKeyName;
		}

		/**
		 * This operations reversely gets the CommandKey from the value.
		 * 
		 * @param commandValue
		 *            The input given by the user.
		 * @return The command based on the input.
		 */
		public static CommandKey get(String commandKeyValue) {
			return lookup.get(commandKeyValue);
		}

	}

	public Command() {
		this.commandName = "";
		this.fieldMap = new HashMap<TaskField, String>();
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String _commandName) {
		this.commandName = _commandName;
	}

	public HashMap<TaskField, String> getFieldMap() {
		return fieldMap;
	}

	public void setFieldMap(HashMap<TaskField, String> _fieldMap) {
		this.fieldMap = _fieldMap;
	}

	public void clear() {
		this.commandName = "";
		this.fieldMap.clear();
	}

	public String addFieldToMap(TaskField fieldName, String fieldValue) {
		return this.fieldMap.put(fieldName, fieldValue);
	}

}
