/*
 * Created on 16-Jul-2004 at 02:57:35.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.enough.polish.Variable;

/**
 * <p>PoulateUtil is used to set variables via reflection.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        16-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public final class PopulateUtil {

	/**
	 * Populates the given object with the specified parameters.
	 * For each parameter-name the given object needs to specify
	 * either set[param-name]( String ) or set[param-name]( File ).
	 * When the parameter "message" is provided, either the method
	 * setMessage( String value ) or setMessage( File value ) needs
	 * to be defined by the given object.
	 * When the object defines both methods, it cannot be foreseen which
	 * one of them will be called.
	 * 
	 * @param object the object which should be populated.
	 * @param parameters the parameters.
	 * @param baseDir the base directory for the population.
	 * @throws  IllegalArgumentException when a parameter has a syntax error
	 *        or when a needed method has not be found. 
	 */
	public final static void populate( Object object, Variable[] parameters, File baseDir ) {
		// put all methods into a hash map:
		Class objectClass = object.getClass();
		Method[] methods = objectClass.getMethods();
		HashMap methodsByName = new HashMap();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Class[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length == 1) {
				Class parameterType = parameterTypes[0];
				if (parameterType == String.class || parameterType == File.class || parameterType == Boolean.TYPE ) {
					methodsByName.put( method.getName(), method );
				}
			}
		}
		
		// split parameters into single ones:
		for (int i = 0; i < parameters.length; i++) {
			Variable param = parameters[i];
			try {
				populate( object, baseDir, param, methodsByName );
			} catch (IllegalArgumentException e) {
				throw e;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				String message = "Unable to set the parameter [" + param.getName() + "] with value [" + param.getValue() + "] for class [" + object.getClass().getName() + "]: " + e.toString();
				throw new IllegalArgumentException( message );				
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				String message = "Unable to set the parameter [" + param.getName() + "] with value [" + param.getValue() + "] for class [" + object.getClass().getName() + "]: " + e.toString();
				throw new IllegalArgumentException( message );				
			}
		}
	}

	/**
	 * Populates the object with the specified parameter.
	 * 
	 * @param object the object which should be populated.
	 * @param baseDir the base directory for the population.
	 * @param param the parameter
	 * @param methodsByName the methods known of the object
	 * @throws IllegalArgumentException when a parameter has a syntax error
	 *        or when a needed method has not be found. 
	 * @throws InvocationTargetException when the method could not be called
	 * @throws IllegalAccessException when the method could not be accessed
	 */
	private static void populate(Object object, File baseDir, Variable param, HashMap methodsByName) 
	throws IllegalArgumentException, IllegalAccessException, InvocationTargetException 
	{
		String name = param.getName();
		String value = param.getValue();
		String methodName = "set" + Character.toUpperCase( name.charAt(0)) + name.substring( 1 );
		Method method = (Method) methodsByName.get( methodName );
		if (method == null) {
			throw new IllegalArgumentException("To use the parameter [" 
					+ name + "] the class [" + object.getClass().getName() 
					+ "] needs to specify the method " + methodName 
					+ "(String), " + methodName 
					+ "(boolean) or " + methodName + "(File). " );
		}
		Class paramType = method.getParameterTypes()[0];
		if (paramType == String.class) {
			method.invoke(object, new Object[]{ value } );
		} else if (paramType == Boolean.TYPE ) {
			Boolean argument = new Boolean( CastUtil.getBoolean(value) );
			method.invoke(object, new Object[]{ argument } );
		} else {
			File file = new File( value );
			if (!file.isAbsolute()) {
				file = new File( baseDir.getAbsolutePath() + File.separator + value );
			}
			method.invoke(object, new Object[]{ file } );
		}
	}

}
