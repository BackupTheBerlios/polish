/*
 * Created on Mar 1, 2005 at 5:36:58 PM.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.plugin.eclipse.css.editor.reconcile;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISharedTextColors;

import antlr.RecognitionException;

import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.model.IModelListener;
import de.enough.polish.plugin.eclipse.css.parser.OffsetAST;
import de.enough.polish.plugin.eclipse.css.parser.PresentationWalker;

/**
 * <p>Reconciles the viewer presentation, the underlying document and the domain model.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 1, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class Reconciler implements IReconciler{

	private ITextViewer textViewer;
	protected CssModel cssModel;
	private TextListener textListener;
	private ModelListener modelListener;
	private ISharedTextColors colors;
	
	
	class TextListener implements ITextInputListener, ITextListener{
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
			// Not needed.
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			Reconciler.this.cssModel.setDocument(newInput);
			reconcileModel();
			reconcilePresentation();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextListener#textChanged(org.eclipse.jface.text.TextEvent)
		 */
		public void textChanged(TextEvent event) {
		    // TODO: Use the TextEvent to finetune the reconciling process.
			Reconciler.this.cssModel.reconcile();
			reconcilePresentation();
		}
	}
	
	// Not really needed at the moment.
	class ModelListener implements IModelListener{

		/* (non-Javadoc)
		 * @see de.enough.polish.plugin.eclipse.css.model.IModelListener#modelChanged()
		 */
		public void modelChanged() {
			//System.out.println("DEBUG:Reconciler.ModelListener.modelChanged().enter.");
		}
		
	}

	
	public Reconciler(CssModel cssModel,ISharedTextColors colors){
		this.cssModel = cssModel;
		this.textListener = new TextListener();
		this.modelListener = new ModelListener();
		this.cssModel.addModelListener(this.modelListener);
		this.colors = colors;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	public void install(ITextViewer viewer) {
		this.textViewer = viewer;
		if( this.textViewer == null){
			System.out.println("ERROR:Reconciler.install():viewer: is null.");
			return;
		}
		this.textViewer.addTextInputListener(this.textListener);
		this.textViewer.addTextListener(this.textListener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#uninstall()
	 */
	public void uninstall() {
		if(this.textViewer != null){
			this.textViewer.removeTextInputListener(this.textListener);
			this.textViewer.removeTextListener(this.textListener);
		}
		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconciler#getReconcilingStrategy(java.lang.String)
	 */
	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		System.out.println("DEBUG:Reconciler.getReconcilingStrategy().enter.");
		return null;
	}

	protected void reconcilePresentation(){
		
	    OffsetAST root = this.cssModel.getAstRoot();
	    if(root == null) {
	        return;
	    }
		TextPresentation textPresentation = new TextPresentation();
		PresentationWalker presentationWalker = new PresentationWalker(); // pull out as field.
		presentationWalker.configure(textPresentation,this.colors);
		try {
            presentationWalker.styleSheet(root);
        } catch (RecognitionException exception) {
            System.out.println("Reconciler.reconcilePresentation():Parse error of AST");
            return;
        }
		this.textViewer.changeTextPresentation(textPresentation,true);
		
	}
	
	protected void reconcileModel(){
		this.cssModel.reconcile();
	}
	
	
	public void dispose(){
		this.cssModel.removeModelListener(this.modelListener);
	}
	
}
