/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 */

package de.enough.polish.netbeans.convert;

import de.enough.polish.plugin.netbeans.project.PolishProjectSupport;
import de.enough.polish.plugin.netbeans.settings.PolishSettings;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.netbeans.database.PolishDeviceDatabase;
import java.io.BufferedReader;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.modules.vmd.api.io.javame.MidpProjectPropertiesSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.project.ProjectInformation;
import org.openide.filesystems.Repository;

/**
 * @author David Kaspar
 */
public final class Converter {

    static void convertToPolish (Project project, String[] devices) {
        AntProjectHelper helper = project.getLookup ().lookup (AntProjectHelper.class);
        FileObject projectRoot = helper.getProjectDirectory ();
        String polishHomePath = PolishSettings.getDefault ().getPolishHome ();
        FileObject polishHome = FileUtil.toFileObject (FileUtil.normalizeFile (new File (polishHomePath)));
        String projectName = project.getLookup().lookup(ProjectInformation.class).getName();
        assert polishHome != null;

        EditableProperties ep = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
        boolean setAsMain = project == OpenProjects.getDefault().getMainProject();
        OpenProjects.getDefault ().close (new Project[] { project });
        project = null;

        try {
            FileObject backupDir = projectRoot.getFileObject ("backup"); // NOI18N
            if (backupDir == null)
                backupDir = projectRoot.createFolder ("backup"); // NOI18N
            FileObject oldBuildXML = projectRoot.getFileObject ("build.xml"); // NOI18N
            FileUtil.moveFile (oldBuildXML, backupDir, "build"); // NOI18N
            FileObject projectProperties = projectRoot.getFileObject ("nbproject/project.properties"); // NOI18N
            FileUtil.moveFile (projectProperties, backupDir, "project"); // NOI18N
            FileObject emptyBuildXML = Repository.getDefault().getDefaultFileSystem().findResource("j2mepolish/build.xml"); // polishHome.getFileObject ("samples/blank/build.xml"); // NOI18N
            FileObject buildXML = FileUtil.copyFile (emptyBuildXML, projectRoot, "build"); // NOI18N
            processBuildXML (projectName, buildXML);
            FileObject resources = projectRoot.getFileObject ("resources"); // NOI18N
            if (resources == null) {
                resources = projectRoot.createFolder("resources"); // NOI18N
                if (resources.getFileObject("base") == null) // NOI18N
                    copyDir (polishHome.getFileObject ("samples/blank/resources/base"), resources, "base"); // NOI18N
            }
        } catch (IOException e) {
            Exceptions.printStackTrace (e);
        }

        DeviceDatabase database = new DeviceDatabase (new File (polishHomePath));
        String configurations = " "; // NOI18N

        Environment env = new Environment (new File (polishHomePath));
        if (devices != null) {
            for (String deviceID : devices) {
                Device device = database.getDevice (deviceID);
                HashMap<String,String> map = PolishDeviceDatabase.createPropertiesMap (device, env);
                if (map == null)
                    continue;
                for (Map.Entry<String, String> entry : map.entrySet ())
                    MidpProjectPropertiesSupport.setProperty (ep, entry.getKey (), deviceID, entry.getValue ());
                configurations += "," + deviceID;
            }
        }

        Device device = database.getDevice ("Generic/DefaultColorPhone"); // NOI18N
        HashMap<String,String> map = PolishDeviceDatabase.createPropertiesMap (device, env);
        for (Map.Entry<String, String> entry : map.entrySet ())
            MidpProjectPropertiesSupport.setProperty (ep, entry.getKey (), null, entry.getValue ());

        MidpProjectPropertiesSupport.setProperty (ep, "all.configurations", null, configurations); // NOI18N
        ep.put (PolishProjectSupport.PROP_USE_POLISH_PROJECT, "true"); // NOI18N

        helper.putProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        helper = null;
        project = FileOwnerQuery.getOwner (projectRoot);
        if (project != null) {
            OpenProjects.getDefault().open (new Project[] { project }, false);
            if (setAsMain)
                OpenProjects.getDefault().setMainProject(project);
        }
    }

    private static void copyDir (FileObject sourceDir, FileObject parentTargetDir, String targetDirName) throws IOException {
        FileObject targetDir = parentTargetDir.createFolder (targetDirName);
        for (FileObject file : sourceDir.getChildren ()) {
            if (file.isFolder ()) {
                copyDir (file, targetDir, file.getNameExt ());
            } else if (file.isData ()) {
                FileUtil.copyFile (file, targetDir, file.getName ());
            }
        }
    }
    
    private static void processBuildXML (String projectName, FileObject file) {
        ArrayList<String> strings = new ArrayList<String> ();
        BufferedReader br = null;
        try {
            br = new BufferedReader (new InputStreamReader (file.getInputStream()));
            for (;;) {
                String line = br.readLine();
                if (line == null)
                    break;
                line = line.replace ("__PROJECT_NAME__", projectName); // NOI18N
                line = line.replace("__POLISH_HOME__", PolishSettings.getDefault().getPolishHome()); // NOI18N
                strings.add (line);
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        } finally {
            try {
                if (br != null)
                    br.close ();
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
        }
        
        PrintStream bw = null;
        try {
            bw = new PrintStream (file.getOutputStream());
            for (String line : strings)
                bw.println(line);
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        } finally {
            if (bw != null)
                bw.close ();
        }
    }

    private static void convertToNB (Project project) {
        // TODO
    }

}
