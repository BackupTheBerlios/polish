/*
 * Created on 13-Sep-2004 at 15:30:22.
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
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * <p>Tests the translation manager.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        13-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationPreprocessorTest extends TestCase {


	public TranslationPreprocessorTest(String name) {
		super(name);
	}
	
	public void testPattern() {
		Pattern p = TranslationPreprocessor.LOCALE_GET_PATTERN;
		System.out.println( p.pattern() );
		Matcher m = p.matcher("  	Locale.get( \"My.Key\" ) ");
		assertTrue( m.find() );
		
		m = p.matcher("  	Locale.get( \"My.Key\", hello ) ");
		assertTrue( m.find() );
		
		m = p.matcher("  	Locale.get( \"My.Key\", \"rudolfo\" ) ");
		assertTrue( m.find() );
		
		m = p.matcher("		Command startGameCmd = new Command( Locale.get( \"cmd.StartGame\" ), Command.ITEM, 8 );" );
		assertTrue( m.find() );
		assertEquals("Locale.get( \"cmd.StartGame\" )", m.group() );
		
	}

}
