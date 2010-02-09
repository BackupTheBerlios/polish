package de.enough.skylight.js.form;

import javax.microedition.lcdui.Command;

import de.enough.polish.ui.CommandListener;
import de.enough.polish.ui.Display;
import de.enough.polish.ui.Displayable;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.StringItem;

public class Alert implements CommandListener{

	static Command cmdOk = new Command("OK", Command.ITEM, 0);
	
	StringItem messageItem;
	
	Displayable parent;
	
	Form form;
	
	public void show() {
		Display display = Display.getInstance();
		Displayable disp = display.getCurrent();
		this.parent = disp;
		display.setCurrent(this.form);
	}
	
	public void init(String message) {
		//#style alert?
		this.form = new Form(null);
		
		//#style alertMessage?
		StringItem messageItem = new StringItem(null,message);
		
		this.form.append(messageItem);
		this.form.addCommand(cmdOk);
		this.form.setCommandListener(this);
	}
	
	public void close() {
		Display.getInstance().setCurrent(this.parent);
	}

	public void commandAction(de.enough.polish.ui.Command command,
			Displayable screen) {
		if(command == cmdOk) {
			close();
		}
	}
}
