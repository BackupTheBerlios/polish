package de.enough.polish.converter;

import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.ossltd.mdevinf.model.Capability;
import com.ossltd.mdevinf.model.Device;
import com.ossltd.mdevinf.model.WURFLInfo;
import com.ossltd.mdevinf.model.WURFLTree;

/**
 * An Adapter to encapulate the wurfl complexity. It is a flyweight object, use setDevice
 * to set its adaptee.
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Apr 4, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class WurflDevice {
    private Device device;
    private WURFLInfo wurflInfo;
    private WURFLTree wurflTree;
    private Hashtable<String, Capability> deviceCapabilities;
    
    public WurflDevice() {
        //
    }
    
    /**
     * This class is intented to be a flyweight so it should change it delegatee
     * with setDevice.
     * @param wurflInfo 
     * @param wurflTree 
     */
    public WurflDevice(WURFLInfo wurflInfo,WURFLTree wurflTree) {
        this.wurflInfo = wurflInfo;
        this.wurflTree = wurflTree;
    }
    public void setDeviceDelegate(Device device) {
        this.device = device;
        Hashtable<String,Capability> capsSoFar = new Hashtable<String,Capability>();
        this.deviceCapabilities = this.wurflInfo.getAllCapabilitiesFor(device,capsSoFar);
        
    }
    public String getBrand() {
        Capability brandCapability = this.deviceCapabilities.get("brand_name");
        if(brandCapability == null) {
            return null;
        }
        String brandName = brandCapability.getValue();
        return brandName;
        
    }
    public String getModel() {
        Capability modelCapability = this.deviceCapabilities.get("model_name");
        if(modelCapability == null) {
            return null;
        }
        String modelName = modelCapability.getValue();
        return modelName;
    }
    
    public String getUserAgent() {
        return getDevice().getUserAgent();
    }
    
    /**
     * 
     * @return all userAgents of this device and all its children. Returns
     * always at least the user agent of the root device.
     */
    public String[] getUserAgents() {
        Device currentDevice = getDevice();
        Device[] childDevices = getChildWurflDevices(currentDevice,true);
        LinkedList<String> list = new LinkedList<String>();
        list.add(this.device.getUserAgent());
        Device childDevice;
        for (int i = 0; i < childDevices.length; i++) {
            childDevice = childDevices[i];
            list.add(childDevice.getUserAgent());
        }
        
        return list.toArray(new String[list.size()]);
    }
    
    public String getJavaPlatform() {
        Capability platform;
        platform = this.deviceCapabilities.get("j2me_midp_2_0");
        if("true".equals(platform.getValue())){
            return "MIDP/2.0";
        }
        platform = this.deviceCapabilities.get("j2me_midp_1_0");
        if("true".equals(platform.getValue())){
            return "MIDP/1.0";
        }
        return "";
    }
    
    public String getJavaConfiguration() {
        Capability configuration;
        configuration = this.deviceCapabilities.get("j2me_cldc_1_1");
        if("true".equals(configuration.getValue())) {
            return "CLDC/1.1";
        }
        configuration = this.deviceCapabilities.get("j2me_cldc_1_0");
        if("true".equals(configuration.getValue())) {
            return "CLDC/1.0";
        }
        return "";
    }
    
    protected Device[] getChildWurflDevices(Device someDevice) {
        return getChildWurflDevices(someDevice,false);
    }
    
    /**
     * TODO: Verify that a user agent is found.
     * @param someDevice
     * @param filterActualDevices true if actual devices and thair children should not appear in the resulting list
     * @return all wurfl devices which are children of the given device.
     */
    protected Device[] getChildWurflDevices(Device someDevice, boolean filterActualDevices) {
        TreePath pathToParentDevice;
        DefaultMutableTreeNode parentNode;
        int parentChildCount;
        LinkedList<Device> resultDevices = new LinkedList<Device>();

        pathToParentDevice = this.wurflTree.getPath(someDevice);
        if(pathToParentDevice == null) {
            return new Device[0];
        }
        parentNode = (DefaultMutableTreeNode) pathToParentDevice.getLastPathComponent();
        parentChildCount = parentNode.getChildCount();

        LinkedList<DefaultMutableTreeNode> childNodes = new LinkedList<DefaultMutableTreeNode>();
        
        Device childDevice;
        for(int i = 0; i < parentChildCount;i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parentNode.getChildAt(i);
            if(filterActualDevices) {
                childDevice = (Device) childNode.getUserObject();
                if(childDevice.isActualDevice()) {
                    continue;
                }
            }
            
            childNodes.add(childNode);
        }
        DefaultMutableTreeNode currentNode;
        
        while(childNodes.size() > 0) {
            currentNode = childNodes.remove();
        
            // Add all children.
            int childCount = currentNode.getChildCount();
            if(childCount > 0) {
                for(int i = 0; i < childCount;i++) {
                    DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode)currentNode.getChildAt(i);
                    if(filterActualDevices) {
                        childDevice = (Device) childTreeNode.getUserObject();
                        if(childDevice.isActualDevice()) {
                            continue;
                        }
                    }
                    childNodes.addLast(childTreeNode);
                }
            }
            
            // Get the device.
            childDevice = (Device)currentNode.getUserObject();
            if(filterActualDevices) {
                if(childDevice.isActualDevice()) {
                    continue;
                }
            }
            resultDevices.add(childDevice);
        }
        
        return resultDevices.toArray(new Device[resultDevices.size()]);
    }

    public String getWurfleId() {
        return getDevice().getDeviceID();
    }
    
    public Device getDevice() {
        if(this.device == null) {
            throw new RuntimeException("No device set. Call setDevice before any actions as this class is a flyweight.");
        }
        return this.device;
    }
    
    public String toString() {
        if(this.device == null) {
            return "Some Wurfl Device";
        }
        return "Wurfl Device:"+this.getBrand()+"/"+this.getModel();
    }
}