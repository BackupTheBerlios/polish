package de.enough.polish.postcompile.java5;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class IteratorMethodVisitor
    extends MethodAdapter
    implements Opcodes
{
  private static final String CLASS_VECTOR = "java/util/Vector";
  private static final String CLASS_ITERABLEMETHODS = "com/rc/retroweaver/runtime/IterableMethods";
  private static final String CLASS_ITERATORUTIL = "de/enough/polish/util/IteratorUtil";
  private static final String METHOD_ITERATOR = "iterator";
  private static final String SIGNATURE_ITERATOR = "()Lde/enough/polish/util/Iterator;";
  private static final String SIGNATURE_ITERATOR_STATIC = "(Ljava/lang/Object;)Lde/enough/polish/util/Iterator;";

  public IteratorMethodVisitor(MethodVisitor mv)
  {
    super(mv);
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc)
  {
    if (INVOKEVIRTUAL == opcode
        && CLASS_VECTOR.equals(owner)
        && METHOD_ITERATOR.equals(name)
        && SIGNATURE_ITERATOR.equals(desc))
      {
        super.visitMethodInsn(INVOKESTATIC, CLASS_ITERATORUTIL, METHOD_ITERATOR, SIGNATURE_ITERATOR_STATIC);
        return;
      }
    
    if (INVOKESTATIC == opcode
        && CLASS_ITERABLEMETHODS.equals(owner)
        && METHOD_ITERATOR.equals(name)
        && SIGNATURE_ITERATOR_STATIC.equals(desc))
      {
        super.visitMethodInsn(INVOKESTATIC, CLASS_ITERATORUTIL, METHOD_ITERATOR, SIGNATURE_ITERATOR_STATIC);
        return;
      }

    super.visitMethodInsn(opcode, owner, name, desc);
  }
}
