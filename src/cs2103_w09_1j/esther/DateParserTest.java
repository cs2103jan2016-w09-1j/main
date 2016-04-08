package cs2103_w09_1j.esther;
/**
 * ============= [DATEPARSER TEST FOR ESTHER] =============
 * 
 * This class used to check the acceptable input for Parser.
 * The testing is split into two types: basic and alternate.
 * Basic refers to inputs that acceptable by Parser.
 * Alternate refers to inputs that are rejected by Parser.
 * 
 * @@author A0126000H
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateParserTest {

	DateParser dateParser;
	String input = "";
	String date = "";
	String time = "";
	String dateFormat = "";
	String timeFormat = "";
	String[] givenDate = new String[2];
	String[] emptyDate = new String[2];
	Calendar cal;
	Date currentDate;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	@Before
	public void beforeRun() {
		this.dateParser = new DateParser();
		cal = Calendar.getInstance();
	}

	@Test //Different date formats
	public void dateFormatTest() throws ParseException, InvalidInputException {
		input = "12/02/2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd/MM/yyyy: ", "12/02/2015", date);

		input = "13.02.2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd.MM.yyyy: ", "13/02/2015", date);

		input = "12-02-2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd-MM-yyyy: ", "12/02/2015", date);

		input = "25022015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format ddMMyyyy: ", "25/02/2015", date);

		input = "12 august,2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MMM,yyyy: ", "12/08/2015", date);

		input = "23 sep,2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MMM,yyyy: ", "23/09/2015", date);

		input = "DECEMBER 7,2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format MMM dd,yyyy: ", "07/12/2015", date);

		input = "12/02";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd/MM: ", "12/02/2017", date);

		input = "12 jan";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MMM: ", "12/01/2017", date);

		input = "8FEBRUARY";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format ddMMM: ", "08/02/2017", date);

		input = "november 24";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format MMM dd: ", "24/11/2016", date);

		input = "oct30";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format MMMdd: ", "30/10/2016", date);
	}

	@Test //Different time formats
	public void timeFormatTest() throws ParseException, InvalidInputException {
		input = "03:00pm";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format hh:mma : ", "15:00", time);

		input = "03:00 PM";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format hh:mm a : ", "15:00", time);

		input = "0300pm";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format hhmma : ", "15:00", time);

		input = "03:00";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format HH:mm : ", "03:00", time);

		input = "15:00";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format HH:mm : ", "15:00", time);

		input = "1500";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format HHmm : ", "15:00", time);

		input = "03pm";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format HHa : ", "15:00", time);

		input = "3 PM";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format HH a : ", "15:00", time);

		input = "15";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format HH : ", "15:00", time);

	}

	@Test //Date and time together
	public void dateTimeFormatTest() throws ParseException, InvalidInputException {

		input = "July 12 3pm";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For Date:", "12/07/2016", givenDate[0]);
		Assert.assertEquals("For Time:", "15:00", givenDate[1]);

		input = "12/02/2013 3";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For Date:", "12/02/2013", givenDate[0]);
		Assert.assertEquals("For Time:", "03:00", givenDate[1]);
	}

	@Test //Wordy date formats
	public void wordyDateTest() throws InvalidInputException {

		//today
		input = "today";
		currentDate = cal.getTime();
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For today: ", sdf.format(currentDate), givenDate[0]);

		cal.add(Calendar.DATE, 1);
		currentDate = cal.getTime();
		
		//tomorrow
		input = "tomorrow";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For tomorrow: ", sdf.format(currentDate), givenDate[0]);
		
		input = "tmr";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For tmr: ", sdf.format(currentDate), givenDate[0]);
		
		input = "tml";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For tml: ", sdf.format(currentDate), givenDate[0]);
		
		input = "tmw";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For tmw: ", sdf.format(currentDate), givenDate[0]);
		
		input = "tom";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For tom: ", sdf.format(currentDate), givenDate[0]);
		
		
		//the day after tomorrow
		cal.add(Calendar.DATE, 1);
		currentDate = cal.getTime();
		
		input = "the day after tomorrow";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For the day after tomorrow: ", sdf.format(currentDate), givenDate[0]);
		
		input = "day after";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For the day after: ", sdf.format(currentDate), givenDate[0]);
		
		input = "tda";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For the day after: ", sdf.format(currentDate), givenDate[0]);

	}

}
