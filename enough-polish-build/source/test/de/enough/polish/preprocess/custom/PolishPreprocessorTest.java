/*
 * Created on 14-Sep-2004 at 21:30:47.
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
package de.enough.polish.preprocess.custom;

import java.util.regex.Matcher;

import junit.framework.TestCase;

/**
 * <p>Tests teh PolishPreprocessor</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        14-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishPreprocessorTest extends TestCase {

	public PolishPreprocessorTest(String name) {
		super(name);
	}
	
	public void testSetTickerPattern() {
		System.out.println( "setTicker-Pattern: " +  PolishPreprocessor.SET_TICKER_STR );
		String code = "  setTicker( new Ticker( \"Hallo Welt wie gehts?!)WIESLDK8*(#$*\" ) );  ";
		Matcher m = PolishPreprocessor.SET_TICKER_PATTERN.matcher(code);
		assertTrue( m.find() );
		assertEquals( " setTicker( new Ticker( \"Hallo Welt wie gehts?!)WIESLDK8*(#$*\" ) )", m.group() );

		code = "this.setTicker( new Ticker( \"Hallo Welt wie gehts?!)WIESLDK8*(#$*\" ) );  ";
		m = PolishPreprocessor.SET_TICKER_PATTERN.matcher(code);
		assertTrue( m.find() );
		assertEquals( ".setTicker( new Ticker( \"Hallo Welt wie gehts?!)WIESLDK8*(#$*\" ) )", m.group() );
		
		code = "setTicker( new Ticker( \"Hallo Welt wie gehts?!)WIESLDK8*(#$*\" ) );  ";
		m = PolishPreprocessor.SET_TICKER_PATTERN.matcher(code);
		assertTrue( m.find() );
		assertEquals( "setTicker( new Ticker( \"Hallo Welt wie gehts?!)WIESLDK8*(#$*\" ) )", m.group() );
		
		code = "this.menuScreen.setTicker( ticker );";
		m = PolishPreprocessor.SET_TICKER_PATTERN.matcher(code);
		assertTrue( m.find() );
		assertEquals( ".setTicker( ticker )", m.group() );

	}
	
	public void testGetTickerPattern() {
		System.out.println( "getTicker-Pattern: " + PolishPreprocessor.GET_TICKER_STR );
		String code = "  getTicker(  );  ";
		Matcher m = PolishPreprocessor.GET_TICKER_PATTERN.matcher(code);
		assertTrue( m.find() );
		assertEquals( " getTicker(  )", m.group() );

		code = "this.getTicker(  );  ";
		m = PolishPreprocessor.GET_TICKER_PATTERN.matcher(code);
		assertTrue( m.find() );
		assertEquals( ".getTicker(  )", m.group() );
		
		code = "getTicker(  );  ";
		m = PolishPreprocessor.GET_TICKER_PATTERN.matcher(code);
		assertTrue( m.find() );
		assertEquals( "getTicker(  )", m.group() );
		
		code = "Ticker ticker = this.menuScreen.getTicker();";
		m = PolishPreprocessor.GET_TICKER_PATTERN.matcher(code);
		assertTrue( m.find() );
		assertEquals( ".getTicker()", m.group() );

	}


}
