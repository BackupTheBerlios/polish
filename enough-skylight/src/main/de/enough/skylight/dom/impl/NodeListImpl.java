package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.Services;
import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.NodeList;

/**
 * @author rickyn
 *
 */
public class NodeListImpl implements NodeList {

	private ArrayList nodeList = new ArrayList();
	private Scriptable scriptableObject;

	public int getLength() {
		return this.nodeList.size();
	}

	public DomNodeImpl item(int index) {
		return (DomNodeImpl)this.nodeList.get(index);
	}

	public void add(DomNodeImpl childNode) {
		this.nodeList.add(childNode);
	}
	
	/**
	 * 
	 * @param newNode
	 * @param oldNode
	 * @return the old node which was replaced
	 */
	DomNodeImpl replace(DomNodeImpl newNode, DomNodeImpl oldNode) {
		int numberNodes = this.nodeList.size();
		for(int i = 0; i < numberNodes; i++) {
			if(this.nodeList.get(i).equals(oldNode)) {
				this.nodeList.set(i, newNode);
				return oldNode;
			}
		}
		throw new DOMException(DOMException.NOT_FOUND_ERR, "The node '"+oldNode+"' is not a child of the node '"+this+"'. Could not replace it.");
	}
	
	public Scriptable getScriptable() {
		if(this.scriptableObject == null) {
			this.scriptableObject = Services.getInstance().getJsEngine().newObject("NodeList", new Object[] {this});
		}
		return this.scriptableObject;
	}
	
	public boolean hasScriptable() {
		return this.scriptableObject != null;
	}

	public DomNodeImpl insertBefore(DomNodeImpl newChild, DomNodeImpl refChild) {
		int numberOfChildren = this.nodeList.size();
		for (int i = 0; i < numberOfChildren; i++) {
			if(refChild.equals(this.nodeList.get(i))) {
				this.nodeList.add(i, newChild);
				return newChild;
			}
		}
		throw new DOMException(DOMException.NOT_FOUND_ERR, "Could not find ");
	}

}
