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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import de.enough.polish.util.XmlUtils;

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

    public static interface ModelMatcher{
        public List<String> matchModel(String modelNameToMatch);
        public String getVendor();
    }
    
    public static class BlackberryModelMatcher implements ModelMatcher{
        boolean first = true;
        public List<String> matchModel(String modelNameToMatch) {
            this.first = false;
            String model = modelNameToMatch;
            model = model.replaceAll("BlackBerry","");
            model = model.replaceAll("\\(Pearl\\)","");
            model = model.trim();
            List<String> models = new LinkedList<String>();
            models.add(model);
            return models;
        }
        public String getVendor(){
            return "BlackBerry";
        }
    }
    
    public static class NokiaModelMatcher implements ModelMatcher{
        public String getVendor() {
            return "Nokia";
        }

        public List<String> matchModel(String modelNameToMatch) {
            String model = modelNameToMatch;
            model = model.replace(" NFC","_NFC");
            model = model.trim();
            List<String> models = new LinkedList<String>();
            models.add(model);
            return models;
        }
        
    }
    
    private WURFLTree wurflTree;
    private WURFLInfo wurflInfo;
    private DeviceManager deviceManager;
    private VendorManager vendorManager;
    private Stats stats;
    private boolean optionInsertMissingDevices = false;
    private Document polishDb;
    private Map<String,Node> polishDbIdentifierNodeByName = new HashMap<String,Node>();
    private Document vendorDb;
    private Map<String,ModelMatcher> modelMatchers = new HashMap<String,ModelMatcher>();
    
    public Converter() throws ConverterException {
        this.wurflTree = WURFLTree.getInstance();
        this.wurflInfo = WURFLInfo.getInstance();
        DeviceDatabase deviceDatabase;
        try {
            deviceDatabase = PolishFacade.getDeviceDatabase();
        } catch (Exception exception) {
            throw new ConverterException("Could not get DeviceDatabase.",exception);
        }
        this.deviceManager = deviceDatabase.getDeviceManager();
        this.vendorManager = deviceDatabase.getVendorManager();
        
        List<ModelMatcher> modelMatcher = new LinkedList<ModelMatcher>();
        
        modelMatcher.add(new BlackberryModelMatcher());
        modelMatcher.add(new NokiaModelMatcher());

        for (ModelMatcher matcher : modelMatcher) {
            this.modelMatchers.put(matcher.getVendor(),matcher);
        }
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
        
        // Only for the stats. Count all polish device.
        int numberOfPolishDevices = 0;
        de.enough.polish.Device[] allDevices = this.deviceManager.getDevices();
        for (int i = 0; i < allDevices.length; i++) {
            if( ! allDevices[i].isVirtual()){
                numberOfPolishDevices++;
            }
        }
        this.stats.numberOfPolishDevices(numberOfPolishDevices);
        
        // Setup the polish db.
        initPolishDb(deviceInputStream);
        
        initVendorDb(vendorsInputStream);
        
        // Look at every wurfl device.
        Hashtable<String, Device> devices = this.wurflInfo.getDevices();
        for (Iterator iterator = devices.keySet().iterator(); iterator.hasNext(); ) {
            
            // 1. Get an actual device.
            String key = (String) iterator.next();
            Device device = devices.get(key);
            if( ! device.isActualDevice()) {
                this.stats.nonActualDeviceFound(key);
                continue;
            }
            this.stats.actualDeviceFound(key);
            
            // 2. Get a polish device.
            WurflDevice wurflDevice = new WurflDevice(this.wurflInfo,this.wurflTree);
            wurflDevice.setDevice(device);
            de.enough.polish.Device polishDevice = getPolishDeviceForWurflDevice(wurflDevice);
            if(polishDevice == null) {
                this.stats.noPolishDeviceFoundForWurflKey(key);
                
                if(this.optionInsertMissingDevices) {
                    //TODO: This is the place to introduce stub devices with only an identifer and 
                    // a user agent. This functionality should be enabled by an option to 
                    // generate a devices_wurfl.xml with lot of stub devices.
                    addWurflDeviceToPolishDB(wurflDevice);
                    
                }
                continue;
            }
            
            // 3. Incorporate wurfl device in polish database.
            String[] userAgents = wurflDevice.getUserAgents();
            addUserAgentsToPolishDB(polishDevice,userAgents);
            this.stats.devicesMapped(polishDevice.getIdentifier(),wurflDevice.getWurfleId());
        }
        
        // 4. Write the database to the output stream.
        XmlUtils.outputDocument(deviceOutputStream, this.polishDb);
        XmlUtils.outputDocument(vendorsOutputStream, this.vendorDb);
        
    }

    /**
     * @param vendorsInputStream
     * @throws ConverterException
     */
    private void initVendorDb(InputStream vendorsInputStream) throws ConverterException {
        this.vendorDb = XmlUtils.getDocumentFromXmlStream(vendorsInputStream);
    }

    /**
     * @param deviceInputStream
     * @throws ConverterException
     */
    private void initPolishDb(InputStream deviceInputStream) throws ConverterException {
        this.polishDb = XmlUtils.getDocumentFromXmlStream(deviceInputStream);
        
        NodeList identifierNodes = this.polishDb.getElementsByTagName("identifier");
        int nodesLength = identifierNodes.getLength();
        Node identifierNode;
        for (int i = 0; i < nodesLength; i++) {
            identifierNode = identifierNodes.item(i);
            String textContent = identifierNode.getTextContent();
            String[] identifiers = textContent.split(",");
            for (String identifier : identifiers) {
                String cleanIdentifier = identifier.trim();
                this.polishDbIdentifierNodeByName.put(cleanIdentifier,identifierNode);
            }
        }
    }

    protected de.enough.polish.Device getPolishDeviceForWurflDevice(WurflDevice wurfleDevice){
        de.enough.polish.Device device = getDeviceByFuzzyName(wurfleDevice.getBrand()+"/"+wurfleDevice.getModel());
        return device;
    }
    
    protected void addWurflDeviceToPolishDB(WurflDevice wurflDevice) {
        String wurflBrand = wurflDevice.getBrand();
        Vendor vendor = this.vendorManager.getVendor(wurflBrand);
        String vendorString;
        if(vendor != null) {
            vendorString = vendor.getIdentifier();
        }
        else {
            vendorString = wurflBrand;
        }
        
        String model = wurflDevice.getModel();
        String[] models = model.split("/");
        if(models.length != 1) {
            this.stats.modelNameContainsSlash(wurflDevice);
            return;
        }
        if(model.indexOf(",") != -1) {
            this.stats.modelNameContainsComma(wurflDevice);
            return;
        }
        String identifier = vendorString + "/" + model;

        // Check if we already have added the device. Sometimes the wurfl stuff is redundant.
        Node identifierElement = this.polishDbIdentifierNodeByName.get(identifier);
        if(identifierElement != null) {
            return;
        }
        
        Element deviceElement = this.polishDb.createElement("device");
        deviceElement.setAttribute("supportsPolishGui","false");

        identifierElement = this.polishDb.createElement("identifier");
        // Update the cache hashmap.
        this.polishDbIdentifierNodeByName.put(identifier,identifierElement);
        
        identifierElement.setTextContent(identifier);
        deviceElement.appendChild(identifierElement);
        
        Element capabilityElement;
        String[] userAgents = wurflDevice.getUserAgents();
        for (int i = 0; i < userAgents.length; i++) {
            capabilityElement = this.polishDb.createElement("capability");
            capabilityElement.setAttribute("name","wap.userAgent");
            capabilityElement.setAttribute("value",userAgents[i]);
            deviceElement.appendChild(capabilityElement);
        }

        String javaPlatform = wurflDevice.getJavaPlatform();
        boolean somethingMissing = false;
        if(javaPlatform == null || javaPlatform.length() == 0) {
            this.stats.noJavaPlatformForWurflDevice(wurflDevice);
            somethingMissing = true;
        }
        String javaConfiguration = wurflDevice.getJavaConfiguration();
        if(javaConfiguration == null || javaConfiguration.length() == 0) {
            this.stats.noJavaConfigurationForWurflDevice(wurflDevice);
            somethingMissing = true;
        }
        if(somethingMissing) {
            return;
        }

        capabilityElement = this.polishDb.createElement("capability");
        capabilityElement.setAttribute("name","JavaPlatform");
        capabilityElement.setAttribute("value",javaPlatform);
        deviceElement.appendChild(capabilityElement);
    
        capabilityElement = this.polishDb.createElement("capability");
        capabilityElement.setAttribute("name","JavaConfiguration");
        capabilityElement.setAttribute("value",javaConfiguration);
        deviceElement.appendChild(capabilityElement);
        
        //TODO: Make this lookup a hashmap lookup.
        Node devicesNode = this.polishDb.getElementsByTagName("devices").item(0);
        devicesNode.appendChild(deviceElement);
        this.stats.wurflDeviceAdded(wurflDevice);
        
        vendor = this.vendorManager.getVendor(wurflBrand);
        if(vendor == null) {
            // Create a new vendor.
            Node vendors = this.vendorDb.getElementsByTagName("vendors").item(0);
            Element vendorElement = this.vendorDb.createElement("vendor");

            Element nameElement = this.vendorDb.createElement("name");
            nameElement.setTextContent(wurflBrand);
            vendorElement.appendChild(nameElement);
            
            Element featuresElement = this.vendorDb.createElement("features");
            vendorElement.appendChild(featuresElement);
            
            vendors.appendChild(vendorElement);
        }
        
    }

    protected void addUserAgentsToPolishDB(de.enough.polish.Device device, String[] userAgents) {
        String identifier = device.getIdentifier();
        int numberOfUserAgents = userAgents.length;
        
        
        Node identifierNode = this.polishDbIdentifierNodeByName.get(identifier);
        
        if(identifierNode == null){
            System.out.println("ERROR: There should be a polish device in the database for device:"+identifier);
            return;
        }
                
        Element deviceNode;
        // Get the device node of this identifer tag.
        deviceNode = (Element)identifierNode.getParentNode();
        
        Element capabilityElement;
        for(int j = 0; j < numberOfUserAgents; j++) {
            // Check if the user agent is already present within the capabilities.
            if(elementContainsUserAgent(deviceNode,userAgents[j])) {
                continue;
            }
            capabilityElement = this.polishDb.createElement("capability");
            capabilityElement.setAttribute("name","wap.userAgent");
            capabilityElement.setAttribute("value",userAgents[j]);
            deviceNode.appendChild(capabilityElement);
        }
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


    /**
     * @param args 
     * @param outputStream
     * @param dbDocument
     * @throws ConverterException
     */
    
    
    public static void main(String[] args) {
        
        final String DEVICE_INPUT_PATH = "../enough-polish-build/devices.xml";
        final String DEVICE_OUTPUT_PATH = "../enough-polish-build/devices_wurfl.xml";
        final String VENDORS_INPUT_PATH = "../enough-polish-build/vendors.xml";
        final String VENDORS_OUTPUT_PATH = "../enough-polish-build/vendors_wurfl.xml";
        
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
        File deviceTempFile;
        File vendorTempFile;
        try {
            devicesInputStream = new FileInputStream(DEVICE_INPUT_PATH);
            deviceTempFile = File.createTempFile("j2mepolish","device");
            deviceOutputStream = new FileOutputStream(deviceTempFile);
            vendorsInputStream = new FileInputStream(VENDORS_INPUT_PATH);
            vendorTempFile = File.createTempFile("j2mepolish","vendor");
            vendorsOutputStream = new FileOutputStream(vendorTempFile);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return;
        } catch (IOException exception) {
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
            deviceInputTemp = new FileInputStream(deviceTempFile);
            deviceOutputTemp = new FileOutputStream(DEVICE_OUTPUT_PATH);
            vendorInputTemp = new FileInputStream(vendorTempFile);
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
    
    // TODO: Put this functionality back into polish as it may be nice to have.
    protected de.enough.polish.Device getDeviceByFuzzyName(String fuzzyName) {
        // Try the fuzzy name as normal identifier.
        de.enough.polish.Device device = this.deviceManager.getDevice(fuzzyName);
        if(device != null) {
            return device;
        }
        
        String vendorString;
        String modelString;
        
        String[] components = fuzzyName.split("/");
        if(components.length != 2) {
            return null;
        }
        vendorString = components[0];
        modelString = components[1];
        
        Vendor vendor = this.vendorManager.getVendor(vendorString);
        if(vendor == null) {
            return null;
        }
        String identifier = vendor.getIdentifier();
        List<String> models;
        ModelMatcher modelMatcher = this.modelMatchers.get(identifier);
        if(modelMatcher != null) {
            models = modelMatcher.matchModel(modelString);
        } else {
            models = new LinkedList<String>();
            models.add(modelString);
        }
        for (String model : models) {
            device = this.deviceManager.getDevice(identifier+"/"+model);
            if(device != null) {
                break;
            }
        }
        
        return device;
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
