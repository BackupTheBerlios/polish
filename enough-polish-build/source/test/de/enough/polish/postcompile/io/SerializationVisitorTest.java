package de.enough.polish.postcompile.io;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import de.enough.bytecode.ASMClassLoader;

import junit.framework.TestCase;

public class SerializationVisitorTest
  extends TestCase
{
  public void test()
    throws Exception
  {
	  doTest("de/enough/polish/postcompile/io/TestSerialization_boolean");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_byte");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_char");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_int");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_double");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_float");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_long");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_short");
	  
	  doTest("de/enough/polish/postcompile/io/TestSerialization_booleanArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_byteArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_charArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_doubleArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_floatArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_intArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_longArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_shortArray");

	  doTest("de/enough/polish/postcompile/io/TestSerialization_Integer");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_String");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_Serializable");

	  doTest("de/enough/polish/postcompile/io/TestSerialization_StringArray");
	  doTest("de/enough/polish/postcompile/io/TestSerialization_SerializableArray");
  }

  private void doTest(String className)
    throws ClassNotFoundException
  {
	  ASMClassLoader loader;
	  ClassNode clazz;
	  StringWriter result;

	  String expected, postcompiled;
	  
	  loader = new ASMClassLoader();
	  clazz = loader.loadClass(className + "_template");
	  result = new StringWriter();
	  clazz.accept(new TraceClassVisitor(new PrintWriter(result)));
	  expected = result.toString().replace("_template", "");
	  
	  loader = new ASMClassLoader();
	  clazz = loader.loadClass(className);
	  result = new StringWriter();
	  clazz.accept(new SerializationVisitor(new TraceClassVisitor(new PrintWriter(result)), loader, null));
	  postcompiled = result.toString();

	  assertEquals(expected, postcompiled);
  }
}
