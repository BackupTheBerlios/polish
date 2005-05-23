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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;
import de.enough.polish.plugin.eclipse.polishEditor.utils.PolishDocumentUtils;
import de.enough.polish.plugin.eclipse.utils.States;

/*
 * TODO: Change algorithm to look only up and down. The marking of the current word is outside of
 * the method so the statemachine inside is easier to understand.
 */

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
    
    //TODO: Move to IPolishConstants
    public static final String[] IF_DIRECTIVES = {"if","ifdef","ifndef"};
    public static final String[] ELSE_DIRECTIVES = {"else","elif","elifdef","elifndef"};
    public static final String[] ENDIF_DIRECTIVES = {"endif"};
    
    public static final int STATE_INIT = 1;
    public static final int STATE_SEARCH_FOR_IF = 2;
    public static final int STATE_SEARCH_FOR_ELSE = 4;
    public static final int STATE_SEARCH_FOR_ENDIF = 8;
    
    private States state;
   
	
    public BlockMarker() {
        this.state = new States();
    }
    
    
    public void updateAnnotations(ITextSelection selection) {
        super.updateAnnotations(selection);
//        List positions = findOtherPositionsforElse(selection.getStartLine());
//        for (Iterator iterator = positions.iterator(); iterator.hasNext(); ) {
//            Position position = (Position) iterator.next();
//            System.out.println("DEBUG:BlockMarker.updateAnnotations(...):position:"+makeStringFromPosition(position));
//        }
//        
//        return;
        
        List newPositions = new ArrayList();
        Position blockDirectiveAsPosition;
        
        blockDirectiveAsPosition = PolishDocumentUtils.findBlockDirectiveAtCaret(getDocument(),selection.getOffset());
        if(blockDirectiveAsPosition == null) {
           System.out.println("DEBUG:BlockMarker.updateAnnotations(...):Nothing to do for BlockMarker.updateAnnotations(...).");
           return;
        }
        
//        blockDirectiveAsPosition = getDirectiveFromLine(selection.getStartLine());
//        if(blockDirectiveAsPosition == null || ! blockDirectiveAsPosition.overlapsWith(selection.getOffset(),selection.getLength())) {
//            System.out.println("DEBUG:BlockMarker.updateAnnotations(...):Nothing to do for BlockMarker.updateAnnotations(...).");
//            return;
//        }
        
        // Search for corresponding directives.
        String blockDirectiveAsString = PolishDocumentUtils.makeStringFromPosition(getDocument(),blockDirectiveAsPosition);
        blockDirectiveAsString = blockDirectiveAsString.intern();
        
        if(PolishDocumentUtils.isDirective(blockDirectiveAsString,IF_DIRECTIVES)) {
            newPositions.addAll(findOtherPositionsForIf(selection.getStartLine()));
        }
        if(PolishDocumentUtils.isDirective(blockDirectiveAsString,ELSE_DIRECTIVES)) {
            newPositions.addAll(findOtherPositionsforElse(selection.getStartLine()));
        }
        if(PolishDocumentUtils.isDirective(blockDirectiveAsString,ENDIF_DIRECTIVES)) {
            newPositions.addAll(findOtherPositionsForEndif(selection.getStartLine()));
        }
        // Annotate the found positions of corresponding directives.
        Position position;
        String text;
        Annotation annotation;
        for(Iterator iterator = newPositions.iterator();iterator.hasNext();) {
          position = (Position)iterator.next();
          text = PolishDocumentUtils.makeStringFromPosition(getDocument(),position);
          if(text.equals("")) {
              System.out.println("BlockMarker.updateAnnotations(...):ERROR:cant extract word from position.");
              continue;
          }
          annotation = new Annotation(IPolishConstants.POLISH_ANNOTATION,false,text);
          addAnnotation(annotation);
          getAnnotationModel().addAnnotation(annotation,position);
      }
    }
    
    

    

	
	

    // Beware to use internd strings only as we compare with ==.

 
    private List findOtherPositionsforElse(int startLineIndex) {
        int level = 0;
        this.state.setState(STATE_SEARCH_FOR_ELSE);
        
        IDocument document = getDocument();
        int maxLineIndex = document.getNumberOfLines() - 1;
        List positionsOfCorrespondingBlockElements = new LinkedList();
        
        if(startLineIndex < 0 || startLineIndex > maxLineIndex) {
            System.out.println("ERROR:BlockMarker.findOtherPositionsForIf(...):Parameter startLineIndex out of bounds.");
            return positionsOfCorrespondingBlockElements;
        }
        
        int currentLineIndex;
        Position directiveAsPosition;
        String directiveAsString;
        
        // Advance up the document.
        for(currentLineIndex = startLineIndex;currentLineIndex >= 0; currentLineIndex--) {
            directiveAsPosition = PolishDocumentUtils.getDirectiveFromLine(getDocument(),currentLineIndex);
           
            directiveAsString = PolishDocumentUtils.makeStringFromPosition(getDocument(),directiveAsPosition);
            if(directiveAsString == null){
                System.out.println("ERROR:BlockMarker.findOtherPositionsOfIf(...):Parameter 'directiveAsString' is null.");
                continue;
            }
            
//          Use intern to speed comparison up a bit.
            directiveAsString = directiveAsString.intern();
	        
            if(PolishDocumentUtils.isDirective(directiveAsString,IF_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_IF)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            break;
	        }
            
            if(PolishDocumentUtils.isDirective(directiveAsString,ELSE_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_ELSE)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            this.state.reset();
	            this.state.addState(STATE_SEARCH_FOR_IF);
	            this.state.addState(STATE_SEARCH_FOR_ELSE);
	            continue;
	        }
            
//          We found a nested if block.
	        if(PolishDocumentUtils.isDirective(directiveAsString,IF_DIRECTIVES) && ! this.state.isInState(STATE_SEARCH_FOR_IF)) { 
	            level = level + 1;
	            continue;
	        }
	        // We found a endif in the wrong level.
	        if(PolishDocumentUtils.isDirective(directiveAsString,ENDIF_DIRECTIVES) && ! this.state.isInState(STATE_SEARCH_FOR_ENDIF)) {
	            level = level - 1;
	            continue;
	        }
        }
        
        // We found the corresponding if, search for the endif and other else.
        this.state.reset();
        this.state.addState(STATE_SEARCH_FOR_ENDIF);
        this.state.addState(STATE_SEARCH_FOR_ELSE);
        
//      Advance down the document.
        for(currentLineIndex = startLineIndex+1;currentLineIndex <= maxLineIndex; currentLineIndex++) {
            directiveAsPosition = PolishDocumentUtils.getDirectiveFromLine(getDocument(),currentLineIndex);
            
//          Do we have a directive in this line?
            if(directiveAsPosition == null) {
                // ... if not, try the next line.
               continue;
            }
            directiveAsString = PolishDocumentUtils.makeStringFromPosition(getDocument(),directiveAsPosition);
            if(directiveAsString == null){
                System.out.println("ERROR:BlockMarker.findOtherPositionsOfIf(...):Parameter 'directiveAsString' is null.");
                continue;
            }
            
	        // Use intern to speed comparison up a bit.
            directiveAsString = directiveAsString.intern();
	        
	        if(PolishDocumentUtils.isDirective(directiveAsString,ELSE_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_ELSE)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            this.state.reset();
	            this.state.addState(STATE_SEARCH_FOR_ENDIF);
	            this.state.addState(STATE_SEARCH_FOR_ELSE); // We may find other else on this level.
	            continue;
	        }
	        // We found a endif on this line in the right level.
	        if(PolishDocumentUtils.isDirective(directiveAsString,ENDIF_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_ENDIF)) {

	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            // Dont go any further, we reached the corresponding endif to this if.
	            break;
	        }
	        // We found a nested if block.
	        if(PolishDocumentUtils.isDirective(directiveAsString,IF_DIRECTIVES) && ! this.state.isInState(STATE_SEARCH_FOR_IF)) { 
	            level = level + 1;
	            continue;
	        }
	        // We found a endif in the wrong level.
	        if(PolishDocumentUtils.isDirective(directiveAsString,ENDIF_DIRECTIVES) && ! this.state.isInState(STATE_SEARCH_FOR_ENDIF)) {
	            level = level - 1;
	            continue;
	        }
        }
        
        return positionsOfCorrespondingBlockElements;
    }

    private List findOtherPositionsForEndif(int startLineIndex) {
        int level = 0;
        this.state.setState(STATE_SEARCH_FOR_ENDIF);
        
        IDocument document = getDocument();
        int maxLineIndex = document.getNumberOfLines() - 1;
        List positionsOfCorrespondingBlockElements = new LinkedList();
        
        if(startLineIndex < 0 || startLineIndex > maxLineIndex) {
            System.out.println("ERROR:BlockMarker.findOtherPositionsForIf(...):Parameter startLineIndex out of bounds.");
            return positionsOfCorrespondingBlockElements;
        }
        
        int currentLineIndex;
        Position directiveAsPosition;
        String directiveAsString;
        
        // Advance up the document.
        for(currentLineIndex = startLineIndex;currentLineIndex >= 0; currentLineIndex--) {
            directiveAsPosition = PolishDocumentUtils.getDirectiveFromLine(getDocument(),currentLineIndex);
           
            
            if(directiveAsPosition == null){
                continue;
            }
            directiveAsString = PolishDocumentUtils.makeStringFromPosition(getDocument(),directiveAsPosition);
//          Use intern to speed comparison up a bit.
            directiveAsString = directiveAsString.intern();
	        
            if(PolishDocumentUtils.isDirective(directiveAsString,IF_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_IF)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            System.out.println("DEBUG:FOUND IF2");
	            break;
	        }
            
            if(PolishDocumentUtils.isDirective(directiveAsString,ELSE_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_ELSE)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
//	            this.state.reset();
//	            this.state.addState(STATE_SEARCH_FOR_IF);
//	            this.state.addState(STATE_SEARCH_FOR_ELSE);
	            System.out.println("DEBUG:FOUND ELSE2");
	            continue;
	        }
            if(PolishDocumentUtils.isDirective(directiveAsString,ENDIF_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_ENDIF)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            System.out.println("DEBUG:FOUND ENDIF2");
	            this.state.reset();
	            this.state.addState(STATE_SEARCH_FOR_IF);
	            this.state.addState(STATE_SEARCH_FOR_ELSE);
	            continue;
	        }
//          We found a nested if block.
	        if(PolishDocumentUtils.isDirective(directiveAsString,IF_DIRECTIVES) && level != 0) { 
	            System.out.println("DEBUG:BlockMarker.findOtherPositionsForEndif(...):levelUp:directiveAsString:"+directiveAsString);
	            level = level + 1;
	            continue;
	        }
	        // We found a endif in the wrong level.
	        if(PolishDocumentUtils.isDirective(directiveAsString,ENDIF_DIRECTIVES) && ! this.state.isInState(STATE_SEARCH_FOR_ENDIF)) {
	            System.out.println("DEBUG:BlockMarker.findOtherPositionsForEndif(...):levelDown:directiveAsString:"+directiveAsString);
	            level = level - 1;
	            continue;
	        }
        }
        return positionsOfCorrespondingBlockElements;
    }
    
    // startLineIndex is the line with the if directive to start from.
    private List findOtherPositionsForIf(int startLineIndex) {
        int level = 0;
        this.state.setState(STATE_SEARCH_FOR_IF);
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
            directiveAsPosition = PolishDocumentUtils.getDirectiveFromLine(getDocument(),currentLineIndex);
            
            // Do we have a directive in this line?
            if(directiveAsPosition == null) {
                // ... if not, try the next line.
               continue;
            }
            directiveAsString = PolishDocumentUtils.makeStringFromPosition(getDocument(),directiveAsPosition);
            if(directiveAsString == null){
                System.out.println("ERROR:BlockMarker.findOtherPositionsOfIf(...):Parameter 'directiveAsString' is null.");
                continue;
            }
	        // Use intern to speed comparison up a bit.
            directiveAsString = directiveAsString.intern();
	        
	        if(PolishDocumentUtils.isDirective(directiveAsString,IF_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_IF)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            this.state.setState(STATE_SEARCH_FOR_ELSE);
	            this.state.addState(STATE_SEARCH_FOR_ENDIF);
	            continue;
	        }
	        if(PolishDocumentUtils.isDirective(directiveAsString,ELSE_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_ELSE)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            continue;
	        }
	        
	        // We found a endif on this line in the right level.
	        if(PolishDocumentUtils.isDirective(directiveAsString,ENDIF_DIRECTIVES) && level == 0 && this.state.isInState(STATE_SEARCH_FOR_ENDIF)) {
	            positionsOfCorrespondingBlockElements.add(directiveAsPosition);
	            // Dont go any further, we reached the corresponding endif to this if.
	            break;
	        }
	        // We found a nested if block.
	        if(PolishDocumentUtils.isDirective(directiveAsString,IF_DIRECTIVES) && ! this.state.isInState(STATE_SEARCH_FOR_IF)) {
	            level = level + 1;
	            continue;
	        }
	        // We found a endif in the wrong level.
	        if(PolishDocumentUtils.isDirective(directiveAsString,ENDIF_DIRECTIVES) && level != 0) {
	            level = level -1;
	            continue;
	        }
        }
        return positionsOfCorrespondingBlockElements;
    }
}
