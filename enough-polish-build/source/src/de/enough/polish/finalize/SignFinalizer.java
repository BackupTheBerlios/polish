/*
 * Created on 28-Apr-2005 at 13:31:31.
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
package de.enough.polish.finalize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.build.SignSetting;
import de.enough.polish.util.ProcessUtil;

/**
 * <p>Signs MIDlets</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        28-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SignFinalizer extends Finalizer {

	/**
	 * Creates a new signing finalizer.
	 */
	public SignFinalizer() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.finalize.Finalizer#finalize(java.io.File, java.io.File, de.enough.polish.Device, java.util.Locale, de.enough.polish.Environment)
	 */
	public void finalize(File jadFile, File jarFile, Device device,
			Locale locale, Environment environment) 
	{
		// locate the JadUtil:
		File jadUtil = environment.resolveFile("${wtk.home}/bin/JadTool.jar");
		if (! jadUtil.exists() ) {
			jadUtil = environment.resolveFile("${polish.home}/bin/JadTool.jar");
			if (!jadUtil.exists()) {
				throw new BuildException("Unable to sign application: neither ${wtk.home}/bin/JadUtil.jar nor ${polish.home}/bin/JadUtil.jar was found.");
			}
		}
		ArrayList parametersList = new ArrayList();
		SignSetting setting = (SignSetting) getExtensionSetting();
		// sign the JAR file:
		parametersList.add( "java" );
		parametersList.add( "-jar" );
		parametersList.add( jadUtil.getAbsolutePath() );
		parametersList.add( "-addjarsig" );
		parametersList.add( "-keypass" );
		parametersList.add( setting.getPassword() );
		parametersList.add( "-alias" );
		parametersList.add( setting.getKey() );
		parametersList.add( "-keystore" );
		parametersList.add( setting.getKeystore().getAbsolutePath() );
		parametersList.add( "-inputjad" );
		parametersList.add( jadFile.getAbsolutePath() );
		parametersList.add( "-outputjad" );
		parametersList.add( jadFile.getAbsolutePath() );
		parametersList.add( "-jarfile" );
		parametersList.add( jarFile.getAbsolutePath() );
		System.out.println( "Adding signature for " + device.getIdentifier() );
		String[] parameters = (String[]) parametersList.toArray( new String[ parametersList.size() ] );
		try {
			int result = ProcessUtil.exec( parameters, "sign: ", true );
			if (result != 0) {
				throw new BuildException("Unable to sign application: unable to execute JadUtil - JadUtil returned [" + result + "]." );
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to sign application: unable to execute JadUtil: " + e.toString() );
		}
		// add the certificate chain:
		parametersList.clear();
		parametersList.add( "java" );
		parametersList.add( "-jar" );
		parametersList.add( jadUtil.getAbsolutePath() );
		parametersList.add( "-addcert" );
		parametersList.add( "-alias" );
		parametersList.add( setting.getKey() );
		parametersList.add( "-keystore" );
		parametersList.add( setting.getKeystore().getAbsolutePath() );
		parametersList.add( "-inputjad" );
		parametersList.add( jadFile.getAbsolutePath() );
		parametersList.add( "-outputjad" );
		parametersList.add( jadFile.getAbsolutePath() );
		System.out.println( "Adding certificate for " + device.getIdentifier() );
		parameters = (String[]) parametersList.toArray( new String[ parametersList.size() ] );
		try {
			int result = ProcessUtil.exec( parameters, "add certificate: ", true );
			if (result != 0) {
				throw new BuildException("Unable to sign application: unable to execute JadUtil - JadUtil returned [" + result + "]." );
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to sign application: unable to execute JadUtil: " + e.toString() );
		}
	}

}
