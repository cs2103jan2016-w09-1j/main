package cs2103_w09_1j.esther;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DateParser {
	private final static ArrayList<String> dateFormatList = new ArrayList<String>(Arrays.asList("dd/MM/yy",
			"dd.MM.yy", "dd-MM-yy", "dd MM yy", "ddMMyy", "d MMM yy", "dMMM yy",
			"d MMM,yy", "MMM d, yy", "dd/MM", "dd.MM","d MMM", "dMMM", "MMM d", "MMMd"));

	private final static ArrayList<String> timeFormatList = new ArrayList<String>(
			Arrays.asList("hh:mma", "hh:mm a", "hhmma", "hhmm a", "HH:mm", "HHmm", "hha", "hh a", "HH"));

	private final static String WHITESPACE = " ";
	private final static String FORWARDSLASH = "/";
	private final static String FULLSTOP = ".";
	private final static String HYPHEN = "-";
	private final static String FULLMONTH = "MMM";
	private final static String HALFYEAR = "yy";
	private final static String[] weekWords = { "this", "coming", };
	private final static String[] nextWeekWords = { "next" };

	private final static HashMap<String, String> monthWords = createMonthMap();
	private final static HashMap<String, Integer> dayWords = createDayMap();
	private final static HashMap<String, Integer> weekDayWords = createWeekMap();

	private final static int daysInAWeek = 7;
	private final static String defaultDateFormat = "dd/MM/yyyy";
	private final static String defaultTimeFormat = "HH:mm";

	private final static SimpleDateFormat convertToDateFormat = new SimpleDateFormat(defaultDateFormat);
	private final static SimpleDateFormat convertToTimeFormat = new SimpleDateFormat(defaultTimeFormat);

	private final static String ERROR_DIFFERENTDATE = "The date you entered is not correct. Please check again.";
	private final static String ERROR_DATEFORMAT = "Improper date format.";

	// @@author A0126000H
	public static void main(String[] args) throws InvalidInputException {
		DateParser dp = new DateParser();
		String[] dt = dp.getDateTime("1/4/2016");
		if (dt[0] != null)
			System.out.println("Date " + dt[0]);
		if (dt[1] != null)
			System.out.println("Time " + dt[1]);
	}

	// @@author A0126000H
	public String[] getDateTime(String input) throws InvalidInputException {
		String[] dateTime = new String[2];
		String oldInput = input;

		input = input.toLowerCase();
		DateParser dp = new DateParser();

		// Check for wordy date first
		String wordyDate = dp.getWordyDateFormat(input);
		input = dp.getProperDateTime(input);

		String[] twentyFourTime = find24HTime(input);
		if (twentyFourTime[0] != null) {
			dateTime[1] = twentyFourTime[0];
			input = twentyFourTime[1];
		}
		while (input != null) {
			oldInput = input;
			if (dateTime[0] == null) {
				String dateFormat = dp.getDateFormat(input);
				if (!dateFormat.isEmpty()) {
					dateTime[0] = dp.getDate(input, dateFormat);
					// SimpleDateFormat givenDateFormat = new
					// SimpleDateFormat(dateFormat);
					// Date inputDate = givenDateFormat.parse(input);
					// String givenDate = givenDateFormat.format(inputDate);
					// String givenMonth = null;
					// String givenYear = null;
					// System.out.println(dateFormat + " " + givenDate);
					// if (dateFormat.contains(FULLMONTH)) {
					// givenMonth = getMonth(givenDate);
					// if (input.contains(givenMonth.toLowerCase())) {
					// givenDate = givenDate.replace(givenMonth.substring(0, 3),
					// givenMonth);
					// }
					// }
					// if (dateFormat.contains(HALFYEAR)) {
					// Calendar cal = Calendar.getInstance();
					// cal.setTime(inputDate);
					// givenYear = givenDate.substring(givenDate.length() - 2,
					// givenDate.length());
					// // System.out.println("YEAR" + cal.get(Calendar.YEAR));
					// if
					// (input.contains(String.valueOf(cal.get(Calendar.YEAR))))
					// {
					// givenDate = givenDate.substring(0, givenDate.length() -
					// 2) + cal.get(Calendar.YEAR);
					// }
					// }
					String givenDate = convertToProperDateFormat(input, dateFormat);
					// System.out.println(givenYear);
					int lastIndexOfDate = input.indexOf(givenDate.toLowerCase());
					try {
						input = input.substring(lastIndexOfDate + givenDate.length());
					} catch (StringIndexOutOfBoundsException sioobe) {
						System.out.println(lastIndexOfDate);
						System.out.println(input);
						System.out.println(givenDate);
						throw sioobe;
					}
					input = input.trim();
					// System.out.println(input);
				}
			}
			if (dateTime[1] == null) {
				String timeFormat = dp.getTimeFormat(input);
				if (!timeFormat.isEmpty()) {
					dateTime[1] = dp.getTime(input, timeFormat);
					SimpleDateFormat givenTimeFormat = new SimpleDateFormat(timeFormat);
					Date inputTime = null;
					try {
						inputTime = givenTimeFormat.parse(input);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String givenTime = givenTimeFormat.format(inputTime);
					if (givenTime.charAt(0) == '0') {
						givenTime = givenTime.substring(1);
					}
					int lastIndexOfTime = input.toLowerCase().lastIndexOf(givenTime.toLowerCase());
					input = input.substring(lastIndexOfTime + givenTime.length());
					input = input.trim();
				}
			}
			input = dp.getProperDateTime(input);
			if (oldInput == input) {
				// looping indefinitely, so cut the string by 1
				input = input.substring(1);
			}
		}
		if (wordyDate != null && dateTime[0] != null)

		{
			if (!(wordyDate.equals(dateTime[0]))) {
				throw new InvalidInputException(ERROR_DIFFERENTDATE);
			}
		}
		if (wordyDate != null)

		{
			dateTime[0] = wordyDate;
		}
		return dateTime;

	}

	// @@author A0126000H
	private String getProperDateTime(String input) {

		// Have to check if number or is a month
		char[] inputArray = input.toCharArray();
		int firstIntegerIndex = -1;
		for (int i = 0; i < inputArray.length; i++) {

			// Check for month
			if ((inputArray.length - i) >= 3) {
				String subsequentThreeLetters = input.substring(i, i + 3);
				List<String> monthList = new ArrayList<String>(monthWords.keySet());
				if (monthList.contains(subsequentThreeLetters.toLowerCase())) {
					firstIntegerIndex = i;
					break;
				}
			}
			// Check for number
			try {
				Integer.parseInt(String.valueOf(inputArray[i]));
				firstIntegerIndex = i;
				break;
			} catch (NumberFormatException nfe) {
				continue;
			}
		}
		if (firstIntegerIndex == -1) {
			return null;
		}
		return input.substring(firstIntegerIndex);
	}

	private String convertToProperDateFormat(String input, String dateFormat) {
		String[] dateFormatSplitByWhiteSpace = null;
		String[] inputSplitByWhiteSpace = input.split(WHITESPACE);
		String date = "";
		try {
			dateFormatSplitByWhiteSpace = dateFormat.split(WHITESPACE);
		} catch (PatternSyntaxException pse) {
			return inputSplitByWhiteSpace[0];
		}
		for (int i = 0; i < dateFormatSplitByWhiteSpace.length; i++) {
			date += inputSplitByWhiteSpace[i] + " ";
		}
		return date.trim();
	}

	// @@author A0126000H
	private String getWordyDateFormat(String input) {

		Calendar cal = Calendar.getInstance();
		int specificDayValue = checkForWordInMap(input, weekDayWords);

		// Case 1: use tomorrow, day after, tomorrow
		if (specificDayValue == -1) {
			int subsequentDayValue = checkForWordInMap(input, dayWords);

			if (subsequentDayValue == -1) {
				// No wordy date format
				return null;
			}
			cal.add(Calendar.DATE, subsequentDayValue);
		} else {
			int currentDayValue = cal.get(Calendar.DAY_OF_WEEK);

			if (currentDayValue < specificDayValue) {
				for (int i = 0; i < nextWeekWords.length; i++) {
					if (input.toLowerCase().contains(nextWeekWords[i])) {
						cal.add(Calendar.DATE, daysInAWeek);
					}
				}
			}
			cal.set(Calendar.DAY_OF_WEEK, specificDayValue - 1);
		}
		return convertToDateFormat.format(cal.getTime());
	}

	// @@author A0126000H
	private String getDateFormat(String input) {
		for (int i = 0; i < dateFormatList.size(); i++) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatList.get(i));
				dateFormat.parse(input);
				return dateFormatList.get(i);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				continue;
			}
		}

		return "";
	}

	// @@author A0126000H
	private String getTimeFormat(String input) {
		for (int i = 0; i < timeFormatList.size(); i++) {
			try {
				SimpleDateFormat timeFormat = new SimpleDateFormat(timeFormatList.get(i));
				Date foundDate = timeFormat.parse(input);
				return timeFormatList.get(i);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				continue;
			}
		}
		return "";
	}

	// @@author A0126000H
	private String getDate(String input, String givenDateFormat) throws InvalidInputException {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(givenDateFormat);
		try {
			date = dateFormat.parse(input);
		} catch (ParseException e) {
			throw new InvalidInputException(ERROR_DATEFORMAT);
		}

		// Check if no year
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.YEAR) == 1970) {
			cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
			date = cal.getTime();
			// For next year
			if (date.before(currentDate)) {
				cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
				date = cal.getTime();
			}
		}
		return convertToDateFormat.format(date);

	}

	// @@author A0126000H
	private String getTime(String input, String givenTimeFormat) {
		SimpleDateFormat timeFormat = new SimpleDateFormat(givenTimeFormat);
		Date date = null;
		try {
			date = timeFormat.parse(input);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertToTimeFormat.format(date);
	}

	// @@author A0126000H
	private String getMonth(String input) {
		for (Entry<String, String> e : monthWords.entrySet()) {
			if (input.toLowerCase().contains(e.getKey())) {
				return e.getValue();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param input
	 * @return
	 * @@author A0127572A
	 */
	protected String[] find24HTime(String input) {
		String[] result = new String[2];

		if (input == null) {
			return result;
		}

		String regex = "(\\A|\\s)(\\d{4})(\\s|\\z)";
		Matcher matcher = Pattern.compile(regex).matcher(input);

		// find all matches of 4 integers
		boolean lastLoopFoundMatch = true;
		boolean foundMatch;
		while (lastLoopFoundMatch) {
			foundMatch = matcher.find();
			if (foundMatch) {
				// assume valid 24H time
				result[0] = matcher.group(2).substring(0, 2) + ":" + matcher.group(2).substring(2);
				result[1] = input.substring(0, matcher.start()) + input.substring(matcher.end());
				return result;
			}
			// otherwise this is an invalid 24H time, ignore
			lastLoopFoundMatch = foundMatch;
		}
		return result;
	}

	/**
	 * 
	 * @param string
	 * @param index
	 * @return
	 * @@author A0127572A
	 */
	private boolean charAtIndexOfStringIsSpace(String string, int index) {
		return string.charAt(index) == ' ';
	}

	// @@author A0126000H
	private static HashMap<String, Integer> createDayMap() {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		result.put("today", 0);
		result.put("tmr", 1);
		result.put("tmw", 1);
		result.put("tom", 1);
		result.put("day after", 2);
		result.put("tda", 2);
		return result;
	}

	// @@author A0126000H
	private static HashMap<String, Integer> createWeekMap() {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		result.put("sun", Calendar.SUNDAY + 1);
		result.put("mon", Calendar.MONDAY + 1);
		result.put("tues", Calendar.TUESDAY + 1);
		result.put("wed", Calendar.WEDNESDAY + 1);
		result.put("thu", Calendar.THURSDAY + 1);
		result.put("fri", Calendar.FRIDAY + 1);
		result.put("sat", Calendar.SATURDAY + 1);
		return result;
	}

	/**
	 * 
	 * @return
	 * @author A0126000H
	 */
	private static HashMap<String, String> createMonthMap() {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("jan", "January");
		result.put("feb", "February");
		result.put("mar", "March");
		result.put("apr", "April");
		result.put("may", "May");
		result.put("jun", "June");
		result.put("jul", "July");
		result.put("aug", "August");
		result.put("sep", "September");
		result.put("oct", "October");
		result.put("nov", "November");
		result.put("dec", "December");
		return result;
	}

	/**
	 * 
	 * @param input
	 * @param map
	 * @return
	 * @author A0126000H
	 */
	private int checkForWordInMap(String input, HashMap<String, Integer> map) {
		for (Entry<String, Integer> e : map.entrySet()) {
			if (input.toLowerCase().contains(e.getKey())) {
				return e.getValue();
			}
		}
		return -1;
	}
}
