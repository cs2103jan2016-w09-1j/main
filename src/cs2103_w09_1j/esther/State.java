package cs2103_w09_1j.esther;

/**
 * ========= [ STATE OBJECT DEFINITIONS ] =========
 * This class contains the representation of the
 * state object that will be used by the Logic
 * component of the program. This class is used to
 * support the undo functionality.
 * 
 * @author Tay Guo Qiang
 */

import java.util.ArrayList;

public class State {
	
	private String _command;
	private ArrayList<Task> _tasks = new ArrayList<Task>();
	
	public State(String command) {
		_command = command;
	}
	
	public void storeOriginalTaskState(Task task) {
		_tasks.add(task);
	}
	
	public void storeInnerMemoryState(ArrayList<Task> tasks) {
		_tasks = tasks;
	}
	
}
