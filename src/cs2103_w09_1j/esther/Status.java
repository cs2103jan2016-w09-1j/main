package cs2103_w09_1j.esther;


public class Status {

	/*enum msg {
		SUCCESS_ADD, ERROR_ADD, SUCCESS_DELETE, ERROR_DELETE,
		SUCCESS_UPDATE, ERROR_UPDATE, ERROR_UPDATE_NOT_FOUND,
		SUCCESS_SET_COMPLETED, ERROR_SET_COMPLETED, SUCCESS_UNDO,
		ERROR_UNDO, UNKNOWN_STATE, SUCCESS_HELP
	}*/

	public enum msg {
		SUCCESS, ERROR, UNKNOWN 
	}

	public static msg _msg = msg.SUCCESS;

	static final String MESSAGE_SUCCESS_ADD = "%1$s is successfully added to file.\n";
	static final String MESSAGE_ERROR_ADD = "[ERROR] Failed to add %1$s to file.\n";
	static final String MESSAGE_SUCCESS_DELETE = "%1$s is successfully deleted from file.\n";
	static final String MESSAGE_ERROR_DELETE = "[ERROR] Failed to delete %1$s from file.\n";
	static final String MESSAGE_SUCCESS_UPDATE = "%1$s is successfully updated.\n";
	static final String MESSAGE_ERROR_UPDATE_NOT_FOUND = "[ERROR] Task with supplied name or ID not found.\n";
	static final String MESSAGE_ERROR_UPDATE = "[ERROR] Failed to update %1$s.\n";
	static final String MESSAGE_SUCCESS_SET_COMPLETED = "%1$s is marked as completed.\n";
	static final String MESSAGE_ERROR_SET_COMPLETED = "[ERROR] Failed to mark %1$s as completed.\n";
	static final String MESSAGE_SUCCESS_UNDO = "Undo is successful.\n";
	static final String MESSAGE_ERROR_UNDO = "[ERROR] Cannot undo any further.\n";
	static final String MESSAGE_UNKNOWN_STATE = "[ERROR] Command not recognized.\n";
	static final String MESSAGE_HELP = "List of commands are:\n1. add\n2. delete\n3. update\n"
			+ "4.completed\n5.undo\n\n" + "Note that for these commands, "
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

		switch(_msg) {
		case SUCCESS :
			message = successCall(taskName, taskID, commandType);
			break;
		case ERROR :
			message = errorCall(taskName, taskID, commandType);
			break;
		default :
			message = MESSAGE_UNKNOWN_STATE;
			break;
		}
		return message;
	}

	private static String successCall(String taskName, String taskID, String commandType) {
		String message = null;

		switch(commandType) {
		case "add" :
			message = String.format(MESSAGE_SUCCESS_ADD, taskName);
			break;

		case "delete" :
			if (taskName != null) {
				message = String.format(MESSAGE_SUCCESS_DELETE, taskName);
			} else {
				message = String.format(MESSAGE_SUCCESS_DELETE, taskID);
			}
			break;

		case "update" :
			if (taskName != null) {
				message = String.format(MESSAGE_SUCCESS_UPDATE, taskName);
			} else {
				message = String.format(MESSAGE_SUCCESS_UPDATE, taskID);
			}
			break;

		case "completed" :
			if (taskName != null) {
				message = String.format(MESSAGE_SUCCESS_SET_COMPLETED, taskName);
			} else {
				message = String.format(MESSAGE_SUCCESS_SET_COMPLETED, taskID);
			}
			break;

		case "undo" :
			message = MESSAGE_ERROR_UNDO;
			break;

		case "help" :
			break;

		}

		return message;
	}
	
	private static String errorCall(String taskName, String taskID, String commandType) {
		String message = null;
		
		switch(commandType) {
		case "add" :
			message = String.format(MESSAGE_ERROR_ADD, taskName);
			break;

		case "delete" :
			if (taskName != null) {
				message = String.format(MESSAGE_ERROR_DELETE, taskName);
			} else {
				message = String.format(MESSAGE_ERROR_DELETE, taskID);
			}
			break;
			
		case "update" : // includes both update error and not found error
			if (taskName != null) {
				message = String.format(MESSAGE_ERROR_UPDATE, taskName);
			} else if (taskID != null){
				message = String.format(MESSAGE_ERROR_UPDATE, taskID);
			} else {
				message = MESSAGE_ERROR_UPDATE_NOT_FOUND;
			}
			break;
			
		case "completed" :
			if (taskName != null) {
				message = String.format(MESSAGE_ERROR_SET_COMPLETED, taskName);
			} else {
				message = String.format(MESSAGE_ERROR_SET_COMPLETED, taskID);
			}
			break;

		case "undo" :
			message = MESSAGE_ERROR_UNDO;
			break;

		case "help" :
			break;
		}
		
		return message;
	}
}
