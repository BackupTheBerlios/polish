/*
 * Created on Aug 14, 2006 at 9:55:04 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.sample.serialization;

public class EnumSampleJavaMe {
	
	public final static int Weekday$MONDAY = 0;
	public final static int Weekday$TUESDAY = 1;
	public final static int Weekday$WEDNESDAY = 2;
	public final static int Weekday$THURSDAY = 3;
	public final static int Weekday$FRIDAY = 4;
	public final static int Weekday$SATURDAY = 5;
	public final static int Weekday$SUNDAY = 6;
	
	public String getLocalizedName( int weekday ) {
		switch( weekday ) {
		case Weekday$MONDAY:
			return "Montag";	
		case Weekday$TUESDAY:
			return "Dienstag";
		case Weekday$WEDNESDAY:
			return "Mittwoch";
		case Weekday$THURSDAY:
			return "Donnerstag";
		case Weekday$FRIDAY:
			return "Freitag";
		case Weekday$SATURDAY:
			return "Sonnabend";
		case Weekday$SUNDAY:
			return "Sonntag";
			
		}
		throw new IllegalArgumentException(); // should not be possible since originally an emum is used
	}

}
