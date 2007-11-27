/*
 * Created on Jan 10, 2007 at 7:04:31 PM.
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
package de.enough.polish.ant.build;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Device;
import de.enough.polish.ant.ConditionalTask;
import de.enough.polish.ui.Style;


/**
 * <p>Checks if a file or property is present and stops the build if it is not present.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 10, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SerializerSetting extends ConditionalTask {
	
	private File file;
	private String regex;
	private String target;
	
	private Device device;
	private String baseDir;
	
	private Hashtable styleTable;
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
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
			
			URLClassLoader loader = new URLClassLoader(getURLs());
			
			Field[] styleFields = loader.loadClass(this.target).getDeclaredFields();
			
			this.styleTable = new Hashtable();
			
			//Iterate over fields of StyleSheet
			for (int i = 0; i < styleFields.length; i++) {
				
				Field styleField = styleFields[i];
				String fieldName = styleField.getName();
				
				if(fieldName.matches(this.regex))
				{
					//name of field matches the regular expression
					String styleName = fieldName.substring(0,fieldName.length() - ("Style").length());
					this.styleTable.put(styleName, styleField.get(null));
				}
			}
			
			if(this.styleTable.size() > 0)
			{
				DataOutputStream stream = null;
				
				//create a new file
				if(this.file.exists())
				{
					this.file.delete();
				}
				
				this.file.createNewFile();
				

				stream = 	new DataOutputStream(
							new BufferedOutputStream(
							new FileOutputStream(this.file)));
				
				//serialize and stream to file				
				//Serializer.serialize(this.styleTable, stream);
				
				System.out.println("Serialized " + this.styleTable.size() + " styles to " + this.file);
			}
			else
			{
				System.out.println("No styles matching " + this.regex + " found !");
			}
		}
		catch(MalformedURLException e)	{
			throw new BuildException(this.baseDir + " is not a valid URL");
		} 
		catch (ClassNotFoundException e) {
			throw new BuildException(this.target + " is not a valid class path");
		} 
		catch (IllegalArgumentException e) {
			throw new BuildException(e.getMessage());
		}
		catch (IllegalAccessException e) {
			throw new BuildException("illegal access");
		}
		catch (Exception e) {
			throw new BuildException(e.getMessage());
		}
	}
	
	/**
	 * Return the path and needed libaries as a array of URLs. Used for URLClassLoader. 
	 * 
	 * @return the array of URLs
	 * @throws MalformedURLException
	 */
	public URL[] getURLs() throws MalformedURLException
	{
		//classpath
		URL sourceURL = new File(this.baseDir).toURI().toURL(); 
		
		//boot classpaths
		String[] bootClassPaths = this.device.getBootClassPaths();
		
		URL[] urls = new URL[bootClassPaths.length + 1];
		urls[0] = sourceURL;
		for (int i = 0; i < bootClassPaths.length; i++) {
			urls[i+1] = new File(bootClassPaths[i]).toURI().toURL();
		}
		
		return urls;
	}
	
	/**
	 * @return the file
	 */
	public File getFile() {
		return this.file;
	}
	/**
	 * @param file the file to store the serialized data
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * @return the community
	 */
	public String getRegex() {
		return this.regex;
	}
	
	/**
	 * @param community the community name to retrieve the styles
	 */
	public void setRegex(String prefix) {
		this.regex = prefix;
	}
	
	/**
	 * @return the class to serialize
	 */
	public String getTarget() {
		return this.target;
	}
	
	/**
	 * Sets the class to serialize
	 * @param target the class to serialize
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	
	/**
	 * @return the base directory to find the class
	 */
	public String getClassesDir() {
		return this.baseDir;
	}
	
	/**
	 * Sets the base directory to find the class
	 * @param baseDir
	 */
	public void setClassesDir(String baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * Returns the current device
	 * 
	 * @return the current device
	 */
	public Device getDevice() {
		return device;
	}

	/**
	 * Sets the current device
	 * @param device the current device
	 */
	public void setDevice(Device device) {
		this.device = device;
	}
}
