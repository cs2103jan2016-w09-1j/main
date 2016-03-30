package cs2103_w09_1j.esther;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * @author Jeremy Hon
 * @@A0127572A
 *
 */
public class DateParserAddon {

	private final String WHITESPACE = " ";
	private final String[] thisWeekWords = { "this", "coming" };
	private final String[] nextWeekWords = { "next" };
	private final String[] dayWords = { "today", "the day after", "tomorrow" };
	private final String[] weekDayWords = { "sun", "mon", "tue", "wed", "thu", "fri", "sat" };

	private Calendar today = Calendar.getInstance();

	String findWordyDate(String dateStr) {
		String result = null;
		Calendar newDay = (Calendar) today.clone();
		
		//look for "this", "coming" and "next"
		result = findDayOfWeekWords(dateStr, newDay);
		
		return result;
	}
	
	/**
	 * Finds strings of 4 integers (XXXX) with neighboring whitespace in the input.
	 * @param input
	 * 	String to look for 4 integers
	 * @return 
	 * 	In slot[0], the found string
	 * 	In slot[1], the remainder of the string after the found string is removed
	 * @author Jeremy Hon
	 */
	String[] find24HTime(String input) {
	    String[] result = new String[2];
	    String regex = "\\d{4}";
	    Matcher matcher = Pattern.compile(regex).matcher(input);
	    
	    //find all matches of 4 integers
	    boolean lastLoopFoundMatch = true;
	    boolean foundMatch;
	    while(lastLoopFoundMatch){
		foundMatch = matcher.find();
		if(foundMatch){
		    //assume valid 24H time can only have space characters next to it
		    //or are at the ends of the string
		    //identify valid 24H times
		    if((matcher.start() == 0 || charAtIndexOfStringIsSpace(input, matcher.start()-1) 
			    && (matcher.end() == input.length() || charAtIndexOfStringIsSpace(input, matcher.end()+1)))) {
			//this is a valid 24H time
			result[0] = matcher.group();
			result[1] = input.substring(0, matcher.start()) + input.substring(matcher.end()).trim();
			return result;
		    }
		    //otherwise this is an invalid 24H time, ignore
		}
		lastLoopFoundMatch = foundMatch;
	    }
	    return result;
	}
	
	private boolean charAtIndexOfStringIsSpace(String string, int index){
	    return string.charAt(index) == ' ';
	}

	/**
	 * @param dateStr
	 * @param result
	 * @param newDay
	 * @return
	 */
	private String findDayOfWeekWords(String dateStr, Calendar newDay) {
		String result = null;
		boolean containsThisWeekWords, containsNextWeekWords = false;
		int dayOfWeek;
		//contains "this" or "coming"
		containsThisWeekWords = containsStrings(dateStr, thisWeekWords);
		if (!containsThisWeekWords) {
			//contains "next"
			containsNextWeekWords = containsStrings(dateStr, nextWeekWords);
		}
		if (containsThisWeekWords || containsNextWeekWords) {
			//contains "sun" - "sat"
			dayOfWeek = findDayOfWeek(dateStr);
			if (dayOfWeek != -1) {
				//advance week if contains "next"
				if(containsNextWeekWords){
					newDay.add(Calendar.WEEK_OF_YEAR, 1);
				}
				//set day of week
				newDay.set(Calendar.DAY_OF_WEEK, dayOfWeek);
				//advance week if day was set to before today
				if(newDay.compareTo(today) < 0) {
					newDay.add(Calendar.WEEK_OF_YEAR, 1);
				}
				result = newDay.getTime().toString();
			}
		}
		return result;
	}

	private int findDayOfWeek(String string) {
		int day = -2;
		for (int i = 0; i < weekDayWords.length; i++) {
			if (containsIgnoreCase(string, weekDayWords[i])) {
				day = i;
				break;
			}
		}
		return day + 1;
	}

	private int differenceInDays(int currentDay, int targetDay) {
		int result = (7 + targetDay - currentDay) % 7;
		return result;
	}

	private boolean containsStrings(String string, String[] strings) {
		boolean containsString = false;
		for (int i = 0; i < strings.length; i++) {
			if (containsIgnoreCase(string, strings[i])) {
				containsString = true;
				break;
			}
		}
		return containsString;
	}
	
	/**
	 * This method assumes the substring is present
	 * @param originalString
	 * @param substringToRemove
	 * @return
	 */
	private String removeSubstring(String originalString, String substringToRemove){
		String modifiedStr = Pattern.compile(substringToRemove, Pattern.CASE_INSENSITIVE).matcher(originalString).replaceAll("");
		return modifiedStr;
	}
	
	private String removeSubstrings(String originalString, String[] substrings){
		String modifiedStr = originalString;
		for (int i = 0; i < substrings.length; i++) {
			modifiedStr = removeSubstring(modifiedStr, substrings[i]);
		}
		return modifiedStr;
	}

	String findTime(String string) {
		return null;
	}

	/**
	 * Thanks to user icza on stackoverflow
	 * 
	 * @param src
	 * @param what
	 * @return
	 */
	public static boolean containsIgnoreCase(String src, String what) {
		final int length = what.length();
		if (length == 0)
			return true; // Empty string is contained

		final char firstLo = Character.toLowerCase(what.charAt(0));
		final char firstUp = Character.toUpperCase(what.charAt(0));

		for (int i = src.length() - length; i >= 0; i--) {
			// Quick check before calling the more expensive regionMatches()
			// method:
			final char ch = src.charAt(i);
			if (ch != firstLo && ch != firstUp)
				continue;

			if (src.regionMatches(true, i, what, 0, length))
				return true;
		}

		return false;
	}
}
