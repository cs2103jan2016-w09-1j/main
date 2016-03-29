import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class DateTimeTester {
	private String formattedDateTime1, formattedDateTime2, datePattern, timePattern;
	private Date creationInstant;
	private boolean hasMinutes, hasTime, hasDate, hasReverse;
	private SimpleDateFormat dateFormatter;
	private SimpleDateFormat reverseDateFormatter;

	public DateTimeTester(Date date, String dateFormat, String timeFormat) {
		setDatePattern(dateFormat);
		setTimePattern(timeFormat);
		if (dateFormat == null || dateFormat.length() == 0) {
			setHasDate(false);
			setHasTime(true);
			dateFormatter = new SimpleDateFormat(timeFormat);
		} else if (timeFormat == null || timeFormat.length() == 0) {
			setHasTime(false);
			setHasDate(true);
			dateFormatter = new SimpleDateFormat(dateFormat);
		} else {
			setHasDate(true);
			setHasTime(true);
			setHasReverse(true);
			dateFormatter = new SimpleDateFormat(dateFormat + " " + timeFormat);
			reverseDateFormatter = new SimpleDateFormat(timeFormat + " " + dateFormat);
		}

		if (isHasTime()) {
			if(getTimePattern().contains("m")){
				setHasMinutes(true);
			} else {
				setHasMinutes(false);
			}
		}
		
		setCreationInstant(date);
		setString1(dateFormatter.format(creationInstant));
		if(isHasReverse()){
			setString2(reverseDateFormatter.format(creationInstant));
		}
	}
	
	public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getCreationInstant());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		if(!isHasMinutes()){
			calendar.set(Calendar.MINUTE, 0);
		}
		if(!isHasTime()){
			calendar.set(Calendar.HOUR, 23);
			calendar.set(Calendar.MINUTE, 59);
		}
		
		return calendar.getTime();
	}

	public String getString1() {
		return formattedDateTime1;
	}

	public void setString1(String format) {
		this.formattedDateTime1 = format;
	}

	public String getString2() {
		return formattedDateTime2;
	}

	public void setString2(String format) {
		this.formattedDateTime2 = format;
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
		if(hasMinutes) {
			setHasTime(hasMinutes);
		}
	}

	public boolean isHasTime() {
		return hasTime;
	}

	public void setHasTime(boolean hasTime) {
		this.hasTime = hasTime;
		if(!hasTime){
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