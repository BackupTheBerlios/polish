package de.enough.polish.netbeans.database;

import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.Vendor;
import de.enough.polish.plugin.netbeans.settings.PolishSettings;
import de.enough.polish.netbeans.J2mePolishProjectGenerator;
import org.netbeans.spi.mobility.cfgfactory.ProjectConfigurationFactory;

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
        HashMap<Vendor,Category> categories = new HashMap<Vendor, Category> ();
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
        String name = checkForJavaIdentifierCompliant (device.getIdentifier ());
        String abilities = J2mePolishProjectGenerator.getAbilities (device.getCapabilities ());
        Device dev = new Device (device.getName (), name, abilities);
        category.getChildren ().add (dev);
    }

    private static String checkForJavaIdentifierCompliant (String instanceName) {
        if (instanceName == null  ||  instanceName.length () < 1)
            return "a"; // NOI18N
        StringBuffer buffer = new StringBuffer ();
        int index = 0;
        if (Character.isJavaIdentifierStart (instanceName.charAt (0))) {
            buffer.append (instanceName.charAt (0));
            index ++;
        } else {
            buffer.append ('a'); // NOI18N
        }
        while (index < instanceName.length ()) {
            char c = instanceName.charAt (index);
            if (Character.isJavaIdentifierPart (c))
                buffer.append (c);
            else if (c == '/')
                buffer.append ('_');
            index ++;
        }
        return buffer.toString ();
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
        private String abilities;

        public Device(String name, String cfgName, String abilities) {
            this.name = name;
            this.cfgName = cfgName;
            this.abilities = abilities;
        }

        public String getDisplayName() {
            return name;
        }
    
        public String getCfgName() {
            return cfgName;
        }

        public Map<String, String> getProjectConfigurationProperties() {
            return Collections.singletonMap("abilities", abilities);
        }

        public Map<String, String> getProjectGlobalProperties() {
            return null;
        }

        public Map<String, String> getPrivateProperties() {
            return null;
        }

    }

}
