package de.enough.skylight.testrange.client;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.Gauge;
import de.enough.skylight.renderer.RenderState;
import de.enough.skylight.renderer.Renderer;

public class Refresh extends Form implements RenderState{

	Gauge progress;
	
	public Refresh() {
		//#style refresh
		super(null);
		
		//#style refresh_progress
		this.progress = new Gauge(null,false,100,0);
		
		append(this.progress);
	}

	public void onRenderState(Renderer renderer, int state) {
		if(isShown()) {
			if(state == RenderState.START) {
				this.progress.setValue(0);
			}
			
			if(state == RenderState.BUILD_DOCUMENT) {
				this.progress.setValue(25);	
			}
			
			if(state == RenderState.BUILD_VIEW) {
				this.progress.setValue(75);
			}
			
			if(state == RenderState.READY) {
				this.progress.setValue(100);
			}
		}
	}

	
}
