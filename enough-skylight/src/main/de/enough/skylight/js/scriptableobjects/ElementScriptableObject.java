package de.enough.skylight.js.scriptableobjects;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;

import de.enough.skylight.dom.impl.AttrImpl;
import de.enough.skylight.dom.impl.ElementImpl;

public class ElementScriptableObject extends AbstractDomNodeScriptableObject{

	// rickyn: This seems to be needed to distingish between constructors.
	private static final Object ELEMENT_TAG = new Object();
	
	// Element attributes
	private static final int Id_tagName			= FIRST_ID;
	
	// Element methods.
	private static final int Id_getAttribute	= FIRST_ID+1;
	private static final int Id_setAttribute	= FIRST_ID+2;
	private static final int Id_removeAttribute	= FIRST_ID+3;
	private static final int Id_getAttributeNode	= FIRST_ID+4;
	private static final int Id_setAttributeNode	= FIRST_ID+5;
	private static final int Id_removeAttributeNode	= FIRST_ID+6;
	private static final int Id_getElementsByTagName= FIRST_ID+7;
	private static final int Id_getAttributeNS		= FIRST_ID+8;
	private static final int Id_setAttributeNS		= FIRST_ID+9;
	private static final int Id_removeAttributeNS	= FIRST_ID+10;
	private static final int Id_getAttributeNodeNS	= FIRST_ID+11;
	private static final int Id_setAttributeNodeNS	= FIRST_ID+12;
	private static final int Id_getElementsByTagNameNS	= FIRST_ID+13;
	private static final int Id_hasAttribute			= FIRST_ID+14;
	private static final int Id_hasAttributeNS		 	= FIRST_ID+15;
	
	public static final int MAX_PROTOTYPE_ID        		= FIRST_ID+15;

	public ElementScriptableObject(ElementImpl element) {
		super(element);
	}
	
	@Override
	public String getClassName() {
		return "Element";
	}

	public static void init(Scriptable scope, boolean sealed) {
		ElementImpl dummyElement = new ElementImpl();
		ElementScriptableObject obj = new ElementScriptableObject(dummyElement);
		obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
	}
	
	@Override
	protected int doFindPrototypeId(String name) {
		if("getAttribute".equals(name)) {
			return Id_getAttribute;
		}
		if("setAttribute".equals(name)) {
			return Id_setAttribute;
		}
		if("removeAttribute".equals(name)) {
			return Id_removeAttribute;
		}
		if("getAttributeNode".equals(name)) {
			return Id_getAttributeNode;
		}
		if("setAttributeNode".equals(name)) {
			return Id_setAttributeNode;
		}
		if("removeAttributeNode".equals(name)) {
			return Id_removeAttributeNode;
		}
		if("getElementsByTagName".equals(name)) {
			return Id_getElementsByTagName;
		}
		if("getAttributeNS".equals(name)) {
			return Id_getAttributeNS;
		}
		if("setAttributeNS".equals(name)) {
			return Id_setAttributeNS;
		}
		if("removeAttributeNS".equals(name)) {
			return Id_removeAttributeNS;
		}
		if("getAttributeNodeNS".equals(name)) {
			return Id_getAttributeNodeNS;
		}
		if("setAttributeNodeNS".equals(name)) {
			return Id_setAttributeNodeNS;
		}
		if("getElementsByTagNameNS".equals(name)) {
			return Id_getElementsByTagNameNS;
		}
		if("hasAttribute".equals(name)) {
			return Id_hasAttribute;
		}
		if("hasAttributeNS".equals(name)) {
			return Id_hasAttributeNS;
		}
		return 0;
	}
	
	@Override
	protected Scriptable constructScriptableObject(Object object) {
		return new ElementScriptableObject((ElementImpl)object);
	}

	@Override
	public Object doExecIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args, int id) {
		ElementScriptableObject o = (ElementScriptableObject)thisObj;
        ElementImpl thisWrappedElement = (ElementImpl)o.wrappedDomNode;
		switch (id) {
        	
        	case Id_getAttribute:{
        		String attriuteName = convertToString(args,0);
        		return thisWrappedElement.getAttribute(attriuteName);
        	}
        	case Id_setAttribute:{
        		String arg1 = convertToString(args,0);
        		String arg2 = convertToString(args,1);
        		thisWrappedElement.setAttribute(arg1,arg2);
        		return UniqueTag.NULL_VALUE;
        	}
        	case Id_removeAttribute:{
        		String arg1 = convertToString(args,0);
        		thisWrappedElement.removeAttribute(arg1);
        		return UniqueTag.NULL_VALUE;
        	}
        	case Id_getAttributeNode:{
        		String arg1 = convertToString(args,0);
        		AttrImpl result = thisWrappedElement.getAttributeNode(arg1);
				return result == null?null:result.getScriptable();
        	}
        	case Id_setAttributeNode:{
        		throw Context.reportRuntimeError("The method 'setAttributeNode' is not implemented.");
        	}
        	case Id_removeAttributeNode: return null;
        	case Id_getElementsByTagName: return null;
        	case Id_getAttributeNS: return null;
        	case Id_setAttributeNS: return null;
        	case Id_removeAttributeNS: return null;
        	case Id_getAttributeNodeNS: return null;
        	case Id_setAttributeNodeNS: return null;
        	case Id_getElementsByTagNameNS: return null;
        	case Id_hasAttribute:{
        		String arg1 = convertToString(args,0);
        		return new Boolean(thisWrappedElement.hasAttribute(arg1));
        	}
        	case Id_hasAttributeNS: return null;
        }
        return null;
	}

	@Override
	protected boolean doInitPrototypeId(int id) {
		String name;
    	int arity;
    	switch (id) {
          // Element methods.
          case Id_getAttribute:   arity=1; name="getAttribute";   break;
          case Id_setAttribute:   arity=2; name="setAttribute";   break;
          case Id_removeAttribute:   arity=1; name="removeAttribute";   break;
          case Id_getAttributeNode:   arity=1; name="getAttributeNode";   break;
          case Id_setAttributeNode:   arity=1; name="setAttributeNode";   break;
          case Id_removeAttributeNode:   arity=1; name="removeAttributeNode";   break;
          case Id_getElementsByTagName:   arity=1; name="getElementsByTagName";   break;
          case Id_getAttributeNS:   arity=2; name="getAttributeNS";   break;
          case Id_setAttributeNS:   arity=3; name="setAttributeNS";   break;
          case Id_removeAttributeNS:   arity=2; name="removeAttriubteNS";   break;
          case Id_getAttributeNodeNS:   arity=2; name="getAttributeNS";   break;
          case Id_setAttributeNodeNS:   arity=1; name="setAttributeNS";   break;
          case Id_getElementsByTagNameNS:   arity=2; name="getElementsByTagNameNS";   break;
          case Id_hasAttribute:   arity=1; name="hasAttributes";   break;
          case Id_hasAttributeNS:   arity=2; name="hasAttributesNS";   break;
          default: return false;
        }
        initPrototypeMethod(ELEMENT_TAG, id, name, arity);
        return true;
	}

	@Override
	protected int doFindInstanceIdInfo(String name) {
		if(name.equals("tagName")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_tagName);
		}
		return 0;
	}

	@Override
	protected String doGetInstanceIdName(int id) {
		switch(id) {
			case Id_tagName: return "tagName";
			default: throw new RuntimeException("Could not find instace with id '"+id+"'");
		}
	}

	@Override
	protected Object doGetInstanceIdValue(int id) {
		ElementImpl wrappedElement = (ElementImpl)this.wrappedDomNode;
		switch(id) {
			case Id_tagName: return wrappedElement.getTagName();
		}
		return null;
	}

	@Override
	protected boolean doSetInstanceIdValue(int id, Object value) {
		// Not handled. Let the suberclass handle it.
		return false;
	}

	@Override
	protected Object getTag() {
		return ELEMENT_TAG;
	}

}
