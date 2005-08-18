/*
 * Created on 22-May-2005 at 23:43:59.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.finalize.blackberry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.ProcessUtil;
import de.enough.polish.util.OutputFilter;

/**
 * <p>Creates COD out of JAR files for RIM BlackBerry devices, also creates an ALX file for deployment using the BlackBerry Desktop Manager.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Stephen Johnson, original converter
 */
public class JarToCodFinalizer 
extends Finalizer
implements OutputFilter
{

	private boolean verbose;

	/**
	 * Creates a new JarToCodFinalizer
	 */
	public JarToCodFinalizer() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device,
			Locale locale, Environment env) 
	{
		String codName = jarFile.getName();
		codName = codName.substring( 0, codName.length() - ".jar".length() );
		String blackberryHome = env.getVariable("blackberry.home");
		if ( blackberryHome == null ) {
			throw new BuildException("You need to define the Ant property \"blackberry.home\" that points to the JDE directory.");
		}
		
		File rapcJarFile = env.resolveFile( "${blackberry.home}/bin/rapc.jar" );
		if ( !rapcJarFile.exists() ) {
			throw new BuildException("Your Ant property \"blackberry.home\" is invalid, it needs to point to the JDE directory (so that it finds the \"bin\\rapc.jar\" file within the ${blackberry.home} directory).");
		}
		
		// check if a MIDlet should be converted or whether a normal
		// blackberry application is used:
		String mainClassName = env.getVariable( "blackberry.main");
		boolean usePolishGui = env.hasSymbol( "polish.usePolishGui" );
		if (mainClassName == null && usePolishGui) {
			boolean useDefaultPackage = env.hasSymbol( "polish.useDefaultPackage" );
			if (useDefaultPackage) {
				mainClassName = "MIDlet";
			} else {
				mainClassName= "de.enough.polish.blackberry.midlet.MIDlet";
			}
		}
		if (mainClassName != null) {
			try {
				/*
				String[] entries = FileUtil.readTextFile( jadFile );
				String[] newEntries = new String[ entries.length + 1 ];
				System.arraycopy( entries, 0, newEntries, 0, entries.length );
				newEntries[ entries.length ] = "RIM-MIDlet-Flags-1: 0";
				*/
				String[] newEntries = new String[]{
						"MIDlet-Name: " + env.getVariable("MIDlet-Name"),
						"MIDlet-Version: 0.0",
						"MIDlet-Vendor: <unknown>",
						"MIDlet-Jar-URL: " + jarFile.getName(),
						"MIDlet-Jar-Size: 0",
						"MicroEdition-Profile: MIDP-2.0",
						"MicroEdition-Configuration: CLDC-1.1",
						"MIDlet-1: " + env.getVariable("MIDlet-Name") + ",,",
						"RIM-MIDlet-Flags-1: 0",
						""
				};

				File rapcFile = new File( jadFile.getParent(), codName + ".rapc");
				FileUtil.writeTextFile( rapcFile, newEntries );
			} catch ( IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!this.verbose) {
			this.verbose = "true".equals( env.getVariable("polish.blackberry.verbose") );
		}
		ArrayList commands = new ArrayList();
		try {
			// call the rapc compiler for converting the JAR to a COD file:
			commands.add( "java" );
			commands.add( "-cp" );
			commands.add( rapcJarFile.getAbsolutePath() );
			commands.add( "net.rim.tools.compiler.Compiler" );
			commands.add( "import=" +  blackberryHome + File.separatorChar + "lib" + File.separatorChar + "net_rim_api.jar" );
			commands.add(  "codename=" + codName );
			//commands.add( "-midlet" );
			if (mainClassName == null) {
				commands.add( "-midlet" );
				commands.add( "jad=" + jadFile.getName() );
			} else {
				commands.add( codName + ".rapc" );
			}
			commands.add( jarFile.getName() );
			System.out.println("rapc: Converting jar to cod for device [" + device.getIdentifier() + "]");
			/*
			Class compilerClass = Class.forName("net.rim.tools.compiler.Compiler");
			Method mainMethod = compilerClass.getMethod("main", new Class[]{ String[].class } );
			String[] args = (String[]) commands.toArray( new String[ commands.size() ]);
			try {
				mainMethod.invoke( null, new Object[]{ args } );
			} catch ( InvocationTargetException e ) {
				// the Compiler class calls System.exit() after the successfull 
			}
			*/
			File distDir = jarFile.getParentFile();
			int result = ProcessUtil.exec( commands, "rapc: ", true, this, distDir );
			
			if ( result != 0 ) {
				System.err.println("rapc-call: " + commands.toString() );
				throw new BuildException("rapc failed and returned " + result );
			}

			File csoFile = new File( distDir, codName + ".cso" );
			File debugFile = new File( distDir, codName + ".debug" );

			csoFile.delete();
			debugFile.delete();
			
			// now create an ALX file for deployment using the BlackBerry Desktop Manager:
			ArrayList lines = new ArrayList();
			lines.add( "<loader version=\"1.0\">");
			lines.add( "<application id=\"" + codName + "\">" );
			lines.add( "<name >" );
			lines.add( env.getVariable("MIDlet-Name") );
			lines.add( "</name>" );
			String value = env.getVariable("MIDlet-Description");
			if (value == null) {
				value = "An application build with J2ME Polish.";
			}
			lines.add( "<description >" );
			lines.add( value );
			lines.add( "</description>" );
			lines.add( "<version >" );
			lines.add( env.getVariable("MIDlet-Version") );
			lines.add( "</version>" );
			lines.add( "<vendor >" );
			lines.add( env.getVariable("MIDlet-Vendor") );
			lines.add( "</vendor>" );
			value = env.getVariable("MIDlet-Copyright");
			lines.add( "<copyright >" );
			if ( value != null ) {
				lines.add( value );
			} else {
				lines.add( "Copyright (c) " + Calendar.getInstance().get( Calendar.YEAR ) + " " +  env.getVariable("MIDlet-Vendor") );
			}
			lines.add( "</copyright>" );
			lines.add( "<fileset Java=\"1.0\">" );
			lines.add( "<directory >" );
			lines.add( "</directory>" );
			lines.add( "<files >"  );
			lines.add( codName + ".cod" );
			lines.add( "</files>" );
			lines.add( "</fileset>" );
			lines.add( "</application>" );			
			lines.add( "</loader>");
			File alxFile = new File( jarFile.getParentFile(),  codName  + ".alx" );
			FileUtil.writeTextFile(alxFile, lines);
		} catch (BuildException e) {
			throw e;
		} catch (Exception e){
		    e.printStackTrace();
			System.err.println("rapc-call: " + commands.toString() );
		    throw new BuildException("rapc was unable to to transform JAR file: " + e.toString() );
		}
    }
	
	public void setVerbose( boolean verbose ) {
		this.verbose = verbose;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.OutputFilter#filter(java.lang.String, java.io.PrintStream)
	 */
	public void filter(String message, PrintStream output) {
		if ( this.verbose 
				|| 
				(message.indexOf("Parsing") == -1 
				&& message.indexOf("Warning!") == -1
				&& message.indexOf("Reading ") == -1
				&& message.indexOf(".class:") == -1
				&& message.indexOf("Duplicate method only") == -1
				&& message.indexOf("not required") == -1
				) ) 
		{
			//output.println( message + this.verbose + message.indexOf("Warning!") );
			output.println( message );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.Extension#initialize(de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void initialize( Device device, Locale locale, Environment env ) {
		String mainClassName = env.getVariable( "blackberry.main");
		boolean usePolishGui = env.hasSymbol( "polish.usePolishGui" );
		if (mainClassName == null && usePolishGui) {
			boolean useDefaultPackage = env.hasSymbol( "polish.useDefaultPackage" );
			if (useDefaultPackage) {
				mainClassName = "MIDlet";
			} else {
				mainClassName= "de.enough.polish.blackberry.midlet.MIDlet";
			}
		}
		if (mainClassName != null) {
			env.addVariable( "polish.classes.main", mainClassName );
		}
	}
	
	

}
