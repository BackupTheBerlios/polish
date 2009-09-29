package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;

public class AttrImpl extends DomNodeImpl implements Attr {

	private DomNode parent;
	private String name;
	private String value;

	protected void init(DomNodeImpl parent, String name, String value) {
		// Do not pass the parent to the super constructor as we do not want to queue this attributes as a proper child of the parent.
		super.init(null, null, name, null, ATTRIBUTE_NODE);
		this.parent = parent;
		this.name = name;
		this.value = value;
		
		defineProperty("name", this.name, READONLY|PERMANENT);
		defineProperty("ownerElement", this.parent, READONLY|PERMANENT);
		defineProperty("specified", null, READONLY|PERMANENT);
		defineProperty("value", null, PERMANENT);
		if("id".equals(name)) {
			// This will update the cache so id getElementById lookups are faster.
			// TODO: Put this more close to the actual adding of the attributes so we do not need to
			// remember the step each time we create a new attribute.
			DocumentImpl ownerDocument = (DocumentImpl)parent.getOwnerDocument();
			ownerDocument.cacheNodeWithId(value,parent);
		}
	}
	
	public Object get(String name, Scriptable start) {
		if("specified".equals(name)) {
			return new Boolean(getSpecified());
		}
		if("value".equals(name)) {
			return this.value == null?NOT_FOUND:this.value;
		}
		return super.get(name, start);
	}

	
	
	public String getClassName() {
		return "Attr";
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

	public void toXmlString(StringBuffer buffer) {
		buffer.append(this.name);
		if(this.value != null) {
			buffer.append("=\"");
			buffer.append(this.value);
			buffer.append("\"");
		}
	}
	
	
	

}
