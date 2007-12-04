package de.enough.polish.ui.effect;

import de.enough.polish.ui.StyleSheet;

public class Vibration{
	public static void run(int duration)
	{
		//#if polish.midp2
			StyleSheet.display.vibrate(duration);
		//#endif
	}
}
