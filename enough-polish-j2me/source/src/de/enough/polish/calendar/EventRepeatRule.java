package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import de.enough.polish.io.Externalizable;

/**
 * Allows to add complex repeat rules to CalendarEntries or other events.
 * 
 * @author Nagendra Sharma
 * @author Robert Virkus
 */
public class EventRepeatRule  implements Externalizable {
	
	private static final int VERSION = 101;
	
	/**
	 * Events that are repeated yearly
	 */
	public static final int INTERVAL_YEARLY = 1;
	/**
	 * Events that are repeated monthly
	 */
	public static final int INTERVAL_MONTHLY = 2;
	/**
	 * Events that are repeated weekly
	 */
	public static final int INTERVAL_WEEKLY = 3;
	

	/**
	 * the interval for repeats, typically yearly
	 */
	private int interval = INTERVAL_YEARLY;
	/**
	 * the factor by which the interval should be repeated, e.g. a factor of 2 with a INTERVAL_WEEKLY interval results in a biweekly interval.
	 */
	private int intervalFactor = 1;

	/**
	 * The month of the event, same as the Calendar constants
	 */
	private int month = -1;
	
	/**
	 * The weekday of the event, e.g. it always falls on a Monday (Calendar.MONDAY)
	 */
	private int weekday = -1;
	
	/**
	 * Which weekday of the given month does match, e.g. the first one (1), second one (2) or last one (-1).
	 */
	private int match;
	
	/**
	 * field to contain by which day of the month of repeat
	 */
	private int monthDay = -1;
	
	/**
	 * field to contain date of end of repeat
	 */
	private Date untilDate;

		
	/**
	 * Creates a new empty repeat rule.
	 */
	public EventRepeatRule() {
		// use methods for defining the repeat rule
	}

	public EventRepeatRule( int weekday, int match ) {
		//this.interval = INTERVAL_YEARLY; is default
		if (weekday == -1 || match == 0) {
			throw new IllegalArgumentException();
		}
		this.weekday = weekday;
		this.match = match;
	}
	

	/**
	 * Retrieves the day of the month for a certain event
	 * @return returns which day of month should be repeated, e.g. 31 for New Years Eve
	 */
	public int getMonthDay() {
		return this.monthDay;
	}

	/**
	 * Sets the day of the month for a certain event
	 * @param monthDay defines which day of month should be repeated, e.g. 31 for New Years Eve
	 */
	public void setMonthDay(int monthDay) {
		this.monthDay = monthDay;
	}

	
	/**
	 * Retrieves the repetition interval of this event.
	 * 
	 * @return returns interval of repeat
	 * @see #INTERVAL_YEARLY
	 * @see #INTERVAL_MONTHLY
	 * @see #INTERVAL_WEEKLY
	 * @see #getIntervalFactor()
	 */
	public int getInterval() {
		return this.interval;
	}

	/**
	 * setter method for interval of repeat
	 * @param interval
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	/**
	 * Retrieves the factor by which the interval should be repeated, e.g. a factor of 2 with a INTERVAL_WEEKLY interval results in a biweekly interval. 
	 * @return the interval factor, typically 1.
	 */
	public int getIntervalFactor() {
		return this.intervalFactor;
	}

	/**
	 * Sets the factor by which the interval should be repeated, e.g. a factor of 2 with a INTERVAL_WEEKLY interval results in a biweekly interval. 
	 * @param intervalFactor the interval factor, typically 1.
	 */
	public void setIntervalFactor(int intervalFactor) {
		this.intervalFactor = intervalFactor;
	}


	/**
	 * Retrieves the month for which the corresponding event should be repeated
	 * @return the month like in Calendar defined, e.g. Calendar.JANUARY
	 */
	public int getMonth() {
		return this.month;
	}



	/**
	 * Sets the month for which the corresponding event should be repeated
	 * @param month the month like in Calendar defined, e.g. Calendar.JANUARY
	 */
	public void setMonth(int month) {
		this.month = month;
	}



	/**
	 * Retrieves the weekday for which the event should be repeated
	 * @return the weekday, e.g. Calendar.MONDAY
	 */
	public int getWeekday() {
		return this.weekday;
	}



	/**
	 * Sets the weekday for which the event should be repeated
	 * @param weekday the weekday, e.g. Calendar.MONDAY
	 */
	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}



	/**
	 * Specifies the matching weekday, e.g. the first (1), second (2) or last (-1) of the gievn month
	 * @return the match the matching weekday
	 */
	public int getMatch() {
		return this.match;
	}



	/**
	 * Sets the matching weekday, e.g. the first (1), second (2) or last (-1) of the gievn month
	 * @param match the matching weekday
	 */
	public void setMatch(int match) {
		this.match = match;
	}


	/**
	 * Retrieves the last date until an event should be repeated
	 * @return returns the end of repeat
	 */
	public Date getUntilDate() {
		return this.untilDate;
	}

	/**
	 * Defines the last date until which an event should be repeated
	 * @param untilDate the last possible recurring date
	 */
	public void setUntilDate(Date untilDate) {
		this.untilDate = untilDate;
	}


	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version != VERSION) {
			throw new IOException("unknown version " + version);
		}
		this.interval = in.readInt();
		this.intervalFactor = in.readInt();
		this.month = in.readInt();
		this.monthDay = in.readInt();
		this.weekday = in.readInt();
		this.match = in.readInt();
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.untilDate = new Date( in.readLong() );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		out.writeInt( this.interval );
		out.writeInt( this.intervalFactor );
		out.writeInt( this.month );
		out.writeInt( this.monthDay );
		out.writeInt( this.weekday );
		out.writeInt( this.match );
		boolean isNotNull = (this.untilDate != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeLong(this.untilDate.getTime());
		}

	}

	/**
	 * Retrieves the next date for the given event
	 * @param event 
	 * @param from
	 * @return
	 */
	public Calendar getNextDate( CalendarEntry event, Calendar from ) {
		Calendar nextDate = Calendar.getInstance();
		return getNextDate( event, from, nextDate );
	}
	
	public Calendar getNextDate( CalendarEntry event, Calendar from, Calendar nextDate ) {
		int fromYear = from.get(Calendar.YEAR);
		int fromMonth = from.get(Calendar.MONTH);
		int fromDay = from.get(Calendar.DAY_OF_MONTH);
		nextDate.setTime( event.getStartDate() );
		int eventYear = nextDate.get(Calendar.YEAR);
		int eventMonth = nextDate.get(Calendar.MONTH);
		int eventDay = nextDate.get(Calendar.DAY_OF_MONTH);
		if (this.interval == INTERVAL_YEARLY) {
//			System.out.println("from: " + fromYear + "-" + fromMonth + "-" + fromDay);
//			System.out.println("event: " + eventYear + "-" + eventMonth + "-" + eventDay);
			while (eventYear < fromYear || (eventYear == fromYear && (eventMonth < fromMonth || (eventMonth == fromMonth && eventDay <= fromDay)))) {
				eventYear += this.intervalFactor;
			}
			nextDate.set( Calendar.YEAR, eventYear );
			if (this.monthDay != -1) {
				//nothing else to change...
			} else {
				int weekdayMatch = this.match;
				if (this.weekday != -1 && weekdayMatch != 0) {
					int offset = 0;
					boolean increaseYear = false;
					if (weekdayMatch > 0) {
						do {
							if (increaseYear) {
								eventYear += this.intervalFactor;
								nextDate.set( Calendar.YEAR, eventYear );
							}
							nextDate.set( Calendar.DAY_OF_MONTH, 1);
							int currentWeekday = nextDate.get(Calendar.DAY_OF_WEEK);
							offset = (currentWeekday <= this.weekday) ? this.weekday - currentWeekday : 7 + this.weekday - currentWeekday;
							offset += (weekdayMatch - 1)*7;
							increaseYear = true;
						} while (fromYear == eventYear && fromMonth == eventMonth && 1 + offset <= fromDay);
						nextDate.set( Calendar.DAY_OF_MONTH, 1 + offset);
					} else {
						int daysInMonth = CalendarItem.getDaysInMonth(nextDate);
						do {
							if (increaseYear) {
								eventYear += this.intervalFactor;
								nextDate.set( Calendar.YEAR, eventYear );
							}
							nextDate.set( Calendar.DAY_OF_MONTH, daysInMonth);
							int currentWeekday = nextDate.get(Calendar.DAY_OF_WEEK);
							offset = (currentWeekday >= this.weekday) ? currentWeekday - this.weekday : 7 - this.weekday + currentWeekday;
							offset -= (weekdayMatch + 1)*7;
							increaseYear = true;
						} while (fromYear == eventYear && fromMonth == eventMonth && daysInMonth - offset <= fromDay);
						nextDate.set( Calendar.DAY_OF_MONTH, daysInMonth - offset);
					}
				}
			}
		}
		
		if (this.untilDate != null && (nextDate.getTime().getTime() > this.untilDate.getTime())) {
			return null;
		}
		return nextDate;
	}
}
