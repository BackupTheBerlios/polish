//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput 
package de.enough.polish.predictive;

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
		
		System.out.println("keyNum");
		
		if(shift == TextField.MODE_UPPERCASE || shift == TextField.MODE_FIRST_UPPERCASE)
			this.keyCodes[this.keyCodes.length - 1] += SHIFT;
		
		this.selectedWordIndex = 0;
	}
	
	public void keyClear() 
	{
		int[] newKeyCodes = new int[this.keyCodes.length - 1];
		System.arraycopy(this.keyCodes, 0, newKeyCodes, 0, this.keyCodes.length - 1);
		this.keyCodes = newKeyCodes;
		
		this.selectedWordIndex = 0;
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
		int totalElements = this.customResults.size() + this.trieResults.size();
		if(totalElements <= this.selectedWordIndex)
			this.selectedWordIndex = 0;
		
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
			return ((TrieReader)this.element).isWordFound() || isCustomFound();
		
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
