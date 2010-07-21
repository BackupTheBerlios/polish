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
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.MutationEvent;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.EventImpl;
import de.enough.skylight.dom.impl.EventProcessorListener;
import de.enough.skylight.dom.impl.NodeListImpl;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.modeller.Box;
import de.enough.skylight.modeller.LayoutModeler;
import de.enough.skylight.modeller.StyleManager;
import de.enough.skylight.renderer.BoxRenderer;
import de.enough.skylight.renderer.Renderer;
import de.enough.skylight.renderer.RendererListener;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.ViewportContext;
import de.enough.skylight.renderer.builder.DocumentManager;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeUtils;
import de.enough.skylight.renderer.node.handler.html.AHandler;

public class Browser extends Form implements CommandListener, RendererListener, ViewportContext {


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
	
	private static Command cmdOpen = new Command("Open",Command.SCREEN,0);
	private static Command cmdRefresh = new Command("Refresh",Command.SCREEN,0);
	private static Command cmdExit = new Command("Exit",Command.EXIT,Integer.MAX_VALUE);
	
	private DocumentManager documentManager;
	private ContentLoader contentLoader;
	private UrlEntryForm urlEntryForm;
	private Viewport viewport;
	private RefreshForm refreshForm;
	private BoxRenderer renderer;
	private Display display;
	// The url may be relative.
	private String url;
	private String host;

	public Browser(Display display) {
		//#style browser
		super(null);
		
		this.display = display;
		this.contentLoader = buildContentLoader();
		this.urlEntryForm = new UrlEntryForm(this);
		this.documentManager = new DocumentManager();
		this.renderer = new BoxRenderer();
		this.viewport = new Viewport(this);
		
		append(this.viewport);
		
		this.refreshForm = new RefreshForm();
		this.renderer.addRendererListener(this.refreshForm);
		this.renderer.addRendererListener(this);
		
		setCommandListener(this);
		
		// TODO: Exit belongs in the client.
		addCommand(cmdExit);
		addCommand(cmdOpen);
		addCommand(cmdRefresh);
	}
	
	public void displayPage(String urlPath) {
		this.url = urlPath;
		this.host = UrlUtil.getPath(this.url);
		
		this.viewport.clear();
		
		DocumentImpl document = this.documentManager.build(this.url);
		
		CssElement rootElement = StyleManager.buildDescription(document, null,this);
		CssElement htmlNode = StyleManager.findBodyCssElement(rootElement);
					
		LayoutModeler layouter = new LayoutModeler();
		// TODO: Get the width from the viewport.
		Box root = layouter.model(htmlNode, 300);
		
		this.renderer.render(this.viewport, root);
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
//		if(this.renderer.getState() == Renderer.STATE_VOID) {
//			this.renderer.render();
//		}
	}
	
	public void onRenderStateChange(int state) {
		if(state == Renderer.STATE_START) {
			System.out.println("Browser.onRenderStateChange():handling render state: "+state+ " and displaying refresh form.");
			this.display.setCurrent(this.refreshForm);
		}
		
		if(state == Renderer.STATE_READY) {
			System.out.println("Browser.onRenderStateChange():handling render state: "+state+" and displaying browser.");
			this.display.setCurrent(this);
		} 
		
		if(state == Renderer.STATE_BUILD_VIEW) {
//			Services service = Services.getInstance();
//			JsEngine jsEngine = service.getJsEngine();
//			jsEngine.setDocument(document);
		}
	}
	
	public void commandAction(Command command, Displayable screen) {
		if(command == cmdOpen) {
			this.urlEntryForm.setUrl(this.url);
			Display.getInstance().setCurrent(this.urlEntryForm);
		}
		
		if(command == cmdExit) {
			StyleSheet.midlet.notifyDestroyed();
		}
	}
	
	public String getLocationUrl() {
		return this.url;
	}

	public ContentLoader getContentLoader() {
		return this.contentLoader;
	}

	public void notifyUserEvent(CssElement cssElement, UserEvent event) {
		if(cssElement.getHandler() instanceof AHandler) {
			String href = NodeUtils.getAttributeValue(cssElement.getNode(),"href");
			String url = de.enough.util.UrlUtil.completeUrl(href, this.getLocationHost());
//			setLocation(url);
		} else {
			DomNodeImpl node = cssElement.getNode();
			Services.getInstance().getEventEmitter().emitClickEvent(node, 0, 0);
		}
	}
	
	public String getLocationHost() {
		return this.host;
	}

}