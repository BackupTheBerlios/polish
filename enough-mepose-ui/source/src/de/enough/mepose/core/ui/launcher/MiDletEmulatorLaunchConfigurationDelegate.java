/*
 * Created on Jan 18, 2006 at 6:08:52 PM.
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
package de.enough.mepose.core.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;

import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.core.ui.plugin.UIPluginActivator;
import de.enough.polish.ant.PolishBuildListener;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jan 18, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MiDletEmulatorLaunchConfigurationDelegate extends
        LaunchConfigurationDelegate {

    public class PolishListener implements PolishBuildListener{

        public void notifyBuildEvent(String name, Object data) {
            System.out.println("DEBUG:PolishListener.notifyBuildEvent(...):name:"+name+".data:"+data);
        }
        
    }
    
    public static final String CONSOLE_TYPE = "j2mepolish.console";
    
    public void launch(ILaunchConfiguration configuration, String mode,
                       ILaunch launch, IProgressMonitor monitor)
                                                                throws CoreException {
        
       IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
       IConsole[] consoles = consoleManager.getConsoles();
       IConsole console = null;
       for (int i = 0; i < consoles.length; i++) {
           if(CONSOLE_TYPE.equals(consoles[i].getType())){
               console = consoles[i];
           }
       }
       if(console == null) {
           console = new IOConsole("J2ME Polish Build",CONSOLE_TYPE,null);
           consoleManager.addConsoles(new IConsole[] {console});
       }
       consoleManager.showConsoleView(console);
       System.out.println("DEBUG:MiDletEmulatorLaunchConfigurationDelegate.launch(...):mode:"+mode);
       
       MeposeModel model = MeposePlugin.getDefault().getMeposeModelManager().getCurrentMeposeModel();
       if(model == null) {
           UIPluginActivator.log("No current meposeModel");
           return;
       }
//       AntRunner antRunner = new AntRunner();
//       antRunner.setAntHome(model.getProjectHome().getAbsolutePath());
//       Map userProperties = new HashMap();
//       userProperties.put("polish.build.listener",new PolishListener().getClass().getName());
//       antRunner.addUserProperties(userProperties);
////       antRunner.setArguments("-Ddevice=\"Generic/midp2\"");
//       antRunner.setBuildFileLocation(model.getBuildxml().getAbsolutePath());
//       Map map = new HashMap();
//       map.put(DebugPlugin.ATTR_CAPTURE_OUTPUT,"true");
//       AntProcess antProcess = new AntProcess("J2ME Polish Build",launch,map);
//       antRunner.run(antProcess);
//       antProcess.terminate();
       
//       if(mode.equals("debug")) {
//           manageDebugger();
//       }
    }

}
