// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sun Feb 29 19:10:56 CET 2004
package javax.microedition.lcdui;

import de.enough.polish.ui.Screen;

/**
 * The <code>TextBox</code> class is a <code>Screen</code> that allows
 * the user to enter and edit
 * text.
 * 
 * <p>A <code>TextBox</code> has a maximum size, which is the maximum
 * number of characters
 * that can be stored in the object at any time (its capacity). This limit is
 * enforced when the <code>TextBox</code> instance is constructed,
 * when the user is editing text within the <code>TextBox</code>, as well as
 * when the application program calls methods on the
 * <code>TextBox</code> that modify its
 * contents. The maximum size is the maximum stored capacity and is unrelated
 * to the number of characters that may be displayed at any given time.
 * The number of characters displayed and their arrangement into rows and
 * columns are determined by the device. </p>
 * 
 * <p>The implementation may place a boundary on the maximum size, and the
 * maximum size actually assigned may be smaller than the application had
 * requested.  The value actually assigned will be reflected in the value
 * returned by <A HREF="../../../javax/microedition/lcdui/TextBox.html#getMaxSize()"><CODE>getMaxSize()</CODE></A>.  A defensively-written
 * application should compare this value to the maximum size requested and be
 * prepared to handle cases where they differ.</p>
 * 
 * <p>The text contained within a <code>TextBox</code> may be more
 * than can be displayed at
 * one time. If this is the case, the implementation will let the user scroll
 * to view and edit any part of the text. This scrolling occurs transparently
 * to the application. </p>
 * 
 * <p>If the constraints are set to <A HREF="../../../javax/microedition/lcdui/TextField.html#ANY"><CODE>TextField.ANY</CODE></A>
 * The text may contain <A HREF="Form.html#linebreak">line breaks</A>.
 * The display of the text must break accordingly and the user must be
 * able to enter line break characters. </p>
 * 
 * <p><code>TextBox</code> has the concept of
 * <em>input constraints</em> that is identical to
 * <code>TextField</code>. The <code>constraints</code> parameters of
 * methods within the
 * <code>TextBox</code> class use constants defined in the <A HREF="../../../javax/microedition/lcdui/TextField.html"><CODE>TextField</CODE></A>
 * class. See the description of
 * <a href="TextField.html#constraints">input constraints</a>
 * in the <code>TextField</code> class for the definition of these
 * constants.  <code>TextBox</code> also has the same notions as
 * <code>TextField</code> of the <em>actual contents</em> and the
 * <em>displayed contents</em>, described in the same section.
 * </p>
 * 
 * <p><code>TextBox</code> also has the concept of <em>input
 * modes</em> that is identical
 * to <code>TextField</code>. See the description of <a
 * href="TextField.html#modes">input
 * modes</a> in the <code>TextField</code> class for more details.
 * <HR>
 * 
 * 
 * @since MIDP 1.0
 */
public class TextBox extends Screen
{
	//following variables are implicitely defined by getter- or setter-methods:
	private String string;
	private int maxSize;
	private int caretPosition;
	private int constraints;
	private String initialInputMode;
	private String title;

	/**
	 * Creates a new <code>TextBox</code> object with the given title
	 * string, initial
	 * contents, maximum size in characters, and constraints.
	 * If the text parameter is <code>null</code>, the
	 * <code>TextBox</code> is created empty.
	 * The <code>maxSize</code> parameter must be greater than zero.
	 * An <code>IllegalArgumentException</code> is thrown if the
	 * length of the initial contents string exceeds <code>maxSize</code>.
	 * However,
	 * the implementation may assign a maximum size smaller than the
	 * application had requested.  If this occurs, and if the length of the
	 * contents exceeds the newly assigned maximum size, the contents are
	 * truncated from the end in order to fit, and no exception is thrown.
	 * 
	 * @param title - the title text to be shown with the display
	 * @param text - the initial contents of the text editing area, null may be used to indicate no initial content
	 * @param maxSize - the maximum capacity in characters. The implementation may limit boundary maximum capacity and the actually assigned capacity may me smaller than requested. A defensive application will test the actually given capacity with getMaxSize().
	 * @param constraints - see input constraints
	 * @throws IllegalArgumentException - if maxSize is zero or less
	 * @throws IllegalArgumentException - if the constraints parameter is invalid
	 * @throws IllegalArgumentException - if text is illegal for the specified constraints
	 * @throws IllegalArgumentException - if the length of the string exceeds the requested maximum capacity
	 */
	public TextBox( String title, String text, int maxSize, int constraints)
	{
		super( title, null, false );
	}

	/**
	 * Gets the contents of the <code>TextBox</code> as a string value.
	 * 
	 * @return the current contents
	 * @see setString(java.lang.String)
	 */
	public String getString()
	{
		return this.string;
	}

	/**
	 * Sets the contents of the <code>TextBox</code> as a string
	 * value, replacing the previous contents.
	 * 
	 * @param text - the new value of the TextBox, or null if the TextBox is to be made empty
	 * @throws IllegalArgumentException - if text is illegal for the current input constraints
	 * @throws IllegalArgumentException - if the text would exceed the current maximum capacity
	 * @see getString()
	 */
	public void setString( String text)
	{
		this.string = text;
	}

	/**
	 * Copies the contents of the <code>TextBox</code> into a
	 * character array starting at
	 * index zero. Array elements beyond the characters copied are left
	 * unchanged.
	 * 
	 * @param data - the character array to receive the value
	 * @return the number of characters copied
	 * @throws ArrayIndexOutOfBoundsException - if the array is too short for the contents
	 * @throws NullPointerException - if data is null
	 * @see setChars(char[], int, int)
	 */
	public int getChars(char[] data)
	{
		return 0;
		//TODO implement getChars
	}

	/**
	 * Sets the contents of the <code>TextBox</code> from a character
	 * array, replacing the
	 * previous contents. Characters are copied from the region of the
	 * <code>data</code> array
	 * starting at array index <code>offset</code> and running for
	 * <code>length</code> characters.
	 * If the data array is <code>null</code>, the <code>TextBox</code>
	 * is set to be empty and the other parameters are ignored.
	 * 
	 * <p>The <code>offset</code> and <code>length</code> parameters must
	 * specify a valid range of characters within
	 * the character array <code>data</code>.
	 * The <code>offset</code> parameter must be within the
	 * range <code>[0..(data.length)]</code>, inclusive.
	 * The <code>length</code> parameter
	 * must be a non-negative integer such that
	 * <code>(offset + length) &lt;= data.length</code>.</p>
	 * 
	 * @param data - the source of the character data
	 * @param offset - the beginning of the region of characters to copy
	 * @param length - the number of characters to copy
	 * @throws ArrayIndexOutOfBoundsException - if offset and length do not specify a valid range within the data array
	 * @throws IllegalArgumentException - if data is illegal for the current input constraints
	 * @throws IllegalArgumentException - if the text would exceed the current maximum capacity
	 * @see getChars(char[])
	 */
	public void setChars(char[] data, int offset, int length)
	{
		//TODO implement setChars
	}

	/**
	 * Inserts a string into the contents of the <code>TextBox</code>.
	 * The string is
	 * inserted just prior to the character indicated by the
	 * <code>position</code> parameter, where zero specifies the first
	 * character of the contents of the <code>TextBox</code>.  If
	 * <code>position</code> is
	 * less than or equal to zero, the insertion occurs at the beginning of
	 * the contents, thus effecting a prepend operation.  If
	 * <code>position</code> is greater than or equal to the current size of
	 * the contents, the insertion occurs immediately after the end of the
	 * contents, thus effecting an append operation.  For example,
	 * <code>text.insert(s, text.size())</code> always appends the string
	 * <code>s</code> to the current contents.
	 * 
	 * <p>The current size of the contents is increased by the number of
	 * inserted characters. The resulting string must fit within the current
	 * maximum capacity. </p>
	 * 
	 * <p>If the application needs to simulate typing of characters it can
	 * determining the location of the current insertion point
	 * (&quot;caret&quot;)
	 * using the with <A HREF="../../../javax/microedition/lcdui/TextBox.html#getCaretPosition()"><CODE>getCaretPosition()</CODE></A> method.
	 * For example,
	 * <code>text.insert(s, text.getCaretPosition())</code> inserts the string
	 * <code>s</code> at the current caret position.</p>
	 * 
	 * @param src - the String to be inserted
	 * @param position - the position at which insertion is to occur
	 * @throws IllegalArgumentException - if the resulting contents would be illegal for the current input constraints
	 * @throws IllegalArgumentException - if the insertion would exceed the current maximum capacity
	 * @throws NullPointerException - if src is null
	 */
	public void insert( String src, int position)
	{
		//TODO implement insert
	}

	/**
	 * Inserts a subrange of an array of characters into the contents of
	 * the <code>TextBox</code>.  The <code>offset</code> and
	 * <code>length</code> parameters indicate the subrange of
	 * the data array to be used for insertion. Behavior is otherwise
	 * identical to <A HREF="../../../javax/microedition/lcdui/TextBox.html#insert(java.lang.String, int)"><CODE>insert(String, int)</CODE></A>.
	 * 
	 * <p>The <code>offset</code> and <code>length</code> parameters must
	 * specify a valid range of characters within
	 * the character array <code>data</code>.
	 * The <code>offset</code> parameter must be within the
	 * range <code>[0..(data.length)]</code>, inclusive.
	 * The <code>length</code> parameter
	 * must be a non-negative integer such that
	 * <code>(offset + length) &lt;= data.length</code>.</p>
	 * 
	 * @param data - the source of the character data
	 * @param offset - the beginning of the region of characters to copy
	 * @param length - the number of characters to copy
	 * @param position - the position at which insertion is to occur
	 * @throws ArrayIndexOutOfBoundsException - if offset and length do not specify a valid range within the data array
	 * @throws IllegalArgumentException - if the resulting contents would be illegal for the current input constraints
	 * @throws IllegalArgumentException - if the insertion would exceed the current maximum capacity
	 * @throws NullPointerException - if data is null
	 */
	public void insert(char[] data, int offset, int length, int position)
	{
		//TODO implement insert
	}

	/**
	 * Deletes characters from the <code>TextBox</code>.
	 * 
	 * <p>The <code>offset</code> and <code>length</code> parameters must
	 * specify a valid range of characters within
	 * the contents of the <code>TextBox</code>.
	 * The <code>offset</code> parameter must be within the
	 * range <code>[0..(size())]</code>, inclusive.
	 * The <code>length</code> parameter
	 * must be a non-negative integer such that
	 * <code>(offset + length) &lt;= size()</code>.</p>
	 * 
	 * @param offset - the beginning of the region to be deleted
	 * @param length - the number of characters to be deleted
	 * @throws IllegalArgumentException - if the resulting contents would be illegal for the current input constraints
	 * @throws StringIndexOutOfBoundsException - if offset and length do not specify a valid range within the contents of the TextBox
	 */
	public void delete(int offset, int length)
	{
		//TODO implement delete
	}

	/**
	 * Returns the maximum size (number of characters) that can be
	 * stored in this <code>TextBox</code>.
	 * 
	 * @return the maximum size in characters
	 * @see setMaxSize(int)
	 */
	public int getMaxSize()
	{
		return this.maxSize;
	}

	/**
	 * Sets the maximum size (number of characters) that can be
	 * contained in this
	 * <code>TextBox</code>. If the current contents of the
	 * <code>TextBox</code> are larger than
	 * <code>maxSize</code>, the contents are truncated to fit.
	 * 
	 * @param maxSize - the new maximum size
	 * @return assigned maximum capacity - may be smaller than requested.
	 * @throws IllegalArgumentException - if maxSize is zero or less.
	 * @throws IllegalArgumentException - if the contents after truncation would be illegal for the current input constraints
	 * @see getMaxSize()
	 */
	public int setMaxSize(int maxSize)
	{
		return 0;
		//TODO implement setMaxSize
	}

	/**
	 * Gets the number of characters that are currently stored in this
	 * <code>TextBox</code>.
	 * 
	 * @return the number of characters
	 */
	public int size()
	{
		return 0;
		//TODO implement size
	}

	/**
	 * Gets the current input position.  For some UIs this may block and ask
	 * the user for the intended caret position, and on other UIs this may
	 * simply return the current caret position.
	 * 
	 * @return the current caret position, 0 if at the beginning
	 */
	public int getCaretPosition()
	{
		return this.caretPosition;
	}

	/**
	 * Sets the input constraints of the <code>TextBox</code>. If the
	 * current contents
	 * of the <code>TextBox</code> do not match the new constraints,
	 * the contents are
	 * set to empty.
	 * 
	 * @param constraints - see input constraints
	 * @throws IllegalArgumentException - if the value of the constraints parameter is invalid
	 * @see getConstraints()
	 */
	public void setConstraints(int constraints)
	{
		this.constraints = constraints;
	}

	/**
	 * Gets the current input constraints of the <code>TextBox</code>.
	 * 
	 * @return the current constraints value (see input constraints)
	 * @see setConstraints(int)
	 */
	public int getConstraints()
	{
		return this.constraints;
	}

	/**
	 * Sets a hint to the implementation as to the input mode that should be
	 * used when the user initiates editing of this
	 * <code>TextBox</code>. The
	 * <code>characterSubset</code> parameter names a subset of Unicode
	 * characters that is used by the implementation to choose an initial
	 * input mode.  If <code>null</code> is passed, the implementation should
	 * choose a default input mode.
	 * 
	 * <p>See <a href="TextField#modes">Input Modes</a> for a full
	 * explanation of input modes. </p>
	 * 
	 * @param characterSubset - a string naming a Unicode character subset, or null
	 * @since  MIDP 2.0
	 */
	public void setInitialInputMode( String characterSubset)
	{
		this.initialInputMode = characterSubset;
	}

	/**
	 * Sets the title of the <code>Displayable</code>. If
	 * <code>null</code> is given,
	 * removes the title.
	 * 
	 * <P>If the <code>Displayable</code> is actually visible on
	 * the display,
	 * the implementation should update
	 * the display as soon as it is feasible to do so.</P>
	 * 
	 * <P>The existence of a title  may affect the size
	 * of the area available for <code>Displayable</code> content.
	 * If the application adds, removes, or sets the title text at runtime,
	 * this can dynamically change the size of the content area.
	 * This is most important to be aware of when using the
	 * <code>Canvas</code> class.</p>
	 * 
	 * @param s - the new title, or null for no title
	 * @see setTitle in class Displayable
	 * @see Displayable.getTitle()
	 * @since  MIDP 2.0
	 */
	public void setTitle( String s)
	{
		this.title = s;
	}

    /*
     * @see de.enough.polish.ui.Screen#createCssSelector()
     */
    protected String createCssSelector() {
        // TODO ricky implement createCssSelector
        return null;
    }

	

}
