package de.enough.polish.predictive;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TextBuilder {
	
	public static final int ALIGN_LEFT 	= 0;
	public static final int ALIGN_FOCUS = 1;
	public static final int ALIGN_RIGHT = 2;
	
	public static final int JUMP_PREV = 0;
	public static final int JUMP_NEXT = 1;
		
	ArrayList textElements = null;
	int currentElement;
	int currentAlign;
	int currentShift;
	
	public TextBuilder()
	{
		this.textElements 	= new ArrayList();
		this.currentElement = -1;
		this.currentAlign	= ALIGN_LEFT;
		this.currentShift 	= TextField.MODE_FIRST_UPPERCASE; 
	}
	
	public TrieReader getCurrentReader()
	{
		return (TrieReader)getTextElement(currentElement).getElement();
	}
	
	public String getCurrentString()
	{
		return (String)getTextElement(currentElement).getElement();
	}
	
	public TextElement getCurrentElement()
	{
		return getTextElement(currentElement);
	}
	
	public int getCurrentIndex()
	{
		return this.currentElement;
	}
	
	public int getCurrentAlign()
	{
		return this.currentAlign;
	}
	
	public void setCurrentAlign(int currentAlign)
	{
		this.currentAlign = currentAlign;
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
			
			if(currentAlign == ALIGN_RIGHT)
				this.textElements.add(currentElement + 1, element);
			
			if( (currentAlign == ALIGN_FOCUS || currentAlign == ALIGN_RIGHT) && currentElement < textElements.size())
				currentElement++;
		}
		else
		{
			this.textElements.add(0, element);
			currentElement = 0;
		}	
	}
	
	public int getCaretLine(String[] textLines)
	{
		int caretPosition = getCaretPosition(); 
		int length = 0;
		int index = 0;
		
		for (index = 0; index < textLines.length; index++) {
			length += textLines[index].length();
			
			if(length >= caretPosition - 1)
				return index;
		}
		
		return index;
	}
	
	public int getJumpPosition(int jumpDirection, String[] textLines)
	{
		int caretLine 		= getCaretLine(textLines); 
		int caretPosition 	= getCaretPosition(); 
		
		if(jumpDirection == JUMP_PREV && caretLine > 0)
			return caretPosition - textLines[caretLine-1].length();
		
		if(jumpDirection == JUMP_NEXT && caretLine != (textLines.length - 1))
			return caretPosition + textLines[caretLine].length();
		
		return -1;
	}
	
	public void setCurrentElementNear(int offset)
	{
		int length = 0;
		int lengthOffset = 0;
		
		int left  = 0;
		int right = 0;
		
		for (int i = 0; i < textElements.size(); i++) {
			length = getTextElement(i).getLength();
			lengthOffset += length;
			
			if(lengthOffset > offset)
			{
				currentElement = i;
				
				left = lengthOffset - length;
				right = lengthOffset;
					
				if((offset - left) > (right - offset))
					currentAlign = ALIGN_RIGHT;
				else
					currentAlign = ALIGN_LEFT;
					
				return;
			}
		}
		
		currentElement = textElements.size() - 1;
		currentAlign = ALIGN_RIGHT;
		
		return;
	}
	
	public void addChar(char character)
	{
		addObject(new TextElement(new Character(character)));
		this.currentAlign = ALIGN_RIGHT;
	}
	
	public void addReader(TrieReader reader)
	{
		addObject(new TextElement(reader));
		this.currentAlign = ALIGN_FOCUS;
	}
	
	public boolean deleteCurrent()
	{
		if(this.textElements.size() > 0)
		{
			int index = currentElement;
			
			if(currentElement == 0)
				this.currentAlign = ALIGN_LEFT;
			else
			{
				this.currentElement--;
				this.currentAlign = ALIGN_RIGHT;
			}
				
			this.textElements.remove(index);
			
			return true;
		}
		
		return false;
	}
	
	public void increaseCaret()
	{	
		if(!isChar(0) && currentAlign == ALIGN_LEFT)
			currentAlign = ALIGN_FOCUS;
		else
			if(currentElement < textElements.size() - 1)
			{
				currentAlign = ALIGN_LEFT;
				currentElement++;
			}
	}

	public void decreaseCaret()
	{		
		switch(currentAlign)
		{
			case ALIGN_LEFT 	:
				if(currentElement > 0)
				{
					if(!isChar(-1))
						currentAlign = ALIGN_FOCUS;
					else
						currentAlign = ALIGN_LEFT;
					
					currentElement--;
				}
			break;
			
			case ALIGN_FOCUS:
				currentAlign = ALIGN_LEFT;
			break;
			
			case ALIGN_RIGHT:
				if(isChar(0))
					currentAlign = ALIGN_LEFT;
				else
					currentAlign = ALIGN_FOCUS;
			break;
		};
	}
	
	public boolean isChar(int offset)
	{
		if(this.textElements.size() > 0 && (currentElement - offset) >= 0)
			return (getTextElement(currentElement + offset).getElement() instanceof Character);
		else
			return true;
	}
		
	public int getCaretPosition()
	{
		int result = 0;
		
		for (int i = 0; i < currentElement; i++) 
			result += getTextElement(i).getLength();
		
		if(this.textElements.size() > 0)
			if( (currentAlign == ALIGN_FOCUS || currentAlign == ALIGN_RIGHT) && currentElement >= 0)
				result += getTextElement(currentElement).getLength();
		
		return result;
	}
	
	public String getText()
	{
		String result = "";
		
		for (int i = 0; i < textElements.size(); i++) {
			TextElement element = getTextElement(i);
			Object object = element.getElement();
			
			if( object instanceof Character)
				result += (Character)object;
			else if( object instanceof TrieReader)
				result += element.getSelectedWord();
		}
		
		return result;
	}

	public int getShift() {
		return this.currentShift;
	}

	public void setShift(int currentShift) {
		this.currentShift = currentShift;
	}
}
