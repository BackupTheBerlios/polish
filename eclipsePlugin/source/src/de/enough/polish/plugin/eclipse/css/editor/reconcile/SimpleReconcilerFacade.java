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

import net.percederberg.grammatica.parser.ParserLogException;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISharedTextColors;

import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.parser.PresentationAnalyzer;

/**
 * <p>A facade for the presentationReconciler and the reconciler. This class will install and trigger
 * our own reconcilers to update the model and repair the screen. So we have full control over updates.</p>
 *<p>Another option is to subclass SourceViewer and get rid of the reconciling architecture in the heard of the application.
 *We need to control the sequence of calling of the reconcilers.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 1, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class SimpleReconcilerFacade implements IPresentationReconciler, IReconciler{

	private ITextViewer textViewer;
	private CssModel cssModel;
	private Listener listener;
	private ISharedTextColors colors;

	class Listener implements ITextInputListener, ITextListener{
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentAboutToBeChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
			System.out.println("DEBUG:SimpleReconcilerFacade.Listener.inputDocumentAboutToBeChanged():enter.");
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextInputListener#inputDocumentChanged(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IDocument)
		 */
		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			System.out.println("DEBUG:SimpleReconcilerFacade.Listener.inputDocumentChanged():enter.");
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.ITextListener#textChanged(org.eclipse.jface.text.TextEvent)
		 */
		public void textChanged(TextEvent event) {
			SimpleReconcilerFacade.this.cssModel.reconcile(event);
			reconcilePresentation();
		}
	}
	
	
	public SimpleReconcilerFacade(CssModel cssModel,ISharedTextColors colors){
		this.cssModel = cssModel;
		this.listener = new Listener();
		this.colors = colors;
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#install(org.eclipse.jface.text.ITextViewer)
	 */
	public void install(ITextViewer viewer) {
		// Beware that both interfaces have this method. So it will be called twice.
		this.textViewer = viewer;
		if( this.textViewer == null){
			System.out.println("DEBUG:SimpleReconcilerFacade.install():viewer: is null.");
			return;
		}
		this.textViewer.addTextInputListener(this.listener);
		this.textViewer.addTextListener(this.listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#uninstall()
	 */
	public void uninstall() {
		System.out.println("DEBUG:SimpleReconcilerFacade.uninstall():enter.");	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#getDamager(java.lang.String)
	 */
	public IPresentationDamager getDamager(String contentType) {
		System.out.println("DEBUG:SimpleReconcilerFacade.getDamager().enter.");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.presentation.IPresentationReconciler#getRepairer(java.lang.String)
	 */
	public IPresentationRepairer getRepairer(String contentType) {
		System.out.println("DEBUG:SimpleReconcilerFacade.getRepairer().enter.");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.reconciler.IReconciler#getReconcilingStrategy(java.lang.String)
	 */
	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		System.out.println("DEBUG:SimpleReconcilerFacade.getReconcilingStrategy().enter.");
		return null;
	}

	private void reconcilePresentation(){
		IDocument document = this.cssModel.getDocument();
		if(document == null){
			return;
		}
		
		TextPresentation textPresentation = new TextPresentation();
		PresentationAnalyzer presentationAnalyzer = new PresentationAnalyzer(textPresentation,document,this.colors);
		try {
			presentationAnalyzer.analyze(this.cssModel.getRootNode());
			//traverseRootNodeAddTextPresentation(this.cssModel.getRootNode(),document,textPresentation);
		} catch (ParserLogException exception) {
			System.out.println("DFDSFDSJJ");
		}
		
		//StyleRange styleRange = new StyleRange(0,100,this.colors.getColor(new RGB(125,125,0)),null);
		//textPresentation.addStyleRange(styleRange);
		this.textViewer.changeTextPresentation(textPresentation,false);
	}
	/*
	private void traverseRootNodeAddTextPresentation(Node rootNode, IDocument document, TextPresentation textPresentation){
		if(rootNode == null){
			System.out.println("ERROR:SimpleReconcilerFacade.traerseRootNodeAddTextPresentation().rootNode:is null.");
			return;
		}
		LinkedList remainingNodes = new LinkedList();
		remainingNodes.addFirst(rootNode);
		Node node;
		while( ! remainingNodes.isEmpty()){
			node = (Node)remainingNodes.getFirst();
			System.out.println("BLA:"+remainingNodes);
			remainingNodes.removeFirst();
			createStyleRange(node,document,textPresentation);
			int childCount = node.getChildCount();
			for(int i = 0; i < childCount; i++){
				remainingNodes.addFirst(rootNode.getChildAt(i));
			}
			
		}
	}
*/
	/**
	 * @param node
	 * @param textPresentation
	 */
	/*
	private void createStyleRange(Node node, IDocument document, TextPresentation textPresentation) {
		if(node.getId() == PolishCssConstants.NAME){
			//TODO: We could change the token class to transport the position in addition to
			// line and offset.
			int position = -1;
			int length = -1;
			try {
				position = document.getLineOffset(node.getStartLine()) + node.getStartColumn();
				length = (document.getLineOffset(node.getEndLine()) + node.getEndColumn()) - position;
				StyleRange styleRange = new StyleRange(position,length,this.colors.getColor(new RGB(100,0,0)),null);
				textPresentation.addStyleRange(styleRange);
			} catch (BadLocationException exception) {
				System.out.println("ERROR:SimpleReconcilerFacade.createStyleRange():position and length beyond document.position:"+position+".length:"+length);
			}
			
			
		}
		
	}
	*/
	
}
