/*
 * Created on Feb 28, 2007 at 4:03:51 AM.
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
package de.enough.polish.propertyfunctions;

import junit.framework.TestCase;

/**
 * <p>Tests the calculate property function</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Feb 28, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CalculateFunctionTest extends TestCase {


	public CalculateFunctionTest(String name) {
		super(name);
	}
	
	
	public void testCalculate() {
		PropertyFunction calculate = new CalculateFunction();
		String output = calculate.process("16 + 1", null, null);
		assertEquals("17", output );
		
		output = calculate.process("16+1", null, null);
		assertEquals("17", output );
		
		output = calculate.process("16 + 1 + 1", null, null);
		assertEquals("18", output );

		output = calculate.process("-16 + 1 + 1", null, null);
		assertEquals("-14", output );
		
		output = calculate.process("16 - 1", null, null);
		assertEquals("15", output );

		output = calculate.process("16-1", null, null);
		assertEquals("15", output );

		output = calculate.process("16+-1", null, null);
		assertEquals("15", output );

	}

}
