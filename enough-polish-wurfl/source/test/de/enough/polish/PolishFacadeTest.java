/*
 * Created on Apr 6, 2006 at 3:00:54 PM.
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

import de.enough.polish.converter.PolishFacade;
import junit.framework.TestCase;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Apr 6, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishFacadeTest extends TestCase {

    /*
     * Test method for 'de.enough.polish.PolishFacade.getDeviceByUserAgent(String)'
     */
    public void testGetDeviceByUserAgent() {
        Device device;
        try {
            device = PolishFacade.getDeviceDatabase().getDeviceManager().getDeviceByUserAgent("SonyEricssonK608i/R2T Browser/SEMC-Browser/4.2 Profile/MIDP-2.0 Configuration/CLDC-1.1");
        } catch (Exception exception) {
            fail(exception.toString());
            return;
        }
        if(device == null) {
            fail("Should have found device.");
            return;
        }
        if( ! device.getIdentifier().equals("Sony-Ericsson/K608")) {
            fail("Wrong device found:"+device.getIdentifier());
            return;
        }
        
//        try {
//            device = PolishFacade.getDeviceByUserAgent("SonyEricssonK608i");
//        } catch (PolishException exception) {
//            fail(exception.toString());
//            return;
//        }
        
        if(device == null) {
            fail("Should have found device.");
            return;
        }
        if( ! device.getIdentifier().equals("Sony-Ericsson/K608")) {
            fail("Wrong device found:"+device.getIdentifier());
            return;
        }
        
        
    }

}
