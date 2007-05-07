/*
 * RssBrowserItemParser.java
 * 
 * Created on Mar 28, 2007, 11:32:57 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.modelparser;

import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.plugin.netbeans.components.items.RssBrowserCD;
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
public class RssBrowserParser extends ItemParser {

    public RssBrowserParser() {
    }

    protected Item createItem(DesignComponent designComponent, ResourcesProvider resourcesProvider) {
        return new RssBrowser();
    }
    
    protected void addAttributes( DesignComponent designComponent, ResourcesProvider resourcesProvider, Item item ) {
        super.addAttributes(designComponent, resourcesProvider, item);
        
        RssBrowser browser = (RssBrowser) item;
        PropertyValue value =  designComponent.readProperty(RssBrowserCD.PROP_URL);
        if (value.getKind() == PropertyValue.Kind.VALUE) {   
            String url = MidpTypes.getString(value);
            browser.go( url );
        }

    }


}
