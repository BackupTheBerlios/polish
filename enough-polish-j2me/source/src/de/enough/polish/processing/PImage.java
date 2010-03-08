//#condition polish.midp
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

package de.enough.polish.processing;

import javax.microedition.lcdui.Image;

/**
 * Placeholder class that emulates the behavior of a Mobile Processing PImage, for compatibility with existing Mobile Processing scripts.
 *
 * @author Ovidiu Iliescu
 */

public class PImage {

    private Image img = null ;
    
    public PImage(int width, int height)
    {
        img = Image.createImage(width, height);
    }

    public PImage(Image img)
    {
        this.img = img;
    }

    public PImage(byte[] data)
    {
        img = Image.createImage(data, 0, data.length);
    }

    public Image getImage()
    {
        return img;
    }

    public int getWidth()
    {
        return img.getWidth();
    }

    public int getHeight()
    {
        return img.getHeight();
    }

}
