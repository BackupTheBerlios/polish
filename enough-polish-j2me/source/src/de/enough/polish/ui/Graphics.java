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

package de.enough.polish.ui;

import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

/**
 *
 * This class provides an abstraction for platform-independent Graphics objects.
 * 
 * @author Ovidiu Iliescu
 */
public class Graphics {

    protected javax.microedition.lcdui.Graphics graphics = null;

    protected RgbImage bufferedImage = null ;

    public static int BASELINE = javax.microedition.lcdui.Graphics.BASELINE;
    public static int BOTTOM = javax.microedition.lcdui.Graphics.BOTTOM;
    public static int TOP = javax.microedition.lcdui.Graphics.TOP;
    public static int LEFT = javax.microedition.lcdui.Graphics.LEFT;
    public static int RIGHT = javax.microedition.lcdui.Graphics.RIGHT;
    public static int DOTTED = javax.microedition.lcdui.Graphics.DOTTED;
    public static int HCENTER = javax.microedition.lcdui.Graphics.HCENTER ;
    public static int VCENTER = javax.microedition.lcdui.Graphics.VCENTER ;
    public static int SOLID = javax.microedition.lcdui.Graphics.SOLID ;

    public Graphics( javax.microedition.lcdui.Graphics g)
    {
        graphics = g;
    }

    public void drawImage(Image img, int x, int y, int anchor)
    {
        graphics.drawImage(img.image, x, y, anchor);
    }

    public void drawRotatedImage( Image image, int imageCenterX, int imageCenterY, int targetCenterX, int targetCenterY, int degrees )
    {

        // Rotate the image around its center
        RgbImage img = new RgbImage(image.getRgbData(), image.getWidth());
        degrees = - ( degrees - 90 ); // We use the correct trigonometric sense, and we consider 0 degrees to be at 3 o'clock.
        ImageUtil.rotate(img, degrees);

        // Calculate the cos and sin of the rotation angle
        double degreeCos = Math.cos(Math.PI * degrees / 180);
        double degreeSin = Math.sin(Math.PI * degrees / 180);

        // Calculate the delta between the image center and the image rotation point
        int centerXDelta = imageCenterX - image.getWidth()/2;
        int centerYDelta = imageCenterY - image.getHeight()/2;

        // Calculate the coordinates of the rotation point after the image has been rotated,
        // with respect to the image's center
        int newCenterX = (int) (centerXDelta * degreeCos - centerYDelta * degreeSin );
        int newCenterY = (int) (centerXDelta * degreeSin + centerYDelta * degreeCos );

        // Calculate the coordinates of the top-left corner of the rotated image
        int posX = targetCenterX - img.getWidth()/2 ;
        int posY = targetCenterY - img.getHeight()/2 ;

        // Apply the necessary offset so that the rotated image's rotation point
        // falls at the specfied point on the Graphics object and draw the rotated image
        img.paint (posX - newCenterX, posY - newCenterY, graphics);

    }

}
