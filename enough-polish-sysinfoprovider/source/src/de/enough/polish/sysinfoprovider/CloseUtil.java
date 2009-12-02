package de.enough.polish.sysinfoprovider;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtil
{
	public static void close(Closeable closeable)
	{
		try {
			if (closeable != null) {
				closeable.close();
			}
		}
		catch (IOException e) {
			// Ignore for now.
		}
	}
}
