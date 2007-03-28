/*
 * DisplayableParser.java
 * 
 * Created on Mar 27, 2007, 11:17:50 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.plugin.netbeans.ItemParserManager;
import de.enough.polish.ui.Item;
import java.util.Collection;
import javax.microedition.lcdui.Displayable;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;

/**
 * <p>Parses screens from DesignComponents</p>
 *
 * @author robertvirkus
 */
public abstract class DisplayableParser {

    public DisplayableParser() {
    }
    
    /**
     * Parses the given DesignComponent to create a new Displayable.
     * 
     * @param designComponent the data of the displayable
     * @return a J2ME Polish displayable
     */ 
    protected abstract Displayable createDisplayable( DesignComponent designComponent );
    
    /**
     * Adds additional attribute like the title of the screen. 
     * Subclasses should call super.parseAttributes(..) unless they ensure that they handle all attributes themselves.
     * 
     * @param designComponent the data of the displayable
     * @param displayable a J2ME Polish displayable
     */
    protected void addAttributes(  DesignComponent designComponent, Displayable displayable ) {
        String title = MidpValueSupport.getHumanReadableString(designComponent.readProperty(DisplayableCD.PROP_TITLE));
        displayable.setTitle(title);
    }
    
     protected Item getItem(  DesignComponent designComponent, Displayable displayable ) {
         ItemParserManager itemManager = ItemParserManager.getInstance();
         return itemManager.parseItem(designComponent);
     }
    
    /**
     * Parses the given DesignComponent and returns a new Displayable.
     * This method calls createDisplayable(..) and then addAttributes(..).
     * 
     * @param designComponent the data of the displayable
     * @return a J2ME Polish displayable
     * @see #createDisplayable( DesignComponent )
     * @see #addAttributes( DesignComponent, Displayable )
     */ 
    public Displayable parse( DesignComponent designComponent ) {
        Displayable displayable = createDisplayable(designComponent);
        if (displayable == null) {
            throw new IllegalStateException("Unable to create displayable");
        }
        addAttributes(designComponent, displayable);
        return displayable;
    }

}
