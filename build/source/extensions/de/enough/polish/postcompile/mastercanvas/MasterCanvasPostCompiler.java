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
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;

import de.enough.bytecode.MethodInvocationMapping;
import de.enough.bytecode.MethodMapper;
import de.enough.polish.Device;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.util.FileUtil;

/**
 * <p>
 * Maps display.setCurrent(), display.getCurrent() and Canvas.repaint() on
 * MasterCanvas.
 * </p>
 * <p>
 * Copyright Enough Software 2005
 * </p>
 * 
 * <pre>
 *  history
 *         07-Jun-2005 - rob creation
 * </pre>
 * 
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class MasterCanvasPostCompiler extends PostCompiler
{
  /**
   * Creates a new post compiler
   */
  public MasterCanvasPostCompiler()
  {
    // Do nothing here.
  }

  /*
   * (non-Javadoc)
   * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File,
   *      de.enough.polish.Device)
   */
  public void postCompile(File classesDir, Device device) throws BuildException
  {
    String[] fileNames = FileUtil.filterDirectory(classesDir, ".class", true);
    ArrayList filesList = new ArrayList(fileNames.length);
    for (int i = 0; i < fileNames.length; i++)
      {
        String fileName = fileNames[i];
        if (!(fileName.endsWith("StyleSheet.class") || fileName.endsWith("MasterCanvas.class")
        // || fileName.endsWith("ScreenChangeAnimation.class")
        // || (fileName.indexOf("screenanimations") != -1)
        // || fileName.endsWith("Locale.class")
        // || fileName.endsWith("Debug.class")
        ))
          {
            filesList.add(new File(classesDir, fileName));
            // } else {
            // System.out.println("ScreenChanger: skipping class " + fileName);
          }
      }
    File[] files = (File[]) filesList.toArray(new File[filesList.size()]);

    try
      {
        System.out.println("MasterCanvas: mapping of Display.setCurrent() for "
                           + files.length + " class files.");
        boolean useDefaultPackage = this.environment.hasSymbol("polish.useDefaultPackage");
        String masterCanvasClassName;
        if (useDefaultPackage)
          {
            masterCanvasClassName = "MasterCanvas";
          }
        else
          {
            masterCanvasClassName = "de/enough/polish/ui/MasterCanvas";
          }

        MethodMapper mapper = new MethodMapper();
        mapper.setClassLoader(device.getClassLoader());

        boolean enableScreenEffects = this.environment.hasSymbol("polish.css.screen-change-animation")
                                      || this.environment.hasSymbol("polish.ScreenChangeAnimation.forward:defined");
        if (enableScreenEffects)
          {
            String styleSheetClass;
            if (useDefaultPackage)
              {
                styleSheetClass = "StyleSheet";
              }
            else
              {
                styleSheetClass = "de/enough/polish/ui/StyleSheet";
              }
            mapper.addMapping(new MethodInvocationMapping(true,
                                                          "javax/microedition/lcdui/Display",
                                                          "setCurrent",
                                                          "(Ljavax/microedition/lcdui/Displayable;)V",
                                                          false,
                                                          styleSheetClass,
                                                          "setCurrent",
                                                          "(Ljavax/microedition/lcdui/Display;Ljavax/microedition/lcdui/Displayable;)V"));

          }
        else
          {
            mapper.addMapping(new MethodInvocationMapping(true,
                                                          "javax/microedition/lcdui/Display",
                                                          "setCurrent",
                                                          "(Ljavax/microedition/lcdui/Displayable;)V",
                                                          false,
                                                          masterCanvasClassName,
                                                          "setCurrent",
                                                          "(Ljavax/microedition/lcdui/Display;Ljavax/microedition/lcdui/Displayable;)V"));
          }
        mapper.addMapping(new MethodInvocationMapping(true,
                                                      "javax/microedition/lcdui/Display",
                                                      "setCurrent",
                                                      "(Ljavax/microedition/lcdui/Alert;Ljavax/microedition/lcdui/Displayable;)V",
                                                      false,
                                                      masterCanvasClassName,
                                                      "setCurrent",
                                                      "(Ljavax/microedition/lcdui/Display;Ljavax/microedition/lcdui/Alert;Ljavax/microedition/lcdui/Displayable;)V"));
        mapper.addMapping(new MethodInvocationMapping(true,
                                                      "javax/microedition/lcdui/Display",
                                                      "getCurrent",
                                                      "()Ljavax/microedition/lcdui/Displayable;",
                                                      false,
                                                      masterCanvasClassName,
                                                      "getCurrent",
                                                      "(Ljavax/microedition/lcdui/Display;)Ljavax/microedition/lcdui/Displayable;"));

        /*
         String accessibleCanvasClassName;
         if ( useDefaultPackage ) {
         accessibleCanvasClassName = "AccessibleCanvas";
         } else {
         accessibleCanvasClassName = "de/enough/polish/ui/AccessibleCanvas";
         }
         */
        // mapping of isShown(), please note the different signature ()Z instead of ()V, since
        // a boolean value is returned:
        mapper.addMapping(new MethodInvocationMapping(true,
                                                      "javax/microedition/lcdui/Displayable",
                                                      "isShown", "()Z", false,
                                                      masterCanvasClassName,
                                                      "isDisplayableShown",
                                                      "(Ljavax/microedition/lcdui/Displayable;)Z"));

        // mapping of repaint():
        mapper.addMapping(new MethodInvocationMapping(true,
                                                      "javax/microedition/lcdui/Canvas",
                                                      "repaint", "()V", false,
                                                      masterCanvasClassName,
                                                      "repaintCanvas",
                                                      "(Ljavax/microedition/lcdui/Canvas;)V"));

        mapper.doMethodMapping(files);
        System.out.println("MasterCanvasPostCompiler finished.");
      }
    catch (Throwable e)
      {
        e.printStackTrace();
        throw new BuildException("Unable to map Display.setCurrent( Displayable ) to StyleSheet.setCurrent( Display, Displayable ): "
                                 + e.toString(), e);
      }
  }
}
