package de.enough.skylight;

import de.enough.skylight.dom.impl.EventEmitter;
import de.enough.skylight.dom.impl.EventProcessor;
import de.enough.skylight.dom.impl.TraceEventProcessorListener;
import de.enough.skylight.html.HtmlExpert;
import de.enough.skylight.js.JsEngine;

public class Services {

	private static Services instance;

	private JsEngine jsEngine;
	private EventEmitter eventEmitter;
	private EventProcessor eventProcessor;

	public static Services getInstance() {
		if(instance == null) {
			instance = new Services();
		}
		return instance;
	}
	
	private Services() {
		this.jsEngine = new JsEngine();
		this.eventProcessor = new EventProcessor();
		this.eventProcessor.addEventProcessorListener(new TraceEventProcessorListener());
		this.eventProcessor.addEventProcessorListener(new HtmlExpert());
		this.eventEmitter = EventEmitter.getInstance(this.eventProcessor);
	}
	
	public JsEngine getJsEngine() {
		return this.jsEngine;
	}

	public EventEmitter getEventEmitter() {
		return this.eventEmitter;
	}

	public EventProcessor getEventProcessor() {
		return this.eventProcessor;
	}

}
