package de.enough.skylight.renderer;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.renderer.builder.DocumentManager;

public class Renderer implements Runnable{
	/**
	 * state that the current render state is void and must be rendered  
	 */
	public static int STATE_VOID = Integer.MIN_VALUE;
	
	/**
	 * state that the rendering is started  
	 */
	public static int STATE_START = 0x01;
	
	/**
	 * state that the document is being build  
	 */
	public static int STATE_BUILD_DOCUMENT = 0x02;
	
	/**
	 * state that the view is being build  
	 */
	public static int STATE_BUILD_VIEW = 0x03;
	
	/**
	 * state that the view is preinitializing
	 */
	public static int STATE_PREINIT = 0x04;
	
	/**
	 * state that the rendering is completed
	 */
	public static int STATE_READY = 0x05;
	
	/**
	 * the url of the document
	 */
	String url;
	
	/**
	 * the listeners to this renderer
	 */
	ArrayList listeners;
	
	/**
	 * the state
	 */
	int state = STATE_VOID;
	
	DocumentManager documentBuilder;
	
	/**
	 * Creates a new Renderer instance
	 * @param viewport the viewport to render to
	 * @param policy the policy for this renderer
	 */
	public Renderer(DocumentManager documentBuilder) {
		this.listeners = new ArrayList();
		this.documentBuilder = documentBuilder;
	}
	
	/**
	 * Adds a renderer listener
	 * @param listener
	 */
	public void addListener(RendererListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * Renders the url depending on the policies in a thread or direct 
	 * @throws IllegalArgumentException if the url is not set
	 */
	public void render() throws IllegalArgumentException {
		new Thread(this).start();
	}
	
	/**
	 * Sets the state and informs the listeners
	 * @param state the state
	 */
	public void setState(int state) {
		this.state = state;
		
		for (int index = 0; index < this.listeners.size(); index++) {
			RendererListener listener = (RendererListener)this.listeners.get(index);
			listener.onRenderStateChange(state);
		}
	}
	
	/**
	 * @return the state
	 */
	public int getState() {
		return this.state;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		setState(STATE_START);
		
		setState(STATE_BUILD_DOCUMENT);
		
		setState(STATE_BUILD_VIEW);
		
		setState(STATE_READY);
	}

	public DocumentManager getDocumentBuilder() {
		return this.documentBuilder;
	}

}
