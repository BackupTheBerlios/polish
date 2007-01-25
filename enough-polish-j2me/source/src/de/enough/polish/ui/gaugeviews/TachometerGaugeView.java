/*
 * Created on Jan 23, 2007 at 3:42:30 PM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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



import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.Style;

/**
 * <p>Shows a tachometer visualization of a Gauge.</p>
 * <p>
 *    The tachometer has 3 different sections: too low, normal, too high which can be customized by setting colors and the areas.
 *    It's also possible to specify a start value and to simulate floating point support by specifying a divide factor.
 *    <br />
 *    Supported attributes:
 * <p>
 * <ul>
 * 	<li><b>gauge-tachometer-startvalue</b>: the lowest possible value, default is 0</li>
 * 	<li><b>gauge-tachometer-section1-start</b>: the value at which the first section starts, default is the startvalue (0).</li>
 * 	<li><b>gauge-tachometer-section2-start</b>: the value at which the second section starts, default is the value range divided by 3.</li>
 * 	<li><b>gauge-tachometer-section3-start</b>: the value at which the third section starts, default is the value range multiplied by 2/3.</li>
 * 	<li><b>gauge-tachometer-section1-color</b>: the color of the first section</li>
 * 	<li><b>gauge-tachometer-section2-color</b>: the color of the second section</li>
 * 	<li><b>gauge-tachometer-section3-color</b>: the color of the third section</li>
 * 	<li><b>gauge-tachometer-clockface-color</b>: the outer color of the tachometer, defaults to black</li>
 * 	<li><b>gauge-tachometer-needle-color</b>: the color of the tachometer needle/pointer/indicator, defaults to red.</li>
 * 	<li><b>gauge-tachometer-factor</b>: an integer factor by which all given values are divided for simulating floating point. A factor of 10 and a value of 14 will result in a shown value of 1.4, for example.</li>
 * </ul>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Jan 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @author Tim Muders
 */
public class TachometerGaugeView extends ItemView {

	private int startValue;
	private int maxValue; // the value range is between 0..maxValue
	private int factor = 1; // the factor by which the values should be divided for simulating floating point
	
	private int section1Start;
	private int section2Start;
	private int section3Start;
	private int section1Color = -1; // -1 means this section is not visible
	private int section2Color = -1; // -1 means this section is not visible
	private int section3Color = -1; // -1 means this section is not visible
	
	private int clockfaceColor = 0x000000; // black
	private int innerColor = 0xFFFFFF; // white
	private int needleColor = 0xFF0000; // red

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#initContent(de.enough.polish.ui.Item, int, int)
	 */
	protected void initContent(Item parent, int firstLineWidth, int lineWidth) {
		Gauge gauge = (Gauge) parent;
		this.maxValue = gauge.getMaxValue(); 
		
		int range = this.maxValue - this.startValue;
		if (this.section2Start == 0) {
			this.section2Start = range / 3;
		}
		if (this.section3Start == 0) {
			this.section3Start = (range * 2) / 3;
		}
		this.contentWidth = ( lineWidth * 2 ) / 3;
		System.out.println("firstline:"+firstLineWidth+";lineWidth:"+lineWidth+";contentWidth:"+this.contentWidth);
		this.contentHeight = this.contentWidth;
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#paintContent(de.enough.polish.ui.Item, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) 
	{
		// draw sections:
		// (to be done)
		if (this.section1Color != -1) {
			// ...
		}
		int itemWidth = parent.itemWidth;
		int itemHeight = parent.itemWidth;
		int widthLine = x + itemWidth;
		int heightLine = y + itemHeight;
		System.out.println("itemHeight: "+parent.itemHeight+"; itemWidth: "+parent.itemWidth);
		// draw outer area
		// (to be done)
		Gauge gauge = (Gauge)parent;
		int centerX = x + itemWidth / 2;
		int centerY = y + itemWidth / 2;
		int innerCircleRadius = this.contentWidth / 10;
		
		System.out.println("x:"+x+";y:"+y+";centerX:"+centerX+";centerY:"+centerY+";innerCircleRadius:"+innerCircleRadius);
		// draw inner circle:
		g.setColor( this.clockfaceColor );
		g.drawRect(x, y, itemWidth, itemHeight);
		g.drawArc(centerX - (innerCircleRadius ), centerY- (innerCircleRadius ), innerCircleRadius * 2, innerCircleRadius * 2, 0, 360 );
		g.drawArc(x , y, itemWidth, itemHeight, 0, 360 );
		int innerStartX = x + 10;
		int innerStartY = y + 10;
		int innerWidth = itemWidth - 20;
		int innerHeight = itemHeight - 20;
		int pointerLength = innerWidth /2;
		System.out.println("pointerLength "+pointerLength);
		g.drawArc(innerStartX, innerStartY, innerWidth , innerHeight , 315, 270 );
		Font font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN,Font.SIZE_SMALL);
		int startValueStringWidth = font.stringWidth(""+this.startValue);
		int maxValueStringWidth = font.stringWidth(""+this.maxValue);
		int gaugeValueStringWidth = font.stringWidth(""+gauge.getValue());
		System.out.println("startValueWidth:"+startValueStringWidth);
		System.out.println("maxValueWidth:"+maxValueStringWidth);
		g.setFont(font);
		g.drawString(""+this.startValue, x + 20 + startValueStringWidth, itemHeight, 0);
		g.drawString(""+this.maxValue, itemWidth - 20 - maxValueStringWidth, itemHeight, 0);
		g.drawString(""+gauge.getValue(), centerX - gaugeValueStringWidth /2 , itemHeight + 15, 0);
		g.setColor( this.needleColor );
		int value = gauge.getValue();
		double valuePercent = ((double)value / (double)maxValue)*100 ;
		System.out.println("(value / maxValue)"+((double)value / (double)maxValue));
		int degree ;
		if(valuePercent >= 0 && valuePercent < 75){
			degree = (int) (225 - (valuePercent*3));
		}else if (valuePercent > 75){
			degree = (int) (360 - ((valuePercent-75)*2));
		}else{
			degree = 0;
		}
		System.out.println("maxValue "+maxValue);
		System.out.println("valuePercent "+valuePercent);
		System.out.println("degree "+degree);
//		if(value < 75){
//			degree = 315 - (value * 3); 
//		}
//		else if(value > 75 ){
//			degree = (value * 3) + 45;
//		}
		double degreeCos = Math.cos(Math.PI*degree/180);
		double degreeSin = Math.sin(Math.PI*degree/180);
		System.out.println("degreeCos "+degreeCos+" degreeSin "+degreeSin*100);
		int angleCos = (int)( degreeCos * pointerLength);
		int angleSin = (int)( degreeSin * pointerLength);
		System.out.println("angleCos "+angleCos+" angleSin "+angleSin);
		System.out.println("degree:"+degree);
		int newX = (angleCos / pointerLength)*100;
		int newY = (angleSin / pointerLength)*100;
//		g.drawLine(x, y, newX, newY);
		System.out.println("newX "+newX+" newY "+newY);
		newX = centerX + (angleCos);
		newY = centerY + (-angleSin);
		
		System.out.println("degree "+degree+" newX "+newX+" newY "+newY+" centerX "+centerX+" centerY"+centerY);
		g.drawLine( centerX, centerY, newX , newY);
//		if( value < 25 ){
//			int sum = ((heightLine - centerY)*100)/value;
//			System.out.println("sum "+sum);
//			g.drawLine( centerX, centerY, x , heightLine - sum);
//		}
//		g.drawLine( centerX, centerY, widthLine  , heightLine);
//		g.drawLine( centerX, centerY, x , heightLine);
//		g.drawLine( centerX, centerY, widthLine, centerY);
//		g.drawLine( centerX, centerY, centerX-((widthLine)/2) , centerY);
//		g.drawLine( centerX, centerY, centerX , y);
//		g.drawLine( centerX, centerY, centerX ,  heightLine);
		// draw outer circle:
		// (to be done)
		// draw needle:
		// (to be done)
	
		}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ItemView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		
		//#if polish.csss.gauge-tachometer-startvalue
			Integer startValueInt = style.getIntProperty("gauge-tachometer-startvalue");
			if (startValueInt != null) {
				this.startValue = startValueInt.intValue();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section1-start
			Integer section1StartInt = style.getIntProperty("gauge-tachometer-section1-start");
			if (section1StartInt != null) {
				this.section1Start = section1StartInt.intValue();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section2-start
			Integer section2StartInt = style.getIntProperty("gauge-tachometer-section2-start");
			if (section2StartInt != null) {
				this.section2Start = section2StartInt.intValue();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section3-start
			Integer section3StartInt = style.getIntProperty("gauge-tachometer-section3-start");
			if (section3StartInt != null) {
				this.section3Start = section3StartInt.intValue();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section1-color
			Color section1ColorObj = style.getColorProperty("gauge-tachometer-section1-color");
			if (section1ColorObj != null) {
				this.section1Color = section1ColorObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section2-color
			Color section2ColorObj = style.getColorProperty("gauge-tachometer-section2-color");
			if (section2ColorObj != null) {
				this.section2Color = section2ColorObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-section3-color
			Color section3ColorObj = style.getColorProperty("gauge-tachometer-section3-color");
			if (section3ColorObj != null) {
				this.section3Color = section3ColorObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-inner-color
			Color colorInnerObj = style.getColorProperty("gauge-tachometer-inner-color");
			if (colorInnerObj != null) {
				this.innerColor = colorInnerObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-clockface-color
			Color colorOuterObj = style.getColorProperty("gauge-tachometer-clockface-color");
			if (colorOuterObj != null) {
				this.clockfaceColor = colorOuterObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-needle-color
			Color colorNeedleObj = style.getColorProperty("gauge-tachometer-needle-color");
			if (colorNeedleObj != null) {
				this.needleColor = colorNeedleObj.getColor();
			}
		//#endif
		//#if polish.css.gauge-tachometer-factor
			Integer factorInt = style.getIntProperty("gauge-tachometer-factor");
			if (factorInt != null) {
				this.factor = factorInt.intValue();
			}
		//#endif
	}
	
	

}
