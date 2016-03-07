package cs2103_w09_1j.esther;

/**
 * ========= [ COMMAND OBJECT DEFINITIONS ] =========
 * This class contains the representation of the
 * command object that will be passed around by the
 * program.
 * 
 * @author Tay Guo Qiang
 */

import java.util.HashMap;
import java.util.Map;

import Command.CommandKey;

public class Command {
	
	private String _command;
	private HashMap<String, String> _parameters;
	
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
		this._command = "";
		this._parameters = new HashMap<TaskField, String>();
	}
	
	/**
	 * Creates a Command object with the command to execute as well as
	 * the parameters needed to create a Task object.
	 * 
	 * @param  command		the operation desired by the user
	 * @param  parameters	the arguments supplied by the user
	 * @author Tay Guo Qiang
	 */
	public Command(String command, HashMap<String, String> parameters) {
		setCommand(command);
		setParameters(parameters);
	}
	
	/**
	 * Getter method for the command that user wishes to execute.
	 * 
	 * Logic will use this to determine the command to execute on the task.
	 * 
	 * @return the command to execute
	 * @author Tay Guo Qiang
	 */
	public String getCommand() {
		return _command;
	}

	/**
	 * Setter method for the command that user wishes to execute.
	 * 
	 * @param  command	the command to execute
	 * @author Tay Guo Qiang
	 */
	public void setCommand(String command) {
		_command = command;
	}

	/**
	 * Getter method for user-supplied parameters.
	 * 
	 * @return the representation of user-supplied parameters
	 * @author Tay Guo Qiang
	 */
	public HashMap<String, String> getParameters() {
		return _parameters;
	}
	
	/**
	 * Returns the String value associated with the parameter key.
	 * 
	 * @param  parameter	the parameter being requested
	 * @return the String value associated with the parameter
	 * @author Tay Guo Qiang
	 */
	public String getSpecificParameter(String parameter) {
		String value;
		switch (parameter) {
			case "taskName" :
				value = _parameters.get(parameter);
				break;
			
			case "updatedTaskName" :
				value = _parameters.get(parameter);
				break;
				
			case "date" :
				value = _parameters.get(parameter);
				break;
			
			case "priority" :
				value = _parameters.get(parameter);
				break;
				
			case "taskId" :
				value = _parameters.get(parameter);
				break;
			
			case "completed" :
				value = _parameters.get(parameter);
				break;
			
			case "order" :
				value = _parameters.get(parameter);
				break;
				
			default:
				value = "Unrecognized key.";
				break;
		}
		return value;
	}
	
	/**
	 * Checks if a parameter exists or not.
	 * 
	 * @param  parameter	the parameter being requested 
	 * @return true if parameter key does not map to null value;
	 * 		   false otherwise.
	 * @author Tay Guo Qiang
	 */
	public boolean hasParameter(String parameter) {
		return _parameters.containsKey(parameter);
	}

	/**
	 * Setter method for user-supplied parameters.
	 * 
	 * @param  parameters	the representation of user-supplied parameters
	 * @author Tay Guo Qiang
	 */
	public void setParameters(HashMap<String, String> parameters) {
		_parameters = parameters;
	}
	
	public void clear() {
		this._command = "";
		this._parameters.clear();
	}

	public String addFieldToMap(TaskField fieldName, String fieldValue) {
		return this._parameters.put(fieldName, fieldValue);
	}

}