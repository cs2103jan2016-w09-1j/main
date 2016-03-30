

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
 * 6. test show-all function
 * 	  (assert internal memory state)
 *     6a - show by priority (order should be correct)
 *     6b - show by date (order should be correct)
 *     6c - show by name (order should be correct)
 * 
 * 7. test sort function
 * 	  (assert internal memory state)
 *     7a - sort by priority (order should be correct)
 *     7b - sort by date (order should be correct)
 *     7c - sort by name (order should be correct)
 *     
 * 8. test undo function
 *   8a - undo add
 *   8b - undo delete
 *   8c - undo update
 *   8d - undo show
 *   8e - undo sort
 * 
 * 
 * =========== [LOGIC TEST CURRENT STATUS] ===========
 * 1. Need to write test cases for show and sort
 * operations.
 * 
 * @@author A0129660A
 */

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Task;

public class LogicTest {
	Logic logic;
	Command addCommand;				// task1
	Command addCommand2;			// task2
	Command addCommand3;			// task3
	Command addCommand4;			// task4
	Command errorAddCommand;		// task2 with invalid date
	Command deleteCommand;			// remove: task1
	Command deleteCommandId;		// remove: task1_id
	Command updateCommand;			// update: task1 -> task3
	Command updateCommandId;		// update: task1_id -> task3
	Command setCompletedCommand;	// task1 -> done
	Command setCompletedCommandId;	// task1_id -> done
	Command sortCommandPriority;	// sort by priority
	Command sortCommandDate;		// sort by date
	Command sortCommandName;		// sort by name
	Command showCommandPriority;	// show by priority
	Command showCommandDate;		// show by date
	Command showCommandName;		// show by name
	Command undoCommand;			// undo
	
	@Before
	public void init() throws ParseException, IOException {
		logic = new Logic();
		
		// undo command
		undoCommand = new Command("undo", null);
		
		// add task1 command
		HashMap<String, String> argsAdd1 = new HashMap<String, String>();
		argsAdd1.put("taskName", "task1");
		argsAdd1.put("endDate", "01/03/2016");
		argsAdd1.put("priority", "2");
		addCommand = new Command("add", argsAdd1);
		
		// add task2 command
		HashMap<String, String> argsAdd2 = new HashMap<String, String>();
		argsAdd2.put("taskName", "task2");
		argsAdd2.put("endDate", "11/03/2016");
		argsAdd2.put("priority", "1");
		addCommand2 = new Command("add", argsAdd2);
		
		// add task3 command
		HashMap<String, String> argsAdd3 = new HashMap<String, String>();
		argsAdd3.put("taskName", "task3");
		argsAdd3.put("endDate", "10/02/2016");
		argsAdd3.put("priority", "1");
		addCommand3 = new Command("add", argsAdd3);
		
		// add task4 command
		HashMap<String, String> argsAdd4 = new HashMap<String, String>();
		argsAdd4.put("taskName", "task4");
		argsAdd4.put("endDate", "11/03/2016");
		argsAdd4.put("priority", "3");
		addCommand4 = new Command("add", argsAdd4);
		
		// delete task1 command
		HashMap<String, String> argsDelete = new HashMap<String, String>();
		argsDelete.put("taskName", "task1");
		deleteCommand = new Command("delete", argsDelete);
		
		// add invalid task command
		HashMap<String, String> argsError = new HashMap<String, String>();
		argsError.put("taskName", "task2");
		argsError.put("endDate", "some date");
		argsError.put("priority", "1");
		errorAddCommand = new Command("add", argsError);
		
		// set task1 completed command
		HashMap<String, String> argsComplete = new HashMap<String, String>();
		argsComplete.put("taskName", "task1");
		argsComplete.put("completed", "true");
		setCompletedCommand = new Command("complete", argsComplete);
		
		// update task1 to task3 command
		HashMap<String, String> argsUpdate = new HashMap<String, String>();
		argsUpdate.put("taskName", "task1");
		argsUpdate.put("updateName", "task3");
		updateCommand = new Command("update", argsUpdate);
		
		// sort, show command by priority
		HashMap<String, String> argsSortPriority = new HashMap<String, String>();
		argsSortPriority.put("order", "priority");
		sortCommandPriority = new Command("sort", argsSortPriority);
		showCommandPriority = new Command("show", argsSortPriority);
		
		// sort, show command by date
		HashMap<String, String> argsSortDate = new HashMap<String, String>();
		argsSortDate.put("order", "endDate");
		sortCommandDate = new Command("sort", argsSortDate);
		showCommandDate = new Command("show", argsSortDate);
		
		// sort, show command by name
		HashMap<String, String> argsSortName = new HashMap<String, String>();
		argsSortName.put("order", "taskName");
		sortCommandName = new Command("sort", argsSortName);
		showCommandName = new Command("show", argsSortName);
	}
	
	/*
	 * =============== [ INITIALIZATION STATE TESTS ] ===============
	 * These group of methods are for checking initialization states.
	 * Two situations are tested here:
	 * 1. initialization with empty file
	 * 2. initialization with non-empty file. 
	 */
	
	@Test
	public void initialize_With_Empty_File() {
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("File should be empty.", 0, internalStorage.size());
	}
	
	@Test
	public void initialize_With_Non_Empty_File() {
		logic.executeCommand(addCommand);
		logic.updateInternalStorage();
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("There should only be 1 item in the file.", 1, internalStorage.size());
	}
	
	
	/*
	 * =============== [ ADD TASK FUNCTIONALITY TESTS ] ===============
	 * These group of methods are for checking add-task functionality.
	 * These tests run on the context of adding tasks with valid
	 * parameters as well as adding tasks without valid parameters.
	 * Generally, these tests check for 3 things:
	 * 1. Adding a task should increase the size of the task list.
	 * 2. The task added should be added to the end of the list.
	 * 3. The correct data is added to the list.
	 */
	
	@Test
	public void valid_Add_Task_To_Empty_File_List_Size_Increase() {
		logic.executeCommand(addCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should have been added into file.", 1, internalStorage.size());
	}
	
	@Test
	public void valid_Add_Task_To_Empty_File_Correct_Contents_Added() {
		logic.executeCommand(addCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertEquals("Task added into file should have name 'task1'.", "task1", taskInList.getName());
	}
	
	@Test
	public void valid_Add_Task_To_Non_Empty_File_List_Size_Increase() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task2 should have been added into file.", 2, internalStorage.size());
	}
	
	@Test
	public void valid_Add_Task_To_Non_Empty_File_Correct_Add_Index() {
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
	public void valid_Add_Task_To_Non_Empty_File_Correct_Contents_Added() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task addedTask = internalStorage.get(1);
		assertEquals("Task added into file should have name 'task2'.", "task2", addedTask.getName());
	}
	
	@Test
	public void invalid_Add_Task() {	
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
	public void valid_Delete_Existent_Task_List_Size_Decrease() {
		logic.executeCommand(addCommand);
		logic.executeCommand(deleteCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should have been deleted from file.", 0, internalStorage.size());
	}
	
	@Test
	public void valid_Delete_Existent_Task_Correct_Task_Deleted() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(deleteCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task remainingTask = internalStorage.get(0);
		assertEquals("task2 should be remaining in the file.", "task2", remainingTask.getName());
	}
	
	@Test
	public void valid_Delete_Existent_Task_By_Id_List_Size_Decrease() {
		logic.executeCommand(addCommand);
		construct_Delete_Command_Id();
		logic.executeCommand(deleteCommandId);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should have been deleted from file.", 0, internalStorage.size());
	}
	
	@Test
	public void valid_Delete_Existent_Task_By_Id_Correct_Task_Deleted() {
		logic.executeCommand(addCommand);
		construct_Delete_Command_Id();
		logic.executeCommand(addCommand2);
		logic.executeCommand(deleteCommandId);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task remainingTask = internalStorage.get(0);
		assertEquals("task2 should be remaining in the file.", "task2", remainingTask.getName());
	}

	@Test
	public void valid_Delete_Non_Existent_Task() {
		logic.executeCommand(addCommand2);
		logic.executeCommand(deleteCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No deletion should have occured for task2.", 1, internalStorage.size());
	}
	
	@Test
	public void valid_Delete_Non_Existent_Task_Correct_Task_Left() {
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
	public void valid_Update_Existent_Task_Correct_List_Size() {
		logic.executeCommand(addCommand);
		logic.executeCommand(updateCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in update.", 1, internalStorage.size());
	}
	
	@Test
	public void valid_Update_Existent_Task_By_Id_Correct_List_Size() {
		logic.executeCommand(addCommand);
		construct_Update_Command_Id();
		logic.executeCommand(updateCommandId);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in update.", 1, internalStorage.size());
	}
	
	@Test
	public void valid_Update_Existent_Task_Correct_Update_State() {
		logic.executeCommand(addCommand);
		logic.executeCommand(updateCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task updatedTask = internalStorage.get(0);
		assertEquals("task1 should have been renamed to task3.", "task3", updatedTask.getName());
	}
	
	@Test
	public void valid_Update_Existent_Task_By_Id_Correct_Update_State() {
		logic.executeCommand(addCommand);
		construct_Update_Command_Id();
		logic.executeCommand(updateCommandId);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task updatedTask = internalStorage.get(0);
		assertEquals("task1 should have been renamed to task3.", "task3", updatedTask.getName());
	}
	
	@Test
	public void valid_Update_Non_Existent_Task_Correct_List_Size() {
		logic.executeCommand(addCommand2);
		logic.executeCommand(updateCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in update.", 1, internalStorage.size());
	}
	
	@Test
	public void valid_Update_Non_Existent_Task_Correct_State() {
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
	public void valid_Completed_Existent_Task_Correct_List_Size() {
		logic.executeCommand(addCommand);
		logic.executeCommand(setCompletedCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in this operation.", 1, internalStorage.size());
	}
	
	@Test
	public void valid_Completed_Existent_Task_By_Id_Correct_List_Size() {
		logic.executeCommand(addCommand);
		construct_Set_Completed_Command_Id();
		logic.executeCommand(setCompletedCommandId);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in this operation.", 1, internalStorage.size());
	}
	
	@Test
	public void valid_Completed_Existent_Task_Correct_Task_Updated() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(setCompletedCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task targetTask = internalStorage.get(0);
		assertTrue("task1 should have been marked as completed.", targetTask.isCompleted());
	}
	
	@Test
	public void valid_Completed_Existent_Task_By_Id_Correct_Task_Updated() {
		logic.executeCommand(addCommand);
		construct_Set_Completed_Command_Id();
		logic.executeCommand(addCommand2);
		logic.executeCommand(setCompletedCommandId);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task targetTask = internalStorage.get(0);
		assertTrue("task1 should have been marked as completed.", targetTask.isCompleted());
	}
	
	@Test
	public void valid_Completed_Non_Existent_Task_Correct_List_Size() {
		logic.executeCommand(addCommand2);
		logic.executeCommand(setCompletedCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("No change in list size should occur in this operation.", 1, internalStorage.size());
	}
	
	@Test
	public void valid_Completed_Non_Existent_Task_Correct_Updated_State() {
		logic.executeCommand(addCommand2);
		logic.executeCommand(setCompletedCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task targetTask = internalStorage.get(0);
		assertFalse("task1 should reflect 'not completed' status.", targetTask.isCompleted());
	}
	
	/*
	 * ================= [ UNDO FUNCTIONALITY TESTS ] =================
	 * These group of methods are for checking undo functionality.
	 * Generally, these tests check for some things:
	 * 1. Undoing adding of task should only remove the added task.
	 * 2. Undoing deletion of task should only add back the task
	 *    (but added task will be added at end of list instead of its
	 *    original position in the list).
	 * 3. Undoing updating of task should only revert the state of
	 *    the task back to before it was updated.
	 * 4. Undoing display of tasks should only revert changes to the
	 *    ordering of tasks. TODO
	 * 5. Undoing sorting of tasks should only revert changes to the
	 *    ordering of tasks. TODO
	 */
	
	@Test
	public void undo_Add_Task_To_Empty_File_Correct_List_Size() {
		logic.executeCommand(addCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("Task list should be empty.", 0, internalStorage.size());
	}
	
	@Test
	public void undo_Add_Task_To_Non_Empty_File_Correct_List_Size() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("Task list should only have 1 item.", 1, internalStorage.size());
	}
	
	@Test
	public void undo_Add_Task_To_Non_Empty_File_Correct_Task_Removed() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertEquals("task2 should not be inside task list.", "task1", taskInList.getName());
	}
	
	@Test
	public void undo_Failed_Add_Task_Nothing_Happen() {
		logic.executeCommand(errorAddCommand);
		String alert = logic.executeCommand(undoCommand);
		assertEquals("Undo should have failed.", "Cannot undo any further.\n", alert);
	}
	
	@Test
	public void undo_Delete_Task_From_Non_Empty_File_Correct_List_Size() {
		logic.executeCommand(addCommand);
		logic.executeCommand(deleteCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("There should only be 1 item in task list.", 1, internalStorage.size());
	}
	
	@Test
	public void undo_Delete_Task_From_Non_Empty_File_Correct_Task_Restored() {
		logic.executeCommand(addCommand);
		logic.executeCommand(deleteCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertEquals("task1 should be back inside the task list.", "task1", taskInList.getName());
	}
	
	@Test
	public void undo_Failed_Delete_Task_Nothing_Happen() {
		logic.executeCommand(deleteCommand);
		String alert = logic.executeCommand(undoCommand);
		assertEquals("Undo should have failed.", "Cannot undo any further.\n", alert);
	}
	
	@Test
	public void undo_Update_Existent_Task_Correct_List_Size() {
		logic.executeCommand(addCommand);
		logic.executeCommand(updateCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("Size of task list should not change.", 1, internalStorage.size());
	}
	
	@Test
	public void undo_Update_Existent_Task_Correct_Task_State_Restored() {	
		logic.executeCommand(addCommand);
		logic.executeCommand(updateCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertEquals("Original name of task should be 'task1'.", "task1", taskInList.getName());
	}
	
	@Test
	public void undo_Failed_Update_Task_Nothing_Happen() {
		logic.executeCommand(updateCommand);
		String alert = logic.executeCommand(undoCommand);
		assertEquals("Undo should have failed.", "Cannot undo any further.\n", alert);
	}
	
	@Test
	public void undo_Completed_Existent_Task_Correct_List_Size() {
		logic.executeCommand(addCommand);
		logic.executeCommand(setCompletedCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("Size of task list should not change.", 1, internalStorage.size());
	}
	
	@Test
	public void undo_Completed_Existent_Task_Correct_Task_State_Restored() {	
		logic.executeCommand(addCommand);
		logic.executeCommand(setCompletedCommand);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		Task taskInList = internalStorage.get(0);
		assertFalse("task1 should have been reverted to uncompleted status.", taskInList.isCompleted());
	}
	
	@Test
	public void undo_Failed_Complete_Task_Nothing_Happen() {
		logic.executeCommand(setCompletedCommand);
		String alert = logic.executeCommand(undoCommand);
		assertEquals("Undo should have failed.", "Cannot undo any further.\n", alert);
	}
	
	@Test
	public void undo_Show_Correct_Ordering() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(addCommand3);
		logic.executeCommand(addCommand4);
		logic.executeCommand(showCommandPriority);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should be first.", "task1", internalStorage.get(0).getName());
		assertEquals("task2 should be second.", "task2", internalStorage.get(1).getName());
		assertEquals("task3 should be third.", "task3", internalStorage.get(2).getName());
		assertEquals("task4 should be last.", "task4", internalStorage.get(3).getName());
	}
	
	@Test
	public void undo_Sort_Correct_Ordering() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(addCommand3);
		logic.executeCommand(addCommand4);
		logic.executeCommand(sortCommandPriority);
		logic.executeCommand(undoCommand);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should be first.", "task1", internalStorage.get(0).getName());
		assertEquals("task2 should be second.", "task2", internalStorage.get(1).getName());
		assertEquals("task3 should be third.", "task3", internalStorage.get(2).getName());
		assertEquals("task4 should be last.", "task4", internalStorage.get(3).getName());
	}
	
	/*
	 * ============== [ DISPLAY-ALL FUNCTIONALITY TESTS ] ==============
	 * These group of methods are for checking display-all functionality.
	 * For now, since there is only the show-all operation, it shall be
	 * checked that for show-all-by-criterion operation, the task
	 * ordering is correct.
	 * 
	 * In general, when full and proper implementations of variations
	 * are completed, tests should check for 2 things:
	 * 1. Number of displayed entries is correct
	 * 2. Within those displayed entries, the ordering is correct
	 */
	
	@Test
	public void display_All_In_Empty_File() {
		logic.executeCommand(showCommandDate);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("File should remain empty.", 0, internalStorage.size());
	}
	
	@Test
	public void display_All_By_Priority_In_Non_Empty_File_Correct_Order() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(addCommand3);
		logic.executeCommand(addCommand4);
		logic.executeCommand(showCommandPriority);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task3 should be first.", "task3", internalStorage.get(0).getName());
		assertEquals("task2 should be second.", "task2", internalStorage.get(1).getName());
		assertEquals("task1 should be third.", "task1", internalStorage.get(2).getName());
		assertEquals("task4 should be last.", "task4", internalStorage.get(3).getName());
	}
	
	@Test
	public void display_All_By_Date_In_Non_Empty_File_Correct_Order() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(addCommand3);
		logic.executeCommand(addCommand4);
		logic.executeCommand(showCommandDate);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task3 should be first.", "task3", internalStorage.get(0).getName());
		assertEquals("task1 should be second.", "task1", internalStorage.get(1).getName());
		assertEquals("task2 should be third.", "task2", internalStorage.get(2).getName());
		assertEquals("task4 should be last.", "task4", internalStorage.get(3).getName());
	}
	
	@Test
	public void display_All_By_Name_In_Non_Empty_File_Correct_Order() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(addCommand3);
		logic.executeCommand(addCommand4);
		logic.executeCommand(showCommandName);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should be first.", "task1", internalStorage.get(0).getName());
		assertEquals("task2 should be second.", "task2", internalStorage.get(1).getName());
		assertEquals("task3 should be third.", "task3", internalStorage.get(2).getName());
		assertEquals("task4 should be last.", "task4", internalStorage.get(3).getName());
	}
	
	/*
	 * ================= [ SORT FUNCTIONALITY TESTS ] =================
	 * These group of methods are for checking sort functionality.
	 * Generally, these tests check for 2 things:
	 * 1. The size of task list does not change.
	 * 2. The order of tasks in the list meet sorting criteria.
	 */
	
	@Test
	public void sort_On_Empty_File() {
		logic.executeCommand(sortCommandDate);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("File should remain empty.", 0, internalStorage.size());
	}
	
	@Test
	public void sort_By_Priority_On_Non_Empty_File_Correct_List_Size() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(addCommand3);
		logic.executeCommand(addCommand4);
		logic.executeCommand(sortCommandPriority);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task3 should be first.", "task3", internalStorage.get(0).getName());
		assertEquals("task2 should be second.", "task2", internalStorage.get(1).getName());
		assertEquals("task1 should be third.", "task1", internalStorage.get(2).getName());
		assertEquals("task4 should be last.", "task4", internalStorage.get(3).getName());
	}
	
	@Test
	public void sort_By_Date_On_Non_Empty_File_Correct_Ordering() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(addCommand3);
		logic.executeCommand(addCommand4);
		logic.executeCommand(sortCommandDate);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task3 should be first.", "task3", internalStorage.get(0).getName());
		assertEquals("task1 should be second.", "task1", internalStorage.get(1).getName());
		assertEquals("task2 should be third.", "task2", internalStorage.get(2).getName());
		assertEquals("task4 should be last.", "task4", internalStorage.get(3).getName());
	}
	
	@Test
	public void sort_By_Name_On_Non_Empty_File_Correct_Ordering() {
		logic.executeCommand(addCommand);
		logic.executeCommand(addCommand2);
		logic.executeCommand(addCommand3);
		logic.executeCommand(addCommand4);
		logic.executeCommand(sortCommandName);
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		assertEquals("task1 should be first.", "task1", internalStorage.get(0).getName());
		assertEquals("task2 should be second.", "task2", internalStorage.get(1).getName());
		assertEquals("task3 should be third.", "task3", internalStorage.get(2).getName());
		assertEquals("task4 should be last.", "task4", internalStorage.get(3).getName());
	}
	
	/*
	 * ================= [ SEARCH FUNCTIONALITY TESTS ] =================
	 * These group of methods are for checking search functionality.
	 * Generally, these tests check for 1 thing:
	 * 1. Search should return all those tasks meeting criteria.
	 */
	
	public void search_On_Empty_File() {
		// search in empty file
		// should have nothing inside
	}
	
	public void valid_Search_On_Non_Empty_File_Has_Result() {
		// search in non-empty file
		// search in this test case has matches
		// check results
	}
	
	public void valid_Search_On_Non_Empty_File_No_Result() {
		// search in non-empty file
		// search in this test case has no matches
		// check results
	}
	
	public void construct_Delete_Command_Id() {
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("taskID", String.valueOf(internalStorage.get(0).getId()));
		//System.out.println("ID of task to delete: " + internalStorage.get(0).getId());
		deleteCommandId = new Command("delete", args);
	}
	
	public void construct_Update_Command_Id() {
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("taskID", String.valueOf(internalStorage.get(0).getId()));
		args.put("updateName", "task3");
		//System.out.println("ID of task to update: " + internalStorage.get(0).getId());
		updateCommandId = new Command("update", args);
	}
	
	public void construct_Set_Completed_Command_Id() {
		ArrayList<Task> internalStorage = logic.getInternalStorage();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("taskID", String.valueOf(internalStorage.get(0).getId()));
		args.put("completed", "true");
		//System.out.println("ID of task to complete: " + internalStorage.get(0).getId());
		setCompletedCommandId = new Command("complete", args);
	}
	
	
	@After
	public void reset() {
		addCommand = null;
		addCommand2 = null;
		addCommand3 = null;
		addCommand4 = null;
		errorAddCommand = null;
		deleteCommand = null;
		updateCommand = null;
		setCompletedCommand = null;
		undoCommand = null;
		showCommandPriority = null;
		showCommandDate = null;
		showCommandName = null;
		sortCommandPriority = null;
		sortCommandDate = null;
		sortCommandName = null;
		logic.flushInternalStorage();
	}
}
