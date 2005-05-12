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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultAutoIndentStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;

import de.enough.polish.plugin.eclipse.polishEditor.editor.occurrenceAnnotations.BlockMarker;
import de.enough.polish.plugin.eclipse.polishEditor.utils.PolishDocumentUtils;

	
public class PolishIndentStrategy extends DefaultAutoIndentStrategy {
    
    public void customizeDocumentCommand(IDocument document, DocumentCommand documentCommand) {
        System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):documentCommand.text:X"+documentCommand.text+"X");
        System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):documentCommand.length:"+documentCommand.length);
        
//      Did the User push return?
        if (documentCommand.length == 0 && documentCommand.text != null && isLineDelimiter(document,documentCommand.text)) {
            System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):return pressed.");
            // Is the curser on a line with a if-like directive?
            if(isSpecificBlockDirectiveOnLine(document,documentCommand.offset,BlockMarker.IF_DIRECTIVES)) {
                System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):if directive found..");
                indentAtIfDirectiveAfterNewLine(document, documentCommand);
            }
            else if(isSpecificBlockDirectiveOnLine(document,documentCommand.offset,BlockMarker.ELSE_DIRECTIVES)) {
                System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):else directive found..");
                indentAtElseDirectiveAfterNewLine(document, documentCommand);
            }
            else if(isSpecificBlockDirectiveOnLine(document,documentCommand.offset,BlockMarker.ENDIF_DIRECTIVES)) {
                System.out.println("DEBUG:PolishIndentStrategy.customizeDocumentCommand(...):endif directive found..");
                indentAtEndifDirectiveAfterNewLine(document, documentCommand);
            }
            else {
                autoIndentAfterNewLine(document,documentCommand);
            }
        }      

//		else if (documentCommand.text.length() == 1) {
//			//smartIndentAfterBlockDelimiter(document, documentCommand);
//		}
//		else if (documentCommand.text.length() > 1 && getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SMART_PASTE)) {
//			//smartPaste(document, documentCommand); // no smart backspace for paste
//		}
    }

    private boolean isSpecificBlockDirectiveOnLine(IDocument document, int offset, String[] directives) {
        Position directiveAsPosition;
        try {
            directiveAsPosition = PolishDocumentUtils.getDirectiveFromLine(document,document.getLineOfOffset(offset));
        } catch (BadLocationException exception) {
            System.out.println("ERROR:PolishIndentStrategy.isIfBlockDirectiveOnLine(...):bad location:offset:"+offset);
            return false;
        }
        if(directiveAsPosition == null) {
            return false;
        }
        String directiveAsString = PolishDocumentUtils.makeStringFromPosition(document,directiveAsPosition);
        directiveAsString = directiveAsString.intern();
        //System.out.println("string is:"+directiveAsString);
        return PolishDocumentUtils.isDirective(directiveAsString,directives);
    }
   
//    private IPreferenceStore getPreferenceStore() {
//        return PolishEditorPlugin.getDefault().getPreferenceStore();
//    }
    
	private synchronized void indentAtIfDirectiveAfterNewLine(IDocument document, DocumentCommand command) {
		
		if (command.offset == -1 || document.getLength() == 0) {
		    System.out.println("ERROR:PolishIndentStrategy.indentAtIfDirectiveAfterNewLine(...):Parameter is invalid:command.offset:"+command.offset+".document.getLength():"+document.getLength());
			return;
		}
		try {
            String indentAsString = getCurrentIndent(document, document.getLineOfOffset(command.offset));
            StringBuffer replacementText = new StringBuffer();
            replacementText.append(command.text);
            replacementText.append(indentAsString);
            replacementText.append("\t");
            int lengthOfInnerIndention = replacementText.length();
            replacementText.append("\n");
            replacementText.append(indentAsString);
            replacementText.append("//#endif");
            command.text = replacementText.toString();
            command.shiftsCaret = false;
            command.caretOffset = command.offset + lengthOfInnerIndention;
            
            
		} catch (BadLocationException exception) {
            System.out.println("ERROR:PolishIndentStrategy.indentAtIfDirectiveAfterNewLine(...):A BadLocationException occurred. An offset could not be retrieved.command.offset:"+command.offset);
            return;
        }
	}
	
	private synchronized void indentAtElseDirectiveAfterNewLine(IDocument document, DocumentCommand command) {
		
		if (command.offset == -1 || document.getLength() == 0) {
		    System.out.println("ERROR:PolishIndentStrategy.indentAtIfDirectiveAfterNewLine(...):Parameter is invalid:command.offset:"+command.offset+".document.getLength():"+document.getLength());
			return;
		}
		 try { 
            String indentAsString = getCurrentIndent(document, document.getLineOfOffset(command.offset));
            String newIndentAsString = indentAsString;
            //shift the else directive to the left by one level.
            if(indentAsString.endsWith("\t")) {
                newIndentAsString = indentAsString.substring(0,indentAsString.length()-1);
                System.out.println("old indent:"+indentAsString.length()+".new indent:"+newIndentAsString.length());
            }
            else if(indentAsString.endsWith(" ")) {
                System.out.println("DEBUG:PolishIndentStrategy.indentAtElseDirectiveAfterNewLine(...):chop of whitespace.");
                newIndentAsString = indentAsString.replaceFirst(" {1,4}$","");
            }
            
            IRegion region= document.getLineInformation(document.getLineOfOffset(command.offset));
            int startPositionOfLine= region.getOffset();

            // Remove old indent.
            document.replace(startPositionOfLine, indentAsString.length(), "");
            document.replace(startPositionOfLine, 0, newIndentAsString);

            command.offset = command.offset - (indentAsString.length()-newIndentAsString.length());
            StringBuffer replacementText = new StringBuffer();
            replacementText.append(command.text);
            replacementText.append(newIndentAsString);
            replacementText.append("\t");
            command.text = replacementText.toString();
            command.shiftsCaret = true;
            
            
		} catch (BadLocationException exception) {
            System.out.println("ERROR:PolishIndentStrategy.indentAtIfDirectiveAfterNewLine(...):A BadLocationException occurred. An offset could not be retrieved.command.offset:"+command.offset);
            return;
        }
	}
	
	private synchronized void indentAtEndifDirectiveAfterNewLine(IDocument document, DocumentCommand command) {
		
		if (command.offset == -1 || document.getLength() == 0) {
		    System.out.println("ERROR:PolishIndentStrategy.indentAtIfDirectiveAfterNewLine(...):Parameter is invalid:command.offset:"+command.offset+".document.getLength():"+document.getLength());
			return;
		}
		 try { 
            String indentAsString = getCurrentIndent(document, document.getLineOfOffset(command.offset));
            String newIndentAsString = indentAsString;
            //shift the else directive to the left by one level.
            if(indentAsString.endsWith("\t")) {
                newIndentAsString = indentAsString.substring(0,indentAsString.length()-1);
                System.out.println("old indent:"+indentAsString.length()+".new indent:"+newIndentAsString.length());
            }
            else if(indentAsString.endsWith(" ")) {
                System.out.println("DEBUG:PolishIndentStrategy.indentAtElseDirectiveAfterNewLine(...):chop of whitespace.");
                newIndentAsString = indentAsString.replaceFirst(" {1,4}$","");
            }
            
            IRegion region= document.getLineInformation(document.getLineOfOffset(command.offset));
            int startPositionOfLine= region.getOffset();

            // Remove old indent.
            document.replace(startPositionOfLine, indentAsString.length(), "");
            document.replace(startPositionOfLine, 0, newIndentAsString);

            command.offset = command.offset - (indentAsString.length()-newIndentAsString.length());
            
            StringBuffer replacementText = new StringBuffer();
            replacementText.append(command.text);
            replacementText.append(newIndentAsString);
            //replacementText.append("\t");
            command.text = replacementText.toString();
            command.shiftsCaret = true;
            
            
		} catch (BadLocationException exception) {
            System.out.println("ERROR:PolishIndentStrategy.indentAtIfDirectiveAfterNewLine(...):A BadLocationException occurred. An offset could not be retrieved.command.offset:"+command.offset);
            return;
        }
	}

	private static String getCurrentIndent(IDocument document, int line){
		IRegion region;
		String result;
        try {
            region = document.getLineInformation(line);
        
            int startPosition= region.getOffset();
            int endPosition= region.getOffset() + region.getLength();
		
		
			int currentPosition= startPosition;
	//		go behind line comments
	//		while (to < endOffset - 2 && document.get(to, 2).equals(LINE_COMMENT))
	//			to += 2;
			
			while (currentPosition < endPosition) {
				char ch= document.getChar(currentPosition);
				if (!Character.isWhitespace(ch))
					break;
				currentPosition++;
			}
			result = document.get(startPosition, currentPosition - startPosition);
        }
        catch (BadLocationException exception) {
            System.out.println("ERROR:PolishIndentStrategy.getCurrentIndent(...):A BadLocationException occurred.");
            return null;
        }
		// don't count the space before javadoc like, asterix-style comment lines
//		if (to > from && to < endOffset - 1 && document.get(to - 1, 2).equals(" *")) { //$NON-NLS-1$
//			String type= TextUtilities.getContentType(document, IJavaPartitions.JAVA_PARTITIONING, to, true);
//			if (type.equals(IJavaPartitions.JAVA_DOC) || type.equals(IJavaPartitions.JAVA_MULTI_LINE_COMMENT))
//				to--;
//		}
		
		return result;
	}
	
	private void autoIndentAfterNewLine(IDocument d, DocumentCommand c) {
		
		if (c.offset == -1 || d.getLength() == 0)
			return;
			
		try {
			// find start of line
			int p= (c.offset == d.getLength() ? c.offset  - 1 : c.offset);
			IRegion info= d.getLineInformationOfOffset(p);
			int start= info.getOffset();
				
			// find white spaces
			int end= findEndOfWhiteSpace(d, start, c.offset);
				
			StringBuffer buf= new StringBuffer(c.text);
			if (end > start) {			
				// append to input
				buf.append(d.get(start, end - start));
			}
			
			c.text= buf.toString();
				
		} catch (BadLocationException excp) {
			// stop work
		}	
	}
	
	private boolean isLineDelimiter(IDocument document, String text) {
		String[] delimiters= document.getLegalLineDelimiters();
		if (delimiters != null)
			return TextUtilities.equals(delimiters, text) > -1;
		return false;
	}
}