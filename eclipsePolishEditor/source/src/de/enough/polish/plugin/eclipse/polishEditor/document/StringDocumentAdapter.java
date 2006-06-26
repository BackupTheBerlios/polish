/*
 * Created on Jun 23, 2006 at 10:21:55 AM.
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
package de.enough.polish.plugin.eclipse.polishEditor.document;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jun 23, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class StringDocumentAdapter implements CharSequence{

    private IDocument document;

    public StringDocumentAdapter(IDocument document) {
        if(document == null){
            throw new IllegalArgumentException("StringDocumentAdapter(...):parameter 'document' is null contrary to API.");
        }
        
        this.document = document;
    }
    
    public int length() {
        return this.document.getLength();
    }

    /**
     * @throws IllegalArgumentException when index is not in the bounds.
     */
    public char charAt(int index) {
        try {
            return this.document.getChar(index);
        } catch (BadLocationException exception) {
            throw new IllegalArgumentException("index '"+index+"' is out of bounds.Document length is '"+this.document.getLength()+"'.");
        }
    }

    public CharSequence subSequence(int start, int end) {
        try {
            return this.document.get(start, end - start);
        } catch (BadLocationException e) {
            throw new IllegalArgumentException("Indices are out of bounds. 'start' is '"+start+"'. 'end' is '"+end+"'. Document length is '"+this.document.getLength()+"'.");
        }
    }

}
