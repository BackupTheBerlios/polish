//#condition polish.JavaSE && polish.useRAG
package de.enough.polish.rag;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>
 * Reads the lines from an obfuscation file and provides
 * methods to get the obfuscation names for classes and fields 
 * </p>
 * 
 * <p>
 * Copyright (c) 2005, 2006, 2007 Enough Software
 * </p>
 * 
 * <pre>
 * history
 *        13-Dec-2007 - asc creation
 * </pre>
 * 
 * @author Andre Schmidt, andre@enough.de
 */
public class RagObfuscationMap {
	ArrayList lines;

	/**
	 * Opens a file and reads the lines to <code>lines</code>
	 * @param file the file
	 * @throws IOException
	 */
	public RagObfuscationMap(File file) throws IOException {
		FileInputStream fileStream = null;
		BufferedInputStream bufferedStream = null;
		
		DataInputStream dataStream = null;
		fileStream = new FileInputStream(file);

		bufferedStream = new BufferedInputStream(fileStream);
		dataStream = new DataInputStream(bufferedStream);

		this.lines = new ArrayList();
		
		while (dataStream.available() != 0) {

			String line = dataStream.readLine();
			this.lines.add(line);
		}

		fileStream.close();
		bufferedStream.close();
		dataStream.close();
	}
	

	private boolean isClass(String line)
	{
		line = line.trim();
		return line.indexOf(":") != -1 && line.indexOf(".") != -1;
	}
	
	private boolean isField(String line)
	{
		line = line.trim();
		String name = line.split("->")[0].trim();
		return name.indexOf(" ") != -1 && name.indexOf("(") == -1;
	}
	
	private boolean isMethod(String line)
	{
		line = line.trim();
		String name = line.split("->")[0].trim();
		return name.indexOf(" ") != -1 && name.indexOf("(") != -1;
	}
	
	private String getNormalName(String line)
	{
		String part = line.split("->")[0].trim();
		
		//method or field
		if(part.indexOf(" ") != -1)
		{
			part = part.split(" ")[1];
			//method
			if(part.indexOf("(") != -1)
			{
				return part.split("\\(")[0].trim();
			}
			else
			{
				return part;
			}
		}
		else
		{
			return part;
		}
	}
	
	private String getObfuscatedName(String line)
	{
		return line.split("->")[1].trim();
	}
	
	/**
	 * Returns the obfuscated or normal name for <code>className</code>
	 * @param className a full class specifier
	 * @param obfuscated true, if <code>className</code> is an obfuscation name and should be resolved
	 * @return the (obfuscation) name of the class
	 */
	public String getClassName(String className, boolean obfuscated)
	{
		for (int i = 0; i < this.lines.size(); i++) {
			String line = ((String)this.lines.get(i)).trim();
			if(isClass(line)) 
			{
				//Remove : from class line
				line = line.substring(0, line.length() - 1);
				
				if(obfuscated)
				{
					if(getObfuscatedName(line).equals(className))
					{
						return getNormalName(line).trim();
					}
				}
				else
				{
					if(getNormalName(line).equals(className))
					{
						return getObfuscatedName(line).trim();
					}
				}
			}
		}
		
		return null;
	}
	
	
	public String getFieldName(String fieldName, boolean obfuscated)
	{
		for (int i = 0; i < this.lines.size(); i++) {
			String line = ((String)this.lines.get(i)).trim();
			if(isField(line)) 
			{	
				if(obfuscated)
				{
					if(getObfuscatedName(line).equals(fieldName))
					{
						return getNormalName(line).trim();
					}
				}
				else
				{
					if(getNormalName(line).equals(fieldName))
					{
						return getObfuscatedName(line).trim();
					}
				}
			}
		}
		
		return null;
	}
	
	public String getMethodName(String methodName, boolean obfuscated)
	{
		for (int i = 0; i < this.lines.size(); i++) {
			String line = ((String)this.lines.get(i)).trim();
			if(isMethod(line)) 
			{	
				if(obfuscated)
				{
					if(getObfuscatedName(line).equals(methodName))
					{
						return getNormalName(line).trim();
					}
				}
				else
				{
					if(getNormalName(line).equals(methodName))
					{
						return getObfuscatedName(line).trim();
					}
				}
			}
		}
		
		return null;
	}
}
