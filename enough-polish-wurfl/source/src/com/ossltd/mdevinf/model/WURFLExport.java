/*
 * Class name : WURFLExport
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 08-Dec-2005
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A singleton class simply used to export the current structure in memory
 * to a new XML file. 
 * @author Jim McLachlan
 * @version 1.0
 */
public class WURFLExport
{
    /** Stores the singleton reference to this class. */
    private static WURFLExport instance = null;
    
    /** Private constructor to maintain singleton integrity. */
    private WURFLExport()
    {
        // Empty constructor
    }
    
    /**
     * Exports the device information currently in memory to either a new
     * wurfl.xml file or to a new patch file.
     * @param file the handle to the file to be written
     * @param fullExport a flag indicating whether the full WURFL data should
     * be written to the file or only the patch data
     * @throws IOException if a problem occurs whilst writing the file
     */
    public void exportWURFL(File file, boolean fullExport) throws IOException
    {
        try
        {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().
                                                           newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            doc.setXmlVersion("1.0");
            
            Element rootElement = null;
            Element curElement = null;
            Element curSubElement = null;
            
            WURFLInfo wurflInfo = WURFLInfo.getInstance();
            
            if (fullExport)
            {
                rootElement = doc.createElement("wurfl");

                curElement = doc.createElement("version");
            
                curSubElement = doc.createElement("ver");
                curSubElement.setTextContent(wurflInfo.getVersion());
                curElement.appendChild(curSubElement);
                
                curSubElement = doc.createElement("last_updated");
                curSubElement.setTextContent(wurflInfo.getVDate());
                curElement.appendChild(curSubElement);
                
                curSubElement = doc.createElement("official_url");
                curSubElement.setTextContent(wurflInfo.getOfficialURL());
                curElement.appendChild(curSubElement);
                
                curSubElement = doc.createElement("maintainers");
                Element curSub2 = null;
                for (Person maintainer : wurflInfo.getMaintainers())
                {
                    curSub2 = doc.createElement("maintainer");
                    curSub2.setAttribute("name", maintainer.getName());
                    curSub2.setAttribute("email", maintainer.getEmail());
                    String homepage = maintainer.getHomepage();
                    if ((homepage != null) && (homepage.length() > 0))
                    {
                        curSub2.setAttribute("home_page", homepage);
                    }
                    curSubElement.appendChild(curSub2);
                }
                curElement.appendChild(curSubElement);
                
                curSubElement = doc.createElement("authors");
                curSub2 = null;
                for (Person author : wurflInfo.getAuthors())
                {
                    curSub2 = doc.createElement("author");
                    curSub2.setAttribute("name", author.getName());
                    curSub2.setAttribute("email", author.getEmail());
                    String homepage = author.getHomepage();
                    if ((homepage != null) && (homepage.length() > 0))
                    {
                        curSub2.setAttribute("home_page", homepage);
                    }
                    curSubElement.appendChild(curSub2);
                }
                curElement.appendChild(curSubElement);
                
                curSubElement = doc.createElement("contributors");
                curSub2 = null;
                for (Person contributor : wurflInfo.getContributors())
                {
                    curSub2 = doc.createElement("contributor");
                    curSub2.setAttribute("name", contributor.getName());
                    curSub2.setAttribute("email", contributor.getEmail());
                    String homepage = contributor.getHomepage();
                    if ((homepage != null) && (homepage.length() > 0))
                    {
                        curSub2.setAttribute("home_page", homepage);
                    }
                    curSubElement.appendChild(curSub2);
                }
                curElement.appendChild(curSubElement);
                
                CDATASection cData = doc.createCDATASection("statement");
                cData.setTextContent("\n" + wurflInfo.getStatement() + "\n");
                curElement.appendChild(cData);
    
                // Append "version" element to root
                rootElement.appendChild(curElement);
            }
            else
            {
                rootElement = doc.createElement("wurfl_patch");
            }
            
            curElement = doc.createElement("devices");
            
            if (fullExport)
            {
                curElement.appendChild(doc.createComment(" \"user_agent\" is " +
                                                         "a subset of the " +
                                                         "user_agent string "));
                curElement.appendChild(doc.createComment(" \"fall_back\" uses" +
                                                         " (unique) ID " +
                                                         "attribute to track " +
                                                         "down more generic " +
                                                         "device to delegate " +
                                                         "to "));
                curElement.appendChild(doc.createComment(" \"id\" is a " +
                                                         "unique ID for each " +
                                                         "device. ID is a " +
                                                         "mnemonic string " +
                                                         "decided by Author "));
            }
            
            Device device = null;
            Hashtable<String, Device> devices = wurflInfo.getDevices();
            ArrayList<String> devKeys = new ArrayList<String>(devices.keySet());
            Collections.sort(devKeys);
            if (devKeys.contains("generic"))
            {
                devKeys.remove("generic");
                devKeys.add(0, "generic");
            }
            Iterator<String> devKeysIt = devKeys.iterator();
            String curDev = null;
            
            Group group = null;
            ArrayList<String> grpKeys = null;
            Iterator<String> grpKeyIt = null;
            String curGrp = null;
            Element grpElement = null;
            
            Capability capability = null;
            ArrayList<String> capKeys = null;
            Iterator<String> capKeyIt = null;
            String curCap = null;
            Element capElement = null;
            
            boolean capAdded = false;
            boolean grpAdded = false; 
            
            while (devKeysIt.hasNext())
            {
                grpAdded = false;
                curDev = devKeysIt.next();
                device = devices.get(curDev);
                if ((fullExport) || (device.isPatchedDevice()))
                {
                    curSubElement = doc.createElement("device");
                    curSubElement.setAttribute("id", device.getDeviceID());
                    curSubElement.setAttribute("fall_back",
                                               device.getFallbackID());
                    curSubElement.setAttribute("user_agent",
                                               device.getUserAgent());
                    if (device.isActualDevice())
                    {
                        curSubElement.setAttribute("actual_device_root",
                                                   "true");
                    }
                    
                    grpKeys = new ArrayList<String>(device.getGroups().
                                                                      keySet());
                    Collections.sort(grpKeys);
                    grpKeyIt = grpKeys.iterator();
                    while (grpKeyIt.hasNext())
                    {
                        capAdded = false;
                        curGrp = grpKeyIt.next();
                        group = device.getGroups().get(curGrp);
                        grpElement = doc.createElement("group");
                        grpElement.setAttribute("id", group.getGroupID());
                        
                        capKeys = new ArrayList<String>(group.getCapabilities().
                                                                      keySet());
                        Collections.sort(capKeys);
                        capKeyIt = capKeys.iterator();
                        while (capKeyIt.hasNext())
                        {
                            curCap = capKeyIt.next();
                            capability = group.getCapabilities().get(curCap);
                            if ((fullExport) || (capability.isPatched()))
                            {
                                capElement = doc.createElement("capability");
                                capElement.setAttribute("name", curCap);
                                capElement.setAttribute("value",
                                                        capability.getValue());
                                
                                grpElement.appendChild(capElement);
                                capAdded = true;
                            }
                        }
                        
                        if (capAdded)
                        {
                            curSubElement.appendChild(grpElement);
                            grpAdded = true;
                        }
                    }
                    
                    if (grpAdded)
                    {
                        curElement.appendChild(curSubElement);
                    }
                }
            }
            
            // Append "devices" element to root
            rootElement.appendChild(curElement);
            
            FileOutputStream fos = new FileOutputStream(file);
            StreamResult sr = new StreamResult(fos);
            
            DOMSource ds = new DOMSource(rootElement);
            
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty("indent", "yes");
            t.transform(ds, sr);
            fos.close();
        }
        catch(ParserConfigurationException pce)
        {
            System.err.println("Couldn't create XML for export file: " +
                               pce.toString());
        }
        catch (TransformerException te)
        {
            System.err.println("Error managing XML for export file: " +
                               te.toString());
        }
    }
    
    /**
     * Accessor
     * @return the singleton instance of this class
     */
    public static WURFLExport getInstance()
    {
        if (instance == null)
        {
            instance = new WURFLExport();
        }
        
        return (instance);
    }
}
