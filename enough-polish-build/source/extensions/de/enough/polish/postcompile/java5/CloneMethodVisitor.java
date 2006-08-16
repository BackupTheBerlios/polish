package de.enough.polish.postcompile.java5;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CloneMethodVisitor
    extends MethodAdapter
    implements Opcodes
{
  private String enumClassName;
  private String enumArrayDesc;
  
  public CloneMethodVisitor(MethodVisitor mv, String enumClassName)
  {
    super(mv);
    
    this.enumClassName = enumClassName;
    this.enumArrayDesc = "[L" + enumClassName + ";";
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    // Rewrite TYPE[].clone() calles.
    if (INVOKEVIRTUAL == opcode
        && this.enumArrayDesc.equals(owner)
        && "clone".equals(name)
        && "()Ljava/lang/Object;".equals(desc))
      {
        this.mv.visitInsn(DUP);
        this.mv.visitInsn(ARRAYLENGTH);
        this.mv.visitInsn(DUP);
        this.mv.visitTypeInsn(ANEWARRAY, this.enumClassName);
        this.mv.visitInsn(DUP_X2);
        this.mv.visitInsn(SWAP);
        this.mv.visitInsn(ICONST_0);
        this.mv.visitInsn(DUP_X2);
        this.mv.visitInsn(SWAP);
        this.mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V");
        return;
      }

    super.visitMethodInsn(opcode, owner, name, desc);
  }
}
