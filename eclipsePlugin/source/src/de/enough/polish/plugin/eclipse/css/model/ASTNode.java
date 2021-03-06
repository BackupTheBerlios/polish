/*
 * Created on Feb 23, 2005 at 2:44:50 PM.
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
package de.enough.polish.plugin.eclipse.css.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>The baseclass for all nodes within an AST. Caution: You are resoponsible to link a child proberly
 * to its parent, so call setParent on the child and addChild on the parent.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Feb 23, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
abstract public class ASTNode {
	
	protected ASTNode parent;
	protected List children;
	protected List problems;
	protected int offset;
	
	public ASTNode(){
		this.parent = null;
		this.children = new ArrayList();
		this.problems = new ArrayList();
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
	
	public void addChild(ASTNode child){
		this.children.add(child);
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
	
	public void addProblem(Problem problem){
		this.problems.add(problem);
	}
	
	public void resetProblems(){
		this.problems.clear();
	}
	
	public List getProblems(){
		return this.problems;
	}
	
	// For debugging only.
	protected String printProblems(){
		StringBuffer result = new StringBuffer();

		Iterator iterator = this.problems.iterator();
		Problem problem;
		boolean cutLastChar = false;
		while(iterator.hasNext()){
			problem = (Problem) iterator.next();
			result.append("(");
			result.append(problem);
			result.append("),");
			cutLastChar = true;
		}
		if(cutLastChar){
			result.deleteCharAt(result.length()-1);
		}
		return result.toString();
	}

	/**
	 * @return boolean
	 */
	public boolean hasProblems() {
		return  ! this.problems.isEmpty();
	}
	
	
	public int getOffset() {
		return this.offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
}
