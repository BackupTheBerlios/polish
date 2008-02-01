/*
 * Created on Feb 1, 2008 at 12:57:54 AM.
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
package de.enough.polish.font;


import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

/**
 * Converts a true type font into a bitmap font
 * @author vera
 *
 */
public class TrueType2BmfConversion extends TrueType2PngConversion
{
	
	private static final String IMAGE_FORMAT_PNG = "png";
	private File file;
	private boolean hasMixedCase;

	/**
	 * Just for testing purposes
	 */
	protected TrueType2BmfConversion()
	{
		super();
	}
	
	public TrueType2BmfConversion(Font trueTypeFont, Color fontColor, String stringToConvert, boolean isAntiAliased, int characterSpacing, File file)
	{
		super(trueTypeFont, fontColor, stringToConvert, isAntiAliased, characterSpacing);
		
		this.file = file;
		hasMixedCase = hasMixedCase();
	}
	
	protected boolean hasMixedCase()
	{
		char[] characters = stringToConvert.toCharArray();
		char lastChar = 0;
		for (int i = 0; i < characters.length; i++) {
			if( lastChar != 0 ){
				if ((Character.isUpperCase( lastChar ) != Character.isUpperCase( characters[i] ) ) ||
					(Character.isLowerCase( lastChar ) != Character.isLowerCase( characters[i] ) )){
					return true;
				}
			} 
			// Too soon to know
			lastChar = characters[i];
		}
		return false;
	}

	public boolean createBmfFont()
	{
		try
		{
			FileOutputStream out = new FileOutputStream(file);
			DataOutputStream dataOut = new DataOutputStream(out);
			
			dataOut.writeBoolean(hasMixedCase);
			dataOut.writeUTF(stringToConvert);

			BufferedImage image = createFontPng();
			int[] charWidthArray = getCharWidthArray();
			for(int i=0; i<stringToConvert.length(); i++)
			{
				dataOut.writeByte((int)charWidthArray[i]);
			}
			
			ImageIO.write( image, IMAGE_FORMAT_PNG, out );
			out.close();
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
}