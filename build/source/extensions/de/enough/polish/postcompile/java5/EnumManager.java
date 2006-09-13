/*
 * Created on Sep 13, 2006 at 12:10:06 AM.
 * 
 * Copyright (c) 2006 Michael Koch / Enough Software
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
package de.enough.polish.postcompile.java5;

import java.util.HashSet;
import java.util.Iterator;

public class EnumManager
{
  private static EnumManager singleton = new EnumManager();

  private HashSet enumClasses;
  private HashSet localVariables;
  
  private EnumManager()
  {
    this.enumClasses = new HashSet();
    this.localVariables = new HashSet();
  }

  public static EnumManager getInstance()
  {
    if (singleton == null)
      {
        singleton = new EnumManager();
      }

    return singleton;
  }
    
  public void addEnumClass(String className)
  {
    this.enumClasses.add(className);
  }
  
  public boolean isEnumClass(String className)
  {
    return (this.enumClasses.contains(className)
            || this.enumClasses.contains("L" + className + ";"));
  }

  public void addLocalVariable(LocalVariableInfo variable)
  {
    this.localVariables.add(variable);
  }
  
  public boolean contains(LocalVariableInfo variable)
  {
    return this.localVariables.contains(variable);
  }

  private String transform(String desc, String replace)
  {
    int pos;
  
    do
      {
        pos = desc.indexOf(replace);
      
        if (pos != -1)
          {
            StringBuffer sb = new StringBuffer(desc.substring(0, pos));
            sb.append("I");
            sb.append(desc.substring(pos + replace.length()));
            desc = sb.toString();
          }
        }
    while (pos != -1);

    return desc;
  }

  public static String transform(String desc)
  {
    EnumManager manager = EnumManager.getInstance();
    
    for (Iterator it = manager.enumClasses.iterator(); it.hasNext(); )
      {
        String className = (String) it.next();
      
        desc = manager.transform(desc, "L" + className + ";");
        desc = manager.transform(desc, className);
      }
  
    return desc;
  }
}
