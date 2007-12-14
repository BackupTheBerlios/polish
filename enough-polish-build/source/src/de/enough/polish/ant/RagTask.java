/*
 * Created on 21-Jan-2003 at 15:15:56.
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
package de.enough.polish.ant;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.Attribute;
import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Variable;
import de.enough.polish.ant.build.ClassSetting;
import de.enough.polish.ant.build.FullScreenSetting;
import de.enough.polish.io.Serializer;
import de.enough.polish.rag.RagContainer;
import de.enough.polish.rag.RagObfuscationMap;
import de.enough.polish.rag.SerializeSetting;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.ReflectionUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>
 * Stores resources and serialized objects to a
 * .rag file. 
 * </p>
 * 
 * <p>
 * Copyright Enough Software 2004, 2005, 2006
 * </p>
 * 
 * <pre>
 * history
 *        10-Dec-2007	- asc creation
 * </pre>
 * 
 * @author Andre Schmidt, andre@enough.de
 */
public class RagTask extends PolishTask {
	private URLClassLoader loader; 
	private RagObfuscationMap list;
	
	/**
	 * Packages the resources and serialized fields to a .rag file.
	 * @param device the device to build for
	 * @param locale the locale to build for
	 */
	protected void bundle(Device device, Locale locale) {
		String file = this.buildSetting.getFileSetting().getFile();
		File ragPath = new File(device.getRagDir().getAbsolutePath());
		
		
		if (!ragPath.exists()) {
			ragPath.mkdirs();
		}
		
		Vector containers = new Vector();
		
		File result = new File(ragPath.getAbsolutePath() + File.separator + file);
		
		try
		{
			// load ProGuard obfuscation map
			File obfuscationMap = new File( device.getBaseDir() + File.separatorChar + "obfuscation-map.txt");
			if (obfuscationMap.exists() && this.doObfuscate) {
				this.list = new RagObfuscationMap(obfuscationMap);
			}
			
			//Get the resources and serializers
			File[] resources = this.resourceManager.getResources(device, locale);
			ArrayList serializers = this.buildSetting.getSerializers();
			
			for (int i = 0; i < serializers.size(); i++) {
				SerializeSetting setting = (SerializeSetting)serializers.get(i);
				
				Field[] fields;
				String target = setting.getTarget();
				
				if(this.doObfuscate)
				{
					target = this.list.getClassName(target, false);
					//Get the obfuscated fields of the obfuscated class 
					fields = this.loader.loadClass(target).getDeclaredFields();
				}
				else
				{
					//Get the fields of the class
					fields = this.loader.loadClass(target).getDeclaredFields();
				}
				
				//Iterate over fields 
				for (int j = 0; j < fields.length; j++) {
					
					Field field = fields[j];
					String fieldName = field.getName();
					
					if(this.doObfuscate)
					{
						fieldName = this.list.getFieldName(fieldName, true);
					}
					
					//Does the field name match the regular expression of the current serializer ?
					if(fieldName.matches(setting.getRegex()))
					{
						//Create a container for the field object
						RagContainer container = getContainer(setting.getTarget(),fieldName,field.get(null));
						containers.add(container);
					}
				}
			}
			
			//Add all resources to the container vector
			for (int i = 0; i < resources.length; i++) {
				RagContainer container = getContainer(resources[i]);
				containers.add(container);
			}
			
			//Write the containers to the file
			writeToFile(result, containers);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new BuildException("unable to stream contents to " + result.getAbsolutePath());
		}
	}
	
	/**
	 * Removes the classes, sources and resources folder from a device folder.
	 * @param device the device
	 * @param locale the locale
	 */
	private void clear(Device device, Locale locale) {
		System.out.println("Removing classes, sources and resources");
		
		//Delete sources directory for the device
		FileUtil.delete(new File(device.getSourceDir()));
		//Delete resources directory for the device
		FileUtil.delete(device.getResourceDir());
		//Delete classes directory for the device
		FileUtil.delete(new File(device.getBaseDir() + File.separator + "classes"));
	}
	
	/**
	 * Returns the classloader with the classpath and needed libaries for serialization etc.  
	 * @param classesDir the classpath
	 * @param device the device to serialize for
	 * @return the resulting classloader
	 * @throws MalformedURLException 
	 */
	private URLClassLoader getURLs(Device device) throws MalformedURLException
	{
		//classpath
		URL classesURL = new File(device.getClassesDir()).toURI().toURL(); 
		
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
		String serializerClassPath = "de.enough.polish.io.Serializer";
		try {
			if(this.doObfuscate)
			{
				serializerClassPath = this.list.getClassName("de.enough.polish.io.Serializer", false);
			}
			
			serializerClass = this.loader.loadClass(serializerClassPath);
			Constructor[] constr = serializerClass.getDeclaredConstructors();
			constr[0].setAccessible(true);
			return constr[0].newInstance( null );
		} catch (ClassNotFoundException e) {
			System.out.println("unable to load serializer " + e);
			e.printStackTrace();
		} catch (InstantiationException e) {
			System.out.println("unable to load serializer " + e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("unable to load serializer " + e);
			e.printStackTrace();
		} catch (SecurityException e) {
			System.out.println("unable to load serializer " + e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("unable to load serializer " + e);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.out.println("unable to load serializer " + e);
			e.printStackTrace();
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
	
	/**
	 * Returns a <code>RagContainer</code> for a file.
	 * @param file the file
	 * @return a <code>RagContainer</code>
	 * @throws IOException
	 */
	private RagContainer getContainer(File file) throws IOException
	{
		System.out.println("Creating rag container from " + file.getAbsolutePath());
		
		//Create a container
		RagContainer container = new RagContainer();
		DataInputStream stream = new DataInputStream(new FileInputStream(file));
		
		byte[] buffer = new byte[(int)file.length()];
		
		//Read the file contents to the buffer
		stream.readFully(buffer);
		
		container.setName(file.getName());
		container.setSize((int)file.length());
		container.setData(buffer);
		
		return container;
	}
	
	/**
	 * Returns a <code>RagContainer</code> for a single field of a class.
	 * 
	 * @param className the class containing the field
	 * @param fieldName the name of the field
	 * @param object the value of the field
	 * @return a <code>RagContainer</code>
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	private RagContainer getContainer(String className, String fieldName, Object object) throws IOException, NoSuchMethodException
	{
		String fullName = className + "." + fieldName;
		System.out.println("Creating rag container from " + fullName + "(" + object + ")");
		
		RagContainer container = new RagContainer();
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream ();
		DataOutputStream stream = new DataOutputStream (byteStream);
		
		//Get the serializer, the signatures and the parameters to call serialize
		Object 		serializer = getSerializer();
		Object[] 	parameters = getParameters(object,stream);
		Class[] 	signatures = getSignatures();
		
		String methodName = "serialize";
		
		if(this.doObfuscate)
		{
			methodName = this.list.getMethodName("serialize", false);
		}
		
		//serialize
		ReflectionUtil.callMethod(methodName, serializer, signatures, parameters);
		
		container.setName(fullName.toLowerCase());
		container.setSize(byteStream.size());
		container.setData(byteStream.toByteArray());
		
		return container;
	}
	
	/**
	 * Writes the resulting containers to a file
 	 * @param file the file to write to 
	 * @param containers the containers
	 * @throws IOException
	 */
	private void writeToFile(File file, Vector containers) throws IOException
	{
		System.out.println("Writing containers to " + file.getAbsolutePath());
		
		DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
		
		int offset = getIndexLength(containers);
		
		//Write the index length
		stream.writeInt(containers.size());
		
		for (int i = 0; i < containers.size(); i++) {
			RagContainer container = (RagContainer) containers.get(i);
			
			stream.writeUTF(container.getName());
			stream.writeInt(offset);
			stream.writeInt(container.getSize());
			
			//Calculate the offset for the next data
			offset += container.getSize();
		}
		
		for (int i = 0; i < containers.size(); i++) {
			RagContainer container = (RagContainer) containers.get(i);
			
			//Write the data
			stream.write(container.getData());
		}
		
		stream.close();
	}
	
	/**
	 * Simulates the creation of the index of a resource assembly and returns the length of it.
	 * This is used to get the offset for the data part of the resulting .rag file.
	 * @param containers the containers that form the index
	 * @return the length of the index
	 * @throws IOException
	 */
	private int getIndexLength(Vector containers) throws IOException
	{
		DataOutputStream stream = new DataOutputStream(new ByteArrayOutputStream());
		
		//Write a dummy
		stream.writeInt(1);
		
		for (int i = 0; i < containers.size(); i++) {
			RagContainer container = (RagContainer) containers.get(i);
			
			stream.writeUTF(container.getName());
			stream.writeInt(1);
			stream.writeInt(1);
		}
		
		return stream.size();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.PolishTask#execute(de.enough.polish.Device, java.util.Locale, boolean)
	 */
	protected void execute(Device device, Locale locale, boolean hasExtensions) {
		initialize( device, locale );
		assembleResources( device, locale );
		preprocess( device, locale );
		compile( device, locale );
		postCompile(device, locale);
		if (this.doObfuscate) {
			obfuscate( device, locale );
		}
		
		try
		{
			this.loader = getURLs(device);
		}
		catch (Exception e) {
			System.out.println("unhable to initialize classloader " + e);
			e.printStackTrace();
		}
		
		bundle(device, locale);
		clear(device,locale);
	}
}