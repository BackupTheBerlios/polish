/*
 * Class name : SplashFrame
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 05-Dec-2005
 * 
 * Copyright (c) 2005-2006, Jim McLachlan (jim@oss-ltd.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.ossltd.mdevinf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Displays the Mobile Device Information splash screen.  This is displayed
 * in it's own thread and a "Loading ..." label updated until the rest of the
 * application data has been loaded. 
 * @author Jim McLachlan
 * @version 1.0
 */
public class SplashFrame extends JFrame implements Runnable
{
    /**
     * Constant defining the border width (note that the original border size
     * of 10 was inadequate for the Web Start version due to the Web Start
     * window decoration.
     */
    private final int borderWidth = 30;
    
    /**
     * Constant defining the width of the splash screen (image size + border
     * width).
     */
    private final int width = 451 + (borderWidth << 1);
    
    /**
     * Constant defining the height of the splash screen (image height + border
     * width).
     */
    private final int height = 326 + (borderWidth << 1);
    
    /** Singleton instance reference for this class. */
    private static SplashFrame instance = null;
    
    /** Progress count (cycles round from 0 .. 10). */
    private int progress;
    
    /** Flag indicating that the application is still loading other data. */
    private boolean loading;
    
    /** Stores the image for display. */
    private Image splashImage;
    
    /** Stores the panel that needs to be repainted as progress increases. */
    private SplashPanel splashPanel;

    /**
     * Creates and initialises the splash screen.
     */
    private SplashFrame()
    {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        MediaTracker mediaTracker = new MediaTracker(this);
        URL imageURL = getClass().getResource("/splash.jpg");
        splashImage = defaultToolkit.getImage(imageURL);
        Dimension screenSize = defaultToolkit.getScreenSize();
        
        setUndecorated(true);
        
        setSize(new Dimension(width, height));
        setLocation((screenSize.width - width) >> 1,
                    (screenSize.height - height) >> 1);
        
        progress = -1;
        loading = false;

        mediaTracker.addImage(splashImage, 0);
        try 
        {
            mediaTracker.waitForID(0);
        }
        catch(InterruptedException ie)
        {
            System.out.println("Cannot track image load.");
        }
        
        splashPanel = new SplashPanel();
        add(splashPanel, BorderLayout.CENTER);
    }

    /**
     * Called by the main application when all data has been loaded.
     */
    public void loaded()
    {
        loading = false;
    }
    
    /**
     * Updates the splash screen repeatedly until the application has finished
     * loading it's data.
     */
    public void run()
    {
        loading = true;
        progress = 0;
        setVisible(true);

        while (loading)
        {
            try
            {
                Thread.sleep(500L);
            }
            catch (InterruptedException ie) {/**/}
            
            progress = (progress + 1) % 10;

            repaint();
        }
        
        setVisible(false);
    }
    
    /**
     * Accessor
     * @return the singleton instance of this class
     */
    public static SplashFrame getInstance()
    {
        if (instance == null)
        {
            instance = new SplashFrame();
        }
        
        return (instance);
    }
    
    /**
     * Extends the JPanel class to override the paintComponent method for
     * refreshing the splash screen as data loads in the background.
     * @author Jim McLachlan
     * @version 1.0
     */
    class SplashPanel extends JPanel
    {
        /** Stores the string "Loading ." + up to 10 further dots. */
        private StringBuffer loadingString;
        
        /** Stores the bold font for displaying the "Loading ..." text. */
        private Font boldFont;
        
        /** Stores the x offset position of the "Loading ..." string. */
        private int loadOffset;
        
        /**
         * Repaints the panel whenever a change in the loading status occurs.
         * @param graphics the <code>Graphics</code> object to use
         */
        public void paintComponent(Graphics graphics)
        {
            if (boldFont == null)
            {
                boldFont = graphics.getFont().deriveFont(Font.BOLD);
                FontMetrics metrics = graphics.getFontMetrics(boldFont);
                loadOffset = width -
                             (metrics.stringWidth("Loading data ..........") +
                              (borderWidth << 1));
                             
            }
            
            loadingString = new StringBuffer("Loading data .");
            for (int i = 0; i < progress; i++)
            {
                loadingString.append('.');
            }
            
            super.paintComponent(graphics);
            graphics.drawImage(splashImage, borderWidth, borderWidth, this);
            graphics.setColor(Color.black);
            graphics.setFont(boldFont);
            graphics.drawString(loadingString.toString(),
                                loadOffset,
                                height - (8 + borderWidth));
        }
    }
}
