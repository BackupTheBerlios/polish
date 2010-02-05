/*
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

import de.enough.polish.ui.Command;
import javax.microedition.lcdui.Graphics;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;

/**
 *
 * @author Ovidiu Iliescu
 */
public class ProcessingScreen extends Screen implements ProcessingContextContainerInterface {

    protected Command cmd = new Command("", Command.ITEM, 0);
    protected String softkeyCommandText = null;
    ProcessingInterface context = null;

    public void initProcessingContext() {
        context.signalInitialization();
        context.setParent(this);
        context.signalHasFocus();
    }

    /**
     * Creates a new, empty <code>ProcessingScreen</code>.
     *
     * @param title the Form's title, or null for no title
     */
    public ProcessingScreen(String title, ProcessingInterface context) {
        super(title, null, true);
        this.context = context;
        initProcessingContext();
    }

    /**
     * Creates a new, empty <code>ProcessingScreen</code>.
     *
     * @param title the Form's title, or null for no title
     * @param style the style of this form
     */
    public ProcessingScreen(String title, ProcessingInterface context, Style style) {
        super(title, style, true);
        this.context = context;
        initProcessingContext();
    }

    /**
     * Checks if a given pixel (relative to the item) is within the bounds
     * of the Processing canvas.
     * @param x
     * @param y
     * @return true if the pixel is within bounds, false otherwise
     */
    protected boolean isWithinBounds(int x, int y) {
        if ((x < contentX) || (x > contentWidth + contentX) || (y < contentY) || (y > contentHeight + contentY))
        {
            return false;
        }
        return true;

    }

    protected void paintScreen(Graphics g) {

        context.signalSizeChange(contentWidth, contentHeight);


        // Draw the processing buffer
        if ( context.isDrawingTransparent() == false )
       {
        g.drawImage(context.getBuffer(), contentX, contentY, Graphics.TOP | Graphics.LEFT );
       }
       else
       {
          context.getTransparentRgbImage().paint(contentX, contentY, g);
       }
    }

    protected boolean handleKeyPressed(int keyCode, int gameAction) {

        context.signalKeyPressed(keyCode);
        return context.areKeypressesCaptured();

    }

    protected boolean handleKeyReleased(int keyCode, int gameAction) {

        context.signalKeyReleased(keyCode);
        return context.areKeypressesCaptured();
    }

    protected boolean handleKeyRepeated(int keyCode, int gameAction) {

        context.signalKeyPressed(keyCode);
        return context.areKeypressesCaptured();

    }

    protected boolean handlePointerPressed(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return context.arePointerEventsCaptured();
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerPressed(x, y);
        return context.arePointerEventsCaptured();
    }

    protected boolean handlePointerReleased(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return false;
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerReleased(x, y);
        return context.arePointerEventsCaptured();
    }

    protected boolean handlePointerDragged(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return context.arePointerEventsCaptured();
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerDragged(x, y);
        return context.arePointerEventsCaptured();
    }

    public boolean handlePointerTouchDown(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return context.arePointerEventsCaptured();
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerPressed(x, y);
        return context.arePointerEventsCaptured();
    }

    public boolean handlePointerTouchUp(int x, int y) {
        if (!isWithinBounds(x, y))
        {
            return context.arePointerEventsCaptured();
        }
        x -= contentX;
        y -= contentY;
        context.signalPointerReleased(x, y);
        return context.arePointerEventsCaptured();
    }

    protected boolean handleCommand(Command cmd) {
        context.signalSoftkeyPressed(cmd.getLabel());
        return super.handleCommand(cmd);
    }

    protected String createCssSelector() {
        return "processing";
    }

    public void processingRequestRepaint() {

        repaint();
    }

    public void hideNotify() {
        context.signalLostFocus();
        super.hideNotify();
    }

    public void releaseResources() {
        context.signalDestroy();
        super.releaseResources();
    }

    public void setSoftkey(String text) {
        softkeyCommandText = text;

        // If requested by the Processing code, add an Item command
        if (softkeyCommandText != null)
        {
            removeCommand(cmd);
            cmd = new Command(softkeyCommandText, Command.ITEM, 0);
            addCommand(cmd);
        }
        else
        {
            removeCommand(cmd);
        }

    }
}
