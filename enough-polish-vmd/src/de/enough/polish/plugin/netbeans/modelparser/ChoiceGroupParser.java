/*
 * ChoiceGroupParser.java
 * 
 * Created on Mar 27, 2007, 10:45:58 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.plugin.netbeans.modelparser.ItemParser;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Item;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.items.ChoiceGroupCD;

/**
 * Converts NetBeans data models of ChoiceGroupCD to actual J2ME Polish ChoiceGroups.
 * 
 * @author robertvirkus
 */
public class ChoiceGroupParser extends ItemParser {

    protected Item createItem(DesignComponent designComponent, ResourcesProvider resourcesProvider) {
        int type = Choice.EXCLUSIVE;
        
        PropertyValue value = designComponent.readProperty(ChoiceGroupCD.PROP_CHOICE_TYPE );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            type = MidpTypes.getInteger(value);
        }
        return new ChoiceGroup(null, type);
    }
    
    protected void addAttributes( DesignComponent designComponent, ResourcesProvider resourcesProvider, Item item ) {
        super.addAttributes(designComponent, resourcesProvider, item);
        ChoiceGroup choiceGroup = (ChoiceGroup) item;
        PropertyValue value = designComponent.readProperty(ChoiceGroupCD.PROP_ELEMENTS );
        if (value.getKind() == PropertyValue.Kind.ARRAY) {
            for (PropertyValue itemValue:value.getArray() )  {
                Item childItem = getItem( itemValue.getComponent(), resourcesProvider, item );
                if (childItem instanceof ChoiceItem) {
                    ChoiceItem choiceItem = (ChoiceItem) childItem ;
                    choiceGroup.append( choiceItem );
                }
                        
            }

        }
    }

}
