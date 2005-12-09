/*
 * Created on Jun 9, 2005 at 5:00:05 PM.
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
package de.enough.mepose.core;

import java.io.File;

import de.enough.mepose.core.model.MeposeModel;
import de.enough.polish.Device;

import junit.framework.Assert;
import junit.framework.TestCase;

public class MeposeProjectTest extends TestCase {

    CorePlugin corePlugin = CorePlugin.getDefault();
    private final String buildxml1_basedir = "/Users/ricky/workspace/enough-polish-demo";
    private final String buildxml1_filename = "build.xml";
    
    MeposeModel meposeProject;
    
    public void setUp() {
        this.meposeProject = this.corePlugin.getDefaultMeposeModel();
        this.meposeProject.setProjectPath(this.buildxml1_basedir);
        this.meposeProject.setBuildxml(new File(this.buildxml1_filename));
    }
    
    public void testSetBuildxml() {
        
        Assert.assertNotNull("'environment' is null.",this.meposeProject.getEnvironment());
        Assert.assertNotNull("'configuredDevices' is null.",this.meposeProject.getConfiguredDevices());
        Device[] devices = this.meposeProject.getConfiguredDevices();
        System.out.print("DEBUG:MeposeProjectTest.testSetBuildxml(...):devices:");
        for (int deviceIndex = 0; deviceIndex < devices.length; deviceIndex++) {
            System.out.print(devices[deviceIndex].getIdentifier() + " ");
        }
        System.out.println();
    }
    
    public void testSetEnvironmentToDevice() {
        Device[] devices = this.meposeProject.getConfiguredDevices();
        this.meposeProject.setEnvironmentToDevice(devices[0]);
        Assert.assertTrue(this.meposeProject.getEnvironment().getDevice().equals(devices[0]));
    }
}
