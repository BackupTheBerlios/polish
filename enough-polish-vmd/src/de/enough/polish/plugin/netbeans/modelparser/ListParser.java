/*
 * TextBoxParser.java
 * 
 * Created on Mar 27, 2007, 11:32:18 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.plugin.netbeans.modelparser.DisplayableParser;
import de.enough.polish.plugin.netbeans.modelparser.DisplayableParser;
import de.enough.polish.ui.ChoiceItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.List;
import de.enough.polish.ui.TextBox;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.components.commands.CommandCD;
import org.netbeans.modules.vmd.midp.components.commands.ListSelectCommandCD;
import org.netbeans.modules.vmd.midp.components.displayables.ListCD;
import org.netbeans.modules.vmd.midp.components.displayables.TextBoxCD;
import org.netbeans.modules.vmd.midp.components.sources.ListSelectCommandEventSourceCD;

/**
 * <p>Parses Lists</p>
 *
 * @author robertvirkus
 */
public class ListParser extends DisplayableParser{

    public ListParser() {
    }

    protected Displayable createDisplayable(DesignComponent designComponent) {
        int type = List.IMPLICIT;
        PropertyValue value =  designComponent.readProperty(ListCD.PROP_LIST_TYPE);
        if (value.getKind() == PropertyValue.Kind.VALUE) {   
            type = MidpTypes.getInteger(value);
        }
        return new List (null, type );
    }
    
    protected void addAttributes(  DesignComponent designComponent, Displayable displayable ) {
        super.addAttributes(designComponent, displayable);
        
        List list = (List) displayable;
        PropertyValue value =  designComponent.readProperty(ListCD.PROP_ELEMENTS);
        if (value.getKind() == PropertyValue.Kind.ARRAY) {
            for (PropertyValue itemValue:value.getArray() )  {
                //System.out.println("ChoiceItem value=" + itemValue.getComponent() );
                Item item = getItem( itemValue.getComponent(), displayable );
                if (item instanceof ChoiceItem) {
                    ChoiceItem choiceItem = (ChoiceItem) item ;
                    list.append( choiceItem );
                //} else {
                //    System.out.println("warning: unable to get ChoiceItem from " + itemValue.getComponent() );
                }
                        
            }
        }
        
        // add select command:
        value = designComponent.readProperty( ListCD.PROP_SELECT_COMMAND);
        if (value.getKind() == PropertyValue.Kind.VALUE && value.getType() == ListSelectCommandEventSourceCD.TYPEID) {
          //ListSelectCommandCD cd;  
        }
        Command selectCommand = getCommand( value.getComponent(), list );
        
        System.out.println("select command: value.type=" + value.getType() + ", command=" + selectCommand );
        if (value.getKind() == PropertyValue.Kind.VALUE) {
            //CommandCD commandCD = (CommandCD) value.getPrimitiveValue();
            //Command command = getCommand(   
        }
    }


}
