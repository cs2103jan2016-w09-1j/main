import java.text.ParseException;

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
	String taskID = "taskID";
	String startDate = "startDate";
	String endDate = "endDate";
	String startTime = "startTime";
	String endTime = "endTime";

	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	
	@Before
	public void beforeTest() {
		command = new Command();
		resultCommand = new Command();
		parser = new Parser(new Config().getFieldNameAliases());

	}

	@Test(expected=InvalidInputException.class)
	public void testWrongCommand() throws ParseException, InvalidInputException{
		input="someothercommand";
		
		resultCommand=parser.acceptUserInput(input);
	}
	
	/*
	 * Test case for add
	 */
	// @Test
	// public void testAdd1() throws ParseException, InvalidInputException {
	// input="add Meeting";
	// command.setCommand("add");
	// command.addFieldToMap(taskName, "Meeting");
	// resultCommand=parser.acceptUserInput(input);
	// Assert.assertEquals(command.getParameters(),
	// resultCommand.getParameters());
	// }
	//
	// @Test
	// public void testAdd2() throws ParseException, InvalidInputException{
	// input="add Office Meeting";
	// command.setCommand("add");
	// command.addFieldToMap(taskName, "Office Meeting");
	// resultCommand=parser.acceptUserInput(input);
	// Assert.assertEquals(command.getParameters(),
	// resultCommand.getParameters());
	//
	// }
	//
	// @Test
	// public void testAdd3() throws ParseException, InvalidInputException{
	// input="add Office Meeting on today";
	// command.clear();
	// resultCommand.clear();
	// command.setCommand("add");
	// command.addFieldToMap(taskName, "Office Meeting");
	// Date date=new Date();
	// SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	// SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
	// String sDate=dateFormat.format(date);
	// String sTime=timeFormat.format(date);
	// command.addFieldToMap(endDate, sDate);
	// command.addFieldToMap(endTime, sTime);
	// resultCommand=parser.acceptUserInput(input);
	// Assert.assertEquals(command.getParameters(),
	// resultCommand.getParameters());
	//
	// }
	//
	// @Test
	// public void testAdd4() throws ParseException, InvalidInputException{
	// input="add Office Meeting on today 3pm";
	// command.clear();
	// resultCommand.clear();
	// command.setCommand("add");
	// command.addFieldToMap(taskName, "Office Meeting");
	// Date date=new Date();
	// SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	// String sDate=dateFormat.format(date);
	// command.addFieldToMap(endDate, sDate);
	// command.addFieldToMap(endTime, "15:00");
	// resultCommand=parser.acceptUserInput(input);
	// Assert.assertEquals(command.getParameters(),
	// resultCommand.getParameters());
	// }
	//
	// @Test
	// public void testAdd5() throws ParseException, InvalidInputException{
	// input="add Office Meeting on 3pm today";
	// command.clear();
	// resultCommand.clear();
	// command.setCommand("add");
	// command.addFieldToMap(taskName, "Office Meeting");
	// Date date=new Date();
	// SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
	// String sDate=dateFormat.format(date);
	// command.addFieldToMap(endDate, sDate);
	// command.addFieldToMap(endTime, "15:00");
	// resultCommand=parser.acceptUserInput(input);
	// Assert.assertEquals(command.getParameters(),
	// resultCommand.getParameters());
	// }
	//
	// @Test
	// public void testAdd6() throws ParseException, InvalidInputException{
	// input="add Office Meeting on 23/03/2016";
	// command.clear();
	// resultCommand.clear();
	// command.setCommand("add");
	// command.addFieldToMap(taskName, "Office Meeting");
	// Date date=new Date();
	// SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
	// String sTime=timeFormat.format(date);
	// command.addFieldToMap(endDate, "23/03/2016");
	// command.addFieldToMap(endTime, sTime);
	// resultCommand=parser.acceptUserInput(input);
	// Assert.assertEquals(command.getParameters(),
	// resultCommand.getParameters());
	//
	// }

	//Basic case
	@Test
	public void testDeleteBasic1() throws ParseException, InvalidInputException {
		input = "delete 1";

		command.setCommand("delete");
		command.addFieldToMap(taskID, "1");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testDeleteBasic2() throws ParseException, InvalidInputException{
		input = "delete task1";

		command.setCommand("delete");
		command.addFieldToMap(taskName, "task1");

		resultCommand = parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testDeleteBasic3() throws ParseException, InvalidInputException{
		input="delete office meeting";
		
		command.setCommand("delete");
		command.addFieldToMap(taskName, "office meeting");
		
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testDeleteBasic4() throws ParseException, InvalidInputException{
		input="delete \"office meeting on budget\"";
		
		command.setCommand("delete");
		command.addFieldToMap(taskName, "office meeting on budget");
		
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	//Alternate case
	@Test(expected=InvalidInputException.class)
	public void testDeleteAlternate1() throws ParseException, InvalidInputException{
		input = "delete";
		resultCommand = parser.acceptUserInput(input);
	}
	
	@Test(expected=InvalidInputException.class)
	public void testDeleteAlternate2() throws ParseException, InvalidInputException{
		input="delete \"office meeting on budget";
		resultCommand=parser.acceptUserInput(input);
	}
	

	@Test
	public void testSearchBasic1() throws ParseException, InvalidInputException {
		input="search anything";
		
		command.setCommand("search");
		command.addFieldToMap(taskName, "anything");
		
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testSearchBasic2() throws ParseException, InvalidInputException{
		input="search for anything";
		command.setCommand("search");
		command.addFieldToMap(taskName, "for anything");
		
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testSearchBasic3() throws ParseException, InvalidInputException{
		input="search \"anything\"";
		command.setCommand("search");
		command.addFieldToMap(taskName, "anything");
		
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test(expected=InvalidInputException.class)
	public void testSearchAlternate1() throws ParseException, InvalidInputException{
		input="search";
		resultCommand=parser.acceptUserInput(input);
	}
	
	@Test(expected=InvalidInputException.class)
	public void testSearchAlternate2() throws ParseException, InvalidInputException{
		input="search \"anything";
		resultCommand=parser.acceptUserInput(input);	
	}

	@Test
	public void testShowBasic1() throws ParseException, InvalidInputException {
		input="show";
		command.setCommand("show");
		command.addFieldToMap("order", "taskID");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testShowBasic2() throws ParseException, InvalidInputException{
		input="show by name";
		command.setCommand("show");
		command.addFieldToMap("order", taskName);
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test(expected=InvalidInputException.class)
	public void testShowAlternate1() throws ParseException, InvalidInputException{
		input="show by number";
		resultCommand=parser.acceptUserInput(input);
	}
	
	@Test(expected=InvalidInputException.class)
	public void testShowAlternate2() throws ParseException, InvalidInputException{
		input="show by";
		resultCommand=parser.acceptUserInput(input);
	}
	

	@Test
	public void testSortBasic1() throws ParseException, InvalidInputException {
		input="sort by name";
		command.setCommand("sort");
		command.addFieldToMap("sort", taskName);
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testSortAlternate1(){
		input="sort";
		command.setCommand("sort");
	}
	
	@Test(expected=InvalidInputException.class)
	public void testSortAlternate() throws ParseException, InvalidInputException{
		input="sort by number";
		resultCommand=parser.acceptUserInput(input);
	}
	
	@Test(expected=InvalidInputException.class)
	public void testSortAlternate2() throws ParseException, InvalidInputException{
		input="sort by";
		resultCommand=parser.acceptUserInput(input);
	}

	@Test
	public void testCompleteBasic1() throws ParseException, InvalidInputException {
		input="complete 3";
		command.setCommand("complete");
		command.addFieldToMap(taskID, "3");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testCompleteBasic2() throws ParseException, InvalidInputException{
		input="complete task1";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "task1");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testCompleteBasic3() throws ParseException, InvalidInputException{
		input="complete office meeting";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "office meeting");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testCompleteBasic4() throws ParseException, InvalidInputException{
		input="complete \"office meeting on budget\"";
		command.setCommand("complete");
		command.addFieldToMap(taskName, "office meeting on budget");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test(expected=InvalidInputException.class)
	public void testCompleteAlternate1() throws ParseException, InvalidInputException{
		input="complete";
		resultCommand=parser.acceptUserInput(input);
	}
	
	@Test(expected=InvalidInputException.class)
	public void testCompleteAlternate2() throws ParseException, InvalidInputException{
		input="complete \"officebudget on";
		resultCommand=parser.acceptUserInput(input);
	}
	

	@Test
	public void testUndoBasic1() throws ParseException, InvalidInputException {
		input="undo";
		command.setCommand("undo");
		command.addFieldToMap("undo", "");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}

	@Test
	public void testHelp() throws ParseException, InvalidInputException {
		input="help";
		command.setCommand("help");
		command.addFieldToMap("help", "");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());

	}

}
