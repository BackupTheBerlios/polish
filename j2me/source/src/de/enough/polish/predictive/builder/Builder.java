package de.enough.polish.predictive.builder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Builder extends Task {
	
	private String source;
	private String dest;
	private Vector words = new Vector();
	private BufferedReader reader;
	private FileOutputStream writer;
	ByteArrayOutputStream stream = new ByteArrayOutputStream( 0 );
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public void setDest(String dest) {
		this.dest = dest;
	}
	
	public void execute() throws BuildException {
		try {
			System.out.println("Reading " + source + "...");
			
			readWords();

			System.out.println("Building " + dest + "...");
			
			Element base = new Element();
						
			buildTrie(base,0);
			writeTrie(base);
			
			writer = new FileOutputStream(dest);
			writer.write(stream.toByteArray());
			writer.close();			
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
}
