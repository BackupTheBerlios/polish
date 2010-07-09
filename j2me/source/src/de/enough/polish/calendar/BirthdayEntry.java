package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import de.enough.polish.io.Externalizable;

public class BirthdayEntry extends CalendarEntry implements Externalizable {

	/**
	 * field to contain the actual birth date. 
	 */
	private Date birthDate;
	
	private int birthYear = Integer.MIN_VALUE;
	
	
	/**
	 * field to contain the date of the next birthday.
	 * Since the startDate field will be used to identify an event we cannot change the startDate.
	 */
	private Date recentStartDate;
	
	
	/**
	 * Constructor for BirthdayEntry
	 */
	public BirthdayEntry() {
		
	}
	
	public BirthdayEntry( String summary, CalendarCategory category, int year, int month, int day ) {
		
		this( summary, category, year, month, day, null );
	}
	
	public BirthdayEntry( String summary, CalendarCategory category, int year, int month, int day, Date birthDate ) {
		
		this( summary, category, year, month, day, birthDate, null );
	}
	
	public BirthdayEntry(String summary, CalendarCategory category, int year, int month, int day, Date birthDate, int reoccurence){
		
		this(summary, category, year, month, day, birthDate, null, reoccurence);
	}
	
	public BirthdayEntry(String summary, CalendarCategory category, int year, int month, int day, Date birthDate, String description){
		
		this(summary, category, year, month, day, birthDate, null, CalendarEntry.REOCCURENCE_NONE);
	}
	
	public BirthdayEntry(String summary, CalendarCategory category, int year, int month, int day, Date birthDate, String description, int reoccurence){
		
		super( summary, category, year, month, day, description, reoccurence );
		this.birthDate = birthDate;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		
		super.write(out);
		
		long time = -1;
		if ( this.birthDate != null ) {
			time = this.birthDate.getTime();
		}
		out.writeLong( time );
		out.writeInt( this.birthYear );
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		
		super.read(in);
		
		long time = in.readLong();
		if( time != -1 ) {
			this.birthDate = new Date(time);
		}
		this.birthYear = in.readInt();
	}
	
	public void setBirthDate(Date birthDate) {
		
		this.birthDate = birthDate;
		//# debug info
		System.out.println("The birth date was set to: " + this.birthDate.toString());
		setBirthYear(birthDate);
	}
	
	
	private void setBirthYear(Date birthDate) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.birthDate);
		this.birthYear = calendar.get(Calendar.YEAR);
		//#debug info
		System.out.println("The birth year was set to: " + this.birthYear);
	}
	
	
	
	/**
	 * Mehtod to get the birth date.
	 * @return date value for a permissible birth date or null if a birth date doesn't exist. 
	 */
	public Date getBirthDate() {
		
		if( this.birthDate == null ) {
			return null;
		} else {
			return this.birthDate;
		}
	}
	
	
	public int getBirthYear() {
		
		return this.birthYear;
	}
	
	
	/**
	 * Mehtod to get the current age according to the birth date and current date.
	 * @return int value for a permissible age or Integer.MIN_VALUE if the age could not be calculated
	 * due to a missing birth date. 
	 */
	public int getCurrentAge() {
		
		Date currentDate = new Date();
		int age = calculateAge(currentDate);
		return age;
	}
	
	/**
	 * Method to calculate the age according to the given date.
	 * @param date - The age will be calculated according to this date. This is not the birth date.
	 * @return int value for a permissible age or Integer.MIN_VALUE if the age could not be calculated
	 * due to a missing birth date.
	 */
	public int calculateAge(Date date) {
		
		int age = Integer.MIN_VALUE;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		int dDay, dMonth, dYear;					// Day, month and year of the passed date.
		int bDay, bMonth, bYear;					// Day, month and year of the birth date.
		
		dDay = c.get(Calendar.DAY_OF_MONTH);
		dMonth = c.get(Calendar.MONTH);
		dYear = c.get(Calendar.YEAR);
		
		if( this.birthDate != null ) {
			c.setTime(this.birthDate);
			bDay = c.get(Calendar.DAY_OF_MONTH);
			bMonth = c.get(Calendar.MONTH);
			bYear = c.get(Calendar.YEAR);
			
			if( dMonth > bMonth ) {
				// This year's birthday was already.
				age = dYear - bYear;
			} else if( (dMonth == bMonth) && (dDay >= bDay) ) {
				// This year's birthday was already or is today.
				age = dYear - bYear;
			} else {
				// This year's birthday is still up front.
				age = (dYear - bYear) - 1;	
			}
		} else {
			//#debug info
			System.out.println("Could not calculate the age since there is no birth date available.");
			// do nothing;
		}
		
		return age;
	}
	
	
	/**
	 * Method to get the date of the next birthday.
	 * Since birthday is a recurring event it has no fix start date.
	 * Therefore we use recentStartDate as the actual calendar startDate.
	 * E.g.
	 * actual birth date:	25.06.2000
	 * current date:		24.06.2010		>	recentStartDate:	25.06.2010
	 * current date:		25.06.2010		>	recentStartDate:	25.06.2010 (birthday is today)
	 * current date:		26.06.2010		>	recentStartDate:	25.06.2011
	 * @return date of the next birthday.
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
	private void updateRecentStartDate() {

		Calendar c = Calendar.getInstance();
		Date currentDate = new Date();
		/* Since we only need the month and the day of the birth date we use the startDate as our birthDate */
		Date birthDate = super.getStartDate();
		int currentYear, currentMonth, currentDay;
		int birthMonth, birthDay;
		int newYear, newMonth, newDay;
		
		c.setTime(birthDate);
		birthMonth = c.get(Calendar.MONTH);
		birthDay = c.get(Calendar.DAY_OF_MONTH);
		
		newMonth = birthMonth;
		newDay = birthDay;
		
		c.setTime(currentDate);
		currentYear = c.get(Calendar.YEAR);
		currentMonth = c.get(Calendar.MONTH);
		currentDay = c.get(Calendar.DAY_OF_MONTH);
		
		if( (birthMonth > currentMonth) || ( (birthMonth == currentMonth) && (birthDay >= currentDay) ) ) {
			newYear = currentYear;
		} else {
			newYear = currentYear + 1;
		}
		
		c.set(Calendar.YEAR, newYear);
		c.set(Calendar.MONTH, newMonth);
		c.set(Calendar.DAY_OF_MONTH, newDay);
		
		this.recentStartDate = c.getTime();
	}
}
