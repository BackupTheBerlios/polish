/*
 * Created on Feb 8, 2006 at 5:02:52 PM.
 * 
 * Copyright (c) 2006 Enough Software
 */
package de.enough.mepose.core.model;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.enough.polish.devices.DeviceDatabase;

/**
 * 
 * <br>Copyright Enough Software 2006
 * <pre>
 * history
 *        Feb 8, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, richard.nkrumah@enough.de
 */
public class PolishDeviceDatabase {

    private static DeviceDatabase deviceDatabase;
    
    private static void init(File polishHome,File projectHome) throws DeviceDatabaseException{
        Map properties = new HashMap();
        properties.put("mpp.home","no/path");
        properties.put("polish.home",polishHome.getAbsolutePath());
        
//        Map inputStreamsByfileName = new HashMap();
        
        // This is deprecated !! Do not use the things within the plugin but the polish home things !!
//        inputStreamsByfileName.put("capabilities.xml",PolishDeviceDatabase.class.getResourceAsStream("/capabilities.xml"));
//        inputStreamsByfileName.put("apis.xml",PolishDeviceDatabase.class.getResourceAsStream("/apis.xml"));
//        inputStreamsByfileName.put("configurations.xml",PolishDeviceDatabase.class.getResourceAsStream("/configurations.xml"));
//        inputStreamsByfileName.put("platforms.xml",PolishDeviceDatabase.class.getResourceAsStream("/platforms.xml"));
//        inputStreamsByfileName.put("groups.xml",PolishDeviceDatabase.class.getResourceAsStream("/groups.xml"));
//        inputStreamsByfileName.put("vendors.xml",PolishDeviceDatabase.class.getResourceAsStream("/vendors.xml"));
//        inputStreamsByfileName.put("devices.xml",PolishDeviceDatabase.class.getResourceAsStream("/devices.xml"));

        try {
//            deviceDatabase = DeviceDatabase.getInstance(properties,polishHome,projectHome,null,null,inputStreamsByfileName,new HashMap());
//            deviceDatabase = DeviceDatabase.getInstance(properties,polishHome,projectHome,null,null,null,new HashMap());
            deviceDatabase = DeviceDatabase.getInstance(polishHome);
        } catch (Exception e) {
            throw new DeviceDatabaseException("Could not create deviceDatabase.",e);
        }
        if(deviceDatabase == null || deviceDatabase.getPolishHome() == null) {
            throw new DeviceDatabaseException("Could not create deviceDatabase.");
        }
    }
    
    public static DeviceDatabase getDeviceDatabase(File polishHome,File projectHome) throws DeviceDatabaseException{
        if(deviceDatabase == null) {
            init(polishHome,projectHome);
        }
        return deviceDatabase;
    }
}
