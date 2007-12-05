package de.enough.polish.util;

import de.enough.polish.ui.StyleSheet;

/**
 * 
 * <p>Controls backlight and vibration in an device-independent manner</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * @author Andre Schmidt
 * @author Robert Virkus
 */
public class DeviceControl extends Thread {
	
	private static DeviceControl thread;
	private boolean lightOff = false; 
	
	private DeviceControl() {
		// disallow instantiation
	}
	
	public void run()
	{
		
		this.lightOff = false;
		long sleeptime = 5000;
		while(!this.lightOff)
		{
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				// ignore
			}
			if (!this.lightOff) {
				switchLightOnFor( (int) (sleeptime >> 1) );
			}
		}
	}
	
	private void switchLightOnFor( int durationInMs ) {
		//#if polish.api.samsung
			com.samsung.util.LCDLight.on(durationInMs);
		//#elif polish.midp2
			StyleSheet.display.flashBacklight(durationInMs);
		//#endif
	}
	
	private void switchLightOff()
	{
		this.lightOff = true;
		//#if polish.api.samsung
			com.samsung.util.LCDLight.off();
		//#elif polish.midp2
			StyleSheet.display.flashBacklight(0);
		//#endif
	}
	
	/**
	 * Turns the backlight on on a device until lightOff is called
	 * 
	 * @return true when backlight is supported on this device.
	 */
	public static boolean lightOn()
	{
		//#if polish.api.nokia-ui && !polish.api.midp2
			com.nokia.mid.ui.DeviceControl.setLights(0,100);
			//# return true;
		//#else
			if (thread == null) {
				if (isLightSupported()) {
					DeviceControl dc = new DeviceControl();
					dc.start();
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		//#endif
	}
	
	/**
	 * Turns the backlight off
	 *
	 */
	public static void lightOff()
	{
		//#if polish.api.nokia-ui && !polish.api.midp2
			com.nokia.mid.ui.DeviceControl.setLights(0,0);
		//#else
			DeviceControl dc = thread;
			if (dc != null) {
				dc.switchLightOff();
				thread = null;
			}
		//#endif
	}
	
	/**
	 * Checks if backlight can be controlled by the application
	 * 
	 * @return true when the backlight can be controlled by the application
	 */
	public static boolean isLightSupported()
	{
		boolean isSupported = false;
		//#if polish.api.nokia-ui && !polish.api.midp2
			isSupported = true;
		//#elif polish.api.samsung
			isSupported = com.samsung.util.LCDLight.isSupported();
		//#elif polish.midp2
			isSupported = StyleSheet.display.flashBacklight(0);
		//#endif
		return isSupported;
	}

	
	

	/**
	 * Vibrates the device for the given duration
	 * 
	 * @param duration the duration in milliseconds
	 */
	public static void vibrate(int duration)
	{
		//#if polish.midp2
			StyleSheet.display.vibrate(duration);
		//#elif polish.api.nokia-ui
			try {
				com.nokia.mid.ui.DeviceControl.startVibra(80, duration);
			} catch (IllegalStateException e) {
				// no vibration support
			}
		//#elif polish.api.samsung
			com.samsung.util.Vibration.start(duration, 100);
		//#endif
	}
	
	/**
	 * Checks if vibration can be controlled by the application
	 * 
	 * @return true when the vibration can be controlled by the application
	 */
	public static boolean isVibrateSupported()
	{
		boolean isSupported = false;
		//#if polish.api.nokia-ui && !polish.api.midp2
			try {
				com.nokia.mid.ui.DeviceControl.startVibra(0, 1);
				isSupported = true;
			} catch (IllegalStateException e) {
				// no vibration support
			}
		//#elif polish.api.samsung
			isSupported = com.samsung.util.Vibration.isSupported();
		//#elif polish.midp2 && polish.usePolishGui
			isSupported = StyleSheet.display.vibrate(0);
		//#endif
		return isSupported;
	}

	
}
