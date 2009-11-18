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
		dispatchEventInteral((EventImpl)event,null);
		return false;
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
	public abstract Scriptable getScriptable();
	public boolean hasAttributes() {
		return this.attributes.getLength() > 0;
	}
	public boolean hasChildNodes() {
		// TODO Auto-generated method stub
		return false;
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
	public void toXmlString(StringBuffer buffer) {
		if(this.type == TEXT_NODE) {
			buffer.append(getNodeValue());
			return;
		}
		buffer.append("<");
		buffer.append(this.name);
		int numberAttributes = this.attributes.getLength();
		if(numberAttributes > 0) {
			for(int i = 0; i < numberAttributes; i++) {
				AttrImpl attr = (AttrImpl)this.attributes.item(i);
				buffer.append(" ");
				attr.toXmlString(buffer);
			}
		}
		buffer.append(">");
		int numberChildren = this.childList.getLength();
		for(int i = 0; i < numberChildren; i++) {
			DomNode childNode = this.childList.item(i);
			childNode.toXmlString(buffer);
		}
		buffer.append("</");
		buffer.append(this.name);
		buffer.append(">");
	}
	
	private NodeListImpl createEventChain(DomNodeImpl target) {
		NodeListImpl nodeList = new NodeListImpl();
		DomNodeImpl domNode = target;
		while(domNode != null) {
			nodeList.add(domNode);
			domNode = domNode.parent;
		}
		return nodeList;
	}

	/**
	 * This method will do the actual dispatch. If the nodeChain is empty, it will be calcuated. This normally happens
	 * right after the inital dispatch.
	 * @param event
	 * @param nodeChain
	 */
	private void dispatchEventInteral(EventImpl event, NodeListImpl eventChain) {
		DomNodeImpl target = (DomNodeImpl) event.getTarget();
		if(eventChain == null ) {
			eventChain = createEventChain(target);
		}
		int index = eventChain.getIndex();
		if(index >= eventChain.getLength()-1) {
			// We reached the event target.
			event.setEventEnvironment(Event.AT_TARGET, this);
			if(event.isPreventDefault()) {
				return;
			}
			doDefaultAction();
			return;
		}
		event.setEventEnvironment(this);
		short eventPhase = event.getEventPhase();
		if(eventPhase == Event.CAPTURING_PHASE) {
			ArrayList listeners = (ArrayList)this.capturingListeners.get(event.getType());
			if(listeners != null) {
				int numberOfListeners = listeners.size();
				for(int i = 0; i < numberOfListeners; i++) {
					EventListener eventListener = (EventListener)listeners.get(i);
					try {
						eventListener.handleEvent(event);
					} catch(Exception exception) {
						// Do nothing with the exception.
					}
				}
			}
			eventChain.increaseIndex();
			// TODO: Now it is breaking. The event propagation should not increase the call stack.
			// So we should have a EventManager who will do the propagation and calling of listeners
			// on the current targets and will call doDefaultAction on the event target.
			// The EventManager chould then be also threaded.
		}
	}
	
	private void doAppendChild(DomNodeImpl childNode) {
		this.childList.add(childNode);
	}
	
	private void doDefaultAction() {
		
	}
	
	private void doSetParent(DomNodeImpl domNodeImpl) {
		this.parent = domNodeImpl;
	}

}