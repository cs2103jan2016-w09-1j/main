import java.util.Calendar;
import java.util.Date;
import java.util.PrimitiveIterator.OfDouble;

import com.sun.xml.internal.ws.util.StringUtils;

import java.text.SimpleDateFormat;

public class DateParserAddon {

	private final String WHITESPACE = " ";
	private final String[] thisWeekWords = { "this", "coming" };
	private final String[] nextWeekWords = { "next" };
	private final String[] dayWords = { "today", "the day after", "tomorrow" };
	private final String[] weekDayWords = { "mon", "tue", "wed", "thu", "fri", "sat", "sun" };

	private Calendar today = Calendar.getInstance();

	String[] findWordyDate(String string) {
		Calendar newDay = (Calendar) today.clone();
		boolean containsThisWeekWords, containsNextWeekWords = false;
		int dayOfWeek;
		containsThisWeekWords = containsStrings(string, thisWeekWords);
		if (!containsThisWeekWords) {
			containsNextWeekWords = containsStrings(string, nextWeekWords);
		}
		if (containsThisWeekWords || containsNextWeekWords) {
			dayOfWeek = findDayInWeek(string);
			if (dayOfWeek != -1) {
				int weeksToAdd = containsNextWeekWords ? 1 : 0;
				newDay.add(Calendar.WEEK_OF_YEAR, weeksToAdd);
				int daysToAdd = differenceInDays(today.get(Calendar.DAY_OF_WEEK),dayOfWeek);
				newDay.add(Calendar.DAY_OF_YEAR, daysToAdd);
			}
		}
		return null;
	}

	private int findDayInWeek(String string) {
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

	String[] findTime(String string) {
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
