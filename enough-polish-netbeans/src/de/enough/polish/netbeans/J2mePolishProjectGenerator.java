/*
 * J2mePolishProjectGenerator.java
 *
 * Created on December 6, 2006, 3:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.enough.polish.netbeans;

import de.enough.polish.ide.swing.DeviceSelector;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.StringUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;

/**
 * A factory for creating J2ME Polish projects.
 * 
 * @author Robert Virkus
 */
public class J2mePolishProjectGenerator {
    
    /** Creates a new instance of J2mePolishProjectGenerator */
    private J2mePolishProjectGenerator() {
        // allow only static access
    }
    
    public static void generateProjectFromTemplate( String name, File projectDir, File templateDir, 
            DeviceSelector targetDevices, Map buildProperties ) 
    throws IOException 
    {
        FileUtil.copyDirectoryContents( templateDir, projectDir, false);
        
        // rename the build-netbeans.xml in case a sample application is used as template:
        File buildNetBeansScript = new File( projectDir, "build-netbeans.xml" );
        if (buildNetBeansScript.exists()) {
            File buildScript = new File( projectDir, "build.xml" );
            if (buildScript.exists()) {
                buildScript.delete();
            }
            buildNetBeansScript.renameTo( buildScript );
        }

        // now set the project's name:
        if (name == null) {
            name = "HelloWorldProject";
        }
        if (buildProperties == null) {
            buildProperties = new HashMap();
        }
        // set name in build.xml script:
        File buildScript = new File( projectDir, "build.xml" );
        String[] lines = FileUtil.readTextFile(buildScript);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int projectNameIndex = line.indexOf("name=\"");
            if ( projectNameIndex != -1) {
                int closingIndex = line.indexOf('"', projectNameIndex + "name=\"".length() + 1 );
                if (closingIndex == -1) {
                    continue;                        
                }
                line = line.substring(0, projectNameIndex + "name=\"".length() )
                        + name + line.substring( closingIndex );
                lines[i] = line;
                FileUtil.writeTextFile(buildScript, lines);
                break;
            }
        }
        // set name in and add configurations to project.xml script:
        File projectScript = new File( projectDir, "nbproject/project.xml" );
        lines = FileUtil.readTextFile(projectScript);
        String[] configurationNames = targetDevices.getSelectedDeviceIdentifiers();
        ArrayList linesList = new ArrayList( lines.length + configurationNames.length );        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int projectNameIndex = line.indexOf("<name>");
            if ( projectNameIndex != -1) {
                int closingIndex = line.indexOf('<', projectNameIndex + "<name>".length() + 1 );
                if (closingIndex == -1) {
                    continue;                        
                }
                line = line.substring(0, projectNameIndex + "<name>".length() )
                        + name + line.substring( closingIndex );
            } else if (line.indexOf("<configurations") != -1) {
                //linesList.add( line );
                for (int j = 0; j < configurationNames.length; j++) {
                    String configuration = configurationNames[j];
                    //linesList.add( "            <configuration>" + configuration + "</configuration>");
                }
                // now skip until the configurations tag is found again:
                do {
                    line = lines[i];
                    i++;
                }while( (line).indexOf("</configurations") == -1);
                line = lines[i];
            }
            linesList.add( line );
        } 
        FileUtil.writeTextFile(projectScript, linesList);
        // set name in project.properties:
        buildProperties.put( "name", name );
        String usedConfigurations = "";
        for (int i = 0; i < configurationNames.length; i++) {
            usedConfigurations = usedConfigurations+" ,"+configurationNames[i];
        }
        buildProperties.put( "all.configurations", usedConfigurations );
        
        // add configuration properties:
        //TODO dynamically find the used J2ME Platform
        String j2mePlatform = (String) buildProperties.get("polish.EmulatorPlatform");//"MPowerPlayer"; // "J2ME_Wireless_Toolkit_2_2"; 
        if (j2mePlatform == null) {
            j2mePlatform = "MPowerPlayer";
        }
        j2mePlatform = StringUtil.parseStringForEmulartorPlattform(j2mePlatform);
        
        String j2mePlatformDescription = "Emulator Platform"; 
        StringBuffer allIdentifiers = new StringBuffer();
        boolean usePolishGui = "true".equals( buildProperties.get("polish.usePolishGui") );
        for (int j = 0; j < configurationNames.length; j++) {
            String identifier = configurationNames[j];
            allIdentifiers.append( identifier );
            if (j != configurationNames.length - 1 ) {
                allIdentifiers.append(',');
            }
            Map deviceProperties = targetDevices.getDeviceProperties( identifier );
            if (usePolishGui) {
                deviceProperties.put("polish.usePolishGui", "true" );
            }
            addProperties( "configs." + identifier + ".", deviceProperties, buildProperties, j2mePlatform, j2mePlatformDescription );            
            if ( j == 0) {
                addProperties( "", deviceProperties, buildProperties, j2mePlatform, j2mePlatformDescription );                            
            }
        }
        buildProperties.put("devices", allIdentifiers.toString() );
        buildProperties.put("platform.device", configurationNames[0] );
        // write build properties:
        File buildPropertiesFile = new File( projectDir, "nbproject/project.properties");
        Map properties = FileUtil.readPropertiesFile( buildPropertiesFile );
        // remove all previous configs.* properties. This is not really necessary but makes the file smaller and easier to manage:
        Object[] existingKeys = properties.keySet().toArray();
        for (int i = 0; i < existingKeys.length; i++) {
            String key = (String) existingKeys[i];
            if (key.startsWith("configs.")) {
                properties.remove(key);
            }
        }

        properties.putAll(buildProperties); 
        FileUtil.writePropertiesFile(buildPropertiesFile, properties);
        
        // write private properties:
        String[] privatePropertiesLines = new String[] {
            "config.active=" + configurationNames[0],
            "javadoc.preview=true"
        };

        File privatePropertiesFile = new File( projectDir, "nbproject/private/private.properties");
        FileUtil.writeTextFile( privatePropertiesFile, privatePropertiesLines );
        
        //ProjectManager manager = ProjectManager.getDefault();
        //FileObject projectFileObject = org.openide.filesystems.FileUtil.toFileObject(projectDir);
        //Project project = manager.findProject( projectFileObject );
        //return project;
        //return null;
    }
    
    private static void  addProperties(String prepend, Map deviceProperties, Map buildProperties, String j2mePlatform, String j2mePlatformDescription ) {
            buildProperties.put( prepend + "platform.active", j2mePlatform );
            buildProperties.put( prepend + "platform.active.description", j2mePlatformDescription );
            buildProperties.put( prepend + "platform.bootclasspath", getBootClassPath( deviceProperties ) );
            buildProperties.put( prepend + "platform.apis", getApis( deviceProperties ) );
            buildProperties.put( prepend + "abilities", getAbilities( deviceProperties ) );
            buildProperties.put( prepend + "platform.profile", getProfile( deviceProperties ) );
            buildProperties.put( prepend + "platform.configuration", getConfiguration( deviceProperties ) );
    }


    public static String getProfile( Map deviceProperties ) {
        String platform = (String) deviceProperties.get("polish.javaplatform");
        if (platform == null) {
            return "";
        }
        String[] platforms = StringUtil.splitAndTrim(platform, ',');
        for (int i = 0; i < platforms.length; i++) {
            platform = platforms[i];
            if (platform.startsWith("MIDP")) {
                break;
            }
        }
        return platform.replace('/', '-');
    }
    
    public static String getConfiguration( Map deviceProperties ) {
        String configuration = (String) deviceProperties.get("polish.javaconfiguration");
        if (configuration == null) {
            return "";
        }
        String[] configurations = StringUtil.splitAndTrim(configuration, ',');
        for (int i = 0; i < configurations.length; i++) {
            configuration = configurations[i];
            if (configuration.startsWith("CLDC")) {
                break;
            }
        }
        return configuration.replace('/', '-');
    }    

    public static String getApis( Map deviceProperties ) {
        String[] supportedApis = (String[]) deviceProperties.get("polish.SupportedApis");
        if (supportedApis == null) {
            return "";
        } 
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < supportedApis.length; i++) {
            String api = supportedApis[i];
            NetBeansLibrary lib = getNetBeansApi( api );
            buffer.append(  lib.getNetBeansName() )
                    .append('-')
                    .append( lib.getVersion() );
            if ( i != supportedApis.length -1 ) {
                buffer.append(',');
            }
        }
        return buffer.toString();
        //return "JSR82-1.0,JSR75-1.0,JSR172-1.0,JSR184-1.0,MMAPI-1.1,WMA-2.0";
    }
    
    public static NetBeansLibrary getNetBeansApi(String polishApi) {
        return NetBeansLibrary.getInstance(polishApi);
    }

    
    public static String getBootClassPath( Map deviceProperties ) {
        //TODO exchange polish home dir with ${polish.home}
       return (String) deviceProperties.get("polish.BootClassPath") + ':' + (String)deviceProperties.get("polish.ClassPath");
        //return "${platform.home}/lib/cldcapi11.jar:${platform.home}/lib/midpapi20.jar:${platform.home}/lib/jsr082.jar:${platform.home}/lib/jsr75.jar:${platform.home}/lib/j2me-ws.jar:${platform.home}/lib/jsr184.jar:${platform.home}/lib/mmapi.jar:${platform.home}/lib/wma20.jar";
    }

    public static String getAbilities( Map deviceProperties ) {
        // problem: J2ME Polish capabilities can include commas, while NetBeans abilities are limited to simple sequences...
        ArrayList allLowerCaseKeys = new ArrayList();
        HashMap registeredKeys = new HashMap();
        StringBuffer buffer = new StringBuffer();
        for (Iterator it = deviceProperties.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (key.startsWith("polish.emulator") || key.startsWith("polish.Emulator") ||  key.startsWith("polish.build") ||  key.startsWith("polish.Build")) {
                continue;
            }
            Object valueObj = deviceProperties.get(key);//.toString();
            if (valueObj == null) {
                //System.out.println("INVALID key=" + key );
                continue;
            }
            if ( hasOnlyLowerCase( key ) ) {
                allLowerCaseKeys.add( key );
                continue;
            }
            registeredKeys.put( key.toLowerCase(), valueObj);
            String value = valueObj.toString().replace(',',';');
            if ("".equals(value)) {
                continue;
            }
            if ("true".equals(value)) {
                buffer.append( key )
                    .append(',');               
            } else {
                buffer.append( key )
                    .append('=')
                    .append( value )
                    .append(',');                
            }
        }
        // now check the lower case keys:
        for (Iterator it = allLowerCaseKeys.iterator(); it.hasNext();) {
            String key = (String)it.next();
            if ( registeredKeys.get(key) == null) {
                Object valueObj = deviceProperties.get(key);//.toString();
                if (valueObj == null) {
                    //System.out.println("INVALID key=" + key );
                    continue;
                }
                String value = valueObj.toString().replace(',',';');
                if ("".equals(value)) {
                    continue;
                }
                if ("true".equals(value)) {
                    buffer.append( key )
                        .append(',');               
                } else {
                    buffer.append( key )
                        .append('=')
                        .append( value )
                        .append(',');                
                }
            }
            
        }

        buffer.setLength( buffer.length() - 1 );

//        String[] supportedApis = (String[]) deviceProperties.get("polish.SupportedApis");
//        if (supportedApis == null) {
//            return "";
//        } 
//        StringBuffer buffer = new StringBuffer();
//        for (int i = 0; i < supportedApis.length; i++) {
//            String api = supportedApis[i];
//            NetBeansLibrary lib = getNetBeansApi( api );
//            buffer.append(  lib.getNetBeansName() )
//                    .append('=')
//                    .append( lib.getVersion() );
//            if ( i != supportedApis.length -1 ) {
//                buffer.append(',');
//            }
//        }
        return buffer.toString();
       // return "JSR82=1.0,JSR75=1.0,JSR172=1.0,JSR184=1.0,MMAPI=1.1,WMA=2.0";
    }
    
    private static boolean hasOnlyLowerCase( String string ) {
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (Character.isUpperCase(c)) {
                return false;
            }
        }

        return true;
    }

}
