/*
 * Class name : SearchPanelManager
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.ossltd.mdevinf.ConfigurationManager;
import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.ComparisonDetails;
import com.ossltd.mdevinf.model.Description;
import com.ossltd.mdevinf.model.Group;
import com.ossltd.mdevinf.model.MatchedDevice;
import com.ossltd.mdevinf.model.SearchDetails;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Manages the layout and updates to the Search tab
 * @author Jim McLachlan
 * @version 1.0
 */
public class SearchPanelManager
{
    /** Stores the reference to the singleton instance of this class. */
    private static SearchPanelManager instance = null;
    
    /** Stores the reference to the Search <code>JPanel</code>. */
    private JPanel searchPanel;
    
    /**
     * A reference to this attribute is required in the object scope to allow
     * the "Clear Page" button to identify which tab is currently selected.
     */
    private JTabbedPane searchTabs;
    
    /**
     * Reference to the <code>QueryField</code>s used to allow entry of search
     * criteria for each capability.
     * <p><i>The key into this <code>Hashtable</code> is the capability name,
     * eg. j2me_midp_2_0.</i></p>
     */
    private Hashtable<String, QueryField> searchFields;
    
    /** Stores the reference to the logical AND button on the search tab. */
    private JRadioButton andButton;
    
    /** Stores the reference to the logical OR button on the search tab. */
    private JRadioButton orButton;
    
    /**
     * Stores the reference to the checkbox that indicates whether only
     * overridden values should be checked.
     */
    private JCheckBox ignoreDefaultsCheck;
    
    /** Stores the search combo selection listener. */
    private SearchSelectionListener searchSelectionListener;
    
    /** Stores the combo box that displays the saved searches. */
    private JComboBox savedSearchCombo;
    
    /** Popup menu for the Search panel. */
    private JPopupMenu searchPopupMenu;
    
    /** Stores the x co-ordinate at which a popup was invoked. */
    private int popX;
    
    /** Stores the y co-ordinate at which a popup was invoked. */
    private int popY;
    
    /**
     * Performs a search based on the criteria specified in the
     * <code>searchFields</code> and provides the results in a popup dialog.
     */
    private void performSearch()
    {
        Hashtable<String, QueryField> toCheck = new Hashtable<String,
                                                              QueryField>();
        Enumeration<String> queryKeys = searchFields.keys();
        String curKey = null;
        QueryField curQuery = null;
        while (queryKeys.hasMoreElements())
        {
            curKey = queryKeys.nextElement();
            curQuery = searchFields.get(curKey);
            if (!curQuery.getComparisonField().getSelectedItem().equals("n/a"))
            {
                toCheck.put(curKey, curQuery);
            }
        }
        
        Vector<MatchedDevice> results = 
               WURFLInfo.getInstance().search(toCheck,
                                              orButton.isSelected(),
                                              ignoreDefaultsCheck.isSelected());
        Collections.sort(results);
        
        ResultsPanelManager rpm = ResultsPanelManager.getInstance();
        rpm.clearResults();
        StyledDocument doc = rpm.getDocument();
        StringBuffer textArea = new StringBuffer("Found ");
        try
        {
            doc.insertString(0, "Found ", doc.getStyle("bold"));
            if (results.size() > 0)
            {
                doc.insertString(doc.getLength(),
                                 Integer.toString(results.size()),
                                 doc.getStyle("boldGreen"));
                textArea.append(Integer.toString(results.size()));
            }
            else
            {
                doc.insertString(doc.getLength(), " no ",
                                 doc.getStyle("boldRed"));
                textArea.append(" no ");
            }

            doc.insertString(doc.getLength(),
                             " matches for the following criteria:\n\n",
                             doc.getStyle("bold"));
            textArea.append(" matches for the following criteria:\n\n");
            
            Enumeration<String> checkKeys = toCheck.keys();
            String curCheck = null;
            QueryField curField = null;
            String comparator = "";
            if (checkKeys.hasMoreElements())
            {
                while (checkKeys.hasMoreElements())
                {
                    curCheck = checkKeys.nextElement();
                    curField = toCheck.get(curCheck);
                    comparator = (String)curField.getComparisonField().
                                                              getSelectedItem();
                    doc.insertString(doc.getLength(), curCheck,
                                     doc.getStyle("regular"));
                    textArea.append(curCheck);
                    doc.insertString(doc.getLength(), " ",
                                                       doc.getStyle("regular"));
                    textArea.append(" ");
                    doc.insertString(doc.getLength(),
                                     comparator, 
                                     doc.getStyle("regular"));
                    textArea.append(comparator);
                    doc.insertString(doc.getLength(), " ", null);
                    textArea.append(" ");
                    doc.insertString(doc.getLength(), curField.getValueAsText(),
                                     doc.getStyle("regular"));
                    textArea.append(curField.getValueAsText());
                    
                    if (checkKeys.hasMoreElements())
                    {
                        if (orButton.isSelected())
                        {
                            doc.insertString(doc.getLength(), " OR\n",
                                             doc.getStyle("regular"));
                            textArea.append(" OR\n");
                        }
                        else
                        {
                            doc.insertString(doc.getLength(), " AND\n",
                                             doc.getStyle("regular"));
                            textArea.append(" AND\n");
                        }
                    }
                }
            }
            else
            {
                doc.insertString(doc.getLength(),
                                 "ALL DEVICES",
                                 doc.getStyle("boldGreen"));
                textArea.append("ALL DEVICES");
            }

            if (results.size() > 0)
            {
                doc.insertString(doc.getLength(),
                                 "\n\nClick on an item to display " +
                                 "it's details in the 'Device' tab\n",
                                 doc.getStyle("bold"));
            }
            
            textArea.append("\n\n");
            rpm.setResultsList(results, textArea.toString());
        }
        catch (BadLocationException ble)
        {
            System.err.println("BLE: " + ble.toString());
        }

        TabbedPaneManager.getInstance().setTab(TabbedPaneManager.results_tab);
        
        Runnable scrollToTop = new Runnable()
        {
            public void run()
            {
                ResultsPanelManager.getInstance().scrollToTop();
            }
        };
        
        SwingUtilities.invokeLater(scrollToTop);
    }
    
    /**
     * Clears all the search field comparators back to n/a and values to 
     * defaults ("" or false).  Also resets the search parameter values.
     */
    private void clearAllSearchFields()
    {
        Enumeration<String> searchKeys = searchFields.keys();
        String curKey = null;
        QueryField curField = null;
        while (searchKeys.hasMoreElements())
        {
            curKey = searchKeys.nextElement();
            curField = searchFields.get(curKey);
            curField.setValueDefault();
            curField.getComparisonField().setSelectedIndex(0);
        }
        
        andButton.setSelected(true);
        ignoreDefaultsCheck.setSelected(true);
    }
    
    /**
     * Sets up all the search fields and search parameters for the provided
     * saved search.
     * @param searchName the name of a previously saved search.
     */
    private void loadSearchDetails(String searchName)
    {
        clearAllSearchFields();
        ArrayList<SearchDetails> searches = 
                               ConfigurationManager.getInstance().getSearches();
        Iterator<SearchDetails> it = searches.iterator();
        boolean found = false;
        SearchDetails curDetails = null;
        while ((!found) && (it.hasNext()))
        {
            curDetails = it.next();
            if (curDetails.getSearchName().equals(searchName))
            {
                found = true;
            }
        }
        
        if (found)
        {
            if (curDetails.isOrComparison())
            {
                orButton.setSelected(true);
            }
            else
            {
                andButton.setSelected(true);
            }
            orButton.setSelected(curDetails.isOrComparison());
            ignoreDefaultsCheck.setSelected(curDetails.isIgnoreDefaults());
            QueryField curQuery = null;
            for (ComparisonDetails comp : curDetails.getComparisonItems())
            {
                curQuery = searchFields.get(comp.getCapability());
                curQuery.getComparisonField().
                                          setSelectedItem(comp.getComparison());
                curQuery.setValue(comp.getValue()); 
            }
        }
    }

    /**
     * Creates the popup menu for the Search panel. 
     */
    private void createPopup()
    {
        JMenuItem clearFields = new JMenuItem("Clear all search fields");
        clearFields.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                if (JOptionPane.showConfirmDialog(searchPanel,
                        "Clear search fields?",
                        "Clear search",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    clearAllSearchFields();
                }
            }
        });

        searchPopupMenu = new JPopupMenu();
        
        searchPopupMenu.add(clearFields);
        
        if (WURFLInfo.getInstance().getMDevInfWSIcon() == null)
        {
            searchPopupMenu.addSeparator();
            JMenuItem manageSaved = new JMenuItem("Manage saved searches");
            manageSaved.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    new SavedSearchManager(null).setVisible(true);
                }
            });
    
            searchPopupMenu.add(manageSaved);
        }
    }

    /**
     * Creates the Search panel
     */
    private SearchPanelManager()
    {
        createPopup();
        
        MouseAdapter popupMouseListener = new MouseAdapter(){
            private void showPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popX = e.getX();
                    popY = e.getY();
                    searchPopupMenu.show(e.getComponent(), popX, popY);
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
        };
        
        searchFields = new Hashtable<String, QueryField>();
        
        searchTabs = new JTabbedPane();
        
        Hashtable<String, Group> groups = WURFLInfo.getInstance().
                                                           getGroups("generic");
        Enumeration<String> groupNames = groups.keys();
        ArrayList<String> sortedNames = new ArrayList<String>();
        while (groupNames.hasMoreElements())
        {
            sortedNames.add(groupNames.nextElement());
        }
        
        Collections.sort(sortedNames);
        
        Hashtable<String, Capability> caps = null;
        Description desc = null;
        
        for (String groupName : sortedNames)
        {
            caps = groups.get(groupName).getCapabilities();
            int numCaps = caps.size() + 1; // Add one to make column wrap work.

            JPanel capsPanel = new JPanel(new GridBagLayout());
            Enumeration<String> capsList = caps.keys();
            ArrayList<String> sortedCaps = new ArrayList<String>();
            while (capsList.hasMoreElements())
            {
                sortedCaps.add(capsList.nextElement());
            }
            
            Collections.sort(sortedCaps);

            GridBagConstraints c = new GridBagConstraints();
            int row = 0;
            int col = 0;
            for (String curKey : sortedCaps)
            {
                JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.
                                                                     TRAILING));
                labelPanel.add(new JLabel(curKey));
                desc = WURFLInfo.getInstance().getDescription(curKey);
                String tooltip = "Sorry, no description available.";
                if (desc != null)
                {
                    tooltip = desc.getHTMLFormattedDescription();
                }
                labelPanel.setToolTipText(tooltip);
                labelPanel.addMouseListener(popupMouseListener);
                c.gridy = row;
                c.gridx = col;
                c.anchor = GridBagConstraints.LINE_END;
                capsPanel.add(labelPanel, c);

                // If we don't know what type it is, it's better to allow
                //  alphanumeric input.
                QueryField.field_type fieldType =
                                              QueryField.field_type.alpha_field;
                if (desc != null)
                {
                    if (desc.getType().equals("true/false"))
                    {
                        fieldType = QueryField.field_type.boolean_field;
                    }
                    else if (desc.getType().equals("integer"))
                    {
                        fieldType = QueryField.field_type.numeric_field;
                    }
                }

                JPanel queryPanel = new JPanel();
                queryPanel.setLayout(new BoxLayout(queryPanel,
                                                   BoxLayout.LINE_AXIS));
                QueryField qField = new QueryField(fieldType);
                searchFields.put(curKey, qField);
                queryPanel.add(qField.getComparisonField());
                queryPanel.add(Box.createHorizontalGlue());
                queryPanel.add(qField.getValueField());

                c.gridy = row;
                c.gridx = col + 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                capsPanel.add(queryPanel, c);

                row++;
                if (row >= (numCaps >> 1))
                {
                    row = 0;
                    col = 3;
                }
            }

            capsPanel.addMouseListener(popupMouseListener);
            searchTabs.addTab(groupName, new JScrollPane(capsPanel));
        }
        
        andButton = new JRadioButton("Match ALL", true);
        andButton.setToolTipText("<html>Match ALL criteria (<b><i>AND</i></b>)" +
                                 "</html>");
        orButton = new JRadioButton("Match ANY");
        orButton.setToolTipText("<html>Match ANY criteria (<b><i>OR</i></b>)" +
                                "</html>");
        ignoreDefaultsCheck = new JCheckBox("Ignore default values", true);
        ignoreDefaultsCheck.setToolTipText("<html>Ignore capabilities that " +
                                           "use the generic <b><i>default</i>" +
                                           "</b><br>values and haven't been " +
                                           "overridden by any devices.</html>");
        ButtonGroup choices = new ButtonGroup();
        choices.add(andButton);
        choices.add(orButton);
        JPanel logicSelection = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        logicSelection.add(andButton, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        logicSelection.add(orButton, c);
        
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.CENTER;
        logicSelection.add(ignoreDefaultsCheck, c);
        
        logicSelection.setBorder(BorderFactory.
                                       createTitledBorder("Search parameters"));
        
        JButton searchButton = new JButton(new SearchAction());
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, 
                                             BoxLayout.LINE_AXIS));
        detailsPanel.add(logicSelection);
        detailsPanel.add(Box.createHorizontalStrut(20));
        detailsPanel.add(searchButton);
        detailsPanel.add(Box.createHorizontalStrut(20));
        if (WURFLInfo.getInstance().getMDevInfWSIcon() == null)
        {
            JPanel historyPanel = new JPanel();
            historyPanel.setLayout(new BoxLayout(historyPanel,
                                                          BoxLayout.PAGE_AXIS));

            savedSearchCombo = new JComboBox();
            searchSelectionListener = new SearchSelectionListener();
            
            prepareSavedSearchCombo();

            savedSearchCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
            historyPanel.add(Box.createVerticalGlue());
            JPanel searchHolder = new JPanel(new FlowLayout());
            searchHolder.add(savedSearchCombo);
            historyPanel.add(searchHolder);
            historyPanel.add(Box.createVerticalGlue());
            
            historyPanel.setBorder(BorderFactory.
                                          createTitledBorder("Saved searches"));
            
            detailsPanel.add(historyPanel);
        }
        
        searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchTabs, BorderLayout.CENTER);
        searchPanel.add(detailsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Sets up the <code>savedSearchCombo</code> with the latest saved search
     * data.
     */
    public void prepareSavedSearchCombo()
    {
        savedSearchCombo.removeActionListener(searchSelectionListener);
        
        savedSearchCombo.removeAllItems();
        
        savedSearchCombo.addItem("-- Select a search --");

        for (SearchDetails detail : 
                               ConfigurationManager.getInstance().getSearches())
        {
            savedSearchCombo.addItem(detail.getSearchName());
        }

        savedSearchCombo.addActionListener(searchSelectionListener);
    }
    
    /**
     * Prepares the saved search information and re-writes the configuration
     * file.
     * @param searchName the name/description of the search configuration
     */
    public void saveSearchDetails(String searchName)
    {
        SearchDetails details =
                            new SearchDetails(searchName,
                                              orButton.isSelected(),
                                              ignoreDefaultsCheck.isSelected());
        
        Enumeration<String> queryKeys = searchFields.keys();
        String curCapability = null;
        QueryField curQuery = null;
        String comp = null;
        while (queryKeys.hasMoreElements())
        {
            curCapability = queryKeys.nextElement();
            curQuery = searchFields.get(curCapability);
            comp = (String)curQuery.getComparisonField().getSelectedItem();
            if (!comp.equals("n/a"))
            {
                ComparisonDetails detail = 
                               new ComparisonDetails(curCapability,
                                                     comp,
                                                     curQuery.getValueAsText());
                details.addComparisonItem(detail);
            }
        }
        
        ConfigurationManager.getInstance().getSearches().remove(details);
        ConfigurationManager.getInstance().getSearches().add(details);
        
        ConfigurationManager.getInstance().saveConfig();
    
        prepareSavedSearchCombo();
    }
    
    /**
     * Accessor
     * @return the Search panel
     */
    public JPanel getSearchPanel()
    {
        return (searchPanel);
    }
    
    /**
     * Used to removed the singleton instance.
     * <p>This allows complete re-creation of the search panel when changes
     * have been made to the WURFL hierarchy.</p>
     */
    public static void destroy()
    {
        instance = null;
    }
    
    /**
     * Accessor
     * @return the singleton instance of this class
     */
    public static SearchPanelManager getInstance()
    {
        if (instance == null)
        {
            instance = new SearchPanelManager();
        }
        
        return (instance);
    }

    /**
     * This internal class is used to manage the action when the user wishes
     * to search for devices that match the criteria they have specified.
     * @author Jim McLachlan
     * @version 1.0
     */
    class SearchAction extends AbstractAction
    {
        /**
         * Creates in instance of this action with the name "Search".
         */
        public SearchAction()
        {
            super("Search");
        }
        
        /**
         * Calls the perfomSearch method to find any matching devices.
         * <p>The call is done inside a separate thread to avoid locking
         * the GUI.
         * @param e unused.
         */
        public void actionPerformed(ActionEvent e)
        {
            Runnable search = new Runnable()
            {
                public void run()
                {
                    performSearch();
                }
            };

            SwingUtilities.invokeLater(search);
        }
    }
    
    /**
     * An ActionListener used to identify changes in the saved searches
     * combo box.
     * @author Jim McLachlan
     * @version 1.0
     */
    class SearchSelectionListener implements ActionListener
    {
        /** Constant defining the default text when a search is unspecified. */
        private String previousSearch = "-- Select a search --";
            
        /**
         * Loads the search criteria into the search tabs.
         * @param e the ActionEvent that triggered this method
         */
        public void actionPerformed(ActionEvent e)
        {
            if ((savedSearchCombo.getComponentCount() > 0) &&
                (!savedSearchCombo.getSelectedItem().
                                     equals("-- Select a search --")))
            {
                previousSearch = (String)savedSearchCombo.
                                                      getSelectedItem();
                loadSearchDetails(previousSearch);
            }
        }
    }
}
