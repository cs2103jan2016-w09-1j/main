package cs2103_w09_1j.esther;

import cs2103_w09_1j.esther.Command.CommandKey;

/**
 * The <code>Status</code> class contains all necessary implementations for conveying success or
 * error messages to the user of <code>ESTHER</code>.
 * 
 * There are two <code>Outcome</code>s of a user operation: success or error.<br><br>
 * If the user operation was performed successfully, the relevant success messages will be returned to UI
 * for display to the user. If an error was encountered, the specific error type will be identified in
 * <code>ErrorCode</code> and the relevant error messages will be returned to UI for display to the user.
 * 
 * @author Guo Mingxuan
 * @@author A0130749A
 */
public class Status {

	public enum ErrorCode {
		SYSTEM, INVALID_COMMAND, ADD_INVALID_FORMAT, ADD_MISSING_NAME, DELETE_NOT_FOUND, DELETE_DUPLICATES_PRESENT,
		UPDATE_NOT_FOUND, UPDATE_DUPLICATES_PRESENT, UPDATE_INVALID_FIELD, UPDATE_START_END_VIOLATE,
		UPDATE_INVALID_PRIORITY, COMPLETED_NOT_FOUND, COMPLETED_DUPLICATES_PRESENT, COMPLETED_ALREADY_COMPLETED,
		SORT_INVALID_CRITERION, SEARCH_INVALID, SET_SAVEPATH, UNDO, UNKNOWN_STATE
	}

	public enum Outcome {
		SUCCESS, ERROR
	}

	public static Outcome _outcome;			// controls the Outcome logical flow
	public static ErrorCode _errorCode;		// controls the ErrorCode logical flow

	// ========== [TASK-ADDING MESSAGES] ========== //	
	static final String MESSAGE_SUCCESS_ADD = "%1$s is successfully added to file.\n";
	static final String MESSAGE_ERROR_ADD_INVALID_FORMAT = "Unable to add task: Please check that your input is of the correct format.\n"; 
	static final String MESSAGE_ERROR_ADD_MISSING_NAME = "Unable to add task: Task name is required.\n";

	// ========== [TASK-DELETING MESSAGES] ========== //
	static final String MESSAGE_SUCCESS_DELETE = "%1$s is successfully deleted from file.\n";
	static final String MESSAGE_ERROR_DELETE_NOT_FOUND = "Unable to delete task: Please supply a proper task name or task ID.\n";
	static final String MESSAGE_ERROR_DELETE_DUPLICATES_PRESENT = "There are multiple tasks sharing the same name '%1$s'. Please delete by ID instead.\n";
	
	// ========== [TASK-UPDATING MESSAGES] ========== //
	static final String MESSAGE_SUCCESS_UPDATE = "%1$s is successfully updated.\n";
	static final String MESSAGE_ERROR_UPDATE_NOT_FOUND = "Unable to update task: Please supply a proper task name or task ID.\n";
	static final String MESSAGE_ERROR_UPDATE_DUPLICATES_PRESENT = "There are multiple tasks sharing the same name '%1$s'. Please update by ID instead.\n";
	static final String MESSAGE_ERROR_UPDATE_INVALID_FIELD = "Unable to update task: The field you have specified does not exist.\n";
	static final String MESSAGE_ERROR_UPDATE_START_END_VIOLATE = "Unable to update task: Start date/time is not before end date/time.\n";
	static final String MESSAGE_ERROR_UPDATE_INVALID_PRIORITY = "Unable to update task: Priority is not within 1 to 5.\n";

	// ========== [TASK-COMPLETING MESSAGES] ========== //
	static final String MESSAGE_SUCCESS_COMPLETED = "%1$s is successfully marked as completed.\n";
	static final String MESSAGE_ERROR_COMPLETED_NOT_FOUND = "Unable to complete task: Please supply a proper task name or task ID.\n";
	static final String MESSAGE_ERROR_COMPLETED_DUPLICATES_PRESENT = "There are multiple tasks sharing the same name '%1$s'. Please complete by ID instead.\n";
	static final String MESSAGE_ERROR_COMPLETED_ALREADY_COMPLETED = "%1$s is already completed.\n";
	
	// ========== [TASK-SORTING MESSAGES] ========== //
	static final String MESSAGE_SUCCESS_SORT = "File is successfully sorted.\n";
	static final String MESSAGE_ERROR_SORT_INVALID_CRITERION = "Unable to sort file: Please specify a recognized criterion to sort the file by.\n";
	
	// ========== [TASK-SEARCHING MESSAGES] ========== //
	static final String MESSAGE_SUCCESS_SEARCH = "Search is successful.\n";
	static final String MESSAGE_ERROR_SEARCH_INVALID = "Search keyword or date-time is either not specified, or not recognized.\n";
	
	// ========== [SETTING SAVE FILE FILEPATH MESSAGES] ========== //
	static final String MESSAGE_SUCCESS_SET_SAVEPATH = "Successfully set file path.\n";
	static final String MESSAGE_ERROR_SET_SAVEPATH = "Unable to set file path.\n";
	
	// ========== [UNDOING MESSAGES] ========== //
	static final String MESSAGE_SUCCESS_UNDO = "Undo is successful.\n";
	static final String MESSAGE_ERROR_UNDO = "Cannot undo any further.\n";
	
	// ========== [GENERIC ERROR MESSAGES] ========== //
	static final String MESSAGE_ERROR_SYSTEM = "A system error has occured in ESTHER. Please restart this application.\n";
	static final String MESSAGE_ERROR_INVALID_COMMAND = "Command is not recognized. Type 'help' to see the list of commands that can be used in ESTHER.\n";
	static final String MESSAGE_ERROR_UNKNOWN_STATE = "ESTHER has encountered an unknown error. Please restart this application.\n";
	
	// ========== [HELP MESSAGES] ========== //
	static final String MESSAGE_SUCCESS_HELP = "Showing help message in new window.\n";
	public static final String MESSAGE_HELP = "Help:\n"
											   + "List of commands are:\n1. add\n2. delete\n3. update\n"
											   + "4. complete\n5. search\n6. sort\n7. set\n8. undo\n\n"
											   + "Note that for these commands, "
											   + "_value_ indicates that these fields are compulsory and\n"
											   + "need to be substituted with the relevant values.\n"
											   + "[optional] indicates optional fields to input.\n\n"
											   + "Using the 'add' command:\n"
											   + "General usage:\n1. add _task name_ [on _date/time_]\n2. add _task name_ [from _date/time_ to _date/time_]\n"
											   + "-> 'add something on this date or time'\n"
											   + "add _task name_ (adds a task with the specified task name)\n"
											   + "add _task name_ on _date/time_ (adds task with deadline)\n"
											   + "add _task name_ from _date/time_ to _date/time_\n\n"
											   + "Using the 'delete' command:\n"
											   + "General usage: delete _task name/task ID_\n"
											   + "-> 'delete something'\n"
											   + "delete _task name_ (deletes a task with exact matching name)\n"
											   + "delete _task ID_ (deletes a task with exact matching ID)\n\n"
											   + "Using the 'update' command:\n"
											   + "General usage: update _task name/task ID_ _field name_ to _value_\n"
											   + "-> 'update something in task to something else'\n"
											   + "update _task name/task ID_ name to _name_ (changes the name of task)\n"
											   + "update _task name/task ID_ startDate to _date_ (updates starting date for the task)\n"
											   + "update _task name/task ID_ startTime to _time_ (updates starting time for the task)\n"
											   + "update _task name/task ID_ endDate to _date_ (updates ending date for the task)\n"
											   + "update _task name/task ID_ endTime to _time_ (updates ending time for the task)\n"
											   + "update _task name/task ID_ priority to _priority_ (changes the priority of task)\n\n"
											   + "Using the 'complete' command:\n"
											   + "General usage: complete _task name/task ID_\n"
											   + "-> 'complete something'\n\n"
											   + "Using the 'search' command:\n"
											   + "General usage:\n1. search for _keywords_\n2. search before/on/after _date_\n"
											   + "-> 'search for any tasks by name or by date'\n"
											   + "search for _keyword_ (searches tasks with names containing a keyword)\n"
											   + "search for _keyword1_ _keyword2_ (searches tasks with names containing "
											   + "keywords keyword1 or keyword2)\n"
											   + "search before _date_ (searches all tasks before specified date)\n"
											   + "search on _date_ (searches all tasks on the specified date)\n"
											   + "search after _date_ (searches all tasks after specified date)\n\n"
											   + "Using the 'sort' command:\n"
											   + "-> 'sort tasks by something'\n"
											   + "sort by date/name/priority (sorts your tasks by date/name/priority)\n\n"
											   + "Using the 'set' command:\n"
											   + "-> 'set the filepath of the text file that you want to store your tasks in'\n"
											   + "set _filePath_ (copies data from old file to new file and stores future data into that file)\n\n"
											   + "Using the 'undo' command:\n"
											   + "General usage: undo\n" + "Undo one step back to previous state.\n";

	/**
	 * Retrieves the message depending on the outcome of a user operation.
	 * 
	 * @param taskName		the name of a task
	 * @param taskID		the ID of a task
	 * @param commandType	the type of command executed in Logic
	 * @return				a success message if the user operation was carried out successfully;
	 * 						an error message otherwise
	 */
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

	/**
	 * Retrieves the success message when the user operation is successfully carried out.
	 * 
	 * @param taskName		the name of a task
	 * @param taskID		the ID of a task
	 * @param commandType	the type of command executed in Logic
	 * @return				the success message related to the type of user operation carried out
	 */
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

			case SET :
				message = MESSAGE_SUCCESS_SET_SAVEPATH;
				break;

			case SEARCH :
				message = MESSAGE_SUCCESS_SEARCH;
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

	/**
	 * Retrieves the error message when the user operation being carried out encounters an error.
	 * 
	 * @param taskName		the name of a task
	 * @param taskID		the ID of a task
	 * @param commandType	the type of command executed in Logic
	 * @return				the error message related to the type of user operation carried out
	 */
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

			case UPDATE :
				message = getUpdateErrorMessage(taskName, taskID);
				break;

			case COMPLETE :
				message = getCompleteErrorMessage(taskName, taskID);
				break;

			case SORT :
				message = MESSAGE_ERROR_SORT_INVALID_CRITERION;
				break;

			case SEARCH :
				message = MESSAGE_ERROR_SEARCH_INVALID;
				break;

			case SET :
				message = MESSAGE_ERROR_SET_SAVEPATH;
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
	
	/**
	 * Retrieves the error message related to task-adding user operations.
	 * 
	 * @param taskName		the name of a task
	 * @param taskID		the ID of a task
	 * @param commandType	the type of command executed in Logic
	 * @return				the error message related to task-adding user operations
	 */
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
	
	/**
	 * Retrieves the error message related to task-deleting user operations.
	 * 
	 * @param taskName		the name of a task
	 * @param taskID		the ID of a task
	 * @param commandType	the type of command executed in Logic
	 * @return				the error message related to task-deleting user operations
	 */
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
	
	/**
	 * Retrieves the error message related to task-updating user operations.
	 * 
	 * @param taskName		the name of a task
	 * @param taskID		the ID of a task
	 * @param commandType	the type of command executed in Logic
	 * @return				the error message related to task-updating user operations
	 */
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
				
			case UPDATE_START_END_VIOLATE :
				message = MESSAGE_ERROR_UPDATE_START_END_VIOLATE;
				break;
				
			case UPDATE_INVALID_PRIORITY :
				message = MESSAGE_ERROR_UPDATE_INVALID_PRIORITY;
				break;
			
			default :
				message = MESSAGE_ERROR_UNKNOWN_STATE;
				break;
		}
		
		return message;
	}
	
	/**
	 * Retrieves the error message related to task-completing user operations.
	 * 
	 * @param taskName		the name of a task
	 * @param taskID		the ID of a task
	 * @param commandType	the type of command executed in Logic
	 * @return				the error message related to task-completing user operations
	 */
	private static String getCompleteErrorMessage(String taskName, String taskID) {
		String message = null;
		
		switch (_errorCode) {
			case COMPLETED_NOT_FOUND :
				message = MESSAGE_ERROR_COMPLETED_NOT_FOUND;
				break;
				
			case COMPLETED_DUPLICATES_PRESENT :
				message = String.format(MESSAGE_ERROR_COMPLETED_DUPLICATES_PRESENT, taskName);
				break;
				
			case COMPLETED_ALREADY_COMPLETED :
				if (taskName != null) {
					message = String.format(MESSAGE_ERROR_COMPLETED_ALREADY_COMPLETED, taskName);
				} else {
					message = String.format(MESSAGE_ERROR_COMPLETED_ALREADY_COMPLETED, taskID);
				}
				break;
			
			default :
				message = MESSAGE_ERROR_UNKNOWN_STATE;
				break;
		}
		
		return message;
	}
}