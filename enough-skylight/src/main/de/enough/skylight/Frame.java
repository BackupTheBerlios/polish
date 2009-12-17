package de.enough.skylight;

import de.enough.skylight.dom.impl.EventEmitter;

public class Frame {

	private static Frame frame;
	
	private JsEngine jsEngine;
	private EventEmitter eventEmitter;
	
	public static Frame getInstance() {
		if(frame == null) {
			frame = new Frame();
		}
		return frame;
	}
	
	private Frame() {
		this.jsEngine = new JsEngine();
		this.eventEmitter = EventEmitter.getInstance();
	}
	
	public JsEngine getJsEngine() {
		return this.jsEngine;
	}

	public EventEmitter getEventEmitter() {
		return this.eventEmitter;
	}
	
}
