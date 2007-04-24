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

package de.enough.polish.netbeans.platform;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.plugin.netbeans.project.PolishProjectSupport;
import de.enough.polish.plugin.netbeans.settings.PolishSettings;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

/**
 * @author David Kaspar
 */
public class PolishPlatform {
    
    private static Environment environment;

    public static void updatePlatform () {
        if (environment == null) {
            environment = new Environment( new File (PolishSettings.getDefault ().getPolishHome ())  );
        }
        DeviceDatabase database = new DeviceDatabase (new File (PolishSettings.getDefault ().getPolishHome ()));
        final StringBuffer buffer = new StringBuffer ();
        buffer.append ("<?xml version='1.0'?>\n" +
                "<!DOCTYPE platform PUBLIC '-//NetBeans//DTD J2ME PlatformDefinition 1.0//EN' 'http://www.netbeans.org/dtds/j2me-platformdefinition-1_0.dtd'>\n" +
                "<platform name=\"J2MEPolishEmulator\" home=\"" + PolishSettings.getDefault ().getPolishHome () + "\" type=\"UEI-1.0.1\" displayname=\"J2ME Polish Emulator\" srcpath=\"\" docpath=\"\">\n");

        for (Device device : database.getDevices ()) {
            StringBuffer deviceBuffer = new StringBuffer ();
            try {
                deviceBuffer.append ("<device name=\"" + checkForJavaIdentifierCompliant (device.getIdentifier ()) + "\" description=\"" + device.getIdentifier () + "\">\n");

                deviceBuffer.append ("<configuration name=\"CLDC\" version=\"1.0\" displayname=\"Connected Limited Device Configuration\" classpath=\"\" dependencies=\"\" default=\"default\"/>\n");

                if (device.getMidpVersion () == 1)
                    deviceBuffer.append ("<profile name=\"MIDP\" version=\"1.0\" displayname=\"Mobile Information Device Profile\" classpath=\"${platform.home}/lib/midpapi10.jar\" dependencies=\"CLDC > 1.0\" default=\"default\"/>\n");
                else
                    deviceBuffer.append ("<profile name=\"MIDP\" version=\"2.0\" displayname=\"Mobile Information Device Profile\" classpath=\"${platform.home}/lib/midpapi10.jar\" dependencies=\"CLDC > 1.0\" default=\"default\"/>\n");

                device.setEnvironment (environment);
                String[] strings = device.getBootClassPaths ();
                for (int i = 0; i < strings.length; i++)
                    deviceBuffer.append ("<optional name=\"OptionalBootPathElement" + (i + 1) + "\" version=\"1.0\" displayname=\"Optional Path Element\" classpath=\"" + strings[i] + "\" dependencies=\"\" default=\"true\"/>\n");
                strings = device.getClassPaths ();
                for (int i = 0; i < strings.length; i++)
                    deviceBuffer.append ("<optional name=\"OptionalPathElement" + (i + 1) + "\" version=\"1.0\" displayname=\"Optional Path Element\" classpath=\"" + strings[i] + "\" dependencies=\"\" default=\"true\"/>\n");

                HashMap capabilities = device.getCapabilities ();
                Dimension screenSize = PolishProjectSupport.parseDimension ((String) capabilities.get ("polish.ScreenSize"));
                Integer bits = PolishProjectSupport.parseInteger ((String) capabilities.get ("polish.BitsPerPixel"));
                if (screenSize != null)
                    deviceBuffer.append ("<screen width=\"" + screenSize.width + "\" height=\"" + screenSize.height + "\" bitDepth=\"" + (bits != null ? bits : "8") + "\" isColor=\"true\" isTouch=\"false\"/>\n");

                deviceBuffer.append ("</device>\n");
                buffer.append (deviceBuffer.toString ());
            } catch (Exception e) {
                Exceptions.printStackTrace (e);
            }
        }

        buffer.append ("</platform>\n");

        final FileSystem fs = Repository.getDefault ().getDefaultFileSystem ();
        try {
            fs.runAtomicAction (new FileSystem.AtomicAction () {
                public void run () throws IOException {
                    FileObject root = fs.getRoot ();
                    FileObject platforms = root.getFileObject ("Services/Platforms/org-netbeans-api-java-Platform");
                    FileObject platform = platforms.getFileObject ("j2me-polish-emulator.xml");
                    if (platform == null)
                        platform = platforms.createData ("j2me-polish-emulator", "xml");
                    OutputStreamWriter writer = new OutputStreamWriter (platform.getOutputStream ());
                    try {
                        writer.append (buffer.toString ());
                    } finally {
                        try {
                            writer.close ();
                        } catch (IOException e) {
                        }
                    }
                }
            });
        } catch (IOException e) {
            Exceptions.printStackTrace (e);
        }
    }

    public static String checkForJavaIdentifierCompliant (String instanceName) {
        if (instanceName == null  ||  instanceName.length () < 1)
            return "a"; // NOI18N
        StringBuffer buffer = new StringBuffer ();
        int index = 0;
        if (Character.isJavaIdentifierStart (instanceName.charAt (0))) {
            buffer.append (instanceName.charAt (0));
            index ++;
        } else {
            buffer.append ('a'); // NOI18N
        }
        while (index < instanceName.length ()) {
            char c = instanceName.charAt (index);
            if (Character.isJavaIdentifierPart (c))
                buffer.append (c);
            else if (c == '/')
                buffer.append ('_');
            index ++;
        }
        return buffer.toString ();
    }

}
