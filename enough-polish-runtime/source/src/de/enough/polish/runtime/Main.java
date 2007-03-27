/*
 * Created on 02-Feb-2007 at 10:19:21.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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
package de.enough.polish.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.microedition.midlet.MIDlet;

public class Main
{
  public static void main(String[] args)
    throws ClassNotFoundException, IOException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException
  {
    // TODO: Find JAD.
    
    Properties jadProperties = new Properties();
    
    InputStream in = Main.class.getResourceAsStream("test.jad");
    jadProperties.load(in);
    in.close();
    
    String midletName = jadProperties.getProperty("MIDlet-1");
    int pos = midletName.lastIndexOf(",");
    midletName = midletName.substring(pos + 1);
    
    Class midletClass = Class.forName(midletName);
    MIDlet midlet = (MIDlet) midletClass.newInstance();
    
    Method main = midletClass.getMethod("main", new Class[] { String[].class });
  }
}
