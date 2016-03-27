import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103_w09_1j.esther.Task;

public class EstherTest {
	String pathString = "esther.txt";
	Path saveLoc = Paths.get(pathString);
	
	public void deleteFile(){
		try {
			if(Files.exists(Paths.get("esther.txt"))){
				Files.delete(Paths.get("esther.txt"));
			}
		} catch (IOException e) {
			
		}
	}
	
	private Logic logic;
	
	@Before
	public void init() throws ParseException, IOException {
		logic = new Logic();
	}
	
	@Test
	public void addTest(){
		assertTrue(logic.executeCommand("add addtask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("add addtask2 on 23/3/2016").contains("success"));
	}
	
	@Test
	public void deleteNameTest(){
		// equivalence partition for delete based on name
		assertTrue(logic.executeCommand("add deltask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("delete deltask").contains("success"));
	}
	
	@Test
	public void deleteIDTest(){
		// equivalence partition for delete based on id
		Task task = new Task();
		task.setGlobalId(0);
		assertTrue(logic.executeCommand("add deltask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("delete 0").contains("success"));
	}
	
	@Test
	public void updateNameTest(){
		// equivalence partition for updating different fields based on name reference
		assertTrue(logic.executeCommand("add updTask on 03/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update updTask taskName to updatedTask").contains("success"));
		assertTrue(logic.executeCommand("update updatedTask date to 04/07/2016").contains("success"));
		assertTrue(logic.executeCommand("update updatedTask priority to 1").contains("success"));
	}
	
	@After
	public void cleanUp(){
		System.out.println("Contents in esther.txt:");
		System.out.println("-----------------------");
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(saveLoc);
			while (reader.ready()) {
				System.out.println((reader.readLine()));
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block for tryLoadFile
			e.printStackTrace();
		}
		deleteFile();
	}
}
