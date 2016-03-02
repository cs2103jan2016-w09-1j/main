/**
 * ============= [LOGIC TEST FOR ESTHER] =============
 * List of things to be tested:
 * 
 * 1. test initialization success
 *    (assert the internal memory state)
 *   1a - initialize on empty file
 *   	  (internal memory should be BLANK)
 *   1b - initialize on non-empty file
 *   	  (internal memory should HAVE CONTENTS)
 * 
 * 2. test add-task function
 *    (assert internal memory state)
 *   2a - valid task add (should PASS)
 *   2b - invalid task add (missing details)
 *   	  (should prompt user)
 *   
 * 3. test delete-task function
 *    (assert internal memory state)
 *   3a - delete a task that exists (should PASS)
 *   3b - delete a non-existent task
 *        (nothing should happen)
 *   
 * 4. test update-task function
 *    (assert internal memory state)
 *   4a - update a task found in file (should PASS)
 *   4b - updating a task that does not exist
 *        (nothing should happen)
 *        
 * 5. test set-task-completed function
 *    (assert internal memory state and task state)
 *    5a - set existent task as completed (should PASS)
 *    5b - set non-existent task as completed
 *         (nothing should happen)
 *   
 * 6. test undo function
 *   5a - undo add
 *   5b - undo delete
 *   5c - undo update
 * 
 * 
 * =========== [LOGIC TEST CURRENT STATUS] ===========
 * Need to write test cases concerning operations
 * given user-supplied task ID.
 * 
 * @author Tay Guo Qiang
 */

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;

import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Task;

public class LogicTest {
	Logic logic = new Logic();
	Command addCommand; // task1
	Command addCommand2; // task2
	Command errorAddCommand; // task2 with invalid date
	Command deleteCommand; // remove: task1
	Command updateCommand; // update: task1 -> task3
	Command setCompletedCommand; // task1 -> done
	Command undoCommand;
	
	@Before
	public void init() {
		HashMap<String, String> args = new HashMap<String, String>();
		undoCommand = new Command("undo", args);
		args.put("taskName", "task1");
		args.put("date", "01/03/2016");
		args.put("priority", "1");
		args.put("completed", "false");
		addCommand = new Command("add", args);
		deleteCommand = new Command("delete", args);
		args.put("completed", "true");
		setCompletedCommand = new Command("completed", args);
		args.put("completed", "false");
		args.put("taskName", "task2");
		addCommand2 = new Command("add", args);
		args.put("taskName", "task1");
		args.put("taskNameUpdated", "task3");
		updateCommand = new Command("update", args);
		args.remove("taskNameUpdated");
		args.put("date", "some date");
		errorAddCommand = new Command("add", args);
	}
	
	/*
	 * =============== [ INITIALIZATION STATE TESTS ] ===============
	 * These group of methods are for checking initialization states.
	 * Two situations are tested here:
	 * 1. initialization with empty file
	 * 2. initialization with non-empty file. 
	 */
	
	@Test
	public void testInitializationWithEmptyFile() {
		logic.flushInternalStorage();
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("File should be empty.", 0, internalStorage.size());
	}
	
	@Test
	public void testInitializationWithNonEmptyFile() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.updateInternalStorage();
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("There should only be 1 item in the file.", 1, internalStorage.size());
	}
	
	
	/*
	 * =============== [ ADD TASK FUNCTIONALITY TESTS ] ===============
	 * These group of methods are for checking add-task functionality.
	 * Generally, these tests check for 3 things:
	 * 1. Adding a task should increase the size of the task list.
	 * 2. The task added should be added to the end of the list.
	 * 3. The correct data is added to the list.
	 */
	
	@Test
	public void testValidAddTaskToEmptyFileListSizeIncrease() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should have been added into file.", 1, internalStorage.size());
	}
	
	@Test
	public void testValidAddTaskToEmptyFileCorrectContentsAdded() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertEquals("Task added into file should have name 'task1'.", "task1", taskInList.getName());
	}
	
	@Test
	public void testValidAddTaskToNonEmptyFileListSizeIncrease() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task2 should have been added into file.", 2, internalStorage.size());
	}
	
	@Test
	public void testValidAddTaskToNonEmptyFileCorrectAddIndex() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		int index = -1;
		for (int i = 0; i < internalStorage.size(); i++) {
			if (internalStorage.get(i).getName().equals("task2")) {
				index = i;
				break;
			}
		}
		assertEquals("task2 should have been located at index 1.", 1, index);
	}
	
	@Test
	public void testValidAddTaskToNonEmptyFileCorrectContentsAdded() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task addedTask = internalStorage.get(1);
		assertEquals("Task added into file should have name 'task2'.", "task2", addedTask.getName());
	}
	
	// this test should prompt user (e.g. missing details)
	@Test
	public void testInvalidAddTask() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(errorAddCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task3 should not have been added to file.", 2, internalStorage.size());
	}
	
	/*
	 * =============== [ DELETE TASK FUNCTIONALITY TESTS ] ===============
	 * These group of methods are for checking delete-task functionality.
	 * Generally, these tests check for 2 things:
	 * 1. Deleting a task should decrease the size of the task list.
	 * 2. The correct data is deleted from the list.
	 */
	
	@Test
	public void testValidDeleteExistentTaskListSizeDecrease() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(deleteCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should have been deleted from file.", 0, internalStorage.size());
	}
	
	@Test
	public void testValidDeleteExistentTaskCorrectTaskDeleted() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(deleteCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task remainingTask = internalStorage.get(0);
		assertEquals("task2 should be remaining in the file.", "task2", remainingTask.getName());
	}

	@Test
	public void testValidDeleteNonExistentTask() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand2);
		logic.executeCommand(deleteCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No deletion should have occured for task2.", 1, internalStorage.size());
	}
	
	@Test
	public void testValidDeleteNonExistentTaskCorrectTaskLeft() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand2);
		logic.executeCommand(deleteCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task remainingTask = internalStorage.get(0);
		assertEquals("task2 should be remaining in the file.", "task2", remainingTask.getName());
	}
	
	/*
	 * =============== [ UPDATE TASK FUNCTIONALITY TESTS ] ===============
	 * These group of methods are for checking update-task functionality.
	 * Generally, these tests check for 2 things:
	 * 1. Updating a task should NOT change the size of the task list.
	 * 2. The correct task is updated.
	 */
	
	@Test
	public void testValidUpdateExistentTask() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(updateCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in update.", 1, internalStorage.size());
	}
	
	@Test
	public void testValidUpdateExistentTaskCorrectUpdateState() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(updateCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task updatedTask = internalStorage.get(0);
		assertEquals("task1 should have been renamed to task3.", "task3", updatedTask.getName());
	}
	
	@Test
	public void testValidUpdateNonExistentTask() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand2);
		logic.executeCommand(updateCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in update.", 1, internalStorage.size());
	}
	
	@Test
	public void testValidUpdateNonExistentTaskCorrectUpdateState() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand2);
		logic.executeCommand(updateCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task updatedTask = internalStorage.get(0);
		assertEquals("task2 should not have been renamed.", "task2", updatedTask.getName());
	}
	
	/*
	 * ========== [ SET TASK COMPLETED FUNCTIONALITY TESTS ] ==========
	 * These group of methods are for checking set-task-completed functionality.
	 * Generally, these tests check for 3 things:
	 * 1. This operation should not change the list size.
	 * 2. The correct task should be updated to 'done' status
	 * 2. If task does not exist, no changes should occur.
	 */
	
	@Test
	public void testValidSetCompletedExistentTaskCorrectListSize() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(setCompletedCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in this operation.", 1, internalStorage.size());
	}
	
	@Test
	public void testValidSetCompletedExistentTaskCorrectTaskUpdated() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(setCompletedCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task targetTask = internalStorage.get(0);
		assertTrue("task1 should have been marked as completed.", targetTask.isCompleted());
	}
	
	@Test
	public void testValidSetCompletedNonExistentTaskCorrectListSize() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand2);
		logic.executeCommand(setCompletedCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in this operation.", 1, internalStorage.size());
	}
	
	@Test
	public void testValidSetCompletedNonExistentTaskCorrectUpdatedState() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand2);
		logic.executeCommand(setCompletedCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task targetTask = internalStorage.get(0);
		assertFalse("task1 should reflect 'not completed' status.", targetTask.isCompleted());
	}
	
	/*
	 * ================= [ UNDO FUNCTIONALITY TESTS ] =================
	 * These group of methods are for checking undo functionality.
	 * Generally, these tests check for 3 things:
	 * 1. Undoing adding of task should only remove the added task.
	 * 2. Undoing deletion of task should only add back the task
	 *    (but added task will be added at end of list instead of its
	 *    original position in the list).
	 * 3. Undoing updating of task should only revert the state of
	 *    the task back to before it was updated.
	 */
	
	@Test
	public void testUndoAddTaskToEmptyFileCorrectListSize() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("Task list should be empty.", 0, internalStorage.size());
	}
	
	@Test
	public void testUndoAddTaskToNonEmptyFileCorrectListSize() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("Task list should only have 1 item.", 1, internalStorage.size());
	}
	
	@Test
	public void testUndoAddTaskToNonEmptyFileCorrectTaskRemoved() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertEquals("task2 should not be inside task list.", "task1", taskInList.getName());
	}
	
	@Test
	public void testUndoDeleteTaskFromNonEmptyFileCorrectListSize() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(deleteCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("There should only be 1 item in task list.", 1, internalStorage.size());
	}
	
	@Test
	public void testUndoDeleteTaskFromNonEmptyFileCorrectTaskRestored() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(deleteCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertEquals("task1 should be back inside the task list.", "task1", taskInList.getName());
	}
	
	@Test
	public void testUndoUpdateExistentTaskCorrectListSize() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(updateCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("Size of task list should not change.", 1, internalStorage.size());
	}
	
	@Test
	public void testUndoUpdateExistentTaskCorrectTaskStateRestored() {
		logic.flushInternalStorage();
		logic.executeCommand(addCommand);
		logic.executeCommand(updateCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertEquals("Original name of task should be 'task1'.", "task1", taskInList.getName());
	}
	
	/*
	 * ============== [ DISPLAY-ALL FUNCTIONALITY TESTS ] ==============
	 * These group of methods are for checking display-all functionality.
	 * Generally, these tests check for 2 things:
	 * 1. The list of tasks displayed have correct number of entries.
	 * 2. The order of tasks in the list remain intact.
	 */
	
	public void testDisplayAllInEmptyFile() {
		// display on empty file
	}
	
	public void testDisplayAllInNonEmptyFile() {
		// display on non-empty file
		// check list size
	}
	
	public void testDisplayAllInNonEmptyFileCorrectOrdering() {
		// iterate through the task list to check ordering
	}
	
	/*
	 * ================= [ SORT FUNCTIONALITY TESTS ] =================
	 * These group of methods are for checking sort functionality.
	 * Generally, these tests check for 2 things:
	 * 1. The size of task list does not change.
	 * 2. The order of tasks in the list meet sorting criteria.
	 */
	
	public void testSortOnEmptyFile() {
		// sort empty file
	}
	
	public void testSortOnNonEmptyFileCorrectListSize() {
		// sort non-empty file
		// check list size
	}
	
	public void testSortOnNonEmptyFileCorrectOrdering() {
		// sort non-empty file
		// check ordering in task list
	}
	
	/*
	 * ================= [ SEARCH FUNCTIONALITY TESTS ] =================
	 * These group of methods are for checking search functionality.
	 * Generally, these tests check for 1 thing:
	 * 1. Search should return all those tasks meeting criteria.
	 */
	
	public void testSearchOnEmptyFile() {
		// search in empty file
		// should have nothing inside
	}
	
	public void testValidSearchOnNonEmptyFileHasResult() {
		// search in non-empty file
		// search in this test case has matches
		// check results
	}
	
	public void testValidSearchOnNonEmptyFileNoResult() {
		// search in non-empty file
		// search in this test case has no matches
		// check results
	}
	
}
