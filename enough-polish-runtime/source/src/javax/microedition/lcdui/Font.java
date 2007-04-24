// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sun Feb 29 19:10:57 CET 2004
package javax.microedition.lcdui;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.enough.polish.runtime.Simulation;

/**
 * The <code>Font</code> class represents fonts and font
 * metrics. <code>Fonts</code> cannot be
 * created by applications. Instead, applications query for fonts
 * based on
 * font attributes and the system will attempt to provide a font that
 * matches
 * the requested attributes as closely as possible.
 * 
 * <p> A <code>Font's</code> attributes are style, size, and face. Values for
 * attributes must be specified in terms of symbolic constants. Values for
 * the style attribute may be combined using the bit-wise
 * <code>OR</code> operator,
 * whereas values for the other attributes may not be combined. For example,
 * the value </p>
 * 
 * <p> <code>
 * STYLE_BOLD | STYLE_ITALIC
 * </code> </p>
 * 
 * <p> may be used to specify a bold-italic font; however </p>
 * 
 * <p> <code>
 * SIZE_LARGE | SIZE_SMALL
 * </code> </p>
 * 
 * <p> is illegal. </p>
 * 
 * <p> The values of these constants are arranged so that zero is valid for
 * each attribute and can be used to specify a reasonable default font
 * for the system. For clarity of programming, the following symbolic
 * constants are provided and are defined to have values of zero: </p>
 * 
 * <p> <ul>
 * <li> <code> STYLE_PLAIN </code> </li>
 * <li> <code> SIZE_MEDIUM </code> </li>
 * <li> <code> FACE_SYSTEM </code> </li>
 * </ul> </p>
 * 
 * <p> Values for other attributes are arranged to have disjoint bit patterns
 * in order to raise errors if they are inadvertently misused (for example,
 * using <code>FACE_PROPORTIONAL</code> where a style is
 * required). However, the values
 * for the different attributes are not intended to be combined with each
 * other. </p>
 * <HR>
 * 
 * 
 * @since MIDP 1.0
 */
public final class Font extends Object
{
	/**
	 * The plain style constant. This may be combined with the
	 * other style constants for mixed styles.
	 * 
	 * <P>Value <code>0</code> is assigned to <code>STYLE_PLAIN</code>.</P></DL>
	 * 
	 */
	public static final int STYLE_PLAIN = 0;

	/**
	 * The bold style constant. This may be combined with the
	 * other style constants for mixed styles.
	 * 
	 * <P>Value <code>1</code> is assigned to <code>STYLE_BOLD</code>.</P></DL>
	 * 
	 */
	public static final int STYLE_BOLD = 1;

	/**
	 * The italicized style constant. This may be combined with
	 * the other style constants for mixed styles.
	 * 
	 * <P>Value <code>2</code> is assigned to <code>STYLE_ITALIC</code>.</P></DL>
	 * 
	 */
	public static final int STYLE_ITALIC = 2;

	/**
	 * The underlined style constant. This may be combined with
	 * the other style constants for mixed styles.
	 * 
	 * <P>Value <code>4</code> is assigned to <code>STYLE_UNDERLINED</code>.</P></DL>
	 * 
	 */
	public static final int STYLE_UNDERLINED = 4;

	/**
	 * The &quot;small&quot; system-dependent font size.
	 * 
	 * <P>Value <code>8</code> is assigned to <code>STYLE_SMALL</code>.</P></DL>
	 * 
	 */
	public static final int SIZE_SMALL = 8;

	/**
	 * The &quot;medium&quot; system-dependent font size.
	 * 
	 * <P>Value <code>0</code> is assigned to <code>STYLE_MEDIUM</code>.</P></DL>
	 * 
	 */
	public static final int SIZE_MEDIUM = 0;

	/**
	 * The &quot;large&quot; system-dependent font size.
	 * 
	 * <P>Value <code>16</code> is assigned to <code>SIZE_LARGE</code>.</P></DL>
	 * 
	 */
	public static final int SIZE_LARGE = 16;

	/**
	 * The &quot;system&quot; font face.
	 * 
	 * <P>Value <code>0</code> is assigned to <code>FACE_SYSTEM</code>.</P></DL>
	 * 
	 */
	public static final int FACE_SYSTEM = 0;

	/**
	 * The &quot;monospace&quot; font face.
	 * 
	 * <P>Value <code>32</code> is assigned to <code>FACE_MONOSPACE</code>.</P></DL>
	 * 
	 */
	public static final int FACE_MONOSPACE = 32;

	/**
	 * The &quot;proportional&quot; font face.
	 * 
	 * <P>Value <code>64</code> is assigned to
	 * <code>FACE_PROPORTIONAL</code>.</P></DL>
	 * 
	 */
	public static final int FACE_PROPORTIONAL = 64;

	/**
	 * Default font specifier used to draw Item and Screen contents.
	 * 
	 * <code>FONT_STATIC_TEXT</code> has the value <code>0</code>.
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int FONT_STATIC_TEXT = 0;

	/**
	 * Font specifier used by the implementation to draw text input by
	 * a user.
	 * 
	 * <code>FONT_INPUT_TEXT</code> has the value <code>1</code>.
	 * 
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final int FONT_INPUT_TEXT = 1;

	//following variables are implicitely defined by getter- or setter-methods:
	private static Font _defaultFont = new Font( FACE_SYSTEM, STYLE_PLAIN, SIZE_MEDIUM  );
	private int style;
	private int size;
	private int face;
	private int fontHeight;
	private int baselinePosition;
	private final java.awt.Font awtFont;

	private FontRenderContext fontRenderContext;

	private boolean isUnderlined;
	
	/**
	 * Creates an object representing a font having the specified face, style,
	 * and size. If a matching font does not exist, the system will
	 * attempt to provide the closest match. This method <em>always</em>
	 * returns
	 * a valid font object, even if it is not a close match to the request.
	 * 
	 * @param face one of FACE_SYSTEM, FACE_MONOSPACE, or FACE_PROPORTIONAL
	 * @param style STYLE_PLAIN, or a combination of STYLE_BOLD, STYLE_ITALIC, and STYLE_UNDERLINED
	 * @param size one of SIZE_SMALL, SIZE_MEDIUM, or SIZE_LARGE
	 * @throws IllegalArgumentException if face, style, or size are not legal values
	 */
	private Font(int face, int style, int size)
	{
		this.face = face;
		this.style = style;
		this.size = size;
		String awtFace;
		switch (face) {
			case FACE_MONOSPACE:
				awtFace = "COURIER";
				break;
			case FACE_PROPORTIONAL:
				awtFace = "ARIAL";
				break;
			default:
				awtFace = "ARIAL";
		}
		int awtStyle = style;
		this.isUnderlined = ((style & STYLE_UNDERLINED) == STYLE_UNDERLINED);
		if (this.isUnderlined){
			awtStyle &= ~STYLE_UNDERLINED; // awt does not support the UNDERLINED style
		}
//		
//		String styleStr;
//		switch (style) {
//			case STYLE_ITALIC:
//				styleStr = "ITALIC";
//				break;
//			case STYLE_BOLD:
//				styleStr = "BOLD";
//				break;
//			case STYLE_UNDERLINED:
//				//TODO allow underlined fonts
//			default:
//				styleStr = "PLAIN";
//		}
		Simulation simulation = Simulation.getCurrentSimulation();
//		if (simulation != null) {
//			System.out.println("Font-small: " + simulation.getFontSizeSmall());
//			System.out.println("Font-medium: " + simulation.getFontSizeMedium());
//			System.out.println("Font-large: " + simulation.getFontSizeLarge());
//		}
		int awtSize;
		switch (size) {
			case SIZE_SMALL:
				if (simulation == null) {
					awtSize = 14;
				} else {
					awtSize = (simulation.getFontSizeSmall() - 2); // adjust size to compensate for bulky Arial font
				}
				break;
			case SIZE_LARGE:
				if (simulation == null) {
					awtSize = 19;
				} else {
					awtSize  = (simulation.getFontSizeLarge() - 2);
				}
				break;
			default:
				if (simulation == null) {
					awtSize = 15;
				} else {
					awtSize = (simulation.getFontSizeMedium() - 2);
				}
		}
		//this.awtFont = java.awt.Font.decode( faceStr + "-" + styleStr + "-" + sizeStr );
		this.awtFont = new java.awt.Font( awtFace, awtStyle, awtSize );
		BufferedImage image =
		    new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		this.fontRenderContext = g.getFontRenderContext();
		LineMetrics lineMetrics = this.awtFont.getLineMetrics("ABCdefg", this.fontRenderContext);
		this.baselinePosition = (int) lineMetrics.getAscent();
		this.fontHeight = (int) lineMetrics.getHeight();
	}

	/**
	 * Gets the <code>Font</code> used by the high level user interface
	 * for the <code>fontSpecifier</code> passed in. It should be used
	 * by subclasses of
	 * <code>CustomItem</code> and <code>Canvas</code> to match user
	 * interface on the device.
	 * 
	 * @param fontSpecifier - one of FONT_INPUT_TEXT, or FONT_STATIC_TEXT
	 * @return font that corresponds to the passed in font specifier
	 * @throws IllegalArgumentException - if fontSpecifier is not a valid fontSpecifier
	 * @since  MIDP 2.0
	 */
	public static Font getFont(int fontSpecifier)
	{
		return null;
		//TODO implement getFont
	}

	/**
	 * Gets the default font of the system.
	 * 
	 * @return the default font
	 */
	public static Font getDefaultFont()
	{
		return _defaultFont;
	}

	/**
	 * Obtains an object representing a font having the specified face, style,
	 * and size. If a matching font does not exist, the system will
	 * attempt to provide the closest match. This method <em>always</em>
	 * returns
	 * a valid font object, even if it is not a close match to the request.
	 * 
	 * @param face - one of FACE_SYSTEM, FACE_MONOSPACE, or FACE_PROPORTIONAL
	 * @param style - STYLE_PLAIN, or a combination of STYLE_BOLD, STYLE_ITALIC, and STYLE_UNDERLINED
	 * @param size - one of SIZE_SMALL, SIZE_MEDIUM, or SIZE_LARGE
	 * @return instance the nearest font found
	 * @throws IllegalArgumentException - if face, style, or size are not legal values
	 */
	public static Font getFont(int face, int style, int size)
	{
		return new Font( face, style, size );
	}

	/**
	 * Gets the style of the font. The value is an <code>OR'ed</code>
	 * combination of
	 * <code>STYLE_BOLD</code>, <code>STYLE_ITALIC</code>, and
	 * <code>STYLE_UNDERLINED</code>; or the value is
	 * zero (<code>STYLE_PLAIN</code>).
	 * 
	 * @return style of the current font
	 * @see #isPlain()
	 * @see #isBold()
	 * @see #isItalic()
	 */
	public int getStyle()
	{
		return this.style;
	}

	/**
	 * Gets the size of the font.
	 * 
	 * @return one of SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE
	 */
	public int getSize()
	{
		return this.size;
	}

	/**
	 * Gets the face of the font.
	 * 
	 * @return one of FACE_SYSTEM, FACE_PROPORTIONAL, FACE_MONOSPACE
	 */
	public int getFace()
	{
		return this.face;
	}

	/**
	 * Returns <code>true</code> if the font is plain.
	 * 
	 * @return true if font is plain
	 * @see #getStyle()
	 */
	public boolean isPlain()
	{
		return this.style == STYLE_PLAIN;
	}

	/**
	 * Returns <code>true</code> if the font is bold.
	 * 
	 * @return true if font is bold
	 * @see #getStyle()
	 */
	public boolean isBold()
	{
		return (( this.style & STYLE_BOLD) == STYLE_BOLD);
	}

	/**
	 * Returns <code>true</code> if the font is italic.
	 * 
	 * @return true if font is italic
	 * @see #getStyle()
	 */
	public boolean isItalic()
	{
		return (( this.style & STYLE_ITALIC) == STYLE_ITALIC);
	}

	/**
	 * Returns <code>true</code> if the font is underlined.
	 * 
	 * @return true if font is underlined
	 * @see #getStyle()
	 */
	public boolean isUnderlined()
	{
		return (( this.style & STYLE_UNDERLINED) == STYLE_UNDERLINED);
	}

	/**
	 * Gets the standard height of a line of text in this font. This value
	 * includes sufficient spacing to ensure that lines of text painted this
	 * distance from anchor point to anchor point are spaced as intended by the
	 * font designer and the device. This extra space (leading) occurs below
	 * the text.
	 * 
	 * @return standard height of a line of text in this font (a  non-negative value)
	 */
	public int getHeight()
	{
		return this.fontHeight;
	}

	/**
	 * Gets the distance in pixels from the top of the text to the text's
	 * baseline.
	 * 
	 * @return the distance in pixels from the top of the text to the text's baseline
	 */
	public int getBaselinePosition()
	{
		return this.baselinePosition;
	}

	/**
	 * Gets the advance width of the specified character in this Font.
	 * The advance width is the horizontal distance that would be occupied if
	 * <code>ch</code> were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * <code>ch</code> necessary for proper positioning of subsequent text.
	 * 
	 * @param ch - the character to be measured
	 * @return the total advance width (a non-negative value)
	 */
	public int charWidth(char ch)
	{
		return stringWidth( new String( new char[]{ ch }) );
	}

	/**
	 * Returns the advance width of the characters in <code>ch</code>,
	 * starting at the specified offset and for the specified number of
	 * characters (length).
	 * The advance width is the horizontal distance that would be occupied if
	 * the characters were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * the characters necessary for proper positioning of subsequent text.
	 * 
	 * <p>The <code>offset</code> and <code>length</code> parameters must
	 * specify a valid range of characters
	 * within the character array <code>ch</code>. The <code>offset</code>
	 * parameter must be within the
	 * range <code>[0..(ch.length)]</code>, inclusive.
	 * The <code>length</code> parameter must be a non-negative
	 * integer such that <code>(offset + length) &lt;= ch.length</code>.</p>
	 * 
	 * @param ch - the array of characters
	 * @param offset - the index of the first character to measure
	 * @param length - the number of characters to measure
	 * @return the width of the character range
	 * @throws ArrayIndexOutOfBoundsException - if offset and length specify an invalid range
	 * @throws NullPointerException - if ch is null
	 */
	public int charsWidth(char[] ch, int offset, int length)
	{
		return stringWidth( new String( ch, offset, length) );
	}

	/**
	 * Gets the total advance width for showing the specified
	 * <code>String</code>
	 * in this <code>Font</code>.
	 * The advance width is the horizontal distance that would be occupied if
	 * <code>str</code> were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * <code>str</code> necessary for proper positioning of subsequent text.
	 * 
	 * @param str - the String to be measured
	 * @return the total advance width
	 * @throws NullPointerException - if str is null
	 */
	public int stringWidth( String str)
	{
		Rectangle2D bounds = this.awtFont.getStringBounds( str, this.fontRenderContext);
		//System.out.println("Font.stringWidth( " + str + " ) = " + bounds.getWidth());
		return (int) bounds.getWidth();
	}

	/**
	 * Gets the total advance width for showing the specified substring in this
	 * <code>Font</code>.
	 * The advance width is the horizontal distance that would be occupied if
	 * the substring were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * the substring necessary for proper positioning of subsequent text.
	 * 
	 * <p>
	 * The <code>offset</code> and <code>len</code> parameters must
	 * specify a valid range of characters
	 * within <code>str</code>. The <code>offset</code> parameter must
	 * be within the
	 * range <code>[0..(str.length())]</code>, inclusive.
	 * The <code>len</code> parameter must be a non-negative
	 * integer such that <code>(offset + len) &lt;= str.length()</code>.
	 * </p>
	 * 
	 * @param str the String to be measured
	 * @param offset zero-based index of first character in the substring
	 * @param len length of the substring
	 * @return the total advance width
	 * @throws StringIndexOutOfBoundsException if offset and length specify an invalid range
	 * @throws NullPointerException if str is null
	 */
	public int substringWidth( String str, int offset, int len)
	{
		return stringWidth( str.substring( offset, len ) );
	}

	/**
	 * @return the base AWT font
	 */
	public java.awt.Font _getAwtFont() {
		return this.awtFont;
	}

}
