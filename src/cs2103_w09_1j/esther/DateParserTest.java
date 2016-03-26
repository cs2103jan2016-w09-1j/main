package cs2103_w09_1j.esther;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

	@Before
	public void beforeRun() {
		this.dateParser=new DateParser();
	}

	@Test
	public void dateFormatTest() throws ParseException, InvalidInputException {
		input = "12/02/2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd/MM/yyyy: ", "12/02/2015", date);

		input = "12.02.2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd.MM.yyyy: ", "12/02/2015", date);

		input = "12-02-2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd-MM-yyyy: ", "12/02/2015", date);

		input = "12 02 2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MM yyyy: ", "12/02/2015", date);

		input = "12022015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format ddMMyyyy: ", "12/02/2015", date);

		input = "12 Feb 2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MMM yyyy: ", "12/02/2015", date);

		input = "12 february 2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MMM yyyy: ", "12/02/2015", date);

		input = "12feb 2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format ddMMM yyyy: ", "12/02/2015", date);

		input = "12 february, 2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MMM, yyyy: ", "12/02/2015", date);

		input = "12 FEB, 2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MMM, yyyy: ", "12/02/2015", date);

		input = "Feb 12, 2015";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format MMM dd, yyyy: ", "12/02/2015", date);

		input = "12/02";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd/MM: ", "12/02/2017", date);

		input = "12 feb";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format dd MMM: ", "12/02/2017", date);

		input = "12FEBRUARY";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format ddMMM: ", "12/02/2017", date);

		input = "feb 12";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format MMM dd: ", "12/02/2017", date);

		input = "feb12";
		date = dateParser.getDateTime(input)[0];
		Assert.assertEquals("For Format MMMdd: ", "12/02/2017", date);
	}

	@Test
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

		System.out.println(1);
		input = "0300 pm";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format hhmm a : ", "15:00", time);

		System.out.println(2);
		input = "03:00";
		time = dateParser.getDateTime(input)[1];
		Assert.assertEquals("For Format HH:mm : ", "03:00", time);

		System.out.println(3);
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

	@Test
	public void dateTimeFormatTest() throws ParseException, InvalidInputException {

		input = "February 12 3pm";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For Date:", "12/02/2017", givenDate[0]);
		Assert.assertEquals("For Time:", "15:00", givenDate[1]);
		
		input= "12/02/2013 3";
		givenDate = dateParser.getDateTime(input);
		Assert.assertEquals("For Date:", "12/02/2013", givenDate[0]);
		Assert.assertEquals("For Time:", "03:00", givenDate[1]);
	}
}
