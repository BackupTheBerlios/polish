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

import de.enough.polish.util.DrawUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotFoundException;

/**
 *
 * @author Ovidiu Iliescu
 */
public class ProcessingContext implements ProcessingInterface  {

    // Inner-working methods. Not related to the Mobile Processing specs
    // -----------------------------------------------------------------

    public ProcessingContextContainerInterface _parent = null ;
    
    public Image _buffer;
    public Graphics _bufferg ;
    public int _timeBetweenFrames = 20;
    public boolean _haltExecution = false;
    public boolean _loop = true;
    public boolean _refreshFlag = false;

    public int _prevWidth = -1;
    public int _prevHeight = -1;

    public int _shapeMode = POLYGON ;
    public int _rectMode = 0;
    public int _ellipseMode = 0;
    public int _imageMode = 0;

    public int[] _vertex;
    public int _vertexIndex;
    public int[] _curveVertex;
    public int _curveVertexIndex;
    public int[] _stack;
    public int _stackIndex;

    public boolean _hasStroke = true;
    public boolean _hasFill = true;
    public int _strokeWidth = 1;
    public int _strokeColor = 0;
    public int _fillColor = 0;
    public int _bgColor = 0;
    public PImage _bgImage = null;
    public boolean _bgImageMode = false ;
    public int _colorMode = RGB;
    public int _colorRange1 = 255;
    public int _colorRange2 = 255;
    public int _colorRange3 = 255;

    public static final String SOFTKEY1_NAME    = "SOFT1";
    public static final String SOFTKEY2_NAME    = "SOFT2";
    public static final String SEND_NAME        = "SEND";

    public static final int     MULTITAP_KEY_SPACE      = 0;
    public static final int     MULTITAP_KEY_UPPER      = 1;
    public static final String  MULTITAP_PUNCTUATION    = ".,?!'\"-_:;/()@&#$%*+<=>^";

    public boolean   _multitap;
    public char[]    _multitapKeySettings;
    public int       _multitapLastEdit;
    public int       _multitapEditDuration;
    public boolean   _multitapIsUpperCase;
    public String    _multitapPunctuation;
    public boolean   _pointerPressed = false ;

    public Calendar _calendar = null ;
    public Runtime _runtime ;
    public long _startTime = -1 ;
    public long _lastFrameTime = -1 ;

    public long _lastDrawTime = 0;

    public String _softkeyLabel = null ;

    public PFont _defaultFont = new PFont ( Font.getDefaultFont() );
    public int _textAlignMode = LEFT;

    public Random _random = null ;

    // Due to the order Java initializes class members when inheritance is
    // involved, we need to call setup() in a lazy manner, to ensure that
    // all offspring class members that are initialized directly in their
    // declarations are properly initialized _before_ setup() is called.
    public boolean _hasBeenInitialized = false ;


    public ProcessingContext ()
    {
        _initVars (-1,-1);
    }
    
    public ProcessingContext(int width, int height)
    {
            _initVars(width, height);
    }

    public void triggerRepaint()
    {
            if ( _parent != null )
            {
                _parent.processingRequestRepaint();
            }
    }

   // Used to process key events
    public static final Canvas canvas = new Canvas() {

        public void paint(Graphics graphics) {

        }
    };

    /**
     * Checks if a refresh of the buffer is needed.
     * 
     * @return true if the buffer should be refreshed, false otherwise.
     */
    public boolean checkForRefresh() {
                
        if (_haltExecution)
        {
            return false;
        }

        // If draw() is not called in a loop
        if (!_loop)
        {

            // Tell us if the buffer has been updated since the last time
            // checkForRefresh() was called
            boolean refreshValue = _refreshFlag ;
            _refreshFlag = false;
            return refreshValue ;
        }
        else
        {
            // If we're in a loop and enough time has passed to render a frame,
            // signal this
            long now = System.currentTimeMillis();

            if (now - _lastFrameTime > _timeBetweenFrames)
            {
                return true;
            } else
            {
                return false;
            }
        }
    }

    /**
     * Sets the parent of this Processing Context
     * @param parent
     */
    public void setParent ( ProcessingContextContainerInterface parent)
    {
        this._parent = parent;
    }


    /**
     * Executes a refresh. The first call of this function also triggers the initialization
     * of the context. If the refresh is forced (outside of the animation timeline)
     * then the last frame time should not be updated in order to avoid potential issues.
     * @param specified if the last frame time should be updated also
     */
    public void executeRefresh(boolean alsoUpdateLastFrameTime)
    {
        if ( alsoUpdateLastFrameTime )
        {
            _refresh();
        }
        else
        {
        // Since the refresh is forced, we should not modify the last frame time
        // as technically the refresh is not part of the natural animation flow.
        // Doing so also avoids a lot of refresh/repaint issues.

        long goodFrameTime = _lastFrameTime;
        _refresh();
        _lastFrameTime = goodFrameTime;
        }
    }

    /**
     * Refreshes the screen and sets the buffer update flag accordingly.
     */
    public void _refresh() {

        if (_haltExecution)
        {
            return;
        }

        if (_hasBeenInitialized == false )
        {
            _initVars(_prevWidth,_prevHeight);
            _hasBeenInitialized = true ;
        }

        if ( _bufferg != null )
        {
            // Paint the background
            if ( _bgImageMode == false )
            {
                    int lastColor = _bufferg.getColor() ;
                    _bufferg.setColor(_bgColor);
                    _bufferg.fillRect(0, 0, width, height);
                    _bufferg.setColor(lastColor);
            }
            else
            {
                _bgImage.paint( (width - _bgImage.getWidth()) / 2, (height - _bgImage.getHeight()) /2, _bufferg );
            }


        draw();
        _lastFrameTime = System.currentTimeMillis();
        _refreshFlag = true;
        
        }
    }

    /**
     * Returns the loop state of the Processing context
     * @return true if it runs in a loop, false otherwise
     */
    public boolean isLooping()
    {
        return _loop ;
    }

    /**
     * Returns the time (in milliseconds) at which the last frame was drawn.
     * @return the time in ms, or -1 if no frame has been drawn before.
     */
    public long getLastFrameTime()
    {
        return _lastFrameTime ;
    }

    /**
     * Returns the desired time interval between frames (in milliseconds).
     *
     * @return
     */
    public long getIntervalBetweenFrames()
    {
        if ( _loop == false )
        {
            return Long.MAX_VALUE;
        }
        else
        {
            return _timeBetweenFrames ;
        }
    }

    /**
     * Returns the contents of the buffer.
     * @return
     */
    public Image getBuffer() {
        return _buffer;
    }

    /*
     * Resets the buffer image to the specified dimensions
     * then reinitializes the Processing part of the code by calling setup().
     * All other variables except the ones related to the image size and
     * the ones modified within setup() are left untouched.
     * 
     */
    public void _resetImageSize(int width, int height)
    {       
            _buffer = Image.createImage(width, height);
            _bufferg = _buffer.getGraphics() ;
            this.width = width;
            this.height = height;
            setup();
            redraw();
    }

    /**
     * Call this method to signal a change in the item's size.
     * If the specified dimensions are the same as the current dimensions
     * nothing happens.
     * @param width
     * @param height
     */
    public void signalSizeChange(int width, int height)
    {
         if ( (width != _prevWidth) || (height != _prevHeight) )
         {
            _prevWidth = width;
            _prevHeight = height;
            _resetImageSize(width, height);
         }

    }

    public void signalPointerDragged(int x, int y)
    {
        pointerX = x;
        pointerY = y;
        pointerDragged();
    }

    public void signalPointerReleased(int x, int y)
    {
        pointerX = x;
        pointerY = y;
        pointerReleased();
    }

    public void signalPointerPressed(int x, int y)
    {
        pointerX = x;
        pointerY = y;
        pointerPressed();
    }


   public void signalKeyPressed(int keyCode) {
        keyPressed = true;

        if (_multitap) {
            _multitapKeyPressed(keyCode);
        }

        _key(keyCode);
        keyPressed();
    }

    public void signalKeyReleased(int keyCode) {
        keyPressed = false;

        _key(keyCode);
        keyReleased();
    }

    public void signalSoftkeyPressed(String label) {
        softkeyPressed(label);
    }

    public void signalApplicationSuspend()
    {
        suspend();
    }

    public void signalApplicationResume()
    {
        resume();
    }

    public void signalDestroy()
    {
        processingContextObjects.removeElement(this);
        destroy();
    }

    public void signalInitialization()
    {
        ProcessingContext.processingContextObjects.removeElement(this);
        ProcessingContext.processingContextObjects.addElement(this);
    }

    public void signalHasFocus()
    {
        focus();
    }

    public void signalLostFocus()
    {
        lostFocus();
    }

    public String getSoftkeyLabel()
    {
        return _softkeyLabel;
    }

    public void _initVars(int width, int height)
    {
        background(200);
        signalSizeChange(width,height);

        _runtime = Runtime.getRuntime() ;
        _hasFill = true;
        _fillColor = 0xFFFFFF;

        _rectMode = CORNER;
        _ellipseMode = CENTER;
        _imageMode = CORNER;

        _prevHeight = - 1;
        _prevWidth = -1;

        _shapeMode = -1;
        _vertex = new int[16];
        _vertexIndex = 0;

        _curveVertex = new int[8];
        _curveVertexIndex = 0;

        multitapBuffer = new char[64];
        multitapText = "";
        _multitapKeySettings = new char[] { '#', '*' };
        _multitapPunctuation = MULTITAP_PUNCTUATION;
        _multitapEditDuration = 1000;

        _startTime = System.currentTimeMillis() ;
        _lastFrameTime = -1 ;

        _stack = new int[6];
        _stackIndex = 0;
        
        // setup() is called in a lazy manner in checkForRefresh();
        // see the comment above the "hasBeenInitialized" variable
        // for more information
    }

    public double _Hue_2_RGB(double v1, double v2, double vH) //Function Hue_2_RGB
    {
        if (vH < 0)
        {
            vH += 1;
        }
        if (vH > 1)
        {
            vH -= 1;
        }
        if ((6 * vH) < 1)
        {
            return (v1 + (v2 - v1) * 6 * vH);
        }
        if ((2 * vH) < 1)
        {
            return (v2);
        }
        if ((3 * vH) < 2)
        {
            return (v1 + (v2 - v1) * ( 2.0 / 3 - vH) * 6);
        }
        return (v1);
    }


    public void _polygon(int startIndex, int endIndex) {
        //// make sure at least 2 vertices
        if (endIndex >= (startIndex + 2)) {
            //// make sure at least 3 vertices for fill
            if (endIndex >= (startIndex + 4)) {
                if (_hasFill) {
                    _bufferg.setColor(_fillColor);

                    int ctr;
                    int size = 1 + ( endIndex - startIndex ) / 2;
                    int pos = 0;
                    int arrX[] = new int[size];
                    int arrY[] = new int[size];
                    for (ctr=startIndex;ctr<=endIndex;ctr=ctr+2)
                    {
                        arrX[pos] = _vertex[ctr];
                        arrY[pos] = _vertex[ctr+1];
                        pos++;
                    }

                    DrawUtil.fillPolygon(arrX, arrY, _fillColor, _bufferg);

                } 
            }

            if (_hasStroke) {
                _bufferg.setColor(_strokeColor);
                for (int i = startIndex + 2; i <= endIndex; i += 2) {
                    line(_vertex[i - 2], _vertex[i - 1], _vertex[i], _vertex[i + 1]);
                }
                line(_vertex[endIndex], _vertex[endIndex + 1], _vertex[startIndex], _vertex[startIndex + 1]);
            }
        }
    }

   

    public void _plotCurveVertices(int x0, int y0, int x1, int y1, int dx0, int dx1, int dy0, int dy1) {
        int x, y, t, t2, t3, h0, h1, h2, h3;
        vertex(x0 >> 8, y0 >> 8);
        for (t = 0; t < 256 /* 1.0f */; t += 26 /* 0.1f */) {
            t2 = (t * t) >> 8;
            t3 = (t * t2) >> 8;

            h0 = ((512 /* 2.0f */ * t3) >> 8) - ((768 /* 3.0f */ * t2) >> 8) + 256 /* 1.0f */;
            h1 = ((-512 /* -2.0f */ * t3) >> 8) + ((768 /* 3.0f */ * t2) >> 8);
            h2 = t3 - ((512 /* 2.0f */ * t2) >> 8) + t;
            h3 = t3 - t2;

            x = ((h0 * x0) >> 8) + ((h1 * x1) >> 8) + ((h2 * dx0) >> 8) + ((h3 * dx1) >> 8);
            y = ((h0 * y0) >> 8) + ((h1 * y1) >> 8) + ((h2 * dy0) >> 8) + ((h3 * dy1) >> 8);
            vertex(x >> 8, y >> 8);
        }
        vertex(x1 >> 8, y1 >> 8);
    }

    public void _clip(int x, int y, int width, int height) {
        int x2 = x + width;
        int y2 = y + height;
        //// get current clip
        int clipX = _bufferg.getClipX();
        int clipY = _bufferg.getClipY();
        int clipX2 = clipX + _bufferg.getClipWidth();
        int clipY2 = clipY + _bufferg.getClipHeight();
        //// check for intersection
        if (!((x >= clipX2) || (x2 <= clipX) || (y >= clipY2) || (y2 <= clipY))) {
            //// intersect
            int intersectX = Math.max(x, clipX);
            int intersectY = Math.max(y, clipY);
            int intersectWidth = Math.min(x2, clipX2) - intersectX;
            int intersectHeight = Math.min(y2, clipY2) - intersectY;
            _bufferg.setClip(intersectX, intersectY, intersectWidth, intersectHeight);
        }
    }



    public void _key(int keyCode) {
        this.rawKeyCode = keyCode;
        //// MIDP 1.0 says the KEY_ values map to ASCII values, but I've seen it
        //// different on some foreign (i.e. Korean) handsets
        if ((keyCode >= Canvas.KEY_NUM0) && (keyCode <= Canvas.KEY_NUM9)) {
            key = (char) ('0' + (keyCode - Canvas.KEY_NUM0));
            this.keyCode = (int) key;
        } else {
            switch (keyCode) {
                case Canvas.KEY_POUND:
                    key = '#';
                    this.keyCode = (int) key;
                    break;
                case Canvas.KEY_STAR:
                    key = '*';
                    this.keyCode = (int) key;
                    break;
                default:
                    String name = canvas.getKeyName(keyCode);
                    if (name.equals(SOFTKEY1_NAME)) {
                        key = 0xffff;
                        this.keyCode = SOFTKEY1;
                    } else if (name.equals(SOFTKEY2_NAME)) {
                        key = 0xffff;
                        this.keyCode = SOFTKEY2;
                    } else if (name.equals(SEND_NAME)) {
                        key = 0xffff;
                        this.keyCode = SEND;
                    } else {
                        key = 0xffff;
                        this.keyCode = canvas.getGameAction(keyCode);
                        if (this.keyCode == 0) {
                            this.keyCode = keyCode;
                        }
                    }
            }
        }
    }



    public final void _multitapKeyPressed(int keyCode) {
        boolean editing = (keyCode == this.keyCode) && ((millis() - _multitapLastEdit) <= _multitapEditDuration);
        char newChar = 0;
        if (editing) {
            newChar = multitapBuffer[multitapBufferIndex - 1];
            if (Character.isUpperCase(newChar)) {
                newChar = Character.toLowerCase(newChar);
            }
        }
        char startChar = 0, endChar = 0, otherChar = 0;
        switch (keyCode) {
            case -8: //// Sun WTK 2.2 emulator
                multitapDeleteChar();
                break;
            case Canvas.KEY_STAR:
                if (_multitapKeySettings[MULTITAP_KEY_SPACE] == '*') {
                    startChar = ' '; endChar = ' '; otherChar = '*';
                } else if (_multitapKeySettings[MULTITAP_KEY_UPPER] == '*') {
                    newChar = _multitapUpperKeyPressed(editing, newChar);
                    editing = (newChar == 0);
                } else {
                    startChar = '*'; endChar = '*'; otherChar = '*';
                    editing = false;
                }
                break;
            case Canvas.KEY_POUND:
                if (_multitapKeySettings[MULTITAP_KEY_SPACE] == '#') {
                    startChar = ' '; endChar = ' '; otherChar = '#';
                } else if (_multitapKeySettings[MULTITAP_KEY_UPPER] == '#') {
                    newChar = _multitapUpperKeyPressed(editing, newChar);
                    editing = (newChar == 0);
                } else {
                    startChar = '#'; endChar = '#'; otherChar = '#';
                    editing = false;
                }
                break;
            case Canvas.KEY_NUM0:
                if (_multitapKeySettings[MULTITAP_KEY_SPACE] == '0') {
                    startChar = ' '; endChar = ' '; otherChar = '0';
                } else if (_multitapKeySettings[MULTITAP_KEY_UPPER] == '0') {
                    newChar = _multitapUpperKeyPressed(editing, newChar);
                    editing = (newChar == 0);
                } else {
                    startChar = '0'; endChar = '0'; otherChar = '0';
                    editing = false;
                }
                break;
            case Canvas.KEY_NUM1:
                int index = 0;
                if (editing) {
                    index = _multitapPunctuation.indexOf(newChar) + 1;
                    if (index == _multitapPunctuation.length()) {
                        index = 0;
                    }
                }
                newChar = _multitapPunctuation.charAt(index);
                break;
            case Canvas.KEY_NUM2:
                startChar = 'a'; endChar = 'c'; otherChar = '2';
                break;
            case Canvas.KEY_NUM3:
                startChar = 'd'; endChar = 'f'; otherChar = '3';
                break;
            case Canvas.KEY_NUM4:
                startChar = 'g'; endChar = 'i'; otherChar = '4';
                break;
            case Canvas.KEY_NUM5:
                startChar = 'j'; endChar = 'l'; otherChar = '5';
                break;
            case Canvas.KEY_NUM6:
                startChar = 'm'; endChar = 'o'; otherChar = '6';
                break;
            case Canvas.KEY_NUM7:
                startChar = 'p'; endChar = 's'; otherChar = '7';
                break;
            case Canvas.KEY_NUM8:
                startChar = 't'; endChar = 'v'; otherChar = '8';
                break;
            case Canvas.KEY_NUM9:
                startChar = 'w'; endChar = 'z'; otherChar = '9';
                break;
            default:
                int action = canvas.getGameAction(keyCode);
                switch (action) {
                    case Canvas.LEFT:
                        _multitapLastEdit = 0;
                        multitapBufferIndex = Math.max(0, multitapBufferIndex - 1);
                        break;
                    case Canvas.RIGHT:
                        _multitapLastEdit = 0;
                        multitapBufferIndex = Math.min(multitapBufferLength, multitapBufferIndex + 1);
                        break;
                }
        }
        if (startChar > 0) {
            if (editing) {
                newChar++;
            } else {
                newChar = startChar;
            }
            if (newChar == (otherChar + 1)) {
                newChar = startChar;
            } else if (newChar > endChar) {
                newChar = otherChar;
            }
        }
        if (newChar > 0) {
            if (_multitapIsUpperCase) {
                newChar = Character.toUpperCase(newChar);
            }
            if (editing) {
                if (multitapBuffer[multitapBufferIndex - 1] != newChar) {
                    multitapBuffer[multitapBufferIndex - 1] = newChar;
                    _multitapLastEdit = millis();
                }
            } else {
                multitapBufferLength++;
                if (multitapBufferLength == multitapBuffer.length) {
                    char[] oldBuffer = multitapBuffer;
                    multitapBuffer = new char[oldBuffer.length * 2];
                    System.arraycopy(oldBuffer, 0, multitapBuffer, 0, multitapBufferIndex);
                    System.arraycopy(oldBuffer, multitapBufferIndex, multitapBuffer, multitapBufferIndex + 1, multitapBufferLength - multitapBufferIndex);
                } else {
                    System.arraycopy(multitapBuffer, multitapBufferIndex, multitapBuffer, multitapBufferIndex + 1, multitapBufferLength - multitapBufferIndex);
                }
                multitapBuffer[multitapBufferIndex] = newChar;
                multitapBufferIndex++;
                _multitapLastEdit = millis();
            }
            multitapText = new String(multitapBuffer, 0, multitapBufferLength);
        }
    }

    public void _checkCalendar() {
        if (_calendar == null) {
            _calendar = Calendar.getInstance();
        }
        _calendar.setTime(new Date());
    }
    
    // Implementation of the Mobile Processing specs
    // ---------------------------------------------
    // Context vars
    public int width = 0;
    public int height = 0;

    // Key related variables
    public char      key = 0;
    public int       keyCode = 0;
    public int       rawKeyCode = 0;
    public boolean   keyPressed = false;

    // Multitap related variables
    public char[]    multitapBuffer = null ;
    public String    multitapText = "";
    public int       multitapBufferIndex = 0;
    public int       multitapBufferLength = 0;

    // Pointer related variables
    public int pointerX = 0;
    public int pointerY = 0;


    /**
     * Forces a redraw by calling draw();
     */
    public void redraw() {
        executeRefresh(false);
    }

    public void loop() {
        _loop = true;
    }

    public void noLoop() {
        _loop = false;
    }

    //#if !tmp.includeContext
    public void destroy() {        
        // Do nothing by default
    }
    //#endif

    public void suspend() {
        // Do nothing by default
    }

    public void resume() {
        // Do nothing by default
    }

    public void setup() {
        // Do nothing by default
    }

    public void exit() {
        destroy();
        _haltExecution = true;
    }

    public void colorMode(int mode) {
        _colorMode = mode;
    }

    public void colorMode(int mode, int range) {
        _colorMode = mode;
        _colorRange1 = _colorRange2 = _colorRange3 = range;
    }

    public void colorMode(int mode, int range1, int range2, int range3) {
        _colorMode = mode;
        _colorRange1 = range1;
        _colorRange2 = range2;
        _colorRange3 = range3;
    }

    public color color(int gray) {
        int col = (255 << 24) | (gray << 16) | (gray << 8) | (gray);
        return new color(col);
    }

    public color color(int gray, int alpha) {
        int col = (alpha << 24) | (gray << 16) | (gray << 8) | (gray);
        return new color(col);
    }

    public color color(int value1, int value2, int value3, int alpha) {
        if (_colorMode == RGB)
        {
            // Normalize the color values according to the parameters set by
            // colorMode();
            value1 = value1 * 255 / _colorRange1;
            value2 = value2 * 255 / _colorRange2;
            value3 = value3 * 255 / _colorRange3;

            int col = (alpha << 24) | (value1 << 16) | (value2 << 8) | (value3);
            return new color(col);
        }
        else if (_colorMode == HSB)
        {
            double R, G, B;
            int Ri, Gi, Bi;
            double temp2, temp1;
            double H = ((double) value1) / _colorRange1;
            double S = ((double) value2) / _colorRange2;
            double L = ((double) value3) / _colorRange3;

            if (S == 0) //HSL from 0 to 1
            {
                R = L * 255;
                G = L * 255;
                B = L * 255;
            }
            else
            {
                if (L < 0.5)
                {
                    temp2 = L * (1 + S);
                } else
                {
                    temp2 = (L + S) - (S * L);
                }

                temp1 = 2 * L - temp2;

                R = 255 * _Hue_2_RGB(temp1, temp2, H + ( 1.0 / 3));
                G = 255 * _Hue_2_RGB(temp1, temp2, H);
                B = 255 * _Hue_2_RGB(temp1, temp2, H - ( 1.0 / 3));

            }

            Ri = (int) R;
            Gi = (int) G;
            Bi = (int) B;

            int col = (alpha << 24) | (Ri << 16) | (Gi << 8) | (Bi);
            return new color(col);
        }
        else
        {
            return new color(0xFFFFFF);
        }
    }

    public void background(int gray) {
        _bgImageMode = false;
        color x = color(gray);
        _bgColor = x.color ;
    }

    public void background(color color)
    {
        _bgImageMode = false;
        _bgColor = color.color ;
    }

    public void background(int value1, int value2, int value3)
    {
        _bgImageMode = false;
        color x = color (value1, value2, value3,255);
        _bgColor = x.color ;
        
    }

    public void background(PImage image)
    {
        _bgImageMode = true;
        _bgImage = image;
    }

    public void strokeWeight(int width) {
        _strokeWidth = width;
    }

    public void stroke(color whatColor) {
        _hasStroke = true;
        _strokeColor = whatColor.color;
    }

    public void stroke(int color) {
        color temp = color(color);
        _strokeColor = temp.color ;
        _hasStroke = true;
    }

    public void stroke(int v1, int v2, int v3) {
        color temp = color(v1,v2,v3,255);
        _strokeColor = temp.color ;
        _hasStroke = true;
    }

    public void noStroke() {
        _hasStroke = false;
    }

    public void fill(int gray)
    {
        _hasFill = true;
        color x = color(gray);
        _fillColor = x.color ;
    }

    public void fill(color color)
    {
        _hasFill = true;
        _fillColor = color.color;
    }

    public void fill(int value1, int value2, int value3)
    {
        _hasFill = true;
        color x = color(value1,value2,value3,255);
        _fillColor = x.color ;
    }

    public void noFill()
    {
        _hasFill = false;
    }

    public void framerate(int framerate) {
        _timeBetweenFrames = 1000 / framerate;
    }

    public void draw() 
    {
        // Does nothing by default
    }

    public void line(int x1, int y1, int x2, int y2) {
        if (_hasStroke) {
            _bufferg.setColor(_strokeColor);
            _bufferg.drawLine(x1, y1, x2, y2);
            if (_strokeWidth > 1) {
                boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);
                if (steep) {
                    int swap = x1;
                    x1 = y1;
                    y1 = swap;
                    swap = x2;
                    x2 = y2;
                    y2 = swap;
                }
                if (x1 > x2) {
                    int swap = x1;
                    x1 = x2;
                    x2 = swap;
                    swap = y1;
                    y1 = y2;
                    y2 = swap;
                }
                int dx = x2 - x1;
                int dy = (y2 > y1) ? y2 - y1 : y1 - y2;
                int error = 0;
                int halfWidth = _strokeWidth >> 1;
                int y = y1 - halfWidth;
                int ystep;
                if (y1 < y2) {
                    ystep = 1;
                } else {
                    ystep = -1;
                }
                for (int x = x1 - halfWidth, endx = x2 - halfWidth; x <= endx; x++) {
                    if (steep) {
                        _bufferg.fillArc(y, x, _strokeWidth, _strokeWidth, 0, 360);
                    } else {
                        _bufferg.fillArc(x, y, _strokeWidth, _strokeWidth, 0, 360);
                    }
                    error += dy;
                    if ((2 * error) >= dx) {
                        y += ystep;
                        error -= dx;
                    }
                }
            }
        }
    }

    public void point(int x1, int y1) {
        if (_hasStroke) {
            _bufferg.setColor(_strokeColor);
            _bufferg.drawLine(x1, y1, x1, y1);
        }
    }

    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        int prevShapeMode = _shapeMode ;
        _shapeMode = POLYGON;
        _vertex[0] = x1;
        _vertex[1] = y1;
        _vertex[2] = x2;
        _vertex[3] = y2;
        _vertex[4] = x3;
        _vertex[5] = y3;
        _vertexIndex = 6;
        endShape();
        _shapeMode = prevShapeMode ;
    }

    public void quad(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        int prevShapeMode = _shapeMode ;
        _shapeMode = POLYGON;
        _vertex[0] = x1;
        _vertex[1] = y1;
        _vertex[2] = x2;
        _vertex[3] = y2;
        _vertex[4] = x3;
        _vertex[5] = y3;
        _vertex[6] = x4;
        _vertex[7] = y4;
        _vertexIndex = 8;
        endShape();
        _shapeMode = prevShapeMode ;
    }

     public void rect(int x, int y, int width, int height) {
        int temp;
        switch (_rectMode) {
            case CORNERS:
                temp = x;
                x = Math.min(x, width);
                width = Math.abs(x - temp);
                temp = y;
                y = Math.min(y, height);
                height = Math.abs(y - temp);
                break;
            case CENTER:
                x -= width / 2;
                y -= height / 2;
                break;
        }
        if (_hasFill) {
            _bufferg.setColor(_fillColor);
            _bufferg.fillRect(x, y, width, height);
        }
        if (_hasStroke) {
            _bufferg.setColor(_strokeColor);
            _bufferg.drawRect(x, y, width, height);
        }
    }

     public void rectMode(int mode) {
        if ((mode >= CENTER) && (mode <= CORNER)) {
            _rectMode = mode;
        }
    }

     public void ellipse(int x, int y, int width, int height) {
        int temp;
        switch (_ellipseMode) {
            case CORNERS:
                temp = x;
                x = Math.min(x, width);
                width = Math.abs(x - temp);
                temp = y;
                y = Math.min(y, height);
                height = Math.abs(y - temp);
                break;
            case CENTER:
                x -= width / 2;
                y -= height / 2;
                break;
            case CENTER_RADIUS:
                x -= width;
                y -= height;
                width *= 2;
                height *= 2;
                break;
        }
        if (_hasFill) {
            _bufferg.setColor(_fillColor);
            _bufferg.fillArc(x, y, width, height, 0, 360);
        }
        if (_hasStroke) {
            _bufferg.setColor(_strokeColor);
            _bufferg.drawArc(x, y, width, height, 0, 360);
        }
    }

    public void ellipseMode(int mode) {
        if ((mode >= CENTER) && (mode <= CORNERS)) {
            _ellipseMode = mode;
        }
    }

    public void curve(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        int prevShapeMode = _shapeMode ;
        beginShape(LINE_STRIP);
        curveVertex(x1, y1);
        curveVertex(x1, y1);
        curveVertex(x2, y2);
        curveVertex(x3, y3);
        curveVertex(x4, y4);
        curveVertex(x4, y4);
        endShape();
        _shapeMode = prevShapeMode ;
    }

    public void bezier(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        int prevShapeMode = _shapeMode ;
        beginShape(LINE_STRIP);
        vertex(x1, y1);
        bezierVertex(x2, y2, x3, y3, x4, y4);
        endShape();
        _shapeMode = prevShapeMode;
    }

     public void curveVertex(int x, int y) {
        //// use fixed point, 8-bit precision
        _curveVertex[_curveVertexIndex] = x << 8;
        _curveVertexIndex++;
        _curveVertex[_curveVertexIndex] = y << 8;
        _curveVertexIndex++;

        if (_curveVertexIndex == 8) {
            int tension = 128 /* 0.5f */;

            int dx0 = ((_curveVertex[4] - _curveVertex[0]) * tension) >> 8;
            int dx1 = ((_curveVertex[6] - _curveVertex[2]) * tension) >> 8;
            int dy0 = ((_curveVertex[5] - _curveVertex[1]) * tension) >> 8;
            int dy1 = ((_curveVertex[7] - _curveVertex[3]) * tension) >> 8;

            _plotCurveVertices(_curveVertex[2], _curveVertex[3],
                              _curveVertex[4], _curveVertex[5],
                              dx0, dx1, dy0, dy1);

            for (int i = 0; i < 6; i++) {
                _curveVertex[i] = _curveVertex[i + 2];
            }
            _curveVertexIndex = 6;
        }
    }

    public void bezierVertex(int x1, int y1, int x2, int y2, int x3, int y3) {

        //// plotCurveVertices will add x0, y0 back
        _vertexIndex -= 2;
        //// use fixed point, 8-bit precision
        int x0 = _vertex[_vertexIndex] << 8;
        int y0 = _vertex[_vertexIndex + 1] << 8;
        //// convert parameters to fixed point
        x1 = x1 << 8;
        y1 = y1 << 8;
        x2 = x2 << 8;
        y2 = y2 << 8;
        x3 = x3 << 8;
        y3 = y3 << 8;
        //// use fixed point, 8-bit precision
        int tension = 768 /* 3.0f */;

        int dx0 = ((x1 - x0) * tension) >> 8;
        int dx1 = ((x3 - x2) * tension) >> 8;
        int dy0 = ((y1 - y0) * tension) >> 8;
        int dy1 = ((y3 - y2) * tension) >> 8;

        _plotCurveVertices(x0, y0,
                          x3, y3,
                          dx0, dx1, dy0, dy1);
    }

    public void vertex(int x, int y) {
        _vertex[_vertexIndex] = x;
        _vertexIndex++;
        _vertex[_vertexIndex] = y;
        _vertexIndex++;

        int length = _vertex.length;
        if (_vertexIndex == length) {
            int[] old = _vertex;
            _vertex = new int[length * 2];
            System.arraycopy(old, 0, _vertex, 0, length);
        }
    }



    public void beginShape(int mode) {
        if ((mode >= POINTS) && (mode <= POLYGON)) {
            _shapeMode = mode;
        } else {
            _shapeMode = POINTS;
        }
        _vertexIndex = 0;
        _curveVertexIndex = 0;
    }


    public void endShape() {
        int i;
        int step;
        switch (_shapeMode) {
            case POINTS:
                i = 0;
                step = 2;
                break;
            case LINES:
                i = 2;
                step = 4;
                break;
            case LINE_STRIP:
            case LINE_LOOP:
                i = 2;
                step = 2;
                break;
            case TRIANGLES:
                i = 4;
                step = 6;
                break;
            case TRIANGLE_STRIP:
                i = 4;
                step = 2;
                break;
            case QUADS:
                i = 6;
                step = 8;
                break;
            case QUAD_STRIP:
                i = 6;
                step = 4;
                break;
            case POLYGON:
                _polygon(0, _vertexIndex - 2);
                return;
            default:
                return;
        }

        for (; i < _vertexIndex; i += step) {
            switch (_shapeMode) {
                case POINTS:
                    point(_vertex[i], _vertex[i + 1]);
                    break;
                case LINES:
                case LINE_STRIP:
                case LINE_LOOP:
                    line(_vertex[i - 2], _vertex[i - 1], _vertex[i], _vertex[i + 1]);
                    break;
                case TRIANGLES:
                case TRIANGLE_STRIP:
                    _polygon(i - 4, i);
                    break;
                case QUADS:
                case QUAD_STRIP:
                    _polygon(i - 6, i);
                    break;
            }
        }
        //// handle loop closing
        if (_shapeMode == LINE_LOOP) {
            if (_vertexIndex >= 2) {
                line(_vertex[_vertexIndex - 2], _vertex[_vertexIndex - 1], _vertex[0], _vertex[1]);
            }
        }

        _vertexIndex = 0;
    }


    public PImage loadImage(String filename) {
        try {
            Image img = Image.createImage("/" + filename);
            return new PImage(img);
        } catch(Exception e) {
            return null;
        }
    }

    public PImage loadImage(byte[] data)
    {
        return new PImage(data);
    }

    public void image(PImage img, int x, int y)
    {
        img.paint(x, y, _bufferg);
    }

   public void image(PImage img, int sx, int sy, int swidth, int sheight, int dx, int dy) {
        if (_imageMode == CORNERS) {
            swidth = swidth - sx;
            sheight = sheight - sy;
        }
        _clip(dx, dy, swidth, sheight);
        img.paint(dx - sx, dy - sy, _bufferg);
        _clip (0,0,width,height);
   }

   public final void multitap() {
        _multitap = true;
    }

    public final void noMultitap() {
        _multitap = false;
    }

    public final void multitapClear() {
        multitapBufferIndex = 0;
        multitapBufferLength = 0;
        multitapText = "";
    }

    public void softkeyPressed(String label)
    {
        // Do nothing by default
    }

    public void softkey(String label)
    {
        _softkeyLabel = label ;
    }

    public final void multitapDeleteChar() {
        if (multitapBufferIndex > 0) {
            System.arraycopy(multitapBuffer, multitapBufferIndex, multitapBuffer, multitapBufferIndex - 1, multitapBufferLength - multitapBufferIndex);
            multitapBufferLength--;
            multitapBufferIndex--;
            _multitapLastEdit = 0;
        }
        multitapText = new String(multitapBuffer, 0, multitapBufferLength);
    }

    public char _multitapUpperKeyPressed(boolean editing, char newChar) {
        _multitapIsUpperCase = !_multitapIsUpperCase;
        if (editing) {
            if (newChar == _multitapKeySettings[MULTITAP_KEY_UPPER]) {
                //// delete the char
                multitapDeleteChar();
                _multitapLastEdit = millis();
                newChar = 0;
            } else {
                newChar = _multitapKeySettings[MULTITAP_KEY_UPPER];
            }
        } else {
            _multitapLastEdit = millis();
        }
        return newChar;
    }


    public void keyPressed() {

        // Do nothing by default
    }

    public void keyReleased() {

        // Do nothing by default
    }

    public void pointerDragged()
    {
        // Do nothing by default
    }

    public void pointerPressed()
    {
        // Do nothing by default
    }

    public void pointerReleased()
    {
        // Do nothing by default
    }

    public final int millis() {
        return (int) (System.currentTimeMillis() - _startTime);
    }

    public final int second() {
        _checkCalendar();
        return _calendar.get(Calendar.SECOND);
    }

    public final int minute() {
        _checkCalendar();
        return _calendar.get(Calendar.MINUTE);
    }

    public final int hour() {
        _checkCalendar();
        return _calendar.get(Calendar.HOUR_OF_DAY);
    }

    public final int day() {
        _checkCalendar();
        return _calendar.get(Calendar.DAY_OF_MONTH);
    }

    public final int month() {
        _checkCalendar();
        return _calendar.get(Calendar.MONTH);
    }

    public final int year() {
        _checkCalendar();
        return _calendar.get(Calendar.YEAR);
    }

    public final int currentMemory() {
        return (int) _runtime.freeMemory();
    }

    public final int reportedMemory() {
        return (int) _runtime.totalMemory();
    }

    /**
     * This method is called when the processing context (or processing context)
     * container receives focus
     */
    public void focus() {
        // Do nothig by default
    }

    /**
     * This method is called when the processing context (or processing context)
     * container looses focus
     */
    public void lostFocus() {
        // Do nothing by default
    }

    public String textInput() {
        return textInput ("", "", Integer.MAX_VALUE);
    }

    public String textInput(String title, String text, int max) {

        // TO BE IMPLEMENTED
        return "not implemented";

    }

     public void print(boolean data) {
        System.out.print(String.valueOf(data));
    }

    public void print(byte data) {
        System.out.print(String.valueOf(data));
    }

    public void print(char data) {
        System.out.print(String.valueOf(data));
    }

    public void print(int data) {
        System.out.print(String.valueOf(data));
    }

    public void print(Object data) {
        System.out.print(String.valueOf(data));
    }

    public void print(String data) {
        System.out.print(data);
    }

    public void println(boolean data) {
        System.out.println(String.valueOf(data));
    }

    public void println(byte data) {
        System.out.println(String.valueOf(data));
    }

    public void println(char data) {
        System.out.println(String.valueOf(data));
    }

    public void println(int data) {
        System.out.println(String.valueOf(data));
    }

    public void println(Object data) {
        System.out.println(String.valueOf(data));
    }

    public void println(String data) {
        System.out.println(data);
    }

    public int length(boolean[] array) {
        return array.length;
    }

    public int length(byte[] array) {
        return array.length;
    }

    public int length(char[] array) {
        return array.length;
    }

    public int length(int[] array) {
        return array.length;
    }

    public int length(Object[] array) {
        return array.length;
    }

    public String join(String[] anyArray, String separator) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = anyArray.length; i < length; i++) {
            buffer.append(anyArray[i]);
            if (i < (length - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    public String join(int[] anyArray, String separator) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = anyArray.length; i < length; i++) {
            buffer.append(anyArray[i]);
            if (i < (length - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    public String join(int[] intArray, String separator, int digits) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = intArray.length; i < length; i++) {
            buffer.append(nf(intArray[i], digits));
            if (i < (length - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    public String nf(int intValue, int digits) {
        StringBuffer buffer = new StringBuffer();
        for (int j = Integer.toString(intValue).length(); j < digits; j++) {
            buffer.append("0");
        }
        buffer.append(intValue);
        return buffer.toString();
    }

    public String nfp(int intValue, int digits) {
        StringBuffer buffer = new StringBuffer();
        if (intValue < 0) {
            buffer.append("-");
        } else {
            buffer.append("+");
        }
        buffer.append(nf(intValue, digits));
        return buffer.toString();
    }

    public String nfs(int intValue, int digits) {
        StringBuffer buffer = new StringBuffer();
        if (intValue < 0) {
            buffer.append("-");
        } else {
            buffer.append(" ");
        }
        buffer.append(nf(intValue, digits));
        return buffer.toString();
    }

    public String[] split(String str) {
        Vector v = new Vector();
        StringBuffer buffer = new StringBuffer();
        char c;
        boolean whitespace = false;
        for (int i = 0, length = str.length(); i < length; i++ ) {
            c = str.charAt(i);
            switch (c) {
                case '\n':
                case '\r':
                case '\f':
                case '\t':
                case ' ':
                case 160:
                    whitespace = true;
                    break;
                default:
                    if (whitespace) {
                        v.addElement(buffer.toString());
                        buffer.delete(0, buffer.length());

                        whitespace = false;
                    }
                    buffer.append(c);
            }
        }
        if (buffer.length() > 0) {
            v.addElement(buffer.toString());
        }

        String[] tokens = new String[v.size()];
        v.copyInto(tokens);

        return tokens;
    }

    public String[] split(String str, char delim) {
        return split(str, new String(new char[] { delim }));
    }

    public String[] split(String str, String delim) {
        Vector v = new Vector();
        int prevIndex = 0;
        int nextIndex = str.indexOf(delim, prevIndex);
        int delimLength = delim.length();
        while (nextIndex >= 0) {
            v.addElement(str.substring(prevIndex, nextIndex));
            prevIndex = nextIndex + delimLength;
            nextIndex = str.indexOf(delim, prevIndex);
        }
        if (prevIndex < str.length()) {
            v.addElement(str.substring(prevIndex));
        }

        String[] tokens = new String[v.size()];
        v.copyInto(tokens);

        return tokens;
    }

    public String trim(String str) {
        //// deal with unicode nbsp later
        return str.trim();
    }

    public String[] append(String[] array, String element) {
        String[] old = array;
        int length = old.length;
        array = new String[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    public boolean[] append(boolean[] array, boolean element) {
        boolean[] old = array;
        int length = old.length;
        array = new boolean[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    public byte[] append(byte[] array, byte element) {
        byte[] old = array;
        int length = old.length;
        array = new byte[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    public char[] append(char[] array, char element) {
        char[] old = array;
        int length = old.length;
        array = new char[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    public int[] append(int[] array, int element) {
        int[] old = array;
        int length = old.length;
        array = new int[length + 1];
        System.arraycopy(old, 0, array, 0, length);
        array[length] = element;
        return array;
    }

    public void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    public String[] concat(String[] array1, String[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        String[] array = new String[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    public boolean[] concat(boolean[] array1, boolean[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        boolean[] array = new boolean[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    public byte[] concat(byte[] array1, byte[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        byte[] array = new byte[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    public char[] concat(char[] array1, char[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        char[] array = new char[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    public int[] concat(int[] array1, int[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        int[] array = new int[length1 + length2];
        System.arraycopy(array1, 0, array, 0, length1);
        System.arraycopy(array2, 0, array, length1, length2);
        return array;
    }

    public boolean[] contract(boolean[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            boolean[] old = array;
            array = new boolean[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    public byte[] contract(byte[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            byte[] old = array;
            array = new byte[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    public char[] contract(char[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            char[] old = array;
            array = new char[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    public int[] contract(int[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            int[] old = array;
            array = new int[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    public String[] contract(String[] array, int newSize) {
        int length = array.length;
        if (length > newSize) {
            String[] old = array;
            array = new String[newSize];
            System.arraycopy(old, 0, array, 0, newSize);
        }
        return array;
    }

    public boolean[] expand(boolean[] array) {
        return expand(array, array.length * 2);
    }

    public boolean[] expand(boolean[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            boolean[] old = array;
            array = new boolean[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    public byte[] expand(byte[] array) {
        return expand(array, array.length * 2);
    }

    public byte[] expand(byte[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            byte[] old = array;
            array = new byte[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    public char[] expand(char[] array) {
        return expand(array, array.length * 2);
    }

    public char[] expand(char[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            char[] old = array;
            array = new char[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    public int[] expand(int[] array) {
        return expand(array, array.length * 2);
    }

    public int[] expand(int[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            int[] old = array;
            array = new int[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    public String[] expand(String[] array) {
        return expand(array, array.length * 2);
    }

    public String[] expand(String[] array, int newSize) {
        int length = array.length;
        if (length < newSize) {
            String[] old = array;
            array = new String[newSize];
            System.arraycopy(old, 0, array, 0, length);
        }
        return array;
    }

    public boolean[] reverse(boolean[] array) {
        int length = array.length;
        boolean[] reversed = new boolean[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    public byte[] reverse(byte[] array) {
        int length = array.length;
        byte[] reversed = new byte[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    public char[] reverse(char[] array) {
        int length = array.length;
        char[] reversed = new char[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    public int[] reverse(int[] array) {
        int length = array.length;
        int[] reversed = new int[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    public String[] reverse(String[] array) {
        int length = array.length;
        String[] reversed = new String[length];
        for (int i = length - 1; i >= 0; i--) {
            reversed[i] = array[length - i - 1];
        }
        return reversed;
    }

    public boolean[] shorten(boolean[] array) {
        boolean[] old = array;
        int length = old.length - 1;
        array = new boolean[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    public byte[] shorten(byte[] array) {
        byte[] old = array;
        int length = old.length - 1;
        array = new byte[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    public char[] shorten(char[] array) {
        char[] old = array;
        int length = old.length - 1;
        array = new char[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    public int[] shorten(int[] array) {
        int[] old = array;
        int length = old.length - 1;
        array = new int[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    public String[] shorten(String[] array) {
        String[] old = array;
        int length = old.length - 1;
        array = new String[length];
        System.arraycopy(old, 0, array, 0, length);
        return array;
    }

    public boolean[] subset(boolean[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    public boolean[] subset(boolean[] array, int offset, int length) {
        boolean[] subset = new boolean[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    public byte[] subset(byte[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    public byte[] subset(byte[] array, int offset, int length) {
        byte[] subset = new byte[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    public char[] subset(char[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    public char[] subset(char[] array, int offset, int length) {
        char[] subset = new char[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    public int[] subset(int[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    public int[] subset(int[] array, int offset, int length) {
        int[] subset = new int[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    public String[] subset(String[] array, int offset) {
        return subset(array, offset, array.length - offset);
    }

    public String[] subset(String[] array, int offset, int length) {
        String[] subset = new String[length];
        System.arraycopy(array, offset, subset, 0, length);
        return subset;
    }

    public boolean[] splice(boolean[] array, boolean value, int index) {
        int length = array.length;
        boolean[] splice = new boolean[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    public boolean[] splice(boolean[] array, boolean[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        boolean[] splice = new boolean[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }

    public byte[] splice(byte[] array, byte value, int index) {
        int length = array.length;
        byte[] splice = new byte[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    public byte[] splice(byte[] array, byte[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        byte[] splice = new byte[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }

    public char[] splice(char[] array, char value, int index) {
        int length = array.length;
        char[] splice = new char[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    public char[] splice(char[] array, char[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        char[] splice = new char[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }

    public int[] splice(int[] array, int value, int index) {
        int length = array.length;
        int[] splice = new int[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    public int[] splice(int[] array, int[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        int[] splice = new int[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }

    public String[] splice(String[] array, String value, int index) {
        int length = array.length;
        String[] splice = new String[length + 1];
        System.arraycopy(array, 0, splice, 0, index);
        splice[index] = value;
        System.arraycopy(array, index, splice, index + 1, length - index);
        return splice;
    }

    public String[] splice(String[] array, String[] array2, int index) {
        int length = array.length;
        int length2 = array2.length;
        String[] splice = new String[length + length2];
        System.arraycopy(array, 0, splice, 0, index);
        System.arraycopy(array2, 0, splice, index, length2);
        System.arraycopy(array, index, splice, index + length2, length - index);
        return splice;
    }


    public String str(boolean val) {
        return String.valueOf(val);
    }

    public String str(byte val) {
        return String.valueOf(val);
    }

    public String str(char val) {
        return String.valueOf(val);
    }

    public String str(int val) {
        return String.valueOf(val);
    }

    public String[] str(boolean[] val) {
        String[] result = new String[val.length];
        for (int i = val.length - 1; i >= 0; i--) {
            result[i] = String.valueOf(val[i]);
        }
        return result;
    }

    public String[] str(byte[] val) {
        String[] result = new String[val.length];
        for (int i = val.length - 1; i >= 0; i--) {
            result[i] = String.valueOf(val[i]);
        }
        return result;
    }

    public String[] str(char[] val) {
        String[] result = new String[val.length];
        for (int i = val.length - 1; i >= 0; i--) {
            result[i] = String.valueOf(val[i]);
        }
        return result;
    }

    public String[] str(int[] val) {
        String[] result = new String[val.length];
        for (int i = val.length - 1; i >= 0; i--) {
            result[i] = String.valueOf(val[i]);
        }
        return result;
    }


    public void pushMatrix() {
        if (_stackIndex == _stack.length) {
            int[] old = _stack;
            _stack = new int[_stackIndex * 2];
            System.arraycopy(old, 0, _stack, 0, _stackIndex);
        }
        _stack[_stackIndex++] = _bufferg.getTranslateX();
        _stack[_stackIndex++] = _bufferg.getTranslateY();
        _stack[_stackIndex++] = _bufferg.getClipX();
        _stack[_stackIndex++] = _bufferg.getClipY();
        _stack[_stackIndex++] = _bufferg.getClipWidth();
        _stack[_stackIndex++] = _bufferg.getClipHeight();
    }

    public void popMatrix() {
        if (_stackIndex > 0) {
            _stackIndex -= 6;
            int translateX = _stack[_stackIndex++];
            int translateY = _stack[_stackIndex++];
            _bufferg.translate(translateX - _bufferg.getTranslateX(), translateY - _bufferg.getTranslateY());
            int clipX = _stack[_stackIndex++];
            int clipY = _stack[_stackIndex++];
            int clipWidth = _stack[_stackIndex++];
            int clipHeight = _stack[_stackIndex++];
            _bufferg.setClip(clipX, clipY, clipWidth, clipHeight);
            _stackIndex -= 6;
        }
    }

    public void resetMatrix() {
        _stackIndex = 0;
        _bufferg.translate(-_bufferg.getTranslateX(), -_bufferg.getTranslateY());
        _bufferg.setClip(0, 0, width, height);
    }

    public void translate(int x, int y) {
        _bufferg.translate(x, y);
    }

    public final int random(int value1) {
        return random(0, value1);
    }

    public final int random(int value1, int value2) {
        if (_random == null) {
            _random = new Random();
        }
        int min = Math.min(value1, value2);
        int range = Math.abs(value2 - value1) + 1;

        return min + Math.abs((_random.nextInt() % range));
    }

    public final byte[] loadBytes(String filename) {
        byte [] result = null ;
        try {
            RecordStore store = null;
            try {
                String name = filename;
                if (name.length() > 32) {
                    name = name.substring(0, 32);
                }
                store = RecordStore.openRecordStore(name, false);
                return store.getRecord(1);
            } catch (RecordStoreNotFoundException rsnfe) {
            } finally {
                if (store != null) {
                    store.closeRecordStore();
                }
            }
        } catch (Exception e) {
            //
        }
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(filename);
            if (is != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int bytesRead = is.read(buffer);
                while (bytesRead >= 0) {
                    baos.write(buffer, 0, bytesRead);
                    bytesRead = is.read(buffer);
                }
                result = baos.toByteArray();
            } else {
                result = new byte[0];
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            //
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                }
            }
        }
        return result;
    }

    public final String[] loadStrings(String filename) {
        try {
            RecordStore store = null;
            try {
                String name = filename;
                if (name.length() > 32) {
                    name = name.substring(0, 32);
                }
                store = RecordStore.openRecordStore(name, false);
                int numRecords = store.getNumRecords();
                String[] strings = new String[numRecords];
                for (int i = 0; i < numRecords; i++) {
                    byte[] data = store.getRecord(i + 1);
                    if (data != null) {
                        strings[i] = new String(data);
                    } else {
                        strings[i] = "";
                    }
                }
                return strings;
            } catch (RecordStoreNotFoundException rsnfe) {
            } finally {
                if (store != null) {
                    store.closeRecordStore();
                }
            }
        } catch (Exception e) {
            //
        }
        Vector v = new Vector();
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(filename);
            if (is != null) {
                Reader r = new InputStreamReader(is);

                int numStrings = 0;

                StringBuffer buffer = new StringBuffer();
                int input = r.read();
                while (true) {
                    if ((input < 0) || (input == '\n')) {
                        String s = buffer.toString().trim();
                        if (s.length() > 0) {
                            numStrings++;
                            v.addElement(s);
                        }
                        buffer.delete(0, Integer.MAX_VALUE);

                        if (input < 0) {
                            break;
                        }
                    } else {
                        buffer.append((char) input);
                    }

                    input = r.read();
                }
            }
        } catch (Exception e) {
            // 
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                }
            }
        }
        String[] strings = new String[v.size()];
        v.copyInto(strings);

        return strings;
    }

    public final void saveBytes(String filename, byte[] data) {
        //// max 32 char names on recordstores
        if (filename.length() > 32) {
            //
        }
        try {
            try {
                RecordStore.deleteRecordStore(filename);
            } catch (RecordStoreNotFoundException rsnfe) {
            }
            RecordStore store = RecordStore.openRecordStore(filename, true);
            store.addRecord(data, 0, data.length);
            store.closeRecordStore();
        } catch (Exception e) {
            // 
        }
    }

    public final void saveStrings(String filename, String[] strings) {
        //// max 32 char names on recordstores
        if (filename.length() > 32) {
            // 
        }
        try {
            //// delete recordstore, if it exists
            try {
                RecordStore.deleteRecordStore(filename);
            } catch (RecordStoreNotFoundException rsnfe) {
            }
            //// create new recordstore
            RecordStore store = RecordStore.openRecordStore(filename, true);
            //// add each string as a record
            byte[] data;
            for (int i = 0, length = strings.length; i < length; i++) {
                data = strings[i].getBytes();
                store.addRecord(data, 0, data.length);
            }
            store.closeRecordStore();
        } catch (Exception e) {
            //
        }
    }

    public InputStream openStream(String fileName) {
        try {
            return getClass().getResourceAsStream("/" + fileName);
        } catch(Exception e) {
           return null;
        }
    }

    public PFont loadFont(String fontname, color fgColor, color bgColor) {
        return new PFont(fontname, fgColor, bgColor);
    }

    public PFont loadFont(String fontname, color fgColor) {
        return new PFont (fontname, fgColor, new color(0x00FFFFFF));
    }

    public PFont loadFont(String fontname) {
        return new PFont(fontname, new color(0x00FFFFFF), new color(0x00FFFFFF));
    }

    public PFont loadFont() {
        return new PFont ( Font.getDefaultFont() );
    }

    public PFont loadFont(int face, int style, int size) {
        return new PFont(Font.getFont(face, style, size));
    }

    public void textFont (PFont font)
    {
        _defaultFont = font ;
    }

    public String[] textWrap(String data, int width) {
        return textWrap(data, width, Integer.MAX_VALUE);
    }

    public String[] textWrap(String data, int width, int height) {

        //// calculate max number of lines that will fit in height
        int maxlines = height / _defaultFont.getHeight();
        //// total number of chars in text
        int textLength = data.length();
        //// current index into text;
        int i = 0;
        //// working character
        char c;
        //// vector of lines
        Vector lines = new Vector();
        //// current line
        char[] line = new char[256];
        //// number of characters in current line
        int lineLength;
        //// index of last whitespace break point in current line
        int last;
        //// width of current line
        int lineWidth;
        while (i < textLength) {
            c = data.charAt(i);
            //// start at first non-whitespace character
            if (c != ' ') {
                //// at first non-whitespace character, start building up line
                lineLength = 0;
                last = -1;
                lineWidth = 0;
                while (lineWidth <= width) {
                    if (i == textLength) {
                        last = lineLength;
                        break;
                    }
                    c = data.charAt(i);
                    i++;
                    line[lineLength] = c;
                    lineLength++;
                    if (c == ' ') {
                        last = lineLength - 1;
                        while ((i < textLength) && (data.charAt(i) == ' ')) {
                            i++;
                            line[lineLength] = ' ';
                            lineLength++;
                        }
                    } else if (c == '\n') {
                        last = lineLength - 1;
                        break;
                    }
                    lineWidth = _defaultFont.charsWidth(line, 0, lineLength);
                }
                if (last >= 0) {
                    //// take chars up to last break point
                    lines.addElement(new String(line, 0, last));
                    i -= lineLength - last;
                } else {
                    //// rare case of very long words (i.e. urls) that can't fit on one line, just split
                    lines.addElement(new String(line, 0, lineLength - 1));
                    i = i - 2;
                }
            }
            //// check if reached max number of lines
            if (lines.size() == maxlines) {
                break;
            }
            //// increment to next character
            i++;
        }
        //// finally, copy into array and return
        String[] array = new String[lines.size()];
        lines.copyInto(array);
        return array;
    }

    public int textWidth(String data) {
        return _defaultFont.stringWidth(data);
    }

    public void textAlign (int mode)
    {
        _textAlignMode = mode ;
    }

    public void text(String text, int x, int y) {
        text(text, x, y, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public void text(String text, int x, int y, int width, int height) {
        String[] data = textWrap(text, width, height);

        int currentColor = _bufferg.getColor();
        _bufferg.setColor(_defaultFont.color);

        //// save current clip and apply clip to bounding area
        pushMatrix();
        _bufferg.setClip(x, y, width, height);
        
        //// adjust starting baseline so that text is _contained_ within the bounds
        int textX = x;
        //y += textFont.getBaseline();

        String line;
        for (int i = 0, length = data.length; i < length; i++) {
            line = data[i];
            //// calculate alignment within bounds
            switch (_textAlignMode) {
                case CENTER:
                    textX = x + ((width - textWidth(line)) >> 1);
                    break;
                case RIGHT:
                    textX = x + width - textWidth(line);
                    break;
            }
            _defaultFont.draw(_bufferg, line, textX, y, LEFT);
            y += _defaultFont.getHeight();
        }
        //// restore clip
        popMatrix();

        _bufferg.setColor(currentColor);
    }

    



    

}
