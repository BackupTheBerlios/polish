/*
 * Created on Feb 24, 2005 at 11:01:17 AM.
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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;

import de.enough.polish.plugin.eclipse.css.CssEditorPlugin;
import de.enough.polish.plugin.eclipse.css.editor.CssEditor;
import de.enough.polish.plugin.eclipse.css.model.ASTNode;
import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.model.IModelListener;

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
public class CopyOfCssContentProvider implements ITreeContentProvider,IModelListener {

	private Viewer treeViewer;
	private CssModel cssModel;
	private SelectionChangedListener selectionChangedListener;
	
	
	private class SelectionChangedListener implements ISelectionChangedListener{
		
		private CssEditor editor;
		
		private SelectionChangedListener(){
			this.editor = CssEditorPlugin.getDefault().getEditor();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			if(selection == null){
				return;
			}
			ASTNode firstSelectedNode = (ASTNode)selection.getFirstElement();
			if(firstSelectedNode == null){
				return;
			}
			this.editor.setCaretToOffset(firstSelectedNode.getOffset());
			this.editor.doActivate();
		}
	}
	
	public CopyOfCssContentProvider(CssModel cssModel){
		this.cssModel = cssModel;
		this.cssModel.addModelListener(this);
		this.selectionChangedListener = new SelectionChangedListener();
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		this.cssModel.removeModelListener(this);
		// FIXME: Danger. Please implement disposable on the model for proper finalization.
		//this.rootParent = null;

	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		System.out.println("DEBUG:CssContentProvider.inputChanged(): enter");
		if(viewer != null){
			if(this.treeViewer != null){
				this.treeViewer.removeSelectionChangedListener(this.selectionChangedListener);
			}
			this.treeViewer = viewer;
			System.out.println("DEBUG:CssContentProvider.inputChanged():oldInput:"+oldInput);
			System.out.println("DEBUG:CssContentProvider.inputChanged():newInput:"+ ((newInput != null) ? newInput.getClass().toString() : "null"));
			if(newInput instanceof CssModel){
				this.cssModel = (CssModel)newInput;
			}
			this.treeViewer.addSelectionChangedListener(this.selectionChangedListener);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		Object[] result = new Object[]{};
		if(parentElement instanceof ASTNode){
			result = ((ASTNode)parentElement).getChildren().toArray();
		}
		return result;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		ASTNode result = null;
		if(element instanceof ASTNode){
			result = ((ASTNode)element).getParent();
		}
		return result;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if(element instanceof ASTNode){
			ASTNode astNode = (ASTNode)element;
			return ! astNode.getChildren().isEmpty();
		}
		return false;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof CssModel){
			return ((CssModel)inputElement).getRoot().getChildren().toArray();
		}
		return new Object[]{};
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.plugin.eclipse.css.model.IModelListener#modelChanged()
	 */
	public void modelChanged() {
		System.out.println("DEBUG:CssContentProvider.modelChanged().enter.");
		this.treeViewer.refresh();
	}

}
