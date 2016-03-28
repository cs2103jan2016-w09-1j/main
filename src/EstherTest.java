import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103_w09_1j.esther.Task;

public class EstherTest {
	String pathString = "esther.txt";
	Path saveLoc = Paths.get(pathString);
	String[] dateFormats = { " ", "dd/MM/yy", "dd/MM/yyyy" };
	String[] timeFormats = { " ", "HHmm", "hha", "HH:mm" };
	ArrayList<String> todayFormats;
	ArrayList<String> todayOneHourFormats;

	private final boolean DEBUG = false;

	public void deleteFile() {
		try {
			if (Files.exists(Paths.get("esther.txt"))) {
				Files.delete(Paths.get("esther.txt"));
			}
		} catch (IOException e) {

		}
	}

	private Logic logic;

	@Before
	public void init() throws ParseException, IOException {
		logic = new Logic();
		cleanUp();
		todayFormats = generateDateTimes(new Date());
		todayOneHourFormats = generateDateTimes(new Date(new Date().getTime() + (60 * 60 * 1000)));
	}

	private ArrayList<String> generateDateTimes(Date date) {
		String dateFormat, timeFormat, dateTimeFormat, dateTimeFormattedString;
		Date today = date;
		ArrayList<String> dates = new ArrayList<>(); 
		for (int i = 0; i < dateFormats.length; i++) {
			for (int j = 0; j < timeFormats.length; j++) {
				// skip the first loop as both date and time are empty
				if (i == 0 && j == 0) {
					continue;
				}
				dateFormat = dateFormats[i];
				timeFormat = timeFormats[j];
				for (int k = 0; k < 2; k++) {
					if (k == 0) {
						dateTimeFormat = (dateFormat + " " + timeFormat).trim();
					} else {
						dateTimeFormat = (timeFormat + " " + dateFormat).trim();
					}
					dateTimeFormattedString = new SimpleDateFormat(dateTimeFormat).format(today);
					dates.add(dateTimeFormattedString);
				}
			}
		}
		return dates;
	}

	@Test
	public void addTestFloating() {
		assertTrue(logic.executeCommand("add task").contains("success"));
	}

	@Test
	public void addTestOn() {
		assertTrue(logic.executeCommand("add task on 13/07/2016 1500").contains("success"));
	}

	@Test
	public void addTestOnDetailed() {
		for (String dTString : todayFormats) {
			String addCommand = ("add task on " + dTString);
			String result = logic.executeCommand(addCommand);
			if (!result.contains("success")) {
				System.out.println("Add test failed");
				System.out.println(addCommand);
				System.out.println(result);
				fail();

			}
		}
		assertTrue(true);
	}

	@Test
	public void addTestFromTo() {
		assertTrue(logic.executeCommand("add task from 13/07/2016 1500 to 13/07/2016 1600").contains("success"));
	}
	
	@Test
	public void addTestFromToDetailed() {
		for (String dTString : todayFormats) {
			for (String dTString2 : todayOneHourFormats) {
				String addCommand = ("add task from " + dTString + " to " + dTString2);
				String result = logic.executeCommand(addCommand);
				if (!result.contains("success")) {
					System.out.println("Add test failed");
					System.out.println(addCommand);
					System.out.println(result);
					fail();
				}
			}
		}
		assertTrue(true);
	}

	@Test
	public void deleteNameTest() {
		// equivalence partition for delete based on name
		assertTrue(logic.executeCommand("add deltask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("delete deltask").contains("success"));
	}

	@Test
	public void deleteIDTest() {
		// equivalence partition for delete based on id
		Task.setGlobalId(0);
		assertTrue(logic.executeCommand("add deltask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("delete 0").contains("success"));
	}

	@Test
	public void updateNameTest() {
		// equivalence partition for updating different fields based on name
		// reference
		assertTrue(logic.executeCommand("add updTask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update updTask taskName to updatedTask").contains("success"));
		assertTrue(logic.executeCommand("update updatedTask date to 04/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update updatedTask priority to 1").contains("success"));
	}

	@Test
	public void updateIDTest() {
		// equivalence partition for updating different fields based on ID
		// reference
		Task.setGlobalId(0);
		assertTrue(logic.executeCommand("add updTask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update 0 name to updatedTask").contains("success"));
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
}
