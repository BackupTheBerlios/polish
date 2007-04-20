/*
 * PolishDataEditorViewLookupFactory.java
 *
 * Created on March 28, 2007, 3:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.palette;

import de.enough.polish.plugin.netbeans.PolishDataEditorView;
import de.enough.polish.runtime.Simulation;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.DataEditorViewLookupFactory;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.cookies.InstanceCookie;
import org.openide.loaders.InstanceDataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;

import javax.swing.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author dave
 */
public class PolishDataEditorViewLookupFactory implements DataEditorViewLookupFactory {
    
    public Collection<? extends Object> getLookupObjects(DataObjectContext context, DataEditorView view) {
        try {
            if (view instanceof PolishDataEditorView)
                return Arrays.asList(PaletteFactory.createPalette("j2mepolish/palette", new PolishPaletteActions(), null, new PolishDragAndDropHandler()));
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
        return null;
    }

    public Collection<? extends Lookup> getLookups (DataObjectContext context, DataEditorView view) {
        return Collections.emptySet ();
    }

    private static class PolishPaletteActions extends PaletteActions {
    
        public Action[] getImportActions() {
            return null; // TODO
        }

        public Action[] getCustomPaletteActions() {
            return null; // TODO
        }

        public Action[] getCustomCategoryActions(Lookup category) {
            return null; // TODO
        }

        public Action[] getCustomItemActions(Lookup item) {
            return null; // TODO
        }

        public Action getPreferredAction(Lookup item) {
            return null; // TODO
        }

    }
    
    private static class PolishDragAndDropHandler extends DragAndDropHandler {
        
        public void customize(ExTransferable t, Lookup item) {
            InstanceDataObject dataObject = item.lookup(InstanceDataObject.class);
            if (dataObject == null)
                return;
            InstanceCookie instanceCookie = dataObject.getCookie(InstanceCookie.class);
            if (instanceCookie == null)
                return;
            Object o;
            try {
                o = instanceCookie.instanceCreate();
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
                return;
            }
            if (! (o instanceof Creator))
                return;
            final Creator creator = (Creator) o;
            t.put(new ExTransferable.Single (Simulation.BACKGROUND_DATA_FLAVOR) {
                protected Object getData() throws IOException, UnsupportedFlavorException {
                    return creator.createBackground();
                }
            });
            t.put(new ExTransferable.Single (Simulation.BORDER_DATA_FLAVOR) {
                protected Object getData() throws IOException, UnsupportedFlavorException {
                    return creator.createBorder();
                }
            });
        }

    }

}
