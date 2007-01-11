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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
public class ChooseTargetPanel extends IzPanel implements ActionListener {
	private static final long serialVersionUID = -7966526000206872182L;
	private JTextField polishHomeField;
	private JButton polishHomeChooserButton;

	/**
	 * @param parent
	 * @param idata
	 */
	public ChooseTargetPanel(InstallerFrame parent, InstallData idata) {
		super(parent, idata);
		
		String installPath = idata.getInstallPath();
		if (installPath == null) {
			setDefaultPath( parent, idata );
		}
		
		JPanel subPanel = new JPanel( new BorderLayout() );
		
		
	    JLabel title = new JLabel("License Selection");
	    title.setFont( title.getFont().deriveFont( title.getFont().getSize() * 2F ));
	    subPanel.add( title, BorderLayout.NORTH );
	    
	    JTextArea area = new JTextArea( "Please select the selection where you want to install J2ME Polish.\nIf you already have installed a previous J2ME Polish version in this location, the installer will preserve your custom device definitions.\n");
	    area.setEditable( false );
	    area.setLineWrap( true );
	    area.setBackground( title.getBackground() );
	    subPanel.add( area, BorderLayout.CENTER );
	    
	    JPanel j2mepolishLocationPanel = new JPanel( new BorderLayout() );
	    // maybe use PathSelectionPanel instead...?
	    JLabel label = new JLabel("polish.home: ");
	    label.setToolTipText("The location into which you want to install J2ME Polish.");
	    j2mepolishLocationPanel.add( label, BorderLayout.WEST );
	    JTextField textField = new JTextField();
	    j2mepolishLocationPanel.add( textField, BorderLayout.CENTER );
	    this.polishHomeField = textField;
	    JButton button = new JButton( "Choose...");
	    button.addActionListener( this );
	    j2mepolishLocationPanel.add( button, BorderLayout.EAST );
	    this.polishHomeChooserButton = button;
	    subPanel.add( j2mepolishLocationPanel, BorderLayout.SOUTH );

	    setLayout( new BorderLayout() );
	    add( subPanel, BorderLayout.NORTH );
	}
	
	private void setDefaultPath(InstallerFrame parent, InstallData idata) {
		PathInputPanel.loadDefaultInstallDir( parent,  idata);
		String defaultInstallDir = PathInputPanel.getDefaultInstallDir();
		idata.setInstallPath( defaultInstallDir );
	}

	/** Called when the panel becomes active. */
    public void panelActivate()
    {
        // Resolve the default for chosenPath
        super.panelActivate();
        this.polishHomeField.setText( this.idata.getInstallPath() );
    }

    /**
     * Actions-handling method.
     * 
     * @param e The event.
     */
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source == this.polishHomeChooserButton )
        {
        	 // The user wants to browse its filesystem

            // Prepares the file chooser
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File(this.polishHomeField.getText()));
            fc.setMultiSelectionEnabled(false);
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.addChoosableFileFilter(fc.getAcceptAllFileFilter());

            // Shows it
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                String path = fc.getSelectedFile().getAbsolutePath();
                this.polishHomeField.setText(path);
            }
        }
    }
    

    /**
     * Indicates wether the panel has been validated or not.
     * 
     * @return Wether the panel has been validated or not.
     */
    public boolean isValidated()
    {
        // Standard behavior of PathInputPanel.
    	File installDir = new File( this.polishHomeField.getText() );
        try {
        	installDir.mkdirs();
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

}
