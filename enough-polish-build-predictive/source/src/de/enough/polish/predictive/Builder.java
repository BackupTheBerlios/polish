package de.enough.polish.predictive;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import de.enough.polish.ui.TextField;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Builder extends Task {
	public static int TYPE_RMS = 0;
	public static int TYPE_FILE = 0;
	
	private String source;
	private String language;
	
	private int magic;
	private int version;
	private int chunkSize;
	private int lineCount;
	private int type;
	
	private Vector words = new Vector();
	private BufferedReader reader;
	private FileOutputStream trieWriter;
	
	ByteArrayOutputStream stream = null;
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public void execute() throws BuildException {
		try {
			byte[] header = getHeader();
			
			System.out.println("Reading " + this.source + "...");
			
			readCharacters();
			readWords();
			
			String file = "./output/predictive." + this.language + ".trie";
			
			System.out.println("Building " + file + " ...");
			
			Element base = new Element();
			this.stream = new ByteArrayOutputStream( 0 );
			
			buildTrie(base,0);
			writeTrie(base);
			
			(new File("./output")).mkdir();
			
			this.trieWriter = new FileOutputStream(file);
			this.trieWriter.write(header);
			this.trieWriter.write(this.stream.toByteArray());
			this.trieWriter.close();			
			
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
				this.words.add("" + characters.charAt(j));
			}
		}
	}
	
	public void readWords() throws Exception
	{
		String line = "";
		
		this.reader = new BufferedReader( new InputStreamReader(new FileInputStream(this.source), "UTF8"));

		while ((line = this.reader.readLine()) != null)
		{
			String word = line.trim().toLowerCase();
			
			if(!word.startsWith("#"))
			{
				if (!this.words.contains(word)) 
					this.words.add(word);
			}
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
	    dos.writeInt(this.type);
	    
	    return bos.toByteArray();
	}
		
	public int buildTrie(Element element, int recordID)
	{
		Vector children = element.findChildren(this.words);
		
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
			this.stream.write(byteToByte(children.size()));
		
		for(int i=0; i < children.size(); i++)
		{
			Element child = (Element)children.elementAt(i);
			
			this.stream.write(charToByte(child.value));
			this.stream.write(byteToByte(child.children.size()));
			
			if(child.children.size() > 0)
				referenceID = (char)((Element)child.children.elementAt(0)).referenceID;
			else
				referenceID = (char)0;
			
			this.stream.write(charToByte(referenceID));
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
		return this.chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public int getLineCount() {
		return this.lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getLanguage() {
		return this.language;
	}

	public String getSource() {
		return this.source;
	}

	public int getMagic() {
		return this.magic;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
