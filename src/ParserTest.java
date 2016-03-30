/**
 * @@author A0126000H
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cs2103_w09_1j.esther.Command;
import cs2103_w09_1j.esther.Config;
import cs2103_w09_1j.esther.InvalidInputException;

public class ParserTest {
	String input;
	Command command;
	Command resultCommand;
	Parser parser;
	String taskName = "taskName";
	String updateName = "updateName";
	String taskID = "taskID";
	String startDate = "startDate";
	String endDate = "endDate";
	String startTime = "startTime";
	String endTime = "endTime";
	String priority = "priority";
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void beforeTest() {
		command = new Command();
		resultCommand = new Command();
		parser = new Parser(new Config().getFieldNameAliases());

	}

	@Test(expected = InvalidInputException.class)
	public void testWrongCommand() throws ParseException, InvalidInputException {
		input = "someothercommand";

		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test case for add
	 */
	@Test
	public void testAddBasic1() throws ParseException, InvalidInputException {
		input = "add Meeting";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Meeting");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testAddBasic1b() throws ParseException, InvalidInputException {
		input = "add Office Meeting";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

	@Test
	public void testAddBasic2() throws ParseException, InvalidInputException {
		input = "add Office Meeting on today";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(date);
		command.addFieldToMap(endDate, sDate);
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

	@Test
	public void testAddBasic3() throws ParseException, InvalidInputException {
		input = "add Office Meeting on today 3pm";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(date);
		command.addFieldToMap(endDate, sDate);
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testAddBasic3b() throws ParseException, InvalidInputException {
		input = "add Office Meeting on 3pm today";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(date);
		command.addFieldToMap(endDate, sDate);
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testAddBasic4() throws ParseException, InvalidInputException {
		input = "add Office Meeting on 1500 today";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String sDate = dateFormat.format(date);
		command.addFieldToMap(endDate, sDate);
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testAddBasic4b() throws ParseException, InvalidInputException {
		input = "add Office Meeting on today 1500";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String eDate = dateFormat.format(date);
		command.addFieldToMap(endDate, eDate);
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testAddBasic5() throws ParseException, InvalidInputException {
		input = "add Office Meeting on 23/03/2016";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(endDate, "23/03/2016");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}
	
	@Test
	public void testAddBasic5b() throws ParseException, InvalidInputException {
		input = "add Office Meeting on 23/3/16";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(endDate, "23/03/2016");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}
	
	@Test
	public void testAddBasic6() throws ParseException, InvalidInputException {
		input = "add Office Meeting on 23/3/16 3pm";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(endDate, "23/03/2016");
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}
	
	@Test
	public void testAddBasic6b() throws ParseException, InvalidInputException {
		input = "add Office Meeting on 23/3/16 1500";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(endDate, "23/03/2016");
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testAddBasic7() throws ParseException, InvalidInputException {
		input = "add Office Meeting from aug 26 3pm to mar 20 4pm";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(startDate, "26/08/2016");
		command.addFieldToMap(startTime, "15:00");
		command.addFieldToMap(endDate, "20/03/2017");
		command.addFieldToMap(endTime, "16:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testAddBasic8() throws ParseException, InvalidInputException {
		input = "add meeting from 4 may to 3pm";
		command.setCommand("add");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(startDate, "04/05/2016");
		command.addFieldToMap(endDate, "04/05/2016");
		command.addFieldToMap(startTime, "00:00");
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test(expected = InvalidInputException.class)
	public void testAddAlternate1() throws ParseException, InvalidInputException {
		input = "add";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testAddAlternate2() throws ParseException, InvalidInputException {
		input = "add \"Office Meeting on budget";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testAddAlternate3() throws ParseException, InvalidInputException {
		input = "add meeting on ";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testAddAlternate4() throws ParseException, InvalidInputException {
		input = "add meeting from to ";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testAddAlternate5() throws ParseException, InvalidInputException {
		input = "add meeting from 4 may to ";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testAddAlternate6() throws ParseException, InvalidInputException {
		input = "add meeting from to 23 may";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test
	public void testUpdateBasic1() throws ParseException, InvalidInputException {
		input = "update meeting startdate to 23 feb";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(startDate, "23/02/2017");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testUpdateBasic2() throws ParseException, InvalidInputException {
		input = "update meeting priority to 3";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(priority, "3");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testUpdateBasic3() throws ParseException, InvalidInputException {
		input = "update meeting starttime to 0500";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(startTime, "05:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testUpdateBasic4() throws ParseException, InvalidInputException {
		input = "update meeting name to newMeeting";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(updateName, "newMeeting");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testUpdateBasic5() throws ParseException, InvalidInputException {
		input = "update meeting endDate to 23/07/2016";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(endDate, "23/07/2016");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate1() throws ParseException, InvalidInputException {
		input = "update";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate2() throws ParseException, InvalidInputException {
		input = "update meeting";
		resultCommand = parser.acceptUserInput(input);
	}
	
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate3() throws ParseException, InvalidInputException {
		input = "update meeting time to 4pm";
		resultCommand = parser.acceptUserInput(input);
	}
	
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate4() throws ParseException, InvalidInputException {
		input = "update meeting starttime to 23 feb";
		resultCommand = parser.acceptUserInput(input);
	}
	
	// Basic case
	@Test
	public void testDeleteBasic1() throws ParseException, InvalidInputException {
		input = "delete 1";

		command.setCommand("delete");
		command.addFieldToMap(taskID, "1");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testDeleteBasic2() throws ParseException, InvalidInputException {
		input = "delete task1";

		command.setCommand("delete");
		command.addFieldToMap(taskName, "task1");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testDeleteBasic3() throws ParseException, InvalidInputException {
		input = "delete office meeting";

		command.setCommand("delete");
		command.addFieldToMap(taskName, "office meeting");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testDeleteBasic4() throws ParseException, InvalidInputException {
		input = "delete \"office meeting on budget\"";

		command.setCommand("delete");
		command.addFieldToMap(taskName, "office meeting on budget");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// Alternate case
	@Test(expected = InvalidInputException.class)
	public void testDeleteAlternate1() throws ParseException, InvalidInputException {
		input = "delete";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testDeleteAlternate2() throws ParseException, InvalidInputException {
		input = "delete \"office meeting on budget";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test
	public void testSearchBasic1() throws ParseException, InvalidInputException {
		input = "search anything";

		command.setCommand("search");
		command.addFieldToMap(taskName, "anything");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testSearchBasic2() throws ParseException, InvalidInputException {
		input = "search for anything";
		command.setCommand("search");
		command.addFieldToMap(taskName, "for anything");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testSearchBasic3() throws ParseException, InvalidInputException {
		input = "search \"anything\"";
		command.setCommand("search");
		command.addFieldToMap(taskName, "anything");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test(expected = InvalidInputException.class)
	public void testSearchAlternate1() throws ParseException, InvalidInputException {
		input = "search";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testSearchAlternate2() throws ParseException, InvalidInputException {
		input = "search \"anything";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test
	public void testShowBasic1() throws ParseException, InvalidInputException {
		input = "show";
		command.setCommand("show");
		command.addFieldToMap("order", "taskID");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testShowBasic2() throws ParseException, InvalidInputException {
		input = "show by name";
		command.setCommand("show");
		command.addFieldToMap("order", taskName);
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test(expected = InvalidInputException.class)
	public void testShowAlternate1() throws ParseException, InvalidInputException {
		input = "show by number";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testShowAlternate2() throws ParseException, InvalidInputException {
		input = "show by";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test
	public void testSortBasic1() throws ParseException, InvalidInputException {
		input = "sort by name";
		command.setCommand("sort");
		command.addFieldToMap("order", taskName);
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test(expected = InvalidInputException.class)
	public void testSortAlternate1() throws ParseException, InvalidInputException {
		input = "sort";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testSortAlternate2() throws ParseException, InvalidInputException {
		input = "sort by number";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testSortAlternate3() throws ParseException, InvalidInputException {
		input = "sort by";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test
	public void testCompleteBasic1() throws ParseException, InvalidInputException {
		input = "complete 3";
		command.setCommand("complete");
		command.addFieldToMap(taskID, "3");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testCompleteBasic2() throws ParseException, InvalidInputException {
		input = "complete task1";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "task1");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testCompleteBasic3() throws ParseException, InvalidInputException {
		input = "complete office meeting";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "office meeting");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testCompleteBasic4() throws ParseException, InvalidInputException {
		input = "complete \"office meeting on budget\"";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "office meeting on budget");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test(expected = InvalidInputException.class)
	public void testCompleteAlternate1() throws ParseException, InvalidInputException {
		input = "complete";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test(expected = InvalidInputException.class)
	public void testCompleteAlternate2() throws ParseException, InvalidInputException {
		input = "complete \"officebudget on";
		resultCommand = parser.acceptUserInput(input);
	}

	@Test
	public void testUndoBasic1() throws ParseException, InvalidInputException {
		input = "undo";
		command.setCommand("undo");
		command.addFieldToMap("undo", "");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testHelp() throws ParseException, InvalidInputException {
		input = "help";
		command.setCommand("help");
		command.addFieldToMap("help", "");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

}
