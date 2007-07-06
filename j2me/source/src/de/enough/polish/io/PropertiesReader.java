package de.enough.polish.io;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.TextUtil;

public class PropertiesReader extends HashMap{
	
	public PropertiesReader(DataInputStream stream)
	{
		try
		{
			String line = "";
			
			while(true)
			{
				line = readLine(stream);
				
				if(line.charAt(0) != '#')
				{
					String[] values = TextUtil.split(line, '=');
					try
					{
						Integer number = Integer.valueOf(values[1]);
						this.put(values[0], number);
					}
					catch(NumberFormatException ex)
					{
						this.put(values[0], values[1]);
					}
				}
			}
		}
		catch(EOFException e){}
		catch(IOException e){e.printStackTrace();}
	}
	
	private String readLine(DataInputStream stream) throws IOException
	{
		byte character;
		String result = "";
		
		while((char)(character = stream.readByte()) != '\r')
			result += (char)character;
		
		//Skip newline \n
		stream.readByte();
		
		return result;
	}
	
	public int getInteger(String key)
	{
		return ((Integer)get(key)).intValue();
	}
	
	public String getString(String key)
	{
		return (String)get(key);
	}
	
	
	
	
}
