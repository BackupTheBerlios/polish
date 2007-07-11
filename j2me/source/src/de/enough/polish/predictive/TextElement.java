//#condition polish.TextField.useDirectInput && !polish.blackberry
package de.enough.polish.predictive;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TextElement {
	Object element = null;
	boolean[] shift = null;
	int shiftPosition = 0;
	
	public TextElement(Object object) {
		this.element 		= object;
		this.shift 			= new boolean[10];
		this.shiftPosition 	= 0;
	}

	public int getLength() {
		if (element != null) {
			if (element instanceof Character) {
				return 1;
			} else if (element instanceof TrieReader) {
				TrieReader reader = (TrieReader) element;
				return reader.getSelectedWord().length();
			}
		}

		return -1;
	}

	public ArrayList getResults() {
		ArrayList results;
		StringBuffer string;
		
		if (element instanceof TrieReader) {
			results = ((TrieReader) element).getResults();

			for (int i = 0; i < results.size(); i++) {
				string = (StringBuffer)results.get(i);
				for (int j = 0; j < shift.length; j++) {
					if (shift[j] == true)
						string.setCharAt(j, Character.toUpperCase(string.charAt(j)));
				}
			}
			
			return results;
		}

		return null;
	}
	
	public StringBuffer getSelectedWord()
	{
		StringBuffer result = null;
		
		if (element instanceof TrieReader) {
			result = ((TrieReader) element).getSelectedWord();
			
			for (int j = 0; j < shift.length; j++) {
				if (shift[j] == true)
					result.setCharAt(j,Character.toUpperCase(result.charAt(j)));
			}
		}
		
		return result;
	}
	
	public void setSelectedWord(int index)
	{
		((TrieReader)element).setSelectedWord(index);
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
