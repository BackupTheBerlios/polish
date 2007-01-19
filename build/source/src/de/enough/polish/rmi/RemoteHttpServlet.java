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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RemoteHttpServlet extends HttpServlet {
	
	private RemoteServer remoteServer;



	public RemoteHttpServlet() {
		super();
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig cfg) throws ServletException {
		// TODO robertvirkus implement init
		super.init(cfg);
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
			Object implementation = implementationClass.newInstance();
			try {
				// check out if there is a configure( java.util.Map ) method:
				Method configureMethod = implementationClass.getMethod("configure", new Class[]{ Map.class } );
				configureMethod.invoke( implementation, new Object[]{ convertConfigurationToMap( cfg ) } );
			} catch (NoSuchMethodException e) {
				// check out if there is a configure( ServletConfig ) method:
				try {
					Method configureMethod = implementationClass.getMethod("configure", new Class[]{ ServletConfig.class } );
					configureMethod.invoke( implementation, new Object[]{ cfg } );
				} catch (NoSuchMethodException e1) {
					System.out.println("Note: if you want to configure your " + implementationClassName + " implementation upon startup, implement public void configure( java.util.Map ) or public void configure( javax.servlet.ServletConfig ).");
				}
			}
			this.remoteServer = new RemoteServer( implementation );
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



	private Map convertConfigurationToMap(ServletConfig cfg) {
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
		if (this.remoteServer == null) {
			response.getWriter().write("this is the J2ME Polish remote server: configuration failed, please check your log.");
			response.flushBuffer();
			return;
		}
		try {
			DataInputStream in = new DataInputStream( request.getInputStream() );
			DataOutputStream out = new DataOutputStream( response.getOutputStream() );
			this.remoteServer.process(in, out);			
		} catch (IOException e) {
			e.printStackTrace();
			try {
				response.getWriter().write("this is the J2ME Polish remote server waiting for input.");
				response.flushBuffer();
			} catch (IOException nestedE) {
				nestedE.printStackTrace();
			}
		}
	}

}
