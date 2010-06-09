package de.enough.ovidiu;

import de.enough.polish.ui.Border;
import de.enough.polish.util.ArrayList;
import de.enough.skylight.renderer.node.CssElement;

public class Box 
{	

	public int absoluteX = 0;
	public int absoluteY = 0;
	
	public int x=0;
	public int y=0;
	public int contentWidth=0;
	public int contentHeight=0;
	
	public int marginLeft = 0;
	public int marginTop = 0;
	public int marginRight = 0;
	public int marginBottom = 0;
	
	public int paddingLeft = 0;
	public int paddingTop = 0;
	public int paddingRight = 0;
	public int paddingBottom = 0;
	
	public int borderLeft = 0;
	public int borderTop = 0;
	public int borderRight = 0;
	public int borderBottom = 0;
	
	public ArrayList children = new ArrayList();
	
	public CssElement correspondingNode = null;
	
	public Box parent = null;
	
	public String comment = "" ;

        public boolean hasDynamicSize = false ;
	
	public Box(CssElement correspondingNode)
	{
		this.correspondingNode = correspondingNode ;
	}	
	
	public void addChild(Box b)
	{
		b.parent = this;
                children.remove(b);
		children.add(b);
	}
	
	public void removeChild(Box b)
	{
		b.parent = null;
		children.remove(b);
	}

        public void removeAllChildren()
        {
            children.clear();
        }
	
	public int getAbsoluteX()
	{
		if ( parent == null )
		{
			return 0;
		}
		else
		{
			return x + parent.getAbsoluteX() + parent.marginLeft + parent.paddingLeft;
		}
	}

        public int getAbsoluteY()
	{
		if ( parent == null )
		{
			return 0;
		}
		else
		{
			return y + parent.getAbsoluteY() + parent.marginTop + parent.paddingTop ;
		}
	}
	
	
	
	
	public int getTotalWidth()
	{
		return contentWidth + paddingLeft + paddingRight + marginLeft + marginRight + borderLeft + borderRight;
	}
	
	public int getTotalHeight()
	{
		return contentHeight + paddingTop + paddingBottom + marginBottom + marginTop + borderBottom + borderTop;
	}
}
