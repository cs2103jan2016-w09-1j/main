package cs2103_w09_1j.esther;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;

public class DateParserTest {

	DateParser dp;
	String input = "";
	String date = "";
	String time = "";
	String dateFormat = "";
	String timeFormat = "";
	String[] givenDate = new String[2];

	@Before
	public void beforeRun() {
		dp = new DateParser();
	}

	@Test
	public void dateFormatTest() throws ParseException {
		input = "12/02/2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd/MM/yyyy: ", "dd/MM/yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd/MM/yyyy: ", "12/02/2015", date);

		input = "12.02.2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd.MM.yyyy: ", "dd.MM.yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd.MM.yyyy: ", "12/02/2015", date);

		input = "12-02-2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd-MM-yyyy: ", "dd-MM-yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd-MM-yyyy: ", "12/02/2015", date);

		input = "12 02 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd MM yyyy: ", "dd MM yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd MM yyyy: ", "12/02/2015", date);

		input = "12022015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format ddMMyyyy: ", "ddMMyyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format ddMMyyyy: ", "12/02/2015", date);

		input = "12 Feb 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd MMM yyyy: ", "dd MMM yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd MMM yyyy: ", "12/02/2015", date);

		input = "12 february 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd MMM yyyy: ", "dd MMM yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd MMM yyyy: ", "12/02/2015", date);

		input = "12feb2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format ddMMMyyyy: ", "ddMMMyyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format ddMMMyyyy: ", "12/02/2015", date);

		input = "12FEBRUARY 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format ddMMMyyyy: ", "ddMMMyyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format ddMMMyyyy: ", "12/02/2015", date);

		input = "12 february, 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd MMM, yyyy: ", "dd MMM, yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd MMM, yyyy: ", "12/02/2015", date);

		input = "12 FEB, 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd MMM, yyyy: ", "dd MMM, yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd MMM, yyyy: ", "12/02/2015", date);

		input = "Feb 12 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format MMM dd yyyy: ", "MMM dd yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format MMM dd yyyy: ", "12/02/2015", date);

		input = "Feb 12, 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format MMM dd, yyyy: ", "MMM dd, yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format MMM dd, yyyy: ", "12/02/2015", date);

		input = "february12 2015";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format MMMdd yyyy: ", "MMMdd yyyy", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format MMMdd yyyy: ", "12/02/2015", date);

		input = "12/02";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd/MM: ", "dd/MM", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd/MM: ", "12/02/2017", date);

		input = "12.02";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd.MM: ", "dd.MM", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd.MM: ", "12/02/2017", date);

		input = "12 feb";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format dd MMM: ", "dd MMM", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format dd MMM: ", "12/02/2017", date);

		input = "12FEBRUARY";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format ddMMM: ", "ddMMM", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format ddMMM: ", "12/02/2017", date);

		input = "feb 12";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format MMM dd: ", "MMM dd", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format MMM dd: ", "12/02/2017", date);

		input = "feb12";
		dateFormat = dp.getDateFormat(input);
		Assert.assertSame("For Format MMMdd: ", "MMMdd", dateFormat);
		date = dp.getDate(input, dateFormat);
		Assert.assertEquals("For Format MMMdd: ", "12/02/2017", date);
	}

	@Test
	public void timeFormatTest() throws ParseException {
		input = "03:00pm";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format hh:mma : ", "hh:mma", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format hh:mma : ", "15:00", time);

		input = "03:00 PM";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format hh:mm a : ", "hh:mm a", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format hh:mm a : ", "15:00", time);

		input = "03-00PM";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format hh-mma : ", "hh-mma", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format hh-mma : ", "15:00", time);

		input = "3-00 pm";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format hh-mm a : ", "hh-mm a", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format hh-mm a : ", "15:00", time);

		input = "0300pm";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format hhmma : ", "hhmma", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format hhmma : ", "15:00", time);

		input = "0300 pm";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format hhmm a : ", "hhmm a", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format hhmm a : ", "15:00", time);

		input = "03:00";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format HH:mm : ", "HH:mm", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format HH:mm : ", "03:00", time);

		input = "15:00";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format HH:mm : ", "HH:mm", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format HH:mm : ", "15:00", time);

		input = "15-00";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format HH-mm : ", "HH-mm", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format HH-mm : ", "15:00", time);

		input = "15.00";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format HH.mm : ", "HH.mm", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format HH.mm : ", "15:00", time);

		input = "1500";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format HHmm : ", "HHmm", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format HHmm : ", "15:00", time);

		input = "03pm";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format hha : ", "hha", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format HHa : ", "15:00", time);

		input = "3 PM";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format hh a : ", "hh a", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format HH a : ", "15:00", time);

		input = "15";
		timeFormat = dp.getTimeFormat(input, "");
		Assert.assertSame("For Format HH : ", "HH", timeFormat);
		time = dp.getTime(input, timeFormat);
		Assert.assertEquals("For Format HH : ", "15:00", time);

	}

	@Test
	public void dateTimeFormatTest() throws ParseException {
		SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat defaultTimeFormat = new SimpleDateFormat("HH:mm");

		input = "February 12 2013 3pm";
		dateFormat = dp.getDateFormat(input);
		timeFormat = dp.getTimeFormat(input, dateFormat);
		givenDate = dp.getDateTime(input, dateFormat + " " + timeFormat);
		Assert.assertEquals("For Date:", "12/02/2013", givenDate[0]);
		Assert.assertEquals("For Time:", "15:00", givenDate[1]);
		
		input= "12/02/2013 3";
		dateFormat = dp.getDateFormat(input);
		timeFormat = dp.getTimeFormat(input, dateFormat);
		givenDate = dp.getDateTime(input, dateFormat + " " + timeFormat);
		Assert.assertEquals("For Date:", "12/02/2013", givenDate[0]);
		Assert.assertEquals("For Time:", "03:00", givenDate[1]);
	}
}
