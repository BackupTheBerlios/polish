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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import de.enough.mepose.core.model.MeposeModel;
import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;
import de.enough.polish.plugin.eclipse.polishEditor.editor.contentAssist.PolishContentAssist;
import de.enough.polish.plugin.eclipse.polishEditor.editor.indention.PolishIndentStrategy;
import de.enough.polish.plugin.eclipse.polishEditor.editor.indention.PolishJavaAutoIndentStrategy;
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

    private PolishSingleLineCommentScanner polishSingleLineCommentScanner;
    private PolishEditor editor;
    
	public PolishSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
        super(colorManager,preferenceStore,editor,partitioning);
        this.polishSingleLineCommentScanner = new PolishSingleLineCommentScanner(colorManager,preferenceStore);
        if( ! (editor instanceof PolishEditor)) {
            throw new IllegalArgumentException("ERROR:PolishSourceViewerConfiguration.PolishSourceViewerConfiguration:Parameter 'editor' is not instance of 'PolishEditor'");
        }
        this.editor = (PolishEditor)editor; 
    }

    public PolishSingleLineCommentScanner getPolishSingleLineCommentScanner() {
        	return this.polishSingleLineCommentScanner;
    }    
    
    
    protected RuleBasedScanner getSinglelineCommentScanner() {
        return getPolishSingleLineCommentScanner();
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
        // This check is needed because adaptToPreferenceChange cokes on unknown events :(
        if (this.polishSingleLineCommentScanner.affectsBehavior(event))
            this.polishSingleLineCommentScanner.adaptToPreferenceChange(event);
        super.handlePropertyChangeEvent(event);
        
    }
    
    // This is the method for 3.1.
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        String partitioning= getConfiguredDocumentPartitioning(sourceViewer);
        if (IJavaPartitions.JAVA_SINGLE_LINE_COMMENT.equals(contentType))
            return new IAutoEditStrategy[] {new PolishIndentStrategy()};
        if(IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) {
            IAutoEditStrategy[] autoEditStrategies= new IAutoEditStrategy[] { new PolishJavaAutoIndentStrategy(partitioning, getProject()) };
            return autoEditStrategies;
        }
        return super.getAutoEditStrategies(sourceViewer, contentType);
    }

//    public IAutoIndentStrategy getAutoIndentStrategy(ISourceViewer sourceViewer,
//                                                     String contentType) {
//        System.out.println("ERROR:PolishSourceViewerConfiguration.getAutoIndentStrategy(...):enter. This method cant be called by 3.1.M6?!");
//        if (IJavaPartitions.JAVA_SINGLE_LINE_COMMENT.equals(contentType))
//			return new PolishIndentStrategy();
//        // content type used when pasting on the plane. __dftl_partition_content_type
//        return super.getAutoIndentStrategy(sourceViewer, contentType);
//    }
    
    
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        IContentAssistant newIContentAssistant = super.getContentAssistant(sourceViewer);
        if(newIContentAssistant instanceof ContentAssistant) {
            ContentAssistant contentAssistant = (ContentAssistant)newIContentAssistant;
            MeposeModel meposeModel = this.editor.getMeposeModel();
            IContentAssistProcessor polishContentAssist = new PolishContentAssist(meposeModel);
//            VariableContentAssistProcessor contentAssistProcessor = new VariableContentAssistProcessor(meposeModel);
//            contentAssistant.setContentAssistProcessor(contentAssistProcessor,IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
            contentAssistant.setContentAssistProcessor(polishContentAssist,IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
            contentAssistant.enablePrefixCompletion(false);
            return contentAssistant;
        }
        return newIContentAssistant;
        
        // TODO: Old, prior to 3.2
//        System.out.println("DEBUG:PolishSourceViewerConfiguration.getContentAssistant(...):enter.");
//        IContentAssistant newIContentAssistant = super.getContentAssistant(sourceViewer);
//        if(newIContentAssistant instanceof ContentAssistant) {
//            // The cast is a hack to be able to set an processor. The interface itself has no useful methods.
//            ContentAssistant contentAssistant = (ContentAssistant)newIContentAssistant;
//            //CompoundContentAssistProcessor compoundContentAssistProcessor = new CompoundContentAssistProcessor();
//            //compoundContentAssistProcessor.add(new DirectiveContentAssistProcessor());
//            
//            MeposeModel meposeModel = this.editor.getMeposeModel();
//            Environment environment = meposeModel.getEnvironment();
//            VariableContentAssistProcessor processor = new VariableContentAssistProcessor(environment);
//            compoundContentAssistProcessor.add(processor);
//            this.editor.getMeposeModel().addPropertyChangeListener(processor);
//   
//            contentAssistant.setContentAssistProcessor(compoundContentAssistProcessor,IJavaPartitions.JAVA_SINGLE_LINE_COMMENT);
//            return contentAssistant;
//        }
//        return newIContentAssistant;
    }
    
    protected IJavaProject getProject() {
        ITextEditor editor2= getEditor();
        if (editor2 == null)
            return null;
        
        IJavaElement element= null;
        IEditorInput input= editor2.getEditorInput();
        IDocumentProvider provider= editor2.getDocumentProvider();
        if (provider instanceof ICompilationUnitDocumentProvider) {
            ICompilationUnitDocumentProvider cudp= (ICompilationUnitDocumentProvider) provider;
            element= cudp.getWorkingCopy(input);
        } else if (input instanceof IClassFileEditorInput) {
            IClassFileEditorInput cfei= (IClassFileEditorInput) input;
            element= cfei.getClassFile();
        }
        
        if (element == null)
            return null;
        
        return element.getJavaProject();
    }
    
    protected IPreferenceStore getPreferenceStore() {
        return PolishEditorPlugin.getDefault().getPreferenceStore();
    }
}
