/*
 * Created on Dec 28, 2006 at 4:08:53 AM.
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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.enough.polish.io.Externalizable;
import de.enough.polish.io.Serializer;

/**
 * <p>A Servlet that tunnels any incoming requests to the RemoteServer, which in turn forwards any calls to the actual implementation object.</p>
 * <p>This servlet requires the "polish.rmi.server" init parameter within its web.xml configuration file. 
 *    You can add your own configuration variables by implementing the method <code>public void init( java.util.Map config )</code> or 
 *    <code>public void init( javax.servlet.ServletConfig config )</code>. 
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 19, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RemoteHttpServlet extends HttpServlet {
	
	private static final long serialVersionUID = -8191803948220688172L;
	
	/** The object that implements a specific Remote interface, which can actually be this servlet (when a user choses to extend this servlet). */
	protected Object implementation;
	/** A map used converting primitive classes into the appropriate primite TYPE types */
	protected static final Map PRIMITIVES_TYPES_MAP = new HashMap();
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
	
	/** Contains a HttServletRequest for each thread. */
	protected final Map requestsByThread;

	/** Creates a new RemoteHttpServlet */
	public RemoteHttpServlet() {
		super();
		this.requestsByThread = new HashMap();
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		if (!getClass().getName().equals("RemoteHttpServlet")) {
			// the user has extended this servlet, which is useful for getting sessions and so on.
			// In this case, no further configuration is needed.
			this.implementation = this;
			return;
		}
		String implementationClassName = cfg.getInitParameter("polish.rmi.server");
		if (implementationClassName == null) {
			throw new ServletException("no \"polish.rmi.server\" parameter given.");
		}
		Class implementationClass = null;
		try {
			implementationClass = Class.forName( implementationClassName );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ServletException("\"polish.rmi.server\" cannot be resolved: " + e.toString() );
		}
		try {
			this.implementation = implementationClass.newInstance();
			try {
				// check out if there is a init( java.util.Map ) method:
				Method configureMethod = implementationClass.getMethod("init", new Class[]{ Map.class } );
				configureMethod.invoke( this.implementation, new Object[]{ convertConfigurationToMap( cfg ) } );
			} catch (NoSuchMethodException e) {
				// check out if there is a init( ServletConfig ) method:
				try {
					Method configureMethod = implementationClass.getMethod("init", new Class[]{ ServletConfig.class } );
					configureMethod.invoke( this.implementation, new Object[]{ cfg } );
				} catch (NoSuchMethodException e1) {
					System.out.println("Note: if you want to configure your " + implementationClassName + " implementation upon startup, implement public void configure( java.util.Map ) or public void configure( javax.servlet.ServletConfig ).");
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new ServletException("\"polish.rmi.server\" cannot be instatiated: " + e.toString() );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new ServletException("\"polish.rmi.server\" cannot be accessed: " + e.toString() );
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("\"polish.rmi.server\" cannot be initialized: " + e.toString() );
		}
	}


	/**
	 * Stores all servlet configurations values into a map.
	 * 
	 * @param cfg the servlet configuration
	 * @return a map with all init parameters taken from the ServletConfig
	 */
	protected Map convertConfigurationToMap(ServletConfig cfg) {
		Map map = new HashMap();
		Enumeration enumeration = cfg.getInitParameterNames();
		while (enumeration.hasMoreElements()) {
			String paramName = (String) enumeration.nextElement();
			String paramValue = cfg.getInitParameter( paramName );
			map.put( paramName, paramValue );
		}
		return map;
	}



	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Thread thread = Thread.currentThread();
		try {
			this.requestsByThread.put( thread, request );
			DataInputStream in = new DataInputStream( request.getInputStream() );
			//DataOutputStream out = new DataOutputStream( response.getOutputStream() );
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream( byteOut );
			process(in, out);
			byte[] responseData = byteOut.toByteArray();
			response.setContentLength( responseData.length );
			response.getOutputStream().write(responseData, 0, responseData.length );
		} catch (IOException e) {
			e.printStackTrace();
			try {
				response.getWriter().write("this is the J2ME Polish remote server waiting for input.");
				response.flushBuffer();
			} catch (IOException nestedE) {
				nestedE.printStackTrace();
			}
		} finally {
			this.requestsByThread.remove( thread );
		}
	}
	
	/**
	 * Processes the actual method request.
	 * 
	 * @param in the input data stream that should contain the method invocation data
	 * @param out the output stream to which the results of the method call are written
	 * @throws IOException when data could not be read or written
	 */
	
	protected void process( DataInputStream in, DataOutputStream out )
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
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (InvocationTargetException e) {
			System.out.println("InvocationTargetException, cause=" + e.getCause() );
			processRemoteException( e, out );
		} catch (RemoteException e) {
			e.printStackTrace();
			processRemoteException( e, out );
		} catch (Exception e) {
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

	/**
	 * Processes an exception which is thrown by the method or while accessing the method.
	 * @param e the exception
	 * @param out the stream to which the exception is written as a result
	 * @throws IOException when data could not be written
	 */
	protected void processRemoteException(Throwable e, DataOutputStream out)
	throws IOException
	{
		Throwable cause = e.getCause();
		if (cause instanceof Externalizable) {
			out.writeInt( Remote.STATUS_CHECKED_EXCEPTION );
			Serializer.serialize( cause, out );
		} else {
			out.writeInt( Remote.STATUS_UNCHECKED_EXCEPTION );
			out.writeUTF( e.toString() );
		}
	}
	
	/**
	 * Retrieves the session and creates it if necessary.
	 * 
	 * @return the session for the current thread.
	 */
	public HttpSession getSession() {
		return getSession( true );
	}
	
	/**
	 * Retrieves the session and creates it if necessary.
	 *
	 * @param create true when the session should be created if it does not yet exist.
	 * @return the session for the current thread.
	 */
	public HttpSession getSession(boolean create) {
		return getRequest().getSession(create);
	}
	
	/**
	 * Retrieves the request for the current thread.
	 * 
	 * @return the HttpServletRequest for the current thread.
	 */
	public HttpServletRequest getRequest() {
		Thread thread = Thread.currentThread();
		return (HttpServletRequest) this.requestsByThread.get( thread );
	}
	

}
