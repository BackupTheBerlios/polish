/*
 * Created on May 15, 2007 at 7:35:21 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ant.build.Midlet;
import de.enough.polish.util.PathClassLoader;


/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 15, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Launcher {
	
	public Launcher() {
		// initialize
	}
	
	public Simulation loadSimulation( File jadOrJarFile, SimulationDevice device  ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, MIDletStateChangeException {
		Simulation simulation = new Simulation( device  );
		MIDlet midlet = loadMidlet( jadOrJarFile );
		midlet._callStartApp();
		return simulation;
	}
	
	public MIDlet loadMidlet( File jadOrJarFile ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Midlet[] midlets;
		if (jadOrJarFile.getName().endsWith(".jar")) {
			midlets = getMidlets( new JarFile( jadOrJarFile ));
		} else {
			Properties properties = new Properties();
			properties.load( new FileInputStream( jadOrJarFile ));
			midlets = getMidlets( properties );
			String jarUrl = properties.getProperty("MIDlet-Jar-URL");
			jadOrJarFile = new File( jadOrJarFile.getParentFile(), jarUrl );
			if (!jadOrJarFile.exists()) {
				// TOTO load midlet via HTTP
				throw new FileNotFoundException("Unable to find JAR file for " + jarUrl + ": unable to load " + jadOrJarFile.getAbsolutePath()  );
			}
		}
		PathClassLoader classLoader = new PathClassLoader( jadOrJarFile );
		Class midletClass = classLoader.findClass( midlets[0].getClassName());
		return (MIDlet) midletClass.newInstance();
	}

	/**
	 * @param jadOrJarFile
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static  Midlet[] getMidlets(File jadOrJarFile) throws FileNotFoundException, IOException {
 		if (jadOrJarFile.getName().endsWith(".jad")) {
 			// this is a JAD file:
 			return getMidlets( new FileInputStream( jadOrJarFile ));
 		} else {
 			// this must be a JAR, so load the properties from the manifest:
 			return getMidlets( new JarFile( jadOrJarFile ) );
 		}
	}

	public static Midlet[] getMidlets(JarFile jarFile ) throws IOException {
		return getMidlets( jarFile.getManifest() );
	}

	/**
	 * @param manifest
	 * @return
	 */
	public static Midlet[] getMidlets(Manifest manifest) {
		ArrayList<Midlet> midletsList = new ArrayList<Midlet>();
		int i = 1;
		Attributes attributes = manifest.getMainAttributes();
		while (true) {
			String definition = attributes.getValue ("MIDlet-" + i );
			if (definition == null) {
				break;
			}
			Midlet midlet = new Midlet( definition );
			midletsList.add(midlet);
			i++;
		}
		return midletsList.toArray( new Midlet[ midletsList.size() ] );
	}

	/**
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public static Midlet[] getMidlets(FileInputStream is) throws IOException {
		Properties properties = new Properties();
		properties.load( is );
		return getMidlets( properties );
	}

	/**
	 * @param properties
	 * @return
	 */
	public  static Midlet[] getMidlets(Properties properties) {
		ArrayList<Midlet> midletsList = new ArrayList<Midlet>();
		int i = 1;
		while (true) {
			String definition = properties.getProperty("MIDlet-" + i );
			if (definition == null) {
				break;
			}
			Midlet midlet = new Midlet( definition );
			midletsList.add(midlet);
			i++;
		}
		return midletsList.toArray( new Midlet[ midletsList.size() ] );
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		try {
		if (args.length > 0) {
			Midlet[] midlets = getMidlets( new File( args[0]));
			System.out.println("found " + midlets.length + " midlets:");
			for (int i = 0; i < midlets.length; i++) {
				Midlet midlet = midlets[i];
				System.out.println(midlet.getName() + ": " + midlet.getClassName() );
			}
		}
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		// TODO robertvirkus implement main

	}

}
