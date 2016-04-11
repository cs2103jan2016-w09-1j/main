package cs2103_w09_1j.esther;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This class is the test-driver for the Logic component only.
 * <br>
 * <br>
 * This test-driver works alongside with Storage, therefore any
 * bugs that are caught in this suite of test cases may be related
 * to the Storage component. However, a sign that the bug is Storage
 * related would be that ALL test cases here would fail due to either
 * IOException or ParseException, which makes it easy to distinguish
 * between bugs in Logic or in Storage.
 * <br>
 * <br>
 * <b>Due to time-sensitive nature of events as well as that these tests
 * use an arbitrary time value, please ensure that this test class is
 * run only before <code>TASK_END_TIME</code>.</b>
 * 
 * @author Tay Guo Qiang
 * @@author A0129660A
 */
public class LogicTest {
	Logic logic;
	SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdformat2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	HashMap<String, String> parameters;
	static final String TASK_END_TIME = "20:00";
	static final String EVENT_START_TIME = "21:00";
	static final String EVENT_END_TIME = "23:00";
	
	// for adding tasks/events
	Command overdueTask;		// [overdueTask, 01/04/2016, 20:00]
	Command overdueEvent;		// [overdueEvent, 01/04/2016, 21:00, 01/04/2016, 23:00]
	Command todayTask;			// [todayTask, TODAY, 20:00]
	Command todayEvent;			// [todayEvent, TODAY, 21:00, TODAY, 23:00]
	Command tomorrowTask;		// [tomorowTask, TOMORROW, 20:00]
	Command tomorrowEvent;		// [tomorrowEvent, TOMORROW, 21:00, TOMORROW, 23:00]
	Command thisWeekTask1;		// [thisWeekTask1, some_date, 20:00]
	Command thisWeekEvent1;		// [thisWeekEvent1, some_date, 21:00, some_date, 23:00]
	Command generalTask;		// [generalTask, 01/06/2016 20:00]
	Command floatingTask;		// [floatingTask]
	Command tomorrowTask2;		// [tomorrowTask, TOMORROW, 20:00] for duplication test
	
	Command dateErrorTask;		// [dateErrorTask, not_a_date, 20:00] FAIL CASE
	Command dateErrorTask2;		// [dateErrorTask2, 30/02/2016, 20:00] NOT SURE. MIGHT PASS, BUT MUST HANDLE CORRECTLY.
	
	// for deleting tasks/events
	Command deleteOverdueTask;		// [delete overdueTask]
	Command deleteTodayTask;		// [delete todayTask]
	Command deleteTomorrowTask;		// [delete tomorrowTask]
	Command deleteThisWeekTask1;	// [delete thisWeekTask1]
	Command deleteFloating;			// [delete floatingTask]
	Command deleteId;				// [delete 0] deletes 'overdueTask'
	
	Command deleteNotFound;			// [delete no such task] FAIL CASE
	Command deleteSameName;			// [delete tomorrowTask] FAIL CASE
	Command deleteInvalidId;		// [delete 50] FAIL CASE
	
	// for updating tasks/events
	Command changeTodayTaskName;		// [todayTask --> todayName]
	Command changeTodayTaskDate1;		// [todayTask: TODAY --> 01/04/2016]
	Command changeTodayTaskDate2;		// [todayTask: TODAY --> TOMORROW 12:00]
	Command changeTodayTaskDate3;		// [todayTask: TODAY --> some_date 12:00]
	Command changeTodayTaskDate4;		// [todayTask: TODAY --> over_this_week 12:00]
	
	Command changeTodayTaskDateError;	// [todayTask: TODAY --> not_a_date] FAIL CASE
	Command changeTodayTaskDateError2;	// [todayTask: TODAY --> 30/02/2016] NOT SURE. MIGHT PASS, BUT MUST HANDLE CORRECTLY.
	Command changeNotFound;				// [update blah priority to 2] FAIL CASE
	Command changeSameName;				// [update tomorrowTask priority to 2] FAIL CASE
	Command changeInvalidId;			// [update 50 priority to 2] FAIL CASE
	
	Command changeTodayEventDate1Start;	// [todayEvent: TODAY (start) --> 01/04/2016 12:00]
	Command changeTodayEventDate2End;	// [todayEvent: TODAY (end) --> TOMORROW 14:00]
	Command changeTodayEventDate2Start;	// [todayEvent: TODAY (start) --> TOMORROW 12:00]
	Command changeTodayEventDate3End;	// [todayEvent: TODAY (end) --> this_week 14:00]
	Command changeTodayEventDate3Start;	// [todayEvent: TODAY (start) --> this_week 12:00]
	Command changeTodayEventDate4End;	// [todayEvent: TODAY (end) --> over_this_week 14:00]
	Command changeTodayEventDate4Start;	// [todayEvent: TODAY (start) --> over_this_week 12:00]
	
	Command changeTodayEventDateError;	// [todayEvent: TODAY (start) --> 01/06/2016 20:00] FAIL CASE
	Command changeTodayEventDateError2;	// [todayEvent: TODAY (start) --> 30/02/2016 20:00] NOT SURE.
	Command changeTodayEventDateError3; // [todayEvent: TODAY (end) --> 01/03/2016 20:00] FAIL CASE
	
	Command changeTodayTaskPriority;	// [todayTask: priority --> 1]
	Command changeTodayEventPriority;	// [todayEvent: priority --> 3 (update by ID)]
	
	Command changeTodayTaskPriorityError;  // [todayTask: priority --> 0] FAIL CASE
	Command changeTodayTaskPriorityError2; // [todayTask: priority --> 7] FAIL CASE
	
	// for completing tasks/events
	Command completeOverdueTask;		// [overdueTask --> done]
	Command completeTodayTask;			// [todayTask --> done]
	Command completeTomorrowTask;		// [tomorrowTask --> done]
	Command completeThisWeekTask;		// [thisWeekTask1 --> done]
	Command completeGeneralTask;		// [generalTask --> done]
	Command completeFloatingTask;		// [floatingTask --> done]
	Command completeId;					// [complete 0: overDue --> done]
	
	Command completeNotFound;			// [complete no such task] FAIL CASE
	Command completeSameName;			// [complete tomorrowTask] FAIL CASE
	Command completeInvalidId;			// [complete 50] FAIL CASE
	
	// for sorting tasks/events
	Command sortByName;		// [sort order: name]
	Command sortByDate;		// [sort order: date]
	Command sortByStartDate;// [sort order: startDate]
	Command sortByEndDate;	// [sort order: endDate]
	Command sortByPriority;	// [sort order: priority]
	
	/* 
	 * smaller version of sort-tests to check if sorting works for tasks with
	 * same priority, same date, same name. 
	 */
	Command generalTask1;		// [generalTask1, 01/06/2016, priority 3]
	Command generalTask2;		// [generalTask2, 01/06/2016, priority 1]
	Command generalTask3;		// [generalTask3, 01/06/2016, priority 4]
	Command generalTask4;		// [generalTask4, 02/06/2016, priority 1]
	Command generalTask5;		// [generalTask1, 03/06/2016, priority 3]
	
	// for searching tasks/events
	Command searchForName;			// [search for todayTask]
	Command searchForNameNotFound;	// [search for blah] FAIL CASE
	
	Command searchBeforeDate;		// [search before TOMORROW]
	Command searchOnDate;			// [search on TOMORROW]
	Command searchAfterDate;		// [search after TOMORROW]
	Command searchNotDate;			// [search on not_date] FAIL CASE
	Command searchSpecialDate;		// [search after 30/02/2016] NOT SURE. MIGHT PASS, BUT MUST HANDLE CORRECTLY.
	
	// for setting save filepath
	Command setSavePath;			// [set newlist.txt]
	
	// for help command
	Command help;
	
	// for testing guard clause for corrupted Command objects
	Command corruptCommand;
	
	// for undo
	Command undo;

	/**
	 * This method initializes all required Command objects to be used by the Logic.
	 * This is to test correctness of Logic functionality independent of the Parser.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	@Before
	public void init() throws ParseException, IOException {
		logic = new Logic();
		Task.setGlobalId(0);
		Date today = new Date();
		String todayString = sdformat.format(today);
		
		Date tomorrow = (Date) today.clone();
		tomorrow.setDate(today.getDate() + 1);
		String tomorrowString = sdformat.format(tomorrow);
		
		Date somewhereThisWeek = (Date) today.clone();
		somewhereThisWeek.setDate(today.getDate() + 4);
		String somewhereThisWeekString = sdformat.format(somewhereThisWeek);
		
		// single HashMap to be used for all commands. Will be cleared where necessary
		parameters = new HashMap<String, String>();
		
		// =================== create undo command =================== //
		undo = new Command("undo", parameters);
		
		// ================= create search commands ================== //
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		searchForName = new Command("search", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "blah");
		searchForNameNotFound = new Command("search", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("keyword", "before");
		parameters.put("endDate", tomorrowString);
		searchBeforeDate = new Command("search", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("keyword", "on");
		parameters.put("endDate", tomorrowString);
		searchOnDate = new Command("search", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("keyword", "after");
		parameters.put("endDate", tomorrowString);
		searchAfterDate = new Command("search", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("keyword", "after");
		parameters.put("endDate", "not a date");
		searchNotDate = new Command("search", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("keyword", "after");
		parameters.put("endDate", "30/02/2016");
		searchSpecialDate = new Command("search", parameters);
		
		// ================= create sort commands ================== //
		parameters = new HashMap<String, String>();
		parameters.put("order", "taskName");
		sortByName = new Command("sort", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("order", "date");
		sortByDate = new Command("sort", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("order", "startDate");
		sortByStartDate = new Command("sort", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("order", "endDate");
		sortByEndDate = new Command("sort", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("order", "priority");
		sortByPriority = new Command("sort", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "generalTask1");
		parameters.put("endDate", "01/06/2016");
		parameters.put("endTime", TASK_END_TIME);
		parameters.put("priority", "3");
		generalTask1 = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "generalTask2");
		parameters.put("endDate", "01/06/2016");
		parameters.put("endTime", TASK_END_TIME);
		parameters.put("priority", "1");
		generalTask2 = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "generalTask3");
		parameters.put("endDate", "01/06/2016");
		parameters.put("endTime", TASK_END_TIME);
		parameters.put("priority", "4");
		generalTask3 = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "generalTask4");
		parameters.put("endDate", "02/06/2016");
		parameters.put("endTime", TASK_END_TIME);
		parameters.put("priority", "1");
		generalTask4 = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "generalTask1");
		parameters.put("endDate", "03/06/2016");
		parameters.put("endTime", TASK_END_TIME);
		parameters.put("priority", "3");
		generalTask5 = new Command("add", parameters);
		
		// ================= create complete commands ================== //
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "overdueTask");
		completeOverdueTask = new Command("complete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		completeTodayTask = new Command("complete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "tomorrowTask");
		completeTomorrowTask = new Command("complete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "thisWeekTask1");
		completeThisWeekTask = new Command("complete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "generalTask");
		completeGeneralTask = new Command("complete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "floatingTask");
		completeFloatingTask = new Command("complete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskID", "0");
		completeId = new Command("complete", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "no such task");
		completeNotFound = new Command("complete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "tomorrowTask");
		completeSameName = new Command("complete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskID", "50");
		completeInvalidId = new Command("complete", parameters);
		
		// ================= create delete commands ================== //
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "overdueTask");
		deleteOverdueTask = new Command("delete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		deleteTodayTask = new Command("delete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "tomorrowTask");
		deleteTomorrowTask = new Command("delete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "tomorrowTask");
		deleteSameName = new Command("delete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "thisWeekTask1");
		deleteThisWeekTask1 = new Command("delete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "floatingTask");
		deleteFloating = new Command("delete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "blah");
		deleteNotFound = new Command("delete", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskID", "0");
		deleteId = new Command("delete", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskID", "50");
		deleteInvalidId = new Command("delete", parameters);
		
		// ================= create add commands ================== //
		parameters = new HashMap<String, String>();		
		parameters.put("taskName", "overdueTask");
		parameters.put("endDate", "01/04/2016");
		parameters.put("endTime", TASK_END_TIME);
		overdueTask = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("endDate", todayString);
		parameters.put("endTime", TASK_END_TIME);
		todayTask = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "tomorrowTask");
		parameters.put("endDate", tomorrowString);
		parameters.put("endTime", TASK_END_TIME);
		tomorrowTask = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "tomorrowTask");
		parameters.put("endDate", tomorrowString);
		parameters.put("endTime", TASK_END_TIME);
		tomorrowTask2 = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "thisWeekTask1");
		parameters.put("endDate", somewhereThisWeekString);
		parameters.put("endTime", TASK_END_TIME);
		thisWeekTask1 = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "generalTask");
		parameters.put("endDate", "01/06/2016");
		parameters.put("endTime", TASK_END_TIME);
		generalTask = new Command("add", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "floatingTask");
		floatingTask = new Command("add", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "dateErrorTask");
		parameters.put("endDate", "not a date");
		dateErrorTask = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "dateErrorTask2");
		parameters.put("endDate", "30/02/2016");
		dateErrorTask2 = new Command("add", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "overdueEvent");
		parameters.put("startDate", "01/04/2016");
		parameters.put("startTime", EVENT_START_TIME);
		parameters.put("endDate", "01/04/2016");
		parameters.put("endTime", EVENT_END_TIME);
		overdueEvent = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("startDate", todayString);
		parameters.put("startTime", EVENT_START_TIME);
		parameters.put("endDate", todayString);
		parameters.put("endTime", EVENT_END_TIME);
		todayEvent = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "tomorrowEvent");
		parameters.put("startDate", tomorrowString);
		parameters.put("startTime", EVENT_START_TIME);
		parameters.put("endDate", tomorrowString);
		parameters.put("endTime", EVENT_END_TIME);
		tomorrowEvent = new Command("add", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "thisWeekEvent1");
		parameters.put("startDate", somewhereThisWeekString);
		parameters.put("startTime", EVENT_START_TIME);
		parameters.put("endDate", somewhereThisWeekString);
		parameters.put("endTime", EVENT_END_TIME);
		thisWeekEvent1 = new Command("add", parameters);
		
		// ================= create update commands ================== //
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("updateName", "todayName");
		changeTodayTaskName = new Command("update", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("endDate", "01/04/2016");
		changeTodayTaskDate1 = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("endDate", tomorrowString);
		changeTodayTaskDate2 = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("endDate", somewhereThisWeekString);
		changeTodayTaskDate3 = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("endDate", "01/06/2016");
		changeTodayTaskDate4 = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("endDate", "not a date");
		changeTodayTaskDateError = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("endDate", "30/02/2016");
		changeTodayTaskDateError2 = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "blah");
		parameters.put("endDate", "01/04/2016");
		changeNotFound = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "tomorrowTask");
		parameters.put("endDate", "01/04/2016");
		changeSameName = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskID", "50");
		changeInvalidId = new Command("complete", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("startDate", "01/04/2016");
		parameters.put("startTime", "12:00");
		changeTodayEventDate1Start = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("startDate", tomorrowString);
		parameters.put("startTime", "12:00");
		changeTodayEventDate2Start = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("startDate", somewhereThisWeekString);
		parameters.put("startTime", "12:00");
		changeTodayEventDate3Start = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("startDate", "01/06/2016");
		parameters.put("startTime", "12:00");
		changeTodayEventDate4Start = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("startDate", "not a date");
		parameters.put("startTime", "12:00");
		changeTodayEventDateError = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("startDate", "30/02/2016");
		parameters.put("startTime", "12:00");
		changeTodayEventDateError2 = new Command("update", parameters);
		
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("endDate", tomorrowString);
		parameters.put("endTime", "14:00");
		changeTodayEventDate2End = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("endDate", somewhereThisWeekString);
		parameters.put("endTime", "14:00");
		changeTodayEventDate3End = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("endDate", "01/06/2016");
		parameters.put("endTime", "14:00");
		changeTodayEventDate4End = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayEvent");
		parameters.put("endDate", "01/03/2016");
		parameters.put("endTime", "14:00");
		changeTodayEventDateError3 = new Command("update", parameters);
		
		parameters = new HashMap<String, String>();		
		parameters.put("taskName", "todayTask");
		parameters.put("priority", "1");
		changeTodayTaskPriority = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskID", "3");
		parameters.put("priority", "3");
		changeTodayEventPriority = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("priority", "0");
		changeTodayTaskPriorityError = new Command("update", parameters);
		parameters = new HashMap<String, String>();
		parameters.put("taskName", "todayTask");
		parameters.put("priority", "7");
		changeTodayTaskPriorityError2 = new Command("update", parameters);

		// ================= create set savepath command ================== //
		parameters = new HashMap<String, String>();
		parameters.put("path", "newlist.txt");
		setSavePath = new Command("set", parameters);

		// ================= create help command ================== //
		parameters = new HashMap<String, String>();
		help = new Command("help", parameters);
		
		// ================= create corrupt command ================== //
		parameters = new HashMap<String, String>();
		corruptCommand = new Command("not a command", parameters);
		
		logic.flushInternalStorage();
	}
	
	/* ===================== INITIALIZATION TESTS ========================== */
	@Test
	public void initializeOnEmptyFile() {
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("Inner memory should have nothing.", 0, mem.size());
	}
	
	/**
	 * A non-empty file is simulated in this test case by adding a task inside
	 * before re-launching Logic.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	@Test
	public void initializeOnNonEmptyFile() throws ParseException, IOException { 
		logic.executeCommand(overdueTask);
		logic = new Logic();
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("Inner memory should have 1 item.", 1, mem.size());
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 1 item in the list.", 1, overdues.size());
	}
	
	
	/* ===================== TASK-ADDING TESTS ========================== */
	@Test
	public void testAddOnEmptyFile() {
		addAllTaskTypesToEmptyFile(true, false);
	}
	
	/**
	 * Adds 10 task/event items into Logic as though Logic initializes with nothing in
	 * the text file. If this addition is to be tested, simply supply <code>isTest = true</code>
	 * as the argument to trigger the test assertions. If duplicate-name entries are also desired,
	 * simply supply <code>toHaveDuplicates = true</code> to trigger duplicate-name entries.
	 * 
	 * @param isTest whether this addition is to be used as a test
	 */
	public void addAllTaskTypesToEmptyFile(boolean isTest, boolean toHaveDuplicates) {
		logic.flushInternalStorage();
		logic.executeCommand(overdueTask);
		logic.executeCommand(overdueEvent);
		logic.executeCommand(todayTask);
		logic.executeCommand(todayEvent);
		logic.executeCommand(tomorrowTask);
		logic.executeCommand(tomorrowEvent);
		logic.executeCommand(thisWeekTask1);
		logic.executeCommand(thisWeekEvent1);
		logic.executeCommand(generalTask);
		logic.executeCommand(floatingTask);
		if (toHaveDuplicates) {
			logic.executeCommand(tomorrowTask2);
		}
		
		if (isTest) {
			// checks general ordering
			ArrayList<Task> mem = logic.getInternalStorage();
			assertEquals("There should be 10 items in the list.", 10, mem.size());
			assertEquals("First task should be 'overdueTask'.", "overdueTask", mem.get(0).getName());
			assertEquals("Second task should be 'overdueEvent'.", "overdueEvent", mem.get(1).getName());
			assertEquals("Third task should be 'todayTask'.", "todayTask", mem.get(2).getName());
			assertEquals("Fourth task should be 'todayEvent'.", "todayEvent", mem.get(3).getName());
			assertEquals("Fifth task should be 'tomorrowTask'.", "tomorrowTask", mem.get(4).getName());
			assertEquals("Sixth task should be 'tomorrowEvent'.", "tomorrowEvent", mem.get(5).getName());
			assertEquals("Seventh task should be 'thisWeekTask1'.", "thisWeekTask1", mem.get(6).getName());
			assertEquals("Eighth task should be 'thisWeekEvent1'.", "thisWeekEvent1", mem.get(7).getName());
			assertEquals("Ninth task should be 'generalTask'.", "generalTask", mem.get(8).getName());
			assertEquals("Tenth task should be 'floatingTask'.", "floatingTask", mem.get(9).getName());

			// the rest below checks if tasks belong to their own categories
			ArrayList<Task> overdues = logic.getOverdueBuffer();
			assertEquals("There should be 2 items in the overdue list.", 2, overdues.size());
			assertEquals("First task should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
			assertEquals("Second task should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

			ArrayList<Task> todayTasks = logic.getTodayBuffer();
			assertEquals("There should be 2 items in today's list.", 2, todayTasks.size());
			assertEquals("First task should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
			assertEquals("Second task should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

			ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
			assertEquals("There should be 2 items in tomorrow's list.", 2, tomorrowTasks.size());
			assertEquals("First task should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
			assertEquals("Second task should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

			ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
			assertEquals("There should be 2 items in this week's list.", 2, thisWeekTasks.size());
			assertEquals("First task should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
			assertEquals("Second task should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

			ArrayList<Task> uncategorizedTasks = logic.getRemainingBuffer();
			assertEquals("There should be 1 item in remaining list.", 1, uncategorizedTasks.size());
			assertEquals("First task should be 'generalTask'.", "generalTask", uncategorizedTasks.get(0).getName());

			ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
			assertEquals("There should be 1 item in remaining list.", 1, floatingTasks.size());
			assertEquals("First task should be 'floatingTask'.", "floatingTask", floatingTasks.get(0).getName());
		}
	}
	
	/**
	 * Non-test method for simulating a file with contents.
	 * 
	 * To simulate a file with entries containing duplicate task names, simply supply
	 * <code>toHaveDuplicates = true</code> as the argument to trigger duplicate-name
	 * task entries.
	 */
	public void makeNonEmptyFile(boolean toHaveDuplicates) {
		addAllTaskTypesToEmptyFile(false, toHaveDuplicates);
	}
	
	@Test
	public void testAddToNonEmptyFile() {
		makeNonEmptyFile(false); // 10 items would already have been in the file
		logic.executeCommand(todayTask);
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 11 items in the list.", 11, mem.size());
	}
	
	@Test
	public void testAddInvalidTask() {
		logic.executeCommand(dateErrorTask);
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be nothing in the list.", 0, mem.size());
	}
	
	/**
	 * This test case is expected to pass, due to the way that Java's
	 * <code>Date</code> class handles <code>30/02/2016</code>. In that class,
	 * <code>30/02/2016</code> is interpreted as <code>01/03/2016</code>.
	 * 
	 * However, it is up to Parser to guard against such weird dates, or to handle
	 * them properly.
	 */
	@Test
	public void testAddWeirdDateTask() {
		logic.executeCommand(dateErrorTask2);
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 1 item in the list.", 1, mem.size());
	}
	
	
	/* ===================== TASK-DELETING TESTS ========================== */
	@Test
	public void deleteOverdueTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteOverdueTask);
		
		// checks that 1 element has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());
		
		// checks that corresponding task in relevant buffer is removed
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 1 item in the list.", 1, overdues.size());
		
		/* 
		 * checks that correct item is removed (i.e. check that correct task is
		 * removed by checking that correct remaining task(s) remain)
		 */
		assertEquals("'overdueEvent' should remain in the list.", "overdueEvent", overdues.get(0).getName());
	}
	
	@Test
	public void deleteTodayTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteTodayTask);
		
		// checks that 1 element has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());
		
		// checks that corresponding task in relevant buffer is removed
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 1 item in the list.", 1, todayTasks.size());
				
		/* 
		 * checks that correct item is removed (i.e. check that correct task is
		 * removed by checking that correct remaining task(s) remain)
		 */
		assertEquals("'todayEvent' should remain in the list.", "todayEvent", todayTasks.get(0).getName());
	}
	
	@Test
	public void deleteTomorrowTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteTomorrowTask);
		
		// checks that 1 element has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());
		
		// checks that corresponding task in relevant buffer is removed
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 1 item in the list.", 1, tomorrowTasks.size());
				
		/* 
		 * checks that correct item is removed (i.e. check that correct task is
		 * removed by checking that correct remaining task(s) remain)
		 */
		assertEquals("'tomorrowEvent' should remain in the list.", "tomorrowEvent", tomorrowTasks.get(0).getName());
	}
	
	@Test
	public void deleteThisWeekTask1() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteThisWeekTask1);
		
		// checks that 1 element has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());
		
		// checks that corresponding task in relevant buffer is removed
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 1 item in the list.", 1, thisWeekTasks.size());
				
		/* 
		 * checks that correct item is removed (i.e. check that correct task is
		 * removed by checking that correct remaining task(s) remain)
		 */
		assertEquals("'thisWeekEvent1' should remain in the list.", "thisWeekEvent1", thisWeekTasks.get(0).getName());
	}
	
	@Test
	public void deleteFloatingTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteFloating);
		
		// checks that 1 element has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());
		
		// checks that corresponding task in relevant buffer is removed
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be nothing in the list.", 0, floatingTasks.size());
				
		/* 
		 * the ONLY floating task inside should have disappeared
		 */
	}
	
	@Test
	public void deleteTaskById() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteId);
		
		// checks that 1 element has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());
		
		// [NOTICE] item deleted is overdueTask (delete 0; overdueTask --> 0)
		// checks that corresponding task in relevant buffer is removed
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 1 item in the list.", 1, overdues.size());
				
		/* 
		 * checks that correct item is removed (i.e. check that correct task is
		 * removed by checking that correct remaining task(s) remain)
		 */
		assertEquals("'overdueEvent' should remain in the list.", "overdueEvent", overdues.get(0).getName());
	}
	
	@Test
	public void deleteNotFoundTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteNotFound);
		
		// checks that NO elements has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
	}
	
	@Test
	public void deleteSameNameTask() {
		makeNonEmptyFile(true);
		logic.executeCommand(deleteSameName);
		
		// checks that NO elements has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 11 items in the list.", 11, mem.size());
	}
	
	@Test
	public void deleteInvalidId() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteInvalidId);
		
		// checks that NO elements has been removed in general
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
	}
	
	
	/* ===================== TASK-UPDATE TESTS ========================== */
	/*
	 * Whenever a task is updated, it is taken out from its previous sub-list, then
	 * placed into a new sub-list depending on the category that the updated task now
	 * falls under. Therefore, it is expected that an updated task is always moved to
	 * the back of any sub-list that it belongs to. 
	 */
	@Test
	public void updateTaskName() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskName);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		assertEquals("First item should be 'todayEvent'.", "todayEvent", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayName'.", "todayName", todayTasks.get(1).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateTaskDateToOverdue() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskDate1);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should now be 3 things in the list.", 3, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());
		assertEquals("'todayTask' should be moved to the OVERDUE category.", "todayTask", overdues.get(2).getName());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 thing in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayEvent'.", "todayEvent", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateTaskDateToTomorrow() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskDate2);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 item in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayEvent'.", "todayEvent", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should now be 3 items in the list.", 3, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", tomorrowTasks.get(2).getName());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateTaskDateToThisWeek() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskDate3);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 item in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayEvent'.", "todayEvent", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should now be 3 items in the list.", 3, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", thisWeekTasks.get(2).getName());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateTaskDateToAfterThisWeek() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskDate4);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 item in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayEvent'.", "todayEvent", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should now be 2 items in the list.", 2, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());
		assertEquals("Second item should be 'todayTask'.", "todayTask", remainingTasks.get(1).getName());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateTaskDateToInvalidDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskDateError);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	/**
	 * This test case is expected to pass because Java <code>Date</code> class will
	 * parse <code>30/02/2016</code> to <code>01/03/2016</code>.
	 * 
	 * However, it is up to Parser to guard against such weird dates, or to handle
	 * them properly.
	 */
	@Test
	public void updateTaskDateToWeirdDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskDateError2);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should now be 3 things in the list.", 3, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());
		assertEquals("'todayTask' should be moved to the OVERDUE category.", "todayTask", overdues.get(2).getName());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 thing in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayEvent'.", "todayEvent", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateNotFoundTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeNotFound);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateSameNameTasks() {
		makeNonEmptyFile(true);
		logic.executeCommand(changeSameName);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 11, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 3, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateInvalidId() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeInvalidId);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateEventStartDateToOverdue() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayEventDate1Start);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should now be 3 items in the list.", 3, overdues.size());
		assertEquals("First item should be 'overdueTask'", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'", "overdueEvent", overdues.get(1).getName());
		assertEquals("Third item should be 'todayEvent'", "todayEvent", overdues.get(2).getName());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 item in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayTask'", "todayTask", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateEventDatesToTomorrow() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayEventDate2End);
		logic.executeCommand(changeTodayEventDate2Start);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 item in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayTask'", "todayTask", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should now be 3 items in the list.", 3, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'", "tomorrowEvent", tomorrowTasks.get(1).getName());
		assertEquals("Third item should be 'todayEvent'", "todayEvent", tomorrowTasks.get(2).getName());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateEventDatesToThisWeek() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayEventDate3End);
		logic.executeCommand(changeTodayEventDate3Start);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 item in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayTask'", "todayTask", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should now be 3 items in the list.", 3, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'", "thisWeekEvent1", thisWeekTasks.get(1).getName());
		assertEquals("Third item should be 'todayEvent'", "todayEvent", thisWeekTasks.get(2).getName());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateEventDatesToAfterThisWeek() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayEventDate4End);
		logic.executeCommand(changeTodayEventDate4Start);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 item in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayTask'", "todayTask", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should now be 2 items in the list.", 2, remainingTasks.size());
		assertEquals("First item should be 'generalTask'", "generalTask", remainingTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'", "todayEvent", remainingTasks.get(1).getName());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateEventStartDateToAfterEndDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayEventDateError);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateEventEndDateToBeforeStartDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayEventDateError3);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	/**
	 * This test case is expected to pass because Java <code>Date</code> class will
	 * parse <code>30/02/2016</code> to <code>01/03/2016</code>.
	 * 
	 * However, it is up to Parser to guard against such weird dates, or to handle
	 * them properly.
	 */
	@Test
	public void updateEventStartDateToWeirdDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayEventDateError2);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should now be 3 things in the list.", 3, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());
		assertEquals("'todayEvent' should be moved to the OVERDUE category.", "todayEvent", overdues.get(2).getName());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should now be 1 thing in the list.", 1, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	/**
	 * This test case is expected to pass because Java <code>Date</code> class will
	 * parse <code>30/02/2016</code> to <code>01/03/2016</code>.
	 * 
	 * However, it is up to Parser to guard against such weird dates, or to handle
	 * them properly.
	 */
	@Test
	public void updateTaskPriority() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskPriority);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		assertEquals("First item should be 'todayEvent'.", "todayEvent", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayTask'.", "todayTask", todayTasks.get(1).getName());
		assertEquals("'todayTask' should have priority level 1.", 1, todayTasks.get(1).getPriority());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateEventPriorityById() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayEventPriority);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());
		assertEquals("'todayEvent' should have priority level 3.", 3, todayTasks.get(1).getPriority());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateTaskInvalidPriority1() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskPriorityError);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());
		assertEquals("'todayTask' should have priority level 5.", 5, todayTasks.get(0).getPriority());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	@Test
	public void updateTaskInvalidPriority2() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeTodayTaskPriorityError2);

		// checks if combined list does not face unexpected behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 10, mem.size());
		
		// checks each sub-list for content integrity
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be no change in list size.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be no change in list size.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());
		assertEquals("'todayTask' should have priority level 5.", 5, todayTasks.get(0).getPriority());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be no change in list size.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be no change in list size.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be no change in list size.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be no change in list size.", 1, floatingTasks.size());
	}
	
	
	/* ===================== TASK-COMPLETING TESTS ========================== */
	@Test
	public void completeOverdueTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeOverdueTask);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * Checks that the correct task is moved to the correct place.
		 * (i.e. check that correct task remains in the sub-list containing
		 * the previously-uncompleted task and that the task is now moved to
		 * the completed-task sub-list)
		 */
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 1 item in the list.", 1, overdues.size());
		assertEquals("'overdueEvent' should remain in the list.", "overdueEvent", overdues.get(0).getName());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be 1 item in the list.", 1, completed.size());
		assertEquals("'overdueTask' should be in the list.", "overdueTask", completed.get(0).getName());
	}
	
	@Test
	public void completeTodayTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeTodayTask);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * Checks that the correct task is moved to the correct place.
		 * (i.e. check that correct task remains in the sub-list containing
		 * the previously-uncompleted task and that the task is now moved to
		 * the completed-task sub-list)
		 */
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 1 item in the list.", 1, todayTasks.size());
		assertEquals("'todayEvent' should remain in the list.", "todayEvent", todayTasks.get(0).getName());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be 1 item in the list.", 1, completed.size());
		assertEquals("'todayTask' should be in the list.", "todayTask", completed.get(0).getName());
	}
	
	@Test
	public void completeTomorrowTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeTomorrowTask);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * Checks that the correct task is moved to the correct place.
		 * (i.e. check that correct task remains in the sub-list containing
		 * the previously-uncompleted task and that the task is now moved to
		 * the completed-task sub-list)
		 */
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 1 item in the list.", 1, tomorrowTasks.size());
		assertEquals("'tomorrowEvent' should remain in the list.", "tomorrowEvent", tomorrowTasks.get(0).getName());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be 1 item in the list.", 1, completed.size());
		assertEquals("'tomorrowTask' should be in the list.", "tomorrowTask", completed.get(0).getName());
	}
	
	@Test
	public void completeThisWeekTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeThisWeekTask);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * Checks that the correct task is moved to the correct place.
		 * (i.e. check that correct task remains in the sub-list containing
		 * the previously-uncompleted task and that the task is now moved to
		 * the completed-task sub-list)
		 */
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 1 item in the list.", 1, thisWeekTasks.size());
		assertEquals("'thisWeekEvent1' should remain in the list.", "thisWeekEvent1", thisWeekTasks.get(0).getName());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be 1 item in the list.", 1, completed.size());
		assertEquals("'thisWeekTask1' should be in the list.", "thisWeekTask1", completed.get(0).getName());
	}
	
	@Test
	public void completeGeneralTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeGeneralTask);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * Checks that the correct task is moved to the correct place.
		 * (i.e. check that correct task remains in the sub-list containing
		 * the previously-uncompleted task and that the task is now moved to
		 * the completed-task sub-list)
		 */
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be NO items in the list.", 0, remainingTasks.size());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be 1 item in the list.", 1, completed.size());
		assertEquals("'generalTask' should be in the list.", "generalTask", completed.get(0).getName());
	}
	
	@Test
	public void completeFloatingTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeFloatingTask);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * Checks that the correct task is moved to the correct place.
		 * (i.e. check that correct task remains in the sub-list containing
		 * the previously-uncompleted task and that the task is now moved to
		 * the completed-task sub-list)
		 */
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO items in the list.", 0, floatingTasks.size());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be 1 item in the list.", 1, completed.size());
		assertEquals("'floatingTask' should be in the list.", "floatingTask", completed.get(0).getName());
	}
	
	@Test
	public void completeTaskById() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeId);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * [NOTICE] the item removed is 'overdueTask'
		 * Checks that the correct task is moved to the correct place.
		 * (i.e. check that correct task remains in the sub-list containing
		 * the previously-uncompleted task and that the task is now moved to
		 * the completed-task sub-list)
		 */
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 1 item in the list.", 1, overdues.size());
		assertEquals("'overdueEvent' should remain in the list.", "overdueEvent", overdues.get(0).getName());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be 1 item in the list.", 1, completed.size());
		assertEquals("'overdueTask' should be in the list.", "overdueTask", completed.get(0).getName());
	}
	
	@Test
	public void completeNotFound() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeNotFound);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * [NOTICE] no tasks were moved around to different buffers
		 * Checks that all sub-lists are not affected.
		 */
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@Test
	public void completeSameNameTasks() {
		makeNonEmptyFile(true);
		logic.executeCommand(completeSameName);
		
		// checks that number of elements remain the same (with duplicate-name task present)
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 11 items in the list.", 11, mem.size());
		
		/*
		 * [NOTICE] no tasks were moved around to different buffers
		 * Checks that all sub-lists are not affected.
		 */
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		
		/*
		 * There is one more 'tomorrowTask' due on tomorrow, so there are 3 elements
		 * in this list now. 
		 */
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 3 items in the list.", 3, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@Test
	public void completeInvalidIdTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeInvalidId);
		
		// checks that number of elements remain the same
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 10 items in the list.", 10, mem.size());
		
		/*
		 * [NOTICE] no tasks were moved around to different buffers
		 * Checks that all sub-lists are not affected.
		 */
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		
		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		
		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		
		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		
		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
		
		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	
	/* ===================== TASK-SORTING TESTS ========================== */
	/* NOTICE: Completed tasks are NEVER sorted and thus, no testing is needed on completed-task list. */
	/**
	 * Checks if no exceptions are thrown when sorting an empty list.
	 */
	@Test
	public void sortEmptyListByValidCriteria() {
		try {
			logic.executeCommand(sortByName);
			ArrayList<Task> mem = logic.getInternalStorage();
			assertEquals("List should not experience a change in size.", 0, mem.size());
			logic.executeCommand(sortByDate);
			mem = logic.getInternalStorage();
			assertEquals("List should not experience a change in size.", 0, mem.size());
			logic.executeCommand(sortByStartDate);
			mem = logic.getInternalStorage();
			assertEquals("List should not experience a change in size.", 0, mem.size());
			logic.executeCommand(sortByEndDate);
			mem = logic.getInternalStorage();
			assertEquals("List should not experience a change in size.", 0, mem.size());
			logic.executeCommand(sortByPriority);
			mem = logic.getInternalStorage();
			assertEquals("List should not experience a change in size.", 0, mem.size());
		} catch (Exception e) {
			fail("No exceptions should be encountered.");
		}
	}
	
	@Test
	public void sortNonEmptyListByName() {
		try {
			makeNonEmptyFile(false);
			logic.executeCommand(sortByName);
			
			// checks that inner memory does not face weird behavior
			ArrayList<Task> mem = logic.getInternalStorage();
			assertEquals("List should not experience a change in size.", 10, mem.size());

			// checks that combined tasks memory does not face weird behavior
			ArrayList<Task> allTasks = logic.getTemporarySortList();
			assertEquals("List should not experience a change in size.", 10, allTasks.size());
			
			// checks that order is correct in combined list of tasks
			assertEquals("First item should be 'floatingTask'.", "floatingTask", allTasks.get(0).getName());
			assertEquals("Second item should be 'generalTask'.", "generalTask", allTasks.get(1).getName());
			assertEquals("Third item should be 'overdueEvent'.", "overdueEvent", allTasks.get(2).getName());
			assertEquals("Fourth item should be 'overdueTask'.", "overdueTask", allTasks.get(3).getName());
			assertEquals("Fifth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(4).getName());
			assertEquals("Sixth item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(5).getName());
			assertEquals("Seventh item should be 'todayEvent'.", "todayEvent", allTasks.get(6).getName());
			assertEquals("Eighth item should be 'todayTask'.", "todayTask", allTasks.get(7).getName());
			assertEquals("Fifth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(8).getName());
			assertEquals("Sixth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(9).getName());
			
			// checks that order is correct in each sublist
			ArrayList<Task> overdues = logic.getOverdueBuffer();
			assertEquals("There should be 2 items in the list.", 2, overdues.size());
			assertEquals("First item should be 'overdueEvent'.", "overdueEvent", overdues.get(0).getName());
			assertEquals("Second item should be 'overdueTask'.", "overdueTask", overdues.get(1).getName());
			
			ArrayList<Task> todayTasks = logic.getTodayBuffer();
			assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
			assertEquals("First item should be 'todayEvent'.", "todayEvent", todayTasks.get(0).getName());
			assertEquals("Second item should be 'todayTask'.", "todayTask", todayTasks.get(1).getName());
			
			ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
			assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
			assertEquals("First item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(0).getName());
			assertEquals("Second item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(1).getName());
			
			ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
			assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
			assertEquals("First item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(0).getName());
			assertEquals("Second item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(1).getName());
			
			ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
			assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
			assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());
			
			ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
			assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
			assertEquals("First item should be 'floatingTask'.", "floatingTask", floatingTasks.get(0).getName());
			
			ArrayList<Task> completed = logic.getCompletedBuffer();
			assertEquals("There should be NO items in the list.", 0, completed.size());
		} catch (Exception e) {
			fail("No exceptions should be encountered.");
		}
	}
	
	@Test
	public void sortNonEmptyListByDate() {
		try {
			makeNonEmptyFile(false);
			logic.executeCommand(sortByDate);
			
			// checks that inner memory does not face weird behavior
			ArrayList<Task> mem = logic.getInternalStorage();
			assertEquals("List should not experience a change in size.", 10, mem.size());
			
			// checks that combined tasks memory does not face weird behavior
			ArrayList<Task> allTasks = logic.getTemporarySortList();
			assertEquals("List should not experience a change in size.", 10, allTasks.size());
			
			// checks that order is correct in combined list of tasks
			assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
			assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
			assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
			assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
			assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
			assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
			assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
			assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
			assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());
			assertEquals("Tenth item should be 'floatingTask'.", "floatingTask", allTasks.get(9).getName());
			
			// checks that order is correct in each sublist
			ArrayList<Task> overdues = logic.getOverdueBuffer();
			assertEquals("There should be 2 items in the list.", 2, overdues.size());
			assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
			assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());
			
			ArrayList<Task> todayTasks = logic.getTodayBuffer();
			assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
			assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
			assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());
			
			ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
			assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
			assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
			assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());
			
			ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
			assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
			assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
			assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());
			
			ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
			assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
			assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());
			
			ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
			assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
			assertEquals("First item should be 'floatingTask'.", "floatingTask", floatingTasks.get(0).getName());
			
			ArrayList<Task> completed = logic.getCompletedBuffer();
			assertEquals("There should be NO items in the list.", 0, completed.size());
		} catch (Exception e) {
			fail("No exceptions should be encountered.");
		}
	}
	
	/**
	 * Creates a simple sample of varied tasks to comprehensively test the tasks-sorting. 
	 */
	public void createSortingTestFileState() {
		logic.executeCommand(generalTask1);
		logic.executeCommand(generalTask2);
		logic.executeCommand(generalTask3);
		logic.executeCommand(generalTask4);
		logic.executeCommand(generalTask5);
	}
	
	@Test
	public void testSortByName() throws ParseException {
		createSortingTestFileState();
		logic.executeCommand(sortByName);
		
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 5 tasks in the list.", 5, mem.size());
		
		Task first = mem.get(0);
		assertEquals("First task should be 'generalTask1'.", "generalTask1", first.getName());
		assertEquals("First task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), first.getEndDate());
		assertEquals("First task should have priority level 3.", 3, first.getPriority());
		
		Task second = mem.get(1);
		assertEquals("Second task should be 'generalTask1'.", "generalTask1", second.getName());
		assertEquals("Second task should be due on 3rd June.", sdformat2.parse("03/06/2016 20:00"), second.getEndDate());
		assertEquals("Second task should have priority level 3.", 3, second.getPriority());
		
		Task third = mem.get(2);
		assertEquals("Third task should be 'generalTask2'.", "generalTask2", third.getName());
		assertEquals("Third task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), third.getEndDate());
		assertEquals("Third task should have priority level 1.", 1, third.getPriority());
		
		Task fourth = mem.get(3);
		assertEquals("Fourth task should be 'generalTask3'.", "generalTask3", fourth.getName());
		assertEquals("Fourth task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), fourth.getEndDate());
		assertEquals("Fourth task should have priority level 4.", 4, fourth.getPriority());
		
		Task fifth = mem.get(4);
		assertEquals("Fifth task should be 'generalTask4'.", "generalTask4", fifth.getName());
		assertEquals("Fifth task should be due on 2nd June.", sdformat2.parse("02/06/2016 20:00"), fifth.getEndDate());
		assertEquals("Fifth task should have priority level 1.", 1, fifth.getPriority());
	}
	
	@Test
	public void testSortByDate() throws ParseException {
		createSortingTestFileState();
		logic.executeCommand(sortByDate);
		
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 5 tasks in the list.", 5, mem.size());
		
		Task first = mem.get(0);
		assertEquals("First task should be 'generalTask2'.", "generalTask2", first.getName());
		assertEquals("First task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), first.getEndDate());
		assertEquals("First task should have priority level 1.", 1, first.getPriority());
		
		Task second = mem.get(1);
		assertEquals("Second task should be 'generalTask1'.", "generalTask1", second.getName());
		assertEquals("Second task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), second.getEndDate());
		assertEquals("Second task should have priority level 3.", 3, second.getPriority());
		
		Task third = mem.get(2);
		assertEquals("Third task should be 'generalTask3'.", "generalTask3", third.getName());
		assertEquals("Third task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), third.getEndDate());
		assertEquals("Third task should have priority level 4.", 4, third.getPriority());
		
		Task fourth = mem.get(3);
		assertEquals("Fourth task should be 'generalTask4'.", "generalTask4", fourth.getName());
		assertEquals("Fourth task should be due on 2nd June.", sdformat2.parse("02/06/2016 20:00"), fourth.getEndDate());
		assertEquals("Fourth task should have priority level 1.", 1, fourth.getPriority());
		
		Task fifth = mem.get(4);
		assertEquals("Fifth task should be 'generalTask1'.", "generalTask1", fifth.getName());
		assertEquals("Fifth task should be due on 3rd June.", sdformat2.parse("03/06/2016 20:00"), fifth.getEndDate());
		assertEquals("Fifth task should have priority level 3.", 3, fifth.getPriority());
	}
	
	@Test
	public void testSortByPriority() throws ParseException {
		createSortingTestFileState();
		logic.executeCommand(sortByPriority);
		
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 5 tasks in the list.", 5, mem.size());
		
		Task first = mem.get(0);
		assertEquals("First task should be 'generalTask2'.", "generalTask2", first.getName());
		assertEquals("First task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), first.getEndDate());
		assertEquals("First task should have priority level 1.", 1, first.getPriority());
		
		Task second = mem.get(1);
		assertEquals("Second task should be 'generalTask4'.", "generalTask4", second.getName());
		assertEquals("Second task should be due on 2nd June.", sdformat2.parse("02/06/2016 20:00"), second.getEndDate());
		assertEquals("Second task should have priority level 1.", 1, second.getPriority());
		
		Task third = mem.get(2);
		assertEquals("Third task should be 'generalTask1'.", "generalTask1", third.getName());
		assertEquals("Third task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), third.getEndDate());
		assertEquals("Third task should have priority level 3.", 3, third.getPriority());
		
		Task fourth = mem.get(3);
		assertEquals("Fourth task should be 'generalTask1'.", "generalTask1", fourth.getName());
		assertEquals("Fourth task should be due on 3rd June.", sdformat2.parse("03/06/2016 20:00"), fourth.getEndDate());
		assertEquals("Fourth task should have priority level 3.", 3, fourth.getPriority());
		
		Task fifth = mem.get(4);
		assertEquals("Fifth task should be 'generalTask3'.", "generalTask3", fifth.getName());
		assertEquals("Fifth task should be due on 1st June.", sdformat2.parse("01/06/2016 20:00"), fifth.getEndDate());
		assertEquals("Fifth task should have priority level 4.", 4, fifth.getPriority());
	}
	
	
	/* ===================== TASK-SEARCHING TESTS ========================== */
	@Test
	public void searchForName() {
		makeNonEmptyFile(false);
		logic.executeCommand(searchForName);
		
		ArrayList<Task> results = logic.getSearchResults();
		assertEquals("There should only be 1 result.", 1, results.size());
		assertEquals("The result should only contain 'todayTask'.", "todayTask", results.get(0).getName());
	}
	
	@Test
	public void searchForNameNotFound() {
		makeNonEmptyFile(false);
		logic.executeCommand(searchForNameNotFound);
		
		ArrayList<Task> results = logic.getSearchResults();
		assertEquals("There should be no results for this search.", 0, results.size());
	}
	
	@Test
	public void searchBeforeDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(searchBeforeDate);
		
		ArrayList<Task> results = logic.getSearchResults();
		assertEquals("There should only be 4 results.", 4, results.size());
		assertEquals("First item in result should be 'overdueTask'.", "overdueTask", results.get(0).getName());
		assertEquals("Second item in result should be 'overdueEvent'.", "overdueEvent", results.get(1).getName());
		assertEquals("Third item in result should be 'todayTask'.", "todayTask", results.get(2).getName());
		assertEquals("Fourth item in result should be 'todayEvent'.", "todayEvent", results.get(3).getName());
	}
	
	@Test
	public void searchOnDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(searchOnDate);
		
		ArrayList<Task> results = logic.getSearchResults();
		assertEquals("There should only be 2 results.", 2, results.size());
		assertEquals("First item in result should be 'tomorrowTask'.", "tomorrowTask", results.get(0).getName());
		assertEquals("Second item in result should be 'tomorrowEvent'.", "tomorrowEvent", results.get(1).getName());
	}
	
	@Test
	public void searchAfterDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(searchAfterDate);
		
		ArrayList<Task> results = logic.getSearchResults();
		assertEquals("There should only be 3 results.", 3, results.size());
		assertEquals("First item in result should be 'thisWeekTask1'.", "thisWeekTask1", results.get(0).getName());
		assertEquals("Second item in result should be 'thisWeekEvent1'.", "thisWeekEvent1", results.get(1).getName());
		assertEquals("Third item in result should be 'generalTask'.", "generalTask", results.get(2).getName());
	}
	
	@Test
	public void searchInvalidDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(searchNotDate);
		
		ArrayList<Task> results = logic.getSearchResults();
		assertEquals("There should be no results.", 0, results.size());
	}
	
	/**
	 * This test case is expected to pass because Java <code>Date</code> class
	 * would interpret <code>30/02/2016</code> as <code>01/03/2016</code>.
	 * 
	 * However, it is up to Parser to guard against such weird dates or to handle
	 * them properly.
	 */
	@Test
	public void searchWeirdDate() {
		makeNonEmptyFile(false);
		logic.executeCommand(searchSpecialDate);
		
		ArrayList<Task> results = logic.getSearchResults();
		assertEquals("There should be 9 results.", 9, results.size());
		assertEquals("First item in result should be 'overdueTask'.", "overdueTask", results.get(0).getName());
		assertEquals("Second item in result should be 'overdueEvent'.", "overdueEvent", results.get(1).getName());
		assertEquals("Third item in result should be 'todayTask'.", "todayTask", results.get(2).getName());
		assertEquals("Fourth item in result should be 'todayEvent'.", "todayEvent", results.get(3).getName());
		assertEquals("Fifth item in result should be 'tomorrowTask'.", "tomorrowTask", results.get(4).getName());
		assertEquals("Sixth item in result should be 'tomorrowEvent'.", "tomorrowEvent", results.get(5).getName());
		assertEquals("Seventh item in result should be 'thisWeekTask1'.", "thisWeekTask1", results.get(6).getName());
		assertEquals("Eighth item in result should be 'thisWeekEvent1'.", "thisWeekEvent1", results.get(7).getName());
		assertEquals("Ninth item in result should be 'generalTask'.", "generalTask", results.get(8).getName());
	}
	
	
	/* ===================== SET SAVE PATH TESTS ========================== */
	@Test
	public void testSetSavePath() {
		makeNonEmptyFile(false);
		logic.executeCommand(setSavePath);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("List should not experience a change in size.", 10, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getTemporarySortList();
		assertEquals("List should not experience a change in size.", 10, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());
		assertEquals("Tenth item should be 'floatingTask'.", "floatingTask", allTasks.get(9).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
		assertEquals("First item should be 'floatingTask'.", "floatingTask", floatingTasks.get(0).getName());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	} 
	
	
	/* ===================== HELP TESTS ========================== */
	@Test
	public void testHelp() {
		makeNonEmptyFile(false);
		logic.executeCommand(help);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("List should not experience a change in size.", 10, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getAllTasks();
		assertEquals("List should not experience a change in size.", 10, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());
		assertEquals("Tenth item should be 'floatingTask'.", "floatingTask", allTasks.get(9).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
		assertEquals("First item should be 'floatingTask'.", "floatingTask", floatingTasks.get(0).getName());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	/* ===================== CORRUPT COMMAND TESTS ========================== */
	/**
	 * This test is expected to throw NullPointerException because the CommandKey enum
	 * retrieved from this corrupted Command object will be <code>null</code>.
	 */
	@Test(expected=NullPointerException.class)
	public void testCorruptCommand() {
		logic.executeCommand(corruptCommand);
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 0, mem.size());
	}
	
	
	/* ===================== UNDOING TESTS ========================== */
	@Test
	public void undoOnEmptyInitialState() {
		logic.executeCommand(undo);
		
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be no change in list size.", 0, mem.size());
	}
	
	@Test
	public void undoAddTask() {
		logic.executeCommand(overdueTask);
		logic.executeCommand(undo);
		
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be NO change in list size.", 0, mem.size());
		
		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be NO items in the list.", 0, overdues.size());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be NO items in the list.", 0, todayTasks.size());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be NO items in the list.", 0, tomorrowTasks.size());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be NO items in the list.", 0, thisWeekTasks.size());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be NO item in the list.", 0, remainingTasks.size());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO item in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@Test
	public void undoDeleteTask() {
		logic.executeCommand(overdueTask);
		logic.executeCommand(deleteOverdueTask);
		logic.executeCommand(undo);
		
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 1 item in list size.", 1, mem.size());
		
		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 1 item in the list.", 1, overdues.size());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be NO items in the list.", 0, todayTasks.size());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be NO items in the list.", 0, tomorrowTasks.size());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be NO items in the list.", 0, thisWeekTasks.size());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be NO item in the list.", 0, remainingTasks.size());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO item in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@Test
	public void undoUpdateTask() {
		logic.executeCommand(todayTask);
		logic.executeCommand(changeTodayTaskName);
		logic.executeCommand(undo);
		
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 1 item in list size.", 1, mem.size());
		
		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be NO items in the list.", 0, overdues.size());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 1 item in the list.", 1, todayTasks.size());
		assertEquals("The item should have name 'todayTask'.", "todayTask", todayTasks.get(0).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be NO items in the list.", 0, tomorrowTasks.size());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be NO items in the list.", 0, thisWeekTasks.size());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be NO item in the list.", 0, remainingTasks.size());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO item in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@Test
	public void undoCompleteTask() {
		logic.executeCommand(todayTask);
		logic.executeCommand(completeTodayTask);
		logic.executeCommand(undo);
		
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 1 item in list size.", 1, mem.size());
		
		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be NO items in the list.", 0, overdues.size());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 1 item in the list.", 1, todayTasks.size());
		assertEquals("'todayTask' should be uncompleted.", false, todayTasks.get(0).isCompleted());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be NO items in the list.", 0, tomorrowTasks.size());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be NO items in the list.", 0, thisWeekTasks.size());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be NO item in the list.", 0, remainingTasks.size());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO item in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@Test
	public void undoSortTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(sortByName);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("List should not experience a change in size.", 10, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getTemporarySortList();
		assertEquals("List should not experience a change in size.", 10, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());
		assertEquals("Tenth item should be 'floatingTask'.", "floatingTask", allTasks.get(9).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
		assertEquals("First item should be 'floatingTask'.", "floatingTask", floatingTasks.get(0).getName());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@Test
	public void undoMultipleSortTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(sortByName);
		logic.executeCommand(sortByDate);
		logic.executeCommand(undo);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("List should not experience a change in size.", 10, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getTemporarySortList();
		assertEquals("List should not experience a change in size.", 10, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());
		assertEquals("Tenth item should be 'floatingTask'.", "floatingTask", allTasks.get(9).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
		assertEquals("First item should be 'floatingTask'.", "floatingTask", floatingTasks.get(0).getName());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	/**
	 * Search command does not trigger storing of previous program state.
	 * Therefore, undoSearch will undo the previous successful operation.
	 */
	@Test
	public void undoSearchTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(searchForName);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getAllTasks();
		assertEquals("List should not experience a change in size.", 9, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		// last successful operation was adding 'floatingTask' into list
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO items in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@Test
	public void undoSetSavePath() {
		makeNonEmptyFile(false);
		logic.executeCommand(setSavePath);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("List should not experience a change in size.", 10, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getAllTasks();
		assertEquals("List should not experience a change in size.", 10, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());
		assertEquals("Tenth item should be 'floatingTask'.", "floatingTask", allTasks.get(9).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be 1 item in the list.", 1, floatingTasks.size());
		assertEquals("First item should be 'floatingTask'.", "floatingTask", floatingTasks.get(0).getName());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	/*
	 * Undo after a failed operation should undo the last successful operation. 
	 */
	@Test
	public void undoFailedAddTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(dateErrorTask);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getAllTasks();
		assertEquals("List should not experience a change in size.", 9, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		// last successful operation was adding 'floatingTask' into list
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO items in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	/*
	 * Undo after a failed operation should undo the last successful operation. 
	 */
	@Test
	public void undoFailedDeleteTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(deleteNotFound);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getAllTasks();
		assertEquals("List should not experience a change in size.", 9, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		// last successful operation was adding 'floatingTask' into list
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO items in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	/*
	 * Undo after a failed operation should undo the last successful operation. 
	 */
	@Test
	public void undoFailedUpdateTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(changeNotFound);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getAllTasks();
		assertEquals("List should not experience a change in size.", 9, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		// last successful operation was adding 'floatingTask' into list
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO items in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	/*
	 * Undo after a failed operation should undo the last successful operation. 
	 */
	@Test
	public void undoFailedCompleteTask() {
		makeNonEmptyFile(false);
		logic.executeCommand(completeNotFound);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getAllTasks();
		assertEquals("List should not experience a change in size.", 9, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		// last successful operation was adding 'floatingTask' into list
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO items in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	/*
	 * 'help' command does not trigger storing of previous program state.
	 * Therefore, undoing 'help' will result in last successful operation being undone. 
	 */
	@Test
	public void undoHelp() {
		makeNonEmptyFile(false);
		logic.executeCommand(help);
		logic.executeCommand(undo);
		
		// checks that inner memory does not face weird behavior
		ArrayList<Task> mem = logic.getInternalStorage();
		assertEquals("There should be 9 items in the list.", 9, mem.size());

		// checks that combined tasks memory does not face weird behavior
		ArrayList<Task> allTasks = logic.getAllTasks();
		assertEquals("List should not experience a change in size.", 9, allTasks.size());

		// checks that order is correct in combined list of tasks
		assertEquals("First item should be 'overdueTask'.", "overdueTask", allTasks.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", allTasks.get(1).getName());
		assertEquals("Third item should be 'todayTask'.", "todayTask", allTasks.get(2).getName());
		assertEquals("Fourth item should be 'todayEvent'.", "todayEvent", allTasks.get(3).getName());
		assertEquals("Fifth item should be 'tomorrowTask'.", "tomorrowTask", allTasks.get(4).getName());
		assertEquals("Sixth item should be 'tomorrowEvent'.", "tomorrowEvent", allTasks.get(5).getName());
		assertEquals("Seventh item should be 'thisWeekTask1'.", "thisWeekTask1", allTasks.get(6).getName());
		assertEquals("Eighth item should be 'thisWeekEvent1'.", "thisWeekEvent1", allTasks.get(7).getName());
		assertEquals("Ninth item should be 'generalTask'.", "generalTask", allTasks.get(8).getName());

		// checks that order is correct in each sublist
		ArrayList<Task> overdues = logic.getOverdueBuffer();
		assertEquals("There should be 2 items in the list.", 2, overdues.size());
		assertEquals("First item should be 'overdueTask'.", "overdueTask", overdues.get(0).getName());
		assertEquals("Second item should be 'overdueEvent'.", "overdueEvent", overdues.get(1).getName());

		ArrayList<Task> todayTasks = logic.getTodayBuffer();
		assertEquals("There should be 2 items in the list.", 2, todayTasks.size());
		assertEquals("First item should be 'todayTask'.", "todayTask", todayTasks.get(0).getName());
		assertEquals("Second item should be 'todayEvent'.", "todayEvent", todayTasks.get(1).getName());

		ArrayList<Task> tomorrowTasks = logic.getTomorrowBuffer();
		assertEquals("There should be 2 items in the list.", 2, tomorrowTasks.size());
		assertEquals("First item should be 'tomorrowTask'.", "tomorrowTask", tomorrowTasks.get(0).getName());
		assertEquals("Second item should be 'tomorrowEvent'.", "tomorrowEvent", tomorrowTasks.get(1).getName());

		ArrayList<Task> thisWeekTasks = logic.getThisWeekBuffer();
		assertEquals("There should be 2 items in the list.", 2, thisWeekTasks.size());
		assertEquals("First item should be 'thisWeekTask1'.", "thisWeekTask1", thisWeekTasks.get(0).getName());
		assertEquals("Second item should be 'thisWeekEvent1'.", "thisWeekEvent1", thisWeekTasks.get(1).getName());

		ArrayList<Task> remainingTasks = logic.getRemainingBuffer();
		assertEquals("There should be 1 item in the list.", 1, remainingTasks.size());
		assertEquals("First item should be 'generalTask'.", "generalTask", remainingTasks.get(0).getName());

		// last successful operation was adding 'floatingTask' into list
		ArrayList<Task> floatingTasks = logic.getFloatingBuffer();
		assertEquals("There should be NO items in the list.", 0, floatingTasks.size());

		ArrayList<Task> completed = logic.getCompletedBuffer();
		assertEquals("There should be NO items in the list.", 0, completed.size());
	}
	
	@After
	public void reset() {
		undo = null;
		overdueTask = null;
		overdueEvent = null;
		todayTask = null;
		todayEvent = null;
		tomorrowTask = null;
		tomorrowEvent = null;
		thisWeekTask1 = null;
		thisWeekEvent1 = null;
		generalTask = null;
		floatingTask = null;
		tomorrowTask2 = null;
		dateErrorTask = null;
		dateErrorTask2 = null;
		deleteOverdueTask = null;
		deleteTodayTask = null;
		deleteTomorrowTask = null;
		deleteThisWeekTask1 = null;
		deleteFloating = null;
		deleteId = null;
		deleteNotFound = null;
		deleteSameName = null;
		deleteInvalidId = null;
		completeOverdueTask = null;
		completeTodayTask = null;
		completeTomorrowTask = null;
		completeThisWeekTask = null;
		completeGeneralTask = null;
		completeId = null;
		completeFloatingTask = null;
		completeSameName = null;
		completeInvalidId = null;
		sortByName = null;
		sortByDate = null;
		sortByStartDate = null;
		sortByEndDate = null;
		sortByPriority = null;
		changeTodayTaskName = null;
		changeTodayTaskDate1 = null;
		changeTodayTaskDate2 = null;
		changeTodayTaskDate3 = null;
		changeTodayTaskDate4 = null;
		changeTodayTaskDateError = null;
		changeTodayTaskDateError2 = null;
		changeNotFound = null;
		changeSameName = null;
		changeInvalidId = null;
		changeTodayEventDate1Start = null;
		changeTodayEventDate2Start = null;
		changeTodayEventDate3Start = null;
		changeTodayEventDate4Start = null;
		changeTodayEventDate2End = null;
		changeTodayEventDate3End = null;
		changeTodayEventDate4End = null;
		changeTodayEventDateError = null;
		changeTodayEventDateError2 = null;
		changeTodayEventDateError3 = null;
		changeTodayTaskPriority = null;
		changeTodayEventPriority = null;
		changeTodayTaskPriorityError = null;
		changeTodayTaskPriorityError2 = null;
		setSavePath = null;
		help = null;
		corruptCommand = null;
		parameters.clear();
		logic.flushInternalStorage();
	}
	
}
