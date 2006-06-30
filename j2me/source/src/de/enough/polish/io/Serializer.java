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
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

//#if polish.midp
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Font;
//#endif
//#if polish.midp2
import javax.microedition.lcdui.Image;
//#endif

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
	
	private static final byte VERSION = 1; // version starts at 1 and is then increased up to 127 when incompatible changes occur
	private static final byte TYPE_EXTERNALIZABLE = 0;
	private static final byte TYPE_EXTERNALIZABLE_ARRAY = 1;
	private static final byte TYPE_OBJECT_ARRAY = 2;
	private static final byte TYPE_BYTE = 3;
	private static final byte TYPE_SHORT = 4;
	private static final byte TYPE_INTEGER = 5;
	private static final byte TYPE_LONG = 6;
	private static final byte TYPE_FLOAT = 7;
	private static final byte TYPE_DOUBLE = 8;
	private static final byte TYPE_STRING = 9;
	private static final byte TYPE_STRING_BUFFER = 10;
	private static final byte TYPE_CHARACTER = 11;
	private static final byte TYPE_BOOLEAN = 12;
	private static final byte TYPE_DATE = 13;
	private static final byte TYPE_CALENDAR = 14;
	private static final byte TYPE_RANDOM = 15;
	private static final byte TYPE_HASHTABLE = 16;
	private static final byte TYPE_STACK = 17;
	private static final byte TYPE_VECTOR = 18;
	private static final byte TYPE_IMAGE = 19;
	private static final byte TYPE_FONT = 20;
	private static final byte TYPE_COMMAND = 21;
	
	private Serializer() {
		// no instantiation allowed
	}
	
	
//	/**
//	 * Serializes the given object.
//	 * 
//	 * @param serializable the serializable object or null
//	 * @param out the data output stream, into which the object is serialized
//	 * @throws IOException when serialization data could not be written
//	 */
//	public static void serialize( Serializable serializable, DataOutputStream out )
//	throws IOException
//	{
//		out.writeByte( VERSION );
//		boolean isNull = (serializable == null);
//		out.writeBoolean( isNull );
//		if ( !isNull ) {
//			Externalizable extern = (Externalizable) serializable;
//			out.writeUTF( extern.getClass().getName() );
//			extern.write( out );		
//		}
//	}
	
//	/**
//	 * Serializes the given array.
//	 * <b>WARNING: </b> The specified array elements need to be of the same type, this is not checked during runtime!
//	 * 
//	 * @param serializables the array with serializable objects of the same type or null
//	 * @param out the data output stream, into which the objects are serialized
//	 * @throws IOException when serialization data could not be written
//	 */
//	public static void serializeArray( Serializable[] serializables, DataOutputStream out )
//	throws IOException
//	{
//		out.writeByte( VERSION );
//		boolean isNull = (serializables == null);
//		out.writeBoolean( isNull );
//		if ( !isNull ) {
//			out.writeInt( serializables.length );
//			if ( serializables.length > 0 ) {
//				Serializable serializable = serializables[0];
//				out.writeUTF( serializable.getClass().getName() );
//				for (int i = 0; i < serializables.length; i++) {
//					Externalizable extern = (Externalizable) serializables[i];
//					out.writeUTF( extern.getClass().getName() );
//					extern.write( out );		
//				}			
//			}
//		}
//	}
	/**
	 * Serializes an Object like java.lang.Integer, java.util.Date, javax.microedition.lcdui.Image etc.
	 * 
	 * @param object the object
	 * @param out the stream into which the object should be serialized
	 * @throws IOException when serialization data could not be written or when encountering an object that cannot be serialized
	 * @see #deserialize(DataInputStream) for deseralizing objects
	 */
	public static void serialize( Object object, DataOutputStream out ) 
	throws IOException 
	{
		out.writeByte( VERSION );
		boolean isNull = (object == null);
		out.writeBoolean( isNull );
		if ( !isNull ) {
			if (object instanceof Externalizable) { 
				out.writeByte(TYPE_EXTERNALIZABLE);
				out.writeUTF( object.getClass().getName() );
				((Externalizable)object).write(out);
			} else if (object instanceof Externalizable[]) { 
				out.writeByte(TYPE_EXTERNALIZABLE_ARRAY);
				Externalizable[] externalizables = (Externalizable[]) object;
				out.writeInt( externalizables.length );
				Hashtable classNames = new Hashtable();
				Class lastClass = null;
				byte lastId = 0;
				byte idCounter = 0;
				for (int i = 0; i < externalizables.length; i++) {
					Externalizable externalizable = externalizables[i];
					Class currentClass = externalizable.getClass();
					if (currentClass == lastClass ) {
						out.writeByte( lastId );
					} else {
						Byte knownId = (Byte) classNames.get( currentClass );
						if (knownId != null) {
							out.writeByte( knownId.byteValue() );
						} else {
							// this is a class that has not yet been encountered:
							out.writeByte( 0 );
							idCounter++;
							out.writeUTF( currentClass.getName() );
							lastClass = currentClass;
							lastId = idCounter;
							classNames.put( currentClass, new Byte( lastId ) );
						}
					}
					externalizable.write(out);
				}
				((Externalizable)object).write(out);
			} else if (object instanceof Object[]) { 
				out.writeByte(TYPE_OBJECT_ARRAY);
				Object[] objects = (Object[]) object;
				out.writeInt( objects.length );
				for (int i = 0; i < objects.length; i++) {
					Object obj = objects[i];
					serialize(obj, out);
				}
			} else if (object instanceof Byte) {
				out.writeByte(TYPE_BYTE);
				out.writeByte( ((Byte)object).byteValue() );
			} else if (object instanceof Short) {
				out.writeByte(TYPE_SHORT);
				out.writeShort( ((Short)object).shortValue() );
			} else if (object instanceof Integer) {
				out.writeByte(TYPE_INTEGER);
				out.writeInt( ((Integer)object).intValue() );
			} else if (object instanceof Long) {
				out.writeByte(TYPE_LONG);
				out.writeLong( ((Long)object).longValue() );
			//#if polish.hasFloatingPoint
			} else if (object instanceof Float) {
				out.writeByte(TYPE_FLOAT);
				out.writeFloat( ((Float)object).floatValue() );
			} else if (object instanceof Double) {
				out.writeByte(TYPE_DOUBLE);
				out.writeDouble( ((Double)object).doubleValue() );
			//#endif
			} else if (object instanceof String) {
				out.writeByte(TYPE_STRING);
				out.writeUTF( (String)object );
			} else if (object instanceof StringBuffer) {
				out.writeByte(TYPE_STRING_BUFFER);
				out.writeUTF( ((StringBuffer)object).toString()  );
			} else if (object instanceof Character) {
				out.writeByte(TYPE_CHARACTER);
				out.writeChar( ((Character)object).charValue() );
			} else if (object instanceof Boolean) {
				out.writeByte(TYPE_BOOLEAN);
				out.writeBoolean( ((Boolean)object).booleanValue() );
			} else if (object instanceof Date) {
				out.writeByte(TYPE_DATE);
				out.writeLong( ((Date)object).getTime() );
			} else if (object instanceof Calendar) {
				out.writeByte(TYPE_CALENDAR);
				out.writeLong( ((Calendar)object).getTime().getTime() );
			} else if (object instanceof Random) {
				out.writeByte(TYPE_RANDOM);
			} else if (object instanceof Hashtable) {
				out.writeByte(TYPE_HASHTABLE);
				Hashtable table = (Hashtable) object;
				out.writeInt( table.size() );
				Enumeration enumeration = table.keys();
				while( enumeration.hasMoreElements() ) {
					Object key = enumeration.nextElement();
					serialize(key, out);
					Object value = table.get( key );
					serialize(value, out);
				}
			} else if (object instanceof Vector) { // also serializes stacks
				if (object instanceof Stack) {
					out.writeByte(TYPE_STACK);					
				} else {
					out.writeByte(TYPE_VECTOR);
				}
				Vector vector = (Vector) object;
				int size = vector.size();
				out.writeInt( size );
				for (int i = 0; i < size; i++) {
					serialize( vector.elementAt(i), out );
				}
			//#if polish.midp2
			} else if (object instanceof Image) {
				out.writeByte(TYPE_IMAGE);
				Image image = (Image) object;
				int width = image.getWidth();
				int height = image.getHeight();
				out.writeInt( width );
				out.writeInt( height );
				int[] rgb = new int[ width * height ];
				image.getRGB(rgb, 0, width, 0, 0, width, height);
				for (int i = 0; i < rgb.length; i++) {
					out.writeInt( rgb[i] );
				}
			//#endif
			//#if polish.midp
			} else if (object instanceof Font) {
				out.writeByte(TYPE_FONT);
				Font font = (Font) object;
				out.writeInt( font.getFace() );
				out.writeInt( font.getStyle() );
				out.writeInt( font.getSize() );
			} else if (object instanceof Command) {
				out.writeByte(TYPE_COMMAND);
				Command command = (Command) object;
				out.writeInt( command.getCommandType() );
				out.writeInt( command.getPriority() );
				out.writeUTF( command.getLabel() );
			//#endif
			} else {
				throw new IOException("Cannot serialize " + object.getClass().getName() );
			}
		}
		
		/*
		 * Special case is the javax.microedition.lcdui.Image – we use getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) and createRGBImage(int[] rgb, int width, int height, boolean processAlpha) for serialization.
		 */
	}


	/**
	 * Deserializes an object from the given stream.
	 * 
	 * @param in the data input stream, from which the object is deserialized
	 * @return the serializable object
	 * @throws IOException when serialization data could not be read or the Serializable class could not get instantiated
	 */
	public static Object deserialize( DataInputStream in )
	throws IOException
	{
		byte version = in.readByte();
		//#if polish.debug.warn
			if (version > VERSION) {
				//#debug warn
				System.out.println("Warning: trying to deserialize class that has been serialized with a newer version (" + version + ">" + VERSION + ").");
			}
		//#endif
		boolean isNull = in.readBoolean();
		if (isNull) {
			return null;
		}
		byte type = in.readByte();
		switch (type) {
		case TYPE_EXTERNALIZABLE:
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
		case TYPE_EXTERNALIZABLE_ARRAY:
			int length = in.readInt();
			Externalizable[] externalizables = new Externalizable[ length ];
			Class[] classes = new Class[ Math.max( length, 7 ) ];
			Class currentClass;
			byte idCounter = 0;
			for (int i = 0; i < externalizables.length; i++) {
				int classId = in.readByte();
				if (classId == 0) { // new class name
					className = in.readUTF();
					try {
						currentClass = Class.forName( className );
					} catch (ClassNotFoundException e) {
						//#debug error
						System.out.println("Unable to load Serializable class \"" + className + "\"" + e);
						throw new IOException( e.toString() );
					}
					if (idCounter > classes.length ) {
						Class[] newClasses = new Class[ classes.length + 7 ];
						System.arraycopy(classes, 0, newClasses, 0, classes.length);
						classes = newClasses;
					}
					classes[idCounter] = currentClass;
					idCounter++;
				} else {
					currentClass = classes[ classId ];
				}
				Externalizable externalizable;
				try {
					externalizable = (Externalizable) currentClass.newInstance();
					externalizable.read(in);
					externalizables[i] = externalizable;
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to instantiate Serializable \"" + currentClass.getName() + "\"" + e);
					throw new IOException( e.toString() );
				}				
			}
			return externalizables;
		case TYPE_OBJECT_ARRAY:
			length = in.readInt();
			Object[] objects = new Object[ length ];
			for (int i = 0; i < objects.length; i++) {
				objects[i] = deserialize(in);
			}
			return objects;
		case TYPE_BYTE:
			return new Byte( in.readByte() );
		case TYPE_SHORT:
			return new Short( in.readShort() );
		case TYPE_INTEGER:
			return new Integer( in.readInt() );
		case TYPE_LONG:
			return new Long( in.readLong() );
		//#if polish.hasFloatingPoint
		case TYPE_FLOAT:
			return new Float( in.readFloat() );
		case TYPE_DOUBLE:
			return new Double( in.readDouble() );
		//#endif
		case TYPE_STRING:
			return in.readUTF();
		case TYPE_STRING_BUFFER:
			return new StringBuffer( in.readUTF() );
		case TYPE_CHARACTER:
			return new Character( in.readChar() );
		case TYPE_BOOLEAN:
			return new Boolean( in.readBoolean() );
		case TYPE_DATE:
			return new Date( in.readLong() );
		case TYPE_CALENDAR:
			Calendar calendar = Calendar.getInstance();
			calendar.setTime( new Date(in.readLong()) );
			return calendar;
		case TYPE_RANDOM:
			return new Random();
		case TYPE_HASHTABLE:
			int size = in.readInt();
			Hashtable hashtable = new Hashtable( size );
			for (int i = 0; i < size; i++) {
				Object key = deserialize(in);
				Object value = deserialize(in);
				hashtable.put( key, value );
			}
			return hashtable;
		case TYPE_STACK:
		case TYPE_VECTOR:
			size = in.readInt();
			Vector vector;
			if (type == TYPE_STACK) {
				vector = new Stack();
			} else {
				vector = new Vector( size );
			}
			for (int i = 0; i < size; i++) {
				Object value = deserialize(in);
				vector.addElement( value );
			}
			return vector;
		//#if polish.midp2
		case TYPE_IMAGE:
			int width = in.readInt();
			int height = in.readInt();
			int[] rgb = new int[ width * height ];
			for (int i = 0; i < rgb.length; i++) {
				rgb[i] = in.readInt();
			}
			return Image.createRGBImage(rgb, width, height, true );
		//#endif
		//#if polish.midp
		case TYPE_FONT:
			int face = in.readInt();
			int style = in.readInt();
			size = in.readInt();
			return Font.getFont(face, style, size);
		case TYPE_COMMAND:
			int cmdType = in.readInt();
			int priority = in.readInt();
			String label = in.readUTF();
			return new Command( label, cmdType, priority );
		//#endif
		default: 
			throw new IOException("Unknown type: " + type );
		}
	}
	
//	/**
//	 * Deserializes an object array from the given stream.
//	 * 
//	 * @return the serializable object
//	 * @param in the data input stream, from which the object is deserialized
//	 * @throws IOException when serialization data could not be read or the Serializable class could not get instantiated
//	 */
//	public static Serializable[] deserializeArray( DataInputStream in )
//	throws IOException
//	{
//		byte version = in.readByte();
//		//#if polish.debug.warn
//			if (version > VERSION) {
//				//#debug warn
//				System.out.println("Warning: trying to deserialize array that has been serialized with a newer version (" + version + ">" + VERSION + ").");
//			}
//		//#endif
//		boolean isNull = in.readBoolean();
//		if (isNull) {
//			return null;
//		}
//		int length = in.readInt();
//		Serializable[] serializables = new Serializable[ length ];
//		if ( length > 0 ) {
//			String className = in.readUTF();
//			try {
//				Class serialClass = Class.forName( className );
//				for (int i = 0; i < serializables.length; i++) {
//					Externalizable extern = (Externalizable) serialClass.newInstance();
//					extern.read( in );
//					serializables[i] = extern;
//				}
//			} catch (IOException e) {
//				//#debug error 
//				System.out.println("Unable to deserlize array of \"" + className + "\"" + e );
//				throw e;
//			} catch (Exception e) {
//				//#debug error 
//				System.out.println("Unable to deserlize array of \"" + className + "\"" + e );
//				throw new IOException( e.toString() );
//			}
//		}
//		return serializables;
//	}

}
