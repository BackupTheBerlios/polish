//#condition polish.usePolishGui
/*
 * Created on Mar 15, 2009 at 12:29:46 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Canvas;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TableItem;
import de.enough.polish.util.TextUtil;
import de.enough.polish.util.TimePeriod;
import de.enough.polish.util.TimePoint;

/**
 * <p>Displays a calendar for a specific month</p>
 *
 * <p>Copyright Enough Software 2009</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CalendarItem extends TableItem
{
	
	
	/**
	 * Show mode for displaying the current month and year within the label of this CalendarItem.
	 */
	public static final int SHOW_MODE_LABEL = 0;
	/**
	 * Show mode for displaying the current month and year within the title of this CalendarItem's screen.
	 */
	public static final int SHOW_MODE_TITLE = 1;
	/**
	 * Show mode for displaying the current month and year within the item that has been specified with setMonthItem().
	 */
	public static final int SHOW_MODE_ITEM = 2;
	
	/**
	 * Days per month - note that February might be 28 or 29 days, depending on whether the current year is a leap year
	 */
	private static final int[] DAYS_PER_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	/**
	 * first day of the week can be Sunday or Monday depending on the country/religion.
	 * ISO mandates Monday is the first day of the week, so we use this as default, unless
	 * polish.CalendarItem.FirstDayOfWeek is set to "Sunday".
	 */
	private static int FIRST_DAY_OF_WEEK =
		//#if ${lowercase(polish.CalendarItem.FirstDayOfWeek)} == sunday
			//# Calendar.SUNDAY;
		//#else
			Calendar.MONDAY;
		//#endif
	private static String WEEKDAY_ABBREVIATIONS = 
		//#if ${lowercase(polish.CalendarItem.FirstDayOfWeek)} == sunday
			//# "S,M,T,W,T,F,S";
		//#else
			"M,T,W,T,F,S,S";
		//#endif
	private static String MONTHS = "January,February,March,April,May,June,July,August,September,October,November,December";

	private TimePoint originalDay;
	private TimePoint shownMonth;
//	private int shownMonth;
//	private int shownYear;
//	private int lastMonth;
//	private int lastYear;
//	private int lastDay;
	private int showMode;
	
	private Style calendarWeekdayStyle;
	private Style calendarDayInactiveStyle;
	private Style calendarDayStyle;
	private Style calendarCurrentdayStyle;
	private CalendarEntryModel model;
	private boolean isLimitToEnabledEntries;
	private CalendarRenderer renderer;
	private TimePoint firstColumnFirstRowDay;
	
	
	

	/**
	 * Retrieves the first day of the week, typically either Calendar.SUNDAY or Calendar.MONDAY.
	 * @return the first day of the week in Calendar format
	 */
	public static int getFirstDayOfWeek()
	{
		return FIRST_DAY_OF_WEEK;
	}

	/**
	 * Sets the first day of the week, typically this is either Monday (ISO, Europe) or Sunday (e.g. US)
	 * @param firstDayOfWeek the first day of the week, e.g. Calendar.MONDAY
	 */
	public static void setFirstDayOfWeek(int firstDayOfWeek)
	{
		FIRST_DAY_OF_WEEK = firstDayOfWeek;
	}
	
	/**
	 * Retrieves a comma separated list of abbreviations for week days.
	 * @return the day abbreviations, by default "M,T,W,T,F,S,S"
	 */
	public static String getWeekDayAbbreviations() {
		return WEEKDAY_ABBREVIATIONS;
	}
	
	/**
	 * Sets the comma separated list of abbreviations for week days
	 * @param abbreviations the list of abbreviations, e.g. "Mo,Tu,We,Th,Fr,Sa,Su"
	 */
	public static void setWeekDayAbbreviations( String abbreviations ) {
		WEEKDAY_ABBREVIATIONS = abbreviations;
	}
	
	/**
	 * Retrieves the names of the month in a comma separated list.
	 * @return the names of the months, by default "January,February,March,..."
	 */
	public static String getMonths() {
		return MONTHS;
	}
	
	/**
	 * Sets the names of the month in a comma separated list.
	 * @param months the names of the months, e.g. "Jan,Feb,Mar,..."
	 */
	public static void setMonths(String months) {
		MONTHS = months;
	}


	/**
	 * Creates a new Calendar Item with the current month shown.
	 */
	public CalendarItem()
	{
		this( TimePoint.now(), null, null );
	}

	/**
	 * Creates a new Calendar Item with the current month shown.
	 * 
	 * @param style the style of the calendar item
	 */
	public CalendarItem(Style style)
	{
		this( TimePoint.now(), null, style );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param cal the month that should be displayed by default.
	 */
	public CalendarItem(Calendar cal)
	{
		this( cal, null );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param cal the month that should be displayed by default.
	 * @param style the style of the calendar item
	 */
	public CalendarItem(Calendar cal, Style style)
	{
		this( new TimePoint(cal), null, style );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param timePoint the month that should be displayed by default.
	 */
	public CalendarItem(TimePoint timePoint) {
		this( timePoint, null, null );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param originalDay the month that should be displayed by default.
	 * @param style the style of the calendar item
	 */
	public CalendarItem(TimePoint originalDay, Style style)
	{
		this( originalDay, null, style );
	}
	
	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param originalDay the month that should be displayed by default.
	 * @param model the model that contains events for this CalendarItem
	 */
	public CalendarItem(TimePoint originalDay, CalendarEntryModel model)
	{
		this( originalDay, model, null);
	}

	/**
	 * Creates a new Calendar Item.
	 * 
	 * @param originalDay the month that should be displayed by default.
	 * @param style the style of the calendar item
	 */
	public CalendarItem(TimePoint originalDay, CalendarEntryModel model, Style style)
	{
		// depending on the month up to 6 rows my be used, typically 5 are enough + 1 for the day abbreviations
		//TODO where are the year and the month name shown? (right now the label is used)
		super( 7, 7, style); 
		this.model = model;
		this.originalDay = originalDay;
		
//		cal.set( Calendar.YEAR, 2009);
//		cal.set( Calendar.MONTH, Calendar.FEBRUARY);
		
		String[] abbreviations = TextUtil.split( WEEKDAY_ABBREVIATIONS, ',' );
		for (int i = 0; i < abbreviations.length; i++)
		{
			String abbreviation = abbreviations[i];
			//#style calendarWeekday?
			StringItem item = new StringItem( null, abbreviation);
			set( i, 0, item );
		}
		
		
		buildCalendar( new TimePoint( originalDay) );
		setSelectionMode( SELECTION_MODE_CELL | SELECTION_MODE_INTERACTIVE );
	}
	
	/**
	 * Builds up this calendar for the specified point in time
	 * @param shownMonth the point in time (most notably the month that should be shown)
	 */
	protected void buildCalendar(TimePoint shownMonth)
	{
		this.shownMonth = shownMonth;
		this.ignoreRepaintRequests = true;
		
		
		
		int selRow = getSelectedRow();
		int selCol = getSelectedColumn();
		
		int currentMonth = shownMonth.getMonth();
		int currentYear = shownMonth.getYear();
		
		String infoText = TextUtil.split( MONTHS, ',')[currentMonth] + " " + currentYear;
		if (this.showMode == SHOW_MODE_LABEL) {
			setLabel( infoText );
		} else if (this.showMode == SHOW_MODE_TITLE) {
			Screen scr = getScreen();
			if (scr != null) {
				scr.setTitle( infoText );
			}
		}
		
		TimePoint day = new TimePoint( shownMonth );
		day.setDay(1);
		int dayOfWeek = day.getDayOfWeek();
		
		int col = getColumn(dayOfWeek);
		while (col > 0) {
			day.addDay(-1);
			col--;
		}
		this.firstColumnFirstRowDay = new TimePoint( day );
		CalendarEntryList eventsList = null;
		CalendarEntry[] entriesForTheDay = null;
		if (this.model != null) {
			int daysToAdd = 7 * (getNumberOfRows() - 1) + 1;
			TimePoint endDate = new TimePoint( day );
			endDate.addDay(daysToAdd);
			TimePeriod period = new TimePeriod( day, true, endDate, false );
			if (this.isLimitToEnabledEntries) {
				eventsList = this.model.getEnabledEntries( period);
			} else {
				eventsList = this.model.getEntries( period);				
			}
		}
		//#debug
		System.out.println(infoText + ": dayOfWeek=" + dayOfWeek + "(" + getDayOfWeekName(dayOfWeek) + "), col=" + col + ", daysInMonth=" + shownMonth.getDaysInMonth());
		for (int row = 1; row < getNumberOfRows(); row++) {
			for (col = 0; col < 7; col++) {
				if (eventsList != null) {
					entriesForTheDay = eventsList.getEntriesForDay(day);
				}
				Item item = createCalendaryDay(day, shownMonth, this.originalDay, entriesForTheDay, col, row);
				set( col, row, item);
				day.addDay(1);
			}
		}
		
		this.ignoreRepaintRequests = false;
		
		if (selCol != -1 && selRow != -1) {
			setSelectedCell( selCol, selRow );
		} else if (this.shownMonth.equalsMonth(this.originalDay)) {
			col = getColumn(dayOfWeek);
			int row = (col + this.shownMonth.getDay()) / 7 + 1;
			shownMonth.setDay( this.shownMonth.getDay() );
			col = getColumn( shownMonth.getDayOfWeek() );
			setSelectedCell( col, row );
		}
		if (this.availableWidth != 0) {
			init( this.availableWidth, this.availableWidth, this.availableHeight );
		}
		repaint();
	}

	/**
	 * Creates an item that represents a day within this CalendarItem
	 * @param day the corresponding day
	 * @param currentMonth the month that is currently shown
	 * @param originalCurrentDay the original day (e.g. today) that was used to initialize this CalendarItem (should be highlighted in most cases)
	 * @param entriesForTheDay the events for the day, may be null
	 * @param col the table column for which the item is generated, you cannot expect a certain column for a specific day due to possible design and architecture changes
	 * @param row the table row for which the item is generated, you cannot expect a certain row for a specific day due to possible design and architecture changes
	 * @return the created item, must not be null
	 */
	protected Item createCalendaryDay(TimePoint day, TimePoint currentMonth, TimePoint originalCurrentDay, CalendarEntry[] entriesForTheDay, int col, int row) {
		if (this.renderer != null) {
			return this.renderer.createCalendaryDay(day, currentMonth, originalCurrentDay, entriesForTheDay);
		} else {
			return createCalendaryDay(day, currentMonth, originalCurrentDay, entriesForTheDay);
		}

	}
	

	/**
	 * Creates an item that represents a day within this CalendarItem
	 * @param day the corresponding day
	 * @param currentMonth the month that is currently shown
	 * @param originalCurrentDay the original day (e.g. today) that was used to initialize this CalendarItem (should be highlighted in most cases)
	 * @param entriesForTheDay the events for the day, may be null
	 * @return the created item, must not be null
	 */
	public static Item createCalendaryDay(TimePoint day, TimePoint currentMonth,
			TimePoint originalCurrentDay, CalendarEntry[] entriesForTheDay) 
	{
		StringItem item;
		if (!day.equalsMonth(currentMonth)) {
			//#style calendarDayInactive?
			item = new StringItem( null, Integer.toString( day.getDay() ));
		} else {
			if (day.equalsDay(originalCurrentDay)) {
				//#style calendarCurrentday?
				item = new StringItem( null, Integer.toString( day.getDay() ));				
			} else {
				//#style calendarDay?
				item = new StringItem( null, Integer.toString( day.getDay() ));
			}

		}
		item.setAppearanceMode( INTERACTIVE );
		return item;
	}

	/**
	 * Specifies a renderer for this CalendarItem
	 * @param renderer a renderer that is responsible for creating calendar items
	 */
	public void setRenderer( CalendarRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @param dayOfWeek
	 * @return
	 */
	private int getColumn(int dayOfWeek)
	{
		return dayOfWeek >= FIRST_DAY_OF_WEEK ? dayOfWeek - FIRST_DAY_OF_WEEK  :  7 + dayOfWeek - FIRST_DAY_OF_WEEK;
	}

	/**
	 * @param dayOfWeek
	 * @return
	 */
	private String getDayOfWeekName(int dayOfWeek)
	{
		String[] days = {null, "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		return days[dayOfWeek];
	}

	/**
	 * Retrieves the days within the currently shown month
	 * @return the number of days within the month that is currently shown.
	 */
	public int getDaysInMonth() {
		return this.originalDay.getDaysInMonth();
	}
	
	/**
	 * Deprecated method to retrieve the number of days of a given calendar
	 * @param cal the calendar
	 * @return the number of days within the month of the given calendar
	 * @deprecated
	 * @see TimePoint#getDaysInMonth()
	 */
	public static int getDaysInMonth(Calendar cal) {
		TimePoint point = new TimePoint( cal );
		return point.getDaysInMonth();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#handleKeyPressed(int, int)
	 */
	protected boolean handleKeyPressed(int keyCode, int gameAction)
	{
		boolean handled = super.handleKeyPressed(keyCode, gameAction);
		if (!handled) {
			if (gameAction == Canvas.LEFT || gameAction == Canvas.UP) {
				goPreviousMonth();
				return true;
			} else if (gameAction == Canvas.RIGHT || gameAction == Canvas.DOWN) {
				goNextMonth();
				return true;
			}
		}
		return handled;
	}

	/**
	 * 
	 */
	public void goNextMonth()
	{
		this.shownMonth.addMonth(1);
		this.shownMonth.setDay(1);
		buildCalendar( this.shownMonth );
	}

	/**
	 * 
	 */
	public void goPreviousMonth()
	{
		this.shownMonth.addMonth(-1);
		this.shownMonth.setDay(1);
		buildCalendar( this.shownMonth );

	}

	
	//#if polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#handlePointerReleased(int, int)
	 */
	protected boolean handlePointerReleased(int relX, int relY)
	{
		if (relY <= this.contentY && relY >= 0) {
			if (relX <= this.itemWidth / 2) {
				goPreviousMonth();
				return true;
			} else if (relX <= this.itemWidth) {
				goNextMonth();
				return true;
			}
		}
		return super.handlePointerReleased(relX, relY);
	}
	//#endif
	
	/**
	 * Retrieves the TimePoint of the given row and column within the shown Calendar
	 * @param row the row index
	 * @param col the column index
	 * @return the corresponding TimePoint
	 */
	public TimePoint getCellTimePoint(int row, int col){
		TimePoint tp = new TimePoint( this.firstColumnFirstRowDay );
		tp.addDay( (row * 7) + col );
		return tp;
	}
	
	/**
	 * Retrieves the Calendar of the given row and column within the shown Calendar
	 * @param row the row index
	 * @param col the column index
	 * @return the corresponding Calendar
	 */
	public Calendar getCellCalendar(int row, int col){
		return getCellTimePoint(row, col).getAsCalendar();

	}
	
	/**
	 * Retrieves the currently selected day as a Calendar
	 * @return a Calendar representing the currently selected day
	 */
	public TimePoint getSelectedTimePoint()
	{
		int col = getSelectedColumn();
		int row = getSelectedRow() - 1;
		return getCellTimePoint(row, col);
	}

	
	/**
	 * Retrieves the currently selected day as a Calendar
	 * @return a Calendar representing the currently selected day
	 */
	public Calendar getSelectedCalendar()
	{
		int col = getSelectedColumn();
		int row = getSelectedRow() - 1;
		return getCellCalendar(row, col);
	}

	/**
	 * Retrieves the currently selected day as a Date
	 * @return a Date representing the currently selected day
	 */
	public Date getSelectedDate()
	{
		return getSelectedCalendar().getTime();
	}
	
	/**
	 * Enables or disables the interactivity state of this calendar item.
	 * @param isInteractive true when the item should be editable and selectable
	 */
	public void setEditable( boolean isInteractive ) {
		if (isInteractive) {
			setAppearanceMode( INTERACTIVE );
		} else {
			setAppearanceMode( PLAIN );
		}
	}

	/**
	 * Gets the style for the headings of this calendar, if different from .calendarWeekday
	 * @return the style
	 */
	public Style getCalendarWeekdayStyle() {
		return this.calendarWeekdayStyle;
	}

	/**
	 * Sets the style for the headings of this calendar
	 * @param calendarWeekdayStyle the style
	 */
	public void setCalendarWeekdayStyle(Style calendarWeekdayStyle) {
		this.calendarWeekdayStyle = calendarWeekdayStyle;
	}

	/**
	 * Gets the style for days of another month, if different from .calendarDayInactive
	 * @return the style
	 */
	public Style getCalendarDayInactiveStyle() {
		return this.calendarDayInactiveStyle;
	}

	/**
	 * Sets the style for days of another month
	 * @param calendarDayInactiveStyle the style
	 */
	public void setCalendarDayInactiveStyle(Style calendarDayInactiveStyle) {
		this.calendarDayInactiveStyle = calendarDayInactiveStyle;
	}

	/**
	 * Gets the style for a normal calendar day entry, if different from .calendarDay
	 * @return the style
	 */
	public Style getCalendarDayStyle() {
		return this.calendarDayStyle;
	}

	/**
	 * Sets the style for a normal calendar day entry
	 * @param calendarDayStyle the style
	 */
	public void setCalendarDayStyle(Style calendarDayStyle) {
		this.calendarDayStyle = calendarDayStyle;
	}

	/**
	 * Gets the style for the currently selected calendar day entry, if different from .calendarCurrentDay
	 * @return the style
	 */
	public Style getCalendarCurrentdayStyle() {
		return this.calendarCurrentdayStyle;
	}

	/**
	 * Sets the style for the currently selected calendar day entry
	 * @param calendarCurrentdayStyle the style
	 */
	public void setCalendarCurrentdayStyle(Style calendarCurrentdayStyle) {
		this.calendarCurrentdayStyle = calendarCurrentdayStyle;
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.ui.TableItem#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		//#if polish.css.customitem-inactive
			Boolean inactiveBool = style.getBooleanProperty("customitem-inactive");
			if (inactiveBool != null) {
				setEditable( !inactiveBool.booleanValue() );
			}
		//#endif
		//#if polish.css.calendar-weekday-style
			Style headingStyle = (Style) style.getObjectProperty("calendar-weekday-style");
			if (headingStyle != null) {
				this.calendarWeekdayStyle = headingStyle;
			}
		//#endif
		//#if polish.css.calendar-day-inactive-style
			Style dayInactiveStyle = (Style) style.getObjectProperty("calendar-day-inactive-style");
			if (dayInactiveStyle != null) {
				this.calendarDayInactiveStyle = dayInactiveStyle;
			}
		//#endif
		//#if polish.css.calendar-day-style
			Style dayStyle = (Style) style.getObjectProperty("calendar-day-style");
			if (dayStyle != null) {
				this.calendarDayStyle = dayStyle;
			}
		//#endif
		//#if polish.css.calendar-current-day-style
			Style currentDayStyle = (Style) style.getObjectProperty("calendar-current-day-style");
			if (currentDayStyle != null) {
				this.calendarCurrentdayStyle = currentDayStyle;
			}
		//#endif		
	}
	
	/**
	 * Retrieves the shown month
	 * @return the shown month as in java.util.Calendar, e.g. java.util.Calendar.JANUARY
	 */
	public int getShownMonth(){
		return this.shownMonth.getMonth();
	}
	
	/**
	 * Retrieves the shown month as a TimePoint
	 * @return the currently shown month as a TimePoint
	 */
	public TimePoint getShownTimePoint() {
		return this.shownMonth;
	}

	/**
	 * Sets a data model that contains calendar events for this CalendarItem.
	 * @param model the model use null to remove the model 
	 * @see #setModelLimitToEnabledCategories(boolean)
	 */
	public void setModel( CalendarEntryModel model ) {
		this.model = model;
	}
	
	/**
	 * Retrieves the model used in this CalendarItem
	 * @return the model used, can be null
	 */
	public CalendarEntryModel getModel() {
		return this.model;
	}

	/**
	 * Specifies whether only enabled categories should be displayed in this CalendarItem
	 * @param limit true when only enabled categories should be shown
	 * @see #setModel(CalendarEntryModel)
	 */
	public void setModelLimitToEnabledCategories( boolean limit ) {
		this.isLimitToEnabledEntries = limit;
	}
}
