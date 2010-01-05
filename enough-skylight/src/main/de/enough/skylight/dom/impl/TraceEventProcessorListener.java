package de.enough.skylight.dom.impl;


public class TraceEventProcessorListener implements EventProcessorListener {

	public void handleAboutToDeliverEvent(EventImpl event) {
		//#debug
		System.out.println("About to deliver event '"+event+"'.");
	}

	public void handleDeliveredEvent(EventImpl event) {
		//#debug
		System.out.println("Delivered event '"+event+"'.");
	}

	public void handleEventProcessingAborted(EventImpl event) {
		// TODO Auto-generated method stub
		
	}

	public void handleEventProcessingStart(EventImpl event,NodeListImpl eventChain) {
		int numberOfEventTargets = eventChain.getLength();
		//#debug
		System.out.println();
		//#debug
		System.out.println("Start processing event '"+event+"' and deliver it to '"+numberOfEventTargets+"' elements.");
	}

	public void handleEventProcessingStopped(EventImpl event) {
		//#debug
		System.out.println("Processing stoped of event '"+event+"'.");
		//#debug
		System.out.println();
	}

}