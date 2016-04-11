import static org.junit.Assert.*;

import java.io.BufferedWriter;
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
	public void init() throws ParseException, IOException {
		if (!setupDone) {
			logic = new Logic();
			storage = new Storage();
			setupDone = true;
		}
	}

	@Test
	public void writeReadSave() throws IOException, ParseException {
		ArrayList<Task> tasks = new ArrayList<>();
		tasks.add(createSampleTask());
		storage.writeSaveFile(tasks);
		storage.readSaveFile();
		assertEquals(tasks, storage.getTasks());
	}

	@Test
	public void writeReadConfig() throws IOException, ParseException {
		Config testCfg = new Config();
		testCfg.setReferenceID(13);
		testCfg.setSavePath(filePath);
		storage.flushFileAtLocation("estherconfig.txt");
		storage.writeConfigFile(testCfg);
		storage.readConfigFile();
		assertEquals(testCfg, storage.getConfig());
	}

	@Test
	public void flushFile() throws IOException {
		ArrayList<Task> tasks = new ArrayList<>();
		storage.writeSaveFile(tasks);
		storage.flushSaveFile();
		assertFalse(storage.getConfig().getSavePath().toFile().exists());
	}

	@Test
	public void changeConfig() throws IOException {
		Config testCfg = new Config();
		storage.setConfig(testCfg);
		testCfg.setReferenceID(13);
		testCfg.setSavePath(filePath);
		storage.setConfig(testCfg);
		assertEquals(testCfg, storage.getConfig());

	}
	
	@Test 
	public void loadBadConfig() throws IOException, ParseException {
		Files.deleteIfExists(Paths.get("estherconfig.txt"));
		Files.createFile(Paths.get("estherconfig.txt"));
		storage.readConfigFile();
	}
	
	@Test 
	public void loadBadTasks() throws IOException, ParseException {
		Path testPath = Paths.get("test.txt");
		BufferedWriter writer = Files.newBufferedWriter(testPath);
		writer.write(" | | |");
		writer.close();
		storage.readSaveFile(testPath);
	}

	@After
	public void cleanUp() throws IOException {
		storage.flushSaveFile();
		storage.flushFileAtLocation("estherconfig.txt");
		storage.flushSaveFile();
	}

	private Task createSampleTask() {
		logic.executeCommand(
				"add newTask from " + defaultTester.getDTString() + " to " + default1HTester.getDTString());
		return logic.getInternalStorage().get(logic.getInternalStorage().size() - 1);
	}
}
