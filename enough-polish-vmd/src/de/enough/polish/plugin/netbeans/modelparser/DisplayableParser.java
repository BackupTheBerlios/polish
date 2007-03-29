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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.components.commands.CommandCD;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;
import org.netbeans.modules.vmd.midp.components.sources.CommandEventSourceCD;

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
        // setting title:
        String title = MidpValueSupport.getHumanReadableString(designComponent.readProperty(DisplayableCD.PROP_TITLE));
        displayable.setTitle(title);
        
        // setting commands:
        PropertyValue value =  designComponent.readProperty(DisplayableCD.PROP_COMMANDS);
        if (value.getKind() == PropertyValue.Kind.ARRAY) {
            for (PropertyValue commandValue:value.getArray() )  {
                DesignComponent commandComponent = commandValue.getComponent();
                //System.out.println(  "commandComponent == designComponent: " + (commandComponent == designComponent) );
                addCommand( commandComponent, displayable );
            }
        }
        
    }
    
    protected void addCommand( DesignComponent commandComponent, Displayable displayable ) {
        Command command = getCommand( commandComponent, displayable );
        if (command != null) {
            //System.out.println("adding command " + command.getLabel() + " to screen " + displayable );
            displayable.addCommand(command);
        }
    }
    
    protected Command getCommand( DesignComponent commandComponent, Displayable displayable ) {
        PropertyValue commandValue = null;
        if (commandComponent.getType().equals( CommandEventSourceCD.TYPEID) ) {
            //System.out.println("found CommandEventSourceCD: " + commandComponent.getType() );
            commandValue = commandComponent.readProperty( CommandEventSourceCD.PROP_COMMAND );
            commandComponent = commandValue.getComponent();
        }
        if (commandComponent.getType().equals( CommandCD.TYPEID) ) {
            String label = null;
            String longLabel = null;
            int priority = Integer.MAX_VALUE;
            int type = Command.SCREEN;
            PropertyValue value =  commandComponent.readProperty( CommandCD.PROP_LABEL );
            if (value.getKind() == PropertyValue.Kind.VALUE) {
                label = MidpTypes.getString(value);
            }
            value =  commandComponent.readProperty( CommandCD.PROP_LONG_LABEL );
            if (value.getKind() == PropertyValue.Kind.VALUE) {
                longLabel = MidpTypes.getString(value);
            }
            value =  commandComponent.readProperty( CommandCD.PROP_PRIORITY );
            if (value.getKind() == PropertyValue.Kind.VALUE) {
                priority = MidpTypes.getInteger(value);
            }
            value =  commandComponent.readProperty( CommandCD.PROP_TYPE );
            if (value.getKind() == PropertyValue.Kind.VALUE) {
                type = MidpTypes.getInteger(value);
            }
            return new Command( label, longLabel, type, priority );
        } else {
            System.out.println("no commandCD: " + commandComponent.getType() );
            return null;
        }
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
