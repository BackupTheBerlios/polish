package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.EventException;
import de.enough.skylight.dom.EventListener;
import de.enough.skylight.dom.NamedNodeMap;
import de.enough.skylight.dom.NodeList;

public abstract class DomNodeImpl implements DomNode {

	private DomNodeImpl parent;
	private String name;
	private NamedNodeMapImpl attributes;
	private int type;
	protected NodeListImpl childList;
//	private String text;
	private DocumentImpl document;
	private String value;
	private HashMap capturingListeners = new HashMap();
	private HashMap bubblingListeners = new HashMap();

	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		if(useCapture) {
			ArrayList listeners = (ArrayList) this.capturingListeners.get(type);
			if(listeners == null) {
				listeners = new ArrayList();
				this.capturingListeners.put(type, listeners);
			}
			if( ! listeners.contains(listener)) {
				listeners.add(listener);
			}
		} else {
			ArrayList listeners = (ArrayList) this.bubblingListeners.get(type);
			if(listeners == null) {
				listeners = new ArrayList();
				this.bubblingListeners.put(type, listeners);
			}
			if( ! listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
		
	}
	
	public DomNode appendChild(DomNode newChild) throws DomException {
		//TODO: We need handling for DocumentFragments.
		DomNodeImpl newChildImpl = (DomNodeImpl)newChild;
		this.childList.add(newChildImpl);
		newChildImpl.doSetParent(this);
		return newChild;
	}
	
	public DomNode cloneNode(boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean dispatchEvent(Event event) throws EventException {
		EventImpl eventImpl = (EventImpl)event;
		eventImpl.setTarget(this);
		return EventProcessor.getInstance().processEvent(eventImpl);
	}
	public NamedNodeMap getAttributes() {
		//TODO: Return a copy of the attributes so the list can not be changed.
		return this.attributes;
	}
	public NodeList getChildNodes() {
		return this.childList;
	}
	public DomNode getFirstChild() {
		if(this.childList.getLength() > 0) {
			return this.childList.item(0);
		}
		return null;
	}
	public DomNode getLastChild() {
		int length = this.childList.getLength();
		if(length > 0) {
			return this.childList.item(length-1);
		}
		return null;
	}
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}
	public DomNode getNextSibling() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getNodeName() {
		return this.name;
	}
	public int getNodeType() {
		return this.type;
	}
	public String getNodeValue() throws DomException {
		return this.value;
	}
	public Document getOwnerDocument() {
		return this.document;
	}
	public DomNode getParentNode() {
		return this.parent;
	}
	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}
	public DomNode getPreviousSibling() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// TODO: Put these two methods in a nice interface.
	public abstract Scriptable getScriptable();
	public abstract boolean hasScriptable();
	
	public boolean hasAttributes() {
		return this.attributes.getLength() > 0;
	}
	public boolean hasChildNodes() {
		return this.childList.getLength() > 0;
	}
	// TODO: init also all collection fields again and release all items in them.
	public void init(DocumentImpl document, DomNodeImpl parent, String name, NamedNodeMapImpl attributes, int type) {
		this.document = document;
		this.parent = parent;
		this.type = type;
		this.childList = new NodeListImpl();
		this.name = name;
		if(attributes == null) {
			this.attributes = new NamedNodeMapImpl();
		} else {
			this.attributes = attributes;
		}

        if(this.parent != null) {
            this.parent.doAppendChild(this);
        }
	}
	public DomNode insertBefore(DomNode newChild, DomNode refChild) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isSupported(String feature, String version) {
		// TODO Auto-generated method stub
		return false;
	}
	public void normalize() {
		// TODO Auto-generated method stub
		
	}
	public DomNode removeChild(DomNode oldChild) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		if(useCapture) {
			ArrayList listeners = (ArrayList) this.capturingListeners.get(type);
			if(listeners == null) {
				return;
			}
			listeners.remove(listener);
			if(listeners.size() == 0) {
				this.capturingListeners.remove(type);
			}
		} else {
			ArrayList listeners = (ArrayList) this.bubblingListeners.get(type);
			if(listeners == null) {
				return;
			}
			listeners.remove(listener);
			if(listeners.size() == 0) {
				this.bubblingListeners.remove(type);
			}
		}
	}
	public DomNode replaceChild(DomNode newChild, DomNode oldChild) throws DomException {
		this.childList.replace((DomNodeImpl)newChild, (DomNodeImpl)oldChild);
		return oldChild;
	}
	public void setNodeValue(String nodeValue) throws DomException {
		this.value = nodeValue;
	}
	
	public void setPrefix(String prefix) throws DomException {
		// TODO Auto-generated method stub
		
	}
//	public void toXmlString(StringBuffer buffer) {
//		if(this.type == TEXT_NODE) {
//			buffer.append(getNodeValue());
//			return;
//		}
//		buffer.append("<");
//		buffer.append(this.name);
//		int numberAttributes = this.attributes.getLength();
//		if(numberAttributes > 0) {
//			for(int i = 0; i < numberAttributes; i++) {
//				AttrImpl attr = (AttrImpl)this.attributes.item(i);
//				buffer.append(" ");
//				attr.toXmlString(buffer);
//			}
//		}
//		buffer.append(">");
//		int numberChildren = this.childList.getLength();
//		for(int i = 0; i < numberChildren; i++) {
//			DomNode childNode = this.childList.item(i);
//			childNode.toXmlString(buffer);
//		}
//		buffer.append("</");
//		buffer.append(this.name);
//		buffer.append(">");
//	}
	
	private void doAppendChild(DomNodeImpl childNode) {
		this.childList.add(childNode);
	}
	
	/**
	 * This method is called by the event system so the node has time to do its default action. This could be for example
	 * changing the url after clicking an anchor element.
	 * @param event
	 */
	protected void doDefaultAction(EventImpl event) {
		// TODO: Make this method abstract.
	}
	
	private void doSetParent(DomNodeImpl domNodeImpl) {
		this.parent = domNodeImpl;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getTypeName());
		if(this.name != null) {
			buffer.append(":");
			buffer.append(this.name);
			buffer.append(".");
		}
		if(this.value != null) {
			buffer.append(";");
			buffer.append(this.value);
			buffer.append(".");
		}
		return buffer.toString();
	}

	protected String getTypeName() {
		switch(this.type) {
		case DomNode.ELEMENT_NODE: return "Element";
		case DomNode.TEXT_NODE: return "Text";
		case DomNode.DOCUMENT_NODE: return "Document";
		case DomNode.COMMENT_NODE: return "Comment";
		case DomNode.ATTRIBUTE_NODE: return "Attribute";
		case DomNode.CDATA_SECTION_NODE: return "CDData";
		case DomNode.PROCESSING_INSTRUCTION_NODE: return "Processing Instruction";
		default: throw new IllegalStateException("The DomNode type '"+this.type+"' is unknown.");
		}
	}
	
	public void handleCaptureEvent(EventImpl event) {
		String type = event.getType();
		ArrayList listeners = (ArrayList)this.capturingListeners.get(type);
		if(listeners == null) {
			return;
		}
		for(int i = 0; i < listeners.size(); i++) {
			EventListener listener = (EventListener)listeners.get(i);
			try {
				listener.handleEvent(event);
			} catch(Exception e) {
				// Ignoring exception
			}
		}
	}

	public void handleBubblingEvent(EventImpl event) {
		String type = event.getType();
		ArrayList listeners = (ArrayList)this.bubblingListeners.get(type);
		if(listeners == null) {
			return;
		}
		for(int i = 0; i < listeners.size(); i++) {
			EventListener listener = (EventListener)listeners.get(i);
			try {
				listener.handleEvent(event);
			} catch(Exception e) {
				// Ignoring exception
			}
		}
	}
}