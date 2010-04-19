package de.enough.skylight.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.Services;
import de.enough.skylight.dom.impl.ElementImpl;

public class ElementIdScriptableObject extends IdScriptableObject{

	// rickyn: This seems to be needed to distingish between constructors.
	private static final Object ELEMENT_TAG = new Object();
	
	// Node attributes.
	private static final int Id_nodeName		= 1;
	private static final int Id_nodeValue		= 2;
	private static final int Id_nodeType		= 3;
	private static final int Id_parentNode		= 4;
	private static final int Id_childNodes		= 5;
	private static final int Id_firstChild		= 6;
	private static final int Id_lastChild		= 7;
	private static final int Id_previousSibling	= 8;
	private static final int Id_nextSibling		= 9;
	private static final int Id_attributes		= 10;
	private static final int Id_ownerDocument	= 11;
	private static final int Id_namespaceURI	= 12;
	private static final int Id_prefix		 	= 13;
	private static final int Id_localName		= 14;
	
	// Element attributes
	private static final int Id_tagName			= 15;
	
	// Node constants.
	private static final int Id_ELEMENT_NODE				= 16;
	private static final int Id_ATTRIBUTE_NODE 				= 17;
	private static final int Id_TEXT_NODE 					= 18;
	private static final int Id_CDATA_SECTION_NODE 			= 19;
	private static final int Id_ENTITY_REFERENCE_NODE 		= 20;
	private static final int Id_ENTITY_NODE 				= 21;
	private static final int Id_PROCESSING_INSTRUCTION_NODE	= 22;
	private static final int Id_COMMENT_NODE 				= 23;
	private static final int Id_DOCUMENT_NODE 				= 24;
	private static final int Id_DOCUMENT_TYPE_NODE 			= 25;
	private static final int Id_DOCUMENT_FRAGMENT_NODE 		= 26;
	private static final int Id_NOTATION_NODE 				= 27;
	
	private static final int MAX_FIELD_ID   = 27;
	
	// Object methods.
	private static final int Id_constructor     = 28;
	private static final int Id_toString        = 29;
	private static final int Id_toLocaleString	= 30;
	private static final int Id_toSource       	= 31;
	// Node methods.
	private static final int Id_insertBefore	= 32;
	private static final int Id_replaceChild	= 33;
	private static final int Id_removeChild		= 34;
	private static final int Id_appendChild		= 35;
	private static final int Id_hasChildNodes	= 36;
	private static final int Id_cloneNode		= 37;
	private static final int Id_normalize		= 38;
	private static final int Id_isSupported		= 39;
	private static final int Id_hasAttributes	= 41;
	// Element methods.
	private static final int Id_getAttribute	= 42;
	private static final int Id_setAttribute	= 43;
	private static final int Id_removeAttribute	= 44;
	private static final int Id_getAttributeNode	= 45;
	private static final int Id_setAttributeNode	= 46;
	private static final int Id_removeAttributeNode	= 47;
	private static final int Id_getElementsByTagName= 48;
	private static final int Id_getAttributeNS		= 49;
	private static final int Id_setAttributeNS		= 50;
	private static final int Id_removeAttributeNS	= 51;
	private static final int Id_getAttributeNodeNS	= 52;
	private static final int Id_setAttributeNodeNS	= 53;
	private static final int Id_getElementsByTagNameNS	= 54;
	private static final int Id_hasAttribute			= 55;
	private static final int Id_hasAttributeNS		 	= 56;
	
	public static final int MAX_PROTOTYPE_ID        		= 56;

	private final ElementImpl element;
//	static void init(Scriptable scope, boolean sealed) {
//		ElementImpl dummyElement = new ElementImpl();
//		ElementIdScriptableObject obj = new ElementIdScriptableObject(dummyElement);
//        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
//    }
	
	public ElementIdScriptableObject(ElementImpl element) {
		this.element = element;
	}
	
	@Override
	public String getClassName() {
		return "Element";
	}

	public static void init(Scriptable scope, boolean sealed) {
		ElementImpl dummyElement = new ElementImpl();
		ElementIdScriptableObject obj = new ElementIdScriptableObject(dummyElement);
		obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
	}
	
	@Override
	protected int findPrototypeId(String name) {
		
		if("constructor".equals(name)) {
			return Id_constructor;
		}
		if("toString".equals(name)) {
			return Id_toString;
		}
		if("toLocaleString".equals(name)) {
			return Id_toLocaleString;
		}
		if("toSource".equals(name)) {
			return Id_toSource;
		}
		if("insertBefore".equals(name)) {
			return Id_insertBefore;
		}
		if("replaceChild".equals(name)) {
			return Id_replaceChild;
		}
		if("removeChild".equals(name)) {
			return Id_removeChild;
		}
		if("appendChild".equals(name)) {
			return Id_appendChild;
		}
		if("hasChildNodes".equals(name)) {
			return Id_hasChildNodes;
		}
		if("cloneNode".equals(name)) {
			return Id_cloneNode;
		}
		if("normalize".equals(name)) {
			return Id_normalize;
		}
		if("isSupported".equals(name)) {
			return Id_isSupported;
		}
		if("hasAttributes".equals(name)) {
			return Id_hasAttributes;
		}
		
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
		
		if("removeAttriubteNS".equals(name)) {
			return Id_removeAttributeNS;
		}
		
		if("getAttributeNS".equals(name)) {
			return Id_getAttributeNodeNS;
		}
		
		if("setAttributeNS".equals(name)) {
			return Id_setAttributeNodeNS;
		}
		
		if("getElementsByTagNameNS".equals(name)) {
			return Id_getElementsByTagNameNS;
		}
		
		if("hasAttributes".equals(name)) {
			return Id_hasAttribute;
		}
		
		if("hasAttributesNS".equals(name)) {
			return Id_hasAttributeNS;
		}
		
		/*
	private static final int Id_NOTATION_NODE 				= 55;
		 */
		if("ELEMENT_NODE".equals(name)) {
			return Id_ELEMENT_NODE;
		}
		if("ATTRIBUTE_NODE".equals(name)) {
			return Id_ATTRIBUTE_NODE;
		}
		if("TEXT_NODE".equals(name)) {
			return Id_TEXT_NODE;
		}
		if("CDATA_SECTION_NODE".equals(name)) {
			return Id_CDATA_SECTION_NODE;
		}
		if("ENTITY_REFERENCE_NODE".equals(name)) {
			return Id_ENTITY_REFERENCE_NODE;
		}
		if("ENTITY_NODE".equals(name)) {
			return Id_ENTITY_NODE;
		}
		if("PROCESSING_INSTRUCTION_NODE".equals(name)) {
			return Id_PROCESSING_INSTRUCTION_NODE;
		}
		if("COMMENT_NODE".equals(name)) {
			return Id_COMMENT_NODE;
		}
		if("DOCUMENT_NODE".equals(name)) {
			return Id_DOCUMENT_NODE;
		}
		if("DOCUMENT_TYPE_NODE".equals(name)) {
			return Id_DOCUMENT_TYPE_NODE;
		}
		if("DOCUMENT_FRAGMENT_NODE".equals(name)) {
			return Id_DOCUMENT_FRAGMENT_NODE;
		}
		if("NOTATION_NODE".equals(name)) {
			return Id_NOTATION_NODE;
		}
		
		return 0;
	}

	@Override
	public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		if (!f.hasTag(ELEMENT_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.getMethodId();
        switch (id) {
        	case Id_constructor:
        		boolean inNewExpr = (thisObj == null);
        		if (!inNewExpr) {
        			// IdFunctionObject.construct will set up parent, proto
        			System.out.println("Calling a function as constructor.");
        			return f.construct(cx, scope, args);
        		}
        		return new ElementIdScriptableObject((ElementImpl)args[0]);
        	case Id_toString:
        		return "[Element "+hashCode()+"]";

        	case Id_toLocaleString:
        		throw new RuntimeException("toLocaleString");

        	case Id_toSource:
        		throw new RuntimeException("toLocaleString");
        	case Id_insertBefore: return null;
        	case Id_replaceChild: return null;
        	case Id_removeChild: return null;
        	case Id_appendChild: return null;
        	case Id_hasChildNodes: return null;
        	case Id_cloneNode: return null;
        	case Id_normalize: return null;
        	case Id_isSupported: return null;
        	case Id_hasAttributes: return null;
        	case Id_getAttribute: return null;
        	case Id_setAttribute: return null;
        	case Id_removeAttribute: return null;
        	case Id_getAttributeNode: return null;
        	case Id_setAttributeNode: return null;
        	case Id_removeAttributeNode: return null;
        	case Id_getElementsByTagName: return null;
        	case Id_getAttributeNS: return null;
        	case Id_setAttributeNS: return null;
        	case Id_removeAttributeNS: return null;
        	case Id_getAttributeNodeNS: return null;
        	case Id_setAttributeNodeNS: return null;
        	case Id_getElementsByTagNameNS: return null;
        	case Id_hasAttribute: return null;
        	case Id_hasAttributeNS: return null;
        }
        throw new IllegalStateException("Could not execute function for id '"+id+"'");
	}

	@Override
	protected void initPrototypeId(int id) {
		String name;
        int attributes;
        Object value;
        if(id <= MAX_FIELD_ID) {
        	// The id is a field
        	switch (id) {
        		case Id_nodeName: name = "nodeName"; value = this.element.getNodeName(); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_ELEMENT_NODE: name = "ELEMENT_NODE"; value = new Integer(1); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_ATTRIBUTE_NODE: name = "ATTRIBUTE_NODE"; value = new Integer(2); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_TEXT_NODE: name = "TEXT_NODE"; value = new Integer(3); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_CDATA_SECTION_NODE: name = "CDATA_SECTION_NODE"; value = new Integer(4); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_ENTITY_REFERENCE_NODE: name = "ENTITY_REFERENCE_NODE"; value = new Integer(5); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_ENTITY_NODE: name = "ENTITY_NODE"; value = new Integer(6); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_PROCESSING_INSTRUCTION_NODE: name = "PROCESSING_INSTRUCTION_NODE"; value = new Integer(7); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_COMMENT_NODE: name = "COMMENT_NODE"; value = new Integer(8); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_DOCUMENT_NODE: name = "DOCUMENT_NODE"; value = new Integer(9); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_DOCUMENT_TYPE_NODE: name = "DOCUMENT_TYPE_NODE"; value = new Integer(10); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_DOCUMENT_FRAGMENT_NODE: name = "DOCUMENT_FRAGMENT_NODE"; value = new Integer(11); attributes = DONTENUM | READONLY | PERMANENT; break;
        		case Id_NOTATION_NODE: name = "NOTATION_NODE"; value = new Integer(12); attributes = DONTENUM | READONLY | PERMANENT; break;
        		default: throw new IllegalArgumentException("Prototype id '"+String.valueOf(id)+"' does not exists.");
        	}
        	initPrototypeValue(id, name, value, attributes);
        } else {
        	// The id is a method
        	int arity;
        	switch (id) {
            // Object methods
              case Id_constructor:    arity=1; name="constructor";    break;
              case Id_toString:       arity=0; name="toString";       break;
              case Id_toLocaleString: arity=1; name="toLocaleString"; break;
              case Id_toSource:       arity=0; name="toSource";       break;
              // Node methods.
              case Id_insertBefore:   arity=2; name="insertBefore";   break;
              case Id_replaceChild:   arity=2; name="replaceChild";   break;
              case Id_removeChild:   arity=1; name="removeChild";   break;
              case Id_appendChild:   arity=1; name="appendChild";   break;
              case Id_hasChildNodes:   arity=0; name="hasChildNodes";   break;
              case Id_cloneNode:   arity=1; name="cloneNode";   break;
              case Id_normalize:   arity=0; name="normalize";   break;
              case Id_isSupported:   arity=2; name="isSupported";   break;
              case Id_hasAttributes:   arity=0; name="hasAttributes";   break;
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
              
              default: throw new IllegalArgumentException("Prototype id '"+String.valueOf(id)+"' does not exists.");
            }
            initPrototypeMethod(ELEMENT_TAG, id, name, arity);
        }
        
        
	}

	
	
	@Override
	protected int findInstanceIdInfo(String name) {
		if(name.equals("nodeName")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_nodeName);
		}
		return 0;
	}

	@Override
	protected String getInstanceIdName(int id) {
		switch(id) {
			case Id_nodeName: return "nodeName";
			default: throw new RuntimeException("Could not find instace with id '"+id+"'");
		}
	}

	@Override
	protected Object getInstanceIdValue(int id) {
		switch(id) {
			case Id_nodeName: return this.element.getNodeName();
			default: throw new RuntimeException("Could not find instace with id '"+id+"'");
		}
	}

	@Override
	protected void setInstanceIdValue(int id, Object value) {
		// TODO Auto-generated method stub
		super.setInstanceIdValue(id, value);
	}
	
	
	
}