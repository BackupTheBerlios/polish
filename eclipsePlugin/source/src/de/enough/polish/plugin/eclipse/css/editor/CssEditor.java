/*
 * Created on 22-Feb-2005
 */
package de.enough.polish.plugin.eclipse.css.editor;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


/**
 * @author rickyn
 */
public class CssEditor extends TextEditor{

	CssOutlinePage cssOutlinePage;
	
	
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
}
