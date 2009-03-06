package de.enough.polish.converter;


import java.io.File;

import de.enough.polish.devices.DeviceDatabase;

/**
 * TODO: This class need not be a singleton.
 * <br>Copyright Enough Software 2006
 * <pre>
 * history
 *        Feb 8, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, richard.nkrumah@enough.de
 */
public class PolishFacade {

    private static DeviceDatabase deviceDatabase;
    private static String polishHomePath = "../enough-polish-build";    
    /**
     * 
     * @return the device database.
     */
    public static DeviceDatabase getDeviceDatabase(){
        if(deviceDatabase == null) {
            initDatabase();
        }
        return deviceDatabase;
    }
    
//    public static DeviceDatabase getDeviceDatabase(File devicesXml, File vendorsXml) {
//        return null;
//    }
    
    private static void initDatabase() {
        try {
            deviceDatabase = new DeviceDatabase(new File(polishHomePath));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create deviceDatabase.",e);
        }
    }
}
