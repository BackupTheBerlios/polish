package de.enough.skylight.js.scriptableobjects;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;

import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.NamedNodeMapImpl;
import de.enough.skylight.dom.impl.NodeListImpl;

public abstract class AbstractDomNodeScriptableObject extends IdScriptableObject {

	protected DomNodeImpl wrappedDomNode;

	// Node constants.
	private static final int Id_ELEMENT_NODE				= 1;
	private static final int Id_ATTRIBUTE_NODE 				= 2;
	private static final int Id_TEXT_NODE 					= 3;
	private static final int Id_CDATA_SECTION_NODE 			= 4;
	private static final int Id_ENTITY_REFERENCE_NODE 		= 5;
	private static final int Id_ENTITY_NODE 				= 6;
	private static final int Id_PROCESSING_INSTRUCTION_NODE	= 7;
	private static final int Id_COMMENT_NODE 				= 8;
	private static final int Id_DOCUMENT_NODE 				= 9;
	private static final int Id_DOCUMENT_TYPE_NODE 			= 10;
	private static final int Id_DOCUMENT_FRAGMENT_NODE 		= 11;
	private static final int Id_NOTATION_NODE 				= 12;
	
	// Node fields.
	private static final int Id_nodeName = 13;
	private static final int Id_nodeValue = 14;
	private static final int Id_nodeType = 15;
	private static final int Id_parentNode = 16;
	private static final int Id_childNodes = 17;
	private static final int Id_firstChild = 18;
	private static final int Id_lastChild = 19;
	private static final int Id_previousSibling = 20;
	private static final int Id_nextSibling = 21;
	private static final int Id_attributes = 22;
	private static final int Id_ownerDocument = 23;
	private static final int Id_namespaceURI = 24;
	private static final int Id_prefix = 25;
	private static final int Id_localName = 26;

	private static final int MAX_FIELD_ID = 26;

	// Object methods.
	private static final int Id_constructor = 27;
	private static final int Id_toString = 28;
	private static final int Id_toLocaleString = 29;
	private static final int Id_toSource = 30;
	
	// Node methods.
	private static final int Id_insertBefore	= 31;
	private static final int Id_replaceChild	= 32;
	private static final int Id_removeChild		= 33;
	private static final int Id_appendChild		= 34;
	private static final int Id_hasChildNodes	= 35;
	private static final int Id_cloneNode		= 36;
	private static final int Id_normalize		= 37;
	private static final int Id_isSupported		= 38;
	private static final int Id_hasAttributes	= 39;
	
	protected static final int FIRST_ID = 40;
	
	public AbstractDomNodeScriptableObject(DomNodeImpl domNodeImpl){
		this.wrappedDomNode = domNodeImpl;
	}
	
	protected abstract int doFindInstanceIdInfo(String name);
	protected abstract int doFindPrototypeId(String name);
	
	/**
	 * Map an id to a name.
	 * @param id
	 * @return null if the id is not known and the superclass should handle the id. Return the name of the id to mark the id as handled.
	 */
	protected abstract String doGetInstanceIdName(int id);
	protected abstract Object doGetInstanceIdValue(int id);
	protected abstract boolean doSetInstanceIdValue(int id, Object value);
	
	/**
	 * Call {@link #initPrototypeMethod(Object, int, String, int)} and {@link #initPrototypeValue(int, String, Object, int)} in this method.
	 * @param id
	 * @return 'true' if this method handled the id by initalizing something. Return 'false' if the superclass should take its shot.
	 */
	protected abstract boolean doInitPrototypeId(int id);
	
	/**
	 * Returns an object which should be the same for every instance of this class. Initialize a simple static object for this task. 
	 * @return
	 */
	protected abstract Object getTag();
	
	/**
	 * 
	 * @param f the function which was called.
	 * @param cx the rhino context
	 * @param scope the rhino scope
	 * @param thisObj null if this is called as a constructor. Otherwise the object on which the method was called.
	 * @param args the arguments to this method call.
	 * @param id the id of the call, extracted already from the function f.
	 * @return
	 */
	protected abstract Object doExecIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args, int id);
	
	/**
	 * The method returns a new object which is a Scriptable. This method must be implemented by subclasses.
	 * @param toBeWrappedDomainObject a domain object which is wrapped by the newly created Scriptable
	 * @return a Scriptable object which wraps the toBeWrappedDomainObject.
	 */
	protected abstract Scriptable constructScriptableObject(Object toBeWrappedDomainObject);

	protected String convertToString(Object[] array, int index) {
		if(array == null || array.length == 0) {
			throw Context.reportRuntimeError("The parameter '"+index+"' of this method must not be null.");
		}
		String string;
		if(array[index] instanceof String) {
			string = (String)array[index];
		} else {
			string = array.toString();
		}
		return string;
	}
	
	protected Boolean convertToBoolean(Object object) {
		if(object instanceof Boolean) {
			return ((Boolean)object);
		}
		throw Context.reportRuntimeError("A parameter must not be a boolean.'");
	}
	
	/**
	 * Extract the wrappedDomNode from the AbstractDomNodeScriptableObject in the parameter. This method is used to handle parameter in execCall
	 * @param arg
	 * @return
	 */
	protected DomNodeImpl convertToWrappedDomNode(Object arg) {
		if(arg == null) {
			throw Context.reportRuntimeError("A parameter must not be null.'");
		}
		if( ! (arg instanceof ElementScriptableObject)) {
			throw Context.reportRuntimeError("a parameter must be an Element.");
		}
		DomNodeImpl domNodeImpl = ((AbstractDomNodeScriptableObject)arg).wrappedDomNode;
		return domNodeImpl;
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
		return 0;
	}
	
	@Override
	protected final int findInstanceIdInfo(String name) {
		int result = doFindInstanceIdInfo(name);
		if(result != 0) {
			return result;
		}
		// Fields
		if(name.equals("nodeName")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_nodeName);
		}
		if(name.equals("nodeValue")) {
			return instanceIdInfo(DONTENUM|PERMANENT, Id_nodeValue);
		}
		if(name.equals("nodeType")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_nodeType);
		}
		if(name.equals("parentNode")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_parentNode);
		}
		if(name.equals("childNodes")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_childNodes);
		}
		if(name.equals("firstChild")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_firstChild);
		}
		if(name.equals("lastChild")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_lastChild);
		}
		if(name.equals("previousSibling")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_previousSibling);
		}
		if(name.equals("nextSibling")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_nextSibling);
		}
		if(name.equals("attributes")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_attributes);
		}
		if(name.equals("ownerDocument")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_ownerDocument);
		}
		if(name.equals("Id_namespaceURI")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_namespaceURI);
		}
		if(name.equals("prefix")) {
			return instanceIdInfo(DONTENUM|PERMANENT, Id_prefix);
		}
		if(name.equals("localName")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_localName);
		}
		
		return 0;
	}
	
	@Override
	protected final String getInstanceIdName(int id) {
		String result = doGetInstanceIdName(id);
		if(result != null) {
			return result;
		}
		switch(id) {
			case Id_nodeName: return "nodeName";
			case Id_nodeValue: return "nodeValue";
			case Id_nodeType: return "nodeType";
			case Id_parentNode: return "parentNode";
			case Id_childNodes: return "childNodes";
			case Id_firstChild: return "firstChild";
			case Id_lastChild: return "lastChild";
			case Id_previousSibling: return "previousSibling";
			case Id_nextSibling: return "nextSibling";
			case Id_attributes: return "attributes";
			case Id_ownerDocument: return "ownerDocument";
			case Id_namespaceURI: return "namespaceURI";
			case Id_prefix: return "prefix";
			case Id_localName: return "localName";
			default: throw new RuntimeException("Could not find instace with id '"+id+"'");
		}
	}

	@Override
	protected final Object getInstanceIdValue(int id) {
		Object result = doGetInstanceIdValue(id);
		if(result != null) {
			return result;
		}
		switch(id) {
			case Id_nodeName: return this.wrappedDomNode.getNodeName();
			case Id_nodeValue: return this.wrappedDomNode.getNodeValue();
			case Id_nodeType: return new Integer(this.wrappedDomNode.getNodeType());
			case Id_parentNode:
				DomNodeImpl parentNode = this.wrappedDomNode.getParentNode();
				if(parentNode != null) {
					return parentNode.getScriptable();
				}
				return UniqueTag.NULL_VALUE;
			case Id_childNodes:
				NodeListImpl childNodes = this.wrappedDomNode.getChildNodes();
				if(childNodes != null) {
					return childNodes.getScriptable();
				}
				return UniqueTag.NULL_VALUE;
			case Id_firstChild:
				DomNodeImpl firstChild = this.wrappedDomNode.getFirstChild();
				if(firstChild != null) {
					return firstChild.getScriptable();
				}
				return UniqueTag.NULL_VALUE;
			case Id_lastChild:
				DomNodeImpl lastChild = this.wrappedDomNode.getLastChild();
				if(lastChild != null) {
					return lastChild.getScriptable();
				}
				return UniqueTag.NULL_VALUE;
			case Id_previousSibling:
				DomNodeImpl previousSibling = this.wrappedDomNode.getPreviousSibling();
				if(previousSibling != null) {
					return previousSibling.getScriptable();
				}
				return UniqueTag.NULL_VALUE;
			case Id_nextSibling:
				DomNodeImpl nextSibling = this.wrappedDomNode.getNextSibling();
				if(nextSibling != null) {
					return nextSibling.getScriptable();
				}
				return UniqueTag.NULL_VALUE;
			case Id_attributes:
				NamedNodeMapImpl attributes  = this.wrappedDomNode.getAttributes();
				if(attributes != null) {
					return attributes.getScriptable();
				}
				return UniqueTag.NULL_VALUE;
			case Id_ownerDocument:
				DocumentImpl ownerDocument = this.wrappedDomNode.getOwnerDocument();
				if(ownerDocument != null) {
					return ownerDocument.getScriptable();
				}
				return UniqueTag.NULL_VALUE;
			case Id_namespaceURI:
				return this.wrappedDomNode.getNamespaceURI();
			case Id_prefix:
				return this.wrappedDomNode.getPrefix();
			case Id_localName:
				return this.wrappedDomNode.getLocalName();
			default: throw new RuntimeException("Could not find instance with id '"+id+"'");
		}
	}

	
	@Override
	protected final void setInstanceIdValue(int id, Object value) {
		boolean handled = doSetInstanceIdValue(id,value);
		if(handled) {
			return;
		}
		switch(id) {
			case Id_nodeValue: this.wrappedDomNode.setNodeValue((String)value); break;
			default: throw new RuntimeException("Could not find instace with id '"+id+"'");
		}
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
        Object execIdCallResult = doExecIdCall(f,cx,scope,thisObj,args,id);
        if(execIdCallResult != null) {
        	return execIdCallResult;
        }
        // We know that thisObj is never null as we it is only null when this object is called as a constructor which we checked in the previous if statement.
        AbstractDomNodeScriptableObject o = (AbstractDomNodeScriptableObject)thisObj;
        DomNodeImpl thisWrappedDomNode = o.wrappedDomNode;
        switch (id) {
        	case Id_toString:{
        		return "["+getClassName()+" "+hashCode()+"]";
        	}
        	case Id_toLocaleString:{
        		throw new RuntimeException("toLocaleString");
        	}
        	case Id_toSource:{
        		throw new RuntimeException("toLocaleString");
        	}
        	case Id_insertBefore:{
        		DomNodeImpl element1 = convertToWrappedDomNode(args[0]);
        		DomNodeImpl element2 = convertToWrappedDomNode(args[1]);
        		DomNodeImpl result = thisWrappedDomNode.insertBefore(element1,element2);
				return result == null?null:result.getScriptable();
        	}
        	case Id_replaceChild:{
        		DomNodeImpl element1 = convertToWrappedDomNode(args[0]);
        		DomNodeImpl element2 = convertToWrappedDomNode(args[1]);
        		DomNodeImpl result = thisWrappedDomNode.replaceChild(element1,element2);
				return result == null?null:result.getScriptable();
        	}
        	case Id_removeChild:{
        		DomNodeImpl element1 = convertToWrappedDomNode(args[0]);
        		DomNodeImpl result = thisWrappedDomNode.removeChild(element1);
				return result == null?null:result.getScriptable();
        	}
        	case Id_appendChild:{
        		DomNodeImpl element1 = convertToWrappedDomNode(args[0]);
        		DomNodeImpl result = thisWrappedDomNode.appendChild(element1);
				return result == null?null:result.getScriptable();
        	}
        	case Id_hasChildNodes:{
        		return new Boolean(thisWrappedDomNode.hasChildNodes());
        	}
        	case Id_cloneNode:
        		Boolean deep = convertToBoolean(args[0]);
        		thisWrappedDomNode.cloneNode(deep.booleanValue());
        		return null;
        	case Id_normalize: return null;
        	case Id_isSupported: return null;
        	case Id_hasAttributes:{
        		return new Boolean(thisWrappedDomNode.hasAttributes());
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
        int attributes;
        Object value;
        if(id <= MAX_FIELD_ID) {
        	// The id is a field
        	switch (id) {
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
        		default: throw new RuntimeException("Could not initialize prototype field with id '"+id+"'");
        	}
        	initPrototypeValue(id, name, value, attributes);
        } else {
        	// The id is a method
        	int arity;
        	switch (id) {
              // Node methods.
        		case Id_constructor:   arity=1; name="constructor";   break;
        		case Id_toSource:   arity=0; name="toSource";   break;
        		case Id_toString:   arity=0; name="toString";   break;
        		case Id_toLocaleString:   arity=0; name="toLocaleString";   break;
        		case Id_insertBefore:   arity=2; name="insertBefore";   break;
        		case Id_replaceChild:   arity=2; name="replaceChild";   break;
        		case Id_removeChild:   arity=1; name="removeChild";   break;
        		case Id_appendChild:   arity=1; name="appendChild";   break;
        		case Id_hasChildNodes:   arity=0; name="hasChildNodes";   break;
        		case Id_cloneNode:   arity=1; name="cloneNode";   break;
        		case Id_normalize:   arity=0; name="normalize";   break;
        		case Id_isSupported:   arity=2; name="isSupported";   break;
        		case Id_hasAttributes:   arity=0; name="hasAttributes";   break;
        		default: throw new RuntimeException("Could not initialize prototype method with id '"+id+"'");
        	}
        	initPrototypeMethod(getTag(), id, name, arity);
        }
		
	}

}