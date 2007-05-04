/*
 * PolishPropertiesProcessor.java
 *
 * Created on March 28, 2007, 1:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.components;

import org.netbeans.modules.vmd.api.model.*;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;
import org.netbeans.modules.vmd.midp.components.items.ItemCD;

import java.util.ArrayList;

/**
 * @author dave
 */
public class PolishPropertiesProcessor extends PropertiesProcessor {
    
    public static final String PROP_STYLE = "j2mepolish.style"; // NOI18N
    
    public PolishPropertiesProcessor() {
        super (MidpDocumentSupport.PROJECT_TYPE_MIDP);
    }
    
    protected void postProcessProperties(ComponentDescriptor descriptor, ArrayList<PropertyDescriptor> properties) {
        while (descriptor != null) {
            if (DisplayableCD.TYPEID.equals (descriptor.getTypeDescriptor().getThisType()) 
                    || ItemCD.TYPEID.equals (descriptor.getTypeDescriptor().getThisType()) ) 
            {
                properties.add(new PropertyDescriptor (PROP_STYLE, MidpTypes.TYPEID_JAVA_LANG_STRING, PropertyValue.createNull(), true, false, Versionable.FOREVER));
                return;
            }
            descriptor = descriptor.getSuperDescriptor();
        }
    }

}
