/*
 * BrowserCD.java
 * 
 * Created on Mar 28, 2007, 2:54:03 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.components.items;

import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.vmd.api.codegen.CodeSetterPresenter;
import org.netbeans.modules.vmd.api.model.ComponentDescriptor;
import org.netbeans.modules.vmd.api.model.Presenter;
import org.netbeans.modules.vmd.api.model.PropertyDescriptor;
import org.netbeans.modules.vmd.api.model.TypeDescriptor;
import org.netbeans.modules.vmd.api.model.TypeID;
import org.netbeans.modules.vmd.api.model.VersionDescriptor;
import org.netbeans.modules.vmd.api.properties.DefaultPropertiesPresenter;
import org.netbeans.modules.vmd.midp.codegen.MidpParameter;
import org.netbeans.modules.vmd.midp.codegen.MidpSetter;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.MidpVersionable;
import org.netbeans.modules.vmd.midp.components.items.CustomItemCD;
import org.netbeans.modules.vmd.midp.propertyeditors.PropertiesCategories;
import org.netbeans.modules.vmd.midp.propertyeditors.PropertyEditorString;

/**
 * <p>Describes the Browser item of J2ME Polish.</p>
 *
 * @author robertvirkus
 */
public class BrowserCD extends ComponentDescriptor {
    
    // we use convention to typeid that reprensents a class that the second parameter is fully-qualified class-name 
    public static final TypeID TYPEID = new TypeID (TypeID.Kind.COMPONENT, "de.enough.polish.browser.html.HtmlBrowser"); // NOI18N
    public static final String PROP_URL = "URL";
    public static final String ICON_PATH = null;

    public BrowserCD() {
    }

    public TypeDescriptor getTypeDescriptor() {
        return new TypeDescriptor (CustomItemCD.TYPEID, TYPEID, true, true);
    }

    public VersionDescriptor getVersionDescriptor() {
        return null;
    }

    public List<PropertyDescriptor> getDeclaredPropertyDescriptors() {
        return Arrays.asList (
            // create a property description
            new PropertyDescriptor (PROP_URL, MidpTypes.TYPEID_JAVA_LANG_STRING, MidpTypes.createStringValue("http://www.j2mepolish.org/mobile/index.html"), false, true, MidpVersionable.MIDP)
        );
    }

    protected List<? extends Presenter> createPresenters() {
        return Arrays.asList (
            // create a properties presenter
            new DefaultPropertiesPresenter()
                // in the general properties category
                .addPropertiesCategory(PropertiesCategories.CATEGORY_PROPERTIES)
                    // add property myValue
                    .addProperty("URL", PropertyEditorString.createInstance(), PROP_URL),
            new CodeSetterPresenter()
                // we will use "myValue" property as a parameter
                .addParameters(MidpParameter.create(PROP_URL))
                // define a default contructor of the "MyForm" class
                .addSetters(MidpSetter.createConstructor(TYPEID, MidpVersionable.MIDP).addParameters())
                // define "setMyValue" setter
                .addSetters (MidpSetter.createSetter("go", MidpVersionable.MIDP).addParameters(PROP_URL))
        );
    }

}
