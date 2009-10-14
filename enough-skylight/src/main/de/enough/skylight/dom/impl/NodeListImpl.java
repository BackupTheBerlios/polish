package de.enough.skylight.dom.impl;

import org.mozilla.javascript.ScriptableObject;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;

/**
 * This implementation of a NodeList is also able to hold the state of a current index into the list.
 * This is helpful to iterate through the list.
 * @author rickyn
 *
 */
public class NodeListImpl extends ScriptableObject implements NodeList {

	private ArrayList nodeList = new ArrayList();
	private int index;
	
	public String getClassName() {
		return "NodeList";
	}

	public int getLength() {
		return this.nodeList.size();
	}

	public DomNode item(int index) {
		return (DomNode) this.nodeList.get(index);
	}

	public void add(DomNodeImpl childNode) {
		this.nodeList.add(childNode);
	}
	
	void replace(DomNodeImpl newNode, DomNodeImpl oldNode) {
		int numberNodes = this.nodeList.size();
		for(int i = 0; i < numberNodes; i++) {
			if(this.nodeList.get(i).equals(oldNode)) {
				this.nodeList.set(i, newNode);
				return;
			}
		}
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void resetIndex() {
		this.index = 0;
	}

	public void increaseIndex() {
		this.index++;
	}
	
}
