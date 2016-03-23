import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cs2103_w09_1j.esther.Command;

public class ParserTest {
	String input;
	Command command;
	Command resultCommand;
	Parser parser;
	String taskName="taskName";
	String taskID="taskID";
	String startDate="startDate";
	String endDate="endDate";
	String startTime="startTime";
	String endTime="endTime";
	
	@Before
	public void beforeTest(){
		command=new Command();
		resultCommand=new Command();
		parser=new Parser();
		
	}

	/*
	 * Test case for add
	 */
	@Test
	public void testAdd1() throws ParseException {
		input="add Meeting";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Meeting");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testAdd2() throws ParseException{
		input="add Office Meeting";
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
		
	}
	
	@Test
	public void testAdd3() throws ParseException{
		input="add Office Meeting on today";
		command.clear();
		resultCommand.clear();
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
		String sDate=dateFormat.format(date);
		String sTime=timeFormat.format(date);
		command.addFieldToMap(endDate, sDate);
		command.addFieldToMap(endTime, sTime);
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
		
	}
	
	@Test
	public void testAdd4() throws ParseException{
		input="add Office Meeting on today 3pm";
		command.clear();
		resultCommand.clear();
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		String sDate=dateFormat.format(date);
		command.addFieldToMap(endDate, sDate);
		command.addFieldToMap(endTime, "15:00");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testAdd5() throws ParseException{
		input="add Office Meeting on 3pm today";
		command.clear();
		resultCommand.clear();
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date=new Date();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
		String sDate=dateFormat.format(date);
		command.addFieldToMap(endDate, sDate);
		command.addFieldToMap(endTime, "15:00");
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
	}
	
	@Test
	public void testAdd6() throws ParseException{
		input="add Office Meeting on 23/03/2016";
		command.clear();
		resultCommand.clear();
		command.setCommand("add");
		command.addFieldToMap(taskName, "Office Meeting");
		Date date=new Date();
		SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
		String sTime=timeFormat.format(date);
		command.addFieldToMap(endDate, "23/03/2016");
		command.addFieldToMap(endTime, sTime);
		resultCommand=parser.acceptUserInput(input);
		Assert.assertEquals(command.getParameters(), resultCommand.getParameters());
		
	}
}
