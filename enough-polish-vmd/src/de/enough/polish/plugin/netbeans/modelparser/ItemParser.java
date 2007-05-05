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
import de.enough.polish.plugin.netbeans.components.PolishPropertiesProcessor;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.resources.StyleProvider;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
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
    protected abstract Item createItem( DesignComponent designComponent, ResourcesProvider resourcesProvider );
    
    /**
     * Adds additional attribute like the label of the item. 
     * Subclasses should call super.parseAttributes(..) unless they ensure that they handle all attributes themselves.
     * 
     * @param designComponent the data of the displayable
     * @param item a J2ME Polish item
     */
    protected void addAttributes(  DesignComponent designComponent, ResourcesProvider resourcesProvider, Item item ) {
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
    protected void addStyle(  DesignComponent designComponent, ResourcesProvider resourcesProvider, Item item ) {
        try {
         PropertyValue styleValue = designComponent.readProperty( PolishPropertiesProcessor.PROP_STYLE );
            if (styleValue != null && styleValue.getKind() == PropertyValue.Kind.VALUE) {
                String styleName = MidpTypes.getString(styleValue);
                System.out.println("Found style [" + styleName + "] for " + item );
                StyleProvider styleProvider = resourcesProvider.getStyle(styleName);
                if (styleProvider != null) {
                    System.out.println("And found the style in resourcesProvider :D");
                    UiAccess.setStyle( item, styleProvider.getStyle() );
                } else {
                    System.out.println("style not registered in resourcesProvider :(");
                }
            } else {
                System.out.println("no style property found for " + item);
            }
        } catch (AssertionError e) {
            System.out.println("Warning: unable to read " + PolishPropertiesProcessor.PROP_STYLE);
            e.printStackTrace();
        }
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
    public Item parse( DesignComponent designComponent, ResourcesProvider resourcesProvider ) {
        Item item = createItem(designComponent, resourcesProvider);
        if (item == null) {
            throw new IllegalStateException("Unable to create item");
        }
        item.setAttribute("DesignComponent", designComponent );
        addAttributes(designComponent, resourcesProvider,item);
        addStyle(designComponent, resourcesProvider,item);
        return item;
    }
    
    
     protected Item getItem(  DesignComponent designComponent, ResourcesProvider resourcesProvider, Item item  ) {
         ItemParserManager itemManager = ItemParserManager.getInstance();
         return itemManager.parseItem(designComponent, resourcesProvider);
     }

}
