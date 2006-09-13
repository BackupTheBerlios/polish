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

import java.util.List;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class Java5ClassVisitor
    extends ClassAdapter
    implements Opcodes
{
  private static final String METHOD_VALUES = "values";
  
  private boolean isEnumClass;
  private String className;
  private String signature_values;
  // TODO: Use this to determine when to rewrite enum constants.
  private List enumClasses;
  
  public Java5ClassVisitor(ClassVisitor cv, List enumClasses)
  {
    super(cv);
    this.enumClasses = enumClasses;
  }

  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
  {
    super.visit(version, access, name, signature, superName, interfaces);
    
    this.isEnumClass = Java5PostCompiler.CLASS_ENUM.equals(superName);
    this.className = name;
    this.signature_values = "()[L" + this.className + ";"; 
  }

  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
  {
    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    mv = new ThrowableMethodVisitor(mv, access, name, desc);
    mv = new IteratorMethodVisitor(mv, access, name, desc);
    
    if (this.isEnumClass
        && METHOD_VALUES.equals(name)
        && this.signature_values.equals(desc))
      {
        mv = new CloneMethodVisitor(mv, access, name, desc,
                                    Type.getType("L" + this.className + ";"));
      }
    
    return mv;
  }
}
