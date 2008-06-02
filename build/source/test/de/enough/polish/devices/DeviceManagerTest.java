/*
 * Created on Jun 2, 2008 at 12:57:55 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.devices;

import java.io.File;

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DeviceManagerTest extends TestCase
{
	
	public void testResolveUserAgent(){
		DeviceDatabase db = DeviceDatabase.getInstance( new File("/Users/robertvirkus/Documents/workspace/enough-polish-build" ) );
		DeviceManager manager = db.getDeviceManager();
		String userAgent;
		de.enough.polish.Device device;
		
		userAgent = "Nokia6300/2.0 (05.50) Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);

		userAgent = "NokiaN90-1/2.0523.1.4 Series60/2.8 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);

		userAgent = "Mozilla/5.0 (SymbianOS/9.2 U; Series60/3.1 NokiaN95_8GB/10.0.007; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);
		
		userAgent = "SAMSUNG-SGH-U700/1.0 SHP/VPP/R5 NetFront/3.4 SMM-MMS/1.2.0 profile/MIDP-2.0 configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);

		userAgent = "MOT-V3c/08.B5.10R MIB/2.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);

		userAgent = "Mozilla/2.0 (compatible; MSIE 3.02; Windows CE; PPC; 240x320) BlackBerry8800/4.2.1 Profile/MIDP-2.0 Configuration/CLDC-1.1 VendorID/102";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);

		userAgent = "LG-KG800/V10e Obigo/WAP2.0 MIDP-2.0/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);

		userAgent = "LGE-KG800/V10e Obigo/WAP2.0 MIDP-2.0/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);

		userAgent = "LGKG800/V10e Obigo/WAP2.0 MIDP-2.0/CLDC-1.1";
		device = manager.getDeviceByUserAgent(userAgent);
		assertNotNull(device);

//		userAgent = "";
//		device = manager.getDeviceByUserAgent(userAgent);
//		assertNotNull(device);

	}

}
