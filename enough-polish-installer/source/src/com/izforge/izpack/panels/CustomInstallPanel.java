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

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
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
public class CustomInstallPanel 
extends InstallPanel
implements Runnable
{

	private boolean isActivated;
	private JLabel deletingInfoLabel;

	/**
	 * @param parent
	 * @param idata
	 */
	public CustomInstallPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.izforge.izpack.panels.InstallPanel#panelActivate()
	 */
	public void panelActivate() {
		// make a clean installation by removing the existing installation first:
		File existingInstallationDir = new File( this.idata.getInstallPath() );
		if (existingInstallationDir.exists()) {
			this.deletingInfoLabel = LabelFactory.create("Please stand by while clearing previous installation...",
		                parent.icons.getImageIcon("information"), LEADING);
			add(this.deletingInfoLabel, IzPanelLayout.getDefaultConstraint(FULL_LINE_CONTROL_CONSTRAINT));
			Thread thread = new Thread( this );
			thread.start();
		} else {			
			super.panelActivate();
		}
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



	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			File existingInstallationDir = new File( this.idata.getInstallPath() );
			// for some reason I'm not able to delete the installation directory itself - could be
			// that IzPack already holds a reference or something.
			// So instead remove files within the install directory:
			File[] files = existingInstallationDir.listFiles();
//			StringBuffer failuresBuffer = new StringBuffer();
			boolean overallSuccess = true;
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				boolean success = FileUtil.delete(file);
				overallSuccess &= success;
//				if (!success) {
//					failuresBuffer.append( file.getAbsolutePath() + "\n");
//				}
			}
			if (overallSuccess) {
				this.deletingInfoLabel.setText("Previous installation has been removed.");
			} else {
				this.deletingInfoLabel.setText("Previous installation could not be removed. Please make a clean install if you encounter problems.");
				// does only work after manually resizing install window...
//				JTextArea area = new JTextArea( "\nFollowing files/directories could not be removed:\n" + failuresBuffer.toString() );
//				area.setEditable( false );
//				area.setLineWrap( true );
//				area.setBackground( getBackground() );
//			    add( area, IzPanelLayout.getDefaultConstraint(FULL_LINE_CONTROL_CONSTRAINT) );
//			    SwingUtilities.updateComponentTreeUI( this );
			}
		} catch (Exception e) {
			System.err.println("Unable to remove previous installation: " + e.toString() );
			this.deletingInfoLabel.setText("Previous installation could not be removed, please make a clean install if you encounter problems.");
			e.printStackTrace();
		} finally {
			super.panelActivate();
		}
	}


}
