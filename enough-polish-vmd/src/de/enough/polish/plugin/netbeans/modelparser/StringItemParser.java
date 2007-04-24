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
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.items.StringItemCD;

/**
 * Converts NetBeans data models of ListElementEventSourceCD to actual J2ME Polish ChoiceItems.
 * 
 * @author robertvirkus
 */
public class StringItemParser extends ItemParser {

    protected Item createItem(DesignComponent designComponent) {
        String text = null;
        int appearanceMode = StringItem.PLAIN;
        
        PropertyValue value = designComponent.readProperty(StringItemCD.PROP_TEXT );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            text = MidpTypes.getString(value);
        } else {
            System.out.println("Unable to retrieve text - Kind=" + value.getKind() );
            text = "default";
        }
        value = designComponent.readProperty(StringItemCD.PROP_APPEARANCE_MODE );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            appearanceMode = MidpTypes.getInteger(value);
        }
        return new StringItem( null, text, appearanceMode );
    }
    
    protected void addAttributes( DesignComponent designComponent, Item item ) {
        super.addAttributes(designComponent, item);
    }

}
