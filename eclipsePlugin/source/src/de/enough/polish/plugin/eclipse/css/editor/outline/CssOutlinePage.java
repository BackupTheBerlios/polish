/*
 * Created on Feb 24, 2005 at 10:12:30 AM.
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
package de.enough.polish.plugin.eclipse.css.editor.outline;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.enough.polish.plugin.eclipse.css.editor.CssEditor;
import de.enough.polish.plugin.eclipse.css.model.CssModel;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 24, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CssOutlinePage extends ContentOutlinePage {

	private CssModel cssModel;
	private ISourceViewer sourceViewer;
	private CssEditor editor;
	
	//FIXME: We need our own model here
	public CssOutlinePage(CssModel cssModel, ISourceViewer sourceViewer, CssEditor editor){
		super();
		this.cssModel = cssModel;
		this.sourceViewer = sourceViewer;
		this.editor = editor;
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		System.out.println("DEBUG:CssOutlinePage.createControl():enter.");
		if(parent.isDisposed()){
			System.out.println("DEBUG:CssOutlinePage.createControl():parent.isDisposed():true");
		}
		
		CssContentProvider cssContentProvider = new CssContentProvider(this.cssModel,this.sourceViewer,this.editor);
		
		TreeViewer viewer= getTreeViewer();

		viewer.setContentProvider(cssContentProvider);
		viewer.setLabelProvider(new CssLabelProvider());
		viewer.addSelectionChangedListener(this);
		//ASTNode rootNode = cssContentProvider.initialInput();
		//ASTNode rootNode = CssEditorPlugin.getDefault().getEditor().getCssModel().getRoot();
		//viewer.setInput(rootNode);
		viewer.setInput(this.cssModel);
	}
	
	/*
	public void setInput(IEditorInput editorInput){
		System.out.println("setInput");
	}
	*/
}
