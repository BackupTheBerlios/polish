/*
 * Created on Mar 23, 2005 at 11:43:21 AM.
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

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.text.IJavaPartitions;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;
import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;




/**
 * <p>This editor is heavily inspired by the ajdt plugin.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 23, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishEditor extends CompilationUnitEditor {
    
    public static final String ID = "de.enough.polish.plugin.eclipse.polishEditor.editor.PolishEditor";
    
    private PropertyChangeListener propertyChangeListener;

    private PolishOccurrencesMarker polishOccurrencesMarker;
    //private PolishOccurrencesMarkerConfigurationUpdater polishOccurrencesMarkerConfigurationUpdater;
    //private ITextListener textListener;
    
    class TextListener implements ITextListener {

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextListener#textChanged(org.eclipse.jface.text.TextEvent)
         */
        public void textChanged(TextEvent event) {
            Position[] positions = new Position[] {};
            try {
                 positions = (event.getDocumentEvent() == null)?positions:event.getDocumentEvent().getDocument().getPositions(IPolishConstants.POLISH_POSITION_CATEGORY);
            } catch (BadPositionCategoryException exception) {
                // TODO ricky handle BadPositionCategoryException
                exception.printStackTrace();
            }
            for(int i = 0; i < positions.length; i++) {
                System.out.print("positions:"+i+" ");
            }
            System.out.println();
            
        }
        
    }

    class PropertyChangeListener implements IPropertyChangeListener{

        /* (non-Javadoc)
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent event) {
            System.out.println("PolishEditor.PropertyChangeListener.propertyChange():event:property:"+event.getProperty()+".event:newValue:"+event.getNewValue());
            handlePreferenceStoreChanged(event);
        }
        
    }
    
    class PolishOccurrencesMarkerConfigurationUpdater  implements ITextInputListener{

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
         /*
          * Not needed.
          */
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
            PolishEditor.this.polishOccurrencesMarker.setDocument(newInput);
        }
    }
    
    class PolishOccurrencesMarker{
        
        private IDocument document;
        private List currentAnnotations;
        
        public PolishOccurrencesMarker() {
            this.currentAnnotations = new ArrayList();
        }

        public void update(ITextSelection selection) {
            System.out.println("PolishOccurrencesMarker.update(...):enter.");
            if( ! isConfigured()) {
                System.out.println("ERROR:PolishEditor.PolishOccurrencesMarker.update(...):this is not configured.");
                return;
            }
            
//          String wordAtSelectionCarret = findWordAtSelectionCarret(selection);
//          System.out.println("PolishOccurrencesMarker.update(...):word around caret:"+wordAtSelectionCarret);
     
            IAnnotationModel annotationModel = PolishEditor.this.getSourceViewer().getAnnotationModel();
            removeAnnotations(annotationModel);
            
            String wordAtSelectionCarret = findWordAtSelectionCaret(selection);
            
            Position[] positions = findPositionsOfWordinDocument(wordAtSelectionCarret);
            Position position;   
            
//            for(int i = 0; i < positions.length; i++) {
//                position = positions[i];
//                System.out.println("position:#"+i+".value"+position.offset);
//            }
            Annotation annotation;
            String text;
            for(int i = 0; i < positions.length; i++) {
                position = positions[i];
                try {
                    text = this.document.get(position.offset,position.length);
                    System.out.println("annotationText:"+text);
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
            final int NOT_FOUND = -1; // due to indexOf() semantics of String.
            int numberOfLinesInDocument = this.document.getNumberOfLines();
            String currentLine;
            Position position;
            List positions = new ArrayList();
            int lengthOfWord = wordAtSelectionCaret.length();
            for(int currentLineNumber = 0; currentLineNumber < numberOfLinesInDocument; currentLineNumber++) {
                try {
                    IRegion lineRegion = this.document.getLineInformation(currentLineNumber);
                    currentLine = this.document.get(lineRegion.getOffset(),lineRegion.getLength());
                    System.out.println("PolishEditor.PolishOccurrencesMarker.findPositionsOfWordinDocument(...):currentLine:"+currentLine.toCharArray());
                } catch (BadLocationException exception) {
                    System.out.println("PolishEditor.PolishOccurrencesMarker.findPositionsOfWordinDocument(...):badLocation.exception:"+exception);
                    continue;
                }
                if(currentLine == null) {
                    continue;
                }
                int offsetOfSearchedWord = currentLine.indexOf(wordAtSelectionCaret);
                if(offsetOfSearchedWord != NOT_FOUND) {
                    try {
                        position = new Position(this.document.getLineOffset(currentLineNumber)+offsetOfSearchedWord,lengthOfWord);
                        System.out.println("position:"+position.getOffset());
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
        
        public void setDocument(IDocument newDocument) {
            this.document = newDocument;
        }
        
        public boolean isConfigured() {
            return this.document != null;
        }

    }
    
    public PolishEditor() {
        System.out.println("PolishEditor.PolishEditor():enter.");
        this.propertyChangeListener = new PropertyChangeListener();
        this.polishOccurrencesMarker = new PolishOccurrencesMarker();
        //this.polishOccurrencesMarkerConfigurationUpdater = new PolishOccurrencesMarkerConfigurationUpdater();
        //this.textListener = new TextListener();
    }
    
    
    public void createPartControl(Composite parent){
        System.out.println("PolishEditor.createPartControl():enter.");
        
        
        IPreferenceStore preferenceStoreJava = this.getPreferenceStore(); // This one origins from JavaEditor and is a chain of 4 stores.
        IPreferenceStore preferenceStorePolish = PolishEditorPlugin.getDefault().getPreferenceStore();

        preferenceStorePolish.addPropertyChangeListener(this.propertyChangeListener);
        
        
       
        IPreferenceStore[] preferenceStores = new IPreferenceStore[2];
        preferenceStores[0] = preferenceStoreJava;
        preferenceStores[1] = preferenceStorePolish;

        // We are forced to do this kludge because all private methods of suberclasses uses this one preferenceStore.
        // We do need write support so we give others this chainPreferenceStore and when we edit some values
        // we get our own store from the plugin. And thus hardwiring as unforgivable like all others.
        IPreferenceStore preferenceStore = new ChainedPreferenceStore(preferenceStores);
        
        
        IColorManager colorManager = new JavaColorManager();
       
        PolishSourceViewerConfiguration polishSourceViewerConfiguration;
        polishSourceViewerConfiguration =  new PolishSourceViewerConfiguration(colorManager,preferenceStore,this,IJavaPartitions.JAVA_PARTITIONING);
        setSourceViewerConfiguration(polishSourceViewerConfiguration);
        
        //getSourceViewer().addTextInputListener(this.polishOccurrencesMarker);
        super.createPartControl(parent);
        
        //getSourceViewer().addTextListener(this.textListener);
        //getSourceViewer().addTextInputListener(this.polishOccurrencesMarkerConfigurationUpdater);
   
    }
   
    protected boolean affectsTextPresentation(PropertyChangeEvent event) {
        if(((PolishSourceViewerConfiguration)getSourceViewerConfiguration()).affectsTextPresentation(event)) {
            return true;
        }
        return super.affectsTextPresentation(event);
    }
    
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
        System.out.println("PolishEditor.handlePreferenceStoreChanged(...):enter.");
        // Here should a change from mark occurences be placed. If e.g. only derectives should be marked.
        super.handlePreferenceStoreChanged(event);
    }
    
    
    protected void updateOccurrenceAnnotations(ITextSelection selection,
            CompilationUnit astRoot) {
//      TODO ricky implement updateOccurrenceAnnotations
        
        // 1. Check if the selection is within //#
        // 2. if so find whole word.
        // 3. use the ast to find all singleLineComments
        // 4. test each line of occurence of word. Consider syntax.
        // 5. Look out for semantics off selected texts. Normal markOcc. does nothing on selecting across word boundaries.
        System.out.println("PolishEditor.updateOccurrenceAnnotations(...):isMarkingOccurrences:"+isMarkingOccurrences());
        System.out.println("PolishEditor.updateOccurrenceAnnotations(...):selection:"+selection.getText());
        this.polishOccurrencesMarker.setDocument(getSourceViewer().getDocument());
        this.polishOccurrencesMarker.update(selection);
        
        
        super.updateOccurrenceAnnotations(selection, astRoot);
    }
    
    // removeOccurrenceAnnotations has to be adopted, too.
}
