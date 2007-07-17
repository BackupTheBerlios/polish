//#condition polish.TextField.useDirectInput && !polish.blackberry
package de.enough.polish.predictive;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import de.enough.polish.io.Serializer;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Properties;

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
		
	private ArrayList textElements = null;
	
	private int element;
	private int align;
	private int mode;
	private int caret;
	
	private TrieCustom custom = null;
	
	private HashMap    stores 	= null;
	private HashMap    records 	= null;
	
	private StringBuffer text = null;
	
	/**
	 * Initializes the <code>TextElement</code> array, set the current element to -1, the align
	 * for the current element to <code>ALIGN_LEFT</code> and the current input mode to 
	 * <code>MODE_FIRST_UPPERCASE</code>.
	 */
	public TextBuilder(int textSize, HashMap stores, HashMap records)
	{
		this.textElements 	= new ArrayList();
		this.element 		= -1;
		this.align			= ALIGN_LEFT;
		this.mode 			= TextField.MODE_FIRST_UPPERCASE;
		this.caret			= 0;
		
		this.custom			= new TrieCustom();
		
		this.stores			= stores;
		this.records		= records;
		
		this.text 			= new StringBuffer(textSize);
	}
	
	public void keyNum(int keyCode) throws RecordStoreException
	{
		if(	isStringBuffer(0) || this.align == ALIGN_LEFT || this.align == ALIGN_RIGHT )
			addReader(new TrieReader(this.stores,this.records));
		
		getReader().keyNum(keyCode);
		getElement().keyNum(keyCode, mode);
		getElement().setResults(custom);
		
		if(!getElement().isWordFound())
			getElement().keyClear();
		else
		{
			if(this.mode == TextField.MODE_FIRST_UPPERCASE)
				this.mode = TextField.MODE_LOWERCASE;
		}
	}
	
	public void keySpace()
	{
		if(!isStringBuffer(0))
			if(getElement().isSelectedCustom())
				getElement().convertReader();
		
		addStringBuffer(" ");
	}
	
	public boolean keyClear() throws RecordStoreException
	{
		if(align == ALIGN_LEFT)
			if(element != 0)
				decreaseCaret();
			else
				return false;
		
		if(isStringBuffer(0))
		{
			if(!decreaseStringBuffer())
			{
				deleteCurrent();
				return false;
			}
			else
				return true;
		}
		else
		{
			if(getElement().getKeyCount() == getReader().getKeyCount())
				getReader().keyClear();
			
			getElement().keyClear();
			getElement().setResults(this.custom);
			
			if(getReader().isEmpty())
			{
				deleteCurrent();
				return false;
			}
			else
			{
				setAlign(ALIGN_FOCUS);
				getElement().setSelectedWordIndex(0);
				return (getElement().getTrieResults().size() > 0) || (getElement().getCustomResults().size() > 0);
			}
		}
	}
	
	public void addWord(String string)
	{
		this.custom.addWord(string);
	}
	
	/**
	 * Returns the <code>TrieReader</code> carried in the current <code>TextElement</code>. It must be checked previously 
	 * via <code>isChar()</code> if the current element is a <code>TrieReader</code>
	 * @return the instance of <code>TrieReader</code> carried in the current <code>TextElement</code> 
	 */
	public TrieReader getReader()
	{
		return (TrieReader)getTextElement(element).getElement();
	}
	
	public StringBuffer getStringBuffer()
	{
		return (StringBuffer)getTextElement(element).getElement();
	}
	
	/**
	 * Returns the current <code>TextElement</code>
	 * @return the current <code>TextElement</code>
	 */
	public TextElement getElement()
	{
		return getTextElement(element);
	}
	
	/**
	 * Returns the align of the current <code>TextElement</code>. 
	 * @return the align of the current <code>TextElement</code>
	 */
	public int getAlign()
	{
		return this.align;
	}
	
	/**
	 * Sets the align of the current <code>TextElement</code>
	 * @param currentAlign the align of the current <code>TextElement</code>
	 */
	public void setAlign(int currentAlign)
	{
		this.align = currentAlign;
	}
	
	/**
	 * Returns the current caret position
	 * @return the current caret position
	 */
	public int getCaret()
	{
		return this.caret;
	}
	
	/**
	 * Returns the TextElement at the given index
	 * @return the TextElement at the given index
	 */
	private TextElement getTextElement(int index)
	{
		if(this.textElements.size() > 0)
			return (TextElement) this.textElements.get(index);
		else
			return null;
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
	private void addElement(TextElement textElement)
	{
		if(element >= 0)
		{
			if(align == ALIGN_LEFT)
				this.textElements.add(element, textElement);
			
			if(align == ALIGN_FOCUS)
				this.textElements.add(element + 1, textElement);
			
			if(align == ALIGN_RIGHT)
				this.textElements.add(element + 1, textElement);
			
			if( (align == ALIGN_FOCUS || align == ALIGN_RIGHT) && element < textElements.size())
				element++;
		}
		else
		{
			this.textElements.add(0, textElement);
			element = 0;
		}	
	}
	
	/**
	 * Retrieves the line the caret is positioned.
	 * @param textLines the text lines of the field
	 * @return the index of the line
	 */
	public int getElementLine(String[] textLines)
	{
		int caretPosition = this.getCaret(); 
		int length = 0;
		int index = 0;
		
		for (index = 0; index < textLines.length; index++) {
			length += textLines[index].length();
			
			if(length >= caretPosition - 1)
				return index;
		}
		
		return textLines.length - 1;
	}
	
	/**
	 * Retrieves the caret position if a the caret should be positioned
	 * in the previousor the next line 
	 * @param jumpDirection the direction of the caret jump
	 * @return the caret position
	 */
	public int getJumpPosition(int jumpDirection, String[] textLines)
	{
		int caretLine 		= getElementLine(textLines); 
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
				element = i;
				
				left = lengthOffset - length;
				right = lengthOffset;
					
				if((position - left) > (right - position))
					align = ALIGN_RIGHT;
				else
					align = ALIGN_LEFT;
					
				return;
			}
		}
		
		element = textElements.size() - 1;
		align = ALIGN_RIGHT;
		
		return;
	}
	
	/**
	 * Creates a new <code>TextElement</code> carrying <code>character</code>
	 * @param character the character the <code>TextElement</code> should carry
	 */
	public void addStringBuffer(String string)
	{
		addElement(new TextElement(new StringBuffer(string)));
		this.align = ALIGN_RIGHT;
	}
	
	/**
	 * Creates a new <code>TextElement</code> carrying <code>reader</code>
	 * @param reader the instance of <code>TrieReader</code> the <code>TextElement</code> should carry
	 */
	public void addReader(TrieReader reader)
	{
		addElement(new TextElement(reader));
		this.align = ALIGN_FOCUS;
	}
	
	/**
	 * Deletes the current element
	 * @return true, if an element was deleted, otherwise false
	 */
	public boolean deleteCurrent()
	{
		if(this.textElements.size() > 0)
		{
			int index = element;
			
			if(element == 0)
				this.align = ALIGN_LEFT;
			else
			{
				this.element--;
				this.align = ALIGN_RIGHT;
			}
				
			this.textElements.remove(index);
			
			return true;
		}
		
		return false;
	}
	
	public boolean decreaseStringBuffer()
	{
		if(this.isStringBuffer(0))
		{
			StringBuffer element = getStringBuffer();
			if(element.length() > 0)
			{
				element.setLength(element.length() - 1);
				setAlign(ALIGN_RIGHT);
				return (element.length() > 0);
			}
			else
				return false;
		}
		else
			return true;
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
		if(textElements.size() != 0)
		{
			if(align == ALIGN_FOCUS)
			{
				if(getElement().isSelectedCustom())
					getElement().convertReader();
				
				align = ALIGN_RIGHT;
			}
			else
				if(element != textElements.size() - 1)
				{
					align = ALIGN_LEFT;
					element++;
				}
				else
					align = ALIGN_RIGHT;
					
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
		if(textElements.size() != 0)
		{
			switch(align)
			{
				case ALIGN_LEFT 	:
					if(element > 0)
					{
						if(!isStringBuffer(-1))
							align = ALIGN_FOCUS;
						else
							align = ALIGN_LEFT;
						
						element--;
					}
				break;
				
				case ALIGN_FOCUS:
					align = ALIGN_LEFT;
				break;
				
				case ALIGN_RIGHT:
					if(isStringBuffer(0))
						align = ALIGN_LEFT;
					else
						align = ALIGN_FOCUS;
				break;
			};
		}
	}
	
	/**
	 * Returns true, if the element at <code>offset</code> from the current element 
	 * index is a character or a <code>TrieReader</code>
	 * @param offset the offset for the element
	 * @return true, if the element is a character, otherwise false
	 */
	public boolean isStringBuffer(int offset)
	{
		if(this.textElements.size() > 0 && (element - offset) >= 0)
			return getTextElement(element + offset).isStringBuffer();
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
		
		for (int i = 0; i < element; i++) 
			result += getTextElement(i).getLength();
		
		if(this.textElements.size() > 0)
			if( (align == ALIGN_FOCUS || align == ALIGN_RIGHT) && element >= 0)
				result += getTextElement(element).getLength();
		
		this.caret = result;
		return result;
	}
	
	/**
	 * Constructs a string to display in the field by
	 * concating the string of the elements in <code>textElements</code>
	 * @return the constructed string
	 */
	public StringBuffer getText()
	{
		this.text.setLength(0);
		
		for (int i = 0; i < textElements.size(); i++) {
			TextElement element = getTextElement(i);
			Object object = element.getElement();
			
			if( object instanceof StringBuffer)
				text.append((StringBuffer)object);
			else if( object instanceof TrieReader)
				text.append(element.getSelectedWord());
		}
		
		return this.text;
	}
	
	/**
	 * Returns the character at the given index
	 * @param index the index of the character to return
	 * @return the character at the given index
	 */
	public char getTextChar(int index)
	{
		return this.text.charAt(index);
	}

	/**
	 * Returns the current input mode
	 * @return the current input mode
	 */
	public int getMode() {
		return this.mode;
	}

	/**
	 * Sets the current input mode
	 * @param currentInputMode the input mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
}
