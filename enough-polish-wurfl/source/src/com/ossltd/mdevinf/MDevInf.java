/*
 * Class name : MDevInf
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 19-Oct-2005
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.ossltd.mdevinf.model.ValidationResult;
import com.ossltd.mdevinf.model.WURFLInfo;
import com.ossltd.mdevinf.ui.AboutDialog;
import com.ossltd.mdevinf.ui.ConsolidateAction;
import com.ossltd.mdevinf.ui.DocStyler;
import com.ossltd.mdevinf.ui.PatchChooser;
import com.ossltd.mdevinf.ui.ResultsPanelManager;
import com.ossltd.mdevinf.ui.TabbedPaneManager;
import com.ossltd.mdevinf.ui.UAPanelManager;
import com.ossltd.mdevinf.ui.WURFLInfoDialog;


/**
 * Entry point and user interface manager for the mDevInf application. 
 * @author Jim McLachlan
 * @version 1.0
 */
public class MDevInf
{
    /** Constant defining the version number of the application. */
    private static final String version = "1.1";
    
    /** Reference to the Info icon. */
    private ImageIcon infoIcon;
    
    /** Reference to the main display window for the application. */
    private JFrame mainFrame;
    
    /**
     * Creates a menu bar with the following options:
     * <ul>
     * <li>File</li>
     * <ul>
     * <li>Select Patch Files...</li>
     * <li>Consolidate...</li>
     * <ul>
     * <li>WURFL...</li>
     * <li>Patch file...</li>
     * </ul>
     * <li>Exit</li>
     * </ul>
     * <li>Tools</li>
     * <ul>
     * <li>Clear Search Results</li>
     * <li>Clear User Agent Results</li>
     * <li>Validate WURFL</li>
     * </ul>
     * <li>Help</li>
     * <ul>
     * <li>WURFL Information...</li>
     * <li>About...</li></ul>
     * </ul> 
     * @return the <code>JMenuBar</code> for the application.
     * @throws MDevInfException if a problem occurs whilst loading the WURFL
     * data (eg. if the WURFLInfo object is returned as <code>null</code>)
     */
    private JMenuBar createMenuBar() throws MDevInfException
    {
        if (WURFLInfo.getInstance() == null)
        {
            throw (new MDevInfException(WURFLInfo.loadingProblem));
        }
        
        JMenuItem fileChange = new JMenuItem("Select Patch Files...");
        fileChange.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                PatchChooser pc = new PatchChooser(mainFrame);
                pc.setVisible(true);
                
                if (pc.isChanged())
                {
                    new Thread(new Runnable(){
                        public void run()
                        {
                            mainFrame.setVisible(false);
                            
                            new Thread(SplashFrame.getInstance()).start();
                            
                            ConfigurationManager.getInstance().saveConfig();

                            WURFLInfo.destroy();
                            TabbedPaneManager.getInstance().
                                                           updateAfterChanges();
                            
                            SplashFrame.getInstance().loaded();
                            
                            mainFrame.setVisible(true);
                        }

                    }).start();
                }
            }
        });

        JMenuItem consolidateWURFL = new JMenuItem("WURFL...");
        consolidateWURFL.addActionListener(new ConsolidateAction(mainFrame,
                                                                 "WURFL...",
                                                                 "wurfl.xml",
                                                                 true));
        
        JMenuItem consolidatePatch = new JMenuItem("Patch file...");
        consolidatePatch.addActionListener(
                                       new ConsolidateAction(mainFrame,
                                                             "Patch file...",
                                                             "wurfl_patch.xml",
                                                             false));
        
        JMenu fileConsolidate = new JMenu("Consolidate...");
        fileConsolidate.add(consolidateWURFL);
        fileConsolidate.add(consolidatePatch);
        
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });
        
        JMenu fileMenu = new JMenu("File");
        if (WURFLInfo.getInstance().getMDevInfWSIcon() == null)
        {
            fileMenu.add(fileChange);
            fileMenu.addSeparator();
            fileMenu.add(fileConsolidate);
            fileMenu.addSeparator();
        }
        fileMenu.add(fileExit);
        
        JMenuItem toolsClearResults = new JMenuItem("Clear Search Results");
        toolsClearResults.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                ResultsPanelManager.getInstance().clearResults();
            }
        });
        
        JMenuItem toolsClearUAs = new JMenuItem("Clear User Agent Results");
        toolsClearUAs.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                UAPanelManager.getInstance().clearListData();
            }
        });
        
        JMenuItem toolsValidateWURFL = new JMenuItem("Validate WURFL");
        toolsValidateWURFL.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                Vector<ValidationResult> result = WURFLInfo.getInstance().
                                                                validateWURFL();
                JTextPane validationResult = new JTextPane();
                validationResult.setPreferredSize(new Dimension(400, 200));
                StyledDocument doc = validationResult.getStyledDocument();
                DocStyler.prepareStyledDocument(doc);
                String r = null;

                for (ValidationResult curResult : result)
                {
                    try
                    {
                        doc.insertString(doc.getLength(),
                                         curResult.getTestName(),
                                         doc.getStyle("bold"));
                        r = curResult.getResult();
                        if (r.endsWith("OK"))
                        {
                            doc.insertString(doc.getLength(),
                                             r,
                                             doc.getStyle("boldGreen"));
                        }
                        else
                        {
                            doc.insertString(doc.getLength(),
                                             r,
                                             doc.getStyle("boldRed"));
                        }
                        doc.insertString(doc.getLength(),
                                         "\n",
                                         doc.getStyle("bold"));
                    }
                    catch (BadLocationException ble)
                    {
                        System.err.println("BadLocation: " + ble.toString());
                    }
                }
                
                JOptionPane.showMessageDialog(mainFrame,
                                              new JScrollPane(validationResult),
                                              "WURFL Validation Results",
                                              JOptionPane.INFORMATION_MESSAGE,
                                              infoIcon);
                
            }
        });
        
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(toolsClearResults);
        toolsMenu.add(toolsClearUAs);
        toolsMenu.addSeparator();
        toolsMenu.add(toolsValidateWURFL);

        JMenuItem helpWURFLVersion = new JMenuItem("WURFL Information...");
        helpWURFLVersion.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                ImageIcon wurflIcon = null;
                java.net.URL imageURL = getClass().
                                                 getResource("/wurfl_logo.png");
                if (imageURL != null)
                {
                    wurflIcon = new ImageIcon(imageURL);
                }
                
                WURFLInfoDialog info = new WURFLInfoDialog(mainFrame,
                                                           wurflIcon);
                info.setVisible(true);
            }
        });
        
        JMenuItem helpAbout = new JMenuItem("About...");
        helpAbout.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                ImageIcon mDevAboutIcon = WURFLInfo.getInstance().
                                                             getMDevInfWSIcon();
                if (mDevAboutIcon == null)
                {
                    java.net.URL imageURL = getClass().
                                              getResource("/mDevInfIcon64.png");
                    if (imageURL != null)
                    {
                        mDevAboutIcon = new ImageIcon(imageURL);
                    }
                }
                
                AboutDialog about = new AboutDialog(mainFrame,
                                                    mDevAboutIcon,
                                                    version);
                about.setVisible(true);
            }
        });
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(helpWURFLVersion);
        helpMenu.addSeparator();
        helpMenu.add(helpAbout);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        return (menuBar);
    }
    
    /**
     * Create and configure the JFrame container and display it.
     */
    private void init()
    {
        new Thread(SplashFrame.getInstance()).start();
       
        ImageIcon mDevInfIcon = new ImageIcon();
        
        java.net.URL imageURL = getClass().getResource("/mDevInfIcon.png");
        if (imageURL != null)
        {
            mDevInfIcon = new ImageIcon(imageURL);
        }
        
        imageURL = getClass().getResource("/info16.png");
        if (imageURL != null)
        {
            infoIcon = new ImageIcon(imageURL);
        }
        
        mainFrame = new JFrame("Mobile Device Information v" + version);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setIconImage(mDevInfIcon.getImage());
        
        try
        {
            mainFrame.setJMenuBar(createMenuBar());
            mainFrame.getContentPane().add(TabbedPaneManager.getInstance().
                                                               getTabbedPane());
            mainFrame.setSize(new Dimension(800, 560));

            SplashFrame.getInstance().loaded();
            
            mainFrame.setVisible(true);
        }
        catch (MDevInfException mdie)
        {
            SplashFrame.getInstance().loaded();
            
            JOptionPane.showMessageDialog(null,
                                          mdie.getMessage(),
                                          "Problem!",
                                          JOptionPane.ERROR_MESSAGE);
            mainFrame = null;
            System.exit(-1);
        }
    }
    
    /**
     * Entry point of the application.  If a WURFL patch file is provided on 
     * the command line, this is passed to the <code>init()</code> method.
     * @param args - usused but mandatory!
     */
    public static void main(String[] args)
    {
        new MDevInf().init();
    }
}
