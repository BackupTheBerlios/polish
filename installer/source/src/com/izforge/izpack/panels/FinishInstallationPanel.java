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
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
public class FinishInstallationPanel extends UserInputPanel {

	private boolean isActivated;

	/**
	 * @param parent
	 * @param idata
	 */
	public FinishInstallationPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
	}
	

	/** Called when the panel becomes active. */
    public void panelActivate()
    {
        super.panelActivate();
        this.parent.lockPrevButton();
        if (this.isActivated) {
        	return;
        }
	    
	    // now write global properties:
	    Map globalProperties = (Map) this.idata.getAttribute("global.properties");
	    if (globalProperties == null) {
	    	System.err.println("Unable to store global.properties - no properties found!");
	    } else {
	        try {
	        	File globalPropertiesFile = new File( this.idata.getInstallPath() + File.separatorChar + "global.properties" );
	        	FileUtil.writePropertiesFile(globalPropertiesFile, globalProperties );
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	emitError(this.parent.langpack.getString("installer.error"), "Unable to write global.properties to " + this.idata.getInstallPath() + ": " + e.toString() );
	    	}	
	    }
	    // restore backuped files:
	    File backupDir = (File) this.idata.getAttribute("polish.backup" );
	    boolean isBackupError = false;
	    if (backupDir != null) {
	    	File[] files = backupDir.listFiles();
	    	File target = new File( this.idata.getInstallPath() );
	    	for (int i = 0; i < files.length; i++) {
				File file = files[i];
				try {
					FileUtil.copy(file, target );
				} catch (IOException e) {
					e.printStackTrace();
		        	emitError(this.parent.langpack.getString("installer.error"), "Unable to restore backup from " + file.getAbsolutePath() + ": " + e.toString() + "\nPlease restore the backup files manually." );
		        	isBackupError = true;
				}
			}
	    	if (!isBackupError) {
	    		FileUtil.delete( backupDir );
	    	}
	    }
	    this.idata.canClose = true;

	    this.isActivated = true;
    }


}
