/*
 * Created on Feb 28, 2005 at 2:44:50 PM.
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
package de.enough.polish.plugin.eclipse.css.editor;

import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import de.enough.polish.plugin.eclipse.css.editor.reconcile.SimpleReconcilerFacade;




/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 28, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CssSourceViewerConfiguration extends SourceViewerConfiguration{
	
	CssEditor editor;
	ISharedTextColors colors;
	SimpleReconcilerFacade simpleReconcilerFacade;
	
	CssSourceViewerConfiguration(CssEditor editor, ISharedTextColors colors){
		this.editor = editor;
		this.colors = colors;
		this.simpleReconcilerFacade = new SimpleReconcilerFacade(this.editor.getCssModel(),colors);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		this.simpleReconcilerFacade.install(sourceViewer);
		//MonoReconciler reconciler = new SimpleReconciler(new SimpleReconcilingStrategy(this.editor.getCssModel()),true);
		//reconciler.install(sourceViewer); //TODO: Obscured program flow: The caller (SourceTextViewer) will call getReconciler().install(this).
		return this.simpleReconcilerFacade;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		this.simpleReconcilerFacade.install(sourceViewer);
		//IPresentationReconciler presentationReconciler = new SimplePresentationReconciler(this.editor.getCssModel());
		/*
		DefaultDamagerRepairer damagerRepairer =  new SimpleDamagerRepairer(getPartionScanner());
		presentationReconciler.setDamager(damagerRepairer,IDocument.DEFAULT_CONTENT_TYPE);
		presentationReconciler.setRepairer(damagerRepairer,IDocument.DEFAULT_CONTENT_TYPE);
		*/
		return this.simpleReconcilerFacade;		
	}

	
	// **************************************************************
	// Stuff for the PresentationReconciler
	/*
	private ITokenScanner getPartionScanner(){
		RuleBasedScanner scanner = new RuleBasedScanner();
		IRule[] rules = new IRule[2];
		
		rules[0] = createCommentRule();
		rules[1] = createStyleNameRule();
		
		scanner.setRules(rules);
		return scanner;	
	}
	
	private IRule createCommentRule(){
		
		IToken commentToken = new Token(new TextAttribute(this.colors.getColor(new RGB(200,100,100)),null,SWT.BOLD));
		*///MultiLineRule commentRule = new MultiLineRule("/*","*/",commentToken);
		/*return commentRule;
	}
	

	private IRule createStyleNameRule(){
		
		IToken styleNameToken = new Token(new TextAttribute(this.colors.getColor(new RGB(0,200,000)),null,SWT.BOLD));
		// FIXME: Eclipse is a mess. If the first string of SingleLineRule is empty or null,
		// a noninformative error message appears "An error occured while opening the editor."
		// without log messages or anything. Conclusion: The predefined rules are too limited.
		SingleLineRule styleNameRule = new SingleLineRule(" ","{",styleNameToken);
		return styleNameRule;
	}
	*/
}
