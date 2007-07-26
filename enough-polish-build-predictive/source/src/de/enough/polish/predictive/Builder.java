package de.enough.polish.predictive;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import de.enough.polish.ui.TextField;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Builder extends Task {
	
	private String source;
	private String directory;
	
	private int magic;
	private int version;
	private int chunkSize;
	private int lineCount;
	
	private Vector words = new Vector();
	private BufferedReader reader;
	private FileOutputStream trieWriter;
	
	ByteArrayOutputStream stream = null;
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
	public void execute() throws BuildException {
		try {
			byte[] header = getHeader();
			
			System.out.println("Reading " + this.source + "...");
			
			readCharacters();
			readWords();
			
			System.out.println("Building " + this.directory + "/predictive.trie ...");
			
			Element base = new Element();
			stream = new ByteArrayOutputStream( 0 );
			
			buildTrie(base,0);
			writeTrie(base);
			
			(new File(directory)).mkdir();
			
			trieWriter = new FileOutputStream(directory + "/predictive.trie");
			trieWriter.write(header);
			trieWriter.write(stream.toByteArray());
			trieWriter.close();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readCharacters()
	{
		for(int i=0; i<TextField.CHARACTERS.length; i++)
		{
			String characters = TextField.CHARACTERS[i];
			
			for(int j=0; j<characters.length(); j++)
			{
				words.add("" + characters.charAt(j));
			}
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
	
	public byte[] getHeader() throws IOException
	{
		if(	this.magic == 0  || this.version == 0 || this.chunkSize == 0 || this.lineCount == 0)
			throw new BuildException("One or more of the header informations is not set : please make sure to set magic, version, chunkSize and lineCount");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    DataOutputStream dos = new DataOutputStream(bos);
	    
	    dos.writeInt(this.magic);
	    dos.writeInt(this.version);
	    dos.writeInt(this.chunkSize);
	    dos.writeInt(this.lineCount);
	    
	    return bos.toByteArray();
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
			stream.write(byteToByte(children.size()));
		
		for(int i=0; i < children.size(); i++)
		{
			Element child = (Element)children.elementAt(i);
			
			stream.write(charToByte(child.value));
			stream.write(byteToByte(child.children.size()));
			
			if(child.children.size() > 0)
				referenceID = (char)((Element)child.children.elementAt(0)).referenceID;
			else
				referenceID = (char)0;
			
			stream.write(charToByte(referenceID));
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getDirectory() {
		return directory;
	}

	public String getSource() {
		return source;
	}

	public int getMagic() {
		return magic;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}
}
