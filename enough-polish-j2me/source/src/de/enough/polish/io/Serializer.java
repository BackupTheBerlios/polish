/*
 * Created on Mar 31, 2006 at 4:18:12 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>The serializer class is used for serializing and de-serializing objects in a unified way.</p>
 * <p>High level serialization components like the RmsStorage use this helper class for the actual serialization.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Mar 31, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class Serializer {
	
	private Serializer() {
		// no instantiation allowed
	}
	
	
	/**
	 * Serializes the given object.
	 * 
	 * @param serializable the serializable object
	 * @param out the data output stream, into which the object is serialized
	 * @throws IOException when serialization data could not be written
	 */
	public static void serialize( Serializable serializable, DataOutputStream out )
	throws IOException
	{
		Externalizable extern = (Externalizable) serializable;
		out.writeUTF( extern.getClass().getName() );
		extern.write( out );		
	}
	
	/**
	 * Serializes the given array.
	 * <b>WARNING: </b> The specified array elements need to be of the same type, this is not checked during runtime!
	 * 
	 * @param serializables the array with serializable objects of the same type
	 * @param out the data output stream, into which the objects are serialized
	 * @throws IOException when serialization data could not be written
	 */
	public static void serializeArray( Serializable[] serializables, DataOutputStream out )
	throws IOException
	{
		out.writeInt( serializables.length );
		if ( serializables.length > 0 ) {
			Serializable serializable = serializables[0];
			out.writeUTF( serializable.getClass().getName() );
			for (int i = 0; i < serializables.length; i++) {
				Externalizable extern = (Externalizable) serializables[i];
				out.writeUTF( extern.getClass().getName() );
				extern.write( out );		
			}			
		}
	}


	/**
	 * Deserializes an object from the given stream.
	 * 
	 * @return the serializable object
	 * @param in the data input stream, from which the object is deserialized
	 * @throws IOException when serialization data could not be read or the Serializable class could not get instantiated
	 */
	public static Serializable deserialize( DataInputStream in )
	throws IOException
	{
		String className = in.readUTF();
		Externalizable extern = null;
		try {
			extern = (Externalizable) Class.forName( className ).newInstance();
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to instantiate Serializable \"" + className + "\"" + e);
			throw new IOException( e.toString() );
		}
		extern.read( in );
		return extern;
	}
	
	/**
	 * Deserializes an object array from the given stream.
	 * 
	 * @return the serializable object
	 * @param in the data input stream, from which the object is deserialized
	 * @throws IOException when serialization data could not be read or the Serializable class could not get instantiated
	 */
	public static Serializable[] deserializeArray( DataInputStream in )
	throws IOException
	{
		int length = in.readInt();
		Serializable[] serializables = new Serializable[ length ];
		if ( length > 0 ) {
			String className = in.readUTF();
			try {
				Class serialClass = Class.forName( className );
				for (int i = 0; i < serializables.length; i++) {
					Externalizable extern = (Externalizable) serialClass.newInstance();
					extern.read( in );
					serializables[i] = extern;
				}
			} catch (IOException e) {
				//#debug error 
				System.out.println("Unable to deserlize array of \"" + className + "\"" + e );
				throw e;
			} catch (Exception e) {
				//#debug error 
				System.out.println("Unable to deserlize array of \"" + className + "\"" + e );
				throw new IOException( e.toString() );
			}
		}
		return serializables;
	}


}
