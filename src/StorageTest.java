import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103_w09_1j.esther.Config;
import cs2103_w09_1j.esther.Task;

public class StorageTest {

	private Logic logic;
	private Storage storage;
	private Date now = new Date();
	private Date oneHrFromNow = new Date(new Date().getTime() + (60 * 60 * 1000));
	private DateTimeTester defaultTester = new DateTimeTester(now, "dd/MM/yy", "HH:mm");
	private DateTimeTester default1HTester = new DateTimeTester(oneHrFromNow, "dd/MM/yy", "HH:mm");

	private String filePath = "estherTest.txt";
	private boolean setupDone = false;

	@Before
	public void init() {
		if (!setupDone) {
			try {
				logic = new Logic();
				storage = new Storage();
				setupDone = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void writeReadSave() {
		ArrayList<Task> tasks = new ArrayList<>();
		tasks.add(createSampleTask());
		try {
			storage.writeSaveFile(tasks);
			storage.readSaveFile();
			assertEquals(tasks, storage.getTasks());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void writeReadConfig() {
		Config testCfg = new Config();
		testCfg.setReferenceID(13);
		testCfg.setSavePath(filePath);
		try {
			storage.flushFileAtLocation("estherconfig.txt");
			storage.writeConfigFile(testCfg);
			storage.readConfigFile();
			assertEquals(testCfg, storage.getConfig());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void flushFile() {
		ArrayList<Task> tasks = new ArrayList<>();
		try {
			storage.writeSaveFile(tasks);
			storage.flushSaveFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertFalse(storage.getConfig().getSavePath().toFile().exists());
	}

	@Test
	public void changeConfig() {
		Config testCfg = new Config();
		try {
			storage.setConfig(testCfg);
			testCfg.setReferenceID(13);
			testCfg.setSavePath(filePath);
			storage.setConfig(testCfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(testCfg, storage.getConfig());

	}

	@After
	public void cleanUp() {
		try {
			storage.flushSaveFile();
			storage.flushFileAtLocation("estherconfig.txt");
			storage.flushSaveFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Task createSampleTask() {
		logic.executeCommand(
				"add newTask from " + defaultTester.getDTString() + " to " + default1HTester.getDTString());
		return logic.getInternalStorage().get(logic.getInternalStorage().size() - 1);
	}
}
