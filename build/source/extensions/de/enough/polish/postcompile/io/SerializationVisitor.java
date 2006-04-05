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
import de.enough.polish.Environment;

public class SerializationVisitor
  extends ClassAdapter
  implements Opcodes
{
  private static final String POLISH_USE_DEFAULT_PACKAGE = "polish.useDefaultPackage";

  // TODO: Make this private.
  public static final String SERIALIZABLE = "de/enough/polish/io/Serializable";
  public static final String EXTERNALIZABLE = "de/enough/polish/io/Externalizable";
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
    methodNamesRead.put("D", "readDouble");
    methodNamesRead.put("F", "readFloat");
    methodNamesRead.put("I", "readInt");
    methodNamesRead.put("J", "readLong");
    methodNamesRead.put("B", "readByte");
    methodNamesRead.put("S", "readShort");
    methodNamesRead.put("Z", "readBoolean");

    methodNamesWrite = new HashMap();
    methodNamesWrite.put("D", "writeDouble");
    methodNamesWrite.put("F", "writeFloat");
    methodNamesWrite.put("I", "writeInt");
    methodNamesWrite.put("J", "writeLong");
    methodNamesWrite.put("B", "writeByte");
    methodNamesWrite.put("S", "writeShort");
    methodNamesWrite.put("Z", "writeBoolean");
  }

  /**
   * Iterates over all implemented interfaces of a class and checks if it
   * implements <code>de.enough.polish.serialization.Serializable</code>.
   * @param classNode
   *          The classNode to check
   * @return <code>true</code> if the class implements
   *         <code>Serializable</code>, otherwise <code>false</code>
   */
  public static boolean isSerializable(ClassNode classNode, Environment env)
  {
    String compare = getEnvironmentClassName(SERIALIZABLE, env);
    Iterator it = classNode.interfaces.iterator();
  
    while (it.hasNext())
      {
        String name = (String) it.next();
        
        if (compare.equals(name))
          {
            return true;
          }
      }
  
    return false;
  }

  /**
   * Iterates over all implemented interfaces of a class and checks if it
   * implements <code>de.enough.polish.serialization.Externazable</code>.
   * @param classNode
   *          The classNode to check
   * @return <code>true</code> if the class implements
   *         <code>Externalizable</code>, otherwise <code>false</code>
   */
  public static boolean isExternalizable(ClassNode classNode, Environment env)
  {
    String compare = getEnvironmentClassName(EXTERNALIZABLE, env);
    Iterator it = classNode.interfaces.iterator();
  
    while (it.hasNext())
      {
        String name = (String) it.next();
        
        if (compare.equals(name))
          {
            return true;
          }
      }
    
    return false;
  }

  public static String getEnvironmentClassName(String className, Environment env)
  {
    if (env.hasSymbol(POLISH_USE_DEFAULT_PACKAGE))
      {
        int index = className.lastIndexOf('/') + 1;
        return className.substring(index);
      }

    return className;
  }

  private String className;
  private ASMClassLoader loader;
  private Environment environment; 
  private HashMap fields;
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

  private static String getArgumentType(String typeName)
  {
    return "L" + typeName + ";";
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

  private static boolean isPrimitiveType(String desc)
  {
    if (desc.startsWith("L")
        || desc.startsWith("[")
        || desc.equals("V"))
      return false;
    
    return true;
  }

  private String[] rewriteInterfaces(String[] interfaces)
  {
    for (int i = 0; i < interfaces.length; i++)
      {
        if (interfaces[i].equals(getEnvironmentClassName(SERIALIZABLE,
                                                         this.environment)))
          {
            interfaces[i] = getEnvironmentClassName(EXTERNALIZABLE,
                                                    this.environment);
          }
      }
    
    return interfaces;
  }

  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
  {
    super.visit(version, access, name, signature, superName,
                rewriteInterfaces(interfaces));
    
    this.className = name;
  }

  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
  {
    // TODO: Handle final fields.
    
    if ((access & ACC_TRANSIENT) != ACC_TRANSIENT)
      {
        int index = name.lastIndexOf('/');
        String fieldName = name.substring(index + 1);
        
        if (isPrimitiveType(desc))
          {
            this.fields.put(name, desc);
          }
        
        if (desc.startsWith("L"))
          {
            String type = desc.substring(1, desc.length() - 1); 

            try
              {
                // TODO: Handle all classes in default package.
                if (this.loader.inherits("de/enough/polish/io/Serializable", type))
                  {
                    this.fields.put(name, desc);
                  }
                else
                  {
                    throw new BuildException("Cannot serialize field " + this.className + "." + name);
                  }
              }
            catch (ClassNotFoundException e)
              {
                System.err.println("WARNING: Cannot check inheritance of " + type);
              }
          }
      }
    
    return super.visitField(access, name, desc, signature, value);
  }

  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
  {
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
    // TODO: Generate default constructor if needed.
    
    // Generate read method for serialization.
    if (generateReadMethod)
      {
        MethodVisitor mv = 
          visitMethod(READ_MODIFIERS, READ_NAME, READ_SIGNATURE,
                      null, new String[] { "java/io/IOException" });
        
        mv.visitCode();
        
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
            String type = (String) this.fields.get(name); 
            mv.visitLabel(label);
            
            if (isPrimitiveType(type))
              {
                // Code: this.field = readType();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAINPUTSTREAM, (String) methodNamesRead.get(type), "()" + type);
                mv.visitFieldInsn(PUTFIELD, this.className, name, type);
              }
            else
              {
                // TODO: Implement me.
                // Code: if (field == null) ...
                // Code: field = new Type();
                
                // Code: field.read(input);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, name, type);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "de/enough/polish/io/Externalizable", "read", "(Ljava/io/DataInputStream;)V");
              }
          }
        
        // Code: return;
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        
        // Local variable table and stack limits.
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", getArgumentType(this.className), null, l0, l2, 0);
        mv.visitLocalVariable("input", getArgumentType(DATAINPUTSTREAM), null, l0, l2, 1);
        // FIXME: This is probably wrong.
        mv.visitMaxs(2, 2);
        
        mv.visitEnd();
      }
    
    // Generate write method for serialization.
    if (generateWriteMethod)
      {
        MethodVisitor mv =
          visitMethod(WRITE_MODIFIERS, WRITE_NAME, WRITE_SIGNATURE,
                      null, new String[] { "java/io/IOException" });
        
        mv.visitCode();
        
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
            String type = (String) this.fields.get(name);
            mv.visitLabel(label);
            
            if (isPrimitiveType(type))
              {
                // Code: output.writeType(field);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, name, type);
                mv.visitMethodInsn(INVOKEVIRTUAL, DATAOUTPUTSTREAM, (String) methodNamesWrite.get(type), "(" + type + ")V");
              }
            else
              {
                Label labelJump = new Label();
//                Label labelGoto = new Label();
                
                // Code: if (field ==null) ...
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, name, type);
                mv.visitJumpInsn(IFNULL, labelJump);
                
                // Code: field.write(output);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, this.className, name, type);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "de/enough/polish/io/Externalizable", "write", "(Ljava/io/DataOutputStream;)V");
//                mv.visitJumpInsn(GOTO, labelGoto);
                mv.visitLabel(labelJump);
                // TODO: Wrote 'null' case to stream.
//                mv.visitLabel(labelGoto);
              }
          }
        
        // Code: return;
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitInsn(RETURN);
        
        // Local variable table and stack limits.
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", getArgumentType(this.className), null, l0, l2, 0);
        mv.visitLocalVariable("output", getArgumentType(DATAOUTPUTSTREAM), null, l0, l2, 1);
        // FIXME: This is probably wrong.
        mv.visitMaxs(2, 2);
        
        mv.visitEnd();
      }
    
    // Call super implementation. 
    super.visitEnd();
  }
}
