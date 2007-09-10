//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput 
package de.enough.polish.predictive.trie;

import de.enough.polish.predictive.TextElement;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TrieTextElement extends TextElement {
	ArrayList trieResults = null;
	ArrayList customResults = null;
	
	public TrieTextElement(Object object) {
		super(object);
		
		if(this.element instanceof TrieReader)
		{
			this.trieResults		= new ArrayList();
			this.customResults		= new ArrayList();
		}
	}
	
	public void setResults() {
		
		if (element instanceof TrieReader) 
		{
				this.customResults.clear();
				
				TextField.PROVIDER.getCustom().getWords(this.customResults,this.keyCodes);
				
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
					
					TextField.PROVIDER.getOrder().getOrder(this.trieResults, this.keyCodes);
						
					shiftResults(this.trieResults);
				}
		}
	}
	
	protected StringBuffer getSelectedStringBuffer()
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
		
		if(selected > 0)
			TextField.PROVIDER.getOrder().addOrder(this.keyCodes, (byte)selected);
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
		TextField.PROVIDER.releaseRecords();
		this.element = getSelectedStringBuffer();
	}
	
	public ArrayList getResults()
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
