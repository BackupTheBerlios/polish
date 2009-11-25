package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Event;

public class EventProcessor {

	private static EventProcessor instance;
	
	// TODO: Static methods are not nice. Inject this object as a dependency or register as a listener on an event source.
	public static EventProcessor getInstance() {
		if(instance == null) {
			instance = new EventProcessor();
		}
		return instance;
	}
	

	/**
	 * 
	 * @param event
	 * @return The return value is true if any listener called preventDefault() on the event. The return value is false otherwise.
	 */
	public boolean processEvent(EventImpl event) {
		DomNodeImpl target = (DomNodeImpl) event.getTarget();
		
		NodeListImpl eventChain = createEventChain(target);
		int numberOfEventTargets;
		numberOfEventTargets = eventChain.getLength();
		
		// Propagate event downstream
		event.setEventEnvironment(Event.CAPTURING_PHASE);
		for(int i = numberOfEventTargets-1; i >= 0; i++){
			DomNodeImpl chainElement = (DomNodeImpl)eventChain.item(i);
			event.setEventEnvironment(chainElement);
			chainElement.handleCaptureEvent(event);
			// React to capturing.
			if(event.isStopPropagation()) {
				return false;
			}
		}
		
		// Deliver event to eventarget
		event.setEventEnvironment(Event.AT_TARGET, target);
		target.handleCaptureEvent(event);
		if( ! event.isPreventDefault()) {
			// Trigger node default action.
			target.doDefaultAction();
		}
		
		// Bubble event upstream.
		event.setEventEnvironment(Event.BUBBLING_PHASE);
		for(int i = 0; i < numberOfEventTargets; i++){
			DomNodeImpl chainElement = (DomNodeImpl)eventChain.item(i);
			event.setEventEnvironment(chainElement);
			chainElement.handleBubblingEvent(event);
			// React to capturing.
			if(event.isStopPropagation()) {
				return false;
			}
		}
		return event.isPreventDefault();
	}
	
	/**
	 * This method creates a list of EventTarget objects which are the direct ancestors of the parameter dom node.
	 * The first item is the parent of the target, the last node is the root of the hierarchy.
	 * @param target
	 * @return A NodeListImpl object with EventTarget objects as list items. The return value is never null.
	 */
	private NodeListImpl createEventChain(DomNodeImpl target) {
		NodeListImpl nodeList = new NodeListImpl();
		DomNodeImpl domNode = (DomNodeImpl)target.getParentNode();
		while(domNode != null) {
			nodeList.add(domNode);
			domNode = (DomNodeImpl)domNode.getParentNode();
		}
		return nodeList;
	}

}
