//#condition polish.TextField.useDirectInput && !polish.blackberry
package de.enough.polish.predictive;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TextElement {
	Object element = null;
	boolean[] shift = null;
	int shiftPosition = 0;
	int[] keyCodes = null;
	
	ArrayList results;
	int selectedWord;
	
	public TextElement(Object object) {
		this.element 		= object;
		
		this.shift 			= new boolean[10];
		this.shiftPosition 	= 0;
		
		this.results		= new ArrayList();
		this.selectedWord	= 0;
		
		this.keyCodes 		= new int[0];
	}

	public int getLength() {
		if (element != null) {
			if (element instanceof Character) {
				return 1;
			} else if (element instanceof TrieReader) {
				return this.getSelectedWord().length();
			}
		}

		return -1;
	}
	
	public void pushKeyCode(int keyCode, int shift)
	{	
		int[] newKeyCodes = new int[this.keyCodes.length + 1];
		System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length);
		newKeyCodes[newKeyCodes.length - 1] = keyCode;
		this.keyCodes = newKeyCodes;
		
		if(this.shiftPosition >= this.shift.length)
		{
			boolean[] newShift = new boolean[this.shift.length * 2];
			System.arraycopy(this.shift, 0, newShift, 0, this.shift.length);
			this.shift = newShift;
		}
		
		if(shift == TextField.MODE_UPPERCASE || shift == TextField.MODE_FIRST_UPPERCASE)
			this.shift[this.shiftPosition] = true;
		else
			this.shift[this.shiftPosition] = false;
		
		this.shiftPosition++;
	}
	
	public void popKeyCode()
	{
		int[] newKeyCodes = new int[this.keyCodes.length - 1];
		System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length - 1);
		this.keyCodes = newKeyCodes;
		
		this.shiftPosition--;
	}

	public void setResults(TrieCustom custom) {
		
		StringBuffer string;
		
		if (element instanceof TrieReader) {
			this.results = ((TrieReader) element).getResults();
			
			custom.getWords(this.results,this.keyCodes);
			
			for (int i = 0; i < this.results.size(); i++) {
				string = (StringBuffer)this.results.get(i);
				for (int j = 0; j < shift.length; j++) {
					if (shift[j] == true)
						string.setCharAt(j, Character.toUpperCase(string.charAt(j)));
				}
			}
		}
	}
	
	public StringBuffer getSelectedWord()
	{
		StringBuffer result = null;
		
		result = (StringBuffer)this.results.get(selectedWord);
		
		for (int j = 0; j < shift.length; j++) {
			if (shift[j] == true)
				result.setCharAt(j,Character.toUpperCase(result.charAt(j)));
		}
		
		return result;
	}
	
	public ArrayList getResults()
	{
		return this.results;
	}
	
	public void setSelectedWord(int selectedWord)
	{
		this.selectedWord = selectedWord;
	}

	public void pushChar(int shift) {
		if(this.shiftPosition >= this.shift.length)
		{
			boolean[] newShift = new boolean[this.shift.length * 2];
			System.arraycopy(this.shift, 0, newShift, 0, this.shift.length);
			this.shift = newShift;
		}
		
		if(shift == TextField.MODE_UPPERCASE || shift == TextField.MODE_FIRST_UPPERCASE)
			this.shift[this.shiftPosition] = true;
		else
			this.shift[this.shiftPosition] = false;
		
		this.shiftPosition++;
	}

	public void popChar() {
		this.shiftPosition--;
	}

	public Object getElement() {
		return element;
	}
}
