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
package de.enough.polish.converter;

import java.io.FileInputStream;
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

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.WURFLInfo;
import com.ossltd.mdevinf.model.WURFLTree;

import de.enough.polish.converter.modelnameextractor.BlackberryModelNameExtractor;
import de.enough.polish.converter.modelnameextractor.MotorolaModelNameExtractor;
import de.enough.polish.converter.modelnameextractor.NokiaModelNameExtractor;
import de.enough.polish.devices.CapabilityManager;
import de.enough.polish.devices.ConfigurationManager;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceGroupManager;
import de.enough.polish.devices.DeviceManager;
import de.enough.polish.devices.LibraryManager;
import de.enough.polish.devices.PlatformManager;
import de.enough.polish.devices.Vendor;
import de.enough.polish.devices.VendorManager;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.XmlUtils;

/**
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
    private VendorManager vendorManager;
    private PlatformManager platformManager;
    private LibraryManager libraryManager;
    private DeviceGroupManager groupManager;
    private CapabilityManager capabilityManager;

    private boolean optionInsertMissingDevices = true;
    private Stats stats;
    private Document polishDb;
    private Document vendorDb;
    private Map<String,Element> polishDbIdentifierNodeByName = new HashMap<String,Element>();
    private Map<String,ModelNameExtractor> modelNameExtractors = new HashMap<String,ModelNameExtractor>();
    private ConfigurationManager configurationManager;
    private boolean firstRun = true;

    
    public Converter() throws ConverterException {
        System.out.println("Initializing WURFLTree");
        this.wurflTree = WURFLTree.getInstance();
        System.out.println("Initializing WURFLInfo");
        this.wurflInfo = WURFLInfo.getInstance();
        DeviceDatabase deviceDatabase;
        try {
            System.out.println("Initializing DeviceDatabase");
            deviceDatabase = PolishFacade.getDeviceDatabase();
        } catch (Exception exception) {
            throw new ConverterException("Could not get DeviceDatabase.",exception);
        }
        this.deviceManager = deviceDatabase.getDeviceManager();
        this.vendorManager = deviceDatabase.getVendorManager();
        this.libraryManager = deviceDatabase.getLibraryManager();
        this.groupManager = deviceDatabase.getGroupManager();
        this.capabilityManager = deviceDatabase.getCapabilityManager();
        this.configurationManager = deviceDatabase.getConfigurationManager();
        this.platformManager = deviceDatabase.getPlatformManager();
        
        List<ModelNameExtractor> modelNameExtractor = new LinkedList<ModelNameExtractor>();
        
        modelNameExtractor.add(new BlackberryModelNameExtractor());
        modelNameExtractor.add(new NokiaModelNameExtractor());
        modelNameExtractor.add(new MotorolaModelNameExtractor());

        for (ModelNameExtractor matcher : modelNameExtractor) {
            this.modelNameExtractors.put(matcher.getVendor(),matcher);
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
        System.out.println("Initializing devices.xml");
        initPolishDb(deviceInputStream);
        System.out.println("Initializing vendors.xml");
        initVendorDb(vendorsInputStream);
        
        // Look at every wurfl device.
        Hashtable<String, Device> wurflDevices = this.wurflInfo.getDevices();
        WurflDevice wurflDevice = new WurflDevice(this.wurflInfo,this.wurflTree);
        for (Iterator wurflDeviceIterator = wurflDevices.keySet().iterator(); wurflDeviceIterator.hasNext(); ) {
            // 1. Get an actual device.
            String key = (String) wurflDeviceIterator.next();
            Device device = wurflDevices.get(key);
            if( ! device.isActualDevice()) {
                this.stats.nonActualDeviceFound(key);
                continue;
            }
            this.stats.actualDeviceFound(key);

            Hashtable<String, Capability> productInfos = device.getCapabilitiesForGroup("product_info");
            Capability modelName = productInfos.get("model_name");
			if(modelName == null || modelName.getValue() == null || modelName.getValue().length() == 0) {
				this.stats.emptyModelNameFound(key);
				continue;
			}
            
            // 2. Get a polish device.
            wurflDevice.setDeviceDelegate(device);
            de.enough.polish.Device polishDevice = getPolishDeviceForWurflDevice(wurflDevice);
            if(polishDevice == null) {
                this.stats.noPolishDeviceFoundForWurflKey(key);
                
                if(!this.optionInsertMissingDevices) {
                    continue;
                }
                polishDevice = addWurflDeviceToPolishDB(wurflDevice);
                if(polishDevice ==  null) {
                    // We could not add the wurfl device to polish. So skip the device.
                    continue;
                }
            }
            
            // 3. Incorporate wurfl device capabilities in polish database.
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
    @SuppressWarnings("unchecked")
    private void initPolishDb(InputStream deviceInputStream) throws ConverterException {
        this.polishDb = XmlUtils.getDocumentFromXmlStream(deviceInputStream);
        
        Iterator<Element> identifierElements = this.polishDb.getDescendants(new ElementFilter("identifier"));
        
        for (; identifierElements.hasNext(); ) {
            Element identifierElement = identifierElements.next();
            String textContent = identifierElement.getText();
            String[] identifiers = textContent.split(",");
            for (String identifier : identifiers) {
                String cleanIdentifier = identifier.trim();
                this.polishDbIdentifierNodeByName.put(cleanIdentifier,identifierElement);
            }
        }
    }

    protected de.enough.polish.Device getPolishDeviceForWurflDevice(WurflDevice wurfleDevice){
        de.enough.polish.Device device = getDeviceByFuzzyName(wurfleDevice.getBrand()+"/"+wurfleDevice.getModel());
        return device;
    }
    
    protected de.enough.polish.Device addWurflDeviceToPolishDB(WurflDevice wurflDevice) {
        // Check if any information is missing in the wurflDevice.
        
        if(this.firstRun ) {
            this.polishDb.getRootElement().addContent(new Comment("The following devices are automatically added from WURFL"));
            this.firstRun = false;
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
            return null;
        }
        
        // Checking the vendor.
        String wurflBrand = wurflDevice.getBrand();
        Vendor polishVendorObject = this.vendorManager.getVendor(wurflBrand);
        String vendor;
        
        if(polishVendorObject == null) {
//          Create a new vendor in vendors.xml.
            Element vendors = this.vendorDb.getRootElement();
            Element vendorElement = new Element("vendor");
            vendors.addContent(vendorElement);
            
            Element nameElement = new Element("name");
            nameElement.setText(wurflBrand);
            vendorElement.addContent(nameElement);
            
            Element featuresElement = new Element("features");
            vendorElement.addContent(featuresElement);
            polishVendorObject = createVendor(vendorElement);
            this.vendorManager.addVendor(polishVendorObject);
        }
        vendor = polishVendorObject.getIdentifier();
        
        String wurflModel = wurflDevice.getModel();
        int indexOfSlashInModelName = wurflModel.split("/").length;
        if(indexOfSlashInModelName != 1) {
            this.stats.modelNameContainsSlash(wurflDevice);
            wurflModel = wurflModel.replaceAll("/","_");
        }
        int indexOfCommaInModelName = wurflModel.indexOf(",");
        if(indexOfCommaInModelName != -1) {
            this.stats.modelNameContainsComma(wurflDevice);
            wurflModel = wurflModel.replaceAll(",","_");
        }
        String finalModelName = wurflModel;
        
        ModelNameExtractor modelNameExtractor = this.modelNameExtractors.get(vendor);
        List<String> extractedModelNames = null;
        if(modelNameExtractor != null) {
            extractedModelNames = modelNameExtractor.matchModel(wurflModel);
        }
        if(extractedModelNames != null && extractedModelNames.size() > 0) {
            finalModelName = extractedModelNames.get(0);
        }
        String identifier = vendor + "/" + finalModelName;

        // Add the wurfl device to devices.xml.
        Element devicesElement = this.polishDb.getRootElement();
        Element deviceElement = new Element("device");
        devicesElement.addContent(deviceElement);
        
        Element identifierElement = new Element("identifier");
        identifierElement.setText(identifier);
        deviceElement.addContent(identifierElement);
        
        Element capabilityElement;
        String[] userAgents = wurflDevice.getUserAgents();
        for (int i = 0; i < userAgents.length; i++) {
            capabilityElement = new Element("capability");
            capabilityElement.setAttribute("name","wap.userAgent");
            capabilityElement.setAttribute("value",userAgents[i]);
            deviceElement.addContent(capabilityElement);
        }

        capabilityElement = new Element("capability");
        capabilityElement.setAttribute("name","JavaPlatform");
        capabilityElement.setAttribute("value",javaPlatform);
        deviceElement.addContent(capabilityElement);
    
        capabilityElement = new Element("capability");
        capabilityElement.setAttribute("name","JavaConfiguration");
        capabilityElement.setAttribute("value",javaConfiguration);
        deviceElement.addContent(capabilityElement);
        
        this.stats.wurflDeviceAdded(wurflDevice);
        // Update the cache.
        this.polishDbIdentifierNodeByName.put(identifier,identifierElement);
        
        // Update the internal database.
        de.enough.polish.Device device;
        device = createPolishDevice(polishVendorObject, identifier, deviceElement);
        this.deviceManager.addDevice(device);
        return device;
    }

    /**
     * @param polishVendorObject
     * @param identifier
     * @param deviceElement
     * @return
     */
    private de.enough.polish.Device createPolishDevice(Vendor polishVendorObject, String identifier, Element deviceElement) {
        String name = identifier.split("/")[1];
        de.enough.polish.Device polishDevice;
        try {
            polishDevice = new de.enough.polish.Device(this.configurationManager,this.platformManager,deviceElement,identifier,name ,polishVendorObject,this.groupManager,this.libraryManager,this.deviceManager,this.capabilityManager);
        } catch (InvalidComponentException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return polishDevice;
    }

    /**
     * @param vendorElement
     * @return
     */
    private Vendor createVendor(Element vendorElement){
        Vendor vendor;
        try {
            vendor = new Vendor(null, vendorElement, this.capabilityManager, this.groupManager);
        } catch (InvalidComponentException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return vendor;
    }

    protected void addUserAgentsToPolishDB(de.enough.polish.Device device, String[] userAgents) {
        if(device == null) {
            return;
        }
        String identifier = device.getIdentifier();
        int numberOfUserAgents = userAgents.length;
        
        
        Element identifierElement = this.polishDbIdentifierNodeByName.get(identifier);
        
        if(identifierElement == null){
            throw new RuntimeException("ERROR: There should be a device element in devices.xml for device:"+identifier);
        }
                
        Element deviceNode;
        // Get the device node of this identifer tag.
        deviceNode = identifierElement.getParentElement();
        
        Element capabilityElement;
        for(int j = 0; j < numberOfUserAgents; j++) {
            // Check if the user agent is already present within the capabilities.
            if(elementContainsUserAgent(deviceNode,userAgents[j])) {
                continue;
            }
            capabilityElement = new Element("capability");
            capabilityElement.setAttribute("name","wap.userAgent");
            capabilityElement.setAttribute("value",userAgents[j]);
            deviceNode.addContent(capabilityElement);
        }
    }
    
    @SuppressWarnings("unchecked")
    private boolean elementContainsUserAgent(Element deviceNode, String userAgent) {
        List<Element> capabiliyNodes;
        capabiliyNodes = deviceNode.getContent(new ElementFilter("capability"));
        for (Element capabilityElement : capabiliyNodes) {
            String name = capabilityElement.getAttributeValue("name");
            if(name == null) {
                throw new RuntimeException("A capability without a name attribute found in device '"+deviceNode+"'");
            }
            String value = capabilityElement.getAttributeValue("value");
            if(value != null && value.equals(userAgent)) {
                return true;
            }
        }
        return false;
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
        ModelNameExtractor modelNameExtractor = this.modelNameExtractors.get(identifier);
        if(modelNameExtractor != null) {
            models = modelNameExtractor.matchModel(modelString);
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
    
}
