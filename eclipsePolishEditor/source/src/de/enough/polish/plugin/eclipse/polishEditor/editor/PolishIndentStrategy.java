/*
 * Created on May 10, 2005 at 11:26:14 AM.
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
package de.enough.polish.plugin.eclipse.polishEditor.editor;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoIndentStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;

	
	public class PolishIndentStrategy implements
        IAutoIndentStrategy {
    
    public void customizeDocumentCommand(IDocument document, DocumentCommand documentCommand) {
//        System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):enter.");
//        System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):documentCommand.text:X"+documentCommand.text+"X");
//        System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):documentCommand.length:"+documentCommand.length);
        if (documentCommand.length == 0 && documentCommand.text != null && isLineDelimiter(document, documentCommand.text)) {
            autoIndentAfterNewLine(document, documentCommand);
        }
		else if (documentCommand.text.length() == 1) {
			//smartIndentAfterBlockDelimiter(document, documentCommand);
		}
		else if (documentCommand.text.length() > 1 && getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SMART_PASTE)) {
			//smartPaste(document, documentCommand); // no smart backspace for paste
		}
    }
    
   
    private IPreferenceStore getPreferenceStore() {
        return PolishEditorPlugin.getDefault().getPreferenceStore();
    }

    protected int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException {
		while (offset < end) {
			char c= document.getChar(offset);
			if (c != ' ' && c != '\t') {
				return offset;
			}
			offset++;
		}
		return end;
	} 
    
	private synchronized void autoIndentAfterNewLine(IDocument d, DocumentCommand c) {
		System.out.println("DEBUG:PolishIndentStrategy.autoIndentAfterNewLine(...):enter.");
		if (c.offset == -1 || d.getLength() == 0) {
			return;
		}
		
		int positionInDocument= (c.offset == d.getLength() ? c.offset  - 1 : c.offset);
		
		try {
			int line= d.getLineOfOffset(positionInDocument);
			IRegion reg= d.getLineInformation(line);
			int lineEnd= reg.getOffset() + reg.getLength();
			int contentStart= findEndOfWhiteSpace(d, c.offset, lineEnd);
			//c.caretOffset = 40;
			//c.shiftsCaret = true;
			// TODO: do something.
		} catch (BadLocationException e) {
			PolishEditorPlugin.log(e);
		}
	}
	
	private boolean isLineDelimiter(IDocument document, String text) {
		String[] delimiters= document.getLegalLineDelimiters();
		if (delimiters != null)
			return TextUtilities.equals(delimiters, text) > -1;
		return false;
	}
}
