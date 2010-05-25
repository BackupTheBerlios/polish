package de.enough.ovidiu;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.renderer.node.CssElement;

public class LayoutContext 
{	
	
	int currentYPosition = 0;
	
	int currentXPosition = 0;
	
	int startXPosition = 0;
	
	int maximumXUsed = 0;
	
	int maximumYUsed = 0;
	
	Box box = null;
	
	FloatBoxManager floats = null;
	
	ArrayList currentRow = new ArrayList();
	
	int childPos = 0;
	int childSize= 0;
	ArrayList children = null ;
	
	public int currentRowMaxWidth = 0;
	
	int currentRowLeftOffset = 0; 
	
	int maxY = 0;
	int maxX = 0;
	
	/**
	 * Set the current Y position
	 * @param y
	 */
	public void setCurrent(int y)
	{
		currentYPosition = y;
		currentXPosition = floats.leftIntersectionPoint(box.getAbsoluteX(), box.absoluteY + y, currentRowMaxWidth);
	}
	
	public int getCurrentY()
	{
		return currentYPosition;
	}
	
	public int getCurrentX() 
	{
		return currentXPosition ;
	}
	
	public int getCurrentRowWidth()
	{
		int i = 0;
		int size = currentRow.size();
		int rowWidth = 0;
		while ( i < size )
		{
			Box temp = (Box) currentRow.get(i);
			rowWidth += temp.getTotalWidth();
			i++;
		}
		return rowWidth;
	}
	
	public int getCurrentRowHeight()
	{
		if ( rowHeightNoFloats != 0 )
		{
			return rowHeightNoFloats;
		}
		else if ( rowHeightFloats != Integer.MAX_VALUE )
		{
			return rowHeightFloats;
		}
		else
		{
			return 0;
		}
	}
	
	public boolean fitsOnCurrentRow(Box box)
	{
		if (getCurrentRowWidth() + box.getTotalWidth() <= currentRowMaxWidth)
		{			
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	/**
	 * Set position and other parameters as if the box were to be placed next on the current row
	 */
	public void prepareToPlaceOnCurrentRow(Box box)
	{
		box.y = getCurrentY()  ;
		box.absoluteY = getCurrentY() + box.parent.absoluteY;
		box.x = getCurrentX() ;
		box.absoluteX = getCurrentX() + box.parent.absoluteX ;
	}
	
	public void arrangeCurrentRow()
	{
		
		// Handle floats
		// -------------
		
		ArrayList leftFloats = new ArrayList();
		ArrayList rightFloats = new ArrayList();
		ArrayList regularItems = new ArrayList();
		
		int i =0;
		int size = currentRow.size() ;
		
		Box temp;
		String floatValue = null;
		while ( i < size )
		{
			temp = (Box) currentRow.get(i);
			floatValue = (String) StyleManager.getProperty(temp, "float");
			if ( "none".equals(floatValue) )
			{
				regularItems.add(temp);
			}
			else if ( "left".equals(floatValue))
			{
				leftFloats.add(temp);
			}
			else if ( "right".equals(floatValue))
			{
				rightFloats.add(temp);
			}
			
			i++;
		}
		
		currentRow.clear();
		currentRow.addAll(leftFloats);
		currentRow.addAll(regularItems);
		currentRow.addAll(rightFloats);
		
		// Concatenate string items
		Box first, second;
		if ( currentRow.size() > 1 )
		{
			i = 1;
			while ( i < currentRow.size() )
			{
				first = (Box) currentRow.get(i-1);
				second = (Box) currentRow.get(i);
				if (  ( first.correspondingNode.getContentType() == CssElement.CONTENT_TEXT )  &&
					 ( second.correspondingNode.getContentType() == CssElement.CONTENT_TEXT ) ) 
				{
					String text = first.correspondingNode.getValue() + second.correspondingNode.getValue() ;
					//System.out.println("MY VALUE: " + text);
					first.correspondingNode.setValue( text );
					second.parent.removeChild(second);
					currentRow.remove(second);
					StyleManager.mapStyleToBox(first) ; 
					continue;
				}
				i++;
			}	
		}
		
		// Update X and Y positions for items on the current row
		i = 0;
		size = currentRow.size();
		int xPos = startXPosition;
		while ( i < size )
		{
			temp = (Box) currentRow.get(i);
			
			temp.y = currentYPosition;
			temp.absoluteY = temp.parent.absoluteY + temp.y ;
			
			temp.x = xPos ;
			temp.absoluteX = temp.parent.absoluteX + temp.x ;
			
			
			if ( ! "none".equals( StyleManager.getProperty(temp, "float") ) ) 
			{
				int type = FloatBoxManager.FLOAT_LEFT ;
				if ( "right".equals( StyleManager.getProperty(temp, "float") ) )
				{
					type = FloatBoxManager.FLOAT_RIGHT;
				}
				floats.addFloat(temp.getAbsoluteX(), temp.absoluteY, temp.contentWidth, temp.contentHeight, type);
			}
			else // This is not a float
			{
				// TODO: Proper vertical align
				temp.absoluteY += ( getCurrentRowHeight() - temp.getTotalHeight() );	
			}
			
			xPos += temp.getTotalWidth();
			
			
			i++;
		}
	}
	
	public int rowHeightNoFloats = 0;
	public int rowHeightFloats = Integer.MAX_VALUE;	
	
	public void placeOnCurrentRow(Box box)
	{
		currentRow.add(box);
		
		// See if this box has the biggest Y so far
		if ( currentYPosition + box.getTotalHeight() > maxY )
		{
			maxY = currentYPosition + box.getTotalHeight();
		}
		
		// See if this box has the biggest X so far
		if ( currentXPosition + box.getTotalWidth() > maxX )
		{
			maxX = currentXPosition + box.getTotalWidth();
		}
		
		currentXPosition += box.getTotalWidth();
		
		if ( "none".equals ( StyleManager.getProperty(box, "float") ) )
		{
			if ( box.getTotalHeight() > rowHeightNoFloats )
			{
				rowHeightNoFloats = box.getTotalHeight() ;
			}
		}
		else
		{
			if ( box.getTotalHeight() < rowHeightFloats )
			{
				rowHeightFloats = box.getTotalHeight() ;
			}
			
		}
	}
	
	public void nextRow()
	{
		// Arrange the current row
		arrangeCurrentRow();
		
		// Step 4 : Profit
		
		// Increment the Y position
		setCurrent(currentYPosition + getCurrentRowHeight() );
				
		// Clear current row buffer
		currentRow.clear();
		
		// Update left offset for this row
		int x = box.getAbsoluteX();
		int y = box.absoluteY + getCurrentY();
		int theoreticalMaxWidth = box.contentWidth;		
		currentRowMaxWidth = getMaxRowWidth(currentYPosition);
		startXPosition = floats.leftIntersectionPoint(x, y, theoreticalMaxWidth) ;
		currentXPosition = startXPosition ;
					
		rowHeightFloats = Integer.MAX_VALUE;
		rowHeightNoFloats = 0;
		
	}
	
	/**
	 * Maximum possible row width for the current box at specified Y offset, with respect to floats
	 * @param yPosition
	 * @return
	 */
	public int getMaxRowWidth(int yPosition)
	{
		int x = box.getAbsoluteX();
		int y = box.absoluteY + yPosition;
		int theoreticalMaxWidth = box.parent.contentWidth ;
		
		
		int leftPoint = floats.leftIntersectionPoint(x, y, theoreticalMaxWidth);
		int rightPoint = floats.rightIntersectionPoint(x, y, theoreticalMaxWidth);			
		
		return (rightPoint - leftPoint);
		
	}
	
	/**
	 * Maximum possible box size, with respect to the parent and floats that apply to this context
	 * @param yPosition
	 * @return
	 */
	public int getMaxBoxWidth(Box box)
	{
		int x = box.getAbsoluteX();
		int y = box.parent.absoluteY + box.y;
		int theoreticalMaxWidth = box.parent.contentWidth;
		
		int leftCut = floats.leftIntersectionPoint(x, y, theoreticalMaxWidth);
		int rightCut = floats.rightIntersectionPoint(x, y, theoreticalMaxWidth);	
		
		
		return (rightCut-leftCut);
		
	}
	
	public void clearCurrentRow()
	{
		currentRow.clear();
	}
	
	
	public int getMaxY()
	{
		return maxY;
	}
	
	
	public int getMaxX()
	{
		return maxX;
	}
	
	public LayoutContext(Box box, FloatBoxManager floats)
	{
		this.box = box;
		this.floats = floats;
		nextRow();
		children = box.correspondingNode.getChildren() ;
		childPos = 0;
		childSize = children.size() ;
	}
	
	
	public boolean hasMoreChildren()
	{
		if ( childPos < childSize )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Box nextChild()
	{
		CssElement temp = (CssElement) children.get(childPos);			
		Box child = new Box(temp);
		childPos++;
		return child;
	}

}
