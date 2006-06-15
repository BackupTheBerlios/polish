/*
 * Created on Mar 28, 2006 at 4:04:48 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.WURFLInfo;
import com.ossltd.mdevinf.model.WURFLTree;

import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceManager;
import de.enough.polish.devices.Vendor;
import de.enough.polish.devices.VendorManager;

/**
 * TODO: Add missing vendors to vendors.xml.
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Mar 28, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class Converter {

    private WURFLTree wurflTree;
    private WURFLInfo wurflInfo;
    private DeviceManager deviceManager;
    private DeviceDatabase deviceDatabase;
    private Stats stats;
    private boolean optionInsertMissingDevices;
    
    public Converter() throws ConverterException {
        this.wurflTree = WURFLTree.getInstance();
        this.wurflInfo = WURFLInfo.getInstance();
        try {
            this.deviceDatabase = PolishFacade.getDeviceDatabase();
        } catch (PolishException exception) {
            throw new ConverterException("Could not get DeviceDatabase.",exception);
        }
        this.deviceManager = this.deviceDatabase.getDeviceManager();
        this.optionInsertMissingDevices = false;
    }
    
    public Stats getStats() {
        return this.stats;
    }


    public boolean isOptionInsertMissingDevices() {
        return this.optionInsertMissingDevices;
    }


    public void setOptionInsertMissingDevices(boolean optionInsertMissingDevices) {
        this.optionInsertMissingDevices = optionInsertMissingDevices;
    }
    
    public void convert(InputStream deviceInputStream, OutputStream deviceOutputStream, InputStream vendorsInputStream, OutputStream vendorsOutputStream) throws ConverterException{
        this.stats = new Stats();
        WurflDevice wurflDevice = new WurflDevice(this.wurflInfo,this.wurflTree);
        
        // Only for the stats. Count all polish device.
        List<de.enough.polish.Device> list = new LinkedList<de.enough.polish.Device>();
        de.enough.polish.Device[] allDevices = this.deviceManager.getDevices();
        for (int i = 0; i < allDevices.length; i++) {
            if( ! allDevices[i].isVirtual()){
                list.add(allDevices[i]);
            }
        }
        this.stats.numberOfPolishDevices(list.size());
        
        // Setup the polish db.
        Document polishDb = getDocumentFromXmlStream(deviceInputStream);
        Document vendorDb = getDocumentFromXmlStream(vendorsInputStream);
        
        // Look at every wurfl device.
        Hashtable<String, Device> devices = this.wurflInfo.getDevices();
        for (Iterator iterator = devices.keySet().iterator(); iterator.hasNext(); ) {
            
            // 1. Get a actual device.
            String key = (String) iterator.next();
            Device device = devices.get(key);
            if( ! device.isActualDevice()) {
                this.stats.nonActualDeviceFound(key);
                continue;
            }
            this.stats.actualDeviceFound(key);
            
            // 2. Get a polish device.
            wurflDevice.setDevice(device);
            de.enough.polish.Device polishDevice = getPolishDeviceForWurflDevice(wurflDevice);
            if(polishDevice == null) {
                this.stats.noPolishDeviceFoundForWurflKey(key);
                
                if(this.optionInsertMissingDevices) {
                    //TODO: This is the place to introduce stub devices with only an identifer and 
                    // a user agent. This functionality should be enabled by an option to 
                    // generate a devices_wurfl.xml with lot of stub devices.
                    addWurflDeviceToPolishDB(polishDb,vendorDb,wurflDevice);
                    
                }
                
                continue;
            }
            
            // 3. Incorporate wurfl device in polish database.
            addWurflUserAgentToPolishDB(polishDb,polishDevice,wurflDevice);
            this.stats.devicesMapped(polishDevice.getIdentifier(),wurflDevice.getWurfleId());
        }
        
        // 4. Write the database to the output stream.
        outputDocument(deviceOutputStream, polishDb);
        outputDocument(vendorsOutputStream, vendorDb);
        
    }

    protected de.enough.polish.Device getPolishDeviceForWurflDevice(WurflDevice wurfleDevice){
        de.enough.polish.Device device = this.deviceManager.getDeviceByFuzzyName(wurfleDevice.getBrand()+"/"+wurfleDevice.getModel());
        return device;
    }
    
    protected void addWurflDeviceToPolishDB(Document db, Document vendorDb, WurflDevice wurflDevice) {
        boolean everythingPresent = true;
        Vendor vendor = this.deviceDatabase.getVendorManager().getVendor(wurflDevice.getBrand());
        String vendorString;
        if(vendor != null) {
            vendorString = vendor.getIdentifier();
        }
        else {
            vendorString = wurflDevice.getBrand();
        }
        
        String[] models = wurflDevice.getModel().split("/");
        if(models.length != 1) {
            this.stats.modelNameContainsSlash(wurflDevice);
            return;
        }
        Element deviceElement = db.createElement("device");
        deviceElement.setAttribute("supportsPolishGui","false");

        Element identifierElement = db.createElement("identifier");
        String identifier = vendorString + "/" + wurflDevice.getModel();
        identifierElement.setTextContent(identifier);
        deviceElement.appendChild(identifierElement);
        
        Element capabilityElement;
        String[] userAgents = wurflDevice.getUserAgents();
        for (int i = 0; i < userAgents.length; i++) {
            capabilityElement = db.createElement("capability");
            capabilityElement.setAttribute("name","wap.userAgent");
            capabilityElement.setAttribute("value",userAgents[i]);
            deviceElement.appendChild(capabilityElement);
        }

        String javaPlatform = wurflDevice.getJavaPlatform();
        if(javaPlatform.length() == 0) {
            this.stats.noJavaPlatformForWurflDevice(wurflDevice);
            everythingPresent = false;
        }
        else {
            capabilityElement = db.createElement("capability");
            capabilityElement.setAttribute("name","JavaPlatform");
            capabilityElement.setAttribute("value",javaPlatform);
            deviceElement.appendChild(capabilityElement);
        }
        
        String javaConfiguration = wurflDevice.getJavaConfiguration();
        if(javaConfiguration.length() == 0) {
            this.stats.noJavaConfigurationForWurflDevice(wurflDevice);
            everythingPresent = false;
        }
        else {
            capabilityElement = db.createElement("capability");
            capabilityElement.setAttribute("name","JavaConfiguration");
            capabilityElement.setAttribute("value",javaConfiguration);
            deviceElement.appendChild(capabilityElement);
        }
        
        if( ! everythingPresent) {
            return;
        }
        
        Node devicesNode = db.getElementsByTagName("devices").item(0);
        devicesNode.appendChild(deviceElement);
        this.stats.wurflDeviceAdded(wurflDevice);
        
        VendorManager vendorManager = this.deviceDatabase.getVendorManager();
        vendor = vendorManager.getVendor(wurflDevice.getBrand());
        if(vendor == null) {
            // Create a new vendor.
            Node vendors = vendorDb.getElementsByTagName("vendors").item(0);
            Element vendorElement = vendorDb.createElement("vendor");

            Element nameElement = vendorDb.createElement("name");
            nameElement.setTextContent(wurflDevice.getBrand());
            vendorElement.appendChild(nameElement);
            
            Element featuresElement = vendorDb.createElement("features");
            vendorElement.appendChild(featuresElement);
            
            vendors.appendChild(vendorElement);
        }
        
        
        
    }

    protected void addWurflUserAgentToPolishDB(Document db, de.enough.polish.Device device, WurflDevice wurflDevice) {
        String identifier = device.getIdentifier();
        String[] userAgents = wurflDevice.getUserAgents();
        int numberOfUserAgents = userAgents.length;
//        String userAgentString = arrayToDelimitedString(userAgents,'\1');
        if(numberOfUserAgents == 0) {
            System.out.println("ERROR:Converter.addWurflDeviceToDB(...):no user agent in wurfl device. This should not happen.wurflId:"+wurflDevice.getWurfleId());
            return;
        }
        
        // Get all identifier tags in the db.
        NodeList identifierNodes = db.getElementsByTagName("identifier");
        int nodesLength = identifierNodes.getLength();
        Element deviceNode;
        Element capabilityElement;
        
        Node identifierNode;
        for (int i = 0; i < nodesLength; i++) {
            identifierNode = identifierNodes.item(i);
            
            // Determine if we found the node with the given identifier.
            if(identifier.equals(identifierNode.getTextContent())){
                
                // Get the device node of this identifer tag.
                deviceNode = (Element)identifierNode.getParentNode();
                
                for(int j = 0; j < numberOfUserAgents; j++) {
                    // Check if the user agent is already present within the capabilities.
                    if(elementContainsUserAgent(deviceNode,userAgents[j])) {
                        continue;
                    }
                    capabilityElement = db.createElement("capability");
                    capabilityElement.setAttribute("name","wap.userAgent");
                    capabilityElement.setAttribute("value",userAgents[j]);
                    deviceNode.appendChild(capabilityElement);
                }
            }
        }
        
        //
    }
    
    private boolean elementContainsUserAgent(Element deviceNode, String userAgent) {
        NodeList capabiliyNodes;
        Element capabilityElement;
        capabiliyNodes = deviceNode.getElementsByTagName("capability");
        int capabiliyNodesLength = capabiliyNodes.getLength();
        for (int j = 0; j < capabiliyNodesLength; j++) {
            capabilityElement = (Element)capabiliyNodes.item(j);
            String name = capabilityElement.getAttribute("name");
            if(name == null) {
                continue;
            }
            String value = capabilityElement.getAttribute("value");
            if(value != null && value.equals(userAgent)) {
                return true;
            }
        }
        return false;
    }


//    private String arrayToDelimitedString(String[] userAgents,char delimiter) {
//        if(userAgents == null) {
//            return "";
//        }
//        StringBuffer stringBuffer = new StringBuffer();
//        int numberOfUserAgents = userAgents.length;
//        for(int i = 0; i < numberOfUserAgents-1;i++) {
//            stringBuffer.append(userAgents[i]);
//            stringBuffer.append(delimiter);
//        }
//        if(numberOfUserAgents >= 1) {
//            stringBuffer.append(userAgents[numberOfUserAgents-1]);
//        }
//        return stringBuffer.toString();
//    }


    
    


    private Document getDocumentFromXmlStream(InputStream xmlStream) throws ConverterException {
        Document dbDocument;
        try {
            DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            dbDocument = newDocumentBuilder.parse(xmlStream);
        } catch (Exception exception) {
            throw new ConverterException("Could not create DOM Document",exception);
        }
        return dbDocument;
    }


    /**
     * @param outputStream
     * @param dbDocument
     * @throws TransformerFactoryConfigurationError
     * @throws ConverterException
     */
    private void outputDocument(OutputStream outputStream, Document dbDocument) throws TransformerFactoryConfigurationError, ConverterException {
        DOMSource domSource = new DOMSource(dbDocument);
        StreamResult streamResult = new StreamResult(outputStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute("indent-number", 4);
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException exception) {
            throw new ConverterException("Could not create transformer.",exception);
        }
        transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
        transformer.setOutputProperty(OutputKeys.METHOD,"xml");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
//        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//        serializer.setOutputProperty(OutputKeys.STANDALONE,"yes");
        try {
          transformer.transform(domSource, streamResult);
        } catch (TransformerException exception) {
            throw new ConverterException("Could not write document to output stream",exception);
        }
    }
    
    public static void main(String[] args) {
        
        final String DEVICE_INPUT_PATH = "../enough-polish-build/devices.xml";
        final String DEVICE_OUTPUT_PATH_TMP = "../enough-polish-build/devices_tmp.xml";
        // For normal operation.
//      final String OUTPUT_DB = "../enough-polish-build/devices.xml";
        // For an extended devices.xml
        final String DEVICE_OUTPUT_PATH = "../enough-polish-build/devices_wurfluseragents.xml";
        
        final String VENDORS_INPUT_PATH = "../enough-polish-build/vendors.xml";
        final String VENDORS_OUTPUT_PATH_TMP = "../enough-polish-build/vendors_tmp.xml";
        final String VENDORS_OUTPUT_PATH = "../enough-polish-build/vendors.xml";
        
        
        
        Converter converter;
        try {
            converter = new Converter();
            converter.setOptionInsertMissingDevices(true);
        } catch (ConverterException exception) {
            System.out.println("Could not create Converter."+exception);
            exception.printStackTrace();
            return;
        }
        InputStream devicesInputStream;
        OutputStream deviceOutputStream;
        
        InputStream vendorsInputStream;
        OutputStream vendorsOutputStream;
        try {
            devicesInputStream = new FileInputStream(DEVICE_INPUT_PATH);
            deviceOutputStream = new FileOutputStream(DEVICE_OUTPUT_PATH_TMP);
            vendorsInputStream = new FileInputStream(VENDORS_INPUT_PATH);
            vendorsOutputStream = new FileOutputStream(VENDORS_OUTPUT_PATH_TMP);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return;
        }
        try {
            converter.convert(devicesInputStream,deviceOutputStream,vendorsInputStream,vendorsOutputStream);
        } catch (ConverterException exception) {
            exception.printStackTrace();
            return;
        }
        FileInputStream deviceInputTemp;
        FileInputStream vendorInputTemp;
        FileOutputStream deviceOutputTemp;
        FileOutputStream vendorOutputTemp;
        try {
            deviceInputTemp = new FileInputStream(DEVICE_OUTPUT_PATH_TMP);
            deviceOutputTemp = new FileOutputStream(DEVICE_OUTPUT_PATH);
            vendorInputTemp = new FileInputStream(VENDORS_OUTPUT_PATH_TMP);
            vendorOutputTemp = new FileOutputStream(VENDORS_OUTPUT_PATH);
        } catch (FileNotFoundException exception) {
            System.out.println("Could not create output file.");
            exception.printStackTrace();
            return;
        }
        try {
            converter.copyFile(deviceInputTemp,deviceOutputTemp);
            converter.copyFile(vendorInputTemp,vendorOutputTemp);
        } catch (IOException exception) {
            System.out.println("Could not copy from temp file.");
            exception.printStackTrace();
        }
        
        converter.getStats().print();
    }
    
    protected void copyFile(FileInputStream in, FileOutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int i = 0;
        while((i=in.read(buf))!=-1) {
          out.write(buf, 0, i);
        }
        in.close();
        out.close();
    }
    
    
    
    
//    /**
//     * No longer needed.
//     * @param stats
//     */
//    private void statsVendors(Stats stats) {
//        Converter converter;
//        try {
//            converter = new Converter();
//        } catch (ConverterException exception) {
//            exception.printStackTrace();
//            return;
//        }
//
//        Vector<String> makes = converter.wurflInfo.getMakes();
//        VendorManager vendorManager = converter.deviceDatabase.getVendorManager();
//        Vendor vendor;
//        for (Iterator iterator = makes.iterator(); iterator.hasNext(); ) {
//            String make = (String) iterator.next();
//            vendor = vendorManager.getVendor(make);
//            if(vendor == null) {
//                stats.missingPolishVendorForWurfleVendor(make);
//                System.out.println("missing polish vendor vor wurfl make:"+make);
//            }
//        }
//        
//    }
}
