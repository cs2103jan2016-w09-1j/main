package cs2103_w09_1j.esther;


/**
 * @@author A0127572A
 */

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Test;

public class TaskTest {

	@Test
	public void parseTest1() {
		String testStr = "";
		try {
			Task testTask = new Task(testStr);
			assertFalse(testTask.isValid());
			// assertEquals(null,testTask);
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void parseTest2() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.MILLISECOND, 0);
	    calendar.set(2016, 2, 13, 23, 59, 0);
	    String name = "task";
		String testStr = "ID: 1 | [] | [13/03/2016 23:59] | "+name+" | Priority: 2 | Completed";
		try {
			Task testTask = new Task(testStr);
			assertTrue(testTask.getId() == 1);
			assertTrue(testTask.getStartDate() == null);
			assertTrue(testTask.getEndDate().equals(calendar.getTime()));
			assertTrue(testTask.getName().equals(name));
			assertTrue(testTask.getPriority() == 2);
			assertTrue(testTask.isCompleted());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void parseTest3() {
		String testStr = "ID: 3 | [] | [] | hello | Priority: 0 | Incomplete";
		try {
			Task testTask = new Task(testStr);
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}
}
