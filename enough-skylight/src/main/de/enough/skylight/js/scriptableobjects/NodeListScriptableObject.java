package de.enough.skylight.js.scriptableobjects;


import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.impl.NodeListImpl;

public class NodeListScriptableObject extends AbstractScriptableObject{

	private static final Object NODELIST_TAG = new Object();
	
	private static final int Id_length = FIRST_ID;
	private static final int Id_item = FIRST_ID+1;
	
	public static final int MAX_PROTOTYPE_ID = FIRST_ID+1;
	
	private NodeListImpl wrappedElement;

	public NodeListScriptableObject(NodeListImpl nodeList) {
		this.wrappedElement = nodeList;
	}
	
	public static void init(Scriptable scope, boolean sealed) {
		NodeListImpl dummyNodeList = new NodeListImpl();
		NodeListScriptableObject obj = new NodeListScriptableObject(dummyNodeList);
		obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
	}
	
	@Override
	public String getClassName() {
		return "NodeList";
	}

	@Override
	protected int doFindPrototypeId(String name) {
		if("item".equals(name)) {
			return Id_item;
		}
		return 0;
	}

	@Override
	protected boolean doInitPrototypeId(int id) {
		String name;
    	int arity;
    	switch (id) {
    		case Id_item:    arity=1; name="item";    break;
    		default: return false;
        }
        initPrototypeMethod(NODELIST_TAG, id, name, arity);
        return true;
	}
	
	@Override
	public Object doExecIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args, int id) {
		NodeListScriptableObject o = (NodeListScriptableObject)thisObj;
        NodeListImpl thisNodeList = o.wrappedElement;
			switch (id) {
        	case Id_item:{
        		int index = ((Integer)args[0]).intValue();
        		return thisNodeList.item(index);
        	}
        	default: throw new IllegalStateException("Could not execute function for id '"+id+"'");
        }
	}

	@Override
	protected Scriptable constructScriptableObject(Object object) {
		return new NodeListScriptableObject((NodeListImpl)object);
	}

	@Override
	protected int doFindInstanceIdInfo(String name) {
		if(name.equals("length")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_length);
		}
		return 0;
	}

	@Override
	protected String doGetInstanceIdName(int id) {
		switch(id) {
		case Id_length: return "length";
		default: return null;
		}
	}

	@Override
	protected Object doGetInstanceIdValue(int id) {
		switch(id) {
			case Id_length: return new Integer(this.wrappedElement.getLength());
			default: return null;
		}
	}

	@Override
	protected boolean doSetInstanceIdValue(int id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Object getTag() {
		return NODELIST_TAG;
	}
}
