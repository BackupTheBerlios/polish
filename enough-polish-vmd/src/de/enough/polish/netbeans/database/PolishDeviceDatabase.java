package de.enough.polish.netbeans.database;

import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.Vendor;
import de.enough.polish.netbeans.J2mePolishProjectGenerator;
import de.enough.polish.netbeans.platform.PolishPlatform;
import de.enough.polish.plugin.netbeans.settings.PolishSettings;
import de.enough.polish.ExtensionManager;
import de.enough.polish.Environment;
import org.netbeans.spi.mobility.cfgfactory.ProjectConfigurationFactory;
import org.openide.util.Exceptions;

import java.io.File;
import java.util.*;

public class PolishDeviceDatabase implements ProjectConfigurationFactory, ProjectConfigurationFactory.CategoryDescriptor {
    
    public PolishDeviceDatabase() {
    }
    
    public CategoryDescriptor getRootCategory() {
        return this;
    }

    public String getDisplayName() {
        return "J2ME Polish Device Database";
    }

    public List<ProjectConfigurationFactory.Descriptor> getChildren() {
        DeviceDatabase database = new DeviceDatabase (new File (PolishSettings.getDefault ().getPolishHome ()));
        Category virtualDevices = null;
        HashMap<Vendor, Category> categories = new HashMap<Vendor, Category> ();
        for (de.enough.polish.Device device : database.getDevices ()) {
            if (device.isVirtual ()) {
                if (virtualDevices == null)
                    virtualDevices = new Category ("Virtual Devices", new ArrayList<Descriptor> ());
                add (device, virtualDevices);
            } else {
                Vendor vendor = device.getVendor ();
                Category category = categories.get (vendor);
                if (category == null) {
                    category = new Category (vendor.getIdentifier (), new ArrayList<Descriptor> ());
                    categories.put (vendor, category);
                }
                add (device, category);
            }
        }
        ArrayList<Descriptor> list = new ArrayList<Descriptor> (categories.values ());
        Collections.sort (list, new Comparator<Descriptor>() {
            public int compare (Descriptor o1, Descriptor o2) {
                return o1.getDisplayName ().compareToIgnoreCase (o2.getDisplayName ());
            }
        });
        if (virtualDevices != null)
            list.add (0, virtualDevices);
        return list;
    }

    private void add (de.enough.polish.Device device, Category category) {
        HashMap<String, String> map = createPropertiesMap (device);
        if (map == null)
            return;

        Device dev = new Device (device.getName (), map.get ("platform.device"), map);
        category.getChildren ().add (dev);
    }

    public static HashMap<String, String> createPropertiesMap (de.enough.polish.Device device) {
        String polishHome = PolishSettings.getDefault ().getPolishHome ();
        try {
            device.setEnvironment (new Environment (new ExtensionManager (new org.apache.tools.ant.Project ()), new HashMap (), new File (polishHome + "/samples/blank/"))); // NOI18N
        } catch (Exception e) {
            Exceptions.printStackTrace (e);
            return null;
        }

        String abilities = J2mePolishProjectGenerator.getAbilities (device.getCapabilities ());

        HashMap<String, String> map = new HashMap<String, String> ();
        map.put ("abilities", abilities); // NOI18N
        map.put ("platform.active", "J2MEPolishEmulator"); // NOI18N
        map.put ("platform.active.description", "J2ME Polish Emulator"); // NOI18N
        map.put ("platform.device", device.getIdentifier ()); // NOI18N
        map.put ("platform.configuration", device.isCldc11 () ? "CLDC-1.1" : "CLDC-1.0"); // NOI18N
        map.put ("platform.profile", device.isMidp2 () ? "MIDP-2.0" : "MIDP-1.0"); // NOI18N

        StringBuffer apis = new StringBuffer ();
        StringBuffer bootclasspath = new StringBuffer ();
        if (device.isCldc11 ())
            bootclasspath.append (polishHome).append ("/import/cldc-1.1.jar:"); // NOI18N
        if (device.isCldc10 ())
            bootclasspath.append (polishHome).append ("/import/cldc-1.0.jar:"); // NOI18N
        if (device.isMidp2 ())
            bootclasspath.append (polishHome).append ("/import/midp-2.0.jar:"); // NOI18N
        if (device.isMidp1 ())
            bootclasspath.append (polishHome).append ("/import/midp-1.0.jar:"); // NOI18N
        String[] strings = device.getBootClassPaths ();
        if (strings != null)
            for (int a = 0; a < strings.length; a ++) {
                apis.append ("OptionalBootPathElement").append (a + 1).append ("-1.0,"); // NOI18N
                bootclasspath.append (strings[a]).append (":"); // NOI18N
            }
        strings = device.getClassPaths ();
        if (strings != null)
            for (int a = 0; a < strings.length; a ++) {
                apis.append ("OptionalPathElement").append (a + 1).append ("-1.0,"); // NOI18N
                bootclasspath.append (strings[a]).append (":");
            }
        apis.append ("OptionalElement1-1.0,");
        bootclasspath.append (polishHome).append ("/import/enough-j2mepolish-client.jar").append (":");
        map.put ("platform.apis", apis.toString ()); // NOI18N
        map.put ("platform.bootclasspath", bootclasspath.toString ()); // NOI18N
        map.put ("javac.source", "1.3"); // NOI18N
        map.put ("javac.target", "1.3"); // NOI18N
        return map;
    }

    class Category implements ProjectConfigurationFactory.CategoryDescriptor {

        private String name;
        private List<ProjectConfigurationFactory.Descriptor> children;
        
        public Category(String name, List<ProjectConfigurationFactory.Descriptor> children) {
            this.name = name;
            this.children = children;
        }

        public String getDisplayName() {
            return name;
        }
    
        public List<ProjectConfigurationFactory.Descriptor> getChildren() {
            return children;
        }

    }
    
    class Device implements ProjectConfigurationFactory.ConfigurationTemplateDescriptor {

        private String name;
        private String cfgName;
        private Map map;

        public Device(String name, String cfgName, Map<String,String> map) {
            this.name = name;
            this.cfgName = cfgName;
            this.map = map;
        }

        public String getDisplayName() {
            return name;
        }
    
        public String getCfgName() {
            return cfgName;
        }

        public Map<String, String> getProjectConfigurationProperties() {
            return map;
        }

        public Map<String, String> getProjectGlobalProperties() {
            return null;
        }

        public Map<String, String> getPrivateProperties() {
            return null;
        }

    }

}
