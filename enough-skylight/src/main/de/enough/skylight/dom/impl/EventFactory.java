package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.EventTarget;

public class EventFactory {

	private static EventFactory eventFactoryInstance;
	
	public static EventFactory getInstance() {
		if(eventFactoryInstance == null) {
			eventFactoryInstance = new EventFactory();
		}
		return eventFactoryInstance;
	}
	
	public void fireDomAttrModifiedEvent(EventTarget target,DomNode relatedNode, String previousValue, String newValue, String attrName, short attrChange) {
		MutationEventImpl event = new MutationEventImpl();
		event.init(target);
		event.initMutationEvent("DOMAttrModified", true, false, relatedNode, previousValue, newValue, attrName, attrChange);
		EventProcessor.getInstance().processEvent(event);
	}
}
