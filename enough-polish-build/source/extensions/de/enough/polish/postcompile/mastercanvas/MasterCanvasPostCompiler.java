/*
 * Created on 07-Jun-2005 at 13:09:58.
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
package de.enough.polish.postcompile.mastercanvas;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;

import de.enough.bytecode.MethodInvocationMapping;
import de.enough.bytecode.MethodMapper;
import de.enough.polish.Device;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.util.FileUtil;

/**
 * <p>Maps display.setCurrent(), display.getCurrent() and Canvas.repaint() on MasterCanvas.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        07-Jun-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MasterCanvasPostCompiler extends PostCompiler
{
	/**
	 * Creates a new post compiler
	 */
	public MasterCanvasPostCompiler() {
		// Do nothing here.
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
	 */
	public void postCompile(File classesDir, Device device)
	throws BuildException 
	{
		String[] fileNames = FileUtil.filterDirectory( classesDir, ".class", true );
		File[] files = new File[ fileNames.length ]; 
		
		for (int i = 0; i < fileNames.length; i++)
		{
			String fileName = fileNames[i];
			files[i] = new File( classesDir, fileName );
		}
		
		try
		{
			System.out.println("MasterCanvas: mapping of Display.setCurrent() for " + files.length + " class files.");
			String targetClassPath;
			if (this.environment.hasSymbol("polish.useDefaultPackage")) {
				targetClassPath = "MasterCanvas";
			} else {
				targetClassPath = "de/enough/polish/ui/MasterCanvas";
			}
			
			MethodMapper mapper = new MethodMapper();
			mapper.setClassLoader(device.getClassLoader());
			
			// Note: The same mappings are done in ScreenChangerPostCompiler.
			mapper.addMapping(
				new MethodInvocationMapping(
											true, "javax/microedition/lcdui/Display", "setCurrent",
											"(Ljavax/microedition/lcdui/Displayable;)V",
											false, targetClassPath, "setCurrent",
											"(Ljavax/microedition/lcdui/Display;Ljavax/microedition/lcdui/Displayable;)V")
			);
			mapper.addMapping(
				new MethodInvocationMapping(
											true, "javax/microedition/lcdui/Display", "getCurrent",
											"()Ljavax/microedition/lcdui/Displayable;",
											false, targetClassPath, "getCurrent",
											"(Ljavax/microedition/lcdui/Display;)Ljavax/microedition/lcdui/Displayable;")
			);
			
			//TODO michael: add repaint() mapping here
			mapper.addMapping(
				new MethodInvocationMapping(
											true, "de/enough/polish/ui/AccessibleCanvas", "repaint",
											"()V",
											false, "de/enough/polish/ui/MasterCanvas", "repaintMasterCanvas",
											"(Lde/enough/polish/ui/AccessibleCanvas;)V"));
			
			mapper.doMethodMapping(files);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new BuildException("Unable to map Display.setCurrent( Displayable ) to StyleSheet.setCurrent( Display, Displayable ): " + e.toString(), e );
		}
	}
}
