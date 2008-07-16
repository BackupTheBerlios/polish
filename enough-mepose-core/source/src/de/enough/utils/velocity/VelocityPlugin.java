package de.enough.utils.velocity;


import java.util.HashMap;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 16, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class VelocityPlugin extends Plugin{

    private HashMap resourceStore;
    private static VelocityPlugin plugin;
    private ServiceRegistration serviceRegistration;

    public VelocityPlugin() {
        VelocityPlugin.plugin = this;
    }
    
    public void start(BundleContext context) throws Exception {
        super.start(context);
        this.resourceStore = new HashMap();
        VelocityService templateService = new DefaultVelocityService(getResourceStore());
        this.serviceRegistration = context.registerService(VelocityService.class.getName(),templateService,null);
    }

    public void stop(BundleContext context) throws Exception {
        super.stop(context);

        this.serviceRegistration.unregister();
        this.resourceStore.clear();
    }

    public HashMap getResourceStore() {
        return this.resourceStore;
    }
    
    public static VelocityPlugin getInstance(){
        return plugin;
    }
}
