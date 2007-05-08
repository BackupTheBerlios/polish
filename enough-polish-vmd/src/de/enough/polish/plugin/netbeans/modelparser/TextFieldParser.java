/*
 * StringItemParser.java
 * 
 * Created on Mar 27, 2007, 10:45:58 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.plugin.netbeans.modelparser.ItemParser;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.TextField;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.items.StringItemCD;
import org.netbeans.modules.vmd.midp.components.items.TextFieldCD;

/**
 * Converts NetBeans data models of TextFieldCD to actual J2ME Polish ChoiceItems.
 * 
 * @author robertvirkus
 */
public class TextFieldParser extends ItemParser {

    protected Item createItem(DesignComponent designComponent, ResourcesProvider resourcesProvider) {
        String text = null;
        int maxSize = 100;
        int constraints = TextField.ANY;
        
        PropertyValue value = designComponent.readProperty(TextFieldCD.PROP_TEXT );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            text = MidpTypes.getString(value);
        } else {
            System.out.println("Unable to retrieve text - Kind=" + value.getKind() );
            text = "default";
        }
        value = designComponent.readProperty(TextFieldCD.PROP_MAX_SIZE);
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            maxSize = MidpTypes.getInteger(value);
        }
        value = designComponent.readProperty(TextFieldCD.PROP_CONSTRAINTS);
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            constraints = MidpTypes.getInteger(value);
        }
        
        return new TextField( null, text, maxSize, constraints );
    }
    


}
