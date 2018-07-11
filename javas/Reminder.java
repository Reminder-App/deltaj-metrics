package br.unb.cic.reminders.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.Patterns;
import br.unb.cic.framework.persistence.DBTypes;
import br.unb.cic.framework.persistence.annotations.Column;
import br.unb.cic.framework.persistence.annotations.Entity;
//#if staticCategory || manageCategory
import br.unb.cic.framework.persistence.annotations.ForeignKey;
//#endif
import br.unb.cic.reminders.view.InvalidHourException;

@Entity(table = "REMINDER")
public class Reminder {
	@Column(column = "PK", primaryKey = true, type = DBTypes.LONG)
	private Long id;
	@Column(column = "TEXT", type = DBTypes.TEXT)
	private String text;
	@Column(column = "DETAILS", type = DBTypes.TEXT)
	private String details;

	//#ifdef fixedDate
	@Column(column = "DATE", type = DBTypes.TEXT)
	private String date;
	//#endif

	//#if fixedDate || dateRepeat
	@Column(column = "HOUR", type = DBTypes.TEXT)
	private String hour;
	//#endif

	//#ifdef dateRange
	@Column(column = "INITIAL_DATE", type = DBTypes.TEXT)
	private String dateStart;
	@Column(column = "INITIAL_HOUR", type = DBTypes.TEXT)
	private String hourStart;
	@Column(column = "FINAL_DATE", type = DBTypes.TEXT)
	private String dateFinal;
	@Column(column = "FINAL_HOUR", type = DBTypes.TEXT)
	private String hourFinal;
	//#endif

	//#ifdef dateRepeat
	@Column(column = "MONDAY", type = DBTypes.INT)
	private boolean monday;
	@Column(column = "TUESDAY", type = DBTypes.INT)
	private boolean tuesday;
	@Column(column = "WEDNESDAY", type = DBTypes.INT)
	private boolean wednesday;
	@Column(column = "THURSDAY", type = DBTypes.INT)
	private boolean thursday;
	@Column(column = "FRIDAY", type = DBTypes.INT)
	private boolean friday;
	@Column(column = "SATURDAY", type = DBTypes.INT)
	private boolean saturday;
	@Column(column = "SUNDAY", type = DBTypes.INT)
	private boolean sunday;
	//#endif

	@Column(column = "DONE", type = DBTypes.INT)
	private boolean done;

	//#if staticCategory || manageCategory
	@Column(column = "FK_CATEGORY", type = DBTypes.LONG)
	@ForeignKey(mappedBy = "id")
	private Category category;
	//#endif

	//#ifdef priority
	@Column(column = "PRIORITY", type = DBTypes.INT)
	private Priority priority;
	//#endif

	public Reminder() {
	}

	public Reminder(Long id, String text) {
		this.id = id;
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (text == null || text.trim().equals("")) {
			throw new InvalidTextException(text);
		}
		this.text = text;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		if (details == null || details.trim().equals("")) {
			this.details = null;
		} else {
			this.details = details;
		}
	}

	//#ifdef fixedDate
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		if (!(date == null || date.equals("")) && !checkFormat(date, Patterns.DATE_PATTERN)) {
			throw new InvalidDateException(date);
		}
		this.date = date;
	}
	//#endif

	//#if fixedDate || dateRepeat
	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		if (!(hour == null || hour.equals("")) && !checkFormat(hour, Patterns.HOUR_PATTERN)) {
			throw new InvalidHourException(hour);
		}
		this.hour = hour;
	}
	//#endif

	//#ifdef dateRange
	public String getDateStart() {
		return dateStart;
	}

	public void setDateStart(String dateStart) {

		if (dateStart == null)
			throw new InvalidDateException(dateStart);
		if (!(dateStart == null || dateStart.equals("")) && !checkFormat(dateStart, Patterns.DATE_PATTERN)) {
			throw new InvalidDateException(dateStart);
		}
		this.dateStart = dateStart;
	}

	public String getHourStart() {
		return hourStart;
	}

	public void setHourStart(String hourStart) {
		if (!(hourStart == null || hourStart.equals("")) && !checkFormat(hourStart, Patterns.HOUR_PATTERN)) {
			throw new InvalidHourException(hourStart);
		}
		this.hourStart = hourStart;
	}

	public String getDateFinal() {
		return dateFinal;
	}

	public void setDateFinal(String dateFinal) {

		if (dateFinal == null)
			throw new InvalidDateException(dateFinal);
		if (!(dateFinal == null || dateFinal.equals("")) && !checkFormat(dateFinal, Patterns.DATE_PATTERN)) {
			throw new InvalidDateException(dateFinal);
		}
		this.dateFinal = dateFinal;
	}

	public String getHourFinal() {
		return hourFinal;
	}

	public void setHourFinal(String hourFinal) {
		if (!(hourFinal == null || hourFinal.equals("")) && !checkFormat(hourFinal, Patterns.HOUR_PATTERN)) {
			throw new InvalidHourException(hourFinal);
		}
		this.hourFinal = hourFinal;
	}
	//#endif

	private boolean checkFormat(String date, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(date);
		return m.matches();
	}

	//#if staticCategory || manageCategory
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	//#endif

	//#ifdef priority
	public int getPriority() {
		return priority.getCode();
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	//#endif

	public boolean isValid() {
		return (text != null
				//#ifdef fixedDate
				&& date != null
				//#endif
				//#if fixedDate || dateRepeat
				&& hour != null
				//#endif
				//#ifdef dateRepeat
				&& isOneCbSelected()
				//#endif
				//#ifdef dateRange
				&& dateStart != null && hourStart != null && dateFinal != null && hourFinal != null
				//#endif
				//#if staticCategory || manageCategory
				&& category != null
				//#endif
				//#ifdef priority
				&& priority != null
				//#endif
		);
	}

	//#ifdef dateRepeat
	public boolean isMonday() {
		return monday;
	}

	public void setMonday(boolean monday) {
		this.monday = monday;
	}

	public void setMonday(int monday) {
		this.monday = (monday == 0 ? false : true);
	}

	public int getMonday() {
		return monday ? 1 : 0;
	}

	public boolean isTuesday() {
		return tuesday;
	}

	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}

	public void setTuesday(int tuesday) {
		this.tuesday = (tuesday == 0 ? false : true);
	}

	public int getTuesday() {
		return tuesday ? 1 : 0;
	}

	public boolean isWednesday() {
		return wednesday;
	}

	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}

	public void setWednesday(int wednesday) {
		this.wednesday = (wednesday == 0 ? false : true);
	}

	public int getWednesday() {
		return wednesday ? 1 : 0;
	}

	public boolean isThursday() {
		return thursday;
	}

	public void setThursday(boolean thursday) {
		this.thursday = thursday;
	}

	public void setThursday(int thursday) {
		this.thursday = (thursday == 0 ? false : true);
	}

	public int getThursday() {
		return thursday ? 1 : 0;
	}

	public boolean isFriday() {
		return friday;
	}

	public void setFriday(boolean friday) {
		this.friday = friday;
	}

	public void setFriday(int friday) {
		this.friday = (friday == 0 ? false : true);
	}

	public int getFriday() {
		return friday ? 1 : 0;
	}

	public boolean isSaturday() {
		return saturday;
	}

	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	public void setSaturday(int saturday) {
		this.saturday = (saturday == 0 ? false : true);
	}

	public int getSaturday() {
		return saturday ? 1 : 0;
	}

	public boolean isSunday() {
		return sunday;
	}

	public void setSunday(boolean sunday) {
		if (!isOneCbSelected() && !sunday) {
            throw new InvalidDaysException("are all false");
        }
		this.sunday = sunday;
	}

	public void setSunday(int sunday) {
		this.sunday = (sunday == 0 ? false : true);
	}

	public int getSunday() {
		return sunday ? 1 : 0;
	}
	
	public boolean isOneCbSelected() {
        return (monday != false || tuesday != false || wednesday != false || thursday != false 
            || friday != false || saturday != false);
    }
	//#endif

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public int getDone() {
		return done ? 1 : 0;
	}

	public void setDone(int done) {
		this.done = (done == 0 ? false : true);
	}
}