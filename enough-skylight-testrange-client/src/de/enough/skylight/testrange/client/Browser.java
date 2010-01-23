package de.enough.skylight.testrange.client;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.renderer.Renderer;
import de.enough.skylight.renderer.RendererListener;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.builder.DocumentBuilder;
import de.enough.skylight.renderer.builder.ViewportBuilder;

public class Browser extends Form implements CommandListener, RendererListener{

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
		
		DocumentBuilder documentBuilder = new DocumentBuilder(url);
		ViewportBuilder viewportBuilder = new ViewportBuilder(this.viewport);
		
		this.renderer = new Renderer(documentBuilder, viewportBuilder);
		this.renderer.addListener(this);
		
		append(this.viewport);
		setCommandListener(this);
		
		this.refresh = new Refresh();
		this.renderer.addListener(this.refresh);
		
		this.display = display;
		
		addCommand(cmdExit);
		addCommand(cmdRefresh);
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
