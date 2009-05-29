//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Wed Mar 25 13:56:47 CET 2009
package de.enough.polish.android.pim;

/**
 * Represents an exception thrown when an attempt is made to add data to a field
 * but the field already has all available slots for data values assigned.
 */
public class FieldFullException extends java.lang.RuntimeException
{
	//following variables are implicitely defined by getter- or setter-methods:
	private int field;

	/**
	 * 
	 * Constructs a new instance of this class with its stack trace filled in. A
	 * default value of -1 is assigned to the Field.
	 * <P></P>
	 * 
	 */
	public FieldFullException()
	{
		//TODO implement FieldFullException
	}

	/**
	 * 
	 * Constructs a new instance of this class with its stack trace and message filled
	 * in. A default value of -1 is assigned to the Field.
	 * <P></P>
	 * 
	 * @param detailMessage - String The detail message for the exception.
	 */
	public FieldFullException(java.lang.String detailMessage)
	{
		//TODO implement FieldFullException
	}

	/**
	 * 
	 * Constructs a new instance of this class with its stack trace, message, and
	 * offending field filled in.
	 * <P></P>
	 * 
	 * @param detailMessage - String The detail message for the exception.
	 * @param field - int the offending field for the exception.
	 */
	public FieldFullException(java.lang.String detailMessage, int field)
	{
		//TODO implement FieldFullException
	}

	/**
	 * 
	 * Method to access the field for which this exception is thrown. -1 indicates no
	 * field value has been assigned to this exception.
	 * <P></P>
	 * 
	 * 
	 * @return int representing the offending field.
	 */
	public int getField()
	{
		return this.field;
	}

}
