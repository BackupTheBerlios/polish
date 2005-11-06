/*
 * Created on 18-Aug-2005 at 15:58:25.
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
package de.enough.polish.precompile.blackberry;

import java.io.File;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.precompile.PreCompiler;
import de.enough.polish.util.FileUtil;

/**
 * <p>Maps imports from javax.microedition.lcdui.* to de.enough.polish.blackberry.ui.*</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        18-Aug-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImportResolver extends PreCompiler {

	public ImportResolver() {
		super();
	}

	public void preCompile(File classesDir, Device device)
	throws BuildException 
	{
		//System.out.println("ImportResolver.preCompile(): start");
		String[] fileNames = FileUtil.filterDirectory( classesDir, ".class", true );
		File[] files = new File[ fileNames.length ];
		for (int i = 0; i < fileNames.length; i++) {
			String name = fileNames[i];
			files[i] = new File( classesDir, name );
			//TODO man-di: implement moving of classes
		}

	}

}
