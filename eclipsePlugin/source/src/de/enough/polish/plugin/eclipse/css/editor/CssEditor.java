/*
 * Created on 22-Feb-2005
 */
package de.enough.polish.plugin.eclipse.css.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.enough.polish.plugin.eclipse.css.CssEditorPlugin;
import de.enough.polish.plugin.eclipse.css.editor.outline.CssOutlinePage;
import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.parser.CssParser;
import de.enough.polish.plugin.eclipse.css.parser.CssTokenizer;


/**
 * @author rickyn
 */
public class CssEditor extends TextEditor{

	CssOutlinePage cssOutlinePage;
	// The facede for the model.
	CssModel cssModel;
	CssParser parser;
	CssTokenizer tokenzier;
	IDocument document;
	
	public CssEditor(){
		setSourceViewerConfiguration(new CssSourceViewerConfiguration(this,getSharedColors()));
	}
	
	public Object getAdapter(Class requiredClass) {
		if (IContentOutlinePage.class.equals(requiredClass)) {
			if (this.cssOutlinePage == null) {
				this.cssOutlinePage = new CssOutlinePage();
			}
			return this.cssOutlinePage;
		}
		return super.getAdapter(requiredClass);
	}
	
	
	public CssModel getCssModel(){
		return this.cssModel;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		//TODO: This shows us that the DocumentListener ist called after the presentation reconciler
		// and before the reconciler... Dont think, swallow.
		this.document = getDocumentProvider().getDocument(getEditorInput());
		this.document.addDocumentListener(new SimpleDocumentListener());
		CssEditorPlugin.getDefault().setEditor(this);
		this.cssModel = new CssModel(this.document);
	}
}
