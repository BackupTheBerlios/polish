package de.enough.polish.postcompile.java5;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ThrowableMethodVisitor
    extends MethodAdapter
    implements Opcodes
{
  private static final String METHOD_INIT_CAUSE = "initCause";
  private static final String SIGNATURE_INIT_CAUSE = "(Ljava/lang/Throwable;)Ljava/lang/Throwable;";
  
  public ThrowableMethodVisitor(MethodVisitor mv)
  {
    super(mv);
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    if (INVOKEVIRTUAL == opcode
        && METHOD_INIT_CAUSE.equals(name)
        && SIGNATURE_INIT_CAUSE.equals(desc))
      {
        this.mv.visitInsn(POP);
        return;
      }

    super.visitMethodInsn(opcode, owner, name, desc);
  }
}
