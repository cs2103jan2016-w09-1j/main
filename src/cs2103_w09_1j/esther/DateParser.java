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

public class DateParser {
    private final static ArrayList<String> dateFormatList = new ArrayList<String>(
	    Arrays.asList("dd/MM/yy", "dd.MM.yy", "dd-MM-yy", "dd MM yy", "ddMMyy", "dd MMM yy", "ddMMM yy",
		    "dd MMM,yy", "MMM dd, yy", "dd/MM", "dd.MM", "dd MMM", "ddMMM", "MMM dd", "MMMdd"));

    private final static ArrayList<String> timeFormatList = new ArrayList<String>(
	    Arrays.asList("hh:mma", "hh:mm a", "hhmma", "hhmm a", "HH:mm", "HHmm", "hha", "hh a", "HH"));

    private final static String WHITESPACE = " ";
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

    public static void main(String[] args) throws ParseException, InvalidInputException {
	DateParser dp = new DateParser();
	String[] dt = dp.getDateTime("23/7/16");
	if (dt[0] != null)
	    System.out.println("Date " + dt[0]);
	if (dt[1] != null)
	    System.out.println("Time " + dt[1]);
    }

    public String[] getDateTime(String input) throws InvalidInputException, ParseException {
	String[] dateTime = new String[2];
	String oldInput = input;

	input = input.toLowerCase();
	DateParser dp = new DateParser();

	// Check for wordy date first
	String wordyDate = dp.getWordyDateFormat(input);
	input = dp.getProperDateTime(input);
	while (input != null) {
	    oldInput = input;
	    if (dateTime[0] == null) {
		String dateFormat = dp.getDateFormat(input);
		if (!dateFormat.isEmpty()) {
		    dateTime[0] = dp.getDate(input, dateFormat);
		    SimpleDateFormat givenDateFormat = new SimpleDateFormat(dateFormat);
		    Date inputDate = givenDateFormat.parse(input);
		    String givenDate = givenDateFormat.format(inputDate);
		    String givenMonth = null;
		    String givenYear = null;
		    //System.out.println(dateFormat + " " + givenDate);
		    if (dateFormat.contains(FULLMONTH)) {
			givenMonth = getMonth(givenDate);
			if (input.contains(givenMonth.toLowerCase())) {
			    givenDate = givenDate.replace(givenMonth.substring(0, 3), givenMonth);
			}
		    }
		    if (dateFormat.contains(HALFYEAR)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(inputDate);
			givenYear = givenDate.substring(givenDate.length() - 2, givenDate.length());
			//System.out.println("YEAR" + cal.get(Calendar.YEAR));
			if (input.contains(String.valueOf(cal.get(Calendar.YEAR)))) {
			    givenDate = givenDate.substring(0, givenDate.length() - 2) + cal.get(Calendar.YEAR);
			}
		    }
		    //System.out.println(givenYear);
		    int lastIndexOfDate = input.indexOf(givenDate.toLowerCase());
		    input = input.substring(lastIndexOfDate + givenDate.length());
		    input = input.trim();
		    //System.out.println(input);
		}
	    }
	    if (dateTime[1] == null) {
		String timeFormat = dp.getTimeFormat(input);
		if (!timeFormat.isEmpty()) {
		    dateTime[1] = dp.getTime(input, timeFormat);
		    SimpleDateFormat givenTimeFormat = new SimpleDateFormat(timeFormat);
		    Date inputTime = givenTimeFormat.parse(input);
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
	    if(oldInput == input){
		//looping indefinitely, so cut the string by 1
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
	    cal.add(Calendar.DATE, specificDayValue);
	}
	return convertToDateFormat.format(cal.getTime());
    }

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

    private String getDate(String input, String givenDateFormat) throws ParseException {
	Date date = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat(givenDateFormat);
	date = dateFormat.parse(input);

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

    private String getTime(String input, String givenTimeFormat) throws ParseException {
	SimpleDateFormat timeFormat = new SimpleDateFormat(givenTimeFormat);
	Date date = timeFormat.parse(input);
	return convertToTimeFormat.format(date);
    }

    private String getMonth(String input) {
	for (Entry<String, String> e : monthWords.entrySet()) {
	    if (input.toLowerCase().contains(e.getKey())) {
		return e.getValue();
	    }
	}
	return null;
    }

    private static HashMap<String, Integer> createDayMap() {
	HashMap<String, Integer> result = new HashMap<String, Integer>();
	result.put("today", 0);
	result.put("tmr", 1);
	result.put("tom", 1);
	result.put("day after", 2);
	result.put("tda", 2);
	return result;
    }

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

    private int checkForWordInMap(String input, HashMap<String, Integer> map) {
	for (Entry<String, Integer> e : map.entrySet()) {
	    if (input.toLowerCase().contains(e.getKey())) {
		return e.getValue();
	    }
	}
	return -1;
    }
}
