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
import java.util.Vector;

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
 * 
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
    
    public Converter() throws ConverterException {
        this.wurflTree = WURFLTree.getInstance();
        this.wurflInfo = WURFLInfo.getInstance();
        try {
            this.deviceDatabase = PolishFacade.getDeviceDatabase();
        } catch (PolishException exception) {
            throw new ConverterException("Could not get DeviceDatabase.",exception);
        }
        this.deviceManager = this.deviceDatabase.getDeviceManager();
    }
    
    
    protected de.enough.polish.Device getPolishDeviceForWurflDevice(WurflDevice wurfleDevice){
        de.enough.polish.Device device = this.deviceManager.getDeviceByFuzzyName(wurfleDevice.getBrand()+"/"+wurfleDevice.getModel());
        return device;
    }
    
    protected void addWurflDeviceToDB(Document db, de.enough.polish.Device device, WurflDevice wurflDevice) {
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


    public void convert(InputStream polishDatabase, OutputStream outputStream) throws ConverterException{
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
        Document dbDocument = getDocument(polishDatabase);
        
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
                //TODO: This is the place to introduce stub devices with only an identifer and 
                // a user agent. This functionality should be enabled by an option to 
                // generate a devices_wurfl.xml with lot of stub devices.
                continue;
            }
            
            // 3. Incorporate wurfl device in polish database.
            addWurflDeviceToDB(dbDocument,polishDevice,wurflDevice);
            this.stats.devicesMapped(polishDevice.getIdentifier(),wurflDevice.getWurfleId());
        }
        
        // 4. Write the database to the output stream.
        outputDocument(outputStream, dbDocument);
        
    }


    /**
     * @param polishDatabase
     * @return
     * @throws ConverterException
     */
    private Document getDocument(InputStream polishDatabase) throws ConverterException {
        Document dbDocument;
        try {
            DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            dbDocument = newDocumentBuilder.parse(polishDatabase);
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
        Transformer serializer;
        try {
            serializer = tf.newTransformer();
        } catch (TransformerConfigurationException exception) {
            throw new ConverterException("Could not create transformer.",exception);
        }
        serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
        serializer.setOutputProperty(OutputKeys.METHOD,"xml");
        serializer.setOutputProperty(OutputKeys.INDENT,"yes");
        serializer.setOutputProperty(OutputKeys.STANDALONE,"yes");
        try {
            serializer.transform(domSource, streamResult);
        } catch (TransformerException exception) {
            throw new ConverterException("Could not write document to output stream",exception);
        }
    }
    
    public static void main(String[] args) {
        
        final String INPUT_DB = "../enough-polish-build/devices.xml";
        final String OUTPUT_DB_TMP = "../enough-polish-build/devices_tmp.xml";
        final String OUTPUT_DB = "../enough-polish-build/devices.xml";
        
        Converter converter;
        try {
            converter = new Converter();
        } catch (ConverterException exception) {
            System.out.println("Could not create Converter."+exception);
            exception.printStackTrace();
            return;
        }
        InputStream polishInputStream;
        OutputStream outputStream;
        try {
            polishInputStream = new FileInputStream(INPUT_DB);
            outputStream = new FileOutputStream(OUTPUT_DB_TMP);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return;
        }
        try {
            converter.convert(polishInputStream,outputStream);
        } catch (ConverterException exception) {
            exception.printStackTrace();
            return;
        }
        FileInputStream input;
        FileOutputStream output;
        try {
            input = new FileInputStream(OUTPUT_DB_TMP);
            output = new FileOutputStream(OUTPUT_DB);
        } catch (FileNotFoundException exception) {
            System.out.println("Could not create output file.");
            exception.printStackTrace();
            return;
        }
        try {
            converter.copyFile(input,output);
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
    
    public Stats getStats() {
        return this.stats;
    }
    
    /**
     * No longer needed.
     * @param stats
     */
    private void statsVendors(Stats stats) {
        Converter converter;
        try {
            converter = new Converter();
        } catch (ConverterException exception) {
            exception.printStackTrace();
            return;
        }

        Vector<String> makes = converter.wurflInfo.getMakes();
        VendorManager vendorManager = converter.deviceDatabase.getVendorManager();
        Vendor vendor;
        for (Iterator iterator = makes.iterator(); iterator.hasNext(); ) {
            String make = (String) iterator.next();
            vendor = vendorManager.getVendor(make);
            if(vendor == null) {
                stats.missingPolishVendorForWurfleVendor(make);
                System.out.println("missing polish vendor vor wurfl make:"+make);
            }
        }
        
    }
}
