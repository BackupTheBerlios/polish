package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.MutationEvent;

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
		boolean doCapture = true;
		if(event instanceof MutationEvent) {
			doCapture = false;
		}
		if(doCapture) {
			// Propagate event downstream
			System.out.println("Capturing event to target");
			event.setEventEnvironment(Event.CAPTURING_PHASE);
			for(int i = numberOfEventTargets-1; i >= 0; i--){
				DomNodeImpl chainElement = (DomNodeImpl)eventChain.item(i);
				event.setEventEnvironment(chainElement);
				System.out.println("About to handle capture at element '"+chainElement+"'");
				chainElement.handleCaptureEvent(event);
				// React to capturing.
				if(event.isStopPropagation()) {
					System.out.println("Stopping propagation");
					return false;
				}
			}
		}
		
		// Deliver event to eventarget
		System.out.println("Handling event at target '"+target+"'");
		event.setEventEnvironment(Event.AT_TARGET, target);
		target.handleCaptureEvent(event);
		if( ! event.isPreventDefault()) {
			// Trigger node default action.
			// TODO: Do not always trigger the default action. On Mutation for example, a link must not be triggered!
			target.doDefaultAction();
		}
		
		if(event.getBubbles()) {
			// Bubble event upstream.
			System.out.println("Handling bubbling of event");
			event.setEventEnvironment(Event.BUBBLING_PHASE);
			for(int i = 0; i < numberOfEventTargets; i++){
				DomNodeImpl chainElement = (DomNodeImpl)eventChain.item(i);
				System.out.println("About to handle bubble at element '"+chainElement+"'");
				event.setEventEnvironment(chainElement);
				chainElement.handleBubblingEvent(event);
				// React to capturing.
				if(event.isStopPropagation()) {
					System.out.println("Stopping propagation");
					return false;
				}
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
