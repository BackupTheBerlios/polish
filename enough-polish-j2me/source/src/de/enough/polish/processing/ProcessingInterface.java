//#condition polish.usePolishGui
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.processing;

import de.enough.polish.util.RgbImage;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Ovidiu Iliescu
 */
public interface ProcessingInterface {

    /**
     * A vector that keeps track of all objects that implement ProcessingInterface
     */
    public static Vector processingContextObjects = new Vector();

    /**
     * Non processing methods that are used by the system.
     *
     */
    public void signalSizeChange(int width, int height);

    public void signalPointerDragged(int x, int y);

    public void signalPointerReleased(int x, int y);

    public void signalPointerPressed(int x, int y);

    public void signalSoftkeyPressed(String label);

    public void signalKeyPressed(int keyCode);

    public void signalKeyReleased(int keyCode);

    public void signalApplicationSuspend();

    public void signalApplicationResume();

    public void signalDestroy();

    public void signalInitialization();

    public void signalHasFocus();

    public void signalLostFocus();

    public Image getBuffer();

    public String getSoftkeyLabel();

    public long getLastFrameTime();

    public long getIntervalBetweenFrames();

    public boolean checkForRefresh();

    public boolean isLooping();

    public void setParent ( ProcessingContextContainerInterface parent);

    public void setPointerCoordinates ( int x, int y);

    public void setKeyAndKeyCode (char key, int keyCode);

    public void executeRefresh(boolean alsoUpdateLastFrameTime);

    public void triggerRepaint();

    public boolean areKeypressesCaptured();

    public boolean arePointerEventsCaptured();

    public boolean isDrawingTransparent();

    public RgbImage getTransparentRgbImage();

    public int getTransparentColor();



    /**
     * J2ME Polish extension methods that shoud be implemented
     */
    public void focus();

    public void lostFocus();

    public void captureKeyPresses();

    public void releaseKeyPresses();

    public void capturePointerEvents();

    public void releasePointerEvents();

    public void repaintBackground();

    public void dontRepaintBackground();

    public void transparentDrawing();

    public void opaqueDrawing();

    public void setTransparentColor ( color color );

    /*
     * Variables that need defining :
     *
     * width, height
     *
     * key, keycode, rawKeyCode
     *
     * pointerX, pointerY
     *
     */


        // color modes
    public static final int RGB = 1;
    public static final int HSB = 2;

    // Shape modes
    public static final int POINTS          = 0;
    public static final int LINES           = 1;
    public static final int LINE_STRIP      = 2;
    public static final int LINE_LOOP       = 3;
    public static final int TRIANGLES       = 4;
    public static final int TRIANGLE_STRIP  = 5;
    public static final int QUADS           = 6;
    public static final int QUAD_STRIP      = 7;
    public static final int POLYGON         = 8;

    // Drawing style and positions
    public static final int CENTER          = 0;
    public static final int CENTER_RADIUS   = 1;
    public static final int CORNER          = 2;
    public static final int CORNERS         = 3;

    // Keycode constants
    public static final int UP              = Canvas.UP;
    public static final int DOWN            = Canvas.DOWN;
    public static final int LEFT            = Canvas.LEFT;
    public static final int RIGHT           = Canvas.RIGHT;
    public static final int FIRE            = Canvas.FIRE;
    public static final int GAME_A          = Canvas.GAME_A;
    public static final int GAME_B          = Canvas.GAME_B;
    public static final int GAME_C          = Canvas.GAME_C;
    public static final int GAME_D          = Canvas.GAME_D;
    public static final int SOFTKEY1        = -6;
    public static final int SOFTKEY2        = -7;
    public static final int SEND            = -10;

    // Font related constants
    public static final int FACE_SYSTEM         = Font.FACE_SYSTEM;
    public static final int FACE_MONOSPACE      = Font.FACE_MONOSPACE;
    public static final int FACE_PROPORTIONAL   = Font.FACE_PROPORTIONAL;

    public static final int STYLE_PLAIN         = Font.STYLE_PLAIN;
    public static final int STYLE_BOLD          = Font.STYLE_BOLD;
    public static final int STYLE_ITALIC        = Font.STYLE_ITALIC;
    public static final int STYLE_UNDERLINED    = Font.STYLE_UNDERLINED;

    public static final int SIZE_SMALL          = Font.SIZE_SMALL;
    public static final int SIZE_MEDIUM         = Font.SIZE_MEDIUM;
    public static final int SIZE_LARGE          = Font.SIZE_LARGE;

    

    public int currentMemory();

    public int reportedMemory();

    public void framerate(int framerate);

    public void draw();

    public void redraw();

    public void loop();

    public void noLoop();

    public void destroy();

    public void suspend();

    public void resume();

    public void setup();

    public void exit();

    public void colorMode(int mode);

    public void colorMode(int mode, int range);

    public void colorMode(int mode, int range1, int range2, int range3);

    public color color(int gray);

    public color color(int gray, int alpha);

    public color color(int value1, int value2, int value3, int alpha);

    public void stroke(color whatColor);

    public void stroke(int gray);

    public void stroke(int param1, int param2, int param3);

    public void strokeWeight(int width);

    public void noStroke();

    public void fill(int gray);

    public void fill(color color);

    public void fill(int value1, int value2, int value3);

    public void noFill();

    public void background(int gray);

    public void background(color x);

    public void background(int value1, int value2, int value3);

    public void background(PImage img);

    public void line(int x1, int y1, int x2, int y2);

    public void bezier(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4);

    public void point(int x, int y);

    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3);

    public void quad(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4);

    public void rect(int x, int y, int width, int height);

    public void rectMode(int mode);

    public void ellipse(int x, int y, int width, int height);

    public void curve(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4);

    public void ellipseMode(int mode);

    public void beginShape(int mode);

    public void curveVertex(int x, int y);

    public void bezierVertex(int x1, int y1, int x2, int y2, int x3, int y3);

    public void vertex(int x, int y);

    public void endShape();

    public PImage loadImage(String filename);

    public PImage loadImage(byte[] data);

    public void image(PImage img, int x, int y);

    public void image(PImage img, int sx, int sy, int swidth, int sheight, int dx, int dy);

    public void softkey(String label);

    public void softkeyPressed(String label);

    public void keyPressed();

    public void keyReleased();

    public void multitap();

    public void noMultitap();

    public void multitapClear();

    public void multitapDeleteChar();

    public String textInput();

    public String textInput(String title, String text, int max);

    public void pointerDragged();

    public void pointerPressed();

    public void pointerReleased();

    public int millis();

    public int second();

    public int minute();

    public int hour();

    public int day();

    public int month();

    public int year();

    public void print(boolean data);

    public void print(byte data);

    public void print(char data);

    public void print(int data);

    public void print(Object data);

    public void print(String data);

    public void println(boolean data);

    public void println(byte data);

    public void println(char data);

    public void println(int data);

    public void println(Object data);

    public void println(String data);

    public int length(boolean[] array);

    public int length(byte[] array);

    public int length(char[] array);

    public int length(int[] array);

    public int length(Object[] array);

    public String join(String[] anyArray, String separator);

    public String join(int[] anyArray, String separator);

    public String join(int[] intArray, String separator, int digits);

    public String nf(int intValue, int digits);

    public String nfp(int intValue, int digits);

    public String[] split(String str, char delim);

    public String[] split(String str, String delim);

    public String trim(String str);

    public String[] append(String[] array, String element);

    public boolean[] append(boolean[] array, boolean element);

    public byte[] append(byte[] array, byte element);

    public char[] append(char[] array, char element);

    public int[] append(int[] array, int element);

    public void arraycopy(Object src, int srcPos, Object dest, int destPos, int length);

    public String[] concat(String[] array1, String[] array2);

    public boolean[] concat(boolean[] array1, boolean[] array2);

    public byte[] concat(byte[] array1, byte[] array2);

    public char[] concat(char[] array1, char[] array2);

    public int[] concat(int[] array1, int[] array2);

    public boolean[] contract(boolean[] array, int newSize);

    public byte[] contract(byte[] array, int newSize);

    public char[] contract(char[] array, int newSize);

    public int[] contract(int[] array, int newSize);

    public String[] contract(String[] array, int newSize);

    public boolean[] expand(boolean[] array);

    public boolean[] expand(boolean[] array, int newSize);

    public byte[] expand(byte[] array);

    public byte[] expand(byte[] array, int newSize);

    public char[] expand(char[] array);

    public char[] expand(char[] array, int newSize);

    public int[] expand(int[] array);

    public int[] expand(int[] array, int newSize);

    public String[] expand(String[] array);

    public String[] expand(String[] array, int newSize);

    public boolean[] reverse(boolean[] array);

    public byte[] reverse(byte[] array);

    public char[] reverse(char[] array);

    public int[] reverse(int[] array);

    public String[] reverse(String[] array);

    public boolean[] shorten(boolean[] array);

    public byte[] shorten(byte[] array);

    public char[] shorten(char[] array);

    public int[] shorten(int[] array);

    public String[] shorten(String[] array);

    public boolean[] subset(boolean[] array, int offset);

    public boolean[] subset(boolean[] array, int offset, int length);

    public byte[] subset(byte[] array, int offset);

    public byte[] subset(byte[] array, int offset, int length);

    public char[] subset(char[] array, int offset);

    public char[] subset(char[] array, int offset, int length);

    public int[] subset(int[] array, int offset);

    public int[] subset(int[] array, int offset, int length);

    public String[] subset(String[] array, int offset);

    public String[] subset(String[] array, int offset, int length);

    public boolean[] splice(boolean[] array, boolean value, int index);

    public boolean[] splice(boolean[] array, boolean[] array2, int index);

    public byte[] splice(byte[] array, byte value, int index);

    public byte[] splice(byte[] array, byte[] array2, int index);

    public char[] splice(char[] array, char value, int index);

    public char[] splice(char[] array, char[] array2, int index);

    public int[] splice(int[] array, int value, int index);

    public int[] splice(int[] array, int[] array2, int index);

    public String[] splice(String[] array, String value, int index);

    public String[] splice(String[] array, String[] array2, int index);

    public String str(boolean val);

    public String str(byte val);

    public String str(char val);

    public String str(int val);

    public String[] str(boolean[] val);

    public String[] str(byte[] val);

    public String[] str(char[] val);

    public String[] str(int[] val);

    public void pushMatrix();

    public void popMatrix();

    public void resetMatrix();

    public void translate(int x, int y); 

    public int abs(int value);

    public int max(int value1, int value2);

    public int min(int value1, int value2);

    public int sq(int value);

    public int pow(int base, int exponent);

    public int constrain(int value, int min, int max); 

    public int random(int value1);

    public int random(int value1, int value2);

    public int mul(int value1, int value2);

    public int div(int dividend, int divisor);

    public int itofp(int value1);

    public int fptoi(int value1);

    public int sqrt(int value_fp);

    public int dist(int x1, int y1, int x2, int y2);

    public int dist_fp(int x1, int y1, int x2, int y2);

    public int floor(int value1);

    public int ceil(int value1);

    public int round(int value1);

    public int radians(int angle);

    public int sin(int rad);

    public int cos(int rad);

    public int atan(int value1);

    public int atan2(int x, int y);

    public byte[] loadBytes(String filename);

    public String[] loadStrings(String filename);

    public void saveBytes(String filename, byte[] data);

    public void saveStrings(String filename, String[] strings);

    public InputStream openStream(String fileName);

    public PFont loadFont(String fontname, color fgColor, color bgColor) ;

    public PFont loadFont(String fontname, color fgColor) ;

    public PFont loadFont(String fontname) ;

    public PFont loadFont() ;

    public PFont loadFont(int face, int style, int size) ;

    public void textFont(PFont font) ;

    public String[] textWrap(String data, int width) ;

    public String[] textWrap(String data, int width, int height) ;

    public int textWidth(String data) ;

    public void textAlign(int mode);

    public void text(String text, int x, int y);

    public void text(String text, int x, int y, int width, int height);
}
