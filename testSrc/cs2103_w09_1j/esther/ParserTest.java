package cs2103_w09_1j.esther;


/**
 * ============= [PARSER TEST FOR ESTHER] =============
 * 
 * This class used to check the acceptable input for Parser.
 * The testing is split into two types: basic and alternate.
 * Basic refers to inputs that acceptable by Parser.
 * Alternate refers to inputs that are rejected by Parser.
 * 
 * @@author A0126000H
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ParserTest {

	// Objects used in ParserTest
	String input;
	Command command;
	Command resultCommand;
	Parser parser;

	// Field names used in ParserTest
	String taskName = "taskName";
	String updateName = "updateName";
	String taskID = "taskID";
	String startDate = "startDate";
	String endDate = "endDate";
	String startTime = "startTime";
	String endTime = "endTime";
	String priority = "priority";
	String keyword = "keyword";
	String undo = "undo";
	String help = "help";
	String path = "path";

	@Before
	public void beforeTest() {
		command = new Command();
		resultCommand = new Command();
		parser = new Parser(new Config().getFieldNameAliases());

	}

	@Test // valid command
	public void testCommandBasic1() throws InvalidInputException {
		input = "add meeting";
		command.setCommand("add");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getCommand(), resultCommand.getCommand());
	}

	@Test // valid command
	public void testCommandBasic2() throws InvalidInputException {
		input = " update meeting name to office meeting";
		command.setCommand("update");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getCommand(), resultCommand.getCommand());
	}

	// invalid command
	@Test(expected = InvalidInputException.class)
	public void testWrongCommand() throws InvalidInputException {
		input = "someothercommand";

		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test cases for add command
	 */

	@Test // floating task
	public void testAddBasic1() throws InvalidInputException {
		input = "add Meeting";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Meeting");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // floating task with more than 1 word
	public void testAddBasic1b() throws InvalidInputException {
		input = "add Office Meeting";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

	@Test // task with a end date (wordy date)
	public void testAddBasic2() throws InvalidInputException {
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

	@Test // task with a end date and time
	public void testAddBasic3() throws InvalidInputException {
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

	@Test // task with a end time and date
	public void testAddBasic3b() throws InvalidInputException {
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

	@Test // task with a different time format
	public void testAddBasic4() throws InvalidInputException {
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

	@Test // task with a reverse date and time
	public void testAddBasic4b() throws InvalidInputException {
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

	@Test // task with end date (proper date)
	public void testAddBasic5() throws InvalidInputException {
		input = "add Office Meeting on 23/03/2016";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(endDate, "23/03/2016");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

	@Test // task with end date (proper date, different format)
	public void testAddBasic5b() throws InvalidInputException {
		input = "add Office Meeting on 23/3/16";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(endDate, "23/03/2016");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

	@Test // task with end date and time (proper date, different format)
	public void testAddBasic6() throws InvalidInputException {
		input = "add Office Meeting on 23/3/16 3pm";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(endDate, "23/03/2016");
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

	@Test // task with end time and date (proper date, different format)
	public void testAddBasic6b() throws InvalidInputException {
		input = "add Office Meeting on 23/3/16 1500";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		command.addFieldToMap(endDate, "23/03/2016");
		command.addFieldToMap(endTime, "15:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // event with start date and time and end date and time
	public void testAddBasic7() throws InvalidInputException {
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

	@Test // event with only not full start/end date and time
	public void testAddBasic8() throws InvalidInputException {
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

	@Test // task with quoted task name
	public void testAddBasic11() throws InvalidInputException {
		input = "add \"meeting on budget\" on 12/5/2017";
		command.setCommand("add");
		command.addFieldToMap(taskName, "meeting on budget");
		command.addFieldToMap(endDate, "12/05/2017");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // task with quoted task name (proper date, different format)
	public void testAddBasic12() throws InvalidInputException {
		input = "add \"meeting on budget\" by 23/4/16";
		command.setCommand("add");
		command.addFieldToMap(taskName, "meeting on budget");
		command.addFieldToMap(endDate, "23/04/2016");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // event with only not full start/end date and time
	public void testAddBasi13() throws InvalidInputException {
		input = "add meeting from 4 may 3pm to 5 may";
		command.setCommand("add");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(startDate, "04/05/2016");
		command.addFieldToMap(endDate, "05/05/2016");
		command.addFieldToMap(startTime, "15:00");
		command.addFieldToMap(endTime, "23:59");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// no field values
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate1() throws InvalidInputException {
		input = "add";
		resultCommand = parser.acceptUserInput(input);
	}

	// no end quote in task name
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate2() throws InvalidInputException {
		input = "add \"Office Meeting on budget";
		resultCommand = parser.acceptUserInput(input);
	}

	// no date and time for task
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate3() throws InvalidInputException {
		input = "add meeting on ";
		resultCommand = parser.acceptUserInput(input);
	}

	// no date and time for event
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate4() throws InvalidInputException {
		input = "add meeting from to ";
		resultCommand = parser.acceptUserInput(input);
	}

	// no end date and time for event
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate5() throws InvalidInputException {
		input = "add meeting from 4 may to ";
		resultCommand = parser.acceptUserInput(input);
	}

	// no start date and time for event
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate6() throws InvalidInputException {
		input = "add meeting from to 23 may";
		resultCommand = parser.acceptUserInput(input);
	}

	// all numbers is not allowed as a task name
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate7() throws InvalidInputException {
		input = "add 10122";
		resultCommand = parser.acceptUserInput(input);
	}

	// invalid date and time
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate8() throws InvalidInputException {
		input = "add meeting on whatever";
		resultCommand = parser.acceptUserInput(input);
	}

	// invalid start date and time
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate9() throws InvalidInputException {
		input = "add meeting from whatever to 9pm";
		resultCommand = parser.acceptUserInput(input);
	}

	// end time earlier than start time
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate10() throws InvalidInputException {
		input = "add meeting from 3pm to 2pm";
		resultCommand = parser.acceptUserInput(input);
	}

	// end date earlier than start date
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate11() throws InvalidInputException {
		input = "add meeting from 11/12/2014 to 10/12/2014";
		resultCommand = parser.acceptUserInput(input);
	}

	// no "to"
	@Test(expected = InvalidInputException.class)
	public void testAddAlternate12() throws InvalidInputException {
		input = "add meeting from 11/12/2014";
		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test cases for update command
	 */

	@Test // update start date
	public void testUpdateBasic1() throws InvalidInputException {
		input = "update meeting startdate to 23 feb";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(startDate, "23/02/2017");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // update priority
	public void testUpdateBasic2() throws InvalidInputException {
		input = "update meeting priority to 3";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(priority, "3");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // update start time
	public void testUpdateBasic3() throws InvalidInputException {
		input = "update meeting starttime to 0500";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(startTime, "05:00");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // update name using id
	public void testUpdateBasic4() throws InvalidInputException {
		input = "update 5 name to newMeeting";
		command.setCommand("update");
		command.addFieldToMap(taskID, "5");
		command.addFieldToMap(updateName, "newMeeting");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // update end date using proper date
	public void testUpdateBasic5() throws InvalidInputException {
		input = "update meeting endDate to 23/07/2016";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(endDate, "23/07/2016");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // update name with quotes
	public void testUpdateBasic6() throws InvalidInputException {
		input = "update meeting name to \"office meeting on budget\"";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting");
		command.addFieldToMap(updateName, "office meeting on budget");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // update by name with quotes
	public void testUpdateBasic7() throws InvalidInputException {
		input = "update \"meeting on budget\" name to \"office meeting on budget\"";
		command.setCommand("update");
		command.addFieldToMap(taskName, "meeting on budget");
		command.addFieldToMap(updateName, "office meeting on budget");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// no field values
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate1() throws InvalidInputException {
		input = "update";
		resultCommand = parser.acceptUserInput(input);
	}

	// no new value to update
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate2() throws InvalidInputException {
		input = "update meeting";
		resultCommand = parser.acceptUserInput(input);
	}

	// all integer not allowed as task name
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate3() throws InvalidInputException {
		input = "update meeting name to 4";
		resultCommand = parser.acceptUserInput(input);
	}

	// no end quote
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate4() throws InvalidInputException {
		input = "update meeting name to \"office meeting on budget";
		resultCommand = parser.acceptUserInput(input);
	}

	// invalid date
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate5() throws InvalidInputException {
		input = "update 4 to startdate to wrongdate";
		resultCommand = parser.acceptUserInput(input);
	}

	// invalid priority
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate6() throws InvalidInputException {
		input = "update 4 priority to wrongpriority";
		resultCommand = parser.acceptUserInput(input);
	}

	// no end quote in name
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate7() throws InvalidInputException {
		input = "update \"meeting on budget to priority to wrongpriority";
		resultCommand = parser.acceptUserInput(input);
	}

	// no such field (something)
	@Test(expected = InvalidInputException.class)
	public void testUpdateAlternate8() throws InvalidInputException {
		input = "update 5 priority to";
		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test cases for delete
	 */

	@Test // delete using id
	public void testDeleteBasic1() throws InvalidInputException {
		input = "delete 1";

		command.setCommand("delete");
		command.addFieldToMap(taskID, "1");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // delete using task name
	public void testDeleteBasic2() throws InvalidInputException {
		input = "delete task1";

		command.setCommand("delete");
		command.addFieldToMap(taskName, "task1");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // delete using long task name
	public void testDeleteBasic3() throws InvalidInputException {
		input = "delete office meeting";

		command.setCommand("delete");
		command.addFieldToMap(taskName, "office meeting");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // delete with quotes
	public void testDeleteBasic4() throws InvalidInputException {
		input = "delete \"office meeting on budget\"";

		command.setCommand("delete");
		command.addFieldToMap(taskName, "office meeting on budget");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// no delete value
	@Test(expected = InvalidInputException.class)
	public void testDeleteAlternate1() throws InvalidInputException {
		input = "delete";
		resultCommand = parser.acceptUserInput(input);
	}

	// no end quote
	@Test(expected = InvalidInputException.class)
	public void testDeleteAlternate2() throws InvalidInputException {
		input = "delete \"office meeting on budget";
		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test cases for search command
	 */

	@Test // searching by task name using "for" key
	public void testSearchBasic1() throws InvalidInputException {
		input = "search for anything";
		command.setCommand("search");
		command.addFieldToMap(taskName, "anything");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // searching for date using "on" key
	public void testSearchBasic2() throws InvalidInputException {
		input = "search on 23 february";
		command.setCommand("search");
		command.addFieldToMap(endDate, "23/02/2017");
		command.addFieldToMap(keyword, "on");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // searching for date using "on" key
	public void testSearchBasic3() throws InvalidInputException {
		input = "search for \"meeting on budget\"";
		command.setCommand("search");
		command.addFieldToMap(taskName, "meeting on budget");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// no search value and key
	@Test(expected = InvalidInputException.class)
	public void testSearchAlternate1() throws InvalidInputException {
		input = "search";
		resultCommand = parser.acceptUserInput(input);
	}

	// only "for" key is allowed for task name
	@Test(expected = InvalidInputException.class)
	public void testSearchAlternate2() throws InvalidInputException {
		input = "search by name";
		resultCommand = parser.acceptUserInput(input);
	}

	// no search value
	@Test(expected = InvalidInputException.class)
	public void testSearchAlternate3() throws InvalidInputException {
		input = "search for";
		resultCommand = parser.acceptUserInput(input);
	}

	// no search value
	@Test(expected = InvalidInputException.class)
	public void testSearchAlternate4() throws InvalidInputException {
		input = "search for \"meeting on budget";
		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test cases for show command
	 */

	@Test // default show is by id
	public void testShowBasic1() throws InvalidInputException {
		input = "show";
		command.setCommand("show");
		command.addFieldToMap("order", "taskID");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // show tasks by name
	public void testShowBasic2() throws InvalidInputException {
		input = "show by name";
		command.setCommand("show");
		command.addFieldToMap("order", taskName);
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // spaces does not affect the command
	public void testShowBasic3() throws InvalidInputException {
		input = "show by      enddate";
		command.setCommand("show");
		command.addFieldToMap("order", endDate);
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// not valid field value
	@Test(expected = InvalidInputException.class)
	public void testShowAlternate1() throws InvalidInputException {
		input = "show by number";
		resultCommand = parser.acceptUserInput(input);
	}

	// no show value
	@Test(expected = InvalidInputException.class)
	public void testShowAlternate2() throws InvalidInputException {
		input = "show by";
		resultCommand = parser.acceptUserInput(input);
	}

	// not valid long field value
	@Test(expected = InvalidInputException.class)
	public void testShowAlternate3() throws InvalidInputException {
		input = "show by something that is unknown";
		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test cases for sort command
	 */

	@Test // sorting by valid field value
	public void testSortBasic1() throws InvalidInputException {
		input = "sort by name";
		command.setCommand("sort");
		command.addFieldToMap("order", taskName);
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// no field value
	@Test(expected = InvalidInputException.class)
	public void testSortAlternate1() throws InvalidInputException {
		input = "sort";
		resultCommand = parser.acceptUserInput(input);
	}

	// invalid field value
	@Test(expected = InvalidInputException.class)
	public void testSortAlternate2() throws InvalidInputException {
		input = "sort by number";
		resultCommand = parser.acceptUserInput(input);
	}

	// no field value
	@Test(expected = InvalidInputException.class)
	public void testSortAlternate3() throws InvalidInputException {
		input = "sort by";
		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test cases for complete command
	 */

	@Test // complete by id
	public void testCompleteBasic1() throws InvalidInputException {
		input = "complete 3";
		command.setCommand("complete");
		command.addFieldToMap(taskID, "3");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // complete by name
	public void testCompleteBasic2() throws InvalidInputException {
		input = "complete task1";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "task1");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // complete by long name
	public void testCompleteBasic3() throws InvalidInputException {
		input = "complete office meeting";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "office meeting");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // complete using quotes
	public void testCompleteBasic4() throws InvalidInputException {
		input = "complete \"office meeting on budget\"";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "office meeting on budget");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// no task to complete
	@Test(expected = InvalidInputException.class)
	public void testCompleteAlternate1() throws InvalidInputException {
		input = "complete";
		resultCommand = parser.acceptUserInput(input);
	}

	// no end quote
	@Test(expected = InvalidInputException.class)
	public void testCompleteAlternate2() throws InvalidInputException {
		input = "complete \"officebudget on";
		resultCommand = parser.acceptUserInput(input);
	}

	/*
	 * Test cases for undo command
	 */

	@Test // simply undo
	public void testUndoBasic1() throws InvalidInputException {
		input = "undo";
		command.setCommand("undo");
		command.addFieldToMap(undo, "");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	/*
	 * Test cases for help command
	 */

	@Test // simply help
	public void testHelpBasic1() throws InvalidInputException {
		input = "help";
		command.setCommand("help");
		command.addFieldToMap(help, "");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

	/*
	 * Test cases for set command
	 */
	@Test // set to input.txt
	public void testSetBasic1() throws InvalidInputException {
		input = "set input.txt";
		command.setCommand("set");
		command.addFieldToMap(path, "input.txt");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test // allow quotes
	public void testSetBasic2() throws InvalidInputException {
		input = "set \"input.txt\"";
		command.setCommand("set");
		command.addFieldToMap(path, "input.txt");
		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	// no file path to set to
	@Test(expected = InvalidInputException.class)
	public void testSeAlternate1() throws InvalidInputException {
		input = "set";
		resultCommand = parser.acceptUserInput(input);
	}
}
