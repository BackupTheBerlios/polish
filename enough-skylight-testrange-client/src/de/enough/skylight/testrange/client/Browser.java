package de.enough.skylight.testrange.client;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.UrlUtil;
import de.enough.skylight.Services;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.MutationEvent;
import de.enough.skylight.dom.impl.EventImpl;
import de.enough.skylight.dom.impl.EventProcessorListener;
import de.enough.skylight.dom.impl.NodeListImpl;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.event.UserEventListener;
import de.enough.skylight.js.JsEngine;
import de.enough.skylight.renderer.Renderer;
import de.enough.skylight.renderer.RendererListener;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.ViewportContext;
import de.enough.skylight.renderer.builder.DocumentBuilder;
import de.enough.skylight.renderer.builder.ViewportBuilder;
import de.enough.skylight.renderer.node.CssElement;

public class Browser extends Form implements CommandListener, RendererListener, UserEventListener, ViewportContext {

	Command cmdOpen = new Command("Open",Command.SCREEN,0);

	class UpdateUIEventProcessorListener implements EventProcessorListener{

		public void handleAboutToDeliverEvent(EventImpl event) {
			// Empty.
		}
		public void handleDeliveredEvent(EventImpl event) {
			// Empty.
		}
		public void handleEventProcessingAborted(EventImpl event) {
			// Empty.
		}
		public void handleEventProcessingStart(EventImpl event, NodeListImpl nodeListImpl) {
				// Empty.
		}
		public void handleEventProcessingStopped(EventImpl event) {
			// TODO: Check if the event is a MutationEvent or an event like Focus. Only then update the UI.
			if(event instanceof MutationEvent) {
				MutationEvent mutationEvent = (MutationEvent)event;
				if(mutationEvent.getPrevValue() != null &&  ! mutationEvent.getPrevValue().equals(mutationEvent.getNewValue())) {
					DomNode node = (DomNode)event.getTarget();
					domModified(node);
				}
			}
		}
	}
	
	Command cmdRefresh = new Command("Refresh",Command.SCREEN,0);
	
	Command cmdExit = new Command("Exit",Command.EXIT,Integer.MAX_VALUE);
	
	DocumentBuilder documentBuilder;
	
	ViewportBuilder viewportBuilder;
	
	UrlField urlField;
	
	Viewport viewport;
	
	Renderer renderer;
	
	Refresh refresh;
	
	Display display;
	
	String url;
	
	String host;

	public Browser(String url, Display display) {
		//#style browser
		super(null);
		
		this.url = url;
		this.host = UrlUtil.getHost(url);
		
		this.urlField = new UrlField(this);
		this.urlField.setUrl(url);
		
		this.viewport = new Viewport(this);
		this.viewport.addUserEventListener(this);
		
		/*UpdateUIEventProcessorListener listener = new UpdateUIEventProcessorListener();
		Services.getInstance().getEventProcessor().addEventProcessorListener(listener);*/
		
		this.documentBuilder = new DocumentBuilder();
		documentBuilder.setUrl(url);
		
		this.viewportBuilder = new ViewportBuilder(this.viewport);
		
		this.renderer = new Renderer(this.documentBuilder, this.viewportBuilder);
		this.renderer.addListener(this);
		
		append(this.viewport);
		setCommandListener(this);
		
		this.refresh = new Refresh();
		this.renderer.addListener(this.refresh);
		
		this.display = display;
		
		addCommand(cmdExit);
		addCommand(cmdOpen);
	}
	
	public void domModified(DomNode node) {
		this.viewport.nodeUpdated(node);
	}

	public void showNotify() {
		super.showNotify();
		if(this.renderer.getState() == Renderer.STATE_VOID) {
			this.renderer.render();
		}
	}
	
	protected void relocate(String url) {
		this.url = url;
		this.host = UrlUtil.getHost(url);
		
		this.documentBuilder.setUrl(url);
		
		this.renderer.setState(Renderer.STATE_VOID);
		
		this.renderer.render();
	}
	
	public void onState(Renderer renderer, int state) {
		if(state == Renderer.STATE_START) {
			this.display.setCurrent(this.refresh);
		}
		
		if(state == Renderer.STATE_READY) {
			this.display.setCurrent(this);
		} 
		
		if(state == Renderer.STATE_BUILD_VIEW) {
			Services service = Services.getInstance();
			JsEngine jsEngine = service.getJsEngine();
			Document document = this.viewportBuilder.getDocument();
			jsEngine.setDocument(document);
		}
	}
	
	public void commandAction(Command command, Displayable screen) {
		if(command == cmdOpen) {
			this.urlField.setUrl(this.url);
			Display.getInstance().setCurrent(this.urlField);
		}
		
		if(command == cmdExit) {
			StyleSheet.midlet.notifyDestroyed();
		}
	}
	
	public void onUserEvent(CssElement element, UserEvent event) {
		DomNode node = element.getNode();
		Services.getInstance().getEventEmitter().emitClickEvent(node, 0, 0);
	}

	public String getHost() {
		return this.host;
	}

	public String getUrl() {
		return this.url;
	}
	
}