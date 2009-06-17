package de.enough.polish.util;

import java.io.File;

public class OsUtil {

	/**
		 * Indicates if the system is running Windows/Vista as OS 
		 * @return true, if the system is running Windows/Vista, otherwise false
		 */
		public static boolean isRunningWindows()
		{
			return (File.separatorChar == '\\' );
	//		String os = System.getProperty("os.name").toLowerCase();
	//		return (os.indexOf("windows") != -1 || os.indexOf("vista") != -1);
		}

}
