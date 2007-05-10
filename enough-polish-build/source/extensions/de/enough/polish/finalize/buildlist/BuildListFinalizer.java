package de.enough.polish.finalize.buildlist;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.util.FileUtil;

public class BuildListFinalizer
	extends Finalizer
{
	private static final String DEFAULT_OUTPUT_FILENAME = "dist/buildlist.txt";
	private File outputFile;
	
	public void setOutputFile( File outputFile ) {
		this.outputFile = outputFile;
	}
	

	public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env)
	{
		StringBuffer userAgents = new StringBuffer();
		String tmpUserAgents = device.getCapability("wap.userAgent");

		if (tmpUserAgents != null) {
			StringTokenizer st = new StringTokenizer(tmpUserAgents, "\u0001");
			
			while (st.hasMoreTokens()) {
				userAgents.append("\"");
				userAgents.append(st.nextToken());
				userAgents.append("\"");
				
				if (st.hasMoreTokens()) {
					userAgents.append(",");
				}
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append(device.getIdentifier());
		sb.append("\t");
		sb.append(locale);
		sb.append("\t");
		sb.append(jarFile.getName());
		sb.append("\t");
		sb.append(jadFile.getName());
		sb.append("\t");
		sb.append(userAgents);
		sb.append("\r\n");

		try {
			if (this.outputFile == null) {
				this.outputFile = new File( env.getBaseDir(), DEFAULT_OUTPUT_FILENAME );
			}
			FileUtil.addLine( this.outputFile, sb.toString() );
		}
		catch (IOException e) {
			throw new BuildException(e);
		}
	}
}
