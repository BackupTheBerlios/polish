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

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;

import antlr.collections.AST;

import de.enough.polish.plugin.eclipse.css.CssEditorPlugin;
import de.enough.polish.plugin.eclipse.css.editor.CssEditor;
import de.enough.polish.plugin.eclipse.css.model.ASTNode;
import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.model.IModelListener;
import de.enough.polish.plugin.eclipse.css.parser.CssLexerTokenTypes;
import de.enough.polish.plugin.eclipse.css.parser.OffsetAST;

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
public class CssContentProvider implements ITreeContentProvider,IModelListener {

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
			OffsetAST firstSelectedNode = (OffsetAST)selection.getFirstElement();
			if(firstSelectedNode == null){
				return;
			}
			this.editor.setCaretToOffset(firstSelectedNode.getOffset());
			this.editor.doActivate();
		}
	}
	
	public CssContentProvider(CssModel cssModel){
		this.cssModel = cssModel;
		this.cssModel.addModelListener(this); //TODO We do not need to listen to the model here.
		this.selectionChangedListener = new SelectionChangedListener();
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		this.cssModel.removeModelListener(this);
		// FIXME: Danger. Please implement disposable on the model for proper finalization.
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//System.out.println("DEBUG:CssContentProvider.inputChanged(): enter");
		if(viewer != null){
			if(this.treeViewer != null){
				this.treeViewer.removeSelectionChangedListener(this.selectionChangedListener);
			}
			this.treeViewer = viewer;
			//System.out.println("DEBUG:CssContentProvider.inputChanged():oldInput:"+oldInput);
			//System.out.println("DEBUG:CssContentProvider.inputChanged():newInput:"+ ((newInput != null) ? newInput.getClass().toString() : "null"));
			if(newInput instanceof CssModel){
				this.cssModel = (CssModel)newInput;
			}
			this.treeViewer.addSelectionChangedListener(this.selectionChangedListener);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object object) {
	    if( ! (object instanceof AST)) {
	        return new Object[] {};
	    }
	    
	    AST parent = (AST)object;
	    switch(parent.getType()) {
	    		case(CssLexerTokenTypes.STYLE_SECTION):
	    		    return getStyleSectionChildren(parent);
	    		case(CssLexerTokenTypes.SECTION):
	    		    return getSectionChildren(parent);
	    		case(CssLexerTokenTypes.STYLE_SHEET):
	    		    return getStyleSheetChildren(parent);
	    		default:
	    		    return new Object[] {};
	    }
	}
	    		
	private Object[] getStyleSectionChildren(AST section) {
	    ArrayList result = new ArrayList();
	    int attributeValuePairOffset = 3;
	    AST sibling = section.getFirstChild(); //StyleSectionName
	    if(sibling != null) {
	        sibling = sibling.getNextSibling(); // Propably extends.
	        if(sibling != null) {
	            if(sibling.getType() == CssLexerTokenTypes.NAME) {
	                attributeValuePairOffset = 2;
	                sibling = sibling.getNextSibling();
	            }
	            else {
	                attributeValuePairOffset = 1;
	            }
	    	    		for(int i = attributeValuePairOffset; i < section.getNumberOfChildren(); i++) {
	    	        
	    	    		    result.add(sibling);
	    	    		    sibling = sibling.getNextSibling();
	    	    		}

	        }
	    }
	    
	    return result.toArray();
	}
	   
	private Object[] getStyleSheetChildren(AST root) {
        if(root == null) {
            return new Object[] {};
        }
        ArrayList result = new ArrayList();
        AST child = root.getFirstChild();
        while (child != null) {
            result.add(child);
            child = child.getNextSibling();
        }
        return result.toArray();
    }
	
	private Object[] getSectionChildren(AST section) {
	    ArrayList result = new ArrayList();
	    AST sibling = section.getFirstChild();
	    if(sibling == null) {
	        return result.toArray();
	    }
	    sibling = sibling.getNextSibling(); // skip the name child.
	    while(sibling != null) {
	        result.add(sibling);
	        sibling = sibling.getNextSibling();
	    }
	    /*
	    for(int i = 1; i < section.getNumberOfChildren(); i++) {
	        result.add(sibling);
	        sibling = sibling.getNextSibling();
	    }
	    */
	    return result.toArray();
	}
	    
	    

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		ASTNode result = null;
		System.out.println("DEBUG:CssContentProvider.getParent():enter. But not implemented...");
		if( ! (element instanceof AST)){
			return null;
		}
		return result;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if( ! (element instanceof AST)){
			return false;
		}
		switch(((AST)element).getType()) {
			case(CssLexerTokenTypes.STYLE_SHEET):
			case(CssLexerTokenTypes.STYLE_SECTION):
			case(CssLexerTokenTypes.SECTION):
			    return true;
			default:
			    return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if( ! (inputElement instanceof CssModel)){
			return new Object[]{};
		}
		return getStyleSheetChildren(((CssModel)inputElement).getAstRoot());
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.plugin.eclipse.css.model.IModelListener#modelChanged()
	 */
	public void modelChanged() {
		//System.out.println("DEBUG:CssContentProvider.modelChanged().enter.");
		this.treeViewer.refresh();
	}

}
