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

import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
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
  private String classDesc;
  private String classArrayDesc;
  private String signature_values;
  
  public Java5ClassVisitor(ClassVisitor cv)
  {
    super(cv);
  }

  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
  {
    super.visit(version, access, name, signature, superName, interfaces);
    
    this.isEnumClass = EnumManager.getInstance().isEnumClass(name);
    this.className = name;
    this.classDesc = "L" + this.className + ";";
    this.classArrayDesc = "[" + this.classDesc;
    this.signature_values = "()[L" + this.className + ";"; 
  }

  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
  {
    // We keep only the values() method in enum classes.
    if (this.isEnumClass
        && ! "values".equals(name))
      {
        return null;
      }
   
    MethodVisitor mv = super.visitMethod(access, name, EnumManager.transform(desc), signature, exceptions);
    mv = new EnumMethodVisitor(mv, access, name, desc, signature, exceptions, Type.getType(this.classDesc));
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

  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
  {
    if (this.isEnumClass
        && this.classDesc.equals(desc))
      {
        String fieldName = this.classDesc + "." + name;
        desc = "I";
        value = EnumManager.getInstance().getEnumValue(fieldName);
        
        if (value == null)
          {
            throw new BuildException("Value for enum is null");
          }
      }
    
    if (this.isEnumClass
        && desc.equals(this.classArrayDesc))
      {
        desc = "[I";
      }
    
    return super.visitField(access, name, desc, signature, value);
  }
}
