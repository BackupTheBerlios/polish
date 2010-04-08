package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;
import de.enough.skylight.Services;
import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.EventException;
import de.enough.skylight.dom.EventListener;

public abstract class DomNodeImpl implements DomNode {

	private DomNodeImpl parent;
	private int type;
	protected NodeListImpl childList;
	protected DocumentImpl ownerDocument;
	private HashMap capturingListeners = new HashMap();
	private HashMap bubblingListeners = new HashMap();

	public void init(DocumentImpl document, DomNodeImpl parent, int type) {
		this.ownerDocument = document;
		this.parent = parent;
		this.type = type;
		this.childList = new NodeListImpl();
		if (this.parent != null) {
			this.parent.doAppendChild(this);
		}
	}
	
	public void addEventListener(String type, EventListener listener,
			boolean useCapture) {
		if (useCapture) {
			ArrayList listeners = (ArrayList) this.capturingListeners.get(type);
			if (listeners == null) {
				listeners = new ArrayList();
				this.capturingListeners.put(type, listeners);
			}
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		} else {
			ArrayList listeners = (ArrayList) this.bubblingListeners.get(type);
			if (listeners == null) {
				listeners = new ArrayList();
				this.bubblingListeners.put(type, listeners);
			}
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}

	}

	public DomNodeImpl appendChild(DomNode newChild) throws DOMException {
		// TODO: We need handling for DocumentFragments.
		DomNodeImpl newChildImpl = (DomNodeImpl) newChild;
		this.childList.add(newChildImpl);
		newChildImpl.doSetParent(this);
		return newChildImpl;
	}

	public DomNodeImpl cloneNode(boolean deep) {
		return null;
	}

	public boolean dispatchEvent(Event event) throws EventException {
		EventImpl eventImpl = (EventImpl) event;
		eventImpl.setTarget(this);
		return Services.getInstance().getEventProcessor().processEvent(eventImpl);
	}

	public NamedNodeMapImpl getAttributes() {
		return null;
	}

	public NodeListImpl getChildNodes() {
		return this.childList;
	}

	public DomNodeImpl getFirstChild() {
		if (this.childList.getLength() > 0) {
			return this.childList.item(0);
		}
		return null;
	}

	public DomNodeImpl getLastChild() {
		int length = this.childList.getLength();
		if (length > 0) {
			return this.childList.item(length - 1);
		}
		return null;
	}

	/**
	 * @unimplemented
	 */
	public String getLocalName() {
		return null;
	}

	/**
	 * @unimplemented
	 */
	public String getNamespaceURI() {
		return null;
	}

	public DomNodeImpl getNextSibling() {
		if(this.parent == null) {
			return null;
		}
		NodeListImpl siblings = this.parent.getChildNodes();
		int numberOfSiblings = siblings.getLength();
		int siblingPosition = -1;
		for(int i = 0; i < numberOfSiblings; i++) {
			if(this.equals(siblings.item(i))){
				siblingPosition = i + 1;
				break;
			}
		}
		if(0 <= siblingPosition && siblingPosition < numberOfSiblings) {
			return siblings.item(siblingPosition);
		}
		return null;
	}

	public int getNodeType() {
		return this.type;
	}

	public DocumentImpl getOwnerDocument() {
		return this.ownerDocument;
	}

	public DomNodeImpl getParentNode() {
		return this.parent;
	}

	/**
	 * @unimplemented
	 */
	public String getPrefix() {
		return null;
	}

	public DomNodeImpl getPreviousSibling() {
		if(this.parent == null) {
			return null;
		}
		NodeListImpl siblings = this.parent.getChildNodes();
		int numberOfSiblings = siblings.getLength();
		int siblingPosition = -1;
		for(int i = numberOfSiblings-1; i >= 0; i--) {
			if(this.equals(siblings.item(i))){
				siblingPosition = i - 1;
				break;
			}
		}
		if(0 <= siblingPosition && siblingPosition < numberOfSiblings) {
			return siblings.item(siblingPosition);
		}
		return null;
	}

	public abstract Scriptable getScriptable();

	public abstract boolean hasScriptable();

	public boolean hasAttributes() {
		return false;
	}

	public boolean hasChildNodes() {
		return this.childList.getLength() > 0;
	}

	/**
	 * @unimplemented
	 */
	public DomNodeImpl insertBefore(DomNode newChild, DomNode refChild)	throws DOMException {
		DomNodeImpl newChildImpl = (DomNodeImpl)newChild;
		DomNodeImpl refChildImpl = (DomNodeImpl)refChild;
		
		return this.childList.insertBefore(newChildImpl,refChildImpl);
	}

	/**
	 * @unimplemented
	 */
	public boolean isSupported(String feature, String version) {
		return false;
	}

	/**
	 * @unimplemented
	 */
	public void normalize() {
		// Unimplemented.
	}

	/**
	 * @unimplemented
	 */
	public DomNodeImpl removeChild(DomNode oldChild) throws DOMException {
		return null;
	}

	public void removeEventListener(String type, EventListener listener,
			boolean useCapture) {
		if (useCapture) {
			ArrayList listeners = (ArrayList) this.capturingListeners.get(type);
			if (listeners == null) {
				return;
			}
			listeners.remove(listener);
			if (listeners.size() == 0) {
				this.capturingListeners.remove(type);
			}
		} else {
			ArrayList listeners = (ArrayList) this.bubblingListeners.get(type);
			if (listeners == null) {
				return;
			}
			listeners.remove(listener);
			if (listeners.size() == 0) {
				this.bubblingListeners.remove(type);
			}
		}
	}

	public DomNodeImpl replaceChild(DomNode newChild, DomNode oldChild)
			throws DOMException {
		return this.childList.replace((DomNodeImpl) newChild, (DomNodeImpl) oldChild);
	}

	/**
	 * @unimplemented
	 */
	public void setPrefix(String prefix) throws DOMException {
		// Unimplemented.
	}

	private void doAppendChild(DomNodeImpl childNode) {
		this.childList.add(childNode);
	}

	private void doSetParent(DomNodeImpl domNodeImpl) {
		this.parent = domNodeImpl;
	}

	protected abstract String getAdditionalProperties();
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getTypeName());
		buffer.append("@");
		buffer.append(hashCode());
		buffer.append(":{");
//		buffer.append("children:");
//		buffer.append(this.childList.getLength());
		String addedProperties = getAdditionalProperties();
		if(addedProperties != null && addedProperties.length() > 0) {
//			buffer.append(",");
			buffer.append(addedProperties);
		}
		buffer.append("}");
		return buffer.toString();
	}

	protected String getTypeName() {
		switch (this.type) {
		case DomNode.ELEMENT_NODE:
			return "Element";
		case DomNode.TEXT_NODE:
			return "Text";
		case DomNode.DOCUMENT_NODE:
			return "Document";
		case DomNode.COMMENT_NODE:
			return "Comment";
		case DomNode.ATTRIBUTE_NODE:
			return "Attribute";
		case DomNode.CDATA_SECTION_NODE:
			return "CDData";
		case DomNode.PROCESSING_INSTRUCTION_NODE:
			return "Processing Instruction";
		default:
			throw new IllegalStateException("The DomNode type '" + this.type
					+ "' is unknown.");
		}
	}

	/**
	 * TODO: Revise this method and is usefulness.
	 * @param event
	 */
	public void propagateEvent(EventImpl event) {
		String type = event.getType();
		ArrayList listeners;
		short eventPhase = event.getEventPhase();
		switch(eventPhase) {
			case Event.CAPTURING_PHASE:
				listeners = (ArrayList) this.capturingListeners.get(type);
				break;
			case Event.AT_TARGET:
				listeners = (ArrayList) this.capturingListeners.get(type);
				break;
			case Event.BUBBLING_PHASE:
				listeners = (ArrayList) this.bubblingListeners.get(type);
				break;
			default: throw new IllegalArgumentException("An event contains the illegal phase value '"+eventPhase+"'");
		}
		 
		if (listeners == null) {
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			EventListener listener = (EventListener) listeners.get(i);
			try {
				listener.handleEvent(event);
			} catch (Exception e) {
				// Ignoring exception
			}
		}
		
		if(eventPhase == Event.AT_TARGET) {
			// TODO: Question: Are all click event handler on all parents triggered?
		}
	}
	
	
}