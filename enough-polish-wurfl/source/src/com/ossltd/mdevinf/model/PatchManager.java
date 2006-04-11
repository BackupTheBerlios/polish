/*
 * Class name : PatchManager
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

package com.ossltd.mdevinf.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
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


/**
 * This class handles the patch file that is created as changes are made to
 * the loaded data. 
 * @author Jim McLachlan
 * @version 1.0
 */
public class PatchManager
{
    /** The reference to the singleton instance of this class. */
    private static PatchManager instance = null;
    
    /** Stores a reference to the XML document builder class. */
    private DocumentBuilder docBuilder;
    
    /** Stores the name of the file being used for output. */
    private File patchFile;
    
    /** Reference to the patch XML document. */
    private Document patchDoc;
    
    /** Reference to the root element of the XML document. */
    private Element rootElement;
    
    /** Reference to the devices element of the XML document. */
    private Element devicesElement;
    
    /**
     * Where the selected patch file merges the data from the selected patch
     * file into the current data structure. 
     */
    private void addPatchFileData()
    {
        try
        {
            Document doc = docBuilder.parse(patchFile);
            NodeList deviceList = doc.getElementsByTagName("device");

            Device device = null;
            String actualDeviceRoot = null;
            boolean actualDevice = false;
            Group group = null;
            Capability capability = null;
            
            Element devEl = null;
            Element grpEl = null;
            Element capEl = null;
            
            int deviceCount = deviceList.getLength();
            for (int i = 0; i < deviceCount; i++)
            {
                devEl = (Element)deviceList.item(i);
                actualDevice = false;
                actualDeviceRoot = devEl.getAttribute("actual_device_root");
                if ((actualDeviceRoot != null) &&
                    (actualDeviceRoot.equals("true")))
                {
                    actualDevice = true;
                }
                device = new Device(devEl.getAttribute("id"),
                                    devEl.getAttribute("fall_back"),
                                    devEl.getAttribute("user_agent"),
                                    actualDevice,
                                    true);
                NodeList groupList = devEl.getElementsByTagName("group");
                int groupCount = groupList.getLength();
                for (int j = 0; j < groupCount; j++)
                {
                    grpEl = (Element)groupList.item(j);
                    group = new Group(grpEl.getAttribute("id"));
                    device.addGroup(group);
                    NodeList capList = grpEl.getElementsByTagName("capability");
                    int capCount = capList.getLength();
                    for (int k = 0; k < capCount; k++)
                    {
                        capEl = (Element)capList.item(k);
                        capability = 
                                 new Capability(capEl.getAttribute("value"),
                                                true,
                                                true);
                        group.getCapabilities().
                                             put(capEl.getAttribute("name"),
                                                 capability);
                    }
                }

                addCapabilities(device, device.getGroups());
            }
        }
        catch (SAXException saxe)
        {
            System.err.println("Failed to load patch data from " +
                               patchFile.getPath());
        }
        catch (IOException ioe)
        {
            System.err.println("Failed to access patch file " +
                               patchFile.getPath());
        }
    }
    
    /**
     * Present the file chooser to allow the user to choose the patch file
     */
    private void choosePatchFile()
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
                else if ((f.getName().startsWith("wurfl_patch")) &&
                         (f.getName().endsWith(".xml")))
                {
                    showFile = true;
                }
                
                return (showFile);
            }
            
            public String getDescription()
            {
                return ("WURFL Patch Files");
            }
        });
        jfc.setSelectedFile(new File("wurfl_patch_XXX.xml"));
        
        int result = jfc.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            patchFile = jfc.getSelectedFile();
            if (patchFile.exists())
            {
                String[] choices = {"Add to file", "Replace file", "Cancel"};
                String choice = (String)JOptionPane.showInputDialog(null,
                                         "File already exists, do you want to:",
                                         "Replace or update",
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         choices,
                                         choices[0]);
                if (choice.equals(choices[0]))
                {
                    addPatchFileData();
                }
                else if (choice.equals(choices[2]))
                {
                    patchFile = null;
                }
            }
        }
    }
    
    /**
     * Creates the singleton instance of the patch manager.
     * <p>Defines the patch files to be loaded.  This is either all patch files
     * in the working directory or only those previously specified by the user
     * if the patch selection file is available.</p>
     * <p>Creates the basic patch XML document for the current session and
     * initialises the mandatory elements.  All patch files must have the
     * following tags:</p>
     * <p><pre>
     *   &lt;wurfl_patch&gt;
     *     &lt;devices&gt;
     *     &lt;/devices&gt;
     *   &lt;/wurfl_patch&gt;
     * </pre></p>
     */
    private PatchManager()
    {
        patchFile = null;
        
        try
        {
            docBuilder = DocumentBuilderFactory.newInstance().
                                                           newDocumentBuilder();
            patchDoc = docBuilder.newDocument();
            patchDoc.setXmlVersion("1.0");
            rootElement = patchDoc.createElement("wurfl_patch");
            devicesElement = patchDoc.createElement("devices");
            rootElement.appendChild(devicesElement);
        }
        catch(ParserConfigurationException pce)
        {
            System.err.println("Couldn't create patch file XML structure: " +
                               pce.toString());
        }
    }
    
    /**
     * Adds any edited capabilities to the patch document.
     * <p>If the device has already been edited, the existing element is
     * updated, otherwise a new device element is created.</p>
     * <p>Similarly, if the capability has already been edited, the existing
     * value is replaced with the new one, otherwise a new capability is
     * added.</p>
     * @param device the device that is being updated
     * @param groups the groups and capabilities that have been updated
     */
    public void addCapabilities(Device device,
                                Hashtable<String, Group> groups)
    {
        NodeList deviceNodes = devicesElement.getChildNodes();
        int numDeviceNodes = deviceNodes.getLength();
        Element deviceEl = null;
        boolean found = false;
        int curNode = 0;
        while ((!found) && (curNode < numDeviceNodes))
        {
            deviceEl = (Element)deviceNodes.item(curNode);
            if (deviceEl.getAttribute("id").equals(device.getDeviceID()))
            {
                found = true;
            }
            curNode++;
        }

        if (!found)
        {
            deviceEl = patchDoc.createElement("device");
            deviceEl.setAttribute("user_agent", device.getUserAgent());
            deviceEl.setAttribute("actual_device_root",
                                  ((Boolean)device.isActualDevice()).
                                                                toString());
            deviceEl.setAttribute("fall_back", device.getFallbackID());
            deviceEl.setAttribute("id", device.getDeviceID());
            
            if ((device.getDeviceID().equals("generic")) &&
                (devicesElement.hasChildNodes()))
            {
                devicesElement.insertBefore(deviceEl,
                                            devicesElement.getFirstChild());
            }
            else
            {
                devicesElement.appendChild(deviceEl);
            }
        }

        Hashtable<String, Capability> caps = null;

        Enumeration<String> grpKeys = groups.keys();
        Group curGroup = null;
        while (grpKeys.hasMoreElements())
        {
            curGroup = groups.get(grpKeys.nextElement());

            // Check for the existence of the group in the device
            NodeList groupNodes = deviceEl.getChildNodes();
            int numGroupNodes = groupNodes.getLength();
            Element groupEl = null;
            found = false;
            curNode = 0;
            while ((!found) && (curNode < numGroupNodes))
            {
                groupEl = (Element)groupNodes.item(curNode);
                if (groupEl.getAttribute("id").equals(curGroup.getGroupID()))
                {
                    found = true;
                }
                curNode++;
            }
            
            if (!found)
            {
                groupEl = patchDoc.createElement("group");
                groupEl.setAttribute("id", curGroup.getGroupID());
                
                deviceEl.appendChild(groupEl);
            }

            caps = curGroup.getCapabilities();
            Enumeration<String> capKeys = caps.keys();
            Capability curCapability = null;
            String curCap = null;
            while (capKeys.hasMoreElements())
            {
                curCap = capKeys.nextElement();
                curCapability = caps.get(curCap);

                // Check for the existence of the capability in the group
                NodeList capNodes = groupEl.getChildNodes();
                int numCapNodes = capNodes.getLength();
                Element capEl = null;
                found = false;
                curNode = 0;
                while ((!found) && (curNode < numCapNodes))
                {
                    capEl = (Element)capNodes.item(curNode);
                    if (capEl.getAttribute("name").equals(curCap))
                    {
                        capEl.setAttribute("value", curCapability.getValue());
                        found = true;
                    }
                    
                    curNode++;
                }
                
                if (!found)
                {
                    capEl = patchDoc.createElement("capability");
                    capEl.setAttribute("name", curCap);
                    capEl.setAttribute("value", curCapability.getValue());
                  
                    groupEl.appendChild(capEl); 
                }
            }
        }
    }
    
    /**
     * Adds the specified capability to the generic device at the top of the
     * patch list.
     * @param group the group in which to add the new capability
     * @param capability the name of the new capability
     * @param value the value of the new capability
     */
    public void addNewCapability(String group, String capability, String value)
    {
        Element newCapEl = patchDoc.createElement("capability");
        newCapEl.setAttribute("name", capability);
        newCapEl.setAttribute("value", value);
        
        Element generic = patchDoc.getElementById("generic");
        if (generic == null)
        {
            generic = patchDoc.createElement("device");
            generic.setAttribute("fall_back", "root");
            generic.setAttribute("id", "generic");
            generic.setAttribute("user_agent", "");
            if (devicesElement.hasChildNodes())
            {
                devicesElement.insertBefore(generic, 
                                            devicesElement.getFirstChild());
            }
            else
            {
                devicesElement.appendChild(generic);
            }
        }
        
        NodeList groupList = generic.getElementsByTagName("group");
        int groupListLength = groupList.getLength();
        Element groupEl = null;
        int i = 0;
        boolean found = false;
        while ((!found) && (i < groupListLength))
        {
            groupEl = (Element)groupList.item(i);
            if (groupEl.getAttribute("id").equals(group))
            {
                found = true;
            }
            i++;
        }
        
        if (!found)
        {
            groupEl = patchDoc.createElement("group");
            groupEl.setAttribute("id", group);
            generic.appendChild(groupEl);
        }
        
        groupEl.appendChild(newCapEl);
    }
    
    /**
     * Creates a patch file with the specified filename.
     * @return <code>true</code> if the patch file was successfully written.
     */
    public boolean outputPatchFile()
    {
        boolean result = false;
        
        if (patchFile == null)
        {
            choosePatchFile();
        }
        
        if (patchFile != null)
        {
            try
            {
                FileOutputStream fos = new FileOutputStream(patchFile);
                StreamResult sr = new StreamResult(fos);
                
                DOMSource ds = new DOMSource(rootElement);
                
                Transformer t = TransformerFactory.newInstance().
                                                               newTransformer();
                t.setOutputProperty("indent", "yes");
                t.transform(ds, sr);
                fos.close();
                
                WURFLInfo.getInstance().
                  processWURFLPatch(rootElement.getElementsByTagName("device"));
    
                result = true;
            }
            catch (TransformerException te)
            {
                System.err.println("Error managing XML for patch file " +
                                   te.toString());
            }
            catch (IOException ioe)
            {
                System.err.println("Couldn't write patch file: " +
                                                                ioe.toString());
            }
        }
        
        return (result);
    }
    
    /**
     * Accessor
     * @return a reference to the singleton instance of this class
     */
    public static PatchManager getInstance()
    {
        if (instance == null)
        {
            instance = new PatchManager();
        }
        
        return (instance);
    }
}
