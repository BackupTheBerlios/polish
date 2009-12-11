package de.enough.skylight.js;


import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.impl.NodeListImpl;

public class NodeListScriptableObject extends ScriptableObject{

	protected NodeListImpl nodeListImpl;

	@Override
	public String getClassName() {
		return "NodeList";
	}
	
	public void init(NodeListImpl nodeList) {
		this.nodeListImpl = nodeList;
		defineProperty("length", null, READONLY|PERMANENT);
		defineProperty("item", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(args.length == 0) {
					throw new DomException();
				}
				Object object = args[0];
				if(object instanceof Double) {
					int intValue = ((Double)object).intValue();
					return NodeListScriptableObject.this.nodeListImpl.item(intValue);
				}
				throw new DomException("Unknown type:"+object.getClass());
			}
		}, PERMANENT);
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		if("length".equals(name)) {
			int length = this.nodeListImpl.getLength();
			return new Double(length);
		} else {
			return super.get(name, start);
		}
	}

}
