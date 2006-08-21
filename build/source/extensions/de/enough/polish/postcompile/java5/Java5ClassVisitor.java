package de.enough.polish.postcompile.java5;

import java.util.List;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
    mv = new ThrowableMethodVisitor(mv);
    mv = new IteratorMethodVisitor(mv);
    
    if (this.isEnumClass
        && METHOD_VALUES.equals(name)
        && this.signature_values.equals(desc))
      {
        return new CloneMethodVisitor(mv, this.className);
      }
    
    return mv;
  }
}
