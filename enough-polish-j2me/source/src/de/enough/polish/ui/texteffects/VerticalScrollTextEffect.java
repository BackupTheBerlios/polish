//#condition polish.usePolishGui
/*
 * Created on 31-Mar-2009 at tea time.
 * 
 * Copyright (c) 2009 Andre Schmidt / Enough Software
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
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.AnimationThread;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.CommandItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;

/**
 * <p>A text effect that scrolls through the wrapped lines</p>
 *
 * <p>Copyright (c) Enough Software 2005 - 2009</p>
 * <pre>
 * history
 *        31-Mar-2009 - asc creation
 * </pre>
 * @author Andre Schmidt, j2mepolish@enough.de
 */
public class VerticalScrollTextEffect extends TextEffect{

	//transient Item parent;

	public static final int STAGE_SHOW = 0x00;
	public static final int STAGE_SCROLL = 0x01;
	
	int stageInterval = 2000;
	
	class Data
	{
		public int lineHeight = 0;
		
		public String[] textLines = null;
		public String[] firstLine = null;
		
		public String[] drawLines = new String[2];
		
		public long stageTime = 0;
		public int stageCurrent = STAGE_SHOW;
		
		public int lineIndex = 0;
		public int lineOffset = 0;
		
		public int lastLineWidth = -1;
	}
	
	public void onAttach(Item parent) {
		setData(parent, new Data());
		AnimationThread.addAnimationItem(parent);
	}

	public void onDetach(Item parent) {
		AnimationThread.removeAnimationItem(parent);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate(de.enough.polish.ui.Item, long, de.enough.polish.ui.ClippingRegion)
	 */
	public void animate(Item parent, long currentTime,
			ClippingRegion repaintRegion) {
		super.animate(parent, currentTime, repaintRegion);
		Data data = (Data)getData(parent);
		if(data.textLines != null && data.textLines.length == 1)
		{
			// there's only one line to draw so the parent item
			// can be removed from the AnimationThread
			AnimationThread.removeAnimationItem(parent);
			return;
		}
		
		boolean addRepaintRegion = false;
		if (data.stageTime == 0) {
			data.stageTime = currentTime;
			addRepaintRegion = true;
		}
		
		// dirty hack
		if(parent instanceof CommandItem)
		{
			CommandItem item = (CommandItem)parent;
			if(item.isOpen())
			{
				data.lineOffset = 0;
				data.stageCurrent = STAGE_SHOW;
				return;
			}
		}
		
		// get the time passed since last animation
		long timePassed = currentTime - data.stageTime;
		
		switch(data.stageCurrent)
		{
			case STAGE_SHOW : 
				if (timePassed > this.stageInterval) {
					//#debug debug
					System.out.println("stage changed to STAGE_SCROLL");

					data.stageCurrent = STAGE_SCROLL;
					data.stageTime = currentTime;
					addRepaintRegion = true;
				}
				break;
			case STAGE_SCROLL : 
				data.lineOffset = getLineOffset(data,timePassed, data.lineHeight );
				addRepaintRegion = true;
				// if the interval time has passed ... 
				if (timePassed > this.stageInterval) {
					//#debug debug
					System.out.println("stage change to STAGE_SHOW");
					
					data.lineIndex = (data.lineIndex + 1) % data.textLines.length;
					data.lineOffset = 0;
					
					data.stageCurrent = STAGE_SHOW;
					data.stageTime = currentTime;
				}
				break;
		};
		
		if (addRepaintRegion) {
			parent.addRepaintArea(repaintRegion);
		}
	}
	
	/**
	 * Calculates and returns the offset to draw the textlines
	 * for the animation  
	 * @param timePassed the passed time since the last animation
	 * @param lineHeight the line height
	 * @return the offset
	 */
	public int getLineOffset(Data data, long timePassed, int lineHeight)
	{
		int progress = (int)(timePassed * 1000 / this.stageInterval) * 100 / 1000;
		return (lineHeight * 1000 / 100) * progress / 1000;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawStrings(java.lang.String[], int, int, int, int, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawStrings(Item parent, String[] textLines, int textColor, int x, int y,
			int leftBorder, int rightBorder, int lineHeight, int maxWidth,
			int layout, Graphics g) {
		Data data = (Data)getData(parent);
		System.out.println(data.textLines[data.textLines.length - 1]);
		
		data.lineHeight = lineHeight;
		
		data.drawLines[0] = data.textLines[data.lineIndex];
		data.drawLines[1] = data.textLines[(data.lineIndex + 1) % data.textLines.length];
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();
		
		//#if polish.Bugs.needsBottomOrientiationForStringDrawing
			g.setClip(x, y + lineHeight, maxWidth, lineHeight);
		//#else
			g.setClip(x, y, maxWidth, lineHeight);
		//#endif
		
		leftBorder = x;
		
		super.drawStrings(data.drawLines, textColor, x, y - data.lineOffset, leftBorder, rightBorder,
				lineHeight, maxWidth, layout, g);
		
		g.setClip(clipX, clipY, clipWidth, clipHeight);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#wrap(java.lang.String, int, javax.microedition.lcdui.Font, int, int)
	 */
	public String[] wrap(Item parent, String text, int textColor, Font font,
			int firstLineWidth, int lineWidth) {
		Data data = (Data)getData(parent);
		if(data.firstLine == null || data.lastLineWidth != firstLineWidth)
		{
			data.textLines = super.wrap(text, textColor, font, firstLineWidth, lineWidth);
			data.firstLine = new String[]{data.textLines[0]};
			data.lastLineWidth = firstLineWidth;
		}
		
		return data.firstLine;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int anchor, Graphics g) {
		g.setColor(textColor);
		g.drawString(text, x, y, anchor);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#setStyle(de.enough.polish.ui.Style)
	 */
	public void setStyle(Style style) {
		super.setStyle(style);
		
		//#if polish.css.vertical-scroll-interval
		Integer stageIntervalInt = style.getIntProperty("vertical-scroll-interval");
		if(stageIntervalInt != null)
		{
			this.stageInterval = stageIntervalInt.intValue();
		}
		//#endif
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#getMaxWidth(java.lang.String[])
	 */
	public int getMaxWidth(Item parent, String[] lines) {
		Data data = (Data)getData(parent);
		
		return super.getMaxWidth(parent, data.textLines);
	}
	
	
	
}
