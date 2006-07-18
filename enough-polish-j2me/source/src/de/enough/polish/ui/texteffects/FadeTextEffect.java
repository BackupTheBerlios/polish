/**
 * 
 */
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * This is a simple fade in/out effect, which supports several parameters. Namely:
 *    time to fade
 *    time to stay faded in
 *    time to stay faded out
 *    colors of (faded in and faded out)
 *    and a delay until the effect starts
 *    
 * @author Simon Schmitt
 *
 */
public class FadeTextEffect extends TextEffect {
	
	private DrawUtil.FadeUtil fader=new DrawUtil.FadeUtil();
	
	private String lastText;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) {
		
		// intialisation
		if (this.lastText!=text || text==null){ // TODO: check for other changes
			this.fader.changed=true;
			this.lastText=text;
		}
		// do not draw if you do not know the color
		if (this.fader.changed){
			this.fader.step();
			//return;
		}
		
		if (text==null) {
			return;
		}
		// draw the current state
		g.setColor(this.fader.cColor);
		g.drawString(text,x,y, orientation);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		
		if (this.lastText == null) {
			return animated;
		}
		
		return this.fader.step() | animated;
		
	}
	
	public void setStyle(Style style) {
		super.setStyle(style);
		
		//#if polish.css.text-fade-out-color
			Color startColorObj = style.getColorProperty( "text-fade-out-color" );
			if (startColorObj != null) {
				this.fader.startColor = startColorObj.getColor();
			}
		//#endif
		this.fader.endColor = style.getFontColor();
		//#if polish.css.text-fade-in-color
			Color endColorObj = style.getColorProperty( "text-fade-in-color" );
			if (endColorObj != null) {
				this.fader.endColor = endColorObj.getColor();
			}
		//#endif
		//#if polish.css.text-fade-delay
			Integer delayInt = style.getIntProperty("text-fade-delay");
			if (delayInt != null ) {
				this.fader.delay = delayInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-steps
			Integer fadeTimeInt = style.getIntProperty("text-fade-steps");
			if (fadeTimeInt != null ) {
				this.fader.stepsIn = fadeTimeInt.intValue();
				this.fader.stepsOut=this.fader.stepsIn;
			}
		//#endif
		//#if polish.css.text-fade-duration-in
			Integer diInt = style.getIntProperty("text-fade-duration-in");
			if (diInt != null ) {
				this.fader.sWaitTimeIn = diInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-duration-out
			Integer doInt = style.getIntProperty("text-fade-duration-out");
			if (doInt != null ) {
				this.fader.sWaitTimeOut = doInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-mode
			String tmpStr = style.getProperty("text-fade-mode");
			if (tmpStr!=null){
				if (tmpStr.equals("fadein")){
					this.fader.mode=this.fader.FADE_IN;
				} else if (tmpStr.equals("fadeout")){
					this.fader.mode=this.fader.FADE_OUT;
				} else {
					this.fader.mode=this.fader.FADE_LOOP;
				}
			}
		//#endif	
	}

}
