package de.enough.utils.velocity;


import java.io.InputStream;
import java.util.HashMap;

import org.apache.velocity.app.VelocityEngine;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 16, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class DefaultVelocityService implements VelocityService{

    private HashMap resourceStore;

    // TODO: This is heavywight as all resources are in the service. The service
    // should be light to be able to be transferred via wire.
    public DefaultVelocityService(HashMap resourceStore) {
        this.resourceStore = resourceStore;
    }
    
    public void setTemplate(String name, InputStream resource) {
        this.resourceStore.put(name,resource);
    }

    /*
     * @see org.apache.velocity.plugin.VelocityService#getNewVelocityEngine()
     */
    public VelocityEngine getNewVelocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        
        velocityEngine.setProperty("resource.loader","eclipse");
        velocityEngine.setProperty("eclipse.resource.loader.class","org.apache.velocity.plugin.EclipseResourceLoader");
        
        return velocityEngine;
    }

}
