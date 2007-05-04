/*
 * PolishPresentersProcessor.java
 *
 * Created on March 28, 2007, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.components;

import org.netbeans.modules.vmd.api.codegen.MultiGuardedSection;
import org.netbeans.modules.vmd.api.model.ComponentDescriptor;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.api.model.Presenter;
import org.netbeans.modules.vmd.api.model.PresentersProcessor;
import org.netbeans.modules.vmd.api.properties.DefaultPropertiesPresenter;
import org.netbeans.modules.vmd.midp.codegen.CodeClassInitHeaderFooterPresenter;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.items.ItemCD;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;
import org.netbeans.modules.vmd.midp.propertyeditors.PropertyEditorString;

import java.util.ArrayList;

/**
 * @author dave
 */
public class PolishPresentersProcessor extends PresentersProcessor {
    
    public static final String CATEGORY_POLISH = "J2ME Polish Properties";
    
    public PolishPresentersProcessor() {
        super (MidpDocumentSupport.PROJECT_TYPE_MIDP);
    }
    
    protected void postProcessPresenters(DesignDocument document, ComponentDescriptor descriptor, ArrayList<Presenter> presenters) {
        if (document.getDescriptorRegistry().isInHierarchy(DisplayableCD.TYPEID, descriptor.getTypeDescriptor().getThisType())
            ||  document.getDescriptorRegistry().isInHierarchy(ItemCD.TYPEID, descriptor.getTypeDescriptor().getThisType())) {
            presenters.add (new DefaultPropertiesPresenter ()
                .addPropertiesCategory(CATEGORY_POLISH)
                    .addProperty ("J2ME Polish Style ID", "The style id specific for J2ME Polish", PropertyEditorString.createInstance(), PolishPropertiesProcessor.PROP_STYLE)
            );
            presenters.add (new CodeClassInitHeaderFooterPresenter () {
                public void generateClassInitializationHeader(MultiGuardedSection section) {
                    String style = MidpTypes.getString (getComponent().readProperty(PolishPropertiesProcessor.PROP_STYLE));
                    if (style != null)
                        section.getWriter().write ("//#style " + style + "\n");
                }
                public void generateClassInitializationFooter(MultiGuardedSection section) {
                }
            });
        }
    }

}
