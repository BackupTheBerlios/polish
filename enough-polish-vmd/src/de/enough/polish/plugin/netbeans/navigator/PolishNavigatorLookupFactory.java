/*
 * PolishNavigatorLookupFactory.java
 *
 * Created on March 28, 2007, 8:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.navigator;

import de.enough.polish.plugin.netbeans.PolishDataEditorView;
import java.util.Arrays;
import java.util.Collection;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.DataEditorViewLookupFactory;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.spi.navigator.NavigatorLookupHint;

/**
 *
 * @author dave
 */
public class PolishNavigatorLookupFactory implements DataEditorViewLookupFactory {
    
    public Collection<? extends Object> getLookupObjects(DataObjectContext context, DataEditorView view) {
        if (view instanceof PolishDataEditorView)
            return Arrays.asList(new NavigatorLookupHint() {
                public String getContentType() {
                    return "polish-styles"; // NOI18N
                }
            });
        return null;
    }

}
