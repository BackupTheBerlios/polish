/*
 * DisplayableParser.java
 * 
 * Created on Mar 27, 2007, 11:17:50 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.plugin.netbeans.ItemParserManager; 
import de.enough.polish.ui.Style;
import javax.microedition.lcdui.Displayable;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;
import org.netbeans.modules.vmd.midp.components.items.ItemCD;

/**
 * <p>Parses screens from DesignComponents</p>
 *
 * @author robertvirkus
 */
public abstract class ItemParser {

    public ItemParser() {
    }
    
    /**
     * Parses the given DesignComponent to create a new Item.
     * 
     * @param designComponent the data of the displayable
     * @return a J2ME Polish Item
     */ 
    protected abstract Item createItem( DesignComponent designComponent );
    
    /**
     * Adds additional attribute like the label of the item. 
     * Subclasses should call super.parseAttributes(..) unless they ensure that they handle all attributes themselves.
     * 
     * @param designComponent the data of the displayable
     * @param item a J2ME Polish item
     */
    protected void addAttributes(  DesignComponent designComponent, Item item ) {
        if (!(item instanceof ChoiceItem)) {
            PropertyValue value = designComponent.readProperty( ItemCD.PROP_LABEL);
             if (value.getKind() == PropertyValue.Kind.VALUE) {
                String label = MidpTypes.getString(value);
                item.setLabel(label);
            }
        }
    }
    
    /**
     * Adds the style of the item. 
     * 
     * @param designComponent the data of the displayable
     * @param item a J2ME Polish item
     */
    protected void addStyle(  DesignComponent designComponent, Item item ) {
        //TODO read the style name from the designComponent and apply it to the item
        item.setStyle( new Style() );
    }
    
    /**
     * Parses the given DesignComponent and returns a new Displayable.
     * This method calls createItem(..) and the addAttributes(..).
     * 
     * @param designComponent the data of the displayable
     * @return a J2ME Polish displayable
     * @see #createItem( DesignComponent )
     * @see #addAttributes( DesignComponent, Displayable )
     */ 
    public Item parse( DesignComponent designComponent ) {
        Item item = createItem(designComponent);
        if (item == null) {
            throw new IllegalStateException("Unable to create item");
        }
        addAttributes(designComponent,item);
        addStyle(designComponent,item);
        return item;
    }
    
    
     protected Item getItem(  DesignComponent designComponent, Item item  ) {
         ItemParserManager itemManager = ItemParserManager.getInstance();
         return itemManager.parseItem(designComponent);
     }

}
