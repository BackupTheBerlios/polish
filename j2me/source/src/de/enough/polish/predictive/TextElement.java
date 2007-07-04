package de.enough.polish.predictive;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TextElement {
	Object element = null;

	String shift = null;

	public TextElement(Object object) {
		this.element = object;
		this.shift = "";
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

	public StringBuffer[] getResults() {
		StringBuffer[] results;
		
		if (element instanceof TrieReader) {
			results = ((TrieReader) element).getResults();

			for (int i = 0; i < results.length; i++) {
				for (int j = 0; j < shift.length(); j++) {
					if (shift.charAt(j) == '1')
						results[i].setCharAt(j, Character.toUpperCase(results[i].charAt(j)));
				}
			}
			
			return results;
		}

		return null;
	}
	
	public String getSelectedWord()
	{
		String result = "";
		
		if (element instanceof TrieReader) {
			result = ((TrieReader) element).getSelectedWord();
			
			char[] resultArray = result.toCharArray();

			for (int j = 0; j < shift.length(); j++) {
				if (shift.charAt(j) == '1')
					resultArray[j] = Character.toUpperCase(resultArray[j]);
			}
				
			result = new String(resultArray);
		}
		
		return result;
	}
	
	public void setSelectedWord(int index)
	{
		((TrieReader)element).setSelectedWord(index);
	}

	public void pushChar(int shift) {
		if(shift == TextField.MODE_UPPERCASE || shift == TextField.MODE_FIRST_UPPERCASE)
			this.shift += '1';
		else
			this.shift += '0';
	}

	public void popChar() {
		if(shift.length() > 0)
			shift = shift.substring(0,shift.length() - 1);
	}

	public Object getElement() {
		return element;
	}
}
