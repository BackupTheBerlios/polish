package de.enough.polish.predictive;

public class TextElement {
	Object element;
	
	public TextElement(Object object)
	{
		this.element = object;
	}
	
	public int getLength()
	{
		if(element != null)
		{
			if(element instanceof String)
			{
				String string = (String)element;
				return string.length();
			}
			else if(element instanceof TrieReader)
			{
				TrieReader reader = (TrieReader)element;
				return reader.getSelectedWord().length();
			}
		}
		
		return -1;
	}

	public Object getElement() {
		return element;
	}
	
}
