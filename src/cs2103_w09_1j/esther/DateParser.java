package cs2103_w09_1j.esther;

/**
 * ============= [DATEPARSER COMPONENT FOR ESTHER] =============
 * 
 * This class checks if the input given by the user has a date and/or a time
 * and covert them to the proper date and time format given by Esther.
 * The current proper date and time format is dd/MM/yyyy and HH:mm respectively.
 * There are two types of date format : wordy date and proper date. 
 *
 * Acceptable proper date format is given in: 
 * ArrayList: dateFormatList
 * 
 * Acceptable wordy date format is given in:
 * LinkedListHashMap: monthWords, dayWords,weekDayWords
 * String[]: thisWeekWords, nextWeekWords.
 * 
 * Acceptable proper time format is given in:
 * ArrayList: timeFormatList
 * 
 * @@author A0126000H
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DateParser {

	// Possible date and time formats
	private final static ArrayList<String> dateFormatList = new ArrayList<String>(Arrays.asList("dd/MM/yy", "dd.MM.yy",
			"dd-MM-yy", "ddMMyy", "dd MMM,yy", "MMM dd,yy", "dd/MM", "dd.MM", "dd MMM", "ddMMM", "MMM dd", "MMMd"));
	private final static ArrayList<String> timeFormatList = new ArrayList<String>(
			Arrays.asList("hh:mma", "hh:mm a", "hha", "hh a", "hhmma", "hhmm a", "HH:mm", "HHmm", "HH"));

	// Name of the possible wordy dates
	private final static LinkedHashMap<String, String> monthWords = createMonthMap();
	private final static LinkedHashMap<String, Integer> dayWords = createDayMap();
	private final static LinkedHashMap<String, Integer> weekDayWords = createWeekMap();
	private final static String[] thisWeekWords = { "this", "coming", };
	private final static String[] nextWeekWords = { "next" };

	// Proper date/time format in Esther
	private final static String defaultDateFormat = "dd/MM/yyyy";
	private final static String defaultTimeFormat = "HH:mm";
	private final static SimpleDateFormat convertToDateFormat = new SimpleDateFormat(defaultDateFormat);
	private final static SimpleDateFormat convertToTimeFormat = new SimpleDateFormat(defaultTimeFormat);

	// Error messages available in DateParser
	private final static String ERROR_DIFFERENTDATE = "The date you entered is not correct. Please check again.";
	private final static String ERROR_DATEFORMAT = "Improper date format.";
	private final static String ERROR_TIMEFORMAT = "Improper time format.";

	private final static String WHITESPACE = " ";
	private final static int daysInAWeek = 7;

	/**
	 * This method finds the date and time inside the string. It is the only
	 * method that is accessible by other classes.
	 * 
	 * @param input
	 *            string to be check
	 * @return the date and time, [0] date, [1] time
	 * @throws InvalidInputException
	 *             wrong date or time
	 */
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

			// Check for date
			if (dateTime[0] == null) {
				String dateFormat = dp.getDateFormat(input);
				if (!dateFormat.isEmpty()) {
					dateTime[0] = dp.getDate(input, dateFormat);
					String givenDate = convertToProperDateFormat(input, dateFormat);
					int lastIndexOfDate = input.indexOf(givenDate.toLowerCase());
					input = input.substring(lastIndexOfDate + givenDate.length()).trim();
				}
			}

			// Check for time
			if (dateTime[1] == null) {
				String timeFormat = dp.getTimeFormat(input);
				if (!timeFormat.isEmpty()) {
					dateTime[1] = dp.getTime(input, timeFormat);
					SimpleDateFormat givenTimeFormat = new SimpleDateFormat(timeFormat);
					Date inputTime = null;
					try {
						inputTime = givenTimeFormat.parse(input);
					} catch (ParseException e) {
					}
					String givenTime = givenTimeFormat.format(inputTime);
					if (givenTime.charAt(0) == '0') {
						givenTime = givenTime.substring(1);
					}
					int lastIndexOfTime = input.toLowerCase().lastIndexOf(givenTime.toLowerCase());
					input = input.substring(lastIndexOfTime + givenTime.length()).trim();
				}
			}
			input = input.trim();
			input = dp.getProperDateTime(input);
			if (oldInput == input) {
				// looping indefinitely, so cut the string by 1
				input = input.substring(1);
			}
		}
		checkSameDate(dateTime, wordyDate);
		return dateTime;

	}

	/**
	 * This method compares if the wordy date and the proper date is the same.
	 * 
	 * @param dateTime
	 *            array that includes the proper date
	 * @param wordyDate
	 *            the wordy date
	 * @throws InvalidInputException
	 *             the wordy date and proper date are different.
	 */
	private void checkSameDate(String[] dateTime, String wordyDate) throws InvalidInputException {
		if (wordyDate != null && dateTime[0] != null) {
			if (!(wordyDate.equals(dateTime[0]))) {
				throw new InvalidInputException(ERROR_DIFFERENTDATE);
			}
		}
		if (wordyDate != null) {
			dateTime[0] = wordyDate;
		}
	}

	/**
	 * This method removes all the extra words that is not a date or time.
	 * 
	 * @param input
	 * @return any possible values that could be a date or time. E.g months and
	 *         integers
	 */
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

	/**
	 * This method gets the date in the string and remove it.
	 * 
	 * @param input
	 *            the string that contains the date
	 * @param dateFormat
	 *            the date format of the date
	 * @return new input without the date
	 */
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
			date += inputSplitByWhiteSpace[i] + WHITESPACE;
		}
		return date.trim();
	}

	/**
	 * This method checks if the input is a wordy date such as: monday, tuesday,
	 * day after tomorrow, tml.
	 * 
	 * @param input
	 *            string to check for wordy date format
	 * @return string that correspond to the wordy date
	 */
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
		} else { // Case 2: use days E.g. monday, tues
			int currentDayValue = cal.get(Calendar.DAY_OF_WEEK);

			cal.set(Calendar.DAY_OF_WEEK, specificDayValue);

			if (currentDayValue >= specificDayValue) {
				cal.add(Calendar.DATE, daysInAWeek);
			}
			for (int i = 0; i < nextWeekWords.length; i++) {
				if (input.toLowerCase().contains(nextWeekWords[i])) {
					String withoutNextInput = input.replaceAll(nextWeekWords[i], "");
					int occurrence = (input.length() - withoutNextInput.length()) / nextWeekWords[i].length();
					for (int p = 0; p < occurrence; p++) {
						cal.add(Calendar.DATE, daysInAWeek);
					}
				}
			}
		}
		return convertToDateFormat.format(cal.getTime());
	}

	/**
	 * This method checks if the input is a proper date using the list of
	 * acceptable date formats in dateFormatList.
	 * 
	 * @param input
	 *            string to check for date format
	 * @return the correct date format if present
	 */
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

	/**
	 * This method checks if the input is a proper time using the list of
	 * acceptable time formats in timeFormatList.
	 * 
	 * @param input
	 *            string to check for time format
	 * @return the correct time format if present
	 */
	private String getTimeFormat(String input) {
		for (int i = 0; i < timeFormatList.size(); i++) {
			try {
				SimpleDateFormat timeFormat = new SimpleDateFormat(timeFormatList.get(i));
				timeFormat.parse(input);
				return timeFormatList.get(i);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				continue;
			}
		}
		return "";
	}

	/**
	 * This method converts the time input by the user to the proper format
	 * decided in Esther. The resulted date is required for Logic.
	 * 
	 * @param input
	 *            given date to be converted
	 * @param givenDateFormat
	 *            the date format of the input
	 * @return the standard format of the time of Esther's
	 * @throws InvalidInputException
	 *             wrong date format for input
	 */
	private String getDate(String input, String givenDateFormat) throws InvalidInputException {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(givenDateFormat);
		try {
			date = dateFormat.parse(input);
		} catch (ParseException e) {
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

	/**
	 * This method converts the time input by the user to the proper format
	 * decided in Esther. The resulted time is required for Logic.
	 * 
	 * @param input
	 *            given time to be converted
	 * @param givenTimeFormat
	 *            the time format of the input
	 * @return the standard format of the time of Esther's
	 * @throws InvalidInputException
	 *             wrong time format for input
	 */
	private String getTime(String input, String givenTimeFormat) throws InvalidInputException {
		SimpleDateFormat timeFormat = new SimpleDateFormat(givenTimeFormat);
		Date date = null;
		try {
			date = timeFormat.parse(input);
		} catch (ParseException e) {
		}
		return convertToTimeFormat.format(date);
	}

	/**
	 * This method checks for the 24 hour time format.
	 * 
	 * @param input
	 *            string to check for format
	 * @return string array of size 2: [0] the time of the task, [1] the input
	 *         excluding the time
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
	 * This method creates the list of possible values for following days:
	 * today, tomorrow, the day after tomorrow. It is used for the dayWords
	 * LinkedHashMap.
	 * 
	 * @@author A0126000H
	 * @return the map of all values for the different days
	 */
	private static LinkedHashMap<String, Integer> createDayMap() {
		LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		result.put("day after", 2);
		result.put("tda", 2);
		result.put("tmr", 1);
		result.put("tmw", 1);
		result.put("tml", 1);
		result.put("tom", 1);
		result.put("today", 0);
		return result;
	}

	/**
	 * This method creates the list of values for the days. It is used for the
	 * weekDayWords LinkedHashMap.
	 * 
	 * @@author A0126000H
	 * @return the map of all the days
	 */
	private static LinkedHashMap<String, Integer> createWeekMap() {
		LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		result.put("sun", Calendar.SUNDAY);
		result.put("mon", Calendar.MONDAY);
		result.put("tues", Calendar.TUESDAY);
		result.put("wed", Calendar.WEDNESDAY);
		result.put("thu", Calendar.THURSDAY);
		result.put("fri", Calendar.FRIDAY);
		result.put("sat", Calendar.SATURDAY);
		return result;
	}

	/**
	 * This method creates the list of values for the months. It is used for the
	 * monthWords LinkedHashMap.
	 * 
	 * @return the map of all the months
	 * @author A0126000H
	 */
	private static LinkedHashMap<String, String> createMonthMap() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
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
	 * This method finds if a substring of a word is present in the hashmap.
	 * 
	 * @param input
	 *            the word to be find in the hashmap
	 * @param map
	 *            the hashmap to to be iterated
	 * @return value that corresponds to the key
	 * @author A0126000H
	 */
	private int checkForWordInMap(String input, LinkedHashMap<String, Integer> map) {
		for (Entry<String, Integer> e : map.entrySet()) {
			if (input.toLowerCase().contains(e.getKey())) {
				return e.getValue();
			}
		}
		return -1;
	}
}
