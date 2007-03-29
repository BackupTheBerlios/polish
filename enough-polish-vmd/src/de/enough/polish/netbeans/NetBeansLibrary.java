/*
 * NetBeansLibrary.java
 *
 * Created on December 7, 2006, 8:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.netbeans;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author robertvirkus
 */
public class NetBeansLibrary {
    
    private static final Map KNOWN_LIBRARIES = new HashMap();
    static {
        KNOWN_LIBRARIES.put("mmapi", new NetBeansLibrary( "mmapi", "MMAPI", "1.0") );
        KNOWN_LIBRARIES.put("mmapi1.1", new NetBeansLibrary( "mmapi", "MMAPI", "1.1") );
        KNOWN_LIBRARIES.put("mmapi2.0", new NetBeansLibrary( "mmapi2.0", "MMAPI", "2.0") );
        KNOWN_LIBRARIES.put("wmapi", new NetBeansLibrary( "wmapi", "WMAPI", "1.1") );
        KNOWN_LIBRARIES.put("wmapi1.1", new NetBeansLibrary( "wmapi1.1", "WMAPI", "1.1") );
        KNOWN_LIBRARIES.put("wmapi2.0", new NetBeansLibrary( "wmapi", "WMAPI", "2.0") );
        KNOWN_LIBRARIES.put("pdaapi", new NetBeansLibrary( "pdaapi", "JSR75", "1.0") );
        KNOWN_LIBRARIES.put("fileconnectionapi", new NetBeansLibrary( "fileconnectionapi", "JSR75", "1.0") );
        KNOWN_LIBRARIES.put("btapi", new NetBeansLibrary( "btapi", "JSR82", "1.0") );
        KNOWN_LIBRARIES.put("pimapi", new NetBeansLibrary( "pimapi", "JSR75", "1.0") );
        KNOWN_LIBRARIES.put("sipapi", new NetBeansLibrary( "sipapi", "JSR180", "1.0") );
        KNOWN_LIBRARIES.put("securityapi", new NetBeansLibrary( "securityapi", "JSR177", "1.0") );
        KNOWN_LIBRARIES.put("3dapi", new NetBeansLibrary( "3dapi", "JSR184", "1.0") );
        KNOWN_LIBRARIES.put("svg", new NetBeansLibrary( "svg", "JSR226", "1.0") );
        KNOWN_LIBRARIES.put("webservice", new NetBeansLibrary( "webservice", "JSR172", "1.0") );
        KNOWN_LIBRARIES.put("nokia-ui", new NetBeansLibrary( "nokia-ui", "NOKIAUIAPI", "1.0") );
        KNOWN_LIBRARIES.put("siemens-color-game-api", new NetBeansLibrary( "siemens-color-game-api", "SIEMENSCOLORGAMEAPI", "1.0") );
        KNOWN_LIBRARIES.put("siemens-extension-api", new NetBeansLibrary( "siemens-extension-api", "SIEMENSEXTENSIONAPI", "1.0") );
    }
    private String polishName;
    private String netBeansName;
    private String version;
    
    /** Creates a new instance of NetBeansLibrary */
    public NetBeansLibrary( String polishName, String netBeansName, String version ) {
        this.polishName = polishName;
        this.netBeansName = netBeansName;
        this.version = version;
    }
    
    public static NetBeansLibrary getInstance( String polishName ) {
        NetBeansLibrary lib = (NetBeansLibrary) KNOWN_LIBRARIES.get(polishName);
        if (lib == null) {
            System.out.println("unknown library: " + polishName );
            String netBeansName = polishName;
            String version = "1-0";
            lib = new NetBeansLibrary( polishName, netBeansName, version );
            KNOWN_LIBRARIES.put(polishName, lib);
        }
        return lib;
    }
    
    
    public String getPolishName() {
        return this.polishName;
    }
    
    public String getNetBeansName() {
        return this.netBeansName;
    }
    
    public String getVersion() {
        return this.version;
    }
    
}
