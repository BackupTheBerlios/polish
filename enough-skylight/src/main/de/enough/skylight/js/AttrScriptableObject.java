package de.enough.skylight.js;


import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import de.enough.skylight.dom.impl.AttrImpl;

public class AttrScriptableObject extends ScriptableObject{

	private AttrImpl attrImpl;

	public void init(AttrImpl attrImpl) {
		this.attrImpl = attrImpl;
		defineProperty("name", this.attrImpl.getName(), READONLY|PERMANENT);
		defineProperty("ownerElement", this.attrImpl.getParentNode(), READONLY|PERMANENT);
		defineProperty("specified", null, READONLY|PERMANENT);
		defineProperty("value", null, PERMANENT);
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		if("specified".equals(name)) {
			return new Boolean(this.attrImpl.getSpecified());
		}
		if("value".equals(name)) {
			String value = this.attrImpl.getValue();
			return value == null?NOT_FOUND:value;
		}
		return super.get(name, start);
	}
	
	@Override
	public String getClassName() {
		return "Attr";
	}
}
