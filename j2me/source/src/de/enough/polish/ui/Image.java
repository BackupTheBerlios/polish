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

import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides an abstraction for platform-independent Images.
 *
 * @author Ovidiu Iliescu
 */
public class Image
{
    protected javax.microedition.lcdui.Image image = null ;

    protected Image()
    {
        
    }
    

    public static Image createImage(byte[] imageData, int imageOffset, int imageLength)
    {
        javax.microedition.lcdui.Image thisImage = javax.microedition.lcdui.Image.createImage(imageData,imageOffset,imageLength);
        Image result = new Image();
        result.image = thisImage ;
        return result;
    }

    public static Image createImage(Image img, int x, int y, int width, int height, int transform)
    {
        Image result = new Image();
        result.image = javax.microedition.lcdui.Image.createImage(img.image, x, y, width, height, transform) ;
        return result;
    }

    public static Image createImage(Image source)
    {
        Image result = new Image();
        result.image = javax.microedition.lcdui.Image.createImage(source.image) ;
        return result;
    }

    public static Image createImage(InputStream stream) throws IOException
    {
        Image result = new Image();
        result.image = javax.microedition.lcdui.Image.createImage(stream) ;
        return result;
    }

    public static Image createImage(int width, int height)
    {
        Image result = new Image();
        result.image = javax.microedition.lcdui.Image.createImage(width,height) ;
        return result;
    }

    public static Image createImage(String name) throws IOException
    {
        Image result = new Image();
        result.image = javax.microedition.lcdui.Image.createImage(name) ;
        return result;
    }

    public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha)
    {
        Image result = new Image();
        result.image = javax.microedition.lcdui.Image.createRGBImage(rgb,width,height,processAlpha) ;
        return result;
    }

    public Graphics getGraphics ()
    {
        if ( image != null )
        {
            return new Graphics(image.getGraphics());
        }
        else
        {
            return null;
        }
    }

    public int getWidth()
    {
        if ( image == null )
        {
            return -1;
        }
        else
        {
            return image.getWidth();
        }
    }

    public int getHeight()
    {
        if ( image == null )
        {
            return -1;
        }
        else
        {
            return image.getHeight();
        }
    }

    public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)
    {
        if ( image != null )
        {
            image.getRGB(rgbData, offset, scanlength, x, y, width, height);
        }

    }

    public boolean isMutable()
    {
        if (image != null)
        {
            return image.isMutable();
        }
        else
        {
            return false;
        }

    }

    // Extra methods
    public int[] getRgbData ()
    {
        int data[] = new int [ image.getWidth() * image.getHeight() ];
        image.getRGB(data, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight() );
        return data;
    }


}
