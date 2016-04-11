package cs2103_w09_1j.esther;


import static org.junit.Assert.*;

import java.awt.Robot;

import org.junit.BeforeClass;
import org.junit.Test;

public class TaskWrapperTest {
	private static TaskWrapper tw, twTest;
	private static Task task;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		task = new Task();
		tw = new TaskWrapper(task);
		twTest = new TaskWrapper("testing title");
	}
	
	@Test
	public void testTaskWrapperTask() {
		assertEquals(tw.getClass(), TaskWrapper.class);
	}

	@Test
	public void testTaskWrapperString() {
		assertEquals(twTest.getId(), "testing title");
	}

	@Test
	public void testGetRecord() {
		assertEquals(tw.getRecord(), task);
	}

	@Test
	public void testGetTaskName() {
		assertEquals(tw.getTaskName(), null);
	}

	@Test
	public void testGetId() {
		assertEquals(twTest.getId(), "testing title");
	}

	@Test
	public void testGetPriority() {
		assertEquals(tw.getPriority(), Integer.toString(0));
	}

	@Test
	public void testGetDate() {
		assertEquals(twTest.getDate(), null);
	}

}
