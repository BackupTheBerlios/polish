package de.enough.polish.processing;

import de.enough.polish.util.BitMapFont;
import de.enough.polish.util.BitMapFontViewer;
import java.io.InputStream;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * Placeholder class, for compatibility with existing Mobile Processing scripts.
 *
 * @author Ovidiu
 */
public class PFont {

    public Font platformFont = null ;
    public BitMapFont bitmapFont = null ;
    public int color = 0x000000;
    public int bgColor;

    public PFont(Font font)
    {
        platformFont = font ;
    }

    public PFont(String fontUrl, Color textColor, Color bgColor)
    {
        this.color = textColor.color;
        this.bgColor = bgColor.color;
        bitmapFont = BitMapFont.getInstance(fontUrl);
    }

    public int getHeight()
    {
        if ( platformFont != null )
        {
            return platformFont.getHeight();
        }
        else
        {
            return bitmapFont.getFontHeight();
        }
    }

    public int charsWidth(char[] ch, int offset, int length) {
        int result = 0;
        int temp;
        if (platformFont != null) {
            result = platformFont.charsWidth(ch, offset, length);
        } else {
            for (int i = offset, end = offset + length; i < end; i++) {
               temp = bitmapFont.charWidth(ch[i]);
               if ( temp == -1 )
               {
                   result += bitmapFont.charWidth('a');
               }
               else
               {
                   result += temp;
               }
            }
        }
        return result;
    }

    public int charWidth(char ch) {
        int result = 0;
        int temp;
        if (platformFont != null) {
            result = platformFont.charWidth(ch);
        } else {
               temp = bitmapFont.charWidth(ch);
               if ( temp == -1 )
               {
                   result += bitmapFont.charWidth('a');
               }
               else
               {
                   result += temp;
               }
        }
        return result;
    }

    public int stringWidth(String str) {
        int result;
        if (platformFont != null) {
            result = platformFont.stringWidth(str);
        } else {
            result = substringWidth(str, 0, str.length());
        }
        return result;
    }

    public int substringWidth(String str, int offset, int length) {
        int result = 0;
        int temp ;
        if (platformFont != null) {
            result = platformFont.substringWidth(str, offset, length);
        } else {
            int index;
            for (int i = offset, end = offset + length; i < end; i++) {
                temp = bitmapFont.charWidth(str.charAt(i));
               if ( temp == -1 )
               {
                   result += bitmapFont.charWidth('a');
               }
               else
               {
                   result += temp;
               }
            }
        }
        return result;
    }

    public void draw(Graphics g, String str, int x, int y, int textAlign) {
        if (platformFont != null) {
            //// system font
            g.setFont(platformFont);
            int align = Graphics.TOP;
            if (textAlign == ProcessingInterface.CENTER) {
                align |= Graphics.HCENTER;
            } else if (textAlign == ProcessingInterface.RIGHT) {
                align |= Graphics.RIGHT;
            } else {
                align |= Graphics.LEFT;
            }
            g.drawString(str, x, y - platformFont.getBaselinePosition(), align);
        } else {
            if (textAlign != ProcessingInterface.LEFT) {
                int width = stringWidth(str);
                if (textAlign == ProcessingInterface.CENTER) {
                    x -= width >> 1;
                } else if (textAlign == ProcessingInterface.RIGHT) {
                    x -= width;
                }
            }
            
            BitMapFontViewer bfv = bitmapFont.getViewer(str, color);
            bfv.paint(x, y, g);

        }
    }

}
