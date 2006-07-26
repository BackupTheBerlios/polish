package de.enough.polish.postcompile.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;

public class TestSerialization_floatArray_template
	implements Externalizable
{
	public float[] i;

	public void read(DataInputStream input)
	  throws IOException
	{
		if (input.readBoolean())
		  {
			int length = input.readInt();
			this.i = new float[length];
			
			for (int i = 0; i < length; i++)
			{
				this.i[i] = input.readFloat();
			}
		  }
	}
	
	public void write(DataOutputStream output)
	  throws IOException
	{
		if (this.i == null)
		  {
			output.writeBoolean(false);
		  }
		else
		  {
			output.writeBoolean(true);
			int length = this.i.length;
			output.writeInt(length);
			
			for (int i = 0; i < length; i++)
			  {
			    output.writeFloat(this.i[i]);
			  }
		  }
	}
}
