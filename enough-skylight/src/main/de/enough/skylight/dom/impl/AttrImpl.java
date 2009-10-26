package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.impl.rhino.AttrScriptableObject;

public class AttrImpl extends DomNodeImpl implements Attr {

	private DomNode parent;
	private String name;
	private String value;
	private AttrScriptableObject scriptableObject;

	protected void init(DomNodeImpl parent, String name, String value) {
		// Do not pass the parent to the super constructor as we do not want to queue this attributes as a proper child of the parent.
		super.init(null, null, name, null, ATTRIBUTE_NODE);
		this.parent = parent;
		this.name = name;
		this.value = value;
		
		if("id".equals(name)) {
			// This will update the cache so id getElementById lookups are faster.
			// TODO: Put this more close to the actual adding of the attributes so we do not need to
			// remember the step each time we create a new attribute.
			DocumentImpl ownerDocument = (DocumentImpl)parent.getOwnerDocument();
			ownerDocument.cacheNodeWithId(value,parent);
		}
	}
	
	public String getName() {
		return this.name;
	}

	public DomNode getOwnerElement() {
		return this.parent;
	}

	public boolean getSpecified() {
		return this.value != null;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) throws DomException {
		this.value = value;
	}

	@Override
	public void toXmlString(StringBuffer buffer) {
		buffer.append(this.name);
		if(this.value != null) {
			buffer.append("=\"");
			buffer.append(this.value);
			buffer.append("\"");
		}
	}
	
	@Override
	public Scriptable getScriptable() {
		if(this.scriptableObject == null) {
			this.scriptableObject = new AttrScriptableObject();
			this.scriptableObject.init(this);
		}
		return this.scriptableObject;
	}
	

}
