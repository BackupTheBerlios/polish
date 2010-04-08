package de.enough.skylight.dom.impl;

public interface EventProcessorListener {

	void handleEventProcessingStart(EventImpl event, NodeListImpl eventChain);

	void handleEventProcessingStopped(EventImpl event);

	void handleEventProcessingAborted(EventImpl event);

	void handleAboutToDeliverEvent(EventImpl event);

	void handleEventDelivered(EventImpl event);

}
