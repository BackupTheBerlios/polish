/*
 * Created on 14-Jan-2004 at 16:07:22.
 *
 * Copyright (c) 2004 Robert Virkus / Enough Software
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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Provides some often used methods for handling files.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        14-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class FileUtil {
	
	/**
	 * Reads a text file.
	 *  
	 * @param fileName the name of the text file
	 * @return the lines of the text file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 */
	public static String[] readTextFile(String fileName ) 
	throws FileNotFoundException, IOException 
	{
		return readTextFile( new File( fileName) );
	}
	
	/**
	 * Reads a text file.
	 *  
	 * @param file the text file
	 * @return the lines of the text file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 */
	public static String[] readTextFile( File file ) 
	throws FileNotFoundException, IOException 
	{
		ArrayList lines = new ArrayList();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		while ((line = in.readLine()) != null) {
			lines.add( line );
		}
		in.close();
		return (String[]) lines.toArray( new String[ lines.size() ]);
	}

	/**
	 * Writes (and creates) a text file.
	 * 
	 * @param file the file to which the text should be written
	 * @param lines the text lines of the file
	 * @throws IOException when there is an input/output error during the saving
	 */
	public static void writeTextFile(File file, String[] lines) 
	throws IOException 
	{
		File parentDir = file.getParentFile(); 
		if (! parentDir.exists()) {
			parentDir.mkdirs();
		}
		PrintWriter out = new PrintWriter(new FileWriter( file ) );
		for (int i = 0; i < lines.length; i++) {
			out.println( lines[i] );
		}
		out.close();
	}

	/**
	 * Copies the given files to the specified target directory.
	 * 
	 * @param files The files which should be copied.
	 * @param targetDir The directory to which the given files should be copied to.
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 * @throws NullPointerException when files or targetDir is null.
	 */
	public static void copy(File[] files, File targetDir) 
	throws FileNotFoundException, IOException 
	{
		String targetPath = targetDir.getAbsolutePath() + File.separatorChar;
		byte[] buffer = new byte[ 1024 * 1024 ];
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			copy( file, new File( targetPath + file.getName() ), buffer );
		}
	}

	/**
	 * Copies a file.
	 * 
	 * @param source The file which should be copied
	 * @param target The file to which the source-file should be copied to.
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 */
	public static void copy(File source, File target) 
	throws FileNotFoundException, IOException 
	{
		copy( source, target, new byte[ 1024 * 1024 ] );
	}
	
	/**
	 * Copies a file.
	 * 
	 * @param source The file which should be copied
	 * @param target The file to which the source-file should be copied to.
	 * @param buffer A buffer used for the copying.
	 * @throws FileNotFoundException when the source file was not found
	 * @throws IOException when there is an error while copying the file.
	 */
	private static void copy(File source, File target, byte[] buffer ) 
	throws FileNotFoundException, IOException 
	{
		InputStream in = new FileInputStream( source );
		// create parent directory of target-file if necessary:
		File parent = target.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		OutputStream out = new FileOutputStream( target );
		int read;
		try {
			while ( (read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read );
			}
		} catch (IOException e) {
			throw e;
		} finally {
			in.close();
			out.close();
		}
	}
	
	/**
	 * Writes the properties which are defined in the given HashMap into a textfile.
	 * The notation in the file will be [name]=[value]\n for each defined property.
	 * 
	 * @param file the file which should be created or overwritten
	 * @param properties the properties which should be written. 
	 * @throws IOException when there is an input/output error during the saving
	 */
	public static void writePropertiesFile( File file, Map properties ) 
	throws IOException 
	{
		Object[] keys = properties.keySet().toArray();
		String[] lines = new String[ keys.length ];
		for (int i = 0; i < lines.length; i++) {
			String key = keys[i].toString();
			String value = properties.get( key ).toString();
			lines[i] = key.toString() + "=" + value.toString();
		}
		writeTextFile( file, lines );
	}

	/**
	 * Reads a properties file.
	 * The notation of the file needs to be 
	 * "[name]=[value]\n"
	 * for each defined property.
	 * 
	 * @param file the file containing the properties
	 * @return a hashmap containing all properties found in the file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 * @throws IllegalArgumentException when the line does not contain a property
	 */
	public static HashMap readPropertiesFile( File file ) 
	throws FileNotFoundException, IOException 
	{
		return readPropertiesFile(file, '=');
	}
	
	/**
	 * Reads a properties file.
	 * The notation of the file needs to be 
	 * "[name]=[value]\n"
	 * for each defined property.
	 * 
	 * @param file the file containing the properties
	 * @param delimiter the sign which is used for separating a property-name from a property-value.
	 * @return a hashmap containing all properties found in the file
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 * @throws IllegalArgumentException when the line does not contain a property
	 */
	public static HashMap readPropertiesFile( File file, char delimiter ) 
	throws FileNotFoundException, IOException 
	{
		
		HashMap map = new HashMap();
		readPropertiesFile( file, delimiter, map );
		return map;
	}

	/**
	 * Reads a properties file.
	 * The notation of the file needs to be 
	 * "[name]=[value]\n"
	 * for each defined property.
	 * 
	 * @param file the file containing the properties
	 * @param delimiter the sign which is used for separating a property-name from a property-value.
	 * @param map the hash map to which the properties should be added 
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when file could not be read.
	 * @throws IllegalArgumentException when the line does not contain a property
	 */
	public static void readPropertiesFile( File file, char delimiter, Map map ) 
	throws FileNotFoundException, IOException 
	{
		String[] lines = readTextFile( file );
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length() > 0 && line.charAt(0) != '#') {
				int equalsPos = line.indexOf( delimiter );
				if (equalsPos == -1) {
					throw new IllegalArgumentException("The line [" + line 
							+ "] of the file [" + file.getAbsolutePath() 
							+ "] contains an invalid property definition: " +
									"missing separater-character (\"" + delimiter + "\")." );
				}
				String key = line.substring(0, equalsPos );
				String value = line.substring( equalsPos + 1);
				map.put( key, value );
			}
		}
	}
	
	/**
	 * Copies the contents of a directory to the specified target directory.
	 * 
	 * @param directory the directory containing files
	 * @param targetDirName the directory to which the files should be copied to
	 * @param update is true when files should be only copied when the source files
	 * 	are newer compared to the target files.
	 * @throws IOException when a file could not be copied
	 * @throws IllegalArgumentException when the directory is not a directory.
	 */
	public static void copyDirectoryContents(File directory, String targetDirName, boolean update)
	throws IOException
	{
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Cannot copy contents of the file [" + directory.getAbsolutePath() + "]: specify a directory instead.");
		}
		String[] fileNames = directory.list();
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			File file = new File( directory.getAbsolutePath() 
					+ File.separatorChar + fileName );
			if (file.isDirectory()) {
				copyDirectoryContents( file, targetDirName + File.separatorChar + fileName, update );
			}  else {
				File targetFile = new File( targetDirName 
						+ File.separatorChar + fileName  );
				if (update) {
					// update only when the source file is newer:
					if ( (!targetFile.exists())
						|| (file.lastModified() > targetFile.lastModified() )) {
						copy( file, targetFile );
					}
				} else {
					// copy the file in all cases:
					copy( file, targetFile );
				}
			}
		}
	}
	
}
