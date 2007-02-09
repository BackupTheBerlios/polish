/*
 * Created on Feb 5, 2007 at 11:03:06 AM.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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
package de.enough.polish.postcompile.j2se;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.postcompile.PostCompiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JavaSEPostCompiler
  extends PostCompiler
{
  /* (non-Javadoc)
   * @see de.enough.polish.postcompile.PostCompiler#postCompile(java.io.File, de.enough.polish.Device)
   */
  public void postCompile(File classesDir, Device device) throws BuildException
  {
    byte[] buffer = new byte[1024 * 1024];
    
    try
    {
      String polishHome = device.getEnvironment().getVariable("polish.home");
      File fileIn = new File(polishHome, "lib/enough-j2mepolish-runtime.jar");
      JarFile jarFile = new JarFile(fileIn);
      Enumeration e = jarFile.entries();
      
      while (e.hasMoreElements())
      {
        JarEntry entry = (JarEntry) e.nextElement();

        if (!entry.isDirectory())
        {
          int bytesRead;
          InputStream in = jarFile.getInputStream(entry);
          File fileOut = new File(classesDir, entry.getName());
          File parentDir = fileOut.getParentFile();
          
          if (!parentDir.exists())
          {
            parentDir.mkdirs();
          }
          
          FileOutputStream out = new FileOutputStream(fileOut);
          
          while ((bytesRead = in.read(buffer)) >= 0)
          {
            out.write(buffer, 0, bytesRead);
          }
          
          out.close();
          in.close();
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
      
      BuildException be = new BuildException();
      be.initCause(e);
      throw be;
    }
  }
}
