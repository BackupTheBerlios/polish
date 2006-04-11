package de.enough.polish;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.enough.polish.Device;
import de.enough.polish.ant.requirements.Requirements;
import de.enough.polish.ant.requirements.VariableDefinedRequirement;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceManager;
import de.enough.polish.util.StringUtil;

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
    private static final String CAPABILITY_WAP_USER_AGENT = "wap.userAgent";
    private static boolean isInitialized = false;
    private static HashMap<String,Device> deviceByUserAgent;
    private static String PATH_POLISH_XML = "../enough-polish-build";
    
    private static void init() throws PolishException{
        initDatabase();
        initUserAgentMap();
        isInitialized = true;
    }

    /**
     * Searches for a device with the given user agent. It also tries to match all
     * prefixes of the user agent.
     * @param userAgent
     * @return the device with the given user agent or null if no device is found.
     * @throws PolishException
     */
    public static Device getDeviceByUserAgent(String userAgent) throws PolishException {
        if( ! isInitialized) {
            init();
        }
        return deviceByUserAgent.get(userAgent);
    }
    
    /**
     * 
     * @return the device database.
     * @throws PolishException if the database could not be instantiated.
     */
    public static DeviceDatabase getDeviceDatabase() throws PolishException{
        if(deviceDatabase == null) {
            init();
        }
        return deviceDatabase;
    }
    
    /**
     * @throws PolishException
     */
    private static void initDatabase() throws PolishException {
        Map<String,String> properties = new HashMap<String,String>();
        properties.put("mpp.home","no/path");
        Map<String,InputStream> inputStreamsByfileName = new HashMap<String,InputStream>();
        FileInputStream capabilityInputStream;
        InputStream apisInputStream;
        InputStream configurationsInputStream;
        InputStream platformsInputStream;
        InputStream groupsInputStream;
        InputStream vendorsInputStream;
        InputStream devicesInputStream;
        try {
            capabilityInputStream = new FileInputStream(PATH_POLISH_XML+"/capabilities.xml");
            apisInputStream = new FileInputStream(PATH_POLISH_XML+"/apis.xml");
            configurationsInputStream = new FileInputStream(PATH_POLISH_XML+"/configurations.xml");
            platformsInputStream = new FileInputStream(PATH_POLISH_XML+"/platforms.xml");
            groupsInputStream = new FileInputStream(PATH_POLISH_XML+"/groups.xml");
            vendorsInputStream = new FileInputStream(PATH_POLISH_XML+"/vendors.xml");
            devicesInputStream = new FileInputStream(PATH_POLISH_XML+"/devices.xml");
        } catch (FileNotFoundException exception) {
            throw new PolishException("Could not load file."+exception);
        }
        inputStreamsByfileName.put("capabilities.xml",capabilityInputStream);
        inputStreamsByfileName.put("apis.xml",apisInputStream);
        inputStreamsByfileName.put("configurations.xml",configurationsInputStream);
        inputStreamsByfileName.put("platforms.xml",platformsInputStream);
        inputStreamsByfileName.put("groups.xml",groupsInputStream);
        inputStreamsByfileName.put("vendors.xml",vendorsInputStream);
        inputStreamsByfileName.put("devices.xml",devicesInputStream);
        try {
            deviceDatabase = new DeviceDatabase(properties,null,null,null,null,inputStreamsByfileName,new HashMap());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create deviceDatabase.",e);
        }
    }
    
    
    private static void initUserAgentMap() throws PolishException {
        DeviceManager deviceManager = deviceDatabase.getDeviceManager();
        Device[] allDevices = deviceManager.getDevices();

        Requirements requirements = new Requirements();
        requirements.addRequirement(new VariableDefinedRequirement(CAPABILITY_WAP_USER_AGENT));
        Device[] devices = requirements.filterDevices(allDevices);
        
        Device cachedDevice;
        Device currentDevice;
        String shortendUserAgentId;
        String currentUserAgentAsString;
        
        deviceByUserAgent = new HashMap<String,Device>();
        for (int deviceIndex = 0; deviceIndex < devices.length; deviceIndex++) {
            
            currentDevice = devices[deviceIndex];
            
            currentUserAgentAsString = currentDevice.getCapability(CAPABILITY_WAP_USER_AGENT);
            if(currentUserAgentAsString == null) {
                continue;
            }
            String[] currentUserAgents = StringUtil.splitAndTrim(currentUserAgentAsString,'\1');
            
            for (int userAgentIndex = 0; userAgentIndex < currentUserAgents.length; userAgentIndex++) {
                
                String currentUserAgent = currentUserAgents[userAgentIndex];
                int lengthOfString = currentUserAgent.length();
                
                for (int postfixIndex = 0; postfixIndex < lengthOfString; postfixIndex++) {
                    shortendUserAgentId = currentUserAgent.substring(0,lengthOfString-postfixIndex);
                    cachedDevice = deviceByUserAgent.get(shortendUserAgentId);
                    if(cachedDevice != null) {
                        // We have already a device with this user agent prefix. Abort.
                        break;
                    }
                    deviceByUserAgent.put(shortendUserAgentId,currentDevice);
                }
            }
        }
    }
    
}
