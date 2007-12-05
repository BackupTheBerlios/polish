package de.enough.polish.util;

import de.enough.polish.ui.StyleSheet;

public class DeviceControl extends Thread implements Runnable{
	private boolean lightOff = false; 
	
	public void run()
	{
		this.lightOff = false;
		
		while(!this.lightOff)
		{
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			lightOn();
		}
	}
	
	public static void lightOn()
	{
		//#if polish.vendor == Nokia
			com.nokia.mid.ui.DeviceControl.setLights(0,100);
		//#elif polish.Vendor == Samsung
			com.samsung.util.LCDLight.on(500);
		//#elif polish.vendor == Sony-Ericsson && polish.midp2 
			StyleSheet.display.flashBacklight(100);
		//#elif polish.midp2
			StyleSheet.display.flashBacklight(100);
		//#endif
	}
	
	public void lightOff()
	{
		this.lightOff = true;
	}
	
	public static void vibrate(int duration)
	{
		//#if polish.midp2
			StyleSheet.display.vibrate(duration);
		//#endif
	}
}
