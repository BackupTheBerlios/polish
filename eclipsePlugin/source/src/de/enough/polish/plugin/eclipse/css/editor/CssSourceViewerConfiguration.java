/*
 * Created on 22-Feb-2005
 */
package de.enough.polish.plugin.eclipse.css.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import de.enough.polish.plugin.eclipse.css.editor.reconcile.SimpleDamagerRepairer;
import de.enough.polish.plugin.eclipse.css.editor.reconcile.SimplePresentationReconciler;
import de.enough.polish.plugin.eclipse.css.editor.reconcile.SimpleReconciler;
import de.enough.polish.plugin.eclipse.css.editor.reconcile.SimpleReconcilingStrategy;


/**
 * @author rickyn
 */
public class CssSourceViewerConfiguration extends SourceViewerConfiguration{
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		MonoReconciler monoReconciler = new SimpleReconciler(new SimpleReconcilingStrategy(),true);
		//monoReconciler.install(sourceViewer); //TODO: Obscured program flow: The caller (SourceTextViewer) will call getReconciler().install(this).
		return monoReconciler;
	}
	
	CssEditor editor;
	ISharedTextColors colors;
	
	CssSourceViewerConfiguration(CssEditor editor, ISharedTextColors colors){
		this.editor = editor;
		this.colors = colors;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		
		PresentationReconciler presentationReconciler = new SimplePresentationReconciler();
		
		DefaultDamagerRepairer damagerRepairer =  new SimpleDamagerRepairer(getPartionScanner());
		presentationReconciler.setDamager(damagerRepairer,IDocument.DEFAULT_CONTENT_TYPE);
		presentationReconciler.setRepairer(damagerRepairer,IDocument.DEFAULT_CONTENT_TYPE);
		return presentationReconciler;		
	}

	
	// **************************************************************
	// Stuff for the PresentationReconciler
	
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
		MultiLineRule commentRule = new MultiLineRule("/*","*/",commentToken);
		return commentRule;
	}
	

	private IRule createStyleNameRule(){
		
		IToken styleNameToken = new Token(new TextAttribute(this.colors.getColor(new RGB(0,200,000)),null,SWT.BOLD));
		// FIXME: Eclipse is a mess. If the first string of SingleLineRule is empty or null,
		// a noninformative error message appears "An error occured while opening the editor."
		// without log messages or anything. Conclusion: The predefined rules are too limited.
		SingleLineRule styleNameRule = new SingleLineRule(" ","{",styleNameToken);
		return styleNameRule;
	}
	
}
