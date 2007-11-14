/*
 * Created on 08-Jun-2004 at 15:00:28.
 * 
 * Copyright (c) 2004 Robert Virkus / Enough Software
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

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;
import com.izforge.izpack.installer.IzPanel;

/**
 * <p>Let the user choose where to install or where to find a resource.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        08-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class ChooseTargetPanel extends IzPanel {
	private static final long serialVersionUID = -7966526000206872182L;
	private FilePropertyPanel polishHomePanel;

	/**
	 * @param parent
	 * @param idata
	 */
	public ChooseTargetPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
		
//		System.err.println("choose target, existing install path=" + idata.getInstallPath());
//		if (idata.getInstallPath() == null) {
//			setDefaultPath( parent, idata );
//		}
		
		JPanel subPanel = new JPanel( new BorderLayout() );
		
		
	    JLabel title = new JLabel("Installation Directory");
	    title.setFont( title.getFont().deriveFont( title.getFont().getSize() * 2F ));
	    subPanel.add( title, BorderLayout.NORTH );
	    
	    JTextArea area = new JTextArea( "Please select the destination directory into which to install J2ME Polish.\nIf you already have installed a previous J2ME Polish version in this location, the installer will preserve your custom device definitions, global.properties and license keys.\n");
	    area.setEditable( false );
	    area.setLineWrap( true );
	    area.setBackground( title.getBackground() );
	    subPanel.add( area, BorderLayout.CENTER );
	    
	    this.polishHomePanel = new FilePropertyPanel( parent, "polish.home: ", idata.getInstallPath(), "Choose...", true, false );
	    subPanel.add( this.polishHomePanel, BorderLayout.SOUTH );

	    setLayout( new BorderLayout() );
	    add( subPanel, BorderLayout.NORTH );
	}
	
	private void setDefaultPath(InstallerFrame parent, InstallData idata) {
		Preferences prefs = Preferences.userRoot().node( "J2ME-Polish" );
		String polishHome = prefs.get("polish.home",  null );
		if (polishHome != null) {
			// polish install location is known...
			idata.setInstallPath( polishHome );
		}
	}

	/** Called when the panel becomes active. */
    public void panelActivate()
    {
        // Resolve the default for chosenPath
        super.panelActivate();
        setDefaultPath(this.parent, this.idata);
        this.polishHomePanel.setValue( this.idata.getInstallPath() );
    }

    

    /**
     * Indicates wether the panel has been validated or not.
     * 
     * @return Wether the panel has been validated or not.
     */
    public boolean isValidated()
    {
        // Standard behavior of PathInputPanel.
    	File installDir = this.polishHomePanel.getValueFile();
        try {
        	if (!installDir.exists()) {
        		installDir.mkdirs();
        	}
        	// check if I can write to the install dir:
        	File testFile = new File( installDir, ".test" );
        	FileOutputStream out = new FileOutputStream( testFile );
        	out.write( new byte[]{12,12,45,55} );
        	out.flush();
        	out.close();
        	testFile.delete();
        } catch (IOException e) {
        	e.printStackTrace();
        	emitError(this.parent.langpack.getString("installer.error"), getI18nStringForClass(
                    "notwritable", "TargetPanel"));
            return false;
        }
        this.idata.setInstallPath( installDir.getAbsolutePath() );
        return true;
    }
    
    public void panelDeactivate()
    {
    	File installDir = this.polishHomePanel.getValueFile();
    	try {
        	boolean installDirCreated = false;
        	if (!installDir.exists()) {
        		installDir.mkdirs();
        		installDirCreated = true;
        	}
        	// backup:
        	if (!installDirCreated) {
        		// check for existing device database customizations, license keys and global.properties:
        		File backupDir = new File( installDir.getAbsolutePath() + "_Backup" + System.currentTimeMillis() );
        		backupDir.mkdir();
        		int backupedFiles = 0;
        		File[] files = installDir.listFiles();
        		for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (!file.isDirectory() && file.getName().startsWith("custom-")) {
						FileUtil.copy( file, backupDir );
						backupedFiles++;
					}
				}
        		// check license keys:
        		File licenseKey = new File( installDir, "license.key" );
        		if (licenseKey.exists()) {
        			FileUtil.copy( licenseKey, backupDir );
        			backupedFiles++;
        		}
        		// check global.properties:
        		File globalPropertiesFile = new File( installDir, "global.properties");
        		if (globalPropertiesFile.exists()) {
        			Map globalProperties = FileUtil.readPropertiesFile( globalPropertiesFile );
        			this.idata.setAttribute("global.properties",  globalProperties );
        		}
        		if (backupedFiles > 0) {
        			this.idata.setAttribute("polish.backup",  backupDir );
        		} else {
        			backupDir.delete();
        		}
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        	emitError(this.parent.langpack.getString("installer.error"), getI18nStringForClass(
                    "notwritable", "TargetPanel"));
            return;
        }
        this.idata.setInstallPath( installDir.getAbsolutePath() );
    }
	

}
