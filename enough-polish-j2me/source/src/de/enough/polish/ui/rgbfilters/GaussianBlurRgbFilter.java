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
import com.ovidiuiliescu.*;

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.RgbFilter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.RgbImage;

/**
 * <p>
 * Blurs an image.
 * </p>
 * 
 * <p>
 * Copyright Enough Software 2008
 * </p>
 * 
 * @author Ovidiu Iliescu
 * @author Nagendra Sharma, nagendra@prompttechnologies.net
 * @author Robert Virkus, j2mepolish@enough.de (blur animation & fixes)
 */
public class GaussianBlurRgbFilter extends RgbFilter {
	protected Dimension blur;
	protected transient RgbImage output;
	int width, height;

	/**
	 * Creates a new GaussianBlurRgb Filter
	 */
	public GaussianBlurRgbFilter() {

		// just create a new instance
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.RgbFilter#isActive()
	 */
	public boolean isActive() {
		if (this.blur == null) {
			return false;
		}
		return (this.blur.getValue(255) != 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.enough.polish.ui.RgbFilter#process(de.enough.polish.util.RgbImage)
	 */
	public RgbImage process(RgbImage input) {
		if (!isActive()) {
			return input;
		}
		//#debug ovidiu
		Timer.startTimer(2);

		this.width = input.getWidth();
		this.height = input.getHeight();
		if (this.output == null || this.output.getWidth() != input.getWidth()
				|| this.output.getHeight() != input.getHeight()) {
			this.output = new RgbImage(input.getWidth(), input.getHeight());
		}
		int[] rgbInput = input.getRgbData();
		int[] rgbOutput = this.output.getRgbData();

		int dimension = this.blur.getValue(100) * 3;
		int outerPercentage = dimension % 100;
		dimension /= 100;
		if (outerPercentage == 0) {
			outerPercentage = 100;
		} else {
			dimension++;
		}
		
		//#debug ovidiu
		Timer.startTimer(4);

		// Define all variables here instead of inside the loops.
		int sourcePixel = 0;
		int red = 0;
		int green = 0;
		int blue = 0;
		int totalPercentage = 100;
		int startY = 0;
		int endY = 0;
		int x = 0, y = 0, dx = 0, dy = 0, startX = 0, endX, c = 0, percentage = 0;
		int yTimesWidth = 0;
		int dyTimesWidth = 0;
		int width = this.width;
		int height = this.height;

		/*
		 * Gaussian Blur is linearly separable, so let's take advantage of this
		 * by doing first a vertical pass and then a horizontal pass.
		 * 
		 * Basically :
		 * 
		 * FullGauss (image,d*d) = HorizGauss(VertGauss(image,d),d)
		 *  
		 * Massive speed improvement obtained because of this, since instead of
		 * width*height*dimension*dimension pixels we only process 2*width*height*dimension
		 * pixels.
		 * 
		 * Also heavily optimized the loops by moving the inner "if"s one level up
		 * and using pre-calculation and bit shifts wherever possible.
		 * 
		 * Because of bit shifts, some values (like totalPercentage) were refactored
		 * to base-2 numbers (128 instead of 100). The math has been modified to take
		 * this into consideration, however very small visual differences might occur
		 * because of this. These differences are, for all intents and purposes unnoticeable.
		 * 
		 */

		// VERTICAL PASS
		for (y = -1; ++y < height;) {
			for (x = -1; ++x < width;) {
				
				sourcePixel = rgbInput[x + yTimesWidth];
				red = (sourcePixel & 0x00ff0000) >> 16;
				green = (sourcePixel & 0x0000ff00) >> 8;
				blue = sourcePixel & 0x000000ff;
				
				totalPercentage = 128;
				startY = y - dimension;
				endY = y + dimension;

				if (startY < 0) {
					startY = 0;
				}
				if (endY >= height) {
					endY = height - 1;
				}

				dyTimesWidth = startY * width;
				for (dy = startY - 1; ++dy <= endY;) {

					c = rgbInput[x + dyTimesWidth];
					percentage = c >>> 25;

					red += (((c & 0x00ff0000) >> 16) * percentage) >> 7;
					green += (((c & 0x0000ff00) >> 8) * percentage) >> 7;
					blue += ((c & 0x000000ff) * percentage) >> 7;

					totalPercentage += percentage;

					dyTimesWidth += width;
				}

				red = ((red << 7) / totalPercentage) << 16;
				green = ((green << 7) / totalPercentage) << 8;
				blue = ((blue << 7) / totalPercentage);

				rgbOutput[x + yTimesWidth] = (sourcePixel & 0xff000000) | red | green | blue;

			}
			yTimesWidth += width;
		}

		// HORIZONTAL PASS
		yTimesWidth = 0;
		dyTimesWidth = 0;

		for (y = -1; ++y < height;) {
			for (x = -1; ++x < width;) {
				
				sourcePixel = rgbOutput[x + yTimesWidth];
				red = (sourcePixel & 0x00ff0000) >> 16;
				green = (sourcePixel & 0x0000ff00) >> 8;
				blue = sourcePixel & 0x000000ff;
				totalPercentage = 128;

				startX = x - dimension;
				endX = x + dimension;

				if (startX < 0) {
					startX = 0;
				}
				if (endX >= width) {
					endX = width - 1;
				}

				for (dx = startX - 1; ++dx <= endX;) {

					c = rgbOutput[dx + yTimesWidth];
					percentage = c >>> 25;

					red += (((c & 0x00ff0000) >> 16) * percentage) >> 7;
					green += (((c & 0x0000ff00) >> 8) * percentage) >> 7;
					blue += ((c & 0x000000ff) * percentage) >> 7;

					totalPercentage += percentage;
				}

				red = ((red << 7) / totalPercentage) << 16;
				green = ((green << 7) / totalPercentage) << 8;
				blue = ((blue << 7) / totalPercentage);

				rgbOutput[x + yTimesWidth] = (sourcePixel & 0xff000000) | red | green | blue;

			}

			yTimesWidth += width; 
		}

		//#debug ovidiu
		Timer.pauseTimer(4);

		//#mdebug ovidiu
		Timer.pauseTimer(2);
		Timer.incrementTimer(3);
		Timer.check(15000);
		//#enddebug

		return this.output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.RgbFilter#setStyle(de.enough.polish.ui.Style,
	 * boolean)
	 */
	public void setStyle(Style style, boolean resetStyle) {

		super.setStyle(style, resetStyle);
		// #if polish.css.filter-blur-grade
		Dimension blurInt = (Dimension) style.getObjectProperty("filter-blur-grade");
		if (blurInt != null) { 
			this.blur = blurInt;
		}
		// #endif

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.enough.polish.ui.RgbFilter#releaseResources()
	 */
	public void releaseResources() {
		this.output = null;
	}

}
