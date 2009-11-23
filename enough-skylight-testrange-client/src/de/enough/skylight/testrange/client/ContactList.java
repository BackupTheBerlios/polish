package de.enough.skylight.testrange.client;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.benchmark.Benchmark;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Form;
import de.enough.polish.util.Prefetch;
import de.enough.skylight.dom.Document;
import de.enough.skylight.renderer.RenderState;
import de.enough.skylight.renderer.Renderer;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.builder.DocumentBuilder;
import de.enough.skylight.renderer.builder.ViewportBuilder;

public class ContactList extends MIDlet{
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

	protected void pauseApp() {}

	protected void startApp() throws MIDletStateChangeException {
		String url = null;
		
		//#if skylight.testrange.url:defined
		//#= url = "${skylight.testrange.url}";
		//#endif
		
		if(url == null) {
			//#debug error
			System.out.println("url is not set, please set skylight.testrange.url in your user.properties");
			return;
		}
		
		Form form = new Form("contactlist");
		
		long start = System.currentTimeMillis();
		
		DocumentBuilder documentBuilder = new DocumentBuilder(url);
		Document document = documentBuilder.build();
		
		ViewportBuilder viewportBuilder = new ViewportBuilder(document);
		for (int i = 0; i < 75; i++) {
			Viewport contact = new Viewport();
			viewportBuilder.setViewport(contact);
			viewportBuilder.build();
			form.append(contact);
		}
		
		Prefetch.prefetch(form);
		
		long duration = System.currentTimeMillis() - start;
		System.out.println(duration);
		
		Display.getDisplay(this).setCurrent(form);
	}
	
}
