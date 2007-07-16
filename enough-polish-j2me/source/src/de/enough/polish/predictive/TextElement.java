//#condition polish.TextField.useDirectInput && !polish.blackberry
package de.enough.polish.predictive;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TextElement {
	Object element = null; 
	
	int[] keyCodes = null; 
	
	ArrayList results = null;
	
	int selectedWord;		
	int customStart;
	
	static final int SHIFT = 10000;
	
	public TextElement(Object object) {
		this.element 		= object;
		
		if(this.element instanceof TrieReader)
		{
			this.results		= new ArrayList();
			this.selectedWord	= 0;
			
			this.keyCodes 		= new int[0];
			this.customStart 	= -1;
		}
	}

	public int getLength() {
		if (element != null) {
			if (element instanceof StringBuffer) {
				return ((StringBuffer)element).length();
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
		
		if(shift == TextField.MODE_UPPERCASE || shift == TextField.MODE_FIRST_UPPERCASE)
			this.keyCodes[this.keyCodes.length - 1] += SHIFT;
	}
	
	public void popKeyCode()
	{
		int[] newKeyCodes = new int[this.keyCodes.length - 1];
		System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length - 1);
		this.keyCodes = newKeyCodes;
	}

	public boolean setResults(TrieCustom custom) {
		
		StringBuffer string;
		
		if (element instanceof TrieReader) {
			if(((TrieReader) element).isWordFound() || custom.hasWords(keyCodes))
			{
				this.results.clear();
				
				if(((TrieReader) element).isWordFound())
				{
					ArrayList nodes = ((TrieReader) element).getNodes();
					
					for (int i = 0; i < nodes.size(); i++)
					{
						TrieNode node = (TrieNode)nodes.get(i);
						this.results.add(node.getWord());
					}
				}
				
				this.customStart = this.results.size();
				
				custom.getWords(this.results,this.keyCodes);
				
				for (int i = 0; i < this.results.size(); i++) {
					string = (StringBuffer)this.results.get(i);
					
					for (int j = 0; j < keyCodes.length; j++)
					{
						if (keyCodes[j] > SHIFT)
							string.setCharAt(j, Character.toUpperCase(string.charAt(j)));
					}
				}

				return true;
			}
			else
				return false;
		}
		
		return false;
	}
	
	public StringBuffer getSelectedWord()
	{
		StringBuffer result = null;
		
		result = (StringBuffer)this.results.get(selectedWord);
		
		return result;
	}
	
	public ArrayList getResults()
	{
		return this.results;
	}
	
	public void setSelectedWord(int selectedWord)
	{
		this.selectedWord = selectedWord;
		
		//convert reader to stringbuffer
		if(this.selectedWord >= this.customStart)
			this.element = this.getSelectedWord();
	}

	public Object getElement() {
		return element;
	}
}
