/*
 * FormParser.java
 * 
 * Created on Mar 28, 2007, 11:20:57 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import javax.microedition.lcdui.Displayable;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.displayables.FormCD;

/**
 * <p>Parses Forms</p>
 *
 * @author robertvirkus
 */
public class FormParser extends DisplayableParser {

    public FormParser() {
    }

    protected Displayable createDisplayable(DesignComponent designComponent) {
        return new Form(null);
    }
    
    protected void addAttributes( DesignComponent designComponent, Displayable displayable ) {
        super.addAttributes(designComponent, displayable);
        Form form = (Form) displayable;
        PropertyValue value =  designComponent.readProperty(FormCD.PROP_ITEMS);
        if (value.getKind() == PropertyValue.Kind.ARRAY) {
             for (PropertyValue itemValue:value.getArray() )  {
                 Item item = getItem( itemValue.getComponent(), displayable );
                 if (item != null) {
                     form.append(item);
                 }
             }
        }
    }

}
