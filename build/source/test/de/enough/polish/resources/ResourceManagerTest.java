/*
 * Created on 11-Sep-2004 at 19:26:44.
 * 
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.resources;

import java.util.Locale;

import junit.framework.TestCase;

/**
 * <p>Tests the resource manager class.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        11-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceManagerTest extends TestCase {

	public ResourceManagerTest(String name) {
		super(name);
	}
	
	public void testLocale() {
		Locale locale = Locale.GERMAN;
		System.out.println("German: [" + locale.toString() + "].");
		System.out.println("Germany: [" + Locale.GERMANY.toString() + "].");
		
		locale = new Locale("en");
		System.out.println("English: [" + locale.toString() + "] - country: [" + locale.getCountry() + "].");
	}

}
