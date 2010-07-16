/*
 * Created on Jul 8, 2010 at 10:27:16 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.calendar;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.polish.util.Comparator;
import de.enough.polish.util.IdentityArrayList;
import de.enough.polish.util.TimePoint;

/**
 * <p>Allows to sort and filter several calendar entries easily</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CalendarEntryList
implements Comparator
{
	
	private final IdentityArrayList list;
	private CalendarEntry[] sortedEntries;

	public CalendarEntryList() {
		this.list = new IdentityArrayList();
	}
	
	/**
	 * Adds a CalendarEntry to this list
	 * @param entry the entry
	 */
	public void add( CalendarEntry entry ) {
		this.sortedEntries = null;
		this.list.add(entry);
	}
	
	/**
	 * Removes a CalendarEntry from this list
	 * @param entry the entry
	 */
	public void remove( CalendarEntry entry ) {
		this.sortedEntries = null;
		this.list.remove(entry);		
	}
	
	/**
	 * Clears the complete list, i.e. removes all entries.
	 */
	public void clear() {
		this.sortedEntries = null;
		this.list.clear();
	}
	
	/**
	 * Retrieves the size of this list
	 * @return the size of this list
	 */
	public int size() {
		return this.list.size();
	}

	/**
	 * Retrieves all entries of this list sorted by their start date (and then their summary)
	 * @return an array of all entries, can be empty but not null
	 */
	public CalendarEntry[] getEntries() {
		CalendarEntry[] entries = this.sortedEntries;
		if (entries == null) {
			entries = (CalendarEntry[]) this.list.toArray(new CalendarEntry[ this.list.size()]);
			Arrays.sort(entries, this);
			this.sortedEntries = entries;
		}
		return entries;
	}
	
	/**
	 * Retrieves all entries for the specified day
	 * @param day the day
	 * @return all entries for the day, sorted by time and summary. Can be empty but not null.
	 */
	public CalendarEntry[] getEntriesForDay( TimePoint day ) {
		if (this.list.size() == 0) {
			return new CalendarEntry[0];
		}
		ArrayList l = new ArrayList();
		CalendarEntry[] entries = getEntries();
		for (int i = 0; i < entries.length; i++) {
			CalendarEntry entry = entries[i];
			TimePoint start = entry.getStartDate();
			if (start != null && start.equalsDay(day)) {
				l.add(entry);
			}
		}
		return (CalendarEntry[]) l.toArray(new CalendarEntry[l.size()]);
	}
	
	
	public CalendarEntry[] searchEntries(String searchString, boolean onlyAtStart) {
		
		if (this.list.size() == 0) {
			return new CalendarEntry[0];
		}
		
		ArrayList foundMatches = new ArrayList();
		CalendarEntry[] entries = getEntries();
		for (int i = 0; i < entries.length; i++) {
			CalendarEntry entry = entries[i];
			if(onlyAtStart){
				if(entry.getSummary().toLowerCase().startsWith(searchString.toLowerCase())){
					foundMatches.add(entry);
				}
			}else{
				if(entry.getSummary().indexOf(searchString) != -1){
					foundMatches.add(entry);
				}
			}
		}
		
		CalendarEntry[] matches = new CalendarEntry[foundMatches.size()];
		foundMatches.toArray(matches);
		
		return matches;
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		CalendarEntry e1 = (CalendarEntry) o1;
		CalendarEntry e2 = (CalendarEntry) o2;
		TimePoint st1 = e1.getStartDate();
		TimePoint st2 = e2.getStartDate();
		int result = 0;
		if (st1 != null && st2 != null) {
			result = st1.compareTo(st2);
		}
		if (result == 0) {
			String su1 = e1.getSummary();
			String su2 = e2.getSummary();
			if (su1 != null && su2 != null) {
				result = su1.compareTo(su2);
			}
		}
		return result;
	}

	/**
	 * Provides access to the internal array of this list.
	 * 
	 * @return the internal array, may contain null values
	 */
	public Object[] getInternalArray() {
		return this.list.getInternalArray();
	}

	/**
	 * Checks if this list contains at least one CalendaryEntry with the specified global unique ID
	 * @param guid the global unique ID of the CalendarEntry
	 * @return true when there is at least one entry with that GUID already present in this list.
	 */
	public boolean containsEntryWithGuid(long guid) {
		Object[] entries = this.list.getInternalArray();
		for (int i = 0; i < entries.length; i++) {
			CalendarEntry entry = (CalendarEntry) entries[i];
			if (entry == null) {
				break;
			}
			if (entry.getGuid() == guid) {
				return true;
			}
		}
		return false;
	}
}
