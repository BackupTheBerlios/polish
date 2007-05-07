/*
 * FormParser.java
 * 
 * Created on Mar 28, 2007, 11:20:57 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.ui.Alert;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import javax.microedition.lcdui.Displayable;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.displayables.AlertCD;
import org.netbeans.modules.vmd.midp.components.displayables.FormCD;

/**
 * <p>Parses Forms</p>
 *
 * @author robertvirkus
 */
public class AlertParser extends DisplayableParser {

    public AlertParser() {
    }

    protected Displayable createDisplayable(DesignComponent designComponent, ResourcesProvider resourcesProvider) {
        return new Alert(null);
    }
    
    protected void addAttributes( DesignComponent designComponent, ResourcesProvider resourcesProvider, Displayable displayable ) {
        super.addAttributes(designComponent, resourcesProvider, displayable);
        Alert alert = (Alert) displayable;
        PropertyValue value =  designComponent.readProperty( AlertCD.PROP_STRING );
        String text = MidpTypes.getString(value);
        alert.setString(text);
        //TODO add support for timeout etc
        /*
 Name 	 TypeID 	 Default Value 	 IsNull 	 IsUserCode 	 Values 	 Is basic 	 Is advanced 	 Version
timeout 	int 	vary across implementation -> null 	no 	yes 	FOREVER (-2) or any positive value (time in millis) 	no 	yes 	MIDP
image 	ImageCD 	null 	yes 	yes 		no 	yes 	MIDP
indicator 	GaugeCD 	null (the gauge component is under the alert) 	yes 	yes 	the special "under" Gauge or null 	no 	yes 	MIDP-2.0
string 	String 	null 	yes 	yes 		yes 	yes 	MIDP
type 	AlertCD.TYPEID_ALERT_TYPE 	null 	yes 	yes 	null (no specific type), ALARM, CONFIRMATION, ERROR, INFO, WARNING 	yes 	yes 	MIDP
         * 
         */
    }

}
