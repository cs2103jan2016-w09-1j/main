import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class StorageTest {

	public String constructPattern() {
		String idnoString = constructIDPattern();
		String dateString = constructDatePattern();
		String nameString = constructNamePattern();
		String prioString = constructPrioPattern();
		String compString = constructCompPattern();
		String finalPattern = idnoString + dateString + nameString + prioString + compString
				+ "\\n";
		return finalPattern;
	}

	private String constructCompPattern() {
		String compString = "Completed: (true|false)";
		return compString;
	}

	private Pattern compilePattern(String s) {
		return Pattern.compile(s);
	}

	private boolean findMatch(String s, Pattern p) {
		return p.matcher(s).find();
	}

	private String constructPrioPattern() {
		String prioString = "Priority: (\\d+)\\| ";
		return prioString;
	}

	private String constructNamePattern() {
		String nameString = "([^\\|]+)\\| ";
		return nameString;
	}

	private String constructDatePattern() {
		String dateString = "\\[([^\\]]+)\\] ";
		return dateString;
	}

	private String constructIDPattern() {
		String idnoString = "ID\\: (\\d+)\\| ";
		return idnoString;
	}

	@Test
	public void checkTaskParser(){
		String testString = "ID: 01| [01/12/2016] test task| Priority: 33| Completed: true";
		
		Pattern idP = compilePattern(constructIDPattern());
		Pattern nameP = compilePattern(constructNamePattern());
		Pattern dateP = compilePattern(constructDatePattern());
		Pattern prioP = compilePattern(constructPrioPattern());
		Pattern compP = compilePattern(constructCompPattern());
		Pattern finalP = compilePattern(constructIDPattern()+constructDatePattern()+constructNamePattern()+constructPrioPattern()+constructCompPattern());
		Matcher matcher = finalP.matcher(testString);
		if(matcher.find()){
			for(int i = 0; i < 6; i++){
				System.out.println(matcher.group(i));
			}
		}
	}

}
