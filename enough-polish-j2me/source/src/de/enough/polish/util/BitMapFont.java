/*
 * Created on 08-Nov-2004 at 23:59:52.
 * 
 * Copyright (c) 2004 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Image;

/**
 * <p>Can be used to use any kind of bitmap fonts.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        08-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class BitMapFont {
	
	private String fontUrl;
	private Image fontImage;
	private boolean hasMixedCase;
	private byte[] characterWidths;
	private char[] characterMap;
	private int fontHeight;

	/**
	 * @param fontUrl
	 * @param hasMixedCase
	 */
	public BitMapFont( String fontUrl ) {
		super();
		this.fontUrl = fontUrl;
	}
	
	/**
	 * Creates a viewer object for the given string.
	 * 
	 * @param input the input which should be shown.
	 * @return a viewer object which shows the font in a performant manner
	 */
	public BitMapFontViewer getViewer( String input ) {
		if (this.fontImage == null) {
			InputStream in = null;
			try {
				in = getClass().getResourceAsStream(this.fontUrl);
				if (in == null) {
					return null;
				}
				DataInputStream dataIn = new DataInputStream( in );
				this.hasMixedCase = dataIn.readBoolean();
				String map = dataIn.readUTF();
				this.characterMap = map.toCharArray();
				int length = map.length();
				this.characterWidths = new byte[ length ];
				for (int i = 0; i < length; i++ ) {
					this.characterWidths[i] = dataIn.readByte();
				}
				this.fontImage = Image.createImage( in );
				this.fontHeight = this.fontImage.getHeight();
				this.fontUrl = null;
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to load bitmap-font [" + this.fontUrl + "]" + e);
				return null;
			} finally {
				try { 
					in.close();
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to close bitmap-font stream" + e);
				}
			}
		}
		//int imageWidth = this.fontImage.getWidth();
		// get the x/y-position and width for each character:
		if (!this.hasMixedCase) {
			input = input.toLowerCase();
		}
		int length = input.length();
		short[] xPositions = new short[ length ];
		//short[] yPositions = new short[ length ];
		byte[] widths = new byte[ length ];
		int completeWidth = 0;
		for (int i = length - 1; i >= 0; i-- ) {
			char inputCharacter = input.charAt(i);
			short xPos = 0;
			//short yPos = 0;
			for (int j = 0; j < this.characterMap.length; j++ ) {
				char mapCharacter = this.characterMap[j];
				byte width = this.characterWidths[ j ];
				if ( inputCharacter == mapCharacter ) {
					xPositions[ i ] = xPos;
					widths[ i ] = width;
					//yPositions[ i ] = yPos;
					completeWidth += width;
					break;
				}
				xPos += width;
				//if (xPos >= imageWidth) {
				//	xPos = 0;
					//yPos += this.fontHeight;
				//}
			}
		}
		return new BitMapFontViewer( this.fontImage, xPositions, widths, completeWidth, this.fontHeight );
	}

}
