package de.enough.skylight.dom.impl.rhino;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.impl.NodeListImpl;

public class NodeListScriptableObject extends ScriptableObject{

	private NodeListImpl nodeList;

	@Override
	public String getClassName() {
		return "NodeList";
	}
	
	public void init(NodeListImpl nodeList) {
		this.nodeList = nodeList;
		defineProperty("length", null, READONLY|PERMANENT);
		defineProperty("item", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(args.length == 0) {
					throw new DomException();
				}
				Object object = args[0];
				if(object instanceof Double) {
					return item(((Double)object).intValue());
				}
				throw new DomException("Unknown type:"+object.getClass());
			}
		}, PERMANENT);
	}
	
	protected Object item(int intValue) {
		// TODO Auto-generated method stub
		return null;
	}

	protected int length() {
		return this.nodeList.getLength();
	}

	@Override
	public Object get(String name, Scriptable start) {
		if("length".equals(name)) {
			return new Integer(length());
		} else {
			return super.get(name, start);
		}
	}

}
