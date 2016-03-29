package cs2103_w09_1j.esther;

import cs2103_w09_1j.esther.Command.CommandKey;

public class Status {

	public enum ErrorCode {
		SYSTEM, INVALID_COMMAND, ADD_INVALID_FORMAT, ADD_MISSING_NAME, DELETE_NOT_FOUND, DELETE_DUPLICATES_PRESENT,
		UPDATE_NOT_FOUND, UPDATE_DUPLICATES_PRESENT, UPDATE_INVALID_FIELD, COMPLETED_NOT_FOUND,
		COMPLETED_DUPLICATES_PRESENT, SORT_INVALID_CRITERION, UNDO, UNKNOWN_STATE
	}

	public enum Outcome {
		SUCCESS, ERROR
	}

	public static Outcome _outcome;
	public static ErrorCode _errorCode;

	static final String MESSAGE_ERROR_SYSTEM = "A system error has occured in ESTHER. Please restart this application.\n";
	static final String MESSAGE_ERROR_INVALID_COMMAND = "Command is not recognized. Type 'help' to see the list of commands that can be used in ESTHER.\n";
	static final String MESSAGE_SUCCESS_ADD = "%1$s is successfully added to file.\n";
	static final String MESSAGE_ERROR_ADD_INVALID_FORMAT = "Unable to add task: Please check that your input is of the correct format.\n"; 
															//"[ERROR] Failed to add %1$s to file.\n";
	static final String MESSAGE_ERROR_ADD_MISSING_NAME = "Unable to add task: Task name is required.\n";
	static final String MESSAGE_SUCCESS_DELETE = "%1$s is successfully deleted from file.\n";
	static final String MESSAGE_ERROR_DELETE_NOT_FOUND = "Unable to delete task: Please supply a proper task name or task ID.\n";
														 //"[ERROR] Failed to delete %1$s from file.\n";
	static final String MESSAGE_ERROR_DELETE_DUPLICATES_PRESENT = "There are multiple tasks sharing the same name '%1$s'. Please delete by ID instead.\n";
	static final String MESSAGE_SUCCESS_UPDATE = "%1$s is successfully updated.\n";
	static final String MESSAGE_ERROR_UPDATE_NOT_FOUND = "Unable to update task: Please supply a proper task name or task ID.\n";
														 //"[ERROR] Task with supplied name or ID not found.\n";
	static final String MESSAGE_ERROR_UPDATE_DUPLICATES_PRESENT = "There are multiple tasks sharing the same name '%1$s'. Please update by ID instead.\n";
	static final String MESSAGE_ERROR_UPDATE_INVALID_FIELD = "Unable to update task: The field you have specified does not exist.\n";
	static final String MESSAGE_SUCCESS_COMPLETED = "%1$s is successfully marked as completed.\n";
	static final String MESSAGE_ERROR_COMPLETED_NOT_FOUND = "Unable to complete task: Please supply a proper task name or task ID.\n";
														    //"[ERROR] Failed to mark %1$s as completed.\n";
	static final String MESSAGE_ERROR_COMPLETED_DUPLICATES_PRESENT = "There are multiple tasks sharing the same name '%1$s'. Please complete by ID instead.\n";
	static final String MESSAGE_SUCCESS_SORT = "File is successfully sorted.\n";
	static final String MESSAGE_ERROR_SORT_INVALID_CRITERION = "Unable to sort file: Please specify a recognized criterion to sort the file by.\n";
	
	static final String MESSAGE_SUCCESS_UNDO = "Undo is successful.\n";
	static final String MESSAGE_ERROR_UNDO = "Cannot undo any further.\n";
	static final String MESSAGE_ERROR_UNKNOWN_STATE = "ESTHER has encountered an unknown error. Please restart this application.\n";
	static final String MESSAGE_HELP = "List of commands are:\n1. add\n2. delete\n3. update\n"
			+ "4. completed\n5. undo\n\n" + "Note that for these commands, "
			+ "_value_ indicates that these fields are compulsory and need "
			+ "to be substituted with the relevant values.\n"
			+ "[optional] indicates optional fields to input.\n\n"
			+ "Using the 'add' command:\n"
			+ "General usage: add _task name_ [on _date/time_]\n"
			+ "-> 'add something on this date or time'\n"
			+ "add _task name_ (adds a task with the specified task name)\n"
			+ "add _task name_ on _date/time_ (adds task with deadline)\n\n"
			+ "Using the 'delete' command:\n"
			+ "General usage: delete _task name/task ID_\n"
			+ "-> 'delete something'\n"
			+ "delete _task name_ (deletes a task with exact matching name)\n"
			+ "delete _task ID_ (deletes a task with exact matching ID)\n\n"
			+ "Using the 'update' command:\n"
			+ "General usage: update _task name/task ID_ _field name_ to _value_\n"
			+ "-> 'update something to something else'\n"
			+ "update _task name_ time to _time_ (updates time for the task)\n"
			+ "update _task name_ name to _name_ (changes the name of task)\n\n"
			+ "Using the 'completed' command:\n"
			+ "General usage: completed _task name_\n"
			+ "-> 'completed a task'\n\n"
			+ "Using the 'undo' command:\n"
			+ "General usage: undo\n" + "Undo one step back to previous state.\n";

	public static String getMessage(String taskName, String taskID, String commandType) {
		String message;

		switch(_outcome) {
		case SUCCESS :
			message = successCall(taskName, taskID, commandType);
			break;
		case ERROR :
			message = errorCall(taskName, taskID, commandType);
			break;
		default :
			message = MESSAGE_ERROR_UNKNOWN_STATE;
			break;
		}
		return message;
	}

	private static String successCall(String taskName, String taskID, String commandType) {
		String message = null;
		CommandKey command = CommandKey.get(commandType);

		switch(command) {
		case ADD :
			message = String.format(MESSAGE_SUCCESS_ADD, taskName);
			break;

		case DELETE :
			if (taskName != null) {
				message = String.format(MESSAGE_SUCCESS_DELETE, taskName);
			} else {
				message = String.format(MESSAGE_SUCCESS_DELETE, taskID);
			}
			break;

		case UPDATE :
			if (taskName != null) {
				message = String.format(MESSAGE_SUCCESS_UPDATE, taskName);
			} else {
				message = String.format(MESSAGE_SUCCESS_UPDATE, taskID);
			}
			break;

		case COMPLETE :
			if (taskName != null) {
				message = String.format(MESSAGE_SUCCESS_COMPLETED, taskName);
			} else {
				message = String.format(MESSAGE_SUCCESS_COMPLETED, taskID);
			}
			break;
			
		case SORT :
			message = MESSAGE_SUCCESS_SORT;
			break;

		case UNDO :
			message = MESSAGE_SUCCESS_UNDO;
			break;

		case HELP :
			message = MESSAGE_HELP;
			break;
			
		default :
			message = MESSAGE_ERROR_UNKNOWN_STATE;
			break;

		}

		return message;
	}
	
	private static String errorCall(String taskName, String taskID, String commandType) {
		String message;
		if (_errorCode == ErrorCode.SYSTEM) {
			message = MESSAGE_ERROR_SYSTEM;
			return message;
		} else if (_errorCode == ErrorCode.INVALID_COMMAND) {
			message = MESSAGE_ERROR_INVALID_COMMAND;
			return message;
		} else {
			message = null;
		}
		
		CommandKey command = CommandKey.get(commandType);
		
		switch(command) {
		case ADD :
			message = getAddErrorMessage(taskName, taskID);
			break;

		case DELETE :
			message = getDeleteErrorMessage(taskName, taskID);
			break;
			
		case UPDATE : // includes both update error and not found error
			message = getUpdateErrorMessage(taskName, taskID);
			break;
			
		case COMPLETE :
			message = getCompleteErrorMessage(taskName, taskID);
			break;
			
		case SORT :
			message = MESSAGE_ERROR_SORT_INVALID_CRITERION;
			break;

		case UNDO :
			message = MESSAGE_ERROR_UNDO;
			break;

		case HELP :
			message = MESSAGE_HELP;
			break;
			
		default :
			message = MESSAGE_ERROR_UNKNOWN_STATE;
			break;
		}
		
		return message;
	}
	
	private static String getAddErrorMessage(String taskName, String taskID) {
		String message = null;
		
		switch (_errorCode) {
			case ADD_INVALID_FORMAT :
				message = MESSAGE_ERROR_ADD_INVALID_FORMAT;
				break;
				
			case ADD_MISSING_NAME :
				message = MESSAGE_ERROR_ADD_MISSING_NAME;
				break;
			
			default :
				message = MESSAGE_ERROR_UNKNOWN_STATE;
				break;
		}
		
		return message;
	}
	
	private static String getDeleteErrorMessage(String taskName, String taskID) {
		String message = null;
		
		switch (_errorCode) {
			case DELETE_NOT_FOUND :
				message = MESSAGE_ERROR_DELETE_NOT_FOUND;
				break;
				
			case DELETE_DUPLICATES_PRESENT :
				message = String.format(MESSAGE_ERROR_DELETE_DUPLICATES_PRESENT, taskName);
				break;
			
			default :
				message = MESSAGE_ERROR_UNKNOWN_STATE;
				break;
		}
		
		return message;
	}
	
	private static String getUpdateErrorMessage(String taskName, String taskID) {
		String message = null;
		
		switch (_errorCode) {
			case UPDATE_NOT_FOUND :
				message = MESSAGE_ERROR_UPDATE_NOT_FOUND;
				break;
				
			case UPDATE_DUPLICATES_PRESENT :
				message = String.format(MESSAGE_ERROR_UPDATE_DUPLICATES_PRESENT, taskName);
				break;
			
			case UPDATE_INVALID_FIELD :
				message = MESSAGE_ERROR_UPDATE_INVALID_FIELD;
				break;
			
			default :
				message = MESSAGE_ERROR_UNKNOWN_STATE;
				break;
		}
		
		return message;
	}
	
	private static String getCompleteErrorMessage(String taskName, String taskID) {
		String message = null;
		
		switch (_errorCode) {
			case COMPLETED_NOT_FOUND :
				message = MESSAGE_ERROR_COMPLETED_NOT_FOUND;
				break;
				
			case COMPLETED_DUPLICATES_PRESENT :
				message = String.format(MESSAGE_ERROR_COMPLETED_DUPLICATES_PRESENT, taskName);
				break;
			
			default :
				message = MESSAGE_ERROR_UNKNOWN_STATE;
				break;
		}
		
		return message;
	}
}
