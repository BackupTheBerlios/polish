//#condition polish.cldc
/*
 * Created on 12-Oct-2005 at 09:45:32.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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

package de.enough.polish.codec;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Decodes hex encoded data on the fly.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        24-Oct-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HexInputStream extends InputStream {

	private final InputStream in;

	public HexInputStream( InputStream in) {
		super();
		this.in = in;
	}

	public int read() throws IOException {
		// we assume that the characters are encoded as ASCII,
		// so there is one byte for each character
//        int res =  (this.in.read() << 4) | this.in.read();
//        return (res & 0xFF);
		char firstChar = (char) this.in.read(); // ((this.in.read() << 8) | this.in.read() );
		char secondChar = (char) this.in.read(); // ((this.in.read() << 8) | this.in.read() );
		//System.out.print( firstChar + secondChar );
		int f = Character.digit( firstChar, 16 ) << 4;
        f = f | Character.digit( secondChar, 16 );
        //TODO there should be an easier/faster method for without using characters at all
        return (f & 0xFF);
	}

}
