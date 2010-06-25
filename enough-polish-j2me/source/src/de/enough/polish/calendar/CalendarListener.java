/**
 * 
 */
package de.enough.polish.calendar;

import java.util.Date;

import de.enough.polish.io.Serializable;

/**
 * @author Ramakrishna
 *
 */
public interface CalendarListener{
	
	boolean notifyDaySelected(Date day);
	
	boolean notifyEntrySelected(CalendarEntry entry);
	
	boolean notifyTimeSelected(Date time);
	
	boolean notifyEntryStarted(CalendarEntry entry);

}
