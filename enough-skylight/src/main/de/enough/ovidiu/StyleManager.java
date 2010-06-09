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
        public static final int MARGIN_ID = 32717;
        public static final int PADDING_ID = -6;
	
	
	public static void mapStyleToBox(Box box)
	{	
                // Margins
                // TODO: Implement properly (e.g. take into consideration margin-left, right, top and bottom,
                // take into consideration % values and more)
                if ( getProperty(box,"margin") != null )
		{
                        int marginValue = 0;
			Dimension d = (Dimension) getProperty(box, "margin");
			if ( d != null )
			{
				if (d.isPercent())
				{
					marginValue = (d.getValue(box.parent.contentWidth));
				}
				else
				{
					marginValue = (d.getValue(100));
				}

                                box.marginLeft = box.marginRight = box.marginTop = box.marginBottom = marginValue;
			}
		}

                // Paddings
                // TODO: Implement properly (e.g. take into consideration padding-left, right, top and bottom,
                // take into consideration % values and more)
                if ( getProperty(box,"padding") != null )
		{
                        int paddingValue = 0;
			Dimension d = (Dimension) getProperty(box, "padding");
			if ( d != null )
			{
				if (d.isPercent())
				{
					paddingValue = (d.getValue(box.parent.contentWidth));
				}
				else
				{
					paddingValue = (d.getValue(100));
				}

                                box.paddingLeft = box.paddingRight = box.paddingTop = box.paddingBottom = paddingValue;
                                if ( box.paddingLeft != 0)
                                {
                                    System.out.println("PADDING: " + box.paddingLeft );
                                }
			}
		}

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
                        box.contentWidth = box.contentWidth - box.marginLeft - box.marginRight - box.paddingLeft - box.paddingRight ;
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
                        box.contentHeight = box.contentHeight - box.marginBottom - box.marginTop - box.paddingTop - box.paddingBottom ;
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
                else if ( propertyName.equals("margin") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(MARGIN_ID) );
                    if ( d == null )
                    {
                        return new Dimension(0);
                    }
                    else
                    {
                        return d;
                    }
                }
                else if ( propertyName.equals("padding") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(PADDING_ID) );
                    if ( d == null )
                    {
                        return new Dimension(0);
                    }
                    else
                    {
                        return d;
                    }
                }
		
		return null;
		
		
	}

}
