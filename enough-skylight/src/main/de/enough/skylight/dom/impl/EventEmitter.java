package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.EventTarget;

public class EventEmitter {

	private static EventEmitter instance;
	
	private EventProcessor eventProcessorInstance;
	
	public static EventEmitter getInstance(EventProcessor eventProcessor) {
		if(instance == null) {
			instance = new EventEmitter(eventProcessor);
		}
		return instance;
	}
	
	private EventEmitter(EventProcessor eventProcessor) {
		// Hidden.
		this.eventProcessorInstance = eventProcessor;
	}
	
	public MouseEventImpl emitClickEvent(EventTarget target, int x, int y) {
		MouseEventImpl event = new MouseEventImpl();
		event.init(target);
		event.initMouseEvent("click", true, true, null, 0, x, y, x, y, false, false, false, false,(short) 0, null);
		this.eventProcessorInstance.processEvent(event);
		return event;
	}
	
	public void emitDomAttrModifiedEvent(EventTarget target,DomNode relatedNode, String previousValue, String newValue, String attrName, short attrChange) {
		MutationEventImpl event = new MutationEventImpl();
		event.init(target);
		event.initMutationEvent("DOMAttrModified", true, false, relatedNode, previousValue, newValue, attrName, attrChange);
		this.eventProcessorInstance.processEvent(event);
		// TODO: Inform the 
	}
}
