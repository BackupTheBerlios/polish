/*
 * Created on May 27, 2008 at 2:10:12 PM.
 * 
 * Copyright (c) 2008 Robert Virkus / Enough Software
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
package de.enough.polish.converter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * <br>Copyright Enough Software 2005-2008
 * <pre>
 * history
 *        May 27, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class Main {

public static void main(String[] args) {
        
        final String DEVICE_INPUT_PATH = "../enough-polish-build/devices.xml";
        final String DEVICE_OUTPUT_PATH = "../enough-polish-build/devices_wurfl.xml";
        final String VENDORS_INPUT_PATH = "../enough-polish-build/vendors.xml";
        final String VENDORS_OUTPUT_PATH = "../enough-polish-build/vendors_wurfl.xml";
        
        Converter converter;
        try {
            converter = new Converter();
        } catch (ConverterException exception) {
            System.out.println("Could not create Converter."+exception);
            exception.printStackTrace();
            return;
        }
        InputStream devicesInputStream;
        OutputStream deviceOutputStream;
        InputStream vendorsInputStream;
        OutputStream vendorsOutputStream;

        try {
            devicesInputStream = new FileInputStream(DEVICE_INPUT_PATH);
            deviceOutputStream = new FileOutputStream(DEVICE_OUTPUT_PATH);
            vendorsInputStream = new FileInputStream(VENDORS_INPUT_PATH);
            vendorsOutputStream = new FileOutputStream(VENDORS_OUTPUT_PATH);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return;
        }
        
        // Options
        converter.setOptionInsertMissingDevices(true);

        try {
            converter.convert(devicesInputStream,deviceOutputStream,vendorsInputStream,vendorsOutputStream);
        } catch (Throwable exception) {
            exception.printStackTrace();
            converter.getStats().print();
            return;
        }
        converter.getStats().print();
    }
}
