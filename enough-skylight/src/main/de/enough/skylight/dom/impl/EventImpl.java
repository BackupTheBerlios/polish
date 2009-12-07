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
	private EventTarget target;
	private long timeStamp;
	private short eventPhase;
	private EventTarget currentTarget;
	private String eventType;
	private boolean stopPropagation;
	private boolean preventDefault;

	public void init(EventTarget target) {
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

	public EventTarget getTarget() {
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
	
	/**
	 * This method is used to set the environment of the event. If the current target or the event phase changes,
	 * the event engine will change the state of this event accordingly.
	 * @param eventPhase
	 * @param currentTarget
	 */
	protected void setEventEnvironment(short eventPhase) {
		this.eventPhase = eventPhase;
	}
	
	/**
	 * This method is used to set the environment of the event. If the current target or the event phase changes,
	 * the event engine will change the state of this event accordingly.
	 * @param eventPhase
	 * @param currentTarget
	 */
	protected void setEventEnvironment(EventTarget currentTarget) {
		this.currentTarget = currentTarget;
	}

	protected boolean isStopPropagation() {
		return this.stopPropagation;
	}

	protected boolean isPreventDefault() {
		return this.preventDefault;
	}

	void setTarget(EventTarget newEventTarget) {
		this.target = newEventTarget;
	}

}
