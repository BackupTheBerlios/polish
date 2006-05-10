/*
 * Created on May 10, 2006 at 4:37:40 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.incparser;

import junit.framework.TestCase;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        May 10, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class IncTokenizerTest extends TestCase {

    public void testNextToken() {
        
        IncTokenizer tokenizer = new IncTokenizer();
        tokenizer.setSourceText("abc cb cc");
        Token nextToken1 = tokenizer.nextToken();
        Token nextToken2 = tokenizer.nextToken();
        Token nextToken3 = tokenizer.nextToken();
        Token nextToken4 = tokenizer.nextToken();
        Token nextToken5 = tokenizer.nextToken();
        
        assertEquals(Types.NAME,nextToken1.getType());
        assertEquals(Types.NAME,nextToken3.getType());
        assertEquals(Types.NAME,nextToken5.getType());
        
        assertEquals(Types.WHITESPACE,nextToken2.getType());
        assertEquals(Types.WHITESPACE,nextToken4.getType());
        
        assertEquals(0,nextToken1.getOffset());
        assertEquals(3,nextToken2.getOffset());
        assertEquals(4,nextToken3.getOffset());
        assertEquals(6,nextToken4.getOffset());
        assertEquals(7,nextToken5.getOffset());
        
        assertEquals(3,nextToken1.getLength());
        assertEquals(1,nextToken2.getLength());
        assertEquals(2,nextToken3.getLength());
        assertEquals(1,nextToken4.getLength());
        assertEquals(2,nextToken5.getLength());
    }
    
    
    public void testNextTokenError() {
        
        IncTokenizer tokenizer = new IncTokenizer();
        tokenizer.setSourceText("abcX cb cc");
        Token nextToken1 = tokenizer.nextToken();
        Token nextToken2 = tokenizer.nextToken();
        Token nextToken3 = tokenizer.nextToken();

        assertEquals(Types.NAME,nextToken1.getType());
        assertEquals(0,nextToken1.getOffset());
        assertEquals(3,nextToken1.getLength());
        
        assertEquals(Types.UNKNOWN,nextToken2.getType());
        assertEquals(3,nextToken2.getOffset());
        assertEquals(1,nextToken2.getLength());
        
        assertEquals(Types.WHITESPACE,nextToken3.getType());
        assertEquals(4,nextToken3.getOffset());
        assertEquals(1,nextToken3.getLength());
    }

}
