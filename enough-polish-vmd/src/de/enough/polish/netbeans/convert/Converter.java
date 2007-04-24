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
import de.enough.polish.netbeans.database.PolishDeviceDatabase;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Kaspar
 */
public final class Converter {

    static void convert (Project project, String[] devices) {
        if (PolishProjectSupport.isPolishProject (project)) {
            convertToNB (project);
        } else {
            convertToPolish (project, devices);
        }
    }

    private static void convertToPolish (Project project, String[] devices) {
        AntProjectHelper helper = project.getLookup ().lookup (AntProjectHelper.class);
        FileObject projectRoot = helper.getProjectDirectory ();
        String polishHomePath = PolishSettings.getDefault ().getPolishHome ();
        FileObject polishHome = FileUtil.toFileObject (FileUtil.normalizeFile (new File (polishHomePath)));
        assert polishHome != null;

        EditableProperties ep = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
        OpenProjects.getDefault ().close (new Project[] { project });
        project = null;

        try {
            FileObject backupDir = projectRoot.getFileObject ("backup"); // NOI18N
            if (backupDir == null)
                backupDir = projectRoot.createFolder ("backup"); // NOI18N
            FileObject buildXML = projectRoot.getFileObject ("build.xml"); // NOI18N
            FileUtil.moveFile (buildXML, backupDir, "build"); // NOI18N
            FileObject projectProperties = projectRoot.getFileObject ("nbproject/project.properties"); // NOI18N
            FileUtil.moveFile (projectProperties, backupDir, "project"); // NOI18N
            FileObject emptyBuildXML = polishHome.getFileObject ("samples/blank/build.xml"); // NOI18N
            FileUtil.copyFile (emptyBuildXML, projectRoot, "build"); // NOI18N
            FileObject resources = projectRoot.getFileObject ("resources"); // NOI18N
            if (resources == null)
                copyDir (polishHome.getFileObject ("samples/blank/resources"), projectRoot, "resources"); // NOI18N
        } catch (IOException e) {
            Exceptions.printStackTrace (e);
        }

        if (devices != null  &&  devices.length > 0) {
            DeviceDatabase database = new DeviceDatabase (new File (polishHomePath));
            String configurations = MidpProjectPropertiesSupport.evaluateProperty (ep, "all.configurations", null);// NOI18N
            if (configurations == null  ||  configurations.length () <= 0)
                configurations = " "; // NOI18N
            for (String deviceID : devices) {
                Device device = database.getDevice (deviceID);
                HashMap<String,String> map = PolishDeviceDatabase.createPropertiesMap (device);
                if (map == null)
                    continue;
                for (Map.Entry<String, String> entry : map.entrySet ())
                    MidpProjectPropertiesSupport.setProperty (ep, entry.getKey (), deviceID, entry.getValue ());
                configurations += "," + deviceID;
            }
            MidpProjectPropertiesSupport.setProperty (ep, "all.configurations", null, configurations);
        }
        ep.put (PolishProjectSupport.PROP_USE_POLISH_PROJECT, "true"); // NOI18N

        helper.putProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH, ep);
        helper = null;
        project = FileOwnerQuery.getOwner (projectRoot);
        if (project != null)
            OpenProjects.getDefault().open (new Project[] { project }, false);

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

    private static void convertToNB (Project project) {
        // TODO
    }

}
