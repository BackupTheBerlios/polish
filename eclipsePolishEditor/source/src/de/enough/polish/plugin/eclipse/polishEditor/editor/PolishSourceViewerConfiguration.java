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

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.rules.RuleBasedScanner;
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
                System.out.println("DocumentListener.documentChanged():document is not instance of IDocumentExtension3");
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
	
	
//    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
//        System.out.println("PolishSourceViewerConfiguration.getPresentationReconciler():enter.");
//        sourceViewer.addTextInputListener(new TextInputListener()); // Only to track invocation.
//        
//        // copy 'n pasted from JavasourceViewerConfiguration.
//        	PresentationReconciler presentationReconciler = new PolishPresentationReconciler();
//        	presentationReconciler.setDocumentPartitioning(IPolishContentTypes.POLISH_PARTITIONING); // We dont need this mechanism.
//
//		DefaultDamagerRepairer defaultDamagerRepairer;
//
//		defaultDamagerRepairer = new DefaultDamagerRepairer(getCodeScanner());
//		presentationReconciler.setDamager(defaultDamagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);
//		presentationReconciler.setRepairer(defaultDamagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);
//
//		defaultDamagerRepairer = new DefaultDamagerRepairer(getJavaDocScanner());
//		presentationReconciler.setDamager(defaultDamagerRepairer, IJavaPartitions.JAVA_DOC);
//		presentationReconciler.setRepairer(defaultDamagerRepairer, IJavaPartitions.JAVA_DOC);
//
//		defaultDamagerRepairer = new DefaultDamagerRepairer(getMultilineCommentScanner());		
//		presentationReconciler.setDamager(defaultDamagerRepairer, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
//		presentationReconciler.setRepairer(defaultDamagerRepairer, IJavaPartitions.JAVA_MULTI_LINE_COMMENT);
//
//		defaultDamagerRepairer = new DefaultDamagerRepairer(getSinglelineCommentScanner());		
//		presentationReconciler.setDamager(defaultDamagerRepairer, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
//		presentationReconciler.setRepairer(defaultDamagerRepairer, IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
//		
//		defaultDamagerRepairer = new DefaultDamagerRepairer(getStringScanner());
//		presentationReconciler.setDamager(defaultDamagerRepairer, IJavaPartitions.JAVA_STRING);
//		presentationReconciler.setRepairer(defaultDamagerRepairer, IJavaPartitions.JAVA_STRING);
//		
//		defaultDamagerRepairer = new DefaultDamagerRepairer(getStringScanner());
//		presentationReconciler.setDamager(defaultDamagerRepairer, IJavaPartitions.JAVA_CHARACTER);
//		presentationReconciler.setRepairer(defaultDamagerRepairer, IJavaPartitions.JAVA_CHARACTER);
//        
//		// Polish stuff.
//		defaultDamagerRepairer = new DefaultDamagerRepairer(getPolishSingleLineCommentScanner());
//        presentationReconciler.setDamager(defaultDamagerRepairer, IPolishContentTypes.POLISH_DIRECTIVE);
//        presentationReconciler.setRepairer(defaultDamagerRepairer, IPolishContentTypes.POLISH_DIRECTIVE);
//        
//        return presentationReconciler;
//    }
    
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

    //TODO: Not needed anymore. Confirm, then remove.
    // Register the content type for polish directives at the end of the java content types.
//    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
//        String[] originalConfiguredContentTypes = super.getConfiguredContentTypes(sourceViewer);
//        String[] newlyConfiguredContentTypes = new String[originalConfiguredContentTypes.length + 1];
//        
//        System.arraycopy(originalConfiguredContentTypes,0,newlyConfiguredContentTypes,0,originalConfiguredContentTypes.length);
//        
//        newlyConfiguredContentTypes[newlyConfiguredContentTypes.length - 1] = IPolishContentTypes.POLISH_DIRECTIVE;
//        
//        return newlyConfiguredContentTypes;
//    }
    
    
    public void handlePropertyChangeEvent(PropertyChangeEvent event) {
        System.out.println("PolishSourceViewerConfiguration.handlePropertyChangeEvent(...):enter.");
        super.handlePropertyChangeEvent(event);
        this.polishSingleLineCommentScanner.adaptToPreferenceChange(event);
    }
}
