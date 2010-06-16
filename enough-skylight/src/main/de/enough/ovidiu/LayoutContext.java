package de.enough.ovidiu;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.renderer.debug.BuildDebug;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.TextCssElement;

public class LayoutContext
{

        class StackFrame
        {
            Box workingBox;
            int childPos;
        }

        ArrayList frameStack = new ArrayList();

	// in the content
	int currentYPosition = 0;

        // in the content
	int currentXPosition = 0;

        // in the content
	int startXPosition = 0;

	int maximumXUsed = 0;

	int maximumYUsed = 0;

        int maxPossibleContentWidth = 0;

	Box box = null;

	FloatBoxManager floats = null;

	ArrayList currentRow = new ArrayList();

        ArrayList savedForLater = new ArrayList();

	int childPos = 0;
	ArrayList children = null ;

	public int currentRowMaxWidth = 0;

	int currentRowLeftOffset = 0;

	int maxY = 0;
	int maxX = 0;

	/**
	 * Set the current Y position
	 * @param y
	 */
	public void setCurrentYPosition(int y)
	{
		currentYPosition = y;
		currentXPosition = floats.leftIntersectionPoint(box.getAbsoluteX() + box.marginLeft + box.paddingLeft, box.getAbsoluteY() + y + box.marginTop + box.paddingTop, currentRowMaxWidth, box);
	}

        public void setMaxPossibleContentWidth(int width)
        {
            if ( width == 0 )
            {

            }
            maxPossibleContentWidth = width ;
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

        private void setTopFrameAsWorkingBoxFrame()
        {
            int pos = frameStack.size() - 1;
            StackFrame frame = (StackFrame) frameStack.get(pos);
            box.frame = null ;
            box = frame.workingBox ;
            childPos = frame.childPos;
            children = box.correspondingNode.getChildren();
            frameStack.remove(pos);
        }

        private void saveCurrentFrameToStack()
        {
            StackFrame frame = new StackFrame();
            frame.workingBox = box;
            frame.childPos = childPos;
            frame.workingBox.frame = frame;
            frameStack.add(frame);
        }

        public void goInto(Box b)
        {
            saveCurrentFrameToStack();
            childPos = 0;
            box = b;
            children = box.correspondingNode.getChildren() ;

        }


        public void removeFloatsForBox(Box b)
        {
            floats.removeFloatsForBox(b);
        }

        public boolean doesNotFitWithin(Box source)
        {
            if ( (source.getTotalWidth() > currentRowMaxWidth) )
            {

                System.out.println("OOPS: " + source.getTotalWidth() + " " + currentRowMaxWidth);
                System.out.println("PARENT: ");
                LayoutModeler.dumpBoxTree(box, 0);
                System.out.println("CHILD: ");
                LayoutModeler.dumpBoxTree(source, 0);

            }
            if ( box.isInline )
            {
                return ( source.getTotalWidth() > currentRowMaxWidth );
            }
            else
            {
                return (source.getTotalWidth() > currentRowMaxWidth);
            } 
        }

        public void forcePlacementOnSeparateRow(Box box)
        {
            do
            {
            nextRow(1);
            removeFloatsForBox(box);
            prepareToPlaceOnCurrentRow(box);
            }
            while ( floats.lineOverlapsAnyFloat(box.getAbsoluteX(), box.getAbsoluteY(), box.getTotalWidth()) ) ;

            placeOnCurrentRow(box);
            nextRow();
        }

	public boolean fitsOnCurrentRow(Box box)
	{
                System.out.println("> " + currentRowMaxWidth + " " + getCurrentRowWidth() + " " + box.getTotalWidth() );
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
		box.x = getCurrentX() ;
	}

        public boolean nextChildFromSavedElements = true ;

        public void saveForLater(Box b)
        {
            nextChildFromSavedElements = false;
            removeFloatsForBox(b);
            if ( savedForLater.indexOf(b) < 0 )
            {
                System.out.println("SAVED FLOAT FOR LATER!");
                savedForLater.add(b);
            }
        }


        public Box lastChild = null ;
        public boolean repeatLastChild = false;

        public void repeatLastChild()
        {
            repeatLastChild = true;
            childPos--;
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

                // <editor-fold>
                Box parent = null;		if ( currentRow.size() > 1 )
		{
                        i=0;
			while ( i < currentRow.size()-1 )
			{
                                System.out.println(i);
				first = (Box) currentRow.get(i);
				second = (Box) currentRow.get(i+1);

                                if ( first == second )
                                {
                                    System.out.println("HOPA!");
                                }

				if (  ( first.correspondingNode.getContentType() == CssElement.CONTENT_TEXT )  &&
					 ( second.correspondingNode.getContentType() == CssElement.CONTENT_TEXT ) )
                                {

                                    TextCssElement elem1 = (TextCssElement) first.correspondingNode ;
                                    TextCssElement elem2 = (TextCssElement) second.correspondingNode ;

                                    // They have the same DOM parent (are pieces of an inline context)
                                    if ( ( (first.DOMParent!=null) && ( first.DOMParent == second.DOMParent) ) ||
                                            ( first.correspondingNode.getParent() == second.correspondingNode.getParent()) )
                                    {                                    
                                        String text = elem1.getValue() + elem2.getValue() ;
                                        elem1.setValue( text );
                                        System.out.println("MY VAL:" + first.correspondingNode.getValue());
                                        StyleManager.mapStyleToBox(first);


                                        // Update frames
                                        int cur = 0;
                                        StackFrame cFrame ;
                                        int secondPos = 0;
                                        System.out.println("UPDATE FRAMES");
                                        while ( cur < frameStack.size() )
                                        {
                                            cFrame = (StackFrame) frameStack.get(cur);
                                            secondPos = cFrame.workingBox.correspondingNode.getChildren().indexOf(elem2);
                                            if ( secondPos >= 0 && secondPos < cFrame.childPos )
                                            {
                                               System.out.println("PROCESS A FRAME!");
                                               cFrame.childPos--;
                                            }
                                            cur++;
                                        }

                                        // Update current context
                                        secondPos = children.indexOf(elem2);
                                        System.out.println("CURRENT FRAME: SECX POS: " + secondPos + " " + childPos );

                                        if ( secondPos < childPos && secondPos > 0 )
                                        {
                                           System.out.println("PROCESS CURRENT FRAME!");
                                           childPos--;
                                        }

                                        
                                        // Remove the second element from .. everywhere
                                        currentRow.remove(second);
                                        if (second.parent != null )
                                        {
                                            second.parent.removeChild(second);
                                        }
                                        if ( first.parent != null)
                                        {
                                            first.parent.removeChild(second);
                                        }
                                        if ( second.DOMParent != null )
                                        {
                                            second.DOMParent.removeChild(second);
                                            second.DOMParent.correspondingNode.remove(elem2);
                                        }
                                        if ( first.DOMParent != null )
                                        {
                                            first.DOMParent.removeChild(second);
                                            first.DOMParent.correspondingNode.remove(elem2);
                                        }
                                        
                                        elem2.getParent().remove(elem2);

                                        continue;

                                        }


                                    }

                                    i++;
                                }

                }

                // </editor-fold>



		// Update X and Y positions for items on the current row
		i = 0;
		size = currentRow.size();
		int leftmostEdge = startXPosition;
                int rightmostEdge = 0;

                // If no width is specified, use the width of the row as the starting
                // rightmost edge
                if ( StyleManager.getProperty(box, "width") == null )
                {
                    rightmostEdge = startXPosition + currentRowMaxWidth ;
                }
                else // use the current row max width
                {
                    rightmostEdge = startXPosition + currentRowMaxWidth;
                }


		while ( i < size )
		{
			temp = (Box) currentRow.get(i);

			temp.y = currentYPosition;

			if ( ! "none".equals( StyleManager.getProperty(temp, "float") ) )
			{
				int type = FloatBoxManager.FLOAT_LEFT ;
				if ( "right".equals( StyleManager.getProperty(temp, "float") ) )
				{
					type = FloatBoxManager.FLOAT_RIGHT;

                                        // Align the box according the the rightmost edge
                                        // and adjust it accordingly
                                        temp.x = rightmostEdge - temp.getTotalWidth();
                                        rightmostEdge = temp.x;
				}
                                else
                                {
                                    // Left float
                                    temp.x = leftmostEdge ;
                                    leftmostEdge += temp.getTotalWidth();
                                }
				FloatBoxManager.Float f = floats.addFloat(temp.getAbsoluteX(), temp.getAbsoluteY(), temp.getTotalWidth(), temp.getTotalHeight(), type, temp);
			}
			else // This is not a float
			{
				// TODO: Proper vertical align
				temp.y += ( getCurrentRowHeight() - temp.getTotalHeight() );

                                // Align the text to the left
                                temp.x = leftmostEdge ;
                                leftmostEdge += temp.getTotalWidth();
			}

			i++;
		}
        }

	public int rowHeightNoFloats = 0;
	public int rowHeightFloats = Integer.MAX_VALUE;

	public void placeOnCurrentRow(Box box)
	{
                savedForLater.remove(box);
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
            nextRow(0);
        }
	public void nextRow(int minimumOffset)
	{

		// Arrange the current row
		arrangeCurrentRow();

		// Step 4 : Profit

		// Increment the Y position
		setCurrentYPosition(Math.max(currentYPosition + minimumOffset,currentYPosition + getCurrentRowHeight()) );

		// Clear current row buffer
		currentRow.clear();

		// Update left offset for this row
		int x = box.getAbsoluteX() + box.marginLeft + box.paddingLeft;
		int y = box.getAbsoluteY() + getCurrentY() + box.marginTop + box.paddingTop ;
		int theoreticalMaxWidth = 99999999;
                //System.out.println("OORO: " + minimumOffset + " " + currentYPosition );
                currentRowMaxWidth = getMaxRowWidth(currentYPosition);
		startXPosition = floats.leftIntersectionPoint(x, y, theoreticalMaxWidth, box);
		currentXPosition = startXPosition ;

		rowHeightFloats = Integer.MAX_VALUE;
		rowHeightNoFloats = 0;

                nextChildFromSavedElements = true ;


                System.out.println("THIS ROW WIDTH: " + currentRowMaxWidth );

	}

	/**
	 * Maximum possible row width for the current box at specified Y offset, with respect to floats
	 * @param yPosition
	 * @return
	 */
	public int getMaxRowWidth(int yPosition)
	{
		int x = box.getAbsoluteX() + box.marginLeft + box.paddingLeft;
		int y = box.getAbsoluteY() + yPosition + box.marginTop + box.paddingTop;
		int theoreticalMaxWidth = box.parent.contentWidth - box.marginLeft - box.marginRight - box.paddingLeft - box.paddingRight ;

                if ( box.contentWidth > 0 )
                {
                    theoreticalMaxWidth = box.contentWidth ;
                }

		int leftPoint = floats.leftIntersectionPoint(x, y, theoreticalMaxWidth, box);
		int rightPoint = floats.rightIntersectionPoint(x, y, theoreticalMaxWidth, box);

//                System.out.println("MRW: " + yPosition + " " + leftPoint + " " + rightPoint + " " + (rightPoint - leftPoint));
               // LayoutModeler.text(box, 0);
		return (rightPoint - leftPoint) ;

	}

	/**
	 * Maximum possible box size, with respect to the parent and floats that apply to this context
	 * @param yPosition
	 * @return
	 */
	public int getMaxContentWidth(Box box)
	{
		int x = box.getAbsoluteX() + box.marginLeft + box.paddingLeft;
		int y = box.getAbsoluteY() + box.marginTop + box.paddingTop;
		int theoreticalMaxWidth = box.parent.contentWidth - box.marginLeft - box.marginRight - box.paddingLeft - box.paddingRight ;

		int leftCut = floats.leftIntersectionPoint(x, y, theoreticalMaxWidth, box);
		int rightCut = floats.rightIntersectionPoint(x, y, theoreticalMaxWidth, box);

		return (rightCut-leftCut) ;

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
                setMaxPossibleContentWidth(box.parent.contentWidth);
		children = box.correspondingNode.getChildren() ;
		childPos = 0;
	}


	public boolean hasMoreChildren()
	{
		if ( (childPos < children.size() ) || ( savedForLater.size() > 0 ) )
		{
                        System.out.println("HAS KID IN THIS FRAME");
			return true;
		}
		else
		{
                        if ( frameStack.size() > 0)
                        {
                             StackFrame frame = (StackFrame) frameStack.get(frameStack.size()-1);
                             if ( frame.childPos < frame.workingBox.correspondingNode.getChildren().size() )
                             {
                                 System.out.println("HAS KID IN OTHER FRAME");
                                 return true;
                             }
                             else
                             {
                                 System.out.println("NO MORE KIDS");
                                 return false;
                             }
                        }
                        else
                        {
                            System.out.println("NO MORE KIDS");
                            return false;
                        }
		}
	}

        public boolean hasMoreRegularChildren()
        {
                if ( (childPos < children.size() ) )
		{
                        System.out.println("HAS KID IN THIS FRAME");
			return true;
		}
		else
		{
                        if ( frameStack.size() > 0)
                        {
                             StackFrame frame = (StackFrame) frameStack.get(frameStack.size()-1);
                             if ( frame.childPos < frame.workingBox.correspondingNode.getChildren().size() )
                             {
                                 System.out.println("HAS KID IN OTHER FRAME");
                                 return true;
                             }
                             else
                             {
                                 System.out.println("NO MORE KIDS");
                                 return false;
                             }
                        }
                        else
                        {
                            System.out.println("NO MORE KIDS");
                            return false;
                        }
		}

        }

        public boolean hasBeenSavedForLater(Box b)
        {
            return ( savedForLater.indexOf(b) >= 0);
        }

        public boolean hasSavedForLaterChildren()
        {
            return ( savedForLater.size() > 0 );
        }

	public Box nextChild()
	{
		CssElement temp = null ;


                Box result = null ;
                if ( ( (savedForLater.size() > 0 ) && nextChildFromSavedElements) ||
                      ( (savedForLater.size() > 0) && ( childPos >= children.size() ) ) )
                {
                    result = (Box) savedForLater.get(0);
                }
                else
                {
                    while ( childPos >= children.size() )
                    {
                        // Remove current box from the visual model (since it's just a container for the actual inline elements)
                        box.parent.removeChild(box);
                        Box tempBox = box;
                        if ( box.DOMParent != null )
                        {
                            box.DOMParent.removeChild(box);
                        }

                        // Restore the previous frame
                        setTopFrameAsWorkingBoxFrame();
                    }
                    System.out.println("KIDS: " + childPos + " " + children.size() );
                    temp = (CssElement) children.get(childPos);
                    childPos++;


                    Box child = null ;
                    if ( temp.box != null )
                    {
                        child = temp.box;
                    }
                    else
                    {
                        child = new Box(temp);
                        temp.box = child ;
                    }
                    result = child;

                }

                if ( box.isInline )
                {
                    result.DOMParent = box;

                    result.parent = box.parent ;

                    result.correspondingNode.setStyle(box.correspondingNode.getStyle());

                    int pos = children.indexOf(result.correspondingNode);

                    System.out.println("WITHIN INLINE! " + pos);
                    if ( pos == 0 )
                    {
                        result.firstElementInInline = true;
                    }
                    if ( pos == children.size() - 1)
                    {
                        result.lastElementInInline = true;
                    }
                }

                lastChild = result;
                return result;
	}

}
