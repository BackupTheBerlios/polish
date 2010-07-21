package de.enough.skylight.testrange.client;

import de.enough.polish.ui.Command;
import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.TextField;

public class UrlEntryForm extends Form implements CommandListener {

	private static Command cmdGo = new Command("Go",Command.SCREEN,0);
	
	private static Command cmdBack = new Command("Back",Command.BACK,0);
	
	Browser browser;

	TextField urlField;
	
	public UrlEntryForm(Browser parent) {
		//#style url
		super(null);
		
		this.browser = parent;

		//#style urlField
		this.urlField = new TextField(null,null,512,TextField.ANY);
		
		append(this.urlField);
		
		addCommand(cmdGo);
		addCommand(cmdBack);
		
		setCommandListener(this);
	}
	
	public void setUrl(String url) {
		this.urlField.setString(url);
	}
	
	public String getUrl() {
		return this.urlField.getString();
	}

	public void commandAction(Command command, Displayable screen) {
		if(command == cmdGo) {
			Display.getInstance().setCurrent(this.browser);
//			String url = getUrl();
//			this.browser.setLocation(url);
		}
		
		if(command == cmdBack) {
			Display.getInstance().setCurrent(this.browser);
		}
	}
}
