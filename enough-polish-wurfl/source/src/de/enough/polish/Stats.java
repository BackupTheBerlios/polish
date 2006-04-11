/*
 * Created on Apr 6, 2006 at 4:01:01 PM.
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
package de.enough.polish;

public class Stats{
        private int noPolishDeviceFound = 0;
        private int actualDeviceFound;
        private int noActualDeviceFound;
        private int devicesMapped;
        private int numberOfPolishDevices;
        private int missingPolishVendorForWurfleVendor;
        public void actualDeviceFound(String key) {
            this.actualDeviceFound++;
        }
        public void nonActualDeviceFound(String key) {
            this.noActualDeviceFound++;
        }
        public void noPolishDeviceFoundForWurflKey(String key) {
            System.out.println("polish device not found for wurfl key:"+key);
            this.noPolishDeviceFound++;
        }
        public void devicesMapped(String identifier, String wurfleId) {
//            System.out.println("devicesMapped:"+identifier+":"+wurfleId);
            this.devicesMapped++;
        }
        public void numberOfPolishDevices(int i) {
            this.numberOfPolishDevices = i;
        }
        public void missingPolishVendorForWurfleVendor(String make) {
            this.missingPolishVendorForWurfleVendor++;
        }
        public void print() {
            System.out.println("Stats");
            System.out.println("# of actual devices found in wurfl:"+this.actualDeviceFound);
            System.out.println("# of wurfl key with no polish device:"+this.noPolishDeviceFound);
            System.out.println("# of devices mapped:"+this.devicesMapped);
            System.out.println("# of polish devices:"+this.numberOfPolishDevices);
            System.out.println("# of missing wurfle vendors in polish:"+this.missingPolishVendorForWurfleVendor);
        }
    }