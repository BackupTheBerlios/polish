/*
 * Created on Dec 3, 2004 at 7:08:43 PM.
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
package de.enough.polish.swing;

import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;


/**
 * <p>Base class for standalong swing applications.</p>
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        Dec 3, 2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */

public class SwingApplication 
extends JFrame 
implements Application
{
	
	protected boolean systemExitOnQuit;
	protected boolean isMacOsX;
	protected NativeIntegration nativeIntegration;
	protected String applicationName;
	
	public SwingApplication( String title, boolean systemExitOnQuit ) {
		super( title );
		this.applicationName = title;
		this.systemExitOnQuit = systemExitOnQuit;
		this.isMacOsX = (System.getProperty("mrj.version") != null);
		loadNativeIntegration();


	}

	/**
	 * Loads the integration for the underlying OS.
	 */
	protected void loadNativeIntegration() {
		try {
			Class nativeIntegrationClass = getNativeIntegrationClass();
			if (nativeIntegrationClass != null) {
				this.nativeIntegration = (NativeIntegration) nativeIntegrationClass.newInstance();
				this.nativeIntegration.init( this, this.applicationName );
			}
		} catch (Exception e) {
			System.err.println("Unable to load native integration: " + e );
			e.printStackTrace();
		}
	}
	
	
	protected Class getNativeIntegrationClass() throws ClassNotFoundException {
		if (this.isMacOsX) {
			return Class.forName("de.enough.polish.swing.MacOsXIntegration");
		} else {
			return null;
		}
	}
	
	protected String getAboutText() {
		return this.applicationName + ": made by Enough Software.";
	}
	
	protected JScrollPane createScrollPane( JComponent component ) {
		if (this.isMacOsX) {
			return new JScrollPane(component,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		} else {
			return new JScrollPane( component );
		}
	}
	
	protected void setWindowDirtyFlag( boolean isDirty ) {
		Boolean flag = Boolean.FALSE;
		if (isDirty) {
			flag = Boolean.TRUE;
		}
		getRootPane().putClientProperty("windowModified", flag );
	}
	
	protected int getNativeShortcutKeyMask() {
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#quit()
	 */
	public void quit() {
		if (this.systemExitOnQuit) {
			System.exit(0);
		} else {
			setVisible( false );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#about()
	 */
	public void about() {
		JOptionPane.showMessageDialog( this, getAboutText() );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#preferences()
	 */
	public void preferences() {
		// can be implemented by sub class
		System.out.println("preferences: not supported.");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#openApplication()
	 */
	public void openApplication() {
		// can be implemented by sub class
		System.out.println("open application: not supported.");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.swing.Application#openDocument()
	 */
	public void openDocument() {
		// can be implemented by sub class
		System.out.println("open document: not supported.");
	}
	
	
	
	
	

}
