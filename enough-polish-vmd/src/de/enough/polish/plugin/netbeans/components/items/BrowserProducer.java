/*
 * BrowserProducer.java
 * 
 * Created on Mar 28, 2007, 3:35:12 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.components.items;

import org.netbeans.modules.vmd.api.model.ComponentProducer;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.api.model.PaletteDescriptor;
import org.netbeans.modules.vmd.midp.palette.MidpPaletteProvider;


/**
 * <p>Generates Browser instances</p>
 *
 * @author robertvirkus
 */
public class BrowserProducer extends ComponentProducer {

    public BrowserProducer() {
        super( BrowserCD.TYPEID.toString(), BrowserCD.TYPEID, 
                new PaletteDescriptor (MidpPaletteProvider.CATEGORY_ITEMS, "HTML Browser", "Displays HTML based content.", BrowserCD.ICON_PATH, BrowserCD.ICON_PATH ) // NOI18N
        );
    }

    public Result createComponent(DesignDocument document) {
        DesignComponent createdComponent = document.createComponent(BrowserCD.TYPEID);
        return new Result (createdComponent);
    }

    public boolean checkValidity(DesignDocument document) {
        return true;
        // later, you can use:
        // return MidpJavaSupport.checkValidity(document, "mylibrary.MyForm"); // NOI18N
        // check whether your class is on the classpath of your project
    }

}
