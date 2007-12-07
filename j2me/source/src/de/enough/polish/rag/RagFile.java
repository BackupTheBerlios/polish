package de.enough.polish.rag;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;

import de.enough.polish.io.Serializable;
import de.enough.polish.util.ArrayList;

public class RagFile {
	private DataInputStream stream = null;
	
	public RagFile(DataInputStream stream)
	{
		this.stream = stream;
	}
	
	public RagContainer getContainer(String id)
	{
		try
		{
			while(true)
			{
				String containerId = this.stream.readUTF();
				System.out.println(containerId);
				long containerSize = this.stream.readLong();
				
				if(containerId.equals(id))
				{
					RagContainer container = new RagContainer();
					container.setName(containerId);
					container.setSize(containerSize);
					
					byte[] data = new byte[(int)containerSize];
					this.stream.read(data, 0, (int)containerSize);
					
					container.setData(data);
					
					return container;
				}
				else
				{
					this.stream.skip(containerSize);
				}
			}
		}
		catch (EOFException e) {}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}	
