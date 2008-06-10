package de.enough.polish.sample.table;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.ChartItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TableItem;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import de.enough.polish.util.TableData;

public class TableMidlet 
extends MIDlet
implements CommandListener
{
	
	private Form form;
	private Command exitCommand = new Command( "Exit", Command.EXIT, 3 );

	public TableMidlet() {
		//#style mainScreen
		this.form = new Form( "J2ME Polish Table");
		this.form.addCommand( this.exitCommand );
		this.form.setCommandListener( this );


	}

     protected void startApp() throws MIDletStateChangeException{
          Display display = Display.getDisplay( this );
          try {
          updateTable();
          } catch (Exception e) {
        	  //#debug error
        	  System.out.println("Unable to initialize table completely");
          }
          display.setCurrent( this.form );
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
		if (cmd == this.exitCommand) {
			notifyDestroyed();
		}
	}
	
	private void updateTable() throws IOException{
		this.form.deleteAll();
		//#style label
		this.form.append("without cell design:");
		//#style defaultTable
		TableItem table = new TableItem(3, 2);
		table.set( 1, 0, "1,0");
		table.set( 2, 0, "2,0");
		table.set( 0, 1, "0,1");
		table.set( 1, 1, "xxxx1,1");
		table.set( 2, 1, "2,1");
		this.form.append( table );
		
		//#style label
		this.form.append("with center cell design:");
		//#style defaultTable
		table = new TableItem(3, 2);
		//#style centeredCell
		table.set( 1, 0, "1,0");
		//#style centeredCell
		table.set( 2, 0, "2,0");
		//#style centeredCell
		table.set( 0, 1, "0,1");
		//#style centeredCell
		table.set( 1, 1, "xxxx1,1");
		//#style centeredCell
		table.set( 2, 1, "2,1");
		table.setLineStyle( TableItem.LINE_STYLE_SOLID, 0x000000 ); // set the line color to black 
		this.form.append( table );
		
		//#style label
		this.form.append("constructed with TableData:");
		TableData data = new TableData(3,2);
		data.set( 1, 0, "1,0");
		data.set( 2, 0, "2,0");
		data.set( 0, 1, "0,1");
		data.set( 1, 1, "xxxx1,1");
		data.set( 2, 1, "2,1");
		//#style defaultTable
		table = new TableItem(data);
		this.form.append( table );
		
				
		//#style label
		this.form.append("items in a table:");
		//#style defaultTable
		table = new TableItem(4, 5);
		//#style heading
		table.set( 1, 0, "Nokia");
		//#style heading
		table.set( 2, 0, "Samsung");
		//#style heading
		table.set( 3, 0, "Motorola");
		//#style heading
		table.set( 0, 1, "Country:");
		Image img = Image.createImage("/fi.png");
		//#style centeredCell
		ImageItem imgItem = new ImageItem(null, img, ImageItem.PLAIN, null);
		table.set( 1, 1, imgItem);
		img = Image.createImage("/ko.png");
		//#style centeredCell
		imgItem = new ImageItem(null, img, ImageItem.PLAIN, null);
		table.set( 2, 1, imgItem);
		img = Image.createImage("/us.png");
		//#style centeredCell
		imgItem = new ImageItem(null, img, ImageItem.PLAIN, null);
		table.set( 3, 1, imgItem);
		//#style heading
		table.set( 0, 2, "Share:");
		//#style centeredCell
		table.set( 1, 2, "40%");
		//#style centeredCell
		table.set( 2, 2, "13%");
		//#style centeredCell
		table.set( 3, 2, "12%");
		//#style heading
		table.set( 0, 3, "Share 07:");
		//#style centeredCell
		table.set( 1, 3, "34%");
		//#style centeredCell
		table.set( 2, 3, "11%");
		//#style centeredCell
		table.set( 3, 3, "17%");
		//#style heading
		table.set( 0, 4, "Share 06:");
		//#style centeredCell
		table.set( 1, 4, "31%");
		//#style centeredCell
		table.set( 2, 4, "9%");
		//#style centeredCell
		table.set( 3, 4, "21%");
		
		table.setSelectionMode( TableItem.SELECTION_MODE_ROW | TableItem.SELECTION_MODE_COLUMN );
		//table.setSelectedBackground( new SimpleBackground(0xffff00)); // using CSS instead now

		this.form.append( table );
		
		

		
	}

}