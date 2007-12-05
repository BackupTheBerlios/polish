package de.enough.polish.util;

//#if polish.usePolishGui
	import de.enough.polish.ui.StyleSheet;
//#endif

/**
 * 
 * <p>Controls backlight and vibration in an device-independent manner</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * @author Andre Schmidt
 * @author Robert Virkus
 */
public class DeviceControl
//#if polish.api.nokia-ui && !polish.Bugs.NoPermanentBacklight
	//#define tmp.useNokiaUi
//#endif
//#if (!tmp.useNokiaUi && polish.usePolishGui && polish.midp2) || polish.api.samsung-api
	//#define tmp.useThread
	extends Thread 
//#endif
{
	
	private static DeviceControl thread;
	private boolean lightOff = false; 
	
	private DeviceControl() {
		// disallow instantiation
	}
	
	public void run()
	{
		int displaytime = 10000;
		long sleeptime = ((displaytime >> 1) + (displaytime >> 2));
		
		while(!this.lightOff)
		{
			switchLightOnFor( displaytime );
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}
	
	private void switchLightOnFor( int durationInMs ) {
		//#if polish.api.samsung
			com.samsung.util.LCDLight.on(durationInMs);
		//#elif polish.api.nokia-ui
			//#if polish.Bugs.BacklightRequiresLightOff
				com.nokia.mid.ui.DeviceControl.setLights(0,0);
			//#endif
			com.nokia.mid.ui.DeviceControl.setLights(0,100);
		//#elif polish.midp2  && polish.usePolishGui
			StyleSheet.display.flashBacklight(durationInMs);
		//#endif
	}
	
	private void switchLightOff()
	{
		this.lightOff = true;
		//#if polish.api.samsung
			com.samsung.util.LCDLight.off();
		//#elif polish.midp2 && polish.usePolishGui
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
		boolean success = false;
		//#if tmp.useNokiaUi
			com.nokia.mid.ui.DeviceControl.setLights(0,100);
			success = true;
		//#elif tmp.useThread
			if (thread == null) {
				if (isLightSupported()) {
					DeviceControl dc = new DeviceControl();
					dc.start();
					success = true;
				}
			} else {
				success = true;
			}
		//#endif
		return success;
	}
	
	/**
	 * Turns the backlight off
	 *
	 */
	public static void lightOff()
	{
		//#if tmp.useNokiaUi
			com.nokia.mid.ui.DeviceControl.setLights(0,0);
		//#elif tmp.useThread
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
		//#if tmp.useNokiaUi
			isSupported = true;
		//#elif polish.api.samsung
			isSupported = com.samsung.util.LCDLight.isSupported();
		//#elif polish.midp2 && polish.usePolishGui
			isSupported = StyleSheet.display.flashBacklight(0);
		//#endif
		return isSupported;
	}

	
	

	/**
	 * Vibrates the device for the given duration
	 * 
	 * @param duration the duration in milliseconds
	 * @return true when the vibration was activated successfully
	 */
	public static boolean vibrate(int duration)
	{
		boolean success = false;
		//#if polish.midp2 && polish.usePolishGui
			success = StyleSheet.display.vibrate(duration);
		//#elif polish.api.nokia-ui
			try {
				com.nokia.mid.ui.DeviceControl.startVibra(80, duration);
				success = true;
			} catch (IllegalStateException e) {
				// no vibration support
			}
		//#elif polish.api.samsung
			com.samsung.util.Vibration.start(duration, 100);
			success = com.samsung.util.Vibration.isSupported();
		//#endif
		return success;
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
