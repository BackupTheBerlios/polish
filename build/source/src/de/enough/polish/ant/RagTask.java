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
import java.lang.reflect.Field;
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
import de.enough.polish.rag.SerializeSetting;
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

	
	/* (non-Javadoc)
	 * @see de.enough.polish.ant.PolishTask#initialize(de.enough.polish.Device, java.util.Locale)
	 */
	public void initialize(Device device, Locale locale) {
		if (this.configurationManager != null) {
			this.configurationManager.preInitialize(device, locale,
					this.environment);
		}
		this.extensionManager.preInitialize(device, locale);
		// intialise the environment
		this.environment.initialize(device, locale);
		device.setEnvironment(this.environment);
		this.environment.set("ant.project", getProject());
		this.environment.set("polish.sourcefiles", this.sourceFiles);

		// set variables and symbols:
		// this.environment.setSymbols( device.getFeatures() );
		// this.environment.setVariables( device.getCapabilities() );
		this.environment.addVariable("polish.identifier", device
				.getIdentifier());
		this.environment.addVariable(device.getIdentifier(), "true");
		this.environment.addSymbol(device.getIdentifier());
		this.environment.addVariable("polish.name", device.getName());
		this.environment.addVariable("polish.vendor", device.getVendorName());
		// set localization-variables:
		if (locale != null
				|| (this.localizationSetting != null && this.localizationSetting
						.isDynamic())) {
			if (locale == null) {
				locale = this.localizationSetting.getDefaultLocale()
						.getLocale();
				this.environment
						.addSymbol("polish.i18n.useDynamicTranslations");
				this.environment.setLocale(locale);
			}
			this.environment.addVariable("polish.SupportedLocales",
					this.localizationSetting
							.getSupportedLocalesAsString(this.environment));
			this.environment.addVariable("polish.locale", locale.toString());
			this.environment.addVariable("polish.language", locale
					.getLanguage());
			String country = locale.getCountry();
			if (country == null || country.length() == 0) {
				this.environment.removeVariable("polish.country");
			} else {
				this.environment.addVariable("polish.country", country);
			}
		}

		// enable the support for the J2ME Polish GUI, part 1:
		// check if a preprocessing variable is set for using the Polish GUI:
		boolean usePolishGui = usePolishGui(device);
		if (usePolishGui) {
			this.environment.addSymbol("polish.usePolishGui");
		}

		// set conditional variables:
		BooleanEvaluator evaluator = this.environment.getBooleanEvaluator();
		Project antProject = getProject();
		Variable[] vars = this.variables.getVariables(this.environment);
		for (int i = 0; i < vars.length; i++) {
			Variable var = vars[i];
			this.environment.addVariable(var.getName(), var.getValue());
		}
		ClassSetting dojaSetting = this.buildSetting.getDojaClassSetting();
		if (dojaSetting != null && dojaSetting.isActive(evaluator, antProject)) {
			this.environment.addVariable("polish.classes.iapplication",
					dojaSetting.getClassName());
		}
		ClassSetting mainSetting = this.buildSetting.getMainClassSetting();
		if (mainSetting != null && mainSetting.isActive(evaluator, antProject)) {
			this.environment.addVariable("polish.classes.main", mainSetting
					.getClassName());
		}

		// now set the full-screen-settings:
		String value = this.environment.getVariable("polish.FullScreen");
		if (value != null) {
			if ("menu".equalsIgnoreCase(value)) {
				this.environment.addSymbol("polish.useMenuFullScreen");
				this.environment.addSymbol("polish.useFullScreen");
			} else if ("yes".equalsIgnoreCase(value)
					|| "true".equalsIgnoreCase(value)) {
				this.environment.addSymbol("polish.useFullScreen");
			}
		} else {
			FullScreenSetting fullScreenSetting = this.buildSetting
					.getFullScreenSetting();
			if (fullScreenSetting != null) {
				if (fullScreenSetting.isMenu()) {
					this.environment.addSymbol("polish.useMenuFullScreen");
					this.environment.addSymbol("polish.useFullScreen");
				} else if (fullScreenSetting.isEnabled()) {
					this.environment.addSymbol("polish.useFullScreen");
				}
			}
		}

		// set support for the J2ME Polish GUI, part 2:
		if (usePolishGui(device)) {
			usePolishGui = true;
			this.environment.addSymbol("polish.usePolishGui");
		} else {
			usePolishGui = false;
			this.environment.removeSymbol("polish.usePolishGui");
		}

		// set the temporary build path used for preprocessing, compilation,
		// preverification, etc:
		String deviceSpecificBuildPath = File.separatorChar
				+ device.getVendorName() + File.separatorChar
				+ device.getName();
		deviceSpecificBuildPath = deviceSpecificBuildPath.replace(' ', '_');
		String buildPath = this.buildSetting.getWorkDir().getAbsolutePath()
				+ deviceSpecificBuildPath;
		if (locale != null) {
			buildPath += File.separatorChar + locale.toString();
		}
		device.setBaseDir(buildPath);
		this.environment.addVariable("polish.base.dir", buildPath);
		String sourceDir = buildPath + File.separatorChar + "source";
		device.setSourceDir(sourceDir);
		this.environment.addVariable("polish.sourcedir", sourceDir);
		this.environment.addVariable("polish.source.dir", sourceDir);
		File resourceDir = new File(buildPath + File.separatorChar
				+ "resources");
		device.setResourceDir(resourceDir);
		this.environment.addVariable("polish.resources.dir", resourceDir
				.getAbsolutePath());

		// okay, now initialize extension manager:
		this.extensionManager.initialize(device, locale, this.environment);

		// check if there is an active obfuscator with the "useDefaultPackage"
		// option:
		this.useDefaultPackage = "true".equals(this.environment
				.getVariable("polish.useDefaultPackage"));
		if (this.useDefaultPackage
				&& this.environment.hasSymbol("polish.api.j2mepolish")) {
			this.useDefaultPackage = false;
			this.environment.removeVariable("polish.useDefaultPackage");
			this.environment.removeSymbol("polish.useDefaultPackage");
		}
		this.preprocessor.setUseDefaultPackage(this.useDefaultPackage);
		// adjust polish.classes.ImageLoader in case the default package is
		// used:
		if (this.useDefaultPackage) {
			String imageLoaderClass = this.environment
					.getVariable("polish.classes.ImageLoader");
			if (imageLoaderClass != null) {
				int classStartIndex = imageLoaderClass.lastIndexOf('.');
				if (classStartIndex != -1) {
					imageLoaderClass = imageLoaderClass
							.substring(classStartIndex + 1);
					this.environment.addVariable("polish.classes.ImageLoader",
							imageLoaderClass);
				}
			}
		}

		this.extensionManager.postInitialize(device, locale, this.environment);

		this.environment.set("polish.home", this.polishHomeDir);
		this.environment.set("polish.apidir", this.buildSetting.getApiDir());

		// set the absolute path to polish.home - this minimizes problems
		// resolving paths
		// relative to polish.home (since the polish.home can be relative
		// to the build.xml script).
		this.environment.addVariable("polish.home", this.polishHomeDir
				.getAbsolutePath());

		// initialize resource manager:
		// get the resource manager:
		this.resourceManager.initialize(this.environment);

		if (this.configurationManager != null) {
			this.configurationManager.postInitialize(device, locale,
					this.environment);
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.PolishTask#checkSettings()
	 */
	public void checkSettings() {
		if (this.deviceRequirements == null) {
			log("Nested element [deviceRequirements] is missing, now the project will be optimized for all known devices.");
		}
		if (this.buildSetting == null) {
			throw new BuildException("Nested element [build] is required.");
		}
		if (this.buildSetting.getFileSetting() == null)
		{
			throw new BuildException("Nested element [file] is required.");
		}
	}
	
	/**
	 * Clears the class and source path of classes used in the serialization
	 * to ensure that they're rebuild before parsing and serializing their fields.
	 * @param device the device to build for
	 * @param locale the locale to build for
	 */
	protected void clear(Device device, Locale locale) {
		ArrayList serializers = this.buildSetting.getSerializers();
		for (int i = 0; i < serializers.size(); i++) {
			SerializeSetting setting = (SerializeSetting)serializers.get(i);
			
			//Rewrite the target classname to a directory structure
			String classpath = setting.getTarget().replace(".","\\");
			
			//Get the File objects for the class and source file of the target to serialize
			File sourceFile = new File(device.getSourceDir() + File.separator + classpath + ".java");
			File classFile = new File(device.getBaseDir() + File.separator + "classes" + File.separator +  classpath + ".class");
			
			System.out.println("Removing source files of " + setting.getTarget());
			
			//Delete the files
			sourceFile.delete();
			classFile.delete();
		}
	}

	/**
	 * Packages the resources and serialized fields to a .rag file.
	 * @param device the device to build for
	 * @param locale the locale to build for
	 */
	protected void bundle(Device device, Locale locale) {
		String file = this.buildSetting.getFileSetting().getFile();
		String path = this.buildSetting.getFileSetting().getPath();
		
		//If path == null, use the device base directory
		if(path == null)
		{
			path = device.getBaseDir();
		}
		
		Vector containers = new Vector();
		
		File result = new File(path + File.separator + file);
		
		try
		{
			//Get the resources and serializers
			File[] resources = this.resourceManager.getResources(device, locale);
			ArrayList serializers = this.buildSetting.getSerializers();
			
			for (int i = 0; i < serializers.size(); i++) {
				SerializeSetting setting = (SerializeSetting)serializers.get(i);
				
				//Get the fields of the class
				Field[] fields = this.loader.loadClass(setting.getTarget()).getDeclaredFields();
				
				//Iterate over fields 
				for (int j = 0; j < fields.length; j++) {
					
					Field field = fields[j];
					String fieldName = field.getName();
					
					//Does the field name match the regular expression of the current serializer ?
					if(fieldName.matches(setting.getRegex()))
					{
						//Create a container for the field object
						RagContainer container = getContainer(setting.getTarget(),field.getName(),field.get(null));
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
		container.setSize(file.length());
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
		
		//serialize
		ReflectionUtil.callMethod("serialize", serializer, signatures, parameters);
		
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
		
		for (int i = 0; i < containers.size(); i++) {
			RagContainer container = (RagContainer) containers.get(i);
			
			stream.writeUTF(container.getName());
			stream.writeLong(container.getSize());
			stream.write(container.getData());
		}
		
		stream.close();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.PolishTask#execute(de.enough.polish.Device, java.util.Locale, boolean)
	 */
	protected void execute(Device device, Locale locale, boolean hasExtensions) {
		
		initialize(device, locale);
		clear(device,locale);
		assembleResources(device, locale);
		preprocess(device, locale);
		compile(device, locale);
		postCompile(device, locale);
		
		try
		{
			this.loader = getURLs(device);
		}
		catch (Exception e) {
			System.out.println("unhable to initialize classloader " + e);
			e.printStackTrace();
		}
		
		bundle(device, locale);
	}
}