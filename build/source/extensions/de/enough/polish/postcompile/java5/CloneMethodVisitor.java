package de.enough.polish.postcompile.java5;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class CloneMethodVisitor
    extends GeneratorAdapter
    implements Opcodes
{
  private static final Type TYPE_SYSTEM = Type.getType("Ljava/lang/System;");
  
  private static final Method METHOD_ARRAYCOPY =
    Method.getMethod("void arraycopy(java.lang.Object, int, java.lang.Object, int, int)");
  
  private static final Method METHOD_CLONE =
    Method.getMethod("java.lang.Object clone()");

  private Type enumType;
  private Type enumArrayDesc;
  
  
  public CloneMethodVisitor(MethodVisitor mv, int access, String name, String desc, Type enumType)
  {
    super(mv, access, name, desc);
    
    this.enumType = enumType;
    this.enumArrayDesc = Type.getType("[" + enumType.toString());
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    Method method = new Method(name, desc);

    // Rewrite TYPE[].clone() calles.
    if (INVOKEVIRTUAL == opcode
        && this.enumArrayDesc.getDescriptor().equals(owner)
        && METHOD_CLONE.equals(method))
      {
        dup();
        arrayLength();
        dup();
        newArray(Type.INT_TYPE);
        dupX2();
        swap();
        push(0);
        dupX2();
        swap();
        invokeStatic(TYPE_SYSTEM, METHOD_ARRAYCOPY);
        return;
      }

    super.visitMethodInsn(opcode, owner, name, desc);
  }
}
