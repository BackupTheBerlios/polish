/*
 * Created on Feb 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.enough.polish.plugin.eclipse.css.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


abstract public class ASTNode {
	
	protected ASTNode parent;
	protected List children;
	protected List problems; // TODO: This will be a list of IMarkers. //TODO: rename to syntaxproblems.
	//protected CssToken startToken; // Mixing the layers? But a ASTNode have to know to which Token it belongs.
	//TODO: We need the source range, too.
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
		while(iterator.hasNext()){
			problem = (Problem) iterator.next();
			result.append("(");
			result.append(problem);
			result.append("),");
		}
		result.deleteCharAt(result.length()-1);
		return result.toString();
	}

	/**
	 * @return boolean
	 */
	public boolean hasProblems() {
		return  ! this.problems.isEmpty();
	}
	
	/*
	// TODO: Do we really want to bother the parser with regions in the document?
	// We could also use offsets in the tokenList...
	public void getProblemRegion(){
		
	}
	*/
	
}
