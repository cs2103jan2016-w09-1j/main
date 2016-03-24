import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import java.text.SimpleDateFormat;

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
