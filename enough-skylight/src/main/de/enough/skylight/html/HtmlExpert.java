package de.enough.skylight.html;

import org.mozilla.javascript.Scriptable;

import de.enough.skylight.Services;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.EventImpl;
import de.enough.skylight.dom.impl.EventProcessorListener;
import de.enough.skylight.dom.impl.NodeListImpl;

public class HtmlExpert implements EventProcessorListener{

	public void handleAboutToDeliverEvent(EventImpl event) {
		// TODO Auto-generated method stub
		
	}

	public void handleDeliveredEvent(EventImpl event) {
		if(event.getEventPhase() != Event.AT_TARGET) {
			return;
		}
		// TODO: Refactor this out.
		if("click".equals(event.getType())){
			DomNodeImpl target = (DomNodeImpl) event.getTarget();
			DomNode scriptAttribute = target.getAttributes().getNamedItem("onclick");
			if(scriptAttribute != null) {
				String scriptText = scriptAttribute.getNodeValue();
				if(scriptText != null && scriptText.length() != 0) {
					Services.getInstance().getJsEngine().runScript(target, scriptText);
				}
			}
		}
	}

	public void handleEventProcessingAborted(EventImpl event) {
		// TODO Auto-generated method stub
		
	}

	public void handleEventProcessingStart(EventImpl event,NodeListImpl nodeList) {
		// TODO Auto-generated method stub
		
	}

	public void handleEventProcessingStopped(EventImpl event) {
		// TODO: Here is a problem. The following must only run if the 'onclick' attribute was not evaluated.
		// This information must be carried on by the event.
		DomNodeImpl target = (DomNodeImpl) event.getTarget();
		// Avoid lazy loading by using hasScriptable() instead of getScriptable().
		if( ! target.hasScriptable()) {
			Scriptable scriptable = target.getScriptable();
			Object onClickFunction = null;
			while(scriptable != null) {
				onClickFunction = scriptable.get("onclick", scriptable);
				if(onClickFunction != null) {
					break;
				}
				scriptable = scriptable.getParentScope();
			}
			if(onClickFunction != null) {
				Services.getInstance().getJsEngine().runFunction(onClickFunction);
			}
		}
	}

}
