package de.enough.skylight.dom.impl;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.MutationEvent;

/**
 * This class propagates an event through the DOM. Depending on the event, the capturing and bubbling phases are processed.
 * EventProcessorListener objects are informed about the current status of the event.
 * <br/>
 * The event propagation mechanism is as follows:
 * <ol>
 * <li> Event created
 * <li> Event capturing
 * <li> Event onclick handling from the markup at the target element (onclick="alert()")
 * <li> Event bubbling
 * <li> Event onclick handling from JS at the DOM Node object (element.onclick = function(){alert()})
 * </ol>
 * Step 5) only takes place if step 3) was omitted because no markup handling was definied. So 3) and 5) are mutally
 * exclusive.
 * <br/>
 * This class is not threadsafe.
 * 
 * @author rickyn
 *
 */
public class EventProcessor {

	private ArrayList<EventProcessorListener> eventProcessorListeners;
	
	public EventProcessor() {
		// Hidden.
		this.eventProcessorListeners = new ArrayList<EventProcessorListener>();
	}
	
	public void addEventProcessorListener(EventProcessorListener eventProcessorListener) {
		if( ! this.eventProcessorListeners.contains(eventProcessorListener)) {
			this.eventProcessorListeners.add(eventProcessorListener);
		}
	}

	public void removeEventProcessorListener(EventProcessorListener eventProcessorListener) {
		this.eventProcessorListeners.remove(eventProcessorListener);
	}
	
	/**
	 * 
	 * @param event
	 * @return The return value is true if any listener called preventDefault() on the event. The return value is false otherwise.
	 */
	public boolean processEvent(EventImpl event) {
		DomNodeImpl target = (DomNodeImpl) event.getTarget();
		
		NodeListImpl eventChain = createEventChain(target);
		informAboutEventProcessingStart(event,eventChain);
		int numberOfEventTargets = eventChain.getLength();
		
		boolean doCapture = true;
		// TODO: Put this into the MutationEvent as default capture=false;
		if(event instanceof MutationEvent) {
			doCapture = false;
		}
		if(doCapture) {
			// Propagate event downstream
			for(int i = numberOfEventTargets-1; i >= 0; i--){
				DomNodeImpl chainElement = (DomNodeImpl)eventChain.item(i);
				event.setEventEnvironment(Event.CAPTURING_PHASE,chainElement);
				
				informListenersAboutToDeliverEvent(event);
				chainElement.propagateEvent(event);
				informListenersDeliveredEvent(event);

				// React to stopping.
				if(event.isStopPropagation()) {
					//#debug
					System.out.println("Stopping propagation of event '"+event+"'");
					informListenersEventProcessingAborted(event);
					return false;
				}
			}
		}
		
		// Deliver event to event target
		event.setEventEnvironment(Event.AT_TARGET, target);
		
		informListenersAboutToDeliverEvent(event);
		target.propagateEvent(event);
		informListenersDeliveredEvent(event);
		
		if(event.getBubbles()) {
			// Bubble event upstream.
			for(int i = 0; i < numberOfEventTargets; i++){
				DomNodeImpl chainElement = (DomNodeImpl)eventChain.item(i);
				event.setEventEnvironment(Event.BUBBLING_PHASE,chainElement);
				
				informListenersAboutToDeliverEvent(event);
				chainElement.propagateEvent(event);
				informListenersDeliveredEvent(event);
				
				// React to stopping.
				if(event.isStopPropagation()) {
					//#debug
					System.out.println("Stopping propagation");
					
					informListenersEventProcessingAborted(event);
					return false;
				}
			}
		}

		informListenersEventProcessingStopped(event);
		// Handle js onclick functions on the elements.
		return event.isPreventDefault();
	}
	
	private void informAboutEventProcessingStart(EventImpl event, NodeListImpl eventChain) {
		int numberOfEventProcessorListeners = this.eventProcessorListeners.size();
		for(int i = 0; i < numberOfEventProcessorListeners; i++) {
			try {
				EventProcessorListener eventProcessorListener = this.eventProcessorListeners.get(i);
				eventProcessorListener.handleEventProcessingStart(event,eventChain);
			} catch(Exception e) {
				// Do nothing.
			}
		}
	}
	
	private void informListenersEventProcessingStopped(EventImpl event) {
		int numberOfEventProcessorListeners = this.eventProcessorListeners.size();
		for(int i = 0; i < numberOfEventProcessorListeners; i++) {
			try {
				EventProcessorListener eventProcessorListener = this.eventProcessorListeners.get(i);
				eventProcessorListener.handleEventProcessingStopped(event);
			} catch(Exception e) {
				// Do nothing.
			}
		}
	}

	private void informListenersEventProcessingAborted(EventImpl event) {
		int numberOfEventProcessorListeners = this.eventProcessorListeners.size();
		for(int i = 0; i < numberOfEventProcessorListeners; i++) {
			try {
				EventProcessorListener eventProcessorListener = this.eventProcessorListeners.get(i);
				eventProcessorListener.handleEventProcessingAborted(event);
			} catch(Exception e) {
				// Do nothing.
			}
		}
	}

	private void informListenersAboutToDeliverEvent(EventImpl event) {
		int numberOfEventProcessorListeners = this.eventProcessorListeners.size();
		for(int i = 0; i < numberOfEventProcessorListeners; i++) {
			try {
				EventProcessorListener eventProcessorListener = this.eventProcessorListeners.get(i);
				eventProcessorListener.handleAboutToDeliverEvent(event);
			} catch(Exception e) {
				// Do nothing.
			}
		}
	}

	private void informListenersDeliveredEvent(EventImpl event) {
		int numberOfEventProcessorListeners = this.eventProcessorListeners.size();
		for(int i = 0; i < numberOfEventProcessorListeners; i++) {
			try {
				EventProcessorListener eventProcessorListener = this.eventProcessorListeners.get(i);
				eventProcessorListener.handleDeliveredEvent(event);
			} catch(Exception e) {
				// Do nothing.
			}
		}
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
