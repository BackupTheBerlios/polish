/*
 * Created on 09-Nov-2004 at 23:01:09.
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
package de.enough.polish.font;


import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JComponent;

/**
 * <p>Shows and manipulates a true type font.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        09-Nov-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TrueTypeFontViewer extends JComponent {
	
	private Font basicFont;
	private Font derivedFont;

	/**
	 * @param fontFile
	 * @throws IOException
	 */
	public TrueTypeFontViewer( File fontFile ) throws IOException {
		super();
		
		InputStream in = new FileInputStream( fontFile );
		try {
			this.basicFont = Font.createFont( Font.TRUETYPE_FONT, in);
			this.derivedFont = this.basicFont.deriveFont( 12F );
			System.out.println("loaded true type font " + fontFile.getName() );
		} catch (FontFormatException e) {
			e.printStackTrace();
			throw new IOException( "Unable to init true type font: " + e.toString() );
		}
	}
	
	


	protected void paintComponent(Graphics g) {
		System.out.println("paint called");
		g.setFont( this.derivedFont );
		g.drawString("Hello World",0, 0);
		//super.paintComponent(g);
	}
	
	public Font getFont() {
		return this.derivedFont;
	}
}
