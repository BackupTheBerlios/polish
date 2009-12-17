package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.EventTarget;

public class EventEmitter {

	private static EventEmitter instance;
	private static EventProcessor eventProcessorInstance;
	
	public static EventEmitter getInstance() {
		if(instance == null) {
			instance = new EventEmitter();
			eventProcessorInstance = EventProcessor.getInstance();
		}
		return instance;
	}
	
	private EventEmitter() {
		// Hidden.
	}
	
	public MouseEventImpl emitClickEvent(EventTarget target, int x, int y) {
		MouseEventImpl event = new MouseEventImpl();
		event.init(target);
		event.initMouseEvent("click", true, true, null, 0, x, y, x, y, false, false, false, false,(short) 0, null);
		eventProcessorInstance.processEvent(event);
		return event;
	}
	
	public void emitDomAttrModifiedEvent(EventTarget target,DomNode relatedNode, String previousValue, String newValue, String attrName, short attrChange) {
		MutationEventImpl event = new MutationEventImpl();
		event.init(target);
		event.initMutationEvent("DOMAttrModified", true, false, relatedNode, previousValue, newValue, attrName, attrChange);
		eventProcessorInstance.processEvent(event);
		// TODO: Inform the 
	}
}
