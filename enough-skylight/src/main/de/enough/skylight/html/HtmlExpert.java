package de.enough.skylight.html;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.HashMap;
import de.enough.skylight.Services;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.EventImpl;
import de.enough.skylight.dom.impl.EventProcessorListener;
import de.enough.skylight.dom.impl.NodeListImpl;
import de.enough.skylight.js.JsEngine;

/**
 * This listener reacts to events when they reach their target and performs HTML stuff like following a link.
 * @author rickyn
 *
 */
public class HtmlExpert implements EventProcessorListener{

	private HashMap<String,String> eventTypeToEventHandlerMap = new HashMap<String,String>();
	
	public HtmlExpert() {
		this.eventTypeToEventHandlerMap.put("load", "onload");
		this.eventTypeToEventHandlerMap.put("unload", "onunload");
		this.eventTypeToEventHandlerMap.put("abort", "onabort");
		this.eventTypeToEventHandlerMap.put("error", "onerror");
		this.eventTypeToEventHandlerMap.put("select", "onselect");
		this.eventTypeToEventHandlerMap.put("change", "onchange");
		this.eventTypeToEventHandlerMap.put("submit", "onsubmit");
		this.eventTypeToEventHandlerMap.put("reset", "onreset");
		this.eventTypeToEventHandlerMap.put("focus", "onfocus");
		this.eventTypeToEventHandlerMap.put("blur", "onblur");
		this.eventTypeToEventHandlerMap.put("click", "onclick");
		this.eventTypeToEventHandlerMap.put("mousedown", "onmousedown");
		this.eventTypeToEventHandlerMap.put("mouseup", "onmouseup");
		this.eventTypeToEventHandlerMap.put("mouseover", "onmouseover");
		this.eventTypeToEventHandlerMap.put("mousemove", "onmousemove");
		this.eventTypeToEventHandlerMap.put("mouseout", "onmouseout");
		
		// These three are not conform to the specification but needed anyway.
		this.eventTypeToEventHandlerMap.put("onkeypress", "onkeypress");
		this.eventTypeToEventHandlerMap.put("keydown", "onkeydown");
		this.eventTypeToEventHandlerMap.put("keyup", "onkeyup");
	}
	
	public void handleAboutToDeliverEvent(EventImpl event) {
		// Ignore.
	}

	public void handleEventDelivered(EventImpl event) {
		invokeMarkupEventHandler(event);
	}

	public void handleEventProcessingAborted(EventImpl event) {
		// Ignore.
	}

	public void handleEventProcessingStart(EventImpl event,NodeListImpl nodeList) {
		// Ignore.
	}

	public void handleEventProcessingStopped(EventImpl event) {
		invokeJsEventHandler(event);
	}
	
	/**
	 * This method will invoke the handler which is set in the markup. If for example a 'click'
	 * event is triggered, an 'onclick' handler is executed.
	 * @param event
	 */
	private void invokeMarkupEventHandler(EventImpl event) {
		if(event.getEventPhase() != Event.AT_TARGET) {
			return;
		}
		
		String eventType = event.getType();
		String eventHandlerName = this.eventTypeToEventHandlerMap.get(eventType);
		
		// There could be a attribute in the markup with a js script to execute.
		if(eventHandlerName != null) {
			DomNodeImpl target = event.getTarget();
			DomNode scriptAttribute = target.getAttributes().getNamedItem(eventHandlerName);
			if(scriptAttribute != null) {
				String scriptText = scriptAttribute.getNodeValue();
				if(scriptText != null && scriptText.length() != 0) {
					// There is a script in the markup for the event. Execute it.
					JsEngine jsEngine = Services.getInstance().getJsEngine();
					jsEngine.runScript(target, scriptText);
				}
			}
		}
	}

	/**
	 * This method will invoke a handler which is registered on the DOM object itself.
	 * The order of calling the javascript handler and markup hanlder is different.
	 * At first the markup handler is called, if this one is not present, then the js handler
	 * is called at the end of the event cycle.
	 * @param event
	 */
	private void invokeJsEventHandler(EventImpl event) {
		String eventType = event.getType();
		String eventHandlerName = this.eventTypeToEventHandlerMap.get(eventType);
		
		if(eventHandlerName != null) {
			DomNodeImpl target = event.getTarget();
			// Avoid lazy loading by using hasScriptable() instead of getScriptable().
			if( ! target.hasScriptable()) {
				Scriptable scriptable = target.getScriptable();
				Object eventHandlerFunction = null;
				while(scriptable != null) {
					eventHandlerFunction = scriptable.get(eventHandlerName, scriptable);
					if(eventHandlerFunction != null) {
						Services.getInstance().getJsEngine().runFunction(eventHandlerFunction);
					}
					scriptable = scriptable.getParentScope();
				}
			}
		}
	}
}
