package de.enough.polish.postcompile.io;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import de.enough.bytecode.ASMClassLoader;
import de.enough.bytecode.PrimitiveTypesHelper;
import de.enough.polish.Environment;

public class SerializationVisitor
  extends ClassAdapter
  implements Opcodes
{
  private static final String POLISH_USE_DEFAULT_PACKAGE = "polish.useDefaultPackage";

  private static final String SERIALIZABLE = "de/enough/polish/io/Serializable";
  private static final String EXTERNALIZABLE = "de/enough/polish/io/Externalizable";
  private static final String DATAINPUTSTREAM = "java/io/DataInputStream";
  private static final String DATAOUTPUTSTREAM = "java/io/DataOutputStream";

  public static int READ_MODIFIERS = Opcodes.ACC_PUBLIC;
  public static final String READ_NAME = "read";
  public static final String READ_SIGNATURE = "(Ljava/io/DataInputStream;)V";

  public static int WRITE_MODIFIERS = Opcodes.ACC_PUBLIC;
  public static final String WRITE_NAME = "write";
  public static final String WRITE_SIGNATURE = "(Ljava/io/DataOutputStream;)V";

  private static final HashMap methodNamesRead;
  private static final HashMap methodNamesWrite;
  
  static
  {
    methodNamesRead = new HashMap();
    methodNamesRead.put("C", "readChar()C");
    methodNamesRead.put("D", "readDouble()D");
    methodNamesRead.put("F", "readFloat()F");
    methodNamesRead.put("I", "readInt()I");
    methodNamesRead.put("J", "readLong()J");
    methodNamesRead.put("B", "readByte()B");
    methodNamesRead.put("S", "readShort()S");
    methodNamesRead.put("Z", "readBoolean()Z");
    methodNamesRead.put("Ljava/lang/String;", "readUTF()Ljava/lang/String;");

    methodNamesWrite = new HashMap();
    methodNamesWrite.put("C", "writeChar(I)V");
    methodNamesWrite.put("D", "writeDouble(D)V");
    methodNamesWrite.put("F", "writeFloat(F)V");
    methodNamesWrite.put("I", "writeInt(I)V");
    methodNamesWrite.put("J", "writeLong(J)V");
    methodNamesWrite.put("B", "write(I)V");
    methodNamesWrite.put("S", "writeShort(I)V");
    methodNamesWrite.put("Z", "writeBoolean(Z)V");
    methodNamesWrite.put("Ljava/lang/String;", "writeUTF(Ljava/lang/String;)V");
  }

  public static String getEnvironmentClassName(String className, Environment env)
  {
    if (env != null && env.hasSymbol(POLISH_USE_DEFAULT_PACKAGE))
      {
        int index = className.lastIndexOf('/') + 1;
        return className.substring(index);
      }

    return className;
  }

  private String className;
  private String superClassName;
  private ASMClassLoader loader;
  private Environment environment; 
  private HashMap fields;
  private boolean generateDefaultConstructor = true;
  private boolean generateReadMethod = true;
  private boolean generateWriteMethod = true;

  public SerializationVisitor(ClassVisitor cv, ASMClassLoader loader,
                              Environment environment)
  {
    super(cv);
    this.loader = loader;
    this.environment = environment;
    this.fields = new HashMap();
  }

  private String[] getSortedFields()
  {
    Iterator it = this.fields.keySet().iterator();
    String[] result = new String[this.fields.size()];

    for (int i = 0; it.hasNext(); i++)
      {
        result[i] = (String) it.next();
      }
    
    Arrays.sort(result, new Comparator() {
      public int compare(Object o1, Object o2)
      {
        String s1 = (String) o1;
        String s2 = (String) o2;
        return s1.compareTo(s2);
      }
    });
    return result;
  }

  private String[] rewriteInterfaces(String[] interfaces)
  {
    boolean found = false;
    String serializable = getEnvironmentClassName(SERIALIZABLE, this.environment);
    String externalizable = getEnvironmentClassName(EXTERNALIZABLE, this.environment);
    
    for (int i = 0; i < interfaces.length; i++)
      {
        if (interfaces[i].equals(serializable))
          {
            interfaces[i] = externalizable;
            found = true;
          }
      }
    
    if (! found)
      {
        String[] tmp = new String[interfaces.length + 1];
        System.arraycopy(interfaces, 0, tmp, 0, interfaces.length);
        tmp[interfaces.length] = externalizable;
        interfaces = tmp;
      }
    
    return interfaces;
  }

  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
  {
    super.visit(version, access, name, signature, superName,
                rewriteInterfaces(interfaces));
    
    this.className = name;
    this.superClassName = superName;
  }

  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
  {
    // Make final fields non-final.
    access &= ~ ACC_FINAL;
    
    // Only do serialication for non-transient fields.
    if ((access & ACC_TRANSIENT) != ACC_TRANSIENT)
      {
        String type = desc;
        
        while (type.charAt(0) == '[')
          {
            type = type.substring(1);
          }
        
        // Primitive fields.
        if (PrimitiveTypesHelper.isPrimitiveType(type))
          {
            this.fields.put(name, desc);
          }
        // Special non-primitive fields.
        else if (type.equals("Ljava/lang/Boolean;")
                 || type.equals("Ljava/lang/Byte;")
                 || type.equals("Ljava/lang/Character;")
                 || type.equals("Ljava/lang/Double;")
                 || type.equals("Ljava/lang/Float;")
                 || type.equals("Ljava/lang/Integer;")
                 || type.equals("Ljava/lang/Long;")
                 || type.equals("Ljava/lang/Short;")
                 || type.equals("Ljava/lang/String;"))
          {
            this.fields.put(name, desc);
          }
        // Non-primitive fields.
        else if (type.startsWith("L"))
          {
            type = type.substring(1, type.length() - 1);
            
            if (this.loader.inherits(getEnvironmentClassName(SERIALIZABLE, this.environment), type))
              {
                this.fields.put(name, desc);
              }
            else
              {
                throw new BuildException("Cannot serialize field " + this.className + "." + name);
              }
          }
      }
    
    return super.visitField(access, name, desc, signature, value);
  }

  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
  {
    if ("<init>".equals(name) && "()V".equals(desc))
      {
        this.generateDefaultConstructor = false;
      }
    
    if (READ_NAME.equals(name)
        && READ_SIGNATURE.equals(desc))
      {
        this.generateReadMethod = false;
      }
    
    if (WRITE_NAME.equals(name)
        && WRITE_SIGNATURE.equals(desc))
      {
        this.generateWriteMethod = false;
      }

    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  public void visitEnd()
  {
    generateDefaultConstructor();
    generateReadMethod();
    generateWriteMethod();
    super.visitEnd();
  }

  private void generateWriteMethod()
  {
    if (!this.generateWriteMethod)
      {
        return;
      }

    MethodVisitor mv =
      super.visitMethod(WRITE_MODIFIERS, WRITE_NAME, WRITE_SIGNATURE,
                        null, new String[] { "java/io/IOException" });
    
    mv.visitCode();
    
    boolean arrayField = false;
    
    Label l0 = new Label();
    String[] fields = getSortedFields();

    for (int i = 0; i < fields.length; i++)
      {
        // Code: instance.field = value;
        Label label = new Label();
        
        if (i == 0)
          {
            label = l0;
          }
        else
          {
            label = new Label();
          }
        
        String name = fields[i];
        String desc = (String) this.fields.get(name);
        mv.visitLabel(label);
        
        // Array types.
        if (PrimitiveTypesHelper.isArrayType(desc))
          {
            if (PrimitiveTypesHelper.isMultiDimensionalArrayType(desc))
              {
                throw new RuntimeException("Multidimensional arrays are not supported by the serialization framework");
              }
            
            arrayField = true;
            String type = desc.substring(1);
            
            Label arrayAfter = new Label();
            Label arrayNonNull = new Label();
            Label arrayLoopBegin = new Label();
            Label arrayLoopExpression = new Label();
            
            // Code: output.write(field.length);
            // Code: for (int i = 0; i < field.length; i++) {
            // Code:   output.write(field[i]);
            // Code: }
            
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            mv.visitJumpInsn(IFNONNULL, arrayNonNull);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, "writeBoolean", "(Z)V");
            mv.visitJumpInsn(GOTO, arrayAfter);
            
            mv.visitLabel(arrayNonNull);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, "writeBoolean", "(Z)V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitVarInsn(ISTORE, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, "writeInt", "(I)V");
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 3);
            mv.visitJumpInsn(GOTO, arrayLoopExpression);
            
            mv.visitLabel(arrayLoopBegin);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            mv.visitVarInsn(ILOAD, 3);
            
            if ("I".equals(type))
              {
                mv.visitInsn(IALOAD);
              }
            else if ("B".equals(type) || "Z".equals(type))
              {
                mv.visitInsn(BALOAD);
              }
            else if ("C".equals(type))
              {
                mv.visitInsn(CALOAD);
              }
            else if ("S".equals(type))
              {
                mv.visitInsn(SALOAD);
              }
            else if ("J".equals(type))
              {
                mv.visitInsn(LALOAD);
              }
            else if ("F".equals(type))
              {
                mv.visitInsn(FALOAD);
              }
            else if ("D".equals(type))
              {
                mv.visitInsn(DALOAD);
              }
            else // if (type.startsWith("L"))
              {
                mv.visitInsn(AALOAD);
              }
            
            String method = (String) methodNamesWrite.get(type);
            
            if (method != null)
              {
                int pos = method.indexOf('(');
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, method.substring(0, pos), method.substring(pos));
              }
            else // if (type.startsWith("L"))
              {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "de/enough/polish/io/Externalizable", "write", "(Ljava/io/DataOutputStream;)V");
              }

            mv.visitIincInsn(3, 1);
            
            mv.visitLabel(arrayLoopExpression);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitJumpInsn(IF_ICMPLT, arrayLoopBegin);
            
            mv.visitLabel(arrayAfter);
          }
        else
          {
            // Primitive types.
            if (PrimitiveTypesHelper.isPrimitiveType(desc))
              {
                // Code: output.writeType(field);

                String method = (String) methodNamesWrite.get(desc);
                int pos = method.indexOf('(');

                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, name, desc);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, method.substring(0, pos), method.substring(pos));
              }
            else
              {
                Label caseIfNull = new Label();
                Label afterElse = new Label();
          
                // Code: if (field != null) {
                // Code:   output.writeBoolean(1);
                // Code:   ...
                // Code: } else {
                // Code:   output.writeBoolean(0);
                // Code: }
               
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, name, desc);
                mv.visitJumpInsn(IFNULL, caseIfNull);
                
                mv.visitVarInsn(ALOAD, 1);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, "writeBoolean", "(Z)V");

                if ("Ljava/lang/String;".equals(desc))
                  {
                    String method = (String) methodNamesWrite.get(desc);
                    int pos = method.indexOf('(');

                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, this.className, name, desc);
                    mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, method.substring(0, pos), method.substring(pos));
                  }
                else
                  {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, this.className, name, desc);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKEINTERFACE, "de/enough/polish/io/Externalizable", "write", "(Ljava/io/DataOutputStream;)V");
                  }
                
                mv.visitJumpInsn(GOTO, afterElse);
                mv.visitLabel(caseIfNull);
                
                mv.visitVarInsn(ALOAD, 1);
                mv.visitInsn(ICONST_0);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, "writeBoolean", "(Z)V");
                
                mv.visitLabel(afterElse);
              }
          }
      }
    
    // Code: return;
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitInsn(RETURN);
    
    // Local variable table and stack limits.
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLocalVariable("this", "L" + this.className + ";", null, l0, l2, 0);
    mv.visitLocalVariable("output", "L" + DATAOUTPUTSTREAM + ";", null, l0, l2, 1);

    if (arrayField)
      {
        // FIXME: The scope of this can be more limited.
        mv.visitLocalVariable("arraySize", "I", null, l0, l2, 2);
        mv.visitLocalVariable("i", "I", null, l0, l2, 3);
      }
    
    mv.visitMaxs(10, 4);
    mv.visitEnd();
  }

  private void generateDefaultConstructor()
  {
    if (!this.generateDefaultConstructor)
      {
        return;
      }
    
    MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "<init>", "()V", null, new String[0]);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, this.superClassName, "<init>", "()V");
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }

  private void generateReadMethod()
  {
    if (!this.generateReadMethod)
      {
        return;
      }

    MethodVisitor mv = 
      super.visitMethod(READ_MODIFIERS, READ_NAME, READ_SIGNATURE,
                        null, new String[] { "java/io/IOException" });
    
    mv.visitCode();
    
    boolean arrayField = false;
    Label l0 = new Label();
    String[] fields = getSortedFields();

    for (int i = 0; i < fields.length; i++)
      {
        // Code: instance.field = value;
        
        Label label;
        
        if (i == 0)
          {
            label = l0;
          }
        else
          {
            label = new Label();
          }
        
        String name = fields[i];
        String desc = (String) this.fields.get(name); 
        mv.visitLabel(label);
        
        if (PrimitiveTypesHelper.isArrayType(desc))
          {
            if (PrimitiveTypesHelper.isMultiDimensionalArrayType(desc))
              {
                throw new RuntimeException("Multidimensional arrays are not supported by the serialization framework");
              }
            
            arrayField = true;
            String type = desc.substring(1);
            
            Label arrayAfter = new Label();
            Label arrayLoopBegin = new Label();
            Label arrayLoopExpression = new Label();
            
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, "readBoolean", "()Z");
            mv.visitJumpInsn(IFEQ, arrayAfter);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, "readInt", "()I");
            mv.visitVarInsn(ISTORE, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 2);

            if (type.startsWith("L"))
              {
                mv.visitTypeInsn(ANEWARRAY, type.substring(1, type.length() - 1));
              }
            else
              {
                mv.visitIntInsn(NEWARRAY, PrimitiveTypesHelper.getPrimitiveArrayType(type));
              }
            
            mv.visitFieldInsn(PUTFIELD, this.className, name, desc);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 3);
            mv.visitJumpInsn(GOTO, arrayLoopExpression);
            mv.visitLabel(arrayLoopBegin);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
            
//            mv.visitTypeInsn(NEW, type);
//            mv.visitInsn(DUP);
//            mv.visitMethodInsn(INVOKESPECIAL, type, "<init>", "()V");
//            mv.visitInsn(AASTORE);
//            mv.visitVarInsn(ALOAD, 0);
//            mv.visitFieldInsn(GETFIELD, this.className, name, desc);
//            mv.visitVarInsn(ILOAD, 3);
//            mv.visitInsn(AALOAD);
//            mv.visitVarInsn(ALOAD, 1);
//            mv.visitMethodInsn(INVOKEVIRTUAL, type, "read", "(Ljava/io/DataInputStream;)V");
            
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ALOAD, 1);

            String method = (String) methodNamesRead.get(type);
            
            if (method != null)
              {
                int pos = method.indexOf('(');
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, method.substring(0, pos), method.substring(pos)); 
              }
            else // if (type.startsWith("L"))
              {
                mv.visitMethodInsn(INVOKEINTERFACE, "de/enough/polish/io/Externalizable", "read", "(Ljava/io/DataInputStream;)V");
              }

            if ("I".equals(type))
              {
                mv.visitInsn(IASTORE);
              }
            else if ("B".equals(type) || "Z".equals(type))
              {
                mv.visitInsn(BASTORE);
              }
            else if ("C".equals(type))
              {
                mv.visitInsn(CASTORE);
              }
            else if ("S".equals(type))
              {
                mv.visitInsn(SASTORE);
              }
            else if ("J".equals(type))
              {
                mv.visitInsn(LASTORE);
              }
            else if ("F".equals(type))
              {
                mv.visitInsn(FASTORE);
              }
            else if ("D".equals(type))
              {
                mv.visitInsn(DASTORE);
              }
            else // if (type.startsWith("L"))
              {
                mv.visitInsn(AASTORE);
              }

            mv.visitIincInsn(3, 1);
            
            mv.visitLabel(arrayLoopExpression);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitJumpInsn(IF_ICMPLT, arrayLoopBegin);
            mv.visitLabel(arrayAfter);
          }
        else
          {
            if (PrimitiveTypesHelper.isPrimitiveType(desc))
              {
                // Code: field = input.readType();
                
                String method = (String) methodNamesRead.get(desc);
                int pos = method.indexOf('(');

                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, method.substring(0, pos), method.substring(pos)); 
                mv.visitFieldInsn(PUTFIELD, this.className, name, desc);
              }
            else
              {
                String descShort = desc.substring(1, desc.length() - 1);
                Label afterRead = new Label();
                
                // Code: boolean nonNull = input.readBoolean();
                // Code: if (nonNull) {
                // Code:   field = new Type();
                // Code:   ...
                // Code: }
                
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, "readBoolean", "()Z");
                mv.visitJumpInsn(IFEQ, afterRead);
                
                mv.visitVarInsn(ALOAD, 0);
                mv.visitTypeInsn(NEW, descShort);
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, descShort, "<init>", "()V");
                mv.visitFieldInsn(PUTFIELD, this.className, name, desc);

                if ("Ljava/lang/String;".equals(desc))
                  {
                    String method = (String) methodNamesRead.get(desc);
                    int pos = method.indexOf('(');

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, method.substring(0, pos), method.substring(pos)); 
                    mv.visitFieldInsn(PUTFIELD, this.className, name, desc);
                  }
                else
                  {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, this.className, name, desc);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKEINTERFACE, "de/enough/polish/io/Externalizable", "read", "(Ljava/io/DataInputStream;)V");
                  }
                
                mv.visitLabel(afterRead);
              }
          }
      }
    
    // Code: return;
    Label l1 = new Label();
    mv.visitLabel(l1);
    mv.visitInsn(RETURN);
    
    // Local variable table and stack limits.
    Label l2 = new Label();
    mv.visitLabel(l2);
    mv.visitLocalVariable("this", "L" + this.className + ";", null, l0, l2, 0);
    mv.visitLocalVariable("input", "L" + DATAINPUTSTREAM + ";", null, l0, l2, 1);
    
    if (arrayField)
      {
        // FIXME: The scope of this can be more limited.
        mv.visitLocalVariable("arraySize", "I", null, l0, l2, 2);
        mv.visitLocalVariable("i", "I", null, l0, l2, 3);
      }
    
    mv.visitMaxs(6, 4);
    
    mv.visitEnd();
  }
}
