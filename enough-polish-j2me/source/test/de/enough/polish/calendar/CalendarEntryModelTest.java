/*
 * Created on Jun 29, 2010 at 3:32:39 PM.
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

import de.enough.polish.util.TimePeriod;
import de.enough.polish.util.TimePoint;

import junit.framework.TestCase;

public class CalendarEntryModelTest extends TestCase {

	public CalendarEntryModelTest(String name) {
		super(name);
	}
	
	public void testRootCategories() {
		CalendarEntryModel model = new CalendarEntryModel();
		
		assertNotNull( model.getRootCategories() );
		assertEquals( 0, model.getRootCategories().length );
		
		CalendarCategory root = new CalendarCategory("test");
		CalendarEntry entry = new CalendarEntry("St. Patricks Day", root,  2010, Calendar.MARCH, 17);
		
		model.addEntry(entry);
		assertNotNull( model.getRootCategories() );
		assertEquals( 1, model.getRootCategories().length );
		assertEquals( "test", model.getRootCategories()[0].getName() );
		
		model.removeEntry(entry);
		assertNotNull( model.getRootCategories() );
		assertEquals( 0, model.getRootCategories().length );
		
		CalendarCategory subCategory = new CalendarCategory("sub");
		root.addChildCategory(subCategory);
		model.addEntry(entry);
		assertNotNull( model.getRootCategories() );
		assertEquals( 1, model.getRootCategories().length );
		assertNotNull( model.getRootCategories()[0] );
		assertEquals( "test", model.getRootCategories()[0].getName() );
		assertEquals( 1, model.getRootCategories()[0].getSizeOfChildCategories() );
		assertEquals( "sub", model.getRootCategories()[0].getChildCategory(0).getName() );
		
		model.removeEntry(entry);
		assertNotNull( model.getRootCategories() );
		assertEquals( 0, model.getRootCategories().length );
		
		CalendarCategory root2 = new CalendarCategory("test2");
		CalendarEntry entry2 = new CalendarEntry("St. Patricks Day2", root2,  2010, Calendar.MARCH, 17);
		model.addEntry(entry);
		model.addEntry(entry2);
		assertNotNull( model.getRootCategories() );
		assertEquals( 2, model.getRootCategories().length );
		
	}
	
	
	public void testGetEntries() {
		CalendarEntryModel model = new CalendarEntryModel();
		
		assertNotNull( model.getRootCategories() );
		assertEquals( 0, model.getRootCategories().length );
		
		CalendarCategory root = new CalendarCategory("test");
		CalendarEntry entry = new CalendarEntry("St. Patricks Day", root,  2010, Calendar.MARCH, 17);
		
		model.addEntry(entry);

		TimePoint start = new TimePoint( 2010, Calendar.JANUARY, 1 );
		TimePoint end = new TimePoint( 2011, Calendar.JANUARY, 1 );
		TimePeriod period = new TimePeriod( start, true, end, true );
		
		CalendarEntryList entries = model.getEntries( period);
		assertNotNull( entries );
		assertEquals( 1, entries.size() );
		
		model.removeEntry(entry);
		entries = model.getEntries( period);
		assertNotNull( entries );
		assertEquals( 0, entries.size() );
		
		
		CalendarCategory root2 = new CalendarCategory("test2");
		CalendarEntry entry2 = new CalendarEntry("St. Patricks Day2", root2,  2010, Calendar.JANUARY, 17);
		
		model.addEntry(entry);
		model.addEntry(entry2);

		entries = model.getEntries( period);
		assertNotNull( entries );
		assertEquals( 2, entries.size() );
		
		CalendarEntry entry3 = new CalendarEntry("First Monday of Jan", root2,  2010, Calendar.JANUARY, 4);		
		model.addEntry(entry3);
		assertNotNull( period.getStart() );
		assertNotNull( period.getEnd() );
		entries = model.getEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );

		
		model.enableCategory(root2);
		
		entries = model.getEnabledEntries( period);
		assertNotNull( entries );
		assertEquals( 2, entries.size() );
		
		model.disableCategory(root2);
		
		entries = model.getEnabledEntries( period);
		assertNotNull( entries );
		assertEquals( 0, entries.size() );
		
		model.enableCategory(root);
		model.enableCategory(root2);
		entries = model.getEnabledEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );

		
		start.setYear( 2010 );
		start.setMonth( Calendar.DECEMBER );
		start.setDay( 31 );
		start.setHour( 23);
		end.setYear( 2012);

		entries = model.getEnabledEntries( period);
		assertNotNull( entries );
		assertEquals( 0, entries.size() );
		entries = model.getEntries( period);
		assertNotNull( entries );
		assertEquals( 0, entries.size() );

		entry.setReoccurence(CalendarEntry.REOCCURENCE_YEARLY);
		entry2.setRepeat( EventRepeatRule.RULE_YEARLY);
		entry3.setRepeat( new EventRepeatRule( Calendar.MONDAY, 1));

		entries = model.getEnabledEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );
		entries = model.getEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );
		
		start.setDate( 2010, Calendar.JANUARY, 1);
		end.setDate( 2010, Calendar.DECEMBER, 31);
		
		System.out.println("STARTING REPEAT TEST ON SAME YEAR");
		entries = model.getEnabledEntries( period);
		assertNotNull( entries );
		CalendarEntry[] ces = entries.getEntries();
		for (int i = 0; i < ces.length; i++) {
			CalendarEntry ce = ces[i];
			System.out.println(i + ": " + ce.getSummary() + ", start="+ ce.getStartDate());
		}

		assertEquals( 3, entries.size() );
		entries = model.getEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );

	}

	public void testSerialization() throws IOException {
		CalendarEntryModel model = new CalendarEntryModel();
		CalendarCategory root = new CalendarCategory("root1");
		CalendarEntry entry = new CalendarEntry("entry1", root,  2010, Calendar.MARCH, 17);
		model.addEntry(entry);
		CalendarCategory root2 = new CalendarCategory("root2");
		CalendarEntry entry2 = new CalendarEntry("entry2", root2,  2010, Calendar.JANUARY, 17);
		model.addEntry(entry2);
		CalendarEntry entry3 = new CalendarEntry("entry3", root2,  2010, Calendar.JANUARY, 4);		
		model.addEntry(entry3);
		model.enableCategory(root2);
		entry.setReoccurence(CalendarEntry.REOCCURENCE_YEARLY);
		entry2.setRepeat( EventRepeatRule.RULE_YEARLY);
		entry3.setRepeat( new EventRepeatRule( Calendar.MONDAY, 1));
		
		TimePoint start = new TimePoint( 2010, Calendar.JANUARY, 1 );
		TimePoint end = new TimePoint( 2011, Calendar.JANUARY, 1 );
		TimePeriod period = new TimePeriod( start, true, end, false );
		
		CalendarEntryList entries = model.getEntries( period);
		assertNotNull( entries );
		CalendarEntry[] ces = entries.getEntries();
		for (int i = 0; i < ces.length; i++) {
			CalendarEntry ce = ces[i];
			System.out.println(i + ": " + ce.getSummary() + ", start="+ ce.getStartDate());
		}
		assertEquals( 3, entries.size() );
		assertTrue( entries.containsEntryWithGuid(entry.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry2.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry3.getGuid()));
		
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bout);
		
		
		model.write( out );
		byte[] buffer = bout.toByteArray();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
		DataInputStream in = new DataInputStream(bin);
		
		CalendarEntryModel model2 = new CalendarEntryModel();
		entries = model2.getEntries( period);
		assertNotNull( entries );
		assertEquals( 0, entries.size() );

		model2.read(in);
		
		entries = model2.getEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );
		assertTrue( entries.containsEntryWithGuid(entry.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry2.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry3.getGuid()));
		
	}
	
	public void testAddModel() {
		CalendarEntryModel model = new CalendarEntryModel();
		CalendarCategory root = new CalendarCategory("root1");
		CalendarEntry entry = new CalendarEntry("entry1", root,  2010, Calendar.MARCH, 17);
		model.addEntry(entry);
		CalendarCategory root2 = new CalendarCategory("root2");
		CalendarEntry entry2 = new CalendarEntry("entry2", root2,  2010, Calendar.JANUARY, 17);
		model.addEntry(entry2);
		CalendarEntry entry3 = new CalendarEntry("entry3", root2,  2010, Calendar.JANUARY, 4);		
		model.addEntry(entry3);
		model.enableCategory(root2);
		entry.setReoccurence(CalendarEntry.REOCCURENCE_YEARLY);
		entry2.setRepeat( EventRepeatRule.RULE_YEARLY);
		entry3.setRepeat( new EventRepeatRule( Calendar.MONDAY, 1));
		
		TimePoint start = new TimePoint( 2010, Calendar.JANUARY, 1 );
		TimePoint end = new TimePoint( 2011, Calendar.JANUARY, 1 );
		TimePeriod period = new TimePeriod( start, true, end, false );
		
		CalendarEntryList entries = model.getEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );
		assertTrue( entries.containsEntryWithGuid(entry.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry2.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry3.getGuid()));
		assertEquals( 2, model.getRootCategories().length );
		
		CalendarEntryModel model2 = new CalendarEntryModel();
		model2.addEntries(model);
		entries = model2.getEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );
		assertTrue( entries.containsEntryWithGuid(entry.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry2.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry3.getGuid()));
		assertEquals( 2, model2.getRootCategories().length );
		
		model2 = new CalendarEntryModel();
		model2.addEntry( entry2 );
		model2.addEntries(model);
		entries = model2.getEntries( period);
		assertNotNull( entries );
		assertEquals( 3, entries.size() );
		assertTrue( entries.containsEntryWithGuid(entry.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry2.getGuid()));
		assertTrue( entries.containsEntryWithGuid(entry3.getGuid()));
		assertEquals( 2, model2.getRootCategories().length );

		assertNotNull( entries.getEntries()[0].getStartDate());
		assertNotNull( entries.getEntries()[1].getStartDate());
		assertNotNull( entries.getEntries()[2].getStartDate());
		

	}
}
