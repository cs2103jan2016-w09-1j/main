package cs2103_w09_1j.esther;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.HashMap;

import org.junit.Test;

import cs2103_w09_1j.esther.Task.TaskField;

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
		String testStr = "ID: 1 | [13/3/2016] | hello | Priority: 2 | Completed";
		try {
			Task testTask = new Task(testStr);
			System.out.println(testTask.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void parseTest3() {
		String testStr = "ID: 3 | [] | hello | Priority: 0 | Completed";
		try {
			Task testTask = new Task(testStr);
			System.out.println(testTask.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}
}
