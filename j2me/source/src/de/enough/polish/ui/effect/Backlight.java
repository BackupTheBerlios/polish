package de.enough.polish.ui.effect;

//#if polish.Vendor == Nokia
	import com.nokia.mid.ui.DeviceControl;
//#endif
	
//#if polish.Vendor == Samsung
	import com.samsung.util.LCDLight;
//#endif

import de.enough.polish.ui.StyleSheet;

public class Backlight extends Thread implements Runnable{
	private boolean quit = false; 
	
	public void run()
	{
		while(!this.quit)
		{
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			on();
		}
	}
	
	public static void on()
	{
		//#elif polish.vendor == Nokia
			DeviceControl.setLights(0,100);
		//#elif polish.Vendor == Samsung
			 LCDLight.on(10000);
		//#elif polish.vendor == Sony-Ericsson || polish.midp2 
			StyleSheet.display.flashBacklight(100);
		//#endif
	}
	
	public void quit()
	{
		this.quit = true;
	}
}
