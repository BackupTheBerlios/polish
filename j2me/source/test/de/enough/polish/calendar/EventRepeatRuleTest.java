/*
 * Created on Jun 9, 2010 at 4:02:44 PM.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;


import junit.framework.TestCase;

public class EventRepeatRuleTest extends TestCase {

	public static void main(String[] args) throws IOException {
		EventRepeatRuleTest test = new EventRepeatRuleTest();
		test.testNextDate();
	}

	public void testNextDate() throws IOException {
		CalendarCategory nationHolidayCategory = new CalendarCategory("National Holidays", "0;2");
		CalendarEntry entry = new CalendarEntry("St. Patricks Day", nationHolidayCategory,  2010, Calendar.MARCH, 17);
		
		Calendar from = Calendar.getInstance();
		from.set( Calendar.YEAR, 2010);
		from.set( Calendar.MONTH, Calendar.JANUARY );
		from.set( Calendar.DAY_OF_MONTH, 1);
		
		Calendar nextDate = Calendar.getInstance();
		
		EventRepeatRule rule = new EventRepeatRule();
		rule.setMonthDay(17);
		Calendar received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 17, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.MARCH, received.get(Calendar.MONTH));
		assertEquals( 2010, received.get(Calendar.YEAR));
		
		from.set( Calendar.MONTH, Calendar.APRIL);
		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 17, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.MARCH, received.get(Calendar.MONTH));
		assertEquals( 2011, received.get(Calendar.YEAR));
		
		from.setTime( received.getTime() );
		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 17, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.MARCH, received.get(Calendar.MONTH));
		assertEquals( 2012, received.get(Calendar.YEAR));

		
		entry = new CalendarEntry("Martin Luther King Jr. Day", nationHolidayCategory,  2010, Calendar.JANUARY, 18);
		rule = new EventRepeatRule(Calendar.MONDAY, 3);
		from.set( Calendar.YEAR, 2010);
		from.set( Calendar.MONTH, Calendar.JANUARY );
		from.set( Calendar.DAY_OF_MONTH, 1);
		
		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 18, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.JANUARY, received.get(Calendar.MONTH));
		assertEquals( 2010, received.get(Calendar.YEAR));
		
		from.set( Calendar.MONTH, Calendar.FEBRUARY );
		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 17, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.JANUARY, received.get(Calendar.MONTH));
		assertEquals( 2011, received.get(Calendar.YEAR));

		from.set( Calendar.MONTH, Calendar.JANUARY );
		from.set( Calendar.DAY_OF_MONTH, 18);
		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 17, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.JANUARY, received.get(Calendar.MONTH));
		assertEquals( 2011, received.get(Calendar.YEAR));
		
		from.setTime( received.getTime() );
		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 16, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.JANUARY, received.get(Calendar.MONTH));
		assertEquals( 2012, received.get(Calendar.YEAR));
		
		
		
		from.set( Calendar.YEAR, 2010);
		from.set( Calendar.MONTH, Calendar.JANUARY );
		from.set( Calendar.DAY_OF_MONTH, 1);
		entry = new CalendarEntry("Memorial Day", nationHolidayCategory,  2010, Calendar.MAY, 31);
		rule = new EventRepeatRule(Calendar.MONDAY, -1);

		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 31, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.MAY, received.get(Calendar.MONTH));
		assertEquals( 2010, received.get(Calendar.YEAR));
		
		from.set( Calendar.MONTH, Calendar.JUNE );
		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 30, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.MAY, received.get(Calendar.MONTH));
		assertEquals( 2011, received.get(Calendar.YEAR));
		
		from.setTime( received.getTime() );
		received = rule.getNextDate(entry, from, nextDate );
		assertNotNull( received );
		assertEquals( 28, received.get(Calendar.DAY_OF_MONTH));
		assertEquals( Calendar.MAY, received.get(Calendar.MONTH));
		assertEquals( 2012, received.get(Calendar.YEAR));
		
		
	}

}
