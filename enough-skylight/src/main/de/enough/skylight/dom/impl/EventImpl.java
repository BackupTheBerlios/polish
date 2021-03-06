package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.EventTarget;

/**
 * Always call init first and then the more specific init methods.
 * @author rickyn
 *
 */
public abstract class EventImpl implements Event{

	private boolean bubbles;
	private boolean cancelable;
	private DomNodeImpl target;
	private long timeStamp;
	private short eventPhase;
	private EventTarget currentTarget;
	private String eventType;
	private boolean stopPropagation;
	private boolean preventDefault;

	public void init(DomNodeImpl target) {
		this.timeStamp = System.currentTimeMillis();
		this.bubbles = true;
		this.cancelable = true;
		this.eventPhase = CAPTURING_PHASE;
		this.stopPropagation = false;
		this.preventDefault = false;
		this.target = target;
	}
	
	public boolean getBubbles() {
		return this.bubbles;
	}

	public boolean getCancelable() {
		return this.cancelable;
	}

	public EventTarget getCurrentTarget() {
		return this.currentTarget;
	}

	public short getEventPhase() {
		return this.eventPhase;
	}

	public DomNodeImpl getTarget() {
		return this.target;
	}

	public long getTimeStamp() {
		return this.timeStamp;
	}

	public String getType() {
		return this.eventType;
	}

	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg) {
		if(this.currentTarget != null) {
			// A call to this method is only allowed before dispatch has begun.
			return;
		}
		this.bubbles = canBubbleArg;
		this.cancelable = cancelableArg;
		this.eventType = eventTypeArg;
	}

	public void preventDefault() {
		this.preventDefault = true;
	}

	public void stopPropagation() {
		this.stopPropagation = true;
	}

	/**
	 * This method is used to set the environment of the event. If the current target or the event phase changes,
	 * the event engine will change the state of this event accordingly.
	 * @param eventPhase
	 * @param currentTarget
	 */
	protected void setEventEnvironment(short eventPhase, EventTarget currentTarget) {
		this.eventPhase = eventPhase;
		this.currentTarget = currentTarget;
	}
	
	protected boolean isStopPropagation() {
		return this.stopPropagation;
	}

	protected boolean isPreventDefault() {
		return this.preventDefault;
	}

	void setTarget(DomNodeImpl newEventTarget) {
		this.target = newEventTarget;
	}

	protected abstract String toStringOfProperties();
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Event@");
		buffer.append(hashCode());
		buffer.append(":[type:'");
		buffer.append(this.eventType);
		buffer.append("',phase:'");
		buffer.append(getNameOfPhase());
		buffer.append("',currentTarget:");
		buffer.append(this.currentTarget);
		buffer.append(",target:");
		buffer.append(this.target);
		String appendedProperties = toStringOfProperties();
		if(appendedProperties != null && appendedProperties.length() > 0) {
			buffer.append(",");
			buffer.append(appendedProperties);
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	private String getNameOfPhase() {
		switch(this.eventPhase) {
			case Event.BUBBLING_PHASE: return "bubbling";
			case Event.CAPTURING_PHASE: return "capturing";
			case Event.AT_TARGET: return "atTarget";
			default:throw new RuntimeException("Unknown event phase: "+this.eventPhase);
		}
	}
}
