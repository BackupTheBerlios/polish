/*
 * Created on May 12, 2005 at 12:13:53 PM.
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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        May 12, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishDocumentUtils {

    public static Position getDirectiveFromLine(IDocument document, int currentLine) {
        IRegion lineAsRegion;
        
        try {
            lineAsRegion = document.getLineInformation(currentLine); 
            //lineAsString = document.get(lineAsRegion.getOffset(),lineAsRegion.getLength());
        }
        catch (BadLocationException exception) {
            System.out.println("ERROR:PolishDocumentUtils.getDirectiveFromLine(...):wrong currentLine."+exception);
	        return null;
        }
        
        int lastIndexInLine = lineAsRegion.getOffset() + lineAsRegion.getLength() -1;
        char c;
        int stateOfScanner = 0;
        int leftmostIndex = 0;
        int rightmostIndex = 0;
        boolean directiveFound = false;
        for(int i = lineAsRegion.getOffset(); i <= lastIndexInLine;i++) {
            try {
                c = document.getChar(i);
                if(stateOfScanner == 0) {
                    if(Character.isWhitespace(c)) {
                        continue;
                    }
                    stateOfScanner = 1;
                }
                if(stateOfScanner == 1) {
                    if(c == '/') {
                        stateOfScanner = 2;
                        continue;
                    }
                }
                if(stateOfScanner == 2) {
                    if(c == '/') {
                        stateOfScanner = 3;
                        continue;
                    }
                }
                if(stateOfScanner == 3) {
                    if(c == '#') {
                        stateOfScanner = 4;
                        continue; 
                    }
                }
                if(stateOfScanner == 4) { // First char of directive found.
                    if(Character.isJavaIdentifierPart(c)) {
                        leftmostIndex = i;
                        rightmostIndex = i;
                        directiveFound = true;
                        stateOfScanner = 5;
                        continue;
                    }
                    directiveFound = false; //the char after # is not valid. abort.
                    break;
                }
                if(stateOfScanner == 5) {
                    if(Character.isJavaIdentifierPart(c)) {
                        rightmostIndex = i;
                        continue;
                    }
                    directiveFound = true; //the char after # is not valid. abort.
                    //rightmostIndex--; //We went to one char to far.
                    break;
                }
            }
            catch(BadLocationException exception) {
                System.out.println("ERROR:PolishDocumentUtils.getDirectiveFromLine(...):wrong offset of char."+exception);
    	        return null;
            }
        }
        if(directiveFound) {
            return new Position(leftmostIndex,rightmostIndex-leftmostIndex+1);
        }
        return null;
    }
    
    /**
     * Beware to intern the string you provide.
     * @param directive this must be an interned string.
     * @param targetDirectives
     * @return true, if directive is in targetDirectives.
     */
    public static boolean isDirective(String directive, String[] targetDirectives) {
        for (int i = 0; i < targetDirectives.length; i++) {
            if(directive == targetDirectives[i]) {
                return true;
            }
        }
        return false;
    }
    
    public static String makeStringFromPosition(IDocument document, Position position) {
        if (position == null) {
            System.out.println("ERROR:PolishDocumentUtils.makeStringFromPosition:Parameter is null.");
            return "";
        }
        String result;
        try {
            result = document.get(position.getOffset(),position.getLength());
        } catch (BadLocationException exception) {
            System.out.println("ERROR:PolishDocumentUtils.makeStringFromPosition(...):cant extract string from position:"+exception);
            return "";
        }
        return result;
    }
}
