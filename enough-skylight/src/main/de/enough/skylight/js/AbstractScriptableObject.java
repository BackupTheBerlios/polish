package de.enough.skylight.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.impl.DomNodeImpl;

public abstract class AbstractScriptableObject extends IdScriptableObject {

	private static final int Id_constructor     = 1;
	private static final int Id_toString        = 2;
	private static final int Id_toLocaleString	= 3;
	private static final int Id_toSource       	= 4;
	protected static final int FIRST_ID			= 5;
	
	protected abstract int doFindInstanceIdInfo(String name);
	protected abstract int doFindPrototypeId(String name);
	protected abstract String doGetInstanceIdName(int id);
	protected abstract Object doGetInstanceIdValue(int id);
	protected abstract boolean doSetInstanceIdValue(int id, Object value);
	protected abstract Object getTag();
	protected abstract Object doExecIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args, int id);
	
	/**
	 * The method returns a new object which is a ScriptableObject. This method must be implemented by subclasses.
	 * @param object
	 * @return
	 */
	protected abstract Scriptable constructScriptableObject(Object object);

	protected String convertToString(Object object) {
		if(object == null) {
			throw Context.reportRuntimeError("The parameter to this method must not be null.");
		}
		String string;
		if(object instanceof String) {
			string = (String)object;
		} else {
			string = object.toString();
		}
		return string;
	}

	@Override
	protected final int findPrototypeId(String name) {
		int result = doFindPrototypeId(name);
		if(result != 0) {
			return result;
		}
		if("constructor".equals(name)) {
			return Id_constructor;
		}
		return 0;
	}
	
	@Override
	protected final int findInstanceIdInfo(String name) {
		int result = doFindInstanceIdInfo(name);
		if(result != 0) {
			return result;
		}
		return 0;
	}
	
	@Override
	protected final String getInstanceIdName(int id) {
		String result = doGetInstanceIdName(id);
		if(result != null) {
			return result;
		}
		throw new RuntimeException("Could not find instace with id '"+id+"'");
	}

	@Override
	protected final Object getInstanceIdValue(int id) {
		Object result = doGetInstanceIdValue(id);
		if(result != null) {
			return result;
		}
		throw new RuntimeException("Could not find instance with id '"+id+"'");
	}

	
	@Override
	protected final void setInstanceIdValue(int id, Object value) {
		doSetInstanceIdValue(id,value);
	}

	@Override
	public final Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		if (!f.hasTag(getTag())) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.getMethodId();
        // Check for the constructor.
        if(id == Id_constructor) {
        	boolean inNewExpr = (thisObj == null);
    		if (!inNewExpr) {
    			// IdFunctionObject.construct will set up parent, proto
    			System.out.println("Calling a function as constructor.");
    			return f.construct(cx, scope, args);
    		}
    		return constructScriptableObject(args[0]);
        }
        Object result = doExecIdCall(f,cx,scope,thisObj,args,id);
        if(result != null) {
        	return result;
        }
        switch (id) {
        	case Id_toString:{
        		return "[Element "+hashCode()+"]";
        	}
        	case Id_toLocaleString:{
        		throw new RuntimeException("toLocaleString");
        	}
        	case Id_toSource:{
        		throw new RuntimeException("toLocaleString");
        	}
        }
        throw new IllegalStateException("Could not execute function for id '"+id+"'");
	}
	
	@Override
	protected final void initPrototypeId(int id) {
		boolean handled = doInitPrototypeId(id);
		if(handled) {
			return;
		}
		String name;
    	int arity;
    	switch (id) {
          // Node methods.
    		case Id_constructor:   arity=1; name="constructor";   break;
    		case Id_toSource:   arity=0; name="toSource";   break;
    		case Id_toString:   arity=0; name="toString";   break;
    		case Id_toLocaleString:   arity=0; name="toLocaleString";   break;
    		default: throw new RuntimeException("Could not initialize prototype method with id '"+id+"'");
    	}
    	initPrototypeMethod(getTag(), id, name, arity);
	}

	protected abstract boolean doInitPrototypeId(int id);
	
}