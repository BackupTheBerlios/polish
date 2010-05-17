package de.enough.skylight.js.scriptableobjects;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.ElementImpl;

public class DocumentScriptableObject extends AbstractDomNodeScriptableObject {

	private static final int Id_doctype = FIRST_ID;
	private static final int Id_implementation = FIRST_ID + 1;
	private static final int Id_documentElement = FIRST_ID + 2;
	private static final int Id_createElement = FIRST_ID + 3;
	private static final int Id_createDocumentFragment = FIRST_ID + 4;
	private static final int Id_createTextNode = FIRST_ID + 5;
	private static final int Id_createComment = FIRST_ID + 6;
	private static final int Id_createCDATASection = FIRST_ID + 7;
	private static final int Id_createProcessingInstruction = FIRST_ID + 8;
	private static final int Id_createAttribute = FIRST_ID + 9;
	private static final int Id_createEntityReference = FIRST_ID + 10;
	private static final int Id_getElementsByTagName = FIRST_ID + 11;
	private static final int Id_importNode = FIRST_ID + 12;
	private static final int Id_createElementNS = FIRST_ID + 13;
	private static final int Id_createAttributeNS = FIRST_ID + 14;
	private static final int Id_getElementsByTagNameNS = FIRST_ID + 15;
	private static final int Id_getElementById = FIRST_ID + 16;
	private static final int MAX_PROTOTYPE_ID = FIRST_ID + 16;
	
	protected DocumentImpl document;
	private final static Object DOCUMENT_TAG = new Object();

	public DocumentScriptableObject(DocumentImpl docuementImpl) {
		super(docuementImpl);
		this.document = docuementImpl;
	}

	public static void init(Scriptable scope, boolean sealed) {
		DocumentImpl dummyElement = new DocumentImpl();
		DocumentScriptableObject obj = new DocumentScriptableObject(dummyElement);
		obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
	}
	
	@Override
	public String getClassName() {
		return "Document";
	}

	@Override
	protected Scriptable constructScriptableObject(Object toBeWrappedDomainObject) {
		return new DocumentScriptableObject((DocumentImpl)toBeWrappedDomainObject);
	}

	@Override
	protected final Object doExecIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args, int id) {
		DocumentScriptableObject o = (DocumentScriptableObject)thisObj;
		DocumentImpl thisWrappedDocument = (DocumentImpl)o.wrappedDomNode;
		switch (id) {
			case Id_createElement:{
				String tagName = convertToString(args,0);
				ElementImpl element = thisWrappedDocument.createElement(tagName);
				return element == null?null:element.getScriptable();
			}
        	case Id_createDocumentFragment:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_createTextNode:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_createComment:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_createCDATASection:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_createProcessingInstruction:{
        		throw  Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_createAttribute:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_createEntityReference:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_getElementsByTagName:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_importNode:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_createElementNS:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_createAttributeNS:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_getElementsByTagNameNS:{
        		throw Context.reportRuntimeError("This method is not supported at the moment.");
        	}
        	case Id_getElementById:{
        		String tagName = convertToString(args,0);
				ElementImpl element = thisWrappedDocument.getElementById(tagName);
				if(element == null) {
					throw Context.reportRuntimeError("No element with id '"+tagName+"' found.");
				}
				return element.getScriptable();
        	}
		}
		return null;
	}

	@Override
	protected final int doFindInstanceIdInfo(String name) {
		if(name.equals("doctype")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_doctype);
		}
		if(name.equals("implementation")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_implementation);
		}
		if(name.equals("documentElement")) {
			return instanceIdInfo(DONTENUM|PERMANENT|READONLY, Id_documentElement);
		}
		return 0;
	}

	@Override
	protected final int doFindPrototypeId(String name) {
		if("createElement".equals(name)) {
			return Id_createElement;
		}
		if("createDocumentFragment".equals(name)) {
			return Id_createDocumentFragment;
		}
		if("createTextNode".equals(name)) {
			return Id_createTextNode;
		}
		if("createComment".equals(name)) {
			return Id_createComment;
		}
		if("createCDATASection".equals(name)) {
			return Id_createCDATASection;
		}
		if("createProcessingInstruction".equals(name)) {
			return Id_createProcessingInstruction;
		}
		if("createAttribute".equals(name)) {
			return Id_createAttribute;
		}
		if("createEntityReference".equals(name)) {
			return Id_createEntityReference;
		}
		if("getElementsByTagName".equals(name)) {
			return Id_getElementsByTagName;
		}
		if("importNode".equals(name)) {
			return Id_importNode;
		}
		if("createElementNS".equals(name)) {
			return Id_createElementNS;
		}
		if("createAttributeNS".equals(name)) {
			return Id_createAttributeNS;
		}
		if("getElementsByTagNameNS".equals(name)) {
			return Id_getElementsByTagNameNS;
		}
		if("getElementById".equals(name)) {
			return Id_getElementById;
		}
		return 0;
	}

	@Override
	protected final String doGetInstanceIdName(int id) {
		switch(id) {
			case Id_doctype: return "doctype";
			case Id_implementation: return "implementation";
			case Id_documentElement: return "documentElement";
		}
		return null;
	}

	@Override
	protected final Object doGetInstanceIdValue(int id) {
		switch(id) {
		case Id_doctype: throw Context.reportRuntimeError("This method is not supported at the moment.");
		case Id_implementation: throw Context.reportRuntimeError("This method is not supported at the moment.");
		case Id_documentElement:
			ElementImpl documentElement = this.document.getDocumentElement();
			return documentElement == null?null:documentElement.getScriptable();
		}
		return null;
	}

	@Override
	protected final boolean doSetInstanceIdValue(int id, Object value) {
		switch(id) {
			case Id_doctype: throw Context.reportRuntimeError("This value is not settable.");
			case Id_implementation: throw Context.reportRuntimeError("This value is not settable.");
			case Id_documentElement: throw Context.reportRuntimeError("This value is not settable.");
		}
		return false;
	}

	@Override
	protected final boolean doInitPrototypeId(int id) {
		String name;
    	int arity;
    	switch (id) {
          // Element methods.
    		case Id_createElement:   arity=1; name="createElement";   break;
    		case Id_createDocumentFragment:   arity=1; name="createDocumentFragment";   break;
    		case Id_createTextNode:   arity=1; name="createTextNode";   break;
    		case Id_createComment:   arity=1; name="createComment";   break;
    		case Id_createCDATASection:   arity=1; name="createCDATASection";   break;
    		case Id_createProcessingInstruction:   arity=2; name="createProcessingInstruction";   break;
    		case Id_createAttribute:   arity=1; name="createAttribute";   break;
    		case Id_createEntityReference:   arity=1; name="createEntityReference";   break;
    		case Id_getElementsByTagName:   arity=1; name="getElementsByTagName";   break;
    		case Id_importNode:   arity=2; name="importNode";   break;
    		case Id_createElementNS:   arity=2; name="createElementNS";   break;
    		case Id_createAttributeNS:   arity=2; name="createAttributeNS";   break;
    		case Id_getElementsByTagNameNS:   arity=2; name="getElementsByTagNameNS";   break;
    		case Id_getElementById:   arity=1; name="getElementById";   break;
    		default: return false;
        }
        initPrototypeMethod(getTag(), id, name, arity);
        return true;
	}

	@Override
	protected Object getTag() {
		return DOCUMENT_TAG;
	}
}
