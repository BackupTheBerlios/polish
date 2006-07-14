/**
 * 
 */
package de.enough.polish.ui.texteffects;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.TextEffect;
import de.enough.polish.util.DrawUtil;

/**
 * @author Simon Schmitt
 *
 */
public class FadeTextEffect extends TextEffect {
	
	private final int FADE_IN=1;
	private final int FADE_OUT=2;
	private final int FADE_LOOP=3;
	private final int FADE_BREAK=0;
	
	private int[] gradient;
	
	private int startColor	=0x900000FF;
	private int endColor	=0xFF00FF00;
	
	private int steps;	// alle
	//private int delay=500; // warten am Anfang
	private int stepsIn=100,stepsOut=100; 
	private int sWaitTime=50; 
	private int mode=FADE_LOOP;
	
	private int cColor;
	private int cStep;
	
	private String lastText;
	private boolean changed;
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#drawString(java.lang.String, int, int, int, int, javax.microedition.lcdui.Graphics)
	 */
	public void drawString(String text, int textColor, int x, int y,
			int orientation, Graphics g) {
		
		// intialisation
		if (lastText!=text){
			//this.changed=true;
			this.lastText=text;
		}
		
		if (text==null) {
			return;
		}
		
		// draw the current state
		g.setColor(cColor);
		g.drawString(text,x,y, Graphics.LEFT | Graphics.TOP);
	}

	private void initialize(){
		/*stepsIn = mode!=FADE_OUT ? fadeTime*1000/10:0; 
		stepsOut= mode!=FADE_IN ? fadeTime*1000/10:0; 
		sWaitTime=waitTime*1000/10;*/

		cStep=0; // TODO schöner machen
		if (mode==FADE_OUT)
			cStep=sWaitTime;
		
		steps=stepsIn+stepsOut+sWaitTime*2;
		
		gradient = DrawUtil.getGradient(startColor,endColor,steps);
		cColor = ( (mode==FADE_IN | mode==FADE_LOOP) ? startColor : endColor); 
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.TextEffect#animate()
	 */
	public boolean animate() {
		boolean animated = super.animate();
		
		if (lastText == null) {
			return animated; 
		}
		// calculate the changes
		
		if (gradient==null) {	// intialize
			initialize();
		} else {
			
			if (mode!=FADE_BREAK) cStep++;
			
			if (cStep<stepsIn){	// fade in
				cColor=gradient[cStep];	
			} else if (cStep<stepsIn+sWaitTime){ // TODO ggf. kompensieren, wenn nur FADEOUT
				// have a break
				cColor=endColor;
			} else if( cStep<stepsIn+sWaitTime+stepsOut){ // fade out 
				cColor=gradient[steps-(stepsIn+sWaitTime+stepsOut-cStep)];
			} else { 
				// another break
				cColor=startColor;
			}
			
			// set counter to zero (in case of a loop) or stop the engine, when we reached the end
			if (cStep==steps){
				cStep=0;
				cColor=endColor;
				if (mode!=FADE_LOOP) {
					mode=FADE_BREAK;
				}
			}
			
		}

		// end if there were no real changes
		if (!this.changed){
			return animated;
		}
		// please redraw
		return true;
	}
}
