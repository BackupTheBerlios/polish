//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput 
package de.enough.polish.predictive.array;

import de.enough.polish.predictive.TextElement;
import de.enough.polish.predictive.trie.TrieReader;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class ArrayTextElement extends TextElement {
	ArrayList results = null;
	ArrayList customResults = new ArrayList();
	
	public ArrayTextElement(Object object) {
		super(object);
		
		if(this.element instanceof ArrayReader)
		{
			this.results = new ArrayList();
		}
	}
	
	public void setResults() {
		this.results = ((ArrayReader)this.element).getResults();
	}
	
	protected StringBuffer getSelectedStringBuffer()
	{
		int totalElements = this.customResults.size() + this.results.size();
		if(totalElements <= this.selectedWordIndex)
			this.selectedWordIndex = 0;
		
		if(this.isSelectedCustom())
			return (StringBuffer)this.customResults.get(this.selectedWordIndex - this.results.size());
		else
			return (StringBuffer)this.results.get(this.selectedWordIndex);
	}
	
	public void setSelectedWordIndex(int selected)
	{
		this.selectedWordIndex = selected;
	}
	
	
	public boolean isSelectedCustom()
	{
		if(this.results.size() > 0)
			return (this.selectedWordIndex >= this.results.size());
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
		return this.results;
	}
	
	public ArrayList getCustomResults() {
		return this.customResults;
	}
	
	public boolean isCustomFound()
	{
		return false;
	}
	
	public boolean isWordFound()
	{
		if(!isStringBuffer())
			return ((ArrayReader)this.element).isWordFound() || isCustomFound();
		
		return false;
	}
}
