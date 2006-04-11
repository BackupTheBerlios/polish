/*
 * Class name : ResultsPanelManager
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 08-Nov-2005
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

package com.ossltd.mdevinf.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.StyledDocument;

import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.MatchedDevice;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Manages the layout and updates to the Results tab
 * @author Jim McLachlan
 * @version 1.0
 */
public class ResultsPanelManager
{
    /** Stores a reference to the singleton instance of this class. */
    private static ResultsPanelManager instance = null;
    
    /** Stores a reference to the Results <code>JPanel</code> */
    private JPanel resultsPanel;
    
    /** Reference to the text component in which search results are written. */
    private JTextPane resultArea;
    
    /** Reference to the list of devices that matched the search criteria. */
    private JList resultList;
    
    /** Reference to the popup menu for the results list. */
    private JPopupMenu resultsPopupMenu;
    
    /**
     * Stores the results in a simple text area for display to allow
     * copying of the text.
     */
    private JTextArea resultsAsText;
    
    /**
     * Creates the popup menu for the results list.
     */
    private void createPopup()
    {
        JMenuItem displayItem = new JMenuItem("Display as text list");
        displayItem.setToolTipText("Allows you to copy the results to your " +
                                   "clipboard"); 
        displayItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                JScrollPane resultsScroller = new JScrollPane(resultsAsText);
                resultsScroller.setPreferredSize(new Dimension(300, 200));
                JOptionPane.showMessageDialog(resultsPanel,
                                              resultsScroller,
                                              "Results as text",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JMenuItem editDevice = new JMenuItem(new EditAction());
        editDevice.setToolTipText("Edit the currently selected device");

        resultsPopupMenu = new JPopupMenu("Results List");
        resultsPopupMenu.add(displayItem);
        if (WURFLInfo.getInstance().getMDevInfWSIcon() == null)
        {
            resultsPopupMenu.addSeparator();
            resultsPopupMenu.add(new SaveSearchAction());
            resultsPopupMenu.addSeparator();
            resultsPopupMenu.add(editDevice);
        }
    }
    
    /**
     * Creates the Results panel
     */
    private ResultsPanelManager()
    {
        createPopup();
        resultArea = new JTextPane();
        resultArea.setEditable(false);
        StyledDocument doc = resultArea.getStyledDocument();
        DocStyler.prepareStyledDocument(doc);

        resultList = new JList();
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        resultList.addListSelectionListener(new ListSelectionListener(){
            private String previousMatch = "";
            
            public void valueChanged(ListSelectionEvent e)
            {
                MatchedDevice selected = (MatchedDevice)resultList.
                                                             getSelectedValue();
                if ((selected != null) &&
                    (!selected.toString().equals(previousMatch)))
                {
                    previousMatch = selected.toString();
                    
                    Device device = WURFLInfo.getInstance().
                                    findDeviceForMakeModel(selected.getMake(),
                                                           selected.getModel(),
                                                           false);
                    
                    if (device != null)
                    {
                        TabbedPaneManager.getInstance().deviceSelected(device);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(
                                                     resultsPanel,
                                                     "No make of '" +
                                                     selected.getMake() +
                                                     "' found in the list!",
                                                     "Error",
                                                     JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        resultList.addMouseListener(new MouseAdapter(){
            private void showPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    resultsPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            public void mousePressed(MouseEvent e)
            {
                showPopup(e);
            }
            
            public void mouseReleased(MouseEvent e)
            {
                showPopup(e);
            }
        });
        
        JPanel resPanel = new JPanel(new BorderLayout());
        resPanel.add(resultArea, BorderLayout.NORTH);
        resPanel.add(resultList, BorderLayout.CENTER);

        resultsPanel = new JPanel(new GridLayout(1, 1));
        JScrollPane resultsScrollPane = new JScrollPane(resPanel);
        resultsPanel.add(resultsScrollPane);
    }
    
    /**
     * Accessor
     * @return a reference to the Results panel
     */
    public JPanel getResultsPanel()
    {
        return (resultsPanel);
    }

    /**
     * Clears the results text area and result list.
     */
    public void clearResults()
    {
        resultArea.setText("");
        resultList.setListData(new Vector());
    }
    
    /**
     * Accessor
     * @return the styled document for the text area
     */
    public StyledDocument getDocument()
    {
        return (resultArea.getStyledDocument());
    }
    
    /**
     * Mutator
     * @param results the results to place in the list
     * @param resultsHeader the text to prepend to the plain text results data
     */
    public void setResultsList(Vector<MatchedDevice> results,
                               String resultsHeader)
    {
        resultList.setListData(results);
        resultsAsText = new JTextArea(resultsHeader);
        resultsAsText.setEditable(false);
        for (MatchedDevice dev : results)
        {
            resultsAsText.append(dev.toString());
            resultsAsText.append("\n");
        }
    }
    
    /**
     * Scrolls the result area to the top.
     */
    public void scrollToTop()
    {
        resultArea.scrollRectToVisible(new Rectangle(1, 1, 2, 2));
    }
    
    /**
     * Accessor
     * @return a reference to the singleton instance of this class
     */
    public static ResultsPanelManager getInstance()
    {
        if (instance == null)
        {
            instance = new ResultsPanelManager();
        }
        
        return (instance);
    }
}
