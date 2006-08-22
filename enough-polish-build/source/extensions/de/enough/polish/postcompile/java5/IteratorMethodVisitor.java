package de.enough.polish.postcompile.java5;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class IteratorMethodVisitor
    extends GeneratorAdapter
    implements Opcodes
{
  private static final Type TYPE_VECTOR =
    Type.getType("Ljava/util/Vector;");
  
  private static final Type TYPE_ITERABLEMETHODS =
    Type.getType("Lcom/rc/retroweaver/runtime/IterableMethods;");
  
  private static final Type TYPE_ITERATORUTIL =
    Type.getType("Lde/enough/polishutil/IteratorUtil;");
  
  private static final Method METHOD_ITERATOR =
    Method.getMethod("de.enough.polish.util.Iterator iterator()");

  private static final Method METHOD_ITERATORUTIL =
    Method.getMethod("de.enough.polish.util.Iterator iterator(java.lang.Object)");

  public IteratorMethodVisitor(MethodVisitor mv, int access, String name, String desc)
  {
    super(mv, access, name, desc);
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    Type type = Type.getType("L" + owner + ";");
    Method method = new Method(name, desc);
    
    if (INVOKEVIRTUAL == opcode
        && TYPE_VECTOR.equals(type)
        && METHOD_ITERATOR.equals(method))
      {
        invokeStatic(TYPE_ITERATORUTIL, METHOD_ITERATORUTIL);
        return;
      }
    
    if (INVOKESTATIC == opcode
        && TYPE_ITERABLEMETHODS.equals(type)
        && METHOD_ITERATORUTIL.equals(method))
      {
        invokeStatic(TYPE_ITERATORUTIL, METHOD_ITERATORUTIL);
        return;
      }

    super.visitMethodInsn(opcode, owner, name, desc);
  }
}
