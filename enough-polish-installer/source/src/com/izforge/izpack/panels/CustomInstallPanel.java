/*
 * Created on 08-Jan-2007 at 15:00:28.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package com.izforge.izpack.panels;

import javax.swing.SwingUtilities;

import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;

/**
 * <p>Installs the application data.</p>
 *
 * <p>copyright Enough Software 2007</p>
 * <pre>
 * history
 *        08-Jan-2007 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class CustomInstallPanel extends InstallPanel {

	private boolean isActivated;

	/**
	 * @param parent
	 * @param idata
	 */
	public CustomInstallPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
	}
	
	public void stopAction()
    {
		final InstallPanel p = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                parent.releaseGUI();
                parent.lockPrevButton();
                
                // With custom actions it is possible, that the current value
                // is not max - 1. Therefore we use always max for both
                // progress bars to signal finish state.
                overallProgressBar.setValue(overallProgressBar.getMaximum());
                int ppbMax = packProgressBar.getMaximum();
                if (ppbMax < 1)
                {
                    ppbMax = 1;
                    packProgressBar.setMaximum(ppbMax);
                }
                packProgressBar.setValue(ppbMax);

                packProgressBar.setString(parent.langpack.getString("InstallPanel.finished"));
                packProgressBar.setEnabled(false);
                try {
	                String no_of_packs = Integer.toString( ReflectionUtil.getIntField( p, "noOfPacks") );
	                overallProgressBar.setString(no_of_packs + " / " + no_of_packs);
	                overallProgressBar.setEnabled(false);
	                packOpLabel.setText(" ");
	                packOpLabel.setEnabled(false);
	                //idata.canClose = true;
	                ReflectionUtil.setField(p, "validated", true);
                } catch (NoSuchFieldException e) {
                	e.printStackTrace();
                }
                //validated = true;
                if (idata.panels.indexOf(this) != (idata.panels.size() - 1)) parent.unlockNextButton();
            }
        });
    }


}
