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
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.Action;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.DataEditorViewLookupFactory;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.ExTransferable;

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
            final Creator creator = item.lookup(Creator.class);
            if (creator == null)
                return;
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
