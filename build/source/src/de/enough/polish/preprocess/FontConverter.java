/*
 * Created on 01-Mar-2004 at 15:27:17.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;

import java.util.HashMap;

/**
 * <p>Respresents a J2ME font.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class FontConverter extends Converter {
	private final static HashMap STYLES = new HashMap();
	static {
		STYLES.put( "plain", "STYLE_PLAIN" );
		STYLES.put( "default", "STYLE_PLAIN" );
		STYLES.put( "normal", "STYLE_PLAIN" );
		STYLES.put( "bold", "STYLE_BOLD" );
		STYLES.put( "italic", "STYLE_ITALIC" );
		STYLES.put( "cursive", "STYLE_ITALIC" );
		STYLES.put( "underlined", "STYLE_UNDERLINED" );
	}
	private final static HashMap SIZES = new HashMap();
	static {
		SIZES.put( "small", "SIZE_SMALL" );
		SIZES.put( "medium", "SIZE_MEDIUM" );
		SIZES.put( "default", "SIZE_MEDIUM" );
		SIZES.put( "normal", "SIZE_MEDIUM" );
		SIZES.put( "large", "SIZE_LARGE" );
		SIZES.put( "big", "SIZE_LARGE" );
	}
	private final static HashMap FACES = new HashMap();
	static {
		FACES.put( "system", "FACE_SYSTEM");
		FACES.put( "default", "FACE_SYSTEM");
		FACES.put( "normal", "FACE_SYSTEM");
		FACES.put( "monospace", "FACE_MONOSPACE");
		FACES.put( "proportional", "FACE_PROPORTIONAL");
	}
	
	private String face = "FACE_SYSTEM";
	private String style = "STYLE_PLAIN";
	private String size = "SIZE_MEDIUM";
	
	/**
	 * Creates a new font converter.
	 */
	public FontConverter() {
		// initialisation is done with the setter methods
	}
	

	/**
	 * Retrieves the statement needed to create this font.
	 * 
	 * @return the new statement for the creation of this font.
	 */
	public String createNewStatement() {
		StringBuffer fontCode = new StringBuffer();
		
		// first param is the font face:
		fontCode.append( "Font.getFont( Font.")
				.append( this.face )
				.append(", Font.")
				.append( this.style )
				.append(", Font.")
				.append( this.size )
				.append(" )");
		
		return fontCode.toString(); 
	}
	
	/**
	 * @param face The face to set.
	 */
	public void setFace(String face) {
		face = face.toLowerCase();
		this.face = (String) FACES.get( face );
		if (this.face == null) {
			throw new BuildException("Invalid CSS: the font-face [" + face + "] is not supported. Allowed values are [system], [proportional] and [monospace].");
		}
	}
	
	/**
	 * @param size The size to set.
	 */
	public void setSize(String size) {
		size = size.toLowerCase();
		this.size = (String) SIZES.get( size );
		if (this.size == null) {
			throw new BuildException("Invalid CSS: the font-size [" + size + "] is not supported. Allowed values are [small], [medium] and [large].");
		}
	}
	/**
	 * @param style The style to set.
	 */
	public void setStyle(String style) {
		style = style.toLowerCase();
		this.style = (String) STYLES.get( style );
		if (this.style == null) {
			throw new BuildException("Invalid CSS: the font-style [" + style + "] is not supported. Allowed values are [plain], [bold], [italic] and [underlined].");
		}
	}

}
