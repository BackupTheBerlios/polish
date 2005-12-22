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

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.plugin.VelocityService;
import org.apache.velocity.plugin.VelocityPlugin;
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
    
    public BuildXMLWriter(MeposeModel meposeModel) {
        this.meposeModel = meposeModel;
    }
    
    public void writeBuildXML(Writer os) {
        try {
            VelocityService velocityService = getVelocityService();
            
            if(velocityService == null) {
                return;
            }
            
            InputStream buildxmlAsStream = getBundleContext().getBundle().getResource(MeposeCoreConstants.PATH_BUILD_XML_TEMPLATE).openStream();
            
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
        }
        catch( Exception e )
        {
            System.out.println(e);
        }
    }

    private VelocityService getVelocityService() {
        BundleContext bundleContext = getBundleContext();
        ServiceReference sr = bundleContext.getServiceReference(VelocityService.class.getName());
        return (VelocityService)bundleContext.getService(sr);
    }

    /**
     * @param context
     */
    private void fillContext(VelocityContext context) {
//      context.put("wtkHome","WTKBLAAA");
//      context.put("polishHome","PolishBBB");
        context.put("wtk.home",this.meposeModel.getWTKHome().toString());
        context.put("polish.home",this.meposeModel.getPolishHome().toString());
    }
    
    public BundleContext getBundleContext() {
        return CorePlugin.getDefault().getBundleContext();
    }
}
