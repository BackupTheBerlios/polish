//#condition polish.JavaSE && polish.useRAG
package de.enough.polish.rag.obfuscation;

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
public class RagMap {
	RagEntry root;

	/**
	 * Opens a file and reads the lines to <code>lines</code>
	 * @param file the file
	 * @throws IOException
	 */
	public RagMap(File file) throws IOException {
		FileInputStream fileStream = null;
		BufferedInputStream bufferedStream = null;
		
		DataInputStream dataStream = null;
		fileStream = new FileInputStream(file);

		bufferedStream = new BufferedInputStream(fileStream);
		dataStream = new DataInputStream(bufferedStream);

		ArrayList lines = new ArrayList();
		
		while (dataStream.available() != 0) {
			lines.add(dataStream.readLine());
		}
		
		fileStream.close();
		bufferedStream.close();
		dataStream.close();
		
		this.root = parseMap(lines);
	}
	
	private RagEntry parseMap(ArrayList lines)
	{
		RagEntry rootEntry = new RagEntry();
		
		rootEntry.setGroup(RagEntry.ROOT);
		
		RagEntry classEntry = null;
		
		for (int i = 0; i < lines.size(); i++) {
			RagEntry entry = parseLine((String)lines.get(i));
			
			switch(entry.getGroup())
			{
				case RagEntry.CLASS:
					classEntry = entry;
					rootEntry.getChildren().add(classEntry);
					break;
				case RagEntry.METHOD:
				case RagEntry.FIELD:
					classEntry.getChildren().add(entry);
					break;
			}
		}
		
		return rootEntry;
	}
	
	private RagEntry parseLine(String line)
	{
		RagEntry entry = new RagEntry();
	
		line = line.trim();
		String[] ids = line.split("->");
		
		if(isClass(line))
		{
			entry.setGroup(RagEntry.CLASS);
			
			entry.setType(null);
			entry.setName(ids[0].trim());
			entry.setObfuscated(ids[1].trim().replace(":", "")); 
		}
		else if(isMethod(line))
		{
			entry.setGroup(RagEntry.METHOD);
			
			String type = ids[0].trim().split(" ")[0];
			String name = ids[0].trim().split(" ")[1];
			
			entry.setType(type);
			entry.setName(name);
			entry.setObfuscated(ids[1].trim());
		}
		else if(isField(line))
		{
			entry.setGroup(RagEntry.FIELD);
			
			String type = ids[0].trim().split(" ")[0];
			String name = ids[0].trim().split(" ")[1];
			
			entry.setType(type);
			entry.setName(name);
			entry.setObfuscated(ids[1].trim());
		}
		
		return entry;
	}
	

	private boolean isClass(String line)
	{
		return line.indexOf(":") != -1 && line.indexOf(".") != -1;
	}
	
	private boolean isField(String line)
	{
		String name = line.split("->")[0].trim();
		return name.indexOf(" ") != -1 && name.indexOf("(") == -1;
	}
	
	private boolean isMethod(String line)
	{
		String name = line.split("->")[0].trim();
		return name.indexOf(" ") != -1 && name.indexOf("(") != -1;
	}
	
	/**
	 * Returns the obfuscated or normal name for <code>className</code>
	 * @param name a full class specifier
	 * @param obfuscated true, if <code>className</code> is an obfuscation name and should be resolved
	 * @return the entry of the class
	 */
	public static RagEntry getChildEntry(RagEntry entry, String name, boolean obfuscated)
	{
		ArrayList classes = entry.getChildren();
		
		for (int i = 0; i < classes.size(); i++) {
			RagEntry child = (RagEntry)classes.get(i);
			
			if(obfuscated)
			{
				if(child.getObfuscated().equals(name))
				{
					return child;
				}
			}
			else
			{
				if(child.getName().equals(name))
				{
					return child;
				}
			}
		}
		
		return null;
	}

	public RagEntry getRoot() {
		return root;
	}

	public void setRoot(RagEntry root) {
		this.root = root;
	}
}
