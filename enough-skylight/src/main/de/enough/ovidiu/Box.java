package de.enough.ovidiu;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.renderer.node.CssElement;

public class Box 
{	

	public int absoluteX = 0, absoluteY = 0;
	
	public int x=0,y=0,contentWidth=0,contentHeight=0;
	
	public int marginLeft = 0, marginTop = 0, marginRight =0, marginBottom = 0;
	
	public int paddingLeft =0, paddingTop = 0, paddingRight = 0, paddingBottom = 0;
	
	public ArrayList children = new ArrayList();
	
	public CssElement correspondingNode = null;
	
	public Box parent = null;
	
	public String comment = "" ;
	
	public Box(CssElement correspondingNode)
	{
		this.correspondingNode = correspondingNode ;
	}	
	
	public void addChild(Box b)
	{
		b.parent = this;
		children.add(b);
	}
	
	public void removeChild(Box b)
	{
		b.parent = null;
		children.remove(b);
	}
	
	public int getAbsoluteX()
	{
		if ( parent == null )
		{
			return 0;
		}
		else
		{
			return x + parent.getAbsoluteX() ;
		}
	}
	
	
	
	
	public int getTotalWidth()
	{
		// TODO: margins, paddings, etc;
		return contentWidth + paddingLeft + paddingRight;
	}
	
	public int getTotalHeight()
	{
		// TODO: margins, paddings, etc;
		return contentHeight + paddingTop + paddingBottom ;
	}
}
