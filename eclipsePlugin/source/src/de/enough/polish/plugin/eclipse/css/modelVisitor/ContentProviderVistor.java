/*
 * Created on Feb 28, 2005 at 11:04:08 AM.
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
package de.enough.polish.plugin.eclipse.css.modelVisitor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.enough.polish.plugin.eclipse.css.model.ASTNode;
import de.enough.polish.plugin.eclipse.css.model.AttributeValuePair;
import de.enough.polish.plugin.eclipse.css.model.Comment;
import de.enough.polish.plugin.eclipse.css.model.IModelVisitor;
import de.enough.polish.plugin.eclipse.css.model.Section;
import de.enough.polish.plugin.eclipse.css.model.StyleSection;
import de.enough.polish.plugin.eclipse.css.model.StyleSheet;

/**
 * <p>This class is a contentProvider for a TreeViewer. It is also a vistor for the model.</p>
 * <p>Using the visitor pattern to call getChildren and the like may seem to be abstraction inversion (Antipattern)
 * but other tasks may be more difficult and a uniform design is key for understanding things.</p>
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 28, 2005 - ricky creation
 * </pre>
 * @deprecated Using a Vistor is to much abstraction inversion for such a task...
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ContentProviderVistor implements IModelVisitor,ITreeContentProvider {

	ASTNode[] children;
	/*
	public ContentProviderVisitor(){
		this.children = new ASTNode[]{};
	}
	*/
	/* (non-Javadoc)
	 * @see de.enough.polish.plugin.eclipse.css.model.IModelVisitor#visit(de.enough.polish.plugin.eclipse.css.model.ASTNode)
	 */
	public void visit(ASTNode astNode) {
		System.out.println("ERROR:ContentProviderVisitor:visit(ASTNode):This method should not be called. There should not be ASTNode Objects in the model.");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.plugin.eclipse.css.model.IModelVisitor#visit(de.enough.polish.plugin.eclipse.css.model.StyleSheet)
	 */
	public void visit(StyleSheet styleSheet) {
		examineChildren(styleSheet);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.plugin.eclipse.css.model.IModelVisitor#visit(de.enough.polish.plugin.eclipse.css.model.Comment)
	 */
	public void visit(Comment comment) {
		examineChildren(comment);
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.plugin.eclipse.css.model.IModelVisitor#visit(de.enough.polish.plugin.eclipse.css.model.Section)
	 */
	public void visit(Section section) {
		examineChildren(section);
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.plugin.eclipse.css.model.IModelVisitor#visit(de.enough.polish.plugin.eclipse.css.model.StyleSection)
	 */
	public void visit(StyleSection styleSection) {
		examineChildren(styleSection);
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.plugin.eclipse.css.model.IModelVisitor#visit(de.enough.polish.plugin.eclipse.css.model.AttributeValuePair)
	 */
	public void visit(AttributeValuePair attributeValuePair) {
		examineChildren(attributeValuePair);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if( ! (parentElement instanceof ASTNode)){
			System.out.println("ERROR:ContentProviderVisitor.getChildren(): Parameter parentElement is not a ASTNode, but "+((parentElement == null)?null:parentElement));
		}
		visit((ASTNode)parentElement);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		// TODO ricky implement hasChildren
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		// TODO ricky implement getElements
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// TODO ricky implement dispose
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO ricky implement inputChanged
		
	}
	
	private void examineChildren(ASTNode astNode){
		this.children = (ASTNode[])astNode.getChildren().toArray();
	}

}
