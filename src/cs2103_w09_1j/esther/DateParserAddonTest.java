package cs2103_w09_1j.esther;
import static org.junit.Assert.*;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;

/**
 * 
 * @author Jeremy Hon
 * @@A0127572A
 *
 */
public class DateParserAddonTest {
	DateParser dateParser = new DateParser();
	String defaultDateFormatPattern = "EEE MMM dd HH:mm:ss zzz yyyy";
	SimpleDateFormat defaultDateFormat = new SimpleDateFormat(defaultDateFormatPattern);
	Calendar today = Calendar.getInstance();
	
	
	@Test
	public void generalTest(){
	    String test = "23/7/2016 2000 ";
	    String[] result = dateParser.find24HTime(test);
	    System.out.println(Arrays.toString(result));
	}
	
	public static Calendar dateToCalendar(Date date){ 
	  Calendar cal = Calendar.getInstance();
	  cal.setTime(date);
	  return cal;
	}
}
