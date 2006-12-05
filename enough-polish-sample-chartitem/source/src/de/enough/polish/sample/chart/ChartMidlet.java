package de.enough.polish.sample.chart;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.ChartItem;

public class ChartMidlet extends MIDlet{
	
	private Form form;

	public ChartMidlet() {
		//#style mainScreen
		this.form = new Form( "J2ME Polish Chart");
		int[][] dataSequences = new int[][] {
			new int[]{ 12, 0, 5, 20, 25, 40 },
			new int[]{ 0, 2, 4, 8, 16, 32 },
			new int[]{ 1, 42, 7, 12, 16, 1 }
		};
		int[] colors = new int[]{ 0xFF0000, 0x00FF00, 0x0000FF };
		//#style lineChart
		ChartItem chart = new ChartItem( null, dataSequences, colors );
		this.form.append( chart );
		
		dataSequences = new int[][] {
				new int[]{ -12, -10, -5, -20, -25, -40, -10, -40, -10 },
				new int[]{ -30, -2, -4, -8, -16, -32, -16, -8, -14 },
				new int[]{ -19, -42, -7, -12, -16, -9, -22, -10, -35 }
		};
		//#style lineChart
		chart = new ChartItem( null, dataSequences, colors );
		chart.setLabelX( "years");
		chart.setLabelY("losses");
		this.form.append( chart );

		dataSequences = new int[][] {
				new int[]{ -12, 0, 5, 20, -25, -40, -10, -40, -10 },
				new int[]{ 0, -2, -4, -8, 16, 32, 16, -8, -4 },
				new int[]{ 1, -42, 7, 12, -16, -1, 0, -10, 35 }
		};
		//#style lineChart
		chart = new ChartItem( null, dataSequences, colors );
		this.form.append( chart );

	}

     protected void startApp() throws MIDletStateChangeException{
          Display display = Display.getDisplay( this );
          display.setCurrent( this.form );
     }

     protected void pauseApp(){
          // TODO: Implement this method.
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // TODO: Implement this method.
     }

}