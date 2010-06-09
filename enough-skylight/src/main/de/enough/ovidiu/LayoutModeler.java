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
		
		String text = "[ " + node.getHandler().getTag() + " D: " + StyleManager.getProperty(box, "display") + " F: " + StyleManager.getProperty(box, "float") + " X: " + box.x + " Y: " + box.y + " M: " + box.marginLeft + " TW: " + box.getTotalWidth() +" CW: " + box.contentWidth + " CH: " + box.contentHeight + " C: " + node.getContent() + " ]";
		
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
		
		if ( b.correspondingNode.getContentType() == CssElement.CONTENT_TEXT )
		{
			g.setFont(b.correspondingNode.getStyle().getFont());
			g.drawString( (String) b.correspondingNode.getContent(), b.getAbsoluteX() + b.paddingLeft + b.marginLeft, b.getAbsoluteY() + b.paddingTop + b.marginTop, Graphics.TOP | Graphics.LEFT );
		}
		else if ( b.correspondingNode.getContentType() == CssElement.CONTENT_IMAGE )
		{
			g.drawImage( ((ImgCssElement) b.correspondingNode).getImage(), b.getAbsoluteX() + b.marginLeft + b.paddingLeft, b.getAbsoluteY() + b.marginTop + b.paddingTop, Graphics.TOP | Graphics.LEFT );
		}
			
		
		int size = b.children.size();
		int i = 0;
		while ( i < size )
		{
			paintBox ( (Box) b.children.get(i), g );
			i++;
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
		
		doModel(myBox);
		
		
		dumpBoxTree(myBox,0);
		rootBox = myBox;

                System.out.println("FLOATS: " + floats.floats.size() );
		return myBox;
		
	}
			
	protected void doModel(Box box)
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
			Dimension d = (Dimension) StyleManager.getProperty(box, "width");
			if ( d != null )
			{
				box.contentWidth = (d.getValue(box.parent.contentWidth)) - box.marginLeft - box.marginRight - box.paddingLeft - box.paddingRight ;
			}
			else
			{
                            // No width specified, use maxwidth for now and figure out the actual width later
                            box.hasDynamicSize = true ;
                            box.contentWidth = 99999999 ; // Temp value.
			}
		}
                
		// If I am a block context, then I should use all available width,
		// unless I am forced to have a certain width
		if ( "block".equals(displayMode) )
		{
			Dimension d = (Dimension) StyleManager.getProperty(box, "width");
			if ( d != null )
			{
				box.contentWidth = (d.getValue(box.parent.contentWidth)) - box.marginLeft - box.marginRight - box.paddingLeft - box.paddingRight;
			}
			else
			{
				// Use all available
				box.contentWidth = context.getMaxContentWidth(box) ;                                
			}
		}
					
		// Process child nodes one by one
		while ( context.hasMoreChildren() )
		{
			Box child = context.nextChild();
			box.addChild(child);

                        // Retrieve some very important layout/display properties
                        String childFloatMode = (String) StyleManager.getProperty(child, "float");                       						
			String childDisplayMode = (String) StyleManager.getProperty(child, "display");

			// If I am a block child, then I should be alone on my own row ....
			if ( "block".equals(childDisplayMode) )
			{					
                                context.nextRow();
                                context.prepareToPlaceOnCurrentRow(child);
                                doModel(child);

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
					context.nextRow(1);
					context.prepareToPlaceOnCurrentRow(child);
					doModel(child);
				}
                                
				context.placeOnCurrentRow(child);
				context.nextRow();
			}

			// If I am an inline element, things get tricky
			else if ( "inline".equals(childDisplayMode) )
			{
				context.prepareToPlaceOnCurrentRow(child);
				doModel(child);

                                // For floats, I should see if I can be placed on the current row,
                                // or if I must be saved for later so other elements can be placed in my stead.
                                // --------------------------------

                                if ( ! "none".equals(childFloatMode) )
                                {
                                    // See if we can place it on the current row :
                                    // if it fits, and
                                    // - if there is no other "saved for later" element, or
                                    // - if it is the next in line "saved for later" element
                                    if ( context.fitsOnCurrentRow(child) && 
                                          ( ( context.hasSavedForLaterChildren() == false ) || ( context.hasBeenSavedForLater(child) ) ) )
                                    {
                                            context.placeOnCurrentRow(child);
                                            continue;
                                    }
                                    // If we can't fit it and we have more regular children, save it for later and
                                    // continue with the regular children
                                    else if ( context.hasMoreRegularChildren() )
                                    {
                                            context.saveForLater(child);
                                            continue;
                                    }
                                    else
                                    {
                                        // No more regular elements left, so we must treat this child
                                        // as a regular element and place it ... somewhere.
                                    }                                    
                                }

                                // For regular (non-float) elements, try to place me on the current row,
                                // otherwise go to the next row.
                                // ---------------------------------

                                // Content width is larger than the parent,
                                // so just force its placement on a separate row.
                                // TODO: is this right ???
                                
                                if ( context.doesNotFitWithin(child) )
                                {
                                    System.out.println("DEGEABA!");
                                    context.forcePlacementOnSeparateRow(child);
                                    continue;
                                }

                                // Try to place on the current row
				if ( context.fitsOnCurrentRow(child) )
				{
					context.placeOnCurrentRow(child);
				}
				else                                
				{
                                    // That didn't work, jump to the next row
                                    context.nextRow(1);

                                    // Make sure that I'm still the next child returned by nextChild()
                                    context.backOneRegularChild();
				}
			}
		}
		
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
                    box.contentWidth = context.getMaxX() ;
                }

                box.parent.addChild(box);
		return;
	}
}
