/*
 * Created on Feb 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.polish.plugin.eclipse.css.model;

import java.util.ArrayList;
import java.util.List;


abstract public class ASTNode {
	
	protected ASTNode parent;
	protected List children;
	
	public ASTNode(){
		this.parent = null;
		this.children = new ArrayList();
	}
	
	public void accept(IModelVisitor modelVisitor){
		modelVisitor.visit(this);
	}
	
	/**
	 * @return Returns the parent.
	 */
	public ASTNode getParent() {
		return this.parent;
	}
	
	
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(ASTNode parent) {
		this.parent = parent;
	}
	
	
	/**
	 * @return Returns the children.
	 */
	public List getChildren() {
		return this.children;
	}
	/**
	 * @param children The children to set.
	 */
	public void setChildren(List children) {
		this.children = children;
	}
}
