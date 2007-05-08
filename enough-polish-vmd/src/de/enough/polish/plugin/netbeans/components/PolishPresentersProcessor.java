/*
 * PolishPresentersProcessor.java
 *
 * Created on March 28, 2007, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.components;

import java.util.List;
import java.util.Map;
import org.netbeans.modules.vmd.api.codegen.MultiGuardedSection;
import org.netbeans.modules.vmd.api.codegen.Parameter;
import org.netbeans.modules.vmd.api.model.ComponentDescriptor;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.api.model.Presenter;
import org.netbeans.modules.vmd.api.model.PresentersProcessor;
import org.netbeans.modules.vmd.api.model.TypeID;
import org.netbeans.modules.vmd.api.model.Versionable;
import org.netbeans.modules.vmd.api.properties.DefaultPropertiesPresenter;
import org.netbeans.modules.vmd.midp.codegen.CodeClassInitHeaderFooterPresenter;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.items.ItemCD;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;
import org.netbeans.modules.vmd.midp.propertyeditors.PropertyEditorString;

import java.util.ArrayList;
import java.util.Arrays;
import org.netbeans.modules.vmd.api.codegen.CodeReferencePresenter;
import org.netbeans.modules.vmd.api.codegen.CodeSetterPresenter;
import org.netbeans.modules.vmd.api.codegen.Setter;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpVersionable;
import org.netbeans.modules.vmd.midp.components.displayables.ListCD;
import org.netbeans.modules.vmd.midp.components.displayables.ListCode;
import org.netbeans.modules.vmd.midp.components.elements.ChoiceElementCD;
import org.netbeans.modules.vmd.midp.components.items.ChoiceGroupCD;
import org.netbeans.modules.vmd.midp.components.items.ChoiceGroupCD;
import org.netbeans.modules.vmd.midp.components.items.ChoiceGroupCode;
import org.netbeans.modules.vmd.midp.components.sources.ListElementEventSourceCD;

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
            ||  document.getDescriptorRegistry().isInHierarchy(ItemCD.TYPEID, descriptor.getTypeDescriptor().getThisType())
            ||  document.getDescriptorRegistry().isInHierarchy(ListElementEventSourceCD.TYPEID, descriptor.getTypeDescriptor().getThisType())
            ||  document.getDescriptorRegistry().isInHierarchy(ChoiceElementCD.TYPEID, descriptor.getTypeDescriptor().getThisType())
        ) {
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
            if (document.getDescriptorRegistry().isInHierarchy(ListCD.TYPEID, descriptor.getTypeDescriptor().getThisType())) {
                presenters.add (new CodeSetterPresenter ().addSetters(new StyleAppendSetter (ListCD.PROP_ELEMENTS, ListCode.PARAM_STRING, ListCode.PARAM_IMAGE)));
            }
            if (document.getDescriptorRegistry().isInHierarchy(ChoiceGroupCD.TYPEID, descriptor.getTypeDescriptor().getThisType())) {
                presenters.add (new CodeSetterPresenter ().addSetters(new StyleAppendSetter (ChoiceGroupCD.PROP_ELEMENTS, ChoiceGroupCode.PARAM_STRING, ChoiceGroupCode.PARAM_IMAGE)));
            }
        }
    }

    private static class StyleAppendSetter implements Setter {

        private String propertyName;
        private List<String> parameterNames;

        public StyleAppendSetter(String propertyName, String... parameterNames) {
            this.propertyName = propertyName;
            this.parameterNames = Arrays.asList (parameterNames);
        }

        public boolean isConstructor() {
            return false;
        }

        public TypeID getConstructorRelatedTypeID() {
            return null;
        }

        public int getPriority() {
            return 1;
        }

        public String getSetterName() {
            return "append";
        }

        public Versionable getVersionable() {
            return MidpVersionable.MIDP;
        }

        public void generateSetterCode(MultiGuardedSection section, DesignComponent component, Map<String, Parameter> name2parameter) {
            List<PropertyValue> array = component.readProperty(propertyName).getArray();
            if (array == null)
                return;
            for (int index = 0; index < array.size (); index ++) {
                DesignComponent child = array.get (index).getComponent ();
                if (child == null)
                    continue;
                String style = MidpTypes.getString(child.readProperty(PolishPropertiesProcessor.PROP_STYLE));
                if (style != null)
                    section.getWriter().write ("//#style " + style + "\n"); // NOI18N
                section.getWriter ().write (CodeReferencePresenter.generateDirectAccessCode (component));
                section.getWriter ().write (".append ("); // NOI18N
                for (int paramIndex = 0; paramIndex < parameterNames.size (); paramIndex ++) {
                    if (paramIndex > 0)
                        section.getWriter ().write (", "); // NOI18N
                    String parameterName = parameterNames.get (paramIndex);
                    Parameter parameter = name2parameter.get (parameterName);
                    parameter.generateParameterCode (component, section, index);
                }
                section.getWriter ().write (");\n"); // NOI18N
            }
        }

        public List<String> getParameters() {
            return parameterNames;
        }

    }

}
