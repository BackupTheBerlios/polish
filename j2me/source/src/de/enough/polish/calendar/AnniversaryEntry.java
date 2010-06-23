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
