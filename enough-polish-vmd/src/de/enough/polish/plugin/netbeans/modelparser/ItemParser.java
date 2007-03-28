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
import javax.microedition.lcdui.Displayable;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;

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
        //String title = MidpValueSupport.getHumanReadableString(designComponent.readProperty(DisplayableCD.PROP_TITLE));
        //displayable.setTitle(title);
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
        return item;
    }

}
