package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Text;
import de.enough.skylight.js.TextScriptableObject;

public class TextImpl extends CharacterDataImpl implements Text{

	private TextScriptableObject scriptableObject;
	
	public void init(DocumentImpl document, DomNodeImpl parent, String data) {
		super.init(document, parent, DomNode.TEXT_NODE,data);
	}
	
	@Override
	public Scriptable getScriptable() {
		if(this.scriptableObject == null) {
			this.scriptableObject = new TextScriptableObject();
			this.scriptableObject.init(this);
		}
		return this.scriptableObject;
	}

	@Override
	public boolean hasScriptable() {
		return this.scriptableObject != null;
	}

	public String getWholeText() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isElementContentWhitespace() {
		// TODO Auto-generated method stub
		return false;
	}

	public Text replaceWholeText(String content) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}

	public Text splitText(int offset) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeName() {
		return "#text";
	}

	public String getNodeValue() throws DomException {
		return this.buffer.toString();
	}

	public void setNodeValue(String nodeValue) throws DomException {
		this.buffer = new StringBuffer(nodeValue);
	}

}
