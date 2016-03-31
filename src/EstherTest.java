import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.ws.handler.LogicalMessageContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.validator.PublicClassValidator;

import cs2103_w09_1j.esther.Task;

/**
 * 
 * @author Jeremy Hon
 * @@author A0127572A
 *
 */
public class EstherTest {

	private String pathString = "esther.txt";
	private String taskName = "task";

	private Path saveLoc = Paths.get(pathString);

	private String[] dateFormats = { "", "dd/MM/yy", "dd/MM/yyyy", "d/M/yy", "d/MM/yy", "dd/M/yy", "E" };
	private String[] timeFormats = { "", "HHmm", "HH:mm", "hha", "hhmma" };

	private ArrayList<DateTimeTester> todayTestFormats;
	private ArrayList<DateTimeTester> todayOneHourTestFormats;
	private ArrayList<DateTimeTester> tmwTestFormats;
	private ArrayList<DateTimeTester> tmwOneHrTestFormats;

	private Date now = new Date();
	private Date nowOneHr = new Date(now.getTime() + (60 * 60 * 1000));
	private Date tmwOneHr = new Date(now.getTime() + (25 * 60 * 60 * 1000));
	private Date tmwTwoHr = new Date(tmwOneHr.getTime() + (60 * 60 * 1000));

	private DateTimeTester defaultTester = new DateTimeTester(now, dateFormats[1], timeFormats[1]);
	private DateTimeTester default1HTester = new DateTimeTester(nowOneHr, dateFormats[1], timeFormats[1]);

	private final boolean DEBUG = false;
	private final boolean EXHAUSTIVE = false;

	private Logic logic;

	@Before
	public void init() throws ParseException, IOException {
		logic = new Logic();
		cleanUp();
		generateTesterLists();
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
					if (dateTimeTester.isHasReverse()) {
						addCommand = "add task on " + dateTimeTester.getTDString();
					} else {
						continue;
					}
				} else {
					// has 1 string anyway
					addCommand = "add task on " + dateTimeTester.getTDString();
				}
				String result = logic.executeCommand(addCommand);
				if (!result.contains("success")) {
					System.out.println("Add test failed");
					System.out.println(addCommand);
					System.out.println(result);
					fail();
				} else {
					assertTrue(verifyEndDate(dateTimeTester));
				}
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
					if (!result.contains("success")) {
						System.out.println("Add test failed on iteration " + index);
						System.out.println(addCommand);
						System.out.println(result);
						fail();
					} else {
						assertTrue(verifyStartDate(tester));
						assertTrue(verifyEndDate(tester1H));
					}
				}
			}
		}
	}
	
	@Test
	public void addEventFail() {
		failCommand("add task from "+default1HTester.getDTString()+" to "+defaultTester.getDTString());
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
		tryCommand("add deltask");
		tryCommand("delete deltask");
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
		int tasks = logic.getInternalStorage().size();
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
		Task.setGlobalId(0);
		tryAddTaskWithDeadline();
		for (DateTimeTester tester : tmwTestFormats) {
			tryCommand("update 0 date to " + tester.getDTString());
			assertTrue(verifyEndDate(tester));
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
		tryAddTask();
		for (DateTimeTester tester : todayTestFormats) {
			tryCommand("update 0 date to " + tester.getDTString());
			tryCommand("update 0 time to " + tester.getTDString());
			assertTrue(verifyEndDate(tester));
		}
	}

	@Test
	public void updateTskToEvtExhaustive() {
		Task.setGlobalId(0);
		tryAddTaskWithDeadline();
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

	// TODO: For GQ all search test cases
	// @Test
	public void searchFor() {
		tryAddTask();
		String searchResult = logic.executeCommand("search for " + taskName);
	}

	// @Test
	public void searchFail() {
		// without using for, on, before, after keywords
	}

	// @Test
	public void searchFailFor() {
		// search for name that doesn't exist
	}

	// @Test
	public void searchOn() {
		// use the on keyword
	}

	// @Test
	public void searchOnFail() {
		// search for task with date that does not exist
	}

	// @Test
	public void searchBefore() {
		// use the before keyword
	}

	// @Test
	public void searchAfterDate() {
		// use the after keyword in a fail test case
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
		assertTrue(verifyName("btask"));
	}

	@Test
	public void sortDateTest() {
		tryCommand("add task on " + default1HTester.getDTString());
		tryCommand("add task2 on " + defaultTester.getDTString());
		tryCommand("sort by date");
		assertTrue(verifyName("task"));
	}

	@Test
	public void sortPriorityTest() {
		Task.setGlobalId(0);
		tryAddTask();
		tryAddTask();
		tryCommand("update 0 pr to 4");
		tryCommand("update 1 pr to 3");
		tryCommand("sort by priority");
		assertTrue(verifyPriority(4));
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
		assertTrue(verifyName("atask"));
	}

	@After
	public void cleanUp() {
		if (DEBUG) {
			System.out.println("Contents in esther.txt:");
			System.out.println("-----------------------");
			BufferedReader reader;
			try {
				reader = Files.newBufferedReader(saveLoc);
				while (reader.ready()) {
					System.out.println((reader.readLine()));
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		deleteFile();
	}

	/**
	 * 
	 * @param command
	 */
	private void tryCommand(String command) {
		String result = logic.executeCommand(command);
		boolean assertResult = result.contains("success");
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
		tryCommand("add task from " + defaultTester.getDTString() + " to " + default1HTester.getDTString());
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

	private Date getNowWithoutSeconds() {
		Date now = setSecondsToZero(new Date());
		return now;
	}

	private Date setSecondsToZero(Date date) {
		return new Date(date.getTime() / 60000 * 60000);
	}

	private Date setMinutesToZero(Date date) {
		return new Date(date.getTime() / (60 * 60000) * (60 * 60000));
	}

	private Date setHoursToZero(Date date) {
		return new Date(date.getTime() / (60 * 60 * 60000) * (60 * 60 * 60000));
	}

	private boolean verifyStartDate(DateTimeTester dateTimeTester) {
		Date date = getLastElement(logic.getInternalStorage()).getStartDate();
		return verifyDate(dateTimeTester, date);
	}

	private boolean verifyEndDate(DateTimeTester dateTimeTester) {
		Date date = getLastElement(logic.getInternalStorage()).getEndDate();
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
		return getLastElement(logic.getInternalStorage()).getName().equals(name);
	}

	private boolean verifyPriority(int priority) {
		return getLastElement(logic.getInternalStorage()).getPriority() == priority;
	}

	private boolean verifyComplete() {
		return getLastElement(logic.getInternalStorage()).isCompleted();
	}

	private <E> E getLastElement(ArrayList<E> list) {
		return list.get(list.size() - 1);
	}

	private void deleteFile() {
		try {
			if (Files.exists(saveLoc)) {
				Files.delete(saveLoc);
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
