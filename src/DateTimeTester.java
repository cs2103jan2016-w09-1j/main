import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Jeremy Hon
 * @@A0127572A
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
		if (dateFormat == null || dateFormat.length() == 0) {
			setHasDate(false);
			setHasTime(true);
			dateTimeFormatter = new SimpleDateFormat(timeFormat);
			timeFormatter = new SimpleDateFormat(timeFormat);
		} else if (timeFormat == null || timeFormat.length() == 0) {
			setHasTime(false);
			setHasDate(true);
			dateTimeFormatter = new SimpleDateFormat(dateFormat);
			dateFormatter = new SimpleDateFormat(dateFormat);
		} else {
			setHasDate(true);
			setHasTime(true);
			setHasReverse(true);
			dateTimeFormatter = new SimpleDateFormat(dateFormat + " " + timeFormat);
			timeDateFormatter = new SimpleDateFormat(timeFormat + " " + dateFormat);
			dateFormatter = new SimpleDateFormat(dateFormat);
			timeFormatter = new SimpleDateFormat(timeFormat);
		}

		if (isHasTime()) {
			if (getTimePattern().contains("m")) {
				setHasMinutes(true);
			} else {
				setHasMinutes(false);
			}
		}

		setCreationInstant(date);
		if (isHasReverse()) {
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
		if (!isHasTime()) {
			calendar.set(Calendar.HOUR, 23);
			calendar.set(Calendar.MINUTE, 59);
		}

		return calendar.getTime();
	}

	public String getDTString() {
		return dateTimeFormatter.format(creationInstant);
	}

	public String getTDString() {
		if (hasReverse) {
			return timeDateFormatter.format(creationInstant);
		} else {
			return "";
		}
	}

	public String getDString() {
		if (hasDate) {
			return dateFormatter.format(creationInstant);
		} else {
			return "";
		}
	}

	public String getTString() {
		if (hasTime) {
			return timeFormatter.format(creationInstant);
		} else {
			return "";
		}
	}

	public String getDatePattern() {
		return datePattern;
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

	public boolean isHasTime() {
		return hasTime;
	}

	public void setHasTime(boolean hasTime) {
		this.hasTime = hasTime;
		if (!hasTime) {
			setHasMinutes(hasTime);
		}
	}

	public boolean isHasDate() {
		return hasDate;
	}

	public void setHasDate(boolean hasDate) {
		this.hasDate = hasDate;
	}

	public boolean isHasReverse() {
		return hasReverse;
	}

	public void setHasReverse(boolean hasReverse) {
		this.hasReverse = hasReverse;
	}
}