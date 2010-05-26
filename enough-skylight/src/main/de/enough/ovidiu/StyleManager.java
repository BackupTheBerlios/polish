package de.enough.ovidiu;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.ImgCssElement;
import de.enough.skylight.renderer.node.TextCssElement;

public class StyleManager {
	
	public static final int WIDTH_ID = 226;
	public static final int HEIGHT_ID = 227;
	public static final int FLOAT_ID = 32709;
	public static final int DISPLAY_ID = 32710;
	
	
	public static void mapStyleToBox(Box box)
	{
		// Width
		if ( getProperty(box,"width") != null )
		{
			Dimension d = (Dimension) getProperty(box, "width");
			if ( d != null )
			{
				if (d.isPercent())
				{
					box.contentWidth = (d.getValue(box.parent.contentWidth));
				}
				else
				{
					box.contentWidth = (d.getValue(100));
				}
			}
		}
		
		// Height
		if ( getProperty(box,"height") != null )
		{
			Dimension d = (Dimension) getProperty(box, "height");
			if ( d != null )
			{
				if (d.isPercent())
				{
					box.contentHeight = (d.getValue(box.parent.contentHeight));
				}
				else
				{
					box.contentHeight = (d.getValue(100));
				}
			}
		}
	}
	
	public static Object getProperty(Box box, String propertyName)
	{
		CssElement elem = box.correspondingNode;
		
		if ( propertyName.equals("width") )
		{
			if ( elem instanceof TextCssElement )
			{
				return new Dimension(elem.getStyle().getFont().stringWidth( ((TextCssElement) elem).getValue() ));
			}
			else if ( elem instanceof ImgCssElement )
			{
				return new Dimension (( (ImgCssElement) elem).getImage().getWidth());
			}
			else
			{
				Dimension d = ( Dimension ) ( elem.getStyle().getObjectProperty(WIDTH_ID) );
				return d;
			}
		}
		
		else if ( propertyName.equals("height") )
		{
			if ( elem instanceof TextCssElement )
			{
				return new Dimension(elem.getStyle().getFont().getHeight());
			}
			else if ( elem instanceof ImgCssElement )
			{
				return new Dimension (( (ImgCssElement) elem).getImage().getHeight());
			}
			else
			{
				Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(HEIGHT_ID) );
				return d;
			}
		}
		else if ( propertyName.equals("float"))
		{
			String floatValue = (String) elem.getStyle().getObjectProperty(FLOAT_ID);
			if ( floatValue == null )
			{
				floatValue = "none";
			}
			return floatValue;
			
		}
		else if ( propertyName.equals("display"))
		{
			return elem.getStyle().getObjectProperty(DISPLAY_ID);
		}
		
		return null;
		
		
	}

}