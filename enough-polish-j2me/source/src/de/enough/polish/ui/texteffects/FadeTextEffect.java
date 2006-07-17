/**
 * 
 */
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * @author Simon Schmitt
 *
 */
public class FadeTextEffect extends TextEffect {
	
	private final int FADE_IN =1;
	private final int FADE_OUT=2;
	private final int FADE_LOOP=3;
	private final int FADE_BREAK=0;
	
	private int[] gradient;
	private boolean changed;
	
	private int startColor	=0xFF000000;
	private int endColor	=0xFF00FF00;
	
	private int steps;
	private int delay=0; 				// time till the effect starts
	private int stepsIn=5,stepsOut=5;  	// fading duration
	private int sWaitTimeIn=10; 		// time to stay faded in
	private int sWaitTimeOut=0; 		// time to stay faded out
	private int mode=this.FADE_LOOP;
	
	private int cColor;
	private int cStep;
	
	private String lastText;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) {
		
		// intialisation
		if (this.lastText!=text || text==null){ // TODO: check for other changes
			this.changed=true;
			this.lastText=text;
		}
		// do not draw if you do not know the color
		if (this.changed){
			return;
		}
		
		if (text==null) {
			return;
		}
		// draw the current state
		g.setColor(this.cColor);
		g.drawString(text,x,y, orientation);
	}

	private void initialize(){
		//System.out.println(" init");

		this.cStep=0;
		
		switch (this.mode){
		case FADE_OUT:
			this.stepsIn=0;
			this.sWaitTimeIn=0;
			this.cColor=this.endColor;
			break;
		case FADE_IN:
			this.stepsOut=0;
			this.sWaitTimeOut=0;
			this.cColor=this.startColor;
			break;
		default://loop
			this.cColor=this.startColor;
		}

		this.cStep-=this.delay;
		
		this.steps= this.stepsIn+this.stepsOut+this.sWaitTimeIn+this.sWaitTimeOut;
		
		this.gradient = DrawUtil.getGradient(this.startColor,this.endColor,Math.max(this.stepsIn, this.stepsOut));

		
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		
		if (this.lastText == null) {
			return animated;
		}
		
		this.cStep++;
		
		// (re)define everything, if something changed 
		if (this.gradient==null | this.changed) {
			initialize();
		} 
		this.changed=false;
		
		// exit, if no animation is neccessary
		if (this.mode==this.FADE_BREAK){
			return false; // TODO false = animated?
		}
		// we have to ensure that a new picture is drawn
		if (this.cStep<0){
			return true;
		}
		
		// set counter to zero (in case of a loop) or stop the engine, when we reached the end
		if (this.cStep==this.steps){
			this.cStep=0;
			
			if (this.mode!=this.FADE_LOOP) {
				this.mode=this.FADE_BREAK;
				return true;
			}
		}
		
		//System.out.println("  fadeing "+this.cStep);
		
		if (this.cStep<this.stepsIn){	
			// fade in
			this.cColor=this.gradient[this.cStep];	
			//System.out.println("  [in] color:"+this.cStep);
			return true;
			
		} else if (this.cStep<this.stepsIn+this.sWaitTimeIn){
			// have a break
			if (this.cColor!=this.endColor){
				this.cColor=this.endColor;
				return true;
			}
			
			//System.out.println("  color:end color");
			
		} else if( this.cStep<this.stepsIn+this.sWaitTimeIn+this.stepsOut){ 
			// fade out 
			this.cColor=this.gradient[this.stepsIn+this.sWaitTimeIn+this.stepsOut-this.cStep-1];
			//System.out.println("  [out] color:"+(this.stepsIn+this.sWaitTimeIn+this.stepsOut-this.cStep-1));
			return true;
			
		} else { 
			// have another break
			if (this.cColor!=this.startColor){
				this.cColor=this.startColor;
				return true;
			}
			//System.out.println("  color:start color");
		}
		
		// it sees as if we had no change...
		return animated;
	}
	
	public void setStyle(Style style) {
		super.setStyle(style);
		
		//#if polish.css.text-fade-out-color
			Color startColorObj = style.getColorProperty( "text-fade-out-color" );
			if (startColorObj != null) {
				this.startColor = startColorObj.getColor();
			}
		//#endif
		this.endColor = style.getFontColor();
		//#if polish.css.text-fade-in-color
			Color endColorObj = style.getColorProperty( "text-fade-in-color" );
			if (endColorObj != null) {
				this.endColor = endColorObj.getColor();
			}
		//#endif
		//#if polish.css.text-fade-delay
			Integer delayInt = style.getIntProperty("text-fade-delay");
			if (delayInt != null ) {
				this.delay = delayInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-steps
			Integer fadeTimeInt = style.getIntProperty("text-fade-steps");
			if (fadeTimeInt != null ) {
				this.stepsIn = fadeTimeInt.intValue();
				this.stepsOut=this.stepsIn;
			}
		//#endif
		//#if polish.css.text-fade-duration-in
			Integer diInt = style.getIntProperty("text-fade-duration-in");
			if (diInt != null ) {
				this.sWaitTimeIn = diInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-duration-out
			Integer doInt = style.getIntProperty("text-fade-duration-out");
			if (doInt != null ) {
				this.sWaitTimeOut = doInt.intValue();
			}
		//#endif
		//#if polish.css.text-fade-mode
			String tmpStr = style.getProperty("text-fade-mode");
			if (tmpStr!=null){
				if (tmpStr.equals("fadein")){
					this.mode=this.FADE_IN;
				} else if (tmpStr.equals("fadeout")){
					this.mode=this.FADE_OUT;
				} else {
					this.mode=this.FADE_LOOP;
				}
			}
		//#endif	
	}

}
