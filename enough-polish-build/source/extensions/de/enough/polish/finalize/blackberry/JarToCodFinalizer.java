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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.jar.JarPackager;
import de.enough.polish.jar.Packager;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.JarUtil;
import de.enough.polish.util.ProcessUtil;
import de.enough.polish.util.OutputFilter;
import de.enough.polish.util.StringUtil;

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
		File blackberryHomeDir;
		if ( blackberryHome == null ) {
			blackberryHomeDir = new File( "C:\\Program Files\\Research In Motion");
			if (blackberryHomeDir.exists()) {
				blackberryHome = blackberryHomeDir.getAbsolutePath();
			} else {
				throw new BuildException("You need to define the Ant property \"blackberry.home\" that points to the JDE directory.");
			}
		} else {
			blackberryHomeDir = new File( blackberryHome );
			if (!blackberryHomeDir.exists()) {
				throw new BuildException("Your Ant property \"blackberry.home\" points to the non existing location "  + blackberryHome + ".");
			} else if (!blackberryHomeDir.isDirectory()) {
				throw new BuildException("Your Ant property \"blackberry.home\" points not to a directory, but a file:"  + blackberryHome );
			}
		}
		
		
		File rapcJarFile = new File( blackberryHomeDir, "/bin/rapc.jar" );
		if ( !rapcJarFile.exists() ) {
			// try to sort out the correct JDE automatically:
			File[] files = blackberryHomeDir.listFiles();
			boolean requires41JDE = !device.hasFeature("polish.hasTrackballEvents");
			Arrays.sort( files );
			for (int i = files.length -1; i >= 0; i--) {
				File file = files[i];
				if (file.isDirectory() && file.getName().indexOf("JDE") != -1) {
					if (!requires41JDE || file.getName().indexOf("4.1") != -1) {
						blackberryHomeDir = file;
						blackberryHome = file.getAbsolutePath();
						rapcJarFile = new File( blackberryHomeDir, "/bin/rapc.jar" );
						if (rapcJarFile.exists()) {
							System.out.println("Using blackberry.home " + blackberryHome);
							break;
						}
					}
				}
			}
			
			if ( !rapcJarFile.exists() ) {
				throw new BuildException("Your Ant property \"blackberry.home\" [" + blackberryHome + "] contains no JDE.");
			}
		}
		
		// check if a MIDlet should be converted or whether a normal
		// blackberry application is used:
		String mainClassName = env.getVariable( "blackberry.main");
		boolean usePolishGui = env.hasSymbol( "polish.usePolishGui" );
		if (mainClassName == null && usePolishGui) {
			// repackage the JAR file:
			File classesDir = new File( device.getClassesDir() );
			boolean useDefaultPackage = env.hasSymbol( "polish.useDefaultPackage" );
			if (useDefaultPackage) {
				mainClassName = "MIDlet";
			} else {
				mainClassName= "de.enough.polish.blackberry.midlet.MIDlet";
			}
			// add JAD file to JAR, so that MIDlet.getAppProperty() works later onwards:
			try {
				File txtJadFile = new File( classesDir, jadFile.getName().substring( 0, jadFile.getName().length() - ".jad".length() ) + ".txt");
				FileUtil.copy( jadFile, txtJadFile );
				FileUtil.delete(jarFile);
				
				Packager packager = (Packager) env.get( Packager.KEY_ENVIRONMENT );
				//System.out.println("Using packager " + packager.getClass().getName() );
				packager.createPackage( classesDir, jarFile, device, locale, env );
				//JarUtil.addToJar(txtJadFile, jarFile, null, true );
			} catch (IOException e) {
				e.printStackTrace();
				throw new BuildException("Unable to store JAD file in BlackBerry JAR file: " + e.toString() );
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
				String iconUrl = env.getVariable("MIDlet-Icon");
				if (iconUrl != null && iconUrl.length() > 1) {
					//iconUrl = iconUrl.substring( 1 );
					// add absolute path for the icon, so that stupid rapcs build before 4.2 can find it:
					iconUrl = device.getClassesDir() + iconUrl;
					System.out.println("using icon path " + iconUrl );
				} else {
					iconUrl = "";
				}
				String[] newEntries = new String[]{
						"MIDlet-Name: " + env.getVariable("MIDlet-Name"),
						//"MIDlet-Name: demo",
						"MIDlet-Version: " + env.getVariable("MIDlet-Version"),
						//"MIDlet-Version: 1.0",
						"MIDlet-Vendor: " + env.getVariable("MIDlet-Vendor"),
						"MIDlet-Jar-URL: " + jarFile.getName(),
						"MIDlet-Jar-Size: " + jarFile.length(),
						//"MIDlet-Jar-Size: 0",
						"MicroEdition-Profile: MIDP-2.0",
						"MicroEdition-Configuration: CLDC-1.1",
						//"MIDlet-1: Demo," + iconUrl + ",",
						"MIDlet-1: " + env.getVariable("MIDlet-Name") + "," + iconUrl + ",",
						//"MIDlet-Icon: " + iconUrl,
						"RIM-MIDlet-Flags-1: 0"
				};

				File rapcFile = new File( jadFile.getParent(), codName + ".rapc");
				FileUtil.writeTextFile( rapcFile, newEntries );
			} catch ( IOException e ) {
				// this shouldn't happen
				e.printStackTrace();
			}
		}
		if (!this.verbose) {
			this.verbose = "true".equals( env.getVariable("polish.blackberry.verbose") );
		}
		// delete existing COD file to force a clean rebuild of the COD:
		File codFile = new File( jadFile.getParent(), codName + ".cod");
		if (codFile.exists()) {
			codFile.delete();
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
//			Object[] args = commands.toArray();
//			StringBuffer argsBuffer = new StringBuffer();
//			for (int i = 0; i < args.length; i++) {
//				Object object = args[i];
//				argsBuffer.append( object ).append(" ");
//			}
//			System.out.println("Call to rapc: " + argsBuffer.toString() );
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
			
			// now rewrite JAD file so that it is ready for OTA download:
			Map jadProperties = FileUtil.readPropertiesFile( jadFile, ':' );	
			Object[] keys = jadProperties.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				String key = (String) keys[i];
				if (key.endsWith("URL")) {
					value = (String) jadProperties.get(key);
					value = StringUtil.replace(value, '$', '%');
					jadProperties.put( key, value );
				}
				if (key.startsWith("RIM") && key.charAt( key.length() - 2) == '-') {
					jadProperties.remove(key);
				}
			}
			FileUtil.writePropertiesFile(jadFile, ':', jadProperties );
			
			// request signature when the "blackberry.certificate.dir" variable is set:
			if ( env.getVariable("blackberry.certificate.dir") != null) {
				SignatureRequester requester = new SignatureRequester();
				try {
					int signResult = requester.requestSignature(device, locale, new File( blackberryHome ), codFile, env);
					if (signResult != 0) {
						System.out.println("Unable to request BlackBerry signature: Signing request returned " + signResult );
					}
				} catch (Exception e) {
				    throw new BuildException("Unable to to request BlackBerry signature: " + e.toString() );
				}
			}
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
	
	
	/**
	 * Can be used to invoke the JAR-2-COD converter without using Ant.
	 * Please specify following System properties by using -D[name]=[value] command line parameters:
	 * polish.home: installation folder of J2ME Polish
	 * blackberry.home: installation folder for JDE or blackberry component packages
	 * device: name of the device, e.g. BlackBerry/8100
	 * jad: jad file that should be used
	 * tmp: optional location of the tempory folder used for the conversion
	 * 
	 * The application exits with 0 when everything works fine.
	 * 1 indicates a missing mandatory parameter,
	 * 2 a parameter that is not valid,
	 * 3 a problem during the conversion.
	 * 
	 * @param args Arguments, see above
	 */
	public static void main(String[] args) {
		// read settings:
		File polishHome = getFile("polish.home");
		File jad = getFile("jad");
		File blackberryHome = getFile("blackberry.home");
		String deviceName = getString("device");
		String tmp = System.getProperty("tmp");
		if (tmp == null) {
			tmp = "./tmp";
		}
		File tmpDir = new File( tmp );
		if (tmpDir.exists()) {
			FileUtil.delete(tmpDir);
		}
		tmpDir.mkdirs();
		
		// create device database:
		DeviceDatabase db = new DeviceDatabase( polishHome );
		Device device = db.getDevice( deviceName );
		if (device == null) {
			System.err.println("Unknown device: \"" + deviceName + "\" - please check your device system environment variable" );
			System.exit(2);
		}
		
		Map jadProperties = null;
		try {
			jadProperties = FileUtil.readPropertiesFile( jad, ':' );
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to read jad file " + jad.getAbsolutePath() );
			System.exit(2);
		}
		
		// extract JAR to temp folder:
		String jarUrl = (String) jadProperties.get("MIDlet-Jar-URL");
		if (jarUrl == null) {
			System.err.println("no \"MIDlet-Jar-URL\" property in JAD file \"" + jad.getAbsolutePath() + "\" found." );
			System.exit(2);
		}
		File jar = new File( jad.getParentFile(), jarUrl.trim() );
		
		try {
			JarUtil.unjar(jar, tmpDir);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Unable to extract referenced JAR file " + jar.getAbsolutePath() );
			System.exit(3);
		}
		
		device.setClassesDir(tmp);

		
		
		Environment env = new Environment(polishHome);
		env.addVariable("polish.home", polishHome.getAbsolutePath() );
		env.addVariable("blackberry.home", blackberryHome.getAbsolutePath() );
		File midletClassFile = new File( tmp, "MIDlet.class");
		if (midletClassFile.exists()) 
		{
			env.addSymbol("polish.useDefaultPackage");
		}
		if (midletClassFile.exists() ||  (new File(tmp, "de/enough/polish/blackberry/midlet/MIDlet").exists() ))
		{
			env.addSymbol( "polish.usePolishGui");
		}
		Object[] keys = jadProperties.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			String key = (String) keys[i];
			String value = (String) jadProperties.get(key);
			env.addVariable(key, value);
		}
		env.set( Packager.KEY_ENVIRONMENT, new JarPackager() );
		
		// now convert the JAD/JAR to a JAD/COD:
		JarToCodFinalizer finalizer = new JarToCodFinalizer();
		finalizer.finalize(jad, jar, device, null, env);

		
		System.exit(0);
	}
	
	private static String getString(String name) {
		String value = System.getProperty(name);
		if (value == null) {
			System.err.println("Missing system environment parameter: " + name );
			printUsageInfo();
			System.exit(1);
		}
		return value;
	}

	/**
	 * @param name
	 * @param value
	 */
	private static File getFile(String name) {
		File file = new File(getString(name));
		if (!file.exists()) {
			System.err.println("System environment parameter \"" + name + "\" does not point to an existing folder" );
			printUsageInfo();
			System.exit(2);
		}
		return file;
	}

	
	private static void printUsageInfo() {
		System.out.println("Usage: please specify all parameters as environment variables using the -D[name]=[value] switch.");
		System.out.println("Mandatory environment variables:");
		System.out.println("polish.home: installation folder of J2ME Polish");
		System.out.println("blackberry.home: installation folder for JDE or blackberry component packages");
		System.out.println("device: name of the device, e.g. BlackBerry/8100");
		System.out.println("jad: jad file that should be used");
		System.out.println("Optional environment variables:");
		System.out.println("tmp: optional location of the tempory folder used for the conversion [default is ./tmp]");
	}
	

}
