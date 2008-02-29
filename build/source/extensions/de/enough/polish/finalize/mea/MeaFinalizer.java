/*
 * Created on Feb 8, 2008 at 12:01:12 PM.
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
package de.enough.polish.finalize.mea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.finalize.Finalizer;
import de.enough.polish.util.FileUtil;

/**
 * 
 * <br>Copyright Enough Software 2005-2007
 * <pre>
 * history
 *        Feb 8, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class MeaFinalizer extends Finalizer{

    private static class Entry{
        private File jadFile;
        private File jarFile;
        private Device device;
        private Locale locale;
        private String croppedJadFilename;
        private String croppedJarFilename;
        public Entry(File jadFile, File jarFile, Device device,Locale locale, String croppedJadFilename,String croppedJarFilename) {
            this.jadFile = jadFile;
            this.jarFile = jarFile;
            this.device = device;
            this.locale = locale;
            this.croppedJadFilename = croppedJadFilename;
            this.croppedJarFilename = croppedJarFilename;
        }
        public Device getDevice() {
            return this.device;
        }
        public File getJadFile() {
            return this.jadFile;
        }
        public File getJarFile() {
            return this.jarFile;
        }
        public Locale getLocale() {
            return this.locale;
        }
        public String getCroppedJadFilename() {
            return this.croppedJadFilename;
        }
        public String getCroppedJarFilename() {
            return this.croppedJarFilename;
        }
    }

    public MeaFinalizer() {
        super();
    }
    
    private static List entries = new ArrayList();
    private String fallbackDevice;
    private String tags;
    private String access = "owner";
    
    public void finalize(File jadFile, File jarFile, Device device, Locale locale, Environment env) {
        File distFile = env.getBuildSetting().getDestDir(env);
        String distFilePath = distFile.getPath();
        String croppedJadFilePath = jadFile.getPath().replaceFirst(distFilePath+"/","");
        String croppedJarFilePath = jarFile.getPath().replaceFirst(distFilePath+"/","");
        Entry entry = new Entry(jadFile,jarFile,device,locale,croppedJadFilePath,croppedJarFilePath);
        entries.add(entry);
    }

    public void notifyBuildEnd(Environment env) {
        super.notifyBuildEnd(env);
        String description = env.getVariable("MIDlet-Description");
        description = description.replaceAll("\"","\\\"");
        String name = env.getVariable("MIDlet-Name");
        String version = env.getVariable("MIDlet-Version");
        String vendor = env.getVariable("MIDlet-Vendor");
        
        // Write contents.xml file.

        FileWriter fileWriter = null;
        File contentsXmlFile = null;
        File distFile = env.getBuildSetting().getDestDir(env);
        File meaFile = new File(distFile,name+".mea");
        System.out.println("Creating file '"+meaFile.getName()+"'.");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(meaFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            
            contentsXmlFile = File.createTempFile("contentxml","");
            fileWriter = new FileWriter(contentsXmlFile);
            String lineSeperator = System.getProperty("line.separator");
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+lineSeperator);
            fileWriter.write("<contents version=\"1.0.0\" lang=\"en\">"+lineSeperator);
            fileWriter.write("<application name=\"");
            fileWriter.write(name);
            fileWriter.write("\" description=\"");
            fileWriter.write(description);
            fileWriter.write("\" access=\""+this.access+"\" fallbackDevice=\"");
            if(this.fallbackDevice == null) {
                this.fallbackDevice = "Generic/Midp2Cldc11";
            }
            fileWriter.write(this.fallbackDevice);
            fileWriter.write("\" version=\"");
            fileWriter.write(version);
            if(this.tags != null && this.tags.length() > 0) {
                fileWriter.write("\" tags=\"");
                fileWriter.write(this.tags);
            }
            fileWriter.write("\" vendor=\"");
            fileWriter.write(vendor);
            fileWriter.write("\">"+lineSeperator);
            
            for (Iterator iterator = entries.iterator(); iterator.hasNext(); ) {
                Entry entry = (Entry) iterator.next();
                
                // Preserve possible subdirectories but make the path as short as possible to put into the zip
                String croppedJadFilename = entry.getCroppedJadFilename();
                String croppedJarFilename = entry.getCroppedJarFilename();
                fileWriter.write("<javaMeBundle>"+lineSeperator);
                fileWriter.write("<supportedDevices>"+lineSeperator);
                fileWriter.write("<supportedDevice>");
                fileWriter.write(entry.getDevice().toString());
                fileWriter.write("</supportedDevice>"+lineSeperator);
                fileWriter.write("</supportedDevices>"+lineSeperator);
                fileWriter.write("<data filename=\""+croppedJadFilename+"\" />"+lineSeperator);
                fileWriter.write("<data filename=\""+croppedJarFilename+"\" />"+lineSeperator);
                fileWriter.write("</javaMeBundle>"+lineSeperator);
                
                // Jad into the zip
                ZipEntry zipEntry;
                byte[] bytes;

                zipEntry = new ZipEntry(croppedJadFilename);
                zipEntry.setSize(entry.getJadFile().length());
                
                zipOutputStream.putNextEntry(zipEntry);
                bytes = FileUtil.getBytesFromFile(entry.getJadFile());
                zipOutputStream.write(bytes);
                
                zipEntry = new ZipEntry(croppedJarFilename);
                zipEntry.setSize(entry.getJarFile().length());
                
                zipOutputStream.putNextEntry(zipEntry);
                bytes = FileUtil.getBytesFromFile(entry.getJarFile());
                zipOutputStream.write(bytes);
            }
            fileWriter.write("</application>"+lineSeperator);
            fileWriter.write("</contents>"+lineSeperator);

            fileWriter.close();
            byte[] bytes = FileUtil.getBytesFromFile(contentsXmlFile);
            
            ZipEntry zipEntry = new ZipEntry("contents.xml");
            zipEntry.setSize(bytes.length);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(bytes);

            zipOutputStream.close();
        } catch (IOException exception) {
            String absolutePath = "";
            if(contentsXmlFile != null) {
                absolutePath = contentsXmlFile.getAbsolutePath();
            }
            throw new BuildException("Could not write to temp file '"+absolutePath+"'",exception);
        }
        finally {
            try {
                if(fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException exception) {
                throw new BuildException("Could not flush temp file",exception);
            }
        }
        
        // Put contents.xml file in zip.
        
    }

    public void setFallbackDevice(String fallbackDevice) {
        this.fallbackDevice = fallbackDevice;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public void setAccess(String accessParameter) {
        boolean valid = "public".equals(accessParameter) || "owner".equals(accessParameter);
        if(!valid) {
            throw new BuildException("The parameter 'access' of the lifeCycleManager tag requires one of the values 'owner' or 'public'.");
        }
        this.access  = accessParameter;
    }
    
}
