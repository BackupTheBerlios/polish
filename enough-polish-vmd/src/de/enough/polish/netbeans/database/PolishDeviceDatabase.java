package de.enough.polish.netbeans.database;

import java.util.*;
import org.netbeans.spi.mobility.cfgfactory.ProjectConfigurationFactory;

public class PolishDeviceDatabase implements ProjectConfigurationFactory, ProjectConfigurationFactory.CategoryDescriptor {
    
    public PolishDeviceDatabase() {
    }
    
    public CategoryDescriptor getRootCategory() {
        return this;
    }

    public String getDisplayName() {
        return "My StJupid Device Database";
    }

    public List<ProjectConfigurationFactory.Descriptor> getChildren() {
        return Arrays.asList(new ProjectConfigurationFactory.Descriptor[] {
            new Category("Devices Category 1", new ProjectConfigurationFactory.Descriptor[] {
                new Device("MyDevice1", "ability1=value1,ability2,ability3"),
                new Category("Devices Category 1 / A", new ProjectConfigurationFactory.Descriptor[] {
                    new Device("MyDevice1A", "ability1=value1,ability2,ability3"),
                }),
                new Device("MyDevice2", "ability1=value2,ability5,ability7"),
            }),
            new Category("Devices Category 2", new ProjectConfigurationFactory.Descriptor[] {
                new Device("MyDevice23", "ability15=value16"),
            }),
            new Device("MyDeviceOutOfCategories", "abilityXX=yyy"),
        });
    }

    class Category implements ProjectConfigurationFactory.CategoryDescriptor {
        private String name;
        private ProjectConfigurationFactory.Descriptor[] children;
        
        public Category(String name, ProjectConfigurationFactory.Descriptor[] children) {
            this.name = name;
            this.children = children;
        }

        public String getDisplayName() {
            return name;
        }
    
        public List<ProjectConfigurationFactory.Descriptor> getChildren() {
            return Arrays.asList(children);
        }
    }
    
    class Device implements ProjectConfigurationFactory.ConfigurationTemplateDescriptor {
        private String name;
        private String abilities;

        public Device(String name, String abilities) {
            this.name = name;
            this.abilities = abilities;
        }

        public String getDisplayName() {
            return name;
        }
    
        public String getCfgName() {
            return name;
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
