/*
 * Created on Jul 28, 2007 at 12:40:56 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Jul 28, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ImageUtilTest extends TestCase {
	
	public ImageUtilTest( String name ) {
		super( name );
	}
	
	public void testParticleScale() {
		// was jsut used to test the performance of different implementations, is now obsolete
		int rounds = 100; //30 * 1000;
		int width = 100;
		int height = 100;
		int startFactor = 120;
		int endFactor = 260;
		int[] source = new int[ width * height ];
		int[] target = new int[ width * height ];
		long startTime = System.currentTimeMillis();
		for (int i=0; i<rounds; i++) {
			int factor = startFactor + ((endFactor - startFactor) * i) / rounds;
			ImageUtil.particleScale( factor, width, height, source, target);
		}
		System.out.println("    optimized used time=" + getTime(System.currentTimeMillis() - startTime));
	}
	
	private String getTime( long time ) {
		StringBuffer buffer = new StringBuffer();
		int seconds = (int) (time / 1000);
		buffer.append( seconds ).append("s ");
		time -= seconds * 1000;
		buffer.append( time ).append("ms ");
		return buffer.toString();
	}
	
	public void testMergePixels() {
		int[] pixels;
		int[] percentages;
		int result;
		
		pixels = new int[]{ 0xff00ff00, 0xffff0000 };
		percentages = new int[] {255, 255};
		result = ImageUtil.merge(pixels, percentages);
		System.out.println(Integer.toHexString( result));
		assertEquals( 0xff7f7f00, result);
		
		pixels = new int[]{ 0xff00ff00, 0xffff0000 };
		percentages = new int[] {255, 0};
		result = ImageUtil.merge(pixels, percentages);
		System.out.println(Integer.toHexString( result));
		assertEquals( 0xff00ff00, result);

	}

}
