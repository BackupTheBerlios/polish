/*
 * Created on 11-Sep-2004 at 20:55:24.
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

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;

/**
 * <p>Tests the ResourceFilter</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        11-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourceFilterTest extends TestCase {


	public ResourceFilterTest(String name) {
		super(name);
	}
	
	public void testFilter() {
		String[] userExcludes = new String[]{ "*.db", "MyFile.txt" };
		String[] defaultExcludes = new String[]{ "README.txt", "polish.css", "messages.txt" };
		String[] input = new String[]{ "image.png", "icon.png", "my.css", "polish.css", 
				"messages.txt", "thumbs.db", "dir.db", "MyFile.txt", "README.txt"  };
		
		ResourceFilter filter = new ResourceFilter( userExcludes, defaultExcludes, true );
		String[] output = filter.filter( input );
		assertEquals( 3, output.length );
		
		filter = new ResourceFilter( userExcludes, defaultExcludes, false );
		output = filter.filter( input );
		assertEquals( 6, output.length );
		
		userExcludes = new String[]{ "*.db", "MyFile*.txt" };
		try {
			filter = new ResourceFilter( userExcludes, defaultExcludes, true );
			fail("ResourceFilter should not accept invalid filter-pattern.");
		} catch (BuildException e) {
			// expected behaviour
		}
		
	}

}
