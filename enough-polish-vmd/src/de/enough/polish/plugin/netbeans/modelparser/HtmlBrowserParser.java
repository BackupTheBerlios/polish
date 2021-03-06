/*
 * BrowserItemParser.java
 * 
 * Created on Mar 28, 2007, 11:32:57 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.plugin.netbeans.components.items.BrowserCD;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.ui.Item;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpTypes;

/**
 * <p>Parses the model of the BrowserCD</p>
 *
 * @author robertvirkus
 */
public class HtmlBrowserParser extends ItemParser {

    public HtmlBrowserParser() {
    }

    protected Item createItem(DesignComponent designComponent, ResourcesProvider resourcesProvider) {
        return new HtmlBrowser();
    }
    
    protected void addAttributes( DesignComponent designComponent, ResourcesProvider resourcesProvider, Item item ) {
        super.addAttributes(designComponent, resourcesProvider, item);
        
        HtmlBrowser browser = (HtmlBrowser) item;
        PropertyValue value =  designComponent.readProperty(BrowserCD.PROP_URL);
        if (value.getKind() == PropertyValue.Kind.VALUE) {   
            String url = MidpTypes.getString(value);
            browser.go( url );
        }

    }


}
