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
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;
import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;


class PolishOccurrencesMarker implements ISelectionChangedListener{
        
        //private final PolishEditor polishEditor;
        private IDocument document;
        private List currentAnnotations;
        private ISourceViewer sourceViewer;
        
        public PolishOccurrencesMarker(PolishEditor editor) {
            this.currentAnnotations = new ArrayList();
            //this.sourceViewer = sourceViewer;
            //this.document = this.sourceViewer.getDocument();
            //this.polishEditor = editor;
        }

//      1. Check if the selection is within //#
        // 2. if so find whole word.
        // 3. use the ast to find all singleLineComments
        // 4. test each line of occurence of word. Consider syntax.
        // 5. Look out for semantics off selected texts. Normal markOcc. does nothing on selecting across word boundaries.
        public void updateAnnotations(ITextSelection selection) {
            if( ! isConfigured()) {
                System.out.println("ERROR:PolishEditor.PolishOccurrencesMarker.update(...):this is not configured.");
                return;
            }
      
           IAnnotationModel annotationModel = this.sourceViewer.getAnnotationModel();
            removeAnnotations(annotationModel);
            
            String wordAtSelectionCaret = findWordAtSelectionCaret(selection);
            
            Position[] positions = findPositionsOfWordinDocument(wordAtSelectionCaret);
            Position position;   
            
            Annotation annotation;
            String text;
            for(int i = 0; i < positions.length; i++) {
                position = positions[i];
                try {
                    text = this.document.get(position.offset,position.length);
                } catch (BadLocationException exception) {
                    System.out.println("PolishEditor.PolishOccurrencesMarker.update(...):wrong position.exception"+exception);
                    continue; 
                }
                annotation = new Annotation(IPolishConstants.POLISH_ANNOTATION,false,text);
                this.currentAnnotations.add(annotation);
                annotationModel.addAnnotation(annotation,positions[i]);
            }    
        }
        
        private void removeAnnotations(IAnnotationModel annotationModel) {
            for (Iterator it= this.currentAnnotations.iterator(); it.hasNext();) {
    				Annotation annotation= (Annotation) it.next();
    				annotationModel.removeAnnotation(annotation);
    			}
    			this.currentAnnotations.clear();
        }
        
        private Position[] findPositionsOfWordinDocument(String wordAtSelectionCaret) {
            
//            List positions = new ArrayList();
//            
//            int numberOfLinesInDocument = this.document.getNumberOfLines();
//            String currentLine;
//            Position position;
//            
            
            //for(int currentLineNumber = 0; currentLineNumber < numberOfLinesInDocument; currentLineNumber++) {
            
            
            List positions = new ArrayList();
            final int OFFSET_NOT_FOUND = -1; // due to indexOf() semantics of String.
            int numberOfLinesInDocument = this.document.getNumberOfLines();
            String currentLine;
            Position position;
            
            int lengthOfWord = wordAtSelectionCaret.length();
            for(int currentLineNumber = 0; currentLineNumber < numberOfLinesInDocument; currentLineNumber++) {
                try {
                    IRegion lineRegion = this.document.getLineInformation(currentLineNumber);
                    currentLine = this.document.get(lineRegion.getOffset(),lineRegion.getLength());
                } catch (BadLocationException exception) {
                    System.out.println("PolishEditor.PolishOccurrencesMarker.findPositionsOfWordinDocument(...):badLocation.exception:"+exception);
                    continue;
                }
                if(currentLine == null) {
                    continue;
                }
                
                if( ! lineHasPolishDirectives(currentLine)) {
                    continue;
                }
                //TODO: Make a loop to get words on the same line.
                int offsetOfSearchedWord = currentLine.indexOf(wordAtSelectionCaret);
                if(offsetOfSearchedWord != OFFSET_NOT_FOUND) {
                    try {
                        position = new Position(this.document.getLineOffset(currentLineNumber)+offsetOfSearchedWord,lengthOfWord);
                        positions.add(position);
                    } catch (BadLocationException exception) {
                        System.out.println("PolishEditor.PolishOccurrencesMarker.findPositionsOfWordinDocument(...):badLocation.exception:"+exception);
                        continue;
                    }
                }
            }
            
            return (Position[]) positions.toArray(new Position[positions.size()]);
            
            
//            String content= this.document.get();
//            List result = new LinkedList();
//            
//            int idx= content.indexOf(wordAtSelectionCarret);
//            Position position;
//    			while (idx != -1) {
//    			    
//    			    position = new Position(idx, wordAtSelectionCarret.length());
//    			    result.add(position);
//    			    
//    			    idx= content.indexOf(wordAtSelectionCarret, idx + 1);
//    		}       
//         return (Position[]) result.toArray(new Position[result.size()]);
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
                // Back off by one because we broke the look on the next char which does not belong the word anymore.
                lastValidCharPositionRightEnd--;
            }
            catch(Exception exception) {
                System.out.println("PolishEditor.PolishOccurrencesMarker.findWordAtSelectionCaret:BadLocation reached at right end.exception:"+exception);
                return "";
            }
            
            int lastValidCharPositionLeftEnd = offset;
            try {
                do {
                    currentChar = this.document.getChar(lastValidCharPositionLeftEnd);
                    lastValidCharPositionLeftEnd--;
                }
                while(isValidCharForWord(currentChar) && (lastValidCharPositionLeftEnd >= 0));
                // Two possiblities:
                //  1. If we reach the end (-1) and the last char was valid, set the position up by one to get the valid position (0)
                //  2. If we do not have a valid char, set the position up by two, one for last valid char and one because we decrement when we read the invalid char.
                if( ! isValidCharForWord(currentChar)) {
                    // 2. possiblity.
                    lastValidCharPositionLeftEnd++;
                }
                // Get to the last valid char.
                lastValidCharPositionLeftEnd++;
            }
            catch(Exception exception) {
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

        /**
         * 
         */
        public void removeAnnotations() {
            removeAnnotations(this.sourceViewer.getAnnotationModel());
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
         */
        public void selectionChanged(SelectionChangedEvent event) {
            System.out.println("PolishOccurrencesMarker.selectionChanged(...):enter.");
            if( ! (event instanceof ITextSelection)) {
                System.out.println("ERROR: PolishOccurrencesMarker.selectionChanged():This shouuld not happend.");
                return;
            }
            ITextSelection textSelectionEvent = (ITextSelection)event;
            
            updateAnnotations(textSelectionEvent);
            
        }

  
    }