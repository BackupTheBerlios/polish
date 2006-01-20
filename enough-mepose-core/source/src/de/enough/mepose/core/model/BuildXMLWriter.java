/*
 * Created on Dec 16, 2005 at 4:43:26 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.mepose.core.model;

import java.io.File;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.plugin.VelocityService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.enough.mepose.core.CorePlugin;
import de.enough.mepose.core.MeposeCoreConstants;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 16, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class BuildXMLWriter {

    private MeposeModel meposeModel;
    private ServiceReference sr;
    
    public BuildXMLWriter(MeposeModel meposeModel) {
        this.meposeModel = meposeModel;
    }
    
    public void writeBuildXML(Writer os) {
        try {
            VelocityService velocityService = getVelocityService();
            
            if(velocityService == null) {
                return;
            }
            URL resource = getBundleContext().getBundle().getEntry(MeposeCoreConstants.PATH_BUILD_XML_TEMPLATE);
            if(resource == null) {
                throw new IllegalStateException("No URL for build template found.");
            }
            InputStream buildxmlAsStream = resource.openStream();
            if(resource == null) {
                throw new IllegalStateException("No build template found.");
            }
            velocityService.setTemplate(MeposeCoreConstants.ID_TEMPLATE_NAME,buildxmlAsStream);
            
            VelocityEngine velocityEngine = velocityService.getNewVelocityEngine();
            velocityEngine.init();
            
            VelocityContext context = new VelocityContext();
            fillContext(context);
            
            Template template =  null;

            try 
            {
                template = velocityEngine.getTemplate(MeposeCoreConstants.ID_TEMPLATE_NAME);
            }
            catch( ResourceNotFoundException rnfe )
            {
                System.out.println("Example : error : cannot find template " + MeposeCoreConstants.ID_TEMPLATE_NAME );
            }
            catch( ParseErrorException pee )
            {
                System.out.println("Example : Syntax error in template " + MeposeCoreConstants.ID_TEMPLATE_NAME + ":" + pee );
            }

            if ( template != null)
                template.merge(context, os);

            os.flush();
            ungetVelocityService();
        }
        catch( Exception e )
        {
            System.out.println(e);
        }
    }

    private VelocityService getVelocityService() {
        BundleContext bundleContext = getBundleContext();
        this.sr = bundleContext.getServiceReference(VelocityService.class.getName());
        return (VelocityService)bundleContext.getService(this.sr);
    }

    private void ungetVelocityService() {
        if(this.sr == null) {
            return;
        }
        getBundleContext().ungetService(this.sr);
    }
    /**
     * @param context
     */
    private void fillContext(VelocityContext context) {
        File polishHome = (File)this.meposeModel.getPropertyValue(MeposeModel.ID_POLISH_HOME);
        File mppHome = (File)this.meposeModel.getPropertyValue(MeposeModel.ID_MPP_HOME);
        File nokiaHome = this.meposeModel.getNokiaHome();
        String projectDescription = this.meposeModel.getProjectDescription();
        
        context.put("polishHome",polishHome.getAbsolutePath());
        context.put("mppHome",mppHome.getAbsolutePath());
        context.put("projectDescription",projectDescription);
        context.put("deviceList",this.meposeModel.getSupportedDevices());
        String temp = null;
        if(nokiaHome.exists()) {
            temp = nokiaHome.getAbsolutePath();
            if(temp == null || temp.length() <= 0) {
                temp = null;
            }
        }
        context.put("nokiaHome",temp);
    }
    
    public BundleContext getBundleContext() {
        return CorePlugin.getDefault().getBundleContext();
    }
}
