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
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.MeposeConstants;
import de.enough.utils.velocity.DefaultVelocityService;
import de.enough.utils.velocity.VelocityService;

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

            VelocityEngine velocityEngine = new VelocityEngine();
            URL resource = getBundleContext().getBundle().getEntry(MeposeConstants.PATH_BUILD_XML_TEMPLATE);
            if(resource == null) {
                throw new IllegalStateException("No URL for build template found.");
            }
            InputStream buildxmlAsStream = resource.openStream();
            if(resource == null) {
                throw new IllegalStateException("No build template found.");
            }
//            velocityService.setTemplate(MeposeConstants.ID_TEMPLATE_NAME,buildxmlAsStream);
            
            velocityEngine.init();
            
            VelocityContext context = new VelocityContext();
            fillContext(context);
            
            Template template =  null;

            try 
            {
                template = velocityEngine.getTemplate(MeposeConstants.ID_TEMPLATE_NAME);
            }
            catch( ResourceNotFoundException rnfe )
            {
                MeposePlugin.log("Could not find template file:"+MeposeConstants.ID_TEMPLATE_NAME);
            }
            catch( ParseErrorException parseErrorException )
            {
                MeposePlugin.log("Syntax error in template " + MeposeConstants.ID_TEMPLATE_NAME + ":" + parseErrorException);
            }

            if ( template != null)
                template.merge(context, os);

            os.flush();
            ungetVelocityService();
        }
        catch( Exception e )
        {
            MeposePlugin.log("Could not write build.xml file.",e);
        }
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
    protected void fillContext(VelocityContext context) {
//        File polishHome = (File)this.meposeModel.getPropertyValue(MeposeModel.ID_POLISH_HOME);
//        File mppHome = (File)this.meposeModel.getPropertyValue(MeposeModel.ID_MPP_HOME);
        File polishHome = this.meposeModel.getPolishHome();
       
//        String projectDescription = this.meposeModel.getProjectDescription();
        
        context.put("polishHome",polishHome.getAbsolutePath());
//        context.put("projectDescription",projectDescription);
        //FIXME: remodel without polish classes.
//        context.put("deviceList",this.meposeModel.getSupportedDevices());
        
//        addPath(context,"nokiaHome",this.meposeModel.getNokiaHome());
//        addPath(context,"sonyHome",this.meposeModel.getSonyHome());
//        addPath(context,"motorolaHome",this.meposeModel.getMotorolaHome());
//        addPath(context,"wtkHome",this.meposeModel.getWTKHome());
//        addPath(context,"mppHome",this.meposeModel.getMppHome());
//        addPath(context,"siemensHome",this.meposeModel.getSiemensHome());
        
    }

    private void addPath(VelocityContext context,String propertyName,File path) {
        String pathString;
        if(path != null && path.exists()) {
            pathString = path.getAbsolutePath();
            context.put(propertyName,pathString);
        }
    }
    
    public BundleContext getBundleContext() {
        return MeposePlugin.getDefault().getBundleContext();
    }
}
