/*
 * Created on Apr 18, 2005 at 1:36:19 PM.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;

import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;
import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;



class PolishOccurrencesMarker /*implements ISelectionChangedListener*/{
        
        
        private IDocument document;
        private List currentAnnotations;
        private ISourceViewer sourceViewer;
        //private List marker; // List of PolishAbstractMarker
        
        private String lastSelectedWord;
        
        public PolishOccurrencesMarker(PolishEditor editor) {
            this.currentAnnotations = new ArrayList();
            this.lastSelectedWord = "";
        }

//      1. Check if the selection is within //#
        // 2. if so find whole word.
        // 3. use the ast to find all singleLineComments
        // 4. test each line of occurence of word. Consider syntax.
        // 5. Look out for semantics off selected texts. Normal markOcc. does nothing on selecting across word boundaries.
        public void updateAnnotations(ITextSelection selection, List listOfComments) {
            System.out.println("update starts.");
            if( ! isConfigured()) {
                System.out.println("ERROR:PolishEditor.PolishOccurrencesMarker.update(...):this is not configured.");
                return;
            }
            if( ! caretInPolishDirective(selection)) {
                System.out.println("PolishOcurrencesMarker.updateAnnotations(...):abort.");
                return;
            }
            
            IAnnotationModel annotationModel = this.sourceViewer.getAnnotationModel();
            
            
            
            String wordAtSelectionCaret = findWordAtSelectionCaret(selection);
            System.out.println("selectedWord:X"+wordAtSelectionCaret+"X");
            
            // We actually have a word.
            if(wordAtSelectionCaret.length() == 0) {
                System.out.println("PolishOcurrencesMarker.updateAnnotations(...):abort.");
                return;
            }
            
            // Get the positions for the selected word ether from the previous invocation or get them new.
            Position[] positions;
            if(wordAtSelectionCaret.equals(this.lastSelectedWord)) {
                System.out.println("PolishOcurrencesMarker.updateAnnotations(...):abort.");
                //positions = this.lastPositionsOfSelectedWord;
                return;
            }
            
            // We have a new word. Remember it as the last selected word and get its new positions.
            positions = findPositionsOfWordinListOfComments(wordAtSelectionCaret,listOfComments);
            //this.lastPositionsOfSelectedWord = positions;
            this.lastSelectedWord = wordAtSelectionCaret;
            removeAnnotations(annotationModel);
           
            System.out.println("positions:length:"+positions.length);
            Position position;   
            Annotation annotation;
            String text;
            for(int i = 0; i < positions.length; i++) {
                position = positions[i];
                try {
                    text = this.document.get(position.offset,position.length); // This should be consistent with wordAtSelectionCaret.
                } catch (BadLocationException exception) {
                    System.out.println("PolishEditor.PolishOccurrencesMarker.update(...):wrong position.exception"+exception);
                    continue; 
                }
                annotation = new Annotation(IPolishConstants.POLISH_ANNOTATION,false,text);
                this.currentAnnotations.add(annotation);
                annotationModel.addAnnotation(annotation,positions[i]);
            }    
            System.out.println("update ends here.");
        }
       

private boolean caretInPolishDirective(ITextSelection selection) {
    IRegion lineAsRegion;
    String lineAsString;
    try {
        lineAsRegion = this.document.getLineInformation(selection
                .getStartLine());
        lineAsString = this.document.get(lineAsRegion.getOffset(),
                lineAsRegion.getLength());
    } catch (Exception e) {
        System.out.println("AXR, should not happen.");
        return false;
    }
    return lineAsString.trim().startsWith("//#");
}

        private Position[] findPositionsOfWordinListOfComments(String wordAtSelectionCaret, List listOfComments) {
            int OFFSET_NOT_FOUND = -1; // Due to indexOf() semantics of String.
            String currentCommentAsString;
            int offsetOfSearchedWord;
            Position position;
            Comment currentCommentAsASTNode;
            List positions = new LinkedList();
            
            Iterator iterator = listOfComments.iterator();
            System.out.println("Go through the list of comments.");
            while(iterator.hasNext()) {
                 currentCommentAsASTNode= (Comment)iterator.next();
                if(currentCommentAsASTNode.isLineComment()) {
                    try {
                        currentCommentAsString = this.document.get(currentCommentAsASTNode.getStartPosition(),currentCommentAsASTNode.getLength());       
                    }
                    catch (BadLocationException exception) {
                        PolishEditorPlugin.log("PolishOcurrencesMarker.findPositionsOfWordInListOfComments(...):badlocationException:"+exception);
                        continue;
                    }
                    if( ! lineHasPolishDirectives(currentCommentAsString)) {
                        continue;
                    }
                    offsetOfSearchedWord = currentCommentAsString.indexOf(wordAtSelectionCaret);
                    //TODO: wrap it in a loop to get all words on a given line.
                    if(offsetOfSearchedWord != OFFSET_NOT_FOUND) {
                        position = new Position(currentCommentAsASTNode.getStartPosition()+offsetOfSearchedWord,wordAtSelectionCaret.length());
                        positions.add(position);
                    }
                }
            }
            return (Position[]) positions.toArray(new Position[positions.size()]);
        }

        
        private void removeAnnotations(IAnnotationModel annotationModel) {
            if(annotationModel == null) {
                return;
            }
            System.out.println("remove annotations.");
            for (Iterator it= this.currentAnnotations.iterator(); it.hasNext();) {
    				Annotation annotation= (Annotation) it.next();
    				annotationModel.removeAnnotation(annotation);
    			}
    			this.currentAnnotations.clear();    			
        }
 
        
        private boolean lineHasPolishDirectives(String currentLine) {
            currentLine = currentLine.trim();
            boolean result = currentLine.startsWith("//#");
            return result;
        }

        private String findWordAtSelectionCaret(ITextSelection selection) {
            String word = "";
            int offset = selection.getOffset();  
            int documentLength = this.document.getLength();
            char currentChar;
            int lastValidCharPositionRightEnd = offset;
            try {
                do {
                    currentChar = this.document.getChar(lastValidCharPositionRightEnd);
                    lastValidCharPositionRightEnd++;
                }
                while(isValidCharForWord(currentChar) && (lastValidCharPositionRightEnd < documentLength));
                // Back off by one because we broke the loopp on the next char which does not belong the word anymore.
                lastValidCharPositionRightEnd--;
            }
            catch(Exception exception) {
                System.out.println("PolishEditor.PolishOccurrencesMarker.findWordAtSelectionCaret:BadLocation reached at right end.exception:"+exception);
                return "";
            }
            
            int lastValidCharPositionLeftEnd = offset;
            try {
                currentChar = this.document.getChar(offset);
                
//              This is nesessary. There are two cases:
//			   1. the offset has a valid char. In this case walk and back off by one when you found an invalid char.
//              2. the offset is invalid so the left bound is the offset itself. Do not backup again !
               
                if(isValidCharForWord(currentChar)) { 
                    
                    while(isValidCharForWord(currentChar) && (lastValidCharPositionLeftEnd > 0)){
                        lastValidCharPositionLeftEnd--;
                        currentChar = this.document.getChar(lastValidCharPositionLeftEnd);  
                    }
                    if( ! isValidCharForWord(currentChar)) {
                        // We overshot the left boundary of the word. Back of my one.
                        lastValidCharPositionLeftEnd++;
                    }
                    if(lastValidCharPositionLeftEnd < 0) {
                        lastValidCharPositionLeftEnd = 0;
                    }
                }
                
            }
            catch (BadLocationException exception) {
                System.out.println("PolishEditor.PolishOccurrencesMarker.findWordAtSelectionCaret:BadLocation reached at left end.exception:"+exception);
               return "";
            }
           
            try {
                word = this.document.get(lastValidCharPositionLeftEnd,lastValidCharPositionRightEnd-lastValidCharPositionLeftEnd);
            } catch (BadLocationException exception) {
                System.out.println("PolishEditor.PolishOccurrencesMarker.findWordAtSelectionCaret:BadLocation calculated for selected word.exception"+exception);
                return "";
            }
            return word;
        }

        private boolean isValidCharForWord(char currentChar) {
            return Character.isLetterOrDigit(currentChar);
        }
        
        public void setSourceViewer(ISourceViewer newSourceViewer) {
            if(newSourceViewer == null) {
                PolishEditorPlugin.log("ERROR:PolishEditor.PolishOccurrencesMarker.setSourceViewer(...):Parameter is null.");
                return;
            }
            this.sourceViewer = newSourceViewer;
            this.document = this.sourceViewer.getDocument();
        }
        
        public boolean isConfigured() {
            return this.document != null;
        }

        //TODO: Rename to removeAnnotationsAndReset().
        public void removeAnnotations() {
            removeAnnotations(this.sourceViewer.getAnnotationModel());
            this.lastSelectedWord = "";
        }

//        /*
//        /* (non-Javadoc)
//         * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
//         */
//        public void selectionChanged(SelectionChangedEvent event) {
//            System.out.println("PolishOccurrencesMarker.selectionChanged(...):enter.");
//            if( ! (event instanceof ITextSelection)) {
//                System.out.println("ERROR: PolishOccurrencesMarker.selectionChanged():This shouuld not happend.");
//                return;
//            }
//            ITextSelection textSelectionEvent = (ITextSelection)event;
//            
//            updateAnnotations(textSelectionEvent);
//            
//        }

  
    }