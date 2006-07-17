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
	private int stepsIn=8,stepsOut=8; 
	private int sWaitTime=0; 
	private int mode=FADE_LOOP;
	
	private int cColor;
	private int cStep;
	
	private String lastText;
	
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
		//System.out.println("  drawing");
		// draw the current state
		g.setColor(cColor);
		g.drawString(text,x,y, Graphics.LEFT | Graphics.TOP);
	}

	private void initialize(){
		/*stepsIn = mode!=FADE_OUT ? fadeTime*1000/10:0; 
		stepsOut= mode!=FADE_IN ? fadeTime*1000/10:0; 
		sWaitTime=waitTime*1000/10;*/

		cStep=-1; // TODO schoener machen
		if (mode==FADE_OUT){
			cStep+=sWaitTime;
		}
		// TODO -=delay
		
		steps=stepsIn+stepsOut+sWaitTime*2;
		
		gradient = DrawUtil.getGradient(startColor,endColor,Math.max(stepsIn, stepsOut));
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
			
//			 set counter to zero (in case of a loop) or stop the engine, when we reached the end
			if (cStep==steps){
				cStep=0;
				cColor=endColor;
				if (mode!=FADE_LOOP) {
					mode=FADE_BREAK;
				}
			}
			
			System.out.println("  fadeing "+cStep);
			
			if (cStep<stepsIn){	// fade in
				cColor=gradient[cStep];	
				System.out.println("  [in] color:"+cStep);
				return true;
			} else if (cStep<stepsIn+sWaitTime){ // TODO ggf. kompensieren, wenn nur FADEOUT
				// have a break
				if (cColor!=endColor){
					cColor=endColor;
					return true;
				}
				
				System.out.println("  color:end color");
				
			} else if( cStep<stepsIn+sWaitTime+stepsOut){ // fade out 
				cColor=gradient[stepsIn+sWaitTime+stepsOut-cStep-1];
				System.out.println("  [out] color:"+(stepsIn+sWaitTime+stepsOut-cStep-1));
				return true;
			} else { 
				// another break
				if (cColor!=startColor){
					cColor=startColor;
					return true;
				}
				System.out.println("  color:start color");
			}
			
		}

		// we had no change
		return animated;
	}
}
