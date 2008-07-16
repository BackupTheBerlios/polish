/*
 * Created on Jun 22, 2006 at 12:05:53 PM.
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
package de.enough.mepose.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.enough.encogen.java.ClazzElement;
import de.enough.encogen.java.CommentStatement;
import de.enough.encogen.java.ImportElement;
import de.enough.encogen.java.MethodElement;
import de.enough.encogen.java.PackageElement;
import de.enough.encogen.java.Rectangle;
import de.enough.mepose.ui.MeposeUIPlugin;

public class TemplateGenerator{
    private static final String SOURCE_FOLDER = "/source/src";
    private NewProjectModel model;
    public TemplateGenerator(NewProjectModel model) {
        this.model = model;
    }
    public void setModel(NewProjectModel model) {
        if(model == null){
            throw new IllegalArgumentException("setModel(...):parameter 'model' is null contrary to API.");
        }
        this.model = model;
    }
    public IFile[] getFiles() {
        
        String result = generateSimpleMidlet();
        
        String packageString = this.model.getTemplatePackageString();
        packageString = packageString.replace('.','/');
        // Thats a nice behavior. substring with
        packageString = packageString.startsWith("/")?packageString.substring(1,packageString.length()):packageString;
        packageString = packageString.endsWith("/")?packageString.substring(0,packageString.length()-1):packageString;
        String clazzString = this.model.getTemplateClazzString();
        String pathString = SOURCE_FOLDER+"/"+packageString+"/"+clazzString+".java";
        IProject project = this.model.getProject();
        IFile simpleMidletFile = project.getFile(pathString);
        
        StringBuffer pathSoFar = new StringBuffer(SOURCE_FOLDER);
        String[] packageParts = packageString.split("/");
        IFolder pathElementSoFar;
        try {
            // Generate all folders for the package.
            for (int i = 0; i < packageParts.length; i++) {
                pathSoFar.append("/");
                pathSoFar.append(packageParts[i]);
                pathElementSoFar = project.getFolder(pathSoFar.toString());
                pathElementSoFar.create(true,true,null);
            }
            
            // Generate file.
            byte[] bytes = result.getBytes("ISO-8859-1");
            ByteArrayInputStream stringInputStream = new ByteArrayInputStream(bytes);
            simpleMidletFile.create(stringInputStream,true,null);
        } catch (CoreException exception) {
            MeposeUIPlugin.log("Could not create template files.",exception);
            return new IFile[] {};
        } catch (UnsupportedEncodingException exception) {
            MeposeUIPlugin.log("Could not encode files.",exception);
            return new IFile[] {};
        }
        return new IFile[] {simpleMidletFile};
    }
    
    private String generateSimpleMidlet() {
        
        String packageString = this.model.getTemplatePackageString();
        
        PackageElement packageElement = new PackageElement(packageString);
        ImportElement importStatement1 = new ImportElement("javax.microedition.midlet.MIDlet");
        ImportElement importStatement2 = new ImportElement("javax.microedition.midlet.MIDletStateChangeException");
        
        CommentStatement commentStatement = new CommentStatement("TODO: Implement this method.");

        MethodElement methodStartApp = new MethodElement("startApp","void",null,"protected",new String[] {"MIDletStateChangeException"});
        methodStartApp.addStatement(commentStatement);
        
        MethodElement methodPauseApp = new MethodElement("pauseApp","void",null,"protected",null);
        methodPauseApp.addStatement(commentStatement);
        
        MethodElement methodDestroyApp = new MethodElement("destroyApp","void",new String[] {"boolean unconditional"},"protected",new String[] {"MIDletStateChangeException"});
        methodDestroyApp.addStatement(commentStatement);
        
        ClazzElement clazzElement = new ClazzElement();
        clazzElement.setParent("MIDlet");
        clazzElement.setClazzName(this.model.getTemplateClazzString());
        clazzElement.setPackageElement(packageElement);
        clazzElement.addImportStatement(importStatement1);
        clazzElement.addImportStatement(importStatement2);
        clazzElement.addMethod(methodStartApp);
        clazzElement.addMethod(methodPauseApp);
        clazzElement.addMethod(methodDestroyApp);
        
        Rectangle rectangle = new Rectangle(0,0,1,1);
        
        String result = clazzElement.print(rectangle);
        return result;
    }
    public void generate() {
        getFiles();
    }
}