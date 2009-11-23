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
	public static int POLICY_PREFETCH = 1;
	public static int POLICY_THREAD = 2;
	
	String url;
	
	
	ArrayList listeners;
	
	int policy = 0;
	
	int state = 0;
	// BLB
	Viewport viewport;
	
	public Renderer(Viewport viewport, int policy) {
		this.viewport = viewport; 
		this.policy = policy;
		this.state = RenderState.VOID;
		this.listeners = new ArrayList();
	}
	
	public void addStateListener(RenderState listener) {
		this.listeners.add(listener);
	}
	
	boolean isThread() {
		return (this.policy & POLICY_THREAD) == POLICY_THREAD;  
	}
	
	boolean isPrefetch() {
		return (this.policy & POLICY_PREFETCH) == POLICY_PREFETCH;  
	}
	
	public void setUrl(String url) {
		this.url = url;
		this.state = RenderState.VOID;
	}
	
	public void render() throws IllegalArgumentException {
		if(this.url == null) {
			throw new IllegalArgumentException("url is not set");
		}
		
		if(isThread()) {
			new Thread(this).start();
		} else {
			synchronized (this) {
				run();
			}
		}
	}
	
	public void setState(int state) {
		this.state = state;
		
		for (int index = 0; index < this.listeners.size(); index++) {
			RenderState listener = (RenderState)this.listeners.get(index);
			listener.onRenderState(this, state);
		}
	}
	
	public int getState() {
		return this.state;
	}

	public void run() {
		
		
		//#debug debug
		System.out.println("rendering " + this.url);
		
		Benchmark.start("render");
		
		setState(RenderState.START);
		
		setState(RenderState.BUILD_DOCUMENT);
		
		Benchmark.start("document.build");
		DocumentBuilder documentBuilder = new DocumentBuilder(this.url);
		Document document = documentBuilder.build();
		Benchmark.stop("document.build","done");
		
		//#debug debug
		System.out.println("building view");
		
		setState(RenderState.BUILD_VIEW);
		
		Benchmark.start("view.build");
		ViewportBuilder viewBuilder = new ViewportBuilder(this.viewport,document);
		viewBuilder.build();
		Benchmark.stop("view.build","done");
		
		if(true) {
			setState(RenderState.PREFETCH);
			
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
		
		setState(RenderState.READY);
	}	
}
