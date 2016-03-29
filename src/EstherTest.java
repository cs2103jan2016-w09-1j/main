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
    String[] dateFormats = { "", "dd/MM/yy", "dd/MM/yyyy" };
    String[] timeFormats = { "", "HHmm", "hha", "HH:mm" };
    ArrayList<String> todayFormats;
    ArrayList<String> todayOneHourFormats;

    private final boolean DEBUG = false;

    private Logic logic;

    @Before
    public void init() throws ParseException, IOException {
	logic = new Logic();
	cleanUp();
	todayFormats = generateDateTimes(new Date());
	todayOneHourFormats = generateDateTimes(new Date(new Date().getTime() + (60 * 60 * 1000)));
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
    public void deleteNameTestDetailed() {
	String result = logic.executeCommand("add \"deltask on\"");
	assertTrue(logic.executeCommand("delete deltask on").contains("success"));
    }

    @Test
    public void deleteIDTest() {
	// equivalence partition for delete based on id
	Task.setGlobalId(0);
	assertTrue(logic.executeCommand("add deltask on 03/07/2016").contains("success"));
	assertTrue(logic.executeCommand("delete 0").contains("success"));
    }

    @Test
    public void updateNameByNameTest() {
	// equivalence partition for updating different fields based on name
	// reference
	assertTrue(logic.executeCommand("add updTask on 03/07/2016").contains("success"));
	assertTrue(logic.executeCommand("update updTask taskName to updatedTask").contains("success"));
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
    public void updateByIDTest() {
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

    private void deleteFile() {
	try {
	    if (Files.exists(Paths.get("esther.txt"))) {
		Files.delete(Paths.get("esther.txt"));
	    }
	} catch (IOException e) {

	}
    }

    private ArrayList<String> generateDateTimes(Date date) {
	String dateFormat, timeFormat, dateTimeFormat, dateTimeFormattedString;
	Date today = date;
	ArrayList<String> dates = new ArrayList<>();
	for (int i = 0; i < dateFormats.length; i++) {
	    for (int j = 0; j < timeFormats.length; j++) {
		dateFormat = dateFormats[i];
		timeFormat = timeFormats[j];
		if (dateFormat.length() != 0 && timeFormat.length() != 0) {
		    for (int k = 0; k < 2; k++) {
			if (k == 0) {
			    dateTimeFormat = (dateFormat + " " + timeFormat).trim();
			} else {
			    dateTimeFormat = (timeFormat + " " + dateFormat).trim();
			}
			dateTimeFormattedString = new SimpleDateFormat(dateTimeFormat).format(today);
			dates.add(dateTimeFormattedString);
		    }
		} else if (dateFormat.length() == 0 && timeFormat.length() != 0) {
		    dateTimeFormattedString = new SimpleDateFormat(timeFormat).format(today);
		    dates.add(dateTimeFormattedString);
		} else if (timeFormat.length() == 0 && dateFormat.length() != 0) {
		    dateTimeFormattedString = new SimpleDateFormat(dateFormat).format(today);
		    dates.add(dateTimeFormattedString);
		}
	    }
	}
	return dates;
    }
}
