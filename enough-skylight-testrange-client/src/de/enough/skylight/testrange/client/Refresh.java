package de.enough.skylight.testrange.client;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.skylight.renderer.RendererListener;
import de.enough.skylight.renderer.Renderer;

public class Refresh extends Form implements RendererListener{

	Gauge progress;
	
	public Refresh() {
		//#style refresh
		super(null);
		
		//#style refresh_progress
		this.progress = new Gauge(null,false,100,0);
		
		append(this.progress);
	}

	public void onState(Renderer renderer, int state) {
		if(isShown()) {
			if(state == Renderer.STATE_START) {
				this.progress.setValue(0);
			}
			
			if(state == Renderer.STATE_BUILD_DOCUMENT) {
				this.progress.setValue(25);	
			}
			
			if(state == Renderer.STATE_BUILD_VIEW) {
				this.progress.setValue(75);
			}
			
			if(state == Renderer.STATE_READY) {
				this.progress.setValue(100);
			}
		}
	}

	
}
