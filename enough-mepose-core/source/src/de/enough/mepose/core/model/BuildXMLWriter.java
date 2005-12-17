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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.plugin.TemplateService;
import org.apache.velocity.plugin.VelocityPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.enough.mepose.core.CorePlugin;

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

    public static final String TEMPLATE_NAME = "build.xml.template";
    
    public void writeBuildXML(Writer os) {
        try {
            /*
             * setup
             */
            // It is important that this is called first to start the Velocity plugin.
            System.out.println("DEBUG:BuildXMLWriter.writeBuildXML(...):velocity plugin:"+VelocityPlugin.getInstance());
            BundleContext bundleContext = CorePlugin.getDefault().getBundleContext();
            ServiceReference sr = bundleContext.getServiceReference(TemplateService.class.getName());
            TemplateService templateService = (TemplateService)bundleContext.getService(sr);
            InputStream buildxmlAsStream = bundleContext.getBundle().getResource("/templates/build.xml.template").openStream();
            templateService.setTemplate(TEMPLATE_NAME,buildxmlAsStream);
            
//            System.out.println("DEBUG:BuildXMLWriter.writeBuildXML(...):classpath:"+System.getProperties().toString());
//            Velocity.init("velocity.properties");
            Velocity.setProperty("resource.loader","eclipse");
            Velocity.setProperty("eclipse.resource.loader.class","org.apache.velocity.plugin.EclipseResourceLoader");
            Velocity.init();
            
            /*
             *  Make a context object and populate with the data.  This 
             *  is where the Velocity engine gets the data to resolve the
             *  references (ex. $list) in the template
             */

            VelocityContext context = new VelocityContext();
            context.put("list", getNames());
            
//            BundleContext bundleContext = CorePlugin.getDefault().getBundleContext();
//            ServiceReference sr = bundleContext.getServiceReference(TemplateService.class.getName());
//            TemplateService templateService = (TemplateService)bundleContext.getService(sr);
//            InputStream buildxmlAsStream = bundleContext.getBundle().getResource("/templates/build.xml.template").openStream();
//            templateService.setTemplate(TEMPLATE_NAME,buildxmlAsStream);
            
            /*
             *  get the Template object.  This is the parsed version of your 
             *  template input file.  Note that getTemplate() can throw
             *   ResourceNotFoundException : if it doesn't find the template
             *   ParseErrorException : if there is something wrong with the VTL
             *   Exception : if something else goes wrong (this is generally
             *        indicative of as serious problem...)
             */

            Template template =  null;

            try 
            {
                template = Velocity.getTemplate(TEMPLATE_NAME);
            }
            catch( ResourceNotFoundException rnfe )
            {
                System.out.println("Example : error : cannot find template " + TEMPLATE_NAME );
            }
            catch( ParseErrorException pee )
            {
                System.out.println("Example : Syntax error in template " + TEMPLATE_NAME + ":" + pee );
            }

            /*
             *  Now have the template engine process your template using the
             *  data placed into the context.  Think of it as a  'merge' 
             *  of the template and the data to produce the output stream.
             */

//            BufferedWriter writer = writer = new BufferedWriter(
//                new OutputStreamWriter(System.out));

            if ( template != null)
                template.merge(context, os);

            os.flush();
            
            /*
             *  flush and cleanup
             */

//            writer.flush();
//            writer.close();
        }
        catch( Exception e )
        {
            System.out.println(e);
        }
    }
    public ArrayList getNames(){
        ArrayList list = new ArrayList();

        list.add("ArrayList element 1");
        list.add("ArrayList element 2");
        list.add("ArrayList element 3");
        list.add("ArrayList element 4");

        return list;
    }
}
