package cs2103_w09_1j.esther;

import static org.junit.Assert.*;

import java.awt.Robot;

import org.junit.BeforeClass;
import org.junit.Test;

public class UiMainControllerTest {

	private static UiMainController controller;
	private static Robot robot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		controller = new UiMainController();
		robot = new Robot();
	}

	@Test
	public void testGetRes() {
		assertEquals(controller.getRes().getClass(), new UIResult().getClass());
	}

	@Test
	public void testSetRes() {
		UIResult res = new UIResult();
		res.setCommandType("test cmd");
		controller.setRes(res);
		assertEquals(controller.getRes().getCommandType(), "test cmd");
	}

}
