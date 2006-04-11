/*
 * Class name : CompPanelManager
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 15-Dec-2005
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ossltd.mdevinf.ConfigurationManager;
import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.ComparisonTableModel;
import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.WURFLInfo;

/**
 * Manages the layout and updates to the comparison panel. 
 * @author Jim McLachlan
 * @version 1.0
 */
public class CompPanelManager
{
    /** Stores the singleton reference to this class. */
    private static CompPanelManager instance = null;
    
    /** Stores the reference to the Comparison <code>JPanel</code>. */
    private JPanel comparisonPanel;

    /** Stores a reference to the comparison table. */
    private ComparisonTable comparisonTable;
    
    /** Stores a reference to the comparison table model. */
    private ComparisonTableModel tableModel;
    
    /** Popup menu for the comparison panel. */
    private JPopupMenu comparisonPopupMenu;
    
    /** Popup menu for renaming capabilities. */
    private JPopupMenu renamingPopupMenu;
    
    /** Stores the x co-ordinate at which a popup was invoked. */
    private int popX;
    
    /** Stores the y co-ordinate at which a popup was invoked. */
    private int popY;
    
    /**
     * Saves the currently selected capabilities and devices to an XML file
     * chosen by the user.
     */
    private void saveTableConfig()
    {
        JFileChooser jfc = new JFileChooser("." + File.separator);
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileFilter(new FileFilter(){
            public boolean accept(File f)
            {
                boolean showFile = false;
                
                if (f.isDirectory())
                {
                    showFile = true;
                }
                else if (f.getName().endsWith(".xml"))
                {
                    showFile = true;
                }
                
                return (showFile);
            }
            
            public String getDescription()
            {
                return ("XML Files");
            }
        });
        jfc.setSelectedFile(new File("comparisons_XXX.xml"));

        int result = jfc.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File compConfigFile = jfc.getSelectedFile();
            if (compConfigFile.exists())
            {
                String[] choices = {"Replace file", "Cancel"};
                String choice = (String)JOptionPane.showInputDialog(null,
                                         "File already exists, do you want to:",
                                         "Replace or update",
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         choices,
                                         choices[0]);
                if (choice.equals(choices[1]))
                {
                    compConfigFile = null;
                }
            }
            
            if (compConfigFile != null)
            {
                try
                {
                    DocumentBuilder compBuilder =
                      DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = compBuilder.newDocument();
                    doc.setXmlVersion("1.0");

                    Element devicesRoot = doc.createElement("devices");
                    ArrayList<String> devices = tableModel.getDevices();

                    TableColumnModel tcm = comparisonTable.getColumnModel();
                    int numCols = tcm.getColumnCount();
                    String devID = null;
                    Element curElement = null;
                    TableColumn col = null;
                    for (int i = 1; i < numCols; i++)
                    {
                        curElement = doc.createElement("device");
                        col = tcm.getColumn(i);
                        devID = devices.get(col.getModelIndex() - 1);
                        curElement.setAttribute("id", devID);
                        devicesRoot.appendChild(curElement);
                    }
                    
                    Element capRoot = doc.createElement("capabilities");
                    ArrayList<String> caps = tableModel.getCapabilities();
                    for (String capName : caps)
                    {
                        curElement = doc.createElement("capability");
                        curElement.setAttribute("name", capName);
                        capRoot.appendChild(curElement);
                    }

                    FileOutputStream fos = new FileOutputStream(compConfigFile);
                    StreamResult sr = new StreamResult(fos);
                    
                    Element compRoot = doc.createElement("mdevinf_comparisons");
                    compRoot.appendChild(devicesRoot);
                    compRoot.appendChild(capRoot);
                    
                    DOMSource ds = new DOMSource(compRoot);
                    
                    Transformer t =
                              TransformerFactory.newInstance().newTransformer();
                    t.setOutputProperty("indent", "yes");
                    t.transform(ds, sr);
                    fos.close();
                }
                catch(ParserConfigurationException pce)
                {
                    System.err.println("Couldn't create XML for comparison " +
                                       "data: " + pce.toString());
                }
                catch (TransformerException te)
                {
                    System.err.println("Error managing XML for comparison " +
                                       "data: " + te.toString());
                }
                catch (IOException ioe)
                {
                    System.err.println("Error creating comparison data file: " +
                                       ioe.toString());
                }
            }
        }
    }

    /**
     * Loads the capabilities and devices from a file chosen by the user and
     * populates the comparison table accordingly.
     */
    private void loadTableConfig()
    {
        JFileChooser jfc = new JFileChooser("." + File.separator);
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileFilter(new FileFilter(){
            public boolean accept(File f)
            {
                boolean showFile = false;
                
                if (f.isDirectory())
                {
                    showFile = true;
                }
                else if (f.getName().endsWith(".xml"))
                {
                    showFile = true;
                }
                
                return (showFile);
            }
            
            public String getDescription()
            {
                return ("XML Files");
            }
        });
        jfc.setSelectedFile(new File("comparisons_XXX.xml"));

        int result = jfc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File compConfigFile = jfc.getSelectedFile();
            if ((compConfigFile.exists()) && (compConfigFile.canRead()))
            {
                try
                {
                    DocumentBuilder builder =
                      DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = builder.parse(compConfigFile);
                    clearAllTableData();
                    NodeList devList = doc.getElementsByTagName("device");
                    int devCount = devList.getLength();

                    Element curElement = null;
                    String deviceId = null;
                    for (int i = 0; i < devCount; i++)
                    {
                        curElement = (Element)devList.item(i);
                        deviceId = curElement.getAttribute("id");
                        Device dev = WURFLInfo.getInstance().getDevices().
                                                                  get(deviceId);
                        if (dev != null)
                        {
                            addSpecificDevice(dev);
                        }
                    }
                    
                    NodeList capList = doc.getElementsByTagName("capability");
                    int capCount = capList.getLength();

                    String capName = null;
                    
                    for (int i = 0; i < capCount; i++)
                    {
                        curElement = (Element)capList.item(i);
                        capName = curElement.getAttribute("name");
                        tableModel.setValueAt(capName, i, 0);
                    }
                    
                }
                catch (ParserConfigurationException pce)
                {
                    System.err.println("Loading configuration data PCE: " +
                                       pce.toString());
                }
                catch (SAXException saxe)
                {
                    System.err.println("Loading configuration data SAXE: " +
                                       saxe.toString());
                }
                catch (IOException ioe)
                {
                    System.err.println("Loading configuration data IOE: " +
                                       ioe.toString());
                }
            }
        }
    }
    
    /**
     * Deletes all the visible data in the table and all the model data too.
     */
    private void clearAllTableData()
    {
        TableColumn col = null;
        while (comparisonTable.getColumnCount() > 1)
        {
            col = comparisonTable.getColumnModel().
                    getColumn(comparisonTable.getColumnCount() - 1);
            comparisonTable.getColumnModel().removeColumn(col);
        }
        tableModel.clear();
    }
    
    /**
     * Creates the popup menu for the Relationships panel. 
     */
    private void createPopups()
    {
        JMenuItem capSelect = new JMenuItem("Select capabilities...");
        capSelect.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                CapabilityChooser chooser =
                      new CapabilityChooser(null, tableModel.getCapabilities());
                chooser.setVisible(true);
                
                if (chooser.isChanged())
                {
                    while (tableModel.getRowCount() > 0)
                    {
                        tableModel.deleteRow(0);
                    }
                    
                    int curRow = 0;
                    for (String capability : chooser.getSelections())
                    {
                        tableModel.setValueAt(capability, curRow++, 0);
                    }
                }
            }
        });
        
        JMenuItem compRemoveDev = new JMenuItem("Remove device");
        compRemoveDev.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                int selectedCol = comparisonTable.getSelectedColumn();
                if (selectedCol > 0)
                {
                    TableColumnModel tcm = comparisonTable.getColumnModel();
                    TableColumn colToDel = tcm.getColumn(selectedCol);
                    int modelCol = colToDel.getModelIndex();
                    comparisonTable.removeColumn(colToDel);
                    tableModel.deleteColumn(modelCol - 1);
                    
                    Enumeration<TableColumn> colEnum = tcm.getColumns();
                    TableColumn colToMove = null;
                    while (colEnum.hasMoreElements())
                    {
                        colToMove = colEnum.nextElement();
                        if (colToMove.getModelIndex() > modelCol)
                        {
                            colToMove.setModelIndex(colToMove.getModelIndex() -
                                                    1);
                        }
                    }
                }
            }
        });
        
        JMenuItem compRemoveCap = new JMenuItem("Remove capability");
        compRemoveCap.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                tableModel.deleteRow(comparisonTable.getSelectedRow());
            }
        });
        
        JMenuItem compClear = new JMenuItem("Clear comparison table");
        compClear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                int result = JOptionPane.showConfirmDialog(comparisonPanel,
                                               "Are you sure that you wish\n" +
                                               "to clear the comparison table?",
                                               "Clear table",
                                               JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION)
                {
                    clearAllTableData();
                }
            }
        });

        comparisonPopupMenu = new JPopupMenu();
        
        comparisonPopupMenu.add(capSelect);
        comparisonPopupMenu.addSeparator();
        if (WURFLInfo.getInstance().getMDevInfWSIcon() == null)
        {
            comparisonPopupMenu.add(new LoadConfigAction());
            comparisonPopupMenu.add(new SaveConfigAction());
            comparisonPopupMenu.addSeparator();
        }
        comparisonPopupMenu.add(compRemoveDev);
        comparisonPopupMenu.add(compRemoveCap);
        comparisonPopupMenu.addSeparator();
        comparisonPopupMenu.add(compClear);
        
        renamingPopupMenu = new JPopupMenu();
        
        if (WURFLInfo.getInstance().getMDevInfWSIcon() == null)
        {
            JMenuItem renameCap = new JMenuItem("Rename capability...");
            renameCap.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e)
                {
                    int capRow = comparisonTable.rowAtPoint(new Point(popX,
                                                                      popY));
                    String wurflName = tableModel.getCapabilityAt(capRow);
                    String capName = (String)tableModel.getValueAt(capRow, 0);
                    String result = (String)JOptionPane.showInputDialog(
                                                   comparisonPanel,
                                                   "New name for " + wurflName,
                                                   "Provide descriptive name",
                                                   JOptionPane.QUESTION_MESSAGE,
                                                   null,
                                                   null,
                                                   capName);
                    if (result != null)
                    {
                        if (result.length() > 0)
                        {
                            ConfigurationManager.getInstance().
                                  getCapabilityNameMap().put(wurflName, result);
                        }
                        else
                        {
                            ConfigurationManager.getInstance().
                                       getCapabilityNameMap().remove(wurflName);
                        }
                        
                        ConfigurationManager.getInstance().saveConfig();
                    }
                    
                    comparisonTable.repaint();
                }
            });
            
            renamingPopupMenu.add(renameCap);
            renamingPopupMenu.addSeparator();
            renamingPopupMenu.add(new LoadConfigAction());
            renamingPopupMenu.add(new SaveConfigAction());
        }
    }

    /**
     * Creates the Comparison panel.
     */
    private CompPanelManager()
    {
        createPopups();
        
        comparisonPanel = new JPanel(new BorderLayout());
        
        tableModel = new ComparisonTableModel();
        
        comparisonTable = new ComparisonTable(tableModel);
        comparisonTable.setColumnSelectionAllowed(true);
        comparisonTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        comparisonTable.getColumnModel().getColumn(0).setHeaderValue("");
        comparisonTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        comparisonTable.setDefaultRenderer(Capability.class,
                                           new CompCellRenderer());
        
        comparisonTable.addMouseListener(new MouseAdapter(){
            private void showPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    popX = e.getX();
                    popY = e.getY();
                    int clickCol = comparisonTable.
                                           columnAtPoint(new Point(popX, popY));
                    if (clickCol == 0)
                    {
                        renamingPopupMenu.show(e.getComponent(),
                                               popX,
                                               popY);
                    }
                    else
                    {
                        comparisonPopupMenu.show(e.getComponent(),
                                                 popX,
                                                 popY);
                    }
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
        
        comparisonPanel.add(new JScrollPane(comparisonTable));
    }
    
    /**
     * Accessor
     * @return a reference to the panel for the Comparison tab
     */
    public JPanel getComparisonPanel()
    {
        return (comparisonPanel);
    }

    /**
     * Used to removed the singleton instance.
     * <p>This allows complete re-creation of the comparison panel when
     * changes have been made to the WURFL hierarchy.</p>
     */
    public static void destroy()
    {
        instance = null;
    }
    
    /**
     * Accessor
     * @return an singleton instance of this class
     */
    public static CompPanelManager getInstance()
    {
        if (instance == null)
        {
            instance = new CompPanelManager();
        }
        
        return (instance);
    }
    
    /**
     * Adds the currently selected device to the comparison table.
     */
    public void addDevice()
    {
        Device dev = TabbedPaneManager.getInstance().getLastSelectedDevice();
        addSpecificDevice(dev);
    }

    /**
     * Adds the specified device to the end of the comparison table
     * @param dev
     */
    private void addSpecificDevice(Device dev)
    {
        int lastCol = tableModel.getColumnCount();
        TableColumn col = new TableColumn(lastCol);
        ImageIcon devImg = WURFLInfo.getInstance().getImage(dev.getDeviceID());
        if (devImg != null)
        {
            col.setHeaderValue("");
            col.setHeaderRenderer(new DefaultTableCellRenderer());
            JLabel renderer = (JLabel)col.getHeaderRenderer();
            renderer.setSize(new Dimension(80, 80));
            renderer.setText("");
            renderer.setIcon(devImg);
        }
        else
        {
            col.setHeaderValue("No image");
        }
        comparisonTable.addColumn(col);
        tableModel.setValueAt(dev, 0, lastCol);
    }
    
    /**
     * This inner class provides the action to load the table configuration.
     * @author Jim McLachlan
     * @version 1.0
     */
    class LoadConfigAction extends AbstractAction
    {
        /** Constructor that sets the Action name to "Load selections..." */
        public LoadConfigAction()
        {
            super("Load selections...");
        }
        
        /**
         * Loads the table configuration when the action is triggered.
         * @param e the ActionEvent that triggered this method.
         */
        public void actionPerformed(ActionEvent e)
        {
            loadTableConfig();
        }
    }
    
    /**
     * This inner class provides the action to save the table configuration.
     * @author Jim McLachlan
     * @version 1.0
     */
    class SaveConfigAction extends AbstractAction
    {
        /** Constructor that sets the Action name to "Save selections..." */
        public SaveConfigAction()
        {
            super("Save selections...");
        }
        
        /**
         * Saves the table configuration when the action is triggered.
         * @param e the ActionEvent that triggered this method.
         */
        public void actionPerformed(ActionEvent e)
        {
            saveTableConfig();
        }
    }
}
