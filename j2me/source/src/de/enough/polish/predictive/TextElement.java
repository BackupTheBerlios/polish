//#condition polish.TextField.useDirectInput && !polish.blackberry
package de.enough.polish.predictive;

import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TextElement {
	Object element = null; 
	
	int[] keyCodes = null; 
	
	ArrayList trieResults = null;
	ArrayList customResults = null;
	
	int selectedWordIndex;		
	
	static final int SHIFT = 10000;
	
	public TextElement(Object object) {
		this.element 		= object;
		
		if(this.element instanceof TrieReader)
		{
			this.trieResults		= new ArrayList();
			this.customResults		= new ArrayList();
			
			this.selectedWordIndex	= 0;
			
			this.keyCodes 			= new int[0];
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
	
	public void keyNum(int keyCode, int shift) 
	{	
		int[] newKeyCodes = new int[this.keyCodes.length + 1];
		System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length);
		newKeyCodes[newKeyCodes.length - 1] = keyCode;
		this.keyCodes = newKeyCodes;
		
		if(shift == TextField.MODE_UPPERCASE || shift == TextField.MODE_FIRST_UPPERCASE)
			this.keyCodes[this.keyCodes.length - 1] += SHIFT;
	}
	
	public void keyClear() 
	{
		int[] newKeyCodes = new int[this.keyCodes.length - 1];
		System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length - 1);
		this.keyCodes = newKeyCodes;
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
				if (keyCodes[j] > SHIFT)
					string.setCharAt(j, Character.toUpperCase(string.charAt(j)));
			}
		}

	}

	public void setResults(TrieCustom custom) {
		
		if (element instanceof TrieReader) 
		{
				this.customResults.clear();
				
				custom.getWords(this.customResults,this.keyCodes);
				
				shiftResults(this.customResults);
				
				this.trieResults.clear();
				
				if(((TrieReader)element).isWordFound() || 
					!this.isCustomFound() ||
					this.getKeyCount() == ((TrieReader)element).getKeyCount())
				{
					ArrayList nodes = ((TrieReader) element).getNodes();
					
					for(int i=0; i<nodes.size();i++)
					{
						TrieNode node = (TrieNode)nodes.get(i);
						this.trieResults.add(node.getWord());
					}
						
					shiftResults(this.trieResults);
				}
		}
	}
	
	private StringBuffer getSelectedStringBuffer()
	{
		if(this.isSelectedCustom())
			return (StringBuffer)this.customResults.get(this.selectedWordIndex - this.trieResults.size());
		else
			return (StringBuffer)this.trieResults.get(this.selectedWordIndex);
	}
	
	public void setSelectedWordIndex(int selected)
	{
		this.selectedWordIndex = selected;
	}
	
	public int getSelectedWordIndex()
	{
		return this.selectedWordIndex;
	}

	public String getSelectedWord()
	{
		String selectedWord = getSelectedStringBuffer().toString();
		
		return selectedWord.substring(0, keyCodes.length);
	}
	
	public boolean isSelectedCustom()
	{
		if(this.trieResults.size() > 0)
			return (this.selectedWordIndex >= this.trieResults.size());
		else
			return (this.customResults.size() > 0);
	}
	
	public void convertReader()
	{
		this.element = getSelectedStringBuffer();
	}
	
	public ArrayList getTrieResults()
	{
		return this.trieResults;
	}
	
	public ArrayList getCustomResults()
	{
		return this.customResults;
	}
	
	public boolean isCustomFound()
	{
		return this.customResults.size() > 0;
	}
	
	public boolean isWordFound()
	{
		if(!isStringBuffer())
		{
			return ((TrieReader)this.element).isWordFound() || isCustomFound();
		}
		
		return false;
	}

	public Object getElement() {
		return element;
	}
	
	public int getKeyCount()
	{
		return this.keyCodes.length;
	}
}
