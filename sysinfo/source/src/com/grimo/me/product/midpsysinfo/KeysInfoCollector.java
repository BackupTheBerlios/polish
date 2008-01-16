/*
 * SoundInfoForm.java
 *
 * Created on 29 de junio de 2004, 19:34
 */

package com.grimo.me.product.midpsysinfo;

/**
 * Collects information about the game controls.
 * 
 * @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 * @author  Robert Virkus <j2mepolish@enough.de> (architectural changes)
 */

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import de.enough.sysinfo.MIDPSysInfoMIDlet;

/**
 * Collects information about the game controls.
 * 
 * @author  Waldemar Baraldi <waldemar.baraldi@grimo-software.com>
 * @author  Robert Virkus <j2mepolish@enough.de> (architectural changes)
 */
public class KeysInfoCollector extends InfoCollector
implements DynamicTestView
{
	private static final int STEP_LEFT_SOFT_KEY = 0; 
	private static final int STEP_RIGHT_SOFT_KEY = 1; 
	private static final int STEP_MIDDLE_SOFT_KEY = 2;
	private static final int STEP_CLEAR_KEY = 3; 
	private static final int STEP_RETURN_KEY = 4; 
	private static final int STEP_SELECT_KEY = 5; 

	/* nokia full keyboard */
	/*
	private static final int STEP_Q_KEY = 5;
	private static final int STEP_W_KEY = 6;
	private static final int STEP_E_KEY = 7;
	private static final int STEP_R_KEY = 8;
	private static final int STEP_T_KEY = 9;
	private static final int STEP_A_KEY = 10;
	private static final int STEP_S_KEY = 11;
	private static final int STEP_D_KEY = 12;
	private static final int STEP_F_KEY = 13;
	private static final int STEP_G_KEY = 14;
	private static final int STEP_Y_KEY = 15;
	private static final int STEP_X_KEY = 16;
	private static final int STEP_C_KEY = 17;
	private static final int STEP_V_KEY = 18;
	private static final int STEP_B_KEY = 19;
	private static final int STEP_Z_KEY = 20;
	private static final int STEP_U_KEY = 21;
	private static final int STEP_I_KEY = 22;
	private static final int STEP_O_KEY = 23;
	private static final int STEP_P_KEY = 24;
	private static final int STEP_UE_KEY = 25;
	private static final int STEP_H_KEY = 26;
	private static final int STEP_J_KEY = 27;
	private static final int STEP_K_KEY = 28;
	private static final int STEP_L_KEY = 29;
	private static final int STEP_OE_KEY = 30;
	private static final int STEP_AE_KEY = 31;
	private static final int STEP_N_KEY = 32;
	private static final int STEP_M_KEY = 33;
	private static final int STEP_KOMMA_KEY = 34;
	private static final int STEP_DOT_KEY = 35;
	private static final int STEP_USCORE_KEY = 36;
	private static final int STEP_ENTER_KEY = 37;
	private static final int STEP_BACKSPACE_KEY = 38;
	private static final int STEP_TILDE_KEY = 39;
	private static final int STEP_LSHIFT_KEY = 40;
	private static final int STEP_RSHIFT_KEY = 41;
	private static final int STEP_POUND_KEY = 42;
	private static final int STEP_CTRL_KEY = 43;
	private static final int STEP_SCHAR_KEY = 44;
	private static final int STEP_1_KEY = 45;
	private static final int STEP_2_KEY = 46;
	private static final int STEP_3_KEY = 47;
	private static final int STEP_4_KEY = 48;
	private static final int STEP_5_KEY = 49;
	private static final int STEP_6_KEY = 50;
	private static final int STEP_7_KEY = 51;
	private static final int STEP_8_KEY = 52;
	private static final int STEP_9_KEY = 53;
	private static final int STEP_0_KEY = 54;
	*/
	
    private int step;
    private DynamicTest test;
    private Canvas canvas;
	private int canvasWidth;
	private int canvasHeight;
	private final Font font;
	private Display display;
	private boolean isFinished;
    //private MIDPSysInfoMIDlet midlet = null;
    /** 
     * Creates a new instance of GameControlsInfoCollector
     */
    public KeysInfoCollector() {
        super();
        this.font = Font.getDefaultFont();
    }
    
	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.InfoCollector#collectInfos(com.grimo.me.product.midpsysinfo.MIDPSysInfoMIDlet, javax.microedition.lcdui.Display)
	 */
	public void collectInfos(MIDPSysInfoMIDlet midlet, Display disp) {
		this.display = disp;                
        System.out.println("testing for keys...");
        try {
        	Class.forName("javax.microedition.pki.Certificate");
        	//#if polish.useDefaultPackage
        		String className = "Midp2FullCanvasTest";
        	//#else
        		//# String className = "com.grimo.me.product.midpsysinfo.Midp2FullCanvasTest";
        	//#endif
        	Class testClass = Class.forName( className );
        	this.test = (DynamicTest) testClass.newInstance();
        	this.canvas = (Canvas) this.test;
        } catch (Exception e) {
        	//#debug error
        	System.out.println("Unable to load Midp2FullCanvasTest" + e);
        } catch (Error e) {
        	//#debug error
        	System.out.println("Unable to load Midp2FullCanvasTest" + e);
        }
        if (this.test == null) {
	        try {
	        	Class.forName( "com.nokia.mid.ui.FullCanvas" );
	        	//#if polish.useDefaultPackage
	        		String className = "NokiaFullCanvasTest";
	        	//#else
	        		//# String className = "com.grimo.me.product.midpsysinfo.NokiaFullCanvasTest";
	        	//#endif
	        	Class testClass = Class.forName( className );
	        	this.test = (DynamicTest) testClass.newInstance();
	        } catch (Exception e) {
	        	//#debug error
	        	System.out.println("Unable to load NokiaFullCanvasTest" + e);
	        } catch (Error e) {
	        	//#debug error
	        	System.out.println("Unable to load NokiaFullCanvasTest" + e);
	        }
	        if (this.test == null) {
	        	this.test = new NormalCanvasTest();
	        }
        }
        this.test.setView( this );
        this.canvasWidth = this.test.getWidth();
        this.canvasHeight = this.test.getHeight();
        disp.setCurrent( this.test.getDisplayable() );
        

    }

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g) {
		g.setColor( 0xFFFFFF );
		g.fillRect( 0, 0, this.canvasWidth, this.canvasHeight );
		//g.fillRect( 0, 0, this.canvas.getWidth(), this.canvas.getHeight() );
		g.setColor( 0 );
		g.setFont( this.font );
		String message = null;
		switch (this.step) {
			case STEP_LEFT_SOFT_KEY:
				message = "Press left Soft-Key";
				break;
			case STEP_RIGHT_SOFT_KEY:
				message = "Press right Soft-Key";
				break;
			case STEP_MIDDLE_SOFT_KEY:
				message = "Press middle Soft-Key";
				break;
			case STEP_CLEAR_KEY:
				message = "Press Clear/Delete-Key";
				break;
			case STEP_RETURN_KEY:
				message = "Press Return-Key";
				break;
			/*
			case STEP_Q_KEY:
				message = "Press Q";
				break;
			case STEP_W_KEY:
				message = "Press W";
				break;
			case STEP_E_KEY:
				message = "Press E";
				break;
			case STEP_R_KEY:
				message = "Press R";
				break;
			case STEP_T_KEY:
				message = "Press T";
				break;
			case STEP_A_KEY:
				message = "Press A";
				break;
			case STEP_S_KEY:
				message = "Press S";
				break;
			case STEP_D_KEY:
				message = "Press D";
				break;
			case STEP_F_KEY:
				message = "Press F";
				break;
			case STEP_G_KEY:
				message = "Press G";
				break;
			case STEP_Y_KEY:
				message = "Press Y";
				break;
			case STEP_X_KEY:
				message = "Press X";
				break;
			case STEP_C_KEY:
				message = "Press C";
				break;
			case STEP_V_KEY:
				message = "Press V";
				break;
			case STEP_B_KEY:
				message = "Press B";
				break;
			case STEP_Z_KEY:
				message = "Press Z";
				break;
			case STEP_U_KEY:
				message = "Press U";
				break;
			case STEP_I_KEY:
				message = "Press I";
				break;
			case STEP_O_KEY:
				message = "Press O";
				break;
			case STEP_P_KEY:
				message = "Press P";
				break;
			case STEP_UE_KEY:
				message = "Press Ü";
				break;
			case STEP_H_KEY:
				message = "Press H";
				break;
			case STEP_J_KEY:
				message = "Press J";
				break;
			case STEP_K_KEY:
				message = "Press K";
				break;
			case STEP_L_KEY:
				message = "Press L";
				break;
			case STEP_OE_KEY:
				message = "Press Ö";
				break;
			case STEP_AE_KEY:
				message = "Press Ä";
				break;
			case STEP_N_KEY:
				message = "Press N";
				break;
			case STEP_M_KEY:
				message = "Press M";
				break;
			case STEP_KOMMA_KEY:
				message = "Press komma";
				break;
			case STEP_DOT_KEY:
				message = "Press dot";
				break;
			case STEP_USCORE_KEY:
				message = "Press underscore";
				break;
			case STEP_ENTER_KEY:
				message = "Press enter";
				break;
			case STEP_BACKSPACE_KEY:
				message = "Press backspace";
				break;
			case STEP_TILDE_KEY:
				message = "Press tilde";
				break;
			case STEP_LSHIFT_KEY:
				message = "Press left shift";
				break;
			case STEP_RSHIFT_KEY:
				message = "Press right shift";
				break;
			case STEP_POUND_KEY:
				message = "Press pound(#)";
				break;
			case STEP_CTRL_KEY:
				message = "Press ctrl";
				break;
			case STEP_SCHAR_KEY:
				message = "Press special char-Key (chr)";
				break;
			case STEP_1_KEY:
				message = "Press 1";
				break;
			case STEP_2_KEY:
				message = "Press 2";
				break;
			case STEP_3_KEY:
				message = "Press 3";
				break;
			case STEP_4_KEY:
				message = "Press 4";
				break;
			case STEP_5_KEY:
				message = "Press 5";
				break;
			case STEP_6_KEY:
				message = "Press 6";
				break;
			case STEP_7_KEY:
				message = "Press 7";
				break;
			case STEP_8_KEY:
				message = "Press 8";
				break;
			case STEP_9_KEY:
				message = "Press 9";
				break;
			case STEP_0_KEY:
				message = "Press 0";
				break;
			*/
			case STEP_SELECT_KEY:
				message = "Press Select-Key";
				break;
			
			default:
				message = "Test finished - please wait.";
		}
		g.drawString(message, 1, 1, Graphics.TOP | Graphics.LEFT );
		int fontHeight = this.font.getHeight() + 5; 
		g.drawString("Or press 0 to skip.", 1, fontHeight, Graphics.TOP | Graphics.LEFT );
		//g.drawString( this.canvas.getWidth() + "x" + this.canvas.getHeight(), 1, fontHeight << 1, Graphics.TOP | Graphics.LEFT );
		
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyPressed(int)
	 */
	public void keyPressed(int keyCode) {
		if (keyCode == Canvas.KEY_NUM0) {
			this.step++;
			if (this.step > STEP_SELECT_KEY) {
				this.isFinished = true;
				
				if (this.view != null) {
					this.display.setCurrent( this.view );
				}
			} else {
				this.test.repaint();
			}
			return;
		}
		switch (this.step) {
			case STEP_LEFT_SOFT_KEY:
				addInfo( "Left Soft-Key: ", "" + keyCode );
				break;
			case STEP_RIGHT_SOFT_KEY:
				addInfo( "Right Soft-Key: ", "" + keyCode );
				break;
			case STEP_MIDDLE_SOFT_KEY:
				addInfo( "Middle Soft-Key: ", "" + keyCode );
				break;
			case STEP_CLEAR_KEY:
				addInfo( "Clear-Key: ", "" + keyCode );
				break;
			case STEP_RETURN_KEY:
				addInfo( "Return-Key: ", "" + keyCode );
				break;
			/*	
			case STEP_Q_KEY:
				addInfo( "Q-Key: ", "" + keyCode );
				break;
			case STEP_W_KEY:
				addInfo( "W-Key: ", "" + keyCode );
				break;
			case STEP_E_KEY:
				addInfo( "E-Key: ", "" + keyCode );
				break;
			case STEP_R_KEY:
				addInfo( "R-Key: ", "" + keyCode );
				break;
			case STEP_T_KEY:
				addInfo( "T-Key: ", "" + keyCode );
				break;
			case STEP_A_KEY:
				addInfo( "A-Key: ", "" + keyCode );
				break;
			case STEP_S_KEY:
				addInfo( "S-Key: ", "" + keyCode );
				break;
			case STEP_D_KEY:
				addInfo( "D-Key: ", "" + keyCode );
				break;
			case STEP_F_KEY:
				addInfo( "F-Key: ", "" + keyCode );
				break;
			case STEP_G_KEY:
				addInfo( "G-Key: ", "" + keyCode );
				break;
			case STEP_Y_KEY:
				addInfo( "Y-Key: ", "" + keyCode );
				break;
			case STEP_X_KEY:
				addInfo( "X-Key: ", "" + keyCode );
				break;
			case STEP_C_KEY:
				addInfo( "C-Key: ", "" + keyCode );
				break;
			case STEP_V_KEY:
				addInfo( "V-Key: ", "" + keyCode );
				break;
			case STEP_B_KEY:
				addInfo( "B-Key: ", "" + keyCode );
				break;
			case STEP_Z_KEY:
				addInfo( "Z-Key: ", "" + keyCode );
				break;
			case STEP_U_KEY:
				addInfo( "U-Key: ", "" + keyCode );
				break;
			case STEP_I_KEY:
				addInfo( "I-Key: ", "" + keyCode );
				break;
			case STEP_O_KEY:
				addInfo( "O-Key: ", "" + keyCode );
				break;
			case STEP_P_KEY:
				addInfo( "P-Key: ", "" + keyCode );
				break;
			case STEP_UE_KEY:
				addInfo( "UE-Key: ", "" + keyCode );
				break;
			case STEP_H_KEY:
				addInfo( "H-Key: ", "" + keyCode );
				break;
			case STEP_J_KEY:
				addInfo( "J-Key: ", "" + keyCode );
				break;
			case STEP_K_KEY:
				addInfo( "K-Key: ", "" + keyCode );
				break;
			case STEP_L_KEY:
				addInfo( "L-Key: ", "" + keyCode );
				break;
			case STEP_OE_KEY:
				addInfo( "OE-Key: ", "" + keyCode );
				break;
			case STEP_AE_KEY:
				addInfo( "AE-Key: ", "" + keyCode );
				break;
			case STEP_N_KEY:
				addInfo( "N-Key: ", "" + keyCode );
				break;
			case STEP_M_KEY:
				addInfo( "M-Key: ", "" + keyCode );
				break;
			case STEP_KOMMA_KEY:
				addInfo( "comma-Key: ", "" + keyCode );
				break;
			case STEP_DOT_KEY:
				addInfo( "dot-Key: ", "" + keyCode );
				break;
			case STEP_USCORE_KEY:
				addInfo( "uscore-Key: ", "" + keyCode );
				break;
			case STEP_ENTER_KEY:
				addInfo( "enter-Key: ", "" + keyCode );
				break;
			case STEP_BACKSPACE_KEY:
				addInfo( "backspace-Key: ", "" + keyCode );
				break;
			case STEP_TILDE_KEY:
				addInfo( "tilde-Key: ", "" + keyCode );
				break;
			case STEP_LSHIFT_KEY:
				addInfo( "lshift-Key: ", "" + keyCode );
				break;
			case STEP_RSHIFT_KEY:
				addInfo( "rshift-Key: ", "" + keyCode );
				break;
			case STEP_POUND_KEY:
				addInfo( "pound-Key: ", "" + keyCode );
				break;
			case STEP_CTRL_KEY:
				addInfo( "ctrl-Key: ", "" + keyCode );
				break;
			case STEP_SCHAR_KEY:
				addInfo( "schars-Key: ", "" + keyCode );
				break;
			case STEP_1_KEY:
				addInfo( "1-Key: ", "" + keyCode );
				break;
			case STEP_2_KEY:
				addInfo( "2-Key: ", "" + keyCode );
				break;
			case STEP_3_KEY:
				addInfo( "3-Key: ", "" + keyCode );
				break;
			case STEP_4_KEY:
				addInfo( "4-Key: ", "" + keyCode );
				break;
			case STEP_5_KEY:
				addInfo( "5-Key: ", "" + keyCode );
				break;
			case STEP_6_KEY:
				addInfo( "6-Key: ", "" + keyCode );
				break;
			case STEP_7_KEY:
				addInfo( "7-Key: ", "" + keyCode );
				break;
			case STEP_8_KEY:
				addInfo( "8-Key: ", "" + keyCode );
				break;
			case STEP_9_KEY:
				addInfo( "9-Key: ", "" + keyCode );
				break;
			case STEP_0_KEY:
				addInfo( "0-Key: ", "" + keyCode );
				break;
			*/
			case STEP_SELECT_KEY:
				addInfo( "Select-Key: ", "" + keyCode );
				try {
					addInfo( "gameAction(Select-Key) == FIRE: ", "" + (((Canvas)this.test).getGameAction( keyCode ) == Canvas.FIRE) );
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to retrieve game action of Select Key " + keyCode + e );
				}
				break;
		}
		this.step++;
		if (this.step > STEP_SELECT_KEY) {
			this.isFinished = true;
			Canvas canvas = (Canvas) this.test;
	        addInfo( "Canvas.UP: ", canvas.getKeyName(canvas.getKeyCode(Canvas.UP)));
	        addInfo( "Canvas.LEFT: ", canvas.getKeyName(canvas.getKeyCode(Canvas.LEFT)));
	        addInfo( "Canvas.RIGHT: ", canvas.getKeyName(canvas.getKeyCode(Canvas.RIGHT)));
	        addInfo( "Canvas.DOWN: ", canvas.getKeyName(canvas.getKeyCode(Canvas.DOWN)));
	        addInfo( "Canvas.FIRE: ", canvas.getKeyName(canvas.getKeyCode(Canvas.FIRE)));
	        addInfo( "Canvas.GAME_A: ", canvas.getKeyName(canvas.getKeyCode(Canvas.GAME_A)));
	        addInfo( "Canvas.GAME_B: ", canvas.getKeyName(canvas.getKeyCode(Canvas.GAME_B)));
	        addInfo( "Canvas.GAME_C: ", canvas.getKeyName(canvas.getKeyCode(Canvas.GAME_C)));
	        addInfo( "Canvas.GAME_D: ", canvas.getKeyName(canvas.getKeyCode(Canvas.GAME_D)));

	        /* send */
	        /*
	        try{
	        	SysInfoServer remoteServer = (SysInfoServer) RemoteClient.open("de.enough.sysinfo.SysInfoServer","http://enough.dyndns.org:8180/sysinfoserver");
	        	Hashtable infos = new Hashtable();
	        	Info[] infoArray = getInfos();
	        	int infoSize = getInfos().length;
	        	for(int i=0; i<infoSize; i++){
	        		infos.put(infoArray[i].name, infoArray[i].value);
	        	}
	        	remoteServer.sendKeys(infos);
	        }catch(Exception e){
	        	
	        }
	        */
	        
	        if (this.view != null) {
				this.display.setCurrent( this.view );
			}
		} else {
			this.test.repaint();
		}
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyReleased(int)
	 */
	public void keyReleased(int keyCode) {
		// TODO enough implement keyReleased
		
	}

	/* (non-Javadoc)
	 * @see com.grimo.me.product.midpsysinfo.DynamicTestView#keyRepeated(int)
	 */
	public void keyRepeated(int keyCode) {
		// TODO enough implement keyRepeated
		
	}
	
	
    
	public void show(Display disp, InfoForm infoForm) {
		this.view = infoForm;
		if (this.isFinished) {
			disp.setCurrent( infoForm );
		}
	}
	
	public boolean isFinished() {
		return this.isFinished;
	}

}