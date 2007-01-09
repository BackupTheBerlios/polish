/*
 * Created on Dec 26, 2006 at 10:27:32 AM.
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
package de.enough.polish.rmi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.enough.polish.io.Serializer;

/**
 * <p>Connects to an actual remote server implementation and makes calls to it.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 26, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RemoteServer {
	private static final int RMI_VERSION = 101; // = 1.0.1 (with support for primitive paramters)
	private final Object implementation;
	private static final Map PRIMITIVES_TYPES_MAP = new HashMap();
	static {
		PRIMITIVES_TYPES_MAP.put( Byte.class, Byte.TYPE );
		PRIMITIVES_TYPES_MAP.put( Short.class, Short.TYPE );
		PRIMITIVES_TYPES_MAP.put( Integer.class, Integer.TYPE );
		PRIMITIVES_TYPES_MAP.put( Long.class, Long.TYPE );
		PRIMITIVES_TYPES_MAP.put( Float.class, Float.TYPE );
		PRIMITIVES_TYPES_MAP.put( Double.class, Double.TYPE );
		PRIMITIVES_TYPES_MAP.put( Boolean.class, Boolean.TYPE );
		PRIMITIVES_TYPES_MAP.put( Character.class, Character.TYPE );
	}

	/**
	 * Creates a new server.
	 * 
	 * @param implementation the implementation to which method calls are tunneled
	 */
	public RemoteServer( Object implementation ) {
		this.implementation = implementation;
	}
	
	public void process( DataInputStream in, DataOutputStream out )
	throws IOException
	{
		try {
			
			int version = in.readInt();
			System.out.println("using RMI version=" + version);
			String methodName = in.readUTF();
			System.out.println("requested method: " + methodName );
			long primitivesFlag = 0;
			if (version > 100) {
				primitivesFlag = in.readLong();
				//System.out.println("primitivesFlag=" + primitivesFlag );
			}
			Object[] parameters = (Object[]) Serializer.deserialize(in);
			Class[] signature = null;
			int flag = 1;
			if (parameters != null) {
				signature = new Class[ parameters.length ];
				for (int i = 0; i < parameters.length; i++) {
					Object param = parameters[i];
					// check for primitive wrapper:
					//System.out.println("primitiveFlag=" + primitivesFlag + ", flag=" + flag + ", (primitiveFlag & flag)=" + (primitivesFlag & flag) );
					if ( (primitivesFlag & flag) == 0) {
						// this is a normal class, not a primitive:
						signature[i] = param.getClass();
					} else {
						// this is a primitive
						Class primitiveType = (Class) PRIMITIVES_TYPES_MAP.get( param.getClass() );
						if (primitiveType == null) {
							throw new RemoteException("Invalid primitives flag, please report this error to j2mepolish@enough.de.");
						}
						//System.out.println("using primitive type " + primitiveType.getName() );
						signature[i] = primitiveType;
					}
					flag <<= 1;
				}
			}
			// idee: server kšnnte beim ersten call eine int-ID zurŸckgeben, damit der Methoden-Name nicht jedes Mal Ÿbermittelt werden muss und das lookup schneller geht... (ist allerdings potentielles memory leak)
			Method method = this.implementation.getClass().getMethod( methodName, signature );
			Object returnValue = method.invoke(this.implementation, parameters); // for void methods null is returned...
			out.writeInt( Remote.STATUS_OK );
			Serializer.serialize(returnValue, out);
		} catch (SecurityException e) {
			// TODO robertvirkus handle SecurityException
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (NoSuchMethodException e) {
			// TODO robertvirkus handle NoSuchMethodException
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (IllegalArgumentException e) {
			// TODO robertvirkus handle IllegalArgumentException
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (IllegalAccessException e) {
			// TODO robertvirkus handle IllegalAccessException
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (InvocationTargetException e) {
			// TODO robertvirkus handle InvocationTargetException
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (RemoteException e) {
			e.printStackTrace();
			processRemoteException( e, out );
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void processRemoteException(Throwable e, DataOutputStream out) {
		try {
			out.writeUTF( e.toString() );
			out.writeInt( Remote.STATUS_UNCHECKED_EXCEPTION );
		} catch (IOException e1) {
			// TODO robertvirkus handle IOException
			e1.printStackTrace();
		}
	}

}
