//#condition polish.TextField.useDirectInput && !polish.blackberry
package de.enough.polish.predictive;

import javax.microedition.lcdui.Canvas;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TrieCustom {
	
	private static int COUNT_SIZE = 1;
	private static int CHAR_SIZE = 2;
	
	byte[] 	bytes = null;
	
	private RecordStore store = null;
	
	public TrieCustom()
	{
		bytes 	= load();
	}
	
	public byte[] load()
	{
		try
		{
			store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0",false);
			
			bytes  = store.getRecord(TrieInstaller.CUSTOM_RECORD);
			
			if(bytes == null)
				bytes = new byte[0];
			
			store.closeRecordStore();
			
			return bytes;
		}
		catch(RecordStoreException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void addWord(String word)
	{
		if(word.length() > 0)
		{
			word = word.toLowerCase();
			
			char[] chars	= word.toCharArray();
			byte[] temp 	= null;
			
			byte length = (byte)chars.length;
			
			byte[] buffer = new byte[COUNT_SIZE + (length * CHAR_SIZE)];
			
			buffer[0] = length;
			
			for(int i=0; i<length; i++)
			{
				buffer[(i * CHAR_SIZE) + 1] = (byte) ((chars[i] >> 8) & 0x000000FF);
				buffer[(i * CHAR_SIZE) + 2] = (byte) (chars[i] & 0x00FF);
			}
			
			temp = new byte[bytes.length + buffer.length];
			
			System.arraycopy(bytes, 0, temp, 0, bytes.length);
			System.arraycopy(buffer, 0, temp, bytes.length, buffer.length);
			
			bytes = new byte[bytes.length + buffer.length];
			
			System.arraycopy(temp, 0, bytes, 0, temp.length);
			
			save(bytes);
		}
	}
	
	public void save(byte[] bytes)
	{
		try
		{
			store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0", false);
			store.setRecord(TrieInstaller.CUSTOM_RECORD, bytes, 0, bytes.length);
			store.closeRecordStore();
		}
		catch(RecordStoreException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean hasWords(int[] keyCodes)
	{
		boolean relevant = true;
		
		for(int i=0; i<bytes.length; i+= ((bytes[i] * CHAR_SIZE) + COUNT_SIZE))
		{
			char character;
			
			if(keyCodes.length <= bytes[i])
			{
				relevant = true;
				
				for(int k=0; k < keyCodes.length; k++)
				{
					character = byteToChar(bytes, i + COUNT_SIZE + (k * CHAR_SIZE));
						
					if(!isInCharset(keyCodes[k],character))
					{
						relevant = false;
						break;
					}
				}
				
				if(relevant == true)
				{
					return true;
				}
			}	
		}
		
		return false;
	}
	
	public void getWords(ArrayList words, int[] keyCodes)
	{
		boolean relevant = true;
		
		for(int i=0; i<bytes.length; i+= ((bytes[i] * CHAR_SIZE) + COUNT_SIZE))
		{
			char character;
			
			if(keyCodes.length <= bytes[i])
			{
				relevant = true;
				
				for(int k=0; k < keyCodes.length; k++)
				{
					character = byteToChar(bytes, i + COUNT_SIZE + (k * CHAR_SIZE));
					
					if(!isInCharset(keyCodes[k],character))
					{
						relevant = false;
						break;
					}
				}
				
				if(relevant == true)
				{
					words.add(getWord(bytes, i));
				}
			}
		}
	}
	
	private boolean isInCharset(int keyCode, char character)
	{
		if(keyCode > TextElement.SHIFT)
			keyCode -= TextElement.SHIFT;
		
		switch(keyCode)
		{
			case Canvas.KEY_NUM0 : return (TextField.CHARACTERS[0].indexOf(character) != -1);
			case Canvas.KEY_NUM1 : return (TextField.CHARACTERS[1].indexOf(character) != -1);
			case Canvas.KEY_NUM2 : return (TextField.CHARACTERS[2].indexOf(character) != -1);
			case Canvas.KEY_NUM3 : return (TextField.CHARACTERS[3].indexOf(character) != -1);
			case Canvas.KEY_NUM4 : return (TextField.CHARACTERS[4].indexOf(character) != -1);
			case Canvas.KEY_NUM5 : return (TextField.CHARACTERS[5].indexOf(character) != -1);
			case Canvas.KEY_NUM6 : return (TextField.CHARACTERS[6].indexOf(character) != -1);
			case Canvas.KEY_NUM7 : return (TextField.CHARACTERS[7].indexOf(character) != -1);
			case Canvas.KEY_NUM8 : return (TextField.CHARACTERS[8].indexOf(character) != -1);
			case Canvas.KEY_NUM9 : return (TextField.CHARACTERS[9].indexOf(character) != -1);
		}
		
		return false;
	}
	
	private char byteToChar(byte[] bytes, int offset)
	{
		int high = bytes[offset] & 0xff;
		int low = bytes[offset+1] & 0xff;
		return (char)((int)( high << 8 | low ));
	}
	
	private StringBuffer getWord(byte[] array, int offset)
	{
		StringBuffer result = new StringBuffer();
		
		for(int i=0; i < array[offset] * CHAR_SIZE; i = i + CHAR_SIZE)
			result.append(byteToChar(array, offset + i + 1));
		
		return result;
	}
}
