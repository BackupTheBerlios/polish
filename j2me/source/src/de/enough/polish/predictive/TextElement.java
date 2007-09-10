//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput 
package de.enough.polish.predictive;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public abstract class TextElement {
	protected Object element = null; 
	
	protected int[] keyCodes = null; 
	
	protected int selectedWordIndex;		
	
	public static final int SHIFT = 10000;
	
	public TextElement(Object object) 
	{
		this.element 		= object;
		
		this.selectedWordIndex	= 0;
		
		this.keyCodes 			= new int[0];
	}

	public int getLength() {
		if (element != null) {
			if (element instanceof StringBuffer) {
				return ((StringBuffer)element).length();
			} else if (element instanceof Reader) {
				return this.getSelectedWord().length();
			}
		}

		return -1;
	}
	
	public void keyNum(int keyCode, int shift) 
	{	
		int[] newKeyCodes = new int[this.keyCodes.length + 1];
		System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length);
		newKeyCodes[newKeyCodes.length - 1] = keyCode;
		this.keyCodes = newKeyCodes;
		
		if(shift == TextField.MODE_UPPERCASE || shift == TextField.MODE_FIRST_UPPERCASE)
			this.keyCodes[this.keyCodes.length - 1] += SHIFT;
		
		this.selectedWordIndex = 0;
	}
	
	public void keyClear() 
	{
		if(this.keyCodes.length > 0)
		{
			int[] newKeyCodes = new int[this.keyCodes.length - 1];
			System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length - 1);
			this.keyCodes = newKeyCodes;
			
			this.selectedWordIndex = 0;
		}
	}
	
	public boolean isStringBuffer()
	{
		if(this.element instanceof StringBuffer)
			return true;
		else
			return false;
	}
		
	public void shiftResults(ArrayList results)
	{
		StringBuffer string = null;
		
		for (int i = 0; i < results.size(); i++) {
			
			string = (StringBuffer)results.get(i);
			
			for (int j = 0; j < keyCodes.length; j++)
			{
				if (keyCodes[j] > SHIFT && j < string.length())
					string.setCharAt(j, Character.toUpperCase(string.charAt(j)));
			}
		}

	}

	public abstract void setResults();
	
	protected abstract StringBuffer getSelectedStringBuffer();
	
	public abstract void setSelectedWordIndex(int selected);
	

	
	public int getSelectedWordIndex()
	{
		return this.selectedWordIndex;
	}

	
	public StringBuffer getSelectedWord()
	{
		if(keyCodes.length == getSelectedStringBuffer().length())
			return getSelectedStringBuffer();
		else
		{
			StringBuffer string = new StringBuffer();
			string.append(getSelectedStringBuffer());
			string.setLength(this.keyCodes.length);
			return string;
		}
		
	}
	
	public abstract boolean isSelectedCustom();
	
	public abstract void convertReader();
	
	public abstract ArrayList getResults();
	
	public abstract ArrayList getCustomResults();
		
	public abstract boolean isWordFound();

	public Object getElement() {
		return this.element;
	}
	
	public int getKeyCount()
	{
		return this.keyCodes.length;
	}
}
