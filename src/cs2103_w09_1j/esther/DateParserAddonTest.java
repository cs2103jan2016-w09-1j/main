package cs2103_w09_1j.esther;
import static org.junit.Assert.*;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;

public class DateParserAddonTest {
	DateParserAddon dateParserAddon = new DateParserAddon();
	String defaultDateFormatPattern = "EEE MMM dd HH:mm:ss zzz yyyy";
	SimpleDateFormat defaultDateFormat = new SimpleDateFormat(defaultDateFormatPattern);
	Calendar today = Calendar.getInstance();
	
	@Test
	public void calTest(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	}
	
	@Test
	public void weekTest1(){
		String result = dateParserAddon.findWordyDate(" this Tuesday");
		assertNotEquals("result was null",result,null);
		if(result != null){
			try {
				Date date = defaultDateFormat.parse(result);
				Calendar calendar = dateToCalendar(date);
				assertTrue(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY);
				assertTrue(calendar.compareTo(today) > 0);
			} catch (ParseException e) {
				fail();
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void weekTest2(){
		String result = dateParserAddon.findWordyDate(" hello next wed");
		assertNotEquals("result was null",result,null);
		if(result != null){
			try {
				Date date = defaultDateFormat.parse(result);
				Calendar calendar = dateToCalendar(date);
				assertTrue(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY);
				assertTrue(calendar.compareTo(today) > 0);
				assertEquals(calendar.get(Calendar.WEEK_OF_YEAR),today.get(Calendar.WEEK_OF_YEAR) + 1);
			} catch (ParseException e) {
				fail();
				e.printStackTrace();
			}
		}
	}
	
	public static Calendar dateToCalendar(Date date){ 
	  Calendar cal = Calendar.getInstance();
	  cal.setTime(date);
	  return cal;
	}
}
