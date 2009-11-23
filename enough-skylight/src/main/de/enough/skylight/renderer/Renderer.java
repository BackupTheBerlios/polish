package de.enough.skylight.renderer;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Container;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.ItemPrefetch;
import de.enough.polish.util.Prefetch;
import de.enough.skylight.dom.Document;
import de.enough.skylight.renderer.builder.DocumentBuilder;
import de.enough.skylight.renderer.builder.ViewportBuilder;

public class Renderer implements Runnable{
	/**
	 * no policies
	 */
	public static int POLICY_NONE = 0;
	
	/**
	 * policy to preinit the viewport after build 
	 */
	public static int POLICY_PREINIT = 1;
	
	/**
	 * policy to run the rendering in a thread 
	 */
	public static int POLICY_THREADED = 2;

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
	 * the policies
	 */
	int policy = POLICY_NONE;
	
	/**
	 * the state
	 */
	int state = STATE_VOID;
	
	/**
	 * the viewport to render to
	 */
	Viewport viewport;
	
	/**
	 * Creates a new Renderer instance
	 * @param viewport the viewport to render to
	 */
	public Renderer(Viewport viewport) {
		this.viewport = viewport; 
		this.listeners = new ArrayList();
	}
	
	/**
	 * Creates a new Renderer instance
	 * @param viewport the viewport to render to
	 * @param policy the policy for this renderer
	 */
	public Renderer(Viewport viewport, int policy) {
		this.viewport = viewport; 
		this.policy = policy;
		this.listeners = new ArrayList();
	}
	
	/**
	 * Adds a renderer listener
	 * @param listener
	 */
	public void addListener(RendererListener listener) {
		this.listeners.add(listener);
	}
	
	/**
	 * @return true if the policy indicates that the build should be run in a thread, otherwise false
	 */
	boolean isThreaded() {
		return (this.policy & POLICY_THREADED) == POLICY_THREADED;  
	}
	
	/**
	 * @return true if the policy indicates that the view should be preinitialized, otherwise false
	 */
	boolean isPreinit() {
		return (this.policy & POLICY_PREINIT) == POLICY_PREINIT;  
	}
	
	
	/**
	 * Sets the url
	 * @param url the url
	 */
	public void setUrl(String url) {
		this.url = url;
		this.state = STATE_VOID;
	}
	
	/**
	 * Renders the url depending on the policies in a thread or direct 
	 * @throws IllegalArgumentException if the url is not set
	 */
	public void render() throws IllegalArgumentException {
		if(this.url == null) {
			throw new IllegalArgumentException("url is not set");
		}
		
		if(isThreaded()) {
			new Thread(this).start();
		} else {
			synchronized (this) {
				run();
			}
		}
	}
	
	/**
	 * Sets the state and informs the listeners
	 * @param state the state
	 */
	public void setState(int state) {
		this.state = state;
		
		for (int index = 0; index < this.listeners.size(); index++) {
			RendererListener listener = (RendererListener)this.listeners.get(index);
			listener.onState(this, state);
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
		//#debug debug
		System.out.println("rendering " + this.url);
		
		Benchmark.start("render");
		
		setState(STATE_START);
		
		setState(STATE_BUILD_DOCUMENT);
		
		Benchmark.start("document.build");
		DocumentBuilder documentBuilder = new DocumentBuilder(this.url);
		Document document = documentBuilder.build();
		Benchmark.stop("document.build","done");
		
		//#debug debug
		System.out.println("building view");
		
		setState(STATE_BUILD_VIEW);
		
		Benchmark.start("view.build");
		ViewportBuilder viewBuilder = new ViewportBuilder(this.viewport,document);
		viewBuilder.build();
		Benchmark.stop("view.build","done");
		
		if(isPreinit()) {
			setState(STATE_PREINIT);
			
			//#debug debug
			System.out.println("prefetching view");
			
			Benchmark.start("view.prefetch");
			
			try {
				ItemPrefetch.prefetch(this.viewport);
			} catch(IllegalArgumentException e) {
				//#debug error
				System.out.println("prefetch error : " + e);
			}
			
			Benchmark.stop("view.prefetch","done");
		}
		
		Benchmark.stop("render","done");
		
		setState(STATE_READY);
	}	
}
