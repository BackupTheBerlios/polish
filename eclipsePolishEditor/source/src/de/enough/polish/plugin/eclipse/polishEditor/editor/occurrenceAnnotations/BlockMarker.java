/*
 * Created on Apr 27, 2005 at 3:49:15 PM.
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
package de.enough.polish.plugin.eclipse.polishEditor.editor.occurrenceAnnotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Apr 27, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class BlockMarker extends AbstractOccurrenceAnnotationMarker {
    
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    
    //private Matcher matcher;
	
    //public BlockMarker() {
        //Pattern pattern = Pattern.compile("$\\s*//#(\\S+)");
        //this.matcher = pattern.matcher("");
    //}
    

    
    public void updateAnnotations(ITextSelection selection) {
        super.updateAnnotations(selection);
        
//        List positions = findOtherPositionsOfIf(selection.getStartLine());
//        for (Iterator iterator = positions.iterator(); iterator.hasNext(); ) {
//            Position position = (Position) iterator.next();
//            System.out.println("DEBUG:BlockMarker.updateAnnotations(...):position:"+makeStringFromPosition(position));
//        }
//        //System.out.println("DEBUG:getDirectiveFromLine:"+makeStringFromPosition(getDirectiveFromLine(selection.getStartLine())));
//        return;
        
        List newPositions = new ArrayList();
        
        Position blockDirectiveAsPosition = findBlockDirectiveAtCaret(selection);
        if(blockDirectiveAsPosition == null) {
           System.out.println("DEBUG:BlockMarker.updateAnnotations(...):Nothing to do for BlockMarker.updateAnnotations(...).");
           return;
        }
        
        String blockDirectiveAsString = makeStringFromPosition(blockDirectiveAsPosition);
        
        if("if".equals(blockDirectiveAsString)) {
            newPositions.addAll(findOtherPositionsForIf(selection.getStartLine()));
        }
        
        Position position;
        String text;
        Annotation annotation;
        for(Iterator iterator = newPositions.iterator();iterator.hasNext();) {
          position = (Position)iterator.next();
          text = makeStringFromPosition(position);
          if(text.equals("")) {
              System.out.println("BlockMarker.updateAnnotations(...):ERROR:cant extract word from position.");
              continue;
          }
          annotation = new Annotation(IPolishConstants.POLISH_ANNOTATION,false,text);
          addAnnotation(annotation);
          getAnnotationModel().addAnnotation(annotation,position);
      }
    }
    
    
    private String makeStringFromPosition(Position position) {
        if (position == null) {
            System.out.println("ERROR:BlockMarker.makeStringFromPosition:Parameter is null.");
            return "";
        }
        
        String result;
        try {
            result = getDocument().get(position.getOffset(),position.getLength());
        } catch (BadLocationException exception) {
            System.out.println("ERROR:BlockMarker.makeStringFromPosition(...):cant extract string from position:"+exception);
            return "";
        }
        return result;
    }

    private Position findBlockDirectiveAtCaret(ITextSelection selection) { 
	    IDocument document = getDocument();
        int offsetOfSelectionInDocument = selection.getOffset();
	    
        Position wordAsPosition = extractWordAtPosition(offsetOfSelectionInDocument);
	    
	    String wordAsString = makeStringFromPosition(wordAsPosition);
        if(wordAsString.equals("")) {
            System.out.println("ERROR:BlockMarker.findDirectiveAtCaret(...):Cant extract string from position");
            return null;
        }
        if(wordAsString.equals("if") ||wordAsString.equals("else") || wordAsString.equals("elif") || wordAsString.equals("endif") || wordAsString.equals("ifdef")) {
	        int offset = wordAsPosition.getOffset();
            if(offset >= 3) {
	            try {
                    if(document.getChar(offset-1) == '#' && document.getChar(offset-2) == '/' && document.getChar(offset-3) == '/') {
                        return wordAsPosition;
                    }
                } catch (BadLocationException exception) {
                    System.out.println("ERROR:BlockMarker.findDirectiveAtCaret(...):cant seek for //#:"+exception);
                    return null;
                }
	        }
	    }
	    return null;
	}

    
	private Position extractWordAtPosition(int offset) {
	    IDocument document = getDocument();
	    int lastIndexOfLine;
        try {
            IRegion lineInformtation = document.getLineInformationOfOffset(offset);
            lastIndexOfLine = lineInformtation.getOffset()+lineInformtation.getLength()-1; // TODO: we need a -1 here??
        } catch (BadLocationException exception) {
            System.out.println("ERROR:BlockMarker.extractWordAtPosition(...):Parameter out of valid range.");
	        return null;
        }
        
	    int leftmostIndexOfWord = offset;
	    int rightmostIndexOfWord = offset;
	    // It makes no sense to catch every exception for itself so we use one big try block.
	    try {
		    // search for the left bound.
		    for(int i = offset-1; i >= 0; i--) {
		        // As we are advancing forward and we found a invalid char, step one back to the last vaid char.
		        if( ! Character.isJavaIdentifierPart(document.getChar(i))) {
		            leftmostIndexOfWord = i+1;
		            break;
		        }
		        leftmostIndexOfWord = i;
		    }
		    
		    // If the current char is invalid, the next chars at the right are uninteressting.
		    if( ! Character.isJavaIdentifierPart(document.getChar(offset))) {
		        rightmostIndexOfWord--;// = offset;
		    }
		    // We have at least one valid char, look for more.
		    else {
			    for(int i = offset+1; i <= lastIndexOfLine;i++) {
			        if( ! Character.isJavaIdentifierPart(document.getChar(i))) {
			            rightmostIndexOfWord = i-1;
			            break;
			        }
			        rightmostIndexOfWord = i;
			    }
		    }
	    }
	    catch(BadLocationException exception) {
	        System.out.println("ERROR:BlockMarker.extractWordAtPosition(...):BadLocationException:"+exception);
	        return null;
	    }
	    return new Position(leftmostIndexOfWord,rightmostIndexOfWord-leftmostIndexOfWord+1);
	}
	
	
    private Position getDirectiveFromLine(int currentLine) {
        IDocument document = getDocument();
        IRegion lineAsRegion;
        
        try {
            lineAsRegion = document.getLineInformation(currentLine); 
            //lineAsString = document.get(lineAsRegion.getOffset(),lineAsRegion.getLength());
        }
        catch (BadLocationException exception) {
            System.out.println("ERROR:BlockMarker.getDirectiveFromLine(...):wrong currentLine."+exception);
	        return null;
        }
        
        //This code uses regex
//        String lineAsString;
//        int leftmostIndex;
//        int rightmostIndex;
//        this.matcher.reset(lineAsString);
//        boolean hasMatched = this.matcher.lookingAt();
//        String matchedWord = "";
//        if(hasMatched) {
//            matchedWord = this.matcher.group();
//        }
//        else {
//            return null;
//        }
//        if(matchedWord != null && matchedWord.length() > 0) {
//            leftmostIndex = lineAsRegion.getOffset()+this.matcher.start();
//            rightmostIndex = lineAsRegion.getOffset()+this.matcher.end();
//            return new Position(leftmostIndex,rightmostIndex-leftmostIndex);
//        }
//        return null;
        
        
        int lastIndexInLine = lineAsRegion.getOffset() + lineAsRegion.getLength() -1;
        char c;
        int state = 0;
        int leftmostIndex = 0;
        int rightmostIndex = 0;
        boolean directiveFound = false;
        for(int i = lineAsRegion.getOffset(); i <= lastIndexInLine;i++) {
            try {
                c = document.getChar(i);
                if(state == 0) {
                    if(Character.isWhitespace(c)) {
                        continue;
                    }
                    state = 1;
                }
                if(state == 1) {
                    if(c == '/') {
                        state = 2;
                        continue;
                    }
                }
                if(state == 2) {
                    if(c == '/') {
                        state = 3;
                        continue;
                    }
                }
                if(state == 3) {
                    if(c == '#') {
                        state = 4;
                        continue; 
                    }
                }
                if(state == 4) { // First char of directive found.
                    if(Character.isJavaIdentifierPart(c)) {
                        leftmostIndex = i;
                        rightmostIndex = i;
                        directiveFound = true;
                        state = 5;
                        continue;
                    }
                    directiveFound = false; //the char after # is not valid. abort.
                    break;
                }
                if(state == 5) {
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
                System.out.println("ERROR:BlockMarker.getDirectiveFromLine(...):wrong offset of char."+exception);
    	        return null;
            }
        }
        if(directiveFound) {
            return new Position(leftmostIndex,rightmostIndex-leftmostIndex+1);
        }
        return null;
    }

 

    // startLineIndex is the line with the if directive to start from.
    private List findOtherPositionsForIf(int startLineIndex) {
        int level = 0;
        int state = 0;
        IDocument document = getDocument();
        int maxLineIndex = document.getNumberOfLines() - 1;
        List positionsOfCorrespondingBlockElements = new LinkedList();
        
        if(startLineIndex < 0 || startLineIndex > maxLineIndex) {
            System.out.println("BlockMarker.findOtherPositionsForIf(...):Parameter startLineIndex out of bounds.");
            return positionsOfCorrespondingBlockElements;
        }
        
        int currentLineIndex;
        Position directiveAsPosition;
        String directiveAsString;
        
        // Advance down the document.
        for(currentLineIndex = startLineIndex;currentLineIndex <= maxLineIndex; currentLineIndex++) {
            directiveAsPosition = getDirectiveFromLine(currentLineIndex);
            
            // Do we have a directive in this line?
            if(directiveAsPosition == null) {
                // ... if not, try the next line.
               continue;
            }
            directiveAsString = makeStringFromPosition(directiveAsPosition);
            if(directiveAsString == null){
                System.out.println("ERROR:BlockMarker.findOtherPositionsOfIf(...):Parameter 'directiveAsString' is null.");
                continue;
            }
	        // Use intern to speed comparison up a bit.
            directiveAsString = directiveAsString.intern();
	        
	        if(directiveAsString == "if" && level == 0 && state == 0) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            state = 1;
	            continue;
	        }
	        if(directiveAsString == "elif" && level == 0 && state == 1) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            continue;
	        }
	        if(directiveAsString == "else" && level == 0 && state == 1) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            continue;
	        }
	        // We found a endif on this line in the right level.
	        if(directiveAsString == "endif" && level == 0 && state == 1) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            state = 2;
	            // Dont go any further, we reached the corresponding endif to this if.
	            break;
	        }
	        // We found a nested if block.
	        if(directiveAsString == "if" && state != 0) {
	            System.out.println("level up from if");
	            level = level + 1;
	            continue;
	        }
	        // We found a endif in the wrong level.
	        if(directiveAsString == "endif" && state != 2) {
	            System.out.println("level down from endif");
	            level = level -1;
	            continue;
	        }
            
        }
        return positionsOfCorrespondingBlockElements;
    }
}
