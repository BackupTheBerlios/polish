/*
 * Created on May 24, 2005 at 11:42:41 AM.
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
package de.enough.polish.plugin.eclipse.polishEditor.utils;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

public class PolishDocumentUtilsTest extends TestCase {

    public void testOffsetInDirective() {
        IDocument document = new Document("//#hallo");
        Position result = PolishDocumentUtils.extractDirectiveAtOffset(document,4);
        if(result == null) {
            fail();
        }
        try {
            if( ! "hallo".equals(document.get(result.getOffset(),result.getLength()))) {
                fail();
            }
        } catch (BadLocationException exception) {
            fail("Bad LocationException occurred.");
        }
    }
    public void testOffsetAtEndOfDirective() {
        IDocument document = new Document("//#hallo  ");
        Position result = PolishDocumentUtils.extractDirectiveAtOffset(document,8);
        if(result == null) {
            fail();
        }
        try {
            if( ! "hallo".equals(document.get(result.getOffset(),result.getLength()))) {
                fail();
            }
        } catch (BadLocationException exception) {
            fail("Bad LocationException occurred.");
        }
    }
    public void testOffsetAtBlankDirective() {
        IDocument document = new Document("//# ");
        Position result = PolishDocumentUtils.extractDirectiveAtOffset(document,3);
        if(result == null) {
            fail();
        }
        try {
            if( ! "".equals(document.get(result.getOffset(),result.getLength()))) {
                fail();
            }
        } catch (BadLocationException exception) {
            fail("Bad LocationException occurred.");
        }
    }
    
    public void testOffsetSomewhereElse1() {
        IDocument document = new Document("//#hallo ");
        Position result = PolishDocumentUtils.extractDirectiveAtOffset(document,9);
        if(result == null) {
            return;
        }
        fail();
    }
    public void testOffsetSomewhereElse2() {
        IDocument document = new Document("//#hallo ");
        Position result = PolishDocumentUtils.extractDirectiveAtOffset(document,1);
        if(result == null) {
            return;
        }
        fail();
    }
    
}
