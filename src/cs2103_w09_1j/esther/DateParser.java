package cs2103_w09_1j.esther;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DateParser {
	private final static ArrayList<String> dateFormatList = new ArrayList<String>(Arrays.asList("dd/MM/yyyy",
			"dd.MM.yyyy", "dd-MM-yyyy", "dd MM yyyy", "ddMMyyyy", "dd MMM yyyy", "ddMMMyyyy", "dd MMM, yyyy",
			"MMM dd yyyy", "MMM dd, yyyy", "MMMdd yyyy", "dd/MM", "dd.MM", "dd MMM", "ddMMM", "MMM dd", "MMMdd"));

	private final static ArrayList<String> timeFormatList = new ArrayList<String>(Arrays.asList("hh:mma", "hh:mm a",
			"hh-mma", "hh-mm a", "hhmma", "hhmm a", "HH:mm", "HH-mm", "HHmm", "HH.mm", "hha", "hh a", "HH"));

	private final static String defaultDateFormat = "dd/MM/yyyy";
	private final static String defaultTimeFormat = "HH:mm";

	public static void main(String[] args) throws ParseException {
		String input = "whatever it is 03:00 PM";
		DateParser dp = new DateParser();
		String dateFormat = dp.getDateFormat(input);
		String timeFormat = dp.getTimeFormat(input, dateFormat);
		String dateTimeFormat = dateFormat + " " + timeFormat;

		

		if (dateFormat != "" && timeFormat != "") {
			String[] givenDate = dp.getDateTime(input, dateTimeFormat);
			System.out.println(givenDate[0]);
			System.out.println(givenDate[1]);
		} else if (dateFormat != "" && timeFormat == "") {
			String date = dp.getDate(input, dateFormat);
			System.out.println(date);
		} else if (timeFormat != "") {
			String time = dp.getTime(input, timeFormat);
			System.out.println(time);
		}
		// if (dateFormat != null) {
		// System.out.println("YES");
		// System.out.println(dp.getDateFormat(input, dateFormat));
		// } else {
		// System.out.println("NO");
		// }
		// if (dp.isTimeFormat(input, format)) {
		// System.out.println("TIME");
		// System.out.println(dp.getTimeFormat(input));
		// }
	}

	public String getDateFormat(String input) {
		for (int i = 0; i < dateFormatList.size(); i++) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatList.get(i));
				Date date = dateFormat.parse(input);
				return dateFormatList.get(i);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				continue;
			}
		}

		return "";
	}

	public String getTimeFormat(String input, String dateFormat) {
		for (int i = 0; i < timeFormatList.size(); i++) {
			try {
				SimpleDateFormat timeFormat = new SimpleDateFormat(dateFormat + "" + timeFormatList.get(i));
				Date time = timeFormat.parse(input);
				return timeFormatList.get(i);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				continue;
			}
		}
		return "";
	}

	public String[] getDateTime(String input, String dateTimeFormat) throws ParseException {

		SimpleDateFormat standardDateFormat = new SimpleDateFormat(defaultDateFormat);
		SimpleDateFormat standardTimeFormat = new SimpleDateFormat(defaultTimeFormat);
		String[] dateTimeList=new String[2];
		
		SimpleDateFormat givenFormat = new SimpleDateFormat(dateTimeFormat);
		Date date = givenFormat.parse(input);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.YEAR) == 1970) {
			Date todayDate = new Date();
			cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
			if (date.before(todayDate)) {
				cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) + 1);
			}
		}
		dateTimeList[0]=standardDateFormat.format(date);
		dateTimeList[1]=standardTimeFormat.format(date);
		return dateTimeList;
	}

	public String getDate(String input, String givenDateFormat) throws ParseException {
		Date date = new Date();
		SimpleDateFormat standardDateFormat = new SimpleDateFormat(defaultDateFormat);
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
		return standardDateFormat.format(date);

	}

	public String getTime(String input, String givenTimeFormat) throws ParseException {
		SimpleDateFormat standardTimeFormat = new SimpleDateFormat(defaultTimeFormat);
		SimpleDateFormat timeFormat = new SimpleDateFormat(givenTimeFormat);
		Date date = timeFormat.parse(input);
		return standardTimeFormat.format(date);
	}

}
