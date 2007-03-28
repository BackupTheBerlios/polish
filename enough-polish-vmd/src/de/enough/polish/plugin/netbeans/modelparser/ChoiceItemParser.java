/*
 * DisplayableParserManager.java
 * 
 * Created on Mar 27, 2007, 10:45:58 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.plugin.netbeans.modelparser.ItemParser;
import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Item;
import javax.microedition.lcdui.Image;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.sources.ListElementEventSourceCD;

/**
 * Converts NetBeans data models of ListElementEventSourceCD to actual J2ME Polish ChoiceItems.
 * 
 * @author robertvirkus
 */
public class ChoiceItemParser extends ItemParser {

    protected Item createItem(DesignComponent designComponent) {
        String text = null;
        Image image = null;
        int type = Choice.IMPLICIT;
        
        PropertyValue value = designComponent.readProperty(ListElementEventSourceCD.PROP_STRING );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            text = MidpTypes.getString(value);
        } else {
            System.out.println("Unable to retrieve text - Kind=" + value.getKind() );
            text = "default";
        }
        value = designComponent.readProperty(ListElementEventSourceCD.PROP_IMAGE );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            // image = ... ???
        }
        return new ChoiceItem( text, image, type );
    }
    
    protected void addAttributes( DesignComponent designComponent, Item item ) {
        super.addAttributes(designComponent, item);
    }

}
