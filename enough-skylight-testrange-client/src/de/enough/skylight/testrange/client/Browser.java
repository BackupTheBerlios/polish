package de.enough.skylight.testrange.client;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.renderer.Renderer;
import de.enough.skylight.renderer.RenderState;
import de.enough.skylight.renderer.Viewport;

public class Browser extends Form implements CommandListener, RenderState{

	Command cmdRefresh = new Command("Refresh",Command.SCREEN,0);
	
	Command cmdExit = new Command("Exit",Command.EXIT,Integer.MAX_VALUE);
	
	Viewport viewport;
	
	Renderer renderer;
	
	Refresh refresh;
	
	Display display;
	
	public Browser(String url, Display display) {
		//#style browser
		super(url);
		
		this.viewport = new Viewport();
		this.renderer = new Renderer(this.viewport, Renderer.POLICY_PREFETCH | Renderer.POLICY_THREADED);
		
		this.renderer.setUrl(url);
		this.renderer.addStateListener(this);
		
		setCommandListener(this);
		append(this.viewport);
		
		this.refresh = new Refresh();
		this.renderer.addStateListener(this.refresh);
		this.refresh.setTitle(url);
		
		this.display = display;
		
		addCommand(cmdExit);
		addCommand(cmdRefresh);
	}
	
	public void showNotify() {
		super.showNotify();
		
		if(this.renderer.getState() == RenderState.VOID) {
			this.renderer.render();
		}
	}
	
	public void onRenderState(Renderer renderer, int state) {
		if(state == RenderState.START) {
			this.display.setCurrent(this.refresh);
		}
		
		if(state == RenderState.READY) {
			this.display.setCurrent(this);
		} 
	}
	
	public void commandAction(Command command, Displayable screen) {
		if(command == cmdRefresh) {
			this.renderer.render();
		}
		
		if(command == cmdExit) {
			StyleSheet.midlet.notifyDestroyed();
		}
	}
}
