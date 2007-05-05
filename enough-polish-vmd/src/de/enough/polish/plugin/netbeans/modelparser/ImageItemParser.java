/*
 * ImageItemParser.java
 * 
 * Created on Mar 27, 2007, 10:45:58 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.plugin.netbeans.modelparser.ItemParser;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import javax.microedition.lcdui.Image;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.items.ImageItemCD;

/**
 * Converts NetBeans data models of ListElementEventSourceCD to actual J2ME Polish ChoiceItems.
 * 
 * @author robertvirkus
 */
public class ImageItemParser extends ItemParser {

    protected Item createItem(DesignComponent designComponent, ResourcesProvider resourcesProvider) {
        String text = null;
        String imageUrl = null;
        Image image = null;
        int layout = ImageItem.LAYOUT_DEFAULT;
        int appearanceMode = ImageItem.PLAIN;
        
        PropertyValue value = designComponent.readProperty(ImageItemCD.PROP_ALT_TEXT );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            text = MidpTypes.getString(value);
        }
        value = designComponent.readProperty(ImageItemCD.PROP_IMAGE );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            
            imageUrl = MidpTypes.getString(value);
        }
        return new ImageItem( null, image, layout, text, appearanceMode );
    }
  

}
