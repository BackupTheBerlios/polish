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
import de.enough.polish.plugin.netbeans.DisplayableParserManager;
import de.enough.polish.ui.TextBox;
import javax.microedition.lcdui.Displayable;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.components.displayables.TextBoxCD;

/**
 * <p>Parses TextBoxes</p>
 *
 * @author robertvirkus
 */
public class TextBoxParser extends DisplayableParser{
    
    static {
        //DisplayableParserManager.registerParser("javax.microedition.lcdui.TextBox", new TextBoxParser() );
        // does not work since the class is not loaded by default...
    }

    public TextBoxParser() {
    }

    protected Displayable createDisplayable(DesignComponent designComponent) {
         String string = MidpValueSupport.getHumanReadableString(designComponent.readProperty(TextBoxCD.PROP_STRING));
        PropertyValue value =  designComponent.readProperty(TextBoxCD.PROP_MAX_SIZE);
        int maxsize = 100;
        if (value.getKind() == PropertyValue.Kind.VALUE) {   
            maxsize = MidpTypes.getInteger(value);
        }
        int constraint = 0;
        value =  designComponent.readProperty(TextBoxCD.PROP_CONSTRAINTS);
        if (value.getKind() == PropertyValue.Kind.VALUE) {   
            constraint = MidpTypes.getInteger(value);                
        }

        return new TextBox (null, string, maxsize, constraint);
    }

}
