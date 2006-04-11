/*
 * Class name : ConfigurationManager
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 09-Dec-2005
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ossltd.mdevinf.model.ComparisonDetails;
import com.ossltd.mdevinf.model.MakeModelDetails;
import com.ossltd.mdevinf.model.SearchDetails;

/**
 * Manages the user configuration file, storing patch selection and search
 * information.
 * @author Jim McLachlan
 * @version 1.0
 */
public class ConfigurationManager
{
    /** Constant defining the name of the configuration file. */
    private final String CONFIG_FILENAME = "mDevInf.cfg.xml";
    
    /** Singleton reference to this class. */
    private static ConfigurationManager instance = null;
    
    /** Stores the currently selected patch files. */
    private Hashtable<String, Boolean> patchSelections;
    
    /** Stores the user-defined searches. */
    private ArrayList<SearchDetails> searches;
    
    /** Stores the device selection history. */
    private ArrayList<MakeModelDetails> deviceHistory;

    /**
     * Stores the mapping between WURFL capability names and user defined names.
     */
    private Hashtable<String, String> capabilityNameMap;
    
    /**
     * Private constructor to maintain the integrity of the singleton pattern.
     */
    private ConfigurationManager()
    {
        patchSelections = new Hashtable<String, Boolean>();
        
        searches = new ArrayList<SearchDetails>();
        
        capabilityNameMap = new Hashtable<String, String>();
        
        deviceHistory = new ArrayList<MakeModelDetails>();

        loadConfig();
    }
    
    /**
     * Saves the current configuration data to the config file.
     */
    public void saveConfig()
    {
        try
        {
            DocumentBuilder patchBuilder = DocumentBuilderFactory.newInstance().
                                                           newDocumentBuilder();
            Document doc = patchBuilder.newDocument();
            doc.setXmlVersion("1.0");

            // ============ START OF DATA PREPARATION ============
            Element patchRoot = doc.createElement("patch_selections");
            Element curElement = null;
            Enumeration<String> patchFileKeys = patchSelections.keys();
            String filename = null;
            File toCheck = null;
            while (patchFileKeys.hasMoreElements())
            {
                filename = patchFileKeys.nextElement();
                toCheck = new File(filename);
                if (toCheck.exists() && (toCheck.canRead()))
                {
                    curElement = doc.createElement("patch_file");
                    curElement.setAttribute("name", filename);
                    curElement.setAttribute("selected",
                                      patchSelections.get(filename).toString());
                    patchRoot.appendChild(curElement);
                }
            }
            
            Element searchesRoot = doc.createElement("searches");
            Element compEl = null;
            for (SearchDetails detail : searches)
            {
                curElement = doc.createElement("search");
                curElement.setAttribute("name", detail.getSearchName());
                curElement.setAttribute("orComp", 
                                     Boolean.toString(detail.isOrComparison()));
                curElement.setAttribute("ignoreDefaults",
                                   Boolean.toString(detail.isIgnoreDefaults()));
                for (ComparisonDetails comp : detail.getComparisonItems())
                {
                    compEl = doc.createElement("comparison");
                    compEl.setAttribute("capability", comp.getCapability());
                    compEl.setAttribute("comparison", comp.getComparison());
                    compEl.setAttribute("value", comp.getValue());
                    curElement.appendChild(compEl);
                }
                
                searchesRoot.appendChild(curElement);
            }
            
            Element nameMapRoot = doc.createElement("capability_names");
            ArrayList<String> capKeys = 
                              new ArrayList<String>(capabilityNameMap.keySet());
            Collections.sort(capKeys);
            Iterator<String> it = capKeys.iterator();
            String curCap = null;
            while (it.hasNext())
            {
                curCap = it.next();
                curElement = doc.createElement("mapped_capability");
                curElement.setAttribute("wurfl_name", curCap);
                curElement.setAttribute("mapped_name",
                                        capabilityNameMap.get(curCap));
                nameMapRoot.appendChild(curElement);
            }
            
            Element deviceHistoryRoot = doc.createElement("device_history");
            for (MakeModelDetails dev : deviceHistory)
            {
                curElement = doc.createElement("device");
                curElement.setAttribute("device_make", dev.getMake());
                curElement.setAttribute("device_model", dev.getModel());
                deviceHistoryRoot.appendChild(curElement);
            }
            // ============= END OF DATA PREPARATION =============
            
            Element configRoot = doc.createElement("mdevinf_config");
            configRoot.appendChild(patchRoot);
            configRoot.appendChild(searchesRoot);
            configRoot.appendChild(nameMapRoot);
            configRoot.appendChild(deviceHistoryRoot);
            
            // Write the file
            FileOutputStream fos = new FileOutputStream(CONFIG_FILENAME);
            StreamResult sr = new StreamResult(fos);
            
            DOMSource ds = new DOMSource(configRoot);
            
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty("indent", "yes");
            t.transform(ds, sr);
            fos.close();
        }
        catch(ParserConfigurationException pce)
        {
            System.err.println("Couldn't create XML for configuration data: " +
                               pce.toString());
        }
        catch (TransformerException te)
        {
            System.err.println("Error managing XML for configuration data: " +
                               te.toString());
        }
        catch (IOException ioe)
        {
            System.err.println("Error creating config file: " + ioe.toString());
        }
    }

    /**
     * Loads the configuration data from the config file.
     */
    public void loadConfig()
    {
        File selectionFile = new File(CONFIG_FILENAME);
        if ((selectionFile.exists()) && (selectionFile.canRead()))
        {
            try
            {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().
                                                           newDocumentBuilder();
                Document doc = builder.parse(selectionFile);
                NodeList selectionList = doc.getElementsByTagName("patch_file");
                int selectionCount = selectionList.getLength();

                Element curElement = null;
                File toCheck = null;
                
                for (int i = 0; i < selectionCount; i++)
                {
                    curElement = (Element)selectionList.item(i);
                    toCheck = new File(curElement.getAttribute("name"));
                    if ((toCheck.exists()) && (toCheck.canRead()))
                    {
                        patchSelections.put(curElement.getAttribute("name"),
                            curElement.getAttribute("selected").equals("true"));
                    }
                }

                searches.clear();
                
                NodeList searchList = doc.getElementsByTagName("search");
                int searchCount = searchList.getLength();
                NodeList compList = null;
                int compCount = 0;
                SearchDetails detail = null;
                Element compEl = null;
                
                for (int i = 0; i < searchCount; i++)
                {
                    curElement = (Element)searchList.item(i);
                    detail = new SearchDetails(curElement.getAttribute("name"),
                             Boolean.valueOf(curElement.getAttribute("orComp")),
                             Boolean.valueOf(curElement.
                                               getAttribute("ignoreDefaults")));
                    compList = curElement.getElementsByTagName("comparison");
                    compCount = compList.getLength();
                    for (int j = 0; j < compCount; j++)
                    {
                        compEl = (Element)compList.item(j);
                        detail.addComparisonItem(new ComparisonDetails(
                                              compEl.getAttribute("capability"),
                                              compEl.getAttribute("comparison"),
                                              compEl.getAttribute("value")));
                    }
                    
                    searches.add(detail);
                }
                
                capabilityNameMap.clear();
                NodeList nameList =
                                  doc.getElementsByTagName("mapped_capability");
                int nameCount = nameList.getLength();
                
                for (int i = 0; i < nameCount; i++)
                {
                    curElement = (Element)nameList.item(i);
                    capabilityNameMap.
                                    put(curElement.getAttribute("wurfl_name"),
                                        curElement.getAttribute("mapped_name"));
                }
                
                deviceHistory.clear();
                NodeList devList = doc.getElementsByTagName("device");
                int devCount = devList.getLength();
                
                for (int i = 0; i < devCount; i++)
                {
                    curElement = (Element)devList.item(i);
                    MakeModelDetails mmd =
                          new MakeModelDetails(curElement.
                                                  getAttribute("device_make"),
                                               curElement.
                                                  getAttribute("device_model"));
                    deviceHistory.add(mmd);
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
    
    /**
     * Gets the list of all patch files.
     * @return the names (keys) and selection statuses (values) of all
     * available patch files
     */
    public Hashtable<String, Boolean> getPatchFileList()
    {
        File workingDirectory = new File(".");
        String[] patchFiles = workingDirectory.list(new FilenameFilter(){
            public boolean accept(File dir, String name)
            {
                boolean result = false;

                if ((name.startsWith("wurfl_patch")) &&
                    (name.endsWith(".xml")))
                {
                    result = true;
                }
                
                return (result);
            }
        });
        
        if (patchFiles != null)
        {
            for (int i = 0; i < patchFiles.length; i++)
            {
                if (!patchSelections.containsKey(patchFiles[i]))
                {
                    patchSelections.put(patchFiles[i], new Boolean(true));
                }
            }
        }
        
        return (patchSelections);
    }
    
    /**
     * Accessor
     * @return the user defined searches currently stored in the configuration
     * file
     */
    public ArrayList<SearchDetails> getSearches()
    {
        return (searches);
    }
    
    /**
     * Accessor
     * @return the mapping of WURFL capability names to user defined names
     */
    public Hashtable<String, String> getCapabilityNameMap()
    {
        return (capabilityNameMap);
    }
    
    /**
     * Accessor
     * @return the makes and models of recently selected devices
     */
    public ArrayList<MakeModelDetails> getDeviceHistory()
    {
        return (deviceHistory);
    }
    
    /**
     * Accessor
     * @return the singleton reference to this class
     */
    public static ConfigurationManager getInstance()
    {
        if (instance == null)
        {
            instance = new ConfigurationManager();
        }
        
        return (instance);
    }
}
