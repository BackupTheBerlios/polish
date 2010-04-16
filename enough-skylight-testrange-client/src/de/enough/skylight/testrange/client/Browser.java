package de.enough.skylight.testrange.client;

import de.enough.polish.content.ContentLoader;
import de.enough.polish.content.filter.impl.HttpContentFilter;
import de.enough.polish.content.filter.impl.ResourceContentFilter;
import de.enough.polish.content.source.impl.HttpContentSource;
import de.enough.polish.content.source.impl.RMSContentStorage;
import de.enough.polish.content.source.impl.RMSStorageIndex;
import de.enough.polish.content.source.impl.ResourceContentSource;
import de.enough.polish.content.transform.impl.ImageContentTransform;
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
import de.enough.skylight.dom.impl.DomNodeImpl;
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
import de.enough.skylight.renderer.node.NodeUtils;
import de.enough.skylight.renderer.node.handler.html.AHandler;

public class Browser extends Form implements CommandListener, RendererListener, ViewportContext {

	Command cmdOpen = new Command("Open",Command.SCREEN,0);

	class UpdateUIEventProcessorListener implements EventProcessorListener{

		public void handleAboutToDeliverEvent(EventImpl event) {
			// Empty.
		}
		public void handleEventDelivered(EventImpl event) {
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
	
	ContentLoader contentLoader;
	
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
		this.host = UrlUtil.getPath(url);
		
		this.urlField = new UrlField(this);
		this.urlField.setUrl(url);
		
		this.viewport = new Viewport(this);
		
		this.documentBuilder = new DocumentBuilder();
		documentBuilder.setUrl(url);
		
		this.viewportBuilder = new ViewportBuilder(this.viewport);
		
		this.contentLoader = buildContentLoader();
		
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
	
	protected ContentLoader buildContentLoader() {
		// build sources
		ResourceContentSource resourceSource = new ResourceContentSource("resources");
		resourceSource.setContentFilter(new ResourceContentFilter());
		
		HttpContentSource httpSource = new HttpContentSource("http");
		
		RMSContentStorage rmsStorage = new RMSContentStorage("rms", new RMSStorageIndex(1000000));
		rmsStorage.setContentFilter(new HttpContentFilter());
		
		ContentLoader contentLoader = new ContentLoader();
		contentLoader.addContentTransform(new ImageContentTransform());
		
		// create hierachy
		rmsStorage.attachSource(httpSource);
		contentLoader.attachSource(rmsStorage);
		
		contentLoader.attachSource(resourceSource);
		
		return contentLoader;
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
	
	public String getLocationHost() {
		return this.host;
	}

	public String getLocationUrl() {
		return this.url;
	}

	public ContentLoader getContentLoader() {
		return this.contentLoader;
	}

	public void setLocation(String url) {
		this.url = url;
		this.host = UrlUtil.getPath(url);
		
		this.documentBuilder.setUrl(url);
		
		this.renderer.setState(Renderer.STATE_VOID);
		
		this.renderer.render();
	}

	public void notifyUserEvent(CssElement cssElement, UserEvent event) {
		if(cssElement.getHandler() instanceof AHandler) {
			String href = NodeUtils.getAttributeValue(cssElement.getNode(),"href");
			String url = de.enough.skylight.util.UrlUtil.completeUrl(href, this);
			setLocation(url);
		} else {
			DomNodeImpl node = cssElement.getNode();
			Services.getInstance().getEventEmitter().emitClickEvent(node, 0, 0);
		}
	}
}