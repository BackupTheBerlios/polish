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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

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
public final class ReflectionUtil {
	
	private final static HashMap PRIMITIVE_CLASS_WRAPPERS = new HashMap();
	static {
		PRIMITIVE_CLASS_WRAPPERS.put(Byte.TYPE, Byte.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Short.TYPE, Short.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Integer.TYPE, Integer.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Long.TYPE, Long.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Float.TYPE, Float.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Double.TYPE, Double.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Character.TYPE, Character.class);
		PRIMITIVE_CLASS_WRAPPERS.put(Boolean.TYPE, Boolean.class);
	}


    public static Object callMethod( Object object, String methodName, int value ) 
    throws NoSuchMethodException 
    {
        Class instanceClass = object.getClass();
        Method method = null;
        while (method == null) {
            try {
                method = instanceClass.getDeclaredMethod( methodName, new Class[]{ Integer.TYPE } );
            } catch (NoSuchMethodException e) {
                instanceClass = instanceClass.getSuperclass();
                if (instanceClass == null) {
                    throw e;
                }
            }
        }
        method.setAccessible(true);
        try {
            return method.invoke(object, new Object[]{ new Integer( value ) } );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new NoSuchMethodException( e.toString() );
        } catch (InvocationTargetException e) {
            System.out.println("PopulateUtil: unable to call method " + methodName + "( " + value + ")");
            e.printStackTrace();
            throw new RuntimeException( e.getCause() );
        }
    }

    public static Object callMethod(String methodName, Object object, Class[] signature, Object[] values) 
    throws NoSuchMethodException 
    {
        Class instanceClass = object.getClass();
        Method method = null;
        while (method == null) {
            try {
                method = instanceClass.getDeclaredMethod( methodName, signature );
            } catch (NoSuchMethodException e) {
                instanceClass = instanceClass.getSuperclass();
                if (instanceClass == null) {
                    throw e;
                }
            }
        }
        method.setAccessible(true);
        try {
            return method.invoke(object, values );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new NoSuchMethodException( e.toString() );
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException( e.getCause() );
        }
    }

    public static Object callMethod(String methodName, Object object) 
    throws NoSuchMethodException 
    {
        Class instanceClass = object.getClass();
        Method method = null;
        while (method == null) {
            try {
                method = instanceClass.getDeclaredMethod( methodName, new Class[0] );
            } catch (NoSuchMethodException e) {
                instanceClass = instanceClass.getSuperclass();
                if (instanceClass == null) {
                    throw e;
                }
            }
        }
        method.setAccessible(true);
        try {
            return method.invoke(object, new Object[0] );
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new NoSuchMethodException( e.toString() );
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException( e.getCause() );
        }
    }
    
	/**
	 * Retrieves the value of the specified int-field of the given object.
	 * 
	 * @param object the object that holds the field
	 * @param fieldName the name of the field
	 * @return the field value
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static int getIntField( Object object, String fieldName ) 
	throws NoSuchFieldException 
	{
		Field field = getField( object, fieldName );
		try {
			return field.getInt(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoSuchFieldException("unable to access field [" + fieldName + "]: " + e.toString() );
		}
	}

	/**
	 * Retrieves the value of the specified boolean-field of the given object.
	 * 
	 * @param object the object that holds the field
	 * @param fieldName the name of the field
	 * @return the field value
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static boolean getBooleanField(Object object, String fieldName ) 
	throws NoSuchFieldException 
	{
	
		Field field = getField( object, fieldName );
		try {
			return field.getBoolean(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoSuchFieldException("unable to access field [" + fieldName + "]: " + e.toString() );
		}
	}

	
	/**
	 * Retrieves the value of the specified String-field of the given object.
	 * 
	 * @param object the object that holds the field
	 * @param fieldName the name of the field
	 * @return the field value
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static String getStringField(Object object, String fieldName) 
	throws NoSuchFieldException 
	{
		Field field = getField( object, fieldName );
		try {
			return (String) field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoSuchFieldException("unable to access field [" + fieldName + "]: " + e.toString() );
		}
	}
	
	/**
	 * Retrieves the specified field of the given object.
	 * 
	 * @param object the object that holds the field
	 * @param fieldName the name of the field
	 * @return the field
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static Field getField( Object object, String fieldName ) 
	throws NoSuchFieldException
	{
		return getField( object.getClass(), fieldName );
	}
	
	/**
	 * Retrieves the specified field of the given object.
	 * 
	 * @param instanceClass the class that contains the field
	 * @param fieldName the name of the field
	 * @return the field
	 * @throws NoSuchFieldException when the field does not exist
	 */
	public static Field getField( Class instanceClass, String fieldName ) 
	throws NoSuchFieldException
	{
		try {
			Field field = null;
			while (field == null) {
				try {
					field = instanceClass.getDeclaredField( fieldName );
				} catch (NoSuchFieldException e) {
					instanceClass = instanceClass.getSuperclass();
					if (instanceClass == null) {
						throw e;
					}
					//System.out.println("trying parent class [" + instanceClass.getName() + "]");
				}
			}
			field.setAccessible(true);
			return field;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to access field [" + fieldName + "]: " + e.toString() );
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new NoSuchFieldException( "Unable to access field [" + fieldName + "]: " + e.toString() );
		}
	}

}
