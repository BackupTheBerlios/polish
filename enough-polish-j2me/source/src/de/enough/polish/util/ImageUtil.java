/*
 * Created on 15-May-2005 at 21:28:30.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

/**
 * <p>Helps to transform images</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        15-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class ImageUtil {

	/**
	 * No instantiation is allowed
	 */
	private ImageUtil() {
		super();
	}
	
	public static void scale(int[] scaledRgbData, int scaleFactor, int width, int height, int[] rgbData) {
		int yStart = ((height - height * 100 / scaleFactor ) / 2) * width;
		int xStart = (width - width * 100 / scaleFactor ) / 2;
		for (int y = 0; y < height; y++) {
			int c1 = y * width;
			int c2 = yStart + (y * 100  / scaleFactor) * width;
			for (int x = 0; x < width; x++) {
				scaledRgbData[c1 + x] = rgbData[ c2 + xStart + x * 100/scaleFactor ];
			}
		}
	}

	
	public static int[] scale(int scaledWidth, int scaledHeight, int scanlength, int sourceWidth, int sourceHeight, int[] rgbData) {
		int scaledRgbData[] = new int[scaledWidth * scaledHeight];
		for (int y = 0; y < scaledHeight; y++) {
			int c1 = y * scaledWidth;
			int c2 = (y * sourceHeight / scaledHeight) * scanlength;
			for (int x = 0; x < scaledWidth; x++) {
				scaledRgbData[c1 + x] = rgbData[c2 + x * sourceWidth / scaledWidth];
			}
		}
		return scaledRgbData;
	}
	


}
