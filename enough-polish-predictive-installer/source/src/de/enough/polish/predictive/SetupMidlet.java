package de.enough.polish.predictive;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.util.Locale;

public class SetupMidlet 
extends MIDlet
implements CommandListener
{
	protected Form form;
	protected Command exitCommand = new Command( Locale.get("cmd.exit"), Command.EXIT, 3 );
	protected Command cancelCommand = new Command( Locale.get("cmd.cancel"), Command.CANCEL, 3 );
	
	protected Command yesCommand = new Command( Locale.get("cmd.yes"), Command.OK, 0 );
	protected Command noCommand = new Command( Locale.get("cmd.no"), Command.CANCEL, 0 );
	
	protected StringItem info = null;
	protected StringItem status = null;
	protected StringItem error = null;
	protected Gauge gauge = null;
	
	protected SetupThread installer  = null;
	protected Thread thread = null;
	
	public SetupMidlet() {
		//#style mainScreen
		this.form = new Form( "Predictive Setup");
		
		this.form.addCommand( this.cancelCommand );
		this.form.setCommandListener( this );
		
		this.info = new StringItem("",Locale.get("text.info"));
		this.status = new StringItem("","");
		this.error = new StringItem("","");
		this.gauge = new Gauge("",true,100,0);

		//#style introItem
		this.form.append(info);
		//#style gauge
		this.form.append(gauge);
		//#style statusItem
		this.form.append(status);
		//#style errorItem
		this.form.append(error);
		
		installer  = new SetupThread(this);
		thread = new Thread(installer);
	}

     protected void startApp() throws MIDletStateChangeException{
    	 thread.start();
    	 Display.getDisplay( this ).setCurrent( this.form );
     }
     	
     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
    	  // ignore
     }

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == this.cancelCommand) {
			Alert cancel = new Alert(Locale.get("text.cancel"));
			
			cancel.setString(Locale.get("text.cancel"));
			cancel.addCommand(this.yesCommand);
			cancel.addCommand(this.noCommand);
			
			cancel.setCommandListener(this);
			
			installer.pause();
			
			Display.getDisplay(this).setCurrent(cancel);
		} else if (cmd == this.exitCommand) {
			notifyDestroyed();
		} 
		
		if(disp instanceof Alert)
		{
			if(cmd == this.yesCommand)
			{
				notifyDestroyed();
			} 
			else if(cmd == this.noCommand)
			{
				synchronized (installer){
					installer.notify();
				}
				
				Display.getDisplay(this).setCurrent(this.form);
			}
		}
		
	}
}