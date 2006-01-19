/*
 * Created on Jan 4, 2006 at 3:29:53 PM.
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

import org.apache.log4j.Logger;

import de.enough.polish.Device;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceManager;
import de.enough.polish.devices.Platform;
import de.enough.polish.devices.PlatformManager;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jan 4, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class DeviceDatabaseProvider {

    public class Node{
        private Node nextSibling;
        private Node firstChild;
        private Object data;
        
        public Node(Node child, Node sibling) {
            super();
            this.firstChild = child;
            this.nextSibling = sibling;
        }

        public Node getFirstChild() {
            return this.firstChild;
        }

        public void setFirstChild(Node firstChild) {
            this.firstChild = firstChild;
        }

        public Node getNextSibling() {
            return this.nextSibling;
        }

        public void setNextSibling(Node nextSibling) {
            this.nextSibling = nextSibling;
        }

        public Object getData() {
            return this.data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
    
    public static Logger logger = Logger.getLogger(DeviceDatabaseProvider.class);
    private DeviceDatabase deviceDatabase;
    
    public DeviceDatabaseProvider(File polishHome,File projectHome) {
        if(polishHome == null){
            logger.error("Parameter 'polishHome' is null contrary to API.");
            return;
        }
        DeviceDatabase deviceDataBase = new DeviceDatabase(null,polishHome,projectHome,null,null,null,null);
        if(projectHome == null){
            logger.error("Parameter 'projectHome' is null contrary to API.");
            return;
        }
        this.deviceDatabase = new DeviceDatabase(null,polishHome,projectHome,null,null,null,null);
    }

    public DeviceDatabase getDeviceDatabase() {
        return this.deviceDatabase;
    }

    public void setDeviceDatabase(DeviceDatabase deviceDatabase) {
        this.deviceDatabase = deviceDatabase;
    }
    
//    public Node getTargetDevicesByGroup() {
//        DeviceDatabase dd = getDeviceDatabase();
//        PlatformManager pm = dd.getPlatformManager();
//        Platform[] platforms = pm.getPlatforms();
//        for (int i = 0; i < platforms.length; i++) {
//            String platformIdentifier = platforms[i].getIdentifier();
//            
//        }
//        DeviceManager dm = dd.getDeviceManager();
//        Device[] d = dm.getVirtualDevices();
////        for (int i = 0; i < d.length; i++) {
////            d[i].
////        }
//        
//        return null;
//    }
}
