/*
 * Created on Jun 13, 2006 at 4:13:40 PM.
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
package de.enough.encogen.java;

import junit.framework.TestCase;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jun 13, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class ClazzElementTest extends TestCase {

    public void testPrint() {
        PackageElement packageElement = new PackageElement("de.enough.sample");
        ImportElement importStatement1 = new ImportElement("javax.microedition.midlet.MIDlet");
        ImportElement importStatement2 = new ImportElement("javax.microedition.midlet.MIDletStateChangeException");
        
        CommentStatement commentStatement = new CommentStatement("TODO: Implement this method.");

        MethodElement methodStartApp = new MethodElement("startApp","void",null,"protected",new String[] {"MIDletStateChangeException"});
        methodStartApp.addStatement(commentStatement);
        
        MethodElement methodPauseApp = new MethodElement("pauseApp","void",null,"protected",null);
        methodPauseApp.addStatement(commentStatement);
        
        MethodElement methodDestroyApp = new MethodElement("destroyApp","void",new String[] {"boolean unconditional"},"protected",new String[] {"MIDletStateChangeException"});
        methodDestroyApp.addStatement(commentStatement);
        
        ClazzElement clazzElement = new ClazzElement();
        clazzElement.setParent("MIDlet");
        clazzElement.setClazzName("SimpleMidlet");
        clazzElement.setPackageElement(packageElement);
        clazzElement.addImportStatement(importStatement1);
        clazzElement.addImportStatement(importStatement2);
        clazzElement.addMethod(methodStartApp);
        clazzElement.addMethod(methodPauseApp);
        clazzElement.addMethod(methodDestroyApp);
        
        Rectangle rectangle = new Rectangle(0,0,1,1);
        
        String result = clazzElement.print(rectangle);
        
        System.out.println(result);
    }
}
