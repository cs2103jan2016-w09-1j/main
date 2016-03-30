import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
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

/**
 * 
 * @author Jeremy Hon
 * @@A0127572A
 *
 */
public class EstherTest {

	private String pathString = "esther.txt";
	private Path saveLoc = Paths.get(pathString);
	private String[] dateFormats = { "", "dd/MM/yy", "dd/MM/yyyy" };
	private String[] timeFormats = { "", "HHmm", "HH:mm", "hha" };
	private ArrayList<DateTimeTester> todayTestFormats;
	private ArrayList<DateTimeTester> todayOneHourTestFormats;
	private Date now = new Date();
	private Date oneHrFromNow = new Date(new Date().getTime() + (60 * 60 * 1000));
	private DateTimeTester defaultTester = new DateTimeTester(now, dateFormats[1], timeFormats[1]);
	private DateTimeTester default1HTester = new DateTimeTester(oneHrFromNow, dateFormats[1], timeFormats[1]);

	private final boolean DEBUG = false;

	private Logic logic;

	@Before
	public void init() throws ParseException, IOException {
		logic = new Logic();
		cleanUp();
		todayTestFormats = generateDateTimes(now);
		todayOneHourTestFormats = generateDateTimes(oneHrFromNow);
	}

	@Test
	public void addTestFloating() {
		assertTrue(logic.executeCommand("add task").contains("success"));
	}

	@Test
	public void addTestOn() {
		assertTrue(logic.executeCommand("add task on " + defaultTester.getString1()).contains("success"));
		assertTrue(verifyTaskEndDate(defaultTester));
	}

	@Test
	public void addTestOnDetailed() {
		String addCommand;
		for (DateTimeTester dateTimeTester : todayTestFormats) {
			for (int i = 0; i < 2; i++) {
				// tester obj has 1 or 2 strings
				if (i == 1) {
					// has 2 strings
					if (dateTimeTester.isHasReverse()) {
						addCommand = "add task on " + dateTimeTester.getString2();
					} else {
						continue;
					}
				} else {
					// has 1 string anyway
					addCommand = "add task on " + dateTimeTester.getString2();
				}
				String result = logic.executeCommand(addCommand);
				if (!result.contains("success")) {
					System.out.println("Add test failed");
					System.out.println(addCommand);
					System.out.println(result);
					fail();
				} else {
					assertTrue(verifyTaskEndDate(dateTimeTester));
				}
			}
		}
		assertTrue(true);
	}

	@Test
	public void addTestFromTo() {
		assertTrue(logic
				.executeCommand("add task from " + defaultTester.getString1() + " to " + default1HTester.getString1())
				.contains("success"));
		assertTrue(verifyTaskStartDate(defaultTester));
		assertTrue(verifyTaskEndDate(default1HTester));
	}

	@Test
	public void addTestFromToDetailed() {
		int index = 0;
		for (DateTimeTester tester : todayTestFormats) {
			for (DateTimeTester tester1H : todayOneHourTestFormats) {
				index++;
				String addCommand = ("add task from " + tester.getString1() + " to " + tester1H.getString1());
				String result = logic.executeCommand(addCommand);
				if (!result.contains("success")) {
					System.out.println("Add test failed on iteration " + index);
					System.out.println(addCommand);
					System.out.println(result);
					fail();
				} else {
					assertTrue(verifyTaskStartDate(tester));
					assertTrue(verifyTaskEndDate(tester1H));
				}
			}
		}
		// System.out.println(index);
	}

	@Test
	public void addWithKeyword() {
		assertTrue(logic.executeCommand("add \"task from to on \"").contains("success"));
	}

	@Test
	public void addDuplicate() {
		for (int i = 0; i < 2; i++) {
			tryCommand("add task");
		}
	}

	@Test
	public void deleteNameTest() {
		// equivalence partition for delete based on name
		int tasks = logic.getInternalStorage().size();
		assertTrue(logic.executeCommand("add deltask").contains("success"));
		assertTrue(logic.executeCommand("delete deltask").contains("success"));
		assertEquals(tasks, logic.getInternalStorage().size());
	}

	@Test
	public void deleteIDTest() {
		// equivalence partition for delete based on id
		Task.setGlobalId(0);
		int tasks = logic.getInternalStorage().size();
		assertTrue(logic.executeCommand("add deltask").contains("success"));
		assertTrue(logic.executeCommand("delete 0").contains("success"));
		assertEquals(tasks, logic.getInternalStorage().size());
	}

	@Test
	public void deleteDuplicate() {
		for (int i = 0; i < 2; i++) {
			tryCommand("add task");
		}
		failCommand("delete task");
	}

	@Test
	public void updateNameByNameTest() {
		// equivalence partition for updating different fields based on name
		// reference
		assertTrue(logic.executeCommand("add updTask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update updTask name to updatedTask").contains("success"));
	}

	@Test
	public void updateDupNameByNameTest() {
		for (int i = 0; i < 2; i++) {
			tryCommand("add task");
		}
		failCommand("update task name to updatedTask");
	}

	@Test
	public void updateDateByNameTest() {
		// equivalence partition for updating different fields based on name
		// reference
		assertTrue(logic.executeCommand("add updTask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update updTask date to 04/07/2016").contains("success"));
	}

	@Test
	public void updatePriorityByNameTest() {
		// equivalence partition for updating different fields based on name
		// reference
		assertTrue(logic.executeCommand("add updTask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update updTask priority to 1").contains("success"));
	}

	@Test
	public void updateFloatToDeadline() {
		tryCommand("add task");
		tryCommand("update task date to " + defaultTester.getString1());
	}

	@Test
	public void updateFloatToEvent() {
		tryCommand("add task");
		tryCommand("update task endtime to 3pm");
		tryCommand("update task date to 29/3/2016");
		tryCommand("update task starttime to 2pm");
		tryCommand("update task sDate to 29/3/2016");
	}

	@Test
	public void updateDeadlineToEvent() {
		tryCommand("add task on " + defaultTester.getString1());
		tryCommand("update task starttime to 2pm");
		tryCommand("update task startdate to 29/3/2016");
	}

	@Test
	public void updateByIDTest() {
		// equivalence partition for updating different fields based on ID
		// reference
		Task.setGlobalId(0);
		assertTrue(logic.executeCommand("add updTask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update 0 name to updatedTask").contains("success"));
	}

	@Test
	public void completeTest() {
		logic.executeCommand("add task");
		String result = logic.executeCommand("complete task");
		assertTrue(result.contains("success"));
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

	private void tryCommand(String command) {
		String result = logic.executeCommand(command);
		boolean assertResult = result.contains("success");
		if (!assertResult) {
			System.out.println("\"" + command + "\" failed.");
			System.out.println(result);
		}
		assertTrue(assertResult);
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

	private boolean verifyTaskStartDate(DateTimeTester dateTimeTester) {
		Date date = getLastElement(logic.getInternalStorage()).getStartDate();
		return verifyDate(dateTimeTester, date);
	}

	private boolean verifyTaskEndDate(DateTimeTester dateTimeTester) {
		Date date = getLastElement(logic.getInternalStorage()).getEndDate();
		return verifyDate(dateTimeTester, date);
	}

	private boolean verifyDate(DateTimeTester dateTimeTester, Date date) {
		if (!date.equals(dateTimeTester.getDate())) {
			System.out.println(dateTimeTester.getDate().toString());
			System.out.println(date.toString());
			return false;
		} else {
			return true;
		}
	}

	private <E> E getLastElement(ArrayList<E> list) {
		return list.get(list.size() - 1);
	}

	private void deleteFile() {
		try {
			if (Files.exists(Paths.get("esther.txt"))) {
				Files.delete(Paths.get("esther.txt"));
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
