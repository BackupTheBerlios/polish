package de.enough.ovidiu;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Dimension;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.renderer.Viewport;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.ImgCssElement;

public class LayoutModeler 
{
	public static void out(String text, int padding)
	{		
		while ( padding-- > 0 ) { System.out.print(" "); }
		System.out.print(text);
		System.out.println();
	}

	public static void text(Box box, int level)
	{
		CssElement node = box.correspondingNode ;
		
		String text = "[ " + node.getHandler().getTag() + " " + " " + box.clearLeft + " " + box.clearRight + " " + box.hasDynamicSize + " D: " +  StyleManager.getProperty(box, "display") + " F: " + StyleManager.getProperty(box, "float") + " X: " + box.x + " Y: " + box.y + " M: " + box.marginLeft + " TW: " + box.getTotalWidth() +" CW: " + box.contentWidth + " CH: " + box.contentHeight + " C: " + node.getContent() + " ]";
		
		out (text,level);
	}
	
	/*public static void recParse(CssElement node, int level)
	{
		ArrayList kids = node.getChildren() ;
		
		out ( text(node) , level );
		
		if ( kids != null )
		{
			int i = 0;
			while ( i < kids.size() )
			{
				recParse( (CssElement) kids.get(i), level+1);
				i++;
			}
		}
	} */
		

	FloatBoxManager floats = null;
	ArrayList toBeProcessed = null ;
	
	public static void dumpBoxTree(Box rootNode,int level)
	{
		text(rootNode,level);
		int i=0;
		ArrayList kids = rootNode.children;
		int kidCount = kids.size();
		
		// Sort the kids from left-right and from top-bottom;
		// Use very very slow bubble sort for now
		
		int x,y;
		Box box1, box2, temp;		
		for (x=0;x<kidCount;x++)
		{
			for (y=x+1;y<kidCount;y++)
			{
				box1 = (Box) kids.get(x);
				box2 = (Box) kids.get(y);
				
				if ( box1.y > box2.y )
				{
					kids.set(x, box2);
					kids.set(y, box1);
				}
				else if ( box1.y == box2.y )
				{
					if ( box1.x > box2.x )
					{
						kids.set(x, box2);
						kids.set(y, box1);
					}
				}
					
			}
		}
		
		while ( i<kidCount )
		{
			Box t = (Box) kids.get(i);
			dumpBoxTree(t,level+1);
			i++;
		}
	}
	
	public static Box rootBox = null ;
	
	public static void paintBox(Box b, Graphics g)
	{

                // Paint floats blue
		if ( ! "none".equals( StyleManager.getProperty(b, "float") ) )
		{
			g.setColor(0x110000FF);
			g.fillRect(b.getAbsoluteX(), b.getAbsoluteY(), b.getTotalWidth(), b.getTotalHeight());
		}

                // Draw outer box margins
		g.setColor(0x11FF0000);
		g.drawRect(b.getAbsoluteX(), b.getAbsoluteY(), b.getTotalWidth(), b.getTotalHeight());

                // Draw inner box margins (actual content borders)
		g.setColor(0x1100FF00);
		g.drawRect(b.getAbsoluteX() + b.marginLeft, b.getAbsoluteY() + b.marginTop, b.getTotalWidth() - b.marginLeft - b.marginRight, b.getTotalHeight() - b.marginTop - b.marginBottom );

                // Draw the actual content
		if ( b.correspondingNode.getContentType() == CssElement.CONTENT_TEXT )
		{
			g.setFont(b.correspondingNode.getStyle().getFont());
			g.drawString( (String) b.correspondingNode.getContent(), b.getAbsoluteX() + b.paddingLeft + b.marginLeft, b.getAbsoluteY() + b.paddingTop + b.marginTop, Graphics.TOP | Graphics.LEFT );
		}
		else if ( b.correspondingNode.getContentType() == CssElement.CONTENT_IMAGE )
		{
			g.drawImage( ((ImgCssElement) b.correspondingNode).getImage(), b.getAbsoluteX() + b.marginLeft + b.paddingLeft, b.getAbsoluteY() + b.marginTop + b.paddingTop, Graphics.TOP | Graphics.LEFT );
		}

                
		Box firstListItem = null ;
                Box workBox = null;

                // Paint sub-children
		int size = b.children.size();
		int i = 0;
		while ( i < size )
		{
                    workBox = (Box) b.children.get(i) ;
                    paintBox ( workBox, g );

                    // First item in a list item ?
                    if ( b.isListItem && firstListItem == null && workBox.isFirstItemOnRow )
                    {
                        firstListItem = workBox;
                    }

                    i++;
		}

                // Draw the bullet point if this is a list-item
                if  ( b.isListItem && firstListItem != null)
                {
                    g.setColor(255, 0, 0);
                    g.fillRect(firstListItem.getAbsoluteX() - 10, firstListItem.getAbsoluteY() + 3 , 5 , 5);
                }
	
	}
	
	public static void paintRootBox(Graphics g)
	{
		if ( rootBox != null )
		{
			paintBox(rootBox,g);
		}
	}
	
	public Box model(CssElement rootNode, int contentWidth)
	{
		System.out.println("HELLO!");
		//recParse(rootNode, 0);
		
		toBeProcessed = new ArrayList();
		floats = new FloatBoxManager();
				
		//recParse ( rootNode, 0 );
		
		// Define an abstract parent box
		Box box = new Box(null);
		box.contentWidth = contentWidth;
		box.contentHeight = Integer.MAX_VALUE ;
		box.correspondingNode = null ;
		
		// Define a box for the root node
		Box myBox = new Box(rootNode);
		box.addChild(myBox);

                LayoutContext context = new LayoutContext(myBox,floats);
		doModel(myBox,context);
		
		
		dumpBoxTree(myBox,0);
		rootBox = myBox;

                System.out.println("FLOATS: " + floats.floats.size() );
		return myBox;
		
	}
			
	protected void doModel(Box box, LayoutContext parentContext)
	{                
		CssElement node = (CssElement) box.correspondingNode;

                // Map CSS attributes to the box. They might be changed later on.
		StyleManager.mapStyleToBox(box);

                // Create a new layout context for the current box
		LayoutContext context = new LayoutContext(box,floats);

                // In case this is a re-model operation for this box, clear all steps from the previous modelling operation(s)
                context.removeFloatsForBox(box);
		box.removeAllChildren();	
				
		// If I am an atomic element of the page (image, text, etc) just map my attributes to my box and be done with me!
		if ( node.getChildren().size() == 0 )
		{		
			return;
		}
		
		// Get some position and display mode information first
		String displayMode = (String) StyleManager.getProperty(box, "display");
		String floatMode = (String) StyleManager.getProperty(box, "float");
		
		// If I am a float, map the width property REGARDLESS of my display type 
		// Otherwise, figure out the total width of my children and then figure out my width
		if ( ! "none".equals(floatMode) )
		{
                        box.isFloat = true ;
			Dimension d = (Dimension) StyleManager.getProperty(box, "width");
			if ( d != null )
			{
				box.contentWidth = (d.getValue(box.parent.contentWidth)) - box.marginLeft - box.marginRight - box.paddingLeft - box.paddingRight ;
			}
			else
			{
                            // No width specified, use maxwidth for now and figure out the actual width later
                            box.hasDynamicSize = true ;
                            box.contentWidth = box.getContentWidthFromTotalWidth(box.parent.contentWidth) ; // Temp value.
			}
		}
                else
		// If I am a block context, then I should use all available width,
		// unless I am forced to have a certain width
		if ( "block".equals(displayMode) || "list-item".equals(displayMode) )
		{
			Dimension d = (Dimension) StyleManager.getProperty(box, "width");
			if ( d != null )
			{
				box.contentWidth = (d.getValue(box.parent.contentWidth)) - box.marginLeft - box.marginRight - box.paddingLeft - box.paddingRight;
			}
			else
                        {
                            // If the parent has dynamic size, so should I
                            if ( box.parent.hasDynamicSize )
                            {
                               // box.hasDynamicSize = true;
                            }

                            // Be as big as I can without overflowing
                            box.contentWidth = box.getContentWidthFromTotalWidth(parentContext.getRemainingRowWidth());
                            System.out.println("HOPA: " + box.contentWidth + " " + parentContext.currentRowMaxWidth + " " + parentContext.getCurrentX() );
                               
			}
		}
                else // For inline context, have dynamic size!
                if (  "inline".equals(displayMode) || "inline-block".equals(displayMode) )
                {
                   box.contentWidth = 9999999 ; // Temp value.
                   box.hasDynamicSize = true ;
                }

                // Initialize the layout context's first row
                context.nextRow();

					
		// Process child nodes one by one
		while ( context.hasMoreChildren() )
		{
			Box child = context.nextChild();
                        context.removeFloatsForBox(child);

                        // Is the child a BR ?
                        if ( child.correspondingNode.getContentType() == CssElement.CONTENT_BR)
                        {
                            context.nextRow(box.correspondingNode.getStyle().getFont().getHeight());
                            continue;
                        }

			box.addChild(child);
                        StyleManager.mapStyleToBox(child);

                        // Retrieve some very important layout/display properties
                        String childFloatMode = (String) StyleManager.getProperty(child, "float");                       						
			String childDisplayMode = (String) StyleManager.getProperty(child, "display");

                        // Handle the clear attribute
                        if ( ( ! "none".equals(childFloatMode) ) || ( "block".equals(childDisplayMode) ) )
                        {
                            
                        }

			// If I am an inline-block element, or an inline float things get tricky
                        if ( "inline-block".equals(childDisplayMode) || ( ! "none".equals(childFloatMode ) ) )
			{
				context.prepareToPlaceOnCurrentRow(child);
				doModel(child, context);

                               // Content width is larger than the parent,
                                // so just force its placement on a separate row.
                                // TODO: is this right ???

                                if ( context.doesNotFitWithin(child) )
                                {
                                    System.out.println("DEGEABA!");
                                    context.forcePlacementOnSeparateRow(child);
                                    continue;
                                }

                                System.out.println("INLINE CHILD OR FLOAT:");
                                dumpBoxTree(child, 0);

                                // For floats, I should see if I can be placed on the current row,
                                // or if I must be saved for later so other elements can be placed in my stead.
                                // --------------------------------

                                if ( ! "none".equals(childFloatMode) )
                                {
                                    if ( child.clearRight )
                                    {
                                        if ( ( context.getRightFloatsCount() ) > 0 || ( context.getCurrentRowRightFloatOccupiedSpace() > 0) )
                                        {
                                            if ( context.hasMoreRegularChildren() )
                                            {
                                                context.removeFloatsForBox(child);
                                                context.saveForLater(child);
                                                continue;
                                            }
                                            else
                                            {
                                                do
                                                {
                                                    context.nextRow(1);
                                                }
                                                while ( ( context.getCurrentRowRightFloatOccupiedSpace() > 0) ) ;
                                            }
                                        }
                                        else
                                        {
                                            context.requestClearRight();
                                        }
                                    }
                                    if ( child.clearLeft )
                                    {
                                        if ( context.hasMoreRegularChildren() )
                                            {
                                                context.removeFloatsForBox(child);
                                                context.saveForLater(child);
                                                continue;
                                            }
                                            else
                                            {
                                                do
                                                {
                                                    context.nextRow(1);
                                                }
                                                while ( ( context.getCurrentRowLeftFloatOccupiedSpace() > 0) ) ;
                                            }
                                    }


                                    // Consider previous clear attributes and go to the next row if a clear: right has
                                    // been requested by a previous element
                                    if ( context.hasClearRightRequested() )
                                    {
                                        context.nextRow();
                                    }

                                    System.out.println("FLOAT! ");
                                    // See if we can place it on the current row :
                                    // if it fits, and
                                    // - if there is no other "saved for later" element, or
                                    // - if it is the next in line "saved for later" element
                                    if ( context.fitsOnCurrentRow(child) && 
                                          ( ( context.hasSavedForLaterChildren() == false ) || ( context.hasBeenSavedForLater(child) ) ) )
                                    {
                                            System.out.println("PLACE ON CURRENT ROW!");
                                            context.placeOnCurrentRow(child);
                                            continue;
                                    }
                                    // If we can't fit it and we have more regular children, save it for later and
                                    // continue with the regular children
                                    else if ( context.hasMoreRegularChildren() )
                                    {
                                            System.out.println("SAVE FOR LATER");
                                            context.saveForLater(child);
                                            continue;
                                    }
                                    else
                                    {
                                        System.out.println("TREAT AS REGULAR CHILD");
                                        // No more regular elements left, so we must treat this child
                                        // as a regular element and place it ... somewhere.
                                    }                                    
                                }

                                // For regular (non-float) elements, try to place me on the current row,
                                // otherwise go to the next row.
                                // ---------------------------------

                                System.out.println("INLINE-BLOCK CHILD");

                                // Try to place on the current row
				if ( context.fitsOnCurrentRow(child) )
				{
                                    System.out.println("FITS ON CURRENT ROW! " + context.currentRowMaxWidth );
                                    context.placeOnCurrentRow(child);
				}
				else                                
				{
                                        System.out.println("NOPE, NEXT ROW!");
                                    // That didn't work, jump to the next row
                                    context.removeFloatsForBox(child);
                                    context.nextRow(1);

                                    // Make sure that I'm still the next child returned by nextChild()
                                    context.repeatLastChild();
				}
			} 

                        // For regular inline stuff, just go into the child
                        else if ( "inline".equals(childDisplayMode) )
                        {
                            System.out.println("GOING INTO A CHILD");
                            child.isInline = true;
                            context.removeFloatsForBox(child);
                            context.goInto(child);
                        }
                        else // If I am a block child, then I should be alone on my own row ....
			if ( "block".equals(childDisplayMode) || "list-item".equals(childDisplayMode) )
			{
                                if ( child.clearRight )
                                {
                                    if ( ( context.getRightFloatsCount() ) > 0 || ( context.getCurrentRowRightFloatOccupiedSpace() > 0) )
                                    {
                                        do
                                        {
                                            context.removeFloatsForBox(child);
                                            context.nextRow(1);
                                        }
                                        while ( ( context.getCurrentRowRightFloatOccupiedSpace() > 0) ) ;
                                    }
                                    else
                                    {
                                        context.requestClearRight();
                                    }
                                }
                                if ( child.clearLeft )
                                {
                                    if ( ( context.getLeftFloatsCount() ) > 0 || ( context.getCurrentRowLeftFloatOccupiedSpace() > 0) )
                                    {
                                        do
                                        {
                                            context.removeFloatsForBox(child);
                                            context.nextRow(1);
                                        }
                                        while ( ( context.getCurrentRowLeftFloatOccupiedSpace() > 0) ) ;
                                    }
                                }

                                System.out.println("BLOCK CHILD");
                                if ( context.currentRowHasOnlyFloats() == false )
                                {
                                    System.out.println("J1");
                                    context.nextRow();
                                }
                                else
                                {
                                    System.out.println("DO NOT JUMP!");
                                }

                                // Adjust some row properties to suit block elements
                                context.clearCalculatedRowLimits();
                                child.isBlock = true ;


                                if ( "list-item".equals(childDisplayMode) )
                                {
                                    child.isListItem = true ;
                                }

                                context.prepareToPlaceOnCurrentRow(child);
                                
                                doModel(child, context);

                                // Content width is larger than the parent,
                                // so just force its placement on a separate row.
                                // TODO: is this right ???

                                if ( context.doesNotFitWithin(child) )
                                {
                                    context.forcePlacementOnSeparateRow(child);
                                    continue;
                                }

                                // If I don't fit on the current row, go lower until I do.
				while ( ! context.fitsOnCurrentRow(child) )
				{
                                        System.out.println("J2");
					context.nextRow(1);
					context.prepareToPlaceOnCurrentRow(child);
					doModel(child, context);
				}

				context.placeOnCurrentRow(child);
                                System.out.println("J3");
				context.nextRow();
			}
		}

                System.out.println("FILL IN THE REMAINING STUFF!");

		// Complete the current row
		context.nextRow() ;
		
		// Is my content height pre-set or should I use my current height (i.e Y-position )
		if ( StyleManager.getProperty(box, "height") != null )
		{
			box.contentHeight = ( (Dimension) StyleManager.getProperty(box, "height") ).getValue(box.parent.contentHeight) ;
		}
		else
		{
			box.contentHeight = context.getMaxY() ;
		}

                // Do I have a dynamic size (based on the maximum X coordinate used) ?
                if ( box.hasDynamicSize == true )
                {
                    if ( "block".equals(displayMode) && "none".equals(floatMode) )
                    {
                        box.contentWidth = Math.max(context.getMaxX(), box.getContentWidthFromTotalWidth(parentContext.getRemainingRowWidth())) ;
                    }
                    else
                    {
                        box.contentWidth = context.getMaxX() ;
                    }
                }

                box.parent.addChild(box);
		return;
	}
}
