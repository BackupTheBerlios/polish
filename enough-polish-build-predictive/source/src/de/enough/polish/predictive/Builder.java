package de.enough.polish.predictive;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Builder extends Task {
	
	private String source;
	private String directory;
	
	private String version;
	private String prefix;
	private int chunkSize;
	private int lineCount;
	
	
	private Vector words = new Vector();
	private BufferedReader reader;
	private FileOutputStream trieWriter;
	
	ByteArrayOutputStream trieStream = null;
	ByteArrayOutputStream headerStream = null;
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public void execute() throws BuildException {
		try {
			System.out.println("Building " + this.directory + "/predictive.header ...");
						
			Properties properties = buildHeader();
			properties.store(new FileOutputStream(directory + "/predictive.header"),"predictive.header");
			
			System.out.println("Reading " + this.source + "...");
			
			readWords();
			
			System.out.println("Building " + this.directory + "/predictive.trie ...");
			
			Element base = new Element();
			trieStream = new ByteArrayOutputStream( 0 );
			
			buildTrie(base,0);
			writeTrie(base);
			
			trieWriter = new FileOutputStream(directory + "/predictive.trie");
			trieWriter.write(trieStream.toByteArray());
			trieWriter.close();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readWords() throws Exception
	{
		String line = "";
		
		reader = new BufferedReader(new FileReader(source));

		while ((line = reader.readLine()) != null)
		{
			String word = line.trim().toLowerCase();
			if (!words.contains(word)) 
				words.add(word);
		}
	}
	
	public Properties buildHeader()
	{
		if(	this.version == null || this.prefix == null || this.chunkSize == 0 || this.lineCount == 0)
			throw new BuildException("One or more of the header informations is set : please make sure to set version, prefix, chunkSize and lineCount");
		
		Properties properties = new Properties();
		
		properties.put("trie.version", this.version);
		properties.put("trie.prefix", this.prefix);
		properties.put("trie.chunkSize", "" + this.chunkSize);
		properties.put("trie.lineCount", "" + this.lineCount);
		
		return properties;
	}
		
	public int buildTrie(Element element, int recordID)
	{
		Vector children = element.findChildren(words);
		
		element.referenceID = recordID;
		
		if(children.size() > 0)
			recordID++;
		
		for(int i=0; i<children.size(); i++)
		{
			Element child = (Element)children.elementAt(i);
			
			recordID = buildTrie(child, recordID);
		}
		
		return recordID;
	}

	public void writeTrie(Element element) throws Exception
	{
		Vector children = element.children;
		char referenceID = 0;
		
		if(children.size() > 0)
			trieStream.write(byteToByte(children.size()));
		
		for(int i=0; i < children.size(); i++)
		{
			Element child = (Element)children.elementAt(i);
			
			trieStream.write(charToByte(child.value));
			trieStream.write(byteToByte(child.children.size()));
			
			if(child.children.size() > 0)
				referenceID = (char)((Element)child.children.elementAt(0)).referenceID;
			else
				referenceID = (char)0;
			
			trieStream.write(charToByte(referenceID));
		}
		
		for(int i=0; i < children.size(); i++)
		{
			Element child = (Element)children.elementAt(i);
			writeTrie(child);
		}
				
		return;			
	}
		
	private byte[] byteToByte (final int integer) throws IOException {
		byte[] result = new byte[1];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(integer);
		dos.flush();
		result[0] = bos.toByteArray()[3];
		return result;
	}
	
	private byte[] charToByte (final char character) throws IOException {	
	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
	      DataOutputStream dos = new DataOutputStream(bos);
	      dos.writeChar(character);
	      dos.flush();
	      return bos.toByteArray();
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDirectory() {
		return directory;
	}

	public String getSource() {
		return source;
	}
}
