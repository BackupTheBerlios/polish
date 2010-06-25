package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import de.enough.polish.io.Externalizable;

public class AnniversaryEntry extends CalendarEntry implements Externalizable {

	/**
	 * field to contain the year of marriage. 
	 */
	private int yearMarried = Integer.MIN_VALUE;
	
	
	/**
	 * field to contain the date of the next anniversary.
	 * Since the startDate field will be used to identify an event we cannot change the startDate.
	 */
	private Date recentStartDate;
	
	
	/**
	 * Constructor for AnniversaryEntry
	 */
	public AnniversaryEntry() {
		
	}
	
	public AnniversaryEntry( String summary, CalendarCategory category, int year, int month, int day ){
		
		this( summary, category, year, month, day, null );
	}
	
	public AnniversaryEntry( String summary, CalendarCategory category, int year, int month, int day, int reoccurence ){
		
		this( summary, category, year, month, day, null, reoccurence );
	}
	
	public AnniversaryEntry( String summary, CalendarCategory category, int year, int month, int day, String description ){
		
		this( summary, category, year, month, day, null, CalendarEntry.REOCCURENCE_NONE );
	}
	
	public AnniversaryEntry( String summary, CalendarCategory category, int year, int month, int day, String description, int reoccurence ){

		super( summary, category, year, month, day, description, reoccurence );
	}
	
	
	public void setYearMarried(int yearMarried) {
		
		this.yearMarried = yearMarried;
	}
	
	/**
	 * Method to get year of the marriage.
	 * @return int value for a permissible year or Integer.MIN_VALUE if no married year is availabel. 
	 */
	public int getYearMarried() {
		
		return this.yearMarried;
	}
	
	/**
	 * Method to get the current number of an anniversary.
	 * @param startYear - The year when the actual event occurred. E.g. married in 2000
	 * @return int value for the current number of the anniversary or Integer.MIN_VALUE if the anniversary could not be calculated.
	 */
	public int getCurrentAnniversay(int startYear) {
		
		Date currentDate = new Date();
		int anniversary = calculateAnniversary(startYear, currentDate);
		return anniversary;
	}
	
	/**
	 * Method to calculate the number of the anniversary according to the given date. 
	 * @param startYear - The year when the actual event occurred. E.g. married in 2000
	 * @param date - The anniversary will be calculated according to this date. This is not the date of the event.
	 * @return int value for the number of the anniversary or Integer.MIN_VALUE if the anniversary could not be calculated.
	 */
	public int calculateAnniversary( int startYear, Date date ) {
		
		int anniversary = Integer.MIN_VALUE;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dYear = c.get(Calendar.YEAR);
		anniversary = dYear - startYear;
		return anniversary;
	}
	
	
	/**
	 * Method to get the date of the next anniversary.
	 * Since anniversary is a recurring event it has no fix start date.
	 * Therefore we use recentStartDate as the actual calendar startDate.
	 * E.g.
	 * actual anniversary date:	25.06.2000 (year married)
	 * current date:			24.06.2010		>	recentStartDate:	25.06.2010
	 * current date:			25.06.2010		>	recentStartDate:	25.06.2010 (anniversary is today)
	 * current date:			26.06.2010		>	recentStartDate:	25.06.2011
	 * @return date of the next anniversary.
	 */
	public Date getRecentStartDate() {
		
		if( this.recentStartDate == null ) {
			updateRecentStartDate();
		} else {
			// do nothing;
		}
		return this.recentStartDate;
	}
	
	
	/**
	 * Private method which updates the recentStartDate. 
	 */
	public void updateRecentStartDate() {

		Calendar c = Calendar.getInstance();
		Date currentDate = new Date();
		/* Since we only need the month and the day of the anniversary date we use the startDate as our anniversaryDate */
		Date anniversaryDate = super.getStartDate();
		int currentYear, currentMonth, currentDay;
		int anniversaryMonth, anniversaryDay;
		int newYear, newMonth, newDay;
		
		c.setTime(anniversaryDate);
		anniversaryMonth = c.get(Calendar.MONTH);
		anniversaryDay = c.get(Calendar.DAY_OF_MONTH);
		
		newMonth = anniversaryMonth;
		newDay = anniversaryDay;
		
		c.setTime(currentDate);
		currentYear = c.get(Calendar.YEAR);
		currentMonth = c.get(Calendar.MONTH);
		currentDay = c.get(Calendar.DAY_OF_MONTH);
		
		if( (anniversaryMonth > currentMonth) || ( (anniversaryMonth == currentMonth) && (anniversaryDay >= currentDay) ) ) {
			newYear = currentYear;
		} else {
			newYear = currentYear + 1;
		}
		
		c.set(Calendar.YEAR, newYear);
		c.set(Calendar.MONTH, newMonth);
		c.set(Calendar.DAY_OF_MONTH, newDay);
		
		this.recentStartDate = c.getTime();
	}
	
	
	
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		
		super.write(out);
		
		out.writeInt( this.yearMarried );
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		
		super.read(in);
		
		this.yearMarried = in.readInt();
	}
}
