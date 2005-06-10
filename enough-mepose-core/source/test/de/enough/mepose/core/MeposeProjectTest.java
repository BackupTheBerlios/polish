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

import junit.framework.Assert;
import junit.framework.TestCase;

public class MeposeProjectTest extends TestCase {

    CorePlugin corePlugin = CorePlugin.getDefault();
    private final String buildxml1_basedir = "/Users/ricky/workspace/enough-polish-demo";
    private final String buildxml1_filename = "build.xml";
    
    public void setUp() {
        System.setProperty("user.dir",this.buildxml1_basedir);
    }
    
    
    public void testSetBuildxml() {
        System.setProperty("user.dir",this.buildxml1_basedir);
        MeposeProject meposeProject = this.corePlugin.getMeposeProject();
        meposeProject.setBuildxml(new File(this.buildxml1_filename));
        Assert.assertNotNull("'environment' is null.",meposeProject.getEnvironment());
        Assert.assertNotNull("'configuredDevices' is null.",meposeProject.getConfiguredDevices());
    }
}
