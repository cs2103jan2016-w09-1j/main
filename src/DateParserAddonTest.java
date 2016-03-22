import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class DateParserAddonTest {
	DateParserAddon dateParserAddon = new DateParserAddon();
	@Test
	public void Test(){
		Calendar today = Calendar.getInstance();
		int day = today.get(Calendar.DAY_OF_WEEK);
		System.out.println(today.getTime().toString());
		System.out.println(day);
		System.out.println(Calendar.SATURDAY);
	}
	
	@Test
	public void weekTest1(){
		String[] result = dateParserAddon.findWordyDate(" this Tuesday");
		assertNotEquals("",result[0],null);
	}
	
	@Test
	public void weekTest2(){
		String[] result = dateParserAddon.findWordyDate("hello next Tuesday");
		assertNotEquals("",result[0],null);
	}
}
