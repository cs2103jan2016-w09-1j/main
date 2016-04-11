import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Jeremy Hon
 * @@author A0127572A
 *
 */
class DateTimeTester {
	private String formattedDateTime, formattedTimeDate, datePattern, timePattern, formattedDate, formattedTime;
	private Date creationInstant;
	private boolean hasMinutes, hasTime, hasDate, hasReverse;
	private SimpleDateFormat dateTimeFormatter, timeDateFormatter, dateFormatter, timeFormatter;

	public DateTimeTester(Date date, String dateFormat, String timeFormat) {
		setDatePattern(dateFormat);
		setTimePattern(timeFormat);
		setHasDate(true);
		setHasTime(true);
		setHasReverse(true);
		dateTimeFormatter = new SimpleDateFormat(dateFormat + " " + timeFormat);
		timeDateFormatter = new SimpleDateFormat(timeFormat + " " + dateFormat);
		dateFormatter = new SimpleDateFormat(dateFormat);
		timeFormatter = new SimpleDateFormat(timeFormat);

		if (hasTime()) {
			if (getTimePattern().contains("m")) {
				setHasMinutes(true);
			} else {
				setHasMinutes(false);
			}
		}

		setCreationInstant(date);
		if (hasReverse()) {
		}
	}

	public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getCreationInstant());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		if (!isHasMinutes()) {
			calendar.set(Calendar.MINUTE, 0);
		}

		return calendar.getTime();
	}

	public String getDTString() {
		return dateTimeFormatter.format(creationInstant);
	}

	public String getTDString() {
		return timeDateFormatter.format(creationInstant);
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public String getTimePattern() {
		return timePattern;
	}

	public void setTimePattern(String timePattern) {
		this.timePattern = timePattern;
	}

	public Date getCreationInstant() {
		return creationInstant;
	}

	public void setCreationInstant(Date creationInstant) {
		this.creationInstant = creationInstant;
	}

	public boolean isHasMinutes() {
		return hasMinutes;
	}

	public void setHasMinutes(boolean hasMinutes) {
		this.hasMinutes = hasMinutes;
		if (hasMinutes) {
			setHasTime(hasMinutes);
		}
	}

	public boolean hasTime() {
		return hasTime;
	}

	public void setHasTime(boolean hasTime) {
		this.hasTime = hasTime;
	}

	public boolean hasDate() {
		return hasDate;
	}

	public void setHasDate(boolean hasDate) {
		this.hasDate = hasDate;
	}

	public boolean hasReverse() {
		return hasReverse;
	}

	public void setHasReverse(boolean hasReverse) {
		this.hasReverse = hasReverse;
	}
}