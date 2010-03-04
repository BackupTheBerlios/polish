//#condition polish.hasFloatingPoint && !polish.android && !polish.blackberry
/*
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */

package de.enough.polish.ui.gaugeviews;

import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Image;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Point;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.MathUtil;
import de.enough.polish.util.RgbImage;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/**
 * This class implements a Clock-like view for gauges.
 *
 * @author Ovidiu Iliescu
 */
public class ClockInstrumentGaugeView extends ItemView {

    transient Image backgroundImage = null ;
    transient Image needleImage = null;
    int needleX = -1;
    int needleY = -1;
    int needleCenterX = -1;
    int needleCenterY = -1;

    int startAngle = 180;
    int endAngle = 0;

    long lastAnimationTime = 0;
    transient Gauge gauge = null;

    //#if polish.css.needle-filter
    private RgbFilter[] needleFilters = null ;
    //#endif


    protected void initContent(Item parent, int firstLineWidth, int availWidth, int availHeight) {

        gauge = (Gauge) parent;
        contentWidth = this.backgroundImage.getWidth();
        contentHeight = this.backgroundImage.getHeight();
            
    }

    protected void setStyle(Style style)
    {
        super.setStyle(style);
    }

    protected void setStyle(Style style, boolean resetStyle) {
        super.setStyle(style,resetStyle);

        removeParentBackground();
        removeParentBorder();

        //#if polish.css.gauge-clock-instrument-needle-image
        String imageUrl = style.getProperty("gauge-clock-instrument-needle-image");
        if (imageUrl != null)
        {
            try
            {
                    this.needleImage = Image.createImage(imageUrl);
            }
            catch(IOException e)
            {
                    //#debug error
                    System.out.println("unable to load image " + e);
            }

            needleCenterX = needleImage.getWidth() / 2;
            needleCenterY = needleImage.getHeight() / 2;
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-background-image
        imageUrl = style.getProperty("gauge-clock-instrument-background-image");
        if (imageUrl != null)
        {
            try
            {
                    this.backgroundImage = Image.createImage(imageUrl);
            }
            catch(IOException e)
            {
                    //#debug error
                    System.out.println("unable to load image " + e);
            }

            needleX = backgroundImage.getWidth() / 2;
            needleY = backgroundImage.getHeight() / 2;
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-center-y
        Integer temp = style.getIntProperty("gauge-clock-instrument-needle-center-y");
        if ( temp != null )
        {
            needleCenterY = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-center-x
        temp = style.getIntProperty("gauge-clock-instrument-needle-center-x");
        if ( temp != null )
        {
            needleCenterX = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-x
        temp = style.getIntProperty("gauge-clock-instrument-needle-x");
        if ( temp != null )
        {
            needleX = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-needle-y
        temp = style.getIntProperty("gauge-clock-instrument-needle-y");
        if ( temp != null )
        {
            needleY = temp.intValue();
        }        
        //#endif

        //#if polish.css.gauge-clock-instrument-start-angle
        temp = style.getIntProperty("gauge-clock-instrument-start-angle");
        if ( temp != null )
        {
           startAngle = temp.intValue();
        }
        //#endif

        //#if polish.css.gauge-clock-instrument-end-angle
        temp = style.getIntProperty("gauge-clock-instrument-end-angle");
        if ( temp != null )
        {
            endAngle = temp.intValue() ;
        }
        //#endif

        //#if polish.css.needle-filter
        RgbFilter [] currentFilters = (RgbFilter[]) style.getObjectProperty("needle-filter");
        if (currentFilters != null) {
            if (currentFilters!= needleFilters)
            {
                needleFilters = new RgbFilter[ currentFilters.length ];
                for (int i = 0; i < currentFilters.length; i++)
                {
                        RgbFilter rgbFilter = currentFilters[i];
                        try
                        {
                                needleFilters[i] = (RgbFilter) rgbFilter.getClass().newInstance();
                                needleFilters[i].setStyle(style, resetStyle);
                        } catch (Exception e)
                        {
                                //#debug warn
                                System.out.println("Unable to initialize filter class " + rgbFilter.getClass().getName() + e );
                        }
                }
            }
        }
        //#endif

    }

    protected void paintContent(Item parent, int x, int y, int leftBorder, int rightBorder, Graphics g) {
      
        
        de.enough.polish.ui.Graphics graphics = new de.enough.polish.ui.Graphics(g);

        double degreesPerTick = ( 1.0 * ( endAngle - startAngle) ) / gauge.getMaxValue();
        int currentAngle = startAngle + (int) ( degreesPerTick * gauge.getValue() );
        int bgLeft = x + (contentWidth - backgroundImage.getWidth()) / 2;
        int bgTop = y + (contentHeight - backgroundImage.getHeight()) / 2;

        // Draw background 
        graphics.drawImage(backgroundImage,bgLeft,bgTop, de.enough.polish.ui.Graphics.TOP | de.enough.polish.ui.Graphics.LEFT);

        // Draw the needle
        //#if polish.css.needle-filter
            RgbImage rgbImage = new RgbImage(needleImage.getRgbData(),needleImage.getWidth());
            if ( needleFilters != null )
            {
                if ( needleFilters.length > 0 )
                {
                    for (int i=0; i<needleFilters.length; i++)
                    {
                            RgbFilter filter = needleFilters[i];
                            rgbImage = filter.process(rgbImage);
                    }
                }
            }
            graphics.drawRotatedImage( rgbImage,  needleCenterX, needleCenterY, bgLeft + needleX , bgTop + needleY, currentAngle );
        //#else
            graphics.drawRotatedImage( needleImage,  needleCenterX, needleCenterY, bgLeft + needleX , bgTop + needleY, currentAngle );
        //#endif
    }

    protected void valueBasedOnPointerPosition(int x, int y)
    {

        // Get the pointer coordinates relative to the content area
        Point p= adjustToContentArea(x, y);
        x = p.x;
        y = p.y ;

        // Next, get the pointer coordinates relative to the needle rotation center
        int bgLeft = (contentWidth - backgroundImage.getWidth()) / 2;
        int bgTop = (contentHeight - backgroundImage.getHeight()) / 2;
        int ndlX =  bgLeft + needleX ;
        int ndlY = bgTop + needleY ;        
        int dX = x - ndlX;
        int dY = ndlY - y;

        // Get the angle defined by the needle center and the pointer
        int angle = (int) ( ( MathUtil.atan2(dX, dY) * 180 ) / Math.PI );

        // Transform the start and end angles to normal trigonometric notation
        int processedStartAngle = startAngle ;
        int processedEndAngle = endAngle;

        // Store various information about the arc defined by the start and end angles
        int totalArcLength = Math.abs ( processedEndAngle - processedStartAngle );
        boolean initialArcFollowsTrigonometricDirection = ( startAngle <= endAngle);

        boolean anglesHaveDifferentSigns = ( ( processedStartAngle * processedEndAngle ) <= 0 ) ;

        // NOTE : Positive trigonometric direction : from quadrant I to quadrant IV
        // NOTE : Negative trigonometric direction : from quadrant IV to quadrant I

        // If, needed, swap the start and end angles so that the resulting arc always
        // follows the positive trigonometric direction.
        // That is, you will always move from the start angle towards the end angle
        // in the positive trigonometric direction.
        if ( ! initialArcFollowsTrigonometricDirection )
        {
            int temp = processedStartAngle;
            processedStartAngle = processedEndAngle;
            processedEndAngle = temp;
        }

        // Convert the angles to the interval 0-360
        processedStartAngle = ( 360 + processedStartAngle ) % 360 ;
        processedEndAngle = ( 360 + processedEndAngle ) % 360 ;

        // Various variables
        int newGaugeValue = 0;
        int arcType = 0;
        boolean isInsideArc = false ;

        // Check if the pointer is within the arc defined by (startAngle,endAngle);
        // For cases like -30, 30, that mix angles with different signs we check
        // if the needle is inside the arc that intersects the positive X-axis,
        // since moving in the positive trigonometric direction in this case means
        // moving from an angle less than 0 towards an angle greater than zero, with
        // the 0 angle somewhere in the middle.      
        if ( anglesHaveDifferentSigns )
        {
            if ( ( angle >= processedStartAngle) || ( angle <= processedEndAngle) )
            {
                isInsideArc = true;
                arcType = 1;
            }

            // NOTE : The check above (with || instead of &&) works because we converted all angles from the
            // (-360,360) interval used in the CSS to the [0,360) interval used by the math functions.
            // More specifically, we convert all negative angles to their corresponding positive values.
            // We can do this because the trigonometric circle "loops" around itself (see Trigonometry 101).
            // Thus, the arc (-30,30) will become (330,30), as -30 is outside the [0,360) interval
            // and, when converted to said interval, will become 330 degrees.
            // A pointer angle of -10 degress (which is clearly inside the (-30,30) arc) will be converted to 350 degrees.
            // A pointer angle of 20 degrees (which is also inside the arc) will remain unchanged.
            // So, the pointer angle is inside the arc if
            // pointerAngle >= startAngle (pointer angle is below zero, but above or equal to the starting angle --> -10 >= -30 becomes 350 >= 330 ) or if
            // pointerAngle <= endAngle (pointer angle is above or equal to zero, but below or equal to the end angle - 20 < 30 )
            // It's all a matter of reference frames and converting between them. :)
            
        }
        else // For cases like -60,-30 or 30,60, with angles that have the same sign, we simply check if Start < needle < End
        {
            if ( ( angle >= processedStartAngle) && ( angle <= processedEndAngle) )
            {
                isInsideArc = true;
                arcType = 2;
            }
        }

        // Calculate the new gauge value based on the pointer position, if needed.
        if ( isInsideArc )
        {
            int semiArcLength = 0;

            if ( arcType == 2 ) // The angles have the same sign.
            {
                if ( initialArcFollowsTrigonometricDirection )
                {
                    semiArcLength = Math.abs ( angle - processedStartAngle );
                }
                else
                {
                    semiArcLength = Math.abs ( angle - processedEndAngle );
                }
                newGaugeValue = ( gauge.getMaxValue() * semiArcLength ) / totalArcLength ;
            }
            else // The angles have different signs. 
            {               
                if ( angle >= processedStartAngle ) // If the needle is "below zero"
                {
                    if ( initialArcFollowsTrigonometricDirection )
                    {
                        semiArcLength = Math.abs ( angle - processedStartAngle);
                    }
                    else
                    {
                        semiArcLength = totalArcLength - Math.abs ( angle - processedStartAngle);
                    }
                }
                else // If the needle is "above zero"
                {
                    if ( initialArcFollowsTrigonometricDirection )
                    {
                        semiArcLength = totalArcLength - Math.abs ( angle - processedEndAngle);
                    }
                    else
                    {
                        semiArcLength = Math.abs ( angle - processedEndAngle);
                    }
                }
                newGaugeValue = ( gauge.getMaxValue() * semiArcLength ) / totalArcLength ;
            }
            
            gauge.setValue(newGaugeValue);
        }

        
    }

    public boolean handlePointerDragged(int x, int y)
    {
        if ( gauge.isInteractive())
        {
            valueBasedOnPointerPosition(x, y);
            return true;
        }
        return false;
    }

    public boolean handlePointerPressed(int x, int y)
    {
        if ( gauge.isInteractive())
        {
            valueBasedOnPointerPosition(x, y);
            return true;

        }
        return false;
    }

    public boolean handlePointerReleased(int x, int y)
    {
        if ( gauge.isInteractive())
        {
            valueBasedOnPointerPosition(x, y);
            return true;
        }
        return false;
    }

}
