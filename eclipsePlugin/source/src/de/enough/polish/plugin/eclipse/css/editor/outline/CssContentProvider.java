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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;

import de.enough.polish.plugin.eclipse.css.model.AttributeValuePair;
import de.enough.polish.plugin.eclipse.css.model.Comment;
import de.enough.polish.plugin.eclipse.css.model.CssModel;
import de.enough.polish.plugin.eclipse.css.model.Section;
import de.enough.polish.plugin.eclipse.css.model.StyleSection;
import de.enough.polish.plugin.eclipse.css.model.StyleSheet;

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
public class CssContentProvider implements ITreeContentProvider,ISelectionChangedListener {

	//ASTNode rootParent;
	Viewer currentViewer;
	CssModel cssModel;
	
	public CssContentProvider(CssModel cssModel){
		//this.rootParent = null;
		this.cssModel = cssModel;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// FIXME: Danger. Please implement disposable on the model for proper finalization.
		//this.rootParent = null;

	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		System.out.println("DEBUG:CssContentProvider.inputChanged(): enter");
		if(viewer != null){
			this.currentViewer = viewer;
			System.out.println("DEBUG:CssContentProvider.inputChanged():oldInput:"+oldInput);
			System.out.println("DEBUG:CssContentProvider.inputChanged():newInput:"+ ((newInput != null) ? newInput.getClass().toString() : "null"));
			//this.rootParent = (ASTNode)newInput;
			if(newInput instanceof CssModel){
				this.cssModel = (CssModel)newInput;
			}
			this.currentViewer.addSelectionChangedListener(this);
		}
	}
	
	public StyleSheet initialInput(){
		StyleSheet root = new StyleSheet();
		
		Comment comment1 = new Comment();
		comment1.setText("comment 1");
		
		Comment comment2 = new Comment();
		comment2.setText("comment 2");
		
		StyleSection styleSection1 = new StyleSection();
		styleSection1.setSectionName("menu");
		
		StyleSection styleSection2 = new StyleSection();
		styleSection2.setSectionName("menuItem");
		
		StyleSection styleSection3 = new StyleSection();
		styleSection3.setSectionName("menuFocuced");
		styleSection3.setParentStyle("menuItem");
		
		Section colors1 = new Section();
		colors1.setSectionName("colors");
		
		Section font1 = new Section();
		font1.setSectionName("font");
		
		AttributeValuePair attributeValuePair1 = new AttributeValuePair();
		attributeValuePair1.setAttribute("attribute1");
		attributeValuePair1.setValue("value1");
		
		AttributeValuePair attributeValuePair2 = new AttributeValuePair();
		attributeValuePair2.setAttribute("attribute2");
		attributeValuePair2.setValue("value2");
		
		AttributeValuePair attributeValuePair3 = new AttributeValuePair();
		attributeValuePair3.setAttribute("attribute3");
		attributeValuePair3.setValue("value3");
		
		AttributeValuePair attributeValuePair4 = new AttributeValuePair();
		attributeValuePair4.setAttribute("attribute4");
		attributeValuePair4.setValue("value4");
		
		root.addComment(comment1);
		root.addStyleSection(styleSection1);
		root.addStyleSection(styleSection2);	
		root.addSection(colors1);
		root.addStyleSection(styleSection3);
		
		styleSection1.addAttributeValuePair(attributeValuePair1);
		
		styleSection2.addAttributeValuePair(attributeValuePair2);
		styleSection2.addSection(font1);
		
		styleSection3.addAttributeValuePair(attributeValuePair3);
		styleSection3.addAttributeValuePair(attributeValuePair4);
		
		
		return root;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		return null;
		/*
		System.out.println("DEBUG:CssContentProvider.getChildren():entered");
		System.out.println("DEBUG:CssContentProvider.getChildren():parentElement:"+parentElement);
		Object[] result = null;
		if(parentElement instanceof ASTNode){
			result = ((ASTNode)parentElement).getChildren().toArray();
		}
		return result;
		*/
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
		/*
		ASTNode result = null;
		if(element instanceof ASTNode){
			result = ((ASTNode)element).getParent();
		}
		return result;
		*/
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return false;
		/*
		if(element instanceof ASTNode){
			ASTNode astNode = (ASTNode)element;
			return ! astNode.getChildren().isEmpty();
		}
		return false;
		*/
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return new String[]{"ELEMENTS"};
		/*
		System.out.println("DEBUG:CssContentProvider.getelements():entered");
		System.out.println("DEBUG:CssContentProvider.getChildren():inputElement"+inputElement);
		
		if(inputElement instanceof StyleSheet){
			return ((StyleSheet)inputElement).getChildren().toArray();
		}
		return null;
		*/
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		System.out.println("DEBUG:CssContentProvider.selectionChanged():enter.");
		System.out.println("DEBUG:CssContentProvider.selectionChanged():event:"+event);
		
	}

}
