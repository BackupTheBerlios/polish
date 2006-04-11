/*
 * Class name : WURFLInfo
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 19-Oct-2005
 * 
 * Copyright (c) 2005-2006, Jim McLachlan  (jim@oss-ltd.com)
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
 * 
 */

package com.ossltd.mdevinf.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ossltd.mdevinf.ConfigurationManager;
import com.ossltd.mdevinf.MDevInfException;
import com.ossltd.mdevinf.ui.QueryField;


/**
 * A singleton class used to access the WURFL information.
 * @author Jim McLachlan
 * @version 1.0
 */
public class WURFLInfo
{
    /** Constant string used to declare when an attribute is missing. */
    private static final String unavailable = "Not available";
    
    /** Reference to the singleton instance of this class. */
    public static WURFLInfo instance = null;
    
    /**
     * Stores the description of any problem that occurred whilst loading
     * the WURFL data.
     */
    public static String loadingProblem = "";
    
    /** Stores the version of the WURFL file that is in use. */
    private String version;
    
    /** Stores the last update date of the file. */
    private String vDate;
    
    /** Stores the official URL for WURFL. */
    private String officialURL;
    
    /** Stores the maintainers of the WURFL. */
    private Vector<Person> maintainers;
    
    /** Stores the authors of the WURFL. */
    private Vector<Person> authors;
    
    /** Stores the contributors to the WURFL. */
    private Vector<Person> contributors;
    
    /** Stores the statement information from the WURFL. */
    private String statement;
    
    /**
     * Stores the WebStart image, if it is available.  This image should
     * only be available if the application is downloaded from a site
     * containing the file mDevInfoIconWS.png.
     */
    private ImageIcon mDevInfWSIcon;
    
    /**
     * Stores all the <code>Device</code> objects created from the wurfl.xml
     * file.
     * <p>The key to this <code>Hashtable</code> is the "id" field from the
     * wurfl.xml file.</p>
     */
    private Hashtable<String, Device> devices;

    /**
     * Stores the thumbnail images for the devices.
     */
    private Hashtable<String, ImageIcon> images;
    
    /**
     * Stores the types and descriptions for each of the capabilities.
     */
    private Hashtable<String, Description> descriptions;
    
    /**
     * Attempts to locate the value of the specified capability.  If the
     * capability is not defined in the device itself, a recursive search
     * is initiated that uses the device "fall_back" refernce to locate the
     * next most appropriate device until either the capability is found or
     * the "fall_back" is "root" (ie. no further ancestors available).
     * @param deviceID The device ID (eg. "mot_v600_ver1")
     * @param groupID The group containing the requested capability (eg. "j2me")
     * @param capability The name of the capability to retrieve.
     * @return The <code>Capability</code> (if found), otherwise
     * <code>null</code>.
     */
    private Capability getCapability(String deviceID,
                                     String groupID,
                                     String capability)
    {
        Capability result = null;
        Device device = devices.get(deviceID);
        if (device == null)
        {
            System.err.println("Failure to get device " + deviceID);
        }
        String fallbackID = device.getFallbackID();
        
        Hashtable<String, Capability> capabilities = null;
        
        if (device != null)
        {
            capabilities = device.getCapabilitiesForGroup(groupID);
            if (capabilities != null)
            {
                result = capabilities.get(capability);
                if ((result == null) &&
                    (fallbackID != null) &&
                    (!fallbackID.equals("root")))
                {
                    result = getCapability(fallbackID, groupID, capability);
                }
            }
            else
            {
                result = getCapability(fallbackID, groupID, capability);
            }
        }

        return (result);
    }
    
    /**
     * Takes the information from the provided document and updates the
     * internal device data structure accordingly.
     * @param doc the XML document to be amalgamated into the
     * <code>devices</code> data structure.
     */
    private void processWURFLContent(Document doc)
    {
        Device curDevice;
        NodeList devs = doc.getElementsByTagName("device");
        int devCount = devs.getLength();
        Element curEl = null;
        String devId = null;
        String devFallback = null;
        String userAgent = null;
        String actualDevice = null;
        NodeList grps = null;
        int grpCount = 0;
        Element curGrp = null;
        Group group = null;
        Hashtable<String, Capability> groupCaps = null;
        NodeList caps = null;
        int capCount = 0;
        Element curCap = null;
        for (int i = 0; i < devCount; i++)
        {
            curEl = (Element)devs.item(i);
            devId = curEl.getAttribute("id");
            devFallback = curEl.getAttribute("fall_back");
            userAgent = curEl.getAttribute("user_agent");
            actualDevice = curEl.getAttribute("actual_device_root");

            curDevice = new Device(devId,
                                   devFallback,
                                   userAgent,
                                   (actualDevice != null) &&
                                   (actualDevice.equals("true")),
                                   false);
            grps = curEl.getElementsByTagName("group");
            grpCount = grps.getLength();
            for (int j = 0; j < grpCount; j++)
            {
                curGrp = (Element)grps.item(j);
                group = new Group(curGrp.getAttribute("id"));
                
                groupCaps = group.getCapabilities();
                
                caps = curGrp.getElementsByTagName("capability");
                capCount = caps.getLength();
                for (int k = 0; k < capCount; k++)
                {
                    curCap = (Element)caps.item(k);
                    groupCaps.put(curCap.getAttribute("name"),
                                  new Capability(curCap.getAttribute("value"),
                                                 !devFallback.equals("root"),
                                                 false));
                }

                curDevice.addGroup(group);
            }

            devices.put(devId, curDevice);
            WURFLTree.getInstance().addNode(curDevice);
        }
    }
    
    /**
     * Reads the version and project member information from the provided
     * XML document.
     * @param doc the XML document containing the version and project member
     * information
     */
    private void processWURFLProjectMembers(Document doc)
    {
        NodeList currentNodes = doc.getElementsByTagName("ver");
        version = unavailable;
        if (currentNodes.getLength() > 0)
        {
            version = currentNodes.item(0).getTextContent();
        }
        vDate = unavailable;
        currentNodes = doc.getElementsByTagName("last_updated");
        if (currentNodes.getLength() > 0)
        {
            vDate = currentNodes.item(0).getTextContent();
        }
        officialURL = unavailable;
        currentNodes = doc.getElementsByTagName("official_url");
        if (currentNodes.getLength() > 0)
        {
            officialURL = currentNodes.item(0).getTextContent();
        }
        maintainers = new Vector<Person>();
        currentNodes = doc.getElementsByTagName("maintainers");
        if (currentNodes.getLength() > 0)
        {
            Element mntEl = (Element)currentNodes.item(0);
            NodeList mntNodes = mntEl.getElementsByTagName("maintainer");
            int mntCount = mntNodes.getLength();
            for (int i = 0; i < mntCount; i++)
            {
                Element curMnt = (Element)mntNodes.item(i);
                maintainers.add(new Person(
                                         curMnt.getAttribute("name"),
                                         curMnt.getAttribute("email"),
                                         curMnt.getAttribute("home_page")));
                
            }
        }
        authors = new Vector<Person>();
        currentNodes = doc.getElementsByTagName("authors");
        if (currentNodes.getLength() > 0)
        {
            Element autEl = (Element)currentNodes.item(0);
            NodeList autNodes = autEl.getElementsByTagName("author");
            int autCount = autNodes.getLength();
            for (int i = 0; i < autCount; i++)
            {
                Element curMnt = (Element)autNodes.item(i);
                authors.add(new Person(curMnt.getAttribute("name"),
                                       curMnt.getAttribute("email"),
                                       curMnt.getAttribute("home_page")));
                
            }
        }
        contributors = new Vector<Person>();
        currentNodes = doc.getElementsByTagName("contributors");
        if (currentNodes.getLength() > 0)
        {
            Element cntEl = (Element)currentNodes.item(0);
            NodeList cntNodes = cntEl.getElementsByTagName("contributor");
            int cntCount = cntNodes.getLength();
            for (int i = 0; i < cntCount; i++)
            {
                Element curMnt = (Element)cntNodes.item(i);
                contributors.add(new Person(
                                         curMnt.getAttribute("name"),
                                         curMnt.getAttribute("email"),
                                         curMnt.getAttribute("home_page")));
                
            }
        }
        
        currentNodes = doc.getElementsByTagName("statement");
        if (currentNodes.getLength() > 0)
        {
            Element stmtEl = (Element)currentNodes.item(0);
            statement = stmtEl.getTextContent().trim();
        }
        else
        {
            statement = "No statement text found.";
        }
    }

    /**
     * Reads the descriptions for the WURFL capabilities from the provided
     * document
     * @param doc the XML document containing the WURFL descriptions
     */
    private void processWURFLDescriptions(Document doc)
    {
        NodeList capDescs = doc.getElementsByTagName("capability");
        int descCount = capDescs.getLength();
        Description curDesc = null;
        Element descEl = null;
        NodeList tNodes = null;
        NodeList dNodes = null;
        NodeList nNodes = null;
        for (int i = 0; i < descCount; i++)
        {
            descEl = (Element)capDescs.item(i);
            tNodes = descEl.getElementsByTagName("type");
            dNodes = descEl.getElementsByTagName("description");
            if ((tNodes.getLength() > 0) && (dNodes.getLength() > 0))
            {
                curDesc = new Description(tNodes.item(0).getTextContent(),
                                          dNodes.item(0).getTextContent());
                nNodes = descEl.getElementsByTagName("name");
                if (nNodes.getLength() > 0)
                {
                    descriptions.put(nNodes.item(0).getTextContent(),
                                     curDesc);
                }
            }
        }
    }

    /**
     * Add the data contained in the WURFL patch file to the data that has
     * already been read from the main WURFL file.
     * @param patchDevs the list of device nodes to add to the hierarchy
     */
    public void processWURFLPatch(NodeList patchDevs)
    {
        int devCount = patchDevs.getLength();
        Element curEl = null;
        String devId = null;
        String devFallback = null;
        String userAgent = null;
        String actualDevice = null;
        NodeList patchGrps = null;
        int grpsCount = 0;
        Element patchEl = null;
        Device device = null;
        Hashtable<String, Group> groups = null;
        String groupName = null;
        Group curGroup = null;
        Hashtable<String, Capability> caps = null;
        NodeList patchCaps = null;
        int capCount = 0;
        Element curCap = null;
        Capability capEntry = null;
        for (int i = 0; i < devCount; i++)
        {
            curEl = (Element)patchDevs.item(i);
            devId = curEl.getAttribute("id");
            devFallback = curEl.getAttribute("fall_back");
            userAgent = curEl.getAttribute("user_agent");
            actualDevice = curEl.getAttribute("actual_device_root");
            device = devices.get(devId);
            
            // Find the existing device, or create a new one.
            if (device != null)
            {
                device.setPatchedDevice(true);
                if (!device.getUserAgent().equals(userAgent))
                {
                    device = null;
                    System.err.println("Error in wurfl_patch.xml, device " +
                                       "with id of " + devId + " should have " +
                                       "a user_agent value of " +
                                       device.getUserAgent() + ", but is " +
                                       userAgent);
                }
            }
            else
            {
                device = new Device(devId,
                                    devFallback,
                                    userAgent,
                                    (actualDevice != null) &&
                                    (actualDevice.equals("true")),
                                    true);
                devices.put(devId, device);
                WURFLTree.getInstance().addNode(device);
            }

            // Do not proceed if the device already exists, but has a different
            //  User Agent string.
            if (device != null)
            {
                groups = device.getGroups();
                patchGrps = curEl.getElementsByTagName("group");
                grpsCount = patchGrps.getLength();
                for (int j = 0; j < grpsCount; j++)
                {
                    patchEl = (Element)patchGrps.item(j);
                    groupName = patchEl.getAttribute("id");
                    curGroup = groups.get(groupName);
                    
                    // If the device exists, there may already be Group data,
                    //  so only create a new Group object if this doesn't exist.
                    if (curGroup == null)
                    {
                        curGroup = new Group(groupName);
                        device.addGroup(curGroup);
                    }

                    caps = curGroup.getCapabilities();
                    patchCaps = patchEl.getElementsByTagName("capability");
                    capCount = patchCaps.getLength();
                    for (int k = 0; k < capCount; k++)
                    {
                        curCap = (Element)patchCaps.item(k);
                        capEntry = caps.get(curCap.getAttribute("name"));
                        if (capEntry == null)
                        {
                            capEntry = new Capability(
                                                   curCap.getAttribute("value"),
                                                   !devFallback.equals("root"),
                                                   true);
                            
                            caps.put(curCap.getAttribute("name"), capEntry);
                        }
                        else
                        {
                            capEntry.setValue(curCap.getAttribute("value"));
                            capEntry.setOverridden(!devFallback.equals("root"));
                            capEntry.setPatched(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * This method reads in the data from the wurfl.zip file and creates
     * the internal data structure of <code>Device</code>s and <code>Group
     * </code>s.  It then reads the capability descriptions from capDesc.zip
     * and stores these in <code>descriptions</code>.
     * @throws MDevInfException if a problem occurs whilst loading the WURFL
     * data
     */
    private void prepareData() throws MDevInfException
    {
        Document doc = null;
        
        devices = new Hashtable<String, Device>();
        images = new Hashtable<String, ImageIcon>();
        descriptions = new Hashtable<String, Description>();

        try
        {
            mDevInfWSIcon = null;
            
            java.net.URL imageURL = getClass().
                                              getResource("/mDevInfIconWS.png");
            if (imageURL != null)
            {
                mDevInfWSIcon = new ImageIcon(imageURL);
            }
            // Check to see if a WURFL file is in the working directory.
            // If it is, use that as the WURFL source instead of the built-in
            //  file.
            InputStream is = null;
            
            File wurflFile = new File("wurfl.zip");
            if ((mDevInfWSIcon == null) &&
                (wurflFile.exists()) && (wurflFile.canRead()))
            {
                is = new ZipInputStream(new FileInputStream(wurflFile));
                ((ZipInputStream)is).getNextEntry();
            }
            else
            {
                wurflFile = new File("wurfl.xml");
                if ((mDevInfWSIcon == null) &&
                    (wurflFile.exists()) && (wurflFile.canRead()))
                {
                    is = new FileInputStream(wurflFile);
                }
                else
                {
                    is = new ZipInputStream(getClass().getClassLoader().
                                              getResourceAsStream("wurfl.zip"));
                    ((ZipInputStream)is).getNextEntry();
                }
            }
            
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().
                                                           newDocumentBuilder();

            doc = builder.parse(is);
            is.close();
            
            processWURFLProjectMembers(doc);

            processWURFLContent(doc);
            
            if (mDevInfWSIcon == null)
            {
                FileInputStream fis = null;
                Hashtable<String, Boolean> patchFileList =
                          ConfigurationManager.getInstance().getPatchFileList();
                
                Enumeration<String> patchFileKeys = patchFileList.keys();
                String patch = null;
                while (patchFileKeys.hasMoreElements())
                {
                    patch = patchFileKeys.nextElement();
                    if (patchFileList.get(patch))
                    {
                        try
                        {
                            File patchFile = new File(patch);
                            if ((patchFile.exists()) && (patchFile.canRead()))
                            {
                                fis = new FileInputStream(patch);
                                builder = DocumentBuilderFactory.newInstance().
                                                           newDocumentBuilder();
                                Document patchDoc = builder.parse(fis);
                                fis.close();
                            
                                if (patchDoc.
                                        getElementsByTagName("wurfl_patch").
                                                               getLength() == 1)
                                {
                                    System.out.println("Including " + patch);                
                                    processWURFLPatch(
                                       patchDoc.getElementsByTagName("device"));
                                }
                                else
                                {
                                    System.err.println(patch + " does not " +
                                                       "contain the " +
                                                       "<wurfl_patch> tag!");
                                }
                            }
                        }
                        catch (Exception patchEx)
                        {
                            System.err.println("Non critical exception " +
                                               "reading " + patch + " : " +
                                               patchEx.toString());
                        }
                    }
                }
            }
            
            WURFLTree.getInstance().manageOrphans();
            
            File imageFile = new File("device_pix.zip");
            ZipInputStream imageFileStream = null;
            
            if ((mDevInfWSIcon == null) &&
                (imageFile.exists()) && (imageFile.canRead()))
            {
                imageFileStream = new ZipInputStream(
                                                new FileInputStream(imageFile));
            }
            else
            {
                imageFileStream = new ZipInputStream(
                                         getClass().getClassLoader().
                                         getResourceAsStream("device_pix.zip"));
            }
            
            if (imageFileStream != null)
            {
                ZipEntry entry = imageFileStream.getNextEntry();
                String imageName = null;
                ImageIcon curImage = null;
                byte[] data = new byte[2048];
                
                while (entry != null)
                {
                    if (!entry.isDirectory())
                    {
                        imageName = entry.getName();
                        int nameStart = imageName.indexOf("/") + 1;
                        int nameEnd = imageName.indexOf(".");
                        imageName = imageName.substring(nameStart, nameEnd);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        int count = 0;
                        while ((count = imageFileStream.read(data, 0, 2048)) !=
                               -1)
                        {
                            bos.write(data, 0, count);
                        }
    
                        curImage = new ImageIcon(bos.toByteArray());
                        
                        images.put(imageName, curImage);
                    }

                    entry = imageFileStream.getNextEntry();
                }
            }
            
            File capDescFile = new File("capDesc.zip");
            if ((mDevInfWSIcon == null) &&
                (capDescFile.exists()) && (capDescFile.canRead()))
            {
                is = new ZipInputStream(new FileInputStream(capDescFile));
                ((ZipInputStream)is).getNextEntry();
            }
            else
            {
                is = new ZipInputStream(getClass().getClassLoader().
                                            getResourceAsStream("capDesc.zip"));
                ((ZipInputStream)is).getNextEntry();
            }
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(is);
            is.close();

            processWURFLDescriptions(doc);
        }
        catch (ParserConfigurationException pce)
        {
            System.err.println("PCE: " + pce.toString());
        }
        catch (SAXException saxe)
        {
            System.err.println("SAXE: " + saxe.toString());
        }
        catch (IOException ioe)
        {
            System.err.println("IOE reading doc: " + ioe.toString());
        }
    }

    /**
     * This constructor is declared <code>private</code> to ensure the
     * integrity of it's singleton pattern.
     * @throws MDevInfException if a problem occurs whilst loading the WURFL
     * data
     */
    private WURFLInfo() throws MDevInfException
    {
        prepareData();
    }

    /**
     * Accessor
     * @return the version of the WURFL file being used.
     */
    public String getVersion()
    {
        return (version);
    }
    
    /**
     * Accessor
     * @return the last update date of the WURFL file being used.
     */
    public String getVDate()
    {
        return (vDate);
    }
    
    /**
     * Accessor
     * @return the official URL of the WURFL project.
     */
    public String getOfficialURL()
    {
        return (officialURL);
    }
    
    /**
     * Accessor
     * @return this list of maintainers of this version of WURFL
     */
    public Vector<Person> getMaintainers()
    {
        return (maintainers);
    }
    
    /**
     * Accessor
     * @return the list of authors of this version of WURFL
     */
    public Vector<Person> getAuthors()
    {
        return (authors);
    }
    
    /**
     * Accessor
     * @return the list of contributors to this version of WURFL
     */
    public Vector<Person> getContributors()
    {
        return (contributors);
    }
    
    /**
     * Accessor
     * @return the statement from the WURFL file
     */
    public String getStatement()
    {
        return (statement);
    }
    
    /**
     * Accessor
     * @return the capability types and descriptions.
     */
    public Hashtable<String, Description> getDescriptions()
    {
        return (descriptions);
    }
    
    /**
     * Accessor
     * @return the currently stored devices.
     */
    public Hashtable<String, Device> getDevices()
    {
        return (devices);
    }
    
    /**
     * Accessor
     * @param capability the capability to be described.
     * @return the <code>Description</code> for the specified capability
     */
    public Description getDescription(String capability)
    {
        return (descriptions.get(capability));
    }
    
    /**
     * Accessor
     * @return the mDevInfWSIcon (WebStart version) if it has been created,
     * otherwise the returned value will be <code>null</code>.
     */
    public ImageIcon getMDevInfWSIcon()
    {
        return (mDevInfWSIcon);
    }
    
    /**
     * Accessor
     * @param deviceId the device whose image is required
     * @return the thumbnail image for the requested device
     */
    public ImageIcon getImage(String deviceId)
    {
        return (images.get(deviceId));
    }
    
    /**
     * Retrieves all the makes (brand names) in the data structure.
     * @return An Vector (for easy use with JComboBox constructor) containing
     * the brand_name Strings.
     */
    public Vector<String> getMakes()
    {
        Vector<String> result = new Vector<String>();
        
        Hashtable<String, Group> groups = null;
        Hashtable<String, Capability> capabilities = null;
        Capability brand = null;
        Enumeration<String> devKeys = devices.keys();
        Device device = null;
        while (devKeys.hasMoreElements())
        {
            device = devices.get(devKeys.nextElement());
            groups = device.getGroups();
            if (groups != null)
            {
                Enumeration<String> groupKeys = groups.keys();
                while (groupKeys.hasMoreElements())
                {
                    Group curGroup = groups.get(groupKeys.nextElement());

                    capabilities = curGroup.getCapabilities();
                    if (capabilities.containsKey("brand_name"))
                    {
                        brand = capabilities.get("brand_name");
                        if ((brand != null) &&
                            (brand.getValue().length() > 0) &&
                            (!result.contains(brand.getValue())))
                        {
                            result.add(brand.getValue());
                        }
                    }
                }
            }
        }
        
        return (result);
    }
    
    /**
     * This method builds and returns a list of all models
     * (<code>model_name</code>) of device for the specified manufacturer
     * (<code>brand_name</code>).
     * @param make The manufacturer selected by the user.
     * @return An Vector (for easy use with JComboBox constructor) containing
     * the brand_name Strings.
     */
    public Vector<String> getModels(String make)
    {
        Vector<String> result = new Vector<String>();
        
        Capability devMake = null;
        Capability devModel = null;
        
        for (Device curDev : devices.values())
        {
            if (curDev.isActualDevice())
            {
                devMake = getCapability(curDev.getDeviceID(),
                                        "product_info",
                                        "brand_name");
                if ((devMake != null) && (devMake.getValue().equals(make)))
                {
                    devModel = getCapability(curDev.getDeviceID(),
                                             "product_info",
                                             "model_name");
                    if ((devModel != null) &&
                        (!result.contains(devModel.getValue())))
                    {
                        result.add(devModel.getValue());
                    }
                }
            }
        }
        
        return (result);
    }

    /**
     * Retrieves all the capabilities for the specified device using 
     * recursive calls to any "fall_back" devices, until "root" is reached.
     * <p>The <code>capsSoFar</code> parameter passed in to this method should
     * originally be an empty <code>Hashtable</code>.  This
     * <code>Hashtable</code> is filled in with the current details and returned
     * back out of the method.  The method should be used as follows:</p>
     * <code>
     * <br/>
     * <p>Device myDevice = ...</p>
     * <p>Hashtable<String, String> caps = new Hashtable<String, String>();</p>
     * <br/>
     * <p>caps = getAllCapabilitiesFor(myDevice, caps);</p>
     * <br/>
     * </code>
     * <p>The final result of the recursion will appear in the <code>caps
     * Hashtable</code>.</p>
     * <p>Any capabilities that are already specified are not overwritten!</p>
     * @param dev The device whose capabilities are being retrieved
     * @param capsSoFar The capabilites accrued to date through the recursion
     * process.
     * @return All the capabilities for the specified device.
     */
    public Hashtable<String, Capability>
                  getAllCapabilitiesFor(Device dev,
                                        Hashtable<String, Capability> capsSoFar)
    {
        Iterator<Group> it = dev.getGroups().values().iterator();
        Group curGroup = null;
        while (it.hasNext())
        {
            curGroup = it.next();
            Hashtable<String, Capability> caps = curGroup.getCapabilities();
            Enumeration<String> capKeys = caps.keys();
            String curCap = null;
            while (capKeys.hasMoreElements())
            {
                curCap = capKeys.nextElement();
                if (!capsSoFar.containsKey(curCap))
                {
                    capsSoFar.put(curCap, caps.get(curCap));
                }
            }
        }
        
        if (!dev.getFallbackID().equals("root"))
        {
            getAllCapabilitiesFor(devices.get(dev.getFallbackID()), capsSoFar);
        }
        
        return (capsSoFar);
    }
    
    /**
     * This method builds and returns a list of all capabilities for the
     * provided device make (<code>brand_name</code>) and model
     * (<code>model_name</code>).
     * @param make The manufacturer selected by the user
     * @param model The model selected by the user
     * @return A list of 
     */
    public Hashtable<String, Capability> getCapabilitiesFor(String make,
                                                            String model)
    {
        String devMake = null;
        String devModel = null;
        
        Iterator<Device> it = devices.values().iterator();
        boolean found = false;
        Device curDev = null;
        while (it.hasNext() && (!found))
        {
            curDev = it.next();

            if (curDev.isActualDevice())
            {
                devMake = getCapability(curDev.getDeviceID(),
                                        "product_info",
                                        "brand_name").getValue();
                if (devMake.equals(make))
                {
                    devModel = getCapability(curDev.getDeviceID(),
                                             "product_info",
                                             "model_name").getValue();
                    if (devModel.equals(model))
                    {
                        found = true;
                    }
                }
            }
        }
        
        Hashtable<String, Capability> result = 
                                            new Hashtable<String, Capability>();
        
        result = getAllCapabilitiesFor(curDev, result);
        
        return (result);
    }
    
    /**
     * Gets all the <code>Group</code>s for the specified device ID.  
     * @param devID The device whose group details are to be retrieved.
     * @return A <code>Hashtable</code> with the group name as the key and
     * <code>Group</code> objects as values for the specified device ID.
     */
    public Hashtable<String, Group> getGroups(String devID)
    {
        return (devices.get(devID).getGroups());
    }
    
    /**
     * Searches the data structure for devices matching the user defined
     * queries.  Note that query fields are logically <i>AND</i>ed together.
     * @param toCheck the collection of <code>QueryField</code> objects to
     * compare against the device data.
     * @param orCriteria a flag to indicate whether the criteria should be
     * logically ORed (<code>true</code>) or ANDed together.
     * @param ignoreDefaults a flag to indicate whether capability values
     * should only be considered if they have been overridden.
     * @return a <code>Vector</code> containing a list of make/model names
     * of each device that matches the provided queries.
     */
    public Vector<MatchedDevice> search(Hashtable<String, QueryField> toCheck,
                                        boolean orCriteria,
                                        boolean ignoreDefaults)
    {
        if (toCheck.size() == 0)
        {
            orCriteria = false;
        }
        
        Vector<MatchedDevice> result = new Vector<MatchedDevice>();
        
        Enumeration<String> devKeys = devices.keys();
        String curDev = null;
        Device device = null;
        Hashtable<String, Capability> curProps = null;
        String propValue = null;
        
        Enumeration<String> compKeys = null;
        String compKey = null;
        String compField = null;
        
        String comparator = "";
        
        while (devKeys.hasMoreElements())
        {
            curDev = devKeys.nextElement();
            device = devices.get(curDev);
            if (device.isActualDevice())
            {
                curProps = new Hashtable<String, Capability>();
                curProps = getAllCapabilitiesFor(devices.get(curDev), curProps);
                compKeys = toCheck.keys();
    
                boolean allMatched = true;
                boolean anyMatched = false;
                boolean continueSearching = true;
                boolean matchFound = !orCriteria;
                
                int val1 = 0;
                int val2 = 0;
                
                while ((compKeys.hasMoreElements()) && (continueSearching))
                {
                    compKey = compKeys.nextElement();
                    compField = toCheck.get(compKey).getValueAsText();
                    comparator = (String)toCheck.get(compKey).
                                         getComparisonField().getSelectedItem();
                    if ((curProps.get(compKey).isOverridden()) ||
                        (!ignoreDefaults))
                    {
                        propValue = curProps.get(compKey).getValue();
                        if (comparator.equals("="))
                        {
                            if (!compField.equals(propValue))
                            {
                                allMatched = false;
                            }
                            else
                            {
                                anyMatched = true;
                            }
                        }
                        else
                        {                    
                            try
                            {
                                val1 = Integer.parseInt(propValue);
                                val2 = Integer.parseInt(compField);
    
                                if (comparator.equals("<"))
                                {
                                      if (val1 >= val2)
                                      {
                                          allMatched = false;
                                      }
                                      else
                                      {
                                          anyMatched = true;
                                      }
                                }
                                else if (comparator.equals("<="))
                                {
                                      if (val1 > val2)
                                      {
                                          allMatched = false;
                                      }
                                      else
                                      {
                                          anyMatched = true;
                                      }
                                }
                                else if (comparator.equals(">="))
                                {
                                      if (val1 < val2)
                                      {
                                          allMatched = false;
                                      }
                                      else
                                      {
                                          anyMatched = true;
                                      }
                                }
                                else if (comparator.equals(">"))
                                {
                                      if (val1 <= val2)
                                      {
                                          allMatched = false;
                                      }
                                      else
                                      {
                                          anyMatched = true;
                                      }
                                }
                                else
                                {
                                    allMatched = false;
                                }
                            }
                            catch (NumberFormatException nfe)
                            {
                                allMatched = false;
                            }
                        }
                    
                        // If we are ORing the criteria and a match was found,
                        //  stop searching.
                        if ((orCriteria) && (anyMatched))
                        {
                            continueSearching = false;
                            matchFound = true;
                        }
                        
                        // If we are ANDing criteria and a match has failed,
                        //  stop searching.
                        if ((!orCriteria) && (!allMatched))
                        {
                            continueSearching = false;
                            matchFound = false;
                        }
                    }
                    else
                    {
                        matchFound = false;
                        continueSearching = false;
                    }
                }
                
                if (matchFound)
                {
                    result.add(
                      new MatchedDevice(curProps.get("brand_name").getValue(),
                                        curProps.get("model_name").getValue()));
                }
            }
        }
        
        return (result);
    }
    
    /**
     * This method checks the content of the data read in from the WURFL file
     * to see if anything is amiss.  At the moment, the following checks are
     * carried out:
     * <p><ul>
     * <li>WURFL version is available</li>
     * <li>WURFL last updated date is available</li>
     * <li>WURFL official URL is available</li>
     * <li>WURFL maintainers are available</li>
     * <li>WURFL authors are available</li>
     * <li>WURFL contributors are available</li>
     * <li>Devices that are <i>actual_root_devices</i> <b>must</b> have both
     * a <code>brand_name</code> and <code>model_name</code> available.</li>
     * <li>No device should contain a capability that does not appear in the
     * <code>generic</code> device.</li>
     * <li>Every capability in the <code>generic</code> device should have a
     * corresponding capability description.</li>
     * </ul>
     * @return The results of the tests.
     */
    public Vector<ValidationResult> validateWURFL()
    {
        Vector<ValidationResult> results = new Vector<ValidationResult>();
        
        // Get "generic" device properities for reference in the following tests
        Hashtable<String, Capability> allCaps =
            new Hashtable<String, Capability>();
               allCaps = getAllCapabilitiesFor(devices.get("generic"), allCaps);
               
        // Check for "version"
        ValidationResult result = new ValidationResult("WURFL Version: ");
        
        if ((version == null) || (version.equals(unavailable)))
        {
            result.setResult(unavailable);
        }

        results.add(result);
        
        // Check for "last update"
        result = new ValidationResult("WURFL last updated date: ");
        if ((vDate == null) || (vDate.equals(unavailable)))
        {
            result.setResult(unavailable);
        }

        results.add(result);
        
        // Check for "official URL"
        result = new ValidationResult("WURFL official URL: ");
        if ((officialURL == null) || (officialURL.equals(unavailable)))
        {
            result.setResult(unavailable);
        }

        results.add(result);
        
        // Check for "maintainers"
        result = new ValidationResult("WURFL Maintainers: ");
        if ((maintainers == null) || (maintainers.size() == 0))
        {
            result.setResult(unavailable);
        }

        results.add(result);

        // Check for "authors"
        result = new ValidationResult("WURFL Authors: ");
        if ((authors == null) || (authors.size() == 0))
        {
            result.setResult(unavailable);
        }

        results.add(result);

        // Check for "contributors"
        result = new ValidationResult("WURFL Contributors: ");
        if ((contributors == null) || (contributors.size() == 0))
        {
            result.setResult(unavailable);
        }

        results.add(result);
        
        // Check actual devices for brand_name and model_name
        // To save on processing this loop (of all devices) can also be used
        //  to check for rogue capabilities
        ValidationResult devResult = new ValidationResult("WURFL Devices: ");
        ValidationResult capResult = new ValidationResult("Capabilities: ");
        Set<String> allCapKeys = allCaps.keySet();
        
        StringBuffer devTest = new StringBuffer();
        StringBuffer capTest = new StringBuffer();
        boolean devProblemFound = false;
        boolean capProblemFound = false;
        
        Enumeration<String> devKeys = devices.keys();
        String curKey = null;
        Capability make = null;
        Capability model = null;
        Device curDev = null;
        Hashtable<String, Group> groups = null;
        Hashtable<String, Capability> caps = null;
        Enumeration<String> grpKeys = null;
        while (devKeys.hasMoreElements())
        {
            caps = new Hashtable<String, Capability>();
            curKey = devKeys.nextElement();
            curDev = devices.get(curKey);
            if (curDev.isActualDevice())
            {
                caps = getAllCapabilitiesFor(curDev, caps);
                make = caps.get("brand_name");
                model = caps.get("model_name");
            
                if ((make == null) || (make.getValue().length() == 0))
                {
                    devTest.append("\n  No make (brand_name) for device id = ");
                    devTest.append(curKey);
                    devProblemFound = true;
                }
                
                if ((model == null) || (model.getValue().length() == 0))
                {
                    devTest.
                           append("\n  No model (model_name) for device id = ");
                    devTest.append(curKey);
                    devProblemFound = true;
                }
            }
            groups = curDev.getGroups();
            grpKeys = groups.keys();

            while (grpKeys.hasMoreElements())
            {
                Set<String> curCapKeys = groups.get(grpKeys.nextElement()).
                                                 getCapabilities().keySet();
                for (String cap : curCapKeys)
                {
                    if (!allCapKeys.contains(cap))
                    {
                        if (!capProblemFound)
                        {
                            capTest.insert(0, "\n ");
                            capTest.insert(0, curDev.getDeviceID());
                            capTest.insert(0, " defines:");
                        }
                        capProblemFound = true;
                        capTest.append("\n  ");
                        capTest.append(cap);
                    }
                }
            }
        }
        
        if (devProblemFound)
        {
            devResult.setResult(devTest.toString());
        }
        
        if (capProblemFound)
        {
            capResult.setResult(capTest.toString());
        }
        
        results.add(devResult);
        results.add(capResult);

        // Check for capability descriptions
        result = new ValidationResult("Capability Descriptions: ");
        StringBuffer curTest = new StringBuffer();
        Enumeration<String> capKeys = allCaps.keys();
        String curCap = null;
        boolean problemFound = false;
        while (capKeys.hasMoreElements())
        {
            curCap = capKeys.nextElement();
            if (descriptions.get(curCap) == null)
            {
                curTest.append("\n  No description for ");
                curTest.append(curCap);
                problemFound = true;
            }
        }

        if (problemFound)
        {
            result.setResult(curTest.toString());
        }
        
        results.add(result);
        
        return (results);
    }
    
    /**
     * Find all user agent profiles in the device list that contain the
     * provided text.
     * @param toMatch the text to find in the user agent strings
     * @param realDevicesOnly only search for real devices
     * @return the list of matching UA-Profiles.
     */
    public Vector<String> findUAMatches(String toMatch, boolean realDevicesOnly)
    {
        Vector<String> result = new Vector<String>();

        Enumeration<String> devKeys = devices.keys();
        Device curDev = null;
        String devUserAgent = null;
        
        while (devKeys.hasMoreElements())
        {
            curDev = devices.get(devKeys.nextElement());
            devUserAgent = curDev.getUserAgent();
            if ((!realDevicesOnly) || (curDev.isActualDevice()))
            {
                if (devUserAgent.indexOf(toMatch) > -1)
                {
                    result.add(devUserAgent);
                }
            }
        }
        
        return (result);
    }
    
    /**
     * Attempts to find the device that matches the user agent profile provided.
     * @param uaProf the user agent profile to search for
     * @return the device (if found), otherwise <code>null</code>
     */
    public Device findDeviceForUAProf(String uaProf)
    {
        Device result = null;
        
        boolean found = false;
        Enumeration<String> devKeys = devices.keys();
        Device curDev = null;
        
        while ((!found) && (devKeys.hasMoreElements()))
        {
            curDev = devices.get(devKeys.nextElement());
            if (curDev.getUserAgent().equals(uaProf))
            {
                result = curDev;
                found = true;
            }
        }
        
        return (result);
    }
    
    /**
     * Find the device that matches the make (brand_name) and model (model_name)
     * @param make the brand_name to find in the device list
     * @param model the model_name to find in the device list
     * @param actualDevice a flag indicating whether only actual devices should
     * be matched.
     * @return the matched <code>Device</code> or <code>null</code> if no match
     * is found
     */
    public Device findDeviceForMakeModel(String make,
                                         String model,
                                         boolean actualDevice)
    {
        Device device = null;
        
        boolean found = false;
        Device curDev = null;
        Enumeration<String> devKeys = devices.keys();
        Hashtable<String, Capability> curProps = null;
                                            
        while((!found) && (devKeys.hasMoreElements()))
        {
            curDev = devices.get(devKeys.nextElement());
            curProps = new Hashtable<String, Capability>();
            curProps = getAllCapabilitiesFor(curDev, curProps);
            if ((make.equals(curProps.get("brand_name").getValue())) &&
                (model.equals(curProps.get("model_name").getValue())) &&
                ((!actualDevice) || (curDev.isActualDevice())))
            {
                device = curDev;
                found = true;
            }
        }
        
        return (device);
    }
    
    /**
     * Confirms whether the supplied device will be unique if added to the
     * WURFL hierarchy.
     * <p>The user agent and device ID must be unique in the hierarchy.</p>
     * @param device the device to check.
     * @return A <code>String</code> containing "OK" if the device is unique
     * or the description of the problem if it isn't.
     */
    public String deviceIsUnique(Device device)
    {
        String result = "OK";
        
        Enumeration<String> devKeys = devices.keys();
        Device curDev = null;
        while ((result.equals("OK")) && (devKeys.hasMoreElements()))
        {
            curDev = devices.get(devKeys.nextElement());
            if (curDev.getUserAgent().equals(device.getUserAgent()))
            {
                result = "User Agent is not unique";
            }
            else
            {
                if (curDev.getDeviceID().equals(device.getDeviceID()))
                {
                    result = "Device ID is not unique";
                }
            }
        }
        
        return (result);
    }
    
    /**
     * Gets the name of the group that contains the specified capability.
     * <p>The primary use for this method is when adding a new capability
     * through the editor.  The expected response in that case is
     * <code>null</code>.
     * @param capability the name of the capability to find
     * @return the name of the group that contains the capability or
     * <code>null</code> if the capability cannot be found.
     */
    public String getGroupForCapability(String capability)
    {
        String result = null;
        
        Device generic = devices.get("generic");
        Hashtable<String, Group> groups = generic.getGroups();
        Enumeration<String> groupKeys = groups.keys();
        String curGroup = null;
        Hashtable<String, Capability> capabilities = null;
        Enumeration<String> capKeys = null;
        
        while ((result == null) && (groupKeys.hasMoreElements()))
        {
            curGroup = groupKeys.nextElement();
            capabilities = groups.get(curGroup).getCapabilities();
            capKeys = capabilities.keys();
            while ((result == null) && (capKeys.hasMoreElements()))
            {
                if (capKeys.nextElement().equals(capability))
                {
                    result = curGroup;
                }
            }
        }
        
        return (result);
    }
    
    /**
     * Adds the specified capability to the specified group
     * @param group the group in which to add the new capability
     * @param capability the name of the new capability
     * @param defaultValue the value of the new capability
     */
    public void addCapability(String group,
                              String capability,
                              String defaultValue)
    {
        Device generic = devices.get("generic");
        Group groupToUpdate = generic.getGroups().get(group);
        Capability cap = new Capability(defaultValue, false, false);
        groupToUpdate.getCapabilities().put(capability, cap);
    }

    /**
     * Used to removed the singleton instance.
     * <p>This forces complete reloading of the data when the object is next
     * accessed.</p>
     */
    public static void destroy()
    {
        instance = null;
    }
    
    /**
     * Accessor for the singleton instance of this class.
     * @return The singleton instance of this class.
     */
    public static WURFLInfo getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = new WURFLInfo();
            }
            catch (MDevInfException mdei)
            {
                instance = null;
                loadingProblem = mdei.toString();
            }
        }
        
        return (instance);
    }
}
