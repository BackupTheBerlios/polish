/*
 * Created on 12-Sep-2004 at 14:37:10.
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

import de.enough.polish.util.IntegerIdGenerator;
import junit.framework.TestCase;

/**
 * <p>Tests teh Translation class</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        12-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TranslationTest extends TestCase {


	public TranslationTest(String name) {
		super(name);
	}
	
	public void testTranslation() {
		IntegerIdGenerator idGenerator = new IntegerIdGenerator();
		Translation translation = new Translation( "labels.startGame", "Start Game.",idGenerator );
		assertEquals( Translation.PLAIN, translation.getType() );
		assertEquals(  "labels.startGame", translation.getKey() );
		
		translation = new Translation( "labels.Welcome", "Hello {0}.",idGenerator );
		assertEquals( Translation.ONE_PARAMETER, translation.getType() );
		assertEquals( "Hello ", translation.getOneValueStart() );
		assertEquals( ".", translation.getOneValueEnd() );
		assertEquals( 0, translation.getParameterIndex() );
		translation = new Translation( "labels.Welcome", "Hello {3}.",idGenerator );
		assertEquals( 3, translation.getParameterIndex() );
		translation = new Translation( "labels.Welcome", "Hello {0}",idGenerator );
		assertEquals( Translation.ONE_PARAMETER, translation.getType() );
		assertEquals( "Hello ", translation.getOneValueStart() );
		assertEquals( "", translation.getOneValueEnd() );
		assertEquals( 0, translation.getParameterIndex() );
		translation = new Translation( "labels.Welcome", "{2} Hello",idGenerator );
		assertEquals( Translation.ONE_PARAMETER, translation.getType() );
		assertEquals( "", translation.getOneValueStart() );
		assertEquals( " Hello", translation.getOneValueEnd() );
		assertEquals( 2, translation.getParameterIndex() );
		
		translation = new Translation( "labels.Welcome", "Hello {3}, bla {1}.",idGenerator );
		assertEquals( Translation.SEVERAL_PARAMETERS, translation.getType() );
		assertEquals( 1, translation.getId() );
		String[] chunks = translation.getValueChunks();
		assertEquals( 3, chunks.length );
		assertEquals( "Hello ", chunks[0] );
		assertEquals( ", bla ", chunks[1] );
		assertEquals( ".", chunks[2] );
		int[] indices = translation.getParameterIndices();
		assertEquals( 2, indices.length );
		assertEquals( 3, indices[0]);
		assertEquals( 1, indices[1]);
		
		translation = new Translation( "labels.Welcome", "{2} Hello {3}, bla {1}",idGenerator );
		assertEquals( Translation.SEVERAL_PARAMETERS, translation.getType() );
		assertEquals( 1, translation.getId() );
		chunks = translation.getValueChunks();
		assertEquals( 4, chunks.length );
		assertEquals( "", chunks[0] );
		assertEquals( " Hello ", chunks[1] );
		assertEquals( ", bla ", chunks[2] );
		assertEquals( "", chunks[3] );
		indices = translation.getParameterIndices();
		assertEquals( 3, indices.length );
		assertEquals( 2, indices[0]);
		assertEquals( 3, indices[1]);
		assertEquals( 1, indices[2]);
	}

}
