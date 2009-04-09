package de.enough.polish.sample.rmi;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import de.enough.polish.rmi.RemoteClient;


public class CallServerForm extends Form implements CommandListener{

	private final static Command exitCommand = new Command("Exit", Command.EXIT, 0);
	private XMLRPCSampleMidlet midlet;
	private static final int i = 10;
	private static final int j = 5;
	
	public CallServerForm(XMLRPCSampleMidlet midlet) {
		super("Calling XML-RPC Server");
		addCommand(CallServerForm.exitCommand);
		setCommandListener(this);
		this.midlet = midlet;
		
		try{
			XMLRPCSampleServer r = (XMLRPCSampleServer) RemoteClient.open("de.enough.polish.sample.rmi.XMLRPCSampleServer", "http://localhost:8080/xmlrpcserver/xmlrpc" );
			//#debug info
			System.out.println("Opened remote server [" + r.getClass().getName() + "]");
			int firstResult = r.XMLRPCSampleServer__sum(CallServerForm.i, CallServerForm.j);
			int secondResult = r.XMLRPCSampleServer__sub(CallServerForm.i, CallServerForm.j);
			
			//#mdebug info
			System.out.println(CallServerForm.i + " + " + CallServerForm.j + " = " + firstResult);
			System.out.println(CallServerForm.i + " - " + CallServerForm.j + " = " + secondResult);
			//#enddebug
			
			StringItem firstResultItem = new StringItem(CallServerForm.i + " + " + CallServerForm.j + " = ", String.valueOf(firstResult));
			StringItem secondResultItem = new StringItem(CallServerForm.i + " - " + CallServerForm.j + " = ", String.valueOf(secondResult));
			append(firstResultItem);
			append(secondResultItem);
			
		}catch (Exception e) {
			//#debug info
			System.out.println("Could not open RMI server: " + e);
			StringItem item = new StringItem("An error occured: ", e.toString());
			append(item);
		}
	}

	public void commandAction(Command cmd, Displayable disp) {
		
		if(CallServerForm.exitCommand.equals(cmd)){
			this.midlet.quitApplication();
		}
		
	}

}
