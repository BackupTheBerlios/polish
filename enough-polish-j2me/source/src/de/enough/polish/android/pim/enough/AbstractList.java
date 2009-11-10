package de.enough.polish.android.pim.enough;

import de.enough.polish.android.pim.PIM;
import de.enough.polish.android.pim.UnsupportedFieldException;

public class AbstractList {

	private FieldInfo[] fieldInfos;
	private final int mode;
	private final String name;
	
	public AbstractList(String name, int mode) {
		this.name = name;
		if(mode != PIM.READ_ONLY && mode != PIM.WRITE_ONLY && mode != PIM.READ_WRITE) {
			throw new IllegalArgumentException("The mode '"+mode+"' is not supported.");
		}
		this.mode = mode;
	}

	protected void setFieldInfos(FieldInfo[] fieldInfos) {
		this.fieldInfos = fieldInfos;
	}
	
	protected FieldInfo findFieldInfo(int fieldId) {
		return findFieldInfo(fieldId,true);
	}

	/**
	 * 
	 * @param fieldId
	 * @param throwException
	 * @return May return null if no field with the given id is present and now exception should be thrown.
	 */
	protected FieldInfo findFieldInfo(int fieldId, boolean throwException) {
		for (int i = 0; i < this.fieldInfos.length; i++) {
			FieldInfo fieldInfo = this.fieldInfos[i];
			if(fieldId == fieldInfo.pimId) {
				return fieldInfo;
			}
		}
		if(throwException) {
			throw new UnsupportedFieldException("The field with id '"+fieldId+"' is not supported.");
		}
		return null;
	}

	/**
	 * This method throws a SecurityException if the list is not readable. This is the case if the list was opened in WRITE_ONLY mode.
	 */
	protected void ensureListReadable() {
		if(this.mode == PIM.WRITE_ONLY) {
			throw new SecurityException("The list is only writeable.");
		}
	}

	protected void ensureListWriteable() {
		if(this.mode == PIM.READ_ONLY) {
			throw new SecurityException("The list is only readable.");
		}
	}

	public String getName() {
		return this.name;
	}
	
	public int[] getSupportedFields() {
		int[] supportedFields = new int[this.fieldInfos.length];
		for (int i = 0; i < this.fieldInfos.length; i++) {
			supportedFields[i] = this.fieldInfos[i].pimId;
		}
		return supportedFields;
	}

}