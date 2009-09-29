package de.enough.skylight.dom.impl;

import org.mozilla.javascript.ScriptableObject;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;

public class NodeListImpl extends ScriptableObject implements NodeList {

	private ArrayList nodeList = new ArrayList();
	
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

}
