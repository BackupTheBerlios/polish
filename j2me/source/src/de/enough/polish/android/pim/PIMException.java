//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Wed Mar 25 13:56:47 CET 2009
package de.enough.polish.android.pim;

/**
 * Represents exceptions thrown by the PIM classes.
 * 
 * Represents exceptions thrown by the PIM classes. This class has a reason code
 * optionally associated with it to provide more information about the PIM
 * exception that occurred.
 */
public class PIMException extends java.lang.Exception
{
	/**
	 * 
	 * Indicates a PIM exception where the functionality is not supported in this
	 * implementation.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int FEATURE_NOT_SUPPORTED = 0;

	/**
	 * 
	 * Indicates a general PIM exception error. This is the default value for the
	 * reason code.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int GENERAL_ERROR = 1;

	/**
	 * 
	 * Indicates a PIM exception where a list is closed and access is attempted.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int LIST_CLOSED = 2;

	/**
	 * 
	 * Indicates a PIM exception where a list is no longer accessible by the
	 * application, such as if the underlying PIM database is deleted.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int LIST_NOT_ACCESSIBLE = 3;

	/**
	 * 
	 * Indicates the max number of categories is exceeded.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int MAX_CATEGORIES_EXCEEDED = 4;

	/**
	 * 
	 * Indicates the data is in an unsupported PIM version.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int UNSUPPORTED_VERSION = 5;

	/**
	 * 
	 * Indicates a PIM exception where the update could not continue.
	 * <P>
	 * <DT><B>See Also:</B>
	 * Field Values</A></DD></DL>
	 * 
	 */
	public static final int UPDATE_ERROR = 6;

	//following variables are implicitely defined by getter- or setter-methods:
	private int reason = GENERAL_ERROR;

	/**
	 * 
	 * Constructs a new instance of this class with its stack trace filled in. The
	 * reason code for this exception is set to the default value of <code>GENERAL_ERROR</code>.
	 * <P></P>
	 * 
	 */
	public PIMException()
	{
		super();
	}

	/**
	 * 
	 * Constructs a new instance of this class with its stacktrace and message filled
	 * in. The reason code for this exception is set to the default value of <code>GENERAL_ERROR</code>.
	 * <P></P>
	 * 
	 * @param detailMessage - String The detail message for the exception.
	 */
	public PIMException(java.lang.String detailMessage)
	{
		super( detailMessage );
	}

	/**
	 * 
	 * Constructs a new instance of this class with its stacktrace, message, and
	 * reason filled in.
	 * <P></P>
	 * 
	 * @param detailMessage - String The detail message for the exception.
	 * @param reason - int Integer representing the reason for the exception.
	 */
	public PIMException(java.lang.String detailMessage, int reason)
	{
		super( detailMessage );
		this.reason = reason;
	}

	/**
	 * 
	 * Returns the reason for the PIM Exception. The int returned is one of the static
	 * error reason values defined in this class.
	 * <P></P>
	 * 
	 * 
	 * @return int reason for the exception.
	 */
	public int getReason()
	{
		return this.reason;
	}

}
