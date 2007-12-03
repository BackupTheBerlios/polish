/*
 * Created on 28-Apr-2005 at 13:31:31.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish.postcompile.serialization;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import de.enough.bytecode.ASMClassLoader;
import de.enough.bytecode.DirClassLoader;
import de.enough.polish.BuildException;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.Variable;
import de.enough.polish.ant.build.SignSetting;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.postcompile.BytecodePostCompiler;
import de.enough.polish.postcompile.PostCompiler;
import de.enough.polish.postcompile.io.SerializationVisitor;
import de.enough.polish.util.ProcessUtil;
import de.enough.polish.util.ReflectionUtil;

/**
 * <p>Serializes the field objects of a class to a file where the fieldname matches the regular expression given.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        29-Dez-2007 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */
public class DataSerializerPostCompiler extends BytecodePostCompiler {
	
	private String file;
	private String regex;
	private String target;
	
	private Hashtable table;
	private URLClassLoader loader; 
	
	/**
	 * Creates a new data serialization post compiler.
	 */
	public DataSerializerPostCompiler() {
		super();
	}

	public void postCompile(File classesDir, Device device, DirClassLoader loader, List classes)
	throws BuildException
	{
		try
		{
			//If required fields are not set, stop build
			if(this.regex == null)
			{
				throw new IllegalArgumentException("regex is not set");
			}
			
			if(this.file == null)
			{
				throw new IllegalArgumentException("file is not set");
			}
			
			if(this.target == null)
			{
				throw new IllegalArgumentException("target is not set");
			}
			
			this.loader = getURLs(classesDir, device);
			
			Field[] fields = this.loader.loadClass(this.target.replace("/", ".")).getDeclaredFields();
			
			this.table = new Hashtable();
			
			//Iterate over fields 
			for (int i = 0; i < fields.length; i++) {
				
				Field styleField = fields[i];
				String fieldName = styleField.getName();
				
				if(fieldName.matches(this.regex))
				{
					//name of field matches the regular expression
					this.table.put(fieldName, styleField.get(null));
				}
			}
			
			removeEntries(classesDir, loader, this.table.keySet());
			
			if(this.table.size() > 0)
			{
				File result = new File(	device.getBaseDir() + 
										File.separator + 
										"resources" + 
										File.separator + 
										this.file);
				
				DataOutputStream stream = null;
				
				//create a new file
				if(result.exists())
				{
					result.delete();
					System.out.println("Deleted " + result);
				}
				
				result.createNewFile();
				
				stream = 	new DataOutputStream(
							new BufferedOutputStream(
							new FileOutputStream(result)));
				
				//Get the serializer, the signatures and the parameters to call serialize
				Object 		serializer = getSerializer();
				Object[] 	parameters = getParameters(this.table,stream);
				Class[] 	signatures = getSignatures();
				
				//serialize
				ReflectionUtil.callMethod("serialize", serializer, signatures, parameters);
				
				stream.close();
				
				System.out.println("Serialized " + this.table.size() + " fields to " + result);
			}
			else
			{
				System.out.println("No fields matching " + this.regex + " found !");
			}
		}
		catch (Exception e) {
			throw new BuildException(e.getMessage());
		}
	}
	
	/**
	 * Removes the externalized entries from the style sheet
	 * @param classesDir the classpath
	 * @param loader the classloader
	 */
	private void removeEntries(File classesDir, DirClassLoader loader, Set unwanted)
	{
		ASMClassLoader asmLoader = new ASMClassLoader(loader);
		try
		{
			ClassNode classNode = asmLoader.loadClass(this.target);
			ClassWriter writer = new ClassWriter(false);
			
			DataSerializationVisitor visitor = new DataSerializationVisitor(writer, unwanted);
            classNode.accept(visitor);
            
            writeClass(classesDir, this.target, writer.toByteArray());
		}
		catch (ClassNotFoundException e) {
			System.out.println(this.target + " could not be found " + e);
		}
		catch (IOException e) {
			System.out.println("unable to write class " + e);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the classloader with the classpath and needed libaries for serialization etc.  
	 * 
	 * @param classesDir the classpath
	 * @param device the device to serialize for
	 * @return the resulting classloader
	 * @throws MalformedURLException 
	 */
	private URLClassLoader getURLs(File classesDir, Device device) throws MalformedURLException
	{
		//classpath
		URL classesURL = classesDir.toURI().toURL(); 
		
		//boot classpaths
		String[] bootClassPaths = device.getBootClassPaths();
		
		URL[] urls = new URL[bootClassPaths.length + 1];
		urls[0] = classesURL;
		for (int i = 0; i < bootClassPaths.length; i++) {
			urls[i+1] = new File(bootClassPaths[i]).toURI().toURL();
		}
		
		return new URLClassLoader(urls);
	}
	
	/**
	 * Returns an instance of de.enough.polish.io.Serializer
	 * @return the instance
	 */
	private Object getSerializer()
	{
		Class serializerClass = null;
		try {
			serializerClass = this.loader.loadClass("de.enough.polish.io.Serializer");
			return serializerClass.newInstance();
		} catch (ClassNotFoundException e) {
			System.out.println("unable to load serializer " + e);
		} catch (InstantiationException e) {
			System.out.println("unable to load serializer " + e);
		} catch (IllegalAccessException e) {
			System.out.println("unable to load serializer " + e);
		}
		
		return null;
	}
	/**
	 * Returns an Object array with object and stream to use as parameters 
	 * in reflection method calling  
	 * @param object the object to serialize
	 * @param stream the output stream
	 * @return the Object array
	 */
	private Object[] getParameters(Object object, DataOutputStream stream)
	{
		Object[] objects = new Object[2];
		
		objects[0] = object;
		objects[1] = stream;
		
		return objects;
	}
	
	/**
	 * Returns the signatures for the method parameters of Serializer.serialize()
	 * @return the signatures as an Class array
	 */
	private Class[] getSignatures()
	{
		Class[] classes = new Class[2];
		classes[0] = Object.class;
		classes[1] = DataOutputStream.class;
		
		return classes;
	}
	
	public void setParameters( Variable[] properties, File baseDir ) {
		this.file 	= getValue("file", properties);
		this.regex 	= getValue("regex", properties);
		this.target	= getValue("target", properties);
	}
	
	/**
	 * Returns the value of a variable named <code>name</code> if found, otherwise null  
	 * @param name the name of the variable
	 * @param properties the variables to search
	 * @return the value of the found variable
	 */
	private String getValue(String name, Variable[] properties)
	{
		for (int i = 0; i < properties.length; i++) {
			if(properties[i].getName().equals(name))
			{
				return properties[i].getValue();
			}
		}
		
		return null;
	}

}
