//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput 
package de.enough.polish.predictive.trie;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.PredictiveAccess;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TrieOrder {
	
	private static int COUNT_SIZE = 1;
	private static int KEY_SIZE = 1;
	private static int INDEX_SIZE = 1;
	
	byte[] 	bytes = null;
	
	public TrieOrder()
	{
		bytes 	= load();
	}
	
	public byte[] load()
	{
		try
		{
			bytes  = PredictiveAccess.PROVIDER.getRecord(TrieInstaller.ORDER_RECORD);
			
			if(bytes == null)
				bytes = new byte[0];
			
			return bytes;
		}
		catch(RecordStoreException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void addOrder(int[] keyCodes, byte index)
	{
		byte[] buffer 	= new byte[COUNT_SIZE + (keyCodes.length * KEY_SIZE)  + INDEX_SIZE];
		byte[] temp 	= null;
		
		buffer[0] = (byte)keyCodes.length;
		
		for(int i=0; i<keyCodes.length; i++ )
		{
			buffer[i + COUNT_SIZE] = (byte)(keyCodes[i] % TrieTextElement.SHIFT);
		}
		
		buffer[buffer.length - 1] = index;
		
		temp = new byte[bytes.length + buffer.length];
		
		System.arraycopy(bytes, 0, temp, 0, bytes.length);
		System.arraycopy(buffer, 0, temp, bytes.length, buffer.length);
		
		bytes = new byte[bytes.length + buffer.length];
		
		System.arraycopy(temp, 0, bytes, 0, temp.length);
		
		save(bytes);
	}
	
	public void save(byte[] bytes)
	{
		try
		{
			PredictiveAccess.PROVIDER.setRecord(TrieInstaller.ORDER_RECORD, bytes);
		}
		catch(RecordStoreException e)
		{
			e.printStackTrace();
		}
	}
	
	public void getOrder(ArrayList words, int[] keyCodes)
	{
		if(bytes.length > 0)
		{
			for(int i=0; i<bytes.length; i+= COUNT_SIZE + (bytes[i] * KEY_SIZE) + INDEX_SIZE)
			{
				if(matchesKeyCode(keyCodes, i))
				{	
					byte length = bytes[i];
					byte index = TrieUtils.byteToByte(bytes, i + COUNT_SIZE + (length * KEY_SIZE));
					
					if(index > 0 && index < words.size())
					{
						String word = (String)words.get(index);
						words.remove(index);
						words.add(0, word);
					}
				}
			}
		}
	}
	
	public boolean matchesKeyCode(int[] keyCodes,int offset)
	{
		if(this.bytes[offset] != keyCodes.length)
		{
			return false;
		}
		else
		{
			int start 	= offset + 1;
			int end 	= (bytes[offset] * KEY_SIZE) + start;
			
			for(int i=start; i < end;i += KEY_SIZE)
			{
				byte keyCode = bytes[i];
				byte storedKeyCode = (byte)(keyCodes[(i - offset - 1) / KEY_SIZE]);
				
				if(keyCode != storedKeyCode)
				{
					return false;
				}
			}
						
			return true;
		}
		
	}
}
