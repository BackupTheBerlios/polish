package de.enough.polish.postcompile.serialization;

import java.util.Set;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public class DataSerializationVisitor extends ClassAdapter {
	private Set unwanted;

	public DataSerializationVisitor(ClassVisitor cv, Set unwanted) {
		super(cv);
		this.unwanted = unwanted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.asm.ClassAdapter#visitField(int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Object)
	 */
	public FieldVisitor visitField(int access, String name, String desc,
			String signature, Object value) {
		System.out.println(name);
		return null;
	}
}
