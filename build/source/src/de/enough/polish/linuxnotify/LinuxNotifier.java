package de.enough.polish.linuxnotify;

/**
 * Helper for notifications on Linux.
 *
 * copyright 2009 Enough Software
 * @author Michael Koch
 */
public class LinuxNotifier {

	/**
	 * Publishes a notification
	 * @param title the title
	 * @param text the text
	 * @return true when the publishing succeeded
	 */
	public static boolean publish(String title, String text)
	{
		try {
			System.err.println("Michael: publish");
			String[] cmd = new String[] {"notify-send", "-t", "0", title, text };
			Runtime.getRuntime().exec(cmd);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Checks if notification framework is available
	 * @return true when notifications can be used
	 */
	public static boolean isNotifyAvailable() {
		String oSName = System.getProperty("os.name").toLowerCase();
		System.err.println("Michael: isAvailable: " + oSName);
		boolean isLinux = oSName.startsWith("linux");
		//TODO check if 'notify-send' command is really available. 
		return isLinux;
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("usage:");
			System.out.println("java de.enough.polish.growl.GrowlNotifier [title] [description]");
			System.exit(1);
		}
		if (isNotifyAvailable()) {
			publish( args[0], args[1]);
		}
		System.exit(0);
	}
}
