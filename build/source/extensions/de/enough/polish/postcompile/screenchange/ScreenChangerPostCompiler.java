/*
 * Created on 18-May-2005 at 15:44:27.
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
package de.enough.polish.postcompile.screenchange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;

import de.enough.bytecode.MethodInvocationMapping;
import de.enough.bytecode.MethodMapper;
import de.enough.polish.Device;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.util.FileUtil;

/**
 * <p>Moves calls to Display.setCurrent( Displayable ) to StyleSheet.setCurrent( Display, Displayable ) when screen change effects are activated.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        18-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ScreenChangerPostCompiler extends PostCompiler {

	/**
	 * Creates a new screen changer post compiler.
	 */
	public ScreenChangerPostCompiler() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
	 */
	public void postCompile(File classesDir, Device device)
	throws BuildException 
	{
		boolean enableScreenEffects = this.environment.hasSymbol("polish.css.screen-change-animation");
		if (!enableScreenEffects) {
			return;
		}
		String[] fileNames = FileUtil.filterDirectory( classesDir, ".class", true );
		ArrayList filesList = new ArrayList( fileNames.length );
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			if (!(fileName.endsWith("StyleSheet.class") 
					|| fileName.endsWith("MasterCanvas.class")
					|| fileName.endsWith("ScreenChangeAnimation.class") 
					|| (fileName.indexOf("screenanimations") != -1) )) 
			{
				filesList.add( new File( classesDir, fileName ) );
			//} else {
			//	System.out.println("ScreenChanger: skipping class " + fileName);
			}
		}
		File[] files = (File[]) filesList.toArray( new File[ filesList.size() ] );
		try {
			System.out.println("mapping of Display.setCurrent() for " + files.length + " class files.");
			String targetClassName;
			if (this.environment.hasSymbol("polish.useDefaultPackage")) {
				targetClassName = "StyleSheet";
			} else {
				targetClassName = "de/enough/polish/ui/StyleSheet";
			}
			MethodMapper mapper = new MethodMapper();
			mapper.setClassLoader(device.getClassLoader());
			
			// Note: This code is duplicated in MasterCanvasPostCompiler.
			mapper.addMapping(
				new MethodInvocationMapping(true, "javax/microedition/lcdui/Display", "setCurrent",
											"(Ljavax/microedition/lcdui/Displayable;)V",
											false, targetClassName, "setCurrent",
											"(Ljavax/microedition/lcdui/Display;Ljavax/microedition/lcdui/Displayable;)V")
			);
			
			mapper.doMethodMapping( files );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to map Display.setCurrent( Displayable ) to StyleSheet.setCurrent( Display, Displayable ): " + e.toString(), e );
		}
	}

}
