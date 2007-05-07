/*
 * PolishPresentersProcessor.java
 *
 * Created on March 28, 2007, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.components;

import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.api.model.PostInitializeProcessor;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.modules.vmd.midp.components.items.ItemCD;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;
import org.netbeans.modules.vmd.midp.components.general.ClassCD;

/**
 * @author dave
 */
public class PolishPostInitializeProcessor extends PostInitializeProcessor {
    
    public PolishPostInitializeProcessor() {
        super (MidpDocumentSupport.PROJECT_TYPE_MIDP);
    }
    protected void postInitialize (DesignComponent component) {
        DesignDocument document = component.getDocument();
        
        if (document.getDescriptorRegistry().isInHierarchy(DisplayableCD.TYPEID, component.getType())
            ||  document.getDescriptorRegistry().isInHierarchy(ItemCD.TYPEID, component.getType())) {
            component.writeProperty(PolishPropertiesProcessor.PROP_STYLE, component.readProperty(ClassCD.PROP_INSTANCE_NAME));
        }
    }

}
