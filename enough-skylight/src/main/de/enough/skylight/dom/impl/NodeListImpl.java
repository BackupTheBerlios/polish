package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.js.NodeListScriptableObject;

/**
 * @author rickyn
 *
 */
public class NodeListImpl implements NodeList {

	private ArrayList nodeList = new ArrayList();
	private NodeListScriptableObject scriptable;
	
	public int getLength() {
		return this.nodeList.size();
	}

	public DomNode item(int index) {
		return (DomNode)this.nodeList.get(index);
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
	
	public Scriptable getScriptable() {
		if(this.scriptable == null) {
			this.scriptable = new NodeListScriptableObject();
			this.scriptable.init(this);
		}
		return this.scriptable;
	}
	
	public boolean hasScriptable() {
		return this.scriptable != null;
	}

}
