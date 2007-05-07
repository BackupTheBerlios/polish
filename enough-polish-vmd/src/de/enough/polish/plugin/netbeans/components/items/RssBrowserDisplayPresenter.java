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

package de.enough.polish.plugin.netbeans.components.items;

import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.api.screen.display.ScreenDeviceInfo;
import org.netbeans.modules.vmd.api.screen.display.ScreenPropertyDescriptor;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.screen.display.ItemDisplayPresenter;
import org.netbeans.modules.vmd.midp.screen.display.property.ScreenStringPropertyEditor;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author David Kaspar
 */
public class RssBrowserDisplayPresenter extends ItemDisplayPresenter {

    private static final Border LABEL_BORDER = BorderFactory.createLineBorder(Color.GRAY);

    private JPanel panel;
    private JLabel l;
    private JLabel url;
    private JLabel www;
    
    public RssBrowserDisplayPresenter () {
        panel = new JPanel ();
        panel.setLayout (new BorderLayout ());
        panel.setOpaque (false);
        l = new JLabel ("URL: ");
        panel.add (l, BorderLayout.WEST);
        url = new JLabel ();
        url.setBorder(LABEL_BORDER);
        panel.add (url, BorderLayout.CENTER);
        www = new JLabel ("RSS");
        www.setForeground (Color.GRAY);
        www.setFont (new Font ("dialog", Font.PLAIN, 32)); // NOI18N
        panel.add (www, BorderLayout.SOUTH);
        setContentComponent (panel);
    }


    public void reload (ScreenDeviceInfo deviceInfo) {
        super.reload (deviceInfo);
        PropertyValue propertyValue = getComponent ().readProperty (RssBrowserCD.PROP_URL);
        String text = MidpValueSupport.getHumanReadableString (propertyValue);
        url.setText (text);
    }
    public Collection<ScreenPropertyDescriptor> getPropertyDescriptors () {
        ArrayList<ScreenPropertyDescriptor> list = new ArrayList<ScreenPropertyDescriptor> (super.getPropertyDescriptors ());
        list.add (new ScreenPropertyDescriptor (getComponent (), url, new ScreenStringPropertyEditor (RssBrowserCD.PROP_URL)));
        return list;
    }

}
