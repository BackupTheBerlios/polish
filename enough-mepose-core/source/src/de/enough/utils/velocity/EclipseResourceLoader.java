package de.enough.utils.velocity;


import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 16, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class EclipseResourceLoader extends ResourceLoader {

    private HashMap resourceStore;
    
    public void init(ExtendedProperties configuration) {
        this.resourceStore = VelocityPlugin.getInstance().getResourceStore();
    }

    public InputStream getResourceStream(String source)
                                                       throws ResourceNotFoundException {
        return (InputStream)this.resourceStore.get(source);
    }

    public boolean isSourceModified(Resource resource) {
        return false;
    }

    public long getLastModified(Resource resource) {
        return 0;
    }

}
