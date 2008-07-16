/*
 * Created on Feb 8, 2006 at 5:02:52 PM.
 * 
 * Copyright (c) 2006 Enough Software
 */
package de.enough.mepose.core.model;


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

//    private static DeviceDatabase deviceDatabase;
//    
//    private static DeviceDatabase init(File polishHome,File projectHome,List targetDevices) throws DeviceDatabaseException{
//        Map properties = new HashMap();
//        properties.put("polish.home",polishHome.getAbsolutePath());
//        
//        if(targetDevices != null) {
//            properties.put("polish.devicedatabase.identifiers",targetDevices);
//        }
//        DeviceDatabase db = null;
//        try {
//            db = DeviceDatabase.getInstance(properties,polishHome,projectHome,null,null,null,new HashMap());
//        } catch (Exception e) {
//            throw new DeviceDatabaseException("Could not create deviceDatabase.(1)",e);
//        }
//        if(db == null || db.getPolishHome() == null) {
//            throw new DeviceDatabaseException("Could not create deviceDatabase.(2)");
//        }
//        return db;
//    }
//    
//    public static DeviceDatabase getDeviceDatabase(File polishHome,File projectHome,List targetDevices) throws DeviceDatabaseException{
//        if(deviceDatabase == null) {
//            deviceDatabase = init(polishHome,projectHome,targetDevices);
//        }
//        return deviceDatabase;
//    }
//    
//    public static DeviceDatabase getNewDeviceDatabase(File polishHome,File projectHome,List targetDevices) throws DeviceDatabaseException{
//        Map properties = new HashMap();
//        properties.put("polish.home",polishHome.getAbsolutePath());
//        
//        if(targetDevices != null) {
//            properties.put("polish.devicedatabase.identifiers",targetDevices);
//        }
//        try {
//            return DeviceDatabase.getInstance(properties,polishHome,projectHome,null,null,null,new HashMap());
//        }
//        catch (Exception e) {
//            throw new DeviceDatabaseException("Could not create deviceDatabase.",e);
//        }
//    }
}
