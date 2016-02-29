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

public class Command {
	
	private String _command;
	private HashMap<String, String> _parameters;
	
	/**
	 * Creates a Command object with the command to execute as well as
	 * the parameters needed to create a Task object.
	 * 
	 * @param  command		the operation desired by the user
	 * @param  parameters	the arguments supplied by the user
	 * @author Tay Guo Qiang
	 */
	public Command(String command, HashMap<String, String> parameters) {
		_command = command;
		_parameters = parameters;
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
	 * Setter method for user-supplied parameters.
	 * 
	 * @param  parameters	the representation of user-supplied parameters
	 * @author Tay Guo Qiang
	 */
	public void set_parameters(HashMap<String, String> parameters) {
		_parameters = parameters;
	}

}