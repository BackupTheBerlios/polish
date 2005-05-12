/*
 * Created on Mar 23, 2005 at 1:13:27 PM.
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

import org.eclipse.jdt.internal.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IAutoIndentStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;

import de.enough.polish.plugin.eclipse.polishEditor.editor.presentation.PolishSingleLineCommentScanner;


/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 23, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishSourceViewerConfiguration extends JavaSourceViewerConfiguration {
    //TODO: Not needed. Remove.
    class DocumentListener implements IDocumentListener{

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
         */
        public void documentAboutToBeChanged(DocumentEvent event) {
            /*
             * 
             */
            
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
         */
        public void documentChanged(DocumentEvent event) {
           
            IDocument document = event.getDocument();
            if( ! (document instanceof IDocumentExtension3)) {
                System.out.println("ERROR:DocumentListener.documentChanged():document is not instance of IDocumentExtension3");
                return;
            }
            IDocumentExtension3 documentExtension3 = (IDocumentExtension3) document;
            System.out.println("DocumentListener.documentChanged():partions:");
            String[] partitions = documentExtension3.getPartitionings();
            for(int i = 0; i < partitions.length; i++) {
                System.out.print(partitions[i]+" ");
            }
            System.out.println();
            
        }
        
    }
    
    
    class TextInputListener implements ITextInputListener{

        DocumentListener documentListener = new DocumentListener();
        
        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
            /*
             * 
             */
       }

        /* (non-Javadoc)
         * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
         */
        public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
            System.out.println("PolishEditor.TextInputListener.inputDocumentChanged():enter.newInput:"+newInput);
            if(oldInput != null) {
                oldInput.removeDocumentListener(this.documentListener);
            }
            if(newInput != null) {
                newInput.addDocumentListener(this.documentListener);
            }
        }
        
    }

    private PolishSingleLineCommentScanner polishSingleLineCommentScanner;
    
	public PolishSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
		super(colorManager,preferenceStore,editor,partitioning);
		this.polishSingleLineCommentScanner = new PolishSingleLineCommentScanner(colorManager,preferenceStore);
	}

    public PolishSingleLineCommentScanner getPolishSingleLineCommentScanner() {
        	return this.polishSingleLineCommentScanner;
    }    
    
    
    protected RuleBasedScanner getSinglelineCommentScanner() {
        //System.out.println("PolishSourceViewerConfiguration.getSinglelineCommentScanner():enter.");
        return this.polishSingleLineCommentScanner;
    }
    
    
	/*// DO NOT USE: the super constructor will use this method which return an not initialzed value (null).
	protected IColorManager getColorManager() {
	    return this.colorManager;
	}
	*/

    public boolean affectsTextPresentation(PropertyChangeEvent event) {
        if(this.polishSingleLineCommentScanner.affectsBehavior(event)) {
            return true;
        }
        return super.affectsTextPresentation(event);
    }
    
    public void handlePropertyChangeEvent(PropertyChangeEvent event) {
        System.out.println("PolishSourceViewerConfiguration.handlePropertyChangeEvent(...):enter.");
        // This check is needed because adaptToPreferenceChange cokes on unknown events :(
        if (this.polishSingleLineCommentScanner.affectsBehavior(event))
            this.polishSingleLineCommentScanner.adaptToPreferenceChange(event);
        super.handlePropertyChangeEvent(event);
        
    }
    
    
    public IAutoIndentStrategy getAutoIndentStrategy(ISourceViewer sourceViewer,
                                                     String contentType) {
        //if (IJavaPartitions.JAVA_SINGLE_LINE_COMMENT.equals(contentType))
        if (IJavaPartitions.JAVA_SINGLE_LINE_COMMENT.equals(contentType))
			return new PolishIndentStrategy();
        return super.getAutoIndentStrategy(sourceViewer, contentType);
    }
}
