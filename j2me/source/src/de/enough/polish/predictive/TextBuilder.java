package de.enough.polish.predictive;

import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;

public class TextBuilder {
	
	/**
	 * Indicates that the caret should be shown to the left of current element 
	 */
	public static final int ALIGN_LEFT 	= 0;
	
	/**
	 * Indicates that the caret should be shown to the right of current element
	 * and that results should be shown.
	 * This value only applies to instances of TextElement carrying a TrieReader
	 */
	public static final int ALIGN_FOCUS = 1;
	
	/**
	 * Indicates that the caret should be shown to the right of current element.
	 */
	public static final int ALIGN_RIGHT = 2;
	
	/**
	 * Indicates that the user pressed the DOWN button and wishes to jump to the 
	 * next line.
	 */
	public static final int JUMP_PREV = 0;
	
	/**
	 * Indicates that the user pressed the UP button and wishes to jump to the 
	 * previous line.
	 */
	public static final int JUMP_NEXT = 1;
		
	ArrayList textElements = null;
	int currentElement;
	int currentAlign;
	int currentInputMode;
	
	StringBuffer text = null;
	
	/**
	 * Initializes the <code>TextElement</code> array, set the current element to -1, the align
	 * for the current element to <code>ALIGN_LEFT</code> and the current input mode to 
	 * <code>MODE_FIRST_UPPERCASE</code>.
	 */
	public TextBuilder(int textSize)
	{
		this.textElements 		= new ArrayList();
		this.currentElement 	= -1;
		this.currentAlign		= ALIGN_LEFT;
		this.currentInputMode 	= TextField.MODE_FIRST_UPPERCASE;
		
		text = new StringBuffer(textSize);
	}
	
	/**
	 * Returns the <code>TrieReader</code> carried in the current <code>TextElement</code>. It must be checked previously 
	 * via <code>isChar()</code> if the current element is a <code>TrieReader</code>
	 * @return the instance of <code>TrieReader</code> carried in the current <code>TextElement</code> 
	 */
	public TrieReader getCurrentReader()
	{
		return (TrieReader)getTextElement(currentElement).getElement();
	}
	
	/**
	 * Returns the current <code>TextElement</code>
	 * @return the current <code>TextElement</code>
	 */
	public TextElement getCurrentElement()
	{
		return getTextElement(currentElement);
	}
	
	/**
	 * Returns the index of the current <code>TextElement</code> 
	 * @return the index of the current <code>TextElement</code>
	 */
	public int getCurrentIndex()
	{
		return this.currentElement;
	}
	
	/**
	 * Returns the align of the current <code>TextElement</code>. 
	 * @return the align of the current <code>TextElement</code>
	 */
	public int getCurrentAlign()
	{
		return this.currentAlign;
	}
	
	/**
	 * Sets the align of the current <code>TextElement</code>
	 * @param currentAlign the align of the current <code>TextElement</code>
	 */
	public void setCurrentAlign(int currentAlign)
	{
		this.currentAlign = currentAlign;
	}
	
	/**
	 * Returns the TextElement at the given index
	 * @return the TextElement at the given index
	 */
	private TextElement getTextElement(int index)
	{
		return (TextElement) this.textElements.get(index);
	}
	
	/**
	 * Adds an instance of <code>TextElement</code> to <code>textElements</code>
	 * in compliance with the current align. 
	 * <p>
	 * If the align is <code>ALIGN_LEFT</code>, the element is inserted at 
	 * the current element position
	 * </p>
	 * <p>
	 * If the align is <code>ALIGN_FOCUS</code>, the element is inserted behind 
	 * the current element position
	 * </p>
	 * <p>
	 * If the align is <code>ALIGN_RIGHT</code>, the element is inserted behind 
	 * the current element position
	 * </p>
	 * The index of the current element is set eventually to the position the new
	 * element is inserted at.
	 * @param the <code>TextElement</code> to add
	 */
	private void addElement(TextElement element)
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
	
	/**
	 * Retrieves the line the caret is positioned.
	 * @param textLines the text lines of the field
	 * @return the index of the line
	 */
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
	
	/**
	 * Retrieves the caret position if a the caret should be positioned
	 * in the previousor the next line 
	 * @param jumpDirection the direction of the caret jump
	 * @return the caret position
	 */
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
	
	/**
	 * Set the element next to caret position <code>offset</code> as
	 * the current element. If the position is closer to the start of
	 * the element the align for the current element is set to 
	 * <code>ALIGN_LEFT</code>, otherwise to <code>ALIGN_RIGHT</code>
	 * @param position the caret position
	 */
	public void setCurrentElementNear(int position)
	{
		int length = 0;
		int lengthOffset = 0;
		
		int left  = 0;
		int right = 0;
		
		for (int i = 0; i < textElements.size(); i++) {
			length = getTextElement(i).getLength();
			lengthOffset += length;
			
			if(lengthOffset > position)
			{
				currentElement = i;
				
				left = lengthOffset - length;
				right = lengthOffset;
					
				if((position - left) > (right - position))
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
	
	/**
	 * Creates a new <code>TextElement</code> carrying <code>character</code>
	 * @param character the character the <code>TextElement</code> should carry
	 */
	public void addChar(char character)
	{
		addElement(new TextElement(new Character(character)));
		this.currentAlign = ALIGN_RIGHT;
	}
	
	/**
	 * Creates a new <code>TextElement</code> carrying <code>reader</code>
	 * @param reader the instance of <code>TrieReader</code> the <code>TextElement</code> should carry
	 */
	public void addReader(TrieReader reader)
	{
		addElement(new TextElement(reader));
		this.currentAlign = ALIGN_FOCUS;
	}
	
	/**
	 * Deletes the current element
	 * @return true, if an element was deleted, otherwise false
	 */
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
	
	/**
	 * Increases the caret position to set in <code>getCaretPosition()</code> by setting the corresponding align and element index
	 * <p>
	 * If the current align is <code>ALIGN_LEFT</code>,
	 * the align is set to <code>ALIGN_FOCUS</code> (for <code>TrieReader</code> elements)
	 * or <code>ALIGN_RIGHT</code> (for characters)
	 * </p>
	 * <p>
	 * If the current align is <code>ALIGN_FOCUS</code> or <code>ALIGN_RIGHT</code>
	 * and the element is not the last element in <code>textElements</code>, the align
	 * is set to <code>ALIGN_LEFT</code> and the current element index is incremented
	 * by 1.
	 * </p> 
	 */
	public void increaseCaret()
	{	
		if(currentAlign == ALIGN_LEFT)
		{
			if(!isChar(0))
				currentAlign = ALIGN_FOCUS;
			else
				currentAlign = ALIGN_RIGHT;
		}
		if(currentAlign == ALIGN_FOCUS)
		{
			currentAlign = ALIGN_RIGHT;
		}
		else
			if(currentElement != textElements.size() - 1)
			{
				currentAlign = ALIGN_LEFT;
				currentElement++;
			}
	}

	/**
	 * Decreases the caret position to set in <code>getCaretPosition()</code> by setting the corresponding align and element index
	 * <p>
	 * If the current align is <code>ALIGN_LEFT</code> and the current element is not 
	 * the first element in <code>textElements</code>, the align is set to <code>ALIGN_FOCUS</code> (for <code>TrieReader</code> elements)
	 * or <code>ALIGN_LEFT</code> (for characters) and the current index gets decremented by 1.
	 * </p>
	 * <p>
	 * If the current align is <code>ALIGN_FOCUS</code>, the align is set to <code>ALIGN_LEFT</code>.
	 * </p>
	 * If the current align is <code>ALIGN_RIGHT</code> or <code>ALIGN_RIGHT</code>
	 * and the element is not the last element in <code>textElements</code>, the align is set to <code>ALIGN_FOCUS</code> (for <code>TrieReader</code> elements)
	 * or <code>ALIGN_LEFT</code> (for characters)
	 * </p> 
	 */
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
	
	/**
	 * Returns true, if the element at <code>offset</code> from the current element 
	 * index is a character or a <code>TrieReader</code>
	 * @param offset the offset for the element
	 * @return true, if the element is a character, otherwise false
	 */
	public boolean isChar(int offset)
	{
		if(this.textElements.size() > 0 && (currentElement - offset) >= 0)
			return (getTextElement(currentElement + offset).getElement() instanceof Character);
		else
			return true;
	}
	
	/**
	 * Retrieves the field caret position by adding
	 * the string length of the elements preceding
	 * the current element. If the current align is
	 * <code>ALIGN_FOCUS</code> or <code>ALIGN_RIGHT</code>,
	 * the caret position is incremented by the string length
	 * of the current element
	 * @return the field caret position
	 */
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
	
	/**
	 * Constructs a string to display in the field by
	 * concating the string of the elements in <code>textElements</code>
	 * @return the constructed string
	 */
	public String getText()
	{
		this.text.setLength(0);
		
		for (int i = 0; i < textElements.size(); i++) {
			TextElement element = getTextElement(i);
			Object object = element.getElement();
			
			if( object instanceof Character)
				text.append((Character)object);
			else if( object instanceof TrieReader)
				text.append(element.getSelectedWord());
		}
		
		return this.text.toString();
	}

	/**
	 * Returns the current input mode
	 * @return the current input mode
	 */
	public int getInputMode() {
		return this.currentInputMode;
	}

	/**
	 * Sets the current input mode
	 * @param currentInputMode the input mode to set
	 */
	public void setInputMode(int currentInputMode) {
		this.currentInputMode = currentInputMode;
	}
}
