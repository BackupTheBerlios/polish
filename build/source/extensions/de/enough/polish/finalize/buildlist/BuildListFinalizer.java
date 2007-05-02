package de.enough.polish.finalize.buildlist;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class BuildListFinalizer
	extends Finalizer
{
	private static final String DEFAULT_OUTPUT_FILENAME = "dist/buildlist.txt";

	public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(device.getIdentifier());
		sb.append("\t");
		sb.append(locale);
		sb.append("\t");
		sb.append(jarFile);
		sb.append("\t");
		sb.append(jadFile);
		sb.append("\r\n");

		try {
			FileOutputStream fileOut = new FileOutputStream(DEFAULT_OUTPUT_FILENAME, true);
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(fileOut));

			out.write(sb.toString().getBytes());

			out.close();
			fileOut.close();
		}
		catch (IOException e) {
			throw new BuildException(e);
		}
	}
}
