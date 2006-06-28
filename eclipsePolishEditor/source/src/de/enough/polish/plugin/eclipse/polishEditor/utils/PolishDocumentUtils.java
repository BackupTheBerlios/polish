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
import org.eclipse.jface.util.Assert;

import de.enough.mepose.core.MeposePlugin;
import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;

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

    public static boolean isPolishLine(IDocument document, int offset) {
        IRegion lineAsRegion;
        String lineAsString;
        try {
            lineAsRegion = document.getLineInformation(document.getLineOfOffset(offset));
            lineAsString = document.get(lineAsRegion.getOffset(),lineAsRegion.getLength());
        }
        catch (BadLocationException exception) {
            System.out.println("ERROR:PolishDocumentUtils.getDirectiveFromLine(...):wrong currentLine."+exception);
            return true;
        }
        return lineAsString.trim().startsWith("//#");
    }
    
    
    /**
     * Returns the position of the directive, if this line is a polish line.
     * "X//#" will return null and not a empty position.
     * @param document
     * @param currentLine
     * @return Position when directive was found, null otherwise.
     */
    public static Position extractDirectiveFromLine(IDocument document, int currentLine) {
        
        IRegion lineAsRegion;
        int offsetOfLine;
        
        try {
            lineAsRegion = document.getLineInformation(currentLine);
            offsetOfLine = document.getLineOffset(currentLine);
        }
        catch (BadLocationException exception) {
            System.out.println("ERROR:PolishDocumentUtils.getDirectiveFromLine(...):wrong currentLine."+exception);
	        return null;
        }
        
        String lineAsString;
        try {
            lineAsString = document.get(lineAsRegion.getOffset(),lineAsRegion.getLength());
        } catch (BadLocationException exception) {
            // TODO rickyn handle BadLocationException
            exception.printStackTrace();
            return null;
        }
        
        int indexOf = lineAsString.indexOf("//#");
        if(indexOf == -1) {
            // No directive found.
            return null;
        }
        // Are we at the end "X//#"?
        int directiveOffset = indexOf+3;
        int lengthOfLine = lineAsString.length();
        if(directiveOffset >= lengthOfLine) {
            return null;
        }
        
        // Is the first char valid? "X//#5"?
        if( ! Character.isJavaIdentifierStart(lineAsString.charAt(directiveOffset))) {
            return null;
        }
        int i = 0;
        
        // Look at subsequent characters.
        // examples: "X//#h", "X//#ha","X//#hallo"
        for(i = directiveOffset+1;i<lengthOfLine;i++) {
            if( ! Character.isJavaIdentifierPart(lineAsString.charAt(i))){
                break;
            }
        }
        return new Position(offsetOfLine+directiveOffset,i-directiveOffset);
        
//        try {
//            String directive;
//            directive = document.get(offsetOfLine+directiveOffset,i-directiveOffset);
//        } catch (BadLocationException exception) {
//            // TODO rickyn handle BadLocationException
//            exception.printStackTrace();
//            return null;
//        }
//        return directive;
        
//        int lastIndexInLine = lineAsRegion.getOffset() + lineAsRegion.getLength() -1;
//        char c;
//        int stateOfScanner = 0;
//        int leftmostIndex = 0;
//        int rightmostIndex = 0;
//        boolean directiveFound = false;
//        for(int i = lineAsRegion.getOffset(); i <= lastIndexInLine;i++) {
//            try {
//                c = document.getChar(i);
//                if(stateOfScanner == 0) {
//                    if(Character.isWhitespace(c)) {
//                        continue;
//                    }
//                    stateOfScanner = 1;
//                }
//                if(stateOfScanner == 1) {
//                    if(c == '/') {
//                        stateOfScanner = 2;
//                        continue;
//                    }
//                }
//                if(stateOfScanner == 2) {
//                    if(c == '/') {
//                        stateOfScanner = 3;
//                        continue;
//                    }
//                }
//                if(stateOfScanner == 3) {
//                    if(c == '#') {
//                        stateOfScanner = 4;
//                        continue; 
//                    }
//                }
//                if(stateOfScanner == 4) { // First char of directive found.
//                    directiveFound = true;
//                    leftmostIndex = i;
//                    rightmostIndex = i;
//                    if(Character.isJavaIdentifierPart(c)) {
//                        stateOfScanner = 5;
//                        continue;
//                    }
//                    //directiveFound = false; //the char after # is not valid. abort.
//                    break;
//                }
//                if(stateOfScanner == 5) {
//                    if(Character.isJavaIdentifierPart(c)) {
//                        rightmostIndex = i;
//                        continue;
//                    }
//                    //directiveFound = true; //the char after # is not valid. abort.
//                    rightmostIndex--; //We went to far by one char.
//                    break;
//                }
//            }
//            catch(BadLocationException exception) {
//                System.out.println("ERROR:PolishDocumentUtils.getDirectiveFromLine(...):wrong offset of char:"+exception);
//    	        return null;
//            }
//        }
//        if(directiveFound) {
//            return new Position(leftmostIndex,rightmostIndex-leftmostIndex+1);
//        }
//        return null;
    }
    
    /**
     * Beware to intern the string you provide before using this method.
     * @param directive this must be an interned string.
     * @param targetDirectives
     * @return true, if directive is in targetDirectives.
     */
    public static boolean isDirectiveInCategory(String directive, String[] targetDirectives) {
        Assert.isNotNull(directive);
        Assert.isNotNull(targetDirectives);
        for (int i = 0; i < targetDirectives.length; i++) {
            if(directive.equals(targetDirectives[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isDirectiveInCategory(IDocument document, Position position, String[] targetDirectives) {
        String textAtPosition = "";
        try {
            textAtPosition = document.get(position.getOffset(),position.getLength());
        } catch (BadLocationException exception) {
            PolishEditorPlugin.log("Bad Offset for get in IDocument.");
            return false;
        }
        return isDirectiveInCategory(textAtPosition,targetDirectives);
    }
    
    public static String makeStringFromPosition(IDocument document, Position position) {
        Assert.isNotNull(document);
        Assert.isNotNull(position);
        
        String result;
        try {
            result = document.get(position.getOffset(),position.getLength());
        } catch (BadLocationException exception) {
            System.out.println("ERROR:PolishDocumentUtils.makeStringFromPosition(...):cant extract string from position:"+exception);
            result = "";
        }
        return result;
    }
    
    // Implement interface TokenExtractor with 'IPosition extract(IDocument document,int offset)'
    public static Position extractVariableAtOffset(IDocument document, int offset) {
        // bla ${hallo} blubb
        // hallo > 0
        Position wordAtOffset = extractWordAtOffset(document, offset);
        boolean variableFound = false;
        Position result;
        
        // If we do not have word ...
        if(wordAtOffset == null) {
            //... consider we are at the beginning of a new directive.
            wordAtOffset = new Position(offset,0);
        }
        try {
            String prefix = document.get(wordAtOffset.getOffset()-2,2);
            System.out.println("X"+PolishDocumentUtils.makeStringFromPosition(document,wordAtOffset)+"X");
            if("${".equals(prefix) || prefix.endsWith("$")) {
                variableFound = true;
            }
            else {
                // TODO: Look out for comparison operators to the right of the word.
            }
        } catch (BadLocationException exception) {
            variableFound = false;
        }
        
        if(variableFound) {
            result = wordAtOffset;
        }
        else {
            result = null;
        }
        
        return result;
    }
    
 
    /**Returns the directive which encloses a given offset.
     * @param document
     * @param offset
     * @return Position, when a directive is found. A zero length Position when offset is in front of '//#' with no directive name
     * and null when offset is not within a directive.
     */
    public static Position extractDirectiveAtOffset(IDocument document, int offset) { 
        Position wordAtOffset = extractWordAtOffset(document, offset);
        boolean directiveFound = false;
        Position result;
        
        // If we do not have word ...
        if(wordAtOffset == null) {
            //... consider we are at the beginning of a new directive.
            wordAtOffset = new Position(offset,0);
        }
        try {
            String prefix = document.get(wordAtOffset.getOffset()-3,3);
            if("//#".equals(prefix)) {
                directiveFound = true;
            }
        } catch (BadLocationException exception) {
            directiveFound = false;
        }
        
        if(directiveFound) {
            result = wordAtOffset;
        }
        else {
            result = null;
        }
        
        return result;
        
    }
    
    /**
     * Extracts a whitespace delimited word at the given offset.
     * "abc|" -> 
     * @param document
     * @param offset
     * @return
     */
    public static Position extractWordAtOffset(IDocument document, int offset) {
        int lastIndexOfLine;
        try {
            IRegion lineInformtation = document.getLineInformationOfOffset(offset);
            int lengthOfLineInformation = lineInformtation.getLength();
            lastIndexOfLine = lineInformtation.getOffset()+lengthOfLineInformation-1;
        } catch (BadLocationException exception) {
            MeposePlugin.log("extractWordAtPosition(...):Parameter out of valid range.",exception);
            return null;
        }
        
        if(offset >= document.getLength()) {
            return null;
        }
        
        int leftmostIndexOfWord = offset;
        int rightmostIndexOfWord = offset;
        // It makes no sense to catch every exception for itself so we use one big try block.
//        try {
            // search for the left bound.
            for(int i = offset-1; i >= 0; i--) {
                // As we are advancing forward and we found a invalid char, step one back to the last vaid char.
                char c;
                try {
                    c = document.getChar(i);
                } catch (BadLocationException exception) {
                    PolishEditorPlugin.log("Wrong position in document.position:"+i,exception);
                    //TODO: Dont do the handling here. Throw an exception.
                    return null;
                }
                if( ! (Character.isJavaIdentifierPart(c) || c == '.')) {
                    leftmostIndexOfWord = i+1;
                    break;
                }
                leftmostIndexOfWord = i;
            }
            
            // If the current char is invalid, the next chars at the right are uninteressting.
            char character;
            try {
                character = document.getChar(offset);
            } catch (BadLocationException exception) {
                PolishEditorPlugin.log("Wrong position in document.position:"+offset,exception);
                //TODO: Dont do the handling here. Throw an exception.
                return null;
            }
            if( ! Character.isJavaIdentifierPart(character)) {
                rightmostIndexOfWord--;// = offset;
            }
            // We have at least one valid char, look for more.
            else {
                for(int i = offset+1; i <= lastIndexOfLine;i++) {
                    try {
                        character = document.getChar(i);
                    } catch (BadLocationException exception) {
                        PolishEditorPlugin.log("Wrong position in document.position:"+i,exception);
                        //TODO: Dont do the handling here. Throw an exception.
                        return null;
                    }
                    if( ! Character.isJavaIdentifierPart(character)) {
                        rightmostIndexOfWord = i-1;
                        break;
                    }
                    rightmostIndexOfWord = i;
                }
            }
//        }
//        catch(BadLocationException exception) {
//            PolishEditorPlugin.log(exception);
//            return null;
//        }
        return new Position(leftmostIndexOfWord,rightmostIndexOfWord-leftmostIndexOfWord+1);
    }
}
