import static org.junit.Assert.*;

import java.awt.Event;
import java.io.IOException;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103_w09_1j.esther.Task;
import cs2103_w09_1j.esther.UIResult;

/**
 * 
 * @author Jeremy Hon
 * @@author A0127572A
 *
 */
public class EstherTest {

	private String taskName = "task";
	private String pathString = "esther.txt";
	private String cfgPathString = "estherconfig.txt";

	private Path cfgLoc = Paths.get(cfgPathString);
	private Path saveLoc = Paths.get(pathString);

	private String[] dateFormats = { "", "dd/MM/yy", "dd/MM/yyyy", "d/M/yy", "d/MM/yy", "dd/M/yy", "E" };
	private String[] timeFormats = { "", "HHmm", "HH:mm", "hha", "hhmma" };

	private ArrayList<DateTimeTester> todayTestFormats;
	private ArrayList<DateTimeTester> todayOneHourTestFormats;
	private ArrayList<DateTimeTester> tmwTestFormats;
	private ArrayList<DateTimeTester> tmwOneHrTestFormats;

	private Date nw = new Date();
	private Date now = new Date(nw.getTime() + (5*60*1000));
	private Date nowOneHr = new Date(now.getTime() + (60 * 60 * 1000));
	private Date tmwOneHr = new Date(now.getTime() + (25 * 60 * 60 * 1000));
	private Date tmwTwoHr = new Date(tmwOneHr.getTime() + (60 * 60 * 1000));

	private DateTimeTester defaultTester = new DateTimeTester(now, dateFormats[1], timeFormats[1]);
	private DateTimeTester default1HTester = new DateTimeTester(nowOneHr, dateFormats[1], timeFormats[1]);

	private boolean setupDone = false;

	private Logic logic;

	private final boolean DEBUG = false;
	private final boolean EXHAUSTIVE = false;

	@Before
	public void init() throws ParseException, IOException {
		if (!setupDone) {
			logic = new Logic();
			cleanUp();
			generateTesterLists();
			setupDone = true;
		}
	}

	/**
	 * 
	 * @@A0127572A
	 */
	private void generateTesterLists() {
		todayTestFormats = generateDateTimes(now);
		todayOneHourTestFormats = generateDateTimes(nowOneHr);
		tmwTestFormats = generateDateTimes(tmwOneHr);
		tmwOneHrTestFormats = generateDateTimes(tmwTwoHr);
	}

	@Test
	public void failCommand() {
		failCommand("blah");
	}

	@Test
	public void addFloatingTest() {
		tryAddTask();
	}

	@Test
	public void addTaskTest() {
		tryAddTaskWithDeadline();
		assertTrue(verifyEndDate(defaultTester));
	}

	@Test
	public void addTaskDetailed() {
		String addCommand;
		for (DateTimeTester dateTimeTester : todayTestFormats) {
			for (int i = 0; i < 2; i++) {
				// tester obj has 1 or 2 strings
				if (i == 1) {
					// has 2 strings
					if (dateTimeTester.hasReverse()) {
						addCommand = "add task on " + dateTimeTester.getTDString();
					} else {
						continue;
					}
				} else {
					// has 1 string
					addCommand = "add task on " + dateTimeTester.getTDString();
				}
				String result = logic.executeCommand(addCommand);
				assertTrue(verifyEndDate(dateTimeTester));
			}
		}
	}

	@Test
	public void addTaskFail() {
		failCommand("add task on");
		failCommand("add task from to");
	}

	@Test
	public void addEventTest() {
		tryAddEvent();
		assertTrue(verifyStartDate(defaultTester));
		assertTrue(verifyEndDate(default1HTester));
	}

	@Test
	public void addEventExhaustive() {
		if (EXHAUSTIVE) {
			int index = 0;
			for (DateTimeTester tester : todayTestFormats) {
				for (DateTimeTester tester1H : todayOneHourTestFormats) {
					index++;
					String addCommand = ("add task from " + tester.getDTString() + " to " + tester1H.getDTString());
					String result = logic.executeCommand(addCommand);
					assertTrue(verifyStartDate(tester));
					assertTrue(verifyEndDate(tester1H));

				}
			}
		}
	}

	@Test
	public void addEventFail() {
		failCommand("add task from " + default1HTester.getDTString() + " to " + defaultTester.getDTString());
	}

	@Test
	public void addWithKeyword() {
		tryCommand("add \"task from to on \"");
		tryCommand("add \"task from to on \" on " + defaultTester.getDTString());
		tryCommand("add \"task from to on \" from " + defaultTester.getDTString() + " to "
				+ default1HTester.getDTString());
	}

	@Test
	public void addDuplicate() {
		tryAddTask();
		tryAddTask();
	}

	@Test
	public void deleteNameTest() {
		// equivalence partition for delete based on name
		int tasks = logic.getInternalStorage().size();
		tryAddTask();
		tryCommand("delete task");
		assertEquals(tasks, logic.getInternalStorage().size());
	}

	@Test
	public void deleteIDTest() {
		// equivalence partition for delete based on id
		Task.setGlobalId(0);
		int tasks = logic.getInternalStorage().size();
		tryCommand("add deltask");
		tryCommand("delete 0");
		assertEquals(tasks, logic.getInternalStorage().size());
	}

	@Test
	public void deleteFail() {
		Task.setGlobalId(0);
		tryAddTask();
		failCommand("delete task2");
		failCommand("delete 1");
	}

	@Test
	public void deleteDuplicate() {
		int tasks = logic.getInternalStorage().size();
		tryAddTask();
		tryAddTask();
		failCommand("delete task");
		assertEquals(tasks + 2, logic.getInternalStorage().size());
	}

	@Test
	public void updateNameByNameTest() {
		// equivalence partition for updating different fields based on name
		// reference
		tryCommand("add updtask");
		tryCommand("update updtask name to updatedTask");
		assertTrue(verifyName("updatedTask"));
	}

	@Test
	public void updateNameFail() {
		tryAddTask();
		failCommand("update task2 name to task");
	}

	@Test
	public void updateDupNameTest() {
		Task.setGlobalId(0);
		for (int i = 0; i < 2; i++) {
			tryAddTask();
		}
		failCommand("update task name to updatedTask");
		tryCommand("update 1 name to updatedTask");
		assertTrue(verifyName("updatedTask"));
	}

	@Test
	public void updateDateByNameTest() {
		// equivalence partition for updating different fields based on name
		// reference
		tryAddTaskWithDeadline();
		tryCommand("update task date to " + default1HTester.getDTString());
		assertTrue(verifyEndDate(default1HTester));
	}

	@Test
	public void updateTimeByNameTest() {
		// equivalence partition for updating different fields based on name
		// reference
		tryAddTaskWithDeadline();
		tryCommand("update task time to " + default1HTester.getDTString());
		assertTrue(verifyEndDate(default1HTester));
	}

	@Test
	public void updatePriorityByNameTest() {
		tryAddTask();
		tryCommand("update task pr to 1");
		assertTrue(verifyPriority(1));
	}

	@Test
	public void updateFloatToDeadline() {
		tryAddTask();
		tryCommand("update task date to " + defaultTester.getDTString());
		assertTrue(verifyEndDate(defaultTester));
	}

	@Test
	public void updateFloatToEvent() {
		tryAddTask();
		tryCommand("update task endtime to " + default1HTester.getDTString());
		tryCommand("update task sDate to " + defaultTester.getDTString());
		assertTrue(verifyEndDate(default1HTester));
		assertTrue(verifyStartDate(defaultTester));
	}

	@Test
	public void updateDeadlineToEvent() {
		tryCommand("add task on " + default1HTester.getDTString());
		tryCommand("update task stime to " + defaultTester.getDTString());
		assertTrue(verifyStartDate(defaultTester));
	}

	@Test
	public void updateByIDTest() {
		// equivalence partition for updating different fields based on ID
		// reference
		Task.setGlobalId(0);
		tryAddTask();
		tryCommand("update 0 name to task2");
		assertTrue(verifyName("task2"));
	}

	@Test
	public void updateTskDateExhaustive() {
		if (EXHAUSTIVE) {
			Task.setGlobalId(0);
			tryAddTaskWithDeadline();
			for (DateTimeTester tester : tmwTestFormats) {
				tryCommand("update 0 date to " + tester.getDTString());
				assertTrue(verifyEndDate(tester));
			}
		}
	}

	@Test
	public void updateEvtDateExhaustive() {
		if (EXHAUSTIVE) {
			Task.setGlobalId(0);
			tryAddEvent();
			for (DateTimeTester tester : tmwTestFormats) {
				for (DateTimeTester laterTester : tmwOneHrTestFormats) {
					tryCommand("update 0 date to " + laterTester.getDTString());
					tryCommand("update 0 st to " + tester.getTDString());
					assertTrue(verifyEndDate(laterTester));
					assertTrue(verifyStartDate(tester));
				}
			}
		}
	}

	@Test
	public void updateFltToTskExhaustive() {
		Task.setGlobalId(0);
		String dt = "";
		tryAddTask();
		for (DateTimeTester tester : todayTestFormats) {
			dt = tester.getDTString();
			tryCommand("update 0 date to " + tester.getDTString());
			assertTrue(verifyEndDate(tester));
		}
	}

	@Test
	public void updateTskToEvtExhaustive() {
		if (EXHAUSTIVE) {
			Task.setGlobalId(0);
			tryAddTaskWithDeadline();
			for (DateTimeTester tester : tmwTestFormats) {
				for (DateTimeTester laterTester : tmwOneHrTestFormats) {
					tryCommand("update 0 date to " + laterTester.getDTString());
					tryCommand("update 0 sd to " + tester.getDTString());
					assertTrue(verifyEndDate(laterTester));
					assertTrue(verifyStartDate(tester));
				}
			}
		}
	}

	@Test
	public void updateFltToEvtExhaustive() {
		if (EXHAUSTIVE) {
			Task.setGlobalId(0);
			tryAddTask();
			for (DateTimeTester tester : tmwTestFormats) {
				for (DateTimeTester laterTester : tmwOneHrTestFormats) {
					tryCommand("update 0 date to " + laterTester.getDTString());
					if (laterTester.hasDate()) {
						tryCommand("update 0 sd to " + tester.getDTString());
					} else {
						tryCommand("update 0 st to " + tester.getTString());
					}
					assertTrue(verifyEndDate(laterTester));
					assertTrue(verifyStartDate(tester));
				}
			}
		}
	}

	@Test
	public void completeTest() {
		tryAddTask();
		tryCommand("complete task");
	}

	@Test
	public void completeByIDTest() {
		Task.setGlobalId(0);
		tryAddTask();
		tryCommand("complete 0");
	}

	@Test
	public void completeDupTest() {
		Task.setGlobalId(0);
		tryAddTask();
		tryAddTask();
		tryCommand("complete 0");
		failCommand("complete task");
	}

	@Test
	public void searchFor() {
		tryAddTask();
		tryCommand("search for task");
	}

	@Test
	public void searchFail() {
		failCommand("search fail");
		// without using for, on, before, after keywords
	}

	@Test
	public void searchFailFor() {
		tryAddEvent();
		failCommand("search for ");
		// search for name that doesn't exist
	}

	@Test
	public void searchOn() {
		tryAddEvent();
		// use the on keyword
		tryCommand("search on today");
	}

	@Test
	public void searchOnFail() {
		tryAddTask();
		failCommand("search on today");
	}

	@Test
	public void searchBefore() {
		// use the before keyword
		tryAddTaskWithDeadline();
		tryCommand("search before tmw");
	}

	@Test
	public void searchBeforeFail() {
		// use the before keyword to search for task that doesn't exist
		tryAddTaskWithDeadline();
		failCommand("search before today");
	}

	@Test
	public void searchAfter() {
		// use the after keyword
		tryCommand("add task on tmw");
		tryCommand("search after today");
	}

	@Test
	public void searchAfterFail() {
		// use the after keyword in a fail test case
		tryCommand("add task on today");
		failCommand("search after today");
	}

	@Test
	public void sortFail() {
		failCommand("sort by blah");
	}

	@Test
	public void sortNameTest() {
		tryCommand("add btask");
		tryCommand("add atask");
		tryCommand("sort by name");
		assertTrue(getUIRes().getBuffer(getUIRes().ALL_INDEX).get(0).getName().equals("atask"));
	}

	@Test
	public void sortDateTest() {
		tryCommand("add task on " + default1HTester.getDTString());
		tryCommand("add task2 on " + defaultTester.getDTString());
		tryCommand("sort by date");
		assertTrue(getUIRes().getBuffer(getUIRes().ALL_INDEX).get(0).getName().equals("task2"));
	}

	@Test
	public void sortPriorityTest() {
		Task.setGlobalId(0);
		tryAddTask();
		tryAddTask();
		tryCommand("update 0 pr to 4");
		tryCommand("update 1 pr to 3");
		tryCommand("sort by priority");
		assertTrue(getUIRes().getBuffer(getUIRes().FLOATING_INDEX).get(0).getPriority() == 3);
	}

	@Test
	public void undoAdd() {
		tryAddTask();
		tryAddTask();
		tryCommand("undo");
		assertTrue(logic.getInternalStorage().size() == 1);
	}

	@Test
	public void undoUpdate() {
		tryAddTask();
		tryCommand("update task name to task2");
		tryCommand("undo");
		assertTrue(verifyName("task"));
	}

	@Test
	public void undoDelete() {
		tryAddTask();
		tryCommand("delete task");
		tryCommand("undo");
		assertTrue(logic.getInternalStorage().size() == 1);
	}

	@Test
	public void undoComplete() {
		tryAddTask();
		tryCommand("complete task");
		tryCommand("undo");
		assertFalse(verifyComplete());
	}

	@Test
	public void undoSort() {
		tryCommand("add btask");
		tryCommand("add atask");
		tryCommand("sort by name");
		tryCommand("undo");
		assertTrue(getUIRes().getBuffer(getUIRes().FLOATING_INDEX).get(0).getName().equals("btask"));
	}

	@Test
	public void setTest() {
		tryCommand("set esther2.txt");
	}

	@Test
	public void setAbsolute() {
		tryCommand("set C://Users/esther.txt");
	}

	@Test
	public void setFail() {
		failCommand("set blah");
	}

	@After
	public void cleanUp() {
		logic.flushInternalStorage();
		deleteFile();
	}

	private UIResult getUIRes() {
		return UiMainController.getRes();
	}

	/**
	 * 
	 * @param command
	 */
	private void tryCommand(String command) {
		String result = logic.executeCommand(command);
		boolean assertResult = result.contains("success") || result.contains("Success");
		if (!assertResult) {
			System.out.println("\"" + command + "\" failed.");
			System.out.println(result);
		}
		assertTrue(assertResult);
	}

	private void tryAddTask() {
		tryCommand("add task");
	}

	/**
	 * 
	 * @@A0127572A
	 */
	private void tryAddTaskWithDeadline() {
		tryCommand("add task on " + defaultTester.getDTString());
	}

	/**
	 * 
	 * @@A0127572A
	 */
	private void tryAddEvent() {
		tryCommand("add " + taskName + " from " + defaultTester.getDTString() + " to " + default1HTester.getDTString());
	}

	private void failCommand(String command) {
		String result = logic.executeCommand(command);
		boolean assertResult = result.contains("success");
		if (assertResult) {
			System.out.println("\"" + command + "\" succeeded where it should have failed.");
			System.out.println(result);
		}
		assertFalse(assertResult);
	}

	private boolean verifyStartDate(DateTimeTester dateTimeTester) {
		Date date = getLastModifiedTask().getStartDate();
		return verifyDate(dateTimeTester, date);
	}

	private boolean verifyEndDate(DateTimeTester dateTimeTester) {
		Date date = getLastModifiedTask().getEndDate();
		return verifyDate(dateTimeTester, date);
	}

	private boolean verifyDate(DateTimeTester dateTimeTester, Date date) {
		if (!date.equals(dateTimeTester.getDate())) {
			System.out.println("Verification of task failed.");
			System.out.println("Expected: " + dateTimeTester.getDate().toString());
			System.out.println("Actual: " + date.toString());
			System.out.println("Date time given: " + dateTimeTester.getDTString());
			return false;
		} else {
			return true;
		}
	}

	private boolean verifyName(String name) {
		return getLastModifiedTask().getName().equals(name);
	}

	private boolean verifyPriority(int priority) {
		return getLastModifiedTask().getPriority() == priority;
	}

	private boolean verifyComplete() {
		return getLastModifiedTask().isCompleted();
	}

	private Task getLastModifiedTask() {
		return getUIRes().getModifiedTask();
	}

	private Task getLastTaskInBuffer(int whichBuffer) {
		if (whichBuffer < 0 || whichBuffer > getUIRes().NUM_BUFFERS - 1) {
			return getUIRes().getTask(whichBuffer, getUIRes().getBuffer(whichBuffer).size() - 1);
		}
		System.out.println("Fail buffer: " + whichBuffer);
		return null;
	}

	private void deleteFile() {
		try {
			if (Files.exists(saveLoc)) {
				Files.delete(saveLoc);
			}
			if (Files.exists(cfgLoc)) {
				Files.delete(cfgLoc);
			}
		} catch (IOException e) {

		}
	}

	private ArrayList<DateTimeTester> generateDateTimes(Date date) {
		String dateFormat, timeFormat, dateTimeFormat, dateTimeFormattedString;
		Date today = date;
		DateTimeTester testerObj;
		ArrayList<DateTimeTester> testerObjs = new ArrayList<>();
		for (int i = 0; i < dateFormats.length; i++) {
			for (int j = 0; j < timeFormats.length; j++) {
				dateFormat = dateFormats[i];
				timeFormat = timeFormats[j];
				if (dateFormat.length() != 0 && timeFormat.length() != 0) {
					testerObj = new DateTimeTester(date, dateFormat, timeFormat);
					testerObjs.add(testerObj);
				}
			}
		}
		return testerObjs;
	}
}
