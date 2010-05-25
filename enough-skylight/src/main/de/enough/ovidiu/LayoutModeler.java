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
		
		String text = "[ " + node.getHandler().getTag() + " D: " + StyleManager.getProperty(box, "display") + " F: " + StyleManager.getProperty(box, "float") + " X: " + box.x + " Y: " + box.y + "AX: " + box.getAbsoluteX() + " AY: " + box.absoluteY + " W: " + box.contentWidth + " H: " + box.contentHeight + " C: " + node.getContent() + " ]"; 
		
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
			g.fillRect(b.getAbsoluteX(), b.absoluteY, b.getTotalWidth(), b.getTotalHeight());
		}

		g.setColor(0x11FF0000);
		g.drawRect(b.getAbsoluteX(), b.absoluteY, b.getTotalWidth(), b.getTotalHeight());
		
		if ( b.correspondingNode.getContentType() == CssElement.CONTENT_TEXT )
		{
			g.setFont(b.correspondingNode.getStyle().getFont());
			g.drawString( (String) b.correspondingNode.getContent(), b.getAbsoluteX() + b.paddingLeft, b.absoluteY + b.paddingTop, Graphics.TOP | Graphics.LEFT );
		}
		else if ( b.correspondingNode.getContentType() == CssElement.CONTENT_IMAGE )
		{
			g.drawImage( ((ImgCssElement) b.correspondingNode).getImage(), b.getAbsoluteX(), b.absoluteY, Graphics.TOP | Graphics.LEFT );					
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
		
		System.out.println("END MODEL");
		
		dumpBoxTree(myBox,0);
		rootBox = myBox;
		return myBox;
		
	}
			
	protected void doModel(Box box)
	{
		CssElement node = (CssElement) box.correspondingNode;
		
		StyleManager.mapStyleToBox(box);
		
		LayoutContext context = new LayoutContext(box,floats);	
	
		
		// Configure the box type based on the content
		
		// I am an atomic element of the page (image, text, etc) so just map my attributes to my box and be done with me!
		if ( node.getChildren().size() == 0 )
		{		
			return;
		}
		
		// Get some position and display mode information first
		String displayMode = (String) StyleManager.getProperty(box, "display");
		String floatMode = (String) StyleManager.getProperty(box, "float");
		
		// Figure out where I start
		// TODO: margins, paddings & co.
		int startX = box.parent.x;
		int startY = box.parent.y ;	
		
		
		// If I am a float, map the width property REGARDLESS of my display type 
		// Otherwise, figure out the width of my children and then figure out my width
		if ( ! "none".equals(floatMode) )
		{
			Dimension d = (Dimension) StyleManager.getProperty(box, "width");
			if ( d != null )
			{
				box.contentWidth = (d.getValue(box.parent.contentWidth));
			}
			else
			{
				// TODO: Figure out width of children
			}
		}
		// If I am a block context, then I should use all available width,
		// unless I am forced to have a certain width
		if ( "block".equals(displayMode) )
		{
			Dimension d = (Dimension) StyleManager.getProperty(box, "width");
			if ( d != null )
			{
				box.contentWidth = (d.getValue(box.parent.contentWidth));
			}
			else
			{
				// Use all available
				box.contentWidth = context.getMaxBoxWidth(box);
			}
		}
		
		// Create a layout context now
		
		//text(box, 0);
		//System.out.println("PARW: " + box.parent.width );
		
		// Current coordinates and maximum coordinates used (relative to this box)
		ArrayList forNextRow = new ArrayList();
	
		// Process child nodes one by one
		while ( context.hasMoreChildren() )
		{
			Box child = context.nextChild();
			box.addChild(child);
			
			// If child is bigger than the parent box right from the onset
			StyleManager.mapStyleToBox(child);
			if ( child.getTotalWidth() > box.getTotalWidth() )
			{
				System.out.println("FUCK THIS SHIT!");
				text(child, 0);
				System.out.println(context.currentXPosition);
				context.nextRow();
				System.out.println(context.currentXPosition);
				context.placeOnCurrentRow(child);
				context.nextRow();
				text(child, 0);
				continue;
			}
			
			// Figure out where the current child node should go.
			
			// If I am a block context, then I should be alone on this row ....
			String childDisplayMode = (String) StyleManager.getProperty(child, "display");
			if ( "block".equals(childDisplayMode) )
			{					
				// This might be very slow in the long run
				do
				{
					context.nextRow();
					context.prepareToPlaceOnCurrentRow(child);
					doModel(child);
				}
				while ( ! context.fitsOnCurrentRow(child) ) ;
				context.placeOnCurrentRow(child);
				context.nextRow();
			}
			// If I am an inline context, I should try to fit on this line, otherwise on the next one
			else if ( "inline".equals(childDisplayMode) )
			{
				context.prepareToPlaceOnCurrentRow(child);
				doModel(child);
				
				if ( context.fitsOnCurrentRow(child) )
				{
					context.placeOnCurrentRow(child);
				}
				else					
				{
					
					do
					{
						context.nextRow();
						context.prepareToPlaceOnCurrentRow(child);
						doModel(child);
					}
					while ( ! context.fitsOnCurrentRow(child) ) ;
					context.placeOnCurrentRow(child);
				}
			}
		}
		
		// Complete the current row
		context.nextRow() ;
		
		// Is my height pre-set or should I use my current height (i.e Y-position )
		if ( StyleManager.getProperty(box, "height") != null )
		{
			box.contentHeight = ( (Dimension) StyleManager.getProperty(box, "height") ).getValue(box.parent.contentHeight);
		}
		else
		{
			box.contentHeight = context.getMaxY();
		}
				
		return;
	}
}
