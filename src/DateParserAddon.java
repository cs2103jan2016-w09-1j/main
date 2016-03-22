import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DateParserAddon {
	
	private final String WHITESPACE = " ";
	private final String[] thisWeekWords = {"this","coming"};
	private final String[] nextWeekWords = {"next"};
	private final String[] dayWords = {"today","the day after", "tomorrow"};
	private final String[] weekDayWords = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

	private Calendar today = Calendar.getInstance();

	String[] findWordyDate(String string) {
		boolean containsThisWeekWords = false;
		for (int i = 0; i < thisWeekWords.length; i++) {
			if(string.contains(thisWeekWords[i])){
				containsThisWeekWords = true;
			}
		}
		if(!containsThisWeekWords) {
			
		}
		return null;
	}
	
	String[] findTime(String string) {
		return null;
	}
}
