/*
 * Created on Jul 23, 2007 at 10:53:12 AM.
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.enough.polish.util.zip.*;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Jul 23, 2007 - Simon creation
 * </pre>
 * @author Simon Schmitt, simon.schmitt@enough.de
 */
public final class ZipUtil {
	public static byte[] decompress( byte[] data ) throws IOException{
		return decompress(data,ZipHelper.TYPE_DEFLATE);
	}
	public static byte[] decompress( byte[] data , int compressionType) throws IOException{
		byte[] tmp=new byte[1024];
		int read;
		
		ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream( data ) ,1024 ,compressionType,true);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
		
		// take from ZipInputStream and fill into ByteArrayOutputStream
		while ( (read=zipInputStream.read(tmp, 0, 1024))>0 ){
			bout.write(tmp,0,read);
		}
		
		return bout.toByteArray();
	}
	
}
