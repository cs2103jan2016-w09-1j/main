import static org.junit.Assert.*;
import org.junit.Test;

public class DateParserAddonTest {
	DateParserAddon dateParserAddon = new DateParserAddon();
	@Test
	public void weekTest1(){
		String[] result = dateParserAddon.findWordyDate(" this Tuesday");
		assertNotEquals("",result[0],null);
	}
	
	public void weekTest2(){
		String[] result = dateParserAddon.findWordyDate("hello next Tuesday");
		assertNotEquals("",result[0],null);
	}
}
