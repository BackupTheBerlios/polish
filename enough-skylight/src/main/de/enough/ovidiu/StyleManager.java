package de.enough.ovidiu;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.ImgCssElement;
import de.enough.skylight.renderer.node.TextCssElement;

public class StyleManager {

        // By how much the left margi of a list item should be increased
        public static final int LIST_ITEM_MARGIN_OFFSET = 20 ;

	public static final int WIDTH_ID = 226;
	public static final int HEIGHT_ID = 227;
	public static final int FLOAT_ID = 32709;
	public static final int DISPLAY_ID = 32710;
        public static final int MARGIN_ID = 32717;
        public static final int PADDING_ID = -6;

        public static final int MARGIN_LEFT_ID = -2;
        public static final int MARGIN_RIGHT_ID = -3;
        public static final int MARGIN_TOP_ID = -4;
        public static final int MARGIN_BOTTOM_ID = -5;

        public static final int PADDING_LEFT_ID = -7;
        public static final int PADDING_RIGHT_ID = -8;
        public static final int PADDING_TOP_ID = -9;
        public static final int PADDING_BOTTOM_ID = -10;

        public static final int CLEAR_ID  = 32712;
	
	
	public static void mapStyleToBox(Box box)
	{	
                // Margins
                // TODO: Implement properly (e.g. take into consideration margin-left, right, top and bottom,
                // take into consideration % values and more)
                /*if ( getProperty(box,"margin") != null )
		{
                        int marginValue = 0;
			Dimension d = (Dimension) getProperty(box, "margin");
			if ( d != null )
			{
                                marginValue = (d.getValue(box.parent.contentWidth));
                                box.marginBottom = box.marginTop = box.marginLeft = box.marginRight = marginValue;
			}
		}*/
                if ( getProperty(box,"margin-left") != null )
		{
                        int marginValue = 0;
			Dimension d = (Dimension) getProperty(box, "margin-left");
			if ( d != null )
			{
                                marginValue = (d.getValue(box.parent.contentWidth));
                                box.marginLeft = marginValue;

                                if ( box.isListItem )
                                {
                                    box.marginLeft += LIST_ITEM_MARGIN_OFFSET;
                                }
			}
		}
                if ( getProperty(box,"margin-right") != null )
		{
                        int marginValue = 0;
			Dimension d = (Dimension) getProperty(box, "margin-right");
			if ( d != null )
			{
                                marginValue = (d.getValue(box.parent.contentWidth));
                                box.marginRight = marginValue;
			}
		}
                if ( getProperty(box,"margin-top") != null )
		{
                        int marginValue = 0;
			Dimension d = (Dimension) getProperty(box, "margin-top");
			if ( d != null )
			{
                                marginValue = (d.getValue(box.parent.contentWidth));
                                box.marginTop = marginValue;
			}
		}
                if ( getProperty(box,"margin-bottom") != null )
		{
                        int marginValue = 0;
			Dimension d = (Dimension) getProperty(box, "margin-bottom");
			if ( d != null )
			{
                                marginValue = (d.getValue(box.parent.contentWidth));
                                box.marginBottom = marginValue;
			}
		}                

                // Paddings
                // TODO: Implement properly (e.g. take into consideration padding-left, right, top and bottom,
                // take into consideration % values and more)
                /*if ( getProperty(box,"padding") != null )
		{
                        int paddingValue = 0;
			Dimension d = (Dimension) getProperty(box, "padding");
			if ( d != null )
			{
                                paddingValue = (d.getValue(box.parent.contentWidth));
                                box.paddingBottom = box.paddingTop = box.paddingLeft = box.paddingRight = paddingValue;
			}
		}*/
                if ( getProperty(box,"padding-left") != null )
		{
                        int paddingValue = 0;
			Dimension d = (Dimension) getProperty(box, "padding-left");
			if ( d != null )
			{
                                paddingValue = (d.getValue(box.parent.contentWidth));
                                box.paddingLeft = paddingValue;
			}
		}
                if ( getProperty(box,"padding-right") != null )
		{
                        int paddingValue = 0;
			Dimension d = (Dimension) getProperty(box, "padding-right");
			if ( d != null )
			{
                                paddingValue = (d.getValue(box.parent.contentWidth));
                                box.paddingRight = paddingValue;
			}
		}
                if ( getProperty(box,"padding-top") != null )
		{
                        int paddingValue = 0;
			Dimension d = (Dimension) getProperty(box, "padding-top");
			if ( d != null )
			{
                                paddingValue = (d.getValue(box.parent.contentWidth));
                                box.paddingTop = paddingValue;
			}
		}
                if ( getProperty(box,"padding-bottom") != null )
		{
                        int paddingValue = 0;
			Dimension d = (Dimension) getProperty(box, "padding-bottom");
			if ( d != null )
			{
                                paddingValue = (d.getValue(box.parent.contentWidth));
                                box.paddingBottom = paddingValue;
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

                if ( getProperty(box,"clear") != null )
                {
                    String value = (String) getProperty(box, "clear");

                    if ( "left".equals(value) )
                    {
                        box.clearLeft = true;
                    }
                    else if ( "right".equals(value) )
                    {
                        box.clearRight = true;
                    }
                    else if ( "both".equals(value) )
                    {
                        box.clearLeft = box.clearRight = true;
                    }
                }
	}
	
	public static Object getProperty(Box box, String propertyName)
	{
		CssElement elem = box.correspondingNode;
		
		if ( propertyName.equals("width") )
		{
                    int startingOffset = 0;
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
                else if ( propertyName.equals("margin-left") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(MARGIN_LEFT_ID) );
                    if ( d == null )
                    {
                        return getProperty(box, "margin");
                    }
                    else
                    {
                        return d;
                    }
                }
                else if ( propertyName.equals("margin-right") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(MARGIN_RIGHT_ID) );
                    if ( d == null )
                    {
                        return getProperty(box, "margin");
                    }
                    else
                    {
                        return d;
                    }
                }
                else if ( propertyName.equals("margin-top") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(MARGIN_TOP_ID) );
                    if ( d == null )
                    {
                        return getProperty(box, "margin");
                    }
                    else
                    {
                        return d;
                    }
                }
                else if ( propertyName.equals("margin-bottom") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(MARGIN_BOTTOM_ID) );
                    if ( d == null )
                    {
                        return getProperty(box, "margin");
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
                else if ( propertyName.equals("padding-left") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(PADDING_LEFT_ID) );
                    if ( d == null )
                    {
                        return getProperty(box, "margin");
                    }
                    else
                    {
                        return d;
                    }
                }
                else if ( propertyName.equals("padding-right") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(PADDING_RIGHT_ID) );
                    if ( d == null )
                    {
                        return getProperty(box, "margin");
                    }
                    else
                    {
                        return d;
                    }
                }
                else if ( propertyName.equals("padding-top") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(PADDING_TOP_ID) );
                    if ( d == null )
                    {
                        return getProperty(box, "margin");
                    }
                    else
                    {
                        return d;
                    }
                }
                else if ( propertyName.equals("padding-bottom") )
                {
                    Dimension d = ( Dimension ) (  elem.getStyle().getObjectProperty(PADDING_BOTTOM_ID) );
                    if ( d == null )
                    {
                        return getProperty(box, "margin");
                    }
                    else
                    {
                        return d;
                    }
                }
                else if ( propertyName.equals("clear") )
                {
                    String value = (String) elem.getStyle().getObjectProperty(CLEAR_ID);

                    // Clear applies only to block elements
                    if ( value == null && "block".equals(elem.getStyle().getObjectProperty(DISPLAY_ID)))
                    {
                        return null;
                    }
                    else
                    {
                        System.out.println("WAAA! [" + value + "]");
                        return value;
                    }
                }

                return null;
	}

}
