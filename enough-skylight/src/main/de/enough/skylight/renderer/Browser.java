package de.enough.skylight.renderer;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Container;
import de.enough.skylight.dom.Document;
import de.enough.skylight.renderer.builder.DocumentBuilder;
import de.enough.skylight.renderer.builder.ViewBuilder;

public class Browser extends Container implements Runnable{
	String url;
	
	public Browser() {
		super(false);
	}
	
	public void setUrl(String url) {
		this.url = url;
		new Thread(this).start();
	}

	public void run() {
		//#debug debug
		System.out.println("building document");
		
		Benchmark.start("document.build");
		DocumentBuilder documentBuilder = new DocumentBuilder(this.url);
		Document document = documentBuilder.build();
		Benchmark.stop("document.build","done");
		
		//#debug debug
		System.out.println("building view");
		
		Benchmark.start("view.build");
		ViewBuilder viewBuilder = new ViewBuilder(this,document);
		viewBuilder.build();
		Benchmark.stop("view.build","done");
	}	
}
