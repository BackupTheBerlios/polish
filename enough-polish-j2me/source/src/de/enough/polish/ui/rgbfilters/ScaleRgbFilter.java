//#condition polish.usePolishGui
/*
 * Created on Jul 8, 2008 at 5:10:39 PM.
 * 
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
package de.enough.polish.ui.rgbfilters;

//#debug ovidiu
import de.enough.polish.benchmark.Benchmark;

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ImageUtil;
import de.enough.polish.util.RgbImage;

/**
 * <p>Transforms the size of an RGB image.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Nagendra Sharma, nagendra@prompttechnologies.net
 */
public class ScaleRgbFilter extends RgbFilter
{
	protected Dimension scaling;
	protected transient RgbImage output;
        protected boolean keepOriginalImageDimensions = true ;
	/**
	 * Creates a new grayscale filter
	 */
	public ScaleRgbFilter()
	{
		// just create a new instance
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#isActive()
	 */
	public boolean isActive()
	{
		boolean isActive = false;
		if (this.scaling != null) {
			isActive = (this.scaling.getValue(100) != 100);
		}
		return isActive;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
	 */
	public RgbImage process(RgbImage input)
	{
		if (!isActive()) {
			return input;
		}

		int width = input.getWidth();
		int height = input.getHeight();
		int scalePercent=this.scaling.getValue(100);
		int[] rgbOutput;
                int[] rgbInput = input.getRgbData();
                
                if ( keepOriginalImageDimensions == true )
                {
                    if (this.output == null || this.output.getWidth() != width || this.output.getHeight() != height ) {
                            this.output = new RgbImage( width, height );
                    }
                    else if (scalePercent < 100 )
                    {
                            this.output = new RgbImage( width, height );
                    }

                    rgbOutput = this.output.getRgbData();

                    ImageUtil.scale(scalePercent, width, height, rgbInput, rgbOutput);
                }
                else
                {
                    int newWidth = (width * scalePercent) / 100 ;
                    int newHeight = ( height * scalePercent ) / 100;
                    this.output = new RgbImage ( ImageUtil.scale(rgbInput, newWidth, newHeight, width, height), newWidth);

                    // TODO :
                    // Add code to call for a layout refresh, to accomodate for the new image size .
                }
                
		return this.output;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style, boolean)
	 */
	public void setStyle(Style style, boolean resetStyle)
	{
		super.setStyle(style, resetStyle);

                //#if polish.css.filter-scale-crop
                Boolean value = style.getBooleanProperty("filter-scale-crop");
                if ( value != null)
                {
                        keepOriginalImageDimensions = value.booleanValue() ;
                }
                else
                {
                    keepOriginalImageDimensions = true ;
                }
                //#endif
                
		//#if polish.css.filter-scale-grade
			Dimension scalingInt = (Dimension) style.getObjectProperty("filter-scale-grade");
			if (scalingInt != null) {
				this.scaling = scalingInt;
				
			}
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.RgbFilter#releaseResources()
	 */
	public void releaseResources()
	{
		this.output = null;
	}

}