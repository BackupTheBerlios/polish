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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.enough.polish.plugin.eclipse.css.CssEditorPlugin;
import de.enough.polish.plugin.eclipse.css.editor.outline.CssOutlinePage;
import de.enough.polish.plugin.eclipse.css.model.CssModel;


/**
 * <p>CssEditor for J2ME Polish CSS files.</p>
 * 
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 28, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CssEditor extends TextEditor{

	CssOutlinePage cssOutlinePage;
	CssModel cssModel;
	//CssParser parser;
	//CssTokenizer tokenzier;
	IDocument document;
	
	public CssEditor(){
		this.cssModel = new CssModel();
		setSourceViewerConfiguration(new CssSourceViewerConfiguration(this,getSharedColors()));
		CssEditorPlugin.getDefault().setEditor(this);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class requiredClass) {
		if (IContentOutlinePage.class.equals(requiredClass)) {
			if (this.cssOutlinePage == null) {
				this.cssOutlinePage = new CssOutlinePage(this.cssModel);
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
	}
	
	public void setFocus(){
		super.setFocus();
	}
	
	public void setCaretToOffset(int offset){
		ISourceViewer sourceViewer = getSourceViewer();
		if(sourceViewer == null){
			return;
		}
		StyledText textWidget = sourceViewer.getTextWidget();
		if(textWidget == null){
			return;
		}
		textWidget.setCaretOffset(offset);
		int line = textWidget.getLineAtOffset(offset);
		textWidget.setTopIndex(line);
	}
	
	public void doActivate(){
		getSite().getPage().activate(this);
	}
}
