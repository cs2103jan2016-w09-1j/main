import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import cs2103_w09_1j.esther.Task;

public class EstherTest {
	private Logic logic = new Logic();
	
	public void deleteFile(){
		try {
			if(Files.exists(Paths.get("esther.txt"))){
				Files.delete(Paths.get("esther.txt"));
			}
		} catch (IOException e) {
			
		}
	}
	
	@Test
	public void addTest(){
		deleteFile();
		String result = logic.executeCommand("add addtask .on 03/07/2016");
		System.out.println(result);
	}
	
	@Test
	public void deleteTest(){
		deleteFile();
		System.out.println(logic.executeCommand("add deltask .on 03/07/2016"));
		System.out.println(logic.executeCommand("delete name deltask"));
	}
}
