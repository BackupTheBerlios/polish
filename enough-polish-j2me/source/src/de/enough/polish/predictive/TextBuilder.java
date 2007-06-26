package de.enough.polish.predictive;

import de.enough.polish.util.ArrayList;

public class TextBuilder {
	
	public static final int ALIGN_LEFT 	= 0;
	public static final int ALIGN_FOCUS 	= 1;
	
	ArrayList textElements = null;
	int currentElement;
	int currentAlign;
	
	public TextBuilder()
	{
		this.textElements 	= new ArrayList();
		this.currentElement = -1;
		this.currentAlign	= this.ALIGN_LEFT;
	}
	
	public TrieReader getCurrentReader()
	{
		return (TrieReader)getTextElement(currentElement).getElement();
	}
	
	public String getCurrentString()
	{
		return (String)getTextElement(currentElement).getElement();
	}
	
	public int getCurrentAlign()
	{
		return this.currentAlign;
	}
	
	private TextElement getTextElement(int index)
	{
		return (TextElement) this.textElements.get(index);
	}
	
	private void addObject(TextElement element)
	{
		if(currentElement >= 0)
		{
			if(currentAlign == ALIGN_LEFT)
				this.textElements.add(currentElement, element);
			
			if(currentAlign == ALIGN_FOCUS)
				this.textElements.add(currentElement + 1, element);
			
			if(currentAlign == ALIGN_FOCUS && currentElement < textElements.size())
				currentElement++;
		}
		else
		{
			this.textElements.add(0, element);
			currentElement = 0;
		}
			
			
	}
		
	public void addString(String string)
	{
		addObject(new TextElement(string));
	}
	
	public void addReader(TrieReader reader)
	{
		addObject(new TextElement(reader));
		this.currentAlign = ALIGN_FOCUS;
	}
	
	public void deleteCurrent()
	{
		int index = currentElement;
		
		if(currentElement == this.textElements.size() - 1)
			currentElement--;
		
		if(textElements.size() > 0)
			this.textElements.remove(index);
	}
	
	public void increaseCaret()
	{	
		switch(currentAlign)
		{
			case ALIGN_LEFT 	:
				if(!isString())
					currentAlign = ALIGN_FOCUS;
				else if(currentElement < textElements.size() - 1)
				{
					currentAlign = ALIGN_LEFT;
					currentElement++;
				}
			break;
			
			case ALIGN_FOCUS	:
				if(currentElement < textElements.size() - 1)
				{
					currentAlign = ALIGN_LEFT;
					currentElement++;
				}
			break;
		};
	}

	public void decreaseCaret()
	{		
		switch(currentAlign)
		{
			case ALIGN_LEFT 	:
				if(currentElement > 0)
				{
					if(!isPrevString())
						currentAlign = ALIGN_FOCUS;
					else
						currentAlign = ALIGN_LEFT;
					
					currentElement--;
				}
			break;
			
			case ALIGN_FOCUS:
				currentAlign = ALIGN_LEFT;
			break;
		};
	}
	
	public boolean isNextString()
	{
		if(currentElement < this.textElements.size() - 1)
			return (getTextElement(currentElement + 1).getElement() instanceof String);
		else
			return false;
	}
	
	public boolean isPrevString()
	{
		if(currentElement > 0)
			return (getTextElement(currentElement - 1).getElement() instanceof String);
		else
			return false;
	}
	
	public boolean isString()
	{
		if(this.textElements.size() > 0)
			return (getTextElement(currentElement).getElement() instanceof String);
		else
			return true;
	}
	
	public int getCurrentOffset()
	{
		int result = 0;
		int length = 0;
		
		for (int i = 0; i < currentElement; i++) {
			length = getTextElement(i).getLength();
		
			result += length;
		}
		
		return result;
	}
	
	public int getCurrentLength()
	{
		return this.getTextElement(this.currentElement).getLength();
	}
		
	public int getCaretPosition()
	{
		int result = 0;
		int length = 0;
		
		for (int i = 0; i < currentElement; i++) {
			length = getTextElement(i).getLength();
			result += length;
		}
		
		if(currentAlign == ALIGN_FOCUS && currentElement >= 0)
		{
			length = getTextElement(currentElement).getLength();
			result += length;
		}
		
		return result;
	}
	
	public String getText()
	{
		String result = "";
		
		for (int i = 0; i < textElements.size(); i++) {
			Object object = getTextElement(i).getElement();
			
			if( object instanceof String)
			{
				result += (String)object;
			}
			else if( object instanceof TrieReader)
			{
				result += ((TrieReader)object).getSelectedWord();
			}
		}
		
		return result;
	}
}
